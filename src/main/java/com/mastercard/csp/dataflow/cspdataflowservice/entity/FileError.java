package com.mastercard.csp.dataflow.cspdataflowservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "file_error", schema = "core")
public class FileError {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "file_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_file_error_file")
    )
    private FileMetadata file;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FileErrorType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private FileErrorSeverity severity;

    @Column(name = "record_id", length = 100)
    private String recordId;

    @Column(name = "service_number", length = 100)
    private String serviceNumber;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;

    @Column(name = "sent_to_stakeholder", nullable = false)
    private Boolean sentToStakeholder = false;
}