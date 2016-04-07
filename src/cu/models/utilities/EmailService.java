package cu.models.utilities;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by T on 07/04/2016.
 */
public class EmailService
{
    private PropertiesManager propertiesManager = new PropertiesManager();
    private Session session;

    public EmailService()
    {
        configureEmail();
    }

    private void configureEmail()
    {
        if(propertiesManager.getProperty("mail.smtp.auth") == null)
        {
            propertiesManager.setProperties("mail.smtp.host", "smtp.gmail.com");
            propertiesManager.setProperties("mail.smtp.socketFactory.port", "465");
            propertiesManager.setProperties("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            propertiesManager.setProperties("mail.smtp.auth", "true");
            propertiesManager.setProperties("mail.smtp.port", "465");
        }
        session = Session.getDefaultInstance(propertiesManager.getProperties(), new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(propertiesManager.getProperty("email"), propertiesManager.getProperty("password"));
            }
        });
    }

    public void sendEmail(String recipientEmail, String subject, String content)
    {
        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(propertiesManager.getProperty("email")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(content);
            Transport.send(message);
        }
        catch(MessagingException e)
        {
            e.printStackTrace();
        }
    }
}
