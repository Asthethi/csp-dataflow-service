package com.mastercard.csp.dataflow.cspdataflowservice.ziparchiveservice;

import com.mastercard.csp.dataflow.cspdataflowservice.model.FlowFileAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class ZipChecksumService {

    private static final Logger LOG = LoggerFactory.getLogger(ZipChecksumService.class);
    private static final String CHECKSUM_ALGORITHM = "SHA-256";
    private static final int BUFFER_SIZE = 8192;

    private ZipMetadata nextProcess;

    public ZipChecksumService(ZipMetadata nextProcess) {
        this.nextProcess = nextProcess;
    }

    public void process(FlowFileAttribute flowFileAttribute) {
        Path filePath = flowFileAttribute.getPath();

        try {
            String checksum = calculateChecksum(filePath);
            flowFileAttribute.getAttributes().put("checksum", checksum);
            //flowFileAttribute.getAttributes().put("checksumAlgorithm", CHECKSUM_ALGORITHM); // NOT NEEDED AS AN ATTRIBUTE AT THE MOMENT

            LOG.info("ZipChecksumService : Calculated {} checksum for file {}", CHECKSUM_ALGORITHM, filePath.toAbsolutePath());
            this.nextProcess.process(flowFileAttribute);
        } catch (IOException e) {
            LOG.warn("Unable to calculate checksum for file {}", filePath.toAbsolutePath(), e);
        }
    }

    private String calculateChecksum(Path filePath) throws IOException {
        MessageDigest digest = createDigest();
        byte[] buffer = new byte[BUFFER_SIZE];

        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(filePath))) {
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }

    private MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance(CHECKSUM_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(CHECKSUM_ALGORITHM + " algorithm is not available", e);
        }
    }
}
