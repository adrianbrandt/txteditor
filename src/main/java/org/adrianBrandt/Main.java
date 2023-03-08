package org.adrianBrandt;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main extends JFrame {
    private JFileChooser fileChooser;
    private JTextArea textArea;
    private File currentFile;
    private JProgressBar progressBar;

    public Main() {
        setTitle("File Editor");

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));

        JPanel panel = new JPanel(new BorderLayout());

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton openButton = new JButton("Open");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(Main.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = reader.readLine();
                        while (line != null) {
                            line = line.replaceAll("[^a-zA-Z0-9\\s]+", "");
                            stringBuilder.append(line).append("\n");
                            line = reader.readLine();
                        }
                        textArea.setText(stringBuilder.toString());
                        currentFile = file;
                        setTitle("File Editor - " + currentFile.getName());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Main.this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile == null) {
                    int returnVal = fileChooser.showSaveDialog(Main.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        currentFile = fileChooser.getSelectedFile();
                        setTitle("File Editor - " + currentFile.getName());
                    }
                }
                if (currentFile != null) {
                    progressBar = new JProgressBar();
                    progressBar.setIndeterminate(true);
                    JPanel progressPanel = new JPanel();
                    progressPanel.add(progressBar);
                    getContentPane().add(progressPanel, BorderLayout.SOUTH);
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentFile), StandardCharsets.UTF_8));
                            textArea.write(writer);
                            writer.close();
                            return null;
                        }

                        @Override
                        protected void done() {
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(100);
                            JOptionPane.showMessageDialog(Main.this, "File saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    };
                    worker.execute();
                }
            }
        });

        JButton printButton = new JButton("Print");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean complete = textArea.print();
                    if (complete) {
                        JOptionPane.showMessageDialog(Main.this, "Printing complete", "Print", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(Main.this, "Printing cancelled", "Print", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(Main.this, "Error printing file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(printButton);
        panel.add(buttonPanel, BorderLayout.NORTH);

        add(panel);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}
