package Print;

import filehandlers.ImageTracker;
import org.junit.Test;

import java.awt.print.Pageable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BFBPrinterTest {

    @Test
    public void previewReturnsForceListAndDeclarationPages() {
        BFBPrinter printer = new BFBPrinter(new ImageTracker());

        assertEquals(2, printer.Preview().getNumberOfPages());
    }

    @Test
    public void printDialogCancellationDoesNotPrint() {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(false);
        BFBPrinter printer = new BFBPrinter(new ImageTracker(), gateway);

        printer.Print();

        assertEquals(1, gateway.pageableCalls);
        assertFalse(gateway.printCalled);
        assertEquals("Battletech Force Balancer", gateway.jobName);
    }

    @Test
    public void printDialogApprovalPrints() {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(true);
        BFBPrinter printer = new BFBPrinter(new ImageTracker(), gateway);

        printer.Print();

        assertEquals(1, gateway.pageableCalls);
        assertTrue(gateway.printCalled);
        assertEquals("Battletech Force Balancer", gateway.jobName);
    }

    private static class TestPrinterJobGateway extends PrinterJobGateway {
        private final boolean dialogResult;
        private int pageableCalls;
        private boolean printCalled;
        private String jobName;

        private TestPrinterJobGateway(boolean dialogResult) {
            this.dialogResult = dialogResult;
        }

        @Override
        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        @Override
        public boolean showPrintDialog() {
            return dialogResult;
        }

        @Override
        public void setPageable(Pageable pageable) {
            pageableCalls++;
        }

        @Override
        public void print() {
            printCalled = true;
        }
    }
}
