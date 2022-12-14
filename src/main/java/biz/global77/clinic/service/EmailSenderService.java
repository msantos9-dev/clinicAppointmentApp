package biz.global77.clinic.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMailWithAttachment(String toEmail,
            String attachment) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("arjungautam8877@gmail.com");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setText("Good day, attached is your medical certificate from Love Clinic. Thank you!");
        mimeMessageHelper.setSubject("Medical Certificate");

        FileSystemResource fileSystemResource = new FileSystemResource(new File(attachment));
        mimeMessageHelper.addAttachment(fileSystemResource.getFilename(),
                fileSystemResource);
        javaMailSender.send(mimeMessage);
        System.out.printf("Mail with attachment sent successfully..");

    }
}
