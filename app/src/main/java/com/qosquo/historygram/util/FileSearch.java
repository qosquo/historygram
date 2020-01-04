package com.qosquo.historygram.util;

import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSearch {

    public static final List<String> EXTENSIONS = Arrays.asList(
            "jpg",
            "jpeg",
            "png");

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return Returns a list of all directories inside
     */
    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();

        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (listFile.isDirectory()) {
                    pathArray.add(listFile.getAbsolutePath());
                }
            }

            return pathArray;
        }

        return null;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return Returns a list of all files inside
     */
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();

        for (File listFile : listFiles) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(listFile.toString());
            if (listFile.isFile() && EXTENSIONS.contains(extension)) {
                pathArray.add(listFile.getAbsolutePath());
            }
        }

        return pathArray;
    }

}
