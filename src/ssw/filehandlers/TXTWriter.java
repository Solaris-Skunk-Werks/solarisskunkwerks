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

import ssw.utilities.CommonTools;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import ssw.*;
import ssw.battleforce.BattleForceStats;
import ssw.battleforce.BattleForceTools;
import ssw.components.*;

public class TXTWriter {

    private Mech CurMech;
    private String NL;
    public boolean CurrentLoadoutOnly = false;

    public TXTWriter( Mech m ) {
        CurMech = m;
        NL = System.getProperty( "line.separator" );
    }

// This is a text formating string (80 chars) I keep around for when it's needed
//        "----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+"
    public void WriteTXT( String filename ) throws IOException {
        BufferedWriter FR = new BufferedWriter( new FileWriter( filename ) );

        // get the text export and write it
        FR.write( GetTextExport() );

        // all done
        FR.close();
    }

    public String GetChatStats( Mech m ) {
        // a fun convenience routine for those who talk in chat or forums.
        String retval = "";

        // pondering some sort of GetChatName() for abplaceables.
        retval += m.GetTonnage() + " tons, " + m.GetAdjustedWalkingMP( false, true ) + "/" + m.GetAdjustedRunningMP( false, true ) + "/" + m.GetAdjustedJumpingMP( false );

        return retval;
    }

    public String GetTextExport() {
        String retval = "";

        if( CurMech.IsOmnimech() ) {
            // start out with the base chassis.  we'll switch later when needed
            CurMech.SetCurLoadout( Constants.BASELOADOUT_NAME );
        }
        retval += CurMech.GetName() + " " + CurMech.GetModel() + NL + NL;
        retval += "Mass: " + CurMech.GetTonnage() + " tons" + NL;
        if( CurMech.IsPrimitive() )  {
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurMech.GetTechBase() ) + " (Primitive)" + NL;
        } else {
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurMech.GetTechBase() ) + NL;
        }
        if( CurMech.IsQuad() ) {
            if( CurMech.IsOmnimech() ) {
                retval += "Chassis Config: Quad Omnimech" + NL;
            } else {
                if( CurMech.IsIndustrialmech() ) {
                    retval += "Chassis Config: Quad IndustrialMech" + NL;
                } else {
                    retval += "Chassis Config: Quad" + NL;
                }
            }
        } else {
            if( CurMech.IsOmnimech() ) {
                retval += "Chassis Config: Biped Omnimech" + NL;
            } else {
                if( CurMech.IsIndustrialmech() ) {
                    retval += "Chassis Config: Biped IndustrialMech" + NL;
                } else {
                    retval += "Chassis Config: Biped" + NL;
                }
            }
        }
        retval += "Rules Level: " + CommonTools.GetRulesLevelString( CurMech.GetRulesLevel() ) + NL;
        retval += "Era: " + CommonTools.DecodeEra( CurMech.GetEra() ) + NL;
        retval += "Tech Rating/Era Availability: " + CurMech.GetAvailability().GetBestCombinedCode() + NL;
        retval += "Production Year: " + CurMech.GetYear() + NL;
        retval += "Cost: " + String.format( "%1$,.0f", Math.floor( CurMech.GetTotalCost() + 0.5 ) ) + " C-Bills" + NL;
        retval += "Battle Value: " + String.format( "%1$,d", CurMech.GetCurrentBV() ) + NL + NL;

        retval += "Chassis: " + CurMech.GetChassisModel() + " " + CurMech.GetIntStruc().CritName() + NL;
        retval += "Power Plant: " + CurMech.GetEngineManufacturer() + " " + CurMech.GetEngine().GetRating() + " " + CurMech.GetEngine() + NL;
        if( CurMech.GetAdjustedWalkingMP( false, true ) != CurMech.GetWalkingMP() ) {
            retval += "Walking Speed: " + ( CurMech.GetWalkingMP() * 10.75 ) + " km/h (" + ( CurMech.GetAdjustedWalkingMP( false, true ) * 10.75 ) + " km/h)" + NL;
        } else {
            retval += "Walking Speed: " + ( CurMech.GetWalkingMP() * 10.75 ) + " km/h" + NL;
        }
        if( CurMech.GetAdjustedRunningMP( false, true ) != CurMech.GetRunningMP() ) {
            retval += "Maximum Speed: " + ( CurMech.GetRunningMP() * 10.75 ) + " km/h (" + ( CurMech.GetAdjustedRunningMP( false, true ) * 10.75 ) + " km/h)" + NL;
        } else {
            retval += "Maximum Speed: " + ( CurMech.GetRunningMP() * 10.75 ) + " km/h" + NL;
        }
        retval += "Jump Jets: " + CurMech.GetJJModel() + NL;
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
                retval += "    Jump Capacity: " + ( CurMech.GetJumpJets().GetNumJJ() * 30 ) + " meters (" + ( CurMech.GetAdjustedJumpingMP( false ) * 30 ) + " meters)" + NL;
            } else {
                retval += "    Jump Capacity: " + ( CurMech.GetJumpJets().GetNumJJ() * 30 ) + " meters" + NL;
            }
        }
        if( CurMech.HasCTCase()|| CurMech.HasLTCase() || CurMech.HasRTCase() ) {
            retval += "Armor: " + CurMech.GetArmorModel() + " " + CurMech.GetArmor().CritName() + " w/ CASE" + NL;
        } else {
            retval += "Armor: " + CurMech.GetArmorModel() + " " + CurMech.GetArmor().CritName() + NL;
        }
        retval += "Armament:" + NL;
        retval += GetArmament();
        retval += "Manufacturer: " + CurMech.GetCompany() + NL;
        retval += "    Primary Factory: " + CurMech.GetLocation() + NL;
        retval += BuildComputerBlock() + NL + NL;
//        retval += "================================================================================" + NL;
        if( ! CurMech.GetOverview().equals( "" ) ) {
            retval += "Overview:" + NL;
            retval += FormatFluff( CurMech.GetOverview() ) + NL + NL;
        }
        if( ! CurMech.GetCapabilities().equals( "" ) ) {
            retval += "Capabilities:" + NL;
            retval += FormatFluff( CurMech.GetCapabilities() ) + NL + NL;
        }
        if( ! CurMech.GetHistory().equals( "" ) ) {
            retval += "Battle History:" + NL;
            retval += FormatFluff( CurMech.GetHistory() ) + NL + NL;
        }
        if( ! CurMech.GetDeployment().equals( "" ) ) {
            retval += "Deployment:" + NL;
            retval += FormatFluff( CurMech.GetDeployment() ) + NL + NL;
        }
        if( ! CurMech.GetVariants().equals( "" ) ) {
            retval += "Variants:" + NL;
            retval += FormatFluff( CurMech.GetVariants() ) + NL + NL;
        }
        if( ! CurMech.GetNotables().equals( "" ) ) {
            retval += "Notable 'Mechs & MechWarriors: " + NL;
            retval += FormatFluff( CurMech.GetNotables() ) + NL + NL;
        }
        retval += "================================================================================" + NL;
        retval += GetMiniTextExport();

        return retval;
    }

    public String GetMiniTextExport() {
        String retval = "";

        if ( CurrentLoadoutOnly ) {
            retval += CurMech.GetName() + " " + CurMech.GetModel() + NL + NL;
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurMech.GetTechBase() ) + NL;
            retval += "Chassis Config: ";
            String chassisString = "Biped";
            if ( CurMech.IsQuad() ) { chassisString.replace("Biped", "Quad"); }
            if ( CurMech.IsOmnimech() ) { chassisString += " Omnimech"; }
            if ( CurMech.IsIndustrialmech() ) { chassisString += " IndustrialMech"; }
            retval += chassisString + NL;
            retval += String.format( "Era: %1$-56s Cost: %2$,.0f", CommonTools.DecodeEra( CurMech.GetEra() ), Math.floor( CurMech.GetTotalCost() + 0.5 ) ) + NL;
            retval += String.format( "Tech Rating/Era Availability: %1$-32s BV2: %2$,d", CurMech.GetAvailability().GetBestCombinedCode(), CurMech.GetCurrentBV() ) + NL + NL;
        }
        retval += "Equipment           Type                         Rating                   Mass  " + NL;
        retval += "--------------------------------------------------------------------------------" + NL;
        retval += String.format( "Internal Structure: %1$-28s %2$3s points              %3$6.2f", CurMech.GetIntStruc().CritName(), CurMech.GetIntStruc().GetTotalPoints(), CurMech.GetIntStruc().GetTonnage() ) + NL;
        if( CurMech.GetIntStruc().NumCrits() > 0 ) {
            retval += "    Internal Locations: " + FileCommon.GetInternalLocations( CurMech ) + NL;
        }
        retval += String.format( "Engine:             %1$-28s %2$3s                     %3$6.2f", CurMech.GetEngine().CritName(), CurMech.GetEngine().GetRating(), CurMech.GetEngine().GetTonnage() ) + NL;
        if( CurMech.GetWalkingMP() != CurMech.GetAdjustedWalkingMP( false , true ) ) {
            retval += "    Walking MP: " + CurMech.GetWalkingMP() + " (" + CurMech.GetAdjustedWalkingMP( false, true ) + ")" + NL;
        } else {
            retval += "    Walking MP: " + CurMech.GetWalkingMP() + NL;
        }
        if( CurMech.GetRunningMP() != CurMech.GetAdjustedRunningMP( false, true ) ) {
            retval += "    Running MP: " + CurMech.GetRunningMP() + " (" + CurMech.GetAdjustedRunningMP( false, true ) + ")" + NL;
        } else {
            retval += "    Running MP: " + CurMech.GetRunningMP() + NL;
        }
        if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
            retval += "    Jumping MP: " + CurMech.GetJumpJets().GetNumJJ() + " (" + CurMech.GetAdjustedJumpingMP( false ) + ")";
        } else {
            retval += "    Jumping MP: " + CurMech.GetJumpJets().GetNumJJ();
        }
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            if( CurMech.GetJumpJets().IsImproved() ) {
                retval += "  (Improved)";
            } else {
                retval += "  (Standard)";
            }
        }
        retval += NL;
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            retval += String.format( "    %1$-68s %2$6.2f", "Jump Jet Locations: " + FileCommon.GetJumpJetLocations( CurMech ), CurMech.GetJumpJets().GetTonnage() ) + NL;
        }
        retval += String.format( "Heat Sinks:         %1$-28s %2$-8s                %3$6.2f", GetHSType(), GetHSNum(), CurMech.GetHeatSinks().GetTonnage() ) + NL;
        if( CurMech.GetHeatSinks().GetNumHS() > CurMech.GetEngine().InternalHeatSinks() ) {
            retval += "    Heat Sink Locations: " + FileCommon.GetHeatSinkLocations( CurMech ) + NL;
        }
        retval += String.format( "Gyro:               %1$-52s %2$6.2f", CurMech.GetGyro().LookupName(), CurMech.GetGyro().GetTonnage() ) + NL;
        retval += String.format( "Cockpit:            %1$-52s %2$6.2f", CurMech.GetCockpit().GetReportName(), CurMech.GetCockpit().GetTonnage() ) + NL;
        if( CurMech.HasEjectionSeat() ) {
            retval += String.format( "    %1$-68s %2$6.2f", "Ejection Seat:", CurMech.GetEjectionSeat().GetTonnage() ) + NL;
        }
        if( ! CurMech.GetEngine().IsNuclear() ) {
            if( CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                retval += String.format( "%1$-72s %2$6.2f", "Power Amplifiers:", CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() ) + NL;
            }
        }
        retval += "    Actuators:      " + FileCommon.BuildActuators( CurMech, false ) + NL;
        if( CurMech.GetPhysEnhance().IsTSM() ) {
            retval += "    TSM Locations: " + FileCommon.GetTSMLocations( CurMech ) + NL;
        }
        if( CurMech.GetArmor().GetBAR() < 10 ) {
            retval += String.format( "Armor:              %1$-28s AV - %2$3s                %3$6.2f", CurMech.GetArmor().CritName() + " (BAR: " + CurMech.GetArmor().GetBAR() +")", CurMech.GetArmor().GetArmorValue(), CurMech.GetArmor().GetTonnage() ) + NL;
        } else {
            retval += String.format( "Armor:              %1$-28s AV - %2$3s                %3$6.2f", CurMech.GetArmor().CritName(), CurMech.GetArmor().GetArmorValue(), CurMech.GetArmor().GetTonnage() ) + NL;
        }
        if( CurMech.GetArmor().NumCrits() > 0 ) {
            retval += "    Armor Locations: " + FileCommon.GetArmorLocations( CurMech ) + NL;
        }
        if( CurMech.GetLoadout().IsUsingClanCASE() ) {
            int[] Locs = CurMech.GetLoadout().FindExplosiveInstances();
            boolean check = false;
            for( int i = 0; i < Locs.length; i++ ) {
                if( Locs[i] > 0 ) {
                    check = true;
                }
            }
            if( check ) {
                retval += String.format( "    %1$-68s %2$6.2f", "CASE Locations: " + FileCommon.GetCaseLocations( CurMech ), CurMech.GetCaseTonnage() ) + NL;
            }
        } else {
            if( CurMech.GetCaseTonnage() != 0.0f ) {
                retval += String.format( "    %1$-68s %2$6.2f", "CASE Locations: " + FileCommon.GetCaseLocations( CurMech ), CurMech.GetCaseTonnage() ) + NL;
            }
        }
        if( CurMech.GetCASEIITonnage() != 0.0f ) {
            retval += String.format( "    %1$-68s %2$6.2f", "CASE II Locations: " + FileCommon.GetCaseIILocations( CurMech ), CurMech.GetCASEIITonnage() ) + NL;
        }
        retval += NL + "                                                      Internal       Armor      " + NL;
        retval += "                                                      Structure      Factor     " + NL;
        retval += String.format( "                                                Head     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetHeadPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_HD ) ) + NL;
        retval += String.format( "                                        Center Torso     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetCTPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_CT ) ) + NL;
        retval += String.format( "                                 Center Torso (rear)                  %1$-3s       ", CurMech.GetArmor().GetLocationArmor( Constants.LOC_CTR ) ) + NL;
        if( CurMech.GetArmor().GetLocationArmor( Constants.LOC_LT ) != CurMech.GetArmor().GetLocationArmor( Constants.LOC_RT ) ) {
            retval += String.format( "                                          Left Torso     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetSidePoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LT ) ) + NL;
            retval += String.format( "                                         Right Torso     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetSidePoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_RT ) ) + NL;
        } else {
            retval += String.format( "                                           L/R Torso     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetSidePoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LT ) ) + NL;
        }
        if( CurMech.GetArmor().GetLocationArmor( Constants.LOC_LTR ) != CurMech.GetArmor().GetLocationArmor( Constants.LOC_RTR ) ) {
            retval += String.format( "                                   Left Torso (rear)                  %1$-3s       ", CurMech.GetArmor().GetLocationArmor( Constants.LOC_LTR ) ) + NL;
            retval += String.format( "                                  Right Torso (rear)                  %1$-3s       ", CurMech.GetArmor().GetLocationArmor( Constants.LOC_RTR ) ) + NL;
        } else {
            retval += String.format( "                                    L/R Torso (rear)                  %1$-3s       ", CurMech.GetArmor().GetLocationArmor( Constants.LOC_LTR ) ) + NL;
        }
        if( CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA ) != CurMech.GetArmor().GetLocationArmor( Constants.LOC_RA ) ) {
            if( CurMech.IsQuad() ) {
                retval += String.format( "                                      Left Front Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA ) ) + NL;
                retval += String.format( "                                     Right Front Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_RA ) ) + NL;
            } else {
                retval += String.format( "                                            Left Arm     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA ) ) + NL;
                retval += String.format( "                                           Right Arm     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_RA ) ) + NL;
            }
        } else {
            if( CurMech.IsQuad() ) {
                retval += String.format( "                                       L/R Front Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA ) ) + NL;
            } else {
                retval += String.format( "                                             L/R Arm     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA ) ) + NL;
            }
        }
        if( CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL ) != CurMech.GetArmor().GetLocationArmor( Constants.LOC_RL ) ) {
            if( CurMech.IsQuad() ) {
                retval += String.format( "                                       Left Rear Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL ) ) + NL;
                retval += String.format( "                                      Right Rear Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_RL ) ) + NL;
            } else {
                retval += String.format( "                                            Left Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL ) ) + NL;
                retval += String.format( "                                           Right Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_RL ) ) + NL;
            }
        } else {
            if( CurMech.IsQuad() ) {
                retval += String.format( "                                        L/R Rear Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL ) ) + NL;
            } else {
                retval += String.format( "                                             L/R Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL ) ) + NL;
            }
        }
        if( CurMech.IsOmnimech() ) {
            Vector l = CurMech.GetLoadouts();
            if ( !CurrentLoadoutOnly ) {
                CurMech.SetCurLoadout( Constants.BASELOADOUT_NAME );
                retval += NL;
                retval += BuildEquipmentBlock() + NL;
                for( int i = 0; i < l.size(); i++ ) {
                    CurMech.SetCurLoadout( ((ifLoadout) l.get( i )).GetName() );
                    retval += NL + "================================================================================" + NL;
                    retval += BuildOmniLoadout() + NL;
                }
            } else {
                retval += NL + "================================================================================" + NL;
                retval += BuildOmniLoadout() + NL;
            }
        } else {
            //retval += NL + "================================================================================" + NL;
            retval += NL;
            retval += BuildEquipmentBlock() + NL;
        }

        return retval;
    }

    private String GetArmament() {
        Vector v = CurMech.GetLoadout().GetNonCore();
        Vector w = new Vector();
        Vector EQ = new Vector();
        String Armament = "";

        // find only the armaments
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof ifWeapon ) {
                w.add( v.get( i ) );
            } else {
                EQ.add( v.get( i ) );
            }
        }

        // do we need to continue?
        if( w.size() <= 0 ) {
            if( CurMech.IsOmnimech() ) {
                return "    " + ( CurMech.GetTonnage() - CurMech.GetCurrentTons() ) + " tons of pod space." + NL;
            } else {
                return "    None" + NL;
            }
        }

        // sort the weapons according to current BV, assuming front facing
        abPlaceable[] weapons = CurMech.SortWeapons( w, false );
        Vector Temp = new Vector();

        // add in the rest of the equipment
        for( int i = 0; i < weapons.length; i++ ) {
            Temp.add( weapons[i] );
        }
        for( int i = 0; i < EQ.size(); i++ ) {
            if( ! ( EQ.get( i ) instanceof Ammunition ) ) {
                Temp.add( EQ.get( i ) );
            }
        }
        weapons = new abPlaceable[Temp.size()];
        for( int i = 0; i < Temp.size(); i++ ) {
            weapons[i] = (abPlaceable) Temp.get( i );
        }

        // initialize the next loop
        int numthistype = 1;
        abPlaceable cur = weapons[0];
        weapons[0] = null;

        // do we need to continue?
        if( weapons.length <= 1 ) {
            if( cur instanceof RangedWeapon ) {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    if( ((RangedWeapon) cur).IsUsingFCS() ) {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.LookupName() + " w/ " + ((abPlaceable) ((RangedWeapon) cur).GetFCS()).LookupName() + NL;
                    } else {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.LookupName() + NL;
                    }
                } else {
                    if( ((RangedWeapon) cur).IsUsingFCS() ) {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.CritName() + " w/ " + ((abPlaceable) ((RangedWeapon) cur).GetFCS()).CritName() + NL;
                    } else {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.CritName() + NL;
                    }
                }
            } else {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    if( cur instanceof Equipment ) {
                        if( ((Equipment) cur).IsVariableSize() ) {
                            Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.CritName() + NL;
                        } else {
                            Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.LookupName() + NL;
                        }
                    } else {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.LookupName() + NL;
                    }
                } else {
                    Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.CritName() + NL;
                }
            }
            if( CurMech.IsOmnimech() ) {
                Armament += "    " + ( CurMech.GetTonnage() - CurMech.GetCurrentTons() ) + " tons of pod space." + NL;
            }
            return Armament;
        }

        // count up individual weapons and build their string
        String plural = "";
        for( int i = 1; i <= weapons.length; i++ ) {
            // find any other weapons of this type
            for( int j = 0; j < weapons.length; j++ ) {
                if( weapons[j] != null ) {
                    if( cur instanceof Equipment ) {
                        if( ((Equipment) cur).IsVariableSize() ) {
                            if( weapons[j].CritName().equals( cur.CritName() ) && weapons[j].GetManufacturer().equals( cur.GetManufacturer() ) ) {
                                numthistype++;
                                weapons[j] = null;
                            }
                        } else {
                            if( FileCommon.LookupStripArc( weapons[j].LookupName() ).equals( FileCommon.LookupStripArc( cur.LookupName() ) ) && weapons[j].GetManufacturer().equals( cur.GetManufacturer() ) ) {
                                numthistype++;
                                weapons[j] = null;
                            }
                        }
                    } else {
                        if( FileCommon.LookupStripArc( weapons[j].LookupName() ).equals( FileCommon.LookupStripArc( cur.LookupName() ) ) && weapons[j].GetManufacturer().equals( cur.GetManufacturer() ) ) {
                            numthistype++;
                            weapons[j] = null;
                        }
                    }
                }
            }

            // add the current weapon to the armament string
            if( numthistype > 1 ) {
                plural = "s";
            } else {
                plural = "";
            }

            if( cur instanceof RangedWeapon ) {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    if( ((RangedWeapon) cur).IsUsingFCS() ) {
                        Armament += "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.LookupStripArc( cur.LookupName() ) + plural + " w/ " + ((abPlaceable) ((RangedWeapon) cur).GetFCS()).LookupName() + NL;
                    } else {
                        Armament += "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.LookupStripArc( cur.LookupName() ) + plural + NL;
                    }
                } else {
                    if( ((RangedWeapon) cur).IsUsingFCS() ) {
                        Armament += "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.LookupStripArc( cur.CritName() ) + plural + " w/ " + ((abPlaceable) ((RangedWeapon) cur).GetFCS()).CritName() + NL;
                    } else {
                        Armament += "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.LookupStripArc( cur.CritName() ) + plural + NL;
                    }
                }
            } else {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    if( cur instanceof Equipment ) {
                        if( ((Equipment) cur).IsVariableSize() ) {
                            Armament += "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.CritName() + NL;
                        } else {
                            Armament += "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.LookupStripArc( cur.LookupName() ) + plural + NL;
                        }
                    } else {
                        Armament += "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.LookupStripArc( cur.LookupName() ) + plural + NL;
                    }
                } else {
                    Armament += "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.LookupStripArc( cur.CritName() ) + plural + NL;
                }
            }

            // find the next weapon type and set it to current
            cur = null;
            numthistype = 0;
            for( int j = 0; j < weapons.length; j++ ) {
                if( weapons[j] != null ) {
                    cur = weapons[j];
                    weapons[j] = null;
                    numthistype = 1;
                    break;
                }
            }

            // do we need to continue?
            if( cur == null ) { break; }
        }

        if( CurMech.IsOmnimech() ) {
            Armament += "    " + ( CurMech.GetTonnage() - CurMech.GetCurrentTons() ) + " tons of pod space." + NL;
        }

        // all done
        return Armament;
    }

    private String GetHSType() {
        if( CurMech.GetHeatSinks().IsCompact() ) {
            return "Compact";
        }
        if( CurMech.GetHeatSinks().IsLaser() ) {
            return "Laser";
        }
        if( CurMech.GetHeatSinks().IsDouble() ) {
            return "Double";
        }
        return "Single";
    }

    private String GetHSNum() {
        // provides a formated heat sink dissipation string
        if( CurMech.GetHeatSinks().IsDouble() ) {
            return CurMech.GetHeatSinks().GetNumHS() + "(" + CurMech.GetHeatSinks().TotalDissipation() + ")";
        } else {
            return "" + CurMech.GetHeatSinks().GetNumHS();
        }
    }

    private String BuildEquipmentBlock() {
        // this routine builds the big equipment block at the bottom of the file
//        String retval = "Equipment                                        Location     Critical    Mass  " + NL;
        String loc = "";
        String crits = "";
        Vector v = (Vector) CurMech.GetLoadout().GetNonCore().clone();

        // add in MASC and the targeting computer if needed.
        if( CurMech.GetPhysEnhance().IsMASC() ) {
            v.add( CurMech.GetPhysEnhance() );
        }
        if( CurMech.UsingTC() ) {
            v.add( CurMech.GetTC() );
        }
        if( CurMech.HasCommandConsole() ) {
            v.add( CurMech.GetCommandConsole() );
        }
        if( CurMech.UsingPartialWing() ) {
            v.add( CurMech.GetPartialWing() );
        }
        if( CurMech.GetLoadout().HasSupercharger() ) {
            v.add( CurMech.GetLoadout().GetSupercharger() );
        }
        if( CurMech.IsQuad() ) {
            if( CurMech.HasLegAES() ) {
                v.add( CurMech.GetRAAES() );
                v.add( CurMech.GetLAAES() );
                v.add( CurMech.GetRLAES() );
                v.add( CurMech.GetLLAES() );
            }
        } else {
            if( CurMech.HasRAAES() ) {
                v.add( CurMech.GetRAAES() );
            }
            if( CurMech.HasLAAES() ){
                v.add( CurMech.GetLAAES() );
            }
            if( CurMech.HasLegAES() ) {
                v.add( CurMech.GetRLAES() );
                v.add( CurMech.GetLLAES() );
            }
        }

        if( v.size() < 1 ) { return ""; }
        String retval = "================================================================================" + NL;
        retval += "Equipment                                 Location    Heat    Critical    Mass  " + NL;
        retval += "--------------------------------------------------------------------------------" + NL;

        // now sort the equipment by location
        v = FileCommon.SortEquipmentForStats( CurMech, v );

        // turn the equipment into an array
        abPlaceable[] equips = new abPlaceable[v.size()];
        for( int i = 0; i < v.size(); i++ ) {
            equips[i] = (abPlaceable) v.get( i );
        }

        // we'll want to consolidate equipment within locations.
        int numthisloc = 1;
        abPlaceable cur = equips[0];
        equips[0] = null;

        if( equips.length <= 1 ) {
            retval += ProcessEquipStatLines( cur, FileCommon.EncodeLocation( CurMech.GetLoadout().Find( cur ), CurMech.IsQuad() ), "" + cur.NumCrits(), 1 );
            return retval;
        }

        // count up individual weapons and build their string
        for( int i = 1; i <= equips.length; i++ ) {
            // find any other weapons of this type
            if( cur.CanSplit() |! cur.Contiguous() || cur instanceof MGArray ) {
                // splittable items are generally too big for two in one
                // location or are split into different areas.  just avoid.
                int[] check = CurMech.GetLoadout().FindInstances( cur );
                loc = FileCommon.EncodeLocations( check, CurMech.IsQuad() );
                crits = FileCommon.DecodeCrits( check );
                retval += ProcessEquipStatLines( cur, loc, crits, 1 );
            } else {
                int locint = CurMech.GetLoadout().Find( cur );
                for( int j = 0; j < equips.length; j++ ) {
                    if( equips[j] != null ) {
                        if( cur instanceof Equipment ) {
                            if( ((Equipment) cur).IsVariableSize() ) {
                                if( equips[j].CritName().equals( cur.CritName() ) && CurMech.GetLoadout().Find( equips[j] ) == locint ) {
                                    numthisloc++;
                                    equips[j] = null;
                                }
                            } else {
                                if( equips[j].LookupName().equals( cur.LookupName() ) && CurMech.GetLoadout().Find( equips[j] ) == locint ) {
                                    numthisloc++;
                                    equips[j] = null;
                                }
                            }
                        } else {
                            if( equips[j].LookupName().equals( cur.LookupName() ) && CurMech.GetLoadout().Find( equips[j] ) == locint ) {
                                numthisloc++;
                                equips[j] = null;
                            }
                        }
                    }
                }

                if( numthisloc > 1) {
                    crits = "" + ( cur.NumCrits() * numthisloc );
                } else {
                    crits = "" + cur.NumCrits();
                }
                loc = FileCommon.EncodeLocation( locint, CurMech.IsQuad() );
                // add the current weapon to the armament string
                retval += ProcessEquipStatLines( cur, loc, crits, numthisloc );
            }

            // find the next weapon type and set it to current
            cur = null;
            numthisloc = 0;
            for( int j = 0; j < equips.length; j++ ) {
                if( equips[j] != null ) {
                    cur = equips[j];
                    equips[j] = null;
                    numthisloc = 1;
                    break;
                }
            }

            // do we need to continue?
            if( cur == null ) { break; }
        }

        // add in any special systems
        boolean Special = false;
        if( CurMech.HasNullSig() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", CurMech.GetNullSig().CritName(), "*", "10", 7, 0.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasVoidSig() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", CurMech.GetVoidSig().CritName(), "*", "10", 7, 0.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasChameleon() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", CurMech.GetChameleon().CritName(), "*", "6", 6, 0.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasBlueShield() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", CurMech.GetBlueShield().CritName(), "*", "-", 7, 3.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasEnviroSealing() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", CurMech.GetEnviroSealing().CritName(), "*", "-", 8, 0.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasTracks() ) {
            if( CurMech.IsQuad() ) {
                retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", CurMech.GetTracks().CritName(), "*", "-", 4, CurMech.GetTracks().GetTonnage() ) + NL;
            } else {
                retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", CurMech.GetTracks().CritName(), "*", "-", 2, CurMech.GetTracks().GetTonnage() ) + NL;
            }
            Special = true;
        }

        retval += String.format( "%1$s %2$-2s", "                                            Free Critical Slots:", CurMech.GetLoadout().FreeCrits() + NL );

        if( Special ) {
            retval += NL;
        }

        // add in the equipment footers if needed
        if( CurMech.HasNullSig() ) {
            retval += "* The " + CurMech.GetNullSig().LookupName() + " occupies 1 slot in every location except the HD." + NL;
        }
        if( CurMech.HasVoidSig() ) {
            retval += "* The " + CurMech.GetVoidSig().LookupName() + " occupies 1 slot in every location except the HD." + NL;
        }
        if( CurMech.HasChameleon() ) {
            retval += "* The " + CurMech.GetChameleon().LookupName() + " occupies 1 slot in every location except the HD and CT." + NL;
        }
        if( CurMech.HasBlueShield() ) {
            retval += "* The " + CurMech.GetBlueShield().LookupName() + " occupies 1 slot in every location except the HD." + NL;
        }
        if( CurMech.HasEnviroSealing() ) {
            retval += "* The " + CurMech.GetEnviroSealing().LookupName() + " occupies 1 slot in every location." + NL;
        }
        if( CurMech.HasTracks() ) {
            retval += "* " + CurMech.GetTracks().LookupName() + " occupy 1 slot in every leg location." + NL;
        }

        BattleForceStats bfs = new BattleForceStats( CurMech );
        int [] BFdmg = CurMech.GetBFDamage( bfs );
        retval += NL + "BattleForce Statistics" + NL;
        retval += String.format( "MV      S (+0)  M (+2)  L (+4)  E (+6)   Wt.   Ov   Armor:     %1$2s    Points: " + CurMech.GetBFPoints(), CurMech.GetBFArmor() ) + NL;
        retval += String.format( "%1$-6s    %2$2s      %3$2s      %4$2s      %5$2s      %6$1s     %7$1s   Structure: %8$2s", BattleForceTools.GetMovementString( CurMech ), BFdmg[Constants.BF_SHORT], BFdmg[Constants.BF_MEDIUM], BFdmg[Constants.BF_LONG], BFdmg[Constants.BF_EXTREME], CurMech.GetBFSize(), BFdmg[Constants.BF_OV], CurMech.GetBFStructure() ) + NL;
        retval += "Special Abilities: " + bfs.getAbilitiesString() + NL;

        return retval;
    }

    private String BuildOmniLoadout() {
        // this routine builds the big equipment block at the bottom of the file
        String retval = "";
        String loc = "";
        String crits = "";
        Vector v = (Vector) CurMech.GetLoadout().GetNonCore().clone();

        retval += String.format( "Loadout Name: %1$-46s Cost: %2$,.0f", CurMech.GetLoadout().GetName(), Math.floor( CurMech.GetTotalCost() + 0.5 ) ) + NL;
        retval += String.format( "Tech Rating/Era Availability: %1$-31s BV2: %2$,d", CurMech.GetAvailability().GetBestCombinedCode(), CurMech.GetCurrentBV() ) + NL;
        if( CurMech.GetBaseRulesLevel() != CurMech.GetLoadout().GetRulesLevel() ) {
            if( CurMech.GetTechBase() != CurMech.GetLoadout().GetTechBase() ) {
                retval += String.format( "Rules Level: %1$-42s %2$s", CommonTools.GetRulesLevelString( CurMech.GetLoadout().GetRulesLevel() ), "Tech Base: " + CommonTools.GetTechbaseString( CurMech.GetLoadout().GetTechBase() ) ) + NL;
            } else {
                retval += "Rules Level: " + CommonTools.GetRulesLevelString( CurMech.GetLoadout().GetRulesLevel() ) + NL;
            }
        }

        // build the starting block for the loadout information
        retval += NL + "Equipment           Type                         Rating                   Mass  " + NL;
        retval += "--------------------------------------------------------------------------------" + NL;
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            if( CurMech.GetJumpJets().IsImproved() ) {
                if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
                    retval += "    Jumping MP: " + CurMech.GetJumpJets().GetNumJJ() + " (" + CurMech.GetAdjustedJumpingMP( false ) + ")  (Improved)" + NL;
                } else {
                    retval += "    Jumping MP: " + CurMech.GetJumpJets().GetNumJJ() + "  (Improved)" + NL;
                }
            } else {
                if( CurMech.GetJumpJets().IsUMU() ) {
                    retval += "    Jumping MP: " + CurMech.GetJumpJets().GetNumJJ() + "  (UMU)" + NL;
                } else {
                    if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
                        retval += "    Jumping MP: " + CurMech.GetJumpJets().GetNumJJ() + " (" + CurMech.GetAdjustedJumpingMP( false ) + ")  (Standard)" + NL;
                    } else {
                        retval += "    Jumping MP: " + CurMech.GetJumpJets().GetNumJJ() + "  (Standard)" + NL;
                    }
                }
            }
        }
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            retval += String.format( "    %1$-68s %2$6.2f", "Jump Jet Locations: " + FileCommon.GetJumpJetLocations( CurMech ), CurMech.GetJumpJets().GetOmniTonnage() ) + NL;
        }
        if( CurMech.GetHeatSinks().GetNumHS() > CurMech.GetHeatSinks().GetBaseLoadoutNumHS() ) {
            retval += String.format( "Heat Sinks:         %1$-28s %2$-8s                %3$6.2f", GetHSType(), GetHSNum(), CurMech.GetHeatSinks().GetOmniTonnage() ) + NL;
            if( CurMech.GetHeatSinks().GetNumHS() > CurMech.GetEngine().InternalHeatSinks() ) {
                retval += "    Heat Sink Locations: " + FileCommon.GetHeatSinkLocations( CurMech ) + NL;
            }
        }
        if( CurMech.GetLoadout().IsUsingClanCASE() ) {
            int[] Locs = CurMech.GetLoadout().FindExplosiveInstances();
            boolean check = false;
            for( int i = 0; i < Locs.length; i++ ) {
                if( Locs[i] > 0 ) {
                    check = true;
                }
            }
            if( check ) {
                retval += String.format( "    %1$-68s %2$6.2f", "CASE Locations: " + FileCommon.GetCaseLocations( CurMech ), CurMech.GetCaseTonnage() ) + NL;
            }
        } else {
            if( CurMech.GetCaseTonnage() != 0.0f ) {
                retval += String.format( "    %1$-68s %2$6.2f", "CASE Locations: " + FileCommon.GetCaseLocations( CurMech ), CurMech.GetCaseTonnage() ) + NL;
            }
        }
        if( CurMech.GetCASEIITonnage() != 0.0f ) {
            retval += String.format( "    %1$-68s %2$6.2f", "CASE II Locations: " + FileCommon.GetCaseIILocations( CurMech ), CurMech.GetCASEIITonnage() ) + NL;
        }
        if( ! CurMech.GetEngine().IsNuclear() ) {
            if( CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                retval += String.format( "%1$-72s %2$6.2f", "Power Amplifiers:", CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() ) + NL;
            }
        }
        retval += "    Actuators:      " + FileCommon.BuildActuators( CurMech, false ) + NL;

        // add in MASC and the targeting computer if needed.
        if( CurMech.GetPhysEnhance().IsMASC() ) {
            v.add( CurMech.GetPhysEnhance() );
        }
        if( CurMech.UsingTC() ) {
            v.add( CurMech.GetTC() );
        }
        if( CurMech.GetLoadout().HasSupercharger() ) {
            v.add( CurMech.GetLoadout().GetSupercharger() );
        }

        // now sort the equipment by location
        v = FileCommon.SortEquipmentForStats( CurMech, v );

        // turn the equipment into an array
        abPlaceable[] equips = new abPlaceable[v.size()];
        for( int i = 0; i < v.size(); i++ ) {
            equips[i] = (abPlaceable) v.get( i );
        }

        // the basic equipment block header
        retval += NL + "Equipment                                 Location    Heat    Critical    Mass  " + NL;
        retval += "--------------------------------------------------------------------------------" + NL;

        // we'll want to consolidate equipment within locations.
        int numthisloc = 1;
        abPlaceable cur = equips[0];
        equips[0] = null;

        if( equips.length <= 1 ) {
            retval += ProcessEquipStatLines( cur, FileCommon.EncodeLocation( CurMech.GetLoadout().Find( cur ), CurMech.IsQuad() ), "" + cur.NumCrits(), 1 );
            return retval;
        }

        // count up individual weapons and build their string
        for( int i = 1; i <= equips.length; i++ ) {
            // find any other weapons of this type
            if( cur.CanSplit() |! cur.Contiguous() || cur instanceof MGArray ) {
                // splittable items are generally too big for two in one
                // location or are split into different areas.  just avoid.
                int[] check = CurMech.GetLoadout().FindInstances( cur );
                loc = FileCommon.EncodeLocations( check, CurMech.IsQuad() );
                crits = FileCommon.DecodeCrits( check );
                retval += ProcessEquipStatLines( cur, loc, crits, 1 );
            } else {
                int locint = CurMech.GetLoadout().Find( cur );
                for( int j = 0; j < equips.length; j++ ) {
                    if( equips[j] != null ) {
                        if( cur instanceof Equipment ) {
                            if( ((Equipment) cur).IsVariableSize() ) {
                                if( equips[j].CritName().equals( cur.CritName() ) && CurMech.GetLoadout().Find( equips[j] ) == locint ) {
                                    numthisloc++;
                                    equips[j] = null;
                                }
                            } else {
                                if( equips[j].LookupName().equals( cur.LookupName() ) && CurMech.GetLoadout().Find( equips[j] ) == locint ) {
                                    numthisloc++;
                                    equips[j] = null;
                                }
                            }
                        } else {
                            if( equips[j].LookupName().equals( cur.LookupName() ) && CurMech.GetLoadout().Find( equips[j] ) == locint ) {
                                numthisloc++;
                                equips[j] = null;
                            }
                        }
                    }
                }

                if( numthisloc > 1) {
                    crits = "" + ( cur.NumCrits() * numthisloc );
                } else {
                    crits = "" + cur.NumCrits();
                }
                loc = FileCommon.EncodeLocation( locint, CurMech.IsQuad() );
                // add the current weapon to the armament string
                retval += ProcessEquipStatLines( cur, loc, crits, numthisloc );
            }

            // find the next weapon type and set it to current
            cur = null;
            numthisloc = 0;
            for( int j = 0; j < equips.length; j++ ) {
                if( equips[j] != null ) {
                    cur = equips[j];
                    equips[j] = null;
                    numthisloc = 1;
                    break;
                }
            }

            // do we need to continue?
            if( cur == null ) { break; }
        }

        retval += String.format( "%1$s %2$-2s", "                                            Free Critical Slots:", CurMech.GetLoadout().FreeCrits() + NL );

        BattleForceStats bfs = new BattleForceStats( CurMech );
        int [] BFdmg = CurMech.GetBFDamage( bfs );
        retval += NL + "BattleForce Statistics" + NL;
        retval += String.format( "MV      S (+0)  M (+2)  L (+4)  E (+6)   Wt.   Ov   Armor:     %1$2s    Points: " + CurMech.GetBFPoints(), CurMech.GetBFArmor() ) + NL;
        retval += String.format( "%1$-6s    %2$2s      %3$2s      %4$2s      %5$2s      %6$1s     %7$1s   Structure: %8$2s", BattleForceTools.GetMovementString( CurMech ), BFdmg[Constants.BF_SHORT], BFdmg[Constants.BF_MEDIUM], BFdmg[Constants.BF_LONG], BFdmg[Constants.BF_EXTREME], CurMech.GetBFSize(), BFdmg[Constants.BF_OV], CurMech.GetBFStructure() ) + NL;
        retval += "Special Abilities: " + bfs.getAbilitiesString() + NL;

        return retval;
    }

    private String BuildComputerBlock() {
        String retval = "";
/*
        abPlaceable ECM = FileCommon.HasECM( CurMech );
        abPlaceable BAP = FileCommon.HasBAP( CurMech );
        Object[] C3 = FileCommon.HasC3( CurMech );
*/
        // start communications system line
        retval += "Communications System: " + CurMech.GetCommSystem() + NL;
/*
        if( ! ( ECM instanceof EmptyItem ) ) {
            retval += NL + "    w/ " + ECM.GetManufacturer() + " " + ECM.GetCritName(); 
            if( C3 != null ) {
                for( int i = 0; i < C3.length; i++ ) {
                    retval += NL + "    and " + ((abPlaceable) C3[i]).GetManufacturer() + " " + ((abPlaceable) C3[i]).GetCritName();
                }
            }
        } else {
            if( C3 != null ) {
                for( int i = 0; i < C3.length; i++ ) {
                    if( i == 0 ) {
                        retval += NL + "    w/ " + ((abPlaceable) C3[i]).GetManufacturer() + " " + ((abPlaceable) C3[i]).GetCritName();
                    } else {
                        retval += NL + "    and " + ((abPlaceable) C3[i]).GetManufacturer() + " " + ((abPlaceable) C3[i]).GetCritName();
                    }
                }
            }
        }
        retval += NL;
*/
        // start targeting and tracking system line
        retval += "Targeting and Tracking System: " + CurMech.GetTandTSystem();
/*        if( ! ( BAP instanceof EmptyItem ) ) {
            if( CurMech.UsingTC() ) {
                retval += NL + "    w/ " + BAP.GetManufacturer() + " " + BAP.GetCritName() + NL + "    and " + CurMech.GetTC().GetCritName();
            } else {
                retval += NL + "    w/ " + BAP.GetManufacturer() + " " + BAP.GetCritName();
            }
        } else {
            if( CurMech.UsingTC() ) {
                retval += NL + "    w/ " + CurMech.GetTC().GetCritName();
            }
        }
*/
        return retval;
    }

    private String ProcessEquipStatLines( abPlaceable p, String loc, String crits, int numthisloc ) {
        String retval = "";
        String name = "";
        if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
            if( p instanceof Equipment ) {
                if( ((Equipment) p).IsVariableSize() ) {
                    if( numthisloc > 1 ) {
                        name = numthisloc + " " + p.CritName();
                    } else {
                        name = p.CritName();
                    }
                } else {
                    if( numthisloc > 1 ) {
                        name = numthisloc + " " + p.LookupName() + "s";
                    } else {
                        name = p.LookupName();
                    }
                }
            } else {
                if( numthisloc > 1 ) {
                    name = numthisloc + " " + p.LookupName() + "s";
                } else {
                    name = p.LookupName();
                }
            }
        } else {
            if( numthisloc > 1 ) {
                name = numthisloc + " " + p.CritName() + "s";
            } else {
                name = p.CritName();
            }
        }

        // build the string based on the type of equipment
        if( p instanceof Ammunition ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", FileCommon.FormatAmmoExportName( (Ammunition) p, numthisloc ), loc, "-", crits, p.GetTonnage() * numthisloc ) + NL;
        } else if( p instanceof RangedWeapon ) {
            double tons = p.GetTonnage();
            String add = "";
            if( ((RangedWeapon) p).IsUsingFCS() ) {
                abPlaceable a = (abPlaceable) ((RangedWeapon) p).GetFCS();
                tons -= a.GetTonnage();
                add += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5$6.2f", a.CritName(), loc, "-", a.NumCrits(), a.GetTonnage() * numthisloc ) + NL;
            }
            if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                tons -= 1.0f;
                abPlaceable a = ((RangedWeapon) p).GetCapacitor();
                add += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5$6.2f", a.CritName(), loc, numthisloc * 5 + "*", a.NumCrits(), a.GetTonnage() * numthisloc ) + NL;
            }
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", name, loc, ((RangedWeapon) p).GetHeat() * numthisloc, crits, tons * numthisloc ) + NL + add;
        } else if( p instanceof MGArray ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", name, loc, "-", crits, ((MGArray) p).GetBaseTons() ) + NL;
            abPlaceable a = ((MGArray) p).GetMGType();
            if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                retval += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5$6.2f", ((MGArray) p).GetNumMGs() + " " + a.LookupName() + "s", loc, ((RangedWeapon) a).GetHeat() * ((MGArray) p).GetNumMGs(), ((MGArray) p).GetNumMGs(), ( ((MGArray) p).GetMGTons() * ((MGArray) p).GetNumMGs() ) ) + NL;
            } else{
                retval += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5$6.2f", ((MGArray) p).GetNumMGs() + " " + a.CritName() + "s", loc, ((RangedWeapon) a).GetHeat() * ((MGArray) p).GetNumMGs(), ((MGArray) p).GetNumMGs(), ( ((MGArray) p).GetMGTons() * ((MGArray) p).GetNumMGs() ) ) + NL;
            }
       } else if( p instanceof Equipment ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", name, loc, ((Equipment) p).GetHeat() * numthisloc, crits, p.GetTonnage() * numthisloc ) + NL;
        } else {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5$6.2f", name, loc, "-", crits, p.GetTonnage() * numthisloc ) + NL;
        }

        return retval;
    }

    private String FormatFluff( String s ) {
        // we're basically checking length here to limit it to 80 chars in length
        // first, seperate out all the newlines.
        s = s.replaceAll( "\n\r", "\n" );
        String[] newline = s.split( "\n" );
        String retval = "";
        for( int i = 0; i < newline.length; i++ ) {
            String[] temp = wrapText( newline[i], 80 );
            // put the string back together
            for( int j = 0; j < temp.length; j++ ) {
                retval += temp[j] + NL;
            }
        }
        return retval;
    }

    static String [] wrapText (String text, int len) {
        // return empty array for null text
        if (text == null)
            return new String [] {};

        // return text if len is zero or less
        if (len <= 0)
            return new String [] {text};

        // return text if less than length
        if (text.length() <= len)
            return new String [] {text};

        // before the wrapping, replace any special characters
        text = text.replace( "\t", "    " );

        char [] chars = text.toCharArray();
        Vector lines = new Vector();
        StringBuffer line = new StringBuffer();
        StringBuffer word = new StringBuffer();

        for (int i = 0; i < chars.length; i++) {
            word.append(chars[i]);

            if (chars[i] == ' ') {
                if ((line.length() + word.length()) > len) {
                    lines.add(line.toString());
                    line.delete(0, line.length());
                }

                line.append(word);
                word.delete(0, word.length());
            }
        }

        // handle any extra chars in current word
        if (word.length() > 0) {
            if ((line.length() + word.length()) > len) {
                lines.add(line.toString());
                line.delete(0, line.length());
            }
            line.append(word);
        }

        // handle extra line
        if (line.length() > 0) {
            lines.add(line.toString());
        }

        String [] ret = new String[lines.size()];
        int c = 0; // counter
        for (Enumeration e = lines.elements(); e.hasMoreElements(); c++) {
            ret[c] = (String) e.nextElement();
        }

        return ret;
    }
}
