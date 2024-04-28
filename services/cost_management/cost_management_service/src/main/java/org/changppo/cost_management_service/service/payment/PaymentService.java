package org.changppo.cost_management_service.service.payment;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.payment.PaymentDto;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.changppo.cost_management_service.entity.payment.PaymentCardInfo;
import org.changppo.cost_management_service.entity.payment.PaymentStatus;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.payment.PaymentRepository;
import org.changppo.cost_management_service.response.exception.payment.PaymentExecutionFailureException;
import org.changppo.cost_management_service.response.exception.payment.PaymentExecutionNotFoundException;
import org.changppo.cost_management_service.response.exception.payment.PaymentNotFoundException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final Job paymentJob;

    @Transactional
    @PreAuthorize("@paymentAccessEvaluator.check(#id) and !@memberPaymentFailureStatusEvaluator.check(#id)")
    public PaymentDto repayment(@Param("id") Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
        JobParameters jobParameters = createJobParameters(payment);
        JobExecution jobExecution = createJobExecution(jobParameters);
        updatePaymentStatus(payment, jobExecution);
        return new PaymentDto(payment.getId(), payment.getAmount(), payment.getStatus(), payment.getStartedAt(), payment.getEndedAt(), payment.getCardInfo(), payment.getCreatedAt());
    }

    private JobParameters createJobParameters(Payment payment) {
        return new JobParametersBuilder()
                .addLong("memberId", payment.getMember().getId())
                .addLong("amount", (long) payment.getAmount())
                .addLocalDateTime("date", payment.getEndedAt())
                .toJobParameters();
    }

    private JobExecution createJobExecution(JobParameters jobParameters) {
        return Optional.ofNullable(jobRepository.getLastJobExecution(paymentJob.getName(), jobParameters))
                .map(jobExecution -> {
                    if (jobExecution.getStatus() == BatchStatus.FAILED) {
                        try {
                            return jobLauncher.run(paymentJob, jobParameters);
                        } catch (Exception e) {
                            throw new PaymentExecutionFailureException(e);
                        }
                    }
                    return jobExecution;
                })
                .orElseThrow(PaymentExecutionNotFoundException::new);
    }

    private void updatePaymentStatus(Payment payment, JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            PaymentCardInfo cardInfo = (PaymentCardInfo) jobExecution.getExecutionContext().get("paymentCardInfo");
            payment.changeStatus(PaymentStatus.COMPLETED_PAID, cardInfo);
            payment.getMember().unbanForPaymentFailure();
            apiKeyRepository.unbanApiKeysForPaymentFailure(payment.getMember().getId());
        }
    }
}