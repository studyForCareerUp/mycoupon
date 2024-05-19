package com.example.mycouponcore.repository.mysql;

import com.example.mycouponcore.model.Coupon;
import com.example.mycouponcore.model.CouponIssue;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponIssueJpaRepository  extends JpaRepository<CouponIssue, Long>, CouponIssueRepository  {

}
