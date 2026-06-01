/*
Copyright (c) 2008~2009, Justin R. Bengtson (poopshotgun@yahoo.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
    * Neither the name of Justin R. Bengtson nor the names of contributors may
        be used to endorse or promote products derived from this software
        without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package Print;

import filehandlers.Media;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PagePrinter {
    private String jobName = "Skunkwerks Print";
    private boolean DoPrint = true;

    private List<PageEntry> pages = new ArrayList<PageEntry>();
    private PrintService service;
    private PrintWorkflow workflow = new PrintWorkflow();

    public PagePrinter() {

    }

    PagePrinter( PrinterJobGateway job ) {
        this.workflow = new PrintWorkflow(job);
    }

    public PagePrinter( PageFormat page, Printable print ) {
        Append(page, print);
    }

    public void Append( PageFormat page, Printable print ) {
        pages.add(new PageEntry(page, print));
    }

    public void Clear() {
        pages.clear();
    }

    public void selectService( String printerName ) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : services ) {
            if ( printer.getName().equals(printerName) ) {
                service = printer;
            }
        }
    }

    public void selectService( PrintService printerName ) {
        service = printerName;
    }
    
    public Book Preview() {
        return workflow.preview(buildRequest());
    }

    public void Print() {
        PrintRequest request = buildRequest();

        //Setting to null so that it will ask the user for now...was having an error when I used the dropdown choice
        /*
        service = null;
        if ( service != null ) {
            try {
                job.setPrintService(service);
            } catch (PrinterException ex) {
                Media.Messager(ex.getMessage());
            }
        } else {
            DoPrint = job.printDialog();
        }
        */
        try {
            DoPrint = workflow.print(request);
        } catch( PrinterException e ) {
            Media.Messager( e.getMessage() );
        }
    }

    public void PrintPagesSeparately() {
        try {
            DoPrint = workflow.printPagesSeparately(buildRequest());
        } catch( PrinterException e ) {
            Media.Messager( e.getMessage() );
        }
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    PrintRequest buildRequest() {
        PrintRequest.Builder request = PrintRequest.builder(printJobName());
        for (PageEntry page : pages) {
            request.addPage(page.pageFormat, page.printable);
        }
        return request.build();
    }

    private String printJobName() {
        if (jobName == null || jobName.trim().isEmpty()) {
            return "Skunkwerks Print";
        }
        return jobName.trim();
    }

    private static final class PageEntry {
        private final PageFormat pageFormat;
        private final Printable printable;

        private PageEntry(PageFormat pageFormat, Printable printable) {
            this.pageFormat = pageFormat;
            this.printable = printable;
        }
    }
}
