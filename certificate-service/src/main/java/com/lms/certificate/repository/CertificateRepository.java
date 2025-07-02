package com.lms.certificate.repository;

import com.lms.certificate.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    boolean existsByCertificateNumber(String certificateNumber);
}