package com.petukhovsky.jvaluer.util.fs;

import com.petukhovsky.jvaluer.util.UnixUtils;
import com.petukhovsky.jvaluer.util.os.OS;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by Arthur Petukhovsky on 7/4/2016.
 */
public class FilesUtils {

    private final static Logger log = Logger.getLogger(FilesUtils.class.getName());

    public static boolean removeRecursiveForce(Path path) {
        for (int i = 0; i < 20; i++) {
            if (Files.notExists(path)) return true;
            myTryDelete(path);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        log.log(Level.SEVERE, "can't delete THAT -> (" + path.toAbsolutePath().toString() + ")");
        return Files.notExists(path);
    }

    public static boolean assureEmptyDir(Path path) {
        if (Files.exists(path) && Files.isDirectory(path)) {
            return cleanDirectory(path);
        }
        removeRecursiveForce(path);
        if (!Files.exists(path) && forceCreateDirs(path)) {
            return true;
        }
        log.log(Level.SEVERE, "can't make THAT -> (" + path.toAbsolutePath().toString() + ") directory empty");
        return false;
    }

    public static void chmod(Path path, int mode) {
        if (path == null) return;
        path = path.toAbsolutePath();
        if (!OS.isUnix()) {
            chmodJava(path, mode);
            return;
        }
        UnixUtils.chmod(path.toString(), mode);
    }

    private static void chmodJava(Path path, int mode) {
        String s = "0000" + mode;
        s = s.substring(s.length() - 3);
        FilePermissions owner = new FilePermissions(s.charAt(0) - '0');
        FilePermissions other = new FilePermissions(s.charAt(1) - '0').apply(new FilePermissions(s.charAt(2) - '0'));
        owner = owner.apply(other);
        File file = path.toFile();
        file.setReadable(owner.isRead(), owner.isRead() != other.isRead());
        file.setWritable(owner.isWrite(), owner.isWrite() != other.isWrite());
        file.setExecutable(owner.isExecute(), owner.isExecute() != other.isExecute());
    }

    private static void delete(Path path) {
        if (Files.isDirectory(path)) {
            cleanDirectoryOld(path);
            try (Stream<Path> it = Files.list(path)){
                it.forEach(FilesUtils::delete);
            } catch (IOException e) {
                //log.log(Level.WARNING, "", e);
            }
        }
        if (Files.exists(path)) {
            try {
                FileDeleteStrategy.FORCE.delete(path.toFile());
            } catch (IOException e) {
                //log.log(Level.WARNING, "", e);
            }
        }
    }

    private static void cleanDirectoryOld(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (!dir.equals(path)) Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            //log.log(Level.WARNING, "can't clean directory", e);
        }
    }

    private static void myTryDelete(Path path) {
        if (Files.isDirectory(path)) {
            cleanDirectory(path);
        }
        try {
            FileUtils.forceDelete(path.toFile());
        } catch (IOException e) {
        }
        FileUtils.deleteQuietly(path.toFile());
        delete(path);
    }

    private static boolean cleanDirectory(Path path) {
        final boolean[] ok = {true};
        try {
            try(DirectoryStream<Path> it = Files.newDirectoryStream(path)){
                it.forEach(child -> ok[0] &= removeRecursiveForce(child));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok[0];
    }

    private static boolean forceCreateDirs(Path path) {
        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

