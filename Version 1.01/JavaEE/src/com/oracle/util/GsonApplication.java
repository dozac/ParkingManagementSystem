
package com.oracle.util;

import com.oracle.restful.JsonResourceWriter;
import com.oracle.restful.AuthResource;
import com.oracle.restful.EmployeeResource;
import com.oracle.restful.ParkingSpacesResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Class used to provide a class Application for declaring the root resource and 
 * provider classes used for the RESTful Web service. 
 * The Web service is extending this class to declare root resource and provider classes.
 */

public class GsonApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(JsonResourceWriter.class);
        classes.add(AuthResource.class);
        classes.add(EmployeeResource.class);
        classes.add(ParkingSpacesResource.class);
        return classes;
    }
}
