package com.interceptor.mcamp;
//Done
import android.content.Context;

public class EmailManager {

    private final String receiverEmail;
    private final Context context;
    
    public EmailManager(Context context, String receiverEmail){
        this.context = context;
        this.receiverEmail = receiverEmail;
    }

    private boolean sendMail(String title, String body){
        Common.startLoading(context, "Sending");
        if(new SendEmail(receiverEmail, title, body).getIsSent()){
            Common.stopLoading();
            return true;
        }else{
            Common.showMessage(context, "Error", "Sorry! \nEmail not send.");
            return false;
        }
    }

    public boolean sendMail(int genOTP){
        return sendMail("mCamp Registration OTP", "Hello Dear, \n\nWelcome to mCamp. \n\nYour Login OTP is " + genOTP + ".\nDo not share this with anyone.\n\n\n If didn't you request this, Please ignore this massage");
    }

    public boolean sendMail(String username, int genOTP){
        return sendMail("OTP Confirmation", "Hello "+username+". "+"\nYour OTP is " + genOTP + ". \nDo not share this with anyone.\n**************Thank You**************");
    }

    public boolean sendMail(String username){
        return sendMail("OTP Confirmation", "Welcome To mCamp. \n"+username+ " you successfully registered with mCamp. \n**************Thank You**************");
    }

    public boolean sendMail(){
        return sendMail("Password was reset", "Hello Dear.\nYou successfully reset password of mCamp.\n**************Thank You**************");
    }

}