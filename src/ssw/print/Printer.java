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

package ssw.print;

import java.util.Vector;
import java.awt.print.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import ssw.Force.Force;
import ssw.battleforce.BattleForce;
import ssw.utilities.CommonTools;
import ssw.components.Mech;
import ssw.filehandlers.Media;
import ssw.gui.*;

public class Printer {
    private frmMain Parent;
    private Vector Mechs = new Vector();
    private BattleForce battleforce;
    private PrintBattleforce printForce;
    private String jobName = "SSW Batch Print",
                    logoPath = "",
                    MechImagePath = "";
    private Boolean Charts = true,
                    Canon = true,
                    useDialog = true,
                    TRO = false;

    private Book pages = new Book();
    private Paper paper = new Paper();
    private PageFormat page = new PageFormat();
    private PrinterJob job = PrinterJob.getPrinterJob();

    //To convert paper size to pixels is inches / 0.0139 rounded down
    public final static PaperSize Letter = new PaperSize(8.5d, 11d);
    public final static PaperSize A4 = new PaperSize(595, 842, 18, 18, 559, 806);
    public final static PaperSize Legal = new PaperSize(8.5d, 14.0d);

    public Printer() {
        this(null);
    }
    
    public Printer(frmMain p) {
        Parent = p;
        setPaperSize(Letter);
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName.trim();
    }

    public PrinterJob getJob() {
        return job;
    }

    public void setPaperSize(PaperSize s) {
        paper.setSize( s.PaperWidth, s.PaperHeight );
        paper.setImageableArea( s.ImageableX, s.ImageableY, s.ImageableWidth, s.ImageableHeight );
    }

    public Boolean getCharts() {
        return Charts;
    }

    public Vector GetMechs() {
        return Mechs;
    }

    public void setCharts(Boolean Charts) {
        this.Charts = Charts;
        for (int index=0; index <= Mechs.size()-1; index++) {
            PrintMech pm = (PrintMech) Mechs.get(index);
            pm.setCharts(Charts);
        }
    }

    public void setCanon( boolean Canon ) {
        this.Canon = Canon;
        for (int index=0; index <= Mechs.size()-1; index++) {
            PrintMech pm = (PrintMech) Mechs.get(index);
            pm.setCanon(Canon);
        }
    }

    public Boolean getCanon() {
        return Canon;
    }

    public Boolean getTRO() {
        return TRO;
    }

    public void setTRO(Boolean TRO) {
        this.TRO = TRO;
        for (int index=0; index <= Mechs.size()-1; index++) {
            PrintMech pm = (PrintMech) Mechs.get(index);
            pm.setTRO(TRO);
        }
    }

    public void setHexConversion( int Rate ) {
        for (int index=0; index <= Mechs.size()-1; index++) {
            PrintMech pm = (PrintMech) Mechs.get(index);
            pm.SetMiniConversion( Rate );
        }
    }

    public void AddMech(Mech m, String Mechwarrior, int Gunnery, int Piloting, boolean Charts, boolean PilotInfo, boolean AdjBV) {
        double BV = (double) m.GetCurrentBV();
        if (AdjBV) BV = CommonTools.GetAdjustedBV(m.GetCurrentBV(), Gunnery, Piloting);

        PrintMech pm = new PrintMech(m);
        pm.SetPilotData(Mechwarrior, Gunnery, Piloting);
        pm.SetOptions(Charts, PilotInfo, BV);
        pm.setCanon(Canon);
        Mechs.add(pm);
    }

    public void AddMech(Mech m, String Mechwarrior, int Gunnery, int Piloting) {
        AddMech(m, Mechwarrior, Gunnery, Piloting, Charts, true, true);
    }

    public void AddMech(Mech m){
        AddMech(m, "", 4, 5, Charts, true, true);
    }

    public void AddForce( BattleForce f ) {
        battleforce = f;
        printForce = new PrintBattleforce(f);
    }

    public void Clear() {
        Mechs.removeAllElements();
        battleforce.BattleForceStats.removeAllElements();
    }

    public void Print(Mech m) {
        AddMech(m);
        Print();
    }

    public void PrintSingles() {
        if (Mechs.size() == 0) { return;}

        page.setPaper( paper );

        if ( useDialog ) {
            if (Mechs.size() == 1) {
                if ( ! PrintDialog( (PrintMech) Mechs.get(0) ) ) return;
            } else {
                if ( ! BatchDialog() ) return;
            }
        }
        
        for (int index=0; index <= Mechs.size()-1; index++) {
            PrintMech pm = (PrintMech) Mechs.get(index);
            job.setJobName( pm.CurMech.GetFullName().trim() );
            job.setPrintable(pm);
            try {
                job.print();
            } catch (PrinterException ex) {
                Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void Print( boolean useDialog ) {
        this.useDialog = useDialog;
        Print();
    }

    public void Print() {
        //If they didn't provide a mech exit
        if (Mechs.size() == 0) { return; }

        job.setJobName(jobName.trim());

        //Here is where we will show the print dialog, determine if it's a single mech or multiples
        if ( useDialog ) {
            if (Mechs.size() == 1) {
                if ( ! PrintDialog( (PrintMech) Mechs.get(0) ) ) return;
            } else {
                if ( ! BatchDialog() ) return;
            }
        }

        //start building the print objects necessary
        GeneratePrints();

        job.setPageable(pages);
        boolean DoPrint = job.printDialog();
        if( DoPrint ) {
            try {
                job.print();
            } catch( PrinterException e ) {
                System.err.println( e.getMessage() );
                System.out.println( e.getStackTrace() );
            }

            Mechs.clear();
        }
    }

    public Book Preview() {
        GeneratePrints();
        return pages;
    }

    private void GeneratePrints() {
        //reset the book so we don't get extra pages!
        pages = new Book();

        //start building the print objects necessary
        page.setPaper( paper );

        for (int index=0; index <= Mechs.size()-1; index++) {
            PrintMech pm = (PrintMech) Mechs.get(index);
            pages.append(pm, page);
            if (Mechs.size() == 1) job.setJobName(pm.CurMech.GetFullName().trim());
        }

    }

    private Boolean PrintDialog(PrintMech pMech) {
        dlgPrintSavedMechOptions POptions = new dlgPrintSavedMechOptions(Parent, true, pMech);
        POptions.setTitle( "Printing " + pMech.CurMech.GetFullName() );
        POptions.setLocationRelativeTo( Parent );

        POptions.setVisible( true );

        if( ! POptions.Result() ) {
            return false;
        }

        pMech.setPrintPilot(POptions.PrintPilot());
        pMech.setCharts(POptions.PrintCharts());
        pMech.setGunnery(POptions.GetGunnery());
        pMech.setPiloting(POptions.GetPiloting());
        pMech.setMechwarrior(POptions.GetWarriorName());
        pMech.setMechImage(POptions.getImage());
        pMech.setLogoImage(POptions.getLogo());
        pMech.setCanon(POptions.getCanon());
        if ( POptions.UseMiniConversion() ) { pMech.SetMiniConversion( POptions.GetMiniConversionRate() );}

        POptions.dispose();
        return true;
    }

    private Boolean BatchDialog() {
        dlgPrintSavedMechOptions POptions = new dlgPrintSavedMechOptions(Parent, true);
        POptions.setTitle( "Printing Batched Units");
        POptions.setLocationRelativeTo( Parent );

        if ( !this.logoPath.isEmpty() ) {
            POptions.setLogo(new File(this.logoPath));
        }
        
        POptions.setVisible( true );

        if( ! POptions.Result() ) {
            return false;
        }
        
        for ( int m=0; m <= Mechs.size()-1; m++ ) {
            PrintMech pMech = (PrintMech) Mechs.get(m);
            pMech.setPrintPilot(POptions.PrintPilot());
            pMech.setCharts(POptions.PrintCharts());
            pMech.setMechImage(POptions.getImage());
            pMech.setLogoImage(POptions.getLogo());
            pMech.setCanon(POptions.getCanon());
            if ( POptions.UseMiniConversion() ) { pMech.SetMiniConversion( POptions.GetMiniConversionRate() );}
        }

        POptions.dispose();
        return true;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
        if ( !logoPath.isEmpty() ) {
            Media media = new Media();
            for (int index=0; index <= Mechs.size()-1; index++) {
                PrintMech pm = (PrintMech) Mechs.get(index);
                pm.setLogoImage(media.GetImage(logoPath));
            }
        } else {
            for (int index=0; index <= Mechs.size()-1; index++) {
                PrintMech pm = (PrintMech) Mechs.get(index);
                pm.setLogoImage(null);
            }
        }
    }

    public void setMechImagePath(String MechImagePath) {
        this.MechImagePath = MechImagePath;
        if ( !MechImagePath.isEmpty() ) {
            Media media = new Media();
            for (int index=0; index <= Mechs.size()-1; index++) {
                PrintMech pm = (PrintMech) Mechs.get(index);
                pm.setMechImage(media.GetImage(MechImagePath));
            }
        } else {
            for (int index=0; index <= Mechs.size()-1; index++) {
                PrintMech pm = (PrintMech) Mechs.get(index);
                pm.setMechImage(null);
            }
        }
    }

    public void setBattleForceSheet( String Type ) {
        printForce.setType(Type);
    }

    public Book PreviewBattleforce() {
        if ( battleforce.BattleForceStats.size() == 0 ) { return new Book(); }
        pages = new Book();
        page.setPaper( paper );
        pages.append(printForce, page);
        return pages;
    }

    public void PrintBattleforce( boolean useDialog ) {
        this.useDialog = useDialog;
        PrintBattleforce();
    }

    public void PrintBattleforce() {
        if ( battleforce.BattleForceStats.size() == 0 ) { return; }
        pages = new Book();
        page.setPaper( paper );
        job.setJobName(battleforce.ForceName.trim());

        if ( useDialog ) {
            dlgPrintBattleforce pForce = new dlgPrintBattleforce(Parent, true, battleforce);
            pForce.setLocationRelativeTo(Parent);
            pForce.setVisible(true);
            if ( !pForce.Result ) {
                return;
            }

            printForce.setType(pForce.Sheet);
        }

        pages.append(printForce, page);

        job.setPageable(pages);
        boolean DoPrint = job.printDialog();
        if( DoPrint ) {
            try {
                job.print();
            } catch( PrinterException e ) {
                System.err.println( e.getMessage() );
                System.out.println( e.getStackTrace() );
            }
        }
    }
}