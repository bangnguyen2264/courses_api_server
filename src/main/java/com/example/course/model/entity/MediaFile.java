package com.example.course.model.entity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "media_file")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile extends BaseEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "data", unique = false, nullable = false)
    private byte[] data;
    private boolean isPublic; // true = public, false = private

}
