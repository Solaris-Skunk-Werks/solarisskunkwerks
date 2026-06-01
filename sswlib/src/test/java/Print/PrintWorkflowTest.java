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
        assertEquals("jobName,dialog,pageable", gateway.events);
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
        assertEquals("jobName,dialog,pageable,print", gateway.events);
        assertTrue(gateway.printCalled);
    }

    @Test
    public void directPrintSkipsDialog() throws PrinterException {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(false);
        PrintWorkflow workflow = new PrintWorkflow(gateway);

        workflow.printDirect(request("Direct Job"));

        assertEquals("Direct Job", gateway.jobName);
        assertTrue(gateway.pageableSet);
        assertFalse(gateway.dialogShown);
        assertEquals("jobName,pageable,print", gateway.events);
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
        private boolean dialogShown;
        private String events = "";

        private TestPrinterJobGateway(boolean dialogResult) {
            this.dialogResult = dialogResult;
        }

        @Override
        public void setJobName(String jobName) {
            this.jobName = jobName;
            addEvent("jobName");
        }

        @Override
        public boolean showPrintDialog() {
            dialogShown = true;
            addEvent("dialog");
            return dialogResult;
        }

        @Override
        public void setPageable(Pageable pageable) {
            pageableSet = true;
            addEvent("pageable");
        }

        @Override
        public void print() {
            printCalled = true;
            addEvent("print");
        }

        private void addEvent(String event) {
            if (!events.isEmpty()) {
                events += ",";
            }
            events += event;
        }
    }

    private static class NoOpPrintable implements Printable {
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            return PAGE_EXISTS;
        }
    }
}
