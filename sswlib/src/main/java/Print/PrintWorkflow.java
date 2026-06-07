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
        PrintDiagnostics.log("print start jobName=" + request.getJobName()
                + " pageCount=" + request.getPageCount()
                + " diagnostics=" + PrintDiagnostics.getLogPath());
        job.setJobName(request.getJobName());
        boolean shouldPrint = job.showPrintDialog();
        PrintDiagnostics.log("print dialog result=" + shouldPrint + " service=" + job.getPrintServiceName());
        Book book = toRasterizedBook(request);
        PrintDiagnostics.logBook("print pageable", book);
        job.setPageable(book);

        if (shouldPrint) {
            try {
                job.print();
                PrintDiagnostics.log("print submitted jobName=" + request.getJobName());
            } catch (PrinterException ex) {
                PrintDiagnostics.logException("print failed jobName=" + request.getJobName(), ex);
                throw ex;
            }
        }

        return shouldPrint;
    }

    public boolean printPagesSeparately(PrintRequest request) throws PrinterException {
        PrintDiagnostics.log("printPagesSeparately start jobName=" + request.getJobName()
                + " pageCount=" + request.getPageCount()
                + " diagnostics=" + PrintDiagnostics.getLogPath());
        job.setJobName(request.getJobName());
        boolean shouldPrint = job.showPrintDialog();
        PrintDiagnostics.log("printPagesSeparately dialog result=" + shouldPrint + " service=" + job.getPrintServiceName());

        if (shouldPrint) {
            Book source = request.toBook();
            for (int i = 0; i < source.getNumberOfPages(); i++) {
                String pageJobName = request.getJobName() + " page " + (i + 1);
                job.setJobName(pageJobName);
                Book book = toRasterizedBook(source, i);
                PrintDiagnostics.logBook("printPagesSeparately pageable jobName=" + pageJobName, book);
                job.setPageable(book);
                try {
                    job.print();
                    PrintDiagnostics.log("printPagesSeparately submitted jobName=" + pageJobName);
                } catch (PrinterException ex) {
                    PrintDiagnostics.logException("printPagesSeparately failed jobName=" + pageJobName, ex);
                    throw ex;
                }
            }
        }

        return shouldPrint;
    }

    public void printDirect(PrintRequest request) throws PrinterException {
        PrintDiagnostics.log("printDirect start jobName=" + request.getJobName()
                + " pageCount=" + request.getPageCount()
                + " service=" + job.getPrintServiceName());
        job.setJobName(request.getJobName());
        Book book = toRasterizedBook(request);
        PrintDiagnostics.logBook("printDirect pageable", book);
        job.setPageable(book);
        try {
            job.print();
            PrintDiagnostics.log("printDirect submitted jobName=" + request.getJobName());
        } catch (PrinterException ex) {
            PrintDiagnostics.logException("printDirect failed jobName=" + request.getJobName(), ex);
            throw ex;
        }
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
