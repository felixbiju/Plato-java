package com.lntinfotech.tcoe.excelImport;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * FAST-Java-Common: ExcelUtils is the singleton class that defines method for interacting with Excel files.
 * @author Deepika
 * @version 1.0
 */
public enum ExcelUtils {

	INSTANCE;

	private static Logger log = Logger.getLogger(ExcelUtils.class);
	/**
	 * Method to copy a directory.
	 * @param src Source directory with complete path
	 * @param dest Destination directory with complete path
	 * @throws IOException
	 */ 
	public void copyDirectory(String src, String dest) throws IOException{
		File sourceDirectory = new File(src);
		File targetDirectory = new File(dest);
		FileUtils.copyDirectory(sourceDirectory, targetDirectory);
		log.debug("Copied directory from \"" + src + "\" to \"" + dest + "\"");
	}

	/**
	 * Method to copy a file.
	 * @param src Source file with complete path
	 * @param dest Destination file with complete path
	 * @throws IOException
	 */ 
	public void copyFile(String src, String dest) throws IOException{
		File sourceDirectory = new File(src);
		File targetDirectory = new File(dest);
		FileUtils.copyFile(sourceDirectory, targetDirectory);
		log.debug("Copied file from \"" + src + "\" to \"" + dest + "\"");
	}

	/**
	 * Method to create MostRecent folder structure.
	 */ 

	public void deleteDirectory(String directoryName) throws IOException{
		File directory = new File(directoryName);
		log.debug("Deleting directory: " + directoryName);
		FileUtils.deleteDirectory(directory);
	}

	/**
	 * Method to write contents to a file.
	 * @param fileName Name of file with complete path
	 * @param fileContents Contents to be written
	 * @param append Boolean value indicating whether to append to a file or not
	 */ 
	public void writeFile(String fileName, String fileContents, boolean append) throws Exception{
		FileOutputStream out = null;
		try{
			byte dataToWrite[] = fileContents.getBytes();
			log.trace("Writing file Output Stream initialized");
			out = new FileOutputStream(fileName, append);
			out.write(dataToWrite);
			log.debug("File written!!");
		}
		finally{
			out.close();
			log.trace("File Output Stream closed");
		}
	}

	/**
	 * Method to read a file into a String.
	 * @param fileName Name of file with complete path
	 * @return fileContents String contents in file
	 */ 
	public String readFile(String fileName) throws Exception{
		File file = new File(fileName);
		String fileContents = FileUtils.readFileToString(file);
		return fileContents;
	}

	/**
	 * Method to close any closeable resource
	 * @param resource Closable resource object
	 */ 
	public void closeResource(Closeable resource) throws Exception{
		if(resource != null)
			resource.close();
	}


	/**
	 * Method to open an excel file and get its Workbook object
	 * @param fileName Name of the excel file
	 * @return workbook Workbook object of the excel file
	 * @throws Exception
	 */
	public Workbook readWorkbook(String fileName) throws Exception{
		FileInputStream inStream = null;
		Workbook workbook = null;

		try{
			log.debug("Reading Excel file: " + fileName);
			File file = new File(fileName);
			log.trace("Excel file Input Stream initialized");
			inStream = new FileInputStream(file);

			//Create Workbook instance holding reference to .xlsx file
			workbook = WorkbookFactory.create(inStream);
		}
		finally{
			closeResource(inStream);
			log.trace("Excel file Input Stream closed!!");
		}

		return workbook;
	}

	/**
	 * Method to create a new Excel file
	 * @param fileName Name of file with complete path
	 * @return Workbook created
	 * @throws Exception
	 */

	public Workbook createWorkbook(String fileName) throws Exception{
		//Create the workbook instance for file
		Workbook workbook = null;
		InputStream infile = new FileInputStream(fileName);


		if(fileName.endsWith(".xls")){
			workbook = new HSSFWorkbook(infile);
		}
		else if(fileName.endsWith(".xlsx")){
			workbook = new XSSFWorkbook(infile);
		}
		else {
			closeResource(infile);
			throw new Exception("Invalid file extension for file: " + fileName + ". Valid extensions are xls/xlsx.");
		}
		closeResource(infile);
		log.trace("Excel file Input Stream closed!!");
		return workbook;
	}

	/**
	 * Method to append columns after the specified column in excel in a given row
	 * @param workbook Excel workbook object
	 * @param sheetId Sheet id in the workbook(sheet name/sheet number)
	 * @param columnValue Array of objects to be inserted
	 * @param rowNo Row number that is to be modified
	 * @param colNo Column number to start insertion from
	 * @param style CellStyle object to be applied to the cells
	 * @return workbook Updated workbook object
	 * @throws Exception
	 */
	public Workbook addHorizontalColumnsInExcel(Workbook workbook, Object sheetId, Object[] columnValue, int rowNo, int colNo, CellStyle style) throws Exception{	
		Sheet sheet = initializeSheet(workbook, sheetId);		
		addHorizontalColumnsInExcel(sheet, columnValue, rowNo, colNo, style);        
		return workbook;
	}

	/**
	 * Added by Adarsh
	 * Method to read Excel file to 2-dimensional array object
	 * @param workbook Workbook instance of Excel file
	 * @param sheetName Sheet name/number to read
	 * @return Data of excel sheet 2-dimensional array
	 * @throws Exception
	 */
	public String[][] readExcelToArray(Workbook workbook, Object sheetName) throws Exception{
		Sheet sheet = initializeSheet(workbook, sheetName);
		int iNoOfRows = sheet.getLastRowNum()+1;
		int iNoOfCols = sheet.getRow(0).getLastCellNum();
		Row row=null;
		Cell cell =null;

		String[][] arrTempRet = new String[iNoOfRows][iNoOfCols];

		for (int iRowCntr=0; iRowCntr<iNoOfRows; iRowCntr++){
			for (int iColCntr=0; iColCntr<iNoOfCols; iColCntr++){				
				row = sheet.getRow(iRowCntr);
				if(row!=null)
				{	
					cell = row.getCell(iColCntr);
					String cellContents = readCellValue(cell);
					
					if(cellContents != null){
						arrTempRet[iRowCntr][iColCntr] = cellContents;
					}
					//else if(cellContents.contains("%"))
					else{
						arrTempRet[iRowCntr][iColCntr] = "";
					}
				}
			}
		}
		return arrTempRet;
	}
	
	public ArrayList<String> readExcelToArrayOnlyDate(Workbook workbook, Object sheetName) throws Exception{
		Sheet sheet = initializeSheet(workbook, sheetName);
		int iNoOfCols = sheet.getRow(0).getLastCellNum();
		Row row=null;
		Cell cell =null;
		Cell cell2 =null;

		  ArrayList<String> a=new ArrayList<String>();
        
	
			for (int iColCntr=0; iColCntr<iNoOfCols; iColCntr++){				
				row = sheet.getRow(2);
				if(row!=null)
				{	
					//cell = row.getCell(iColCntr);
					
					try{
						 row = sheet.getRow(0);
						 cell2 = row.getCell(iColCntr);
						if(readCellValue(cell2).toLowerCase().contains("date"))
						{
					   
						a.add(readCellValue(cell2));
						System.out.println(">>>>>>>>>"+readCellValue(cell2));
						}
					}
					catch(Exception  e)
					{
						System.out.println("error");
					}
					/*if(DateUtil.isCellDateFormatted(cell))
					{
						row = sheet.getRow(0);
						cell2 = row.getCell(iColCntr);
						a.add(readCellValue(cell2));
						System.out.println(">>>>>>>>>"+readCellValue(cell2));
					}*/
					
					
					
				}
			
		}
		return a;
	}
	
	
	public int readNoOfColumns(Workbook workbook, Object sheetName) throws Exception{
		Sheet sheet = initializeSheet(workbook, sheetName);
		int iNoOfRows = sheet.getLastRowNum()+1;
		int iNoOfCols = sheet.getRow(0).getLastCellNum();
		
		
		return iNoOfCols;
	}
	
	public String[][] writeArrayTo(Workbook workbook, String sheetName) throws Exception{
		Sheet sheet = initializeSheet(workbook, sheetName);
		int iNoOfRows = sheet.getLastRowNum()+1;
		int iNoOfCols = sheet.getRow(0).getLastCellNum();
		Row row=null;
		Cell cell =null;

		String[][] arrTempRet = new String[iNoOfRows][iNoOfCols];

		for (int iRowCntr=0; iRowCntr<iNoOfRows; iRowCntr++){
			for (int iColCntr=0; iColCntr<iNoOfCols; iColCntr++){				
				row = sheet.getRow(iRowCntr);
				if(row!=null)
				{	
					cell = row.getCell(iColCntr);
					String cellContents = readCellValue(cell);
					if(cellContents != null){
						arrTempRet[iRowCntr][iColCntr] = cellContents;
					}
					else{
						arrTempRet[iRowCntr][iColCntr] = "";
					}
				}
			}
		}
		return arrTempRet;
	}

	/**
	 * Method to set Header Style for Excel file
	 * @param sampleWorkBook Workbook instance of Excel file
	 * @param fontStyle String font Style of header
	 * @param colorIndex Short color index
	 * @param boldWeight Bold weight value
	 * @return CellStyle object created
	 * @throws Exception
	 */
	public CellStyle setHeaderStyle(Workbook sampleWorkBook,String fontStyle,short colorIndex,short boldWeight,short borderBottom) throws Exception{
		Font font = sampleWorkBook.createFont();
		font.setFontName(fontStyle);
		font.setColor(colorIndex);
		font.setBoldweight(boldWeight);
		CellStyle cellStyle = sampleWorkBook.createCellStyle();
		cellStyle.setBorderBottom(borderBottom);
		cellStyle.setBorderRight(borderBottom);
		cellStyle.setWrapText(true);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * Method to add Horizontal Column in Excel file
	 * @param sheet Sheet object
	 * @param columnValue Object array of values to add in column
	 * @param rowNo Row number
	 * @param colNo Starting column number
	 * @param style CellStyle object
	 * @return Sheet object Modified
	 * @throws Exception
	 */
	public Sheet addHorizontalColumnsInExcel(Sheet sheet, Object[] columnValue, int rowNo, int colNo, CellStyle style) throws Exception{
		//Get the row from sheet
		Row row = sheet.getRow(rowNo);

		if(row == null){
			row = sheet.createRow(rowNo);
		}

		for (Object column : columnValue) {
			if(style!= null)
			{
				
				try
				{
					style.setFillPattern(CellStyle.SOLID_FOREGROUND);
					/*if(column.toString().equalsIgnoreCase(Constants.PASS))
    					style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
    				else if(column.toString().equalsIgnoreCase(Constants.FAIL))
    					style.setFillForegroundColor(IndexedColors.RED.getIndex());
    				else
    					style.setFillForegroundColor(IndexedColors.WHITE.getIndex());*/
				}

				catch(Exception e)
				{
					style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
					 
				}
			}
			writeExcelCellValue(row, colNo++, column, style);
		}

		return sheet;
	}

	/**
	 * Method to add Vertical Column in Excel file
	 * @param workbook Workbook instance of Excel file
	 * @param sheetId Sheet ID(Name/Number)
	 * @param columnValue Object array of values to add in column
	 * @param rowNo Row number
	 * @param colNo Starting column number
	 * @param style CellStyle object
	 * @return Workbook modified
	 * @throws Exception
	 */
	public Workbook addVerticalColumnInExcel(Workbook workbook, Object sheetId, Object[] columnValue, int rowNo, int colNo, CellStyle style) throws Exception{		
		Sheet sheet = initializeSheet(workbook, sheetId);

		for(int i = 0; i < columnValue.length; i++){
			//Get the row from sheet
			Row row = sheet.getRow(rowNo);	        
			if(row == null){
				row = sheet.createRow(rowNo);
			}
			rowNo++;	       
			writeExcelCellValue(row, colNo, columnValue[i], style);
		}        
		return workbook;
	}

	/**
	 * Method to initialize Sheet in an Excel file
	 * @param workbook Workbook instance of Excel file
	 * @param sheetId Sheet ID(Name/Number)
	 * @return Sheet object initialized
	 * @throws Exception
	 */
	public Sheet initializeSheet(Workbook workbook, Object sheetId) throws Exception{
		Sheet sheet = null;
		if(sheetId instanceof String)
			sheet = workbook.getSheet((String)sheetId);
		else if(sheetId instanceof Integer)
			sheet = workbook.getSheetAt((Integer)sheetId);
		else if(sheetId == null)
			sheet = workbook.getSheetAt(0);
		else
			throw new Exception("Invalid sheet Id: " + sheetId.toString());

		return sheet;
	}

	/**
	 * Method to create a new Sheet in Excel file
	 * @param workbook Workbook instance of Excel file
	 * @param sheetId Sheet Id(Name/Number)
	 * @return Newly created Sheet
	 * @throws Exception
	 */
	public Sheet createSheet(Workbook workbook, String sheetId) throws Exception{
		Sheet sheet = workbook.createSheet((String)sheetId);
		return sheet;
	}

	/**
	 * Method to write a cell in Excel file
	 * @param row Row object to modify
	 * @param cellNo Cell number in the row to write
	 * @param cellValue Value to be written
	 * @param style Style of Cell
	 */
	public void writeExcelCellValue(Row row, int cellNo, Object cellValue, CellStyle style){
		Cell cell = row.getCell(cellNo);

		if(cell == null){
			cell = row.createCell(cellNo);
		}

		if(cellValue instanceof Date)
			cell.setCellValue((Date)cellValue);
		else if(cellValue instanceof Boolean)
			cell.setCellValue((Boolean)cellValue);
		else if(cellValue instanceof String)
			cell.setCellValue((String)cellValue);
		else if(cellValue instanceof Double)
			cell.setCellValue((Double)cellValue);
		else if(cellValue instanceof Long)
			cell.setCellValue((Long)cellValue);
		else if(cellValue instanceof Integer)
			cell.setCellValue((Integer)cellValue);

		
		if(style != null){
			cell.setCellStyle(style);
		}
	}

	/**
	 * Method to write an excel file
	 * @param workbook Workbook object to be written
	 * @param fileName Name of the excel file to be generated
	 * @throws Exception
	 */ 
	public void writeExcel(Workbook workbook, File file) throws Exception{
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			workbook.write(out);
		}
		finally{
			closeResource(out);
		}
	}

	/**
	 * Method to write an image to an Excel file
	 * @param workbook Workbook instance of Excel
	 * @param sheet Sheet in Excel to modify
	 * @param imgFile Image file to add
	 * @param startRow Starting row
	 * @param endRow Ending row
	 * @param startCol Starting column
	 * @param endCol Ending column
	 * @return Modified Workbook object
	 * @throws Exception
	 */
	/*public Workbook writeImageInExcel(Workbook workbook, Sheet sheet, String imgFile, int startRow, int endRow, int startCol, int endCol) throws Exception{
		InputStream inputStream = null;
		try {
			//FileInputStream obtains input bytes from the image file
			inputStream = new FileInputStream(imgFile);

			//Get the contents of an InputStream as a byte[].
			byte[] bytes = IOUtils.toByteArray(inputStream);

			//Adds a picture to the workbook
			int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

			//Returns an object that handles instantiating concrete classes
			CreationHelper helper = workbook.getCreationHelper();

			//Creates the top-level drawing patriarch.
			Drawing drawing = sheet.createDrawingPatriarch();

			//Create an anchor that is attached to the worksheet
			ClientAnchor anchor = helper.createClientAnchor();

			//set corner positions for the image
			anchor.setRow1(startRow);
			//anchor.setRow2(endRow);
			anchor.setCol1(startCol);
			//anchor.setCol2(endCol);

			//Creates a picture
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			//Reset the image to the original size
			pict.resize();

			return workbook;
		}
		finally{
			FASTFileUtils.INSTANCE.closeResource(inputStream);
		}
	}*/

	/**
	 * Method to write a row in excel file
	 * @param row Instance of Row class
	 * @param objArr Cell data to write in row
	 */ 
	public void writeDataInRow(Row row, Object [] objArr,Workbook workbook)
	{
		int cellnum = 0;
		for (Object obj : objArr)
		{
			org.apache.poi.ss.usermodel.Cell cell = row.createCell(cellnum++);
			CellStyle style = workbook.createCellStyle(); 
            style.setWrapText(true); 
            cell.setCellStyle(style); 
			if(obj instanceof Date)
				cell.setCellValue((Date)obj);
			else if(obj instanceof Boolean)
				cell.setCellValue((Boolean)obj);
			else if(obj instanceof String)
				cell.setCellValue((String)obj);
			else if(obj instanceof Double)
				cell.setCellValue((Double)obj);
		}
	}	

	/**
	 * Method to read Non-String value(as per the type of Cell) from Cell object.
	 * @param cell Cell object
	 * @return cellValue Object read from cell
	 */ 
	public Object readNonStringCellValue(Cell cell){
		Object cellValue = null;

		if(cell != null){
			switch(cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN: cellValue = cell.getBooleanCellValue();
			break;
			case Cell.CELL_TYPE_NUMERIC: cellValue = cell.getNumericCellValue(); 
			break;
			case Cell.CELL_TYPE_STRING:  cellValue = cell.getStringCellValue();
			break;
		
			}
		}

		return cellValue;
	}

	/**
	 * Method to read String value from Cell object.
	 * @param cell Cell object
	 * @return cellValue String value in cell
	 */ 
	public String readCellValue(Cell cell){
		String cellValue = null;

		if(cell != null){
			switch(cell.getCellType())
			{
				case Cell.CELL_TYPE_FORMULA : {
					                          
					 try{
						 /*if(cell.getCellStyle().getDataFormatString().contains("%"))
						 {
							 cellValue = Math.round((cell.getNumericCellValue() * 100)+"%";
						 }
						 else*/
							 cellValue=""+cell.getNumericCellValue();
					 }catch(Exception e){
						    cell.setCellType(Cell.CELL_TYPE_STRING);
							cellValue = cell.getStringCellValue();
							cellValue = cellValue.trim();
					 }
					 return cellValue;
					}
				case Cell.CELL_TYPE_NUMERIC: {
					if (cell.getCellStyle().getDataFormatString().contains("%")) {
						cellValue = ""+cell.getNumericCellValue() * 100;
					}
					else
						cellValue=""+cell.getNumericCellValue();
					 return cellValue;
				}
				default :                                
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cellValue = cell.getStringCellValue();
				cellValue = cellValue.trim();
		  }
		}
		return cellValue;
	}

	/**
	 * Method to write list of object arrays to an excel file
	 * @param data List of object arrays to write in excel
	 * @param filePath Name of file(with complete path)
	 * @param sheetName Name of the sheet to be created in excel file
	 */ 
	public void writeDataListToExcel(List<Object[]> data, String filePath, String sheetName, int srcRow) throws Exception{
		File file = new File(filePath);
		Workbook workbook = null;
		Sheet sheet = null;
		int row1 = 0;
/*		if(file.exists() && file.length() == 1){
			workbook = readWorkbook(filePath);
			sheet = initializeSheet(workbook, sheetName);
			row1 = sheet.getLastRowNum()+1;
		}
		else{
			//workbook = createWorkbook(filePath);
			//sheet = workbook.createSheet(sheetName);
			row1 = srcRow;
			if(row1==0 && srcCol==0)
			{
				row1 = sheet.getLastRowNum()+1;
				
			}
		} 
		*/
		workbook = readWorkbook(filePath);
		sheet = initializeSheet(workbook, sheetName);
		
		Row srcrow = sheet.getRow(srcRow);
		
		//sheet.removeRow(srcrow);
		row1 = srcRow;
		for (int i = 0; i < data.size(); i++) {
			Row row = sheet.createRow(row1);
			writeDataInRow(row, data.get(i),workbook);
		}
		writeExcel(workbook, new File(filePath));
	}

	public int getColumnNumberFromName(Sheet sheet, String columnName){
		Row headerRow = sheet.getRow(0);
		int columns = headerRow.getPhysicalNumberOfCells();

		for(int i = 0; i < columns; i++){
			if(readCellValue(headerRow.getCell(i)).equals(columnName))
			{
				return i;
			}
		}
		return -1;
	}
	public void writeDataListToExcel1(List<Object[]> data, String filePath, String sheetName) throws Exception{
		File file = new File(filePath);
		Workbook workbook = null;
		Sheet sheet = null;
		int row1 = 0;
		if(file.exists()){
			workbook = readWorkbook(filePath);
			sheet = initializeSheet(workbook, sheetName);
			row1 = sheet.getLastRowNum();
			System.out.println("row1-->"+row1);
		
			Row rowdata = sheet.getRow(row1);			
			if(rowdata != null)
			{
				row1 += 1;
			}
		}
		else{
			workbook = createWorkbook(filePath);
			sheet = workbook.createSheet(sheetName);
		} 
		for (int i = 0; i < data.size(); i++) {
			//Row row = sheet.getRow(row1+i);
			Row row = sheet.createRow(row1+i);
			writeDataInRow(row, data.get(i),workbook);
		}
		writeExcel(workbook, new File(filePath));
	}

	/*public void updateCellValue(String sheet,int row,int col,Object data,String filePath)throws Exception{
		
		File file = new File(filePath);
		Workbook workbook = null;
		Sheet sheetName = null;
		if(file.exists()){
			workbook = readWorkbook(filePath);
			sheetName = initializeSheet(workbook, sheet);
			Row rowdata = sheetName.getRow(row);
			if(rowdata != null)
			{
				Cell cell = rowdata.getCell(arg0);
				cell.setCellValue(data.toString());
			}
		}
	}*/
	
}
