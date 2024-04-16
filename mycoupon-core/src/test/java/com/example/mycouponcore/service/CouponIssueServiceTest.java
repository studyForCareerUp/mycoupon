package com.example.mycouponcore.service;

import com.example.mycouponcore.TestConfig;
import com.example.mycouponcore.exception.CouponIssueException;
import com.example.mycouponcore.exception.ErrorCode;
import com.example.mycouponcore.model.Coupon;
import com.example.mycouponcore.model.CouponIssue;
import com.example.mycouponcore.model.CouponType;
import com.example.mycouponcore.repository.mysql.CouponIssueJpaRepository;
import com.example.mycouponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.example.mycouponcore.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponIssueServiceTest extends TestConfig {

    @Autowired
    CouponIssueService sut;

    @Autowired
    CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void setUp() {
        // init data set(수정해도 모든 테스트에 영향을 주지 않는다면)
        // 테스트 실행전에 config 설정 or 초기 데이터 셋팅에 주로 사용
    }

    @AfterEach
    void tearDown() {
        // data cleaning
        couponJpaRepository.deleteAllInBatch();
        couponIssueJpaRepository.deleteAllInBatch();
    }


    @DisplayName("발급 수량, 발급 기한, 중복 발급 문제 없다면 쿠폰을 발급한다.")
    @Test
    void issue_1() {
        // given
        long userId = 1;

        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        // when
        sut.issue(coupon.getId(), userId);

        // then
        Coupon couponResult = couponJpaRepository
                .findById(coupon.getId())
                .orElseThrow(() -> {
                    throw new CouponIssueException(ErrorCode.COUPON_NOT_EXIST, "존재하지 않는 쿠폰입니다.");
                });
        assertThat(couponResult.getIssuedQuantity()).isEqualTo(1);

        CouponIssue couponIssueResult = couponIssueJpaRepository.findFirstCouponIssue(coupon.getId(), userId);
        assertThat(couponIssueResult).isNotNull();

    }

    @DisplayName("발급 수량이 문제가 있으면 예외가 발생한다.")
    @Test
    void issue_2() {
        // given
        long userId = 1;

        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        // when && then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("ErrorCode")
                .isEqualTo(INVALID_COUPON_ISSUE_QUANTITY);

    }

    @DisplayName("발급 기한에 문제가 있으면 예외가 발생한다.")
    @Test
    void issue_3() {
        // given
        long userId = 1;

        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().plusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        // when && then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("ErrorCode")
                .isEqualTo(INVALID_COUPON_ISSUE_DATE);

    }

    @DisplayName("쿠폰 중복 발급 검증에 문제가 예외가 발생한다.")
    @Test
    void issue_4() {
        // given
        long userId = 1;

        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        CouponIssue couponIssue = CouponIssue
                .builder()
                .userId(userId)
                .couponId(coupon.getId())
                .build();

        couponIssueJpaRepository.save(couponIssue);


        // when && then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("ErrorCode")
                .isEqualTo(DUPLICATED_COUPON_ISSUE);

    }

    @DisplayName("쿠폰이 존재하지 않는다면 예외가 발생한다.")
    @Test
    void issue_5() {
        // given
        long userId = 1;
        long couponId = 1;

        // when && then
        assertThatThrownBy(() -> sut.issue(couponId, userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("ErrorCode")
                .isEqualTo(COUPON_NOT_EXIST);

    }

    @DisplayName("쿠폰이 존재하면 쿠폰을 조회한다.")
    @Test
    void findCoupon_1() {
        // given
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        // when
        Coupon result = sut.findCoupon(coupon.getId());

        // then
        assertThat(result).isNotNull();
    }

    @DisplayName("쿠폰이 존재하지 않으면 예외가 발생한다.")
    @Test
    void findCoupon_2() {
        // given
        long couponId = 1;

        // when && then
        assertThatThrownBy(() -> sut.findCoupon(couponId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode")
                .isEqualTo(COUPON_NOT_EXIST);
    }

    @DisplayName("다른 쿠폰을 조회하면 예외가 발생한다.")
    @Test
    void findCoupon_3() {
        // given
        long couponId = 100;

        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        // when && then
        assertThatThrownBy(() -> sut.findCoupon(couponId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode")
                .isEqualTo(COUPON_NOT_EXIST);
    }

    @DisplayName("쿠폰 발급 내역이 존재하면 예외가 발생한다.")
    @Test
    void saveCouponIssue_1() {
        // given
        long userId = 1;
        long couponId= 1;
        CouponIssue couponIssue = CouponIssue
                .builder()
                .couponId(couponId)
                .userId(userId)
                .build();

        couponIssueJpaRepository.save(couponIssue);

        // when && then
        assertThatThrownBy(() -> sut.saveCouponIssue(couponId, userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode")
                .isEqualTo(DUPLICATED_COUPON_ISSUE);

    }

    @DisplayName("쿠폰 발급 내역이 존재하지 않으면 쿠폰을 발급한다.")
    @Test
    void saveCouponIssue_2() {
        // given
        long userId = 1;
        long couponId= 1;

        // when
        sut.saveCouponIssue(couponId, userId);

        // then
        CouponIssue result = couponIssueJpaRepository.findFirstCouponIssue(couponId, userId);
        assertThat(result).isNotNull();
    }
}