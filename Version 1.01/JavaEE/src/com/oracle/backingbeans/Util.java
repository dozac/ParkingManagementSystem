package com.oracle.backingbeans;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
 
/**
 * It contains the implementation for handling the logged user session
 */
public class Util {
 
	  //Get session object from session ID
      public static HttpSession getSession() {
        return (HttpSession)
        FacesContext.getCurrentInstance().getExternalContext().getSession(false);
      }
       
      //Gets the HTTP servlet request object.
      public static HttpServletRequest getRequest() {
       return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      }
 
  
}
