package com.shawnjb.luacraftbeta.console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiConsoleManager {
    private static JTextArea outputArea;
    private static JTextField inputField;

    public static void launch() {
        JFrame frame = new JFrame("LuaCraftBeta Management Console");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(outputArea);

        inputField = new JTextField();
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                inputField.setText("");
                printToConsole("luacraft> " + input);

                try {
                    ConsoleManager.handleCommand(input);
                } catch (Exception ex) {
                    printError("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputField, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void printToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public static void printError(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append("[Error] " + text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public static void printInfo(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append("[Info] " + text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }
} 
