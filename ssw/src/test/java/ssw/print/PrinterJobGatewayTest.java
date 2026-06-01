package ssw.print;

import Print.PrinterJobGateway;
import components.Mech;
import filehandlers.MechReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PrinterJobGatewayTest {

    @BeforeClass
    public static void useHeadlessAwt() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void printDialogCancellationDoesNotPrint() throws Exception {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(false);
        Printer printer = new Printer(null, gateway);
        printer.AddMech(readBalgaire());

        printer.Print(false);

        assertEquals(1, gateway.pageableCalls);
        assertFalse(gateway.printCalled);
        assertEquals("Cancelled print should leave queued mechs intact", 1, printer.GetMechs().size());
    }

    @Test
    public void printDialogApprovalPrintsAndClearsQueue() throws Exception {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(true);
        Printer printer = new Printer(null, gateway);
        printer.AddMech(readBalgaire());

        printer.Print(false);

        assertEquals(1, gateway.pageableCalls);
        assertTrue(gateway.printCalled);
        assertEquals("Successful print should clear queued mechs", 0, printer.GetMechs().size());
    }

    @Test
    public void printSinglesUsesPrintableGateway() throws Exception {
        TestPrinterJobGateway gateway = new TestPrinterJobGateway(true);
        Printer printer = new Printer(null, gateway);
        printer.AddMech(readBalgaire());

        printer.PrintSingles(false);

        assertEquals(1, gateway.printableCalls);
        assertTrue(gateway.printCalled);
    }

    private Mech readBalgaire() throws Exception {
        return new MechReader().ReadMech(resourceFile("mechs/Balgaire.ssw").getAbsolutePath());
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }

    private static class TestPrinterJobGateway extends PrinterJobGateway {
        private final boolean dialogResult;
        private int pageableCalls;
        private int printableCalls;
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
            pageableCalls++;
        }

        @Override
        public void setPrintable(Printable printable) {
            printableCalls++;
        }

        @Override
        public void print() {
            printCalled = true;
        }
    }
}
