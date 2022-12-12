package biz.global77.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import biz.global77.clinic.model.MedicalCertificate;

public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificate, Integer> {
    
}
