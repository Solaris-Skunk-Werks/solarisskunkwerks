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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Vector;
import ssw.utilities.CommonTools;
import ssw.Constants;
import ssw.components.*;
import ssw.filehandlers.FileCommon;
import ssw.filehandlers.Media;
import ssw.gui.frmMain;

public class PrintMech implements Printable {

    private frmMain Parent;
    public Mech CurMech;
    private Image MechImage = null,
                  LogoImage = null,
                  RecordSheet = null,
                  ChartImage = null;
    private boolean Advanced = false,
                    Charts = false,
                    PrintPilot = true,
                    UseA4Paper = false,
                    Canon = false,
                    TRO = false;
    private String PilotName = "";
    private int Piloting = 5,
                Gunnery = 4,
                MiniConvRate = 1;
    private double BV = 0.0;
    private ifPrintPoints points = null;
    private Font BoldFont = new Font( "Arial", Font.BOLD, 8 );
    private Font PlainFont = new Font( "Arial", Font.PLAIN, 8 );
    private Font ItalicFont = new Font( "Arial", Font.ITALIC, 8 );
    private Font SmallFont = new Font( "Arial", Font.PLAIN, 7 );
    private Font SmallItalicFont = new Font( "Arial", Font.ITALIC, 7 );
    private Font SmallBoldFont = new Font( "Arial", Font.BOLD, 7 );
    private Font ReallySmallFont = new Font( "Arial", Font.PLAIN, 6 );
    private Font XtraSmallBoldFont = new Font( "Arial", Font.BOLD, 6 );
    private Font XtraSmallFont = new Font( "Arial", Font.PLAIN, 6 );
    private Color Black = new Color( 0, 0, 0 ),
                  Grey = new Color( 128, 128, 128 );
    private Media media = new Media();

    // <editor-fold desc="Constructors">
    public PrintMech( frmMain parent, Mech m, Image i, boolean adv, boolean A4) {
        Parent = parent;
        CurMech = m;
        MechImage = media.GetImage(m.GetSSWImage());
        Advanced = adv;
        BV = CommonTools.GetAdjustedBV(CurMech.GetCurrentBV(), Gunnery, Piloting);
        UseA4Paper = A4;
        GetRecordSheet();
        ChartImage = media.GetImage(PrintConsts.BP_ChartImage );
        if ( CurMech.IsQuad() ) { ChartImage = media.GetImage(PrintConsts.QD_ChartImage); }
    }

    public PrintMech( Mech m, Image i, boolean adv, boolean A4) {
        this(null, m, i, adv, A4);
    }

    public PrintMech( Mech m ) {
        this(null, m, null, false, false);
    }

    public PrintMech( Mech m, String Warrior, int Gun, int Pilot) {
        this(null, m, null, false, false);
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

    public void setMechImage(Image MechImage) {
        if ( MechImage != null) { this.MechImage = MechImage; }
    }

    public void setLogoImage(Image LogoImage) {
        if ( LogoImage != null) { this.LogoImage = LogoImage; }
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
    
    public Image getMechImage() {
        return MechImage;
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
            return Printable.PAGE_EXISTS;
        }
    }
    
    private void PreparePrint( Graphics2D graphics ) {
        this.BV = CommonTools.GetAdjustedBV(CurMech.GetCurrentBV(), Gunnery, Piloting);

        // adjust the printable area for A4 paper size
        if( UseA4Paper ) {
            graphics.scale( 0.9705d, 0.9705d );
        }
        
        // adjust the printable area for use with helpful charts
        if( Charts ) {
            graphics.scale( 0.8d, 0.8d );
        }

        graphics.drawImage( RecordSheet, 0, 0, 576, 756, null );

        DrawPips( graphics );
        DrawCriticals( graphics );
        DrawMechData( graphics );
        DrawImages( graphics );

        if( Charts ) {
            // reset the scale and add the charts
            graphics.scale( 1.25d, 1.25d );
            graphics.drawImage( ChartImage, 0, 0, 576, 756, null );
            //AddCharts( graphics );
        }
        //DrawGrid( graphics );
    }

    private void DrawPips( Graphics2D graphics ) {
        PIPPrinter ap = new PIPPrinter(graphics, CurMech, Canon);
        ap.Render();
    }

    private void DrawCriticals( Graphics2D graphics ) {
        abPlaceable[] a = null;
        Point[] p = null;
        graphics.setFont( SmallFont );

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_HD );
        p = points.GetCritHDPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( a[i].NumCrits() > 1 && a[i].Contiguous() &! ( a[i] instanceof Engine ) &! ( a[i] instanceof Gyro ) ) {
                // print the multi-slot indicator before the item
                abPlaceable Current = a[i];
                int j = i;
                int End = Current.NumCrits() + j;
                if( End > a.length ) {
                    End = a.length - 1;
                }
                for( ; j < End; j++ ) {
                    if( j == i ) {
                        // starting out
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x + 2, p[j].y - 3 );
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x, p[j].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else if( j == End - 1 ) {
                        // end the line
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x + 2, p[j].y - 2 );
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else {
                        // continue the line
                        graphics.drawLine( p[j].x, p[j].y, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    }
                }
                i = j - 1;
            } else {
                // single slot item
                if( ! a[i].IsCritable() ) {
                    DrawNonCritable( graphics, GetPrintName( a[i] ), p[i].x, p[i].y );
                } else {
                    if( a[i].IsArmored() ) {
                        graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                        graphics.drawString( GetPrintName( a[i] ), p[i].x + 7, p[i].y );
                    } else if( a[i] instanceof Ammunition ) {
                        graphics.drawString( FileCommon.FormatAmmoPrintName( (Ammunition) a[i], 1 ), p[i].x, p[i].y );
                    } else {
                        graphics.drawString( GetPrintName( a[i] ), p[i].x, p[i].y );
                    }
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_CT );
        p = points.GetCritCTPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( a[i].NumCrits() > 1 && a[i].Contiguous() &! ( a[i] instanceof Engine ) &! ( a[i] instanceof Gyro ) ) {
                // print the multi-slot indicator before the item
                abPlaceable Current = a[i];
                int j = i;
                int End;
                if( Current.CanSplit() ) {
                    int[] check = CurMech.GetLoadout().FindInstances( Current );
                    End = check[Constants.LOC_CT] + j;
                } else {
                    End = Current.NumCrits() + j;
                }
                if( End > a.length ) {
                    End = a.length - 1;
                }
                for( ; j < End; j++ ) {
                    if( j == i ) {
                        // starting out
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x + 2, p[j].y - 3 );
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x, p[j].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else if( j == End - 1 ) {
                        // end the line
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x + 2, p[j].y - 2 );
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else {
                        // continue the line
                        graphics.drawLine( p[j].x, p[j].y, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    }
                }
                i = j - 1;
            } else {
                // single slot item
                if( ! a[i].IsCritable() ) {
                    DrawNonCritable( graphics, GetPrintName( a[i] ), p[i].x, p[i].y );
                } else {
                    if( a[i].IsArmored() ) {
                        graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                        graphics.drawString( GetPrintName( a[i] ), p[i].x + 7, p[i].y );
                    } else if( a[i] instanceof Ammunition ) {
                        graphics.drawString( FileCommon.FormatAmmoPrintName( (Ammunition) a[i], 1 ), p[i].x, p[i].y );
                    } else {
                        graphics.drawString( GetPrintName( a[i] ), p[i].x, p[i].y );
                    }
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_LT );
        p = points.GetCritLTPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( a[i].NumCrits() > 1 && a[i].Contiguous() &! ( a[i] instanceof Engine ) &! ( a[i] instanceof Gyro ) ) {
                // print the multi-slot indicator before the item
                abPlaceable Current = a[i];
                int j = i;
                int End;
                if( Current.CanSplit() ) {
                    int[] check = CurMech.GetLoadout().FindInstances( Current );
                    End = check[Constants.LOC_LT] + j;
                } else {
                    End = Current.NumCrits() + j;
                }
                if( End > a.length ) {
                    End = a.length - 1;
                }
                for( ; j < End; j++ ) {
                    if( j == i ) {
                        // starting out
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x + 2, p[j].y - 3 );
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x, p[j].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else if( j == End - 1 ) {
                        // end the line
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x + 2, p[j].y - 2 );
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else {
                        // continue the line
                        graphics.drawLine( p[j].x, p[j].y, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    }
                }
                i = j - 1;
            } else {
                // single slot item
                if( ! a[i].IsCritable() ) {
                    DrawNonCritable( graphics,GetPrintName( a[i] ), p[i].x, p[i].y );
                } else {
                    if( a[i].IsArmored() ) {
                        graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                        graphics.drawString( GetPrintName( a[i] ), p[i].x + 7, p[i].y );
                    } else if( a[i] instanceof Ammunition ) {
                        graphics.drawString( FileCommon.FormatAmmoPrintName( (Ammunition) a[i], 1 ), p[i].x, p[i].y );
                    } else {
                        graphics.drawString( GetPrintName( a[i] ), p[i].x, p[i].y );
                    }
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_RT );
        p = points.GetCritRTPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( a[i].NumCrits() > 1 && a[i].Contiguous() &! ( a[i] instanceof Engine ) &! ( a[i] instanceof Gyro ) ) {
                // print the multi-slot indicator before the item
                abPlaceable Current = a[i];
                int j = i;
                int End;
                if( Current.CanSplit() ) {
                    int[] check = CurMech.GetLoadout().FindInstances( Current );
                    End = check[Constants.LOC_RT] + j;
                } else {
                    End = Current.NumCrits() + j;
                }
                if( End > a.length ) {
                    End = a.length - 1;
                }
                for( ; j < End; j++ ) {
                    if( j == i ) {
                        // starting out
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x + 2, p[j].y - 3 );
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x, p[j].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else if( j == End - 1 ) {
                        // end the line
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x + 2, p[j].y - 2 );
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else {
                        // continue the line
                        graphics.drawLine( p[j].x, p[j].y, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    }
                }
                i = j - 1;
            } else {
                // single slot item
                if( ! a[i].IsCritable() ) {
                    DrawNonCritable( graphics, GetPrintName( a[i] ), p[i].x, p[i].y );
                } else {
                    if( a[i].IsArmored() ) {
                        graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                        graphics.drawString( GetPrintName( a[i] ), p[i].x + 7, p[i].y );
                    } else if( a[i] instanceof Ammunition ) {
                        graphics.drawString( FileCommon.FormatAmmoPrintName( (Ammunition) a[i], 1 ), p[i].x, p[i].y );
                    } else {
                        graphics.drawString( GetPrintName( a[i] ), p[i].x, p[i].y );
                    }
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_LA );
        p = points.GetCritLAPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( a[i].NumCrits() > 1 && a[i].Contiguous() &! ( a[i] instanceof Engine ) &! ( a[i] instanceof Gyro ) ) {
                // print the multi-slot indicator before the item
                abPlaceable Current = a[i];
                int j = i;
                int End;
                if( Current.CanSplit() ) {
                    int[] check = CurMech.GetLoadout().FindInstances( Current );
                    End = check[Constants.LOC_LA] + j;
                } else {
                    End = Current.NumCrits() + j;
                }
                if( End > a.length ) {
                    End = a.length - 1;
                }
                for( ; j < End; j++ ) {
                    if( j == i ) {
                        // starting out
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x + 2, p[j].y - 3 );
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x, p[j].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else if( j == End - 1 ) {
                        // end the line
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x + 2, p[j].y - 2 );
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else {
                        // continue the line
                        graphics.drawLine( p[j].x, p[j].y, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    }
                }
                i = j - 1;
            } else {
                // single slot item
                if( ! a[i].IsCritable() ) {
                    DrawNonCritable( graphics, GetPrintName( a[i] ), p[i].x, p[i].y );
                } else {
                    if( a[i].IsArmored() ) {
                        graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                        graphics.drawString( GetPrintName( a[i] ), p[i].x + 7, p[i].y );
                    } else if( a[i] instanceof Ammunition ) {
                        graphics.drawString( FileCommon.FormatAmmoPrintName( (Ammunition) a[i], 1 ), p[i].x, p[i].y );
                    } else {
                        graphics.drawString( GetPrintName( a[i] ), p[i].x, p[i].y );
                    }
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_RA );
        p = points.GetCritRAPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( a[i].NumCrits() > 1 && a[i].Contiguous() &! ( a[i] instanceof Engine ) &! ( a[i] instanceof Gyro ) ) {
                // print the multi-slot indicator before the item
                abPlaceable Current = a[i];
                int j = i;
                int End;
                if( Current.CanSplit() ) {
                    int[] check = CurMech.GetLoadout().FindInstances( Current );
                    End = check[Constants.LOC_RA] + j;
                } else {
                    End = Current.NumCrits() + j;
                }
                if( End > a.length ) {
                    End = a.length - 1;
                }
                for( ; j < End; j++ ) {
                    if( j == i ) {
                        // starting out
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x + 2, p[j].y - 3 );
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x, p[j].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else if( j == End - 1 ) {
                        // end the line
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x + 2, p[j].y - 2 );
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else {
                        // continue the line
                        graphics.drawLine( p[j].x, p[j].y, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    }
                }
                i = j - 1;
            } else {
                // single slot item
                if( ! a[i].IsCritable() ) {
                    DrawNonCritable( graphics, GetPrintName( a[i] ), p[i].x, p[i].y );
                } else {
                    if( a[i].IsArmored() ) {
                        graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                        graphics.drawString( GetPrintName( a[i] ), p[i].x + 7, p[i].y );
                    } else if( a[i] instanceof Ammunition ) {
                        graphics.drawString( FileCommon.FormatAmmoPrintName( (Ammunition) a[i], 1 ), p[i].x, p[i].y );
                    } else {
                        graphics.drawString( GetPrintName( a[i] ), p[i].x, p[i].y );
                    }
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_LL );
        p = points.GetCritLLPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( a[i].NumCrits() > 1 && a[i].Contiguous() &! ( a[i] instanceof Engine ) &! ( a[i] instanceof Gyro ) ) {
                // print the multi-slot indicator before the item
                abPlaceable Current = a[i];
                int j = i;
                int End;
                if( Current.CanSplit() ) {
                    int[] check = CurMech.GetLoadout().FindInstances( Current );
                    End = check[Constants.LOC_LL] + j;
                } else {
                    End = Current.NumCrits() + j;
                }
                if( End > a.length ) {
                    End = a.length - 1;
                }
                for( ; j < End; j++ ) {
                    if( j == i ) {
                        // starting out
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x + 2, p[j].y - 3 );
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x, p[j].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else if( j == End - 1 ) {
                        // end the line
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x + 2, p[j].y - 2 );
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else {
                        // continue the line
                        graphics.drawLine( p[j].x, p[j].y, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    }
                }
                i = j - 1;
            } else {
                // single slot item
                if( ! a[i].IsCritable() ) {
                    DrawNonCritable( graphics, GetPrintName( a[i] ), p[i].x, p[i].y );
                } else {
                    if( a[i].IsArmored() ) {
                        graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                        graphics.drawString( GetPrintName( a[i] ), p[i].x + 7, p[i].y );
                    } else if( a[i] instanceof Ammunition ) {
                        graphics.drawString( FileCommon.FormatAmmoPrintName( (Ammunition) a[i], 1 ), p[i].x, p[i].y );
                    } else {
                        graphics.drawString( GetPrintName( a[i] ), p[i].x, p[i].y );
                    }
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_RL );
        p = points.GetCritRLPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( a[i].NumCrits() > 1 && a[i].Contiguous() &! ( a[i] instanceof Engine ) &! ( a[i] instanceof Gyro ) ) {
                // print the multi-slot indicator before the item
                abPlaceable Current = a[i];
                int j = i;
                int End;
                if( Current.CanSplit() ) {
                    int[] check = CurMech.GetLoadout().FindInstances( Current );
                    End = check[Constants.LOC_RL] + j;
                } else {
                    End = Current.NumCrits() + j;
                }
                if( End > a.length ) {
                    End = a.length - 1;
                }
                for( ; j < End; j++ ) {
                    if( j == i ) {
                        // starting out
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x + 2, p[j].y - 3 );
                        graphics.drawLine( p[j].x, p[j].y - 3, p[j].x, p[j].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else if( j == End - 1 ) {
                        // end the line
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x + 2, p[j].y - 2 );
                        graphics.drawLine( p[j].x, p[j].y - 2, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    } else {
                        // continue the line
                        graphics.drawLine( p[j].x, p[j].y, p[j].x, p[j-1].y );
                        if( a[j].IsArmored() ) {
                            graphics.drawOval( p[j].x + 3, p[j].y - 5, 5, 5 );
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 10, p[j].y );
                        } else {
                            graphics.drawString( GetPrintName( a[j] ), p[j].x + 3, p[j].y );
                        }
                    }
                }
                i = j - 1;
            } else {
                // single slot item
                if( ! a[i].IsCritable() ) {
                    DrawNonCritable( graphics, GetPrintName( a[i] ), p[i].x, p[i].y );
                } else {
                    if( a[i].IsArmored() ) {
                        graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                        graphics.drawString( GetPrintName( a[i] ), p[i].x + 7, p[i].y );
                    } else if( a[i] instanceof Ammunition ) {
                        graphics.drawString( FileCommon.FormatAmmoPrintName( (Ammunition) a[i], 1 ), p[i].x, p[i].y );
                    } else {
                        graphics.drawString( GetPrintName( a[i] ), p[i].x, p[i].y );
                    }
                }
            }
        }
    }

    private void DrawMechData( Graphics2D graphics ) {
        Point[] p = null;

        p = points.GetHeatSinkPoints();
        for( int i = 0; i < CurMech.GetHeatSinks().GetNumHS(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        PlaceableInfo[] a = SortEquipmentByLocation();
        p = points.GetWeaponChartPoints();
        graphics.setFont( SmallFont );
        if (a.length >= 9) { graphics.setFont( XtraSmallFont ); }
        int offset = 0;
        boolean PrintSpecials = false;
        for( int i = 0; i < a.length; i++ ) {
            PlaceableInfo item = a[i];
            graphics.drawString( item.Count + "", p[0].x, p[0].y + offset );
            graphics.drawString( GetPrintName( item.Item ), p[1].x, p[1].y + offset );
            graphics.drawString( FileCommon.EncodeLocation( item.Location, CurMech.IsQuad() ), p[2].x, p[2].y + offset );
            if( item.Item instanceof Equipment ) {
                graphics.drawString( ((Equipment) item.Item).GetHeat() + "", p[3].x, p[3].y + offset );
            } else if( item.Item instanceof ifWeapon ) {
                if( ((ifWeapon) item.Item).IsUltra() || ((ifWeapon) item.Item).IsRotary() ) {
                    graphics.drawString( ((ifWeapon) item.Item).GetHeat() + "/s", p[3].x, p[3].y + offset );
                } else {
                    graphics.drawString( ((ifWeapon) item.Item).GetHeat() + "", p[3].x, p[3].y + offset );
                }
            } else {
                graphics.drawString( "-", p[3].x, p[3].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                if( ((ifWeapon) item.Item).GetWeaponClass() == ifWeapon.W_MISSILE ) {
                    graphics.drawString( ((ifWeapon) item.Item).GetDamageShort() + "/m", p[4].x, p[4].y + offset );
                    PrintSpecials = true;
                } else {
                    if( ((ifWeapon) item.Item).GetDamageShort() != ((ifWeapon) item.Item).GetDamageMedium() ||  ((ifWeapon) item.Item).GetDamageShort() != ((ifWeapon) item.Item).GetDamageLong() ||  ((ifWeapon) item.Item).GetDamageMedium() != ((ifWeapon) item.Item).GetDamageLong() ) {
                        graphics.drawString( ((ifWeapon) item.Item).GetDamageShort() + "/" + ((ifWeapon) item.Item).GetDamageMedium() + "/" + ((ifWeapon) item.Item).GetDamageLong(), p[4].x, p[4].y + offset );
                        PrintSpecials = true;
                    } else {
                        if( ((ifWeapon) item.Item).GetSpecials().equals( "-" ) ) {
                            graphics.drawString( ((ifWeapon) item.Item).GetDamageShort() + " [" + ((ifWeapon) item.Item).GetType() + "]", p[4].x, p[4].y + offset );
                            PrintSpecials = false;
                        } else {
                            graphics.drawString( ((ifWeapon) item.Item).GetDamageShort() + "", p[4].x, p[4].y + offset );
                            PrintSpecials = true;
                        }
                    }
                }
            } else {
                if( item.Item instanceof Equipment ) {
                    if( ((Equipment) item.Item).GetSpecials().equals( "-" ) ) {
                        graphics.drawString( "[" + ((Equipment) item.Item).GetType() + "]", p[4].x, p[4].y + offset );
                        PrintSpecials = false;
                    } else {
                        graphics.drawString( "-", p[4].x, p[4].y + offset );
                        PrintSpecials = true;
                    }
                } else {
                    graphics.drawString( "-", p[4].x, p[4].y + offset );
                    PrintSpecials = true;
                }
            }
            if( item.Item instanceof ifWeapon ) {
                if( ((ifWeapon) item.Item).GetRangeMin() < 1 ) {
                    graphics.drawString( "-", p[5].x, p[5].y + offset );
                } else {
                    graphics.drawString( (((ifWeapon) item.Item).GetRangeMin() * MiniConvRate ) + "", p[5].x, p[5].y + offset );
                }
            } else {
                graphics.drawString( "-", p[5].x, p[5].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                graphics.drawString( (((ifWeapon) item.Item).GetRangeShort() * MiniConvRate ) + "", p[6].x, p[6].y + offset );
            } else if( item.Item instanceof Equipment ) {
                graphics.drawString( (((Equipment) item.Item).GetShortRange() * MiniConvRate ) + "", p[6].x, p[6].y + offset );
            } else {
                graphics.drawString( "-", p[6].x, p[6].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                graphics.drawString( (((ifWeapon) item.Item).GetRangeMedium() * MiniConvRate ) + "", p[7].x, p[7].y + offset );
            } else if( item.Item instanceof Equipment ) {
                graphics.drawString( (((Equipment) item.Item).GetMediumRange() * MiniConvRate ) + "", p[7].x, p[7].y + offset );
            } else {
                graphics.drawString( "-", p[7].x, p[7].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                graphics.drawString( (((ifWeapon) item.Item).GetRangeLong() * MiniConvRate ) + "", p[8].x, p[8].y + offset );
            } else if( item.Item instanceof Equipment ) {
                graphics.drawString( (((Equipment) item.Item).GetLongRange() * MiniConvRate ) + "", p[8].x, p[8].y + offset );
            } else {
                graphics.drawString( "-", p[8].x, p[8].y + offset );
            }

            offset += graphics.getFont().getSize();

            // check to see how if we need to print our special codes.
            if( PrintSpecials ) {
                String Codes = "";
                if( item.Item instanceof ifWeapon ) {
                    ifWeapon w = (ifWeapon) item.Item;
                    Codes = ("[" + w.GetType() + ", " + w.GetSpecials() + "]").replace(", -", "");
                } else if( item.Item instanceof Equipment ) {
                    Equipment e = (Equipment) item.Item;
                    Codes = ("[" + e.GetType() + ", " + e.GetSpecials() + "]").replace(", -", "");
                }
                graphics.drawString( Codes, p[1].x + 2, p[1].y + offset );
                offset += graphics.getFont().getSize();
            }
        }

        //HARD CODED CHECK FOR TC!! SHOULD BE REPLACED SOMETIME IN THE FUTURE!!!
        if (CurMech.GetLoadout().UsingTC()) {
            TargetingComputer tc = CurMech.GetLoadout().GetTC();
            graphics.drawString("1", p[0].x, p[0].y + offset);
            graphics.drawString(tc.CritName(), p[1].x, p[1].y + offset);
            offset += graphics.getFont().getSize();
        }
        offset += (graphics.getFont().getSize() * 2);

        //Output the list of Ammunition
        Vector AmmoList = GetAmmo();
        if ( AmmoList.size() > 0 ) {
            graphics.drawString("Ammunition Type", p[0].x, p[0].y + offset);
            graphics.drawString("Rounds", p[2].x, p[2].y + offset);
            offset += 2;
            graphics.drawLine(p[0].x, p[0].y + offset, p[8].x + 8, p[8].y + offset);
            offset += graphics.getFont().getSize();
        }
        for ( int index=0; index < AmmoList.size(); index++ ) {
            AmmoData CurAmmo = (AmmoData) AmmoList.get(index);
            graphics.drawString(CurAmmo.PrintName, p[0].x, p[0].y + offset);
            graphics.drawString(CurAmmo.LotSize + "", p[2].x, p[2].y + offset);
            offset += graphics.getFont().getSize();
        }

        graphics.setFont( BoldFont );
        p = points.GetDataChartPoints();
        graphics.drawString( CurMech.GetFullName(), p[PrintConsts.MECHNAME].x, p[PrintConsts.MECHNAME].y );

        // have to hack the movement to print the correct stuff here.
        if( CurMech.GetAdjustedWalkingMP( false, true ) != CurMech.GetWalkingMP() ) {
            graphics.drawString( ( CurMech.GetWalkingMP() * MiniConvRate ) + " (" + ( CurMech.GetAdjustedWalkingMP( false, true ) * MiniConvRate ) + ")", p[PrintConsts.WALKMP].x, p[PrintConsts.WALKMP].y );
        } else {
            graphics.drawString( ( CurMech.GetWalkingMP() * MiniConvRate ) + "", p[PrintConsts.WALKMP].x, p[PrintConsts.WALKMP].y );
        }
        if( CurMech.GetAdjustedRunningMP( false, true ) != CurMech.GetRunningMP() ) {
            if( CurMech.GetAdjustedRunningMP( false, true ) < CurMech.GetRunningMP() ) {
                graphics.drawString( ( CurMech.GetAdjustedRunningMP( false, true ) * MiniConvRate ) + "", p[PrintConsts.RUNMP].x, p[PrintConsts.RUNMP].y );
            } else {
                graphics.drawString( ( CurMech.GetRunningMP() * MiniConvRate ) + " (" + ( CurMech.GetAdjustedRunningMP( false, true ) * MiniConvRate ) + ")", p[PrintConsts.RUNMP].x, p[PrintConsts.RUNMP].y );
            }
        } else {
            graphics.drawString( ( CurMech.GetRunningMP() * MiniConvRate ) + "", p[PrintConsts.RUNMP].x, p[PrintConsts.RUNMP].y );
        }
        if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
            graphics.drawString( ( CurMech.GetJumpJets().GetNumJJ() * MiniConvRate ) + " (" + ( CurMech.GetAdjustedJumpingMP( false ) * MiniConvRate ) + ")", p[PrintConsts.JUMPMP].x, p[PrintConsts.JUMPMP].y );
        } else {
            graphics.drawString( ( CurMech.GetJumpJets().GetNumJJ() * MiniConvRate ) + "", p[PrintConsts.JUMPMP].x, p[PrintConsts.JUMPMP].y );
        }
        // end hacking of movement.

        graphics.drawString( CurMech.GetTonnage() + "", p[PrintConsts.TONNAGE].x, p[PrintConsts.TONNAGE].y );
        graphics.drawString( String.format( "%1$,.0f C-Bills", Math.floor( CurMech.GetTotalCost() + 0.5f ) ), p[PrintConsts.COST].x, p[PrintConsts.COST].y );
        
        if ( !TRO ) {
            graphics.drawString( String.format( "%1$,.0f (Base: %2$,d)", BV, CurMech.GetCurrentBV() ), p[PrintConsts.BV2].x, p[PrintConsts.BV2].y );
            graphics.drawString( "Weapon Heat (" + CurMech.GetWeaponHeat() + ")", p[PrintConsts.MAX_HEAT].x, p[PrintConsts.MAX_HEAT].y );
            graphics.setFont(SmallFont);
            graphics.drawString( "Armor Pts: " + CurMech.GetArmor().GetArmorValue(), p[PrintConsts.TOTAL_ARMOR].x, p[PrintConsts.TOTAL_ARMOR].y );
            graphics.setFont(BoldFont);
        } else {
            graphics.drawString( String.format( "%1$,d", CurMech.GetCurrentBV() ), p[PrintConsts.BV2].x, p[PrintConsts.BV2].y );
        }

        if ( TRO ) {
            graphics.drawString( "_______________________", p[PrintConsts.PILOT_NAME].x, p[PrintConsts.PILOT_NAME].y );
            graphics.drawString( "___", p[PrintConsts.PILOT_GUN].x, p[PrintConsts.PILOT_GUN].y);
            graphics.drawString( "___", p[PrintConsts.PILOT_PILOT].x-4, p[PrintConsts.PILOT_PILOT].y);
        } else if( PrintPilot ) {
            graphics.drawString( PilotName, p[PrintConsts.PILOT_NAME].x, p[PrintConsts.PILOT_NAME].y );
            graphics.drawString( Gunnery + "", p[PrintConsts.PILOT_GUN].x, p[PrintConsts.PILOT_GUN].y );
            graphics.drawString( Piloting + "", p[PrintConsts.PILOT_PILOT].x, p[PrintConsts.PILOT_PILOT].y );
        }

        // check boxes
        graphics.setFont( PlainFont );
        String temp = CurMech.GetHeatSinks().LookupName();
        temp = temp.split( " " )[0];
        graphics.drawString( temp, p[PrintConsts.HEATSINK_NUMBER].x, p[PrintConsts.HEATSINK_NUMBER].y + 11 );

        temp = CommonTools.GetTechbaseString( CurMech.GetLoadout().GetTechBase() );
        graphics.drawString( temp, p[PrintConsts.TECH_IS].x, p[PrintConsts.TECH_IS].y );

        graphics.drawString( CurMech.GetYear() + "", p[PrintConsts.TECH_IS].x, p[PrintConsts.TECH_IS].y + 10 );

        if ( !TRO ) {
            //Armor Type
            graphics.setFont(SmallFont);
            if ( CurMech.IsQuad() ) { graphics.setFont(XtraSmallFont); }

            int baseX = points.GetArmorInfoPoints()[Constants.LOC_CT].x;
            int baseY = points.GetArmorInfoPoints()[Constants.LOC_CT].y + 15;

            if ( CurMech.GetArmor().RequiresExtraRules() ) {
                graphics.setFont(SmallBoldFont);
                if ( CurMech.IsQuad() ) { graphics.setFont(XtraSmallBoldFont); }
            }

            String[] parts = CurMech.GetArmor().CritName().trim().split(" ");
            for (String part: parts) {
                if ( !part.trim().isEmpty() ) {
                    int xCoord = baseX - ((part.trim().length() / 2) * 3);
                    graphics.drawString( part, xCoord, baseY );
                    baseY += 10;
                }
            }
            graphics.setFont(PlainFont);

            //Availability Codes
            graphics.drawString(CurMech.GetAvailability().GetBestCombinedCode(), p[PrintConsts.TECH_IS].x, p[PrintConsts.TECH_IS].y+20);
        }

        //heat sinks
        graphics.setFont( PlainFont );
        graphics.drawString( CurMech.GetHeatSinks().GetNumHS() + "", p[PrintConsts.HEATSINK_NUMBER].x, p[PrintConsts.HEATSINK_NUMBER].y );
        graphics.drawString( CurMech.GetHeatSinks().TotalDissipation() + "", p[PrintConsts.HEATSINK_DISSIPATION].x, p[PrintConsts.HEATSINK_DISSIPATION].y );

        // internal information
        graphics.setFont( SmallFont );
        p = points.GetInternalInfoPoints();
        graphics.drawString( CurMech.GetIntStruc().GetCTPoints() + "", p[Constants.LOC_CT].x, p[Constants.LOC_CT].y );
        graphics.drawString( CurMech.GetIntStruc().GetSidePoints() + "", p[Constants.LOC_LT].x, p[Constants.LOC_LT].y );
        graphics.drawString( CurMech.GetIntStruc().GetSidePoints() + "", p[Constants.LOC_RT].x, p[Constants.LOC_RT].y );
        graphics.drawString( CurMech.GetIntStruc().GetArmPoints() + "", p[Constants.LOC_LA].x, p[Constants.LOC_LA].y );
        graphics.drawString( CurMech.GetIntStruc().GetArmPoints() + "", p[Constants.LOC_RA].x, p[Constants.LOC_RA].y );
        graphics.drawString( CurMech.GetIntStruc().GetLegPoints() + "", p[Constants.LOC_LL].x, p[Constants.LOC_LL].y );
        graphics.drawString( CurMech.GetIntStruc().GetLegPoints() + "", p[Constants.LOC_RL].x, p[Constants.LOC_RL].y );

        // armor information
        p = points.GetArmorInfoPoints();
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_HD ) + "", p[Constants.LOC_HD].x, p[Constants.LOC_HD].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_CT ) + "", p[Constants.LOC_CT].x, p[Constants.LOC_CT].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_LT ) + "", p[Constants.LOC_LT].x, p[Constants.LOC_LT].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_RT ) + "", p[Constants.LOC_RT].x, p[Constants.LOC_RT].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA ) + "", p[Constants.LOC_LA].x, p[Constants.LOC_LA].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_RA ) + "", p[Constants.LOC_RA].x, p[Constants.LOC_RA].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL ) + "", p[Constants.LOC_LL].x, p[Constants.LOC_LL].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_RL ) + "", p[Constants.LOC_RL].x, p[Constants.LOC_RL].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_CTR ) + "", p[Constants.LOC_CTR].x, p[Constants.LOC_CTR].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_LTR ) + "", p[Constants.LOC_LTR].x, p[Constants.LOC_LTR].y );
        graphics.drawString( CurMech.GetArmor().GetLocationArmor( Constants.LOC_RTR ) + "", p[Constants.LOC_RTR].x, p[Constants.LOC_RTR].y );
        if( CurMech.GetArmor().GetBAR() < 10 ) {
            graphics.setFont( XtraSmallFont );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_HD].x, p[Constants.LOC_HD].y + 7 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_CT].x - 5, p[Constants.LOC_CT].y + 8 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_LT].x - 4, p[Constants.LOC_LT].y + 7 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_RT].x - 4, p[Constants.LOC_RT].y + 7 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_LA].x - 4, p[Constants.LOC_LA].y + 8 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_RA].x - 5, p[Constants.LOC_RA].y + 8 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_LL].x - 4, p[Constants.LOC_LL].y + 8 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_RL].x - 4, p[Constants.LOC_RL].y + 8 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_CTR].x + 2, p[Constants.LOC_CTR].y + 8 );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_LTR].x + 13, p[Constants.LOC_LTR].y );
            graphics.drawString( "BAR:" + CurMech.GetArmor().GetBAR(), p[Constants.LOC_RTR].x - 22, p[Constants.LOC_RTR].y );
            graphics.setFont( SmallFont );
        }
    }

    private void DrawNonCritable( Graphics2D graphics, String Item, int X, int Y ) {
        // save the old font
        Font OldFont = graphics.getFont();

        // set the new font
        graphics.setFont( OldFont.deriveFont( Font.ITALIC ) );
        graphics.setColor( Grey );
        graphics.drawString( Item, X, Y );
        graphics.setFont( OldFont );
        graphics.setColor( Black );
    }

    private void DrawImages( Graphics2D graphics ) {
        //Mech Image
        if( getMechImage() != null ) {
            // See if we need to scale
            int h = getMechImage().getHeight( null );
            int w = getMechImage().getWidth( null );
            if ( w > 145 || h > 200 ) {
                if ( w > h ) { // resize based on width
                    double resize = 145.0d / w;
                    h = (int) ( h * resize );
                    w = (int) ( w * resize );
                    if( h > 200 ) {
                        // resize again, this time based on height
                        resize = 200.0d / h;
                        h = (int) ( h * resize );
                        w = (int) ( w * resize );
                    }
                } else { // resize based on height
                    double resize = 200.0d / h;
                    h = (int) ( h * resize );
                    w = (int) ( w * resize );
                    if( w > 145 ) {
                        // resize again, this time based on width
                        resize = 145.0d / w;
                        h = (int) ( h * resize );
                        w = (int) ( w * resize );
                    }
                }
            }
            // get the offsets to print the image more or less centered
            int offx = 0; // (int) ( ( 145 - w ) / 2 );
            int offy = 0; // (int) ( ( 200 - h ) / 2 );
            graphics.drawImage( getMechImage(), points.GetMechImageLoc().x + offx, points.GetMechImageLoc().y + offy, w, h, null );
        }

        if ( LogoImage != null ) {
            graphics.drawImage( LogoImage, points.GetLogoImageLoc().x, points.GetLogoImageLoc().y, 50, 50, null );
        }
    }

    private void DrawGrid( Graphics2D graphics ) {
        graphics.setFont( ReallySmallFont );
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

    private Vector GetAmmo() {
        //Output the list of Ammunition
        Vector all = CurMech.GetLoadout().GetNonCore();
        Vector AmmoList = new Vector();
        for ( int index=0; index < all.size(); index++ ) {
            if(  all.get( index ) instanceof Ammunition ) {
                AmmoData CurAmmo = new AmmoData((Ammunition) all.get(index));

                boolean found = false;
                for ( int internal=0; internal < AmmoList.size(); internal++ ) {
                    AmmoData existAmmo = (AmmoData) AmmoList.get(internal);
                    if ( CurAmmo.PrintName.equals( existAmmo.PrintName ) ) {
                        existAmmo.LotSize += CurAmmo.LotSize;
                        found = true;
                        break;
                    }
                }
                if ( !found ) {
                    AmmoList.add(CurAmmo);
                }
            }
        }
        return AmmoList;
    }

    private PlaceableInfo[] SortEquipmentByLocation() {
        Vector v = CurMech.GetLoadout().GetNonCore();
        Vector temp = new Vector();
        abPlaceable[] a = new abPlaceable[v.size()];
        for( int i = 0; i < v.size(); i++ ) {
            if( ! ( v.get( i ) instanceof Ammunition ) ) {
                a[i] = (abPlaceable) v.get( i );
            }
        }

        // now group them by location
        int count = 0;
        abPlaceable b = null;
        PlaceableInfo p = null;
        for( int i = 0; i < a.length; i++ ) {
            if( a[i] != null ) {
                p = new PlaceableInfo();
                b = a[i];
                p.Item = b;
                p.Location = CurMech.GetLoadout().Find( b );
                a[i] = null;
                count ++;
                // search for other matching weapons in the same location
                for( int j = 0; j < a.length; j++ ) {
                    if( a[j] != null ) {
                        if( a[j].CritName().equals( b.CritName() ) ) {
                            if( CurMech.GetLoadout().Find( a[j] ) == p.Location ) {
                                count++;
                                a[j] = null;
                            }
                        }
                    }
                }

                // set the weapon count and add it to the temp vector
                p.Count = count;
                temp.add( p );
                count = 0;
            }
        }

        // produce an array from the vector
        PlaceableInfo[] retval = new PlaceableInfo[temp.size()];
        for( int i = 0; i < temp.size(); i++ ) {
            retval[i] = (PlaceableInfo) temp.get( i );
        }
        return retval;
    }

    private void GetRecordSheet() {
        // loads the correct record sheet and points based on the information given
        RecordSheet = media.GetImage( PrintConsts.RS_TW_BP );
        points = new TWBipedPoints();

        if ( CurMech.IsQuad() ) {
            RecordSheet = media.GetImage( PrintConsts.RS_TW_QD );
            points = new TWQuadPoints();
        }

        if ( Advanced ) {
            RecordSheet = media.GetImage( PrintConsts.RS_TO_BP );
        }
    }

    private String GetPrintName( abPlaceable a ) {
        // returns a modified PrintName, useful for special situations such as
        // mixed-tech mechs.
        String retval = a.CritName();
        if( a instanceof RangedWeapon && CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
            switch( ((RangedWeapon) a).GetTechBase() ) {
                case AvailableCode.TECH_INNER_SPHERE:
                    retval = "(IS) " + retval;
                    break;
                case AvailableCode.TECH_CLAN:
                    retval = "(CL) " + retval;
                    break;
            }
        }
        return retval;
    }


/*    private void RandomArmorDotsLA( Graphics2D graphics, int AV, Point top, Point bottom ) {
        int cols = 3; // using three columns for the left arm.

        // find the number of dots per column
        int numcols = cols;
        int numpercol = AV / cols;
        int overflow = AV % cols;
        boolean centerline = false;
        if( overflow % 2 > 0 ) { centerline = true; }

        // how many columns are we going to use?
        if( numpercol < 2 ) {
            numcols = 1;
            numpercol = AV;
            overflow = 0;
            // overlfow always goes to the centerline
            centerline = true;
        } else if( numpercol < 4 ) {
            numcols = 2;
            numpercol = AV / numcols;
            overflow = AV % numcols;
            // overlfow always goes to the centerline
            centerline = true;
        }

        // find the acceptable offset for each column
        int offx = 

        // draw the circles
        graphics.drawOval( p[i].x, p[i].y, 5, 5 );
    }*/

    private class PlaceableInfo {
        public int Location,
                   Count;
        public abPlaceable Item;
    }

    private class AmmoData {
        public String PrintName;
        public int LotSize;

        public AmmoData( Ammunition ammo ) {
            this.PrintName = ammo.CritName();
            this.LotSize = ammo.GetLotSize();
        }
    }
}
