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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;
import ssw.Constants;
import ssw.components.*;

public class XMLWriter {
    private Mech CurMech;
    private String tab = "    ";
    private String NL = System.getProperty( "line.separator" );

    public XMLWriter( Mech m ) {
        CurMech = m;
    }

    public void WriteXML( String filename ) throws IOException {
        //BufferedWriter FR = new BufferedWriter( new FileWriter( filename ) );
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        // beginning of an XML file:
        FR.write( "<?xml version=\"1.0\" encoding =\"UTF-8\"?>" );
        FR.newLine();

        // start parsing the mech
        FR.write( "<mech name=\"" + FileCommon.EncodeFluff( CurMech.GetName() ) + "\" model=\"" + FileCommon.EncodeFluff( CurMech.GetModel() ) + "\" tons=\"" + CurMech.GetTonnage() + "\" omnimech=\"" + GetBoolean( CurMech.IsOmnimech() ) + "\" solaris7id=\"" + CurMech.GetSolaris7ID() + "\" solaris7imageid=\"" + CurMech.GetSolaris7ImageID() + "\" sswimage=\"" + CurMech.GetSSWImage() + "\">" );
        FR.newLine();

        // add the battle value if this is not an omnimech.  otherwise, we'll
        // add the battle value for each omni loadout.  NOTE: This value is never
        // used by SSW since the BV is dynamically calculated.  This is purely for
        // other programs that may want to use the program.
        if( ! CurMech.IsOmnimech() ) {
            FR.write( tab + "<battle_value>" + CurMech.GetCurrentBV() + "</battle_value>" );
            FR.newLine();
        }

        FR.write( tab + "<rules_level>" + CurMech.GetBaseRulesLevel() + "</rules_level>" );
        FR.newLine();

        FR.write( tab + "<era>" + CurMech.GetEra() + "</era>" );
        FR.newLine();

        if( CurMech.IsIndustrialmech() ) {
            FR.write( tab + "<mech_type>IndustrialMech</mech_type>" );
            FR.newLine();
        } else {
            FR.write( tab + "<mech_type>BattleMech</mech_type>" );
            FR.newLine();
        }

        FR.write( tab + "<techbase manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetCompany() ) + "\" location=\"" + FileCommon.EncodeFluff( CurMech.GetLocation() ) + "\">" + GetTechbase() + "</techbase>" );
        FR.newLine();

        FR.write( tab + "<year restricted=\"" + GetBoolean( CurMech.IsYearRestricted() ) + "\">" + CurMech.GetYear() + "</year>" );
        FR.newLine();

        FR.write( tab + "<motive_type>" + GetMotiveType() + "</motive_type>" );
        FR.newLine();

        FR.write( tab + "<structure manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetChassisModel() ) + "\">" );
        FR.newLine();
        FR.write( tab + tab + "<type>" + CurMech.GetIntStruc().GetLookupName() + "</type>" );
        FR.newLine();
        if( CurMech.GetIntStruc().NumCrits() > 0 ) {
            FR.write( GetLocationLines( tab + tab, CurMech.GetIntStruc() ) );
        }
        FR.write( tab + "</structure>" );
        FR.newLine();

        Vector engineLocs = CurMech.GetLoadout().FindIndexes( CurMech.GetEngine() );
        LocationIndex ls = new LocationIndex();
        LocationIndex rs = new LocationIndex();
        ls.Location = Constants.LOC_LT;
        ls.Index = 12;
        rs.Location = Constants.LOC_RT;
        rs.Index = 12;
        for( int i = 0; i < engineLocs.size(); i++ ) {
            ls.SetFirst( ((LocationIndex) engineLocs.get( i )) );
            rs.SetFirst( ((LocationIndex) engineLocs.get( i )) );
        }
        if( ls.Index == 12 ) { ls.Index = -1; }
        if( rs.Index == 12 ) { rs.Index = -1; }
        FR.write( tab + "<engine rating=\"" + CurMech.GetEngine().GetRating() + "\" manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetEngineManufacturer() ) + "\" lsstart=\"" + ls.Index + "\" rsstart=\"" + rs.Index + "\">" + CurMech.GetEngine().GetLookupName() + "</engine>" );
        FR.newLine();

        FR.write( tab + "<gyro>" + CurMech.GetGyro().GetLookupName() + "</gyro>" );
        FR.newLine();

        if( CurMech.HasEjectionSeat() ) {
            FR.write( tab + "<cockpit ejectionseat=\"true\">" + CurMech.GetCockpit().GetLookupName() + "</cockpit>" );
        } else {
            FR.write( tab + "<cockpit>" + CurMech.GetCockpit().GetLookupName() + "</cockpit>" );
        }
        FR.newLine();

        if( CurMech.GetPhysEnhance().IsTSM() || CurMech.GetPhysEnhance().IsMASC() ) {
            FR.write( tab + "<enhancement>" );
            FR.newLine();
            FR.write( tab + tab + "<type>" + CurMech.GetPhysEnhance().GetLookupName() + "</type>" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab, CurMech.GetPhysEnhance() ) );
            FR.write( tab + "</enhancement>" );
            FR.newLine();
        }

        FR.write( tab + "<armor manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetArmorModel() ) + "\">" );
        FR.newLine();
        FR.write( tab + tab + "<type>" + CurMech.GetArmor().GetLookupName() + "</type>" );
        FR.newLine();
        FR.write( tab + tab + "<hd>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_HD ) + "</hd>" );
        FR.newLine();
        FR.write( tab + tab + "<ct>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_CT ) + "</ct>" );
        FR.newLine();
        FR.write( tab + tab + "<ctr>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_CTR ) + "</ctr>" );
        FR.newLine();
        FR.write( tab + tab + "<lt>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_LT ) + "</lt>" );
        FR.newLine();
        FR.write( tab + tab + "<ltr>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_LTR ) + "</ltr>" );
        FR.newLine();
        FR.write( tab + tab + "<rt>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_RT ) + "</rt>" );
        FR.newLine();
        FR.write( tab + tab + "<rtr>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_RTR ) + "</rtr>" );
        FR.newLine();
        FR.write( tab + tab + "<la>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_LA ) + "</la>" );
        FR.newLine();
        FR.write( tab + tab + "<ra>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_RA ) + "</ra>" );
        FR.newLine();
        FR.write( tab + tab + "<ll>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_LL ) + "</ll>" );
        FR.newLine();
        FR.write( tab + tab + "<rl>" + CurMech.GetArmor().GetLocationArmor( Constants.LOC_RL ) + "</rl>" );
        FR.newLine();
        if( CurMech.GetArmor().NumCrits() > 0 ) {
            FR.write( GetLocationLines( tab + tab, CurMech.GetArmor() ) );
        }
        FR.write( tab + "</armor>" );
        FR.newLine();

        if( CurMech.IsOmnimech() ) {
            CurMech.SetCurLoadout( Constants.BASELOADOUT_NAME );
        }
        FR.write( tab + "<baseloadout a4srm=\"" + GetBoolean( CurMech.UsingA4SRM() ) + "\" a4lrm=\"" + GetBoolean( CurMech.UsingA4LRM() ) + "\" a4mml=\"" + GetBoolean( CurMech.UsingA4MML() ) + "\">" );
        FR.newLine();
        FR.write( tab + tab + "<actuators lla=\"" + GetBoolean( CurMech.GetActuators().LeftLowerInstalled() ) + "\" lh=\"" + GetBoolean( CurMech.GetActuators().LeftHandInstalled() ) + "\" rla=\"" + GetBoolean( CurMech.GetActuators().RightLowerInstalled() ) + "\" rh=\"" + GetBoolean( CurMech.GetActuators().RightHandInstalled() ) + "\"/>" );
        FR.newLine();
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            FR.write( tab + tab + "<jumpjets number=\"" + CurMech.GetJumpJets().GetNumJJ() + "\">" );
            FR.newLine();
            if( CurMech.GetJumpJets().IsImproved() ) {
                FR.write( tab + tab + tab + "<type>Improved Jump Jet</type>" );
            } else {
                FR.write( tab + tab + tab + "<type>Standard Jump Jet</type>" );
            }
            FR.newLine();
            FR.write( GetJumpJetLines( tab + tab + tab, true ) );
            FR.write( tab + tab + "</jumpjets>" );
            FR.newLine();
        }
        FR.write( tab + tab + "<heatsinks number=\"" + CurMech.GetHeatSinks().GetNumHS() + "\">" );
        FR.newLine();
        if( CurMech.GetHeatSinks().IsDouble() ) {
            FR.write( tab + tab + tab + "<type>Double Heat Sink</type>" );
        } else {
            FR.write( tab + tab + tab + "<type>Single Heat Sink</type>" );
        }
        FR.newLine();
        FR.write( GetHeatsinkLines( tab + tab + tab, true ) );
        FR.write( tab + tab + "</heatsinks>" );
        FR.newLine();
        if( CurMech.HasNullSig() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetNullSig().GetCritName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetNullSig() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasVoidSig() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetVoidSig().GetCritName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetVoidSig() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasBlueShield() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetBlueShield().GetCritName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetBlueShield() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasChameleon() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetChameleon().GetCritName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetChameleon() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasEnviroSealing() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetEnviroSealing().GetCritName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetEnviroSealing() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        FR.write( GetEquipmentLines( tab + tab ) );
        if( CurMech.GetRulesLevel() == Constants.EXPERIMENTAL && CurMech.GetEra() == Constants.CLAN_INVASION ) {
            // check for armored components
            FR.write( GetArmoredLocations( tab + tab ) );
        }
        FR.write( tab + "</baseloadout>" );
        FR.newLine();

        if( CurMech.IsOmnimech() ) {
            Vector v = CurMech.GetLoadouts();
            for( int i = 0; i < v.size(); i++ ) {
                CurMech.SetCurLoadout( ((ifLoadout) v.get( i )).GetName() );
                if( CurMech.GetBaseRulesLevel() != CurMech.GetLoadout().GetRulesLevel() ) {
                    FR.write( tab + "<loadout name=\"" + FileCommon.EncodeFluff( CurMech.GetLoadout().GetName() ) + "\" ruleslevel=\"" + CurMech.GetLoadout().GetRulesLevel() + "\" a4srm=\"" + GetBoolean( CurMech.UsingA4SRM() ) + "\" a4lrm=\"" + GetBoolean( CurMech.UsingA4LRM() ) + "\" a4mml=\"" + GetBoolean( CurMech.UsingA4MML() ) + "\">" );
                } else {
                    FR.write( tab + "<loadout name=\"" + FileCommon.EncodeFluff( CurMech.GetLoadout().GetName() ) + "\" a4srm=\"" + GetBoolean( CurMech.UsingA4SRM() ) + "\" a4lrm=\"" + GetBoolean( CurMech.UsingA4LRM() ) + "\" a4mml=\"" + GetBoolean( CurMech.UsingA4MML() ) + "\">" );
                }
                FR.newLine();
                // add in the battle value for this loadout
                FR.write( tab + tab + "<battle_value>" + CurMech.GetCurrentBV() + "</battle_value>" );
                FR.newLine();
                FR.write( tab + tab + "<actuators lla=\"" + GetBoolean( CurMech.GetActuators().LeftLowerInstalled() ) + "\" lh=\"" + GetBoolean( CurMech.GetActuators().LeftHandInstalled() ) + "\" rla=\"" + GetBoolean( CurMech.GetActuators().RightLowerInstalled() ) + "\" rh=\"" + GetBoolean( CurMech.GetActuators().RightHandInstalled() ) + "\"/>" );
                FR.newLine();
                if( CurMech.GetJumpJets().GetNumJJ() > CurMech.GetJumpJets().GetBaseLoadoutNumJJ() ) {
                    FR.write( tab + tab + "<jumpjets number=\"" + CurMech.GetJumpJets().GetNumJJ() + "\">" );
                    FR.newLine();
                    if( CurMech.GetJumpJets().IsImproved() ) {
                        FR.write( tab + tab + tab + "<type>Improved Jump Jet</type>" );
                    } else {
                        FR.write( tab + tab + tab + "<type>Standard Jump Jet</type>" );
                    }
                    FR.newLine();
                    FR.write( GetJumpJetLines( tab + tab + tab, false ) );
                    FR.write( tab + tab + "</jumpjets>" );
                    FR.newLine();
                }
                if( CurMech.GetLoadout().GetHeatSinks().GetNumHS() > CurMech.GetLoadout().GetHeatSinks().GetBaseLoadoutNumHS() ) {
                    FR.write( tab + tab + "<heatsinks number=\"" + CurMech.GetHeatSinks().GetNumHS() + "\">" );
                    FR.newLine();
                    if( CurMech.GetHeatSinks().IsDouble() ) {
                        FR.write( tab + tab + tab + "<type>Double Heat Sink</type>" );
                    } else {
                        FR.write( tab + tab + tab + "<type>Single Heat Sink</type>" );
                    }
                    FR.newLine();
                    FR.write( GetHeatsinkLines( tab + tab + tab, false ) );
                    FR.write( tab + tab + "</heatsinks>" );
                    FR.newLine();
                }
                FR.write( GetEquipmentLines( tab + tab ) );
                if( CurMech.GetRulesLevel() == Constants.EXPERIMENTAL && CurMech.GetEra() == Constants.CLAN_INVASION ) {
                    // check for armored components
                    FR.write( GetArmoredLocations( tab + tab ) );
                }
                FR.write( tab + "</loadout>" );
                FR.newLine();
            }
        }

        FR.write( tab + "<fluff>" );
        FR.newLine();
        FR.write( tab + tab + "<overview>" + FileCommon.EncodeFluff( CurMech.GetOverview() ) + "</overview>" );
        FR.newLine();
        FR.write( tab + tab + "<capabilities>" + FileCommon.EncodeFluff( CurMech.GetCapabilities() ) + "</capabilities>" );
        FR.newLine();
        FR.write( tab + tab + "<battlehistory>" + FileCommon.EncodeFluff( CurMech.GetHistory() ) + "</battlehistory>" );
        FR.newLine();
        FR.write( tab + tab + "<deployment>" + FileCommon.EncodeFluff( CurMech.GetDeployment() ) + "</deployment>" );
        FR.newLine();
        FR.write( tab + tab + "<variants>" + FileCommon.EncodeFluff( CurMech.GetVariants() ) + "</variants>" );
        FR.newLine();
        FR.write( tab + tab + "<notables>" + FileCommon.EncodeFluff( CurMech.GetNotables() ) + "</notables>" );
        FR.newLine();
        FR.write( tab + tab + "<additional>" + FileCommon.EncodeFluff( CurMech.GetAdditional() ) + "</additional>" );
        FR.newLine();
        FR.write( tab + tab + "<jumpjet_model>" + FileCommon.EncodeFluff( CurMech.GetJJModel() ) + "</jumpjet_model>" );
        FR.newLine();
        FR.write( tab + tab + "<commsystem>" + FileCommon.EncodeFluff( CurMech.GetCommSystem() ) + "</commsystem>" );
        FR.newLine();
        FR.write( tab + tab + "<tandtsystem>" + FileCommon.EncodeFluff( CurMech.GetTandTSystem() ) + "</tandtsystem>" );
        FR.newLine();
        FR.write( tab + "</fluff>" );
        FR.newLine();

        FR.write( "</mech>" );
        FR.newLine();
        FR.close();
    }

    private String GetBoolean( boolean b ) {
        if( b ) {
            return "TRUE";
        } else {
            return "FALSE";
        }
    }

    private String GetTechbase() {
        if( CurMech.IsClan() ) {
            return Constants.strCLAN;
        } else {
            return Constants.strINNER_SPHERE;
        }
    }

    private String GetMotiveType() {
        if( CurMech.IsQuad() ) {
            return "Quad";
        } else {
            return "Biped";
        }
    }

    private String GetLocationLines( String Prefix, abPlaceable p ) {
        String retval = "";
        if( p.Contiguous() ) {
            if( p.CanSplit() ) {
                Vector v = CurMech.GetLoadout().FindSplitIndex( p );
                if( v.size() < 1 ) {
                    return "";
                } else if( v.size() == 1 ) {
                    LocationIndex l = (LocationIndex) v.get( 0 );
                    retval += Prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, CurMech.IsQuad() ) + "</location>" + NL;
                } else {
                    for( int i = 0; i < v.size(); i++ ) {
                        LocationIndex l = (LocationIndex) v.get( i );
                        retval += Prefix + "<splitlocation index=\"" + l.Index + "\" number=\"" + l.Number + "\">" + FileCommon.EncodeLocation( l.Location, CurMech.IsQuad() ) + "</splitlocation>" + NL;
                    }
                }
            } else {
                LocationIndex l = CurMech.GetLoadout().FindIndex( p );
                retval += Prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, CurMech.IsQuad() ) + "</location>" + NL;
            }
        } else {
            Vector v = CurMech.GetLoadout().FindIndexes( p );
            if( v.size() < 1 ) {
                return "";
            }

            for( int i = 0; i < v.size(); i++ ) {
                LocationIndex l = (LocationIndex) v.get( i );
                retval += Prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, CurMech.IsQuad() ) + "</location>" + NL;
            }
        }

        return retval;
    }

    private String GetJumpJetLines( String prefix, boolean base ) {
        String retval = "";
        JumpJet[] JJThisLoadout = CurMech.GetJumpJets().GetPlacedJumps();
        JumpJet[] JJBaseLoadout = CurMech.GetLoadout().GetBaseLoadout().GetJumpJets().GetPlacedJumps();

        for( int i = 0; i < JJThisLoadout.length; i++ ) {
            if( base ) {
                LocationIndex l = CurMech.GetLoadout().FindIndex( (abPlaceable) JJThisLoadout[i] );
                retval += prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, CurMech.IsQuad() ) + "</location>" + NL;
            } else {
                boolean isbase = false;
                for( int j = 0; j < JJBaseLoadout.length; j++ ) {
                    if( JJThisLoadout[i] == JJBaseLoadout[j] ) {
                        isbase = true;
                    }
                }
                if( ! isbase ) {
                    LocationIndex l = CurMech.GetLoadout().FindIndex( (abPlaceable) JJThisLoadout[i] );
                    retval += prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, CurMech.IsQuad() ) + "</location>" + NL;
                }
            }
        }

        return retval;
    }

    private String GetHeatsinkLines( String prefix, boolean base ) {
        String retval = "";
        HeatSink[] HSThisLoadout = CurMech.GetHeatSinks().GetPlacedHeatSinks();
        HeatSink[] HSBaseLoadout = CurMech.GetLoadout().GetBaseLoadout().GetHeatSinks().GetPlacedHeatSinks();

        for( int i = 0; i < HSThisLoadout.length; i++ ) {
            if( base ) {
                LocationIndex l = CurMech.GetLoadout().FindIndex( (abPlaceable) HSThisLoadout[i] );
                retval += prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, CurMech.IsQuad() ) + "</location>" + NL;
            } else {
                boolean isbase = false;
                for( int j = 0; j < HSBaseLoadout.length; j++ ) {
                    if( HSThisLoadout[i] == HSBaseLoadout[j] ) {
                        isbase = true;
                    }
                }
                if( ! isbase ) {
                    LocationIndex l = CurMech.GetLoadout().FindIndex( (abPlaceable) HSThisLoadout[i] );
                    retval += prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, CurMech.IsQuad() ) + "</location>" + NL;
                }
            }
        }

        return retval;
    }

    private String GetEquipmentLines( String prefix ) {
        String retval = "";
        Vector v = CurMech.GetLoadout().GetNonCore();
        Vector vBase = CurMech.GetBaseLoadout().GetNonCore();
        boolean OmniLoad = false;
        if( ! CurMech.GetLoadout().GetName().equals( Constants.BASELOADOUT_NAME ) ) {
            OmniLoad = true;
        }
        for( int i = 0; i < v.size(); i++ ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( vBase.contains( p ) && OmniLoad ) {
                // the logic escapes me.  do nothing here.
            } else {
                retval += prefix + "<equipment>" + NL;
                retval += prefix + tab + "<name manufacturer=\"" + FileCommon.EncodeFluff( p.GetManufacturer() ) + "\">" + FileCommon.EncodeFluff( p.GetCritName() ) + "</name>" + NL;
                retval += prefix + tab + "<type>" + GetEquipmentType( p ) + "</type>" + NL;
                retval += GetLocationLines( prefix + tab, p );
                retval += prefix + "</equipment>" + NL;
            }
        }
        if( CurMech.HasCTCase() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetCTCase();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASE</name>" + NL;
            retval += prefix + tab + "<type>CASE</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.HasLTCase() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetLTCase();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASE</name>" + NL;
            retval += prefix + tab + "<type>CASE</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.HasRTCase() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetRTCase();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASE</name>" + NL;
            retval += prefix + tab + "<type>CASE</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasHDCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetHDCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASEII</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasCTCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetCTCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASEII</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasLTCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetLTCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASEII</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasRTCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetRTCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASEII</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasLACASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetLACaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASEII</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasRACASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetRACaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASEII</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasLLCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetLLCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASEII</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasRLCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetRLCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">CASEII</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.UsingTC() ) {
            abPlaceable p = (abPlaceable) CurMech.GetTC();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">TargetingComputer</name>" + NL;
            retval += prefix + tab + "<type>TargetingComputer</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasSupercharger() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetSupercharger();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">Supercharger</name>" + NL;
            retval += prefix + tab + "<type>Supercharger</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        return retval;
    }

    private String GetEquipmentType( abPlaceable p ) {
        if( p instanceof BallisticWeapon ) {
            return "ballistic";
        } else if( p instanceof EnergyWeapon ) {
            return "energy";
        } else if( p instanceof MissileWeapon ) {
            return "missile";
        } else if( p instanceof MGArray ) {
            return "mgarray";
        } else if( p instanceof PhysicalWeapon ) {
            return "physical";
        } else if( p instanceof Equipment ) {
            return "equipment";
        } else if( p instanceof Ammunition ) {
            return "ammunition";
        } else if( p instanceof Artillery ) {
            return "artillery";
        } else {
            return "unknown";
        }
    }

    private String GetArmoredLocations( String prefix ) {
        String retval = "";
        ifLoadout l = CurMech.GetLoadout();

        // now we'll need to go through each and every location to determine if
        // it is armored
        for( int i = 0; i < 8; i++ ) {
            abPlaceable[] test = l.GetCrits( i );
            for( int j = 0; j < test.length; j++ ) {
                if( test[j].IsArmored() ) {
                    retval += tab + prefix + "<location index=\"" + j + "\">" + FileCommon.EncodeLocation( i, CurMech.IsQuad() ) + "</location>" + NL;
                }
            }
        }

        if( ! retval.equals( "" ) ) {
            retval = prefix + "<armored_locations>" + NL + retval + prefix + "</armored_locations>" + NL;
        }
        return retval;
    }
}
