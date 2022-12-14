package biz.global77.clinic.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    public List<Appointment> findByDoctorID(String doctorID);

    public List<Appointment> findByHasArrived(boolean has_arrived);

    public List<Appointment> findByStatus(String status);

    public List<Appointment> findByStatusOrderById(String status);

    public List<Appointment> findByStatusAndDoctorIDOrderById(String status, User doctorID);

    public List<Appointment> findByHasArrivedAndPatientID(boolean has_arrived, User patientID);

    public List<Appointment> findByStatusAndPatientID(String status, User patientID);

    public List<Appointment> findByStatusAndDate(String status, Date date);

    public Appointment findById(int id);

    public List<Appointment> findByPatientID(User id);

    @Query("SELECT COUNT(a) FROM Appointment a")
    public long appointments();

}