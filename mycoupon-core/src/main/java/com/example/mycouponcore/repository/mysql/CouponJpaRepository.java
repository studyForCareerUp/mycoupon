package com.example.mycouponcore.repository.mysql;

import com.example.mycouponcore.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

}
