package com.mastercard.csp.dataflow.cspdataflowservice.ziparchiveservice;

import com.mastercard.csp.dataflow.cspdataflowservice.config.CspConfiguration;
import com.mastercard.csp.dataflow.cspdataflowservice.model.FlowFileAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ZipArchiveService {

    Logger LOG = LoggerFactory.getLogger(ZipArchiveService.class.getName());

    private final CspConfiguration cspConfiguration;

    public ZipArchiveService(CspConfiguration cspConfiguration) {
        this.cspConfiguration = cspConfiguration;
    }

    public void process(FlowFileAttribute flowFileAttribute) {
        try {

            Path archiveDir = Paths.get(
                    cspConfiguration.fileBasePath(),
                    cspConfiguration.archiveFolder());

            Files.createDirectories(archiveDir);

            Path source = flowFileAttribute.getPath();
            Path destination = archiveDir.resolve(source.getFileName());

            LOG.info("Archiving {} -> {}", source, destination);

            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);

            LOG.info("Successfully archived {}", source.getFileName());

        } catch (Exception ex) {
            LOG.error("Failed to archive file {}", flowFileAttribute.getPath(), ex);
        }
    }
}
