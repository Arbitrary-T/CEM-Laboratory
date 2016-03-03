package cu.models;

import cu.Main;
import cu.interfaces.CodeScannerInterface;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import org.apache.commons.lang3.StringUtils;

import javax.management.timer.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Created by T on 02/03/2016.
 */
public class CodeScanner implements Runnable
{
    private static CodeScannerInterface agent;
    GlobalKeyboardHook keyboardHook;
    private final String scannerPrefix = "././";
    private final String scannerSuffix = ".\\.\\";
    private String keyInput = "";
    String checkEntry;
    public static void activateAgent(CodeScannerInterface mainAgent)
    {
        agent = mainAgent;
    }

    @Override
    public void run()
    {
        keyboardHook = new GlobalKeyboardHook();
        Main.getPrimaryStage().setOnCloseRequest(event -> keyboardHook.shutdownHook());
        keyboardHook.addKeyListener(new GlobalKeyAdapter()
        {
            @Override
            public void keyPressed(GlobalKeyEvent event)
            {
                //We don't want to act as if we are a key logger here yyyaaaaarrrrrrr!
                if(Main.getPrimaryStage().getScene().getWindow().isFocused())
                {
                    if(event.getVirtualKeyCode() == 0x0D) //if enter is pressed
                    {
                        checkEntry = StringUtils.substringBetween(keyInput, scannerPrefix, scannerSuffix);
                        if(checkEntry != null)
                        {
                            agent.onCodeScanner(checkEntry);
                            keyInput = "";
                        }
                        else
                        {
                            System.out.println("Buffer cleared");
                            keyInput = "";
                        }
                    }
                    else
                    {
                        keyInput+=event.getKeyChar();
                    }
                    System.out.println(event.getKeyChar());
                }
            }
        });
    }
}