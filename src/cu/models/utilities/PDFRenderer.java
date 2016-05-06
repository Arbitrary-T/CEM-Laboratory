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

    private int cursorXPosition = 60;
    private int cursorYPosition = 665;
    private int counter = 0;
    private int currentPage = 0;

    public PDFRenderer()
    {
        doc = new PDDocument();
    }

    /**
     * Generate a pdf file composed of QR codes
     * @param listOfQRCodes the list of equipment to be 'labeled' with QR codes
     */
    public void createLabelsFromQRCode(List<Equipment> listOfQRCodes)
    {
        //Could be enhanced by allowing variable image size. It currently is a static 100 x 100.
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

    /**
     * Adds a QR label as an image into the PDF file.
     * @param bufferedImage the image (label) to be added
     * @throws IOException
     */
    private void addImage(BufferedImage bufferedImage) throws IOException
    {
        PDImageXObject image = JPEGFactory.createFromImage(doc,bufferedImage);
        PDPageContentStream contentStream = new PDPageContentStream(doc, pages.get(currentPage), PDPageContentStream.AppendMode.APPEND, false);
        contentStream.drawImage(image, cursorXPosition, cursorYPosition);
        contentStream.close();
    }

    /**
     * Adds text into the PDF file
     * @param text the text to be added
     * @throws IOException
     */
    private void addText(String text) throws IOException
    {
        PDPageContentStream contentStream = new PDPageContentStream(doc, pages.get(currentPage), PDPageContentStream.AppendMode.APPEND, false);
        contentStream.beginText();
        contentStream.setFont(font, 8);
        contentStream.newLineAtOffset(cursorXPosition +text.length(), cursorYPosition);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.close();
    }

    /**
     * called when an image/text is added, it adjusts the current cursor in the PDF document.
     */
    private void adjustAlignment()
    {
        cursorXPosition +=95;
        if(cursorXPosition >= 450)
        {
            cursorXPosition = 60;
            cursorYPosition -=102;
            counter++;
            if(counter == 7)
            {
                PDPage newPage = new PDPage();
                doc.addPage(newPage);
                pages.add(newPage);
                cursorYPosition = 665;
                currentPage++;
                counter = 0;
            }
        }
    }
}
