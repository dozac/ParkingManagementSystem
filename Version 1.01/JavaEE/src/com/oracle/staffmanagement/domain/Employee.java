package com.oracle.staffmanagement.domain;

import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.annotations.Expose;


/**
 * 
 * Employee Entity mapping the Employee DB Table and relationships with the other tables
 *
 */

@Entity
@Table(name="employee")
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Employee implements java.io.Serializable
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Expose
	private int employee_id;
	
	@Expose
	private String employee_no;
	@Expose
	private String firstname;
	@Expose
	private String lastname;
	@XmlTransient
	private String password;
	@Expose
	private String employment_status;
	@Expose
	private String access_type;
	 
	
	@Temporal(TemporalType.DATE)
	@Expose
	private Date application_date;
	
	@Temporal(TemporalType.DATE)
	@Expose
	private Date allocation_exp_date;
	
	// Relationships tables annotations
	@XmlTransient
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinColumn(name="employee_employee_id")
	private List<Note> notes;
	
	@XmlTransient
	@OneToOne(cascade=CascadeType.ALL,mappedBy = "employee")
	private OutOfOffice outOffice;
	
	@XmlTransient
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinColumn(name="employee_employee_id")
	private List<Contact> contactDetails;
	
	@XmlTransient
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinColumn(name="employee_employee_id")
	private List<Car> employeeCar;
	
	@XmlTransient
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinColumn(name="employee_employee_id")
	private List<Visitor> employeeVisitors;
	
	//@XmlTransient
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinColumn(name="employee_employee_id")
	@Expose
	private List<ParkingAllocation> parkingAllocation;
	
	@XmlTransient
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinColumn(name="employee_employee_id")
	private List<AllocationRequest> allocationRequests;

	
	
	public Employee() 
	{
		
		// required by JPA, but not used by us.		  
	}
		
	//Add Employee object to DB along with the Note, OutOfOffice, contact details.
	public Employee(String employee_no, String firstname, String lastname, String password, String access_type,
			Date application_date, String employment_status, Date allocation_exp_date) 
	{
		super();
		this.notes = new ArrayList<Note>();
		this.contactDetails = new ArrayList<Contact>();
		this.employeeCar = new ArrayList<Car>();
		this.employeeVisitors = new ArrayList<Visitor>();
		
		this.employee_no = employee_no;
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
		this.access_type = access_type;
		this.application_date = application_date;
		this.employment_status = employment_status;
		this.allocation_exp_date = allocation_exp_date;
	}

	public String toString()
	{
		return "Employee " + this.employee_no + " " + this.firstname;
	}
	
	

	//Add Contact details
	public void addContact(String conCompany, String conEmail, int conPhoneNumber, 
			String conLineManager, int conPostCode, String conLineOfBusiness)
	{
		Contact newContact = new Contact(conCompany, conEmail, conPhoneNumber, 
							conLineManager, conPostCode, conLineOfBusiness);
		this.contactDetails.add(newContact);

	}
	
	//add Car details
	public void addCar(String vehicle_regno, String make, String model, String colour, 
			Date date_vehicle_added, String carMotorbikeSpace, String stickernumber)
	{
		Car newCar  = new Car(vehicle_regno, make, model, colour, date_vehicle_added, carMotorbikeSpace, 
			stickernumber);
		this.employeeCar.add(newCar);
		
	}
	
	//Add OutOfOffice	
		
		public void addOutOfOffice(Date out, Date in)
		{
	            if(outOffice==null){
			outOffice = new OutOfOffice();
	                outOffice.setEmployee(this);
	            }
	        
			outOffice.setOutAndIn(out, in);
		}
	
	//Add Note
	public void addNote(String newNoteText)
	{
		Note newNote = new Note(newNoteText);
		this.notes.add(newNote);
	}
	
	
	//Add Visitor
	public void addVisitor(String visitorName, Date startDate, Date endDate, ParkingSpace ps){
		Visitor newVisitor = new Visitor(visitorName, startDate, endDate, ps);
		employeeVisitors.add(newVisitor);
		
	}
	
	
		
	//Add Parking Allocation
    public void addParkingAllocation(ParkingAllocation a){
		
		parkingAllocation.add(a);
	}
	
  //Add Allocation Request
    public void addAllocationRequest(AllocationRequest a){
		allocationRequests.add(a);
	}
	
	
	
	
	//Check if Parking allocation is permanent and return Object
	public ParkingAllocation getPermParking(){
		
		for( ParkingAllocation allocation : parkingAllocation) {
			 if( allocation.getIs_permanent() == true) {
			 return allocation;

			}
			
		}
		return null;
	}
	
	
	//Check if Parking allocation is Temporary and return Object
		public ParkingAllocation getTempParking(){
			
			for( ParkingAllocation allocation : parkingAllocation) {
				 if( allocation.getIs_permanent() == false) {
				 return allocation;

				}
				
			}
			return null;
		}
	

	public AllocationRequest getLastRequest() {
		   if( allocationRequests != null & allocationRequests.size() > 0 ) {
		       return allocationRequests.get(0);
		}
		return null;
}
	
	
	//Getter and Setters  CONTACT
	public List<Contact> getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(List<Contact> contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	public List<Contact> getAllContactDetails() 
	{
		return this.contactDetails;
	}
	
	
    ////Getter and Setters  CAR
	public List<Car> getEmployeeCar() {
		return employeeCar;
	}

	public void setEmployeeCar(List<Car> employeeCar) {
		this.employeeCar = employeeCar;
	}

	//Getter and Setters  OUT OFF OFFICE
	public OutOfOffice getOutOffice() {
		return outOffice;
	}

	public void setOutOffice(OutOfOffice outOffice) {
		this.outOffice = outOffice;
	}


	public  OutOfOffice getActiveOutOffice() {
		return   outOffice;
	}

	//Getter and Setters  NOTE
	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public List<Note> getAllNotes() 
	{
		return this.notes;
	}
	
	
	public List<Visitor> getEmployeeVisitors() {
		return employeeVisitors;
	}


	public void setEmployeeVisitors(List<Visitor> employeeVisitors) {
		this.employeeVisitors = employeeVisitors;
	}


	//Getters and Setters EMPLOYEE
	public String getEmployee_no() {
		return employee_no;
	}

	public void setEmployee_no(String employee_no) {
		this.employee_no = employee_no;
	}

	public int getEmployee_id() {
		return employee_id;
	}

	
	public void setEmployee_id(int employee_id) {
		this.employee_id = employee_id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassword() {
		return password;
	}
	
	public String getAccess_type() {
		return access_type;
	}

	public void setAccess_type(String access_type) {
		this.access_type = access_type;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getApplication_date() {
		return application_date;
	}

	public void setApplication_date(Date application_date) {
		this.application_date = application_date;
	}

	public String getEmployment_status() {
		return employment_status;
	}

	public void setEmployment_status(String employment_status) {
		this.employment_status = employment_status;
	}

	public Date getAllocation_exp_date() {
		return allocation_exp_date;
	}

	public void setAllocation_exp_date(Date allocation_exp_date) {
		this.allocation_exp_date = allocation_exp_date;
	}


	//Getters and Setters EMPLOYEE
	public List<ParkingAllocation> getParkingAllocation() {
		return parkingAllocation;
	}


	public void setParkingAllocation(List<ParkingAllocation> parkingAllocation) {
		this.parkingAllocation = parkingAllocation;
	}

	//Getters and Setters ALLOCATION REQUEST
	public List<AllocationRequest> getAllocationRequests() {
		return allocationRequests;
	}


	public void setAllocationRequests(List<AllocationRequest> allocationRequests) {
		this.allocationRequests = allocationRequests;
	}	

}
