package qrexamreader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;


/* PDF BOX */
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
    private Button choosePdfFileBtn; 
    
    @FXML
    private Button chooseDataPathBtn;
    
    @FXML
    private Button executeDataBtn;
    
    @FXML
    private Button nextBtn;
    
    @FXML
    private Button prevBtn;
    
    @FXML
    private ImageView questionImage;
    
    @FXML
    private Text studentNameText;
    
    @FXML
    private Text studentIDText;
    
    @FXML
    private Text questionText;
    
    @FXML
    private Text sumOfGradeText;
    
    @FXML
    private TextArea questionPrompt;
    
    @FXML
    private TextField maxGradePromptText;
    
    
    private static String OS = System.getProperty("os.name").toLowerCase();
    
    public String pdfFilePath = null;
    public File pdfFolderPath = null;
    public ArrayList<File> pagePath = new ArrayList();
    public ArrayList<QRCode> openQR = new ArrayList();
    public File pngFolderPath = null;
    public File dataFolderPath = null;
    public ArrayList<String> allQuestions = new ArrayList();
    public ArrayList<Integer> questionsMaxPoints = new ArrayList();
    public ArrayList<Integer> questionsNumber = new ArrayList();
    public int currentQR = 0;
    public int currentQuestion = 0;
    public int currentPosition = 0;
    public File selectedDirectory = null;
    public String examType = null;
    public HashMap<String,Integer> map = new HashMap<String, Integer>();
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
        if(isWindows()){
            choosePdfFileBtn.setOnAction((ActionEvent event) -> {
            pdfFilePath = null;
            pdfFolderPath = null;
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File("."));
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
            chooser.setTitle("Select EXAM");
            Stage stage = new Stage();
            File file = chooser.showOpenDialog(stage);
            if (file == null) {
                return;
            }
            chooseDataPathBtn.setDisable(false);
            System.out.println("file " + file.getAbsolutePath()); 
            pdfFilePath = file.getAbsolutePath();
            });


            choosePdfPathBtn.setOnAction((ActionEvent event) -> {
                pdfFilePath = null;
                pdfFolderPath = null;
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Exam Directory");
                Stage stage = new Stage();
                File selectedDirectory = directoryChooser.showDialog(stage);
                if(selectedDirectory == null){
                    return;
                }
                chooseDataPathBtn.setDisable(false);
                pdfFolderPath = selectedDirectory;
                System.out.println("folder " + selectedDirectory.getAbsolutePath());
            });

            chooseDataPathBtn.setOnAction((ActionEvent event) -> {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Exam Data Directory");
                Stage stage = new Stage();
                selectedDirectory = directoryChooser.showDialog(stage);
                if(selectedDirectory == null){
                    return;
                }  
                executeDataBtn.setDisable(false);
            });
            
            executeDataBtn.setOnAction((ActionEvent event) -> {
                String newPngPath2 = selectedDirectory + "\\data";
                dataFolderPath = new File(newPngPath2);
                if(!dataFolderPath.exists()){
                    dataFolderPath.mkdir();
                }
                String newPngPath1 = selectedDirectory + "\\other";
                pngFolderPath = new File(newPngPath1);
                if(!pngFolderPath.exists()){
                    pngFolderPath.mkdir();
                }
                System.out.println("folder " + pngFolderPath.getAbsolutePath());

                
                if (selectedDirectory.getAbsolutePath() != null && pdfFolderPath != null){
                    try {
                        readEachPdf(pdfFolderPath, pngFolderPath.getAbsolutePath());
                        savePng(pngFolderPath.getAbsolutePath());
                        storeStudentTotalGrade();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if (selectedDirectory.getAbsolutePath() != null && pdfFilePath != null){
                    try {
                        PDFToPNG(pdfFilePath, pngFolderPath.getAbsolutePath());
                        savePng(pngFolderPath.getAbsolutePath());
                        storeStudentTotalGrade();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }else{
                    System.out.println("error");
                }

                try {
                    splitPage();
                } catch (IOException ex) {
                    Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                }
                setGradeScreen(0);
            });

        }
        else if(isMac()){
            choosePdfFileBtn.setOnAction((ActionEvent event) -> {
            pdfFilePath = null;
            pdfFolderPath = null;
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File("."));
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
            chooser.setTitle("Select EXAM");
            Stage stage = new Stage();
            File file = chooser.showOpenDialog(stage);
            if (file == null) {
                return;
            }
            chooseDataPathBtn.setDisable(false);
            System.out.println("file " + file.getAbsolutePath()); 
            pdfFilePath = file.getAbsolutePath();
            });


            choosePdfPathBtn.setOnAction((ActionEvent event) -> {
                pdfFilePath = null;
                pdfFolderPath = null;
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Exam Directory");
                Stage stage = new Stage();
                File selectedDirectory = directoryChooser.showDialog(stage);
                if(selectedDirectory == null){
                    return;
                }
                chooseDataPathBtn.setDisable(false);
                pdfFolderPath = selectedDirectory;
                System.out.println("folder " + selectedDirectory.getAbsolutePath());
            });

            chooseDataPathBtn.setOnAction((ActionEvent event) -> {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Exam Data Directory");
                Stage stage = new Stage();
                selectedDirectory = directoryChooser.showDialog(stage);
                if(selectedDirectory == null){
                    return;
                } 
                executeDataBtn.setDisable(false);
            });
            
            executeDataBtn.setOnAction((ActionEvent event) -> {
                String newPngPath2 = selectedDirectory + "/data";
                dataFolderPath = new File(newPngPath2);
                if(!dataFolderPath.exists()){
                    dataFolderPath.mkdir();
                }
                String newPngPath1 = selectedDirectory + "/other";
                pngFolderPath = new File(newPngPath1);
                if(!pngFolderPath.exists()){
                    pngFolderPath.mkdir();
                }
                System.out.println("folder " + pngFolderPath.getAbsolutePath());


                if (selectedDirectory.getAbsolutePath() != null && pdfFolderPath != null){
                    try {
                        readEachPdf(pdfFolderPath, pngFolderPath.getAbsolutePath());
                        savePng(pngFolderPath.getAbsolutePath());
                        storeStudentTotalGrade();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if (selectedDirectory.getAbsolutePath() != null && pdfFilePath != null){
                    try {
                        PDFToPNG(pdfFilePath, pngFolderPath.getAbsolutePath());
                        savePng(pngFolderPath.getAbsolutePath());
                        storeStudentTotalGrade();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }else{
                    System.out.println("error");
                }

                try {
                    splitPage();
                } catch (IOException ex) {
                    Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                }
                setGradeScreen(0);
                
            });
        }
        else{
            
        }
        
        nextBtn.setOnAction((ActionEvent event) -> {
            if(currentQuestion == openQR.get(currentQR).questions.size()-1){
                currentQR++;              
                currentQuestion = 0;
            }
            else{
                currentQuestion++;
            }
            currentPosition++;
            setGradeScreen(1);

            
        });
        
       
        
    }
    
    private void storeStudentTotalGrade(){
        for(int i=0; i<openQR.size(); i++){
            String key = openQR.get(i).studentName;
            if(!map.containsKey(key)){
                map.put(key, 0);
            }
        }
    }
    
    
    private void setGradeScreen(int place){
        if(place == 1){
            int studentCurrentGrade = map.get(openQR.get(currentQR).studentName);
            studentCurrentGrade += Integer.parseInt(maxGradePromptText.getText());
            map.put(openQR.get(currentQR).studentName, studentCurrentGrade);
        }
        String[] part = openQR.get(currentQR).questions.get(currentQuestion).split("\\.");
        String studentID = String.valueOf(openQR.get(currentQR).studentID);
        studentNameText.setText(openQR.get(currentQR).studentName);
        studentIDText.setText(studentID);
        questionText.setText(part[1]);
        maxGradePromptText.setPromptText("Max : " + part[2]);
        sumOfGradeText.setText(String.valueOf(map.get(openQR.get(currentQR).studentName)));
        
        Image newImageView = new Image("file:"+allQuestions.get(currentPosition));
        questionImage.setImage(newImageView);
        
        maxGradePromptText.setText(null);
    }

    
    private static boolean isWindows() {
        return (OS.contains("win"));
    }
    private static boolean isMac() {
        return (OS.contains("mac"));
    }
    
    private void readEachPdf(File folder, String outputPngPath) throws IOException {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                readEachPdf(fileEntry, outputPngPath);
            } else {
                pdfFilePath = fileEntry.getAbsolutePath();
                PDFToPNG(pdfFilePath, outputPngPath);
            }
        }
    }

    
    
    private void PDFToPNG(String inputPdfPath, String outputPngPath) throws IOException {
        File f = new File(outputPngPath);
        if (!f.exists()) {
            f.mkdir();
        }
        try (PDDocument document = PDDocument.load(new File(inputPdfPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                ImageIOUtil.writeImage(bim, outputPngPath + "/Page " + "-" + (page + 1) + ".png", 300);
                String pathX = outputPngPath + "/Page " + "-" + (page + 1) + ".png";
                File pageFile = new File(pathX);
                pagePath.add(pageFile);
                System.out.println("page" + (page+1) + " finished...");
            }
        }
        
    }
    
    private void savePng(String outputPngPath) throws IOException {
        int i=1;
        String pathX=null;
        for (File fileEntry : pagePath) {
            if (fileEntry.isDirectory()) {
            }else{
                BufferedImage img = ImageIO.read(new File(fileEntry.getAbsolutePath()));
                BufferedImage croppedImage = img.getSubimage(1779, 10, 700, 600);
                if(isWindows()){
                   pathX = outputPngPath + "\\subImage" + "-" + i + ".png"; 
                }
                else if(isMac()){
                   pathX = outputPngPath + "/subImage" + "-" + i + ".png"; 
                }
                else{
                    System.out.println("Error");
                }
                File outputPath = new File(pathX);
                ImageIO.write(croppedImage, "png", outputPath);
                openQR.add(new QRCode(outputPath));
                i++;
            }
        }
    }
    
    private void control(){
        for (int i=0; i<openQR.size(); i++) {
            System.out.println("Page : " + pagePath.get(i).getAbsolutePath());
            System.out.println(openQR.get(i).toString());
        }
    }

    private void splitPage() throws IOException {
        for (int i=0; i<openQR.size(); i++) {
            BufferedImage img = ImageIO.read(new File(pagePath.get(i).getAbsolutePath()));
            int height = img.getHeight();
            int width = img.getWidth();
            if(isWindows()){
                for(int k=0; k<openQR.get(i).questions.size(); k++){
                    String percs[] = openQR.get(i).questions.get(k).split("%");
                    String parts[] = percs[0].split("\\.");
                    String pageNumber = parts[0];
                    int questionNumber = Integer.parseInt(parts[1].trim());
                    questionsNumber.add(questionNumber);
                    int questionMaxPoint = Integer.parseInt(parts[2].trim());
                    questionsMaxPoints.add(questionMaxPoint);
                    float questFloat = Float.parseFloat(percs[1].trim());
                    if(openQR.get(i).examType.equals("M")){
                        examType = "Midterm";
                    }
                    else{
                        examType = "Final";
                    }
                    String newDir = dataFolderPath.getAbsolutePath() + "\\" 
                            + openQR.get(i).examName + "\\" 
                            + examType + "\\" 
                            + openQR.get(i).studentName;
                    File theDir = new File(newDir);
                    if (!theDir.exists()) {
                        theDir.mkdirs();
                    }
                    String newPath = newDir + "\\" + "question-" + questionNumber + ".png";
                    allQuestions.add(newPath);
                    if(k == openQR.get(i).questions.size()-1){
                        int start = (int)(((float)height/100) * questFloat);
                        int distance = height - start;
                        BufferedImage croppedImage = img.getSubimage(0, start, width, distance);
                        File outputPath = new File(newPath);
                        ImageIO.write(croppedImage, "png", outputPath); 
                    }
                    else{
                        String endPerc[] = openQR.get(i).questions.get(k+1).split("%");
                        float endQuestFloat = Float.parseFloat(endPerc[1].trim());
                        int start = (int)((height/100) * questFloat);
                        int end = (int)((height/100) * endQuestFloat);
                        int distance = end - start;
                        BufferedImage croppedImage = img.getSubimage(0, start, width, distance);
                        File outputPath = new File(newPath);
                        ImageIO.write(croppedImage, "png", outputPath); 
                    }
                }
            }
            else if(isMac()){
                for(int k=0; k<openQR.get(i).questions.size(); k++){
                    String percs[] = openQR.get(i).questions.get(k).split("%");
                    String parts[] = percs[0].split("\\.");
                    String pageNumber = parts[0];
                    int questionNumber = Integer.parseInt(parts[1].trim());
                    questionsNumber.add(questionNumber);
                    int questionMaxPoint = Integer.parseInt(parts[2].trim());
                    questionsMaxPoints.add(questionMaxPoint);
                    float questFloat = Float.parseFloat(percs[1].trim());
                    if(openQR.get(i).examType.equals("M")){
                        examType = "Midterm";
                    }
                    else{
                        examType = "Final";
                    }
                    String newDir = dataFolderPath.getAbsolutePath() + "\\" 
                            + openQR.get(i).examName + "\\" 
                            + examType + "\\" 
                            + openQR.get(i).studentName;
                    File theDir = new File(newDir);
                    if (!theDir.exists()) {
                        theDir.mkdirs();
                    }
                    String newPath = newDir + "/" + "question-" + questionNumber + ".png";
                    allQuestions.add(newPath);
                    if(k == openQR.get(i).questions.size()-1){
                        int start = (int)(((float)height/100) * questFloat);
                        int distance = height - start;
                        BufferedImage croppedImage = img.getSubimage(0, start, width, distance);
                        File outputPath = new File(newPath);
                        ImageIO.write(croppedImage, "png", outputPath); 
                    }
                    else{
                        String endPerc[] = openQR.get(i).questions.get(k+1).split("%");
                        float endQuestFloat = Float.parseFloat(endPerc[1].trim());
                        int start = (int)((height/100) * questFloat);
                        int end = (int)((height/100) * endQuestFloat);
                        int distance = end - start;
                        BufferedImage croppedImage = img.getSubimage(0, start, width, distance);
                        File outputPath = new File(newPath);
                        ImageIO.write(croppedImage, "png", outputPath); 
                    }
                }
            }
            else{
               System.out.println("Error"); 
            }
           
        }
        
        
        
        
        
        control();
        System.out.println("Success...");
    }
}