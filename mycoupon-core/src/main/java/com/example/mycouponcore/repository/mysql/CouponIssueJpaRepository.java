package com.example.mycouponcore.repository.mysql;

import com.example.mycouponcore.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository  extends JpaRepository<CouponIssue, Long>, CouponIssueRepository  {
}
