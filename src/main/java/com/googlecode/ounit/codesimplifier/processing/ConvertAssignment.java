package com.googlecode.ounit.codesimplifier.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConvertAssignment {

    public static String FS = System.getProperty("file.separator");
    public static String RESULT_SUFFIX = "-res";
    public static String TEACHER = "teacher";
    public static String STUDENTS = "students";
    public static String INTEGER_REGEX = "\\d+";

    public static void main(String[] args) {
        CallListf("/home/urmas/tmp/", "treenode");
    }

    private static void CallListf(String path1, String path2) {
        String resultDirectory = path1 + path2 + RESULT_SUFFIX;
        String studentSaveDirectory = resultDirectory + FS + STUDENTS;
        String teacherSaveDirectory = resultDirectory + FS + TEACHER;

        File result = new File(resultDirectory);
        File teacherResult = new File(teacherSaveDirectory);
        File studentsResult = new File(studentSaveDirectory);

        result.mkdir();
        teacherResult.mkdir();
        studentsResult.mkdir();

        listf(path1 + path2, studentSaveDirectory, teacherSaveDirectory);
    }

    public static List<File> listf(String directoryName, String studentSaveDirectory, String teacherSaveDirectory) {
        File directory = new File(directoryName);
        List<File> resultList = new ArrayList<>();

        // get all the files from a directory
        // http://stackoverflow.com/questions/14676407/list-all-files-in-the-folder-and-also-sub-folders
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                Path p = Paths.get(file.getAbsolutePath());
                File f;
                String[] separated = p.toString().split(FS);
                String userId = separated[separated.length - 2];
                String fileName = p.getFileName().toString();
                // process
                String res = Java2SimpleJava.processFile(file.getAbsolutePath(), "");

                if (userId.matches(INTEGER_REGEX)) {
                    // student
                    f = new File(studentSaveDirectory + FS + userId);
                    f.mkdir();
                    try (PrintWriter out = new PrintWriter(studentSaveDirectory + FS + userId + FS + fileName)) {
                        out.println(res);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ConvertAssignment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (userId.equals(TEACHER)) {
                    // teacher
                    f = new File(teacherSaveDirectory);
                    f.mkdir();
                    try (PrintWriter out = new PrintWriter(teacherSaveDirectory + FS + fileName)) {
                        out.println(res);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ConvertAssignment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    throw new RuntimeException("Unknown folderstructure");
                }
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath(), studentSaveDirectory, teacherSaveDirectory));
            }
        }
        return resultList;
    }

}
