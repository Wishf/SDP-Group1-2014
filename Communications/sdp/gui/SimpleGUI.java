package sdp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by conrad on 21/01/15.
 */
public class SimpleGUI extends JFrame implements ActionListener {

    private ArduinoWrapper arduino;
    private JPanel panel;
    private JTextArea debugInfo;

    public static void main(String args[]) {
        ArduinoWrapper arduino = new ArduinoWrapper();
        new SimpleGUI(arduino);

    }

    public SimpleGUI(ArduinoWrapper arduino) {
        this.arduino = arduino;
        arduino.start();

        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        add(panel);

        String[] buttons = { "Forward", "Backward", "Kick", "Start", "Stop" };
        for(String button : buttons) {
            addButton(button, button);
        }

        debugInfo = new JTextArea(10, 30);
        JScrollPane spane = new JScrollPane(debugInfo);
        panel.add(spane);


        setSize(new Dimension(400, 300));
        setTitle("SDP Group 1 // Simple GUI");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void addDebugInfo(String info) {
        debugInfo.append(info);
    }
    private void addButton(String name, String command) {
        JButton button = new JButton(name);
        button.addActionListener(this);
        button.setActionCommand(command);
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        if(command.equals("Start")) {
        }
    }
}
