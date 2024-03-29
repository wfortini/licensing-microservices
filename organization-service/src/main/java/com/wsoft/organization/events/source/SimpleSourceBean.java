package com.wsoft.organization.events.source;

import com.wsoft.organization.events.model.OrganizationChangeModel;
import com.wsoft.organization.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimpleSourceBean {
    private Source source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    @Autowired
    public SimpleSourceBean(Source source){
        this.source = source; // Spring Cloud Stream will inject a
                              //Source interface implementation
                              //for use by the service.
    }

    public void publishOrgChange(String action,String orgId){
        logger.debug("Sending Kafka message {} for Organization Id: {}", action, orgId);
        OrganizationChangeModel change =  new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action,
                orgId,
                UserContext.getCorrelationId());

        boolean ok = source.output().send(MessageBuilder.withPayload(change).build());
        logger.debug("Sending Kafka message {} send: {}", action, ok);
    }
}
