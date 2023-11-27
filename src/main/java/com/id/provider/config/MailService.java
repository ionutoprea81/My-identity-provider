package com.id.provider.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
@Service(value="MailService")

public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;
    private SimpleMailMessage message;
    public MailService(SimpleMailMessage simpleMailMessage, JavaMailSender javaMailSender) {
        this.message = simpleMailMessage;
        this.javaMailSender = javaMailSender;
    }

    public MailService(){

    }
    @Async
    public void sendMail(String mailAddress, String title, String mailMessage){
        if(this.message == null){
            this.message = new SimpleMailMessage();
        }
        message.setFrom("DataSpaceAdmin");
        message.setSubject(title);
        message.setText(mailMessage);
        message.setTo(mailAddress);
        javaMailSender.send(message);
    }

}