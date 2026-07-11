package com.mastercard.csp.dataflow.cspdataflowservice.ziparchiveservice;

import com.mastercard.csp.dataflow.cspdataflowservice.model.FlowFileAttribute;
import org.springframework.stereotype.Service;

import java.nio.file.Files;

@Service
public class ZipArchiveService {

    public void process(FlowFileAttribute flowFileAttribute) {
        try{
            Files.deleteIfExists(flowFileAttribute.getPath());
        } catch (Exception ex) {
            System.out.println("Could not delete file: " + flowFileAttribute.getPath());
        }

        System.out.println("Processing Zip Archive : Yet to be implemented");
    }
}
