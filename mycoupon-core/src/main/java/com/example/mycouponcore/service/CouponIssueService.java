package com.example.mycouponcore.service;


import com.example.mycouponcore.exception.CouponIssueException;
import com.example.mycouponcore.exception.ErrorCode;
import com.example.mycouponcore.model.Coupon;
import com.example.mycouponcore.model.CouponIssue;
import com.example.mycouponcore.repository.mysql.CouponIssueJpaRepository;
import com.example.mycouponcore.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.mycouponcore.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponIssueService {
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponJpaRepository couponJpaRepository;

    @Transactional
    public void issue(long couponId, long userId) {

        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }

    public Coupon findCoupon(long couponId) {
        return couponJpaRepository
                .findById(couponId)
                .orElseThrow(() -> {
                    throw new CouponIssueException(COUPON_NOT_EXIST, "쿠폰이 존재하지 않습니다. %s".formatted(couponId));
                });


    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {

        // 방어 코드
        checkAlreadyIssue(couponId, userId);

        CouponIssue issue = CouponIssue
                .builder()
                .couponId(couponId)
                .userId(userId)
                .build();

        return couponIssueJpaRepository.save(issue);

    }

    private void checkAlreadyIssue(long couponId, long userId) {
        CouponIssue issue = couponIssueJpaRepository.findFirstCouponIssue(couponId, userId);
        if (issue != null) {
            throw new CouponIssueException(DUPLICATED_COUPON_ISSUE, "이미 발급된 쿠폰입니다. user_id: %s, coupon_id: %s".formatted(userId, couponId));

        }

    }


}
