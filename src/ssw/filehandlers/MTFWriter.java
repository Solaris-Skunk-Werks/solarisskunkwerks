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

package ssw.filehandlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import ssw.*;
import ssw.components.*;

public class MTFWriter {
    // writes the given mech to an MTF file supported by MegaMek.

    private Mech CurMech;

    public MTFWriter( Mech m ) {
        CurMech = m;
    }

    public void WriteMTF( String filename ) throws IOException {
        BufferedWriter FR = new BufferedWriter( new FileWriter( filename ) );

        // first block for vesioning and name
        FR.write( "Version:1.1" );
        FR.newLine();
        FR.write( CurMech.GetName() );
        FR.newLine();
        if( CurMech.IsOmnimech() ) {
            if( CurMech.GetModel().equals( "" ) ) {
                FR.write( CurMech.GetLoadout().GetName() );
            } else {
                FR.write( CurMech.GetModel() + " " + CurMech.GetLoadout().GetName() );
            }
        } else {
            FR.write( CurMech.GetModel() );
        }
        FR.newLine();

        // second block handles general mech stuff
        FR.newLine();
        if( CurMech.IsQuad() ) {
            if( CurMech.IsOmnimech() ) {
                FR.write( "Config:Quad Omnimech" );
            } else {
                FR.write( "Config:Quad" );
            }
        } else {
            if( CurMech.IsOmnimech() ) {
                FR.write( "Config:Biped Omnimech" );
            } else {
                FR.write( "Config:Biped" );
            }
        }
        FR.newLine();
        if( CurMech.IsClan() ) {
            FR.write( "TechBase:Clan" );
        } else {
            FR.write( "TechBase:Inner Sphere" );
        }
        FR.newLine();
        FR.write( "Era:" + CurMech.GetYear() );
        FR.newLine();
        FR.write( "Rules Level:" + CurMech.GetMegaMekLevel() );
        FR.newLine();

        // third block for mech specifics
        FR.newLine();
        FR.write( "Mass:" + CurMech.GetTonnage() );
        FR.newLine();
        FR.write( "Engine:" + CurMech.GetEngine().GetRating() + " " + CurMech.GetEngine().GetCritName() );
        FR.newLine();
        FR.write( "Structure:" + CurMech.GetIntStruc().GetMMName( false ) );
        FR.newLine();
        // check for a wierd gyro config, since MM always wants to see "Gyro" in
        // the crit slots.
        if( ! CurMech.GetGyro().GetCritName().matches( "Gyro" ) ) {
            FR.write( "Gyro:" + CurMech.GetGyro().GetCritName() );
            FR.newLine();
        }
        // check for a small cockpit.
        if( ! CurMech.GetCockpit().GetCritName().equals( "Cockpit") ) {
            FR.write( "Cockpit:" + CurMech.GetCockpit().GetCritName() );
            FR.newLine();
        }
        FR.write( "Myomer:" + CurMech.GetPhysEnhance().GetMMName( false ) );
        FR.newLine();

        // fourth block for movement and heat
        FR.newLine();
        if( CurMech.GetHeatSinks().IsDouble() ) {
            FR.write( "Heat Sinks:" + CurMech.GetHeatSinks().GetNumHS() + " Double" );
        } else {
            FR.write( "Heat Sinks:" + CurMech.GetHeatSinks().GetNumHS() + " Single" );
        }
        FR.newLine();
        FR.write( "Walk MP:" + CurMech.GetWalkingMP() );
        FR.newLine();
        FR.write( "Jump MP:" + CurMech.GetJumpJets().GetNumJJ() );
        FR.newLine();

        // fifth block for armor information
        FR.newLine();
        FR.write( "Armor:" + CurMech.GetArmor().GetMMName( false ) );
        FR.newLine();
        FR.write( "LA Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA) );
        FR.newLine();
        FR.write( "RA Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_RA) );
        FR.newLine();
        FR.write( "LT Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_LT) );
        FR.newLine();
        FR.write( "RT Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_RT) );
        FR.newLine();
        FR.write( "CT Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_CT) );
        FR.newLine();
        FR.write( "HD Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_HD) );
        FR.newLine();
        FR.write( "LL Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL) );
        FR.newLine();
        FR.write( "RL Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_RL) );
        FR.newLine();
        FR.write( "RTL Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_LTR) );
        FR.newLine();
        FR.write( "RTR Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_RTR) );
        FR.newLine();
        FR.write( "RTC Armor:" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_CTR) );
        FR.newLine();

        // sixth block for weapon information.  Get the loadout directly as this
        // will make things much easier
        ifLoadout l = CurMech.GetLoadout();
        FR.newLine();
        Vector v = l.GetNonCore();
        // now we have to split up the vector into equipment and ammo
        Vector eq = new Vector();
        Vector ammo = new Vector();
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof Ammunition ) {
                ammo.add( v.get( i ) );
            } else {
                eq.add( v.get( i ) );
            }
        }
        FR.write( "Weapons:" + eq.size() );
        FR.newLine();
        for( int i = 0; i < eq.size(); i++ ) {
            // each piece of equipment has it's own line.  If it's a weapon,
            // we'll have to find out how much ammo it has as well
            abPlaceable p = (abPlaceable) eq.get( i );
            if( p instanceof ifWeapon ) {
                if( ((ifWeapon) p).HasAmmo() ) {
                    // since we're removing ammo as we add it, ignore the ammo
                    // index if it doesn't exist in the vector
                    int ammoindex = ((ifWeapon) p).GetAmmoIndex();
                    int ammoamount = 0;
                    for( int j = ammo.size() - 1; j >= 0; j-- ) {
                        // we're using the literal ammo lot size, not what the ammo says.
                        if( ((Ammunition) ammo.get( j )).GetAmmoIndex() == ammoindex ) {
                            ammoamount += ((ifWeapon) p).GetAmmo();
                            ammo.remove( j );
                        }
                    }
                    // check for a rear-facing weapon
                    String rear = "";
                    if( p.IsMountedRear() ) {
                        rear = " (R)";
                    }
                    // now that we have the amount, add the line in
                    if( ammoamount > 0 ) {
                        FR.write( "1 " + p.GetMMName( false ) + ", " + Constants.Locs[l.Find( p )] + rear + ", Ammo:" + ammoamount );
                    } else {
                        FR.write( "1 " + p.GetMMName( false ) + ", " + Constants.Locs[l.Find( p )] + rear );
                    }
                } else {
                    // check for a rear-facing weapon
                    String rear = "";
                    if( p.IsMountedRear() ) {
                        rear = " (R)";
                    }
                    // no ammo checking needed
                    FR.write( "1 " + p.GetMMName( false ) + ", " + Constants.Locs[l.Find( p )] + rear );
                }
                FR.newLine();
            } else {
                // not a weapon so no ammo checking.  Add it to the file
                FR.write( "1 " + p.GetMMName( false ) + ", " + Constants.Locs[l.Find( p )] );
                FR.newLine();
            }
            // format is:
            // "1 <weapon lookupname>, <location>, Ammo:<total lot size>
        }

        // start internals.
        FR.newLine();
        FR.write( "Left Arm:" );
        FR.newLine();
        FR.write( l.GetLACrits()[0].GetMMName( l.GetLACrits()[0].IsMountedRear() ) );
        FR.newLine();
        FR.write( l.GetLACrits()[1].GetMMName( l.GetLACrits()[1].IsMountedRear() ) );
        FR.newLine();
        FR.write( l.GetLACrits()[2].GetMMName( l.GetLACrits()[2].IsMountedRear() ) );
        FR.newLine();
        FR.write( l.GetLACrits()[3].GetMMName( l.GetLACrits()[3].IsMountedRear() ) );
        FR.newLine();
        FR.write( l.GetLACrits()[4].GetMMName( l.GetLACrits()[4].IsMountedRear() ) );
        FR.newLine();
        FR.write( l.GetLACrits()[5].GetMMName( l.GetLACrits()[5].IsMountedRear() ) );
        FR.newLine();
        // if a quad, the rest are ignored, otherwise fill them in
        if( CurMech.IsQuad() ) {
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
        } else {
                FR.write( l.GetLACrits()[6].GetMMName( l.GetLACrits()[6].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetLACrits()[7].GetMMName( l.GetLACrits()[7].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetLACrits()[8].GetMMName( l.GetLACrits()[8].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetLACrits()[9].GetMMName( l.GetLACrits()[9].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetLACrits()[10].GetMMName( l.GetLACrits()[10].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetLACrits()[11].GetMMName( l.GetLACrits()[11].IsMountedRear() ) );
            FR.newLine();
        }

        FR.newLine();
        FR.write( "Right Arm:" );
        FR.newLine();
            FR.write( l.GetRACrits()[0].GetMMName( l.GetRACrits()[0].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRACrits()[1].GetMMName( l.GetRACrits()[1].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRACrits()[2].GetMMName( l.GetRACrits()[2].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRACrits()[3].GetMMName( l.GetRACrits()[3].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRACrits()[4].GetMMName( l.GetRACrits()[4].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRACrits()[5].GetMMName( l.GetRACrits()[5].IsMountedRear() ) );
        FR.newLine();
        // if a quad, the rest are ignored, otherwise fill them in
        if( CurMech.IsQuad() ) {
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
            FR.write( "-Empty-" );
            FR.newLine();
        } else {
                FR.write( l.GetRACrits()[6].GetMMName( l.GetRACrits()[6].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetRACrits()[7].GetMMName( l.GetRACrits()[7].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetRACrits()[8].GetMMName( l.GetRACrits()[8].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetRACrits()[9].GetMMName( l.GetRACrits()[9].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetRACrits()[10].GetMMName( l.GetRACrits()[10].IsMountedRear() ) );
            FR.newLine();
                FR.write( l.GetRACrits()[11].GetMMName( l.GetRACrits()[11].IsMountedRear() ) );
            FR.newLine();
        }

        FR.newLine();
        FR.write( "Left Torso:" );
        FR.newLine();
            FR.write( l.GetLTCrits()[0].GetMMName( l.GetLTCrits()[0].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[1].GetMMName( l.GetLTCrits()[1].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[2].GetMMName( l.GetLTCrits()[2].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[3].GetMMName( l.GetLTCrits()[3].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[4].GetMMName( l.GetLTCrits()[4].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[5].GetMMName( l.GetLTCrits()[5].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[6].GetMMName( l.GetLTCrits()[6].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[7].GetMMName( l.GetLTCrits()[7].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[8].GetMMName( l.GetLTCrits()[8].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[9].GetMMName( l.GetLTCrits()[9].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[10].GetMMName( l.GetLTCrits()[10].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLTCrits()[11].GetMMName( l.GetLTCrits()[11].IsMountedRear() ) );
        FR.newLine();

        FR.newLine();
        FR.write( "Right Torso:" );
        FR.newLine();
            FR.write( l.GetRTCrits()[0].GetMMName( l.GetRTCrits()[0].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[1].GetMMName( l.GetRTCrits()[1].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[2].GetMMName( l.GetRTCrits()[2].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[3].GetMMName( l.GetRTCrits()[3].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[4].GetMMName( l.GetRTCrits()[4].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[5].GetMMName( l.GetRTCrits()[5].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[6].GetMMName( l.GetRTCrits()[6].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[7].GetMMName( l.GetRTCrits()[7].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[8].GetMMName( l.GetRTCrits()[8].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[9].GetMMName( l.GetRTCrits()[9].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[10].GetMMName( l.GetRTCrits()[10].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRTCrits()[11].GetMMName( l.GetRTCrits()[11].IsMountedRear() ) );
        FR.newLine();

        FR.newLine();
        FR.write( "Center Torso:" );
        FR.newLine();
            FR.write( l.GetCTCrits()[0].GetMMName( l.GetCTCrits()[0].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[1].GetMMName( l.GetCTCrits()[1].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[2].GetMMName( l.GetCTCrits()[2].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[3].GetMMName( l.GetCTCrits()[3].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[4].GetMMName( l.GetCTCrits()[4].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[5].GetMMName( l.GetCTCrits()[5].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[6].GetMMName( l.GetCTCrits()[6].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[7].GetMMName( l.GetCTCrits()[7].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[8].GetMMName( l.GetCTCrits()[8].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[9].GetMMName( l.GetCTCrits()[9].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[10].GetMMName( l.GetCTCrits()[10].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetCTCrits()[11].GetMMName( l.GetCTCrits()[11].IsMountedRear() ) );
        FR.newLine();

        FR.newLine();
        FR.write( "Head:" );
        FR.newLine();
            FR.write( l.GetHDCrits()[0].GetMMName( l.GetHDCrits()[0].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetHDCrits()[1].GetMMName( l.GetHDCrits()[1].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetHDCrits()[2].GetMMName( l.GetHDCrits()[2].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetHDCrits()[3].GetMMName( l.GetHDCrits()[3].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetHDCrits()[4].GetMMName( l.GetHDCrits()[4].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetHDCrits()[5].GetMMName( l.GetHDCrits()[5].IsMountedRear() ) );
        // needed but ignored in Megamek
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();

        FR.newLine();
        FR.write( "Left Leg:" );
        FR.newLine();
            FR.write( l.GetLLCrits()[0].GetMMName( l.GetLLCrits()[0].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLLCrits()[1].GetMMName( l.GetLLCrits()[1].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLLCrits()[2].GetMMName( l.GetLLCrits()[2].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLLCrits()[3].GetMMName( l.GetLLCrits()[3].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLLCrits()[4].GetMMName( l.GetLLCrits()[4].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetLLCrits()[5].GetMMName( l.GetLLCrits()[5].IsMountedRear() ) );
        // needed but ignored in Megamek
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();

        FR.newLine();
        FR.write( "Right Leg:" );
        FR.newLine();
            FR.write( l.GetRLCrits()[0].GetMMName( l.GetRLCrits()[0].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRLCrits()[1].GetMMName( l.GetRLCrits()[1].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRLCrits()[2].GetMMName( l.GetRLCrits()[2].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRLCrits()[3].GetMMName( l.GetRLCrits()[3].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRLCrits()[4].GetMMName( l.GetRLCrits()[4].IsMountedRear() ) );
        FR.newLine();
            FR.write( l.GetRLCrits()[5].GetMMName( l.GetRLCrits()[5].IsMountedRear() ) );
        FR.newLine();
        // needed but ignored in Megamek
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();
        FR.write( "-Empty-" );
        FR.newLine();

        // all done
        FR.close();
    }
}
