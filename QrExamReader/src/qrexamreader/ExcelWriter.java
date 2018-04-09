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
import java.util.HashMap;
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
    public int numberOfQuestion;
    public ArrayList<Integer> questionsPoints = null;
    public HashMap<String, Integer> map = null;
    ArrayList<Integer> questionsMaxPoints = null;

    ExcelWriter(ArrayList<QRCode> openQR, int numberOfQuestion, HashMap<String, Integer> map, ArrayList<Integer> questionsPoints, ArrayList<Integer> questionsMaxPoints) {
        this.openQR = new ArrayList(openQR);
        this.numberOfQuestion = numberOfQuestion;
        this.map = new HashMap(map);
        this.questionsPoints = new ArrayList(questionsPoints);
        this.questionsMaxPoints = new ArrayList(questionsMaxPoints);
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
        Cell cN = row.createCell(0);
        cN.setCellStyle(headerStyle);
        cN.setCellValue("Course Name:");

        cN = row.createCell(1);
        cN.setCellStyle(ansStyle);
        cN.setCellValue(openQR.get(0).examName);

        row = sheet.createRow(1);
        cN = row.createCell(0);
        cN.setCellStyle(headerStyle);
        cN.setCellValue("Exam Name");

        cN = row.createCell(1);
        cN.setCellStyle(ansStyle);
        if(openQR.get(0).examType.equals("M")){
            cN.setCellValue("Midterm");
        }
        else{
            cN.setCellValue("Final");
        }
        
        row = sheet.createRow(2);
        cN = row.createCell(0);
        cN.setCellStyle(headerStyle);
        cN.setCellValue("Questions");

        System.out.println("QP: " + questionsPoints.toString());
        System.out.println("QR " + openQR.toString());
        System.out.println("NQ " + numberOfQuestion);
        System.out.println("QMP" + questionsMaxPoints.toString());

        int studentCounter = 1;
        int examPageCounter = 1;
        for (int i = 0; i < openQR.size() - 1; i++) {
            if (!openQR.get(i).studentName.equals(openQR.get(i + 1).studentName)) {
                studentCounter++;
            }
        }

        for (int j = 0; j < openQR.size() - 1; j++) {
            if (openQR.get(0).studentName.equals(openQR.get(j + 1).studentName)) {
                examPageCounter++;
                System.out.println(examPageCounter);
            }
        }

        int cellCountQuestions = 2;
        for (int i = 0; i < openQR.size() / studentCounter; i++) {
            for (int j = 0; j < openQR.get(i).questions.size(); j++) {
                cN = row.createCell(cellCountQuestions);
                cN.setCellStyle(ansStyle);
                cN.setCellValue("Q" + openQR.get(i).questions.get(j).substring(0, 3));
                cellCountQuestions++;
            }
        }

        cN = row.createCell(cellCountQuestions);
        cN.setCellStyle(ansStyle);
        cN.setCellValue("Total");

        row = sheet.createRow(3);
        cN = row.createCell(0);
        cN.setCellStyle(headerStyle);
        cN.setCellValue("Max Scores:");

        int cellCountScores = 2;
        int maxExamGrade = 0;
        for (int i = 0; i < questionsMaxPoints.size() / studentCounter; i++) {
            cN = row.createCell(cellCountScores);
            cN.setCellStyle(ansStyle);
            cN.setCellValue(questionsMaxPoints.get(i));
            maxExamGrade += questionsMaxPoints.get(i);
            cellCountScores++;
        }
        cN = row.createCell(cellCountScores);
        cN.setCellStyle(ansStyle);
        cN.setCellValue(maxExamGrade);

        row = sheet.createRow(4);
        cN = row.createCell(0);
        cN.setCellStyle(headerStyle);
        cN.setCellValue("Student Name");

        cN = row.createCell(1);
        cN.setCellStyle(ansStyle);
        cN.setCellValue("Student No");

        int questionCounter = 0;
        int studentRowCounter = 5;
        for (int i = 0; i < studentCounter; i++) {
            row = sheet.createRow(studentRowCounter);
            cN = row.createCell(0);
            cN.setCellStyle(headerStyle);
            cN.setCellValue(openQR.get(examPageCounter * i).studentName);

            cN = row.createCell(1);
            cN.setCellStyle(ansStyle);
            cN.setCellValue(openQR.get(examPageCounter * i).studentID);
            studentRowCounter++;

            int studentColumnCounter = 2;
            int totalGrade = 0;
            for (int j = 0; j < (questionsPoints.size() / studentCounter) + 1; j++) {
                cN = row.createCell(studentColumnCounter);
                cN.setCellStyle(ansStyle);
                if (j < (questionsPoints.size() / studentCounter)) {
                    cN.setCellValue(questionsPoints.get(questionCounter));
                    totalGrade += questionsPoints.get(questionCounter);
                    questionCounter++;
                } else if (j == (questionsPoints.size() / studentCounter)) {
                    cN.setCellValue(totalGrade);
                }
                studentColumnCounter++;
            }

        }
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
