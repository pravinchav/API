package com.java.restapi;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.HttpRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.framework.security.Security;
import com.google.gson.Gson;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

@Path("/API")
public class CompsoftApi {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public JSONObject sayPlainTextHello() {

		JSONObject loPayTrans = new JSONObject();
		loPayTrans.put("Testing", "Response Successful.");

		return loPayTrans;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/submitinquiry")
	public String submitinquiry(@QueryParam("first_name") String first_name, @QueryParam("email_id") String email_id,
			@QueryParam("phone_no") String phone_no, @QueryParam("comment") String comment,
			@QueryParam("sender_email") String sender_email, @QueryParam("sender_key") String sender_key,
			@QueryParam("receiver_email") String receiver_email) {

		// props.put("mail.smtp.host", "smtpout.asia.secureserver.net");
		// props.put("mail.smtp.socketFactory.port", "465");
		// props.put("mail.smtp.port", "465");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		// get Session
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender_email, com.framework.security.Security.decrypt(sender_key));
			}
		});
		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email_id));
			message.setSubject("Thank you " + first_name);
			message.setText("Hello!!!! " + first_name
					+ ", Your inquiry has been submitted successfully. We will be contacting you shortly.");
			Transport.send(message);

			message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver_email));
			message.setSubject("Inquiry from - " + first_name);
			message.setText(comment + " Contact:" + phone_no + " e-mail:" + email_id);
			Transport.send(message);

			return "Success";
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Failed";
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/api-inquiry")
	public String api_inquiry(@QueryParam("requestData") String requestData) {
		JSONObject loReturn = new JSONObject();

		try {
			JSONParser parser = new JSONParser();
			JSONObject requestJson = (JSONObject) parser.parse(requestData);
			String first_name = (String) requestJson.get("first_name");
			String email_id = (String) requestJson.get("email_id");
			String phone_no = (String) requestJson.get("phone_no");
			String comment = (String) requestJson.get("comment");
			String sender_email = (String) requestJson.get("sender_email");
			String sender_key = (String) requestJson.get("sender_key");
			String receiver_email = (String) requestJson.get("receiver_email");

			// props.put("mail.smtp.host", "smtpout.asia.secureserver.net");
			// props.put("mail.smtp.socketFactory.port", "465");
			// props.put("mail.smtp.port", "465");
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			// get Session
			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(sender_email,
							com.framework.security.Security.decrypt(sender_key));
				}
			});

			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email_id));
			message.setSubject("Thank you " + first_name);
			message.setText("Hello!!!! " + first_name
					+ ", Your inquiry has been submitted successfully. We will be contacting you shortly.");
			Transport.send(message);

			message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver_email));
			message.setSubject("Inquiry from - " + first_name);
			message.setText(comment + " Contact:" + phone_no + " e-mail:" + email_id);
			Transport.send(message);
			return  "Request submitted successfully." ;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Error in submitting request. Please try later. Error-" + ex.getMessage();
		}
	}

}