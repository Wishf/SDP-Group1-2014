package sdp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by conrad on 21/01/15.
 */
public class SimpleGUI extends JFrame implements ActionListener{
    JPanel panel;
    public static void main(String args[]) {
        new SimpleGUI();
    }

    public SimpleGUI() {
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        add(panel);

        String[] buttons = { "Forward", "Backward", "Kick", "Start", "Stop" };
        for(String button : buttons) {
            addButton(button, button);
        }

        setSize(new Dimension(300, 300));
        setTitle("SDP Group 1 // Simple GUI");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void addButton(String name, String command) {
        JButton button = new JButton(name);
        button.addActionListener(this);
        button.setActionCommand(command);
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
