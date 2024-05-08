package org.changppo.account.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.payment.FakeBillingInfoClient;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.type.PaymentStatus;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProcessorConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final Job paymentExecutionJob;
    private final FakeBillingInfoClient fakeBillingInfoClient;
    private final PaymentRepository paymentRepository;

    @Bean
    public ItemProcessor<Member, Payment> paymentProcessorForAutomaticPayment() {
        return member -> processPaymentForMember(member, LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIDNIGHT));
    }

    @Bean
    public ItemProcessor<Member, Payment> paymentProcessorForDeletion() {
        return member -> processPaymentForMember(member, member.getDeletionRequestedAt());
    }

    private Payment processPaymentForMember(Member member, LocalDateTime periodEndAdjuster) {
        LocalDateTime periodStart = paymentRepository.findFirstByMemberIdOrderByEndedAtDesc(member.getId()).map(Payment::getEndedAt).orElse(member.getCreatedAt());;
        LocalDateTime periodEnd = LocalDateTime.now().with(periodEndAdjuster);
        BigDecimal paymentAmount = fakeBillingInfoClient.getBillingAmountForPeriod(member.getId(), periodStart, periodEnd).getData().orElseThrow(() -> new RuntimeException("Payment amount is not found"));
        return decidePaymentExecution(member, new BigDecimal("0"), periodStart, periodEnd);
    }

    private Payment decidePaymentExecution(Member member, BigDecimal amount, LocalDateTime start, LocalDateTime end) {
        if (amount.compareTo(BigDecimal.valueOf(100.0)) <= 0) {
            return createCompletedPayment(member, null, amount, null, start, end);
        } else {
            return executePayment(member, amount, start, end);
        }
    }

    private Payment executePayment(Member member, BigDecimal amount, LocalDateTime start, LocalDateTime end) {
        JobParameters jobParameters = buildJobParameters(member.getId(), amount, end);
        JobExecution jobExecution = createJobExecution(jobParameters);
        return processJobExecution(jobExecution, member, amount, start, end);
    }

    private JobParameters buildJobParameters(Long memberId, BigDecimal amount, LocalDateTime date) {
        return new JobParametersBuilder()
                .addLong("memberId", memberId)
                .addString("amount", amount.toString())
                .addLocalDateTime("date", date)
                .toJobParameters();
    }

    private JobExecution createJobExecution(JobParameters jobParameters) {
        JobExecution jobExecution = jobRepository.getLastJobExecution(paymentExecutionJob.getName(), jobParameters);
        if (jobExecution == null) {
            try {
                jobExecution = jobLauncher.run(paymentExecutionJob, jobParameters);
            } catch (Exception e) {
                log.error("Payment execution failed", e);
            }
        }
        return jobExecution;
    }

    private Payment processJobExecution(JobExecution jobExecution, Member member, BigDecimal amount, LocalDateTime start, LocalDateTime end) {
        if (jobExecution != null && jobExecution.getStatus() == BatchStatus.COMPLETED) {
            PaymentExecutionJobResponse paymentExecutionJobResponse = extractPaymentDetails(jobExecution);
            return createCompletedPayment(member, paymentExecutionJobResponse.getKey(), amount,
                    new PaymentCardInfo(paymentExecutionJobResponse.getCardType(), paymentExecutionJobResponse. getCardIssuerCorporation(), paymentExecutionJobResponse.getCardBin()), start, end);
        }
        return createFailedPayment(member, amount, start, end);
    }

    private PaymentExecutionJobResponse extractPaymentDetails(JobExecution jobExecution) {
        String key = safeExtractString(jobExecution, "key");
        String cardType = safeExtractString(jobExecution, "cardType");
        String cardIssuerCorporation = safeExtractString(jobExecution, "cardIssuerCorporation");
        String cardBin = safeExtractString(jobExecution, "cardBin");
        return new PaymentExecutionJobResponse(key, cardType, cardIssuerCorporation, cardBin);
    }

    private String safeExtractString(JobExecution jobExecution, String key) {
        Object value = jobExecution.getExecutionContext().get(key);
        return value != null ? value.toString() : "Unknown";
    }

    private Payment createFailedPayment(Member member, BigDecimal amount, LocalDateTime start, LocalDateTime end) {
        return Payment.builder()
                .key(null)
                .amount(amount)
                .status(PaymentStatus.FAILED)
                .startedAt(start)
                .endedAt(end)
                .member(member)
                .cardInfo(null)
                .build();
    }

    private Payment createCompletedPayment(Member member, String key, BigDecimal amount, PaymentCardInfo paymentCardInfo, LocalDateTime start, LocalDateTime end) {
        return Payment.builder()
                .key(key)
                .amount(amount)
                .status(PaymentStatus.COMPLETED_PAID)
                .startedAt(start)
                .endedAt(end)
                .member(member)
                .cardInfo(paymentCardInfo)
                .build();
    }
}
