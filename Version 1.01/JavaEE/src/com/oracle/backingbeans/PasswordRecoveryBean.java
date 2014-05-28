package com.oracle.backingbeans;

import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@ManagedBean(name="recPwdBean")
@SessionScoped
public class PasswordRecoveryBean {
	/**
	 * It contains the password recovery implementation
	 */
	
	private String employeeName;
	private String email;
	private String phoneNumber;
	private String text;
	final String username = "admin";
	final String password = "xxxxxxxxxxxx";
	
	
	//Implementation password reset button
	public void check(ActionEvent e){
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "“Your request has been received, and is being reviewed by Commuter Manager Office”",null));
		//Send email to Employee and Coomuter office
		allocationConf(getEmail());
		//reset form
		resetPwdForm();
	}
	
	//Reset the recovery password dialog fields
	private void resetPwdForm() {
		employeeName = null;
		email = null;
		phoneNumber = null;
	}

    //redirect to passwordRecovery from index page
	public String pwdRecovery() throws Exception {

		return "passwordRecovery?faces-redirect=true";
	}


	//Send Confirmation email Temp allocation approval
	private void allocationConf(String email){
		//Set the Server propetries
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		//pass the user name and password of the email sender into the session
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			//Body of the email message passing the arguments
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("danielozac@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			message.setSubject("Password Reset Request from: " + employeeName);
			message.setText("Employee details:"
					+ "\n\n Name: " + employeeName
					+ "\n Email address: " + email
					+ "\n Phone number: " + phoneNumber
					+ "\n\n=========================================="
					);
			//send message to the receiver
			Transport.send(message);
			
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}


	
	//Getters and setters
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
	

    
}
