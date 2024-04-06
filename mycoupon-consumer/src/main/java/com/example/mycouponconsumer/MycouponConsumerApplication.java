package com.example.mycouponconsumer;

import com.example.mycouponcore.MyCouponCoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(MyCouponCoreConfiguration.class)
@SpringBootApplication
public class MycouponConsumerApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application-core, application-consumer");
        SpringApplication.run(MycouponConsumerApplication.class, args);
    }

}
