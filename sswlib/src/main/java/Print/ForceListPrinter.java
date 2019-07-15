/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package Print;

import Force.*;
import filehandlers.ImageTracker;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;


public class ForceListPrinter implements Printable {
    public Graphics2D Graphic;
    //private Force[] forces;
    private ArrayList<Force> forces = new ArrayList<Force>();
    private PageFormat format = null;
    private String Title = "Battletech Force Balancer";
    private ImageTracker imageTracker;
    private boolean printLogo = true;

    public int currentX = 0;
    public int currentY = 0;

    public ForceListPrinter(ArrayList<Force> forces, ImageTracker imageTracker){
        this.forces = forces;
        this.imageTracker = imageTracker;
    }

    public ForceListPrinter( ImageTracker imageTracker ){
        this.imageTracker = imageTracker;
    }

    public void AddForces( ArrayList<Force> forces ) {
        this.forces = forces;
    }

    public void AddForce( Force force ) {
        this.forces.add(force);
    }

    public void clearForces() {
        this.forces.clear();
    }

    public int CreateForce( Force force ) {
        this.forces.add(new Force(force));
        return this.forces.size()-1;
    }
    
    public boolean AddGroup( int ForceIndex, Group g ) {
        this.forces.get(ForceIndex).AddGroup(g);
        return (getForcePrintCount() <= 50);
    }

    public void SetTotals( int ForceIndex, Force f ) {
        this.forces.get(ForceIndex).SetTotals(f);
    }

    public int getForcePrintCount() {
        int count = 0;
        for ( Force f : forces ) {
            count += (f.Groups.size() * 2);
            count += f.getUnits().size();
        }
        return count;
    }
    
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphic = (Graphics2D) graphics;
        format = pageFormat;
        Reset();
        Graphic.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        PreparePrint();
        return Printable.PAGE_EXISTS;
    }

    private void PreparePrint() {
        Reset();
        
        //Background Image
        Image Recordsheet = imageTracker.getImage( PrintConsts.BT_LOGO );
        Graphic.drawImage( Recordsheet, 0, 0, 306, 49, null);
        setFont(PrintConsts.BaseFont.deriveFont(Font.BOLD));
        currentY = 16;
        for ( String line : PrintConsts.wrapText(Title, 25, false) ) {
            Graphic.drawString(line, 310, currentY);
            currentY += Graphic.getFont().getSize();
        }
        currentY = 75;

        for ( Force force : forces ) {
            force.RenderPrint(this, imageTracker);
            NewLine();
            NewLine();
        }

        Graphic.setFont( PrintConsts.SmallBoldFont );
        Graphic.drawString(PrintConsts.getCopyright()[0], 100, (int)format.getHeight()-40);
        Graphic.drawString(PrintConsts.getCopyright()[1], 60, (int)format.getHeight()-30);
    }

    public void WriteStr(String line, int changeX) {
        Graphic.drawString(line, currentX, currentY);
        currentX += changeX;
    }

    public void WriteLine() {
        currentY -= 5;
        Graphic.drawLine(0, currentY, (int) format.getImageableWidth(), currentY);
        currentY += 10;
    }

    public void setFont(Font font) {
        Graphic.setFont(font);
    }

    public void NewLine() {
        currentX = 0;
        currentY += 10;
    }

    public void ResetX() {
        currentX = (int) format.getImageableX();
    }

    public void ResetY() {
        currentY = (int) format.getImageableY();
    }

    public void Reset() {
        currentX = 0; //(int) format.getImageableX();
        currentY = 80; //(int) format.getImageableY();
    }

    /**
     * @return the Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @param Title the Title to set
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setPrintLogo(boolean printLogo) {
        this.printLogo = printLogo;
    }

    public boolean PrintLogo() {
        return printLogo;
    }

}
