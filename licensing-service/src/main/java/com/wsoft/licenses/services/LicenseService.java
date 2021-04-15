package com.wsoft.licenses.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.wsoft.licenses.clients.OrganizationDiscoveryClient;
import com.wsoft.licenses.clients.OrganizationFeignClient;
import com.wsoft.licenses.clients.OrganizationRestTemplateClient;
import com.wsoft.licenses.config.ServiceConfig;
import com.wsoft.licenses.model.License;
import com.wsoft.licenses.model.Organization;
import com.wsoft.licenses.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class LicenseService {
    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;

    @HystrixCommand
    private Organization retrieveOrgInfo(String organizationId, String clientType){
        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
        }

        return organization;
    }

    public License getLicense(String organizationId,String licenseId, String clientType) {
        License license = licenseRepository.findByOrganizationIdAndId(organizationId, licenseId);

        Organization org = retrieveOrgInfo(organizationId, clientType);

        return license.withOrganizationName(org.getName())
                .withContactEmail(org.getContactName())
                .withContactEmail(org.getContactEmail())
                .withContactPhone(org.getContactPhone())
                .withComment(config.getExampleProperty());
    }

    /*
        @HystrixCommand annotation is used to wrapper the getLicenseByOrg() method with a Hystrix circuit breaker
        execution.isolation.thread.timeoutInMilliseconds property to set the maximum timeout a Hystrix call will
        wait before failing to be 12 seconds.

     */
    @HystrixCommand(fallbackMethod = "buildFallbackLicenseList",
            commandProperties = {@HystrixProperty(
            name = "execution.isolation.thread.timeoutInMilliseconds",
            value = "6000"
    )})
    public List<License> getLicensesByOrg(String organizationId){
        randomlyRunLong(); // slow

        return licenseRepository.findByOrganizationId( organizationId );
    }

    public void saveLicense(License license){
        license.withId( UUID.randomUUID().toString());

        licenseRepository.save(license);

    }

    public void updateLicense(License license){
        licenseRepository.save(license);
    }

    public void deleteLicense(License license){
        licenseRepository.delete( license );
    }

    private void randomlyRunLong(){
        Random rand = new Random();

        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        if (randomNum==3) sleep();
    }
    private void sleep(){
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
         Method fallback
     */
    private List<License> buildFallbackLicenseList(String organizationId){
        List<License> fallbackList = new ArrayList<>();
        License license = new License()
                .withId("0000000-00-00000")
                .withOrganizationId( organizationId )
                .withProductName("Sorry no licensing information currently available");

        fallbackList.add(license);
        return fallbackList;
    }
}
