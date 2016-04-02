package cu.models.utilities;

import cu.models.equipment.Equipment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by T on 30/03/2016.
 */
public class PDFRenderer
{
    private PDFont font = PDType1Font.HELVETICA;
    private PDDocument doc;
    private ArrayList<PDPage> pages = new ArrayList<>();

    private int currentWidth = 60;
    private int currentHeight = 665;
    private int counter = 0;
    private int currentPage = 0;

    public PDFRenderer()
    {
        doc = new PDDocument();
    }
    //Could be enhanced by allowing variable image size. :S
    public void createLabelsFromQRCode(List<Equipment> listOfQRCodes)
    {
        try
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
            String fileName = LocalDateTime.now().format(formatter) +".pdf";
            PDPage page = new PDPage();
            pages.add(page);
            doc.addPage(page);

            for(Equipment listOfQRCode : listOfQRCodes)
            {
                addImage(QRGenerator.generateBufferedQRCode(listOfQRCode.getItemID() + "", 100, 100));
                addText(listOfQRCode.getItemID() + " " + listOfQRCode.getItemName());
                adjustAlignment();
            }
            doc.save(fileName);
            doc.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addImage(BufferedImage bufferedImage) throws IOException
    {
        PDImageXObject image = JPEGFactory.createFromImage(doc,bufferedImage);
        PDPageContentStream contentStream = new PDPageContentStream(doc, pages.get(currentPage), PDPageContentStream.AppendMode.APPEND, false);
        contentStream.drawImage(image, currentWidth, currentHeight);
        contentStream.close();
    }
    private void addText(String text) throws IOException
    {
        PDPageContentStream contentStream = new PDPageContentStream(doc, pages.get(currentPage), PDPageContentStream.AppendMode.APPEND, false);
        contentStream.beginText();
        contentStream.setFont(font, 8);
        contentStream.newLineAtOffset(currentWidth+text.length(), currentHeight);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.close();
    }
    private void adjustAlignment()
    {
        //Rows Vs Columns of the labels
        currentWidth+=95;
        if(currentWidth >= 450)
        {
            currentWidth = 60;
            currentHeight-=102;
            counter++;
            if(counter == 7)
            {
                PDPage newPage = new PDPage();
                doc.addPage(newPage);
                pages.add(newPage);
                currentHeight = 665;
                currentPage++;
                counter = 0;
                System.out.println("Page + 1");
            }
        }
    }
}
