package com.oracle.backingbeans;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.staffmanagement.domain.OutOfOffice;
import com.oracle.staffmanagement.domain.ParkingAllocation;
import com.oracle.staffmanagement.domain.ParkingSpace;


@ViewScoped
@ManagedBean(name="oooBean")
public class OutOfOfficeBean {
	/**
	 * It contains the quick temp allocation for the Out Off office intimations without a web request in place 
	 */
	
	@EJB
	private EmployeeManagementServiceLocal employeeService;
	private List <OutOfOffice> oooList;

	@EJB
	private EmployeeManagementServiceLocal employeeManagement;
	private Employee selectedEmployee;
	
	private String employeeNo;	
	private Date startDate;
	private Date endDate;
	
	//when EJB is instantiated get the the list of active Our of Office intimations
	@PostConstruct
	public void init()
	{
		oooList = employeeService.getActiveOutOfOfficeIntimations();	
	}
	
	//constructor
	public OutOfOfficeBean(){
		this.selectedEmployee = new Employee();
		
	}
	
	
	//Implementation of Temporary Parking allocation 
	public void allocateSpace(ActionEvent event){

		try {
            //get the employee ID
			this.selectedEmployee = this.employeeService.getEmployeeDetails(this.selectedEmployee.getEmployee_id());
			//get the parking space against which the allocation is being done
			ParkingSpace psOfEmployeeOnLeave = this.selectedEmployee.getPermParking().getParkingspace();
			//Set the Employee to null
			Employee tempAllocEmployee = null; 
			FacesMessage msg = null;
			try{
				//if the employee number against the allocation is being done is not in the DB throw exception
				tempAllocEmployee = employeeService.searchByEmployeeNo(employeeNo);

			} catch (Throwable e) 
			{	//catch and let the user know that employee no is not existent 
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee number non existenet", employeeNo );  
				FacesContext.getCurrentInstance().addMessage(null, msg);  

				e.printStackTrace(); 
				return;
			}

			try 
			{	//validation of start and end date	
				if(endDate.before(startDate)){
					FacesMessage msgDate = new FacesMessage("End Date cannot be before Start Date.");  
					FacesContext.getCurrentInstance().addMessage(null, msgDate);  
					return;
				} 

			} catch (Throwable e) {	
				e.printStackTrace();

			}
            //instantiate new parking allocation object
			ParkingAllocation tempAllocation = new ParkingAllocation();			
			//add the end date
			tempAllocation.setEnd_date(getEndDate());
			//add the start date
			tempAllocation.setStart_date(getStartDate());
			//set the is permanent to false 
			tempAllocation.setIs_permanent(false);
			//add the parking space number
			tempAllocation.setParkingspace(psOfEmployeeOnLeave);
            //add the parking allocation to the employee object
			tempAllocEmployee.addParkingAllocation(tempAllocation);
			//update the employee object
			employeeService.UpdateEmployee(tempAllocEmployee);
			//set the status of out of office to 1 so it wont show on the available parking spaces list on Web and Android
			OutOfOffice ooo = this.selectedEmployee.getOutOffice();
			ooo.setStatus(1);
			//update the employee object
			employeeService.UpdateEmployee(this.selectedEmployee);

		} catch (Throwable e) {	
			e.printStackTrace();

		}
		
	}

	     
	//Show Parking space dialog Option
	public void showAllocationDialogAction() {
        //instantiate the faces context
		FacesContext fc = FacesContext.getCurrentInstance();
        //load the employee id from UI 
		Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
	    //the the employee ID
		String empId = params.get("paramEmployeeId");
	    //load the employee object
		if( empId != null && empId.length() > 0 ) {
			Employee employee = employeeService.getEmployeeDetails(Integer.parseInt(empId) );
			selectedEmployee = employee;	    	
		} else {
			//if not toast the message
			FacesMessage msg = null;
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee non existent", employeeNo );  
			FacesContext.getCurrentInstance().addMessage(null, msg); 
		}
	}


	//Getters and Setters
	public List<OutOfOffice> getOooList() {
		oooList = employeeService.getActiveOutOfOfficeIntimations();
		return oooList;
	}

	public void setOooList(List<OutOfOffice> oooList) {
		this.oooList = oooList;
	}


	public Date getStartDate() {
		return startDate;
	}


	public String getEmployeeNo() {
		return employeeNo;
	}


	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}



	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
}
