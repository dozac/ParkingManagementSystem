package com.oracle.staffmanagement.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

/**
 * 
 * Allocation Request Entity Mapping the allocation_request DB Table and relationships
 *
 */

@Entity
@XmlRootElement
@Table(name = "allocation_request")
public class AllocationRequest {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Expose
	private int request_id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Expose
	private Date requestdate;
		
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="employee_employee_id", referencedColumnName="employee_id")
	@Expose
	private Employee employee;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="out_of_office_outofoffice_id", referencedColumnName="outofoffice_id")
	private OutOfOffice outofoffice;

	
	//Getters and Setters
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getRequest_id() {
		return request_id;
	}

	public void setRequest_id(int request_id) {
		this.request_id = request_id;
	}

	public Date getRequestdate() {
		return requestdate;
	}

	public void setRequestdate(Date requestdate) {
		this.requestdate = requestdate;
	}

	public OutOfOffice getOutofoffice() {
		return outofoffice;
	}

	public void setOutofoffice(OutOfOffice outofoffice) {
		this.outofoffice = outofoffice;
	}
	
	
}
