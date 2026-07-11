package com.mastercard.csp.dataflow.cspdataflowservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "file_metadata", schema = "core")
public class FileMetadata {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    private UUID id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "parent_id",
//            foreignKey = @ForeignKey(name = "fk_file_metadata_parent")
//    )
//    private FileMetadata parent;

    @Column(name = "parent_id", columnDefinition = "parent id reference to self metadata table for child data files not for zip files")
    private UUID parentId;

//    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
//    private List<FileMetadata> children = new ArrayList<>();

    // TODO : check this afterwards
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "stakeholder_id",
//            foreignKey = @ForeignKey(name = "fk_file_metadata_stakeholder")
//    )
//    private Stakeholder stakeholder;

    @Column(name = "stakeholder_id", columnDefinition = "uuid")
    private UUID stakeholderId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

//    @Column(name = "status", nullable = false)
//    private String status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "core.file_status")
    private FileStatus status;

    @Column(name = "checksum", length = 128)
    private String checksum;

    @Column(name = "size")
    private Long size;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

}