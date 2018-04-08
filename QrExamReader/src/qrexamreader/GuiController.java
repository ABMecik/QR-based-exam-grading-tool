package qrexamreader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import java.io.FileOutputStream;
import java.io.IOException;


/* PDF BOX */
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/* EXCEL POI*/
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    private Button newExamButton;
    
    @FXML
    private Button excelOptionBtn;
    
    @FXML
    private ImageView questionImage;
    
    @FXML
    private ImageView logoImage;
    
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

    @FXML
    private TextField pdfFilePathText;
    
    @FXML
    private TextField saveToPathText;
    
    @FXML
    private Text questPointText;

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
    public ArrayList<Integer> questionsPoints = new ArrayList();
    public File selectedDirectory = null;
    public String examType = null;
    public HashMap<String, Integer> map = new HashMap<String, Integer>();
    public PrintWriter writer = null;
    public File dataFile = new File("data.txt");
    public File positionFile = new File("position.txt");
    public int numberOfQuestion = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        if (!dataFile.exists()) {
            try {
                writer = new PrintWriter(dataFile);
                writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            try {
                loadData();
                setPage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (!positionFile.exists()) {
            try {
                writer = new PrintWriter(positionFile);
                writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            try {
                loadCurrentInfo();
                setPage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
           
        excelOptionBtn.setOnAction((ActionEvent event) -> {
            ExcelWriter excelWriter = new ExcelWriter(openQR);
            String excelFilePath = dataFolderPath.getAbsolutePath() + "\\" + "Result.xslx";
            try {
                excelWriter.writeExcel(excelFilePath);
            } catch (IOException ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        if (isWindows()) {
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
                pdfFilePathText.setText(pdfFilePath);
            });

            choosePdfPathBtn.setOnAction((ActionEvent event) -> {
                pdfFilePath = null;
                pdfFolderPath = null;
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Exam Directory");
                Stage stage = new Stage();
                File selectedDirectory = directoryChooser.showDialog(stage);
                if (selectedDirectory == null) {
                    return;
                }
                chooseDataPathBtn.setDisable(false);
                pdfFolderPath = selectedDirectory;
                pdfFilePathText.setText(pdfFolderPath.getAbsolutePath());
                System.out.println("folder " + selectedDirectory.getAbsolutePath());
            });

            chooseDataPathBtn.setOnAction((ActionEvent event) -> {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Exam Data Directory");
                Stage stage = new Stage();
                selectedDirectory = directoryChooser.showDialog(stage);
                if (selectedDirectory == null) {
                    return;
                }
                saveToPathText.setText(selectedDirectory.getAbsolutePath());
                executeDataBtn.setDisable(false);
            });

            executeDataBtn.setOnAction((ActionEvent event) -> {
                String newPngPath2 = selectedDirectory + "\\data";
                dataFolderPath = new File(newPngPath2);
                if (!dataFolderPath.exists()) {
                    dataFolderPath.mkdir();
                }
                String newPngPath1 = selectedDirectory + "\\other";
                pngFolderPath = new File(newPngPath1);
                if (!pngFolderPath.exists()) {
                    pngFolderPath.mkdir();
                }
                System.out.println("folder " + pngFolderPath.getAbsolutePath());

                if (selectedDirectory.getAbsolutePath() != null && pdfFolderPath != null) {
                    try {
                        readEachPdf(pdfFolderPath, pngFolderPath.getAbsolutePath());
                        savePng(pngFolderPath.getAbsolutePath());
                        storeStudentTotalGrade();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (selectedDirectory.getAbsolutePath() != null && pdfFilePath != null) {
                    try {
                        PDFToPNG(pdfFilePath, pngFolderPath.getAbsolutePath());
                        savePng(pngFolderPath.getAbsolutePath());
                        storeStudentTotalGrade();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    System.out.println("error");
                }

                try {
                    splitPage();
                } catch (IOException ex) {
                    Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                }
                setPage();
                storeData();
                storeCurrentInfo();
                
            });

        } else if (isMac()) {
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
                pdfFilePathText.setText(pdfFilePath);
            });

            choosePdfPathBtn.setOnAction((ActionEvent event) -> {
                pdfFilePath = null;
                pdfFolderPath = null;
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Exam Directory");
                Stage stage = new Stage();
                File selectedDirectory = directoryChooser.showDialog(stage);
                if (selectedDirectory == null) {
                    return;
                }
                chooseDataPathBtn.setDisable(false);
                pdfFolderPath = selectedDirectory;
                pdfFilePathText.setText(pdfFolderPath.getAbsolutePath());
                System.out.println("folder " + selectedDirectory.getAbsolutePath());
            });

            chooseDataPathBtn.setOnAction((ActionEvent event) -> {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Exam Data Directory");
                Stage stage = new Stage();
                selectedDirectory = directoryChooser.showDialog(stage);
                if (selectedDirectory == null) {
                    return;
                }
                saveToPathText.setText(selectedDirectory.getAbsolutePath());
                executeDataBtn.setDisable(false);
            });

            executeDataBtn.setOnAction((ActionEvent event) -> {
                String newPngPath2 = selectedDirectory + "/data";
                dataFolderPath = new File(newPngPath2);
                if (!dataFolderPath.exists()) {
                    dataFolderPath.mkdir();
                }
                String newPngPath1 = selectedDirectory + "/other";
                pngFolderPath = new File(newPngPath1);
                if (!pngFolderPath.exists()) {
                    pngFolderPath.mkdir();
                }
                System.out.println("folder " + pngFolderPath.getAbsolutePath());

                if (selectedDirectory.getAbsolutePath() != null && pdfFolderPath != null) {
                    try {
                        readEachPdf(pdfFolderPath, pngFolderPath.getAbsolutePath());
                        savePng(pngFolderPath.getAbsolutePath());
                        storeStudentTotalGrade();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (selectedDirectory.getAbsolutePath() != null && pdfFilePath != null) {
                    try {
                        PDFToPNG(pdfFilePath, pngFolderPath.getAbsolutePath());
                        savePng(pngFolderPath.getAbsolutePath());
                        storeStudentTotalGrade();
                    } catch (IOException ex) {
                        Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    System.out.println("error");
                }

                try {
                    splitPage();
                } catch (IOException ex) {
                    Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                }
                setPage();
                storeData();
                storeCurrentInfo();

            });
        } else {

        }

        prevBtn.setOnAction((ActionEvent event) -> {
            if(currentPosition > 0){
                if(currentQuestion == 0){
                    currentQR--;
                    currentQuestion = openQR.get(currentQR).questions.size() - 1;
                    System.out.println(currentQuestion);
                }else{
                    currentQuestion--;
                }
                currentPosition--;
                setPage();
            }else{
                return;
            }
            
        });
        nextBtn.setOnAction((ActionEvent event) -> {

            String pattern = "[0-9]+";
            if (regExCheck(maxGradePromptText.getText(), pattern) == true) {
                String[] part = openQR.get(currentQR).questions.get(currentQuestion).split("\\.");
                if (Integer.parseInt(maxGradePromptText.getText()) > Integer.parseInt(part[2])) {
                    return;
                }
                if(currentPosition < questionsPoints.size()){
                    String[] currentPart = openQR.get(currentQR).questions.get(currentQuestion).split("\\.");
                    int studentCurrentGrade = map.get(openQR.get(currentQR).studentName) - questionsPoints.get(currentPosition);
                    map.put(openQR.get(currentQR).studentName, studentCurrentGrade);
                    questionsPoints.set(currentPosition, Integer.parseInt(maxGradePromptText.getText()));
                }else{
                    questionsPoints.add(Integer.parseInt(maxGradePromptText.getText()));
                }
                setGradeScreen();
                if (currentQuestion == openQR.get(currentQR).questions.size() - 1) {
                    currentQR++;
                    currentQuestion = 0;
                } else {
                    currentQuestion++;
                }
                currentPosition++;
                setPage();
                storeCurrentInfo();
            } else if(maxGradePromptText.getText().isEmpty()){
                if(currentPosition < questionsPoints.size()){
                    if (currentQuestion == openQR.get(currentQR).questions.size() - 1) {
                    currentQR++;
                    currentQuestion = 0;
                } else {
                    currentQuestion++;
                }
                currentPosition++;
                setPage();
                }else{
                    return;
                }  
            }else {
                return;
            }
        }
        );
        
        
    }
    
    
    
    private void loadCurrentInfo() throws FileNotFoundException, IOException{
        Scanner input = new Scanner(positionFile);
        
        currentQuestion = Integer.parseInt(input.nextLine());
        currentPosition = Integer.parseInt(input.nextLine());
        currentQR = Integer.parseInt(input.nextLine());
        String fullMap = input.nextLine();
        fullMap = fullMap.replaceAll("\\{", "").replaceAll("\\}", "");
        String parts[] = fullMap.split(",");
        for (String part : parts) {
            String[] secParts = part.split("=");
            map.put(secParts[0].trim(), Integer.parseInt(secParts[1].trim()));
        }
        String line;
        line = input.nextLine();
        line = line.replaceAll("\\[", "").replaceAll("\\]", "");
        String currentQuestionParts[] = line.split(",");
        for (String part : currentQuestionParts) {
            questionsPoints.add(Integer.parseInt(part.trim()));
        }
        
    }
    
    private void loadData() throws FileNotFoundException{
        Scanner input = new Scanner(dataFile);
        
        pdfFilePath = input.nextLine();
        pdfFolderPath = new File(input.nextLine()); 
        String line;
        line = input.nextLine();
        line = line.replaceAll("\\[", "").replaceAll("\\]", "");
        String pagePathParts[] = line.split(",");
        for (String part : pagePathParts) {
            pagePath.add(new File(part));
        }
        pngFolderPath = new File(input.nextLine()); 
        dataFolderPath = new File(input.nextLine());
        line = input.nextLine();
        line = line.replaceAll("\\[", "").replaceAll("\\]", "");
        String allQuestionsParts[] = line.split(",");
        for (String part : allQuestionsParts) {
            allQuestions.add(part.trim());
        }
        line = input.nextLine();
        line = line.replaceAll("\\[", "").replaceAll("\\]", "");
        String questionsMaxPointsParts[] = line.split(",");
        for (String part : questionsMaxPointsParts) {
            questionsMaxPoints.add(Integer.parseInt(part.trim()));
        }
        line = input.nextLine();
        line = line.replaceAll("\\[", "").replaceAll("\\]", "");
        String questionsNumberParts[] = line.split(",");
        for (String part : questionsNumberParts) {
            questionsNumber.add(Integer.parseInt(part.trim()));
        }
        selectedDirectory = new File(input.nextLine());
        
        examType = input.nextLine();
        
        String openQRParts[] = input.nextLine().split("\\@");
        for (String part : openQRParts) {
            
            String questionParts[] = part.trim().split("\\$");
            
            String newQuestionPart = questionParts[questionParts.length-1].trim();
            newQuestionPart = newQuestionPart.replaceAll("\\[", "").replaceAll("\\]", "");
            String questParts[] = newQuestionPart.split(",");
            ArrayList<String> newQuestionList = new ArrayList();
            for (String per : questParts) {
                newQuestionList.add(per);
            }
            /*
            System.out.println(questionParts[0]);
            System.out.println(questionParts[1]);
            System.out.println(questionParts[2]);
            System.out.println(Integer.parseInt(questionParts[3]));
            System.out.println(questionParts[4]);
            System.out.println(Integer.parseInt(questionParts[5]));
            System.out.println(newQuestionList);
            */
            openQR.add(new QRCode(questionParts[0],questionParts[1],questionParts[2],Integer.parseInt(questionParts[3]),questionParts[4],Integer.parseInt(questionParts[5]),newQuestionList));
        }
    }
    
    /*
    public String pdfFilePath = null;
    public File pdfFolderPath = null;
    public ArrayList<File> pagePath = new ArrayList();
    public File pngFolderPath = null;
    public File dataFolderPath = null;
    public ArrayList<String> allQuestions = new ArrayList();
    public ArrayList<Integer> questionsMaxPoints = new ArrayList();
    public ArrayList<Integer> questionsNumber = new ArrayList();
    public File selectedDirectory = null;
    public String examType = null;
    public ArrayList<QRCode> openQR = new ArrayList();

    */
    
    private void storeCurrentInfo(){
        try {
            writer = new PrintWriter(positionFile);
            writer.println(currentQuestion);
            writer.println(currentPosition);
            writer.println(currentQR);
            writer.println(map);
            writer.println(questionsPoints);
            
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void storeData(){
        try {
            writer = new PrintWriter(dataFile);
            writer.println(pdfFilePath);
            writer.println(pdfFolderPath);
            writer.println(pagePath);
            writer.println(pngFolderPath);
            writer.println(dataFolderPath);
            writer.println(allQuestions);
            writer.println(questionsMaxPoints);
            writer.println(questionsNumber);
            writer.println(selectedDirectory);
            writer.println(examType);
            for(int i = 0; i < openQR.size(); i++){
                writer.print(openQR.get(i).examName + "$");
                writer.print(openQR.get(i).examDate + "$");
                writer.print(openQR.get(i).examType + "$");
                writer.print(openQR.get(i).examPageNumber + "$");
                writer.print(openQR.get(i).studentName + "$");
                writer.print(openQR.get(i).studentID + "$");
                writer.print(openQR.get(i).questions);
                writer.print("@");
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
        
    

    private void storeStudentTotalGrade() {
        for (int i = 0; i < openQR.size(); i++) {
            String key = openQR.get(i).studentName;
            if (!map.containsKey(key)) {
                map.put(key, 0);
            }
        }
    }

    private void setPage() {
        String[] part = openQR.get(currentQR).questions.get(currentQuestion).split("\\.");
        String studentID = String.valueOf(openQR.get(currentQR).studentID);
        studentNameText.setText(openQR.get(currentQR).studentName);
        studentIDText.setText(studentID);
        questionText.setText(part[1]);
        int numberX = Integer.parseInt(part[1]);
        if(numberOfQuestion<numberX){
            numberOfQuestion = numberX;
        }
        maxGradePromptText.setPromptText("Max : " + part[2]);
        sumOfGradeText.setText(String.valueOf(map.get(openQR.get(currentQR).studentName)));
        
        if(currentPosition < questionsPoints.size()){
            questPointText.setText(String.valueOf(questionsPoints.get(currentPosition)));
        }else{
            questPointText.setText("-");
        }

        Image newImageView = new Image("file:" + allQuestions.get(currentPosition));
        questionImage.setImage(newImageView);

        maxGradePromptText.setText(null);
    }

    private void setGradeScreen() {
        String[] part = openQR.get(currentQR).questions.get(currentQuestion).split("\\.");
        int studentCurrentGrade = map.get(openQR.get(currentQR).studentName);
        studentCurrentGrade += Integer.parseInt(maxGradePromptText.getText());
        map.put(openQR.get(currentQR).studentName, studentCurrentGrade);
    }

    private static boolean regExCheck(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
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
                System.out.println("page" + (page + 1) + " finished...");
            }
        }

    }

    private void savePng(String outputPngPath) throws IOException {
        int i = 1;
        String pathX = null;
        for (File fileEntry : pagePath) {
            if (fileEntry.isDirectory()) {
            } else {
                BufferedImage img = ImageIO.read(new File(fileEntry.getAbsolutePath()));
                BufferedImage croppedImage = img.getSubimage(1779, 10, 700, 600);
                if (isWindows()) {
                    pathX = outputPngPath + "\\subImage" + "-" + i + ".png";
                } else if (isMac()) {
                    pathX = outputPngPath + "/subImage" + "-" + i + ".png";
                } else {
                    System.out.println("Error");
                }
                File outputPath = new File(pathX);
                ImageIO.write(croppedImage, "png", outputPath);
                openQR.add(new QRCode(outputPath));
                i++;
            }
        }
    }

    private void control() {
        for (int i = 0; i < openQR.size(); i++) {
            System.out.println("Page : " + pagePath.get(i).getAbsolutePath());
            System.out.println(openQR.get(i).toString());
        }
    }

    private void splitPage() throws IOException {
        for (int i = 0; i < openQR.size(); i++) {
            BufferedImage img = ImageIO.read(new File(pagePath.get(i).getAbsolutePath()));
            int height = img.getHeight();
            int width = img.getWidth();
            if (isWindows()) {
                for (int k = 0; k < openQR.get(i).questions.size(); k++) {
                    String percs[] = openQR.get(i).questions.get(k).split("%");
                    String parts[] = percs[0].split("\\.");
                    String pageNumber = parts[0];
                    int questionNumber = Integer.parseInt(parts[1].trim());
                    questionsNumber.add(questionNumber);
                    int questionMaxPoint = Integer.parseInt(parts[2].trim());
                    questionsMaxPoints.add(questionMaxPoint);
                    float questFloat = Float.parseFloat(percs[1].trim());
                    if (openQR.get(i).examType.equals("M")) {
                        examType = "Midterm";
                    } else {
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
                    if (k == openQR.get(i).questions.size() - 1) {
                        int start = (int) (((float) height / 100) * questFloat);
                        int distance = height - start;
                        BufferedImage croppedImage = img.getSubimage(0, start, width, distance);
                        File outputPath = new File(newPath);
                        ImageIO.write(croppedImage, "png", outputPath);
                    } else {
                        String endPerc[] = openQR.get(i).questions.get(k + 1).split("%");
                        float endQuestFloat = Float.parseFloat(endPerc[1].trim());
                        int start = (int) ((height / 100) * questFloat);
                        int end = (int) ((height / 100) * endQuestFloat);
                        int distance = end - start;
                        BufferedImage croppedImage = img.getSubimage(0, start, width, distance);
                        File outputPath = new File(newPath);
                        ImageIO.write(croppedImage, "png", outputPath);
                    }
                }
            } else if (isMac()) {
                for (int k = 0; k < openQR.get(i).questions.size(); k++) {
                    String percs[] = openQR.get(i).questions.get(k).split("%");
                    String parts[] = percs[0].split("\\.");
                    String pageNumber = parts[0];
                    int questionNumber = Integer.parseInt(parts[1].trim());
                    questionsNumber.add(questionNumber);
                    int questionMaxPoint = Integer.parseInt(parts[2].trim());
                    questionsMaxPoints.add(questionMaxPoint);
                    float questFloat = Float.parseFloat(percs[1].trim());
                    if (openQR.get(i).examType.equals("M")) {
                        examType = "Midterm";
                    } else {
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
                    if (k == openQR.get(i).questions.size() - 1) {
                        int start = (int) (((float) height / 100) * questFloat);
                        int distance = height - start;
                        BufferedImage croppedImage = img.getSubimage(0, start, width, distance);
                        File outputPath = new File(newPath);
                        ImageIO.write(croppedImage, "png", outputPath);
                    } else {
                        String endPerc[] = openQR.get(i).questions.get(k + 1).split("%");
                        float endQuestFloat = Float.parseFloat(endPerc[1].trim());
                        int start = (int) ((height / 100) * questFloat);
                        int end = (int) ((height / 100) * endQuestFloat);
                        int distance = end - start;
                        BufferedImage croppedImage = img.getSubimage(0, start, width, distance);
                        File outputPath = new File(newPath);
                        ImageIO.write(croppedImage, "png", outputPath);
                    }
                }
            } else {
                System.out.println("Error");
            }

        }

        control();
        System.out.println("Success...");
    }
}
