package com.oracle.restful;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.util.EncryptDecrypt;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Path("/auth")
@Stateless
public class AuthResource {

	private static int countMethodCalls = 0;
	public static final int MIN_PASSWORD_LENGTH = 3;
	public static final int MAX_PASSWORD_LENGTH = 20;
	
	@EJB
	private EmployeeManagementServiceLocal employeeManagement;
    private EncryptDecrypt encryptDecrypt;
	
    //constructor
    public AuthResource(){
		
		encryptDecrypt = new EncryptDecrypt();	
	}
	
    //REST resource for User Login on Android 
    @POST
    @Produces("application/json")
    @Path("/user")
    public Employee authenticate(@FormParam("username") String username, @FormParam("password") String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        //check for the employee object based on the username (employee number)
        Employee authenticatedEmployee = null;
        try {
            authenticatedEmployee = employeeManagement.searchByEmployeeNo(username);
        } catch (Exception e) {
            authenticatedEmployee = null;
        }// check credential and validate the login request
        if (authenticatedEmployee != null && authenticatedEmployee.getPassword().equals(encryptDecrypt.encrypt(password))) {
            return authenticatedEmployee;
        } else {
            authenticatedEmployee = new Employee();
            authenticatedEmployee.setEmployee_no("##INVALID##");
            return authenticatedEmployee;
        
        }
    }
    
  //REST resource for Change Password on Android 
    @POST
    @Produces("application/json")
    @Path("/changepassword")
    public String changePassword(@FormParam("employeeId") String employeeId, @FormParam("oldPassword") String oldPassword, @FormParam("newPassword") String newPassword) {
        //msg that will return to the user 
    	String result = null;
        Employee updateEmployee = null;
        try {
        	//look for the employee object
            updateEmployee = employeeManagement.searchByEmployeeNo(employeeId);
            //validate new password details 
            if (updateEmployee != null && updateEmployee.getPassword().equals(encryptDecrypt.encrypt(oldPassword))) {

                if (this.isValidPassword(newPassword)) {
                	//encrypt new password
                    String encryptedPassword = encryptDecrypt.encrypt(newPassword);
                    //persist and update employee object
                    updateEmployee.setPassword(encryptedPassword);
                    employeeManagement.UpdateEmployee(updateEmployee);
                    //return msg depending on the validation to user which will be tosted on his Android device.
                    result = "Update successfully!";
                } else {
                    result = "New password is not valid!(password shouldn't start from a number and should contains alphabets and numbers.)";
                }

            } else {
                result = "Unvalid old password!";
            }
        } catch (Exception e) {
            result = "Error:" + e.getMessage();
        }
        return result;
    }
    
    
   	//Password Validation
	private boolean isValidPassword(String newPassword)
		{   // check password pattern 
			boolean isValid = false;
			String passwordPattern  ="^[^\\d].*\\d"; // password shouldn't start from a number and should contains alphabets and numbers.
			FacesMessage msg = null;
			//check if new password is equal to the confirmation password
			if ((newPassword == null) || (newPassword.equals("")))
			{
                            return false;
			}
				if (newPassword.matches(passwordPattern))
				{
					isValid = true;
				}
			return isValid;
		}
    
     
}


