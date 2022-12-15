package biz.global77.clinic.service;

import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class MessageSenderService {

    public void sendMessage(String messageContent) {
       final String ACCOUNT_SID = "AC6e468560159566ea34eb3c8e50c49c40";
		final String AUTH_TOKEN = "b37b163e793a09b8a3637e3415b4a775";

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message message = Message.creator(
		new com.twilio.type.PhoneNumber("+639610768081"),
		"MGc03f15fa700ba449638ca4d8682c793b",
		messageContent)

		.create();
    }

}
