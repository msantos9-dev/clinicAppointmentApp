package biz.global77.clinic.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.Queue;
import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.QueueRepository;

@Service
public class QueueServiceImpl implements QueueService {

    @Autowired
    private QueueRepository queueRepo;

    @Override
    public Queue createQueue(Queue queue, Appointment appointment, User nurse) {

        // public Queue createQueue(Queue queue, Appointment appointment) {

        queue.setPatientID(appointment.getPatientID());
        // queue.setNurseID(nurse);
        queue.setType("Appointment");

        queue.setStartTime(LocalTime.now());

        return queueRepo.save(queue);
    }

}