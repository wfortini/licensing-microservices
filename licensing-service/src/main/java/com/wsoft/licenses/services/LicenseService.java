package com.wsoft.licenses.services;

import com.wsoft.licenses.config.ServiceConfig;
import com.wsoft.licenses.model.License;
import com.wsoft.licenses.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LicenseService {
    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    public License getLicense(String organizationId,String licenseId) {
       // License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        return new License().withComment(config.getExampleProperty());
    }

    /**
    public List<License> getLicensesByOrg(String organizationId){
        return licenseRepository.findByOrganizationId( organizationId );
    }
     **/

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
}
