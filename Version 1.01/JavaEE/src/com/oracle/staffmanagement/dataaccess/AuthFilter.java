package com.oracle.staffmanagement.dataaccess;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthFilter implements Filter {
	/**
	 * User Authentication ilter implementation
	 */
	
	//Constructor
    public AuthFilter() {
    }
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
         
    }
    //filter implementation for the user login 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
         try {
 
            // check if the session variable is set
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            HttpSession ses = req.getSession(false);
            //  permission for user to access if url is login.jsf or passwordRecovery.jsf 
            //  If user is authenticated he can then access any page in /public folder
            String reqURI = req.getRequestURI();
              if ( reqURI.indexOf("/index.jsf")  >= 0 || reqURI.indexOf("/passwordRecovery.jsf")  >= 0 || (ses != null && ses.getAttribute("username") != null)
                                       || reqURI.indexOf("/public/") >= 0 || reqURI.contains("javax.faces.resource") )
                   chain.doFilter(request, response);
            else   // If the user is not authenticated but is trying to access a page which requires login return the index page
                   res.sendRedirect(req.getContextPath() + "/index.jsf");  //Redirect user to login page
      }
     catch(Throwable t) {
         System.out.println( t.getMessage());
     }
    } //doFilter
 
    @Override
    public void destroy() {
         
    }
}
