package com.example.course.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    protected LocalDateTime createdAt;
    @Column(name = "created_by", updatable = false)
    protected String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    protected LocalDateTime updatedAt;
    protected String updatedBy;
}
