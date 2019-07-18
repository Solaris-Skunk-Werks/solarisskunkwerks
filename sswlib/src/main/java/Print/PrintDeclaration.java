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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

public class PrintDeclaration implements Printable {
    public Graphics2D Graphic;
    private ArrayList<Force> forces = new ArrayList<Force>();
    private ArrayList<FireChit> units = new ArrayList<FireChit>();
    private PageFormat format = null;
    private ImageTracker imageTracker;
    private String[] Types = new String[]{"  Primary", "Secondary", "Secondary"};
    private String Title = "Fire Declaration Markers";
    private boolean PrintUnitWarrior = true,
                    PrintUnitLogo = true;

    public int currentX = 0;
    public int currentY = 0;

    public PrintDeclaration( ImageTracker imageTracker ) {
        this.imageTracker = imageTracker;
    }

    public PrintDeclaration( ArrayList<Force> forces, ImageTracker imageTracker ) {
        this(imageTracker);
        this.forces = forces;
    }

    public PrintDeclaration( ImageTracker imageTracker, boolean PrintWarrior, boolean PrintLogo ) {
        this(imageTracker);
        this.PrintUnitWarrior = PrintWarrior;
        this.PrintUnitLogo = PrintLogo;
    }

    public void printUnitWarrior( boolean value ) {
        PrintUnitWarrior = value;
    }

    public void printUnitLogo( boolean value ) {
        PrintUnitLogo = value;
    }

    public int UnitCount() {
        return units.size();
    }

    public boolean IsFull() {
        if ( units.size() >= 24 ) return true;
        return false;
    }

    public boolean IsEmpty() {
        if ( units.size() == 0 ) return true;
        return false;
    }

    public void AddUnit( Group g, Unit u ) {
        units.add(new FireChit(g, u));
    }

    public void AddForces( ArrayList<Force> forces ) {
        for (Force f : forces)
        {
            AddForce(f);
        }
        //this.forces = forces;
    }

    public void AddForce( Force force ) {
        for ( Group g : force.Groups ) {
            for ( Unit u : g.getUnits() ) {
                units.add(new FireChit(g, u));
            }
        }
        //this.forces.add(force);
    }

    public void clearForces() {
        forces.clear();
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if( units.isEmpty() ) { return Printable.NO_SUCH_PAGE; }
        Graphic = (Graphics2D) graphics;
        format = pageFormat;
        Reset();
        Graphic.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        PreparePrint();
        return Printable.PAGE_EXISTS;
    }

    private void PreparePrint() {
        Reset();
        int shift = 0;
        for ( FireChit c : units )
        {
            Image logo = imageTracker.getImage(c.LogoPath);
            for (int k=0; k<Types.length; k++) {
                shift = 5;
                if ( PrintUnitLogo ) {
                    if ( logo != null && logo.getWidth(null) > 0 ) {
                        Graphic.drawImage(logo, currentX+1, currentY-10, 25, 25, null);
                        shift = 30;
                    }
                }
                Graphic.setFont(PrintConsts.SmallFont);
                Graphic.drawString(c.unit.TypeModel, currentX+shift, currentY);
                if ( PrintUnitWarrior ) Graphic.drawString((c.GroupName + " (" + c.unit.getMechwarrior() + ")").replace("()", ""), currentX+shift, currentY+10);
                Graphic.drawRect(currentX, currentY-12, 175, 30);
                Graphic.setFont(PrintConsts.BoldFont);
                Graphic.drawString(Types[k], currentX+120, currentY+10);

                currentX += 175;
            }
            currentX = (int) format.getImageableX();
            currentY += 30;
        }
    }

    public void Reset() {
        currentX = (int) format.getImageableX();
        currentY = (int) format.getImageableY();
    }

    public static class FireChit
    {
        public String LogoPath = "",
                        GroupName = "";
        public Unit unit;

        public FireChit(Group g, Unit u) {
            LogoPath = g.getLogo();
            GroupName = g.getName();
            unit = u;
        }
    }
}
