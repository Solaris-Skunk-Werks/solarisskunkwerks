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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import common.*;
import battleforce.BattleForceStats;
import components.*;

public class CVWriter {
    private CombatVehicle CurUnit;
    private String tab = "    ";
    private String NL = System.getProperty( "line.separator" );

    public CVWriter( ) {

    }

    public CVWriter( CombatVehicle v ) {
        CurUnit = v;
    }

    public void WriteXML( String filename ) throws IOException {
        //BufferedWriter FR = new BufferedWriter( new FileWriter( filename ) );
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        // beginning of an XML file:
        FR.write( "<?xml version=\"1.0\" encoding =\"UTF-8\"?>" );
        FR.newLine();

        WriteXML(FR);

        FR.close();
    }

    public void WriteXML( BufferedWriter FR ) throws IOException {
        // start parsing the mech
        FR.write( "<combatvehicle name=\"" + FileCommon.EncodeFluff( CurUnit.GetName() ) + "\" model=\"" + FileCommon.EncodeFluff( CurUnit.GetModel() ) + "\" tons=\"" + CurUnit.GetTonnage() + "\" motive=\"" + CurUnit.getCurConfig().GetMotiveLookupName() + "\" omni=\"" + FileCommon.GetBoolean( CurUnit.IsOmni() ) + "\" solaris7id=\"" + CurUnit.GetSolaris7ID() + "\" solaris7imageid=\"" + CurUnit.GetSolaris7ImageID() + "\" sswimage=\"" + CurUnit.GetSSWImage() + "\">" );
        FR.newLine();

        // version number for new files
        FR.write( tab + "<ssw_savefile_version>3</ssw_savefile_version>" );
        FR.newLine();

        // add the battle value if this is not an omnimech.  otherwise, we'll
        // add the battle value for each omni loadout.  NOTE: This value is never
        // used by SSW since the BV is dynamically calculated.  This is purely for
        // other programs that may want to use the program.
        if( ! CurUnit.IsOmni() ) {
            FR.write( tab + "<battle_value>" + CurUnit.GetCurrentBV() + "</battle_value>" );
            FR.newLine();
        }

        FR.write( tab + "<motive type=\"" + CurUnit.getCurConfig().GetMotiveLookupName() + "\" cruise=\"" + CurUnit.getCruiseMP() + "\" turret=\"" + CurUnit.GetTurretLookupName() + "\" />");
        FR.newLine();
        
        FR.write( tab + "<cost>" + CurUnit.GetTotalCost() + "</cost>" );
        FR.newLine();

        FR.write( tab + "<rules_level>" + CurUnit.GetBaseRulesLevel() + "</rules_level>" );
        FR.newLine();

        if( CurUnit.UsingFractionalAccounting() ) {
            FR.write( tab + "<fractional />" );
            FR.newLine();
        }

        FR.write( tab + "<era>" + CurUnit.GetBaseEra() + "</era>" );
        FR.newLine();
        FR.write( tab + "<productionera>" + CurUnit.GetBaseProductionEra() + "</productionera>" );
        FR.newLine();

        FR.write( tab + "<techbase manufacturer=\"" + FileCommon.EncodeFluff( CurUnit.GetCompany() ) + "\" location=\"" + FileCommon.EncodeFluff( CurUnit.GetLocation() ) + "\">" + GetBaseTechbase() + "</techbase>" );
        FR.newLine();

        FR.write( tab + "<year restricted=\"" + FileCommon.GetBoolean( CurUnit.IsYearRestricted() ) + "\">" + CurUnit.GetYear() + "</year>" );
        FR.newLine();
        
        FR.write( tab + "<structure manufacturer=\"" + FileCommon.EncodeFluff( CurUnit.GetChassisModel() ) + "\" techbase=\"" + CurUnit.GetIntStruc().GetTechBase() + "\">" );
        FR.newLine();
        FR.write( tab + tab + "<type>" + CurUnit.GetIntStruc().LookupName() + "</type>" );
        FR.newLine();
        FR.write( tab + tab + "<mods flotation=\"" + CurUnit.HasFlotationHull() + "\" "
                                    + "limitedamph=\"" + CurUnit.HasLimitedAmphibious() + "\" "
                                    + "fullamph=\"" + CurUnit.HasFullAmphibious() + "\" "
                                    + "dunebuggy=\"" + CurUnit.HasDuneBuggy() + "\" "
                                    + "enviroseal=\"" + CurUnit.HasEnvironmentalSealing() + "\" "
                                    + "trailer=\"" + CurUnit.isTrailer() + "\" />");
        FR.newLine();       
        FR.write( tab + "</structure>" );
        FR.newLine();
        
        FR.write( tab + "<engine rating=\"" + GetBaseEngineRating() + "\" manufacturer=\"" + FileCommon.EncodeFluff( CurUnit.GetEngineManufacturer() ) + "\" techbase=\"" + CurUnit.GetEngine().GetTechBase() + "\">" + CurUnit.GetEngine().LookupName() + "</engine>" );
        FR.newLine();

        FR.write( tab + "<armor manufacturer=\"" + FileCommon.EncodeFluff( CurUnit.GetArmorModel() ) + "\" techbase=\"" + CurUnit.GetArmor().GetTechBase() + "\">" );
        FR.newLine();
        FR.write( tab + tab + "<type>" + CurUnit.GetArmor().LookupName() + "</type>" );
        FR.newLine();
        if( CurUnit.GetArmor().IsPatchwork() ) {
            FR.write( tab + tab + "<front type=\"" + CurUnit.GetArmor().GetFrontArmorType().LookupName() + "\" techbase=\"" + CurUnit.GetArmor().GetFrontArmorType().GetAvailability().GetTechBase() + "\">" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_FRONT ) + "</front>" );
            FR.newLine();
            FR.write( tab + tab + "<left type=\"" + CurUnit.GetArmor().GetLeftArmorType().LookupName() + "\" techbase=\"" + CurUnit.GetArmor().GetLeftArmorType().GetAvailability().GetTechBase() + "\">" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_LEFT ) + "</left>" );
            FR.newLine();
            FR.write( tab + tab + "<right type=\"" + CurUnit.GetArmor().GetRightArmorType().LookupName() + "\" techbase=\"" + CurUnit.GetArmor().GetRightArmorType().GetAvailability().GetTechBase() + "\">" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_RIGHT ) + "</right>" );
            FR.newLine();
            FR.write( tab + tab + "<rear type=\"" + CurUnit.GetArmor().GetRearArmorType().LookupName() + "\" techbase=\"" + CurUnit.GetArmor().GetRearArmorType().GetAvailability().GetTechBase() + "\">" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_REAR ) + "</rear>" );
            FR.newLine();
            FR.write( tab + tab + "<primary type=\"" + CurUnit.GetArmor().GetTurret1ArmorType().LookupName() + "\" techbase=\"" + CurUnit.GetArmor().GetTurret1ArmorType().GetAvailability().GetTechBase() + "\">" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET1 ) + "</primaryturret>" );
            FR.newLine();
            FR.write( tab + tab + "<secondary type=\"" + CurUnit.GetArmor().GetTurret2ArmorType().LookupName() + "\" techbase=\"" + CurUnit.GetArmor().GetTurret2ArmorType().GetAvailability().GetTechBase() + "\">" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET2 ) + "</secondaryturret>" );
            FR.newLine();
            FR.write( tab + tab + "<rotor type=\"" + CurUnit.GetArmor().GetRotorArmorType().LookupName() + "\" techbase=\"" + CurUnit.GetArmor().GetRotorArmorType().GetAvailability().GetTechBase() + "\">" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_ROTOR ) + "</rotor>" );
            FR.newLine();
        } else {
            FR.write( tab + tab + "<front>" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_FRONT ) + "</front>" );
            FR.newLine();
            FR.write( tab + tab + "<left>" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_LEFT ) + "</left>" );
            FR.newLine();
            FR.write( tab + tab + "<right>" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_RIGHT ) + "</right>" );
            FR.newLine();
            FR.write( tab + tab + "<rear>" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_REAR ) + "</rear>" );
            FR.newLine();
            FR.write( tab + tab + "<primaryturret>" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET1 ) + "</primaryturret>" );
            FR.newLine();
            FR.write( tab + tab + "<secondaryturret>" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_TURRET2 ) + "</secondaryturret>" );
            FR.newLine();
            FR.write( tab + tab + "<rotor>" + CurUnit.GetArmor().GetLocationArmor( LocationIndex.CV_LOC_ROTOR ) + "</rotor>" );
            FR.newLine();
        }
        FR.write( GetLocationLines( tab + tab, CurUnit.GetArmor() ) );
        FR.write( tab + "</armor>" );
        FR.newLine();

        if( CurUnit.IsOmni() ) {
            CurUnit.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
        }
        FR.write( tab + "<baseloadout fcsa4=\"" + FileCommon.GetBoolean( CurUnit.UsingArtemisIV() ) + "\" fcsa5=\"" + FileCommon.GetBoolean( CurUnit.UsingArtemisV() ) + "\" fcsapollo=\"" + FileCommon.GetBoolean( CurUnit.UsingApollo() ) + "\" turretlimit=\"" + CurUnit.GetBaseLoadout().GetTurret().GetMaxTonnage() + "\" >" );
        FR.newLine();

        FR.write( tab + tab + "<source>" + FileCommon.EncodeFluff( CurUnit.getSource() ) + "</source>" );
        FR.newLine();

        // chat information
        FR.write( tab + tab + "<info>" + CurUnit.GetChatInfo() + "</info>" );
        FR.newLine();

        BattleForceStats stat = new BattleForceStats(CurUnit);
        stat.SerializeXML(FR, 2);
        FR.newLine();

        if( CurUnit.GetJumpJets().GetNumJJ() > 0 ) {
            FR.write( tab + tab + "<jumpjets number=\"" + CurUnit.GetJumpJets().GetNumJJ() + "\">" );
            FR.newLine();
            FR.write( tab + tab + tab + "<type>" + CurUnit.GetJumpJets().LookupName() + "</type>" );
            FR.newLine();
            FR.write( GetJumpJetLines( tab + tab + tab, true ) );
            FR.write( tab + tab + "</jumpjets>" );
            FR.newLine();
        }
        FR.write( tab + tab + "<heatsinks number=\"" + CurUnit.GetHeatSinks().GetNumHS() + "\" techbase=\"" + CurUnit.GetHeatSinks().GetTechBase() + "\">" );
        FR.newLine();
        FR.write( tab + tab + tab + "<type>" + CurUnit.GetHeatSinks().LookupName() + "</type>" );
        FR.newLine();
        FR.write( tab + tab + "</heatsinks>" );
        FR.newLine();
        if( CurUnit.HasBlueShield() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurUnit.GetBlueShield().LookupName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurUnit.GetBlueShield() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        FR.write( GetEquipmentLines( tab + tab ) );
        FR.write( tab + "</baseloadout>" );
        FR.newLine();

        if( CurUnit.IsOmni() ) {
            String curLoadout = CurUnit.GetLoadout().GetName();
            ArrayList v = CurUnit.GetLoadouts();
            for( int i = 0; i < v.size(); i++ ) {
                CurUnit.SetCurLoadout( ((ifCVLoadout) v.get( i )).GetName() );
                if( CurUnit.GetBaseRulesLevel() != CurUnit.GetLoadout().GetRulesLevel() ) {
                    FR.write( tab + "<loadout name=\"" + FileCommon.EncodeFluff( CurUnit.GetLoadout().GetName() ) + "\" ruleslevel=\"" + CurUnit.GetLoadout().GetRulesLevel() + "\" fcsa4=\"" + FileCommon.GetBoolean( CurUnit.UsingArtemisIV() ) + "\" fcsa5=\"" + FileCommon.GetBoolean( CurUnit.UsingArtemisV() ) + "\" fcsapollo=\"" + FileCommon.GetBoolean( CurUnit.UsingApollo() ) + "\">" );
                } else {
                    FR.write( tab + "<loadout name=\"" + FileCommon.EncodeFluff( CurUnit.GetLoadout().GetName() ) + "\" fcsa4=\"" + FileCommon.GetBoolean( CurUnit.UsingArtemisIV() ) + "\" fcsa5=\"" + FileCommon.GetBoolean( CurUnit.UsingArtemisV() ) + "\" fcsapollo=\"" + FileCommon.GetBoolean( CurUnit.UsingApollo() ) + "\">" );
                }
                FR.newLine();
                FR.write( tab + tab + "<source>" + FileCommon.EncodeFluff( CurUnit.getSource() ) + "</source>" );
                FR.newLine();
                FR.write( tab + tab + "<loadout_era>" + CurUnit.GetEra() + "</loadout_era>" );
                FR.newLine();
                FR.write( tab + tab + "<loadout_productionera>" + CurUnit.GetProductionEra() + "</loadout_productionera>" );
                FR.newLine();
                FR.write( tab + tab + "<loadout_year>" + CurUnit.GetYear() + "</loadout_year>" );
                FR.newLine();
                // chat information
                FR.write( tab + tab + "<info>" + CurUnit.GetChatInfo() + "</info>" );
                FR.newLine();

                stat = new BattleForceStats(CurUnit);
                stat.SerializeXML(FR, 2);
                FR.newLine();

                if( CurUnit.GetBaseTechbase() != CurUnit.GetLoadout().GetTechBase() ) {
                    FR.write( tab + tab + "<techbase>" + GetTechbase() + "</techbase>" );
                    FR.newLine();
                }

                // add in the battle value for this loadout
                FR.write( tab + tab + "<battle_value>" + CurUnit.GetCurrentBV() + "</battle_value>" );
                FR.newLine();
                FR.write( tab + tab + "<cost>" + CurUnit.GetTotalCost() + "</cost>" );
                FR.newLine();
                //FR.write( tab + tab + "<clancase>" + FileCommon.GetBoolean( CurUnit.GetLoadout().IsUsingClanCASE() ) + "</clancase>" );
                //FR.newLine();
                if( CurUnit.GetJumpJets().GetNumJJ() > CurUnit.GetJumpJets().GetBaseLoadoutNumJJ() ) {
                    FR.write( tab + tab + "<jumpjets number=\"" + CurUnit.GetJumpJets().GetNumJJ() + "\">" );
                    FR.newLine();
                    FR.write( tab + tab + tab + "<type>" + CurUnit.GetJumpJets().LookupName() + "</type>" );
                    FR.newLine();
                    FR.write( GetJumpJetLines( tab + tab + tab, false ) );
                    FR.write( tab + tab + "</jumpjets>" );
                    FR.newLine();
                }
                if( CurUnit.GetLoadout().GetHeatSinks().GetNumHS() > CurUnit.GetLoadout().GetHeatSinks().GetBaseLoadoutNumHS() ) {
                    FR.write( tab + tab + "<heatsinks number=\"" + CurUnit.GetHeatSinks().GetNumHS() + "\" techbase=\"" + CurUnit.GetHeatSinks().GetTechBase() + "\">" );
                    FR.newLine();
                    FR.write( tab + tab + tab + "<type>" + CurUnit.GetHeatSinks().LookupName() + "</type>" );
                    FR.newLine();
                    FR.write( tab + tab + "</heatsinks>" );
                    FR.newLine();
                }
                FR.write( GetEquipmentLines( tab + tab ) );
                if( CurUnit.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
                    // check for armored components
                    FR.write( GetArmoredLocations( tab + tab ) );
                }
                FR.write( tab + "</loadout>" );
                FR.newLine();
            }
            CurUnit.SetCurLoadout(curLoadout);
        }

        FR.write( tab + "<fluff>" );
        FR.newLine();
        FR.write( tab + tab + "<overview>" + FileCommon.EncodeFluff( CurUnit.getOverview() ) + "</overview>" );
        FR.newLine();
        FR.write( tab + tab + "<capabilities>" + FileCommon.EncodeFluff( CurUnit.getCapabilities() ) + "</capabilities>" );
        FR.newLine();
        FR.write( tab + tab + "<battlehistory>" + FileCommon.EncodeFluff( CurUnit.getHistory() ) + "</battlehistory>" );
        FR.newLine();
        FR.write( tab + tab + "<deployment>" + FileCommon.EncodeFluff( CurUnit.getDeployment() ) + "</deployment>" );
        FR.newLine();
        FR.write( tab + tab + "<variants>" + FileCommon.EncodeFluff( CurUnit.getVariants() ) + "</variants>" );
        FR.newLine();
        FR.write( tab + tab + "<notables>" + FileCommon.EncodeFluff( CurUnit.getNotables() ) + "</notables>" );
        FR.newLine();
        FR.write( tab + tab + "<additional>" + FileCommon.EncodeFluff( CurUnit.GetAdditional() ) + "</additional>" );
        FR.newLine();
        FR.write( tab + tab + "<jumpjet_model>" + FileCommon.EncodeFluff( CurUnit.GetJJModel() ) + "</jumpjet_model>" );
        FR.newLine();
        FR.write( tab + tab + "<commsystem>" + FileCommon.EncodeFluff( CurUnit.GetCommSystem() ) + "</commsystem>" );
        FR.newLine();
        FR.write( tab + tab + "<tandtsystem>" + FileCommon.EncodeFluff( CurUnit.GetTandTSystem() ) + "</tandtsystem>" );
        FR.newLine();
        FR.write( tab + "</fluff>" );
        FR.newLine();

        FR.write( "</combatvehicle>" );
        FR.newLine();
    }

    public CombatVehicle getUnit() {
        return CurUnit;
    }

    public void setUnit( CombatVehicle m ) {
        this.CurUnit = m;
    }

    private String GetBaseTechbase() {
        return AvailableCode.TechBaseSTR[CurUnit.GetBaseTechbase()];
    }

    private String GetTechbase() {
        return AvailableCode.TechBaseSTR[CurUnit.GetTechbase()];
    }

    private String GetLocationLines( String Prefix, abPlaceable p ) {
        String retval = "";
        ArrayList v = CurUnit.GetLoadout().FindIndexes( p );
        if( v.size() < 1 ) {
            return "";
        }

        for( int i = 0; i < v.size(); i++ ) {
            LocationIndex l = (LocationIndex) v.get( i );
            retval += Prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, false ) + "</location>" + NL;
        }

        return retval;
    }

    private String GetJumpJetLines( String prefix, boolean base ) {
        String retval = "";
        JumpJet[] JJThisLoadout = CurUnit.GetJumpJets().GetPlacedJumps();
        JumpJet[] JJBaseLoadout = CurUnit.GetLoadout().GetBaseLoadout().GetJumpJets().GetPlacedJumps();

        for( int i = 0; i < JJThisLoadout.length; i++ ) {
            if( base ) {
                LocationIndex l = CurUnit.GetLoadout().FindIndex( (abPlaceable) JJThisLoadout[i] );
                retval += prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, false ) + "</location>" + NL;
            } else {
                boolean isbase = false;
                for( int j = 0; j < JJBaseLoadout.length; j++ ) {
                    if( JJThisLoadout[i] == JJBaseLoadout[j] ) {
                        isbase = true;
                    }
                }
                if( ! isbase ) {
                    LocationIndex l = CurUnit.GetLoadout().FindIndex( (abPlaceable) JJThisLoadout[i] );
                    retval += prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, false ) + "</location>" + NL;
                }
            }
        }

        return retval;
    }

    private String GetHeatsinkLines( String prefix, boolean base ) {
        String retval = "";
        HeatSink[] HSThisLoadout = CurUnit.GetHeatSinks().GetPlacedHeatSinks();
        HeatSink[] HSBaseLoadout = CurUnit.GetLoadout().GetBaseLoadout().GetHeatSinks().GetPlacedHeatSinks();

        for( int i = 0; i < HSThisLoadout.length; i++ ) {
            if( base ) {
                LocationIndex l = CurUnit.GetLoadout().FindIndex( (abPlaceable) HSThisLoadout[i] );
                retval += prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, false ) + "</location>" + NL;
            } else {
                boolean isbase = false;
                for( int j = 0; j < HSBaseLoadout.length; j++ ) {
                    if( HSThisLoadout[i] == HSBaseLoadout[j] ) {
                        isbase = true;
                    }
                }
                if( ! isbase ) {
                    LocationIndex l = CurUnit.GetLoadout().FindIndex( (abPlaceable) HSThisLoadout[i] );
                    retval += prefix + "<location index=\"" + l.Index + "\">" + FileCommon.EncodeLocation( l.Location, false ) + "</location>" + NL;
                }
            }
        }

        return retval;
    }

    private String GetEquipmentLines( String prefix ) {
        String retval = "";
        ArrayList v = CurUnit.GetLoadout().GetNonCore();
        ArrayList vBase = CurUnit.GetBaseLoadout().GetNonCore();
        boolean OmniLoad = false;
        if( ! CurUnit.GetLoadout().GetName().equals( Constants.BASELOADOUT_NAME ) ) {
            OmniLoad = true;
        }
        for( int i = 0; i < v.size(); i++ ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( vBase.contains( p ) && OmniLoad ) {
                // the logic escapes me.  do nothing here.
            } else {
                retval += prefix + "<equipment>" + NL;
                retval += prefix + tab + "<name manufacturer=\"" + FileCommon.EncodeFluff( p.GetManufacturer() ) + "\">" + FileCommon.EncodeFluff( p.LookupName() ) + "</name>" + NL;
                retval += prefix + tab + "<type>" + GetEquipmentType( p ) + "</type>" + NL;
                retval += prefix + tab + "<location>" + FileCommon.EncodeLocation(CurUnit.GetLoadout().Find(p), false, CurUnit) + "</location>" + NL;
                if( p instanceof VehicularGrenadeLauncher ) {
                    retval += prefix + tab + "<vglarc>" + ((VehicularGrenadeLauncher) p).GetCurrentArc() + "</vglarc>" + NL;
                    retval += prefix + tab + "<vglammo>" + ((VehicularGrenadeLauncher) p).GetAmmoType() + "</vglammo>" + NL;
                }
                if( p instanceof Equipment ) {
                    if( ((Equipment) p).IsVariableSize() ) {
                        retval += prefix + tab + "<tons>" + ((Equipment)p).GetTonnage(false) + "</tons>" + NL;
                    }
                }
                if( ( p instanceof Ammunition ) && CurUnit.UsingFractionalAccounting() ) {
                    if( ((Ammunition) p).OddLotSize() ) {
                        retval += prefix + tab + "<lot>" + ((Ammunition) p).GetLotSize() + "</lot>" + NL;
                    }
                }
                retval += GetLocationLines( prefix + tab, p );
                retval += prefix + "</equipment>" + NL;
            }
        }
        /*
        if( CurUnit.UsingTC() ) {
            abPlaceable p = (abPlaceable) CurUnit.GetTC();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>TargetingComputer</type>" + NL;
            retval += prefix + tab + "<location>Body</location>" + NL;
            retval += prefix + "</equipment>" + NL;
        }
        */
        if( CurUnit.GetLoadout().HasSupercharger() ) {
            abPlaceable p = (abPlaceable) CurUnit.GetLoadout().GetSupercharger();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">Supercharger</name>" + NL;
            retval += prefix + tab + "<type>Supercharger</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        return retval;
    }

    private String GetEquipmentType( abPlaceable p ) {
        if( p instanceof RangedWeapon ) {
            switch( ((RangedWeapon) p).GetWeaponClass() ) {
                case RangedWeapon.W_BALLISTIC:
                    return "ballistic";
                case RangedWeapon.W_ENERGY:
                    return "energy";
                case RangedWeapon.W_MISSILE:
                    return "missile";
                case RangedWeapon.W_ARTILLERY:
                    return "artillery";
                default:
                    return "unknown";
            }
        } else if( p instanceof VehicularGrenadeLauncher ) {
            return "artillery";
        } else if( p instanceof MGArray ) {
            return "mgarray";
        } else if( p instanceof PhysicalWeapon ) {
            return "physical";
        } else if( p instanceof Equipment ) {
            return "equipment";
        } else if( p instanceof Ammunition ) {
            return "ammunition";
        } else if( p instanceof TargetingComputer ) {
            return "TargetingComputer";
        } else if ( p instanceof CASE ) {
            return "CASE";
        } else {
            return "miscellaneous";
        }
    }

    private String GetArmoredLocations( String prefix ) {
        String retval = "";
        return retval;
    }

    private int GetBaseEngineRating() {
        // provided for primitive 'Mechs.  Since the engine rating is handled by
        // the 'Mech and engine, we need to know what the base is.
        return CurUnit.getCruiseMP() * CurUnit.GetTonnage();
    }
}
