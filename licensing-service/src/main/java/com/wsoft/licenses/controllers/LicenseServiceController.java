package com.wsoft.licenses.controllers;

import com.wsoft.licenses.model.License;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/organizations/{organizationId}/licenses")
public class LicenseServiceController {

    @RequestMapping(value = "/{licenseId}", method = RequestMethod.GET)
    public License getLicense(@PathVariable("organizationId")  String organizationId,
                              @PathVariable("licenseId") String licenseId){
              return new License()
                      .withId(licenseId)
                      .withProductName("Teleco")
                      .withLicenseType("Seat")
                      .withOrganizationId("TestOrg");
    }
}
