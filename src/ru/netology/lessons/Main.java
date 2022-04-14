package ru.netology.lessons;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Main {

    static private final List<String> listNameFile = new ArrayList<>();
    static private final StringBuilder builder = new StringBuilder();
    static void makeDir(String pathDir) {
        File dir = new File(pathDir);
        try {
            builder.append(dir.mkdirs() ? "Стуктура каталогов создана " + dir.getCanonicalPath() + "\n" :
                    "Ошибка при создании структуры каталогов " + dir.getCanonicalPath() +
                            ". (Возможно, каталоги уже существуют).\n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            builder.append(ex.getMessage());
        }
    }
    static void openZip(String pathFileZip, String pathDir) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(pathFileZip))) {
            ZipEntry entry;
            String nameFile;
            while ((entry = zin.getNextEntry()) != null) {
                nameFile = pathDir + entry.getName();
                FileOutputStream fout = new FileOutputStream(nameFile);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
                builder.append("Файл ")
                        .append(entry.getName())
                        .append(" распакован.\n");
                String nameFileCon = getCanonicalFileName(nameFile);
                listNameFile.add(nameFileCon);
                if (checkExistsFile(nameFileCon)) {
                    builder.append("Файл ")
                            .append(nameFileCon)
                            .append(" записан на диск.\n");
                } else {
                    builder.append("По указаному пути ")
                            .append(nameFileCon)
                            .append(" файл отсутствует.\n");
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            builder.append(ex.getMessage());
        }
    }

    static GameProgress openProgress(String pathFileSave) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(pathFileSave);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            builder.append(ex.getMessage());
        }
        return gameProgress;
    }

    static void saveLog(String nameFile) {
        String logOut = builder.toString();
        writeLog(nameFile, logOut);
    }
    static void saveGame(String pathFilesSave) {
        if (GameProgress.listGameProgress.isEmpty()) {
            System.out.println("Нечего записывать в файл. Отсутствуют сохранённые данные.");
        } else {
            for (int i = 0; i < GameProgress.listGameProgress.size(); i++) {
                String nameFile = pathFilesSave + "save" + (i + 1) + ".dat";
                try (FileOutputStream fos = new FileOutputStream(nameFile);
                     ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(GameProgress.listGameProgress.get(i));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    builder.append(ex.getMessage());
                }
                String nameFileCon = getCanonicalFileName(nameFile);
                if (checkExistsFile(nameFileCon)) {
                    builder.append("Файл ")
                            .append(nameFileCon)
                            .append(" записан на диск.\n");
                    listNameFile.add(nameFileCon);
                } else {
                    builder.append("По указаному пути ")
                            .append(nameFileCon)
                            .append(" файл отсутствует.\n");
                }
            }
        }
    }
    static void zipFiles(String pathFileZip) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(pathFileZip))) {
            for (String pathFile : listNameFile) {
                int lastSlash = pathFile.lastIndexOf(File.separator) + 1;
                String nameFile = pathFile.substring(lastSlash);
                FileInputStream fis = new FileInputStream(pathFile);
                ZipEntry entry = new ZipEntry(nameFile);
                zout.putNextEntry(entry);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                zout.write(buffer);
                zout.closeEntry();
                fis.close();
                builder.append("Файл ")
                        .append(pathFile)
                        .append(" добавлен в архив.\n");
            }
            String pathFileZipCon = getCanonicalFileName(pathFileZip);
            if (checkExistsFile(pathFileZipCon)) {
                builder.append("Файл ")
                        .append(pathFileZipCon)
                        .append(" записан на диск.\n");
            } else {
                builder.append("По указаному пути ")
                        .append(pathFileZipCon)
                        .append(" файл отсутствует.\n");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            builder.append(ex.getMessage());
        }
    }
    public static void main(String[] args) {
        GameProgress gameProgress1 = new GameProgress(88, 11, 2, 30);
        GameProgress gameProgress2 = new GameProgress(7, 8, 9, 12);
        GameProgress gameProgress3 = new GameProgress(100, 13, 120, 180.44);
        makeDir("./Games/savegames");
        makeDir("./Games/temp");

        saveGame("./Games/savegames/");
        zipFiles("./Games/savegames/savegame.zip");
        deleteFiles(listNameFile);
        listNameFile.clear();
        openZip("./Games/savegames/savegame.zip", "./Games/savegames/");
        for (String nameFile : listNameFile) {
            System.out.println(openProgress(nameFile).toString());
        }
        saveLog("./Games/temp/temp.txt");
    }
    public static boolean checkExistsFile(String nameFile) {
        File fileOnDisk = new File(nameFile);
        return fileOnDisk.exists();
    }

    public static String getCanonicalFileName(String nameFile) {
        File fileOnDisk = new File(nameFile);
        String nameCanonicalFile = "";
        try {
            nameCanonicalFile = String.valueOf(fileOnDisk.getCanonicalFile());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            builder.append(ex.getMessage());
        }
        return nameCanonicalFile;
    }
    static void deleteFiles(List<String> listNameFile) {
        for (String nameFile : listNameFile) {
            File delFile = new File(nameFile);
            builder.append(delFile.delete() ? "Файл " + nameFile + " удалён.\n" :
                    "Ошибка при удалении файла " + nameFile + ".\n");
        }
    }
    public static void writeLog(String nameFile, String logOut) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nameFile, true))) {
            bw.write(logOut);
            bw.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            builder.append(ex.getMessage());
        }
    }
}