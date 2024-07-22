package de.ferrixx.backup.ftp;

import de.ferrixx.backup.gui.GuiCreator;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUploader {

    private String server;
    private String username;
    private String password;
    private String fileName;
    private String fileNameOnServer;
    private int port = 21;
    private FTPClient ftpClient;

    public FtpUploader(String server, String username, String password, String fileName, String fileNameOnServer) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.fileName = fileName;
        this.fileNameOnServer = fileNameOnServer;
        this.ftpClient = new FTPClient();
    }

    public void uploadFiles() {
        boolean error = false;

        try {
            File fileToUpload = new File(fileName);

            // Connect to FTP server
            try {
                ftpClient.connect(server, port);
            } catch (IOException e) {
                GuiCreator.log.setText("Es konnte keine Verbindung zum FTP Server hergestellt werden! \n" +
                        "Entweder ist die IP Falsch oder es ist kein FTP-Server vorhanden! \n" + GuiCreator.log.getText());
                error = true;
            }

            // Login to FTP server
            boolean login = false;
            if (!error) {
                try {
                    login = ftpClient.login(username, password);
                } catch (IOException e) {
                    e.printStackTrace();
                    error = true;
                }

                if (!login) {
                    GuiCreator.log.setText("Deine angegebenen Logindaten sind nicht korrekt! \n" + GuiCreator.log.getText());
                    error = true;
                } else {
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                }
            }

            // Upload file
            if (!error) {
                GuiCreator.log.setText("FTP Upload wurde gestartet \n" + GuiCreator.log.getText());

                try (FileInputStream inputStream = new FileInputStream(fileToUpload)) {
                    boolean done = ftpClient.storeFile(fileNameOnServer, inputStream);
                    if (done) {
                        File file = new File("C:/Users/" + System.getProperty("user.name") + "/Documents/" + fileNameOnServer);
                        file.delete();
                        GuiCreator.log.setText("FTP Upload war erfolgreich! \n" + GuiCreator.log.getText());
                    } else {
                        GuiCreator.log.setText("Es ist ein Fehler beim FTP-Upload aufgetreten. \n" + GuiCreator.log.getText());
                    }
                } catch (IOException e) {
                    GuiCreator.log.setText("Es ist ein Fehler aufgetreten! - Keine Internet Verbindung \n" + GuiCreator.log.getText());
                }
            }
        } catch (Exception e) {
            GuiCreator.log.setText("Es ist ein Fehler aufgetreten! \n" + GuiCreator.log.getText());
            e.printStackTrace();
        } finally {
            // Logout and disconnect from FTP server
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                GuiCreator.log.setText("Es ist ein Fehler aufgetreten! \n" + GuiCreator.log.getText());
            }
        }
    }
}
