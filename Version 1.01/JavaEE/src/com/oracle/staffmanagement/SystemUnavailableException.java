package com.oracle.staffmanagement;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class SystemUnavailableException extends Exception {

}
