package biz.global77.clinic.model;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode
@ToString
@Table(name = "queue")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private User patientID;

    private String type; // appt/w.i

    @DateTimeFormat(iso = ISO.TIME)
    private LocalTime startTime;

    @DateTimeFormat(iso = ISO.TIME)
    private LocalTime endTme;

    private String totalTime;

    @OneToOne
    private User nurseID;

    @OneToOne
    private Appointment appointment;

}