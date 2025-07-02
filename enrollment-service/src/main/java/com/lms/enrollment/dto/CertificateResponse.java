package com.lms.enrollment.dto;

import lombok.Data;

@Data
public class CertificateResponse {
    private Long certificateId;
    private String certificateNumber;
    private String pdfUrl;
}