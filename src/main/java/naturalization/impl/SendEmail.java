package naturalization.impl;

import java.util.*;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail
{
    public static void SendEmail(){
        Logger logger = Logger.getLogger(SendEmail.class.getName());

        // Sender's email ID needs to be mentioned
        String from = "";
        String pass ="";
        // Recipient's email ID needs to be mentioned.
        String to = "";
        String host = "smtp.mail.yahoo.com";

        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to, false));

            // Set Subject: header field
            message.setSubject("Rendez-vous naturalisation disponible !");

            // Now set the actual message
            message.setText("Rendez-vous disponible ! 93 easy !");

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            logger.warning("Rdv e-mail sucessfully sent.");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}