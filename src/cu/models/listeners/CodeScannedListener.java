package cu.models.listeners;
import cu.interfaces.CodeScannerInterface;
import cu.models.utilities.PropertiesManager;
import gnu.io.*;
import gnu.io.CommPortIdentifier;
import javafx.application.Platform;
import java.io.*;
import java.util.*;

/**
 * Created by T on 20/03/2016.
 */
public class CodeScannedListener implements Runnable, SerialPortEventListener
{

    private static ArrayList<CodeScannerInterface> agents = new ArrayList<>();
    private CommPortIdentifier portId;
    private InputStream inputStream;
    private SerialPort serialPort;
    private PropertiesManager propertiesManager = new PropertiesManager();

    /**
     * A runnable that handles the QR Code reader, starts by configuring all parameters for the
     */
    public CodeScannedListener()
    {
        try
        {
            portId = CommPortIdentifier.getPortIdentifier(propertiesManager.getProperty("default.port"));
            serialPort = (gnu.io.SerialPort) portId.open("Barcode Listener", 2000);
            inputStream = serialPort.getInputStream();
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.setSerialPortParams(9600, gnu.io.SerialPort.DATABITS_8, gnu.io.SerialPort.STOPBITS_1, gnu.io.SerialPort.PARITY_NONE);
        }
        catch (UnsupportedCommOperationException | TooManyListenersException | IOException | PortInUseException | NoSuchPortException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Activates a interface that is bound to this thread and notifies the 'listeners' of updates
     * @param mainAgent
     */
    public static void activateAgent(CodeScannerInterface mainAgent)
    {
        agents.add(mainAgent);
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(20000);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                serialPort.close();
                System.exit(0);
                return;
            }
        }
    }

    /**
     * listens for serial events i.e. code scans.
     * @param serialPortEvent the port to listen too
     */
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent)
    {
        switch (serialPortEvent.getEventType())
        {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[256];
                ArrayList<Byte> byteArrayList = new ArrayList<>();
                int readInput = 0;
                try
                {
                    while (inputStream.available() > 0)
                    {
                        readInput = inputStream.available();
                        int numBytes = inputStream.read(readBuffer);
                    }
                    for (int i = 0; i < readInput; i++)
                    {
                        byteArrayList.add(readBuffer[i]);
                    }
                    System.out.println(byteArrayList);
                    byte[] array = new byte[byteArrayList.size()];
                    int i = 0;
                    for (Byte current : byteArrayList)
                    {
                        array[i] = current;
                        i++;
                    }
                    String bytesToString = new String(array);
                    System.out.println(bytesToString);
                    if (bytesToString.length() > 1)
                    {
                        if (bytesToString.charAt(0) == 'Q' && bytesToString.charAt(1) == 'R')
                        {
                            bytesToString = bytesToString.substring(2, bytesToString.length());
                            for (Iterator<CodeScannerInterface> codeScannerInterfaceIterator = agents.iterator(); codeScannerInterfaceIterator.hasNext();)
                            {
                                CodeScannerInterface codeScannerInterface = codeScannerInterfaceIterator.next();
                                if(codeScannerInterface != null)
                                {
                                    final String finalS = bytesToString;
                                    Platform.runLater(() -> codeScannerInterface.onCodeScanner(finalS));
                                }
                                else
                                {
                                    codeScannerInterfaceIterator.remove();
                                }
                            }
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }
}