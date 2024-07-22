package de.ferrixx.backup.main;

import de.ferrixx.backup.gui.GuiCreator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm");

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        
        try {
            GuiCreator.createGui();
            GuiCreator.log.append("Programm gestartet! - Willkommen! :)");
        } catch (Exception e) {
            System.err.println("Fehler beim Erstellen der GUI: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
