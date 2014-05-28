package com.oracle.backingbeans;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.domain.AllocationRequest;
import com.oracle.staffmanagement.domain.Contact;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.staffmanagement.domain.OutOfOffice;
import com.oracle.staffmanagement.domain.ParkingAllocation;
import com.oracle.staffmanagement.domain.ParkingSpace;

@RequestScoped
@ManagedBean(name="workForToday")
public class WorkForTodayPageBean  {
	/**
	* It contain the business Logic for handling the Work for today 
	 */
	@EJB
	private EmployeeManagementServiceLocal employeeService;
	
	@EJB
	private EmployeeManagementServiceLocal employeeManagement;

	private Employee selectedEmployee;
	public Date endDate;
	private UIData dataTable;
	final String username = "admin";
	final String password = "xxxxxxxx";
	private Contact employeeContact;
	
	//constructor 
	public WorkForTodayPageBean(){
		//new employee object
		selectedEmployee = new Employee();
	}
	
	//get all employees which requested a Parking allocation
	public List<AllocationRequest> getAllAllocationRequest()
	{		
	 return employeeService.getAllocationRequests();
	}
	
	
	
	// Approve parking allocation implementation 
	public String allocateApprove(ActionEvent event){

		try { 
			//get the data table object
			Class clazz = dataTable.getRowData().getClass(); 
			//get the allocation request object from the table
			AllocationRequest ar = (AllocationRequest ) dataTable.getRowData();
			//extract the employee who did the request
			selectedEmployee = ar.getEmployee();			
			selectedEmployee = employeeService.getEmployeeDetails( this.selectedEmployee.getEmployee_id() );
			//get the contact details object of the employee
			Contact contact = selectedEmployee.getAllContactDetails().get(0);
            OutOfOffice employeeOnLeaveOoO = selectedEmployee.getLastRequest().getOutofoffice();
            //load the employee object who is on leave
			Employee employeeOnLeave = employeeOnLeaveOoO.getEmployee();
			//get parking space of employee on leave
			ParkingSpace psOfEmployeeOnLeave = employeeOnLeave.getPermParking().getParkingspace();
			//instantiate a new parking allocation object
			ParkingAllocation tempAllocation = new ParkingAllocation();
            //get the details and set the param
			tempAllocation.setEnd_date( employeeOnLeaveOoO.getOffice_datein() );
			tempAllocation.setStart_date( employeeOnLeaveOoO.getOffice_dateout() );
			tempAllocation.setIs_permanent(false);
			tempAllocation.setParkingspace(psOfEmployeeOnLeave);
			//add the new temporary allocation to the parking allocation table
			selectedEmployee.addParkingAllocation(tempAllocation);
			//update the employee object
			employeeService.UpdateEmployee(selectedEmployee); 
			//delete the allocation request
			employeeService.deleteAllocationRequest(selectedEmployee.getLastRequest());
			//email the employee with the temporary parking details 
            allocationConf(contact.getEmail(),employeeOnLeaveOoO.getOffice_dateout(),
            employeeOnLeaveOoO.getOffice_datein(),psOfEmployeeOnLeave);
			//return the work for today page and update the table        
            return "comWorkForToday";
		} catch (Throwable e) 
		{	
			e.printStackTrace();
			return "comSystemDown";
		}
	}
	
	// Revoke parking request implementation
	public String allocateRevoke(ActionEvent event){

		try {
			AllocationRequest ar = (AllocationRequest ) dataTable.getRowData();
			System.out.println("################allocation req " + ar.getRequest_id() );
			selectedEmployee = ar.getEmployee();
			selectedEmployee = employeeService.getEmployeeDetails( this.selectedEmployee.getEmployee_id() );
			Contact contact = selectedEmployee.getAllContactDetails().get(0); //GEt email
			OutOfOffice employeeOnLeaveOoO = selectedEmployee.getLastRequest().getOutofoffice();
			Employee employeeOnLeave = employeeOnLeaveOoO.getEmployee();
			ParkingSpace psOfEmployeeOnLeave = employeeOnLeave.getPermParking().getParkingspace();
			
            //get the OutOfOffice ID
			int OutOfficeEmplId = selectedEmployee.getLastRequest().getOutofoffice().getOutofoffice_id();
			// Find outOfOffice object
            OutOfOffice outOfOffice = employeeManagement.getOutOfOfficeById(OutOfficeEmplId);
            Employee parkingOwner  = employeeManagement.getEmployeeDetails(outOfOffice.getEmployee().getEmployee_id());
            employeeManagement.reverseOutOffice(OutOfficeEmplId);    
            //delete Allocation Request
			employeeService.deleteAllocationRequest(selectedEmployee.getLastRequest());
			//send email to employee
			revokeConf(contact.getEmail(),psOfEmployeeOnLeave);
			return "comWorkForToday";
			
		} catch (Throwable e)
		{	
			e.printStackTrace();
			return "comSystemDown";
		}
	}


	
	//Send Confirmation email Temp allocation approval
	private void allocationConf(String email, Date office_dateout,
			Date office_datein, ParkingSpace psOfEmployeeOnLeave){		
		 		//Set the Server propetries
		        Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "false");
				props.put("mail.smtp.host", "localhost");
				props.put("mail.smtp.port", "25");
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
					message.setFrom(new InternetAddress("admin@commutermanager.info"));
					message.setRecipients(Message.RecipientType.TO,
							//pass the email address of receiver (i.e employee email)	
							InternetAddress.parse(email));
					message.setSubject("Confirmation of Parking Allocation");
					message.setText("Please be informed that your Temporary Parking Allocation Request has been approved."
						+ "\n\nParking space details:"
						+ "\n\n Block no: " + psOfEmployeeOnLeave.getBlock().getBlock_description()
						+ "\n Space no: " + psOfEmployeeOnLeave.getSpace_no()
						+ "\n Valid From: " + office_dateout
					    + "\n Until: " + office_datein
					    + "\n\n================================================================================="
					    + "\n This is an automatically generated email. Please do not reply to this message.");
					// send message to the receiver
					Transport.send(message);
		
				} catch (MessagingException e) 
				{
					throw new RuntimeException(e);
				}
				
		  }
	

	//Revoke Confirmation email for Temp allocation approval
	private void revokeConf(String email, ParkingSpace psOfEmployeeOnLeave){
		//Set the Server propetries
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", "25");
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
			message.setFrom(new InternetAddress("admin@commutermanager.info"));
			message.setRecipients(Message.RecipientType.TO,
					//pass the email address of receiver (i.e employee email)	
					InternetAddress.parse(email));
			message.setSubject("Confirmation of Parking Allocation");
			message.setText("Please be informed that your Temporary Parking Allocation Request "
					+ "for Space no: " + psOfEmployeeOnLeave.getSpace_no() + 
					" in " + psOfEmployeeOnLeave.getBlock().getBlock_description() + " has been revoked."

								+ "\n\nPlease check and apply for a diffrent parking space."
								+ "\n\nWe apologize for any inconvenience caused and thank you for your cooperation. "
								+ "\nIf you have further questions, please contact the Commuter Desk at 01 803xxxxx"
								+ "\n\n================================================================================="
								+ "\n This is an automatically generated email. Please do not reply to this message.");
			// send message to the receiver
			Transport.send(message);
		} catch (MessagingException e) 
		{
			throw new RuntimeException(e);
		}

	}

			
    // GETTERS AND SETTERS           	
	public UIData getDataTable() {
		return dataTable;
	}

	public void setDataTable(UIData dataTable) {
		this.dataTable = dataTable;
	}
   
    public EmployeeManagementServiceLocal getEmployeeService() {
		return employeeService;
	}

	public void setEmployeeService(EmployeeManagementServiceLocal employeeService) {
		this.employeeService = employeeService;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	

	
}
