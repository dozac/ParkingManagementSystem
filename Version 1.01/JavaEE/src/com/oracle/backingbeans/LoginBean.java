package com.oracle.backingbeans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.domain.AllocationRequest;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.staffmanagement.domain.OutOfOffice;
import com.oracle.staffmanagement.domain.ParkingSpace;
import com.oracle.util.EncryptDecrypt;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {
	/**
	* It contain the Business Logic for the Login and the 
	* Implementation for Employee Webb app 
	 */
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private EmployeeManagementServiceLocal employeeService;
	private Employee selectedEmployee;
	private OutOfOffice selectedooo;
	@EJB
	private EmployeeManagementServiceLocal employeeManagement;
	private EncryptDecrypt encryptDecrypt;
	
	private String password;
	private String message, uname;
	private String lastName;
	private String firstName;
	private String uName;
	private Date startDate;
	private Date endDate;
	
    //Constructor
	public LoginBean() {
		//instantiate new Employee object
		selectedEmployee = new Employee();
		//instantiate new EencryptDecrypt object
		encryptDecrypt = new EncryptDecrypt();
		//instantiate new out of office object
        selectedooo = new OutOfOffice();
	}

	//return employee object while searching by employee no
	public Employee searchByEmployeeNo(String employee_no)
	{	
		return employeeService.searchByEmployeeNo(employee_no);
	}

	//check user validation - user input password against DB Password and return true of false
	public boolean userValidation() throws Exception{
        try {
        	//load the employee object
        	Employee employee = searchByEmployeeNo(getUname());
    		selectedEmployee = (Employee) employee;
    		//get the user password
    		String dbPassword = new String(this.selectedEmployee.getPassword());
    		//compare the user pass stored into the DB with the input (compare hasing passwords)
    		String userPassword  = getDecryptedPassword(getPassword());
    		if(dbPassword.equals(userPassword)){
    			
    			return true;
    		} else {
    			return false;
    		}
			
		} catch (Exception e) {
			return false;
		}
		
	}
	
	//return the encrypted password
	@SuppressWarnings("unused")
	private String getDecryptedPassword(String password) throws Exception
	{
		return encryptDecrypt.encrypt(password);
	}
	

	// If validation is passed check user access (admin or employee), start session and return the landing page  
	public String loginProject() throws Exception {
        //password check 
		boolean result = userValidation();
		//Get Last Name
		this.lastName = this.selectedEmployee.getLastname();
		//Get First Name
		this.firstName = this.selectedEmployee.getFirstname();    
		
		this.uName = firstName + " " + lastName;
		//check the access type and compare 
		String accessType = new String(this.selectedEmployee.getAccess_type());
		String admin = new String("admin");
        //if the access type is admin get seession and return the home page of admin
		if (result && (accessType.equals(admin))){
			// get Http Session and store username
			HttpSession session = Util.getSession();
			session.setAttribute("username", uName);
			session.setAttribute("employeId", this.selectedEmployee.getEmployee_id()+"");
			return "comAllEmployee";
		//if the access type is employee get session and return the home page of employee
		} else if (result) {
			// get Http Session and store username
			HttpSession session = Util.getSession();
			session.setAttribute("username", uName);
			session.setAttribute("employeId", this.selectedEmployee.getEmployee_id()+"");		
			return "employeeHomePage";

		} else {
            //is login doesn't pass validation alert the user accordingly
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Invalid Login!", "Please Try Again!"));

			// invalidate session, and redirect back to index page
			message = "Invalid Login. Please Try Again!";
			return "index";
		}
	}
	

	// User Logout
	public String logout() {
		//destory session and return the user to the index page
		HttpSession session = Util.getSession();
		session.invalidate();
		return "index";
	}
	
    
	// Add Out Of office Implementation
	public void addOutOffice(ActionEvent event){
			
		try {
	 	    //Get the employee object who does the updating  
            Employee employee = employeeService.searchByEmployeeNo(this.selectedEmployee.getEmployee_no());
            //Check if the employee is updating the out of office for the first time
            Date officedate_in = null;
            int status = 0;
            if( employee.getActiveOutOffice() != null) {
            	status = employee.getActiveOutOffice().getStatus();
            	officedate_in = employee.getActiveOutOffice().getOffice_datein();
            }
            Date date = new Date(); /*time stamp of employee when is trying to edit the parking space
            if the parkingSpace is taken up and the update time before than office_date_in time, we finish this 
            method and return a prompt message.*/
            if ((status == 1) && date.before(officedate_in)) {
                String alertMsg = "Your parkingSpace has been taken up, please contact the Admin.";
                System.out.println(alertMsg);
                FacesMessage msg = new FacesMessage(alertMsg);  
        		FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            // user validation input out of office dates    
            } else if(endDate.before(startDate)){
				FacesMessage msg = new FacesMessage("End Date cannot be before Start Date.");  
				FacesContext.getCurrentInstance().addMessage(null, msg);  
				return;
			}
            // update employee's Out Of office, will update table out_of_office accordingly.
            employee.addOutOfOffice(startDate, endDate);
            employee.getActiveOutOffice().setStatus(0);
            //update the employee object
            employeeService.UpdateEmployee(employee);
            selectedEmployee = employee;
            //reset the update out office UI dialog fields
            resetDialogOutOffice();
        } catch (Exception ex) 
        {
            ex.printStackTrace();
        }
					
	}
	
	//Reset the AddNote dialog field
	private void resetDialogOutOffice() {
		startDate = null;
		endDate = null;
	}


	//Show Parking space dialog Option
	public void showOooDialogAction() {
		//instantiate the faces context
		FacesContext fc = FacesContext.getCurrentInstance();
		//using the UI dialog load the employee ID (key value) from the table
		Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
		//get the ID
		String oooId = params.get("paramOooId");
		// get the Out Of Office object by ID 
		selectedooo = employeeService.getOutOfOfficeById(Integer.parseInt(oooId));			
	}
   	    
		 
    // Web Allocation Request Implementation
	public void webAllocationRequest(ActionEvent event) { 
        //get the employee ID
		String employeeId = this.selectedEmployee.getEmployee_no();
		//get the out of office ID
		int outOfOfficeId = selectedooo.getOutofoffice_id();
		Employee oooEmployee = null; 
		try {
			//Check if the employee has any pending allocation request
			Employee employee = employeeManagement.searchByEmployeeNo(employeeId);
			if (employee.getAllocationRequests().size() >= 1) {
				return ;
			}
			// find outOfOffice Object
			OutOfOffice outOfOffice = employeeManagement.getOutOfOfficeById(outOfOfficeId);
			//load the Employee object whos out of the office
			oooEmployee = outOfOffice.getEmployee();
            //if the status of the out of office is 1 the request is not possibile
			if(outOfOffice.getStatus()==1){
				return;
			}else{
				//otherwise move on and set the allocation to 1
				outOfOffice.setStatus(1);
			}
			// create new AllocationRequest Object
			AllocationRequest allocationRequest = new AllocationRequest();
			allocationRequest.setRequestdate(new Date());
            //add the allocation request to the Employee object
			employeeManagement.addAllocationRequest(allocationRequest);
			employee.addAllocationRequest(allocationRequest);
            //update the employee object
			employeeManagement.UpdateEmployee(employee);
            //persist the allocation req in the out of office object (i.e status change to 1)
			outOfOffice.addAllocationRequest(allocationRequest);
            //update the out of office employee object
			employeeManagement.UpdateEmployee(oooEmployee);

		} catch (Exception ex) 
		{
			ex.printStackTrace();
			return ;
		}

	}
    
	
    //Getters and Setters
	public List<ParkingSpace> getAllFreeSpaces(){
        
		return employeeService.findAllSpaces();

	}
	
	public List<OutOfOffice> getActiveOutOfOfficeIntimations(){
        
		return employeeService.getActiveOutOfOfficeIntimations();

	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	
	public Date getStartDate() {
		return startDate;
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

	public OutOfOffice getSelectedooo() {
		return selectedooo;
	}

	public void setSelectedooo(OutOfOffice selectedooo) {
		this.selectedooo = selectedooo;
	}

}
