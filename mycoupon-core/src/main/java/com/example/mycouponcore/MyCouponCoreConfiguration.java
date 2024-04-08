package com.example.mycouponcore;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableAutoConfiguration
@ComponentScan
@EnableJpaAuditing
public class MyCouponCoreConfiguration {
}
