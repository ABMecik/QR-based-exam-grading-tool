/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrexamreader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;



import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

/**
 *
 * @author MEFLAB409
 */
public class GuiController implements Initializable {
    
    @FXML // fx:id="choosePdfPathBtn"
    private Button choosePdfPathBtn;
    
    @FXML
    private Button choosePngPathBtn; 
    
    @FXML
    private Button choosePdfFileBtn; 
    
    public String PdfPath = null;
    public File PdfDirectoryPath = null;
    public String PngPath = null;
    public File PngDirectoryPath = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        choosePdfFileBtn.setOnAction((ActionEvent event) -> {
            PdfPath = null;
            PdfDirectoryPath = null;
            FileChooser chooser = new FileChooser();
                chooser.setInitialDirectory(new File("."));
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
                chooser.setTitle("Select EXAM");
                Stage stage = new Stage();
                File file = chooser.showOpenDialog(stage);
                if (file == null) {
                    return;
                }
                System.out.println("file " + file.getAbsolutePath()); 
                PdfPath = file.getAbsolutePath();
        });
        
        
        choosePdfPathBtn.setOnAction((ActionEvent event) -> {
            PdfPath = null;
            PdfDirectoryPath = null;
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Exam Directory");
            Stage stage = new Stage();
            File selectedDirectory = directoryChooser.showDialog(stage);
            if(selectedDirectory == null){
                return;
            }
            PdfDirectoryPath = selectedDirectory;
            System.out.println("folder " + selectedDirectory.getAbsolutePath());
        });
        
        choosePngPathBtn.setOnAction((ActionEvent event) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Exam Data Directory");
            Stage stage = new Stage();
            File selectedDirectory = directoryChooser.showDialog(stage);
            if(selectedDirectory == null){
                return;
            }
            System.out.println("folder " + selectedDirectory.getAbsolutePath());
            PngDirectoryPath = selectedDirectory;
            
            
            if (selectedDirectory.getAbsolutePath() != null && PdfDirectoryPath != null){
                try {
                    readEachPdf(PdfDirectoryPath, selectedDirectory.getAbsolutePath());
                } catch (IOException ex) {
                    Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else if (selectedDirectory.getAbsolutePath() != null && PdfPath != null){
                try {
                    PDFToPNG(PdfPath,selectedDirectory.getAbsolutePath());
                } catch (IOException ex) {
                    Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                System.out.println("error");
            }
            
            readPNG();
        });
    }    
    
    private void readEachPdf(File folder, String outputPngPath) throws IOException {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                readEachPdf(fileEntry, outputPngPath);
            } else {
                PdfPath = fileEntry.getAbsolutePath();
                System.out.println("folder " + PdfPath);
                PDFToPNG(PdfPath, outputPngPath);
            }
        }
    }

    private void PDFToPNG(String inputPdfPath, String outputPngPath) throws IOException {
        File f = new File(outputPngPath);
        if (!f.exists()) {
            f.mkdir();
        }
        PDDocument document = PDDocument.load(new File(inputPdfPath));
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
            ImageIOUtil.writeImage(bim, outputPngPath + "/Page " + (page + 1) + ".png", 300);
            System.out.println("page " + (page+1) + "finished...");
        }
        document.close();
        
    }
    
    private void readPNG() {
        for (File fileEntry : PngDirectoryPath.listFiles()) {
                PngPath = fileEntry.getAbsolutePath();
                System.out.println("png path: " + PngPath);
                BufferedImage img = null;
                try {
                    img = ImageIO.read(new File(PngPath));
                } catch (IOException e) {
                }
        }
    }
}