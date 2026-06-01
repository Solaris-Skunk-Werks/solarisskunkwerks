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

public class PrintWorkflowTest {

    @Test
    public void previewReturnsRequestBook() {
        PrintRequest request = request("Preview Job");
        PrintWorkflow workflow = new PrintWorkflow(new TestPrinterJobGateway(false));

        assertEquals(1, workflow.preview(request).getNumberOfPages());
    }

    @Test
    public void canceledDialogDoesNotPrint() throws PrinterException {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(false);
        PrintWorkflow workflow = new PrintWorkflow(gateway);

        boolean printed = workflow.print(request("Canceled Job"));

        assertFalse(printed);
        assertEquals("Canceled Job", gateway.jobName);
        assertTrue(gateway.pageableSet);
        assertFalse(gateway.printCalled);
    }

    @Test
    public void approvedDialogPrints() throws PrinterException {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(true);
        PrintWorkflow workflow = new PrintWorkflow(gateway);

        boolean printed = workflow.print(request("Approved Job"));

        assertTrue(printed);
        assertEquals("Approved Job", gateway.jobName);
        assertTrue(gateway.pageableSet);
        assertTrue(gateway.printCalled);
    }

    private static PrintRequest request(String jobName) {
        return PrintRequest.builder(jobName)
                .addPage(new PageFormat(), new NoOpPrintable())
                .build();
    }

    private static class TestPrinterJobGateway extends PrinterJobGateway {
        private final boolean dialogResult;
        private String jobName;
        private boolean pageableSet;
        private boolean printCalled;

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
            pageableSet = true;
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
