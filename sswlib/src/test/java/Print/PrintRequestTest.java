package Print;

import org.junit.Test;

import java.awt.Graphics;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import static org.junit.Assert.assertEquals;

public class PrintRequestTest {

    @Test
    public void requestConvertsPagesToBook() {
        PrintRequest request = PrintRequest.builder("Test Job")
                .addPage(new PageFormat(), new NoOpPrintable())
                .addPage(new PageFormat(), new NoOpPrintable())
                .build();

        Book book = request.toBook();

        assertEquals("Test Job", request.getJobName());
        assertEquals(2, request.getPageCount());
        assertEquals(2, book.getNumberOfPages());
    }

    private static class NoOpPrintable implements Printable {
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            return PAGE_EXISTS;
        }
    }
}
