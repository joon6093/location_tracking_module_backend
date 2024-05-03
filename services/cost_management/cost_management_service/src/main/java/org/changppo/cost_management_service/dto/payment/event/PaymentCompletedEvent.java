package org.changppo.cost_management_service.dto.payment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.cost_management_service.entity.member.Member;

@Data
@AllArgsConstructor
public class PaymentCompletedEvent {
    private Member member;
}