package de.ferrixx.backup.main;

import de.ferrixx.backup.gui.guiCreator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    public static void zipFolder(String sourceFolder, String zipFolder) {
        try(FileOutputStream fos = new FileOutputStream(zipFolder);
            ZipOutputStream zos = new ZipOutputStream(fos)) {

            final Path sourcePath = Paths.get(sourceFolder, new String[0]);
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) throws IOException {
                    guiCreator.log.setText("Ordner: " + sourcePath.relativize(dir).toString() + " \n" + guiCreator.log.getText());

                    if(!sourcePath.equals(dir)) {
                        zos.putNextEntry(new ZipEntry(sourcePath.relativize(dir).toString() + "/"));
                        zos.closeEntry();
                    }

                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
                    guiCreator.log.setText("Datei: " + sourcePath.relativize(file).toString() + " \n" + guiCreator.log.getText());
                    zos.putNextEntry(new ZipEntry(sourcePath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            guiCreator.log.setText("Es ist ein Fehler aufgetreten! \n" + guiCreator.log.getText());
        } catch (IOException e) {
            e.printStackTrace();
            guiCreator.log.setText("Es ist ein Fehler aufgetreten! \n" + guiCreator.log.getText());
        } ;

    }

}
