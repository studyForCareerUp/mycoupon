package com.example.mycouponapi.service;

import com.example.mycouponapi.dto.CouponIssueRequestDto;
import com.example.mycouponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponIssueRequestService {
    private final CouponIssueService couponIssueService;

    public void issueRequestV1(CouponIssueRequestDto requestDto) {

        synchronized (this) {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        }

        log.info("쿠폰 발급 완료. couponId: %s, userId: %s"
                .formatted(requestDto.couponId(), requestDto.userId()));

    }
}
