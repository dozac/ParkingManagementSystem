package com.oracle.backingbeans;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.SystemUnavailableException;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.util.EncryptDecrypt;


@ManagedBean(name="enterEmployee")
public class EnterEmployeePageBackingBean {
	/**
	 * It contain the business Logic for the creation of the Employee Object  
	 */
    //Employee variables 
	private String employee_no;
	private String firstname;
	private String lastname;
	private String password = passwordGenerator();
	private String access_type;
	private Date application_date ;
	private Date allocation_exp_date;
	private String employeeStatus;
	
	//Note variables
	private String notes;
	
	//Out of Office variables
	private Date office_dateout ;
	private Date office_datein;
	
	//Contact variables
	private String company;
	private String email;
	private int phonenumber;
	private String linemanager;
	private int postcode;
	private String lineofbusiness;
	
	//Car Variables
	private String vehicle_regno;
	private String make;
	private String model;
	private String colour;
	private Date date_vehicle_added;
	private String carMotorbikeSpace;
	private String stickernumber;
	
	// Change Password variables
	private String currentPassword;
	private String newPassword;
	private String confirmPassword;
	public static final int MIN_PASSWORD_LENGTH = 3;
	public static final int MAX_PASSWORD_LENGTH = 20;
	
	private EncryptDecrypt encryptDecrypt;
		
	//Handle Employment status selector
    private Map<String, String> status = new HashMap<String, String>();
    private Map<String, String> empAccess = new HashMap<String, String>();
	
	//Constructor
	public EnterEmployeePageBackingBean() {	
        status.put("Yes", "Yes");  
        status.put("No", "No");
        empAccess.put("admin", "admin");  
        empAccess.put("employee", "employee"); 
        encryptDecrypt = new EncryptDecrypt();
	}
	
	@EJB
	private EmployeeManagementServiceLocal employeeService;
	
	//Create Employee object when new Employee is added to the system
	public String createEmployee() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, 
	NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, 
	BadPaddingException {
		//Enrypt the generated password
		String encryptedPassword = encryptDecrypt.encrypt(password);	
        //create new Instance of the Employee Object
		Employee newEmployee = new Employee(employee_no, firstname, lastname, encryptedPassword, access_type, 
				application_date, employeeStatus, allocation_exp_date);
		//add note if any
		newEmployee.addNote(notes);
		//add out of office note if any
		newEmployee.addOutOfOffice(office_dateout, office_datein);
		//add Contact details if any
		newEmployee.addContact(company, email, phonenumber, linemanager, postcode, lineofbusiness);
		//add vehilce details if any
		newEmployee.addCar(vehicle_regno, make, model, colour, date_vehicle_added, carMotorbikeSpace, 
				stickernumber);
		try 
		{   //Persit new instance of Employee object to DB
			employeeService.registerEmployee(newEmployee);
			//return the all employee page 
			return "comAllEmployee";
		} catch (Throwable e) {	

			e.printStackTrace();
			return "comSystemDown";
		}
	}
	
	//Update Password  Implementation 
	public void updatePassword(ActionEvent actionEvent) throws SystemUnavailableException, NoSuchAlgorithmException, InvalidKeySpecException
	{   
		//Context Used for password Validation
		RequestContext context = RequestContext.getCurrentInstance(); 
		FacesMessage msg = null;
		boolean isPasswordUpdated = false;
        //check if the current password is correct
		if (this.isUserExist(this.currentPassword))
		{   //check if the new password match with the repeat password 
			if (this.isValidPassword(this.newPassword, this.confirmPassword))
			{   
				//get the employee ID from the session
				isPasswordUpdated = true;
				String employeId = (String)Util.getSession().getAttribute("employeId");
				//Load the employee object
				Employee updatedEmployee = employeeService.getEmployeeDetails(Long.valueOf(employeId).intValue());
                //Encrypt the new password
				String encryptedPassword = encryptDecrypt.encrypt(newPassword);
				//Update the employee object with the new encrypted password
				updatedEmployee.setPassword(encryptedPassword);
				employeeService.UpdateEmployee(updatedEmployee);
				//inform the User that password was successfully changed
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, updatedEmployee.getFirstname(),"Password Changed successfully.");
			}
			else
			{   //if the validation password don't pass for the reason below, inform the User
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Password should be combination of alphabets and numbers","Password should not start from numbers"); 
				isPasswordUpdated = false;

			}
		}
		else
		{   //if the validation password don't pass for the reason below, inform the User
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Current Password is incorrect","Please enter the correct password.");
			isPasswordUpdated = false;
		}
        //Clear the dialog UI fields
		FacesContext.getCurrentInstance().addMessage(null, msg); 
		context.addCallbackParam("isPasswordUpdated", isPasswordUpdated);
		this.currentPassword = null;
		this.newPassword = null;
		this.confirmPassword = null;
	}

	//Check if the user who's trying to change password is the real user. Do this by asking the current password
	private boolean isUserExist(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
	{   
		// we assume that user doesn't exist
		boolean isUserExist = false; 
		//get the logged user session
		HttpSession session = Util.getSession();
		//get the employee ID
		String employeIdStr = (String)session.getAttribute("employeId");
		Long employeId = Long.parseLong(employeIdStr);
		//load the Employee Object
		Employee employee = employeeService.getEmployeeDetails(employeId.intValue());
		//load the encrypted password
		String encryptedPassword = encryptDecrypt.encrypt(password);
		//compare if encrypted password with the pass confirmation
		if (encryptedPassword.equals(employee.getPassword()))
		{
			isUserExist = true;
		}
		return isUserExist;
		
	}
				
	
	//New Password Validation method
	private boolean isValidPassword(String newPassword, String confirmPassword)
		{
			boolean isValid = false;
			// password shouldn't start from a number and should contains alphabets and numbers.
			String passwordPattern  ="^[^\\d].*\\d"; 
			FacesMessage msg = null;
			if ((newPassword == null) || (newPassword.equals("")))
			{   //if pattern doesn't match inform the user
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"New Password Can't be Empty","Please enter the new password.");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			if ((confirmPassword == null) || (confirmPassword.equals("")))
			{   //if pattern doesn't match and the new pass field is enpty inform the user
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Confirm Password Can't be Empty","Please enter the confirm password.");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			// check match between new password and repeat password.
			if (newPassword.equals(confirmPassword))
			{
				if (newPassword.matches(passwordPattern))
				{
					isValid = true;
				}
			}
			return isValid;
		}
		 
    //rest password error msg
	public void initDialog()
		{
			RequestContext.getCurrentInstance().reset("password_chg_form:password_chg_msg");  
		}
	
	
	//Generate password
	private static final Random RANDOM = new SecureRandom();
	public static final int PASSWORD_LENGTH = 8;

	public static String passwordGenerator() {
		// Pick from some letters that won't be easily mistaken for each other
		String randonChar = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

		String passGen = "";
		for (int i=0; i<PASSWORD_LENGTH; i++)
		{
			int index = (int)(RANDOM.nextDouble()*randonChar.length());
			passGen += randonChar.substring(index, index+1);
		}
		return passGen;
	}
	
	
    //Getters and setters
	public Map<String, String> getStatus() {  
		return status;  
	}  

	public void setStatus(Map<String, String> status) {  
		this.status = status;  
	} 
	
	//Getters and setters EMPLOYEE
	public String getEmployeeStatus() {
		return employeeStatus;
	}

    
	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}
    
    
	public String getEmployee_no() {
		return employee_no;
	}


	public void setEmployee_no(String employee_no) {
		this.employee_no = employee_no;
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


	public void setPassword(String password) {
		this.password = password;
	}


	public Date getApplication_date() {
		return application_date;
	}


	public void setApplication_date(Date application_date) {
		this.application_date = application_date;
	}

	public Date getAllocation_exp_date() {
		return allocation_exp_date;
	}


	public void setAllocation_exp_date(Date allocation_exp_date) {
		this.allocation_exp_date = allocation_exp_date;
	}
	
	public String getAccess_type() {
		return access_type;
	}
    
    public void addMessage(FacesMessage message) {  
        FacesContext.getCurrentInstance().addMessage(null, message);  
    }

	public void setAccess_type(String access_type) {
		this.access_type = access_type;
	}


	//Getter and setters NOTES
	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}
	
  
	//Getter and setters OUT OF OFFICE
	public Date getOffice_dateout() {
		return office_dateout;
	}


	public void setOffice_dateout(Date office_dateout) {
		this.office_dateout = office_dateout;
	}


	public Date getOffice_datein() {
		return office_datein;
	}


	public void setOffice_datein(Date office_datein) {
		this.office_datein = office_datein;
	}
	
	
	//Getter and setters CONTACT
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
	
	//Getter and setters Vehicle
	public void todo(){}


	public String getVehicle_regno() {
		return vehicle_regno;
	}


	public void setVehicle_regno(String vehicle_regno) {
		this.vehicle_regno = vehicle_regno;
	}


	public String getMake() {
		return make;
	}


	public void setMake(String make) {
		this.make = make;
	}


	public String getModel() {
		return model;
	}


	public void setModel(String model) {
		this.model = model;
	}


	public String getColour() {
		return colour;
	}


	public void setColour(String colour) {
		this.colour = colour;
	}


	public Date getDate_vehicle_added() {
		return date_vehicle_added;
	}


	public void setDate_vehicle_added(Date date_vehicle_added) {
		this.date_vehicle_added = date_vehicle_added;
	}


	public String getCarMotorbikeSpace() {
		return carMotorbikeSpace;
	}


	public void setCarMotorbikeSpace(String carMotorbikeSpace) {
		this.carMotorbikeSpace = carMotorbikeSpace;
	}


	public String getStickernumber() {
		return stickernumber;
	}


	public void setStickernumber(String stickernumber) {
		this.stickernumber = stickernumber;
	}
	
	//Getters and Setter Password
	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	

}

   
