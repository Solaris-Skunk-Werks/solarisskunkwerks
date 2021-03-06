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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import battleforce.BattleForce;
import battleforce.BattleForceStats;
import filehandlers.ImageTracker;
import java.awt.Font;
import java.awt.Point;

public class BattleforceCardPrinter implements Printable {
    private BattleForce battleforce;
    private ImageTracker imageTracker;
    private Graphics2D graphic;
    private Image RecordSheet,
                    Unit,
                    Charts;
    private int // UnitSize = 4, // Variable not read anywhere
                UnitImageWidth = 187,
                UnitImageHeight = 234,
                ElementLimit = 2;
    private boolean printMechs = true,
                    printLogo = true,
                    printWarriorData = true,
                    useTerrainMod = false;

    private int x = 0,
                y = 0;

    public BattleforceCardPrinter( BattleForce f, ImageTracker images) {
        battleforce = f;
        imageTracker = images;
        RecordSheet = images.getImage( PrintConsts.BF_BG );
        Unit = images.getImage( PrintConsts.BF_Card );
        Charts = images.getImage( PrintConsts.BF_Chart );
        setType(battleforce.Type);
    }

    public BattleforceCardPrinter(ImageTracker images) {
        this(new BattleForce(), images);
    }

    public void Add( BattleForceStats stat ) {
        getBattleforce().BattleForceStats.add(stat);
    }

    public void setRecordSheet( String sheet ) {
        RecordSheet = imageTracker.getImage(sheet);
    }

    public void setUnitSheet( String item ) {
        Unit = imageTracker.getImage( item );
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
        // UnitSize = 4;// Set but not used
    }

    public void setClan() {
        // UnitSize = 5;// Set but not used
    }

    public void setComstar() {
        // UnitSize = 6; // Set but not used
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if( RecordSheet == null) { return Printable.NO_SUCH_PAGE; }
        x = 0;
        y = (int) pageFormat.getImageableY();
        ((Graphics2D) graphics).translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        graphic = (Graphics2D) graphics;
        Render();
        return Printable.PAGE_EXISTS;
    }

    public void Render() {
        int elementCount = 0;
        Image icon = null;
        Dimension d = new Dimension();

        //Unit Logo
        if ( !battleforce.LogoPath.isEmpty() && printLogo ) {
            icon = imageTracker.getImage(getBattleforce().LogoPath);
            d = imageTracker.media.reSize(icon, 20, 20);
        }

        graphic.setFont( PrintConsts.TitleFont );
        if ( getBattleforce().ForceName.isEmpty() ) { getBattleforce().ForceName = getBattleforce().Type; }
        graphic.drawString(getBattleforce().ForceName + " - " + getBattleforce().BattleForceStats.get(0).getUnit(), x, y);
        graphic.drawString("PV: " + getBattleforce().PointValue(), (UnitImageWidth*3)-48, y);

        y += graphic.getFont().getSize()-10;

        //Charts
        graphic.drawImage( Charts, (UnitImageWidth*2)+2, y, 194, 562, null);

        //Output individual units
        for ( int i=0; i < getBattleforce().BattleForceStats.size(); i++ ) {
            BattleForceStats stats = (BattleForceStats) getBattleforce().BattleForceStats.get(i);

            if ( elementCount == ElementLimit ) {
                elementCount = 0;
                x = 0;
                y += UnitImageHeight;
            }

            graphic.drawImage( Unit, x, y, UnitImageWidth, UnitImageHeight, null);

            elementCount += 1;

            //Image
            if ( printMechs ) {
                if ( stats.getImage().replace("../BFB.Images/No_Image.png", "").isEmpty() )
                    stats.setImage(imageTracker.media.DetermineMatchingImage(stats.getName(), stats.getModel(), stats.getImage()));
                if ( !stats.getImage().isEmpty() ) {
                    Image image = imageTracker.getImage(stats.getImage());
                    Dimension dim = imageTracker.media.reSize(image, 110d, 130d);
                    image.getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH);
                    Point offset = imageTracker.media.offsetImageBottom( new Dimension(110, 140), dim);
                    graphic.drawImage(image, x+10+offset.x, y+58+offset.y, dim.width, dim.height, null);

                    if ( icon != null && printLogo ) {
                        graphic.drawImage(icon, x+105, y+62, d.width, d.height, null);
                    }
                }
            }

            //graphic.setFont( new Font("Verdana", Font.PLAIN, 8) );
            graphic.setFont( PrintConsts.PlainFont );

            //Unit Name
            int offset = y + 55;
            for ( String line : PrintConsts.wrapText(stats.getElement(), 24, false) ) {
                graphic.drawString( line, x+5, offset);
                offset += graphic.getFont().getSize();
            }

            if ( printWarriorData ) {
                //Pilot Name
                graphic.setFont( PrintConsts.SmallFont );
                graphic.drawString( stats.getWarrior(), x+5, offset);

                //Unit
                //graphic.drawString( stats.getUnit(), x+5, y+68);

                graphic.setFont( PrintConsts.PlainFont );
                //Skill
                graphic.drawString(stats.getSkill()+"", x+127, y+218);
            }

            graphic.setFont( PrintConsts.PlainFont );
            //Movement (MV)
            offset = 15;
            if ( stats.getMovement(useTerrainMod).length() > 2 ) { offset -= 5; }
            graphic.drawString(stats.getMovement(useTerrainMod), x+offset, y+218);

            //Damage Values (S,M,L,E)
            graphic.drawString(stats.getShort()+"", x+34, y+218);
            graphic.drawString(stats.getMedium()+"", x+54, y+218);
            graphic.drawString(stats.getLong()+"", x+72, y+218);
            graphic.drawString(stats.getExtreme()+"", x+92, y+218);

            //Weight Class
            graphic.drawString(stats.getWeight()+"", x+111, y+218);

            //Overheat (OV)
            graphic.drawString(stats.getOverheat()+"", x+144, y+218);

            //PV
            graphic.drawString(stats.getPointValue()+"", x+161, y+218);

            //Armor
            int xoffset = 132,
                yoffset = 72,
                indexer = 0;
            for ( int a=0; a < stats.getArmor(); a++ ) {
                if ( indexer == 5 ) { yoffset += 9; xoffset = 132; indexer = 0; }
                graphic.drawOval(x+xoffset, y+yoffset, 8, 8);
                xoffset += 9;
                indexer += 1;
            }

            //Internal Structure
            xoffset = 132;
            yoffset += 10;
            indexer = 0;
            Color curColor = graphic.getColor();
            for ( int s=0; s < stats.getInternal(); s++ ) {
                if ( indexer == 5 ) { yoffset += 9; xoffset = 132; indexer = 0; }
                graphic.setColor(Color.LIGHT_GRAY);
                graphic.fillOval(x+xoffset, y+yoffset, 8, 8);
                graphic.setColor(curColor);
                graphic.drawOval(x+xoffset, y+yoffset, 8, 8);
                xoffset += 9;
                indexer += 1;
            }

            //Abilities
            xoffset = 132;
            yoffset = 130;
            indexer = 0;
            graphic.setFont(new Font("Arial", Font.PLAIN, 5));
            for ( String ability : stats.getAbilities() ) {
                graphic.drawString(ability, x+xoffset, y+yoffset);
                yoffset += graphic.getFont().getSize();
            }

            x += UnitImageWidth + 1;
        }


        graphic.setFont( PrintConsts.RegularFont );
        //Output Group Totals for previous group
        //graphic.drawString(PointTotal + "", x+460, y-UnitImageHeight+27);
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

    public boolean UseTerrain() {
        return useTerrainMod;
    }

    public void setTerrain(boolean useTerrainMod) {
        this.useTerrainMod = useTerrainMod;
    }

    public void setPrintWarriorData(boolean printData ) {
        this.printWarriorData = printData;
    }
}
