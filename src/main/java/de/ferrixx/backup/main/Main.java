package de.ferrixx.backup.main;

import de.ferrixx.backup.gui.guiCreator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm");
    public static LocalDateTime localDateTime = LocalDateTime.now();

    public static void main(String[] args) {
        guiCreator.createGui();
        guiCreator.log.append("Programm gestartet! - Willkommen! :)");
    }

}
