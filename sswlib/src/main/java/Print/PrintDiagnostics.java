package Print;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class PrintDiagnostics {
    private static final String ENABLED_PROPERTY = "ssw.print.diagnostics";
    private static final File LOG_FILE = new File(System.getProperty("java.io.tmpdir"), "SSWPrintDiagnostics.log");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private PrintDiagnostics() {
    }

    public static String getLogPath() {
        return LOG_FILE.getAbsolutePath();
    }

    public static boolean isEnabled() {
        return Boolean.getBoolean(ENABLED_PROPERTY);
    }

    public static void log(String message) {
        if (!isEnabled()) {
            return;
        }
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true));
            try {
                writer.println(DATE_FORMAT.format(new Date()) + " " + message);
            } finally {
                writer.close();
            }
        } catch (IOException ex) {
            System.err.println("Unable to write print diagnostics: " + ex.getMessage());
        }
    }

    public static void logBook(String label, Book book) {
        if (!isEnabled()) {
            return;
        }
        log(label + " pageCount=" + book.getNumberOfPages());
        for (int i = 0; i < book.getNumberOfPages(); i++) {
            PageFormat format = book.getPageFormat(i);
            Printable printable = book.getPrintable(i);
            log(label + " page=" + (i + 1)
                    + " printable=" + printable.getClass().getName()
                    + " width=" + format.getWidth()
                    + " height=" + format.getHeight()
                    + " imageableX=" + format.getImageableX()
                    + " imageableY=" + format.getImageableY()
                    + " imageableWidth=" + format.getImageableWidth()
                    + " imageableHeight=" + format.getImageableHeight());
        }
    }

    public static void logException(String label, Throwable throwable) {
        if (!isEnabled()) {
            return;
        }
        log(label + " exception=" + throwable.getClass().getName() + " message=" + throwable.getMessage());
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true));
            try {
                throwable.printStackTrace(writer);
            } finally {
                writer.close();
            }
        } catch (IOException ex) {
            System.err.println("Unable to write print diagnostics: " + ex.getMessage());
        }
    }
}
