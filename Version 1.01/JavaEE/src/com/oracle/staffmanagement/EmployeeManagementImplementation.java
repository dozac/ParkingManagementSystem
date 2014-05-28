package com.oracle.staffmanagement;

import java.util.List;
import java.util.Map;
import java.text.ParseException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import com.oracle.staffmanagement.dataaccess.EmployeeDataAccess;
import com.oracle.staffmanagement.domain.AllocationRequest;
import com.oracle.staffmanagement.domain.Block;
import com.oracle.staffmanagement.domain.Car;
import com.oracle.staffmanagement.domain.Contact;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.staffmanagement.domain.Note;
import com.oracle.staffmanagement.domain.OutOfOffice;
import com.oracle.staffmanagement.domain.ParkingAllocation;
import com.oracle.staffmanagement.domain.ParkingSpace;
import com.oracle.staffmanagement.domain.Visitor;

@Stateless
@WebService(name="employeeManagementService")
public class EmployeeManagementImplementation 
    implements EmployeeManagementServiceRemote, EmployeeManagementServiceLocal {
	/**
	* It contain the business Logic Layer between the Java Beans and the Data Access Object 
	 */
	
	@EJB
	private EmployeeDataAccess dao;

    // Get All employee DAO
	public List<Employee> getAllEmployees() 
	{
		return dao.findAll();
	}
	
	 // Get All Allocation Request DAO
	public List<AllocationRequest> getAllocationRequests(){
		
		return dao.findAllocationRequest();
	}
	
	//Get employeeDetails DAO
	public Employee getEmployeeDetails(int employeeId)
	{
		return dao.findEmployeeDetails(employeeId);
	}
	
	 // Search employee by employee number DAO
	public Employee searchByEmployeeNoo(String employee_no)
	{
		return dao.findByEmployeeNoo(employee_no);
	}
	
	//Get All Parking Spaces Webservice DAO
     public List<ParkingSpace> getAllFreeSpaces() {
		
		return dao.findAllSpaces();
	}
     
     // Get Parking spaces list DAO
     public List<Object[]> getOutOfficeSpaces() {
    	 
    	 return dao.getOutOfficeSpaces();
     }
	
	// Update Employee Object DAO
	public void UpdateEmployee(Employee newUpdateEmployee) throws SystemUnavailableException 
	{
		dao.updateEmployee(newUpdateEmployee);
		
	}
		
	//Delete Parking Space by Employee ID DAO
	public void deleteParkingSpace(int employeeID) {
		
		dao.deleteParkingSpace(employeeID);
	}
	
	
	//Update Employee with parking space allocation DAO
	public void addParkingAllocation(Employee newUpdateEmployee) throws SystemUnavailableException 
	{
		dao.updateEmployee(newUpdateEmployee);
		
	}
		
	//Search Employee By Surname DAO
	public List<Employee> searchBySurname(String surname) {
		return dao.findBySurname(surname);
	}	
	
	//Search by employee number DAO
	public Employee searchByEmployeeNo(String employee_no){
		
		return dao.findByEmployeeNo(employee_no);
	}
	
	//Delete Allocation Request DAO
	public void deleteAllocationRequest(AllocationRequest allocationRequest) {
		dao.deleteAllocationRequest(allocationRequest);
	}
	
	//Find and return all Temporary parking allocations DAO
	public List<ParkingAllocation> getParkingAllocations()
	{
		return dao.findAllParkingAllocation();
	}
	
	//Find and return all Non Active Temporary parking allocations DAO
	public List<ParkingAllocation> getNonActiveParkingAllocations()
	{
		return dao.findNonActiveParkingAllocation();
	}
	
	//Find Parking space by ID DAO
	@Override
	public ParkingSpace findParkingSpaceById(int parkingSpaceId) {
		return dao.findParkingSpaceById(parkingSpaceId);
	}

	//Final all Parking Blocks DAO
	public List<Block> findAllBlocks() {
		return dao.findAllBlocks();
	}
	
	//Find All Visitors DAO
	@Override
	public List<Visitor> getVisitors() 
	{
		return dao.findAllVisitor();
	}
    
	//Find all Parking Blocks DAO
	@Override
	public List<Block> getAllBlocks() 
	{
		return dao.findAllBlocks();
	}

	// Get Parking spaces by Block ID DAO
	@Override
	public List<ParkingSpace> getParkingSpacesByBlock(String blockId)
	{
		return dao.findParkingSpaceByBlockId(blockId);
	}
	
	
	//Used to get the non allocated permanent parking spaces DAO
	@Override
	public List<ParkingSpace> findAvailableParkingSpacesByBlock(String blockId)
	{
		return dao.findAvailableParkingSpaceByBlockId(blockId);
	}
	
    // Add Visitor DAO
	@Override
	public void addVisitor(Visitor visitor)
	{
		dao.insertVisitor(visitor);	
	}
    
	//Update Vistor DAO
	@Override
	public void updateVisitor(Visitor visitor) 
	{
		dao.updateVisitor(visitor);	
	}
    
	//get the employee based on the visitor name DAO
	@Override
	public Employee getEmployeeByVisitor(String visitorName)
	{
		return dao.findEmloyeeByVisitor(visitorName);
	}
	

	// Find all parking spaces
	public List<ParkingSpace> findAllSpaces() {
		return dao.findAllSpaces();
	
	}
    
	// get all active Out of Office objects DAO
	@Override
	public List<OutOfOffice> getActiveOutOfOfficeIntimations() {
		return dao.getActiveOutOfOfficeIntimations();
	}
	
	//get Out of Office by ID DAO
	@Override
    public OutOfOffice getOutOfOfficeById(int outOfOfficeId) {
         return dao.findOutOfOfficeById(outOfOfficeId);
    }
	
	//add allocation Request Object 
	@Override
	public void addAllocationRequest(AllocationRequest allocationRequest){
		dao.addAllocationRequest(allocationRequest);
	}

    //get parking space by Block and ID DAO
	@Override
	public ParkingSpace getParkingSpaceByBlockAndId(Integer blockId,
			Integer parkingSpaceId) 
	{
		return dao.findParkingSpaceByBlockAndId(blockId, parkingSpaceId);
	}
    
	//get visitor by ID DAO
 	@Override
 	public Visitor getVisitorById(int visitorId) 
 	{
 		return dao.findVisistorById(visitorId);
 	}
    
 	//get remove visitor by ID DAO
 	@Override
 	public void removeVisitor(int visitorId) 
 	{
 		dao.removeVisitor(visitorId);	
 	}
 	
 	// Get employee by visitor ID DAO
 	@Override
	public Employee getEmployeeByVisitorId(int visitorId) 
	{
		return dao.findEmployeeByVisitorId(visitorId);
	}
	
    
 	//Update Note Employee Profile
 	@Override
	public void updateNote(Note note) 
	{
		dao.updateNote(note);	
	}
    
 	//Update Contact Employee Profile
	@Override
	public void updateContact(Contact contact) 
	{
		dao.updateContact(contact);
	}
    
	//Update Vehicle Employee Profile
	@Override
	public void updateCar(Car car) {
		dao.updateCar(car);
		
	}
	
	// reverse Out of office DAO
	public void reverseOutOffice(int outOfficeID){
		
		dao.reverseOutOffice(outOfficeID);
	}
	
	// get employee yearly data DAO
	@Override
	public Map<String, Long> getEmployeeYearlyData(String currentYear) throws ParseException 
	{
		return dao.retrieveEmployeeYearlyData(currentYear);
	}
    
	
	// get the Parking yearly data 
	@Override
	public Map<String, Long> getParkingYearlyData(String currentYear) 
			throws ParseException 
			{
		return dao.retrieveParkingYearlyData(currentYear);
	}
     
    //get the visitor yearly data 
	@Override
	public Map<String, Long> getVisitorYearlyData(String currentYear)
			throws ParseException 
	{
		return dao.retrieveVisitorYearlyData(currentYear);
	}
	
	//update the allocation request SQL statement DAO
	@Override
	public void updateAllocationRequestBySql(String updateSql) {
		dao.updateAllocationRequestBySql(updateSql);
	}
	
	//uopdate the out of office Object DAO
	public void updateOutOfOffice(OutOfOffice outOfOffice)
	{
		dao.updateOutOfOffice(outOfOffice);
	}
    
	//register new Employee DAO
	@Override
	public void registerEmployee(Employee newEmployee)
			throws SystemUnavailableException {
		dao.insert(newEmployee);
		
	}

	
}
