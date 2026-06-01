package Print;

import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

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

    public void print() throws PrinterException {
        job.print();
    }

    public PrinterJob getPrinterJob() {
        return job;
    }
}
