package com.oracle.backingbeans;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@ManagedBean(name="mapBean")
@SessionScoped
public class MapBean implements Serializable { 
	/**
	 * It contains the Google Map implementation including markers and overlay 
	 */
  
	//Bean Variables
    private MapModel simpleModel; 
    private MapModel advancedModel;
    private Marker marker;  
  
    public MapBean() {  
        simpleModel = new DefaultMapModel(); 
          
        //Shared coordinates of the East Point Business Park 
        LatLng coord1 = new LatLng(53.3574521, -6.2217928);  
        LatLng coord2 = new LatLng(53.3578684, -6.2264598);  
        LatLng coord3 = new LatLng(53.3580476, -6.228391);  
        LatLng coord4 = new LatLng(53.3583358, -6.2279726);
        LatLng coord5 = new LatLng(53.3555845,-6.2224949); 
          
        //Basic marker for the overlay  
        simpleModel.addOverlay(new Marker(coord1, "Oracle Block P5"));  
        simpleModel.addOverlay(new Marker(coord2, "Oracle Block C"));  
        simpleModel.addOverlay(new Marker(coord3, "Oracle Block I"));  
        simpleModel.addOverlay(new Marker(coord4, "Oracle Block H"));
        simpleModel.addOverlay(new Marker(coord5, "OverFlow Car Park"));
        
        //Add Icons and data description  
        simpleModel.addOverlay(new Marker(coord1, "Block P5", "blockp5.jpg"));  
        simpleModel.addOverlay(new Marker(coord2, "Block C", "ataturkparki.png"));  
        simpleModel.addOverlay(new Marker(coord4, "Block I", "blocki.jpg"));  
        simpleModel.addOverlay(new Marker(coord3, "Block H", "karaalioglu.png"));  
    }  
        
         
    //on select marker on the map show the title
    public void onMarkerSelect(OverlaySelectEvent event) {  
        marker = (Marker) event.getOverlay();  
          
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Selected", marker.getTitle()));  
    }
    
    //FacesContext for toasting the msg 
    public void addMessage(FacesMessage message) {  
        FacesContext.getCurrentInstance().addMessage(null, message);  
    } 
    
    //Getters and Setters
    public Marker getMarker() {  
        return marker;  
    }  
    
    public MapModel getSimpleModel() {  
        return simpleModel;  
    }
      
     
}  

