package com.mastercard.csp.dataflow.cspdataflowservice.zipvalidationservice;

import com.mastercard.csp.dataflow.cspdataflowservice.config.CspConfiguration;
import com.mastercard.csp.dataflow.cspdataflowservice.model.FlowFileAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class ZipFileNameValidator implements FileValidator {

    Logger LOG = LoggerFactory.getLogger(ZipFileNameValidator.class);

    private final CspConfiguration cspConfiguration;

    ZipFileNameValidator(CspConfiguration cspConfiguration) {
        this.cspConfiguration = cspConfiguration;
    }

    @Override
    public boolean validateFile(FlowFileAttribute flowFileAttribute) {
        LOG.info("csp zip File Filter {}", this.cspConfiguration.zipFileFilter());
        Pattern pattern = Pattern.compile(this.cspConfiguration.zipFileFilter());
        String fileName = flowFileAttribute.getAttributes().get("filename");
        return Objects.nonNull(fileName) && pattern.matcher(fileName).matches();
    }
}
