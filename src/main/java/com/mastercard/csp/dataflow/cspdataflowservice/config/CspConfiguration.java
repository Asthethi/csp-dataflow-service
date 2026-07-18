package com.mastercard.csp.dataflow.cspdataflowservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "csp")
public record CspConfiguration(
        String fileBasePath,
        String inboundFolder,
        String outboundFolder,
        String archiveFolder,
        String backupZipFolder,
        String zipFileFilter,
        Scanner scanner
) {

    public record Scanner(long fixedDelayMs, long minimumFileAgeMs, boolean includeHidden){}
}