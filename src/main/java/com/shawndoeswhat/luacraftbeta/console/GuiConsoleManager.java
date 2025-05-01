package com.shawndoeswhat.luacraftbeta.console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiConsoleManager {
    private static JFrame frame;
    private static JTextArea outputArea;
    private static JTextField inputField;

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null && frame.isShowing()) {
                frame.toFront();
                frame.requestFocus();
                return;
            }

            frame = new JFrame("LuaCraftBeta Management Console");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(600, 400);
            frame.getContentPane().setBackground(Color.decode("#F5F5F5"));

            outputArea = new JTextArea();
            outputArea.setEditable(false);
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            outputArea.setBackground(Color.WHITE);
            outputArea.setForeground(Color.BLACK);
            outputArea.setCaretColor(Color.BLACK);
            JScrollPane scrollPane = new JScrollPane(outputArea);

            inputField = new JTextField();
            inputField.setBackground(Color.WHITE);
            inputField.setForeground(Color.BLACK);
            inputField.setCaretColor(Color.BLACK);
            inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
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

            frame.setLayout(new BorderLayout(10, 10));
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.add(inputField, BorderLayout.SOUTH);

            printToConsole("Welcome to the LuaCraftBeta Management Console!");
            printToConsole("Type 'help' for a list of available commands.");
            printToConsole("\nCommands:");
            printToConsole(" - setplayer [name] : Set an active player.");
            printToConsole(" - clearplayer : Clear the active player.");
            printToConsole(" - loadscript [scriptName] : Load a Lua script.");
            printToConsole(" - remoteload [url] : Load a Lua script from a URL.");
            printToConsole(" - runscript [code] : Run Lua code inline.");
            printToConsole(" - listscripts : List available Lua scripts.");
            printToConsole(" - exit : Close the console.");

            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        });
    }

    public static void showConsole() {
        SwingUtilities.invokeLater(() -> {
            if (frame == null || !frame.isShowing()) {
                launch();
            } else {
                frame.toFront();
                frame.requestFocus();
            }
        });
    }

    public static void printToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            if (outputArea != null) {
                outputArea.append(text + "\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        });
    }

    public static void printError(String text) {
        SwingUtilities.invokeLater(() -> {
            if (outputArea != null) {
                outputArea.append("[Error] " + text + "\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        });
    }

    public static void printInfo(String text) {
        SwingUtilities.invokeLater(() -> {
            if (outputArea != null) {
                outputArea.append("[Info] " + text + "\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        });
    }
}
