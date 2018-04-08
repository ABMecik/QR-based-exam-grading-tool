/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrexamreader;

import java.awt.print.Book;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author MONSTER
 */
public class ExcelWriter {
    
    public ArrayList<QRCode> openQR = null;

    ExcelWriter(ArrayList<QRCode> openQR) {
        this.openQR = new ArrayList(openQR);
    }
    
    public void writeExcel(String excelFilePath) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		
		createHeaderRow(sheet);
		

		
		try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
			workbook.write(outputStream);
		}		
	}
	
	private void createHeaderRow(Sheet sheet) {
		
		
                //fonts-->
                CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
		Font headerFont = sheet.getWorkbook().createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 15);
		headerStyle.setFont(headerFont);

                
                CellStyle ansStyle = sheet.getWorkbook().createCellStyle();
                Font ansFont = sheet.getWorkbook().createFont();
		ansFont.setBold(false);
		ansFont.setFontHeightInPoints((short) 12);
		ansStyle.setFont(headerFont);
                ansStyle.setAlignment(HorizontalAlignment.CENTER);
                
                //<--fonts
                
                sheet.setColumnWidth(0, 7500);
                sheet.setColumnWidth(1, 6000);
                
		Row row = sheet.createRow(0);
		Cell cN1 = row.createCell(0);
		cN1.setCellStyle(headerStyle);
		cN1.setCellValue("Course Name:");
                

                Cell cN2 = row.createCell(1);
                cN2.setCellStyle(ansStyle);
                cN2.setCellValue(openQR.get(0).examName);
                
		row = sheet.createRow(1);
		Cell cellAuthor = row.createCell(0);
		cellAuthor.setCellStyle(headerStyle);
		cellAuthor.setCellValue("Exam Name:");
		
	}
	/*
	private void writeBook(Book aBook, Row row) {
		Cell cell = row.createCell(1);
		cell.setCellValue(aBook.getTitle());

		cell = row.createCell(2);
		cell.setCellValue(aBook.getAuthor());
		
		cell = row.createCell(3);
		cell.setCellValue(aBook.getPrice());
	}
	
	private List<Book> getListBook() {
		Book book1 = new Book("Head First Java", "Kathy Serria", 79);
		Book book2 = new Book("Effective Java", "Joshua Bloch", 36);
		Book book3 = new Book("Clean Code", "Robert Martin", 42);
		Book book4 = new Book("Thinking in Java", "Bruce Eckel", 35);
		
		List<Book> listBook = Arrays.asList(book1, book2, book3, book4);
		
		return listBook;
	}
*/
    
}
