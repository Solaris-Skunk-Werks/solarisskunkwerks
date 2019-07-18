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

import Force.Force;
import Force.Group;
import Force.Scenario;
import Force.Unit;
import battleforce.BFConstants;
import battleforce.BattleForceStats;
import battleforce.BattleForceTools;
import common.CommonTools;
import components.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import list.UnitList;
import list.UnitListData;
import utilities.CostBVBreakdown;

public class CVTXTWriter {

    private CombatVehicle CurVee;
    private String NL,
                   tformat = "$6.2f";
    private ArrayList<Force> forces;

    public boolean CurrentLoadoutOnly = false;

    public CVTXTWriter() {
        NL = System.getProperty( "line.separator" );
    }

    public CVTXTWriter( ArrayList<Force> forces ) {
        this.forces = forces;
    }

    public CVTXTWriter( CombatVehicle m ) {
        this();
        CurVee = m;
        if( CurVee.UsingFractionalAccounting() ) {
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

        if( CurVee.IsOmni() ) {
            // start out with the base chassis.  we'll switch later when needed
            CurVee.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
        }
        retval += CurVee.GetName() + " " + CurVee.GetModel() + NL + NL;
        retval += "Mass: " + CurVee.GetTonnage() + " tons" + NL;
        if( CurVee.IsPrimitive() )  {
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurVee.GetBaseTechbase() ) + " (Primitive)" + NL;
        } else {
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurVee.GetBaseTechbase() ) + NL;
        }
        retval += "Motive Type: " + CurVee.GetMotiveLookupName() + ( CurVee.IsOmni() ? " OmniVehicle":"") + NL;
        retval += "Rules Level: " + CommonTools.GetRulesLevelString( CurVee.GetRulesLevel() ) + NL;
        retval += "Era: " + CommonTools.DecodeEra( CurVee.GetEra() ) + NL;
        retval += "Tech Rating/Era Availability: " + CurVee.GetAvailability().GetBestCombinedCode() + NL;
        retval += "Production Year: " + CurVee.GetYear() + NL;
        retval += "Cost: " + String.format( "%1$,.0f", Math.floor( CurVee.GetTotalCost() + 0.5 ) ) + " C-Bills" + NL;
        retval += "Battle Value: " + String.format( "%1$,d", CurVee.GetCurrentBV() ) + NL + NL;

        if( CurVee.UsingFractionalAccounting() ) {
            retval += "Construction Options: Fractional Accounting" + NL + NL;
        }

        //retval += "Chassis: " + CurVee.GetChassisModel() + " " + CurVee.GetIntStruc().CritName() + NL;
        retval += "Power Plant: " + CurVee.GetEngineManufacturer() + " " + CurVee.GetEngine().GetRating() + " " + CurVee.GetEngine() + NL;
        retval += "Cruise Speed: " + CommonTools.FormatSpeed( CurVee.getCruiseMP() * 10.8 ) + " km/h" + NL;
        retval += "Flanking Speed: " + CommonTools.FormatSpeed( CurVee.getFlankMP() * 10.8 ) + " km/h" + NL;
        if ( CurVee.GetJumpJets().GetNumJJ() > 0 ) {
            retval += "Jump Jets: " + CurVee.GetJJModel() + NL;
            retval += "    Jump Capacity: " + GetJumpJetDistanceLine() + NL;
        }
        retval += "Armor: " + CurVee.GetArmorModel() + " " + CurVee.GetArmor().CritName() + NL;
        retval += "Armament:" + NL;
        retval += GetArmament();
        retval += "Manufacturer: " + CurVee.GetCompany() + NL;
        retval += "    Primary Factory: " + CurVee.GetLocation() + NL;
        retval += BuildComputerBlock() + NL + NL;
//        retval += "================================================================================" + NL;
        if( ! CurVee.getOverview().equals( "" ) ) {
            retval += "Overview:" + NL;
            retval += FormatFluff( CurVee.getOverview() ) + NL + NL;
        }
        if( ! CurVee.getCapabilities().equals( "" ) ) {
            retval += "Capabilities:" + NL;
            retval += FormatFluff( CurVee.getCapabilities() ) + NL + NL;
        }
        if( ! CurVee.getHistory().equals( "" ) ) {
            retval += "Battle History:" + NL;
            retval += FormatFluff( CurVee.getHistory() ) + NL + NL;
        }
        if( ! CurVee.getDeployment().equals( "" ) ) {
            retval += "Deployment:" + NL;
            retval += FormatFluff( CurVee.getDeployment() ) + NL + NL;
        }
        if( ! CurVee.getVariants().equals( "" ) ) {
            retval += "Variants:" + NL;
            retval += FormatFluff( CurVee.getVariants() ) + NL + NL;
        }
        if( ! CurVee.getNotables().equals( "" ) ) {
            retval += "Notable 'Mechs & MechWarriors: " + NL;
            retval += FormatFluff( CurVee.getNotables() ) + NL + NL;
        }
        if( ! CurVee.GetAdditional().equals( "" ) ) {
            retval += "Additional: " + NL;
            retval += FormatFluff( CurVee.GetAdditional() ) + NL + NL;
        }
        retval += "================================================================================" + NL;
        retval += GetMiniTextExport();

        return retval;
    }

    public String GetMiniTextExport() {
        String retval = "";

        if ( CurrentLoadoutOnly ) {
            retval += CurVee.GetName() + " " + CurVee.GetModel() + NL + NL;
            retval += "Tech Base: " + CommonTools.GetTechbaseString( CurVee.GetTechbase() ) + NL;
            retval += "Chassis Config: ";
            String chassisString = CurVee.GetMotiveLookupName();
            if ( CurVee.IsOmni() ) { chassisString += " OmniVehicle"; }
            retval += chassisString + NL;
            retval += String.format( "Era: %1$-56s Cost: %2$,.0f", CommonTools.DecodeEra( CurVee.GetEra() ), Math.floor( CurVee.GetTotalCost() + 0.5 ) ) + NL;
            retval += String.format( "Tech Rating/Era Availability: %1$-32s BV2: %2$,d", CurVee.GetAvailability().GetBestCombinedCode(), CurVee.GetCurrentBV() ) + NL + NL;
        }
        retval += "Equipment           Type                         Rating                   Mass  " + NL;
        retval += "--------------------------------------------------------------------------------" + NL;
        retval += String.format( "Internal Structure: %1$-28s %2$3s points              %3" + tformat, CurVee.GetIntStruc().CritName(), CurVee.GetIntStruc().GetTotalPoints(), CurVee.GetIntStruc().GetTonnage() ) + NL;
        retval += String.format( "Engine:             %1$-28s %2$3s                     %3" + tformat, FileCommon.GetExportName( CurVee, CurVee.GetEngine() ), CurVee.GetEngine().GetRating(), CurVee.GetEngine().GetTonnage() ) + NL;
        retval += "    Cruise MP:  " + CurVee.getCruiseMP() + NL;
        retval += "    Flank MP:   " + CurVee.getFlankMP() + NL;
        if( CurVee.GetJumpJets().GetNumJJ() > 0 ) {
            retval += "    Jumping MP: " + GetJumpingMPLine() + " " + GetJumpJetTypeLine() + NL;
            retval += String.format( "    %1$-68s %2$6.2f", "Jump Jet Locations: " + FileCommon.GetJumpJetLocations( CurVee ), CurVee.GetJumpJets().GetTonnage() ) + NL;
        }
        retval += String.format( "Heat Sinks:         %1$-28s %2$-8s                %3" + tformat, GetHSType(), GetHSNum(), CurVee.GetHeatSinks().GetTonnage() ) + NL;
        retval += String.format( "Control Equipment:  %1$-28s %2$-8s                %3" + tformat, "", "", CurVee.GetControls() ) + NL;
        retval += String.format( "Lift Equipment:     %1$-28s %2$-8s                %3" + tformat, "", "", CurVee.GetLiftEquipmentTonnage() ) + NL;
        if( ! CurVee.GetEngine().IsNuclear() ) {
            if( CurVee.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                retval += String.format( "%1$-72s %2" + tformat, "Power Amplifiers:", CurVee.GetLoadout().GetPowerAmplifier().GetTonnage() ) + NL;
            }
        }
        if ( CurVee.isHasTurret1() ) 
            retval += String.format( "Turret:             %1$-28s %2$-8s                %3" + tformat, "", "", CurVee.GetLoadout().GetTurret().GetTonnage() ) + NL;
        if ( CurVee.isHasTurret2() ) 
            retval += String.format( "Rear Turret:        %1$-28s %2$-8s                %3" + tformat, "", "", CurVee.GetLoadout().GetRearTurret().GetTonnage() ) + NL;
        if( CurVee.GetArmor().GetBAR() < 10 ) {
            retval += String.format( "Armor:              %1$-28s AV - %2$3s                %3" + tformat, CurVee.GetArmor().CritName() + " (BAR: " + CurVee.GetArmor().GetBAR() +")", CurVee.GetArmor().GetArmorValue(), CurVee.GetArmor().GetTonnage() ) + NL;
        } else {
            retval += String.format( "Armor:              %1$-28s AV - %2$3s                %3" + tformat, CurVee.GetArmor().CritName(), CurVee.GetArmor().GetArmorValue(), CurVee.GetArmor().GetTonnage() ) + NL;
        }
        retval += NL + "                                                      Armor      " + NL;
        retval += "                                                      Factor     " + NL;
        if( CurVee.GetArmor().IsPatchwork() ) {
            retval += String.format( "                   Front %1$29s    %2$-3s       ", "(" + CurVee.GetArmor().GetFrontArmorType().LookupName() + ")", CurVee.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) ) + NL;
            retval += String.format( "                  Left %1$29s    %2$-3s       ", "(" + CurVee.GetArmor().GetLeftArmorType().LookupName() + ")", CurVee.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) ) + NL;
            retval += String.format( "                  Right %1$29s    %2$-3s       ", "(" + CurVee.GetArmor().GetRightArmorType().LookupName() + ")", CurVee.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) ) + NL;
            retval += String.format( "            Rear %1$29s    %2$-3s       ", "(" + CurVee.GetArmor().GetRearArmorType().LookupName() + ")", CurVee.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) ) + NL;
            if ( CurVee.isHasTurret1() ) {
                retval += String.format( "         Turret %1$29s    %2$-3s       ", "(" + CurVee.GetArmor().GetTurret1ArmorType().LookupName() + ")", CurVee.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) ) + NL;
            }
            if ( CurVee.isHasTurret2() ) {
                retval += String.format( "         Rear Turret %1$29s    %2$-3s       ", "(" + CurVee.GetArmor().GetTurret2ArmorType().LookupName() + ")", CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET2 ) ) + NL;
            }
            if ( CurVee.IsVTOL() ) {
                retval += String.format( "         Rotor %1$29s    %2$-3s       ", "(" + CurVee.GetArmor().GetRotorArmorType().LookupName() + ")", CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_ROTOR ) ) + NL;
            }
        } else {
            retval += String.format( "                                               Front     %1$-3s       ", CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_FRONT ) ) + NL;
            retval += String.format( "                                          Left/Right  %1$3s/%2$-3s       ", CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_LEFT ), CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_RIGHT) ) + NL;
            if ( CurVee.isHasTurret1() )
                retval += String.format( "                                              Turret     %1$-3s       ", CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET1 ) ) + NL;
            retval += String.format( "                                                Rear     %1$-3s       ", CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_REAR ) ) + NL;
            if ( CurVee.isHasTurret2() )
                retval += String.format( "                                         Rear Turret     %1$-3s       ", CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET2 ) ) + NL;
            if ( CurVee.IsVTOL() )
                retval += String.format( "                                               Rotor     %1$-3s       ", CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_ROTOR ) ) + NL;
        }
        if( CurVee.IsOmni() ) {
            ArrayList l = CurVee.GetLoadouts();
            if ( !CurrentLoadoutOnly ) {
                CurVee.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
                retval += NL;
                retval += BuildEquipmentBlock() + NL;
                for( int i = 0; i < l.size(); i++ ) {
                    CurVee.SetCurLoadout( ((ifCVLoadout) l.get( i )).GetName() );
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
        ArrayList v = CurVee.GetLoadout().GetNonCore();
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
            if( CurVee.IsOmni() ) {
                return "    " + ( CurVee.GetTonnage() - CurVee.GetCurrentTons() ) + " tons of pod space." + NL;
            } else {
                return "    None" + NL;
            }
        }

        // sort the weapons according to current BV, assuming front facing
        abPlaceable[] weapons = CurVee.SortWeapons( w, false );
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
                    Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.GetFluffName( CurVee, cur ) + " w/ " + ((abPlaceable) ((RangedWeapon) cur).GetFCS()).CritName() + NL;
                } else {
                    Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.GetFluffName( CurVee, cur ) + NL;
                }
            } else {
                if( cur instanceof Equipment ) {
                    if( ((Equipment) cur).IsVariableSize() ) {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + cur.CritName() + NL;
                    } else {
                        Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.GetFluffName( CurVee, cur ) + NL;
                    }
                } else {
                    Armament = "    " + numthistype + " " + cur.GetManufacturer() + " " + FileCommon.GetFluffName( CurVee, cur ) + NL;
                }
            }
            if( CurVee.IsOmni() ) {
                Armament += "    " + ( CurVee.GetTonnage() - CurVee.GetCurrentTons() ) + " tons of pod space." + NL;
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
                if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
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
                if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
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

        if( CurVee.IsOmni() ) {
            Armament += "    " + ( CurVee.GetTonnage() - CurVee.GetCurrentTons() ) + " tons of pod space." + NL;
        }

        // all done
        return Armament;
    }

    private String GetHSType() {
        String retval = CurVee.GetHeatSinks().GetCurrentState().LookupName();
        if( CurVee.GetTechBase() == AvailableCode.TECH_BOTH ) {
            if( CurVee.GetHeatSinks().GetTechBase() == AvailableCode.TECH_CLAN ) {
                retval = "(CL) " + retval;
            } else {
                retval = "(IS) " + retval;
            }
        }
        return retval;
    }

    private String GetHSNum() {
        // provides a formated heat sink dissipation string
        if( CurVee.GetHeatSinks().IsDouble() ) {
            return CurVee.GetHeatSinks().GetNumHS() + "(" + CurVee.GetHeatSinks().TotalDissipation() + ")";
        } else {
            return "" + CurVee.GetHeatSinks().GetNumHS();
        }
    }

    private String BuildEquipmentBlock() {
        // this routine builds the big equipment block at the bottom of the file
//        String retval = "Equipment                                        Location     Critical    Mass  " + NL;
        String loc = "";
        String crits = "";
        ArrayList v = (ArrayList) CurVee.GetLoadout().GetNonCore().clone();

        // add in MASC and the targeting computer if needed.
        if( CurVee.GetPhysEnhance().IsMASC() ) {
            v.add( CurVee.GetPhysEnhance() );
        }
        if( CurVee.UsingTC() ) {
            //don't add as we already have it in the Body 
            //v.add( CurVee.GetTC() );
        }
        if( CurVee.GetLoadout().HasSupercharger() ) {
            v.add( CurVee.GetLoadout().GetSupercharger() );
        }

        if( v.size() < 1 ) { return ""; }
        String retval = "================================================================================" + NL;
        retval += "Equipment                                 Location    Heat     Spaces     Mass  " + NL;
        retval += "--------------------------------------------------------------------------------" + NL;

        // now sort the equipment by location
        v = FileCommon.SortEquipmentForStats( CurVee, v );

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
            retval += ProcessEquipStatLines( cur, FileCommon.EncodeLocation( CurVee.GetLoadout().Find( cur ), CurVee.IsQuad() ), "" + cur.NumCVSpaces(), 1 );
            return retval;
        }
*/
        // count up individual weapons and build their string
        for( int i = 1; i <= equips.length; i++ ) {
            // find any other weapons of this type
            int locint = CurVee.GetLoadout().Find( cur );
            for( int j = 0; j < equips.length; j++ ) {
                if( equips[j] != null ) {
                    if( cur instanceof Equipment ) {
                        if( ((Equipment) cur).IsVariableSize() ) {
                            if( equips[j].CritName().equals( cur.CritName() ) && CurVee.GetLoadout().Find( equips[j] ) == locint ) {
                                numthisloc++;
                                equips[j] = null;
                            }
                        } else {
                            if( equips[j].LookupName().equals( cur.LookupName() ) && CurVee.GetLoadout().Find( equips[j] ) == locint ) {
                                numthisloc++;
                                equips[j] = null;
                            }
                        }
                    } else {
                        if( equips[j].LookupName().equals( cur.LookupName() ) && CurVee.GetLoadout().Find( equips[j] ) == locint ) {
                            numthisloc++;
                            equips[j] = null;
                        }
                    }
                }
            }

            if( numthisloc > 1) {
                crits = "" + ( cur.NumCVSpaces() * numthisloc );
            } else {
                crits = "" + cur.NumCVSpaces();
            }
            loc = FileCommon.EncodeLocation( locint, CurVee.IsQuad(), common.CommonTools.Vehicle );
            // add the current weapon to the armament string
            retval += ProcessEquipStatLines( cur, loc, crits, numthisloc );

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
        if( CurVee.HasBlueShield() ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, CurVee.GetBlueShield().CritName(), "*", "-", 7, 3.0 ) + NL;
            Special = true;
        }

        if( Special ) {
            retval += NL;
        }

        // add in the equipment footers if needed
        if( CurVee.HasBlueShield() ) {
            retval += "* The " + CurVee.GetBlueShield().LookupName() + " occupies 1 slot in every location except the HD." + NL;
        }

        BattleForceStats bfs = new BattleForceStats( CurVee );
        int [] BFdmg = CurVee.GetBFDamage( bfs );
        retval += NL + "BattleForce Statistics" + NL;
        retval += String.format( "MV      S (+0)  M (+2)  L (+4)  E (+6)   Wt.   Ov   Armor:     %1$2s    Points: " + CurVee.GetBFPoints(), CurVee.GetBFArmor() ) + NL;
        retval += String.format( "%1$-6s    %2$2s      %3$2s      %4$2s      %5$2s      %6$1s     %7$1s   Structure: %8$2s", BattleForceTools.GetMovementString( CurVee ), BFdmg[BFConstants.BF_SHORT], BFdmg[BFConstants.BF_MEDIUM], BFdmg[BFConstants.BF_LONG], BFdmg[BFConstants.BF_EXTREME], CurVee.GetBFSize(), BFdmg[BFConstants.BF_OV], CurVee.GetBFStructure() ) + NL;
        retval += "Special Abilities: " + bfs.getAbilitiesString() + NL;

        return retval;
    }

    private String BuildOmniLoadout() {
        // this routine builds the big equipment block at the bottom of the file
        String retval = "";
        String loc = "";
        String crits = "";
        ArrayList v = (ArrayList) CurVee.GetLoadout().GetNonCore().clone();

        retval += String.format( "Loadout Name: %1$-46s Cost: %2$,.0f", CurVee.GetLoadout().GetName(), Math.floor( CurVee.GetTotalCost() + 0.5 ) ) + NL;
        retval += String.format( "Tech Rating/Era Availability: %1$-31s BV2: %2$,d", CurVee.GetAvailability().GetBestCombinedCode(), CurVee.GetCurrentBV() ) + NL;
        if( CurVee.GetBaseRulesLevel() != CurVee.GetLoadout().GetRulesLevel() ) {
            if( CurVee.GetBaseTechbase() != CurVee.GetLoadout().GetTechBase() ) {
                retval += String.format( "Rules Level: %1$-42s %2$s", CommonTools.GetRulesLevelString( CurVee.GetLoadout().GetRulesLevel() ), "Tech Base: " + CommonTools.GetTechbaseString( CurVee.GetLoadout().GetTechBase() ) ) + NL;
            } else {
                retval += "Rules Level: " + CommonTools.GetRulesLevelString( CurVee.GetLoadout().GetRulesLevel() ) + NL;
            }
        }

        // build the starting block for the loadout information
        retval += NL + "Equipment           Type                         Rating                   Mass  " + NL;
        retval += "--------------------------------------------------------------------------------" + NL;
        if( CurVee.GetJumpJets().GetNumJJ() > 0 ) {
            if( CurVee.GetJumpJets().IsImproved() ) {
                retval += "    Jumping MP: " + CurVee.GetJumpJets().GetNumJJ() + "  (Improved)" + NL;
            } else {
                if( CurVee.GetJumpJets().IsUMU() ) {
                    retval += "    Jumping MP: " + CurVee.GetJumpJets().GetNumJJ() + "  (UMU)" + NL;
                } else {
                    retval += "    Jumping MP: " + CurVee.GetJumpJets().GetNumJJ() + "  (Standard)" + NL;
                }
            }
        }
        if( CurVee.GetJumpJets().GetNumJJ() > 0 ) {
            retval += String.format( "    %1$-68s %2$6.2f", "Jump Jet Locations: " + FileCommon.GetJumpJetLocations( CurVee ), CurVee.GetJumpJets().GetOmniTonnage() ) + NL;
        }
        if( CurVee.GetHeatSinks().GetNumHS() > CurVee.GetHeatSinks().GetBaseLoadoutNumHS() ) {
            retval += String.format( "Heat Sinks:         %1$-28s %2$-8s                %3" + tformat, GetHSType(), GetHSNum(), CurVee.GetHeatSinks().GetOmniTonnage() ) + NL;
        }
        if( ! CurVee.GetEngine().IsNuclear() ) {
            if( CurVee.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                retval += String.format( "%1$-72s %2" + tformat, "Power Amplifiers:", CurVee.GetLoadout().GetPowerAmplifier().GetTonnage() ) + NL;
            }
        }

        // add in MASC and the targeting computer if needed.
        if( CurVee.GetPhysEnhance().IsMASC() ) {
            v.add( CurVee.GetPhysEnhance() );
        }
        if( CurVee.UsingTC() ) {
            //don't add as we already have it in the Body of the Vehicle
            //v.add( CurVee.GetTC() );
        }
        if( CurVee.GetLoadout().HasSupercharger() ) {
            v.add( CurVee.GetLoadout().GetSupercharger() );
        }

        // now sort the equipment by location
        v = FileCommon.SortEquipmentForStats( CurVee, v );

        // turn the equipment into an array
        abPlaceable[] equips = new abPlaceable[v.size()];
        for( int i = 0; i < v.size(); i++ ) {
            equips[i] = (abPlaceable) v.get( i );
        }

        // the basic equipment block header
        retval += NL + "Equipment                                 Location    Heat     Spaces     Mass  " + NL;
        retval += "--------------------------------------------------------------------------------" + NL;

        if( equips.length < 1 ) { return retval; }

        // we'll want to consolidate equipment within locations.
        int numthisloc = 1;
        abPlaceable cur = equips[0];
        equips[0] = null;

        if( equips.length <= 1 ) {
            retval += ProcessEquipStatLines( cur, FileCommon.EncodeLocation( CurVee.GetLoadout().Find( cur ), CurVee.IsQuad(), common.CommonTools.Vehicle ), "" + cur.NumCVSpaces(), 1 );
            return retval;
        }

        // count up individual weapons and build their string
        for( int i = 1; i <= equips.length; i++ ) {
            // find any other weapons of this type
            int locint = CurVee.GetLoadout().Find( cur );
            for( int j = 0; j < equips.length; j++ ) {
                if( equips[j] != null ) {
                    if( cur instanceof Equipment ) {
                        if( ((Equipment) cur).IsVariableSize() ) {
                            if( equips[j].CritName().equals( cur.CritName() ) && CurVee.GetLoadout().Find( equips[j] ) == locint ) {
                                numthisloc++;
                                equips[j] = null;
                            }
                        } else {
                            if( equips[j].LookupName().equals( cur.LookupName() ) && CurVee.GetLoadout().Find( equips[j] ) == locint ) {
                                numthisloc++;
                                equips[j] = null;
                            }
                        }
                    } else {
                        if( equips[j].LookupName().equals( cur.LookupName() ) && CurVee.GetLoadout().Find( equips[j] ) == locint ) {
                            numthisloc++;
                            equips[j] = null;
                        }
                    }
                }
            }

            if( numthisloc > 1) {
                crits = "" + ( cur.NumCVSpaces() * numthisloc );
            } else {
                crits = "" + cur.NumCVSpaces();
            }
            loc = FileCommon.EncodeLocation( locint, CurVee.IsQuad(), common.CommonTools.Vehicle );
            // add the current weapon to the armament string
            retval += ProcessEquipStatLines( cur, loc, crits, numthisloc );

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

        BattleForceStats bfs = new BattleForceStats( CurVee );
        int [] BFdmg = CurVee.GetBFDamage( bfs );
        retval += NL + "BattleForce Statistics" + NL;
        retval += String.format( "MV      S (+0)  M (+2)  L (+4)  E (+6)   Wt.   Ov   Armor:     %1$2s    Points: " + CurVee.GetBFPoints(), CurVee.GetBFArmor() ) + NL;
        retval += String.format( "%1$-6s    %2$2s      %3$2s      %4$2s      %5$2s      %6$1s     %7$1s   Structure: %8$2s", BattleForceTools.GetMovementString( CurVee ), BFdmg[BFConstants.BF_SHORT], BFdmg[BFConstants.BF_MEDIUM], BFdmg[BFConstants.BF_LONG], BFdmg[BFConstants.BF_EXTREME], CurVee.GetBFSize(), BFdmg[BFConstants.BF_OV], CurVee.GetBFStructure() ) + NL;
        retval += "Special Abilities: " + bfs.getAbilitiesString() + NL;

        return retval;
    }

    private String BuildComputerBlock() {
        String retval = "";
/*
        abPlaceable ECM = FileCommon.HasECM( CurVee );
        abPlaceable BAP = FileCommon.HasBAP( CurVee );
        Object[] C3 = FileCommon.HasC3( CurVee );
*/
        // start communications system line
        retval += "Communications System: " + CurVee.GetCommSystem() + NL;
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
        retval += "Targeting and Tracking System: " + CurVee.GetTandTSystem();
/*        if( ! ( BAP instanceof EmptyItem ) ) {
            if( CurVee.UsingTC() ) {
                retval += NL + "    w/ " + BAP.GetManufacturer() + " " + BAP.GetCritName() + NL + "    and " + CurVee.GetTC().GetCritName();
            } else {
                retval += NL + "    w/ " + BAP.GetManufacturer() + " " + BAP.GetCritName();
            }
        } else {
            if( CurVee.UsingTC() ) {
                retval += NL + "    w/ " + CurVee.GetTC().GetCritName();
            }
        }
*/
        return retval;
    }

    private String ProcessEquipStatLines( abPlaceable p, String loc, String crits, int numthisloc ) {
        String retval = "";
        String name = FileCommon.GetExportName( CurVee, p );
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
                add += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurVee, a ), loc, "-", a.NumCVSpaces() * numthisloc, a.GetTonnage() * numthisloc ) + NL;
            }
            if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                tons -= 1.0f;
                abPlaceable a = ((RangedWeapon) p).GetCapacitor();
                add += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5" + tformat, FileCommon.GetExportName( CurVee, p ), loc, numthisloc * 5 + "*", a.NumCVSpaces(), a.GetTonnage() * numthisloc ) + NL;
            }
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, name, loc, ((RangedWeapon) p).GetHeat() * numthisloc, crits, tons * numthisloc ) + NL + add;
        } else if( p instanceof MGArray ) {
            retval += String.format( "%1$-44s %2$-9s %3$-9s %4$-7s %5" + tformat, name, loc, "-", crits, ((MGArray) p).GetBaseTons() ) + NL;
            abPlaceable a = ((MGArray) p).GetMGType();
            retval += String.format( "    %1$-40s %2$-9s %3$-9s %4$-7s %5" + tformat, ((MGArray) p).GetNumMGs() + " " + FileCommon.GetExportName( CurVee, a ) + "s", loc, ((RangedWeapon) a).GetHeat() * ((MGArray) p).GetNumMGs(), ((MGArray) p).GetNumMGs(), ( ((MGArray) p).GetMGTons() * ((MGArray) p).GetNumMGs() ) ) + NL;
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
        String retval = ( CurVee.GetJumpJets().GetNumJJ() * 30 ) + " meters";
        return retval;
    }

    private String GetJumpingMPLine() {
        String retval = "" + CurVee.GetJumpJets().GetNumJJ();
        return retval;
    }

    private String GetJumpJetTypeLine() {
        String retval = "";
        if( CurVee.GetJumpJets().GetNumJJ() <= 0 ) {
            retval = "";
        } else {
            if( CurVee.GetJumpJets().IsImproved() ) {
                retval = "Improved";
            } else {
                if( CurVee.GetJumpJets().IsUMU() ) {
                    retval = "UMU";
                } else{
                    retval = "Standard";
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
            datum = "Combat Vehicle";
            if ( u.IsOmni() ) { datum = "OmniVehicle"; }
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

        String datum = "";
        for (int i=0; i < list.Size(); i++) {
            UnitListData data = (UnitListData) list.Get(i);
            FR.write( CSVFormat("Combat Vehicle") );
            datum = "Combat Vehicle";
            if ( data.isOmni() ) { datum = "OmniVehicle"; }
            FR.write( CSVFormat(datum) );
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
