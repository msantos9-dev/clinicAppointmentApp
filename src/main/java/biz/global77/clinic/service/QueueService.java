package biz.global77.clinic.service;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.Queue;
import biz.global77.clinic.model.User;

public interface QueueService {

    public Queue createQueue(Queue queue, Appointment appointment, User nurse);

    // public Queue createQueue(Queue queue, Appointment appointment);

}
