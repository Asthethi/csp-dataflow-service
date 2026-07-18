package com.mastercard.csp.dataflow.cspdataflowservice.zipvalidationservice;

import com.mastercard.csp.dataflow.cspdataflowservice.model.FlowFileAttribute;

public interface FileValidator  {

    public boolean validateFile(FlowFileAttribute flowFileAttribute);

}
