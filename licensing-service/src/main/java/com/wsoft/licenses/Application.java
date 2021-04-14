package com.wsoft.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient   /*  enable the application to use the Discovery client and Ribbon libraries */
@SpringBootApplication
@EnableFeignClients
public class Application {

    /*
         Invoking services with Ribbon-aware Spring RestTemplate
         use a RestTemplate that’s Ribbon-
         aware. This is one of the more common mechanisms for interacting with Ribbon via
         Spring.

         The @LoadBalanced annotation tells Spring Cloud to create a
         Ribbon backed RestTemplate class.
     */
    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
