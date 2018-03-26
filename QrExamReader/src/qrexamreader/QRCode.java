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

public class QRCode {

  public static void main(String[] args) throws WriterException, IOException,
      NotFoundException {
    String filePath = "C:\\Users\\MONSTER\\Desktop\\qr.png";
    String charset = "UTF-8"; // or "ISO-8859-1"
    Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
    
    System.out.println("Data read from QR Code: "
        + readQRCode(filePath, charset, hintMap));

  }

  public static String readQRCode(String filePath, String charset, Map hintMap)
      throws FileNotFoundException, IOException, NotFoundException {
    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
        new BufferedImageLuminanceSource(
            ImageIO.read(new FileInputStream(filePath)))));
    Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap,
        hintMap);
    return qrCodeResult.getText();
  }
}
