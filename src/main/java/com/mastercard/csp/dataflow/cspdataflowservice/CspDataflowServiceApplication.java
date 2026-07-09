package com.mastercard.csp.dataflow.cspdataflowservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class CspDataflowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CspDataflowServiceApplication.class, args);
    }

}
