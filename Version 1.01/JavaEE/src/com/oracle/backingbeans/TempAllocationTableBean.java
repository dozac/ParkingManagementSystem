package com.oracle.backingbeans;



import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.domain.ParkingAllocation;


@ViewScoped
@ManagedBean(name="tempAllocationBean")
public class TempAllocationTableBean {
	/**
	 * It provides implementation for getting the active and non active Temporary 
	 * allocation displayed to the Commuter manager
	 */	
	
	@EJB
	private EmployeeManagementServiceLocal employeeService;
	private List<ParkingAllocation> tempAllocationList;
	private List<ParkingAllocation> tempNonActiveAllocationList;

	//when EJB is instantiated and get the temporary parking intimations
	@PostConstruct
	public void init()
	{   //get active temp allocations
		tempAllocationList = employeeService.getParkingAllocations();
		//get past/non active temp allocations
		setTempNonActiveAllocationList(employeeService.getNonActiveParkingAllocations());
	}


  
	// Getters and Setters
	public List<ParkingAllocation> getTempAllocationList() {
		return tempAllocationList;
	}


	public void setTempAllocationList(List<ParkingAllocation> tempAllocationList) {
		this.tempAllocationList = tempAllocationList;
	}



	public List<ParkingAllocation> getTempNonActiveAllocationList() {
		return tempNonActiveAllocationList;
	}



	public void setTempNonActiveAllocationList(
			List<ParkingAllocation> tempNonActiveAllocationList) {
		this.tempNonActiveAllocationList = tempNonActiveAllocationList;
	}
	

}
