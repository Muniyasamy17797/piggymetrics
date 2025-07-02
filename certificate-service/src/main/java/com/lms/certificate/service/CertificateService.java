package com.lms.certificate.service;

import com.lms.certificate.model.Certificate;
import org.springframework.core.io.Resource;

public interface CertificateService {
    Certificate generateCertificate(Long studentId, Long courseId, Double finalGrade);
    Resource downloadCertificate(Long certificateId);
    Certificate getCertificate(Long certificateId);
    boolean verifyCertificate(String certificateNumber);
}