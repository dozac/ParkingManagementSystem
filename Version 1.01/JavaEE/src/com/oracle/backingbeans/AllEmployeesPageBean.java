package com.oracle.backingbeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.domain.Block;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.staffmanagement.domain.ParkingAllocation;
import com.oracle.staffmanagement.domain.ParkingSpace;

@ViewScoped
@ManagedBean(name="allEmployeesPageBean")
public class AllEmployeesPageBean  {
/**
* It contain the business Logic for the handling the Employees table and behaviours 
 */
    
	@EJB
	private EmployeeManagementServiceLocal employeeService;
	private Employee selectedEmployee;
	public List<SelectItem> listBlock;
	public List<SelectItem> listParkingSpace;
	private List<Employee> employeeList;	
	public ParkingSpace selectedParkingSpace;
	public ParkingAllocation selectedParkingAllocation;
	public String selectedParkingSpaceId ="";
	public String selectedBlockId="";
	public Block selectedBlock;
	public int selectedEmployeeId;

	private UIData dataTable;
    
	//when EJB instantiated call and load All employee object
	@PostConstruct
	public void init()
	{
		employeeList = employeeService.getAllEmployees();
	}

	//Constructor
	public AllEmployeesPageBean(){

		this.selectedEmployee = new Employee();
		this.selectedBlockId = "";
		this.listBlock = new ArrayList<SelectItem>();
		this.listParkingSpace = new ArrayList<SelectItem>();	
	}
	
	//get all employees
	public List<Employee> getAllEmployees()
	{
		return employeeService.getAllEmployees();
	}

	//get individual Employee Details by ID
	public Employee getEmployeeDetails(int employeeId)
	{

		return employeeService.getEmployeeDetails(employeeId);
	}

	//display individual employee details and return the employee page.	
	public String showEmployee(){
		this.selectedEmployee  = (Employee)dataTable.getRowData();
		Employee employee = getEmployeeDetails(this.selectedEmployee.getEmployee_id());
		selectedEmployee = employee;
		return "comEmployeeDetail"; 
	}

    
	// Add Permanent Parking Space to Employee
	public void addParkingSpace(ActionEvent event)
	{
		try
		{
			//get the employee Id and check if the employee has already a parking space assigned
			this.selectedEmployee = this.employeeService.getEmployeeDetails(this.selectedEmployee.getEmployee_id());
			ParkingAllocation ps = this.selectedEmployee.getPermParking();
			//if a parking space is already assigned to employee return below msg
			if (ps != null) {    		
				FacesMessage msg = new FacesMessage("The employee has already a parking space no " + ps.getParkingspace().getSpace_no() + 
						" Please cancel the parking space before assining a new one");     
				FacesContext.getCurrentInstance().addMessage(null, msg);  
			//if no parking space assigned then we allocate the space
			} else { 
				//check for the parking space object (no and block)
				int psId = Integer.parseInt(selectedParkingSpaceId);
				ParkingSpace parkingSpace = this.employeeService.findParkingSpaceById(psId);
                // set instantiate the parking object for adding a  new entry
				ParkingAllocation pa = new ParkingAllocation();
				//set the permanent to true
				pa.setIs_permanent(true);
				//set the parking space number
				pa.setParkingspace(parkingSpace);
				//bind the parking space to the employee object
				this.selectedEmployee.addParkingAllocation(pa);
				//update employee object
				this.employeeService.UpdateEmployee(this.selectedEmployee);
				//update and refresh Employee object 
				this.employeeList = employeeService.getAllEmployees();
				//reset the UI dialog
				resetDialogAddDelete();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}



	//Remove Parking Space
	public void removeParkingSpace(ActionEvent event)
	{
		try {   
            //get the employee id
			this.selectedEmployee = this.employeeService.getEmployeeDetails(this.selectedEmployee.getEmployee_id());
			//get the permanent parking space object
			ParkingAllocation ps = this.selectedEmployee.getPermParking();
            //if no permanent parking space allocated, alert user
			if (ps == null) {
				System.out.println("================= IF STATEMENT = " + this.selectedEmployee.getEmployee_id());   		
				FacesMessage msg = new FacesMessage("No Permanent Space allocated to this Employee");     
				FacesContext.getCurrentInstance().addMessage(null, msg);
		    //if a permanent parking space assigned continue 
			} else {
				//remove the parking space no of the selected employee from the Parking allocation Table
				selectedEmployee.getParkingAllocation().remove(ps);
				//update employee object
				employeeService.UpdateEmployee(selectedEmployee);
				//remove the parking allocation ID, now the entry row is completely removed from the Parking allocation table
				employeeService.deleteParkingSpace(ps.getAllocation_id());
				//get all employees and refresh the object
				this.employeeList = employeeService.getAllEmployees();
				//reset the UI dialog
				resetDialogAddDelete();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}	
     
	//Ajax behaviour to handle the block change on the UI
	public void handleBlockChange(AjaxBehaviorEvent abe)
		{   
			if ((selectedBlockId != null) && (selectedBlockId.length()>0))
			{   /*Loop through the parking space object and retrive only the parking spaces which are 
				not assigned to an Employee*/
				listParkingSpace.clear();
				for(ParkingSpace ps : employeeService.findAvailableParkingSpacesByBlock(selectedBlockId)) {
					listParkingSpace.add(new SelectItem(ps.getPspace_id()+"", ps.getSpace_no()+""));
				}
			}		
		}

	//Show Parking space dialog Option and display employee name on the dialog
	public void showParkingSpaceDialogAction() {
		FacesContext fc = FacesContext.getCurrentInstance();
		System.out.println("x");
		Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
		System.out.println("y");
		String empId = params.get("paramEmployeeId");
		System.out.println("z");
		if( empId != null && empId.length() > 0 ) {
			Employee employee = getEmployeeDetails( Integer.parseInt(empId) );
			selectedEmployee = employee;	    	
			RequestContext rc = RequestContext.getCurrentInstance();
		} else {
			System.out.println("-------------- EMPLOYEE ID is NULL --------------");
		}
	}
	
	//Get the list of all parking blocks
	public List<SelectItem> getListBlock()
	{
		try
		{	listBlock.clear();
		//Loop through the block Object and retrive all blocks
		for(Block b : this.employeeService.findAllBlocks()) {
			listBlock.add(new SelectItem(b.getBlock_id()+"", b.getBlock_code()));
			
		}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return this.listBlock;
	}
     
	//Reset the AddVisitor dialog fields on the UI
	private void resetDialogAddDelete() {
		this.getListBlock();
		// clear the parking space field and block
		listParkingSpace.clear();
		selectedParkingSpaceId = "";
		selectedBlockId="";
		}
	
	//show the name of employee on the parking allocation dialog
	public void showParkingSpaceDialog(ActionEvent event){
		//get the employee object from the Employee list
		this.selectedEmployee  = (Employee)dataTable.getRowData();
		Employee employee = getEmployeeDetails(this.selectedEmployee.getEmployee_id());
		selectedEmployee = employee;	    	
	}

	//Getter and setters for the Filtering Employee Object
	private List<Employee> filteredEmployee;

	public void setFilteredEmployee(List<Employee> filteredEmployee) {
		this.filteredEmployee = filteredEmployee;
	}

	public List<Employee> getFilteredEmployee() {  
		return filteredEmployee;  
	} 

	//Getters and Setters for all variables 
	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	} 

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public UIData getDataTable() {
		return dataTable;
	}

	public void setDataTable(UIData dataTable) {
		this.dataTable = dataTable;
	}

	public void setSelectedBlockId(String selectedBlockId) {
		this.selectedBlockId = selectedBlockId;
	}

	public ParkingSpace getSelectedParkingSpace() {
		return selectedParkingSpace;
	}
	public void setSelectedParkingSpace(ParkingSpace selectedParkingSpace) {
		this.selectedParkingSpace = selectedParkingSpace;
	}
	
	
	public void setListBlock(List<SelectItem> listBlock) {
		this.listBlock = listBlock;
	}

	public void setListParkingSpace(List<SelectItem> listParkingSpace) {
		this.listParkingSpace = listParkingSpace;
	}

	public List<SelectItem> getListParkingSpace() {
		return this.listParkingSpace;
	}

    
	public String getSelectedBlockId()
	{
		return this.selectedBlockId;
	}

	public void setSelectedBlock(String selectedBlockId)
	{
		this.selectedBlockId = selectedBlockId;
	}

	
	public String getSelectedParkingSpaceId()
	{
		return this.selectedParkingSpaceId;
	}

	public void setSelectedParkingSpaceId(String parkingSpaceId)
	{
		this.selectedParkingSpaceId = parkingSpaceId;
	}

	public Block getSelectedBlock()
	{
		return this.selectedBlock;
	}

	public void setSelectedBlock(Block selectedBlock)
	{
		this.selectedBlock = selectedBlock;
	}
	
	
	public int getSelectedEmployeeId() {
		return selectedEmployeeId;
	}


	public void setSelectedEmployeeId(int selectedEmployeeId) {
		this.selectedEmployeeId = selectedEmployeeId;
	}  


	//Inline editing for the Employee Records
	public String onEdit(RowEditEvent event) {
		//Hold the Old value
		Employee oldValue = (Employee) event.getObject();
		//Hold the new value
		Employee newValue = (Employee) event.getObject();
		try 
		{   //Update the employee object
			employeeService.UpdateEmployee(newValue);
			return "comAllEmployee";
		} catch (Throwable e) {	

			e.printStackTrace();
			return "systemDown";
		}
	}  
    
	//Alert Message when in line editing is cancelled by the user
	public void onCancel(RowEditEvent event) {  
		FacesMessage msg = new FacesMessage("Event Cancelled", ((Employee) event.getObject()).getEmployee_no());  
		FacesContext.getCurrentInstance().addMessage(null, msg);  
	}

}
