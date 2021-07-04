package com.wsoft.licenses.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * While  Spring Data  “auto-magically” injects the configuration
 * data  for the  database into  a  database  connection object,
 * all other  properties  must be  injected  using the @Value  annotation.
 * With the  previous example,  the  @Value  annotation
 * pulls the example.property  from the  Spring  Cloud
 * configuration server  and injects it into the  example.property
 * attribute  on  the  ServiceConfig  class.
 *
 */
@Component
public class ServiceConfig {

    @Value("${example.property}")
    private String exampleProperty;

    @Value("${redis.server}")
    private String redisServer="";

    @Value("${redis.port}")
    private String redisPort="";

    @Value("${signing.key}")
    private String jwtSigningKey="";


    public String getJwtSigningKey() {
        return jwtSigningKey;
    }

    public String getRedisServer(){
        return redisServer;
    }

    public Integer getRedisPort(){
        return new Integer( redisPort ).intValue();
    }

    public String getExampleProperty(){
        return exampleProperty;
    }
}
