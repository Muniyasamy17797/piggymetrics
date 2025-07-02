package com.lms.certificate.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.lms.certificate.model.Certificate;
import com.lms.certificate.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final RestTemplate restTemplate;
    private static final String CERTIFICATE_PATH = "certificates/";

    @Override
    public Certificate generateCertificate(Long studentId, Long courseId, Double finalGrade) {
        // TODO: Get student and course details from respective services
        Certificate certificate = new Certificate();
        certificate.setStudentId(studentId);
        certificate.setCourseId(courseId);
        certificate.setFinalGrade(finalGrade);
        certificate.setCertificateNumber(generateCertificateNumber());
        certificate.setIssuedAt(LocalDateTime.now());
        
        // Generate PDF
        String pdfPath = generatePDF(certificate);
        certificate.setPdfUrl(pdfPath);
        
        return certificateRepository.save(certificate);
    }

    @Override
    public Resource downloadCertificate(Long certificateId) {
        Certificate certificate = getCertificate(certificateId);
        try {
            Path path = Paths.get(certificate.getPdfUrl());
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("Could not read certificate file");
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public Certificate getCertificate(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @Override
    public boolean verifyCertificate(String certificateNumber) {
        return certificateRepository.existsByCertificateNumber(certificateNumber);
    }

    private String generateCertificateNumber() {
        return UUID.randomUUID().toString();
    }

    private String generatePDF(Certificate certificate) {
        try {
            String fileName = CERTIFICATE_PATH + certificate.getCertificateNumber() + ".pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Certificate of Completion"));
            document.add(new Paragraph("This is to certify that"));
            document.add(new Paragraph(certificate.getStudentName()));
            document.add(new Paragraph("has successfully completed the course"));
            document.add(new Paragraph(certificate.getCourseName()));
            document.add(new Paragraph("with a grade of " + certificate.getFinalGrade()));
            document.add(new Paragraph("Certificate Number: " + certificate.getCertificateNumber()));
            document.add(new Paragraph("Issue Date: " + certificate.getIssuedAt()));

            document.close();
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }
}