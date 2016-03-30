package cu.models;

import javafx.scene.image.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by T on 30/03/2016.
 */
public class PDFRenderer
{
    public PDFRenderer()
    {

    }

    public void createFromQRCode(ArrayList<BufferedImage> listOfQRCodes)
    {
        try
        {

            String fileName = "pdfWithImage.pdf";
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();

            doc.addPage(page);
            //PDImageXObject image = JPEGFactory.createFromImage(doc, listOfQRCodes.get(0));


            //25 115 630
            for(int i = 30; i < 725; i=i+90)
            {
                addImage(doc, page, QRGenerator.generateBufferedQRCode("1", 100, 100), i, i);
            }


            doc.save(fileName);

            doc.close();

        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void addImage(PDDocument document, PDPage pdPage, BufferedImage bufferedImage, int x, int y) throws IOException
    {
        PDImageXObject image = JPEGFactory.createFromImage(document,bufferedImage);
        PDPageContentStream contentStream = new PDPageContentStream(document, pdPage, PDPageContentStream.AppendMode.APPEND, false);
        contentStream.drawImage(image, 0, y);
        contentStream.close();
    }

    public BufferedImage toBufferedImage(Image img)
    {
        if(img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(img,0,0,null);
        graphics2D.dispose();
        return bufferedImage;
    }
}
