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

public class MechWriter {
    private Mech CurMech;
    private String tab = "    ";
    private String NL = System.getProperty( "line.separator" );

    public MechWriter( ) {

    }

    public MechWriter( Mech m ) {
        CurMech = m;
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
        FR.write( "<mech name=\"" + FileCommon.EncodeFluff( CurMech.GetName() ) + "\" model=\"" + FileCommon.EncodeFluff( CurMech.GetModel() ) + "\" tons=\"" + CurMech.GetTonnage() + "\" omnimech=\"" + FileCommon.GetBoolean( CurMech.IsOmnimech() ) + "\" solaris7id=\"" + CurMech.GetSolaris7ID() + "\" solaris7imageid=\"" + CurMech.GetSolaris7ImageID() + "\" sswimage=\"" + CurMech.GetSSWImage() + "\">" );
        FR.newLine();

        // version number for new files
        FR.write( tab + "<ssw_savefile_version>3</ssw_savefile_version>" );
        FR.newLine();

        // add the battle value if this is not an omnimech.  otherwise, we'll
        // add the battle value for each omni loadout.  NOTE: This value is never
        // used by SSW since the BV is dynamically calculated.  This is purely for
        // other programs that may want to use the program.
        if( ! CurMech.IsOmnimech() ) {
            FR.write( tab + "<battle_value>" + CurMech.GetCurrentBV() + "</battle_value>" );
            FR.newLine();
        }

        FR.write( tab + "<cost>" + CurMech.GetTotalCost() + "</cost>" );
        FR.newLine();

        FR.write( tab + "<rules_level>" + CurMech.GetBaseRulesLevel() + "</rules_level>" );
        FR.newLine();

        if( CurMech.UsingFractionalAccounting() ) {
            FR.write( tab + "<fractional />" );
            FR.newLine();
        }

        FR.write( tab + "<era>" + CurMech.GetBaseEra() + "</era>" );
        FR.newLine();
        FR.write( tab + "<productionera>" + CurMech.GetBaseProductionEra() + "</productionera>" );
        FR.newLine();
/*
        FR.write( tab + "<source>" + FileCommon.EncodeFluff( CurMech.GetSource() ) + "</source>" );
        FR.newLine();
*/
        if( CurMech.IsIndustrialmech() ) {
            if( CurMech.IsPrimitive() ) {
                FR.write( tab + "<mech_type>PrimitiveIndustrialMech</mech_type>" );
            } else {
                FR.write( tab + "<mech_type>IndustrialMech</mech_type>" );
            }
        } else {
            if( CurMech.IsPrimitive() ) {
                FR.write( tab + "<mech_type>PrimitiveBattleMech</mech_type>" );
            } else {
                FR.write( tab + "<mech_type>BattleMech</mech_type>" );
            }
        }
        FR.newLine();

        FR.write( tab + "<techbase manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetCompany() ) + "\" location=\"" + FileCommon.EncodeFluff( CurMech.GetLocation() ) + "\">" + GetBaseTechbase() + "</techbase>" );
        FR.newLine();

        FR.write( tab + "<year restricted=\"" + FileCommon.GetBoolean( CurMech.IsYearRestricted() ) + "\">" + CurMech.GetBaseYear() + "</year>" );
        FR.newLine();

        FR.write( tab + "<motive_type>" + GetMotiveType() + "</motive_type>" );
        FR.newLine();

        FR.write( tab + "<structure manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetChassisModel() ) + "\" techbase=\"" + CurMech.GetIntStruc().GetTechBase() + "\">" );
        FR.newLine();
        FR.write( tab + tab + "<type>" + CurMech.GetIntStruc().LookupName() + "</type>" );
        FR.newLine();
        if( CurMech.GetIntStruc().NumCrits() > 0 ) {
            FR.write( GetLocationLines( tab + tab, CurMech.GetIntStruc() ) );
        }
        FR.write( tab + "</structure>" );
        FR.newLine();

        ArrayList engineLocs = CurMech.GetLoadout().FindIndexes( CurMech.GetEngine() );
        LocationIndex ls = new LocationIndex();
        LocationIndex rs = new LocationIndex();
        ls.Location = LocationIndex.MECH_LOC_LT;
        ls.Index = 12;
        rs.Location = LocationIndex.MECH_LOC_RT;
        rs.Index = 12;
        for( int i = 0; i < engineLocs.size(); i++ ) {
            ls.SetFirst( ((LocationIndex) engineLocs.get( i )) );
            rs.SetFirst( ((LocationIndex) engineLocs.get( i )) );
        }
        if( ls.Index == 12 ) { ls.Index = -1; }
        if( rs.Index == 12 ) { rs.Index = -1; }
//        FR.write( tab + "<engine rating=\"" + CurMech.GetEngine().GetRating() + "\" manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetEngineManufacturer() ) + "\" lsstart=\"" + ls.Index + "\" rsstart=\"" + rs.Index + "\" techbase=\"" + CurMech.GetEngine().GetTechbase() + "\">" + CurMech.GetEngine().GetLookupName() + "</engine>" );
        FR.write( tab + "<engine rating=\"" + GetBaseEngineRating() + "\" manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetEngineManufacturer() ) + "\" lsstart=\"" + ls.Index + "\" rsstart=\"" + rs.Index + "\" techbase=\"" + CurMech.GetEngine().GetTechBase() + "\">" + CurMech.GetEngine().LookupName() + "</engine>" );
        FR.newLine();

        FR.write( tab + "<gyro techbase=\"" + CurMech.GetGyro().GetTechBase() + "\">" + CurMech.GetGyro().LookupName() + "</gyro>" );
        FR.newLine();

        FR.write( tab + "<cockpit>" );
        FR.newLine();
        if( CurMech.GetCockpit().IsTorsoMounted() ) {
            // need to check locations for this.
            FR.write( tab + tab + "<type>" + CurMech.GetCockpit().LookupName() + "</type>" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab, CurMech.GetCockpit() ) );
            FR.write( GetLocationLines( tab + tab, CurMech.GetCockpit().GetThirdSensors() ) );
            FR.write( GetLocationLines( tab + tab, CurMech.GetCockpit().GetFirstLS() ) );
            FR.write( GetLocationLines( tab + tab, CurMech.GetCockpit().GetSecondLS() ) );
        } else {
            FR.write( tab + tab + "<type ejectionseat=\"" + FileCommon.GetBoolean( CurMech.HasEjectionSeat() ) + "\" commandconsole=\"" + FileCommon.GetBoolean( CurMech.HasCommandConsole() ) + "\" fhes=\"" + FileCommon.GetBoolean( CurMech.HasFHES() ) + "\">" + CurMech.GetCockpit().LookupName() + "</type>" );
            FR.newLine();
        }
        FR.write( tab + "</cockpit>" );
        FR.newLine();

        if( CurMech.GetPhysEnhance().IsTSM() || CurMech.GetPhysEnhance().IsMASC() ) {
            FR.write( tab + "<enhancement techbase=\"" + CurMech.GetPhysEnhance().GetTechBase() + "\">" );
            FR.newLine();
            FR.write( tab + tab + "<type>" + CurMech.GetPhysEnhance().LookupName() + "</type>" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab, CurMech.GetPhysEnhance() ) );
            FR.write( tab + "</enhancement>" );
            FR.newLine();
        }

        FR.write( tab + "<armor manufacturer=\"" + FileCommon.EncodeFluff( CurMech.GetArmorModel() ) + "\" techbase=\"" + CurMech.GetArmor().GetTechBase() + "\">" );
        FR.newLine();
        FR.write( tab + tab + "<type>" + CurMech.GetArmor().LookupName() + "</type>" );
        FR.newLine();
        if( CurMech.GetArmor().IsPatchwork() ) {
            FR.write( tab + tab + "<hd type=\"" + CurMech.GetArmor().GetHDArmorType().LookupName() + "\" techbase=\"" + CurMech.GetArmor().GetHDArmorType().GetAvailability().GetTechBase() + "\">" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) + "</hd>" );
            FR.newLine();
            FR.write( tab + tab + "<ct type=\"" + CurMech.GetArmor().GetCTArmorType().LookupName() + "\" techbase=\"" + CurMech.GetArmor().GetCTArmorType().GetAvailability().GetTechBase() + "\">" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) + "</ct>" );
            FR.newLine();
            FR.write( tab + tab + "<ctr>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CTR ) + "</ctr>" );
            FR.newLine();
            FR.write( tab + tab + "<lt type=\"" + CurMech.GetArmor().GetLTArmorType().LookupName() + "\" techbase=\"" + CurMech.GetArmor().GetLTArmorType().GetAvailability().GetTechBase() + "\">" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) + "</lt>" );
            FR.newLine();
            FR.write( tab + tab + "<ltr>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) + "</ltr>" );
            FR.newLine();
            FR.write( tab + tab + "<rt type=\"" + CurMech.GetArmor().GetRTArmorType().LookupName() + "\" techbase=\"" + CurMech.GetArmor().GetRTArmorType().GetAvailability().GetTechBase() + "\">" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) + "</rt>" );
            FR.newLine();
            FR.write( tab + tab + "<rtr>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) + "</rtr>" );
            FR.newLine();
            FR.write( tab + tab + "<la type=\"" + CurMech.GetArmor().GetLAArmorType().LookupName() + "\" techbase=\"" + CurMech.GetArmor().GetLAArmorType().GetAvailability().GetTechBase() + "\">" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) + "</la>" );
            FR.newLine();
            FR.write( tab + tab + "<ra type=\"" + CurMech.GetArmor().GetRAArmorType().LookupName() + "\" techbase=\"" + CurMech.GetArmor().GetRAArmorType().GetAvailability().GetTechBase() + "\">" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) + "</ra>" );
            FR.newLine();
            FR.write( tab + tab + "<ll type=\"" + CurMech.GetArmor().GetLLArmorType().LookupName() + "\" techbase=\"" + CurMech.GetArmor().GetLLArmorType().GetAvailability().GetTechBase() + "\">" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) + "</ll>" );
            FR.newLine();
            FR.write( tab + tab + "<rl type=\"" + CurMech.GetArmor().GetRLArmorType().LookupName() + "\" techbase=\"" + CurMech.GetArmor().GetRLArmorType().GetAvailability().GetTechBase() + "\">" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) + "</rl>" );
            FR.newLine();
        } else {
            FR.write( tab + tab + "<hd>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) + "</hd>" );
            FR.newLine();
            FR.write( tab + tab + "<ct>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) + "</ct>" );
            FR.newLine();
            FR.write( tab + tab + "<ctr>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CTR ) + "</ctr>" );
            FR.newLine();
            FR.write( tab + tab + "<lt>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) + "</lt>" );
            FR.newLine();
            FR.write( tab + tab + "<ltr>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) + "</ltr>" );
            FR.newLine();
            FR.write( tab + tab + "<rt>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) + "</rt>" );
            FR.newLine();
            FR.write( tab + tab + "<rtr>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) + "</rtr>" );
            FR.newLine();
            FR.write( tab + tab + "<la>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) + "</la>" );
            FR.newLine();
            FR.write( tab + tab + "<ra>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) + "</ra>" );
            FR.newLine();
            FR.write( tab + tab + "<ll>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) + "</ll>" );
            FR.newLine();
            FR.write( tab + tab + "<rl>" + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) + "</rl>" );
            FR.newLine();
        }
        FR.write( GetLocationLines( tab + tab, CurMech.GetArmor() ) );
        FR.write( tab + "</armor>" );
        FR.newLine();

        if( CurMech.IsOmnimech() ) {
            CurMech.SetCurLoadout( common.Constants.BASELOADOUT_NAME );
        }
        FR.write( tab + "<baseloadout fcsa4=\"" + FileCommon.GetBoolean( CurMech.UsingArtemisIV() ) + "\" fcsa5=\"" + FileCommon.GetBoolean( CurMech.UsingArtemisV() ) + "\" fcsapollo=\"" + FileCommon.GetBoolean( CurMech.UsingApollo() ) + "\">" );
        FR.newLine();

        FR.write( tab + tab + "<source>" + FileCommon.EncodeFluff( CurMech.GetSource() ) + "</source>" );
        FR.newLine();

        // chat information
        FR.write( tab + tab + "<info>" + CurMech.GetChatInfo() + "</info>" );
        FR.newLine();

        BattleForceStats stat = new BattleForceStats(CurMech);
        stat.SerializeXML(FR, 2);
        FR.newLine();

        FR.write( tab + tab + "<actuators lla=\"" + FileCommon.GetBoolean( CurMech.GetActuators().LeftLowerInstalled() ) + "\" lh=\"" + FileCommon.GetBoolean( CurMech.GetActuators().LeftHandInstalled() ) + "\" rla=\"" + FileCommon.GetBoolean( CurMech.GetActuators().RightLowerInstalled() ) + "\" rh=\"" + FileCommon.GetBoolean( CurMech.GetActuators().RightHandInstalled() ) + "\"/>" );
        FR.newLine();
        FR.write( tab + tab + "<clancase>" + FileCommon.GetBoolean( CurMech.GetLoadout().IsUsingClanCASE() ) + "</clancase>" );
        FR.newLine();
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            FR.write( tab + tab + "<jumpjets number=\"" + CurMech.GetJumpJets().GetNumJJ() + "\">" );
            FR.newLine();
            FR.write( tab + tab + tab + "<type>" + CurMech.GetJumpJets().LookupName() + "</type>" );
            FR.newLine();
            FR.write( GetJumpJetLines( tab + tab + tab, true ) );
            FR.write( tab + tab + "</jumpjets>" );
            FR.newLine();
        }
        FR.write( tab + tab + "<heatsinks number=\"" + CurMech.GetHeatSinks().GetNumHS() + "\" techbase=\"" + CurMech.GetHeatSinks().GetTechBase() + "\">" );
        FR.newLine();
        FR.write( tab + tab + tab + "<type>" + CurMech.GetHeatSinks().LookupName() + "</type>" );
        FR.newLine();
        FR.newLine();
        FR.write( GetHeatsinkLines( tab + tab + tab, true ) );
        FR.write( tab + tab + "</heatsinks>" );
        FR.newLine();
        if( CurMech.HasNullSig() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetNullSig().LookupName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetNullSig() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasVoidSig() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetVoidSig().LookupName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetVoidSig() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasBlueShield() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetBlueShield().LookupName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetBlueShield() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasChameleon() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetChameleon().LookupName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetChameleon() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasEnviroSealing() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetEnviroSealing().LookupName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetEnviroSealing() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.HasTracks() ) {
            // this can only ever go in the base loadout, so we'll save it here
            FR.write( tab + tab + "<multislot name=\"" + CurMech.GetTracks().LookupName() + "\">" );
            FR.newLine();
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetTracks() ) );
            FR.write( tab + tab + "</multislot>" );
            FR.newLine();
        }
        if( CurMech.UsingPartialWing() ) {
            // this can only ever go in the base loadout, so we'll save it here
            ArrayList pwlocs = CurMech.GetLoadout().FindIndexes( CurMech.GetPartialWing() );
            ls = new LocationIndex();
            rs = new LocationIndex();
            ls.Location = LocationIndex.MECH_LOC_LT;
            ls.Index = 12;
            rs.Location = LocationIndex.MECH_LOC_RT;
            rs.Index = 12;
            for( int i = 0; i < pwlocs.size(); i++ ) {
                ls.SetFirst( ((LocationIndex) pwlocs.get( i )) );
                rs.SetFirst( ((LocationIndex) pwlocs.get( i )) );
            }
            if( ls.Index == 12 ) { ls.Index = -1; }
            if( rs.Index == 12 ) { rs.Index = -1; }
            FR.write( tab + tab + "<partialwing tech=\"" + ( CurMech.GetPartialWing().IsClan() ? AvailableCode.TECH_CLAN : AvailableCode.TECH_INNER_SPHERE ) + "\" lsstart=\"" + ls.Index + "\" rsstart=\"" + rs.Index + "\" />" );
            FR.newLine();
        }
        if( CurMech.UsingJumpBooster() ) {
            // only in the base loadout so far.  We'll find out otherwise later
            FR.write( tab + tab + "<jumpbooster mp=\"" + CurMech.GetJumpBoosterMP() + "\" />" );
            FR.newLine();
        }
        if( CurMech.HasLegAES() ) {
            FR.write( tab + tab + "<leg_aes>" );
            FR.newLine();
            if( CurMech.IsQuad() ) {
                FR.write( GetLocationLines( tab + tab + tab, CurMech.GetFLLAES() ) );
                FR.write( GetLocationLines( tab + tab + tab, CurMech.GetFRLAES() ) );
            }
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetLLAES() ) );
            FR.write( GetLocationLines( tab + tab + tab, CurMech.GetRLAES() ) );
            FR.write( tab + tab + "</leg_aes>" );
            FR.newLine();
        }
        if( CurMech.HasLAAES() ) {
            FR.write( tab + tab + "<arm_aes location=\"" + FileCommon.EncodeLocation( LocationIndex.MECH_LOC_LA, CurMech.IsQuad() ) + "\" index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetLAAES() ).Index + "\"/>" );
            FR.newLine();
        }
        if( CurMech.HasRAAES() ) {
            FR.write( tab + tab + "<arm_aes location=\"" + FileCommon.EncodeLocation( LocationIndex.MECH_LOC_RA, CurMech.IsQuad() ) + "\" index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetRAAES() ).Index + "\"/>" );
            FR.newLine();
        }
        if( CurMech.GetBaseLoadout().HasHDTurret() ) {
            FR.write( tab + tab + "<turret type=\"head\" index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetBaseLoadout().GetHDTurret() ).Index + "\"/>" );
            FR.newLine();
        }
        if( CurMech.GetBaseLoadout().HasLTTurret() ) {
            FR.write( tab + tab + "<turret type=\"left torso\" index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetBaseLoadout().GetLTTurret() ).Index + "\"/>" );
            FR.newLine();
        }
        if( CurMech.GetBaseLoadout().HasRTTurret() ) {
            FR.write( tab + tab + "<turret type=\"right torso\" index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetBaseLoadout().GetRTTurret() ).Index + "\"/>" );
            FR.newLine();
        }
        if ( CurMech.GetBaseLoadout().HasBoobyTrap() ) {
            FR.write( tab + tab + "<boobytrap index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetBaseLoadout().GetBoobyTrap() ).Index + "\"/>" );
            FR.newLine();
        }
        FR.write( GetEquipmentLines( tab + tab ) );
        if( CurMech.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            // check for armored components
            FR.write( GetArmoredLocations( tab + tab ) );
        }
        FR.write( tab + "</baseloadout>" );
        FR.newLine();

        if( CurMech.IsOmnimech() ) {
            String curLoadout = CurMech.GetLoadout().GetName();
            ArrayList v = CurMech.GetLoadouts();
            for( int i = 0; i < v.size(); i++ ) {
                CurMech.SetCurLoadout( ((ifMechLoadout) v.get( i )).GetName() );
                if( CurMech.GetBaseRulesLevel() != CurMech.GetLoadout().GetRulesLevel() ) {
                    FR.write( tab + "<loadout name=\"" + FileCommon.EncodeFluff( CurMech.GetLoadout().GetName() ) + "\" ruleslevel=\"" + CurMech.GetLoadout().GetRulesLevel() + "\" fcsa4=\"" + FileCommon.GetBoolean( CurMech.UsingArtemisIV() ) + "\" fcsa5=\"" + FileCommon.GetBoolean( CurMech.UsingArtemisV() ) + "\" fcsapollo=\"" + FileCommon.GetBoolean( CurMech.UsingApollo() ) + "\">" );
                } else {
                    FR.write( tab + "<loadout name=\"" + FileCommon.EncodeFluff( CurMech.GetLoadout().GetName() ) + "\" fcsa4=\"" + FileCommon.GetBoolean( CurMech.UsingArtemisIV() ) + "\" fcsa5=\"" + FileCommon.GetBoolean( CurMech.UsingArtemisV() ) + "\" fcsapollo=\"" + FileCommon.GetBoolean( CurMech.UsingApollo() ) + "\">" );
                }
                FR.newLine();
                FR.write( tab + tab + "<source>" + FileCommon.EncodeFluff( CurMech.GetSource() ) + "</source>" );
                FR.newLine();
                FR.write( tab + tab + "<loadout_era>" + CurMech.GetEra() + "</loadout_era>" );
                FR.newLine();
                FR.write( tab + tab + "<loadout_productionera>" + CurMech.GetProductionEra() + "</loadout_productionera>" );
                FR.newLine();
                FR.write( tab + tab + "<loadout_year>" + CurMech.GetYear() + "</loadout_year>" );
                FR.newLine();
                // chat information
                FR.write( tab + tab + "<info>" + CurMech.GetChatInfo() + "</info>" );
                FR.newLine();

                stat = new BattleForceStats(CurMech);
                stat.SerializeXML(FR, 2);
                FR.newLine();

                if( CurMech.GetBaseTechbase() != CurMech.GetLoadout().GetTechBase() ) {
                    FR.write( tab + tab + "<techbase>" + GetTechbase() + "</techbase>" );
                    FR.newLine();
                }

                // add in the battle value for this loadout
                FR.write( tab + tab + "<battle_value>" + CurMech.GetCurrentBV() + "</battle_value>" );
                FR.newLine();
                FR.write( tab + tab + "<cost>" + CurMech.GetTotalCost() + "</cost>" );
                FR.newLine();
                FR.write( tab + tab + "<actuators lla=\"" + FileCommon.GetBoolean( CurMech.GetActuators().LeftLowerInstalled() ) + "\" lh=\"" + FileCommon.GetBoolean( CurMech.GetActuators().LeftHandInstalled() ) + "\" rla=\"" + FileCommon.GetBoolean( CurMech.GetActuators().RightLowerInstalled() ) + "\" rh=\"" + FileCommon.GetBoolean( CurMech.GetActuators().RightHandInstalled() ) + "\"/>" );
                FR.newLine();
                FR.write( tab + tab + "<clancase>" + FileCommon.GetBoolean( CurMech.GetLoadout().IsUsingClanCASE() ) + "</clancase>" );
                FR.newLine();
                if( CurMech.GetJumpJets().GetNumJJ() > CurMech.GetJumpJets().GetBaseLoadoutNumJJ() ) {
                    FR.write( tab + tab + "<jumpjets number=\"" + CurMech.GetJumpJets().GetNumJJ() + "\">" );
                    FR.newLine();
                    FR.write( tab + tab + tab + "<type>" + CurMech.GetJumpJets().LookupName() + "</type>" );
                    FR.newLine();
                    FR.write( GetJumpJetLines( tab + tab + tab, false ) );
                    FR.write( tab + tab + "</jumpjets>" );
                    FR.newLine();
                }
                if( CurMech.GetLoadout().GetHeatSinks().GetNumHS() > CurMech.GetLoadout().GetHeatSinks().GetBaseLoadoutNumHS() ) {
                    FR.write( tab + tab + "<heatsinks number=\"" + CurMech.GetHeatSinks().GetNumHS() + "\" techbase=\"" + CurMech.GetHeatSinks().GetTechBase() + "\">" );
                    FR.newLine();
                    FR.write( tab + tab + tab + "<type>" + CurMech.GetHeatSinks().LookupName() + "</type>" );
                    FR.newLine();
                    FR.write( GetHeatsinkLines( tab + tab + tab, false ) );
                    FR.write( tab + tab + "</heatsinks>" );
                    FR.newLine();
                }
                if( CurMech.GetLoadout().HasHDTurret() &! CurMech.GetBaseLoadout().HasHDTurret() ) {
                    FR.write( tab + tab + "<turret type=\"head\" index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetLoadout().GetHDTurret() ).Index + "\"/>" );
                    FR.newLine();
                }
                if( CurMech.GetLoadout().HasLTTurret() &! CurMech.GetBaseLoadout().HasLTTurret() ) {
                    FR.write( tab + tab + "<turret type=\"left torso\" index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetLoadout().GetLTTurret() ).Index + "\"/>" );
                    FR.newLine();
                }
                if( CurMech.GetLoadout().HasRTTurret() &! CurMech.GetBaseLoadout().HasRTTurret() ) {
                    FR.write( tab + tab + "<turret type=\"right torso\" index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetLoadout().GetRTTurret() ).Index + "\"/>" );
                    FR.newLine();
                }
                if ( CurMech.GetLoadout().HasBoobyTrap() ) {
                    FR.write( tab + tab + "<boobytrap index=\"" + CurMech.GetLoadout().FindIndex( CurMech.GetLoadout().GetBoobyTrap() ).Index + "\"/>" );
                    FR.newLine();
                }
                FR.write( GetEquipmentLines( tab + tab ) );
                if( CurMech.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
                    // check for armored components
                    FR.write( GetArmoredLocations( tab + tab ) );
                }
                FR.write( tab + "</loadout>" );
                FR.newLine();
            }
            CurMech.SetCurLoadout(curLoadout);
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
    }

    public Mech getMech() {
        return CurMech;
    }

    public void setMech( Mech m ) {
        this.CurMech = m;
    }

    private String GetBaseTechbase() {
        return AvailableCode.TechBaseSTR[CurMech.GetBaseTechbase()];
    }

    private String GetTechbase() {
        return AvailableCode.TechBaseSTR[CurMech.GetTechbase()];
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
                ArrayList v = CurMech.GetLoadout().FindSplitIndex( p );
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
            ArrayList v = CurMech.GetLoadout().FindIndexes( p );
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
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        ArrayList vBase = CurMech.GetBaseLoadout().GetNonCore();
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
                retval += prefix + tab + "<name manufacturer=\"" + FileCommon.EncodeFluff( p.GetManufacturer() ) + "\">" + FileCommon.EncodeFluff( p.LookupName() ) + "</name>" + NL;
                retval += prefix + tab + "<type>" + GetEquipmentType( p ) + "</type>" + NL;
                if( p instanceof VehicularGrenadeLauncher ) {
                    retval += prefix + tab + "<vglarc>" + ((VehicularGrenadeLauncher) p).GetCurrentArc() + "</vglarc>" + NL;
                    retval += prefix + tab + "<vglammo>" + ((VehicularGrenadeLauncher) p).GetAmmoType() + "</vglammo>" + NL;
                }
                if( p instanceof Equipment ) {
                    if( ((Equipment) p).IsVariableSize() ) {
                        retval += prefix + tab + "<tons>" + ((Equipment)p).GetTonnage(false) + "</tons>" + NL;
                    }
                }
                if( ( p instanceof Ammunition ) && CurMech.UsingFractionalAccounting() ) {
                    if( ((Ammunition) p).OddLotSize() ) {
                        retval += prefix + tab + "<lot>" + ((Ammunition) p).GetLotSize() + "</lot>" + NL;
                    }
                }
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
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasCTCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetCTCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasLTCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetLTCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasRTCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetRTCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasLACASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetLACaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasRACASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetRACaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasLLCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetLLCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.GetLoadout().HasRLCASEII() ) {
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetRLCaseII();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
            retval += prefix + tab + "<type>CASEII</type>" + NL;
            retval += GetLocationLines( prefix + tab, p );
            retval += prefix + "</equipment>" + NL;
        }
        if( CurMech.UsingTC() && CurMech.GetTC().NumCrits() > 0 ) {
            abPlaceable p = (abPlaceable) CurMech.GetTC();
            retval += prefix + "<equipment>" + NL;
            retval += prefix + tab + "<name manufacturer=\"\">" + p.LookupName() + "</name>" + NL;
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
        } else {
            return "miscellaneous";
        }
    }

    private String GetArmoredLocations( String prefix ) {
        String retval = "";
        ifMechLoadout l = CurMech.GetLoadout();

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

    private int GetBaseEngineRating() {
        // provided for primitive 'Mechs.  Since the engine rating is handled by
        // the 'Mech and engine, we need to know what the base is.
        return CurMech.GetWalkingMP() * CurMech.GetTonnage();
    }
}
