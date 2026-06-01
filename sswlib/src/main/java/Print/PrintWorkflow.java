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
        job.setPageable(toRasterizedBook(request));

        if (shouldPrint) {
            job.print();
        }

        return shouldPrint;
    }

    public void printDirect(PrintRequest request) throws PrinterException {
        job.setJobName(request.getJobName());
        job.setPageable(toRasterizedBook(request));
        job.print();
    }

    private Book toRasterizedBook(PrintRequest request) {
        Book source = request.toBook();
        Book rasterized = new Book();
        for (int i = 0; i < source.getNumberOfPages(); i++) {
            rasterized.append(new RasterizedPrintable(source.getPrintable(i)), source.getPageFormat(i));
        }
        return rasterized;
    }
}
