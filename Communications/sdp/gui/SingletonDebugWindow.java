package sdp.gui;

import javax.swing.*;

/**
 * Created by conrad on 23/01/15.
 */
public class SingletonDebugWindow {

    private static JTextArea debugInfo;

    public SingletonDebugWindow() {
        if(debugInfo == null) {
            debugInfo = new JTextArea(10,30);
        }
    }

    public void addDebugInfo(String info) {
        debugInfo.append(info + "\n");
    }

    public JTextArea getTextArea() {
        return debugInfo;
    }
}
