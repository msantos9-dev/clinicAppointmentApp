package biz.global77.clinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.User;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    public List<Appointment> findByDoctorID(String doctorID);
    public List<Appointment> findByHasArrived(boolean has_arrived);
    public List<Appointment> findByStatus(String status);
    public List<Appointment> findByStatusOrderById(String status);
    public List<Appointment> findByStatusAndDoctorIDOrderById(String status, User doctorID);

    public List<Appointment> findByHasArrivedAndPatientID(boolean has_arrived, User patientID);
    public List<Appointment> findByStatusAndPatientID(String status, User patientID);

    // public Appointment findByID(int id);

}
