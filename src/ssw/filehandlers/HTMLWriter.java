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

import battleforce.*;
import components.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import common.CommonTools;
import filehandlers.FileCommon;

public class HTMLWriter {

    private Mech CurMech;
    private Hashtable lookup = new Hashtable<String, String>( 90 );
    private String NL = "<BR />";

    public HTMLWriter( Mech m ) {
        CurMech = m;
        // if the current mech is an omnimech, set the loadout to the base
        // before we build the hash table
        if( CurMech.IsOmnimech() ) {
            CurMech.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
        }
        BuildHash();
    }

    public void WriteHTML( String template, String destfile ) throws IOException {
        BufferedWriter FW = new BufferedWriter( new FileWriter( destfile ) );
        BufferedReader FR = new BufferedReader( new FileReader( template ) );
        boolean EOF = false;
        String read = "";
        String write = "";
        Vector equip = new Vector();
        Vector omni = new Vector();

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
                    if( CurMech.IsOmnimech() ) {
                        // yes, go ahead with the export
                        try {
                            FW.write( BuildOmniLines( omni ) );
                            FW.newLine();
                        } catch( IOException e ) {
                            throw e;
                        }
                    }
                } else if( read.contains( "<+-SSW_REMOVE_IF_OMNI_NO_FIXED-+>" ) ) {
                    // see if the mech is an omni and whether we need to remove
                    // this.
                    if( CurMech.IsOmnimech() ) {
                        if( CurMech.GetLoadout().GetNonCore().size() > 0 ) {
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

    private String BuildEquipLines( Vector lines, abPlaceable[] equips, boolean fluff ) {
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
                if( cur instanceof MultiSlotSystem || cur.CanSplit() |! cur.Contiguous() ) {
                    // splittable items are generally too big for two in one
                    // location or are split into different areas.  just avoid.
                    retval += ProcessEquipStatLines( cur, lines, 1 );
                } else {
                    int loc = CurMech.GetLoadout().Find( cur );
                    for( int j = 0; j < equips.length; j++ ) {
                        if( equips[j] != null ) {
                            if( equips[j] instanceof Equipment ) {
                                if( ((Equipment) equips[j]).IsVariableSize() ) {
                                    if( equips[j].CritName().equals( cur.CritName() ) && CurMech.GetLoadout().Find( equips[j] ) == loc ) {
                                        numthisloc++;
                                        equips[j] = null;
                                    }
                                } else {
                                    if( equips[j].LookupName().equals( cur.LookupName() ) && CurMech.GetLoadout().Find( equips[j] ) == loc ) {
                                        numthisloc++;
                                        equips[j] = null;
                                    }
                                }
                            } else {
                                if( equips[j].LookupName().equals( cur.LookupName() ) && CurMech.GetLoadout().Find( equips[j] ) == loc ) {
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

    private String BuildOmniLines( Vector lines ) throws IOException {
        // this routine will build each omnimech loadout and add it to retval.
        String retval = "";
        Vector loadouts = CurMech.GetLoadouts();
        Vector EQLines = new Vector();

        for( int i = 0; i < loadouts.size(); i++ ) {
            // set the mech to the current loadout
            CurMech.SetCurLoadout( ((ifMechLoadout) loadouts.get(i)).GetName() );
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
        CurMech.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
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
        BattleForceStats bfs = new BattleForceStats( CurMech );
        if( tag.equals( "<+-SSW_OMNI_LOADOUT_NAME-+>" ) ) {
            return CurMech.GetLoadout().GetName();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BV-+>" ) ) {
            return String.format( "%1$,d", CurMech.GetCurrentBV() );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_RULES_LEVEL-+>" ) ) {
            if( CurMech.GetBaseRulesLevel() == CurMech.GetLoadout().GetRulesLevel() ) {
                return "";
            } else {
                return CommonTools.GetRulesLevelString( CurMech.GetLoadout().GetRulesLevel() );
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_TECHBASE-+>" ) ) {
            if( CurMech.GetLoadout().GetTechBase() != CurMech.GetTechBase() ) {
                return CommonTools.GetTechbaseString( CurMech.GetLoadout().GetTechBase() );
            } else {
                return "";
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_COST-+>" ) ) {
            return String.format( "%1$,.0f", CurMech.GetTotalCost() );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_ACTUATOR_LINE-+>" ) ) {
            return FileCommon.BuildActuators( CurMech, true );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_SPACE-+>" ) ) {
            return "" + CurMech.GetHeatSinks().NumCrits();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_LOCATION_LINE-+>" ) ) {
            return FileCommon.GetHeatSinkLocations( CurMech );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_TONNAGE-+>" ) ) {
            return FormatTonnage( CurMech.GetHeatSinks().GetLoadoutTonnage(), 1 );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_COUNT-+>" ) ) {
            return "" + CurMech.GetHeatSinks().GetNumHS();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_DISSIPATION-+>" ) ) {
            return "" + CurMech.GetHeatSinks().TotalDissipation();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_HEATSINK_DISSIPATION_LINE-+>" ) ) {
            return GetHeatSinkLine();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_JUMPJET_SPACE-+>" ) ) {
            return "" + CurMech.GetJumpJets().ReportCrits();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_SPEED_JUMP_MP-+>" ) ) {
            if( CurMech.GetJumpJets().GetNumJJ() == 0 ) {
                return "";
            } else {
                if( CurMech.UsingPartialWing() ) {
                    return CurMech.GetJumpJets().GetNumJJ() + " (" + CurMech.GetAdjustedJumpingMP( true ) + ")";
                } else {
                    return "" + CurMech.GetJumpJets().GetNumJJ();
                }
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_JUMPJET_LOCATION_LINE-+>" ) ) {
            return FileCommon.GetJumpJetLocations( CurMech );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_JUMPJET_TONNAGE-+>" ) ) {
            return FormatTonnage( CurMech.GetJumpJets().GetTonnage(), 1 );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_CASE_TONNAGE-+>" ) ) {
            if( CurMech.GetLoadout().GetTechBase() >= AvailableCode.TECH_CLAN && CurMech.GetLoadout().IsUsingClanCASE() ) {
                int[] Locs = CurMech.GetLoadout().FindExplosiveInstances();
                boolean check = false;
                for( int i = 0; i < Locs.length; i++ ) {
                    if( Locs[i] > 0 ) {
                        check = true;
                    }
                }
                if( check ) {
                    return FormatTonnage( CurMech.GetCaseTonnage(), 1 );
                } else {
                    return "";
                }
            } else {
                if( CurMech.GetCaseTonnage() <= 0.0 ) {
                    return "";
                } else {
                    return FormatTonnage( CurMech.GetCaseTonnage(), 1 );
                }
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_CASE_LOCATION_LINE-+>" ) ) {
            return FileCommon.GetCaseLocations( CurMech );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_CASEII_TONNAGE-+>" ) ) {
            if( CurMech.GetCASEIITonnage() <= 0.0 ) {
                return "";
            } else {
                return FormatTonnage( CurMech.GetCASEIITonnage(), 1 );
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_CASEII_LOCATION_LINE-+>" ) ) {
            return FileCommon.GetCaseIILocations( CurMech );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_AVAILABILITY-+>" ) ) {
            return CurMech.GetAvailability().GetBestCombinedCode();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_JUMPJET_TYPE-+>" ) ) {
            if( CurMech.GetJumpJets().IsImproved() ) {
                return "Improved";
            } else {
                return "Standard";
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_POWER_AMP_TONNAGE-+>" ) ) {
            if( CurMech.GetEngine().IsNuclear() ) {
                return "";
            } else {
                if( CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                    return FormatTonnage( CurMech.GetLoadout().GetPowerAmplifier().GetTonnage(), 1 );
                } else {
                    return "";
                }
            }
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_STRING-+>" ) ) {
            int [] BFdmg = CurMech.GetBFDamage( bfs );
            return BFdmg[BFConstants.BF_SHORT] + "/" + BFdmg[BFConstants.BF_MEDIUM] + "/" + BFdmg[BFConstants.BF_LONG] + "/" + BFdmg[BFConstants.BF_EXTREME];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_SHORT-+>" ) ) {
            return "" + CurMech.GetBFDamage( bfs )[BFConstants.BF_SHORT];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_MEDIUM-+>" ) ) {
            return "" + CurMech.GetBFDamage( bfs )[BFConstants.BF_MEDIUM];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_LONG-+>" ) ) {
            return "" + CurMech.GetBFDamage( bfs )[BFConstants.BF_LONG];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_DAMAGE_EXTREME-+>" ) ) {
            return "" + CurMech.GetBFDamage( bfs )[BFConstants.BF_EXTREME];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_OVERHEAT-+>" ) ) {
            return "" + CurMech.GetBFDamage( bfs )[BFConstants.BF_OV];
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_ARMOR-+>" ) ) {
            return "" + CurMech.GetBFArmor();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_STRUCTURE-+>" ) ) {
            return "" + CurMech.GetBFStructure();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_POINTS-+>" ) ) {
            return "" + CurMech.GetBFPoints();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_SIZE-+>" ) ) {
            return "" + CurMech.GetBFSize();
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_MOVEMENT-+>" ) ) {
            return BattleForceTools.GetMovementString( CurMech );
        } else if( tag.equals( "<+-SSW_OMNI_LOADOUT_BF_SPECIALS-+>" ) ) {
            return "" + bfs.getAbilitiesString();
        } else {
            return "";
        }
    }

    private String ProcessEquipFluffLines( abPlaceable a, int num, Vector lines ) {
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
                            if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
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
                            if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
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
                        if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
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
                        if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
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

    private String ProcessEquipStatLines( abPlaceable a, Vector lines, int numthisloc ) {
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
                                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                                    if( a instanceof Equipment ) {
                                        if( ((Equipment) a).IsVariableSize() ) {
                                            retval += a.CritName();
                                            plural = "";
                                        } else {
                                            retval += a.LookupName();
                                        }
                                    } else {
                                        retval += a.LookupName();
                                    }
                                } else {
                                    retval += a.CritName();
                                }
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
                                retval += FileCommon.DecodeCrits( CurMech.GetLoadout().FindInstances( a ) );
                            } else {
                                if( a instanceof MGArray ) {
                                    if( numthisloc > 1 ) {
                                        retval += numthisloc;
                                    } else {
                                        retval += "1";
                                    }
                                } else if( a instanceof MultiSlotSystem ) {
                                    retval += ((MultiSlotSystem) a).ReportCrits();
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
                                retval += FileCommon.EncodeLocations( CurMech.GetLoadout().FindInstances( a ), CurMech.IsQuad() );
                            } else {
                                if( a instanceof MultiSlotSystem ) {
                                    retval += "*";
                                } else {
                                    retval += FileCommon.EncodeLocation( CurMech.GetLoadout().Find( a ), CurMech.IsQuad() );
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_HEAT-+>" ) ) {
                            if( a instanceof ifWeapon ) {
                                if( ((ifWeapon) a).IsUltra() ) {
                                    retval += ((ifWeapon) a).GetHeat() + " /shot (" + ( ((ifWeapon) a).GetBVHeat() * numthisloc ) + " max)";
                                } else if( ((ifWeapon) a).IsRotary() ) {
                                    retval += ((ifWeapon) a).GetHeat() + " /shot (" + ( ((ifWeapon) a).GetBVHeat() * numthisloc ) + " max)";
                                } else {
                                    retval += "" + ((ifWeapon) a).GetBVHeat() * numthisloc;
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
                                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                                    if( a instanceof Equipment ) {
                                        if( ((Equipment) a).IsVariableSize() ) {
                                            retval += a.CritName();
                                            plural = "";
                                        } else {
                                            retval += a.LookupName();
                                        }
                                    } else {
                                        retval += a.LookupName();
                                    }
                                } else {
                                    retval += a.CritName();
                                }
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
                                retval += FileCommon.DecodeCrits( CurMech.GetLoadout().FindInstances( a ) );
                            } else {
                                if( a instanceof MGArray ) {
                                    if( numthisloc > 1 ) {
                                        retval += numthisloc;
                                    } else {
                                        retval += "1";
                                    }
                                } else if( a instanceof MultiSlotSystem ) {
                                    retval += ((MultiSlotSystem) a).ReportCrits();
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
                                retval += FileCommon.EncodeLocations( CurMech.GetLoadout().FindInstances( a ), CurMech.IsQuad() );
                            } else {
                                if( a instanceof MultiSlotSystem ) {
                                    retval += "*";
                                } else {
                                    retval += FileCommon.EncodeLocation( CurMech.GetLoadout().Find( a ), CurMech.IsQuad() );
                                }
                            }
                        } else if( check.equals( "<+-SSW_EQUIP_HEAT-+>" ) ) {
                            if( a instanceof ifWeapon ) {
                                if( ((ifWeapon) a).IsUltra() ) {
                                    retval += ((ifWeapon) a).GetHeat() + " /shot (" + ( ((ifWeapon) a).GetBVHeat() * numthisloc ) + " max)";
                                } else if( ((ifWeapon) a).IsRotary() ) {
                                    retval += ((ifWeapon) a).GetHeat() + " /shot (" + ( ((ifWeapon) a).GetBVHeat() * numthisloc ) + " max)";
                                } else {
                                    retval += "" + ((ifWeapon) a).GetBVHeat() * numthisloc;
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
        Vector v = CurMech.GetLoadout().GetNonCore();
        Vector ret = new Vector();
        if( fluff ) {
            Vector EQ = new Vector();

            // get the weapons for sort first.
            for( int i = 0; i < v.size(); i++ ) {
                if( v.get( i ) instanceof ifWeapon ) {
                    ret.add( v.get( i ) );
                } else {
                    EQ.add( v.get( i ) );
                }
            }

            // sort the weapons by BV
            Object[] o = CurMech.SortWeapons( ret, false );
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
            ret = (Vector) v.clone();

            // add in certain items, such as the targeting computer and MASC
            if( CurMech.GetPhysEnhance().IsMASC() ) {
                ret.add( CurMech.GetPhysEnhance() );
            }
            if( CurMech.UsingTC() ) {
                ret.add( CurMech.GetTC() );
            }
            if( CurMech.HasCommandConsole() ) {
                ret.add( CurMech.GetCommandConsole() );
            }
            if( CurMech.UsingPartialWing() ) {
                ret.add( CurMech.GetPartialWing() );
            }
            if( CurMech.GetLoadout().HasSupercharger() ) {
                ret.add( CurMech.GetLoadout().GetSupercharger() );
            }
            if( CurMech.IsQuad() ) {
                if( CurMech.HasLegAES() ) {
                    ret.add( CurMech.GetRAAES() );
                    ret.add( CurMech.GetLAAES() );
                    ret.add( CurMech.GetRLAES() );
                    ret.add( CurMech.GetLLAES() );
                }
            } else {
                if( CurMech.HasRAAES() ) {
                    ret.add( CurMech.GetRAAES() );
                }
                if( CurMech.HasLAAES() ) {
                    ret.add( CurMech.GetLAAES() );
                }
                if( CurMech.HasLegAES() ) {
                    ret.add( CurMech.GetRLAES() );
                    ret.add( CurMech.GetLLAES() );
                }
            }

            // sort the weapons by location
            ret = FileCommon.SortEquipmentForStats( CurMech, ret );

            // check for artemis and MG arrays
            for( int i = 0; i < ret.size(); i++ ) {
                if( ret.get( i ) instanceof RangedWeapon ) {
                    if( ((RangedWeapon) ret.get( i )).IsUsingFCS() ) {
                        ret.insertElementAt( ((RangedWeapon) ret.get( i )).GetFCS(), i + 1 );
                    }
                    if( ((RangedWeapon) ret.get( i )).IsUsingCapacitor() ) {
                        ret.insertElementAt( ((RangedWeapon) ret.get( i )).GetCapacitor(), i + 1 );
                    }
                } else if( ret.get( i ) instanceof MGArray ) {
                    ret.insertElementAt( ((MGArray) ret.get( i )).GetMGs()[0], i + 1 );
                    ret.insertElementAt( ((MGArray) ret.get( i )).GetMGs()[1], i + 2 );
                    if( ((MGArray) ret.get( i )).GetMGs()[2] != null ) {
                        ret.insertElementAt( ((MGArray) ret.get( i )).GetMGs()[2], i + 3 );
                    }
                    if( ((MGArray) ret.get( i )).GetMGs()[3] != null ) {
                        ret.insertElementAt( ((MGArray) ret.get( i )).GetMGs()[3], i + 4 );
                    }
                }
            }

            // now add in any multi slot systems.
            if( CurMech.HasNullSig() ) {
                ret.add( CurMech.GetNullSig() );
            }
            if( CurMech.HasChameleon() ) {
                ret.add( CurMech.GetChameleon() );
            }
            if( CurMech.HasVoidSig() ) {
                ret.add( CurMech.GetVoidSig() );
            }
            if( CurMech.HasBlueShield() ) {
                ret.add( CurMech.GetBlueShield() );
            }
            if( CurMech.HasEnviroSealing() ) {
                ret.add( CurMech.GetEnviroSealing() );
            }
            if( CurMech.HasTracks() ) {
                ret.add( CurMech.GetTracks() );
            }
        }

        // turn the return vector into an array
        abPlaceable[] retval = new abPlaceable[ret.size()];
        for( int i = 0; i < ret.size(); i++ ) {
            retval[i] = (abPlaceable) ret.get( i );
        }
        return retval;
    }

    private void BuildHash() {
        // creates the hash table from values from the mech.
        lookup.put( "<+-SSW_NAME-+>", CurMech.GetName() );
        lookup.put( "<+-SSW_MODEL-+>", CurMech.GetModel() );
        if( CurMech.IsPrimitive() ) {
            lookup.put( "<+-SSW_TECHBASE-+>", CommonTools.GetTechbaseString( CurMech.GetTechBase() ) + " (Primitive)" );
        } else {
            lookup.put( "<+-SSW_TECHBASE-+>", CommonTools.GetTechbaseString( CurMech.GetTechBase() ) );
        }
        lookup.put( "<+-SSW_TONNAGE-+>", FormatTonnage( CurMech.GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_DRY_TONNAGE-+>", FormatTonnage( CurMech.GetCurrentDryTons(), 1 ) );
        AvailableCode AC = CurMech.GetAvailability();
        lookup.put( "<+-SSW_AVAILABILITY-+>", AC.GetBestCombinedCode() );
        lookup.put( "<+-SSW_PROD_YEAR-+>", "" + CurMech.GetYear() );
        switch( AC.GetTechBase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                if( AC.WentExtinctIS() ) {
                    if( AC.WasReIntrodIS() ) {
                        if( AC.GetISIntroDate() >= AC.GetISReIntroDate() ) {
                            if( CurMech.YearWasSpecified() ) {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                            } else {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetISIntroDate() );
                            }
                            lookup.put( "<+-SSW_EXTINCT_BY-+>", "Never" );
                        } else {
                            if( CurMech.YearWasSpecified() ) {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                            } else {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetISIntroDate() );
                            }
                            lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetISExtinctDate() );
                        }
                    } else {
                        if( CurMech.YearWasSpecified() ) {
                            lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                        } else {
                            lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetISIntroDate() );
                        }
                        lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetISExtinctDate() );
                    }
                } else {
                    if( CurMech.YearWasSpecified() ) {
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
                            if( CurMech.YearWasSpecified() ) {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                            } else {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetCLIntroDate() );
                            }
                            lookup.put( "<+-SSW_EXTINCT_BY-+>", "Never" );
                        } else {
                            if( CurMech.YearWasSpecified() ) {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                            } else {
                                lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetCLIntroDate() );
                            }
                            lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetCLExtinctDate() );
                        }
                    } else {
                        if( CurMech.YearWasSpecified() ) {
                            lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" );
                        } else {
                            lookup.put( "<+-SSW_EARLIEST_YEAR-+>", "" + AC.GetCLIntroDate() );
                        }
                        lookup.put( "<+-SSW_EXTINCT_BY-+>", "" + AC.GetCLExtinctDate() );
                    }
                } else {
                    if( CurMech.YearWasSpecified() ) {
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
        lookup.put( "<+-SSW_OVERVIEW-+>", ProcessFluffString( CurMech.GetOverview() ) );
        lookup.put( "<+-SSW_CAPABILITIES-+>", ProcessFluffString( CurMech.GetCapabilities() ) );
        lookup.put( "<+-SSW_BATTLE_HISTORY-+>", ProcessFluffString( CurMech.GetHistory() ) );
        lookup.put( "<+-SSW_DEPLOYMENT-+>", ProcessFluffString( CurMech.GetDeployment() ) );
        lookup.put( "<+-SSW_VARIANTS-+>", ProcessFluffString( CurMech.GetVariants() ) );
        lookup.put( "<+-SSW_NOTABLES-+>", ProcessFluffString( CurMech.GetNotables() ) );
        lookup.put( "<+-SSW_ADDITIONAL-+>", ProcessFluffString( CurMech.GetAdditional() ) );
        lookup.put( "<+-SSW_MANUFACTURER-+>", CurMech.GetCompany() );
        lookup.put( "<+-SSW_MANUFACTURER_LOCATION-+>", CurMech.GetLocation() );
        lookup.put( "<+-SSW_MANUFACTURER_ENGINE-+>", CurMech.GetEngineManufacturer()+ " " + CurMech.GetEngine().GetRating() + " " + CurMech.GetEngine() );
        lookup.put( "<+-SSW_MANUFACTURER_CHASSIS-+>", CurMech.GetChassisModel() + " " + CurMech.GetIntStruc().CritName() );
        if( CurMech.HasCTCase()|| CurMech.HasLTCase() || CurMech.HasRTCase() ) {
            lookup.put( "<+-SSW_MANUFACTURER_ARMOR-+>", CurMech.GetArmorModel() + " " + CurMech.GetArmor().CritName() + " w/ CASE" );
        } else {
            lookup.put( "<+-SSW_MANUFACTURER_ARMOR-+>", CurMech.GetArmorModel() + " " + CurMech.GetArmor().CritName() );
        }
        lookup.put( "<+-SSW_MANUFACTURER_JUMPJETS-+>", CurMech.GetJJModel() );
        lookup.put( "<+-SSW_MANUFACTURER_COMM_SYSTEM-+>", GetCommSystem() );
        lookup.put( "<+-SSW_MANUFACTURER_T_AND_T_SYSTEM-+>", GetTandTSystem() );
        lookup.put( "<+-SSW_CHASSIS_TONNAGE-+>", FormatTonnage( CurMech.GetIntStruc().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_ARMOR_TONNAGE-+>", FormatTonnage( CurMech.GetArmor().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_ENGINE_TONNAGE-+>", FormatTonnage( CurMech.GetEngine().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_GYRO_TONNAGE-+>", FormatTonnage( CurMech.GetGyro().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_COCKPIT_TONNAGE-+>", FormatTonnage( CurMech.GetCockpit().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_HEATSINK_TONNAGE-+>", FormatTonnage( CurMech.GetHeatSinks().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_JUMPJET_TONNAGE-+>", FormatTonnage( CurMech.GetJumpJets().GetTonnage(), 1 ) );
        lookup.put( "<+-SSW_ENHANCEMENT_TONNAGE-+>", FormatTonnage( CurMech.GetPhysEnhance().GetTonnage(), 1 ) );
        // need a routine for this...
        lookup.put( "<+-SSW_EQUIPMENT_TOTAL_TONNAGE-+>", "" );
        lookup.put( "<+-SSW_HD_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) );
        lookup.put( "<+-SSW_CT_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) );
        lookup.put( "<+-SSW_LT_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) );
        lookup.put( "<+-SSW_RT_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) );
        if( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) != CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) ) {
            lookup.put( "<+-SSW_TORSO_ARMOR-+>", "LT: " + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) + " RT: " + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) );
        } else {
            lookup.put( "<+-SSW_TORSO_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) );
        }
        lookup.put( "<+-SSW_LA_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) );
        lookup.put( "<+-SSW_RA_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) );
        if( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) != CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) ) {
            lookup.put( "<+-SSW_ARM_ARMOR-+>", "LA: " + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) + " RA: " + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) );
        } else {
            lookup.put( "<+-SSW_ARM_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) );
        }
        lookup.put( "<+-SSW_LL_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) );
        lookup.put( "<+-SSW_RL_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) );
        if( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) != CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) ) {
            lookup.put( "<+-SSW_LEG_ARMOR-+>", "LL: " + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) + " RL: " + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) );
        } else {
            lookup.put( "<+-SSW_LEG_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) );
        }
        lookup.put( "<+-SSW_CTR_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CTR ) );
        lookup.put( "<+-SSW_LTR_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) );
        lookup.put( "<+-SSW_RTR_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) );
        if( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) != CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) ) {
            lookup.put( "<+-SSW_TORSO_REAR_ARMOR-+>", "LTR: " + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) + " RTR: " + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) );
        } else {
            lookup.put( "<+-SSW_TORSO_REAR_ARMOR-+>", "" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) );
        }
        lookup.put( "<+-SSW_HD_INTERNAL-+>", "" + CurMech.GetIntStruc().GetHeadPoints() );
        lookup.put( "<+-SSW_CT_INTERNAL-+>", "" + CurMech.GetIntStruc().GetCTPoints() );
        lookup.put( "<+-SSW_TORSO_INTERNAL-+>", "" + CurMech.GetIntStruc().GetSidePoints() );
        lookup.put( "<+-SSW_ARM_INTERNAL-+>", "" + CurMech.GetIntStruc().GetArmPoints() );
        lookup.put( "<+-SSW_LEG_INTERNAL-+>", "" + CurMech.GetIntStruc().GetLegPoints() );
        lookup.put( "<+-SSW_ARMOR_COVERAGE-+>", "" + CurMech.GetArmor().GetCoverage() );
        lookup.put( "<+-SSW_JUMPJET_COUNT-+>", "" + CurMech.GetJumpJets().GetNumJJ() );
        if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
            lookup.put( "<+-SSW_JUMPJET_DISTANCE-+>", ( CurMech.GetJumpJets().GetNumJJ() * 30 ) + " meters (" + ( CurMech.GetAdjustedJumpingMP( true ) * 30 ) + " meters)" );
        } else {
            lookup.put( "<+-SSW_JUMPJET_DISTANCE-+>", ( CurMech.GetJumpJets().GetNumJJ() * 30 ) + " meters" );
        }
        lookup.put( "<+-SSW_SPEED_WALK_KMPH-+>", "" + ( CurMech.GetWalkingMP() * 10.75f ) + " km/h" );
        if( CurMech.GetAdjustedRunningMP( false, true ) != CurMech.GetRunningMP() ) {
            lookup.put( "<+-SSW_SPEED_RUN_KMPH-+>", "" + ( CurMech.GetRunningMP() * 10.75f ) + " km/h (" + ( CurMech.GetAdjustedRunningMP( false, true ) * 10.75f ) + " km/h)" );
        } else {
            lookup.put( "<+-SSW_SPEED_RUN_KMPH-+>", "" + ( CurMech.GetRunningMP() * 10.75f ) + " km/h" );
        }
        lookup.put( "<+-SSW_SPEED_WALK_MP-+>", "" + CurMech.GetWalkingMP() );
        if( CurMech.GetAdjustedRunningMP( false, true ) != CurMech.GetRunningMP() ) {
            lookup.put( "<+-SSW_SPEED_RUN_MP-+>", CurMech.GetRunningMP() + " (" + CurMech.GetAdjustedRunningMP( false, true ) + ")" );
        } else {
            lookup.put( "<+-SSW_SPEED_RUN_MP-+>", "" + CurMech.GetRunningMP() );
        }
        if( CurMech.GetAdjustedJumpingMP( false ) != CurMech.GetJumpJets().GetNumJJ() ) {
            lookup.put( "<+-SSW_SPEED_JUMP_MP-+>", CurMech.GetJumpJets().GetNumJJ() + " (" + CurMech.GetAdjustedJumpingMP( true ) + ")" );
        } else {
            lookup.put( "<+-SSW_SPEED_JUMP_MP-+>", "" + CurMech.GetJumpJets().GetNumJJ() );
        }
        lookup.put( "<+-SSW_COST-+>", String.format( "%1$,.0f", CurMech.GetTotalCost() ) );
        lookup.put( "<+-SSW_DRY_COST-+>", String.format( "%1$,.0f", CurMech.GetDryCost() ) );
        lookup.put( "<+-SSW_BV2-+>", String.format( "%1$,d", CurMech.GetCurrentBV() ) );
        lookup.put( "<+-SSW_ENGINE_SPACE-+>", "" + CurMech.GetEngine().ReportCrits() );
        lookup.put( "<+-SSW_CHASSIS_SPACE-+>", "" + CurMech.GetIntStruc().NumCrits() );
        lookup.put( "<+-SSW_CHASSIS_LOCATION_LINE-+>", FileCommon.GetInternalLocations( CurMech ) );
        lookup.put( "<+-SSW_COCKPIT_SPACE-+>", "" + CurMech.GetCockpit().ReportCrits() );
        lookup.put( "<+-SSW_GYRO_SPACE-+>", "" + CurMech.GetGyro().NumCrits() );
        lookup.put( "<+-SSW_ARMOR_SPACE-+>", "" + CurMech.GetArmor().NumCrits() );
        lookup.put( "<+-SSW_ARMOR_LOCATION_LINE-+>", FileCommon.GetArmorLocations( CurMech ) );
        lookup.put( "<+-SSW_HEATSINK_TOTAL_SPACE-+>", "" + CurMech.GetHeatSinks().NumCrits() );
        lookup.put( "<+-SSW_HEATSINK_LOCATION_LINE-+>", FileCommon.GetHeatSinkLocations( CurMech ) );
        lookup.put( "<+-SSW_ACTUATOR_LINE-+>", FileCommon.BuildActuators( CurMech, true ) );
        lookup.put( "<+-SSW_ENHANCEMENT_SPACE-+>", "" + CurMech.GetPhysEnhance().NumCrits() );
        lookup.put( "<+-SSW_ENHANCEMENT_LOCATION_LINE-+>", FileCommon.GetTSMLocations( CurMech ) );
        lookup.put( "<+-SSW_JUMPJET_SPACE-+>", "" + CurMech.GetJumpJets().ReportCrits() );
        lookup.put( "<+-SSW_JUMPJET_LOCATION_LINE-+>", FileCommon.GetJumpJetLocations( CurMech ) );
        lookup.put( "<+-SSW_ARMOR_FACTOR-+>", "" + CurMech.GetArmor().GetArmorValue() );
        lookup.put( "<+-SSW_ENGINE_RATING-+>", "" + CurMech.GetEngine().GetRating() );
        lookup.put( "<+-SSW_ENGINE_TYPE-+>", CurMech.GetEngine().CritName() );
        lookup.put( "<+-SSW_HEATSINK_DISSIPATION_LINE-+>", GetHeatSinkLine() );
        if( CurMech.GetHeatSinks().GetNumHS() < CurMech.GetEngine().InternalHeatSinks() ) {
            lookup.put( "<+-SSW_HEATSINKS_IN_ENGINE-+>", "" + CurMech.GetHeatSinks().GetNumHS() );
        } else {
            lookup.put( "<+-SSW_HEATSINKS_IN_ENGINE-+>", "" + CurMech.GetEngine().InternalHeatSinks() );
        }
        if( CurMech.GetHeatSinks().IsDouble() ) {
            lookup.put( "<+-SSW_HEATSINK_TYPE-+>", "Double" );
        } else if( CurMech.GetHeatSinks().IsCompact() ) {
            lookup.put( "<+-SSW_HEATSINK_TYPE-+>", "Compact" );
        } else if( CurMech.GetHeatSinks().IsLaser() ) {
            lookup.put( "<+-SSW_HEATSINK_TYPE-+>", "Laser" );
        } else {
            lookup.put( "<+-SSW_HEATSINK_TYPE-+>", "Single" );
        }
        lookup.put( "<+-SSW_GYRO_TYPE-+>", CurMech.GetGyro().GetReportName() );
        lookup.put( "<+-SSW_COCKPIT_TYPE-+>", CurMech.GetCockpit().GetReportName() );
        lookup.put( "<+-SSW_ARMOR_TYPE-+>", CurMech.GetArmor().CritName() );
        if( CurMech.GetJumpJets().GetNumJJ() <= 0 ) {
            lookup.put( "<+-SSW_JUMPJET_TYPE-+>", "" );
        } else {
            if( CurMech.GetJumpJets().IsImproved() ) {
                lookup.put( "<+-SSW_JUMPJET_TYPE-+>", "Improved" );
            } else {
                if( CurMech.GetJumpJets().IsUMU() ) {
                    lookup.put( "<+-SSW_JUMPJET_TYPE-+>", "UMU" );
                } else{
                    lookup.put( "<+-SSW_JUMPJET_TYPE-+>", "Standard" );
                }
            }
        }
        lookup.put( "<+-SSW_HEATSINK_COUNT-+>", "" + CurMech.GetHeatSinks().GetNumHS() );
        lookup.put( "<+-SSW_HEATSINK_DISSIPATION-+>", "" + CurMech.GetHeatSinks().TotalDissipation() );
        lookup.put( "<+-SSW_INTERNAL_TYPE-+>", CurMech.GetIntStruc().CritName() );
        lookup.put( "<+-SSW_CASE_LOCATION_LINE-+>", FileCommon.GetCaseLocations( CurMech ) );
        lookup.put( "<+-SSW_CASE_TONNAGE-+>", FormatTonnage( CurMech.GetCaseTonnage(), 1 ) );
        lookup.put( "<+-SSW_CASEII_LOCATION_LINE-+>", FileCommon.GetCaseIILocations( CurMech ) );
        lookup.put( "<+-SSW_CASEII_TONNAGE-+>", FormatTonnage( CurMech.GetCASEIITonnage(), 1 ) );
        if( CurMech.IsQuad() ) {
            if( CurMech.IsIndustrialmech() ) {
                lookup.put( "<+-SSW_CHASSIS_CONFIG-+>", "Quad IndustrialMech" );
            } else {
                lookup.put( "<+-SSW_CHASSIS_CONFIG-+>", "Quad" );
            }
            lookup.put( "<+-SSW_ARM_LOCATION_NAME-+>", "R/L Front Leg" );
            lookup.put( "<+-SSW_LEG_LOCATION_NAME-+>", "R/L Rear Leg" );
            lookup.put( "<+-SSW_ARM_LOCATION_LONGNAME-+>", "Right/Left Front Leg" );
            lookup.put( "<+-SSW_LEG_LOCATION_LONGNAME-+>", "Right/Left Rear Leg" );
        } else {
            if( CurMech.IsIndustrialmech() ) {
                lookup.put( "<+-SSW_CHASSIS_CONFIG-+>", "Biped IndustrialMech" );
            } else {
                lookup.put( "<+-SSW_CHASSIS_CONFIG-+>", "Biped" );
            }
            lookup.put( "<+-SSW_ARM_LOCATION_NAME-+>", "R/L Arm" );
            lookup.put( "<+-SSW_LEG_LOCATION_NAME-+>", "R/L Leg" );
            lookup.put( "<+-SSW_ARM_LOCATION_LONGNAME-+>", "Right/Left Arm" );
            lookup.put( "<+-SSW_LEG_LOCATION_LONGNAME-+>", "Right/Left Leg" );
        }
        lookup.put( "<+-SSW_RULES_LEVEL-+>", CommonTools.GetRulesLevelString( CurMech.GetRulesLevel() ) );
        if( CurMech.IsOmnimech() ) {
            lookup.put( "<+-SSW_POD_TONNAGE-+>", FormatTonnage( ( CurMech.GetTonnage() - CurMech.GetCurrentTons() ), 1 ) );
        } else {
            lookup.put( "<+-SSW_POD_TONNAGE-+>", "" );
        }
        if( CurMech.GetEngine().IsNuclear() ) {
            lookup.put( "<+-SSW_POWER_AMP_TONNAGE-+>", "" );
        } else {
            if( CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() > 0 ) {
                lookup.put( "<+-SSW_POWER_AMP_TONNAGE-+>", FormatTonnage( CurMech.GetLoadout().GetPowerAmplifier().GetTonnage(), 1 ) );
            } else {
                lookup.put( "<+-SSW_POWER_AMP_TONNAGE-+>", "" );
            }
        }
        // added this in to avoid the "null" result.
        lookup.put( "<+-SSW_REMOVE_IF_OMNI_NO_FIXED-+>", "" );
        lookup.put( "<+-SSW_MULTISLOTNOTES-+>", BuildMultiSlotNotes() );
        if( CurMech.HasEjectionSeat() ) {
            lookup.put( "<+-SSW_EJECTIONSEAT_TONNAGE-+>", "" + CurMech.GetEjectionSeat().GetTonnage() );
        } else {
            lookup.put( "<+-SSW_EJECTIONSEAT_TONNAGE-+>", "" );
        }
        BattleForceStats bfs = new BattleForceStats( CurMech );
        int [] BFdmg = CurMech.GetBFDamage( bfs );
        lookup.put( "<+-SSW_BF_DAMAGE_STRING-+>", BFdmg[BFConstants.BF_SHORT] + "/" + BFdmg[BFConstants.BF_MEDIUM] + "/" + BFdmg[BFConstants.BF_LONG] + "/" + BFdmg[BFConstants.BF_EXTREME] );
        lookup.put( "<+-SSW_BF_DAMAGE_SHORT-+>", "" + BFdmg[BFConstants.BF_SHORT] );
        lookup.put( "<+-SSW_BF_DAMAGE_MEDIUM-+>", "" + BFdmg[BFConstants.BF_MEDIUM] );
        lookup.put( "<+-SSW_BF_DAMAGE_LONG-+>", "" + BFdmg[BFConstants.BF_LONG] );
        lookup.put( "<+-SSW_BF_DAMAGE_EXTREME-+>", "" + BFdmg[BFConstants.BF_EXTREME] );
        lookup.put( "<+-SSW_BF_OVERHEAT-+>", "" + BFdmg[BFConstants.BF_OV] );
        lookup.put( "<+-SSW_BF_ARMOR-+>", "" + CurMech.GetBFArmor() );
        lookup.put( "<+-SSW_BF_STRUCTURE-+>", "" + CurMech.GetBFStructure() );
        lookup.put( "<+-SSW_BF_POINTS-+>", "" + CurMech.GetBFPoints() );
        lookup.put( "<+-SSW_BF_SIZE-+>", "" + CurMech.GetBFSize() );
        lookup.put( "<+-SSW_BF_MOVEMENT-+>", BattleForceTools.GetMovementString( CurMech ) );
        lookup.put( "<+-SSW_BF_SPECIALS-+>", bfs.getAbilitiesString() );
//        lookup.put( "<+-SSW_+->", CurMech );
    }

    private String GetHeatSinkLine() {
        String retval = "";
        if( CurMech.GetHeatSinks().IsDouble() ) {
            retval = CurMech.GetHeatSinks().GetNumHS() + " (" + CurMech.GetHeatSinks().TotalDissipation() + ")";
        } else {
            retval = CurMech.GetHeatSinks().GetNumHS() + "";
        }
        return retval;
    }

    private String GetCommSystem() {
        return CurMech.GetCommSystem();
    }

    private String GetTandTSystem() {
        return CurMech.GetTandTSystem();
    }

    private String ProcessFluffString( String fluff ) {
        // this turns a big string of fluff into something that is HTML friendly
        if( fluff.equals( "" ) ) {
            return "";
        }
        String retval = "";
        retval = retval.replaceAll( "\n\r", "\n" );
        String[] s = fluff.split( "\n", -1 );

        for( int i = 0; i < s.length; i++ ) {
            retval += s[i] + NL;
        }

        return retval;
    }

    private String BuildMultiSlotNotes() {
        String retval = "";
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
        return retval;
    }

    private String FormatTonnage( double d, int num ) {
        return String.format( "%1$6.2f", d * num );
    }
}