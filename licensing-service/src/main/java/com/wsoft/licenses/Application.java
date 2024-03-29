package com.wsoft.licenses;

import com.wsoft.licenses.config.ServiceConfig;
import com.wsoft.licenses.events.model.OrganizationChangeModel;
import com.wsoft.licenses.utils.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@EnableDiscoveryClient   /*  enable the application to use the Discovery client and Ribbon libraries */
@SpringBootApplication
@EnableCircuitBreaker   /* Tells Spring Cloud you’re going to use Hystrix for your service */
@EnableFeignClients
@EnableResourceServer
@EnableBinding(Sink.class) /*
                               Because the licensing service is a consumer of a message, you’re going to pass the
                               @EnableBinding annotation the value Sink.class . This tells Spring Cloud Stream
                                to bind to a message broker using the default Spring Sink interface.
                           */
public class Application {

    @Autowired
    private ServiceConfig serviceConfig;

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     *  Spring security OAuth proparga token atraves de chamadas
     *

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oauth2ClientContext,
                                                 OAuth2ProtectedResourceDetails details) {
        return new OAuth2RestTemplate(details, oauth2ClientContext);
    }
    */

    /*
         Invoking services with Ribbon-aware Spring RestTemplate
         use a RestTemplate that’s Ribbon-
         aware. This is one of the more common mechanisms for interacting with Ribbon via
         Spring.

         The @LoadBalanced annotation tells Spring Cloud to create a
         Ribbon backed RestTemplate class.
     */
    //@LoadBalanced
    @Primary
    @Bean
    public RestTemplate getCustomREstTemplate(){

        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors==null){
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        }
        else{
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
        jedisConnFactory.setHostName( serviceConfig.getRedisServer());
        jedisConnFactory.setPort( serviceConfig.getRedisPort() );
        return jedisConnFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    // estou usando CustomChannels.class
    //@StreamListener(Sink.INPUT)
    //public void loggerSink(OrganizationChangeModel orgChange) {
    //    logger.info("Received an event for organization id {}", orgChange.getOrganizationId());
    //    logger.debug("Received an event for organization id {}", orgChange.getOrganizationId());
   // }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
