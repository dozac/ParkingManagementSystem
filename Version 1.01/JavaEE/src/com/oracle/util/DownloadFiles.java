package com.oracle.util;

import java.io.InputStream;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Class used for Download files implementation on the employeeHomePage (Android Tab)
 */
public class DownloadFiles {

		private StreamedContent file;
		//Download implementation for the Android app
		public DownloadFiles() {
			//define download path of the file
	        InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/assets/commuterApp.apk");
			//Stream content
	        file = new DefaultStreamedContent(stream, "android/apk", "commuterApp.apk");
		}
        
		//Getter for the file variable.
	    public StreamedContent getFile() {
	        return file;
	    }  
	}
	
	
	


