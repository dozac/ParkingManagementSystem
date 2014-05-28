package com.oracle.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.domain.AllocationRequest;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.staffmanagement.domain.OutOfOffice;
import com.oracle.staffmanagement.domain.ParkingSpace;

import javax.ws.rs.core.MediaType;



@Path("/spaces")
@Stateless
public class ParkingSpacesResource {
	
	
	@EJB
	private EmployeeManagementServiceLocal employeeManagement;
	
	@GET
	@Produces("application/json")
	//Get JSON Object from JPQL query
	public List<ParkingSpace> getAllFreeSpaces(){
		return employeeManagement.getAllFreeSpaces();

	}
    
	//REST resource for getting the active Out Of office objects on Android
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ooo")
	public List<OutOfOffice> getOutOfOfficeIntimations(){
		// return the out of office object
		List<OutOfOffice> oooList = new ArrayList<OutOfOffice>();
		oooList = employeeManagement.getActiveOutOfOfficeIntimations();
		return oooList;
	}
	
	//REST resource for Parking allocation Request on  Android 
	@POST
    @Produces("application/json")
    @Path("/allocated")
    public String allocatedSpace(@FormParam("employeeId") String employeeId, @FormParam("outOfOfficeId") int outOfOfficeId) {
        try {
            // find employee object by employee ID        
            Employee employee = employeeManagement.searchByEmployeeNo(employeeId);
           List<AllocationRequest> allAllocationRequests= employeeManagement.getAllocationRequests();
           //check if the employee have done already a parking allocation request
           if(allAllocationRequests!=null){
               for(AllocationRequest allocationRequest:allAllocationRequests ){
                  if( allocationRequest.getEmployee()!=null && allocationRequest.getEmployee().getEmployee_no().equals(employeeId)){
                      // if there is a AllocationRequest for the employee, then the system won't let the guy request another one.
                      return "You have already apply one, can't apply more!";
                  }
               }
           }

            //find outOfOffice object by ID
            OutOfOffice outOfOffice = employeeManagement.getOutOfOfficeById(outOfOfficeId);
            //if status is 1 it means that somebody else already applied in the mantime for the same space
            if(outOfOffice.getStatus()==1){
                System.out.println("### This place has been taken...");
                return "The space was taken by others, please choose another one!";
            }else{
                outOfOffice.setStatus(1);
            }
            //instantiate AllocationRequest
            AllocationRequest allocationRequest = new AllocationRequest();
            //add the allocation request to the employee object 
            employee.addAllocationRequest(allocationRequest);
            //up[date the out of office
            outOfOffice.addAllocationRequest(allocationRequest);
            //pass the date 
            allocationRequest.setRequestdate(new Date());
            //persit the allocation request 
            employeeManagement.addAllocationRequest(allocationRequest);

        } catch (Exception ex) 
        {
            ex.printStackTrace();
            return "Apply space failed";
        }
        //return msg to the Android device
        return "Succeed!";
    }
			
}
