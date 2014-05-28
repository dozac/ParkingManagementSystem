package com.oracle.staffmanagement.dataaccess;

import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
public class EmployeeDataAccessImplementation implements EmployeeDataAccess
{
	/**
	 * Employee Data Access Layer contains all the JPQL query used by the Enterprise Java Beans and the
	 * REST resource  
	 */	
	
	@PersistenceContext
	private EntityManager em;

	// Web service, find Parking SPaces using JPQL query
	public List<ParkingSpace> findAllSpaces() {
		Query q = em.createQuery("SELECT ps FROM Employee e, OutOfOffice ooo, ParkingAllocation pa, ParkingSpace ps "  +
				" WHERE e.employee_id = ooo.employee.employee_id AND " + 
				" CURRENT_DATE <= ooo.office_dateout AND " +
				" e.employee_id = pa.employee.employee_id " + 
				" AND pa.parkingspace.pspace_id = ps.pspace_id " 
				+ " AND pa.is_permanent= TRUE"
				);
		List<ParkingSpace> spaces = q.getResultList();	
		return spaces;	
	}

	//Web service, (JUST FOR TESTING)  - find all spaces by querying the DB using a normal SQL statement
	public List<Object[]> getOutOfficeSpaces(){

		Query q = 	em.createNativeQuery("SELECT ps.pspace_id, e.employee_id FROM Employee e, "
				+ " Out_Of_Office ooo, Parking_Allocation pa, Parking_Space ps WHERE "  
				+ "e.employee_id = ooo.employee_employee_id "
				+" AND "
				+" CURRENT_DATE <= ooo.office_dateout "
				+" AND "
				+" e.employee_id = pa.employee_employee_id "
				+" AND "
				+" pa.parking_space_pspace_id = ps.pspace_id "
				+" AND "
				+" pa.IS_PERMANENT=1;"); 
		List<Object[]> result = q.getResultList();
		return result;
	}


	// Find all Allocation Requests
	public List<AllocationRequest> findAllocationRequest(){		
		em.getEntityManagerFactory().getCache().evictAll();
		Query q = em.createQuery("select allocation from AllocationRequest allocation");
		List<AllocationRequest> allAllocationReq = q.getResultList();	
		return allAllocationReq;
		
	}

	//Find all Employees
	public List<Employee> findAll() {		
		Query q = em.createQuery("select employee from Employee employee");		
		List<Employee> results = q.getResultList();
		return results;
	}

	//Find Employee and Note
	public Employee findEmployeeDetails(int employeeId) {
		Employee employeeDet = em.find(Employee.class, employeeId);
        em.refresh(employeeDet);
		return employeeDet;
	}	

	//Insert New Employee 
	public void insert(Employee newEmployee) {
		em.persist(newEmployee);

	}

	//Update Existing Employee 
	public void updateEmployee(Employee updateNewEmployee){
		em.merge(updateNewEmployee);
		em.flush();
        Employee emp = em.find(Employee.class, updateNewEmployee.getEmployee_id());
        em.refresh(emp);
        em.getEntityManagerFactory().getCache().evictAll();
	}

	//Delete Allocation Request
	public void deleteAllocationRequest(AllocationRequest allocationRequest) {
		AllocationRequest ar = em.find(AllocationRequest.class, allocationRequest.getRequest_id());
		em.remove(ar);
	}

	//Find Employee by employee no
	public Employee findByEmployeeNo(String employee_no) {
		Query q = em.createQuery("select employee from Employee employee where employee.employee_no = :employee_no");
		q.setParameter("employee_no", employee_no);
		return (Employee) q.getSingleResult();
	}
    
	// Find Employee By Surname
	public List<Employee> findBySurname(String surname) {
		Query q = em.createQuery("select employee from Employee employee where employee.surname = :surname");
		q.setParameter("surname", surname);
		return q.getResultList();     
	}

	// Find and return all temporary parking allocation spaces
	public List<ParkingAllocation> findAllParkingAllocation() 
	{
		Query q = em.createQuery("select pa from ParkingAllocation pa where CURRENT_DATE <= pa.end_date"
				+" and pa.is_permanent = 0");
		return q.getResultList();

	}	

	// Find and return all Non Active temporary parking allocation spaces alloacted to employees
	public List<ParkingAllocation> findNonActiveParkingAllocation() 
	{
		Query q = em.createQuery("select pa from ParkingAllocation pa where CURRENT_DATE > pa.end_date"
				+" and pa.is_permanent = 0");
		return q.getResultList();
	}

	// Return parking space by ID
	public ParkingSpace findParkingSpaceById(int parkingSpaceId) {		
		ParkingSpace parkingSpace = em.find(ParkingSpace.class, parkingSpaceId);		
		return parkingSpace;
	}

	//Find all parking Blocks
	public List<Block> findAllBlocks() {
		Query q = em.createQuery("select b from Block b");
		List<Block> spaces = q.getResultList();	
		return spaces;	
	}

    // Get all Visitors
	@SuppressWarnings("unchecked")
	@Override
	public List<Visitor> findAllVisitor() 
	{
		Query q = em.createQuery("select vs from Visitor vs where vs.allocation_start_datetime is not null and vs.allocation_end_datetime is not null");
		return q.getResultList();
	}

    //Get all parking visitor parking spaces for a specific Block 
	@SuppressWarnings("unchecked")
	@Override
	public List<ParkingSpace> findParkingSpaceByBlockId(String blockId)
	{
	    Query q = em.createQuery("select ps from ParkingSpace ps where ps.block_block_id = :bblockId AND ps.is_visitor=1");
		q.setParameter("bblockId", new Integer(blockId).intValue());
		return q.getResultList();
	}

	//Used for All Employee Permanent Allocation
	@SuppressWarnings("unchecked")
	@Override
	public List<ParkingSpace> findAvailableParkingSpaceByBlockId(String blockId)
	{
	    //	Query q = em.createQuery("select ps from ParkingSpace ps where ps.block_block_id = :bblockId");
		Query q = em.createQuery("select ps from ParkingSpace ps where ps.block_block_id = :bblockId AND ps.is_visitor=0 AND "
			    + "ps.pspace_id not in (select pa.parkingspace.pspace_id from ParkingAllocation pa where pa.is_permanent=1)");
		q.setParameter("bblockId", new Integer(blockId).intValue());
		return q.getResultList();
	}


	//Add new Visitor
	@Override
	public void insertVisitor(Visitor visitor)
	{
		em.persist(visitor);
		em.flush();
		em.refresh(visitor.getEmployee());
	}

	
	//Update Visitor
	@Override
	public void updateVisitor(Visitor visitor) 
	{
		em.merge(visitor);
		em.flush();
	}


	//Find employee by visitor name
	@Override
	public Employee findEmloyeeByVisitor(String visitorName) 
	{
		Query q = em.createQuery(" select e from Employee e JOIN e.employeeVisitors ev where ev.visitor_name = :vvisitorName order by ev.allocation_start_datetime desc ");
		q.setParameter("vvisitorName", visitorName);
		Employee employee = null;
		List<Employee> employeeList = q.getResultList();
		if (employeeList != null && !employeeList.isEmpty())
		{
			employee = employeeList.get(0);	// we will take the first value as it is the latest entry and discarding the rest
		}
		return employee;
	}

	//Find and return the Out Of Office, including parking spaces, block details  - Webservice   
	@Override
	public List<OutOfOffice> getActiveOutOfOfficeIntimations() {
		Query q = em.createQuery("SELECT ooo FROM OutOfOffice ooo WHERE CURRENT_DATE <= ooo.office_datein and ooo.status=0");		
		List<OutOfOffice> oooList = q.getResultList();
		return oooList;
	}

	//Find Employee by employee number and return the employee Object
	public Employee findByEmployeeNoo(String employee_no) {		

		Employee employeeDet = em.find(Employee.class, employee_no);
		return employeeDet;
	}


	//Find OutOfOffice based on the ID - Webservice
	@Override
	public OutOfOffice findOutOfOfficeById(int outOfOfficeId) {
		Query q = em.createQuery("SELECT ooo FROM OutOfOffice ooo WHERE ooo.outofoffice_id = :outOfOfficeId");
		q.setParameter("outOfOfficeId", outOfOfficeId);
		return (OutOfOffice) q.getSingleResult();
	}

	//Add Allocation Request - WebService
	@Override
	public void addAllocationRequest(AllocationRequest allocationRequest) {
		em.persist(allocationRequest);
		em.flush();
		em.refresh(allocationRequest);
	}
    
	//Update Allocation request using a SQL query
	@Override
	public void updateAllocationRequestBySql(String updateSql) {
		Query updateQuery = em.createQuery(updateSql);
		int updateCount = updateQuery.executeUpdate();
		System.out.println("Allocation Request Update count = " + updateCount);
	}

	// Find Parking space by BLOCK and ID 
	@Override
	public ParkingSpace findParkingSpaceByBlockAndId(Integer blockId,
			Integer parkingSpaceId) {
		Query q = em.createQuery(" select ps from ParkingSpace ps where ps.block_block_id = :bblockId and ps.space_no = :pparkingSpace");
		q.setParameter("bblockId", blockId).setParameter("pparkingSpace", parkingSpaceId);
		ParkingSpace parkingSpace = null;
		//return list of Parking space objects
		List<ParkingSpace> parkingSpaceList = q.getResultList();
		if (parkingSpaceList != null && !parkingSpaceList.isEmpty())
		{
			parkingSpace= parkingSpaceList.get(0);
		}
		return parkingSpace;
	}


	// Find VISITOR by ID  
	@Override
	public Visitor findVisistorById(int visitorId) 
	{
		return em.find(Visitor.class, visitorId);
	}



	// Remove Visitor from Schedule
	@Override
	public void removeVisitor(int visitorId){
		Visitor visitor = em.find(Visitor.class, visitorId);
		em.remove(visitor);
	}

    // Find Employee Object by visitor ID
	@Override
	public Employee findEmployeeByVisitorId(int visitorId) 
	{
		Query q = em.createQuery("select e from Employee e JOIN e.employeeVisitors ev where ev.visitor_id = :vvisitorId");
		q.setParameter("vvisitorId", visitorId);
		Employee employee = null;
		List<Employee> employeeList = q.getResultList();
		if (employeeList != null && !employeeList.isEmpty())
		{
			employee = employeeList.get(0);
		}
		return employee;
	}

    
	//Delete Parking space from Allocation Table based on the Allocation ID
	public void deleteParkingSpace(int allocationID){ 
		ParkingAllocation pa = em.find(ParkingAllocation.class, allocationID);
		//Employee emp = pa.getEmployee();
		em.remove(pa);
		em.flush();
		//em.refresh(emp);

	}

    //Set the status of the Out of Office to 0 so it becomes available for temp allocation
	public void reverseOutOffice(int outOfficeID) {

		OutOfOffice ooo = em.find(OutOfOffice.class, outOfficeID);
		Employee emp = ooo.getEmployee();
		ooo.setStatus(0);
		em.merge(ooo);
		em.flush();
		em.refresh(emp);


	}

    
	//Update Note in the Employee Detail
	@Override
	public void updateNote(Note note) 
	{
		em.merge(note);
		em.flush();
	}
    
	//Update Contact in the Employee Detail
	@Override
	public void updateContact(Contact contact) 
	{
		em.merge(contact);
		em.flush();
	}
    
	//Update Vehicle details in the Employee Detail
	@Override
	public void updateCar(Car car) 
	{
		em.merge(car);
		em.flush();
	}
    
	//Get the Visitors data - Used for Data Analisys
	private Long retrieveVisitorData(String year) throws ParseException
	{
		String currentDateString = year+"-01-01";
		Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDateString);
		Query query = em.createQuery(" SELECT count(visitor.visitor_id) FROM Visitor visitor " +
		"where visitor.allocation_start_datetime > :ccurrentDate" +
				" order by visitor.allocation_start_datetime desc ");
		query.setParameter("ccurrentDate", currentDate);
		return (Long)query.getSingleResult();
	}

    
	//Get Employee Yearly Data - Used for Data Analisys
	public Map<String, Long> retrieveEmployeeYearlyData(String currentYear) throws ParseException 
	{
		Map<String,Long> employeeMap = new HashMap<String, Long>();
		int currentYearInt = Integer.parseInt(currentYear); 
		for (int index = 0; index < 10; index++)
		{
			employeeMap.put(currentYearInt+"", retrieveYearData(currentYearInt+""));
			currentYearInt--;
		}
		return employeeMap;
	}

	//Get Parking Yearly Data - Used for Data Analisys
	public Map<String, Long> retrieveParkingYearlyData(String currentYear)
			throws ParseException 
	{
		Map<String,Long> parkingMap = new HashMap<String,Long>();
		int currentYearInt = Integer.parseInt(currentYear); 
		for (int index = 0; index < 10; index++)
		{
			parkingMap.put(currentYearInt+"", retrieveParkingData(currentYearInt+""));
			currentYearInt--;
		}
		return parkingMap;
	}
    
	//Get Employee based on the employment date and status - Used for Data Analisys
	private Long retrieveYearData(String year) throws ParseException
	{
		String currentDateString = year+"-01-01";
		Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDateString);
		Query query = em.createQuery(" SELECT count(employee.employee_id) FROM Employee employee " +
				"where employee.application_date >= :ccurrentDate " +
				"and employee.employment_status = 'Yes' and employee.access_type ='employee' " +
				"order by employee.application_date desc"); 
		query.setParameter("ccurrentDate", currentDate);
		return (Long)query.getSingleResult();
	}
    
	//Get Parking based on the employment date and status - Used for Data Analisys
	private Long retrieveParkingData(String year) throws ParseException
	{
		String currentDateString = year+"-01-01";
		Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDateString);
		Query query = em.createQuery(" SELECT count(parkingAllocation.employee.employee_id) FROM ParkingAllocation parkingAllocation " +
				"JOIN parkingAllocation.employee e " +
				"JOIN parkingAllocation.parkingspace ps " +
				"where parkingAllocation.start_date >= :ccurrentDate " +
				" and e.employment_status = 'Yes' and e.access_type ='employee' " +
				" and ps.space_no > 0 order by parkingAllocation.start_date desc "); 
		query.setParameter("ccurrentDate", currentDate);
		return (Long)query.getSingleResult();
	}

	//Get Visitor  yearly data - Used for Data Analisys
	public Map<String, Long> retrieveVisitorYearlyData(String currentYear)
			throws ParseException 
	{
		Map<String,Long> visitorMap = new HashMap<String,Long>();
		int currentYearInt = Integer.parseInt(currentYear); 
		for (int index = 0; index < 10; index++)
		{
			visitorMap.put(currentYearInt+"", retrieveVisitorData(currentYearInt+""));
			currentYearInt--;
		}
		return visitorMap;
	}
	
	// Update Out Of Office
	public void updateOutOfOffice(OutOfOffice outOfOffice)
	{
		em.merge(outOfOffice);
		em.flush();
	}

}