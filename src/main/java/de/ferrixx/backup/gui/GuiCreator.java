package de.ferrixx.backup.gui;

import de.ferrixx.backup.ftp.ftpUploader;
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

public class guiCreator extends JPanel implements ActionListener {

    JButton openButton, uploadButton;
    JTextField server, username, password;
    JFileChooser fileChooser;
    JLabel serverLabel, usernameLabel, passwordLabel, copyright;
    public static JTextArea log;

    public static JFrame jFrame;

    public static File fileToUpload;
    public static File zipFile;

    public guiCreator() {
        openButton = new JButton("Wähle einen Pfad auf zum Sichern");
        openButton.addActionListener(this);
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        uploadButton = new JButton("Ausgewählten Pfad auf dem Server sichern!");
        uploadButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(uploadButton);
        buttonPanel.setBounds(70,60,500,30);

        serverLabel = new JLabel();
        serverLabel.setText("Server-IP");
        server = new JTextField();
        serverLabel.setBounds(90, 0, 200, 30);
        server.setBounds(10, 25, 200, 30);

        usernameLabel = new JLabel();
        usernameLabel.setText("Benutzername");
        username = new JTextField();
        usernameLabel.setBounds(275, 0, 200, 30);
        username.setBounds(215, 25, 200, 30);

        passwordLabel = new JLabel();
        passwordLabel.setText("Passwort");
        password = new JPasswordField();
        passwordLabel.setBounds(495, 0, 200, 30);
        password.setBounds(420, 25, 200, 30);

        log = new JTextArea(12, 75);
        log.setEditable(false);
        log.setSize(getWidth(), getHeight());
        JScrollPane logScrollPane = new JScrollPane(log);
        JPanel logPanel = new JPanel();
        logPanel.add(logScrollPane);
        logPanel.setBounds(10, 100, 620, 500);

        JLabel copyright = new JLabel();
        copyright.setText("Copyright (c) Justin Ippen 2021 - https://ferrixx.de - Version 1.0.6");
        copyright.setBounds(145, 335, 400, 20);

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
        jFrame = new JFrame();
        jFrame.setTitle("Backup Programm");

        URL url = Main.class.getResource("/logo.png");
        jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(url));

        jFrame.setResizable(false);
        jFrame.setSize(645, 395);
        jFrame.add(new guiCreator());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(jFrame);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        jFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == openButton) {
            File startDirectory = new File("C:/");
            fileChooser.setCurrentDirectory(startDirectory);
            int returnVal = fileChooser.showOpenDialog(this);
            if(returnVal == 0) {
                fileToUpload = fileChooser.getSelectedFile();
                log.setText("Pfad " + fileToUpload.getAbsolutePath() + "\n" + log.getText());
            } else {
                log.setText("Keinen Pfad ausgewählt." + "\n" + log.getText());
            }
        } else if(e.getSource() == uploadButton) {
            try {
                String fileNameOnServer = fileToUpload.getName() + " -- " + Main.localDateTime.format(Main.dateTimeFormatter) + ".zip";
                TimerTask zip = new TimerTask() {
                    @Override
                    public void run() {
                        Zipper.zipFolder(fileToUpload.getAbsolutePath(), "C:/Users/" + System.getProperty("user.name") + "/Documents/" + fileNameOnServer);

                        TimerTask ftp = new TimerTask() {
                            @Override
                            public void run() {
                                File zippedFile = new File("C:/Users/" + System.getProperty("user.name") + "/Documents/" + fileNameOnServer);
                                ftpUploader ftpUploader = new ftpUploader(server.getText(), username.getText(), password.getText(), zippedFile.getAbsolutePath(), fileNameOnServer);
                                ftpUploader.uploadFiles();
                            }
                        };
                        java.util.Timer FTPTimer = new Timer("FTPTimer");
                        long delay = 2000L;
                        FTPTimer.schedule(ftp, delay);
                    }
                };
                java.util.Timer ZIPTimer = new Timer("ZIPTimer");
                long delay = 2000L;
                ZIPTimer.schedule(zip, delay);


            } catch (Exception ex) {
                log.setText("Es ist ein Fehler aufgetreten! \n" + log.getText());
            }
        }
    }
}
