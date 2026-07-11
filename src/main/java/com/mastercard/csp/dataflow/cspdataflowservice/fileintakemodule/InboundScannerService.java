package com.mastercard.csp.dataflow.cspdataflowservice.fileintakemodule;


import com.mastercard.csp.dataflow.cspdataflowservice.config.CspConfiguration;
import com.mastercard.csp.dataflow.cspdataflowservice.model.FlowFileAttribute;
import com.mastercard.csp.dataflow.cspdataflowservice.ziparchiveservice.ZipChecksumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class InboundScannerService {

    private static final Logger LOG = LoggerFactory.getLogger(InboundScannerService.class);

    private final CspConfiguration cspConfiguration;
    private final ZipChecksumService nextProcess;
    private final Set<Path> processedFiles = new HashSet<>();

    public InboundScannerService(CspConfiguration cspConfiguration, ZipChecksumService nextProcess) {
        this.cspConfiguration = cspConfiguration;
        this.nextProcess = nextProcess;
    }

    @Scheduled(fixedDelayString = "${csp.scanner.fixed-delay-ms}")
    public void scanInbound() {

        Path inboundScanPath = Paths.get(
                this.cspConfiguration.fileBasePath(),
                this.cspConfiguration.inboundFolder());

        if(!Files.isDirectory(inboundScanPath)) {
            LOG.warn("Inbound scan directory is not a directory");
            return;
        }

        long minAgeMs = cspConfiguration.scanner().minimumFileAgeMs();
        Instant cutOff = Instant.now().minus(Duration.ofMillis(minAgeMs));

        try(DirectoryStream<Path> files = Files.newDirectoryStream(inboundScanPath)) {
            for(Path file : files) {
                LOG.info("Processing file: {}", file.toAbsolutePath());

                if(!this.performBasicFileChecks(file)) {
                    processedFiles.remove(file);
                    continue;
                }

                //check if the file has been already been processed.
                if(this.processedFiles.contains(file)) continue;

                BasicFileAttributes fileAttributes = Files.readAttributes(file, BasicFileAttributes.class);
                if(fileAttributes.lastModifiedTime().toInstant().isAfter(cutOff)) continue;

                // its a new file add it in Set
                this.processedFiles.add(file);

                // call next process to create a new parent zip file entry in database
                nextProcess.process(getFlowFileAttributes(file, fileAttributes.size()));


            }
        } catch (IOException e) {
            LOG.warn("Error processing files", e);
        }

    }

    private boolean performBasicFileChecks(Path file) {
        String fileName = file.getFileName().toString();
        Path absolutePath = file.toAbsolutePath();

        if(!Files.exists(absolutePath)) {
            LOG.warn("File {} does not exist", absolutePath);
            return false;
        }

        if(!Files.isReadable(absolutePath)) {
            LOG.warn("File {} is not readable", absolutePath);
            return false;
        }

        if (!Files.isRegularFile(file)) {
            LOG.error("File {} is not a regular file", absolutePath);
            return false;
        }

        if (!cspConfiguration.scanner().includeHidden() && fileName.startsWith(".")) {
            LOG.info("File {} is hidden", absolutePath);
            return false;
        }

        if (fileName.endsWith(".zip")) {
            LOG.info("File {} with extension .zip is not allowed", absolutePath);
            return false;
        }

        return true;
    }

    private FlowFileAttribute getFlowFileAttributes(Path path, long size) {
       FlowFileAttribute flowFileAttribute = new FlowFileAttribute(path, size);
       flowFileAttribute.getAttributes().put("file.id", UUID.randomUUID().toString());
       flowFileAttribute.getAttributes().put("parent.id", "");
        return flowFileAttribute;
    }

}
