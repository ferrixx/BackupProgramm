package de.ferrixx.backup.main;

import de.ferrixx.backup.gui.GuiCreator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    public static void zipFolder(String sourceFolder, String zipFolder) {
        try (FileOutputStream fos = new FileOutputStream(zipFolder);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            final Path sourcePath = Paths.get(sourceFolder);

            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    GuiCreator.log.setText("Ordner: " + sourcePath.relativize(dir).toString() + " \n" + GuiCreator.log.getText());

                    if (!sourcePath.equals(dir)) {
                        zos.putNextEntry(new ZipEntry(sourcePath.relativize(dir).toString() + "/"));
                        zos.closeEntry();
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    GuiCreator.log.setText("Datei: " + sourcePath.relativize(file).toString() + " \n" + GuiCreator.log.getText());
                    zos.putNextEntry(new ZipEntry(sourcePath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            GuiCreator.log.setText("Es ist ein Fehler aufgetreten! \n" + GuiCreator.log.getText());
        }
    }
}
