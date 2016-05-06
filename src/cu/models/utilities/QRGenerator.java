package cu.models.utilities;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by T on 26/12/2015.
 */

public class QRGenerator
{

    /**
     * Generates a QR Code Image via the ZXing library
     * @param text contents of the QR code
     * @param w the width of the image
     * @param h the height of the image
     * @return
     */
    public static Image generateQRCode(String text, int w, int h)
    {
        Image imageFromString = null;
        String charset = "UTF-8"; // or "ISO-8859-1"
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try
        {
            BitMatrix matrix = new MultiFormatWriter().encode(new String(text.getBytes("UTF-8"), charset), BarcodeFormat.QR_CODE, w, h, hintMap);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            imageFromString = SwingFXUtils.toFXImage(image, null);
        }
        catch (UnsupportedEncodingException | WriterException e)
        {
            e.printStackTrace();
        }
        return imageFromString;
    }
    /**
     * Generates a QR Code BufferedImage via the ZXing library
     * @param text contents of the QR code
     * @param w the width of the image
     * @param h the height of the image
     * @return
     */
    public static BufferedImage generateBufferedQRCode(String text, int w, int h)
    {
        BufferedImage image = null;
        String charset = "UTF-8"; // or "ISO-8859-1"
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try
        {
            BitMatrix matrix = new MultiFormatWriter().encode(new String(text.getBytes("UTF-8"), charset), BarcodeFormat.QR_CODE, w, h, hintMap);
            image = MatrixToImageWriter.toBufferedImage(matrix);
        }
        catch (UnsupportedEncodingException | WriterException e)
        {
            e.printStackTrace();
        }
        return image;
    }
}
