package org.changppo.account.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.account.service.dto.payment.PaymentDto;

import java.util.List;

@Data
@AllArgsConstructor
public class PaymentListDto {
    private int numberOfElements;

    private boolean hasNext;

    private List<PaymentDto> paymentList;
}
