package com.mastercard.csp.dataflow.cspdataflowservice.model;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.HashMap;

@Getter
@Setter
public class FlowFileAttribute {
    private Path path;
    private long size;
    private HashMap<String, String> attributes;

    public FlowFileAttribute(Path path, long size) {
        this.path = path;
        this.size = size;
        this.attributes = new HashMap<>();
        this.attributes.put("filename", path.getFileName().toString());
        this.attributes.put("absolutePath", path.toAbsolutePath().toString());
    }

}

