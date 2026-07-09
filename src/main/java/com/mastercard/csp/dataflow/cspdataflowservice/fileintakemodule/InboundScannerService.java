package com.mastercard.csp.dataflow.cspdataflowservice.fileintakemodule;


import com.mastercard.csp.dataflow.cspdataflowservice.config.CspConfiguration;
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
import java.util.HashSet;
import java.util.Set;

@Service
public class InboundScannerService {

    private static final Logger LOG = LoggerFactory.getLogger(InboundScannerService.class);

    private final CspConfiguration cspConfiguration;
    private final Set<Path> processedFiles = new HashSet<>();

    public InboundScannerService(CspConfiguration cspConfiguration) {
        this.cspConfiguration = cspConfiguration;
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
        System.out.println("Cutoff : "+cutOff.toString());

        try(DirectoryStream<Path> files = Files.newDirectoryStream(inboundScanPath)) {
            for(Path file : files) {
                LOG.info("Processing file: {}", file.toAbsolutePath());

                if(!this.performBasicFileChecks(file)) {
                    continue;
                }

                //check if the file has been already been processed.
                if(this.processedFiles.contains(file)) continue;

                BasicFileAttributes fileAttriburtes = Files.readAttributes(file, BasicFileAttributes.class);
                System.out.println("Lastmodified time of file : "+fileAttriburtes.lastModifiedTime().toInstant().toString());
                if(fileAttriburtes.lastModifiedTime().toInstant().isAfter(cutOff)) continue;

                // its a new file add it in Set
                this.processedFiles.add(file);
                LOG.info("File {} processed, now deleting the file .... ", file.toAbsolutePath());
                Files.delete(file);
            }
        } catch (IOException e) {
            LOG.warn("Error processing files", e);
        }

    }

    private boolean performBasicFileChecks(Path file) {
        String fileName = file.getFileName().toString();
        Path absolutePath = file.toAbsolutePath();

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

}
