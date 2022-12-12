package biz.global77.clinic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.repository.AppointmentRepository;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    
    @Autowired
	private AppointmentRepository appointmentRepo;

    @Override
	public void saveAppointment(Appointment appointment) {
		this.appointmentRepo.save(appointment);
	}

}
