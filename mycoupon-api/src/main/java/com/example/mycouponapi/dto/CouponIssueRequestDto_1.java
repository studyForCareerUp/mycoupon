package com.example.mycouponapi.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CouponIssueRequestDto_1 {
    private long userId;
    private long couponId;
}
