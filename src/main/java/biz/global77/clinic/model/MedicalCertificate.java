package biz.global77.clinic.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode
@ToString
@Table(name = "medicalCertificate")
public class MedicalCertificate implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

    @OneToOne
    private User patientID;

    @ManyToOne
    private User doctorID;

    private String reason;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

}