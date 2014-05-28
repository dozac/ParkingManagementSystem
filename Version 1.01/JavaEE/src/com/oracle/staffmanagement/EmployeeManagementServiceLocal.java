package com.oracle.staffmanagement;

import java.util.List;
import java.util.Map;
import java.text.ParseException;

import javax.ejb.Local;

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

@Local
public interface EmployeeManagementServiceLocal 
{
	/**
	* It contain the interface for local client calls
	 */
	public void registerEmployee(Employee newEmployee) throws SystemUnavailableException ;
	public void UpdateEmployee(Employee newUpdateEmployee) throws SystemUnavailableException;
	public void addParkingAllocation(Employee newUpdateEmployee) throws SystemUnavailableException;
	public List<Employee> getAllEmployees();
	public List<AllocationRequest> getAllocationRequests();
	public Employee getEmployeeDetails(int employeId);
	public List<ParkingSpace> getAllFreeSpaces();
	public List<Employee> searchBySurname(String surname);
	public Employee searchByEmployeeNo(String employee_no);
	public Employee searchByEmployeeNoo(String employee_no);
	
	public void deleteAllocationRequest(AllocationRequest allocationRequest);
	public List<ParkingAllocation> getParkingAllocations();
	public List<ParkingAllocation> getNonActiveParkingAllocations();
	
	public ParkingSpace findParkingSpaceById(int parkingSpaceId);
	public List<Block> findAllBlocks();
	public List<ParkingSpace> findAllSpaces();
	
	public List<Visitor> getVisitors();
	public List<Block> getAllBlocks();
	public List<ParkingSpace> getParkingSpacesByBlock(String blockId);
	public void addVisitor(Visitor visitor);
	public void updateVisitor(Visitor visitor);
	public Employee getEmployeeByVisitor(String visitorName);
	public List<Object[]> getOutOfficeSpaces();

	public List<OutOfOffice> getActiveOutOfOfficeIntimations();	
	public OutOfOffice getOutOfOfficeById(int outOfOfficeId);
	public void addAllocationRequest(AllocationRequest allocationRequest);	
	public ParkingSpace getParkingSpaceByBlockAndId(Integer blockId,Integer parkingSpaceId);
	
	public Visitor getVisitorById(int visitorId);
	public void removeVisitor(int visitorId);
	public Employee getEmployeeByVisitorId(int visitorId);
	public void deleteParkingSpace(int employeeID);
	
    public void updateNote(Note note);
	public void updateContact(Contact contact);
	public void updateCar(Car car);
	public void reverseOutOffice(int outOfficeID);
	public List<ParkingSpace> findAvailableParkingSpacesByBlock(String blockId);
	
	public Map<String, Long> getEmployeeYearlyData(String currentYear)throws ParseException;
	public Map<String, Long> getParkingYearlyData(String currentYear)throws ParseException;
	public Map<String, Long> getVisitorYearlyData(String currentYear)throws ParseException;
	public void updateAllocationRequestBySql(String updateSql);
	public void updateOutOfOffice(OutOfOffice outOfOffice);
	
}
