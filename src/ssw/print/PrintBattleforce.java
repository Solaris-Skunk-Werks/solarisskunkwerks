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

import java.awt.Color;
import java.awt.Dimension;
import ssw.gui.frmMain;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import ssw.battleforce.BattleForce;
import ssw.battleforce.BattleForceStats;
import ssw.filehandlers.Media;

public class PrintBattleforce implements Printable {
    private frmMain Parent;
    private BattleForce battleforce;
    private Media media = new Media();
    private Graphics2D graphic;
    private Image RecordSheet,
                    Unit;
    private int UnitSize = 4,
                UnitImageHeight = 225;
    private boolean printMechs = true,
                    printLogo = true;

    private int x = 0,
                y = 0;

    public PrintBattleforce( frmMain parent, BattleForce f) {
        Parent = parent;
        battleforce = f;
        RecordSheet = media.GetImage( PrintConsts.BF_IS );
        Unit = media.GetImage( PrintConsts.BF_IS_Unit );
        setType(battleforce.Type);
    }

    public PrintBattleforce( BattleForce bf ) {
        this(null, bf);
    }

    public PrintBattleforce() {
        this(null, new BattleForce());
    }

    public void Add( BattleForceStats stat ) {
        getBattleforce().BattleForceStats.add(stat);
    }

    public void setRecordSheet( String sheet ) {
        RecordSheet = media.GetImage(sheet);
    }
    
    public void setUnitSheet( String item ) {
        Unit = media.GetImage( item );
    }

    public void setType( String Type ) {
        if ( Type.equals(BattleForce.InnerSphere) ) {
            setInnerSphere();
        } else if ( Type.equals(BattleForce.Clan) ) {
            setClan();
        } else {
            setComstar();
        }
    }

    public void setInnerSphere() {
        RecordSheet = media.GetImage(PrintConsts.BF_IS);
        Unit = media.GetImage( PrintConsts.BF_IS_Unit);
        UnitSize = 4;
        UnitImageHeight = 225;
    }

    public void setClan() {
        RecordSheet = media.GetImage(PrintConsts.BF_CL);
        Unit = media.GetImage( PrintConsts.BF_CL_Unit);
        UnitSize = 5;
        UnitImageHeight = 360;
    }

    public void setComstar() {
        RecordSheet = media.GetImage(PrintConsts.BF_CS);
        Unit = media.GetImage( PrintConsts.BF_CS_Unit);
        UnitSize = 6;
        UnitImageHeight = 323;
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if( RecordSheet == null) { return Printable.NO_SUCH_PAGE; }
        ((Graphics2D) graphics).translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        graphic = (Graphics2D) graphics;
        Render();
        return Printable.PAGE_EXISTS;
    }
    
    public void Render() {
        x = 20;
        y = 91;
        int Groups = 1,
            PointTotal = 0;
        int y2 = 0;
        boolean groupChanged = false;

        graphic.drawImage( RecordSheet, 0, 0, 576, 756, null );
        graphic.drawImage( Unit, 0, 67, 576, UnitImageHeight, null);

        if ( !battleforce.LogoPath.isEmpty() && printLogo ) {
            Image icon = media.GetImage(getBattleforce().LogoPath);
            Dimension d = media.reSize(icon, 50, 50);
            graphic.drawImage(icon, 300, 5, d.width, d.height, null);
        }


        //Print the Unit Name at the top of the sheet
        graphic.setFont( PrintConsts.TitleFont );

        if ( getBattleforce().ForceName.isEmpty() ) { battleforce.ForceName = getBattleforce().Type; }
        String title = getBattleforce().ForceName;
        String[] pTitle = new String[]{"", "", "Record Sheet"};
        int titleY = 23;

        
        if ( title.length() <= 20 ) {
            pTitle[0] = title;
            pTitle[1] = "Record Sheet";
            pTitle[2] = "";
            titleY = 30;
        } else if ( title.length() > 20 && title.length() <= 40 ) {
            pTitle[0] = title.substring(0, title.lastIndexOf(" ", 20) );
            title = title.replace(pTitle[0], "").trim();
            pTitle[1] = title;
            titleY = 20;
            //graphic.setFont( PrintConsts.TitleFont.deriveFont( 10 ) );
        } else {
            pTitle[0] = title.substring(0, title.lastIndexOf(" ", 20) );
            title = title.replace(pTitle[0], "").trim();
            int nextIndex = 20;
            if ( title.length() < nextIndex ) { nextIndex = title.length(); }
            pTitle[1] = title.substring(0, title.lastIndexOf(" ", nextIndex) );
            title = title.replace(pTitle[1], "").trim();
            pTitle[2] = title;
            titleY = 20;
        }

        for ( int t=0; t < pTitle.length; t++ ) {
            if ( !pTitle[t].isEmpty() ) {
                graphic.drawString(pTitle[t], 360, titleY);
                titleY += 12;
            }
        }

        //Output individual units
        for ( int i=0; i < getBattleforce().BattleForceStats.size(); i++ ) {
            groupChanged = false;
            graphic.setFont( PrintConsts.RegularFont );
            BattleForceStats stats = (BattleForceStats) getBattleforce().BattleForceStats.get(i);

            if ( i == (Groups * UnitSize) ) {
                //Output Group Totals for previous group
                graphic.drawString(PointTotal + "", x+460, y-UnitImageHeight+27);

                graphic.drawImage( Unit, 0, 67 + ( UnitImageHeight * Groups ), 576, UnitImageHeight, null);

                x = 20;
                y = 91 + ( UnitImageHeight * Groups );
                Groups += 1;

                groupChanged = true;
                PointTotal = 0;
            }
            
            if ( groupChanged || i == 0 ) {
                //Force Name
                graphic.drawString(stats.getUnit(), x+49, y-13);
            }

            PointTotal += stats.getPointValue();

            //Unit Name
            graphic.drawString(stats.getElement(), x, y);

            //Image
            if ( !stats.getImage().isEmpty() && printMechs ) {
                Image image = media.GetImage(stats.getImage());
                Dimension d = media.reSize(image, 35d, 33d);
                image.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
                graphic.drawImage(image, x, y, d.width, d.height, null);
            }

            //Movement (MV)
            //graphic.setFont( PrintConsts.BoldFont );
            y2 = y + 25;
            x += 42;
            graphic.drawString(stats.getMovement(), x, y2);

            //Damage Values (S,M,L,E)
            x += 35;
            graphic.drawString(stats.getShort()+"", x, y2);
            x += 32;
            graphic.drawString(stats.getMedium()+"", x, y2);
            x += 35;
            graphic.drawString(stats.getLong()+"", x, y2);
            x += 36;
            graphic.drawString(stats.getExtreme()+"", x, y2);
            x += 30;

            //Weight Class
            graphic.drawString(stats.getWeight()+"", x, y2);

            //Skill
            x += 28;
            graphic.drawString(stats.getSkill()+"", x, y2);

            //Overheat (OV)
            x += 23;
            graphic.drawString(stats.getOverheat()+"", x, y2);

            //Armor
            x += 25;
            y2 -= 11;
            int setX = x;
            for ( int a=0; a < stats.getArmor(); a++ ) {
                graphic.drawOval(x-1, y2, 8, 8);
                x += 9;
            }

            //Internal Structure
            x = setX;
            y2 += 9;
            
            Color curColor = graphic.getColor();
            for ( int s=0; s < stats.getInternal(); s++ ) {
                graphic.setColor(Color.LIGHT_GRAY);
                graphic.fillOval(x-1, y2, 8, 8);
                graphic.setColor(curColor);
                graphic.drawOval(x-1, y2, 8, 8);
                x += 9;
            }

            //Abilities
            graphic.setFont(PrintConsts.SmallFont);
            graphic.drawString(stats.getAbilitiesString(), setX+72, y2+17);

            x = 20;
            y += 49;
        }

        graphic.setFont( PrintConsts.RegularFont );
        //Output Group Totals for previous group
        graphic.drawString(PointTotal + "", x+460, y-UnitImageHeight+27);
    }

    public BattleForce getBattleforce() {
        return battleforce;
    }

    public void setBattleforce(BattleForce battleforce) {
        this.battleforce = battleforce;
    }

    public void setPrintMechs(boolean printMechs) {
        this.printMechs = printMechs;
    }

    public void setPrintLogo(boolean printLogo) {
        this.printLogo = printLogo;
    }

}
