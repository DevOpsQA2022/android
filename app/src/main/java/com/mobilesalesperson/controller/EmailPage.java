package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.mobilesalesperson.util.ThemeUtil;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import myjava.awt.datatransfer.Transferable;

public class EmailPage extends AppBaseActivity {
    private EditText edtToAddress,edtSubject,edtMessage;
    private Button btn_sendEmail;
    String sEmail,sPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.email_activity);


        edtToAddress = findViewById(R.id.to_address_email);
        edtSubject = findViewById(R.id.to_subject_email);
        edtMessage = findViewById(R.id.to_message_email);
        btn_sendEmail =findViewById(R.id.btn_send_email);

        sEmail = "elumalaimathes10@gmail.com";
        sPassword = "elumalaimathes@10";

        btn_sendEmail.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
//                SendMail();
                String host = "smtp.gmail.com";
                //Initialize properties

                Properties properties = new Properties();
                properties.put("mail.smtp.auth","true");

                properties.put("mail.smtp.starttls.enable","true");
                properties.put("mail.smtp.host",host);
                properties.put("mail.smtp.port","587");

                //port 465 //port 587

                //Intialize session

                Session session = Session.getDefaultInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sEmail,sPassword);
                    }
                });



                try {
                    //Initialize email content
                    Message message = new MimeMessage(session);
                    //sender email
                    message.setFrom(new InternetAddress(sEmail));
                    //Recipient email
                    message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(edtToAddress.getText().toString().trim()));
                    //Email subject
                    message.setSubject(edtSubject.getText().toString().trim());
                    //Email message
                    message.setText(edtMessage.getText().toString().trim());

                    //send email
                    new Sendmail().execute(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });

    }


   //sendmail throung intent


//    private void SendMail() {
//        String ReceiptentList = edtToAddress.getText().toString();
//        String[] Receiptent = ReceiptentList.split(",");
//        String subject = edtSubject.getText().toString();
//        String Message = edtMessage.getText().toString();
//
//        Intent intent  = new Intent(Intent.ACTION_SEND);
//        intent.putExtra(Intent.EXTRA_EMAIL,Receiptent);
//        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
//        intent.putExtra(Intent.EXTRA_TEXT,Message);
//        intent.setType("message/rfc822");
//        startActivity(Intent.createChooser(intent,"Choose the email option"));
//    }

    private class Sendmail extends AsyncTask<Message,String,String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EmailPage.this,"pleaseWait","Sending mail...",true,false);

        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s.equals("Success")){
                AlertDialog.Builder builder = new AlertDialog.Builder(EmailPage.this);
                builder.setCancelable(false);
                builder.setTitle("<font color='#509324'>Success</font>");
                builder.setMessage("Message send Successfully.");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.dismiss();

                   edtMessage.setText("");
                   edtSubject.setText("");
                   edtToAddress.setText("");
                    }
                });
            }else if(s.equals("Error")){
                Toast.makeText(EmailPage.this,"Error sending email",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
