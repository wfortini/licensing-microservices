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

    public String getExampleProperty(){
        return exampleProperty;
    }
}
