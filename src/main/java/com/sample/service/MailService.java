package com.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@EnableAsync
public class MailService {

    @Autowired
    JavaMailSender mailSender;

    @Async
    public void sendEmail(Integer id,String mail,String token)
    {
        try
        {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom("Hey_Net@126.com");
            message.setTo(mail);
            message.setSubject("邮箱验证");
            String mes="点击链接，激活邮箱！";
            String link = "http://localhost:8080/#/validate/" + id.toString()+'/'+token;
            message.setText(mes+link);
            this.mailSender.send(mimeMessage);
            System.out.println("ok!");
        }
        catch(Exception ex)
        {
            System.out.println("wrong!");
            System.out.println(token);
        }
    }

    @Async
    public void sendEmailForPassword(String mail,String password)
    {
        try
        {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom("Hey_Net@126.com");
            message.setTo(mail);
            message.setSubject("找回密码");

            String text = "重置密码为"+password;
            message.setText(text);
            this.mailSender.send(mimeMessage);
            System.out.println("ok!");
        }
        catch(Exception ex)
        {
            System.out.println("wrong!");
        }
    }
}

