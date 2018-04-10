package qrexamreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class QRCode {

    public String examName = "None";
    public String examDate = "None";
    public String examType = "None";
    public int examPageNumber = 0;
    public String studentName = "None";
    public int studentID = 0;
    public ArrayList<String> questions = new ArrayList();

    public QRCode(File file) throws IOException, NotFoundException {
        decodeQRCode(file);
    }

    QRCode(String newExamName, String newDate, String newExamType, int newPageNumber, String newStudentName, int newStudentID, ArrayList<String> newQuestions) {
        examName = newExamName;
        examDate = newDate;
        examType = newExamType;
        examPageNumber = newPageNumber;
        studentName = newStudentName;
        studentID = newStudentID;
        questions = newQuestions;
    }

    /**
     *
     * @param newExamName
     * @param newDate
     * @param newExamType
     * @param newPageNumber
     * @param newStudentName
     * @param newStudentID
     * @param newQuestions
     */
    public void writeInfo(String newExamName, String newDate, String newExamType, int newPageNumber, String newStudentName, int newStudentID, ArrayList<String> newQuestions) {
        examName = newExamName;
        examDate = newDate;
        examType = newExamType;
        examPageNumber = newPageNumber;
        studentName = newStudentName;
        studentID = newStudentID;
        questions = newQuestions;
    }

    public void decodeQRCode(File qrCodeimage) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));


        Result result = new MultiFormatReader().decode(bitmap);
        splitResult(result.getText());

    }

    public void splitResult(String qrText) {
        String parts[] = qrText.split(",");
        String newExamName = parts[0];
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
        String newDate = parts[1];
        String newExamType = parts[2];
        int newPageNumber = Integer.parseInt(parts[3]);
        String newStudentName = parts[4];
        int newStudentID = Integer.parseInt(parts[5]);
        ArrayList<String> newQuestions = new ArrayList();
        for (int i = 6; i < parts.length; i++) {
            newQuestions.add(parts[i]);
        }
        writeInfo(newExamName, newDate, newExamType, newPageNumber, newStudentName, newStudentID, newQuestions);
    }

    public String toString() {
        return String.format("Exam Name : " + examName + "\n"
                + "Exam Date : " + examDate + "\n"
                + "Exam Type : " + examType + "\n"
                + "Exam-Page Number : " + examPageNumber + "\n"
                + "Student Name : " + studentName + "\n"
                + "Student ID : " + studentID + "\n");
    }

}
