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
    //@HystrixCommand(fallbackMethod = "buildFallbackLicenseList")
    @HystrixCommand(fallbackMethod = "buildFallbackLicenseList",
            threadPoolKey = "licenseByOrgThreadPool",
            threadPoolProperties = /* The threadPoolProperties attribute lets you define and customize the behavior of the threadPool.*/
                    {@HystrixProperty(name = "coreSize",value="30"), /*The coreSize attribute lets you define the maximum number of
                                                                         threads in the thread pool.*/
                            @HystrixProperty(name="maxQueueSize", value="10")}, /*The maxQueueSize lets you define a queue
                                                                                that sits in front of your thread pool and
                                                                                 that can queue incoming requests.*/
                    commandProperties={
                            /* Controls the amount of consecutive calls that must occur within a 10-second window before Hystrix
                               will consider tripping the circuit breaker for the call.*/
                            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),

                            /* is the percentage of calls that must fail (due to timeouts, an exception being thrown, or a HTTP 500 being returned) after the
                               circuitBreaker.requestVolumeThreshold value has been passed before the circuit breaker it tripped. */
                            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),

                            /* is the amount of time Hystrix will sleep once the circuit breaker is tripped before Hystrix will allow another call through to
                                see if the service is healthy again. */
                            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),

                            /* is used to control the size of the window that will be used by Hystrix to monitor for problems with a ser-
                               vice call. The default value for this is 10,000 milliseconds (that is, 10 seconds).*/
                            @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),

                            /* controls the number of times statistics are collected in the window you’ve defined. Hystrix collects met-
                               rics in buckets during this window and checks the stats in those buckets to determine
                               if the remote resource call is failing.*/
                            @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")}
    )
           // commandProperties=
             //       {@HystrixProperty(
               //             name="execution.isolation.thread.timeoutInMilliseconds",
                 //           value="12000")})
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
