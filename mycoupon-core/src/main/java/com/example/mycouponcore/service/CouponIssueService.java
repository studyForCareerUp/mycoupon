package com.example.mycouponcore.service;


import com.example.mycouponcore.exception.CouponIssueException;
import com.example.mycouponcore.model.Coupon;
import com.example.mycouponcore.model.CouponIssue;
import com.example.mycouponcore.repository.mysql.CouponIssueJpaRepository;
import com.example.mycouponcore.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.mycouponcore.exception.ErrorCode.COUPON_NOT_EXIST;
import static com.example.mycouponcore.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponIssueService {
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponJpaRepository couponJpaRepository;

    @Transactional
    public void issue(long couponId, long userId) {

//        synchronized (this) {
//            Coupon coupon = findCoupon(couponId);
//            coupon.issue();
//            saveCouponIssue(couponId, userId);
//        }

        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }

     /*
    트랜잭션 시작
    2번 요청

    lock 획득
    Coupon coupon = findCoupon(couponId); <---- 여기서 문제 발생!!!!
    coupon.issue();
    saveCouponIssue(couponId, userId);
    lock 반납

    1번 요청

    트랜잭션 커밋

    아직 1번 요청이 커밋이 되지 않았는데, 2번요청이 findCoupon(couponId); 실행하여 아직 커밋되기 전에 데이터를 조회하여 프로세스를 진행한다.
    트랜잭션 내부에서 lock을 여는 행위는 주의해야 함!!!!!!!


     */

    /*
    lock 획득

    트랜잭션 시작

    Coupon coupon = findCoupon(couponId);
    coupon.issue();
    saveCouponIssue(couponId, userId);


    1번 요청

    트랜잭션 커밋

    lock 반납

    이렇게 바꿔어야 문제 해결



     */

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
