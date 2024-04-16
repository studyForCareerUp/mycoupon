package com.example.mycouponcore.repository.mysql;


import com.example.mycouponcore.model.CouponIssue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.mycouponcore.model.QCouponIssue.couponIssue;


@Repository
@RequiredArgsConstructor
public class CouponIssueRepositoryImpl implements CouponIssueRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public CouponIssue findFirstCouponIssue(long couponId, long userId) {

        return queryFactory.selectFrom(couponIssue)
                .where(couponIssue.couponId.eq(couponId)
                        .and(couponIssue.userId.eq(userId)))
                .fetchFirst();
    }

}
