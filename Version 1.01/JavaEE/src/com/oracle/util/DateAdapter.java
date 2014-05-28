
package com.oracle.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XMLAdapter Class used to format the date. Used for the Web Service call
 *
 */

public class DateAdapter extends XmlAdapter<String, Date> {
    // format the date 
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd");

    @Override
    public String marshal(Date v) {
        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(String v) {
        try {
            return dateFormat.parse(v);
        } catch (ParseException e) {
            throw new WebApplicationException();
        }
    }
}
