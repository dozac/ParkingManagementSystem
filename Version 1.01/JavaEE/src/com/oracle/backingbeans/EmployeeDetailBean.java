package com.oracle.backingbeans;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.primefaces.event.RowEditEvent;

import com.oracle.staffmanagement.EmployeeManagementServiceLocal;
import com.oracle.staffmanagement.SystemUnavailableException;
import com.oracle.staffmanagement.domain.Block;
import com.oracle.staffmanagement.domain.Car;
import com.oracle.staffmanagement.domain.Contact;
import com.oracle.staffmanagement.domain.Employee;
import com.oracle.staffmanagement.domain.Note;
import com.oracle.staffmanagement.domain.OutOfOffice;
import com.oracle.staffmanagement.domain.ParkingSpace;
import com.oracle.staffmanagement.domain.Visitor;
import com.oracle.util.EncryptDecrypt;


@ManagedBean(name="employeeDetailBean")
@javax.faces.bean.SessionScoped
public class EmployeeDetailBean {
	/**
	 * It contain the business Logic for the handling the Employee profile details on Admin Implementation 
	 */
	@EJB
	private EmployeeManagementServiceLocal employeeService;
	private Employee selectedEmployee;
	private OutOfOffice selectedOooEmployee;
	private UIData dataTable;
	private List<Employee> filteredEmployee;
	private EncryptDecrypt encryptDecrypt;

	public String visitorName;	
	public Date startDate;
	public Date endDate;
	public int parkingSpaceId;

	public List<SelectItem> listBlock;
	public List<SelectItem> listParkingSpace;
    private List<Visitor> employeeVisitors;
	private List<Note> notesList;

	public int blockId;
	public ParkingSpace selectedParkingSpace;
	public String selectedParkingSpaceId ="";
	public String selectedBlockId="";
	public Block selectedBlock;
	public String note;
	final String adminEmail  = "admin@commutermanager.info";
	final String username = "admin";
	final String password = "xxxxxxx";
	    
	//Constructor 
	public EmployeeDetailBean() {
		//instantiate and create the employee profile
		this.selectedEmployee = new Employee();
		this.selectedBlockId = "";
		this.listBlock = new ArrayList<SelectItem>();
		this.listParkingSpace = new ArrayList<SelectItem>();	
	    this.selectedBlock = new Block();
	    setEncryptDecrypt(new EncryptDecrypt());
	}
		
	//Get the employee object and add them to an Array List (used for Jasper Reports)
	private List<Employee> listofIndividual; 
	public List<Employee> getIndividualList(){
    	listofIndividual = new ArrayList<Employee>();
    	listofIndividual.add(selectedEmployee);
    	return listofIndividual;
    }
	
	//path of the report jasper file. The report engine get the images from the specified path.
	JasperPrint jasperPrint;
	public void init() throws JRException{
		//get the employee Object
		listofIndividual = getIndividualList();
		System.out.println("=======LIST COunt============="  + listofIndividual.size());
		//pass the employee Object to the JasperReport DataSource
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listofIndividual);
		//provide the Jasper report template path
		String  reportPath=  FacesContext.getCurrentInstance().getExternalContext().getRealPath("/css/images/report.jasper");        
		System.out.println("==========PATHH+++++++++++++++++++ " + reportPath);
		// bind the Employee details to the report
		Map params =  new HashMap();
		params.put("REPORTS_DIR", "/");
		jasperPrint=JasperFillManager.fillReport(reportPath, params,beanCollectionDataSource); 
		
	}
	
	// When Pdf button is clicked Send the PDF file to client via stream.
	public void pdfCreate(ActionEvent actionEvent)  throws JRException, IOException{
		init();
		HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		//set the name of the PDF file 
		httpServletResponse.addHeader("Content-disposition", "attachment; filename=report.pdf");  
		ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream(); 
		//Call the Jasper Export Manager class in order to export the PDF document 
		JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
		FacesContext.getCurrentInstance().responseComplete();  
	}
	
    // Reset Password implementation
	public void confirmResetPassword(ActionEvent actionEvent) throws NoSuchAlgorithmException, InvalidKeySpecException, SystemUnavailableException
	{   
		// get the new generated password
		String resetPassword = EnterEmployeePageBackingBean.passwordGenerator();
		//create the employee object holding an instance of the selected employee
		Employee updatedEmployee = this.selectedEmployee;
		//look for the email address of the employee
		String employeeEmail  = updatedEmployee.getContactDetails().get(0).getEmail();
		//email the new password to the employee using the the autotmatic email method 
		resetPasswordEmail(resetPassword, employeeEmail); 
		//encrypt the new password
		EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
		String encryptedPassword = encryptDecrypt.encrypt(resetPassword);
		updatedEmployee.setPassword(encryptedPassword);
		//update the employee object with the new password
		employeeService.UpdateEmployee(updatedEmployee);
		//display the msg to the user that password reset was successfully. 
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, updatedEmployee.getFirstname(),"Password reset successfully.");
        FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	
	//Revoke Confirmation email for Temp allocation approval
	private void resetPasswordEmail(String restPassword, String employeeEmail){		
					    //Set the Server propetries
						Properties props = new Properties();
						props.put("mail.smtp.auth", "true");
						props.put("mail.smtp.starttls.enable", "false");
						props.put("mail.smtp.host", "localhost");
						props.put("mail.smtp.port", "25");
				        //pass the user name and password of the email sender into the session
						Session session = Session.getInstance(props,
						  new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username, password);
							}
						  });
				 
						try {
				            //Body of the email message passing the arguments
							Message message = new MimeMessage(session);
							message.setFrom(new InternetAddress("admin@commutermanager.info"));
							message.setRecipients(Message.RecipientType.TO,
									//pass the email address of receiver (i.e employee email)	
									InternetAddress.parse(employeeEmail));
							message.setRecipients(Message.RecipientType.CC,
									InternetAddress.parse(adminEmail));
							message.setSubject("Confirmation of Password Reset");
							message.setText("Please be informed that your new password is: "+ restPassword
									+ "\n\n================================================================================="
									+ "\n This is an automatically generated email. Please do not reply to this message.");
				            // send message to the receiver
							Transport.send(message);
							
						} catch (MessagingException e) {
							throw new RuntimeException(e);
						}
						
				  }
	
	
	
	//Get all employees
	public List<Employee> getAllEmployees()
	{        
		return employeeService.getAllEmployees();
	}

	//Get Employee Details
	public Employee getEmployeeDetails(int employeeId)
	{
		return employeeService.getEmployeeDetails(employeeId);
	}

	//Display individual employee details        
	public String showEmployee(){
		this.selectedEmployee  = (Employee)dataTable.getRowData();
		Employee employee = getEmployeeDetails(this.selectedEmployee.getEmployee_id());
		selectedEmployee = employee;
		return "comEmployeeDetail"; 
	}

	// Add Visitor to the Employee Object
   public void addVisitor(ActionEvent event)
	{
		try 
		{	//validation for the start and end date for visitor booking	
			if(endDate.before(startDate)){
				FacesMessage msg = new FacesMessage("End Date cannot be before Start Date.");  
				FacesContext.getCurrentInstance().addMessage(null, msg);  
				return;
			}
			//get the employee ID
			this.selectedEmployee = this.employeeService.getEmployeeDetails(this.selectedEmployee.getEmployee_id());
			//get the selected parking space details
			int psId = Integer.parseInt(this.selectedParkingSpaceId);
			ParkingSpace parkingSpace = this.employeeService.findParkingSpaceById(psId);
            //pass the Visitor Object to the Employee Object 
			this.selectedEmployee.addVisitor(this.visitorName, this.startDate, this.endDate, parkingSpace);
			//update the employee Object
			this.employeeService.UpdateEmployee(this.selectedEmployee);
			//reset the add visitor UI dialog values
			resetDialogAddVisitor();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	//Add New Note to Employee Object
	public String addNewNote(ActionEvent event){

		try {
			// get the employee ID
			selectedEmployee = employeeService.getEmployeeDetails( this.selectedEmployee.getEmployee_id() );
			//add note to the employee object
			selectedEmployee.addNote(note);
			//update the employee object 
			employeeService.UpdateEmployee(selectedEmployee);
            //reset the note dialog UI
			resetDialogAddNote();
            // return the comAllEmployee.xhtml page
			return "comAllEmployee";
		} catch (Throwable e) 
		{	
			e.printStackTrace();
			return "comSystemDown";
		}	
	}
	

	//Reset the AddVisitor dialog fields
	private void resetDialogAddVisitor() {
		visitorName = null;
		startDate = null;
		endDate = null;
	}

	//Reset the AddNote dialog field
	private void resetDialogAddNote() {
		note = null;

	}
	
	
    //Inline editing on Employee Profile for Visitor
	public String onEdit(RowEditEvent event) {
		//new instance of the visitor
		Visitor visitor = (Visitor)event.getObject();
		//update the visitor object 
		employeeService.updateVisitor(visitor);
		return "comEmployeeDetail";
	}
	// on cancel editing visitor, no event
	public void onCancel(RowEditEvent event) {  
		 
	}
	
	//Inline editing on Employee Profile for Note
		public String onEditNote(RowEditEvent event){
			//new instance of the note
			Note note = (Note)event.getObject();
			//update the Note Object
			employeeService.updateNote(note);
			return "comEmployeeDetail";	
		}
	//on cancel editing note, no event	
	public void onCancelNote(RowEditEvent event)
	{
		
	}
	
    //Inline editing on Employee Profile for Contact
	public String onEditContact(RowEditEvent event)
	{   //new instance of the contact details
		Contact contact = (Contact)event.getObject();
		//update the Contact object
		employeeService.updateContact(contact);
		return "comEmployeeDetail";
	}
	//on cancel editing Contact, no event	
	public void onCancelContact(RowEditEvent event)
	{
		
	}
	
    //Inline editing on Employee Profile for Vehicle
	public String onEditVehicle(RowEditEvent event)
	{   //new instance of the car details
		Car car = (Car)event.getObject();
		//update the vehicle object
		employeeService.updateCar(car);
		return "comEmployeeDetail";
	}
	//on cancel editing Vehicle, no event	
	public void onCancelVehicle(RowEditEvent event)
	{
		
	}
	
	//Inline editing on Employee Profile for Out Off Office
	public String onEditOutOfOffice(RowEditEvent event)
	{   //new instance of the Out of Office details
		OutOfOffice outOfOffice = (OutOfOffice)event.getObject();
		//update the Out of Office object
		employeeService.updateOutOfOffice(outOfOffice);
		return "comEmployeeDetail";
		
	}
	//on cancel editing OutOfOffice, no event
	public void onCancelOutOfOffice(RowEditEvent event)
	{
		
	}
	
	
	/**
	 * Handle Block change on Add Visitor form 
	 *
	 */
	public void handleBlockChange(AjaxBehaviorEvent abe)
	{
		//when a specific block is slected
		if ((selectedBlockId != null) && (selectedBlockId.length()>0))
		{
			listParkingSpace.clear();
			//loop through the visitor parking spaces only and get the available ones 
			for(ParkingSpace ps : employeeService.getParkingSpacesByBlock(selectedBlockId) ) {
				listParkingSpace.add(new SelectItem(ps.getPspace_id()+"", ps.getSpace_no()+""));
			}
		}		
	}
	
	//Get all the Parking Blocks
	public List<SelectItem> getListBlock()
	{   
		try
		{	listBlock.clear();
		//loop through the Block and return the Block list
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
	
	
	//GETTERS AND SETTERS
	public void setSelectedBlockId(String selectedBlockId) {
		this.selectedBlockId = selectedBlockId;
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

	public String getVisitorName() {
		return visitorName;
	}
	public void setVisitorName(String visitorName) {
		this.visitorName = visitorName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
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

	public int getBlockId()
	{
		return this.blockId;
	}

	public void setBlockId(int blockId)
	{
		this.blockId = blockId;
	}

	public int getParkingSpaceId() {
		return parkingSpaceId;
	}

	public void setParkingSpaceId(int parkingSpaceId) {
		this.parkingSpaceId = parkingSpaceId;
	}
	
	
	public List<Visitor> getEmployeeVisitors() {
		return employeeVisitors;
	}
	public List<Note> getNotesList() {
		return notesList;
	}

	public void setNotesList(List<Note> notesList) {
		this.notesList = notesList;
	}

	public void setEmployeeVisitors(List<Visitor> employeeVisitors) {
		this.employeeVisitors = employeeVisitors;
	}

	public OutOfOffice getSelectedOooEmployee() {
		return selectedOooEmployee;
	}

	public void setSelectedOooEmployee(OutOfOffice selectedOooEmployee) {
		this.selectedOooEmployee = selectedOooEmployee;
	}

	public EncryptDecrypt getEncryptDecrypt() {
		return encryptDecrypt;
	}

	public void setEncryptDecrypt(EncryptDecrypt encryptDecrypt) {
		this.encryptDecrypt = encryptDecrypt;
	}

	public List<Employee> getFilteredEmployee() {
		return filteredEmployee;
	}

	public void setFilteredEmployee(List<Employee> filteredEmployee) {
		this.filteredEmployee = filteredEmployee;
	}

    
}
