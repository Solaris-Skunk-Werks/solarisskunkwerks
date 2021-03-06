/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Print;

import battleforce.BattleForce;
import battleforce.BattleForceStats;
import filehandlers.ImageTracker;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 *
 * @author gblouin
 */
public class QSCardSheetPrinter implements Printable {
    private BattleForce battleforce;
    private ImageTracker imageTracker;
    private Graphics2D graphic;
    private Image RecordSheet,
                    Unit,
                    Charts;
    private int // UnitSize = 4, // Variable not used
                UnitImageWidth = 164, //187,
                UnitImageHeight = 234,
                ElementLimit = 2;
    private boolean printMechs = true,
                    printLogo = true,
                    printWarriorData = true,
                    useTerrainMod = false;

    private int x = 0,
                y = 0;

    private Color   OVColor = Color.BLACK,
                    PVColor = OVColor,
                    MoveColor = OVColor,
                    SkillColor = OVColor,
                    NameColor = OVColor,
                    PilotColor = OVColor,
                    SizeColor = OVColor,
                    Shadow = Color.WHITE,
                    DarkShadow = Color.WHITE;

    public QSCardSheetPrinter( BattleForce f, ImageTracker images) {
        battleforce = f;
        imageTracker = images;
        RecordSheet = images.getImage( PrintConsts.BF_BG );
        Unit = images.getImage( PrintConsts.BF_Card2 );
        Charts = images.getImage( PrintConsts.BF_Chart2 );
    }

    public QSCardSheetPrinter(ImageTracker images) {
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
        graphic.drawString("PV: " + getBattleforce().PointValue(), (UnitImageWidth*3), y);

        y += graphic.getFont().getSize()-10;

        //Charts
        Dimension cdim = imageTracker.media.reSize(Charts, 247d, 548d);
        Charts.getScaledInstance(cdim.width, cdim.height, Image.SCALE_SMOOTH);
        graphic.drawImage( Charts, (UnitImageWidth*2)+2, y, cdim.width, cdim.height, null);

        //Output individual units
        for ( int i=0; i < getBattleforce().BattleForceStats.size(); i++ ) {
            BattleForceStats stats = (BattleForceStats) getBattleforce().BattleForceStats.get(i);

            if ( elementCount == ElementLimit ) {
                elementCount = 0;
                x = 0;
                y += UnitImageHeight + 1;
            }
            Point p = new Point(x,y);

            graphic.drawImage( Unit, x, y, UnitImageWidth, UnitImageHeight, null);

            elementCount += 1;

            //Image
            //graphic.drawRect(x+15, y+40, 75, 117);

            if ( printMechs ) {
                if ( stats.getImage().replace("../BFB.Images/No_Image.png", "").isEmpty() )
                    stats.setImage(imageTracker.media.DetermineMatchingImage(stats.getName(), stats.getModel(), stats.getImage()));
                if ( !stats.getImage().isEmpty() ) {
                    Image image = imageTracker.getImage(stats.getImage());
                    Dimension dim = imageTracker.media.reSize(image, 75d, 117d);
                    image.getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH);
                    Point offset = imageTracker.media.offsetImageCenter( new Dimension(75, 117), dim);
                    graphic.drawImage(image, x+15+offset.x, y+40+offset.y, dim.width, dim.height, null);

                    if ( icon != null && printLogo ) {
                        graphic.drawImage(icon, x+70, y+40, d.width, d.height, null);
                    }
                }
            }

            //Overheat (OV)
            PrintConsts.ShadowText( graphic, PrintConsts.OVFont, OVColor, Shadow, stats.getOverheat()+"", (double) x+111, (double) y+50);

            //PV
            PrintConsts.ShadowText( graphic, PrintConsts.BoldFont, PVColor, DarkShadow, stats.getPointValue()+" POINTS", x+106, y+12);

            //Unit Name
            PrintConsts.ShadowText( graphic, PrintConsts.SmallBoldFont, NameColor, DarkShadow, stats.getModel(), x+2, y+7);
            p.y = y + 15;
            int CharLimit = 14;
            Font nameFont = PrintConsts.BoldFont;
            if ( PrintConsts.wrapText(stats.getName().toUpperCase(), CharLimit, false).length > 1 ) {
                nameFont = PrintConsts.SmallBoldFont;
                p.y -= 2;
                CharLimit = 18;
                if ( PrintConsts.wrapText(stats.getName().toUpperCase(), CharLimit, false).length > 1 ) {
                    nameFont = PrintConsts.XtraSmallBoldFont;
                    CharLimit = 24;
                }
            }
            for ( String line : PrintConsts.wrapText(stats.getName().toUpperCase(), CharLimit, false) ) {
                PrintConsts.ShadowText( graphic, nameFont, NameColor, DarkShadow, line, x+2, p.y);
                p.y += nameFont.getSize();
            }

            if ( printWarriorData ) {
                //Pilot Name
                String Info = "Unit / Force";
                //if ( !stats.getWarrior().isEmpty() ) Info = Info.replace("Pilot", stats.getWarrior());
                if ( !stats.getUnit().isEmpty() ) Info = Info.replace("Unit", stats.getUnit());
                if ( !stats.getForceName().isEmpty() ) Info = Info.replace("Force", stats.getForceName());
                Info = Info.replace("Pilot", "").replace("Unit", "").replace("Force", "").replace("[, ]", "").trim();
                if ( Info.trim().startsWith("/") || Info.trim().endsWith("/") )
                    Info = Info.replace("/", "").trim();
                p.y = y + 24;
                PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallFont, PilotColor, DarkShadow, stats.getWarrior(), p.x+2, p.y);
                PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallFont, PilotColor, DarkShadow, Info, p.x+2, p.y+5);

                //Skill
                PrintConsts.ShadowText( graphic, PrintConsts.OVFont, SkillColor, Shadow, stats.getSkill()+"", x+38, y+195);
            }

            p.y = 195;

            //Movement (MV)
            p.x = x + 20;
            p.x -= stats.getMovement(useTerrainMod).length() * 2;
            if ( stats.getMovement(useTerrainMod).length() > 3 ) p.x -= 4;
            PrintConsts.ShadowText( graphic, PrintConsts.OVFont, MoveColor, Shadow, stats.getMovement(useTerrainMod), p.x, y+p.y );

            //Weight Class
            PrintConsts.ShadowText( graphic, PrintConsts.OVFont, SizeColor, Shadow, stats.getWeight()+"", x+55, y+p.y);

            int[] data = {70, 84, 104, 125, 144};
            p.y = 188;

            //Damage Values (S,M,L,E)
            PrintConsts.ShadowText( graphic, PrintConsts.SmallBoldFont, PVColor, Shadow, stats.getShort()+"", x+data[1], y+p.y);
            PrintConsts.ShadowText( graphic, PrintConsts.SmallBoldFont, PVColor, Shadow, stats.getMedium()+"", x+data[2], y+p.y);
            PrintConsts.ShadowText( graphic, PrintConsts.SmallBoldFont, PVColor, Shadow, stats.getLong()+"", x+data[3], y+p.y);
            PrintConsts.ShadowText( graphic, PrintConsts.SmallBoldFont, PVColor, Shadow, stats.getExtreme()+"", x+data[4], y+p.y);
            p.y += graphic.getFont().getSize();

            for ( String[] ability : stats.getDamageAbilities() ) {
                PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallBoldFont, PVColor, Shadow, ability[0]+"", x+data[0], y+p.y);
                PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallBoldFont, PVColor, Shadow, ability[1]+"", x+data[1]+1, y+p.y);
                PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallBoldFont, PVColor, Shadow, ability[2]+"", x+data[2]+1, y+p.y);
                PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallBoldFont, PVColor, Shadow, ability[3]+"", x+data[3]+1, y+p.y);
                PrintConsts.ShadowText( graphic, PrintConsts.XtraSmallBoldFont, PVColor, Shadow, ability[4]+"", x+data[4]+1, y+p.y);
                p.y += graphic.getFont().getSize();
            }

            //Armor
            p.setLocation(109, 60);
            int indexer = 0;
            for ( int a=0; a < stats.getArmor(); a++ ) {
                if ( indexer == 5 ) { p.setLocation(109, p.y+9); indexer = 0; }
                PrintConsts.FilledCircle( graphic, Color.BLACK, Color.WHITE, 8, x+p.x, y+p.y);
                indexer += 1;
                p.x += 9;
            }

            //Internal Structure
            indexer = 0;
            p.setLocation(109, p.y+10);
            for ( int s=0; s < stats.getInternal(); s++ ) {
                if ( indexer == 5 ) { p.setLocation(109, p.y+9); indexer = 0; }
                PrintConsts.FilledCircle( graphic, Color.BLACK, Color.LIGHT_GRAY, 8, x+p.x, y+p.y);
                indexer += 1;
                p.x += 9;
            }

            //Abilities
            p.setLocation(109, 121);
            graphic.setFont( PrintConsts.XtraSmallFont );
            for ( String ability : stats.getFilteredAbilities() ) {
                //ShadowText( PrintConsts.XtraSmallFont, Color.BLACK, Color.LIGHT_GRAY, ability, x+p.x, y+p.y);
                graphic.drawString(ability, x+p.x, y+p.y);
                p.y += graphic.getFont().getSize();
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
