package de.ferrixx.backup.ftp;

import de.ferrixx.backup.gui.guiCreator;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ftpUploader {

    public String server, username, password, fileName, fileNameOnServer;
    public int port = 21;

    public ftpUploader(String server, String username, String password, String fileName, String fileNameOnServer) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.fileName = fileName;
        this.fileNameOnServer = fileNameOnServer;
    }

    public FTPClient ftpClient = new FTPClient();

    public void uploadFiles() {

        boolean error = false;
        try{
            File fileToUpload = new File(fileName);
            try {
                ftpClient.connect(server);
            } catch(IOException e) {
                guiCreator.log.setText("Es konnte keine Verbindung zum FTP Server hergestellt werden! \n Entweder ist die IP Falsch oder es ist kein FTP-Server vorhanden! \n" + guiCreator.log.getText());
                error = true;
            }

            boolean done = false;
            boolean login = false;

            try {
                login = ftpClient.login(username, password);
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
            }

            if(!login) {
                guiCreator.log.setText("Deine angegebenen Logindaten sind nicht Korrekt! \n" + guiCreator.log.getText());
                error = true;
            } else {
                ftpClient.enterLocalPassiveMode();
            }

            ftpClient.setFileType(2);

            FileInputStream inputStream = new FileInputStream(fileToUpload);

            if(error == false) {
                guiCreator.log.setText("FTP Upload wurde gestartet \n" + guiCreator.log.getText());

                try {
                    done = ftpClient.storeFile(fileNameOnServer, inputStream);
                } catch (IOException e){
                    guiCreator.log.setText("Es ist ein Fehler aufgetreten! - Keine Internet Verbindung \n" + guiCreator.log.getText());
                }
            }

            if(done) {
                File file = new File("C:/Users/" + System.getProperty("user.name") + "/Documents/" + fileNameOnServer);
                file.delete();
                guiCreator.log.setText("FTP Upload war erfolgreich! \n" + guiCreator.log.getText());
            }

            inputStream.close();
        } catch (Exception e) {
            guiCreator.log.setText("Es ist ein Fehler aufgetreten! \n" + guiCreator.log.getText());
            e.printStackTrace();
        } finally {
            try {
                if(ftpClient.isConnected()) ftpClient.logout(); ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                guiCreator.log.setText("Es ist ein Fehler aufgetreten! \n" + guiCreator.log.getText());
            }
        }


    }

}
