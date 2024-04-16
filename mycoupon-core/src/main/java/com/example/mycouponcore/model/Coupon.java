package com.example.mycouponcore.model;

import com.example.mycouponcore.exception.CouponIssueException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.mycouponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.example.mycouponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    // 발행 가능한지 기간 검증
    public boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }

    // 발행 가능한지 수량 체크
    public boolean availableIssueQuantity() {
        // totalQuantity 가 null 이면 무제한 발급 가능하다는 것
        if (totalQuantity == null) {
            return true;
        }
        return totalQuantity > issuedQuantity;
    }


    // 발행
    public void issue() {
        if (!availableIssueQuantity()) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY,
                    "발급 가능한 수량을 초과합니다. total : %s, issued : %s"
                            .formatted(totalQuantity, issuedQuantity));
        }
        if (!availableIssueDate()) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_DATE,
                    "발급 가능한 일자가 아닙니다. request : %s, issueStart %s, issueEnd %s"
                            .formatted(LocalDateTime.now(), dateIssueStart, dateIssueEnd));
        }
        issuedQuantity++;
    }


}