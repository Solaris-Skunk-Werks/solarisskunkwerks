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

package filehandlers;

import Force.*;
import battleforce.BFConstants;
import common.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.ArrayList;
import battleforce.BattleForceStats;
import battleforce.BattleForceTools;
import components.*;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import list.*;
import utilities.CostBVBreakdown;

public class TXTWriter {

    private Mech CurMech;
    private String NL,
                   tformat = "$6.2f";
    private ArrayList<Force> forces;

    public boolean CurrentLoadoutOnly = false;

    public TXTWriter() {
        NL = System.getProperty( "line.separator" );
    }

    public TXTWriter( ArrayList<Force> forces ) {
        this.forces = forces;
    }

    public TXTWriter( Mech m ) {
        this();
        CurMech = m;
        if( CurMech.UsingFractionalAccounting() ) {
            tformat = "$7.3f";
        } else {
            tformat = "$6.2f";
        }
    }

    public void Write( String filename ) throws IOException {
        if ( !filename.endsWith(".txt") ) { filename += ".txt"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        for (Force force : forces) {
            FR.write(force.SerializeClipboard());
            FR.newLine();
        }
        FR.close();
    }
    
    public void WriteForces( String filename, Force force ) throws IOException {
        forces = new ArrayList<Force>();
        forces.add(force);
        WriteForces( filename, forces );
    }

    public void WriteForces( String filename, ArrayList<Force> forces ) throws IOException {
        if ( !filename.endsWith(".txt") ) { filename += ".txt"; }
        BufferedWriter FR = new BufferedWriter( new FileWriter(filename) );

        for (Force force : forces) {
            FR.write(force.SerializeClipboard());
            FR.newLine();
        }
        FR.close();
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
            CurMech.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
        }
        retval += CurMech.GetName() + " " + CurMech.GetModel() + NL + NL;
        retval += "Mass: " + CurMech.GetTonnage() + " tons" + NL;
        if( CurMech.IsPrimitive() )  {
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurMech.GetBaseTechbase() ) + " (Primitive)" + NL;
        } else {
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurMech.GetBaseTechbase() ) + NL;
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

        if( CurMech.UsingFractionalAccounting() ) {
            retval += "Construction Options: Fractional Accounting" + NL + NL;
        }

        retval += "Chassis: " + CurMech.GetChassisModel() + " " + CurMech.GetIntStruc().CritName() + NL;
        retval += "Power Plant: " + CurMech.GetEngineManufacturer() + " " + CurMech.GetEngine().GetRating() + " " + CurMech.GetEngine() + NL;
        if( CurMech.GetAdjustedWalkingMP( false, true ) != CurMech.GetWalkingMP() ) {
            retval += "Walking Speed: " + CommonTools.FormatSpeed( CurMech.GetWalkingMP() * 10.8 ) + " km/h (" + CommonTools.FormatSpeed( CurMech.GetAdjustedWalkingMP( false, true ) * 10.8 ) + " km/h)" + NL;
        } else {
            retval += "Walking Speed: " + CommonTools.FormatSpeed( CurMech.GetWalkingMP() * 10.8 ) + " km/h" + NL;
        }
        if( CurMech.GetAdjustedRunningMP( false, true ) != CurMech.GetRunningMP() ) {
            retval += "Maximum Speed: " + CommonTools.FormatSpeed( CurMech.GetRunningMP() * 10.8 ) + " km/h (" + CommonTools.FormatSpeed( CurMech.GetAdjustedRunningMP( false, true ) * 10.8 ) + " km/h)" + NL;
        } else {
            retval += "Maximum Speed: " + CommonTools.FormatSpeed( CurMech.GetRunningMP() * 10.8 ) + " km/h" + NL;
        }
        retval += "Jump Jets: " + CurMech.GetJJModel() + NL;
        retval += "    Jump Capacity: " + GetJumpJetDistanceLine() + NL;
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
        if( ! CurMech.GetAdditional().equals( "" ) ) {
            retval += "Additional: " + NL;
            retval += FormatFluff( CurMech.GetAdditional() ) + NL + NL;
        }
        retval += "================================================================================" + NL;
        retval += GetMiniTextExport();

        return retval;
    }

    public String GetMiniTextExport() {
        String retval = "";

        if ( CurrentLoadoutOnly ) {
            retval += CurMech.GetName() + " " + CurMech.GetModel() + NL + NL;
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurMech.GetTechbase() ) + NL;
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
        retval += String.format( "Internal Structure: %1$-28s %2$3s points              %3" + tformat, CurMech.GetIntStruc().CritName(), CurMech.GetIntStruc().GetTotalPoints(), CurMech.GetIntStruc().GetTonnage() ) + NL;
        if( CurMech.GetIntStruc().NumCrits() > 0 ) {
            retval += "    Internal Locations: " + FileCommon.GetInternalLocations( CurMech ) + NL;
        }
        retval += String.format( "Engine:             %1$-28s %2$3s                     %3" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetEngine() ), CurMech.GetEngine().GetRating(), CurMech.GetEngine().GetTonnage() ) + NL;
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
        retval += "    Jumping MP: " + GetJumpingMPLine() + " " + GetJumpJetTypeLine() + NL;
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            retval += String.format( "    %1$-68s %2$6.2f", "Jump Jet Locations: " + FileCommon.GetJumpJetLocations( CurMech ), CurMech.GetJumpJets().GetTonnage() ) + NL;
        }
        retval += String.format( "Heat Sinks:         %1$-28s %2$-8s                %3" + tformat, GetHSType(), GetHSNum(), CurMech.GetHeatSinks().GetTonnage() ) + NL;
        if( CurMech.GetHeatSinks().GetNumHS() > CurMech.GetEngine().InternalHeatSinks() ) {
            retval += "    Heat Sink Locations: " + FileCommon.GetHeatSinkLocations( CurMech ) + NL;
        }
        retval += String.format( "Gyro:               %1$-52s %2" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetGyro() ), CurMech.GetGyro().GetTonnage() ) + NL;
        retval += String.format( "Cockpit:            %1$-52s %2" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetCockpit() ), CurMech.GetCockpit().GetTonnage() ) + NL;
        if( CurMech.HasEjectionSeat() ) {
            retval += String.format( "    %1$-68s %2" + tformat, "Ejection Seat:", CurMech.GetEjectionSeat().GetTonnage() ) + NL;
        }
        if( FileCommon.NeedsCockpitComponentLine( CurMech ) ) {
            retval += "    " + FileCommon.GetCockpitComponentLine( CurMech );
        }
        if( ! CurMech.GetEngine().IsNuclear() ) {
            if( CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                retval += String.format( "%1$-72s %2" + tformat, "Power Amplifiers:", CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() ) + NL;
            }
        }
        if( FileCommon.NeedsLegActuatorLine( CurMech ) ) {
            retval += "    Arm Actuators:      " + FileCommon.BuildActuators( CurMech, false ) + NL;
            retval += "    Leg Actuators:      " + FileCommon.BuildLegActuators( CurMech, false ) + NL;
        } else {
            retval += "    Actuators:      " + FileCommon.BuildActuators( CurMech, false ) + NL;
        }
        if( CurMech.GetPhysEnhance().IsTSM() ) {
            retval += "    TSM Locations: " + FileCommon.GetTSMLocations( CurMech ) + NL;
        }
        if( CurMech.GetArmor().GetBAR() < 10 ) {
            retval += String.format( "Armor:              %1$-28s AV - %2$3s                %3" + tformat, CurMech.GetArmor().CritName() + " (BAR: " + CurMech.GetArmor().GetBAR() +")", CurMech.GetArmor().GetArmorValue(), CurMech.GetArmor().GetTonnage() ) + NL;
        } else {
            retval += String.format( "Armor:              %1$-28s AV - %2$3s                %3" + tformat, CurMech.GetArmor().CritName(), CurMech.GetArmor().GetArmorValue(), CurMech.GetArmor().GetTonnage() ) + NL;
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
                retval += String.format( "    %1$-68s %2" + tformat, "CASE Locations: " + FileCommon.GetCaseLocations( CurMech ), CurMech.GetCaseTonnage() ) + NL;
            }
        } else {
            if( CurMech.GetCaseTonnage() != 0.0f ) {
                retval += String.format( "    %1$-68s %2" + tformat, "CASE Locations: " + FileCommon.GetCaseLocations( CurMech ), CurMech.GetCaseTonnage() ) + NL;
            }
        }
        if( CurMech.GetCASEIITonnage() != 0.0f ) {
            retval += String.format( "    %1$-68s %2" + tformat, "CASE II Locations: " + FileCommon.GetCaseIILocations( CurMech ), CurMech.GetCASEIITonnage() ) + NL;
        }
        retval += NL + "                                                      Internal       Armor      " + NL;
        retval += "                                                      Structure      Factor     " + NL;
        if( CurMech.GetArmor().IsPatchwork() ) {
            retval += String.format( "                   Head %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetHDArmorType().LookupName() + ")", CurMech.GetIntStruc().GetHeadPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) ) + NL;
            retval += String.format( "           Center Torso %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetCTArmorType().LookupName() + ")", CurMech.GetIntStruc().GetCTPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) ) + NL;
            retval += String.format( "    Center Torso (rear)                                               %1$-3s       ", CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CTR ) ) + NL;
            retval += String.format( "             Left Torso %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetLTArmorType().LookupName() + ")", CurMech.GetIntStruc().GetSidePoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) ) + NL;
            retval += String.format( "      Left Torso (rear)                                               %1$-3s       ", CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) ) + NL;
            retval += String.format( "            Right Torso %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetRTArmorType().LookupName() + ")", CurMech.GetIntStruc().GetSidePoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) ) + NL;
            retval += String.format( "     Right Torso (rear)                                               %1$-3s       ", CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) ) + NL;
            if( CurMech.IsQuad() ) {
                retval += String.format( "         Left Front Leg %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetLAArmorType().LookupName() + ")", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) ) + NL;
                retval += String.format( "        Right Front Leg %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetRAArmorType().LookupName() + ")", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) ) + NL;
                retval += String.format( "          Left Rear Leg %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetLLArmorType().LookupName() + ")", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) ) + NL;
                retval += String.format( "         Right Rear Leg %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetRLArmorType().LookupName() + ")", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) ) + NL;
            } else {
                retval += String.format( "               Left Arm %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetLAArmorType().LookupName() + ")", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) ) + NL;
                retval += String.format( "              Right Arm %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetRAArmorType().LookupName() + ")", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) ) + NL;
                retval += String.format( "               Left Leg %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetLLArmorType().LookupName() + ")", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) ) + NL;
                retval += String.format( "              Right Leg %1$29s    %2$-3s          %3$-3s       ", "(" + CurMech.GetArmor().GetRLArmorType().LookupName() + ")", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) ) + NL;
            }
        } else {
            retval += String.format( "                                                Head     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetHeadPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) ) + NL;
            retval += String.format( "                                        Center Torso     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetCTPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) ) + NL;
            retval += String.format( "                                 Center Torso (rear)                  %1$-3s       ", CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CTR ) ) + NL;
            if( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) != CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) ) {
                retval += String.format( "                                          Left Torso     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetSidePoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) ) + NL;
                retval += String.format( "                                         Right Torso     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetSidePoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) ) + NL;
            } else {
                retval += String.format( "                                           L/R Torso     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetSidePoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) ) + NL;
            }
            if( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) != CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) ) {
                retval += String.format( "                                   Left Torso (rear)                  %1$-3s       ", CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) ) + NL;
                retval += String.format( "                                  Right Torso (rear)                  %1$-3s       ", CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) ) + NL;
            } else {
                retval += String.format( "                                    L/R Torso (rear)                  %1$-3s       ", CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) ) + NL;
            }
            if( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) != CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) ) {
                if( CurMech.IsQuad() ) {
                    retval += String.format( "                                      Left Front Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) ) + NL;
                    retval += String.format( "                                     Right Front Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) ) + NL;
                } else {
                    retval += String.format( "                                            Left Arm     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) ) + NL;
                    retval += String.format( "                                           Right Arm     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) ) + NL;
                }
            } else {
                if( CurMech.IsQuad() ) {
                    retval += String.format( "                                       L/R Front Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) ) + NL;
                } else {
                    retval += String.format( "                                             L/R Arm     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetArmPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) ) + NL;
                }
            }
            if( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) != CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) ) {
                if( CurMech.IsQuad() ) {
                    retval += String.format( "                                       Left Rear Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) ) + NL;
                    retval += String.format( "                                      Right Rear Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) ) + NL;
                } else {
                    retval += String.format( "                                            Left Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) ) + NL;
                    retval += String.format( "                                           Right Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) ) + NL;
                }
            } else {
                if( CurMech.IsQuad() ) {
                    retval += String.format( "                                        L/R Rear Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) ) + NL;
                } else {
                    retval += String.format( "                                             L/R Leg     %1$-3s          %2$-3s       ", CurMech.GetIntStruc().GetLegPoints(), CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) ) + NL;
                }
            }
        }
        if( CurMech.IsOmnimech() ) {
            ArrayList l = CurMech.GetLoadouts();
            if ( !CurrentLoadoutOnly ) {
                CurMech.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
                retval += NL;
                retval += BuildEquipmentBlock() + NL;
                for( int i = 0; i < l.size(); i++ ) {
                    CurMech.SetCurLoadout( ((ifMechLoadout) l.get( i )).GetName() );
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
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        ArrayList w = new ArrayList();
        ArrayList EQ = new ArrayList();
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
        ArrayList Temp = new ArrayList();

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
                if( ((RangedWeapon) cur).IsUsingFCS() ) {
                    Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.GetFluffName( CurMech, cur ) + " w/ " + ((abPlaceable) ((RangedWeapon) cur).GetFCS()).CritName() + NL;
                } else {
                    Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.GetFluffName( CurMech, cur ) + NL;
                }
            } else {
                if( cur instanceof Equipment ) {
                    if( ((Equipment) cur).IsVariableSize() ) {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.CritName() + NL;
                    } else {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.GetFluffName( CurMech, cur ) + NL;
                    }
                } else {
                    Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.GetFluffName( CurMech, cur ) + NL;
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
        String retval = CurMech.GetHeatSinks().GetCurrentState().LookupName();
        if( CurMech.GetTechBase() == AvailableCode.TECH_BOTH ) {
            if( CurMech.GetHeatSinks().GetTechBase() == AvailableCode.TECH_CLAN ) {
                retval = "(CL) " + retval;
            } else {
                retval = "(IS) " + retval;
            }
        }
        return retval;
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
        ArrayList v = (ArrayList) CurMech.GetLoadout().GetNonCore().clone();

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
/*
        if( equips.length <= 1 ) {
            retval += ProcessEquipStatLines( cur, FileCommon.EncodeLocation( CurMech.GetLoadout().Find( cur ), CurMech.IsQuad() ), "" + cur.NumCrits(), 1 );
            return retval;
        }
*/
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
        if( CurMech.GetBaseLoadout().HasHDTurret() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetBaseLoadout().GetHDTurret() ), "CT", "-", 1, CurMech.GetBaseLoadout().GetHDTurret().GetTonnage() ) + NL;
        }
        if( CurMech.GetBaseLoadout().HasLTTurret() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetBaseLoadout().GetLTTurret() ), "LT", "-", 1, CurMech.GetBaseLoadout().GetLTTurret().GetTonnage() ) + NL;
        }
        if( CurMech.GetBaseLoadout().HasRTTurret() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetBaseLoadout().GetRTTurret() ), "RT", "-", 1, CurMech.GetBaseLoadout().GetRTTurret().GetTonnage() ) + NL;
        }
        if( CurMech.HasNullSig() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, CurMech.GetNullSig().CritName(), "*", "10", 7, 0.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasVoidSig() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, CurMech.GetVoidSig().CritName(), "*", "10", 7, 0.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasChameleon() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, CurMech.GetChameleon().CritName(), "*", "6", 6, 0.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasBlueShield() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, CurMech.GetBlueShield().CritName(), "*", "-", 7, 3.0 ) + NL;
            Special = true;
        }
        if( CurMech.UsingJumpBooster() ) {
            if( CurMech.IsQuad() ) {
                retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetJumpBooster() ), "*", "-", 8, CurMech.GetJumpBooster().GetTonnage() ) + NL;
            } else {
                retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetJumpBooster() ), "*", "-", 4, CurMech.GetJumpBooster().GetTonnage() ) + NL;
            }
            Special = true;
        }
        if( CurMech.HasEnviroSealing() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, CurMech.GetEnviroSealing().CritName(), "*", "-", 8, 0.0 ) + NL;
            Special = true;
        }
        if( CurMech.HasTracks() ) {
            if( CurMech.IsQuad() ) {
                retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetTracks() ), "*", "-", 4, CurMech.GetTracks().GetTonnage() ) + NL;
            } else {
                retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetTracks() ), "*", "-", 2, CurMech.GetTracks().GetTonnage() ) + NL;
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
        if( CurMech.UsingJumpBooster() ) {
            retval += "* The " + CurMech.GetJumpBooster().LookupName() + " occupies 2 slots in each leg." + NL;
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
        retval += String.format( "%1$-6s    %2$2s      %3$2s      %4$2s      %5$2s      %6$1s     %7$1s   Structure: %8$2s", BattleForceTools.GetMovementString( CurMech ), BFdmg[BFConstants.BF_SHORT], BFdmg[BFConstants.BF_MEDIUM], BFdmg[BFConstants.BF_LONG], BFdmg[BFConstants.BF_EXTREME], CurMech.GetBFSize(), BFdmg[BFConstants.BF_OV], CurMech.GetBFStructure() ) + NL;
        retval += "Special Abilities: " + bfs.getAbilitiesString() + NL;

        return retval;
    }

    private String BuildOmniLoadout() {
        // this routine builds the big equipment block at the bottom of the file
        String retval = "";
        String loc = "";
        String crits = "";
        ArrayList v = (ArrayList) CurMech.GetLoadout().GetNonCore().clone();

        retval += String.format( "Loadout Name: %1$-46s Cost: %2$,.0f", CurMech.GetLoadout().GetName(), Math.floor( CurMech.GetTotalCost() + 0.5 ) ) + NL;
        retval += String.format( "Tech Rating/Era Availability: %1$-31s BV2: %2$,d", CurMech.GetAvailability().GetBestCombinedCode(), CurMech.GetCurrentBV() ) + NL;
        if( CurMech.GetBaseRulesLevel() != CurMech.GetLoadout().GetRulesLevel() ) {
            if( CurMech.GetBaseTechbase() != CurMech.GetLoadout().GetTechBase() ) {
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
            retval += String.format( "Heat Sinks:         %1$-28s %2$-8s                %3" + tformat, GetHSType(), GetHSNum(), CurMech.GetHeatSinks().GetOmniTonnage() ) + NL;
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
                retval += String.format( "    %1$-68s %2" + tformat, "CASE Locations: " + FileCommon.GetCaseLocations( CurMech ), CurMech.GetCaseTonnage() ) + NL;
            }
        } else {
            if( CurMech.GetCaseTonnage() != 0.0f ) {
                retval += String.format( "    %1$-68s %2" + tformat, "CASE Locations: " + FileCommon.GetCaseLocations( CurMech ), CurMech.GetCaseTonnage() ) + NL;
            }
        }
        if( CurMech.GetCASEIITonnage() != 0.0f ) {
            retval += String.format( "    %1$-68s %2" + tformat, "CASE II Locations: " + FileCommon.GetCaseIILocations( CurMech ), CurMech.GetCASEIITonnage() ) + NL;
        }
        if( ! CurMech.GetEngine().IsNuclear() ) {
            if( CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                retval += String.format( "%1$-72s %2" + tformat, "Power Amplifiers:", CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() ) + NL;
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

        if( equips.length < 1 ) { return retval; }

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

        if( CurMech.GetLoadout().HasHDTurret() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetLoadout().GetHDTurret() ), "CT", "-", 1, CurMech.GetLoadout().GetHDTurret().GetTonnage() ) + NL;
        }
        if( CurMech.GetLoadout().HasLTTurret() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetLoadout().GetLTTurret() ), "LT", "-", 1, CurMech.GetLoadout().GetLTTurret().GetTonnage() ) + NL;
        }
        if( CurMech.GetLoadout().HasRTTurret() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, CurMech.GetLoadout().GetRTTurret() ), "RT", "-", 1, CurMech.GetLoadout().GetRTTurret().GetTonnage() ) + NL;
        }
        retval += String.format( "%1$s %2$-2s", "                                            Free Critical Slots:", CurMech.GetLoadout().FreeCrits() + NL );

        BattleForceStats bfs = new BattleForceStats( CurMech );
        int [] BFdmg = CurMech.GetBFDamage( bfs );
        retval += NL + "BattleForce Statistics" + NL;
        retval += String.format( "MV      S (+0)  M (+2)  L (+4)  E (+6)   Wt.   Ov   Armor:     %1$2s    Points: " + CurMech.GetBFPoints(), CurMech.GetBFArmor() ) + NL;
        retval += String.format( "%1$-6s    %2$2s      %3$2s      %4$2s      %5$2s      %6$1s     %7$1s   Structure: %8$2s", BattleForceTools.GetMovementString( CurMech ), BFdmg[BFConstants.BF_SHORT], BFdmg[BFConstants.BF_MEDIUM], BFdmg[BFConstants.BF_LONG], BFdmg[BFConstants.BF_EXTREME], CurMech.GetBFSize(), BFdmg[BFConstants.BF_OV], CurMech.GetBFStructure() ) + NL;
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
        String name = FileCommon.GetExportName( CurMech, p );
        if( numthisloc > 1 ) {
            name = numthisloc + " " + name + "s";
        }

        // build the string based on the type of equipment
        if( p instanceof Ammunition ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.FormatAmmoExportName( (Ammunition) p, numthisloc ), loc, "-", crits, p.GetTonnage() * numthisloc ) + NL;
        } else if( p instanceof RangedWeapon ) {
            double tons = p.GetTonnage();
            String add = "";
            if( ((RangedWeapon) p).IsUsingFCS() ) {
                abPlaceable a = (abPlaceable) ((RangedWeapon) p).GetFCS();
                tons -= a.GetTonnage();
                add += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, a ), loc, "-", a.NumCrits() * numthisloc, a.GetTonnage() * numthisloc ) + NL;
            }
            if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                tons -= 1.0f;
                abPlaceable a = ((RangedWeapon) p).GetCapacitor();
                add += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurMech, p ), loc, numthisloc * 5 + "*", a.NumCrits(), a.GetTonnage() * numthisloc ) + NL;
            }
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, name, loc, ((RangedWeapon) p).GetHeat() * numthisloc, crits, tons * numthisloc ) + NL + add;
        } else if( p instanceof MGArray ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, name, loc, "-", crits, ((MGArray) p).GetBaseTons() ) + NL;
            abPlaceable a = ((MGArray) p).GetMGType();
            retval += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5" + tformat, ((MGArray) p).GetNumMGs() + " " + FileCommon.GetExportName( CurMech, a ) + "s", loc, ((RangedWeapon) a).GetHeat() * ((MGArray) p).GetNumMGs(), ((MGArray) p).GetNumMGs(), ( ((MGArray) p).GetMGTons() * ((MGArray) p).GetNumMGs() ) ) + NL;
        } else if( p instanceof Equipment ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, name, loc, ((Equipment) p).GetHeat() * numthisloc, crits, p.GetTonnage() * numthisloc ) + NL;
        } else {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, name, loc, "-", crits, p.GetTonnage() * numthisloc ) + NL;
        }

        return retval;
    }

    private String FormatFluff( String s ) {
        // we're basically checking length here to limit it to 80 chars in length
        // first, seperate out all the newlines.
        s = s.replaceAll( "\n\r", "\n" );
        s = s.replace( "      ", "" );
        s = s.replace( "    ", "" );
        s = s.replace( "<p style=\"margin-top: 0\">", "" );
        s = s.replaceAll( "\t", "    " );
        s = s.replace( "</p>", "\n" );
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
        ArrayList lines = new ArrayList();
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
        Iterator itr = lines.iterator();
        while(itr.hasNext()) {
            ret[c] = (String) itr.next();
            c++;
        }

        return ret;
    }

    private String GetJumpJetDistanceLine() {
        String retval = ( CurMech.GetJumpJets().GetNumJJ() * 30 ) + " meters";
        if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
            retval += " (" + ( CurMech.GetAdjustedJumpingMP( false ) * 30 ) + " meters)";
        }
        if( CurMech.UsingJumpBooster() ) {
            retval += " / " + ( CurMech.GetJumpBoosterMP() * 30 ) + " meters";
            if( CurMech.GetJumpBoosterMP() != CurMech.GetAdjustedBoosterMP( false ) ) {
                retval += " (" + ( CurMech.GetAdjustedBoosterMP( false ) * 30 ) + " meters)";
            }
        }
        return retval;
    }

    private String GetJumpingMPLine() {
        String retval = "" + CurMech.GetJumpJets().GetNumJJ();
        if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
            retval += " (" + CurMech.GetAdjustedJumpingMP( false ) + ")";
        }
        if( CurMech.UsingJumpBooster() ) {
            retval += " / " + CurMech.GetJumpBoosterMP();
            if( CurMech.GetJumpBoosterMP() != CurMech.GetAdjustedBoosterMP( false ) ) {
                retval += " (" + CurMech.GetAdjustedBoosterMP( false ) + ")";
            }
        }
        return retval;
    }

    private String GetJumpJetTypeLine() {
        String retval = "";
        if( CurMech.UsingJumpBooster() ) {
            if( CurMech.GetJumpJets().GetNumJJ() <= 0 ) {
                retval = "Jump Booster";
            } else {
                if( CurMech.GetJumpJets().IsImproved() ) {
                    retval = "Improved + Jump Booster";
                } else {
                    if( CurMech.GetJumpJets().IsUMU() ) {
                        retval = "UMU + Jump Booster";
                    } else{
                        retval = "Standard + Jump Booster";
                    }
                }
            }
        } else {
            if( CurMech.GetJumpJets().GetNumJJ() <= 0 ) {
                retval = "";
            } else {
                if( CurMech.GetJumpJets().IsImproved() ) {
                    retval = "Improved";
                } else {
                    if( CurMech.GetJumpJets().IsUMU() ) {
                        retval = "UMU";
                    } else{
                        retval = "Standard";
                    }
                }
            }
        }
        return retval;
    }


    public void WriteList(String filename, Scenario scenario) throws IOException {
        if ( !filename.endsWith(".csv") ) { filename += ".csv"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        FR.write( CSVFormat("unit_type") );
        FR.write( CSVFormat("sub_unit_type") );
        FR.write( CSVFormat("unit_name") );
        FR.write( CSVFormat("model_number") );
        FR.write( CSVFormat("tonnage") );
        FR.write( CSVFormat("bv2") );
        FR.write( CSVFormat("tw rules_level") );
        FR.write( CSVFormat("technology base") );
        FR.write( CSVFormat("source") );
        FR.write( CSVFormat("date") );
        FR.write( CSVFormat("era") );
        FR.write( "PV,Wt,MV,S,M,L,E,OV,Armor,Internal,Special Abilities" );
        FR.newLine();

        String datum = "";
        for ( Unit u : scenario.getUnits() ) {
            FR.write( CSVFormat(CommonTools.UnitTypes[u.UnitType]) );
            datum = "BattleMech";
            if ( u.IsOmni() ) { datum = "OmniMech"; }
            FR.write( CSVFormat(datum) );
            FR.write( CSVFormat(u.Name ));
            FR.write( CSVFormat((u.Model + " " + u.Configuration).trim()) );
            FR.write( CSVFormat(u.Tonnage+"") );
            FR.write( CSVFormat(u.BaseBV+"") );
            FR.write( CSVFormat("") );
            FR.write( CSVFormat("") );
            FR.write( CSVFormat("") );
            FR.write( CSVFormat("") );
            FR.write( CSVFormat("") );
            FR.write( u.getBFStats().SerializeCSV( false ) );
            FR.newLine();
        }
        FR.close();
    }

    public void WriteList(String filename, UnitList list) throws IOException {
        if ( !filename.endsWith(".csv") ) { filename += ".csv"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        FR.write( CSVFormat("unit_type") );
        FR.write( CSVFormat("sub_unit_type") );
        FR.write( CSVFormat("unit_name") );
        FR.write( CSVFormat("model_number") );
        FR.write( CSVFormat("tonnage") );
        FR.write( CSVFormat("bv2") );
        FR.write( CSVFormat("tw rules_level") );
        FR.write( CSVFormat("technology base") );
        FR.write( CSVFormat("source") );
        FR.write( CSVFormat("date") );
        FR.write( CSVFormat("era") );
        FR.write( CSVFormat("cost") );
        FR.write( "PV,Wt,MV,S,M,L,E,OV,Armor,Internal,Special Abilities" );
        FR.newLine();

        String datum = "", omniname = "";
        for (int i=0; i < list.Size(); i++) {
            UnitListData data = (UnitListData) list.Get(i);
            switch(data.getUnitType()) {
                case CommonTools.BattleMech:
                    datum = "BattleMech";
                    omniname = data.isOmni() ? "OmniMech":datum;
                    break;
                case CommonTools.Vehicle:
                    datum = "Combat Vehicle";
                    omniname = data.isOmni() ? "OmniVehicle":datum;
                    break;
            }
            FR.write( CSVFormat(datum) );
            FR.write( CSVFormat(omniname) );
            FR.write( CSVFormat(data.getName()) );
            FR.write( CSVFormat((data.getModel() + " " + data.getConfig()).trim()) );
            FR.write( CSVFormat(data.getTonnage()+"") );
            FR.write( CSVFormat(data.getBV()+"") );
            FR.write( CSVFormat(data.getLevel()) );
            FR.write( CSVFormat(data.getTech()) );
            FR.write( CSVFormat(data.getSource()) );
            FR.write( CSVFormat(data.getYear()+"") );
            FR.write( CSVFormat(data.getEra()) );
            FR.write( CSVFormat(data.getCost()+"") );
            FR.write( data.getBattleForceStats().SerializeCSV( false ) );
            FR.newLine();
        }
        FR.close();
    }

    public void WriteCost( Mech m, String filename ) throws IOException {
        filename += m.GetFullName();
        if ( !filename.endsWith(".txt") ) { filename += ".txt"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        CostBVBreakdown cost = new CostBVBreakdown(m);
        FR.write(cost.Render());
        FR.close();
    }

    public void WriteBFList(String filename, UnitList list) throws IOException {
        if ( !filename.endsWith(".csv") ) { filename += ".csv"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        String message = "";
        FR.write("Element,PV,Wt,MV,S,M,L,E,OV,Armor,Internal,Special Abilities");
        FR.newLine();
        for ( UnitListData mech : list.getList() ) {
            FR.write(mech.getBattleForceStats().SerializeCSV( true ));
            FR.newLine();
        }

        FR.close();

        if ( !message.isEmpty() ) {
            Media.Messager("Could not write out the following:\n" + message);
        }
    }

    public void WriteBFList(String filename, Scenario scenario) throws IOException {
        if ( !filename.endsWith(".csv") ) { filename += ".csv"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        String message = "", force= "", group = "";
        FR.write("Force,Group,Element,PV,Wt,MV,S,M,L,E,OV,Armor,Internal,Special Abilities");
        FR.newLine();
        for ( Force f : scenario.getForces() ) {
            force = f.ForceName;
            for ( Unit u : f.getUnits() ) {
                group = u.getGroup();
                FR.write(CSVFormat(force) + CSVFormat(group) + u.getBFStats().SerializeCSV( true ));
                FR.newLine();
            }
        }

        FR.close();

        if ( !message.isEmpty() ) {
            Media.Messager("Could not write out the following:\n" + message);
        }
    }

    public void WriteFactorList(String filename, Scenario scenario ) throws IOException {
        if ( !filename.endsWith(".csv") ) { filename += ".csv"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        String message = "";
        FR.write("Unit,Type,Prb,ECM,Spd,Jmp,TSM,Phys,Armr,TC,8+,10+,Hd Cap,Tot Dmg,Base BV, Adj BV");
        FR.newLine();
        for ( Force f : scenario.getForces() ) {
            if ( !f.ForceName.isEmpty()) {FR.write(f.ForceName);}
            for ( Group g : f.Groups ) {
                FR.newLine();
                for ( Unit u : g.getUnits() ) {
                    FR.write(u.SerializeFactors());
                    FR.newLine();
                }
            }
            FR.write(f.SerializeFactors());
            FR.newLine();
            FR.newLine();
        }

        FR.close();

        if ( !message.isEmpty() ) {
            Media.Messager("Could not write out the following:\n" + message);
        }

    }

    public String CSVFormat( String data ) {
        if ( data.contains(",") )
            return "\"" + data + "\",";
        else
            return data + ",";
    }
}
