package de.ferrixx.backup.gui;

import de.ferrixx.backup.ftp.FtpUploader;
import de.ferrixx.backup.main.Main;
import de.ferrixx.backup.main.Zipper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class GuiCreator extends JPanel implements ActionListener {

    JButton openButton, uploadButton;
    JTextField server, username, password;
    JFileChooser fileChooser;
    JLabel serverLabel, usernameLabel, passwordLabel, copyright;
    public static JTextArea log;
    public static JFrame jFrame;
    public static File fileToUpload;

    public GuiCreator() {
        // Initialize buttons and add action listeners
        openButton = new JButton("Wähle einen Pfad zum Sichern");
        openButton.addActionListener(this);
        uploadButton = new JButton("Ausgewählten Pfad auf dem Server sichern!");
        uploadButton.addActionListener(this);

        // Initialize file chooser to select files and directories
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // Create panel for buttons and set layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(uploadButton);
        buttonPanel.setBounds(70, 60, 500, 30);

        // Initialize labels and text fields for server, username, and password
        serverLabel = new JLabel("Server-IP");
        server = new JTextField();
        serverLabel.setBounds(90, 0, 200, 30);
        server.setBounds(10, 25, 200, 30);

        usernameLabel = new JLabel("Benutzername");
        username = new JTextField();
        usernameLabel.setBounds(275, 0, 200, 30);
        username.setBounds(215, 25, 200, 30);

        passwordLabel = new JLabel("Passwort");
        password = new JPasswordField();
        passwordLabel.setBounds(495, 0, 200, 30);
        password.setBounds(420, 25, 200, 30);

        // Initialize log area to display messages
        log = new JTextArea(12, 75);
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);
        JPanel logPanel = new JPanel();
        logPanel.add(logScrollPane);
        logPanel.setBounds(10, 100, 620, 500);

        // Initialize copyright label
        copyright = new JLabel("Copyright (c) Justin Ippen 2021 - https://ferrixx.de - Version 1.0.6");
        copyright.setBounds(145, 335, 400, 20);

        // Add components to the main panel
        add(copyright);
        add(buttonPanel);
        add(serverLabel);
        add(server);
        add(usernameLabel);
        add(username);
        add(passwordLabel);
        add(password);
        add(logPanel);
        setLayout(null);
    }

    public static void createGui() {
        // Create and set up the main frame
        jFrame = new JFrame("Backup Programm");
        URL url = Main.class.getResource("/logo.png");
        if (url != null) {
            jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(url));
        }
        jFrame.setResizable(false);
        jFrame.setSize(645, 395);
        jFrame.add(new GuiCreator());

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(jFrame);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        jFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            handleOpenButton();
        } else if (e.getSource() == uploadButton) {
            handleUploadButton();
        }
    }

    private void handleOpenButton() {
        // Handle file selection
        File startDirectory = new File("C:/");
        fileChooser.setCurrentDirectory(startDirectory);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileToUpload = fileChooser.getSelectedFile();
            log.setText("Pfad " + fileToUpload.getAbsolutePath() + "\n" + log.getText());
        } else {
            log.setText("Keinen Pfad ausgewählt.\n" + log.getText());
        }
    }

    private void handleUploadButton() {
        // Handle file upload
        try {
            String fileNameOnServer = fileToUpload.getName() + " -- " + Main.localDateTime.format(Main.dateTimeFormatter) + ".zip";
            TimerTask zipTask = new TimerTask() {
                @Override
                public void run() {
                    Zipper.zipFolder(fileToUpload.getAbsolutePath(), "C:/Users/" + System.getProperty("user.name") + "/Documents/" + fileNameOnServer);

                    TimerTask ftpTask = new TimerTask() {
                        @Override
                        public void run() {
                            File zippedFile = new File("C:/Users/" + System.getProperty("user.name") + "/Documents/" + fileNameOnServer);
                            FtpUploader ftpUploader = new FtpUploader(server.getText(), username.getText(), password.getText(), zippedFile.getAbsolutePath(), fileNameOnServer);
                            ftpUploader.uploadFiles();
                        }
                    };
                    new Timer("FTPTimer").schedule(ftpTask, 2000L);
                }
            };
            new Timer("ZIPTimer").schedule(zipTask, 2000L);
        } catch (Exception ex) {
            log.setText("Es ist ein Fehler aufgetreten!\n" + log.getText());
            ex.printStackTrace();
        }
    }
}
