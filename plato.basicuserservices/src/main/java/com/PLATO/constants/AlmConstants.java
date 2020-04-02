package com.PLATO.constants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
* FAST-Utilities: Constants class defines all constants used in FAST-Utilities.
* @author Deepika
* @version 1.0
*/
public class AlmConstants {
	//Separators
	public static final String DASH_SEPARATOR = "-";
	public static final String BLANK = "";
	public static final String BACK_SLASH_SEPARATOR = "/";
	public static final String RUN_DEFAULT_NAME = "FAST Automation";
	public static final String TAB_SEPARATOR = "\t";
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String ENCODE_LINE_SEPARATOR = "\r\n";
	public static final String PIPE_SEPARATOR = "|";
	public static final String DOUBLE_QUOTE_SEPARATOR = "\"";
	public static final String SPACE = " ";
	
	//FAST-Status. Also managed in Constants class of FAST-Java-Common.
	public static final String PASS_STATUS = "Pass";
	public static final String FAIL_STATUS = "Fail";
	public static final String NOT_COMPLETED_STATUS = "Not Completed";
	
	//Timestamp formats
	public static final DateFormat LICENSE_TIMESTAMP_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	
	//Used in generation of XML reports
	public static final String YES_SMALL = "yes";
}
