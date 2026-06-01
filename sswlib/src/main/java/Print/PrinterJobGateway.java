package Print;

import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;

public class PrinterJobGateway {
    private final PrinterJob job;

    public PrinterJobGateway() {
        this(PrinterJob.getPrinterJob());
    }

    public PrinterJobGateway(PrinterJob job) {
        this.job = job;
    }

    public void setJobName(String jobName) {
        job.setJobName(jobName);
    }

    public boolean showPrintDialog() {
        return job.printDialog();
    }

    public void setPageable(Pageable pageable) {
        job.setPageable(pageable);
    }

    public void setPrintable(Printable printable) {
        job.setPrintable(printable);
    }

    public void print() throws PrinterException {
        job.print();
    }

    public PrinterJob getPrinterJob() {
        return job;
    }

    public String getPrintServiceName() {
        PrintService service = job.getPrintService();
        if (service == null) {
            return "(no print service)";
        }
        return service.getName();
    }
}
