package com.ale.productmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class ProductmanagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductmanagementApplication.class, args);
    }

}
