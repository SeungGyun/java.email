
package com.ilhsk.email;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;



/**
 * @Auth ilhsk
 * @Description 
 * <pre></pre>   
 */
public class EmailSample {

	public static void main(String[] args) throws Exception {
		// SMTP info
		String host = "메일서버 주소 -> 112.119.1.1";
		String port = "22";
		String mailFrom = "보내느사람이메일주소 -> test@test.com";
		String password = "";

		// message info
		String mailTo = "ilhsk@ilhsk.com";
		String subject = "한굴 이 Test e-mail with inline images";
		StringBuffer body = new StringBuffer("<html>테스트 This message contains two inline images.<br>");
		body.append("The first image is a chart:<br>");
		body.append("<img src=\"cid:image1\" width=\"30%\" height=\"30%\" /><br>");
		body.append("The second one is a cube:<br>");
		body.append("<img src=\"cid:image2\" width=\"15%\" height=\"15%\" /><br>");
		body.append("End of message.");
		body.append("</html>");

		// inline images
		Map<String, String> inlineImages = new HashMap<String, String>();
		inlineImages.put("image1", "d:/tt.png");
		inlineImages.put("image2", "d:/tt.png");

		try {
			EmailSample.send(host, port, mailFrom, password, mailTo.split(","), subject, body.toString(), inlineImages);
			System.out.println("Email sent.");
		} catch (Exception ex) {
			System.out.println("Could not send email.");
			ex.printStackTrace();
		}
	}

	public static void send(String host, String port, final String userName, final String password, String[] toAddress, String subject, String htmlBody, Map<String, String> mapInlineImages) throws AddressException, MessagingException, UnsupportedEncodingException {
		// sets SMTP server properties
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", host);
		props.setProperty("mail.user", "");
		props.setProperty("mail.password", "");
		//properties.put("mail.smtp.auth", "false");
		//properties.put("mail.smtp.starttls.enable", "true");
		//properties.put("mail.user", userName);
		//properties.put("mail.password", password);

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
		Session session = Session.getInstance(props, null);
		session.setDebug(true);
		
		// creates a new e-mail message
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(userName,userName));
		InternetAddress[] internetAddress = new InternetAddress[toAddress.length];
		for(int i =0 ; i < toAddress.length ; i++) {
			internetAddress[i] = new InternetAddress(toAddress[i]);
		}
		
		
		msg.setRecipients(Message.RecipientType.TO, internetAddress);
		msg.setSubject(MimeUtility.encodeText(subject,"UTF-8","B"));
		msg.setSentDate(new Date());

		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		//messageBodyPart.setContent(htmlBody, "text/html;UTF-8");
		messageBodyPart.setContent(htmlBody, "text/html; charset=UTF-8");

		// creates multi-part
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		// adds inline image attachments
		if (mapInlineImages != null && mapInlineImages.size() > 0) {
			Set<String> setImageID = mapInlineImages.keySet();

			for (String contentId : setImageID) {
				MimeBodyPart imagePart = new MimeBodyPart();
				imagePart.setHeader("Content-ID", "<" + contentId + ">");
				imagePart.setDisposition(MimeBodyPart.INLINE);

				String imageFilePath = mapInlineImages.get(contentId);
				try {
					imagePart.attachFile(imageFilePath);
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				multipart.addBodyPart(imagePart);
			}
		}

		msg.setContent(multipart);
		
		
		Transport.send(msg);

		
			
	}

}
