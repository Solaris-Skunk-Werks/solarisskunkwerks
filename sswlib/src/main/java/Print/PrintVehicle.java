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

import common.CommonTools;
import common.Constants;
import components.Ammunition;
import components.CombatVehicle;
import components.LocationIndex;
import components.PlaceableInfo;
import filehandlers.ImageTracker;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class PrintVehicle implements Printable {
    public CombatVehicle CurVee;
    private Image UnitImage = null,
                  LogoImage = null,
                  RecordSheet = null,
                  ChartImage = null;
    private boolean Advanced = false,
                    Charts = false,
                    PrintPilot = true,
                    UseA4Paper = false,
                    Canon = false,
                    TRO = false,
                    printMech = false,
                    printLogo = false,
                    makeAmmoGeneric = false;
    private String PilotName = "",
                    GroupName = "",
                    currentAmmoFormat = "";
    private int Piloting = 5,
                Gunnery = 4,
                MiniConvRate = 0;
    private double BV = 0.0;
    private ifPrintPoints points = null;
    private Color Black = new Color( 0, 0, 0 ),
                  Grey = new Color( 128, 128, 128 );
    private ImageTracker imageTracker;
    private Preferences Prefs = Preferences.userRoot().node( Constants.SSWPrefs );

    private ArrayList<PlaceableInfo> Items;
    private PIPPrinter ap;
    private ArrayList<AmmoData> AmmoList;

    // <editor-fold desc="Constructors">
    public PrintVehicle( CombatVehicle m, Image i, boolean adv, boolean A4, ImageTracker images) {
        CurVee = m;
        imageTracker = images;
        if ( !m.GetSSWImage().equals("../BFB.Images/No_Image.png")  ) UnitImage = imageTracker.getImage(m.GetSSWImage());
        Advanced = adv;
        BV = CommonTools.GetAdjustedBV(CurVee.GetCurrentBV(), Gunnery, Piloting);
        UseA4Paper = A4;
        GetRecordSheet(imageTracker);
        AmmoList = GetAmmo();
    }

    public PrintVehicle( CombatVehicle m, ImageTracker images ) {
        this( m, null, false, false, images);
    }

    public PrintVehicle( CombatVehicle m, String Warrior, int Gun, int Pilot, ImageTracker images) {
        this( m, null, false, false, images);
        SetPilotData(Warrior, Gun, Pilot);
    }
    // </editor-fold>
    
    // <editor-fold desc="Settor Methods">
    public void SetPilotData( String pname, int pgun, int ppilot ) {
        PilotName = pname;
        Piloting = ppilot;
        Gunnery = pgun;
        setBV(CommonTools.GetAdjustedBV(BV, Gunnery, Piloting));
    }

    public void SetOptions( boolean charts, boolean PrintP, double UseBV ) {
        Charts = charts;
        setBV(UseBV);
        PrintPilot = PrintP;
    }

    public void SetMiniConversion( int conv ) {
        MiniConvRate = conv;
    }

    public void setMechwarrior(String name) {
        PilotName = name;
    }

    public void setGunnery(int gunnery) {
        Gunnery = gunnery;
    }

    public void setPiloting(int piloting) {
        Piloting = piloting;
    }

    public void setCharts(Boolean b) {
        Charts = b;
    }

    public void setPrintPilot(Boolean b) {
        PrintPilot = b;
    }

    public void setUnitImage(Image UnitImage) {
        if ( UnitImage != null) {
            this.UnitImage = UnitImage;
            this.printMech = true;
        }
    }

    public void setPrintMech( Boolean PrintMech ) {
        this.printMech = PrintMech;
    }

    public void setLogoImage(Image LogoImage) {
        if ( LogoImage != null) {
            this.LogoImage = LogoImage;
            this.printLogo = true;
        }
    }

    public void setPrintLogo( Boolean PrintLogo ) {
        this.printLogo = PrintLogo;
    }

    public void setBV(double BV) {
        this.BV = BV;
    }

    public void setCanon( boolean Canon ) {
        this.Canon = Canon;
    }

    public void setTRO(boolean TRO) {
        this.TRO = TRO;
        setCanon(true);
        setCharts(false);
        SetMiniConversion(1);
        setPrintPilot(false);
        currentAmmoFormat = Prefs.get( "AmmoNamePrintFormat", "" );
        Prefs.put( "AmmoNamePrintFormat", "Ammo (%P) %L" );
    }

    // </editor-fold>

    // <editor-fold desc="Gettor Methods">
    public String getMechwarrior(){
        return PilotName;
    }

    public int getGunnery(){
        return Gunnery;
    }

    public int getPiloting(){
        return Piloting;
    }
    
    public Image getUnitImage() {
        return UnitImage;
    }
    
    public Image getLogoImage() {
        return LogoImage;
    }

    public boolean isTRO() {
        return TRO;
    }
    // </editor-fold>

    public int print( Graphics graphics, PageFormat pageFormat, int pageIndex ) throws PrinterException {
        ((Graphics2D) graphics).translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        if( RecordSheet == null ) {
            return Printable.NO_SUCH_PAGE;
        } else {
            PreparePrint( (Graphics2D) graphics );
            if ( !currentAmmoFormat.isEmpty() ) { Prefs.put( "AmmoNamePrintFormat", currentAmmoFormat); }
            return Printable.PAGE_EXISTS;
        }
    }
    
    private void PreparePrint( Graphics2D graphics ) {
        Items = PrintConsts.SortEquipmentByLocation( CurVee, MiniConvRate );
        ap = new PIPPrinter(graphics, CurVee, Canon, imageTracker);
        this.BV = CommonTools.GetAdjustedBV(CurVee.GetCurrentBV(), Gunnery, Piloting);

        //DrawImages( graphics );
        DrawSheet( graphics );
        DrawMechData( graphics );
        DrawPips( graphics );

        if( Charts ) {
            // reset the scale and add the charts
            graphics.scale( 1.25d, 1.25d );
            graphics.drawImage( ChartImage, 0, 0, 576, 756, null );
            //AddCharts( graphics );
        }
        //DrawGrid( graphics );
    }

    private void DrawSheet( Graphics2D graphics ) {
        // adjust the printable area for A4 paper size
        if( UseA4Paper ) {
            graphics.scale( 0.9705d, 0.9705d );
        }

        // adjust the printable area for use with helpful charts
        if( Charts ) {
            graphics.scale( 0.8d, 0.8d );
        }
        
        graphics.drawImage( RecordSheet, 0, 0, 576, 756, null );
        //graphics.drawImage( RecordSheet, 0, 0, 556, 760, null ); vee size?
        //graphics.drawImage( RecordSheet, 0, 0, 560, 757, null );
        
        Point start = ap.GetPoints().GetMechImageLoc();
        start.x -= 3;
        start.y -= 6;
        Point bounds = ap.GetPoints().GetImageBounds();
        if ( printMech ) {
            UnitImage = imageTracker.media.GetImage(imageTracker.media.DetermineMatchingImage(CurVee.GetName(), CurVee.GetModel(), CurVee.GetSSWImage()));
            if( UnitImage != null ) {
                //graphics.drawRect(start.x, start.y, 150, 210);
                Dimension d = imageTracker.media.reSize(getUnitImage(), bounds.x, bounds.y);
                Point offset = imageTracker.media.offsetImageCenter( new Dimension(bounds.x, bounds.y), d);
                graphics.drawImage( getUnitImage(), start.x + offset.x, start.y + offset.y, d.width, d.height, null );
            }
        }

        if ( printLogo && LogoImage != null ) {
            Dimension d = imageTracker.media.reSize(LogoImage, 50, 50);
            Point offset = imageTracker.media.offsetImageBottom(new Dimension(bounds.x, bounds.y), d);
            graphics.drawImage( LogoImage, start.x + offset.x, start.y+offset.y, d.width, d.height, null );
        }
    }

    private void DrawPips( Graphics2D graphics ) {
        ap.Render(graphics, CurVee, Canon);
    }

    private int TotalItemLines() {
        int TotItems = Items.size();
        for ( PlaceableInfo item : Items ) {
            if ( item.name2.length() > 0 ) { TotItems += 1;}
            if ( item.specials.replace("-", "").length() > 0 ) { TotItems += 1;}
        }
        TotItems += AmmoList.size();
        return TotItems;
    }

    private void DrawMechData( Graphics2D graphics ) {
        Point[] p = null;

        //ArrayList<PlaceableInfo> a = SortEquipmentByLocation();
        p = ap.GetPoints().GetWeaponChartPoints();

        //Range TH Modifiers
//        if ( !TRO ) {
//            graphics.setFont( PrintConsts.CrazyTinyFont);
//            graphics.drawString("+0", p[6].x, p[6].y-15);
//            graphics.drawString("+2", p[7].x, p[7].y-15);
//            graphics.drawString("+4", p[8].x, p[8].y-15);
//        }
		
		//Coverup the (hexes) above the ranges if we are not using traditional measurements
        if (MiniConvRate > 0)
        {
            graphics.setColor(Color.white);
            graphics.fillRect(p[6].x-5, p[6].y-28, 30, 10);
            graphics.setColor(Color.black);
        }

        graphics.setFont( PrintConsts.ReallySmallFont );
        if (TotalItemLines() > 15) { graphics.setFont( PrintConsts.TinyFont ); }
        if (TotalItemLines() >= 20) { graphics.setFont( PrintConsts.CrazyTinyFont); }
        int offset = 0,
            xoffset = 0;

        for ( PlaceableInfo item : Items ) {
            xoffset = 0;
            graphics.drawString( item.Count + "", p[0].x+1, p[0].y + offset );
            graphics.drawString( item.name, p[1].x-3, p[1].y + offset );
            graphics.drawString( item.locName, p[2].x, p[2].y + offset );
            //graphics.drawString( item.heat, p[3].x, p[3].y + offset );
            if ( item.damage.length() > 3 ) xoffset = (int)Math.ceil(item.damage.length());
            graphics.drawString( item.damage, p[4].x - xoffset, p[4].y + offset );
            graphics.drawString( item.min, p[5].x, p[5].y + offset );
            graphics.drawString( item.rShort, p[6].x, p[6].y + offset );
            graphics.drawString( item.rMed, p[7].x, p[7].y + offset );
            graphics.drawString( item.rLong, p[8].x, p[8].y + offset );

            offset += graphics.getFont().getSize();

            // check to see now if we need to print our special codes or more of the name
            if ( (item.specials.replace("-", "").length() > 0) || (item.name2.length() > 0) ) {
                int lineoffset = 0;
                if ( item.name2.length() > 0 ) {
                    graphics.drawString( item.name2, p[1].x, p[1].y + offset );
                    lineoffset = graphics.getFont().getSize();
                }
                //if we aren't printing a TRO specific sheet...get rid of all the excess!
                if (TRO || TotalItemLines() < 15) {
                    if ( item.specials.replace("-", "").length() > 0 ) {
                        xoffset = (int)Math.ceil(item.specials.length());
                        //Font curFont = graphics.getFont();
                        //graphics.setFont(PrintConsts.CrazyTinyFont);
                        graphics.drawString( item.specials, p[4].x - xoffset, p[4].y + offset );
                        lineoffset = graphics.getFont().getSize();
                        //graphics.setFont(curFont);
                    }
                }
                offset += lineoffset;
            }

            offset += 2;
        }

        //Output the list of Ammunition
        if ( !TRO ) {
            if ( AmmoList.size() > 0 ) {
                offset += 2;
                graphics.drawString("Ammunition Type" + ( CurVee.GetLoadout().HasISCASE() ? " [CASE]":"" ), p[0].x, p[0].y + offset);
                graphics.drawString("Rounds", p[3].x-30, p[3].y + offset);
                offset += 2;
                graphics.drawLine(p[0].x, p[0].y + offset, p[8].x + 8, p[8].y + offset);
                offset += graphics.getFont().getSize();
            }
            for ( int index=0; index < AmmoList.size(); index++ ) {
                AmmoData CurAmmo = (AmmoData) AmmoList.get(index);
                graphics.drawString( CurAmmo.Format(), p[0].x, p[0].y + offset);
                graphics.drawString( CurAmmo.LotSize + "", p[3].x-30, p[3].y + offset);
                
                //Ammo boxes
                Point spot = new Point(p[3].x-15, p[3].y + offset - 5);
                graphics.setStroke(new BasicStroke(0.5f));
                int lotsize = Math.min(CurAmmo.LotSize, 100);
                if ( CurAmmo.LotSize >= 100 ) lotsize = 40;
                while( lotsize > 0 ) {
                    for ( int i=0; i < Math.min(lotsize, 20); i++ ) {
                        //graphics.drawRect(spot.x, spot.y, 4, 4);
                        //spot.setLocation(spot.x+((((i+1)%5) == 0) ? 5 : 4), spot.y);
                        graphics.drawOval(spot.x, spot.y, 5, 5);
                        spot.setLocation(spot.x+((((i+1)%5) == 0) ? 7 : 6), spot.y);
                    }
                    offset += 5;
                    spot.setLocation(p[3].x-15, spot.y + 6);
                    lotsize -= 20;
                }
                offset += 1;
            }
        }

        graphics.setFont( PrintConsts.DesignNameFont );
        p = ap.GetPoints().GetDataChartPoints();
        graphics.setFont( PrintConsts.Small8Font );
        graphics.drawString( CurVee.GetFullName(), p[PrintConsts.MECHNAME].x, p[PrintConsts.MECHNAME].y );

        // have to hack the movement to print the correct stuff here.
        graphics.setFont( PrintConsts.Small8Font );
        graphics.drawString( ( CurVee.getCruiseMP() * MiniConvRate ) + "", p[PrintConsts.WALKMP].x, p[PrintConsts.WALKMP].y );
        graphics.drawString( CurVee.getFlankMP( MiniConvRate ) + "", p[PrintConsts.RUNMP].x, p[PrintConsts.RUNMP].y );
        
        // Movement and Engine
        if ( !CurVee.IsVTOL() ) {
            graphics.drawString( CurVee.GetMotiveLookupName() + "" + CurVee.GetChassisModifierString(), p[19].x, p[19].y );
            graphics.drawString( CurVee.GetEngine().CritName() + "", p[20].x, p[20].y );
        } else {
            graphics.drawString( CurVee.GetEngine().CritName() + "", p[19].x, p[19].y );
        }
        

        //Jumping Movement!
        /*
        String JumpMP = "";
        if ( CurVee.GetJumpJets().GetNumJJ() > 0 ) 
            JumpMP += (CurVee.GetJumpJets().GetNumJJ() * MiniConvRate) + "";

        if (JumpMP.isEmpty()) JumpMP = "0";
        if ( CurVee.GetJumpJets().IsImproved() ) JumpMP += " IMP";
        if ( CurVee.GetJumpJets().IsUMU() ) JumpMP += " UMU";

        graphics.drawString( JumpMP, p[PrintConsts.JUMPMP].x, p[PrintConsts.JUMPMP].y );
        */
        // end hacking of movement.

        //Tonnage
        graphics.drawString( CurVee.GetTonnage() + "", p[PrintConsts.TONNAGE].x, p[PrintConsts.TONNAGE].y );

        //Cost
        graphics.setFont( PrintConsts.SmallFont );
        graphics.drawString( String.format("%1$,.0f C-Bills", CurVee.GetTotalCost()), p[PrintConsts.COST].x, p[PrintConsts.COST].y);
        graphics.drawString( String.format("+%1$,.0f (ammo)", CurVee.GetAmmoCosts()), p[PrintConsts.AMMO].x, p[PrintConsts.AMMO].y);

        //BV
        if ( !TRO ) {
            if ( Gunnery == 4 && Piloting == 5 )
                graphics.drawString( String.format( "%1$,d", CurVee.GetCurrentBV() ), p[PrintConsts.BV2].x, p[PrintConsts.BV2].y );
            else
                graphics.drawString( String.format( "%1$,.0f (Base: %2$,d)", BV, CurVee.GetCurrentBV() ), p[PrintConsts.BV2].x, p[PrintConsts.BV2].y );
            graphics.setFont( PrintConsts.SmallFont );
            graphics.drawString( CurVee.GetArmor().CritName() + " Pts: " + CurVee.GetArmor().GetArmorValue(), p[PrintConsts.TOTAL_ARMOR].x, p[PrintConsts.TOTAL_ARMOR].y );
            graphics.setFont( PrintConsts.BoldFont );
        } else {
            graphics.drawString( String.format( "%1$,d", CurVee.GetCurrentBV() ), p[PrintConsts.BV2].x, p[PrintConsts.BV2].y );
        }

        //Mechwarrior
        graphics.setFont( PrintConsts.PlainFont );
        if ( TRO ) {
            graphics.setFont( PrintConsts.BoldFont );
            graphics.drawLine(p[PrintConsts.PILOT_NAME].x+1, p[PrintConsts.PILOT_NAME].y+1, p[PrintConsts.PILOT_NAME].x + 107, p[PrintConsts.PILOT_NAME].y+1);
            graphics.drawLine(p[PrintConsts.PILOT_GUN].x, p[PrintConsts.PILOT_GUN].y+1, p[PrintConsts.PILOT_GUN].x + 14, p[PrintConsts.PILOT_GUN].y+1);
            graphics.drawLine(p[PrintConsts.PILOT_PILOT].x-4, p[PrintConsts.PILOT_PILOT].y+1, p[PrintConsts.PILOT_PILOT].x + 10, p[PrintConsts.PILOT_PILOT].y+1);
        } else if( PrintPilot ) {
            graphics.setFont( PrintConsts.SmallFont );
            if ( !GroupName.replace("[]", "").isEmpty() ) {
                graphics.drawString( PilotName, p[PrintConsts.PILOT_NAME].x, p[PrintConsts.PILOT_NAME].y-4 );
                graphics.drawString( GroupName.replace("[]", ""), p[PrintConsts.PILOT_NAME].x, p[PrintConsts.PILOT_NAME].y+3 );
            } else {
                graphics.drawString( PilotName, p[PrintConsts.PILOT_NAME].x, p[PrintConsts.PILOT_NAME].y );
            }
            graphics.setFont( PrintConsts.PlainFont );
            graphics.drawString( Gunnery + "", p[PrintConsts.PILOT_GUN].x, p[PrintConsts.PILOT_GUN].y );
            graphics.drawString( Piloting + "", p[PrintConsts.PILOT_PILOT].x, p[PrintConsts.PILOT_PILOT].y );
        }

        // check boxes
        graphics.setFont( PrintConsts.PlainFont );
        String temp;
        temp = CommonTools.GetTechbaseString( CurVee.GetLoadout().GetTechBase() );
        graphics.drawString( temp, p[PrintConsts.TECH_IS].x, p[PrintConsts.TECH_IS].y );

        graphics.drawString( CurVee.GetYear() + "", p[PrintConsts.TECH_IS].x, p[PrintConsts.TECH_IS].y + 10 );

        if ( !TRO ) {
            //Availability Codes
            graphics.drawString(CurVee.GetAvailability().GetBestCombinedCode(), p[PrintConsts.TECH_IS].x, p[PrintConsts.TECH_IS].y+20);
        }

        // armor information
        p = ap.GetPoints().GetArmorInfoPoints();
        graphics.drawString( "(" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_FRONT ) + ")", p[LocationIndex.CV_LOC_FRONT].x, p[LocationIndex.CV_LOC_FRONT].y );
        graphics.drawString( "(" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_LEFT ) + ")", p[LocationIndex.CV_LOC_LEFT].x, p[LocationIndex.CV_LOC_LEFT].y );
        graphics.drawString( "(" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_RIGHT ) + ")", p[LocationIndex.CV_LOC_RIGHT].x, p[LocationIndex.CV_LOC_RIGHT].y );
        graphics.drawString( "(" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_REAR ) + ")", p[LocationIndex.CV_LOC_REAR].x, p[LocationIndex.CV_LOC_REAR].y );
        if ( CurVee.IsVTOL() ) 
            graphics.drawString( "(" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_ROTOR ) + ")", p[LocationIndex.CV_LOC_ROTOR].x, p[LocationIndex.CV_LOC_ROTOR].y );
        if ( CurVee.isHasTurret1() )
            graphics.drawString( "(" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET1 ) + ")", p[LocationIndex.CV_LOC_TURRET1].x, p[LocationIndex.CV_LOC_TURRET1].y );
        if ( CurVee.isHasTurret2() )
            graphics.drawString( "(" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET2 ) + ")", p[LocationIndex.CV_LOC_TURRET2].x, p[LocationIndex.CV_LOC_TURRET2].y );
    }

    private void DrawImages( Graphics2D graphics ) {
        //PrintMech Image
        Point start = ap.GetPoints().GetMechImageLoc();
        start.x -= 3;
        start.y -= 6;
        if( getUnitImage() != null ) {
            //graphics.drawRect(start.x, start.y, 160, 200);
            Dimension d = imageTracker.media.reSize(getUnitImage(), 160, 200);
            Point offset = imageTracker.media.offsetImageCenter( new Dimension(160, 200), d);
            graphics.drawImage( getUnitImage(), start.x + offset.x, start.y + offset.y, d.width, d.height, null );
        }

        if ( LogoImage != null ) {
            graphics.drawImage( LogoImage, ap.GetPoints().GetLogoImageLoc().x, ap.GetPoints().GetLogoImageLoc().y, 50, 50, null );
        }
    }

    private void DrawGrid( Graphics2D graphics ) {
        graphics.setFont( PrintConsts.ReallySmallFont );
        boolean bPrint = true;
        for (int x = 0; x <= 576; x += 10) {
            if (bPrint) { graphics.drawString(x+"", x-5, 5); }
            bPrint = !bPrint;
            graphics.drawLine(x, 0, x, 756);
        }
        bPrint = false;
        for (int y = 0; y <= 756; y += 10) {
            if (bPrint) { graphics.drawString(y+"", 0, y+5); }
            bPrint = !bPrint;
            graphics.drawLine(0, y, 576, y);
        }
    }

    private ArrayList<AmmoData> GetAmmo() {
        //Output the list of Ammunition
        ArrayList all = CurVee.GetLoadout().GetNonCore();
        ArrayList<AmmoData> AmmoLister = new ArrayList<AmmoData>();
        for ( int index=0; index < all.size(); index++ ) {
            if(  all.get( index ) instanceof Ammunition ) {
                AmmoData CurAmmo = new AmmoData((Ammunition) all.get(index));
                CurAmmo.makeGeneric = makeAmmoGeneric;
                boolean found = false;
                for ( int internal=0; internal < AmmoLister.size(); internal++ ) {
                    AmmoData existAmmo = (AmmoData) AmmoLister.get(internal);
                    if ( CurAmmo.Name().equals( existAmmo.Name() ) ) {
                        existAmmo.LotSize += CurAmmo.LotSize;
                        found = true;
                        break;
                    }
                }
                if ( !found ) {
                    AmmoLister.add(CurAmmo);
                }
            }
        }
        return AmmoLister;
    }

    private boolean AmmoContains( ArrayList<AmmoData> AmmoList, String CheckExpr ) {
        for ( AmmoData data : AmmoList ) {
            if ( data.Format().contains(CheckExpr) ) return true;
        }
        return false;
    }

    private void GetRecordSheet( ImageTracker images ) {
        // loads the correct record sheet and points based on the information given
        RecordSheet = images.getImage( PrintConsts.RS_TW_GROUND );
        ChartImage = images.getImage(PrintConsts.BP_ChartImage );
        points = new TWGroundPoints();

        //We have to use the advanced sheet for dual turrets
        if ( CurVee.isHasTurret2() )
            RecordSheet = images.getImage( PrintConsts.RS_TO_GROUND );
        
        if ( CurVee.IsVTOL() ) {
            if ( CurVee.isHasTurret1() )
                RecordSheet = images.getImage( PrintConsts.RS_TW_VTOL );
            else
                RecordSheet = images.getImage( PrintConsts.RS_TW_VTOL );
        }
        
        if ( CurVee.IsNaval() )
            RecordSheet = images.getImage( PrintConsts.RS_TW_NAVAL );
    }


    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String GroupName) {
        this.GroupName = GroupName;
    }

    public void setAmmoGeneric( boolean action ) {
        makeAmmoGeneric = action;
        AmmoList = GetAmmo();
    }

    public void setA4()
    {
        UseA4Paper = true;
    }

    public void setLetter()
    {
        UseA4Paper = false;
    }
    private class AmmoData {
        public String ActualName,
                      ChatName,
                      GenericName,
                      CritName,
                      LookupName;
        public int LotSize;
        public boolean makeGeneric = false;

        public AmmoData( Ammunition ammo ) {
            this.ActualName = ammo.ActualName();
            this.ChatName = ammo.ChatName();
            this.CritName = ammo.CritName().replace("@", "").trim();
            this.LookupName = ammo.LookupName();

            this.GenericName = ammo.CritName().replace("@", "").replace("(Slug)", "").replace("(Cluster)", "");
            this.LotSize = ammo.GetLotSize();
        }

        public String Name() {
            if ( !makeGeneric )
                return ActualName;
            else
                return GenericName;
        }

        public String Format() {
            if ( !makeGeneric )
                return ("%P").replace("%P", CritName).replace("%F", LookupName).replace("%L", "");
            else
                return ("%P").replace("%P", GenericName).replace("%F", GenericName).replace("%L", "");
        }
    }
}
