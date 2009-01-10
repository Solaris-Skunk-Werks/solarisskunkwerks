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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Vector;
import ssw.Constants;
import ssw.components.*;
import ssw.filehandlers.FileCommon;
import ssw.gui.frmMain;

public class PrintMech implements Printable {

    private frmMain Parent;
    private Mech CurMech;
    private Image MechImage = null,
                  RecordSheet = null;
    private boolean Advanced = false,
                    Charts = false,
                    PrintPilot = true,
                    UseA4Paper = false;
    private String PilotName = "";
    private int Piloting = 0,
                Gunnery = 0;
    private float BV = 0.0f;
    private ifPrintPoints points = null;
    private Font BoldFont = new Font( "Arial", Font.BOLD, 8 );
    private Font PlainFont = new Font( "Arial", Font.PLAIN, 8 );
    private Font ItalicFont = new Font( "Arial", Font.ITALIC, 8 );
    private Font SmallFont = new Font( "Arial", Font.PLAIN, 7 );
    private Font SmallItalicFont = new Font( "Arial", Font.ITALIC, 7 );

    public PrintMech( frmMain parent, Mech m, Image i, boolean adv, boolean A4 ) {
        Parent = parent;
        CurMech = m;
        MechImage = i;
        Advanced = adv;
        BV = CurMech.GetCurrentBV();
        UseA4Paper = A4;
        GetRecordSheet();
    }

    public void SetPilotData( String pname, int ppilot, int pgun ) {
        PilotName = pname;
        Piloting = ppilot;
        Gunnery = pgun;
    }

    public void SetOptions( boolean charts, boolean PrintP, float UseBV ) {
        Charts = charts;
        BV = UseBV;
        PrintPilot = PrintP;
    }

    public int print( Graphics graphics, PageFormat pageFormat, int pageIndex ) throws PrinterException {
        if( pageIndex >= 1 ) { return Printable.NO_SUCH_PAGE; }
        ((Graphics2D) graphics).translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        if( RecordSheet == null ) {
            return Printable.NO_SUCH_PAGE;
        } else {
            PreparePrint( (Graphics2D) graphics );
            return Printable.PAGE_EXISTS;
        }
    }

    private void PreparePrint( Graphics2D graphics ) {
        // adjust the printable area for A4 paper size
        if( UseA4Paper ) {
            graphics.scale( 0.9705d, 0.9705d );
        }
        
        // adjust the printable area for use with helpful charts
        if( Charts ) {
            graphics.scale( 0.8d, 0.8d );
        }

        graphics.drawImage( RecordSheet, 0, 0, 576, 756, null );

        //if( MechImage != null ) {
        //    graphics.drawImage( MechImage, points.GetMechImageLoc().x, points.GetMechImageLoc().y, 0, 0, null );
        //}

        DrawArmorCircles( graphics );
        DrawInternalCircles( graphics );
        DrawCriticals( graphics );
        DrawMechData( graphics );

        if( Charts ) {
            // reset the scale and add the charts
            graphics.scale( 1.25d, 1.25d );
            AddCharts( graphics );
        }
    }

    private void DrawArmorCircles( Graphics2D graphics ) {
        Point[] p = null;

        p = points.GetArmorHDPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_HD ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorCTPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_CT ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorLTPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_LT ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorRTPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_RT ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorLAPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorRAPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_RA ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorLLPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorRLPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_RL ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorCTRPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_CTR ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorLTRPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_LTR ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetArmorRTRPoints();
        for( int i = 0; i < CurMech.GetArmor().GetLocationArmor( Constants.LOC_RTR ); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }
    }

    private void DrawInternalCircles( Graphics2D graphics ) {
        Point[] p = null;

        p = points.GetInternalHDPoints();
        for( int i = 0; i < CurMech.GetIntStruc().GetHeadPoints(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetInternalCTPoints();
        for( int i = 0; i < CurMech.GetIntStruc().GetCTPoints(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetInternalLTPoints();
        for( int i = 0; i < CurMech.GetIntStruc().GetSidePoints(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetInternalRTPoints();
        for( int i = 0; i < CurMech.GetIntStruc().GetSidePoints(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetInternalLAPoints();
        for( int i = 0; i < CurMech.GetIntStruc().GetArmPoints(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetInternalRAPoints();
        for( int i = 0; i < CurMech.GetIntStruc().GetArmPoints(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetInternalLLPoints();
        for( int i = 0; i < CurMech.GetIntStruc().GetLegPoints(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }

        p = points.GetInternalRLPoints();
        for( int i = 0; i < CurMech.GetIntStruc().GetLegPoints(); i++ ) {
            graphics.drawOval( p[i].x, p[i].y, 5, 5 );
        }
    }

    private void DrawCriticals( Graphics2D graphics ) {
        abPlaceable[] a = null;
        Point[] p = null;
        graphics.setFont( PlainFont );

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_HD );
        p = points.GetCritHDPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( ! a[i].IsCritable() ) {
                graphics.setFont( ItalicFont );
                graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                graphics.setFont( PlainFont );
            } else {
                if( a[i].IsArmored() ) {
                    graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                    graphics.drawString( a[i].GetCritName(), p[i].x + 7, p[i].y );
                } else if( a[i] instanceof Ammunition ) {
                    graphics.drawString( "(" + ((Ammunition) a[i]).GetLotSize() + ") " + a[i].GetCritName(), p[i].x, p[i].y );
                } else {
                    graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_CT );
        p = points.GetCritCTPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( ! a[i].IsCritable() ) {
                graphics.setFont( ItalicFont );
                graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                graphics.setFont( PlainFont );
            } else {
                if( a[i].IsArmored() ) {
                    graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                    graphics.drawString( a[i].GetCritName(), p[i].x + 7, p[i].y );
                } else if( a[i] instanceof Ammunition ) {
                    graphics.drawString( "(" + ((Ammunition) a[i]).GetLotSize() + ") " + a[i].GetCritName(), p[i].x, p[i].y );
                } else {
                    graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_LT );
        p = points.GetCritLTPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( ! a[i].IsCritable() ) {
                graphics.setFont( ItalicFont );
                graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                graphics.setFont( PlainFont );
            } else {
                if( a[i].IsArmored() ) {
                    graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                    graphics.drawString( a[i].GetCritName(), p[i].x + 7, p[i].y );
                } else if( a[i] instanceof Ammunition ) {
                    graphics.drawString( "(" + ((Ammunition) a[i]).GetLotSize() + ") " + a[i].GetCritName(), p[i].x, p[i].y );
                } else {
                    graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_RT );
        p = points.GetCritRTPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( ! a[i].IsCritable() ) {
                graphics.setFont( ItalicFont );
                graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                graphics.setFont( PlainFont );
            } else {
                if( a[i].IsArmored() ) {
                    graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                    graphics.drawString( a[i].GetCritName(), p[i].x + 7, p[i].y );
                } else if( a[i] instanceof Ammunition ) {
                    graphics.drawString( "(" + ((Ammunition) a[i]).GetLotSize() + ") " + a[i].GetCritName(), p[i].x, p[i].y );
                } else {
                    graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_LA );
        p = points.GetCritLAPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( ! a[i].IsCritable() ) {
                graphics.setFont( ItalicFont );
                graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                graphics.setFont( PlainFont );
            } else {
                if( a[i].IsArmored() ) {
                    graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                    graphics.drawString( a[i].GetCritName(), p[i].x + 7, p[i].y );
                } else if( a[i] instanceof Ammunition ) {
                    graphics.drawString( "(" + ((Ammunition) a[i]).GetLotSize() + ") " + a[i].GetCritName(), p[i].x, p[i].y );
                } else {
                    graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_RA );
        p = points.GetCritRAPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( ! a[i].IsCritable() ) {
                graphics.setFont( ItalicFont );
                graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                graphics.setFont( PlainFont );
            } else {
                if( a[i].IsArmored() ) {
                    graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                    graphics.drawString( a[i].GetCritName(), p[i].x + 7, p[i].y );
                } else if( a[i] instanceof Ammunition ) {
                    graphics.drawString( "(" + ((Ammunition) a[i]).GetLotSize() + ") " + a[i].GetCritName(), p[i].x, p[i].y );
                } else {
                    graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_LL );
        p = points.GetCritLLPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( ! a[i].IsCritable() ) {
                graphics.setFont( ItalicFont );
                graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                graphics.setFont( PlainFont );
            } else {
                if( a[i].IsArmored() ) {
                    graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                    graphics.drawString( a[i].GetCritName(), p[i].x + 7, p[i].y );
                } else if( a[i] instanceof Ammunition ) {
                    graphics.drawString( "(" + ((Ammunition) a[i]).GetLotSize() + ") " + a[i].GetCritName(), p[i].x, p[i].y );
                } else {
                    graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                }
            }
        }

        a = CurMech.GetLoadout().GetCrits( Constants.LOC_RL );
        p = points.GetCritRLPoints();
        for( int i = 0; i < a.length && i < p.length; i++ ) {
            if( ! a[i].IsCritable() ) {
                graphics.setFont( ItalicFont );
                graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
                graphics.setFont( PlainFont );
            } else {
                if( a[i].IsArmored() ) {
                    graphics.drawOval( p[i].x, p[i].y - 5, 5, 5 );
                    graphics.drawString( a[i].GetCritName(), p[i].x + 7, p[i].y );
                } else if( a[i] instanceof Ammunition ) {
                    graphics.drawString( "(" + ((Ammunition) a[i]).GetLotSize() + ") " + a[i].GetCritName(), p[i].x, p[i].y );
                } else {
                    graphics.drawString( a[i].GetCritName(), p[i].x, p[i].y );
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
        int offset = 0;
        for( int i = 0; i < a.length; i++ ) {
            PlaceableInfo item = a[i];
            graphics.drawString( item.Count + "", p[0].x, p[0].y + offset );
            graphics.drawString( item.Item.GetCritName(), p[1].x, p[1].y + offset );
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
                graphics.drawString( "--", p[3].x, p[3].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                if( item.Item instanceof MissileWeapon ) {
                    graphics.drawString( ((ifWeapon) item.Item).GetDamageShort() + "/m", p[4].x, p[4].y + offset );
                } else {
                    if( ((ifWeapon) item.Item).GetDamageShort() != ((ifWeapon) item.Item).GetDamageMedium() ||  ((ifWeapon) item.Item).GetDamageShort() != ((ifWeapon) item.Item).GetDamageLong() ||  ((ifWeapon) item.Item).GetDamageMedium() != ((ifWeapon) item.Item).GetDamageLong() ) {
                        graphics.drawString( ((ifWeapon) item.Item).GetDamageShort() + "/" + ((ifWeapon) item.Item).GetDamageMedium() + "/" + ((ifWeapon) item.Item).GetDamageLong(), p[4].x, p[4].y + offset );
                    } else {
                        graphics.drawString( ((ifWeapon) item.Item).GetDamageShort() + "", p[4].x, p[4].y + offset );
                    }
                }
            } else {
                graphics.drawString( "--", p[4].x, p[4].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                graphics.drawString( ((ifWeapon) item.Item).GetRangeMin() + "", p[5].x, p[5].y + offset );
            } else {
                graphics.drawString( "--", p[5].x, p[5].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                graphics.drawString( ((ifWeapon) item.Item).GetRangeShort() + "", p[6].x, p[6].y + offset );
            } else if( item.Item instanceof Equipment ) {
                graphics.drawString( ((Equipment) item.Item).GetShortRange() + "", p[6].x, p[6].y + offset );
            } else {
                graphics.drawString( "--", p[6].x, p[6].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                graphics.drawString( ((ifWeapon) item.Item).GetRangeMedium() + "", p[7].x, p[7].y + offset );
            } else if( item.Item instanceof Equipment ) {
                graphics.drawString( ((Equipment) item.Item).GetMediumRange() + "", p[7].x, p[7].y + offset );
            } else {
                graphics.drawString( "--", p[7].x, p[7].y + offset );
            }
            if( item.Item instanceof ifWeapon ) {
                graphics.drawString( ((ifWeapon) item.Item).GetRangeLong() + "", p[8].x, p[8].y + offset );
            } else if( item.Item instanceof Equipment ) {
                graphics.drawString( ((Equipment) item.Item).GetLongRange() + "", p[8].x, p[8].y + offset );
            } else {
                graphics.drawString( "--", p[8].x, p[8].y + offset );
            }
            offset += 8;
        }

        graphics.setFont( BoldFont );
        p = points.GetDataChartPoints();
        if( CurMech.IsOmnimech() ) {
            graphics.drawString( CurMech.GetName() + " " + CurMech.GetModel() + " " + CurMech.GetLoadout().GetName(), p[PrintConsts.MECHNAME].x, p[PrintConsts.MECHNAME].y );
        } else {
            graphics.drawString( CurMech.GetName() + " " + CurMech.GetModel(), p[PrintConsts.MECHNAME].x, p[PrintConsts.MECHNAME].y );
        }
        if( CurMech.GetAdjustedWalkingMP( false, true ) != CurMech.GetWalkingMP() ) {
            graphics.drawString( CurMech.GetWalkingMP() + " (" + CurMech.GetAdjustedWalkingMP( false, true ) + ")", p[PrintConsts.WALKMP].x, p[PrintConsts.WALKMP].y );
        } else {
            graphics.drawString( CurMech.GetWalkingMP() + "", p[PrintConsts.WALKMP].x, p[PrintConsts.WALKMP].y );
        }
        if( CurMech.GetAdjustedRunningMP( false, true ) != CurMech.GetRunningMP() ) {
            graphics.drawString( CurMech.GetRunningMP() + " (" + CurMech.GetAdjustedRunningMP( false, true ) + ")", p[PrintConsts.RUNMP].x, p[PrintConsts.RUNMP].y );
        } else {
            graphics.drawString( CurMech.GetRunningMP() + "", p[PrintConsts.RUNMP].x, p[PrintConsts.RUNMP].y );
        }
        if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
            graphics.drawString( CurMech.GetJumpJets().GetNumJJ() + " (" + CurMech.GetAdjustedJumpingMP( false ) + ")", p[PrintConsts.JUMPMP].x, p[PrintConsts.JUMPMP].y );
        } else {
            graphics.drawString( CurMech.GetJumpJets().GetNumJJ() + "", p[PrintConsts.JUMPMP].x, p[PrintConsts.JUMPMP].y );
        }
        graphics.drawString( CurMech.GetTonnage() + "", p[PrintConsts.TONNAGE].x, p[PrintConsts.TONNAGE].y );
        graphics.drawString( String.format( "%1$,.0f C-Bills", Math.floor( CurMech.GetTotalCost() + 0.5f ) ), p[PrintConsts.COST].x, p[PrintConsts.COST].y );
        graphics.drawString( String.format( "%1$,.0f", BV ), p[PrintConsts.BV2].x, p[PrintConsts.BV2].y );
        if( PrintPilot ) {
            graphics.drawString( PilotName, p[PrintConsts.PILOT_NAME].x, p[PrintConsts.PILOT_NAME].y );
            graphics.drawString( Gunnery + "", p[PrintConsts.PILOT_GUN].x, p[PrintConsts.PILOT_GUN].y );
            graphics.drawString( Piloting + "", p[PrintConsts.PILOT_PILOT].x, p[PrintConsts.PILOT_PILOT].y );
        }

        // check boxes
        if( CurMech.GetHeatSinks().IsDouble() ) {
            int[][] check = GetCheck( new Point( p[PrintConsts.HEATSINK_DOUBLE].x, p[PrintConsts.HEATSINK_DOUBLE].y ) );
            graphics.drawPolygon( check[0], check[1], 8 );
        } else {
            int[][] check = GetCheck( new Point( p[PrintConsts.HEATSINK_SINGLE].x, p[PrintConsts.HEATSINK_SINGLE].y ) );
            graphics.drawPolygon( check[0], check[1], 8 );
        }
        if( CurMech.IsClan() ) {
            int[][] check = GetCheck( new Point( p[PrintConsts.TECH_CLAN].x, p[PrintConsts.TECH_CLAN].y ) );
            graphics.drawPolygon( check[0], check[1], 8 );
        } else {
            int[][] check = GetCheck( new Point( p[PrintConsts.TECH_IS].x, p[PrintConsts.TECH_IS].y ) );
            graphics.drawPolygon( check[0], check[1], 8 );
        }

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
    }

    private void AddCharts( Graphics2D graphics ) {
        // added for helpful charts, such as to-hit mods and cluster hits.

        // to-hit modifiers
        graphics.setFont( SmallFont );
        graphics.drawString( "Attack Modifiers: Attacker...", 469, 10 );
        graphics.drawString( "  is Stationary", 469, 18 );
        graphics.drawString( "+0", 560, 18 );
        graphics.drawString( "  Walked", 469, 26 );
        graphics.drawString( "+1", 560, 26 );
        graphics.drawString( "  Ran", 469, 34 );
        graphics.drawString( "+2", 560, 34 );
        graphics.drawString( "  Jumped", 469, 42 );
        graphics.drawString( "+3", 560, 42 );
        graphics.drawString( "Attack Modifiers: Damaged...", 469, 50 );
        graphics.drawString( "  Sensors", 469, 58 );
        graphics.drawString( "+2", 560, 58 );
        graphics.drawString( "  Shoulder", 469, 66 );
        graphics.drawString( "+4", 560, 66 );
        graphics.setFont( SmallItalicFont );
        graphics.drawString( "    (weapons in that arm)", 469, 74 );
        graphics.setFont( SmallFont );
        graphics.drawString( "  Arm Actuator", 469, 82 );
        graphics.drawString( "+1", 560, 82 );
        graphics.setFont( SmallItalicFont );
        graphics.drawString( "    (cumulative, in that arm)", 469, 90 );
        graphics.setFont( SmallFont );
        graphics.drawString( "Attack Modifiers: Target...", 469, 98 );
        graphics.drawString( "  Moved 0-2 hexes", 469, 106 );
        graphics.drawString( "+0", 560, 106 );
        graphics.drawString( "  Moved 3-4 hexes", 469, 114 );
        graphics.drawString( "+1", 560, 114 );
        graphics.drawString( "  Moved 5-6 hexes", 469, 122 );
        graphics.drawString( "+2", 560, 122 );
        graphics.drawString( "  Moved 7-9 hexes", 469, 130 );
        graphics.drawString( "+3", 560, 130 );
        graphics.drawString( "  Moved 10-17 hexes", 469, 138 );
        graphics.drawString( "+4", 560, 138 );
        graphics.drawString( "  Moved 18-24 hexes", 469, 146 );
        graphics.drawString( "+5", 560, 146 );
        graphics.drawString( "  Moved 25+ hexes", 469, 154 );
        graphics.drawString( "+6", 560, 154 );
        graphics.drawString( "  Jumped", 469, 162 );
        graphics.drawString( "+1", 560, 162 );
        graphics.setFont( SmallItalicFont );
        graphics.drawString( "    (additional)", 469, 170 );
        graphics.setFont( SmallFont );
        graphics.drawString( "  has Partial Cover", 469, 178 );
        graphics.drawString( "+1", 560, 178 );
        graphics.drawString( "  Prone: Adjacent", 469, 186 );
        graphics.drawString( "-2", 560, 186 );
        graphics.drawString( "  Prone: not Adjacent", 469, 194 );
        graphics.drawString( "+1", 560, 194 );
        graphics.drawString( "Range", 469, 202 );
        graphics.drawString( "  Short", 469, 210 );
        graphics.drawString( "+0", 560, 210 );
        graphics.drawString( "  Medium", 469, 218 );
        graphics.drawString( "+2", 560, 218 );
        graphics.drawString( "  Long", 469, 226 );
        graphics.drawString( "+4", 560, 226 );

        // bounding box
        graphics.drawLine( 575, 3, 575, 229 );
        graphics.drawLine( 466, 3, 466, 229 );
        graphics.drawLine( 466, 3, 575, 3 );
        graphics.drawLine( 466, 229, 575, 229 );

        // main hit location chart
        if( CurMech.IsQuad() ) {
            graphics.drawString( "LRL", 490, 273 );
            graphics.drawString( "RFL", 520, 273 );
            graphics.drawString( "RRL", 550, 273 );
            graphics.drawString( "LFL", 490, 281 );
            graphics.drawString( "RFL", 520, 281 );
            graphics.drawString( "RFL", 550, 281 );
            graphics.drawString( "LFL", 490, 289 );
            graphics.drawString( "RRL", 520, 289 );
            graphics.drawString( "RFL", 550, 289 );
            graphics.drawString( "LRL", 490, 297 );
            graphics.drawString( "RRL", 550, 297 );
            graphics.drawString( "LRL", 520, 321 );
            graphics.drawString( "RFL", 490, 329 );
            graphics.drawString( "LFL", 520, 329 );
            graphics.drawString( "LFL", 550, 329 );
            graphics.drawString( "RRL", 490, 337 );
            graphics.drawString( "LFL", 520, 337 );
            graphics.drawString( "LRL", 550, 337 );
        } else {
            graphics.drawString( "LL", 490, 273 );
            graphics.drawString( "RA", 520, 273 );
            graphics.drawString( "RL", 550, 273 );
            graphics.drawString( "LA", 490, 281 );
            graphics.drawString( "RA", 520, 281 );
            graphics.drawString( "RA", 550, 281 );
            graphics.drawString( "LA", 490, 289 );
            graphics.drawString( "RL", 520, 289 );
            graphics.drawString( "RA", 550, 289 );
            graphics.drawString( "LL", 490, 297 );
            graphics.drawString( "RL", 550, 297 );
            graphics.drawString( "LL", 520, 321 );
            graphics.drawString( "RA", 490, 329 );
            graphics.drawString( "LA", 520, 329 );
            graphics.drawString( "LA", 550, 329 );
            graphics.drawString( "RL", 490, 337 );
            graphics.drawString( "LA", 520, 337 );
            graphics.drawString( "LL", 550, 337 );
        }
        graphics.drawString( "Main Hit Location Chart", 469, 241 );
        graphics.drawString( "Roll", 469, 249 );
        graphics.drawString( "Left", 489, 249 );
        graphics.drawString( "Fr/Bk", 516, 249 );
        graphics.drawString( "Right", 549, 249 );
        graphics.drawString( "2", 474, 257 );
        graphics.drawString( "LT", 490, 257 );
        graphics.drawString( "CT", 520, 257 );
        graphics.drawString( "RT", 550, 257 );
        graphics.drawString( "", 474, 265 );
        graphics.drawString( "(crit)", 490, 265 );
        graphics.drawString( "(crit)", 520, 265 );
        graphics.drawString( "(crit)", 550, 265 );
        graphics.drawString( "3", 474, 273 );
        graphics.drawString( "4", 474, 281 );
        graphics.drawString( "5", 474, 289 );
        graphics.drawString( "6", 474, 297 );
        graphics.drawString( "RT", 520, 297 );
        graphics.drawString( "7", 474, 305 );
        graphics.drawString( "LT", 490, 305 );
        graphics.drawString( "CT", 520, 305 );
        graphics.drawString( "RT", 550, 305 );
        graphics.drawString( "8", 474, 313 );
        graphics.drawString( "CT", 490, 313 );
        graphics.drawString( "LT", 520, 313 );
        graphics.drawString( "CT", 550, 313 );
        graphics.drawString( "9", 474, 321 );
        graphics.drawString( "RT", 490, 321 );
        graphics.drawString( "LT", 550, 321 );
        graphics.drawString( "10", 474, 329 );
        graphics.drawString( "11", 474, 337 );
        graphics.drawString( "12", 474, 345 );
        graphics.drawString( "HD", 490, 345 );
        graphics.drawString( "HD", 520, 345 );
        graphics.drawString( "HD", 550, 345 );

        // bounding box
        graphics.drawLine( 466, 233, 575, 233 );
        graphics.drawLine( 575, 233, 575, 348 );
        graphics.drawLine( 466, 233, 466, 348 );
        graphics.drawLine( 466, 348, 575, 348 );

        // punch hit location chart
        if( CurMech.IsQuad() ) {
            graphics.drawString( "LFL/LRL", 513, 376 );
            graphics.drawString( "LFL", 490, 400 );
            graphics.drawString( "RFL", 550, 400 );
            graphics.drawString( "LRL", 490, 408 );
            graphics.drawString( "RFL/RRL", 513, 408 );
            graphics.drawString( "RRL", 550, 408 );
        } else {
            graphics.drawString( "LA", 520, 376 );
            graphics.drawString( "LA", 490, 400 );
            graphics.drawString( "RA", 550, 400 );
            graphics.drawString( "LA", 490, 408 );
            graphics.drawString( "RA", 520, 408 );
            graphics.drawString( "RA", 550, 408 );
        }
        graphics.drawString( "Punch Hit Location Chart", 469, 360 );
        graphics.drawString( "Roll", 469, 368 );
        graphics.drawString( "Left", 489, 368 );
        graphics.drawString( "Fr/Bk", 516, 368 );
        graphics.drawString( "Right", 549, 368 );
        graphics.drawString( "1", 474, 376 );
        graphics.drawString( "LT", 490, 376 );
        graphics.drawString( "RT", 550, 376 );
        graphics.drawString( "2", 474, 384 );
        graphics.drawString( "LT", 490, 384 );
        graphics.drawString( "LT", 520, 384 );
        graphics.drawString( "RT", 550, 384 );
        graphics.drawString( "3", 474, 392 );
        graphics.drawString( "CT", 490, 392 );
        graphics.drawString( "CT", 520, 392 );
        graphics.drawString( "CT", 550, 392 );
        graphics.drawString( "4", 474, 400 );
        graphics.drawString( "RT", 520, 400 );
        graphics.drawString( "5", 474, 408 );
        graphics.drawString( "6", 474, 416 );
        graphics.drawString( "HD", 490, 416 );
        graphics.drawString( "HD", 520, 416 );
        graphics.drawString( "HD", 550, 416 );

        // bounding box
        graphics.drawLine( 466, 352, 575, 352 );
        graphics.drawLine( 466, 352, 466, 419 );
        graphics.drawLine( 575, 352, 575, 419 );
        graphics.drawLine( 466, 419, 575, 419 );

        // kick hit location chart
        if( CurMech.IsQuad() ) {
            graphics.drawString( "LFL", 490, 447 );
            graphics.drawString( "RFL/RRL", 513, 447 );
            graphics.drawString( "RFL", 550, 447 );
            graphics.drawString( "LRL", 490, 455 );
            graphics.drawString( "LFL/LRL", 513, 455 );
            graphics.drawString( "RRL", 550, 455 );
        } else {
            graphics.drawString( "LL", 490, 447 );
            graphics.drawString( "RL", 520, 447 );
            graphics.drawString( "RL", 550, 447 );
            graphics.drawString( "LL", 490, 455 );
            graphics.drawString( "LL", 520, 455 );
            graphics.drawString( "RL", 550, 455 );
        }
        graphics.drawString( "Kick Hit Location Chart", 469, 431 );
        graphics.drawString( "Roll", 469, 439 );
        graphics.drawString( "Left", 489, 439 );
        graphics.drawString( "Fr/Bk", 516, 439 );
        graphics.drawString( "Right", 549, 439 );
        graphics.drawString( "1-3", 469, 447 );
        graphics.drawString( "4-6", 469, 455 );

        // bounding box
        graphics.drawLine( 466, 423, 575, 423 );
        graphics.drawLine( 466, 423, 466, 458 );
        graphics.drawLine( 575, 423, 575, 458 );
        graphics.drawLine( 466, 458, 575, 458 );

        // cluster hits table
        graphics.drawString( "Cluster Hits Table", 469, 470 );
        graphics.drawString( "Roll", 467, 478 );
        graphics.drawString( "2", 484, 478 );
        graphics.drawString( "3", 492, 478 );
        graphics.drawString( "4", 500, 478 );
        graphics.drawString( "5", 508, 478 );
        graphics.drawString( "6", 516, 478 );
        graphics.drawString( "9", 524, 478 );
        graphics.drawString( "10", 532, 478 );
        graphics.drawString( "12", 543, 478 );
        graphics.drawString( "15", 554, 478 );
        graphics.drawString( "20", 565, 478 );
        graphics.drawString( "2", 469, 488 );
        graphics.drawString( "1", 484, 488 );
        graphics.drawString( "1", 492, 488 );
        graphics.drawString( "1", 500, 488 );
        graphics.drawString( "1", 508, 488 );
        graphics.drawString( "2", 516, 488 );
        graphics.drawString( "3", 524, 488 );
        graphics.drawString( " 3", 532, 488 );
        graphics.drawString( " 4", 543, 488 );
        graphics.drawString( " 5", 554, 488 );
        graphics.drawString( " 6", 565, 488 );
        graphics.drawString( "3", 469, 496 );
        graphics.drawString( "1", 484, 496 );
        graphics.drawString( "1", 492, 496 );
        graphics.drawString( "2", 500, 496 );
        graphics.drawString( "2", 508, 496 );
        graphics.drawString( "2", 516, 496 );
        graphics.drawString( "3", 524, 496 );
        graphics.drawString( " 3", 532, 496 );
        graphics.drawString( " 4", 543, 496 );
        graphics.drawString( " 5", 554, 496 );
        graphics.drawString( " 6", 565, 496 );
        graphics.drawString( "4", 469, 504 );
        graphics.drawString( "1", 484, 504 );
        graphics.drawString( "1", 492, 504 );
        graphics.drawString( "2", 500, 504 );
        graphics.drawString( "2", 508, 504 );
        graphics.drawString( "3", 516, 504 );
        graphics.drawString( "4", 524, 504 );
        graphics.drawString( " 4", 532, 504 );
        graphics.drawString( " 5", 543, 504 );
        graphics.drawString( " 6", 554, 504 );
        graphics.drawString( " 9", 565, 504 );
        graphics.drawString( "5", 469, 512 );
        graphics.drawString( "1", 484, 512 );
        graphics.drawString( "2", 492, 512 );
        graphics.drawString( "2", 500, 512 );
        graphics.drawString( "3", 508, 512 );
        graphics.drawString( "3", 516, 512 );
        graphics.drawString( "5", 524, 512 );
        graphics.drawString( " 6", 532, 512 );
        graphics.drawString( " 8", 543, 512 );
        graphics.drawString( " 9", 554, 512 );
        graphics.drawString( "12", 565, 512 );
        graphics.drawString( "6", 469, 520 );
        graphics.drawString( "1", 484, 520 );
        graphics.drawString( "2", 492, 520 );
        graphics.drawString( "2", 500, 520 );
        graphics.drawString( "3", 508, 520 );
        graphics.drawString( "4", 516, 520 );
        graphics.drawString( "5", 524, 520 );
        graphics.drawString( " 6", 532, 520 );
        graphics.drawString( " 8", 543, 520 );
        graphics.drawString( " 9", 554, 520 );
        graphics.drawString( "12", 565, 520 );
        graphics.drawString( "7", 469, 528 );
        graphics.drawString( "1", 484, 528 );
        graphics.drawString( "2", 492, 528 );
        graphics.drawString( "3", 500, 528 );
        graphics.drawString( "3", 508, 528 );
        graphics.drawString( "4", 516, 528 );
        graphics.drawString( "5", 524, 528 );
        graphics.drawString( " 6", 532, 528 );
        graphics.drawString( " 8", 543, 528 );
        graphics.drawString( " 9", 554, 528 );
        graphics.drawString( "12", 565, 528 );
        graphics.drawString( "8", 469, 536 );
        graphics.drawString( "2", 484, 536 );
        graphics.drawString( "2", 492, 536 );
        graphics.drawString( "3", 500, 536 );
        graphics.drawString( "3", 508, 536 );
        graphics.drawString( "4", 516, 536 );
        graphics.drawString( "5", 524, 536 );
        graphics.drawString( " 6", 532, 536 );
        graphics.drawString( " 8", 543, 536 );
        graphics.drawString( " 9", 554, 536 );
        graphics.drawString( "12", 565, 536 );
        graphics.drawString( "9", 469, 544 );
        graphics.drawString( "2", 484, 544 );
        graphics.drawString( "2", 492, 544 );
        graphics.drawString( "3", 500, 544 );
        graphics.drawString( "4", 508, 544 );
        graphics.drawString( "5", 516, 544 );
        graphics.drawString( "7", 524, 544 );
        graphics.drawString( " 8", 532, 544 );
        graphics.drawString( "10", 543, 544 );
        graphics.drawString( "12", 554, 544 );
        graphics.drawString( "16", 565, 544 );
        graphics.drawString( "10", 469, 552 );
        graphics.drawString( "2", 484, 552 );
        graphics.drawString( "3", 492, 552 );
        graphics.drawString( "3", 500, 552 );
        graphics.drawString( "4", 508, 552 );
        graphics.drawString( "5", 516, 552 );
        graphics.drawString( "7", 524, 552 );
        graphics.drawString( " 8", 532, 552 );
        graphics.drawString( "10", 543, 552 );
        graphics.drawString( "12", 554, 552 );
        graphics.drawString( "16", 565, 552 );
        graphics.drawString( "11", 469, 560 );
        graphics.drawString( "2", 484, 560 );
        graphics.drawString( "3", 492, 560 );
        graphics.drawString( "4", 500, 560 );
        graphics.drawString( "5", 508, 560 );
        graphics.drawString( "6", 516, 560 );
        graphics.drawString( "9", 524, 560 );
        graphics.drawString( "10", 532, 560 );
        graphics.drawString( "12", 543, 560 );
        graphics.drawString( "15", 554, 560 );
        graphics.drawString( "20", 565, 560 );
        graphics.drawString( "12", 469, 568 );
        graphics.drawString( "2", 484, 568 );
        graphics.drawString( "3", 492, 568 );
        graphics.drawString( "4", 500, 568 );
        graphics.drawString( "5", 508, 568 );
        graphics.drawString( "6", 516, 568 );
        graphics.drawString( "9", 524, 568 );
        graphics.drawString( "10", 532, 568 );
        graphics.drawString( "12", 543, 568 );
        graphics.drawString( "15", 554, 568 );
        graphics.drawString( "20", 565, 568 );

        // bounding box
        graphics.drawLine( 466, 462, 575, 462 );
        graphics.drawLine( 466, 462, 466, 571 );
        graphics.drawLine( 575, 462, 575, 571 );
        graphics.drawLine( 466, 571, 575, 571 );
        graphics.drawLine( 480, 480, 575, 480 );
        graphics.drawLine( 480, 480, 480, 571 );

        // common melee weapons
        graphics.drawString( "Common Melee Weapons", 469, 583 );
        graphics.drawString( "+Hit", 494, 591 );
        graphics.drawString( "Table", 514, 591 );
        graphics.drawString( "Arc", 539, 591 );
        graphics.drawString( "TSM", 557, 591 );
        graphics.drawString( "Punch", 469, 599 );
        graphics.drawString( "+0", 494, 599 );
        graphics.drawString( "Punch", 514, 599 );
        graphics.drawString( "Arm", 539, 599 );
        graphics.drawString( "Yes", 557, 599 );
        graphics.drawString( "Kick", 469, 607 );
        graphics.drawString( "-2", 494, 607 );
        graphics.drawString( "Kick", 514, 607 );
        graphics.drawString( "Frnt", 539, 607 );
        graphics.drawString( "Yes", 557, 607 );
        graphics.drawString( "Hatchet", 469, 615 );
        graphics.drawString( "-1", 494, 623 );
        graphics.drawString( "Main*", 514, 623 );
        graphics.drawString( "Arm", 539, 623 );
        graphics.drawString( "Yes", 557, 623 );
        graphics.drawString( "Sword", 469, 631 );
        graphics.drawString( "-2", 494, 631 );
        graphics.drawString( "Main*", 514, 631 );
        graphics.drawString( "Arm", 539, 631 );
        graphics.drawString( "Yes", 557, 631 );
        graphics.drawString( "Retractable Blade", 469, 639 );
        graphics.drawString( "-2", 494, 647 );
        graphics.drawString( "Main*", 514, 647 );
        graphics.drawString( "Arm", 539, 647 );
        graphics.drawString( "Yes", 557, 647 );
        graphics.setFont( SmallItalicFont );
        graphics.drawString( "(* May use punch table with", 469, 663 );
        graphics.drawString( "  an additional +4 to hit)", 469, 671 );
        graphics.setFont( SmallFont );

        // bounding box
        graphics.drawLine( 466, 575, 575, 575 );
        graphics.drawLine( 466, 575, 466, 674 );
        graphics.drawLine( 575, 575, 575, 674 );
        graphics.drawLine( 466, 674, 575, 674 );

        // critical hit chart
        graphics.drawString( "Determining Critical Hits", 469, 686 );
        graphics.drawString( "Roll", 469, 694 );
        graphics.drawString( "Effect", 494, 694 );
        graphics.drawString( "2-7", 469, 702 );
        graphics.drawString( "None", 494, 702 );
        graphics.drawString( "8-9", 469, 710 );
        graphics.drawString( "One Critical", 494, 710 );
        graphics.drawString( "10-11", 469, 718 );
        graphics.drawString( "Two Criticals", 494, 718 );
        graphics.drawString( "12", 469, 730 );
        graphics.drawString( "Head/Limb blown off", 494, 726 );
        graphics.drawString( "  or Three Criticals", 494, 734 );
        graphics.setFont( SmallItalicFont );
        graphics.drawString( "  (torso only)", 494, 742 );
        graphics.setFont( SmallFont );

        // bounding box
        graphics.drawLine( 466, 678, 678, 678 );
        graphics.drawLine( 466, 678, 466, 745 );
        graphics.drawLine( 575, 678, 575, 745 );
        graphics.drawLine( 466, 745, 575, 745 );

        // turn record
        graphics.drawString( "Turn", 4, 615 );
        graphics.drawString( "Type", 27, 615 );
        graphics.drawString( "Num", 53, 615 );
        graphics.drawString( "To-Hit", 77, 615 );
        graphics.drawString( "Prev", 114, 615 );
        graphics.drawString( "+Move", 139, 615 );
        graphics.drawString( "+Weap", 169, 615 );
        graphics.drawString( "+Other", 198, 615 );
        graphics.drawString( "=Total", 229, 615 );
        graphics.drawString( "-Heat", 261, 615 );
        graphics.drawString( "=Heat", 290, 615 );
        graphics.drawString( "#", 9, 623 );
        graphics.drawString( "W/R/J", 26, 623 );
        graphics.drawString( "Hexes", 51, 623 );
        graphics.drawString( "Mod", 81, 623 );
        graphics.drawString( "Heat", 113, 623 );
        graphics.drawString( "Heat", 143, 623 );
        graphics.drawString( "Heat", 173, 623 );
        graphics.drawString( "Heat", 203, 623 );
        graphics.drawString( "Heat", 233, 623 );
        graphics.drawString( "Sunk", 262, 623 );
        graphics.drawString( "Level", 292, 623 );
        graphics.drawString( "Notes", 321, 623 );
        graphics.drawString( "1", 9, 634 );
        graphics.drawString( "2", 9, 644 );
        graphics.drawString( "3", 9, 654 );
        graphics.drawString( "4", 9, 664 );
        graphics.drawString( "5", 9, 674 );
        graphics.drawString( "6", 9, 684 );
        graphics.drawString( "7", 9, 694 );
        graphics.drawString( "8", 9, 704 );
        graphics.drawString( "9", 9, 714 );
        graphics.drawString( "10", 6, 724 );
        graphics.drawString( "11", 6, 734 );
        graphics.drawString( "12", 6, 744 );
        graphics.drawString( "13", 6, 754 );

        // graph
        graphics.drawLine( 23, 607, 23, 756 );
        graphics.drawLine( 48, 607, 48, 756 );
        graphics.drawLine( 74, 607, 74, 756 );
        graphics.drawLine( 104, 607, 104, 756 );
        graphics.drawLine( 106, 607, 106, 756 );
        graphics.drawLine( 136, 607, 136, 756 );
        graphics.drawLine( 166, 607, 166, 756 );
        graphics.drawLine( 196, 607, 196, 756 );
        graphics.drawLine( 226, 607, 226, 756 );
        graphics.drawLine( 256, 607, 256, 756 );
        graphics.drawLine( 286, 607, 286, 756 );
        graphics.drawLine( 316, 607, 316, 756 );
        graphics.drawLine( 318, 607, 318, 755 );
        graphics.drawLine( 0, 626, 462, 626 );
        graphics.drawLine( 0, 636, 462, 636 );
        graphics.drawLine( 0, 646, 462, 646 );
        graphics.drawLine( 0, 656, 462, 656 );
        graphics.drawLine( 0, 666, 462, 666 );
        graphics.drawLine( 0, 676, 462, 676 );
        graphics.drawLine( 0, 686, 462, 686 );
        graphics.drawLine( 0, 696, 462, 696 );
        graphics.drawLine( 0, 706, 462, 706 );
        graphics.drawLine( 0, 716, 462, 716 );
        graphics.drawLine( 0, 726, 462, 726 );
        graphics.drawLine( 0, 736, 462, 736 );
        graphics.drawLine( 0, 746, 462, 746 );

        // bounding box
        graphics.drawLine( 0, 607, 462, 607 );
        graphics.drawLine( 0, 607, 0, 756 );
        graphics.drawLine( 462, 607, 462, 756 );
        graphics.drawLine( 0, 756, 462, 756 );
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
                        if( a[j].GetCritName().equals( b.GetCritName() ) ) {
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
        if( CurMech.IsQuad() ) {
            if( Advanced ) {
                RecordSheet = Parent.GetImage( PrintConsts.RS_TO_QD );
            } else {
                RecordSheet = Parent.GetImage( PrintConsts.RS_TW_QD );
                points = new TWQuadPoints();
            }
        } else {
            if( Advanced ) {
                RecordSheet = Parent.GetImage( PrintConsts.RS_TO_BP );
            } else {
                RecordSheet = Parent.GetImage( PrintConsts.RS_TW_BP );
                points = new TWBipedPoints();
            }
        }
    }

    private int[][] GetCheck( Point p ) {
        int[][] retval = { { 0, 1, 1, 2, 8, 9, 3, 0 }, { 3, 3, 6, 6, 0, 0, 7, 7 } };
        // use the given point as a transform to create the checkbox
        for( int i = 0; i < 8; i++ ) {
            retval[0][i] += p.x;
            retval[1][i] += p.y;
        }
        return retval;
    }

    private class PlaceableInfo {
        public int Location,
                   Count;
        public abPlaceable Item;
    }
}
