package com.interceptor.mcamp;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class sendEmail {

    boolean isSent = false;

    public sendEmail(String stringReceiverEmail, String title, String body) {
        try {
            String stringSenderEmail = "planetd.interceptor@gmail.com";
            String stringPasswordSenderEmail = "zcghxrcaqizvmoct";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail); //super.getPasswordAuthentication();
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);


            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            mimeMessage.setSubject(title);//"TEST Mail"
            mimeMessage.setText(body);//"Hello all\n\n Thank you."

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(mimeMessage);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
            this.isSent = true;
            thread.start();


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    boolean getIsSent(){
        return isSent;
    }
}


//    String body = "Dear Friend, \n\nWelcome to Cipher. \n\nYour Login OTP is " + genOTP + ".\n\n If didn't you request this, Please ignore this massage";
//    sendEmail se = new sendEmail(stringEmail, "Cipher Registration OTP", body);
//    boolean isSent = se.getIsSent();
//Common.stopLoading();



//rnum = Random.Range(99999, 999998);
//        rnum++;
//        mail.Subject = "OTP Confirmation";
//        mail.Body = "Hellow "+username+". "+"\nYour OTP is " + rnum + ". \nDo not shear this to anyone.\n**************Thank You**************";


//mail.Subject = "OTP Confirmation";
//        mail.Body = "Welcome To mCamp. \n"+username.text+ " you successfully registered with mCamp. \n**************Thank You**************";


//mail.Subject = "OTP Confirmation";
//        mail.Body = "Dear " + username + ".\nYou successfully reset password of mCamp.\n**************Thank You**************";