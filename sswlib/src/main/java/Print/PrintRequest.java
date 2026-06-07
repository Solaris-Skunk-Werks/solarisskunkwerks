package Print;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PrintRequest {
    private final String jobName;
    private final List<PrintPage> pages;

    private PrintRequest(String jobName, List<PrintPage> pages) {
        this.jobName = jobName;
        this.pages = Collections.unmodifiableList(new ArrayList<PrintPage>(pages));
    }

    public String getJobName() {
        return jobName;
    }

    public int getPageCount() {
        return pages.size();
    }

    public Book toBook() {
        Book book = new Book();
        for (PrintPage page : pages) {
            book.append(page.printable, page.pageFormat);
        }
        return book;
    }

    public static Builder builder(String jobName) {
        return new Builder(jobName);
    }

    public static final class Builder {
        private final String jobName;
        private final List<PrintPage> pages = new ArrayList<PrintPage>();

        private Builder(String jobName) {
            this.jobName = jobName;
        }

        public Builder addPage(PageFormat pageFormat, Printable printable) {
            pages.add(new PrintPage(pageFormat, printable));
            return this;
        }

        public Builder addBook(Book book) {
            for (int i = 0; i < book.getNumberOfPages(); i++) {
                addPage(book.getPageFormat(i), book.getPrintable(i));
            }
            return this;
        }

        public PrintRequest build() {
            return new PrintRequest(jobName, pages);
        }
    }

    private static final class PrintPage {
        private final PageFormat pageFormat;
        private final Printable printable;

        private PrintPage(PageFormat pageFormat, Printable printable) {
            this.pageFormat = pageFormat;
            this.printable = printable;
        }
    }
}
