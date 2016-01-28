package cu.models;

import cu.interfaces.CardInterface;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javax.smartcardio.*;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

public class CardListener implements Runnable
{
    TerminalFactory terminalFactory;
    List<CardTerminal> availableTerminals;
    CardTerminal firstReader;
    Card scannedCard;
    CardChannel cardChannel;
    CommandAPDU getChallenge;
    ResponseAPDU response;
    long pastTime = System.currentTimeMillis() - 2000;
    static CardInterface agent;
    byte[] byteHistory = {0x00};

    public static void activateAgent(CardInterface mainAgent)
    {
        agent = mainAgent;
    }

    @Override
    public void run()
    {
        try
        {
            terminalFactory = TerminalFactory.getInstance("PC/SC", null);
            System.out.println(terminalFactory);

            if(terminalFactory.terminals().list().isEmpty())
            {
                Platform.runLater(() ->
                {
                    boolean firstTime = true;
                    if(firstTime)
                    {
                        Alert notifyCardReader = new Alert(Alert.AlertType.ERROR);
                        notifyCardReader.setTitle("NFC Reader");
                        notifyCardReader.setHeaderText("Error: No NFC reader found.");
                        notifyCardReader.setContentText("Connect an NFC reader to use all of the features.");
                        notifyCardReader.show();
                        firstTime = false;
                    }
                });
            }
            else
            {
                availableTerminals = terminalFactory.terminals().list();
                firstReader = availableTerminals.get(0);
            }

        }
        catch (Exception e)
        {
            System.out.println("Oops! The following error occurred:" + e.toString());
        }

        while(!Thread.currentThread().isInterrupted())
        {
            try
            {
                if (firstReader != null)
                {
                    firstReader.waitForCardPresent(0);
                    scannedCard = firstReader.connect("*");
                    cardChannel = scannedCard.getBasicChannel();
                    getChallenge = new CommandAPDU(new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x04});
                    response = cardChannel.transmit(getChallenge);
                    if ((System.currentTimeMillis() - pastTime) > 3000)
                    {
                        pastTime = System.currentTimeMillis();
                        byteHistory = response.getBytes();
                        agent.onCardScanned(DatatypeConverter.printHexBinary(byteHistory));
                    }
                    Thread.sleep(1000);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}