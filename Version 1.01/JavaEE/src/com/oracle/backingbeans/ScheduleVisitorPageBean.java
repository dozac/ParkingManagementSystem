package com.oracle.backingbeans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.SystemUnavailableException;
import com.oracle.staffmanagement.domain.Block;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.staffmanagement.domain.ParkingSpace;
import com.oracle.staffmanagement.domain.Visitor;



@ManagedBean(name="schedulePageBean")
//@SessionScoped
@ViewScoped
public class ScheduleVisitorPageBean implements Serializable
{	
	/**
	 * It provide the business logic for Calendar Visitor Schedule
	 */
	
	private static final long serialVersionUID = 1L;

	@EJB(mappedName="EmployeeManagementImplementation")
	private EmployeeManagementServiceLocal employeeService; 
	
	private ScheduleModel eventModel; 
	private ScheduleEvent event = new DefaultScheduleEvent();
	private Integer selectedBlockId;
	private Integer selectedParkingSpaceId;
	private Employee selectedEmployee;
	private List<Block> blocks;
	private List<ParkingSpace> parkingSpaces;
	private List<Employee> employees;
	private Visitor selectedVisitor;
	private int hostEmployee;
	private String visitorName;

	//Constructor
	public ScheduleVisitorPageBean()
	{   
		//Initialise block id to zero
		selectedBlockId = 0;
		//Initialise the selected parking space to zero
		selectedParkingSpaceId = 0;
		//instantiate an Array of parking spaces objects
		parkingSpaces = new ArrayList<ParkingSpace>();
		//instantiate an array of employees
		employees= new ArrayList<Employee>();
		//instantiate a new visitor
		selectedVisitor = new Visitor();
	}
	
	//when EJB is instantiated update the calendar Schedule with the visitor objects
	@PostConstruct
	public void initiaze()
	{
		this.updateSchedule();
		
	}
	
	
	//Add new Visitor
	public void addEvent(ActionEvent actionEvent)
	{   //Instantiate a new visitor object, get and set the visitor details
		Visitor visitor = new Visitor();
		visitor.setAllocation_start_datetime(event.getStartDate());
		visitor.setAllocation_end_datetime(event.getEndDate());
		this.visitorName = selectedVisitor.getVisitor_name();
		visitor.setVisitor_name(visitorName.trim());
		//if the event is null add the new visitor to the calendar
		if(event.getId() == null)
		{
			eventModel.addEvent(event);			
			employeeService.addVisitor(visitor);
		}
		else
		{   //if the event is not null just update the visitor details
			Employee hostEmployee = this.selectedEmployee;
			ParkingSpace parkingSpace = employeeService.getParkingSpaceByBlockAndId(new Integer(this.selectedBlockId),new Integer(this.selectedParkingSpaceId));
			//remove the visitor old values
			employeeService.removeVisitor(new Integer(event.getData()+"").intValue());
			//add the visitor new values
			hostEmployee.addVisitor(visitor.getVisitor_name(), visitor.getAllocation_start_datetime(), visitor.getAllocation_end_datetime(), parkingSpace);
			//refresh and update the caldendar scheduler
			eventModel.updateEvent(event);
			try {
				//update the employee object
				employeeService.UpdateEmployee(hostEmployee);
				this.initiaze();
			} catch (SystemUnavailableException e) 
			{
				e.printStackTrace();
			}
		}
        //rest the event to default
		event = new DefaultScheduleEvent();
	}

	//delete Visitor
	public void deleteEvent(ActionEvent actionEvent)
	{
		employeeService.removeVisitor(new Integer(event.getData()+"").intValue());
		this.updateSchedule();
	}
	
	//Update Scheduler with the Visitor Objects
	private void updateSchedule()
	{   //instantiate new event 
		eventModel = new DefaultScheduleModel();
		//get a list of visitors and loop through
		List<Visitor> visitorList = employeeService.getVisitors();
		for (Visitor vs : visitorList)
		{	//populate the scheduler with the visitors objects		
			DefaultScheduleEvent defaultScheduleEvent = new DefaultScheduleEvent(vs.getVisitor_name()+","+vs.getParkingSpace().getBlock().getBlock_description()+","+vs.getParkingSpace().getSpace_no(),vs.getAllocation_start_datetime(), vs.getAllocation_end_datetime());
			defaultScheduleEvent.setData(vs.getVisitor_id());
			eventModel.addEvent(defaultScheduleEvent); 
		}
		//load the parking blocks
		blocks = employeeService.getAllBlocks();
		//load the employees 
		employees = employeeService.getAllEmployees();
	}
	
	//on UI click visitor populate the dialog with the visitor details
	public void onEventSelect(SelectEvent selectEvent) 
	{
		event = (ScheduleEvent) selectEvent.getObject();
		selectedVisitor = employeeService.getVisitorById(new Integer(event.getData().toString()).intValue());
		selectedEmployee = employeeService.getEmployeeByVisitorId(selectedVisitor.getVisitor_id());
		selectedBlockId = selectedVisitor.getParkingSpace().getBlock_block_id();
		selectedParkingSpaceId = selectedVisitor.getParkingSpace().getSpace_no();
		parkingSpaces.add(selectedVisitor.getParkingSpace());
	}
	
	//if any date is clicked load the dialog with the last visitor object
	public void onDateSelect(SelectEvent selectEvent)
	{
		event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
	}
	
	//Handle the scheduler changes when objects are dynamically moved on the calendar
	public void onEventMove(ScheduleEntryMoveEvent event) 
	{
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
		addMessage(message);
	}
	//Handle the scheduler changes when the dialog is expanded or resized on the scheduler calendar
	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());	
		addMessage(message);
	}
	
	// used to call the Primefaces msg current instance 
	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	//handle the Block change on edit the visitor details and change his parking space
	public void handleBlockChange()
	{
		if ((selectedBlockId!= null) && (selectedBlockId.intValue()> 0))
		{   //get all the parking spaces blocks
			parkingSpaces.clear();
			parkingSpaces.addAll(employeeService.getParkingSpacesByBlock(selectedBlockId.toString()));
		}
	}
	
	
	// Getters and Setters
	public ScheduleModel getEventModel() {
		return eventModel;
	}
	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}
	public ScheduleEvent getEvent() {
		return event;
	}
	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}
	public EmployeeManagementServiceLocal getEmployeeService() {
		return employeeService;
	}
	public void setEmployeeService(EmployeeManagementServiceLocal employeeService) {
		this.employeeService = employeeService;
	}
	
	public Integer getSelectedBlockId() {
		return selectedBlockId;
	}
	public void setSelectedBlockId(Integer selectedBlockId) {
		this.selectedBlockId = selectedBlockId;
	}
	public void setSelectedParkingSpaceId(Integer selectedParkingSpaceId) {
		this.selectedParkingSpaceId = selectedParkingSpaceId;
	}
	public int getSelectedParkingSpaceId() {
		return selectedParkingSpaceId;
	}
	public void setSelectedParkingSpaceId(int selectedParkingSpaceId) {
		this.selectedParkingSpaceId = selectedParkingSpaceId;
	}
	public List<Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
	public List<ParkingSpace> getParkingSpaces() {
		return parkingSpaces;
	}
	public void setParkingSpaces(List<ParkingSpace> parkingSpaces) {
		this.parkingSpaces = parkingSpaces;
	}
	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}
	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	public int getHostEmployee() {
		return hostEmployee;
	}
	public void setHostEmployee(int hostEmployee) {
		this.hostEmployee = hostEmployee;
	}
	public String getVisitorName() {
		return visitorName;
	}
	public void setVisitorName(String visitorName) {
		this.visitorName = visitorName;
	}
	public Visitor getSelectedVisitor() {
		return selectedVisitor;
	}
	public void setSelectedVisitor(Visitor selectedVisitor) {
		this.selectedVisitor = selectedVisitor;
	}
	
}
