package com.oracle.staffmanagement.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contact Entity mapping the contact DB Table and relationships
 *
 */

@Entity
@XmlRootElement
@Table(name="contact")
public class Contact
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int contact_id;
	
	private String company;
	private String email;
	private int phonenumber;
	private String linemanager;
	private int postcode;
	private String lineofbusiness;
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="employee_employee_id", referencedColumnName="employee_id")
	private Employee employee;
	
	public Contact()
	{
		// constructor used by the JPA
	}
 
	
	public Contact(String conCompany, String conEmail, int conPhoneNumber, 
			String conLineManager, int conPostCode, String conLineOfBusiness)
	{
		this.company = conCompany;
		this.email  = conEmail;
	    this.phonenumber = conPhoneNumber; 
	    this.linemanager = conLineManager;
		this.postcode = conPostCode;
		this.lineofbusiness = conLineOfBusiness;
	}

	
    // Getters and setters
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	//
	/*public int getEmployee_employee_id() {
		return employee_employee_id;
	}

	public void setEmployee_employee_id(int employee_employee_id) {
		this.employee_employee_id = employee_employee_id;
	}*/
	
	
	
	public String getCompany() {
		return company;
	}


	public void setCompany(String company) {
		this.company = company;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public int getPhonenumber() {
		return phonenumber;
	}


	public void setPhonenumber(int phonenumber) {
		this.phonenumber = phonenumber;
	}


	public String getLinemanager() {
		return linemanager;
	}


	public void setLinemanager(String linemanager) {
		this.linemanager = linemanager;
	}


	public int getPostcode() {
		return postcode;
	}


	public void setPostcode(int postcode) {
		this.postcode = postcode;
	}


	public String getLineofbusiness() {
		return lineofbusiness;
	}


	public void setLineofbusiness(String lineofbusiness) {
		this.lineofbusiness = lineofbusiness;
	}	
	
}
