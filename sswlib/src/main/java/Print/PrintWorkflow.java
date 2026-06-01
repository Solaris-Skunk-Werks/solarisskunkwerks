package Print;

import java.awt.print.Book;
import java.awt.print.PrinterException;

public class PrintWorkflow {
    private final PrinterJobGateway job;

    public PrintWorkflow() {
        this(new PrinterJobGateway());
    }

    public PrintWorkflow(PrinterJobGateway job) {
        this.job = job;
    }

    public Book preview(PrintRequest request) {
        return request.toBook();
    }

    public boolean print(PrintRequest request) throws PrinterException {
        job.setJobName(request.getJobName());
        boolean shouldPrint = job.showPrintDialog();
        job.setPageable(request.toBook());

        if (shouldPrint) {
            job.print();
        }

        return shouldPrint;
    }
}
