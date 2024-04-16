package com.example.mycouponcore.model;

import com.example.mycouponcore.exception.CouponIssueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.example.mycouponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.example.mycouponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @DisplayName("발급 수량이 남아 있다면 true를 반환한다.")
    @Test
    void availableIssueQuantityWithSpareIssueQuantity() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        assertThat(result).isTrue();

    }

    @DisplayName("발급 수량이 남아 있다면 false를 반환한다.")
    @Test
    void availableIssueQuantityWithNoSpareIssueQuantity() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        assertThat(result).isFalse();

    }

    @DisplayName("최대 발급 수량이 설정 되어 있지 않다면 true를 반환한다.")
    @Test
    void availableIssueQuantityWithEmptyTotalQuantity() {
        // given
        Coupon coupon = Coupon.builder()
                .issuedQuantity(100)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        assertThat(result).isTrue();

    }

    @DisplayName("발급 기간이 시작되지 않았다면 false를 반환한다.")
    @Test
    void availableIssueDateWhenNotStartTheIssueDate() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("발급 기간이 해당되면 true를 반환한다.")
    @Test
    void availableIssueDateWhenIncludeTheIssueDate() {
        // given
        Coupon coupon = Coupon
                .builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd((LocalDateTime.now().plusDays(2)))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("발급 기간이 종료되었다면 false를 반환한다.")
    @Test
    void availableIssueDateWhenEndTheIssueDate() {
        // given
        Coupon coupon = Coupon
                .builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd((LocalDateTime.now().minusDays(2)))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        assertThat(result).isFalse();
    }



    @DisplayName("발급 수량과 발급 기간이 유효하다면 발급에 성공한다.")
    @Test
    void issue_1() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd((LocalDateTime.now().plusDays(2)))
                .build();

        // when
        coupon.issue();

        // then
        assertThat(coupon.getIssuedQuantity()).isEqualTo(100);
    }

    @DisplayName("발급 수량을 초과하면 예외를 반환한다.")
    @Test
    void issue_2() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd((LocalDateTime.now().plusDays(2)))
                .build();

        assertThatThrownBy(coupon::issue)
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode")
                .isEqualTo(INVALID_COUPON_ISSUE_QUANTITY);
    }

    @DisplayName("발급 기간이 아니면 예외를 반환한다.")
    @Test
    void issue_3() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd((LocalDateTime.now().plusDays(2)))
                .build();

        // when &  then
        assertThatThrownBy(coupon::issue)
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode")
                .isEqualTo(INVALID_COUPON_ISSUE_DATE);
    }

}