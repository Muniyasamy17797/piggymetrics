package com.lms.certificate.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "certificates")
@EntityListeners(AuditingEntityListener.class)
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    
    private Long courseId;
    
    private String courseName;
    
    private String studentName;
    
    private String certificateNumber;
    
    private Double finalGrade;
    
    @CreatedDate
    private LocalDateTime issuedAt;
    
    private String pdfUrl;
}