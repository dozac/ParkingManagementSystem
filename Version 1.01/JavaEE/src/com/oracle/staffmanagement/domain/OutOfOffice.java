
package com.oracle.staffmanagement.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.gson.annotations.Expose;
import com.oracle.util.DateAdapter;

/**
 * 
 * Out Of Office Entity mapping the out_of_office DB Table and relationships
 *
 */
@Entity
@XmlRootElement
@Table(name="out_of_office")

public class OutOfOffice
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Expose
	private int outofoffice_id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Expose
	private Date office_dateout;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Expose
	private Date office_datein;
	
	@OneToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="employee_employee_id")
	
	@Expose
	private Employee employee;
    
	@Expose
	private int status;
	
    @XmlTransient
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinColumn(name="out_of_office_outofoffice_id")
    @Expose
    private List<AllocationRequest> allocationRequests;
    
   
	public OutOfOffice()
	{
		// used by the JPA
	}

	//method used to set a new Instance of the OutOfOffice object
    public void setOutAndIn(Date out, Date in) {
	        this.office_dateout = out;
	        this.office_datein = in;
	        this.status = 0;
	    }
	
	//Getters and Setters
	public int getOutofoffice_id() {
		return outofoffice_id;
	}

	public void setOutofoffice_id(int outofoffice_id) {
		this.outofoffice_id = outofoffice_id;
	}
    
	@XmlJavaTypeAdapter(DateAdapter.class) 
	public Date getOffice_dateout() {
		return office_dateout;
	}

	public void setOffice_dateout(Date office_dateout) {
		this.office_dateout = office_dateout;
	}
    
	@XmlJavaTypeAdapter(DateAdapter.class) 
	public Date getOffice_datein() {
		return office_datein;
	}

	public void setOffice_datein(Date office_datein) {
		this.office_datein = office_datein;
	}
	
	public void setAllocationRequests(List<AllocationRequest> allocationRequests) {
		this.allocationRequests = allocationRequests;
	}
    
	public void addAllocationRequest(AllocationRequest a){
		allocationRequests.add(a);
	}
	
	public int getStatus() {
	        return status;
	    }

	public void setStatus(int status) {
	        this.status = status;
	    }
	public Employee getEmployee(){
		return employee;
	}
	
	
	public void setEmployee(Employee e) {
		this.employee = e;
	}


}
