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
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PagePrinter {
    private String jobName = "Skunkwerks Print";
    private boolean DoPrint = true;

    private Book pages = new Book();
    private PrintService service;
    private PrinterJob job = PrinterJob.getPrinterJob();

    public PagePrinter() {

    }

    public PagePrinter( PageFormat page, Printable print ) {
        pages.append(print, page);
    }

    public void Append( PageFormat page, Printable print ) {
        pages.append(print, page);
    }

    public void Clear() {
        pages = new Book();
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
        return pages;
    }

    public void Print() {
        job.setJobName(jobName);

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
        DoPrint = job.printDialog();

        job.setPageable(Preview());
        
        if( DoPrint ) {
            try {
                job.print();
            } catch( PrinterException e ) {
                Media.Messager( e.getMessage() );
            }
        }
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
