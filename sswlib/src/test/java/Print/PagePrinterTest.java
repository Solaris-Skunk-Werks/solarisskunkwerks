package Print;

import org.junit.Test;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PagePrinterTest {

    @Test
    public void previewReturnsAppendedPages() {
        PagePrinter printer = new PagePrinter();

        printer.Append(new PageFormat(), new NoOpPrintable());

        assertEquals(1, printer.Preview().getNumberOfPages());
    }

    @Test
    public void buildRequestUsesCurrentJobName() {
        PagePrinter printer = new PagePrinter();
        printer.setJobName("Vehicle Sheet");
        printer.Append(new PageFormat(), new NoOpPrintable());

        PrintRequest request = printer.buildRequest();

        assertEquals("Vehicle Sheet", request.getJobName());
        assertEquals(1, request.getPageCount());
    }

    @Test
    public void printDialogCancellationDoesNotPrint() {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(false);
        PagePrinter printer = new PagePrinter(gateway);
        printer.Append(new PageFormat(), new NoOpPrintable());

        printer.Print();

        assertFalse(gateway.printCalled);
    }

    @Test
    public void emptyJobNameFallsBackToDefault() {
        PagePrinter printer = new PagePrinter();
        printer.setJobName("");
        printer.Append(new PageFormat(), new NoOpPrintable());

        assertEquals("Skunkwerks Print", printer.buildRequest().getJobName());
    }

    @Test
    public void printPagesSeparatelyUsesGatewayPrint() {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(true);
        PagePrinter printer = new PagePrinter(gateway);
        printer.Append(new PageFormat(), new NoOpPrintable());
        printer.Append(new PageFormat(), new NoOpPrintable());

        printer.PrintPagesSeparately();

        assertEquals(2, gateway.printCalls);
        assertTrue(gateway.printCalled);
    }

    private static class TestPrinterJobGateway extends PrinterJobGateway {
        private final boolean dialogResult;
        private boolean printCalled;
        private int printCalls;

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
            printCalls++;
        }
    }

    private static class NoOpPrintable implements Printable {
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            return PAGE_EXISTS;
        }
    }
}
