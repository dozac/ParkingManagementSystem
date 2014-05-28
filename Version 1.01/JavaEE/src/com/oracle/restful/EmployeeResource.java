package com.oracle.restful;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.domain.Employee;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

@Path("/employees")
@Stateless
public class EmployeeResource {
    
	//date format
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @EJB
    private EmployeeManagementServiceLocal employeeManagement;
    
    //REST resource for employee list on Android 
    @GET
    @Produces("application/json")
    public List<Employee> getAllEmployees() {

        return employeeManagement.getAllEmployees();
    }

    //REST resource for the Update Out of office on Android 
    @POST
    @Produces("application/json")
    @Path("/updateOutOfOffice")
    public String outOfOffice(@FormParam("employeeId") String employeeId, @FormParam("dateOut") String dateOut, @FormParam("dateIn") String dateIn) {
        
        
        System.out.println("employeeId:" + employeeId + ", dateOut:" + dateOut + ",dateIn:" + dateIn);
        try {
            Employee employee = employeeManagement.searchByEmployeeNo(employeeId);
            Date officedate_in = employee.getActiveOutOffice().getOffice_datein();
            int status = employee.getActiveOutOffice().getStatus();
            Date date = new Date(); //time stamp of employee when is trying to edit the parking space
            /* if the parkingSpace is taken up and the update time before than office_date_in time, we finish this 
            method and return a prompt message.*/
            if ((status == 1) && date.before(officedate_in)) {
                return "Your parkingSpace has been taken up, please contact the Admin.";
            }
            // else update the out of office
            // parse the out and in date.
            Date dateOutDate = dateFormat.parse(dateOut);
            Date dateInDate = dateFormat.parse(dateIn);
            // update employee's Out Of office, will update table out_of_office accordingly.
            employee.addOutOfOffice(dateOutDate, dateInDate);
            employee.getActiveOutOffice().setStatus(0);
            employeeManagement.UpdateEmployee(employee);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error on the server side. Reason:"+ex.getMessage();
            // Logger.getLogger(EmployeeResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Succeed!";
    }
    
     
}
