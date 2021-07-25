package com.wsoft.organization.services;

import com.wsoft.organization.events.source.SimpleSourceBean;
import com.wsoft.organization.model.Organization;
import com.wsoft.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    SimpleSourceBean simpleSourceBean;

    public Organization getOrg(String organizationId) {
        return orgRepository.findById(organizationId).get();
    }

    public void saveOrg(Organization org){
        org.setId( UUID.randomUUID().toString() );

        orgRepository.save(org);
        simpleSourceBean.publishOrgChange("SAVE", org.getId());

    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
        simpleSourceBean.publishOrgChange("UPDATE", org.getId());
    }

    public void deleteOrg(Organization org){
        orgRepository.delete(org);
        simpleSourceBean.publishOrgChange("DELETE", org.getId());
    }
}
