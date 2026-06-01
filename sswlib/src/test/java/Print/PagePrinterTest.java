package Print;

import org.junit.Test;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PagePrinterTest {

    @Test
    public void previewReturnsAppendedPages() {
        PagePrinter printer = new PagePrinter();

        printer.Append(new PageFormat(), new NoOpPrintable());

        assertEquals(1, printer.Preview().getNumberOfPages());
    }

    @Test
    public void printDialogCancellationDoesNotPrint() {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(false);
        PagePrinter printer = new PagePrinter(gateway);
        printer.Append(new PageFormat(), new NoOpPrintable());

        printer.Print();

        assertFalse(gateway.printCalled);
    }

    private static class TestPrinterJobGateway extends PrinterJobGateway {
        private final boolean dialogResult;
        private boolean printCalled;

        private TestPrinterJobGateway(boolean dialogResult) {
            this.dialogResult = dialogResult;
        }

        @Override
        public void setJobName(String jobName) {
        }

        @Override
        public boolean showPrintDialog() {
            return dialogResult;
        }

        @Override
        public void setPageable(Pageable pageable) {
        }

        @Override
        public void print() {
            printCalled = true;
        }
    }

    private static class NoOpPrintable implements Printable {
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            return PAGE_EXISTS;
        }
    }
}
