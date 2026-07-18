package com.mastercard.csp.dataflow.cspdataflowservice.zipvalidationservice;

import com.mastercard.csp.dataflow.cspdataflowservice.model.FlowFileAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ZipValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ZipValidationService.class);

    private final ZipFileNameValidator zipFileNameValidator;


    public ZipValidationService(ZipFileNameValidator zipFileNameValidator) {
        this.zipFileNameValidator = zipFileNameValidator;
    }

    public void process(FlowFileAttribute flowFileAttribute) {


        if (flowFileAttribute == null || flowFileAttribute.getPath() == null) {
            LOG.warn("Rejecting inbound file because file attributes or path are missing");
            return;
        }


        if (!zipFileNameValidator.validateFile(flowFileAttribute)) {
            LOG.warn("Rejecting inbound file {} because filename does not support the allowed file pattern",
                    flowFileAttribute.getPath().toAbsolutePath());
            return;
        }

        LOG.info("Validated inbound file {}", flowFileAttribute.getPath().toAbsolutePath());
    }
}
