package cu.models;
import cu.interfaces.CodeScannerInterface;
import gnu.io.*;
import gnu.io.CommPortIdentifier;
import javafx.application.Platform;
import java.io.*;
import java.util.*;

/**
 * Created by T on 20/03/2016.
 */
public class CodeScannerCOM implements Runnable, SerialPortEventListener
{
    private static ArrayList<CodeScannerInterface> agents = new ArrayList<>();

    private CommPortIdentifier portId;
    private Enumeration portList;
    private InputStream inputStream;
    private SerialPort serialPort;

    public CodeScannerCOM()
    {
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements())
        {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                if (portId.getName().equals("COM5"))
                {
                    //if (portId.getName().equals("/dev/term/a")) {
                }
            }
        }
        try
        {
            serialPort = (gnu.io.SerialPort) portId.open("SimpleReadApp", 2000);
            inputStream = serialPort.getInputStream();
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.setSerialPortParams(9600, gnu.io.SerialPort.DATABITS_8, gnu.io.SerialPort.STOPBITS_1, gnu.io.SerialPort.PARITY_NONE);
        }
        catch (UnsupportedCommOperationException | TooManyListenersException | IOException | PortInUseException e)
        {
            e.printStackTrace();
        }

    }

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
                ArrayList<Byte> eee = new ArrayList<>();
                int a = 0;
                try
                {
                    while (inputStream.available() > 0)
                    {
                        a = inputStream.available();
                    }
                    for (int i = 0; i < a; i++)
                    {
                        eee.add(readBuffer[i]);
                    }
                    System.out.println(eee);
                    byte[] array = new byte[eee.size()];
                    int i = 0;
                    for (Byte current : eee)
                    {
                        array[i] = current;
                        i++;
                    }
                    String s = new String(array);
                    System.out.println(s);
                    if (s.length() > 1)
                    {
                        if (s.charAt(0) == 'Q' && s.charAt(1) == 'R')
                        {
                            s = s.substring(2, s.length());
                            for (Iterator<CodeScannerInterface> codeScannerInterfaceIterator = agents.iterator(); codeScannerInterfaceIterator.hasNext();)
                            {
                                CodeScannerInterface codeScannerInterface = codeScannerInterfaceIterator.next();
                                if(codeScannerInterface != null)
                                {
                                    final String finalS = s;
                                    Platform.runLater(() -> codeScannerInterface.onCodeScanner(finalS));
                                }
                                else
                                {
                                    codeScannerInterfaceIterator.remove();
                                }
                            }
                        }
                    }
                    //(?<=QR).*
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }
}