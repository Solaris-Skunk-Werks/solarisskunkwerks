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

package saw.filehandlers;

import battleforce.*;
import components.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import common.CommonTools;
import filehandlers.FileCommon;
import java.util.HashMap;

public class HTMLWriter {

    private CombatVehicle CurVee;
    private HashMap lookup = new HashMap<String, String>( 90 );
    private String NL = "<br />",
                   tformat = "";

    public HTMLWriter( CombatVehicle v ) {
        CurVee = v;
        // if the current mech is an omnimech, set the loadout to the base
        // before we build the hash table
        if( CurVee.IsOmni() ) {
            CurVee.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
        }
        if( CurVee.UsingFractionalAccounting() ) {
            tformat = "$7.3f";
        } else {
            tformat = "$6.2f";
        }
        BuildHash();
    }

    public void WriteHTML( String template, String destfile ) throws IOException {
        BufferedWriter FW = new BufferedWriter( new FileWriter( destfile ) );
        BufferedReader FR = new BufferedReader( new FileReader( template ) );
        boolean EOF = false;
        String read = "";
        String write = "";
        ArrayList equip = new ArrayList();
        ArrayList omni = new ArrayList();
        ArrayList armor = new ArrayList();

        // we'll basically go through line by line and do our replacement,
        // writing as we go along.
        while( EOF == false ) {
            // for each line in the template file, check for a tag and replace it
            read = FR.readLine();
            if( read == null ) {
                // reached the end of the file
                EOF = true;
            } else {
                // first, check for special tags
                if( read.contains( "<+-SSW_START_EQUIPMENT_FLUFF_BLOCK-+>" ) ) {
                    // read until we get to the end of the equipment fluff block
                    equip.clear();
                    boolean end = false;
                    while( end == false ) {
                        read = FR.readLine();
                        // see if someone forgot to end the fluff line
                        if( read == null ) { throw new IOException( "Unexpected EOF: No End Equipment Fluff tag."); }
                        // check for the end, then add the line
                        if( read.contains( "<+-SSW_END_EQUIPMENT_FLUFF_BLOCK-+>" ) ) {
                            end = true;
                        } else {
                            equip.add( read );
                        }
                    }
                    abPlaceable[] a = GetEquips( true );
                    FW.write( BuildEquipLines( equip, a, true ) );
                    FW.newLine();
                } else if( read.contains( "<+-SSW_START_EQUIPMENT_STAT_BLOCK-+>" ) ) {
                    // read until we get to the end of the equipment block
                    equip.clear();
                    boolean end = false;
                    while( end == false ) {
                        read = FR.readLine();
                        // see if someone forgot to end the fluff line
                        if( read == null ) { throw new IOException( "Unexpected EOF: No End Equipment tag."); }
                        // check for the end, then add the line
                        if( read.contains( "<+-SSW_END_EQUIPMENT_STAT_BLOCK-+>" ) ) {
                            end = true;
                        } else {
                            equip.add( read );
                        }
                    }
                    abPlaceable[] a = GetEquips( false );
                    FW.write( BuildEquipLines( equip, a, false ) );
                    FW.newLine();
                } else if( read.contains( "<+-SSW_START_OMNIMECH_STAT_BLOCK-+>" ) ) {
                    // read until we get to the end of the omni block
                    equip.clear();
                    omni.clear();
                    boolean end = false;
                    while( end == false ) {
                        read = FR.readLine();
                        // see if someone forgot to end the fluff line
                        if( read == null ) { throw new IOException( "Unexpected EOF: No End Omnimech tag."); }
                        // check for the end, then add the line
                        if( read.contains( "<+-SSW_END_OMNIMECH_STAT_BLOCK-+>" ) ) {
                            end = true;
                        } else {
                            omni.add( read );
                        }
                    }
                    // are we actually exporting an omnimech?  If not, we'll
                    // discard the lines we just read.
                    if( CurVee.IsOmni() ) {
                        // yes, go ahead with the export
                        try {
                            FW.write( BuildOmniLines( omni ) );
                            FW.newLine();
                        } catch( IOException e ) {
                            throw e;
                        }
                    }
                } else if( read.contains( "<+-SSW_START_NORMAL_ARMOR_BLOCK-+>" ) ) {
                    // read until we get to the end of the armor block
                    armor.clear();
                    boolean end = false;
                    while( end == false ) {
                        read = FR.readLine();
                        // see if someone forgot to end the fluff line
                        if( read == null ) { throw new IOException( "Unexpected EOF: No End Normal Armor tag."); }
                        // check for the end, then add the line
                        if( read.contains( "<+-SSW_END_NORMAL_ARMOR_BLOCK-+>" ) ) {
                            end = true;
                        } else {
                            armor.add( read );
                        }
                    }
                    // are we actually exporting an omnimech?  If not, we'll
                    // discard the lines we just read.
                    if( ! CurVee.GetArmor().IsPatchwork() ) {
                        // yes, go ahead with the export
                        try {
                            for( int i = 0; i < armor.size(); i++ ) {
                                FW.write( ProcessLine( (String) armor.get( i ) ) );
                                FW.newLine();
                            }
                        } catch( IOException e ) {
                            throw e;
                        }
                    }
                } else if( read.contains( "<+-SSW_START_PATCHWORK_ARMOR_BLOCK-+>" ) ) {
                    // read until we get to the end of the armor block
                    armor.clear();
                    boolean end = false;
                    while( end == false ) {
                        read = FR.readLine();
                        // see if someone forgot to end the fluff line
                        if( read == null ) { throw new IOException( "Unexpected EOF: No End Patchwork Armor tag."); }
                        // check for the end, then add the line
                        if( read.contains( "<+-SSW_END_PATCHWORK_ARMOR_BLOCK-+>" ) ) {
                            end = true;
                        } else {
                            armor.add( read );
                        }
                    }
                    // are we actually exporting an omnimech?  If not, we'll
                    // discard the lines we just read.
                    if( CurVee.GetArmor().IsPatchwork() ) {
                        // yes, go ahead with the export
                        try {
                            for( int i = 0; i < armor.size(); i++ ) {
                                FW.write( ProcessLine( (String) armor.get( i ) ) );
                                FW.newLine();
                            }
                        } catch( IOException e ) {
                            throw e;
                        }
                    }
                } else if( read.contains( "<+-SSW_REMOVE_IF_OMNI_NO_FIXED-+>" ) ) {
                    // see if the mech is an omni and whether we need to remove
                    // this.
                    if( CurVee.IsOmni() ) {
                        if( CurVee.GetLoadout().GetNonCore().size() > 0 ) {
                            // process the line and rewrite it
                            write = ProcessLine( read );
                            FW.write( write );
                            FW.newLine();
                        }
                    } else {
                        // process the line and rewrite it
                        write = ProcessLine( read );
                        FW.write( write );
                        FW.newLine();
                    }
                } else {
                    // process the line and rewrite it
                    write = ProcessLine( read );
                    FW.write( write );
                    FW.newLine();
                }
            }
        }

        // all done, close the files
        FW.close();
        FR.close();
    }

    private String ProcessLine( String read ) {
        // processes the template string and returns a completed line
        // ignores any line without a tag in it.
        String retval = "";
        String getval = "";
        boolean RemoveIfBlank = false;
        boolean DitchString = false;
        if( read.contains( "<+-SSW_REMOVE_IF_BLANK-+>" ) ) {
            // we'll be removing this line if the second tag is blank
            RemoveIfBlank = true;
        }

        if( read.contains( "<+-SSW_" ) ) {
            // we have a tag, or maybe more.  replace it with the correct information
            String[] s = read.split( "<\\+-SSW_" );
            // how many tags do we have?
            if( s.length > 2 ) {
                // more than one.
                retval += s[0];
                for( int i = 1; i < s.length; i++ ) {
                    // now find the rest of the tag
                    String[] s1 = s[i].split( "-\\+>" );
                    // build the tag again, check for blank, and find the value
                    String test = "<+-SSW_" + s1[0] + "-+>";
                    if( ! test.equals( "<+-SSW_REMOVE_IF_BLANK-+>" ) ) {
                        // add it to the line
                        if( RemoveIfBlank ) {
                            getval = (String) lookup.get( test );
                            if( getval != null ) {
                                if( getval.equals( "" ) ) {
                                    // remove the whole line
                                    DitchString = true;
                                } else {
                                    retval += getval;
                                }
                            }
                        } else {
                            retval += lookup.get( test );
                        }
                    }
                    // now put the string back together
                    for( int j = 1; j < s1.length; j++ ) {
                        retval += s1[j];
                    }
                }
            } else {
                // only one.  the first part of the string is added to retval
                retval += s[0];
                String[] s1 = s[1].split( "-\\+>" );
                // build the tag again and find the value
                retval += lookup.get( "<+-SSW_" + s1[0] + "-+>" );
                // now put the string back together
                for( int i = 1; i < s1.length; i++ ) {
                    retval += s1[i];
                }
            }
        } else {
            // nothing in there, return the read string
            retval = read;
        }
        if( DitchString ) {
            return "";
        } else {
            return retval;
        }
    }

    private String BuildEquipLines( ArrayList lines, abPlaceable[] equips, boolean fluff ) {
        // this routine reads the mech's equipment and provides a completed
        // equipment block.  lines should contain the equipment line.
        String retval = "";

        if( equips.length <=0 ) { return retval; }

        if( fluff ) {
            // building for a fluff block.
            // initialize the next loop
            int numthistype = 1;
            abPlaceable cur = equips[0];
            equips[0] = null;

            // do we need to continue?
            if( equips.length <= 1 ) {
                retval += ProcessEquipFluffLines( cur, numthistype, lines );
                return retval;
            }

            // count up individual weapons and build their string
            for( int i = 1; i <= equips.length; i++ ) {
                // find any other weapons of this type
                for( int j = 0; j < equips.length; j++ ) {
                    if( equips[j] != null ) {
                        if( equips[j] instanceof Equipment ) {
                            if( ((Equipment) equips[j]).IsVariableSize() ) {
                                if( equips[j].CritName().equals( cur.CritName() ) && equips[j].GetManufacturer().equals( cur.GetManufacturer() ) ) {
                                    numthistype++;
                                    equips[j] = null;
                                }
                            } else {
                                if( FileCommon.LookupStripArc( equips[j].LookupName() ).equals( FileCommon.LookupStripArc( cur.LookupName() ) ) && equips[j].GetManufacturer().equals( cur.GetManufacturer() ) ) {
                                    numthistype++;
                                    equips[j] = null;
                                }
                            }
                        } else {
                            if( FileCommon.LookupStripArc( equips[j].LookupName() ).equals( FileCommon.LookupStripArc( cur.LookupName() ) ) && equips[j].GetManufacturer().equals( cur.GetManufacturer() ) ) {
                                numthistype++;
                                equips[j] = null;
                            }
                        }
                    }
                }

                // add the current weapon to the armament string
                retval += ProcessEquipFluffLines( cur, numthistype, lines );

                // find the next weapon type and set it to current
                cur = null;
                numthistype = 0;
                for( int j = 0; j < equips.length; j++ ) {
                    if( equips[j] != null ) {
                        cur = equips[j];
                        equips[j] = null;
                        numthistype = 1;
                        break;
                    }
                }

                // do we need to continue?
                if( cur == null ) { break; }
            }
        } else {
            // we'll want to consolidate equipment within locations.
            int numthisloc = 1;
            abPlaceable cur = equips[0];
            equips[0] = null;

            if( equips.length <= 1 ) {
                retval += ProcessEquipStatLines( cur, lines, 1 );
                return retval;
            }

            // count up individual weapons and build their string
            for( int i = 1; i <= equips.length; i++ ) {
                // find any other weapons of this type
                if( cur instanceof MultiSlotSystem || cur instanceof MechanicalJumpBooster || cur.CanSplit() |! cur.Contiguous() ) {
                    // splittable items are generally too big for two in one
                    // location or are split into different areas.  just avoid.
                    retval += ProcessEquipStatLines( cur, lines, 1 );
                } else {
                    int loc = CurVee.GetLoadout().Find( cur );
                    for( int j = 0; j < equips.length; j++ ) {
                        if( equips[j] != null ) {
                            if( equips[j] instanceof Equipment ) {
                                if( ((Equipment) equips[j]).IsVariableSize() ) {
                                    if( equips[j].CritName().equals( cur.CritName() ) && CurVee.GetLoadout().Find( equips[j] ) == loc ) {
                                        numthisloc++;
                                        equips[j] = null;
                                    }
                                } else {
                                    if( equips[j].LookupName().equals( cur.LookupName() ) && CurVee.GetLoadout().Find( equips[j] ) == loc ) {
                                        numthisloc++;
                                        equips[j] = null;
                                    }
                                }
                            } else {
                                if( equips[j].LookupName().equals( cur.LookupName() ) && CurVee.GetLoadout().Find( equips[j] ) == loc ) {
                                    numthisloc++;
                                    equips[j] = null;
                                }
                            }
                        }
                    }

                    // add the current weapon to the armament string
                    retval += ProcessEquipStatLines( cur, lines, numthisloc );
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
        }

        return retval;
    }

    private String BuildOmniLines( ArrayList lines ) throws IOException {
        // this routine will build each omnimech loadout and add it to retval.
        String retval = "";
        ArrayList loadouts = CurVee.GetLoadouts();
        ArrayList EQLines = new ArrayList();

        for( int i = 0; i < loadouts.size(); i++ ) {
            // set the mech to the current loadout
            CurVee.SetCurLoadout( ((ifCVLoadout) loadouts.get(i)).GetName() );
            EQLines.clear();

            // now read each line in turn and fill in the blanks.
            for( int j = 0; j < lines.size(); j++ ) {
                String test = (String) lines.get( j );
                // first, check and see if we have an equipment block
                if( test.contains( "<+-SSW_START_EQUIPMENT_STAT_BLOCK-+>" ) ) {
                    // read until we get to the end of the equipment block
                    boolean end = false;
                    int add = j + 1;
                    String read = "";
                    while( end == false ) {
                        read = (String) lines.get( add );
                        // see if someone forgot to end the fluff line
                        if( read == null ) { throw new IOException( "Unexpected EOF: No End Equipment tag in Omnimech block."); }
                        // check for the end, then add the line
                        if( read.contains( "<+-SSW_END_EQUIPMENT_STAT_BLOCK-+>" ) ) {
                            end = true;
                        } else {
                            EQLines.add( read );
                            add++;
                        }
                    }
                    abPlaceable[] a = GetEquips( false );
                    retval += BuildEquipLines( EQLines, a, false );
                    j = add;
                } else {
                    retval += ProcessOmniLine( test );
                }
            }
        }

        // now that we're done with omni stuff, set the mech back to its base
        CurVee.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
        return retval;
    }

    private String ProcessOmniLine( String read ) {
        // processes the template string and returns a completed line
        // ignores any line without a tag in it.
        String retval = "";
        String getval = "";
        boolean RemoveIfBlank = false;
        boolean DitchString = false;
        if( read.contains( "<+-SSW_REMOVE_IF_BLANK-+>" ) ) {
            // we'll be removing this line if the second tag is blank
            RemoveIfBlank = true;
        }

        if( read.contains( "<+-SSW_" ) ) {
            // we have a tag, or maybe more.  replace it with the correct information
            String[] s = read.split( "<\\+-SSW_" );
            // how many tags do we have?
            if( s.length > 2 ) {
                // more than one.
                retval += s[0];
                for( int i = 1; i < s.length; i++ ) {
                    // now find the rest of the tag
                    String[] s1 = s[i].split( "-\\+>" );
                    // build the tag again, check for blank, and find the value
                    String test = "<+-SSW_" + s1[0] + "-+>";
                    if( ! test.equals( "<+-SSW_REMOVE_IF_BLANK-+>" ) ) {
                        // add it to the line
                        if( RemoveIfBlank ) {
                            getval = (String) GetOmniValue( test );
                            if( getval != null ) {
                                if( getval.equals( "" ) ) {
                                    // remove the whole line
                                    DitchString = true;
                                } else {
                                    retval += getval;
                                }
                            }
                        } else {
                            retval += GetOmniValue( test );
                        }
                    }
                    // now put the string back together
                    for( int j = 1; j < s1.length; j++ ) {
                        retval += s1[j];
                    }
                }
            } else {
                // only one.  the first part of the string is added to retval
                retval += s[0];
                String[] s1 = s[1].split( "-\\+>" );
                // build the tag again and find the value
                retval += GetOmniValue( "<+-SSW_" + s1[0] + "-+>" );
                // now put the string back together
                for( int i = 1; i < s1.length; i++ ) {
                    retval += s1[i];
                }
            }
        } else {
            // nothing in there, return the read string
            retval = read;
        }
        if( DitchString ) {
            return "";
        } else {
            return retval + System.getProperty( "line.separator" );
        }
    }

    private String GetOmniValue( String tag ) {
        // provides a current value based on the tag provided
        BattleForceStats bfs = new BattleForceStats( CurVee );
        if( tag.equals( "<+-SSW_OMNI_LOADOUT_NAME-+>" ) ) {
            return CurVee.GetLoadout().GetName();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BV-+>" ) ) {
            return String.format( "%1$,d", CurVee.GetCurrentBV() );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_RULES_LEVEL-+>" ) ) {
            if( CurVee.GetBaseRulesLevel() == CurVee.GetLoadout().GetRulesLevel() ) {
                return "";
            } else {
                return CommonTools.GetRulesLevelString( CurVee.GetLoadout().GetRulesLevel() );
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_TECHBASE-+>" ) ) {
            if( CurVee.GetLoadout().GetTechBase() != CurVee.GetBaseTechbase() ) {
                return CommonTools.GetTechbaseString( CurVee.GetLoadout().GetTechBase() );
            } else {
                return "";
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_COST-+>" ) ) {
            return String.format( "%1$,.0f", CurVee.GetTotalCost() );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_ACTUATOR_LINE-+>" ) ) {
            return "";
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_SPACE-+>" ) ) {
            return "" + CurVee.GetHeatSinks().NumCrits();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_LOCATION_LINE-+>" ) ) {
            return "";
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_TONNAGE-+>" ) ) {
            return FormatTonnage( CurVee.GetHeatSinks().GetLoadoutTonnage(), 1 );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_COUNT-+>" ) ) {
            return "" + CurVee.GetHeatSinks().GetNumHS();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_DISSIPATION-+>" ) ) {
            return "" + CurVee.GetHeatSinks().TotalDissipation();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_DISSIPATION_LINE-+>" ) ) {
            return GetHeatSinkLine();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_JUMPJET_SPACE-+>" ) ) {
            return "" + CurVee.GetJumpJets().ReportCrits();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_SPEED_JUMP_MP-+>" ) ) {
            if( CurVee.GetJumpJets().GetNumJJ() == 0 ) {
                return "";
            } else {
                return "" + CurVee.GetJumpJets().GetNumJJ();
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_JUMPJET_LOCATION_LINE-+>" ) ) {
            return FileCommon.GetJumpJetLocations( CurVee );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_JUMPJET_TONNAGE-+>" ) ) {
            return FormatTonnage( CurVee.GetJumpJets().GetTonnage(), 1 );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_CASE_TONNAGE-+>" ) ) {
           return "";
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_CASE_LOCATION_LINE-+>" ) ) {
            return "";
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_CASEII_TONNAGE-+>" ) ) {
            return "";
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_CASEII_LOCATION_LINE-+>" ) ) {
            return "";
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_AVAILABILITY-+>" ) ) {
            return CurVee.GetAvailability().GetBestCombinedCode();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_JUMPJET_TYPE-+>" ) ) {
            if( CurVee.GetJumpJets().IsImproved() ) {
                return "Improved";
            } else {
                return "Standard";
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_POWER_AMP_TONNAGE-+>" ) ) {
            if( CurVee.GetEngine().IsNuclear() ) {
                return "";
            } else {
                if( CurVee.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                    return FormatTonnage( CurVee.GetLoadout().GetPowerAmplifier().GetTonnage(), 1 );
                } else {
                    return "";
                }
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_STRING-+>" ) ) {
            int [] BFdmg = CurVee.GetBFDamage( bfs );
            return BFdmg[BFConstants.BF_SHORT] + "/" + BFdmg[BFConstants.BF_MEDIUM] + "/" + BFdmg[BFConstants.BF_LONG] + "/" + BFdmg[BFConstants.BF_EXTREME];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_SHORT-+>" ) ) {
            return "" + CurVee.GetBFDamage( bfs )[BFConstants.BF_SHORT];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_MEDIUM-+>" ) ) {
            return "" + CurVee.GetBFDamage( bfs )[BFConstants.BF_MEDIUM];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_LONG-+>" ) ) {
            return "" + CurVee.GetBFDamage( bfs )[BFConstants.BF_LONG];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_EXTREME-+>" ) ) {
            return "" + CurVee.GetBFDamage( bfs )[BFConstants.BF_EXTREME];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_OVERHEAT-+>" ) ) {
            return "" + CurVee.GetBFDamage( bfs )[BFConstants.BF_OV];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_ARMOR-+>" ) ) {
            return "" + CurVee.GetBFArmor();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_STRUCTURE-+>" ) ) {
            return "" + CurVee.GetBFStructure();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_POINTS-+>" ) ) {
            return "" + CurVee.GetBFPoints();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_SIZE-+>" ) ) {
            return "" + CurVee.GetBFSize();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_MOVEMENT-+>" ) ) {
            return BattleForceTools.GetMovementString( CurVee );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_SPECIALS-+>" ) ) {
            return "" + bfs.getAbilitiesString();
        } else {
            return "";
        }
    }

    private String ProcessEquipFluffLines( abPlaceable a, int num, ArrayList lines ) {
        String retval = "";
        String test = "";
        String plural = "";
        if( num > 1 ) {
            plural = "s";
        }

        for( int i = 0; i < lines.size(); i++ ) {
            test = (String) lines.get(i);
            if( test.contains( "<+-SSW_" ) ) {
                // we have a tag, or maybe more.  replace it with the correct information
                String[] s = test.split( "<\\+-SSW_" );
                // how many tags do we have?
                if( s.length > 2 ) {
                    // more than one.
                    retval += s[0];
                    for( int j = 1; j < s.length; j++ ) {
                        // now find the rest of the tag
                        String[] s1 = s[j].split( "-\\+>" );
                        // build the tag again and find the value
                        String check = "<+-SSW_" + s1[0] + "-+>";

                        if( check.equals( "<+-SSW_EQUIP_COUNT_THIS_TYPE-+>" ) ) {
                            retval += "" + num;
                        } else if( check.equals( "<+-SSW_EQUIP_NAME-+>" ) ) {
                            if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                                if( a instanceof Equipment ) {
                                    if( ((Equipment) a).IsVariableSize() ) {
                                        retval += FileCommon.LookupStripArc( a.CritName() );
                                    } else {
                                        retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                                    }
                                } else {
                                    retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                                }
                            } else {
                                retval += FileCommon.LookupStripArc( a.CritName() ) + plural;
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_FULL_NAME-+>" ) ) {
                            if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                                if( a instanceof RangedWeapon ) {
                                    if( ((RangedWeapon) a).IsUsingFCS() ) {
                                        retval += FileCommon.LookupStripArc( a.LookupName() ) + plural + " w/ " + ((abPlaceable) ((RangedWeapon) a).GetFCS()).LookupName();
                                    } else {
                                        retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                                    }
                                } else if( a instanceof Equipment ) {
                                    if( ((Equipment) a).IsVariableSize() ) {
                                        retval += FileCommon.LookupStripArc( a.CritName() );
                                    } else {
                                        retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                                    }
                                } else {
                                        retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                                }
                            } else {
                                if( a instanceof RangedWeapon ) {
                                    if( ((RangedWeapon) a).IsUsingFCS() ) {
                                        retval += FileCommon.LookupStripArc( a.CritName() ) + plural + " w/ " + ((abPlaceable) ((RangedWeapon) a).GetFCS()).CritName();
                                    } else {
                                        retval += FileCommon.LookupStripArc( a.CritName() ) + plural;
                                    }
                                } else {
                                    retval += FileCommon.LookupStripArc( a.CritName() ) + plural;
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_MANUFACTURER-+>" ) ) {
                            retval += a.GetManufacturer();
                        }

                        // now put the string back together
                        for( int k = 1; k < s1.length; k++ ) {
                            retval += s1[k];
                        }
                    }
                } else {
                    // only one.  the first part of the string is added to retval
                    retval += s[0];
                    String[] s1 = s[1].split( "-\\+>" );
                    // build the tag again and find the value
                    String check = "<+-SSW_" + s1[0] + "-+>";

                    if( check.equals( "<+-SSW_EQUIP_COUNT_THIS_TYPE-+>" ) ) {
                        if( num > 1 ) {
                            retval += num + " ";
                        }
                    } else if( check.equals( "<+-SSW_EQUIP_NAME-+>" ) ) {
                        if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                            if( a instanceof Equipment ) {
                                if( ((Equipment) a).IsVariableSize() ) {
                                    retval += FileCommon.LookupStripArc( a.CritName() );
                                } else {
                                    retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                                }
                            } else {
                                retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                            }
                        } else {
                            retval += FileCommon.LookupStripArc( a.CritName() ) + plural;
                        }
                    } else if( check.equals( "<+-SSW_EQUIP_FULL_NAME-+>" ) ) {
                        if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                            if( a instanceof RangedWeapon ) {
                                if( ((RangedWeapon) a).IsUsingFCS() ) {
                                    retval += FileCommon.LookupStripArc( a.LookupName() ) + plural + " w/ " + ((abPlaceable) ((RangedWeapon) a).GetFCS()).LookupName();
                                } else {
                                    retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                                }
                            } else if( a instanceof Equipment ) {
                                if( ((Equipment) a).IsVariableSize() ) {
                                    retval += FileCommon.LookupStripArc( a.CritName() );
                                } else {
                                    retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                                }
                            } else {
                                retval += FileCommon.LookupStripArc( a.LookupName() ) + plural;
                            }
                        } else {
                            if( a instanceof RangedWeapon ) {
                                if( ((RangedWeapon) a).IsUsingFCS() ) {
                                    retval += FileCommon.LookupStripArc( a.CritName() ) + plural + " w/ " + ((abPlaceable) ((RangedWeapon) a).GetFCS()).CritName();
                                } else {
                                    retval += FileCommon.LookupStripArc( a.CritName() ) + plural;
                                }
                            } else {
                                retval += FileCommon.LookupStripArc( a.CritName() ) + plural;
                            }
                        }
                    } else if( check.equals( "<+-SSW_EQUIP_MANUFACTURER-+>" ) ) {
                        retval += a.GetManufacturer();
                    }

                    // now put the string back together
                    for( int j = 1; j < s1.length; j++ ) {
                        retval += s1[j];
                    }
                }
                retval += System.getProperty( "line.separator" );
            } else {
                // nothing in there, return the read string
                retval += test + System.getProperty( "line.separator" );
            }
        }

        return retval;
    }

    private String ProcessEquipStatLines( abPlaceable a, ArrayList lines, int numthisloc ) {
        String retval = "";
        String test = "";

        for( int i = 0; i < lines.size(); i++ ) {
            test = (String) lines.get(i);
            if( test.contains( "<+-SSW_" ) ) {
                // we have a tag, or maybe more.  replace it with the correct information
                String[] s = test.split( "<\\+-SSW_" );
                // how many tags do we have?
                if( s.length > 2 ) {
                    // more than one.
                    retval += s[0];
                    for( int j = 1; j < s.length; j++ ) {
                        // now find the rest of the tag
                        String[] s1 = s[j].split( "-\\+>" );
                        // build the tag again and find the value
                        String check = "<+-SSW_" + s1[0] + "-+>";

                        if( check.equals( "<+-SSW_EQUIP_NAME-+>" ) ) {
                            String plural = "";
                            if( numthisloc > 1 ) {
                                plural = "s";
                            }
                            if( a instanceof Ammunition ) {
                                retval += FileCommon.FormatAmmoExportName( ((Ammunition) a), numthisloc );
                            } else {
                                retval += FileCommon.GetExportName( CurVee, a );
                                retval += plural;
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_COUNT_THIS_LOC-+>" ) ) {
                            if( ! ( a instanceof Ammunition ) ) {
                                if( numthisloc > 1 ) {
                                    retval += numthisloc + " ";
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_MANUFACTURER-+>" ) ) {
                            retval += a.GetManufacturer();
                        } else if( check.equals( "<+-SSW_EQUIP_TONNAGE-+>" ) ) {
                            if( a instanceof RangedWeapon ) {
                                if( ((RangedWeapon) a).IsUsingFCS() ) {
                                    double tons = a.GetTonnage() -  ((abPlaceable) ((RangedWeapon) a).GetFCS()).GetTonnage();
                                    retval += FormatTonnage( tons, numthisloc );
                                } else if( ((RangedWeapon) a).IsInArray() ) {
                                    MGArray m = ((RangedWeapon) a).GetMyArray();
                                    retval += FormatTonnage( m.GetMGTons(), numthisloc );
                                } else if( ((RangedWeapon) a).IsUsingCapacitor() ) {
                                    retval += FormatTonnage( ( a.GetTonnage() - 1.0f ), numthisloc );
                                } else {
                                    retval += FormatTonnage( a.GetTonnage(), numthisloc );
                                }
                            } else if( a instanceof ifMissileGuidance ) {
                                retval += FormatTonnage( a.GetTonnage(), numthisloc );
                            } else if( a instanceof MGArray ) {
                                retval += FormatTonnage( ((MGArray) a).GetBaseTons(), numthisloc );
                            } else {
                                retval += FormatTonnage( a.GetTonnage(), numthisloc );
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_CRITS-+>" ) ) {
                            if( a.CanSplit() ) {
                                retval += FileCommon.DecodeCrits( CurVee.GetLoadout().FindInstances( a ) );
                            } else {
                                if( a instanceof MGArray ) {
                                    if( numthisloc > 1 ) {
                                        retval += numthisloc;
                                    } else {
                                        retval += "1";
                                    }
                                } else if( a instanceof MultiSlotSystem ) {
                                    retval += ((MultiSlotSystem) a).ReportCrits();
                                } else if( a instanceof MechanicalJumpBooster ) {
                                    if( false ) {
                                        retval += 8;
                                    } else {
                                        retval += 4;
                                    }
                                } else {
                                    if( numthisloc > 1 ) {
                                        retval += a.NumCrits() * numthisloc;
                                    } else {
                                        retval += a.NumCrits();
                                    }
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_LOCATION-+>" ) ) {
                            if( a.CanSplit() ) {
                                retval += FileCommon.EncodeLocations( CurVee.GetLoadout().FindInstances( a ), false, common.CommonTools.Vehicle );
                            } else {
                                if( a instanceof MultiSlotSystem || a instanceof MechanicalJumpBooster ) {
                                    retval += "*";
                                } else {
                                    retval += FileCommon.EncodeLocation( CurVee.GetLoadout().Find( a ), false, common.CommonTools.Vehicle );
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_HEAT-+>" ) ) {
                            if( a instanceof ifWeapon ) {
                                if( ((ifWeapon) a).IsUltra() ) {
                                    retval += ((ifWeapon) a).GetHeat() + " /shot (" + ( ((ifWeapon) a).GetBVHeat() * numthisloc ) + " max)";
                                } else if( ((ifWeapon) a).IsRotary() ) {
                                    retval += ((ifWeapon) a).GetHeat() + " /shot (" + ( ((ifWeapon) a).GetBVHeat() * numthisloc ) + " max)";
                                } else {
                                    retval += "" + ((ifWeapon) a).GetHeat() * numthisloc;
                                }
                            } else if( a instanceof Equipment ) {
                                retval += "" + ((Equipment) a).GetHeat() * numthisloc;
                            } else if( a.GetMechModifier() != null ) {
                                retval += a.GetMechModifier().HeatAdder();
                            } else {
                                retval += "--";
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_DAMAGE-+>" ) ) {
                            if( a instanceof ifWeapon ) {
                                ifWeapon w = (ifWeapon) a;
                                if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                                    if( w.IsCluster() ) {
                                        retval += w.GetDamageShort() + "/msl";
                                    } else {
                                        retval += w.GetDamageShort() + "";
                                    }
                                } else if( w.GetDamageShort() == w.GetDamageMedium() && w.GetDamageShort() == w.GetDamageLong() ) {
                                    if( w.IsUltra() || w.IsRotary() ) {
                                        retval += w.GetDamageShort() + "/shot";
                                    } else {
                                        retval += w.GetDamageShort();
                                    }
                                } else {
                                    retval += w.GetDamageShort() + "/" + w.GetDamageMedium() + "/" + w.GetDamageLong();
                                }
                            } else {
                                retval += "--";
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_RANGE-+>" ) ) {
                            if( a instanceof ifWeapon ) {
                                ifWeapon w = (ifWeapon) a;
                                if( w.GetRangeLong() < 1 ) {
                                    if( w.GetRangeMedium() < 1 ) {
                                        retval += w.GetRangeShort();
                                    } else {
                                        retval += w.GetRangeMin() + "/" + w.GetRangeShort() + "/" + w.GetRangeMedium() + "/-";
                                    }
                                } else {
                                    retval += w.GetRangeMin() + "/" + w.GetRangeShort() + "/" + w.GetRangeMedium() + "/" + w.GetRangeLong();
                                }
                            } else if( a instanceof Equipment ) {
                                Equipment e = (Equipment) a;
                                if( e.GetShortRange() <= 0 && e.GetMediumRange() <= 0 ) {
                                    if( e.GetLongRange() > 0 ) {
                                        retval += e.GetLongRange();
                                    } else {
                                        retval += "--";
                                    }
                                } else {
                                    retval += "0/" + e.GetShortRange() + "/" + e.GetMediumRange() + "/" + e.GetLongRange();
                                }
                            } else {
                                retval += "--";
                            }
                        }

                        // now put the string back together
                        for( int k = 1; k < s1.length; k++ ) {
                            retval += s1[k];
                        }
                    }
                } else {
                    // only one.  the first part of the string is added to retval
                    retval += s[0];
                    String[] s1 = s[1].split( "-\\+>" );
                    // build the tag again and find the value
                    String check = "<+-SSW_" + s1[0] + "-+>";

                        if( check.equals( "<+-SSW_EQUIP_NAME-+>" ) ) {
                            if( a instanceof Ammunition ) {
                                retval += FileCommon.FormatAmmoExportName( ((Ammunition) a), numthisloc );
                            } else {
                                String plural = "";
                                if( numthisloc > 1 ) {
                                    plural = "s";
                                }
                                retval += FileCommon.GetExportName( CurVee, a );
                                retval += plural;
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_COUNT_THIS_LOC-+>" ) ) {
                            if( ! ( a instanceof Ammunition ) ) {
                                if( numthisloc > 1 ) {
                                    retval += numthisloc + " ";
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_MANUFACTURER-+>" ) ) {
                            retval += a.GetManufacturer();
                        } else if( check.equals( "<+-SSW_EQUIP_TONNAGE-+>" ) ) {
                            if( a instanceof RangedWeapon ) {
                                if( ((RangedWeapon) a).IsUsingFCS() ) {
                                    double tons = a.GetTonnage() -  ((abPlaceable) ((RangedWeapon) a).GetFCS()).GetTonnage();
                                    retval += FormatTonnage( tons, numthisloc );
                                } else if( ((RangedWeapon) a).IsInArray() ) {
                                    MGArray m = ((RangedWeapon) a).GetMyArray();
                                    retval += FormatTonnage( m.GetMGTons(), numthisloc );
                                } else if( ((RangedWeapon) a).IsUsingCapacitor() ) {
                                    retval += FormatTonnage( ( a.GetTonnage() - 1.0f ), numthisloc );
                                } else {
                                    retval += FormatTonnage( a.GetTonnage(), numthisloc );
                                }
                            } else if( a instanceof ifMissileGuidance ) {
                                retval += FormatTonnage( a.GetTonnage(), numthisloc );
                            } else if( a instanceof MGArray ) {
                                retval += FormatTonnage( ((MGArray) a).GetBaseTons(), numthisloc );
                            } else {
                                retval += FormatTonnage( a.GetTonnage(), numthisloc );
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_CRITS-+>" ) ) {
                            if( a.CanSplit() ) {
                                retval += FileCommon.DecodeCrits( CurVee.GetLoadout().FindInstances( a ) );
                            } else {
                                if( a instanceof MGArray ) {
                                    if( numthisloc > 1 ) {
                                        retval += numthisloc;
                                    } else {
                                        retval += "1";
                                    }
                                } else if( a instanceof MultiSlotSystem ) {
                                    retval += ((MultiSlotSystem) a).ReportCrits();
                                } else if( a instanceof MechanicalJumpBooster ) {
                                    if( false ) {
                                        retval += 8;
                                    } else {
                                        retval += 4;
                                    }
                                } else {
                                    if( numthisloc > 1 ) {
                                        retval += a.NumCrits() * numthisloc;
                                    } else {
                                        retval += a.NumCrits();
                                    }
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_LOCATION-+>" ) ) {
                            if( a.CanSplit() ) {
                                retval += FileCommon.EncodeLocations( CurVee.GetLoadout().FindInstances( a ), false, common.CommonTools.Vehicle );
                            } else {
                                if( a instanceof MultiSlotSystem || a instanceof MechanicalJumpBooster ) {
                                    retval += "*";
                                } else {
                                    retval += FileCommon.EncodeLocation( CurVee.GetLoadout().Find( a ), false, common.CommonTools.Vehicle );
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_HEAT-+>" ) ) {
                            if( a instanceof ifWeapon ) {
                                if( ((ifWeapon) a).IsUltra() ) {
                                    retval += ((ifWeapon) a).GetHeat() + " /shot (" + ( ((ifWeapon) a).GetBVHeat() * numthisloc ) + " max)";
                                } else if( ((ifWeapon) a).IsRotary() ) {
                                    retval += ((ifWeapon) a).GetHeat() + " /shot (" + ( ((ifWeapon) a).GetBVHeat() * numthisloc ) + " max)";
                                } else {
                                    retval += "" + ((ifWeapon) a).GetHeat() * numthisloc;
                                }
                            } else if( a instanceof Equipment ) {
                                retval += "" + ((Equipment) a).GetHeat() * numthisloc;
                            } else if( a.GetMechModifier() != null ) {
                                retval += a.GetMechModifier().HeatAdder();
                            } else {
                                retval += "--";
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_DAMAGE-+>" ) ) {
                            if( a instanceof ifWeapon ) {
                                ifWeapon w = (ifWeapon) a;
                                if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                                    if( w.IsCluster() ) {
                                        retval += w.GetDamageShort() + "/msl";
                                    } else {
                                        retval += w.GetDamageShort() + "";
                                    }
                                } else if( w.GetDamageShort() == w.GetDamageMedium() && w.GetDamageShort() == w.GetDamageLong() ) {
                                    if( w.IsUltra() || w.IsRotary() ) {
                                        retval += w.GetDamageShort() + "/shot";
                                    } else {
                                        retval += w.GetDamageShort();
                                    }
                                } else {
                                    retval += w.GetDamageShort() + "/" + w.GetDamageMedium() + "/" + w.GetDamageLong();
                                }
                            } else {
                                retval += "--";
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_RANGE-+>" ) ) {
                            if( a instanceof ifWeapon ) {
                                ifWeapon w = (ifWeapon) a;
                                if( w.GetRangeLong() < 1 ) {
                                    if( w.GetRangeMedium() < 1 ) {
                                        retval += w.GetRangeShort();
                                    } else {
                                        retval += w.GetRangeMin() + "/" + w.GetRangeShort() + "/" + w.GetRangeMedium() + "/-";
                                    }
                                } else {
                                    retval += w.GetRangeMin() + "/" + w.GetRangeShort() + "/" + w.GetRangeMedium() + "/" + w.GetRangeLong();
                                }
                            } else if( a instanceof Equipment ) {
                                Equipment e = (Equipment) a;
                                if( e.GetShortRange() <= 0 && e.GetMediumRange() <= 0 ) {
                                    if( e.GetLongRange() > 0 ) {
                                        retval += e.GetLongRange();
                                    } else {
                                        retval += "--";
                                    }
                                } else {
                                    retval += "0/" + e.GetShortRange() + "/" + e.GetMediumRange() + "/" + e.GetLongRange();
                                }
                            } else {
                                retval += "--";
                            }
                        }

                    // now put the string back together
                    for( int j = 1; j < s1.length; j++ ) {
                        retval += s1[j];
                    }
                }
                retval += System.getProperty( "line.separator" );
            } else {
                // nothing in there, return the read string
                retval += test + System.getProperty( "line.separator" );
            }
        }

        return retval;
    }

    private abPlaceable[] GetEquips( boolean fluff ) {
        // returns an array of placeables that can be used to build equipment blocks
        ArrayList v = CurVee.GetLoadout().GetNonCore();
        ArrayList ret = new ArrayList();
        if( fluff ) {
            ArrayList EQ = new ArrayList();

            // get the weapons for sort first.
            for( int i = 0; i < v.size(); i++ ) {
                if( v.get( i ) instanceof ifWeapon ) {
                    ret.add( v.get( i ) );
                } else {
                    EQ.add( v.get( i ) );
                }
            }

            // sort the weapons by BV
            Object[] o = CurVee.SortWeapons( ret, false );
            ret.clear();
            for( int i = 0; i < o.length; i++ ) {
                ret.add( o[i] );
            }

            // now add any extra equipment to the end of the list.
            for( int i = 0; i < EQ.size(); i++ ) {
                if( ! ( EQ.get( i ) instanceof Ammunition ) ) {
                    ret.add( EQ.get( i ) );
                }
            }
        } else {
            // return all equipment in the loadout
            ret = (ArrayList) v.clone();

            // add in certain items, such as the targeting computer and MASC
            if( CurVee.UsingTC() ) {
                ret.add( CurVee.GetTC() );
            }
            if( CurVee.GetLoadout().HasSupercharger() ) {
                ret.add( CurVee.GetLoadout().GetSupercharger() );
            }

            // sort the weapons by location
            ret = FileCommon.SortEquipmentForStats( CurVee, ret );

            // check for artemis and MG arrays
            for( int i = 0; i < ret.size(); i++ ) {
                if( ret.get( i ) instanceof RangedWeapon ) {
                    if( ((RangedWeapon) ret.get( i )).IsUsingFCS() ) {
                        ret.add( i + 1, ((RangedWeapon) ret.get( i )).GetFCS() );
                    }
                    if( ((RangedWeapon) ret.get( i )).IsUsingCapacitor() ) {
                        ret.add( i + 1, ((RangedWeapon) ret.get( i )).GetCapacitor() );
                    }
                } else if( ret.get( i ) instanceof MGArray ) {
                    ret.add( i + 1, ((MGArray) ret.get( i )).GetMGs()[0] );
                    ret.add( i + 2, ((MGArray) ret.get( i )).GetMGs()[1] );
                    if( ((MGArray) ret.get( i )).GetMGs()[2] != null ) {
                        ret.add( i + 3, ((MGArray) ret.get( i )).GetMGs()[2] );
                    }
                    if( ((MGArray) ret.get( i )).GetMGs()[3] != null ) {
                        ret.add( i + 4, ((MGArray) ret.get( i )).GetMGs()[3] );
                    }
                }
            }

            // now add in any multi slot systems.
            if( CurVee.HasBlueShield() ) {
                ret.add( CurVee.GetBlueShield() );
            }
        }

        // turn the return ArrayList into an array
        abPlaceable[] retval = new abPlaceable[ret.size()];
        for( int i = 0; i < ret.size(); i++ ) {
            retval[i] = (abPlaceable) ret.get( i );
        }
        return retval;
    }

    private void BuildHash() {
        // creates the hash table from values from the mech.
        lookup.put( "<+-SSW_NAME-+>", CurVee.GetName() );
        lookup.put( "<+-SSW_MODEL-+>", CurVee.GetModel() );
        if( CurVee.IsPrimitive() ) {
            lookup.put( "<+-SSW_TECHBASE-+>", CommonTools.GetTechbaseString( CurVee.GetBaseTechbase() ) + " (Primitive)" );
        } else {
            lookup.put( "<+-SSW_TECHBASE-+>", CommonTools.GetTechbaseString( CurVee.GetBaseTechbase() ) );
        }
        lookup.put( "<+-SSW_TONNAGE-+>", FormatTonnage( CurVee.GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_DRY_TONNAGE-+>", FormatTonnage( CurVee.GetCurrentDryTons(), 1 ) );
        lookup.put( "<+-SSW_CHASSIS_CONFIG-+>", CurVee.GetMotiveLookupName() );
        AvailableCode AC = CurVee.GetAvailability();
        lookup.put( "<+-SSW_AVAILABILITY-+>", AC.GetBestCombinedCode() );
        lookup.put( "<+-SSW_PROD_YEAR-+>", "" + CurVee.GetYear() );
        switch( AC.GetTechBase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                if( AC.WentExtinctIS() ) {
                    if( AC.WasReIntrodIS() ) {
                        if( AC.GetISIntroDate() >= AC.GetISReIntroDate() ) {
                            if( CurVee.YearWasSpecified() ) {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                                if( CurVee.GetYear() >= AC.GetISReIntroDate() ) {
                                    lookup.put( "<+-SSW_EXTINCT_BY-+>", "Never" );
                                } else {
                                    lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetISExtinctDate() );
                                }
                            } else {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetISIntroDate() );
                                lookup.put( "<+-SSW_EXTINCT_BY-+>", "Never" );
                            }
                        } else {
                            if( CurVee.YearWasSpecified() ) {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                                if( CurVee.GetYear() >= AC.GetISReIntroDate() ) {
                                    lookup.put( "<+-SSW_EXTINCT_BY-+>", "Never" );
                                } else {
                                    lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetISExtinctDate() );
                                }
                            } else {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetISIntroDate() );
                                lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetISExtinctDate() );
                            }
                        }
                    } else {
                        if( CurVee.YearWasSpecified() ) {
                            lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                        } else {
                            lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetISIntroDate() );
                        }
                        lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetISExtinctDate() );
                    }
                } else {
                    if( CurVee.YearWasSpecified() ) {
                        lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                    } else {
                        lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetISIntroDate() );
                    }
                    lookup.put( "<+-SSW_EXTINCT_BY-+>", "Never" );
                }
                break;
            case AvailableCode.TECH_CLAN:
                if( AC.WentExtinctCL() ) {
                    if( AC.WasReIntrodCL() ) {
                        if( AC.GetCLIntroDate() >= AC.GetCLReIntroDate() ) {
                            if( CurVee.YearWasSpecified() ) {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                            } else {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetCLIntroDate() );
                            }
                            lookup.put( "<+-SSW_EXTINCT_BY-+>", "Never" );
                        } else {
                            if( CurVee.YearWasSpecified() ) {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                            } else {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetCLIntroDate() );
                            }
                            lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetCLExtinctDate() );
                        }
                    } else {
                        if( CurVee.YearWasSpecified() ) {
                            lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                        } else {
                            lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetCLIntroDate() );
                        }
                        lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetCLExtinctDate() );
                    }
                } else {
                    if( CurVee.YearWasSpecified() ) {
                        lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                    } else {
                        lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetCLIntroDate() );
                    }
                    lookup.put( "<+-SSW_EXTINCT_BY-+>", "Never" );
                }
                break;
            case AvailableCode.TECH_BOTH:
                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                lookup.put( "<+-SSW_EXTINCT_BY-+>", "Unknown" );
                break;
        }
        lookup.put( "<+-SSW_OVERVIEW-+>", ProcessFluffString( CurVee.getOverview() ) );
        lookup.put( "<+-SSW_CAPABILITIES-+>", ProcessFluffString( CurVee.getCapabilities() ) );
        lookup.put( "<+-SSW_BATTLE_HISTORY-+>", ProcessFluffString( CurVee.getHistory() ) );
        lookup.put( "<+-SSW_DEPLOYMENT-+>", ProcessFluffString( CurVee.getDeployment() ) );
        lookup.put( "<+-SSW_VARIANTS-+>", ProcessFluffString( CurVee.getVariants() ) );
        lookup.put( "<+-SSW_NOTABLES-+>", ProcessFluffString( CurVee.getNotables() ) );
        lookup.put( "<+-SSW_ADDITIONAL-+>", ProcessFluffString( CurVee.GetAdditional() ) );
        lookup.put( "<+-SSW_MANUFACTURER-+>", CurVee.GetCompany() );
        lookup.put( "<+-SSW_MANUFACTURER_LOCATION-+>", CurVee.GetLocation() );
        lookup.put( "<+-SSW_MANUFACTURER_ENGINE-+>", CurVee.GetEngineManufacturer()+ " " + CurVee.GetEngine().GetRating() + " " + CurVee.GetEngine() );
        lookup.put( "<+-SSW_MANUFACTURER_CHASSIS-+>", CurVee.GetChassisModel() + " " + CurVee.GetIntStruc().CritName() );
        lookup.put( "<+-SSW_MANUFACTURER_ARMOR-+>", CurVee.GetArmorModel() + " " + CurVee.GetArmor().CritName() );
        lookup.put( "<+-SSW_MANUFACTURER_JUMPJETS-+>", CurVee.GetJJModel() );
        lookup.put( "<+-SSW_MANUFACTURER_COMM_SYSTEM-+>", GetCommSystem() );
        lookup.put( "<+-SSW_MANUFACTURER_T_AND_T_SYSTEM-+>", GetTandTSystem() );
        lookup.put( "<+-SSW_CHASSIS_TONNAGE-+>", FormatTonnage( CurVee.getInternalStructure(), 1 ) );
        lookup.put( "<+-SSW_ARMOR_TONNAGE-+>", FormatTonnage( CurVee.GetArmor().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_ENGINE_TONNAGE-+>", FormatTonnage( CurVee.GetEngine().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_HEATSINK_TONNAGE-+>", FormatTonnage( CurVee.GetHeatSinks().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_JUMPJET_TONNAGE-+>", FormatTonnage( CurVee.GetJumpJets().GetTonnage(), 1 ) );
        // need a routine for this...
        lookup.put( "<+-SSW_EQUIPMENT_TOTAL_TONNAGE-+>", "" );
        lookup.put( "<+-SSW_FRONT_ARMOR-+>", "" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_FRONT ) );
        lookup.put( "<+-SSW_LEFT_ARMOR-+>", "" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_LEFT ) );
        lookup.put( "<+-SSW_RIGHT_ARMOR-+>", "" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_RIGHT ) );
        lookup.put( "<+-SSW_REAR_ARMOR-+>", "" + CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_REAR ) );
        lookup.put( "<+-SSW_TURRET_ARMOR-+>", "" + (CurVee.isHasTurret1() ? CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET1 ) : "") );
        lookup.put( "<+-SSW_ROTOR_ARMOR-+>", "" + (CurVee.IsVTOL() ? CurVee.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_ROTOR ) : "") );
        lookup.put( "<+-SSW_FRONT_ARMOR_TYPE-+>", " (" + CurVee.GetArmor().GetFrontArmorType().LookupName() + ")" );
        lookup.put( "<+-SSW_LEFT_ARMOR_TYPE-+>", " (" + CurVee.GetArmor().GetLeftArmorType().LookupName() + ")" );
        lookup.put( "<+-SSW_RIGHT_ARMOR_TYPE-+>", " (" + CurVee.GetArmor().GetRightArmorType().LookupName() + ")" );
        lookup.put( "<+-SSW_TURRET_ARMOR_TYPE-+>", (CurVee.isHasTurret1() ? " (" + CurVee.GetArmor().GetTurret1ArmorType().LookupName() + ")" : "") );
        lookup.put( "<+-SSW_ROTOR_ARMOR_TYPE-+>", ( CurVee.IsVTOL() ? " (" + CurVee.GetArmor().GetRotorArmorType().LookupName() + ")" : "" ) );
        lookup.put( "<+-SSW_REAR_ARMOR_TYPE-+>", " (" + CurVee.GetArmor().GetRearArmorType().LookupName() + ")" );
        lookup.put( "<+-SSW_ARMOR_COVERAGE-+>", "" + CurVee.GetArmor().GetCoverage() );
        lookup.put( "<+-SSW_JUPMJET_COUNT-+>", "" + CurVee.GetJumpJets().GetNumJJ() );
        lookup.put( "<+-SSW_JUMPJET_DISTANCE-+>", GetJumpJetDistanceLine() );
        lookup.put( "<+-SSW_SPEED_CRUISE_KMPH-+>", CommonTools.FormatSpeed( CurVee.getCruiseMP() * 10.8 ) + " km/h" );
        lookup.put( "<+-SSW_SPEED_FLANK_KMPH-+>", CommonTools.FormatSpeed( CurVee.getFlankMP() * 10.8 ) + " km/h" );
        lookup.put( "<+-SSW_SPEED_CRUISE_MP-+>", "" + CurVee.getCruiseMP() );
        lookup.put( "<+-SSW_SPEED_FLANK_MP-+>", "" + CurVee.getFlankMP() );
        lookup.put( "<+-SSW_SPEED_JUMP_MP-+>", GetJumpingMPLine() );
        lookup.put( "<+-SSW_COST-+>", String.format( "%1$,.0f", CurVee.GetTotalCost() ) );
        //lookup.put( "<+-SSW_DRY_COST-+>", String.format( "%1$,.0f", CurVee.GetDryCost() ) );
        lookup.put( "<+-SSW_BV2-+>", String.format( "%1$,d", CurVee.GetCurrentBV() ) );
        lookup.put( "<+-SSW_ENGINE_SPACE-+>", "" + CurVee.GetEngine().ReportCrits() );
        //lookup.put( "<+-SSW_CHASSIS_LOCATION_LINE-+>", FileCommon.GetInternalLocations( CurVee ) );
        lookup.put( "<+-SSW_ARMOR_SPACE-+>", "" + CurVee.GetArmor().NumCrits() );
        //lookup.put( "<+-SSW_ARMOR_LOCATION_LINE-+>", FileCommon.GetArmorLocations( CurVee ) );
        lookup.put( "<+-SSW_HEATSINK_TOTAL_SPACE-+>", "" + CurVee.GetHeatSinks().NumCrits() );
        //lookup.put( "<+-SSW_ENHANCEMENT_LOCATION_LINE-+>", FileCommon.GetTSMLocations( CurVee ) );
        lookup.put( "<+-SSW_JUMPJET_SPACE-+>", "" + CurVee.GetJumpJets().ReportCrits() );
        lookup.put( "<+-SSW_JUMPJET_LOCATION_LINE-+>", FileCommon.GetJumpJetLocations( CurVee ) );
        lookup.put( "<+-SSW_ARMOR_FACTOR-+>", "" + CurVee.GetArmor().GetArmorValue() );
        lookup.put( "<+-SSW_ENGINE_RATING-+>", "" + CurVee.GetEngine().GetRating() );
        lookup.put( "<+-SSW_ENGINE_TYPE-+>", FileCommon.GetExportName( CurVee, CurVee.GetEngine() ) );
        lookup.put( "<+-SSW_HEATSINK_DISSIPATION_LINE-+>", GetHeatSinkLine() );
        if( CurVee.GetHeatSinks().GetNumHS() < CurVee.GetEngine().InternalHeatSinks() ) {
            lookup.put( "<+-SSW_HEATSINKS_IN_ENGINE-+>", "" + CurVee.GetHeatSinks().GetNumHS() );
        } else {
            lookup.put( "<+-SSW_HEATSINKS_IN_ENGINE-+>", "" + CurVee.GetEngine().InternalHeatSinks() );
        }
        if( CurVee.GetTechBase() == AvailableCode.TECH_BOTH ) {
            lookup.put( "<+-SSW_HEATSINK_TYPE-+>", CurVee.GetHeatSinks().GetCurrentState().LookupName() );
        } else {
            if( CurVee.GetHeatSinks().IsDouble() ) {
                lookup.put( "<+-SSW_HEATSINK_TYPE-+>", "Double" );
            } else if( CurVee.GetHeatSinks().IsCompact() ) {
                lookup.put( "<+-SSW_HEATSINK_TYPE-+>", "Compact" );
            } else if( CurVee.GetHeatSinks().IsLaser() ) {
                lookup.put( "<+-SSW_HEATSINK_TYPE-+>", "Laser" );
            } else {
                lookup.put( "<+-SSW_HEATSINK_TYPE-+>", "Single" );
            }
        }
        lookup.put( "<+-SSW_ARMOR_TYPE-+>", CurVee.GetArmor().CritName() );
        lookup.put( "<+-SSW_JUMPJET_TYPE-+>", GetJumpJetTypeLine() );
        lookup.put( "<+-SSW_HEATSINK_COUNT-+>", "" + CurVee.GetHeatSinks().GetNumHS() );
        lookup.put( "<+-SSW_HEATSINK_DISSIPATION-+>", "" + CurVee.GetHeatSinks().TotalDissipation() );
        lookup.put( "<+-SSW_INTERNAL_TYPE-+>", CurVee.GetIntStruc().CritName() );
        lookup.put( "<+-SSW_CONTROLS_TONNAGE-+>", CurVee.GetControls() );
        lookup.put( "<+-SSW_LIFTEQUIPMENT_TONNAGE-+>", (CurVee.GetLiftEquipmentTonnage() == 0) ? "" : CurVee.GetLiftEquipmentTonnage() + "" );
        lookup.put( "<+-SSW_TURRET_TONNAGE-+>", (CurVee.GetLoadout().GetTurret().GetTonnage() == 0) ? "" : CurVee.GetLoadout().GetTurret().GetTonnage() + "" );
        lookup.put( "<+-SSW_RULES_LEVEL-+>", CommonTools.GetRulesLevelString( CurVee.GetRulesLevel() ) );
        if( CurVee.IsOmni() ) {
            lookup.put( "<+-SSW_POD_TONNAGE-+>", FormatTonnage( ( CurVee.GetTonnage() - CurVee.GetCurrentTons() ), 1 ) );
        } else {
            lookup.put( "<+-SSW_POD_TONNAGE-+>", "" );
        }
        if( CurVee.GetEngine().IsNuclear() ) {
            lookup.put( "<+-SSW_POWER_AMP_TONNAGE-+>", "" );
        } else {
            if( CurVee.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                lookup.put( "<+-SSW_POWER_AMP_TONNAGE-+>", FormatTonnage( CurVee.GetLoadout().GetPowerAmplifier().GetTonnage(), 1 ) );
            } else {
                lookup.put( "<+-SSW_POWER_AMP_TONNAGE-+>", "" );
            }
        }
        // added this in to avoid the "null" result.
        lookup.put( "<+-SSW_REMOVE_IF_OMNI_NO_FIXED-+>", "" );
        lookup.put( "<+-SSW_MULTISLOTNOTES-+>", BuildMultiSlotNotes() );
        lookup.put( "<+-SSW_EJECTIONSEAT_TONNAGE-+>", "" );
        BattleForceStats bfs = new BattleForceStats( CurVee );
        int [] BFdmg = CurVee.GetBFDamage( bfs );
        lookup.put( "<+-SSW_BF_DAMAGE_STRING-+>", BFdmg[BFConstants.BF_SHORT] + "/" + BFdmg[BFConstants.BF_MEDIUM] + "/" + BFdmg[BFConstants.BF_LONG] + "/" + BFdmg[BFConstants.BF_EXTREME] );
        lookup.put( "<+-SSW_BF_DAMAGE_SHORT-+>", "" + BFdmg[BFConstants.BF_SHORT] );
        lookup.put( "<+-SSW_BF_DAMAGE_MEDIUM-+>", "" + BFdmg[BFConstants.BF_MEDIUM] );
        lookup.put( "<+-SSW_BF_DAMAGE_LONG-+>", "" + BFdmg[BFConstants.BF_LONG] );
        lookup.put( "<+-SSW_BF_DAMAGE_EXTREME-+>", "" + BFdmg[BFConstants.BF_EXTREME] );
        lookup.put( "<+-SSW_BF_OVERHEAT-+>", "" + BFdmg[BFConstants.BF_OV] );
        lookup.put( "<+-SSW_BF_ARMOR-+>", "" + CurVee.GetBFArmor() );
        lookup.put( "<+-SSW_BF_STRUCTURE-+>", "" + CurVee.GetBFStructure() );
        lookup.put( "<+-SSW_BF_POINTS-+>", "" + CurVee.GetBFPoints() );
        lookup.put( "<+-SSW_BF_SIZE-+>", "" + CurVee.GetBFSize() );
        lookup.put( "<+-SSW_BF_MOVEMENT-+>", BattleForceTools.GetMovementString( CurVee ) );
        lookup.put( "<+-SSW_BF_SPECIALS-+>", bfs.getAbilitiesString() );
        if( CurVee.UsingFractionalAccounting() ) {
            lookup.put( "<+-SSW_USING_FRACTIONAL_ACCOUNTING-+>", "Fractional Accounting" );
        } else {
            lookup.put( "<+-SSW_USING_FRACTIONAL_ACCOUNTING-+>", "" );
        }
//        lookup.put( "<+-SSW_+->", CurVee );
    }

    private String GetHeatSinkLine() {
        String retval = "";
        if( CurVee.GetHeatSinks().IsDouble() ) {
            retval = CurVee.GetHeatSinks().GetNumHS() + " (" + CurVee.GetHeatSinks().TotalDissipation() + ")";
        } else {
            retval = CurVee.GetHeatSinks().GetNumHS() + "";
        }
        return retval;
    }

    private String GetCommSystem() {
        return CurVee.GetCommSystem();
    }

    private String GetTandTSystem() {
        return CurVee.GetTandTSystem();
    }

    private String ProcessFluffString( String fluff ) {
        // this turns a big string of fluff into something that is HTML friendly
        if( fluff.equals( "" ) || fluff.equals( "\n" ) || fluff.equals( "\n\r" ) ) {
            return "";
        }
        String retval = "";
        fluff = fluff.replaceAll( "\n\r", "\n" );
        fluff = fluff.replaceAll( "\t", "&nbsp;&nbsp;&nbsp;&nbsp;" );
        fluff = fluff.replaceAll( ":tab:", "" );
        String[] s = fluff.split( "\n", -1 );

        for( int i = 0; i < s.length; i++ ) {
            retval += s[i] + NL;
        }

        return retval;
    }

    private String BuildMultiSlotNotes() {
        String retval = "";
        if( CurVee.HasBlueShield() ) {
            retval += "* The " + CurVee.GetBlueShield().LookupName() + " occupies 1 slot in every location except the HD." + NL;
        }
        return retval;
    }

    private String FormatTonnage( double d, int num ) {
        return String.format( "%1" + tformat, d * num );
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
}