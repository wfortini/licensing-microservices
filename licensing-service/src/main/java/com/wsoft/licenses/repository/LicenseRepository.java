package com.wsoft.licenses.repository;

import com.wsoft.licenses.model.License;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends CrudRepository<License, String> {
    public List<License> findByOrganizationId(String organizationId);
    //public License findByOrganizationIdAndLicenseId(String organizationId,String licenseId);
}
