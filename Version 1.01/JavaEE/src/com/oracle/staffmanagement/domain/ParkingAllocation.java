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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.annotations.Expose;

/** 
 * Parking Allocation Entity mapping the parking_allocation DB Table and relationships
 */
@Entity
@XmlRootElement
@Table(name = "parking_allocation")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ParkingAllocation {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Expose
	private int allocation_id;
	
	@Temporal(TemporalType.DATE)
	@Expose
	private Date start_date;
	
	@Temporal(TemporalType.DATE)
	@Expose
	private Date end_date;
	
	@Expose
	private Boolean is_permanent;
		
	@XmlTransient
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="employee_employee_id", referencedColumnName="employee_id")
	private Employee employee;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="parking_space_pspace_id")
	@Expose
	private ParkingSpace parkingspace;
	
	

	//Getters and Setters
	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public Boolean getIs_permanent() {
		return is_permanent;
	}

	public void setIs_permanent(Boolean is_permanent) {
		this.is_permanent = is_permanent;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getAllocation_id() {
		return allocation_id;
	}

	public void setAllocation_id(int allocation_id) {
		this.allocation_id = allocation_id;
	}

	public ParkingSpace getParkingspace() {
		return parkingspace;
	}

	public void setParkingspace(ParkingSpace parkingspace) {
		this.parkingspace = parkingspace;
	}
	
	
}
