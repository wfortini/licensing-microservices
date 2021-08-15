package com.wsoft.licenses.clients;

import com.netflix.discovery.DiscoveryClient;
import com.wsoft.licenses.model.Organization;
import com.wsoft.licenses.repository.OrganizationRedisRepository;
import com.wsoft.licenses.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

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

    @Autowired
    OrganizationRedisRepository orgRedisRepo;

    private Organization checkRedisCache(String organizationId) {
        try {
            return orgRedisRepo.findOrganization(organizationId);
        }
        catch (Exception ex){
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache.  Exception {}", organizationId, ex);
            return null;
        }
    }

    private void cacheOrganizationObject(Organization org) {
        try {
            orgRedisRepo.saveOrganization(org);
        }catch (Exception ex){
            logger.error("Unable to cache organization {} in Redis. Exception {}", org.getId(), ex);
        }
    }


    public Organization getOrganization(String organizationId){
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());
        /*
           http://{applicationid}/v1/organizations/{organizationId}
         */
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

        Organization org = checkRedisCache(organizationId);

        if (org!=null){
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}", organizationId, org);
            return org;
        }

        logger.debug("Unable to locate organization from the redis cache: {}.", organizationId);
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        //http://organizationservice/v1/organizations/{organizationId}",
                        "http://zuulserver:5555/api/organization/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
