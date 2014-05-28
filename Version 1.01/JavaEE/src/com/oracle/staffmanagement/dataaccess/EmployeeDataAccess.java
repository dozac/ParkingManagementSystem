package com.oracle.staffmanagement.dataaccess;

import java.util.List;
import java.util.Map;
import java.text.ParseException;

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

/**
 * Employee Data Access Implementation Interface
 */

public interface EmployeeDataAccess {

	public abstract List<Employee> findAll();
	public abstract List<ParkingSpace> findAllSpaces();
	public List<AllocationRequest> findAllocationRequest();
	public abstract Employee findEmployeeDetails(int employeeId);
	public abstract Employee findByEmployeeNoo(String employee_no);
	public abstract List<Employee> findBySurname(String surname);	
	public abstract Employee findByEmployeeNo(String employee_no);	
	public abstract void insert(Employee newEmployee);	
	public abstract void updateEmployee(Employee updateEmployee);	
	public void deleteAllocationRequest(AllocationRequest allocationRequest);
	public List<ParkingAllocation> findAllParkingAllocation();
	public List<ParkingAllocation> findNonActiveParkingAllocation();
	
	public ParkingSpace findParkingSpaceById(int parkingSpaceId);
	public abstract List<Block> findAllBlocks();	
	public void insertVisitor(Visitor visitor);
	public void updateVisitor(Visitor visitor);	
	public Employee findEmloyeeByVisitor(String visitorName);
	public List<Visitor> findAllVisitor();
	public List<ParkingSpace> findParkingSpaceByBlockId(String blockId);
	public List<Object[]> getOutOfficeSpaces();
	public List<OutOfOffice> getActiveOutOfOfficeIntimations();
	
	public OutOfOffice findOutOfOfficeById(int outOfOfficeId);
	public void addAllocationRequest(AllocationRequest allocationRequest);  
	public ParkingSpace findParkingSpaceByBlockAndId(Integer blockId,Integer parkingSpaceId) ;
	public Visitor findVisistorById(int visitorId);
	public void removeVisitor(int visitorId);
	
	public Employee findEmployeeByVisitorId(int visitorId);
	public void deleteParkingSpace(int employeeID);	
	public void updateNote(Note note);
	public void updateContact(Contact contact);
	public void updateCar(Car car);
	public void reverseOutOffice(int outOfficeID);
	public List<ParkingSpace> findAvailableParkingSpaceByBlockId(String blockId);
	
	public Map<String, Long> retrieveEmployeeYearlyData(String currentYear) throws ParseException;
	public Map<String, Long> retrieveParkingYearlyData(String currentYear) throws ParseException;
	public Map<String, Long> retrieveVisitorYearlyData(String currentYear) throws ParseException;
	public void updateAllocationRequestBySql(String updateSql);
	public void updateOutOfOffice(OutOfOffice outOfOffice);

}