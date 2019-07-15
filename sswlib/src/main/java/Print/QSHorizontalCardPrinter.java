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
import java.awt.Point;

public class QSHorizontalCardPrinter implements Printable {
    private BattleForce battleforce;
    private ImageTracker imageTracker;
    private Graphics2D graphic;
    private Image   Background, CardBack;
    private Image[] BFIcons;
    private int UnitImageWidth = PaperSize.getInchesInPixels(3.5),
                UnitImageHeight = PaperSize.getInchesInPixels(2.5),
                HorizontalLimit = 2,
                MaxUnits = 8;

    private boolean printMechs = true,
                    printLogo = true,
                    printWarriorData = true,
                    useTerrainMod = false,
                    printCardBack = false,
                    isBlackandWhite = false;

    private Point defaultPoint = new Point(0, 0);

    private int x = 0,
                y = 0;

    private Color   Shadow = Color.BLACK, //Color.DARK_GRAY,
                    DarkShadow = Color.BLACK,
                    OVColor = new Color(204, 0, 0),
                    PVColor = Color.WHITE,
                    MoveColor = Color.ORANGE,
                    SkillColor = new Color(238, 216, 0),
                    NameColor = Color.ORANGE,
                    PilotColor = Color.ORANGE,
                    SizeColor = new Color(242, 242, 242);

    public QSHorizontalCardPrinter( BattleForce f, ImageTracker images) {
        battleforce = f;
        imageTracker = images;
        Background = images.getImage( PrintConsts.COLOR_HORIZ_QS_CARD );
        CardBack = imageTracker.getImage( PrintConsts.COLOR_HORIZ_QS_CARD_BACK );
    }

    public QSHorizontalCardPrinter(ImageTracker images) {
        this(new BattleForce(), images);
    }

    public void setCardBack( boolean Value ) {
        printCardBack = Value;
        if ( Value )
            BFIcons = new Image[]{ imageTracker.getImage( PrintConsts.BF_ICON_INDUSTRIAL ),
                                    imageTracker.getImage( PrintConsts.BF_ICON_LIGHT ),
                                    imageTracker.getImage( PrintConsts.BF_ICON_MEDIUM ),
                                    imageTracker.getImage( PrintConsts.BF_ICON_HEAVY ),
                                    imageTracker.getImage( PrintConsts.BF_ICON_ASSAULT ) };
    }

    public void setBlackAndWhite() {
        isBlackandWhite = true;
        Background = imageTracker.getImage( PrintConsts.BW_HORIZ_QS_CARD );
        CardBack =imageTracker.getImage( PrintConsts.BW_HORIZ_QS_CARD_BACK );
        OVColor = Color.BLACK;
        PVColor = OVColor;
        MoveColor = OVColor;
        SkillColor = OVColor;
        NameColor = OVColor;
        PilotColor = OVColor;
        SizeColor = OVColor;
        Shadow = Color.WHITE;
        DarkShadow = Color.WHITE;

        if ( printCardBack )
            BFIcons = new Image[]{ imageTracker.getImage( PrintConsts.BF_ICON_INDUSTRIAL_BW ),
                                    imageTracker.getImage( PrintConsts.BF_ICON_LIGHT_BW ),
                                    imageTracker.getImage( PrintConsts.BF_ICON_MEDIUM_BW ),
                                    imageTracker.getImage( PrintConsts.BF_ICON_HEAVY_BW ),
                                    imageTracker.getImage( PrintConsts.BF_ICON_ASSAULT_BW ) };
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        //if( RecordSheet == null) { return Printable.NO_SUCH_PAGE; }
        defaultPoint.x = (int) pageFormat.getImageableX();
        defaultPoint.y = (int) pageFormat.getImageableY();
        x = defaultPoint.x;
        y = defaultPoint.y;
        ((Graphics2D) graphics).translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        graphic = (Graphics2D) graphics;
        Render();
        return Printable.PAGE_EXISTS;
    }
    
    public void Render() {
        int elementCount = 0;
        Image icon = null;
        Dimension d = new Dimension();
        Point p = new Point(0,0);

        //Output individual units
        for ( BattleForceStats stats : getBattleforce().BattleForceStats ) {
            if ( elementCount == HorizontalLimit ) {
                elementCount = 0;
                x = defaultPoint.x;
                y += UnitImageHeight + 1;
            }

            if ( isBlackandWhite ) graphic.drawImage( Background, x, y, UnitImageWidth, UnitImageHeight, null);

            elementCount += 1;

            //Image
            if ( printMechs ) {
                stats.setImage(imageTracker.media.DetermineMatchingImage(stats.getName(), stats.getModel(), stats.getImage()));
                if ( !stats.getImage().isEmpty() ) {
                    Dimension space = new Dimension(0, 0);
                    if ( isBlackandWhite ) {
                        space.setSize(100, 130);
                        p.setLocation(147, 26);
                    } else {
                        space.setSize(78, 98);
                        p.setLocation(158, 45);
                    }

                    Image image = imageTracker.getImage(stats.getImage());
                    Dimension dim = imageTracker.media.reSize(image, space.width, space.height);
                    image.getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH);
                    Point offset = imageTracker.media.offsetImageCenter( space, dim );

                    graphic.drawImage(image, x+p.x+offset.x, y+p.y+offset.y , dim.width, dim.height, null);

                    //graphic.drawRect(x+p.x, y+p.y, space.width, space.height);

                    if ( icon != null && printLogo ) {
                        graphic.drawImage(icon, x+UnitImageWidth-d.width-5, y+25, d.width, d.height, null);
                    }
                }
            }

            if ( !isBlackandWhite ) graphic.drawImage( Background, x, y, UnitImageWidth, UnitImageHeight, null);

            //PV
            PrintConsts.ShadowText( graphic, PrintConsts.SmallBoldFont, PVColor, DarkShadow, stats.getPointValue()+" POINTS", x+206, y+14.50);

            //Unit Name
            PrintConsts.ShadowText( graphic, PrintConsts.SmallBoldFont, NameColor, DarkShadow, stats.getModel(), x+5, y+12 );
            p.setLocation(5, 22);
            for ( String line : PrintConsts.wrapText(stats.getName().toUpperCase(), 24, false) ) {
                PrintConsts.ShadowText( graphic, PrintConsts.OVFont, NameColor, DarkShadow, line, x+p.x, y+p.y);
                p.y += graphic.getFont().getSize();
            }

            if ( printWarriorData ) {
                String Info = "Pilot [Unit, Force]";
                if ( !stats.getWarrior().isEmpty() ) Info = Info.replace("Pilot", stats.getWarrior());
                if ( !stats.getUnit().isEmpty() ) Info = Info.replace("Unit", stats.getUnit());
                if ( !stats.getForceName().isEmpty() ) Info = Info.replace("Force", stats.getForceName());
                Info = Info.replace("Pilot", "").replace("Unit", "").replace("Force", "").replace("[, ]", "").trim();
                if ( Info.trim().startsWith("[, ") )
                    Info = Info.replace("[, ", "").replace("]", "").trim();
                else
                    Info = Info.replace("[, ", "[").replace(", ]", "]").trim();
                //Pilot Name
                PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallBoldFont, NameColor, DarkShadow, Info, x+5, y+p.y-5 );

                //Unit Name
                //PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallBoldFont, NameColor, DarkShadow, (stats.getUnit() + " [" + battleforce.ForceName + "]").replace("[]", ""), x+10, y+p.y+1 );
            
                //Skill
                PrintConsts.ShadowText( graphic, PrintConsts.Small8Font, SkillColor, Shadow, stats.getSkill()+"", x+128.0, (double) y+49.50);
            }

            p.setLocation(37, 49.50);
            //Weight Class
            PrintConsts.ShadowText( graphic, PrintConsts.Small8Font, SizeColor, Shadow, stats.getWeight()+"", x+p.x, (double) y+p.getY());

            //Movement (MV)
            p.setLocation(84.0, 49.50);
            PrintConsts.ShadowText( graphic, PrintConsts.Small8Font, MoveColor, Shadow, stats.getMovement(useTerrainMod), x+p.x, (double) y+p.getY());

            int[] data = {0, 28, 63, 94, 124};
            p.setLocation(p.x, 76.5);

            //Damage Values (S,M,L,E)
            PrintConsts.ShadowText( graphic, PrintConsts.PlainFont, PVColor, Shadow, stats.getShort()+"", x+data[1], (double) y+p.getY());
            PrintConsts.ShadowText( graphic, PrintConsts.PlainFont, PVColor, Shadow, stats.getMedium()+"", x+data[2], (double) y+p.getY());
            PrintConsts.ShadowText( graphic, PrintConsts.PlainFont, PVColor, Shadow, stats.getLong()+"", x+data[3], (double) y+p.getY());
            PrintConsts.ShadowText( graphic, PrintConsts.PlainFont, PVColor, Shadow, stats.getExtreme()+"", x+data[4], (double) y+p.getY());

            //Overheat (OV)
            PrintConsts.ShadowText( graphic, PrintConsts.PlainFont, OVColor, Shadow, stats.getOverheat()+"", x+27, y+93);

            //Armor
            p.setLocation(22, 103);
            for ( int a=0; a < stats.getArmor(); a++ ) {
                PrintConsts.FilledCircle( graphic, Color.BLACK, Color.WHITE, 7, x+p.x, y+p.y);
                p.x += 9;
            }

            //Internal Structure
            p.setLocation(22, p.y+9);
            for ( int s=0; s < stats.getInternal(); s++ ) {
                PrintConsts.FilledCircle( graphic, Color.BLACK, Color.LIGHT_GRAY, 7, x+p.x, y+p.y);
                p.x += 9;
            }

            //Abilities
            p.setLocation(51, 136);
            graphic.setFont( PrintConsts.Small8Font );
            String[] Abilities = PrintConsts.wrapText(stats.getAbilitiesString(), 20, true);
            if ( Abilities.length > 2 ) {Abilities[1] += Abilities[2]; Abilities[2] = "";}
            for ( String ability : Abilities ) {
                graphic.drawString(ability, x+p.x, y+p.y);
                p.x = 16;
                p.y += graphic.getFont().getSize();
            }

            if ( printLogo ) {
                if ( !stats.getLogo().isEmpty() ) {
                    p.x = 148;
                    p.y = 140;
                    Image logo = imageTracker.getImage(stats.getLogo());
                    graphic.drawImage(logo, x+p.x, y+p.y, 15, 15, null);
                }
            }
            
            x += UnitImageWidth + 1;

            if ( printCardBack ) {
                printCardBack( stats );
                elementCount++;
            }
        }

        graphic.setFont( PrintConsts.RegularFont );
        //Output Group Totals for previous group
        //graphic.drawString(PointTotal + "", x+460, y-UnitImageHeight+27);
    }


    private void printCardBack( BattleForceStats stats ) {
        Point p = new Point(0,0);

        graphic.drawImage( CardBack, x, y, UnitImageWidth, UnitImageHeight, null);

        //Unit Name
        PrintConsts.ShadowText( graphic, PrintConsts.SmallBoldFont, NameColor, DarkShadow, stats.getModel(), x+10, y+12);
        p.setLocation(10, 22);
        for ( String line : PrintConsts.wrapText(stats.getName().toUpperCase(), 12, false) ) {
            PrintConsts.ShadowText( graphic, PrintConsts.BoldFont, NameColor, DarkShadow, line, x+p.x, y+p.y);
            p.y += graphic.getFont().getSize();
        }

        graphic.drawImage( BFIcons[stats.getWeight()], x+122, y+8, 54, 28, null);

        graphic.setFont(PrintConsts.SmallBoldFont);
        graphic.drawString("Notes:", x+24, y+54);
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

    public void setPrintWarriorData(boolean printGroup) {
        this.printWarriorData = printGroup;
    }
}
