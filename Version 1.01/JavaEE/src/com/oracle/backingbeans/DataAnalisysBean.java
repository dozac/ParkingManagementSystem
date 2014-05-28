package com.oracle.backingbeans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;

@ManagedBean(name="dataAnalisysBean")
@SessionScoped
public class DataAnalisysBean implements Serializable{  	  
	     
    /**
	 * It provide the business logic for analysing data based on the existing records from the Database 
	 */
	private static final long serialVersionUID = 1L;

	@EJB(mappedName="EmployeeManagementImplementation")
	private EmployeeManagementServiceLocal employeeService;
	private CartesianChartModel categoryModel;
	private CartesianChartModel linearModel;
    
	//constructor
	public DataAnalisysBean() {  
	}  
    
	//when EJB is instantiated call the Category and Linear methods for Chart creation
	@PostConstruct
	public void initialize() throws ParseException
	{
		this.createCategoryModel();
		this.createLinearModel();
	}

	//Create model for the Parking Allocation chart to analyse the supply and demand
	private void createCategoryModel() throws ParseException {
		//instantiate the category model
		categoryModel = new CartesianChartModel();  
        //crate an instance of employees required by the chart
		ChartSeries employees = new ChartSeries();  
		employees.setLabel("Employees");  
        //instantiate the calendar
		Calendar cal = new GregorianCalendar();
		//get the time and years
		Date creationDate = cal.getTime();
		//date format so it can only display the years 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
		String currentYear = simpleDateFormat.format(creationDate);
		//load the number of employee in Hashmap based on employee start date
		Map<String, Long> employeeMap = employeeService.getEmployeeYearlyData(currentYear);
		for (Map.Entry<String, Long> entry: employeeMap.entrySet()) 
		{
			employees.set(entry.getKey(), entry.getValue());
		}
		//create an instance of Parking spaces required by the chart
		ChartSeries pspaces = new ChartSeries();  
		pspaces.setLabel("Parking Spaces");  
		//load the number of total parking spaces in Hashmap 
		Map<String, Long> parkingMap = employeeService.getParkingYearlyData(currentYear);
		for(Map.Entry<String, Long> entry: parkingMap.entrySet())
		{
			pspaces.set(entry.getKey(), entry.getValue());
		}
        //add results to the category Object 
		categoryModel.addSeries(employees);  
		categoryModel.addSeries(pspaces);  
	}
    
	
	private void createLinearModel() throws ParseException 
	{   //instantiate the category model
		linearModel = new CartesianChartModel();  
		 //crate an instance of employees required by the chart
		LineChartSeries employeeSeries = new LineChartSeries();  
		employeeSeries.setLabel("employeeSeries");
		//instantiate the calendar
		Calendar cal = new GregorianCalendar();
		//get the time and years
		Date creationDate = cal.getTime();
		//date format so it can only display the years 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
		String currentYear = simpleDateFormat.format(creationDate);
		//load the number of employee in Hashmap based on employee start date 
		Map<String, Long> employeeMap = employeeService.getEmployeeYearlyData(currentYear);
		for(Map.Entry<String, Long> entry: employeeMap.entrySet())
		{
			employeeSeries.set(entry.getKey(), entry.getValue());
		} 
		
		//crate an instance of Visitors required by the chart
		LineChartSeries visitorSeries = new LineChartSeries();  
		visitorSeries.setLabel("visitorSeries");  
		visitorSeries.setMarkerStyle("diamond");  
		//load the number of total visitors per year in a Hashmap 
		Map<String, Long> visitorMap = employeeService.getVisitorYearlyData(currentYear);
		for(Map.Entry<String, Long> entry: visitorMap.entrySet())
		{
			visitorSeries.set(entry.getKey(), entry.getValue());
		}
		//add results to the category Object 
		linearModel.addSeries(employeeSeries);  
		linearModel.addSeries(visitorSeries);  
	}  

	
	//GETTERS AND SETTERS
	public EmployeeManagementServiceLocal getEmployeeService() {
		return employeeService;
	}

	public void setEmployeeService(EmployeeManagementServiceLocal employeeService) {
		this.employeeService = employeeService;
	}

	public CartesianChartModel getLinearModel() {  
		return linearModel;  
	}  

	public CartesianChartModel getCategoryModel() {  
		return categoryModel;  
	}  
	    
	}  
