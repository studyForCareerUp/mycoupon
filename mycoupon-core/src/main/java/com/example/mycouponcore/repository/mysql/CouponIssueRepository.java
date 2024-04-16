package com.example.mycouponcore.repository.mysql;

import com.example.mycouponcore.model.CouponIssue;

public interface CouponIssueRepository {

    CouponIssue findFirstCouponIssue(long couponId, long userId);
}
