package com.wsoft.licenses.clients;

import com.netflix.discovery.DiscoveryClient;
import com.wsoft.licenses.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {

      /*
         Invoking services with Ribbon-aware Spring RestTemplate
         use a RestTemplate that’s Ribbon-
         aware. This is one of the more common mechanisms for interacting with Ribbon via
         Spring.

         The @LoadBalanced annotation tells Spring Cloud to create a
         Ribbon backed RestTemplate class.

         you need to define a Rest-Template bean construction method with a Spring Cloud annotation called @Load-
         Balanced in class Application.class .
     */

    @Autowired
    RestTemplate restTemplate;

    public Organization getOrganization(String organizationId){
        /*
           http://{applicationid}/v1/organizations/{organizationId}
         */
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        "http://organizationservice/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
