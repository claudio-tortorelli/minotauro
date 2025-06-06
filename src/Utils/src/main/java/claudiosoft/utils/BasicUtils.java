/**
 * MIT License - 2021
 */
package claudiosoft.utils;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Constants;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import static java.nio.file.Files.newBufferedReader;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Some usefull common utilities
 */
public class BasicUtils {

    public static DateTimeFormatter DT_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    public static DateTimeFormatter T_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static RandomAccessFile lockFis = null;
    private static FileLock fileLock;

    private static long nanoTimeStart;

    public static File getFileFromRes(String resPath) throws IOException {

        URL resUrl = Thread.currentThread().getContextClassLoader().getResource(resPath);
        if (resUrl == null) {
            throw new FileNotFoundException("not found " + resPath);
        }
        String urlStr = resUrl.toString();
        InputStream is = null;
        File tmp = null;
        try {
            if (urlStr.startsWith("jar:")) {
                final JarURLConnection connection = (JarURLConnection) resUrl.openConnection();
                is = connection.getInputStream();
                tmp = File.createTempFile("native", ".tmp");
                java.nio.file.Files.copy(is, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return tmp;
            } else if (urlStr.startsWith("file:")) {
                return new File(resUrl.getFile());
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (tmp != null) {
                tmp.deleteOnExit();
            }
        }
        throw new FileNotFoundException("not found " + resPath);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static Date stringToDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(date);
    }

    public static LocalDate stringToLocalDate(String dateAAAAMMDD) throws DateTimeParseException {
        LocalDate localDate = LocalDate.parse(dateAAAAMMDD, DT_FORMATTER);
        return localDate;
    }

    public static String localDateToString(LocalDate dateAAAAMMDD) throws DateTimeParseException {
        return dateAAAAMMDD.format(DT_FORMATTER);
    }

    public static String nowToString() {
        return nowToString(DT_FORMATTER);
    }

    public static String nowToString(DateTimeFormatter formatter) {
        return LocalDateTime.now().format(formatter);
    }

    public static LocalDate getShiftDateFromNow(int days) {
        return getShiftDate(LocalDate.now(), days);
    }

    public static LocalDate getShiftDate(LocalDate date, int days) {
        return date.plusDays(days);
    }

    public static String timeToString(LocalDateTime dateTime) {
        return dateTime.format(T_FORMATTER); //TODO hour only
    }

    public static String getJarFolder() throws URISyntaxException {
        return new File(BasicUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
    }

    public static String getExtension(File file) throws IOException {
        return getExtension(file.getCanonicalPath());
    }

    public static String getExtension(String filePath) {
        String extension = "";
        int i = filePath.lastIndexOf('.');
        int p = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        if (i > p) {
            extension = filePath.substring(i + 1);
        }
        return extension.trim().toLowerCase();
    }

    public static void testLockFile(File testFile) throws CTException {

        try (RandomAccessFile fis = new RandomAccessFile(testFile, "rw")) {
            FileLock lck = fis.getChannel().tryLock();
            lck.release();
        } catch (Exception ex) {
            throw new CTException("Locked");
        }
        // try further with rename
        String parent = testFile.getParent();
        String rnd = UUID.randomUUID().toString();
        File newName = new File(parent + "/" + rnd);
        if (testFile.renameTo(newName)) {
            newName.renameTo(testFile);
        } else {
            throw new CTException("Locked");
        }
    }

    public static void doLock(File lockFile) throws FileNotFoundException, IOException {
        lockFis = new RandomAccessFile(lockFile, "rw");
        fileLock = lockFis.getChannel().lock();
    }

    public static void doUnlock() throws IOException {
        fileLock.release();
        lockFis.close();
    }

    public static void deleteDirectory(File directoryToBeDeleted) throws CTException {
        if (!directoryToBeDeleted.exists()) {
            return;
        }
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directoryToBeDeleted.delete();
    }

    public static void copyFolder(File source, File destination) throws FileNotFoundException, IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }
            String files[] = source.list();
            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;

            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[Constants.BUFFER_SIZE];

                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } finally {
                closeQuietly(in);
                closeQuietly(out);
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static byte[] getSHA1(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return hash;
    }

    public static byte[] getSHA1(File inputFile) throws NoSuchAlgorithmException, IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
            return getSHA1(fis);
        } finally {
            BasicUtils.closeQuietly(fis);
        }
    }

    public static byte[] getSHA1(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] dataBuffer = new byte[Constants.BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(dataBuffer)) >= 0) {
            digest.update(dataBuffer, 0, bytesRead);
        }
        byte[] hash = digest.digest();
        return hash;
    }

    public static byte[] getSHA256(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return hash;
    }

    public static byte[] getSHA256(File inputFile) throws NoSuchAlgorithmException, IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
            return getSHA256(fis);
        } finally {
            BasicUtils.closeQuietly(fis);
        }
    }

    public static byte[] getSHA256(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] dataBuffer = new byte[Constants.BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(dataBuffer)) >= 0) {
            digest.update(dataBuffer, 0, bytesRead);
        }
        byte[] hash = digest.digest();
        return hash;
    }

    public static String bytesToHex(final byte[] data) {
        final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
        final int l = data.length;
        final char[] hexChars = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            hexChars[j++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
            hexChars[j++] = HEX_DIGITS[0x0F & data[i]];
        }
        return new String(hexChars);
    }

    public static void inputToOutput(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[Constants.BUFFER_SIZE];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    /**
     * Supported OS
     */
    public static enum OS {
        WINDOWS,
        LINUX,
        OSX,
        UNKNOWN
    }

    public static OS getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac") || osName.startsWith("darwin")) {
            return OS.OSX;
        } else if (osName.startsWith("linux")) {
            return OS.LINUX;
        } else if (osName.startsWith("windows")) {
            return OS.WINDOWS;
        }
        return OS.UNKNOWN;
    }

    public static void inheritIO(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                Scanner sc = new Scanner(src);
                while (sc.hasNextLine()) {
                    dest.println(sc.nextLine());
                }
            }
        }).start();
    }

    public static synchronized void startElapsedTime() {
        nanoTimeStart = System.nanoTime();
    }

    public static synchronized long getElapsedTime() {
        return TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - nanoTimeStart);
    }

    // imported from jdk > 1.7
    public static List<String> readAllLines(Path path) throws IOException {
        try (BufferedReader reader = newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<String> result = new ArrayList<>();
            for (;;) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                result.add(line);
            }
            return result;
        }
    }
}
