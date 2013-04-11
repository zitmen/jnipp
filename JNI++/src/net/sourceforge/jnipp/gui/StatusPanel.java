package net.sourceforge.jnipp.gui;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class StatusPanel extends JScrollPane {

    private JTextArea textArea = new JTextArea("");
    private final PipedInputStream pin = new PipedInputStream();
    private PollStatusThread statusThread = new PollStatusThread(textArea);

    public StatusPanel() {
        super();
        this.setMinimumSize(new Dimension(0, 20));
        textArea.setSize(this.getSize());
        textArea.setEditable(false);
        this.viewport.add(textArea);

        try {
            PipedOutputStream pout = new PipedOutputStream(this.pin);
            System.setOut(new PrintStream(pout));
        } catch (java.io.IOException e) {
            textArea.setText("[error initializing status display stream] : \n" + e.getMessage());
        }

        statusThread = new PollStatusThread(textArea);
        statusThread.start();
    }

    class PollStatusThread extends Thread {

        Runnable runnable;
        String input;

        public PollStatusThread(final JTextArea textWindow) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        textWindow.append(input);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.currentThread().sleep(100);
                    input = this.readLine(pin);
                    SwingUtilities.invokeLater(runnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private synchronized String readLine(PipedInputStream in) throws IOException {
            String input = "";
            do {
                int available = in.available();
                if (available == 0) {
                    break;
                }
                byte b[] = new byte[available];
                in.read(b);
                input = input + new String(b, 0, b.length);
            } while (!input.endsWith("\n") && !input.endsWith("\r\n") && !input.endsWith("\r"));

            return input;
        }
    }

    public static void main(String args[]) {
        //test the control.
        JFrame window = new JFrame();
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        window.setSize(new Dimension(600, 400));
        window.setTitle("Status panel test");

        final JTextField txtInput = new JTextField("test...");
        StatusPanel statusPanel = new StatusPanel();

        JButton cmdGo = new JButton("GO");
        cmdGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(txtInput.getText());
            }
        });

        window.getContentPane().add(txtInput, BorderLayout.NORTH);
        window.getContentPane().add(cmdGo, BorderLayout.EAST);
        window.getContentPane().add(statusPanel, BorderLayout.CENTER);

        System.out.println("this is a test");
        window.show();
        System.out.println("this is a test again");
    }
}
