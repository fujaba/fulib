package org.fulib;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Tools {

    public static int javac(String classPath, String outFolder, String sourceFolder) {
        ArrayList<String> args = new ArrayList<>();

        try {
            Files.createDirectories(Paths.get(outFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File source = new File(sourceFolder);

        collectJavaFiles(sourceFolder, args, source);

        args.add("-d");
        args.add(outFolder);
        args.add("-classpath");
        args.add(outFolder + File.pathSeparator + classPath);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        return compiler.run(null, System.out, System.err, args.toArray(new String[0]));
    }



    public static ArrayList<String> collectJavaFiles(String sourceFolder) {
        File source = new File(sourceFolder);
        ArrayList<String> args = new ArrayList<>();
        collectJavaFiles(sourceFolder, args, source);

        return args;
    }



    public static void collectJavaFiles(String sourceFolder, ArrayList<String> args, File source)
    {
        if (source == null) {
            return;
        }


        File[] listFiles = source.listFiles();
        if (listFiles == null) {
            return;
        }

        for (File file : listFiles) {
            if (file.isDirectory()) {
                collectJavaFiles(sourceFolder + "/" + file.getName(), args, file);
            }
            else if (file.getName().endsWith(".java")) {
                args.add(sourceFolder + "/" + file.getName());
            }
        }
    }



    public static int javac(String outFolder, String sourceFolder) {
        final String classPath = System.getProperty("java.class.path");
        return javac(classPath, outFolder, sourceFolder);
    }

    public static void removeDirAndFiles(String toBeDeletedDir) throws IOException {
        Path rootPath = Paths.get(toBeDeletedDir);

        if (!Files.exists(rootPath)) {
            return;
        }

        final List<Path> pathsToDelete = Files.walk(rootPath).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        for (Path path : pathsToDelete) {
            Files.deleteIfExists(path);
        }
    }
}
