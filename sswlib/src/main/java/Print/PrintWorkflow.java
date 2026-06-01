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

    public boolean printPagesSeparately(PrintRequest request) throws PrinterException {
        job.setJobName(request.getJobName());
        boolean shouldPrint = job.showPrintDialog();

        if (shouldPrint) {
            Book source = request.toBook();
            for (int i = 0; i < source.getNumberOfPages(); i++) {
                job.setJobName(request.getJobName() + " page " + (i + 1));
                job.setPageable(toRasterizedBook(source, i));
                job.print();
            }
        }

        return shouldPrint;
    }

    public void printDirect(PrintRequest request) throws PrinterException {
        job.setJobName(request.getJobName());
        job.setPageable(toRasterizedBook(request));
        job.print();
    }

    private Book toRasterizedBook(PrintRequest request) {
        return toRasterizedBook(request.toBook());
    }

    private Book toRasterizedBook(Book source) {
        Book rasterized = new Book();
        for (int i = 0; i < source.getNumberOfPages(); i++) {
            rasterized.append(new RasterizedPrintable(source.getPrintable(i)), source.getPageFormat(i));
        }
        return rasterized;
    }

    private Book toRasterizedBook(Book source, int pageIndex) {
        Book rasterized = new Book();
        rasterized.append(new RasterizedPrintable(source.getPrintable(pageIndex)), source.getPageFormat(pageIndex));
        return rasterized;
    }
}
