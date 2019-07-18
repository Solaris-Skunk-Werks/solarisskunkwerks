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

package utilities;

import common.CommonTools;
import components.*;
import java.util.ArrayList;

public class CostBVBreakdown {
    private Mech CurMech = null;
    private String NL = System.getProperty( "line.separator" );

    public CostBVBreakdown(Mech mech) {
        CurMech = mech;
    }

    public String Render() {
        // this method returns a formated string with the cost/bv breakdown
        //  ----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
        String retval = "";
        retval += String.format( "Mech Name:   %1$-41s Tonnage:    %2$d", CurMech.GetName() + " " + CurMech.GetModel(), CurMech.GetTonnage() ) + NL;
        retval += String.format( "Rules Level: %1$-41s Total Cost: %2$,.0f", CommonTools.GetRulesLevelString( CurMech.GetRulesLevel() ), CurMech.GetTotalCost() ) + NL;
        retval += String.format( "Tech Base:   %1$-41s Total BV:   %2$,d", CommonTools.GetTechbaseString( CurMech.GetLoadout().GetTechBase() ), CurMech.GetCurrentBV() ) + NL;
        retval += NL;
        retval +=                "Item                                            DefBV     OffBV             Cost" + NL;
        retval += String.format( "Internal Structure - %1$-22s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetIntStruc().toString(), CurMech.GetIntStruc().GetDefensiveBV(), CurMech.GetIntStruc().GetOffensiveBV(), CurMech.GetIntStruc().GetCost() ) + NL;
        retval += String.format( "Engine - %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetEngine().CritName(), CurMech.GetEngine().GetDefensiveBV(), CurMech.GetEngine().GetOffensiveBV(), CurMech.GetEngine().GetCost() ) + NL;
        retval += String.format( "Gyro - %1$-36s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetGyro().CritName(), CurMech.GetGyro().GetDefensiveBV(), CurMech.GetGyro().GetOffensiveBV(), CurMech.GetGyro().GetCost() ) + NL;
        if( CurMech.IsPrimitive() && CurMech.GetYear() < 2450 ) {
            if( CurMech.IsIndustrialmech() ) {
                retval += String.format( "Cockpit - %1$-33s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetCockpit().GetReportName() + " (early)", CurMech.GetCockpit().GetDefensiveBV(), CurMech.GetCockpit().GetOffensiveBV(), CurMech.GetCockpit().GetCost() + 50000.0f ) + NL;
            } else {
                retval += String.format( "Cockpit - %1$-33s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetCockpit().GetReportName() + " (early)", CurMech.GetCockpit().GetDefensiveBV(), CurMech.GetCockpit().GetOffensiveBV(), CurMech.GetCockpit().GetCost() + 100000.0f ) + NL;
            }
        } else {
            retval += String.format( "Cockpit - %1$-33s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetCockpit().GetReportName(), CurMech.GetCockpit().GetDefensiveBV(), CurMech.GetCockpit().GetOffensiveBV(), CurMech.GetCockpit().GetCost() ) + NL;
        }
        retval += String.format( "Heat Sinks - %1$-30s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetHeatSinks().LookupName(), CurMech.GetHeatSinks().GetDefensiveBV(), CurMech.GetHeatSinks().GetOffensiveBV(), CurMech.GetHeatSinks().GetCost() ) + NL;
        if( CurMech.GetPhysEnhance().IsTSM() ) {
            retval += String.format( "Musculature - %1$-29s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Triple-Strength", CurMech.GetPhysEnhance().GetDefensiveBV(), CurMech.GetPhysEnhance().GetOffensiveBV(), CurMech.GetPhysEnhance().GetCost() ) + NL;
        } else {
            if( CurMech.IsPrimitive() ) {
                if( CurMech.GetYear() < 2450 ) {
                    retval += String.format( "Musculature - %1$-29s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Primitive (early)", 0.0f, CurMech.GetTonnage() * 1.0f, CurMech.GetTonnage() * 2000.0f ) + NL;
                } else {
                    retval += String.format( "Musculature - %1$-29s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Primitive", 0.0f, CurMech.GetTonnage() * 1.0f, CurMech.GetTonnage() * 1000.0f ) + NL;
                }
            } else {
                retval += String.format( "Musculature - %1$-29s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Standard", 0.0f, CurMech.GetTonnage() * 1.0f, CurMech.GetTonnage() * 2000.0f ) + NL;
            }
        }
        retval += String.format( "Actuators %1$-33s %2$,6.0f    %3$,6.0f    %4$,16.2f", "", CurMech.GetActuators().GetDefensiveBV(), CurMech.GetActuators().GetOffensiveBV(), CurMech.GetActuators().GetCost() ) + NL;
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            retval += String.format( "Jump Jets - %1$-31s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetJumpJets().LookupName(), CurMech.GetJumpJets().GetDefensiveBV(), CurMech.GetJumpJets().GetOffensiveBV(), CurMech.GetJumpJets().GetCost() ) + NL;
        }
        if( CurMech.GetPhysEnhance().IsMASC() ) {
            retval += String.format( "%1$-43s %2$,6.0f    %3$,6.0f    %4$,16.2f", "MASC", CurMech.GetPhysEnhance().GetDefensiveBV(), CurMech.GetPhysEnhance().GetOffensiveBV(), CurMech.GetPhysEnhance().GetCost() ) + NL;
        }
        if( ! CurMech.GetEngine().IsNuclear() ) {
            retval += String.format( "%1$-43s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Power Amplifiers", 0.0f, 0.0f, CurMech.GetLoadout().GetPowerAmplifier().GetCost() ) + NL;
        }
        if( CurMech.IsPrimitive() && CurMech.GetYear() < 2450 ) {
            retval += String.format( "Armor - %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().LookupName() + " (early)", CurMech.GetArmor().GetDefensiveBV(), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetCost() ) + NL;
            if( CurMech.GetArmor().IsPatchwork() ) {
                int[] ModArmor = CurMech.GetLoadout().FindModularArmor();
                if( CurMech.IsQuad() ) {
                    retval += String.format( "     HD: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetHDArmorType().LookupName(), CurMech.GetArmor().GetHDDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetHDCost() ) + NL;
                    retval += String.format( "     CT: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetCTArmorType().LookupName(), CurMech.GetArmor().GetCTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetCTCost() ) + NL;
                    retval += String.format( "     LT: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLTArmorType().LookupName(), CurMech.GetArmor().GetLTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLTCost() ) + NL;
                    retval += String.format( "     RT: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRTArmorType().LookupName(), CurMech.GetArmor().GetRTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRTCost() ) + NL;
                    retval += String.format( "    FLL: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLAArmorType().LookupName(), CurMech.GetArmor().GetLADefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLACost() ) + NL;
                    retval += String.format( "    FRL: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRAArmorType().LookupName(), CurMech.GetArmor().GetRADefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRACost() ) + NL;
                    retval += String.format( "    RLL: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLLArmorType().LookupName(), CurMech.GetArmor().GetLLDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLLCost() ) + NL;
                    retval += String.format( "    RRL: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRLArmorType().LookupName(), CurMech.GetArmor().GetRLDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRLCost() ) + NL;
                } else {
                    retval += String.format( "    HD: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetHDArmorType().LookupName(), CurMech.GetArmor().GetHDDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetHDCost() ) + NL;
                    retval += String.format( "    CT: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetCTArmorType().LookupName(), CurMech.GetArmor().GetCTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetCTCost() ) + NL;
                    retval += String.format( "    LT: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLTArmorType().LookupName(), CurMech.GetArmor().GetLTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLTCost() ) + NL;
                    retval += String.format( "    RT: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRTArmorType().LookupName(), CurMech.GetArmor().GetRTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRTCost() ) + NL;
                    retval += String.format( "    LA: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLAArmorType().LookupName(), CurMech.GetArmor().GetLADefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLACost() ) + NL;
                    retval += String.format( "    RA: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRAArmorType().LookupName(), CurMech.GetArmor().GetRADefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRACost() ) + NL;
                    retval += String.format( "    LL: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLLArmorType().LookupName(), CurMech.GetArmor().GetLLDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLLCost() ) + NL;
                    retval += String.format( "    RL: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRLArmorType().LookupName(), CurMech.GetArmor().GetRLDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRLCost() ) + NL;
                }
            }
        } else {
            retval += String.format( "Armor - %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().LookupName(), CurMech.GetArmor().GetDefensiveBV(), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetCost() ) + NL;
            if( CurMech.GetArmor().IsPatchwork() ) {
                int[] ModArmor = CurMech.GetLoadout().FindModularArmor();
                if( CurMech.IsQuad() ) {
                    retval += String.format( "     HD: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetHDArmorType().LookupName(), CurMech.GetArmor().GetHDDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetHDCost() ) + NL;
                    retval += String.format( "     CT: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetCTArmorType().LookupName(), CurMech.GetArmor().GetCTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetCTCost() ) + NL;
                    retval += String.format( "     LT: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLTArmorType().LookupName(), CurMech.GetArmor().GetLTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLTCost() ) + NL;
                    retval += String.format( "     RT: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRTArmorType().LookupName(), CurMech.GetArmor().GetRTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRTCost() ) + NL;
                    retval += String.format( "    FLL: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLAArmorType().LookupName(), CurMech.GetArmor().GetLADefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLACost() ) + NL;
                    retval += String.format( "    FRL: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRAArmorType().LookupName(), CurMech.GetArmor().GetRADefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRACost() ) + NL;
                    retval += String.format( "    RLL: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLLArmorType().LookupName(), CurMech.GetArmor().GetLLDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLLCost() ) + NL;
                    retval += String.format( "    RRL: %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRLArmorType().LookupName(), CurMech.GetArmor().GetRLDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRLCost() ) + NL;
                } else {
                    retval += String.format( "    HD: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetHDArmorType().LookupName(), CurMech.GetArmor().GetHDDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetHDCost() ) + NL;
                    retval += String.format( "    CT: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetCTArmorType().LookupName(), CurMech.GetArmor().GetCTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetCTCost() ) + NL;
                    retval += String.format( "    LT: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLTArmorType().LookupName(), CurMech.GetArmor().GetLTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLTCost() ) + NL;
                    retval += String.format( "    RT: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRTArmorType().LookupName(), CurMech.GetArmor().GetRTDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRTCost() ) + NL;
                    retval += String.format( "    LA: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLAArmorType().LookupName(), CurMech.GetArmor().GetLADefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLACost() ) + NL;
                    retval += String.format( "    RA: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRAArmorType().LookupName(), CurMech.GetArmor().GetRADefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRACost() ) + NL;
                    retval += String.format( "    LL: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetLLArmorType().LookupName(), CurMech.GetArmor().GetLLDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetLLCost() ) + NL;
                    retval += String.format( "    RL: %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurMech.GetArmor().GetRLArmorType().LookupName(), CurMech.GetArmor().GetRLDefensiveBV( ModArmor ), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetRLCost() ) + NL;
                }
            }
        }
        retval += NL;
        retval += GetEquipmentCostLines();
        retval += NL;
        retval += String.format( "Cost Multiplier                                                            %1$,1.3f", CurMech.GetCostMult() ) + NL;
        retval += String.format( "Dry Cost                                                           %1$,13.0f", CurMech.GetDryCost() ) + NL;
        retval += String.format( "Total Cost                                                         %1$,13.0f", CurMech.GetTotalCost() ) + NL;
        retval += NL + NL;
        retval += "Defensive BV Calculation Breakdown" + NL;
        if( CurMech.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            retval += "(Note: BV Calculations include defensive BV for armored components.)" + NL;
        }
        retval += "________________________________________________________________________________" + NL;
        if( CurMech.GetArmor().IsPatchwork() ) {
            retval += "Patchwork Armor Calculations" + NL;
            if( CurMech.GetCockpit().IsTorsoMounted() ) {
                retval += "    (Front and Rear CT armor value doubled due to Torso-Mounted Cockpit)" + NL;
            }
            int[] ModArmor = CurMech.GetLoadout().FindModularArmor();
            retval += String.format( "    %1$-67s %2$,8.2f", "HD Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD) + ModArmor[LocationIndex.MECH_LOC_HD] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetHDArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetHDDefensiveBV( ModArmor ) ) + NL;
            if( CurMech.GetCockpit().IsTorsoMounted() ) {
                retval += String.format( "    %1$-67s %2$,8.2f", "( CT Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) + ModArmor[LocationIndex.MECH_LOC_CT] * 10 + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CTR ) + ModArmor[LocationIndex.MECH_LOC_CTR] * 10 ) + ") ) * 2 * Armor Type Modifier (" + CurMech.GetArmor().GetCTArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetCTDefensiveBV( ModArmor ) ) + NL;
            } else {
                retval += String.format( "    %1$-67s %2$,8.2f", "CT Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) + ModArmor[LocationIndex.MECH_LOC_CT] * 10 + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CTR ) + ModArmor[LocationIndex.MECH_LOC_CTR] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetCTArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetCTDefensiveBV( ModArmor ) ) + NL;
            }
            retval += String.format( "    %1$-67s %2$,8.2f", "LT Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LT ) + ModArmor[LocationIndex.MECH_LOC_LT] * 10 + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LTR ) + ModArmor[LocationIndex.MECH_LOC_LTR] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetLTArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetLTDefensiveBV( ModArmor ) ) + NL;
            retval += String.format( "    %1$-67s %2$,8.2f", "RT Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RT ) + ModArmor[LocationIndex.MECH_LOC_RT] * 10 + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RTR ) + ModArmor[LocationIndex.MECH_LOC_RTR] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetRTArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetRTDefensiveBV( ModArmor ) ) + NL;
            if( CurMech.IsQuad() ) {
                retval += String.format( "    %1$-67s %2$,8.2f", "FLL Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) + ModArmor[LocationIndex.MECH_LOC_LA] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetLAArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetLADefensiveBV( ModArmor ) ) + NL;
                retval += String.format( "    %1$-67s %2$,8.2f", "FRL Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) + ModArmor[LocationIndex.MECH_LOC_RA] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetRAArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetRADefensiveBV( ModArmor ) ) + NL;
                retval += String.format( "    %1$-67s %2$,8.2f", "RLL Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) + ModArmor[LocationIndex.MECH_LOC_LL] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetLLArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetLLDefensiveBV( ModArmor ) ) + NL;
                retval += String.format( "    %1$-67s %2$,8.2f", "RRL Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) + ModArmor[LocationIndex.MECH_LOC_RL] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetRLArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetRLDefensiveBV( ModArmor ) ) + NL;
            } else {
                retval += String.format( "    %1$-67s %2$,8.2f", "LA Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LA ) + ModArmor[LocationIndex.MECH_LOC_LA] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetLAArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetLADefensiveBV( ModArmor ) ) + NL;
                retval += String.format( "    %1$-67s %2$,8.2f", "RA Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RA ) + ModArmor[LocationIndex.MECH_LOC_RA] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetRAArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetRADefensiveBV( ModArmor ) ) + NL;
                retval += String.format( "    %1$-67s %2$,8.2f", "LL Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_LL ) + ModArmor[LocationIndex.MECH_LOC_LL] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetLLArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetLLDefensiveBV( ModArmor ) ) + NL;
                retval += String.format( "    %1$-67s %2$,8.2f", "RL Armor Factor (" + ( CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_RL ) + ModArmor[LocationIndex.MECH_LOC_RL] * 10 ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetRLArmorType().GetBVTypeMult() + ")", CurMech.GetArmor().GetRLDefensiveBV( ModArmor ) ) + NL;
            }
            retval += String.format( "%1$-71s %2$,8.2f", "    Total Location Armor BV (" + ( CurMech.GetArmor().GetHDDefensiveBV( ModArmor ) + CurMech.GetArmor().GetCTDefensiveBV( ModArmor ) + CurMech.GetArmor().GetRTDefensiveBV( ModArmor ) + CurMech.GetArmor().GetLTDefensiveBV( ModArmor ) + CurMech.GetArmor().GetRADefensiveBV( ModArmor ) + CurMech.GetArmor().GetLADefensiveBV( ModArmor ) + CurMech.GetArmor().GetRLDefensiveBV( ModArmor ) + CurMech.GetArmor().GetLLDefensiveBV( ModArmor ) ) + ") * 2.5", CurMech.GetArmor().GetDefensiveBV() ) + NL;
        } else {
            if( CurMech.GetCockpit().IsTorsoMounted() ) {
                retval += "( Total Armor Factor (" + ( CurMech.GetArmor().GetArmorValue() + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT) + CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CTR) ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetBVTypeMult() + ")" + NL;
                retval += String.format( "%1$-71s %2$,8.2f", "    + Modular Armor Value (" + CurMech.GetArmor().GetModularArmorValue() + ") ) * 2.5", CurMech.GetArmor().GetDefensiveBV() ) + NL;
                retval += "    (Front and Rear CT armor value doubled due to Torso-Mounted Cockpit)" + NL;
            } else {
                retval += String.format( "%1$-71s %2$,8.2f", "Total Armor Factor (" + CurMech.GetArmor().GetArmorValue() + ") * Armor Type Modifier (" + CurMech.GetArmor().GetBVTypeMult() + ") * 2.5", CurMech.GetArmor().GetDefensiveBV() ) + NL;
            }
        }
        retval += "Total Structure Points (" + CurMech.GetIntStruc().GetTotalPoints() + ") * Structure Type Modifier (" + CurMech.GetIntStruc().GetBVTypeMult() + ")" + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "    * Engine Type Modifier (" + CurMech.GetEngine().GetBVMult() + ") * 1.5", CurMech.GetIntStruc().GetDefensiveBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Mech Tonnage (" + CurMech.GetTonnage() + ") * Gyro Type Modifer (" + CurMech.GetGyro().GetBVTypeMult() + ")", CurMech.GetGyro().GetDefensiveBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Total Defensive BV of all Equipment", CurMech.GetDefensiveEquipBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Excessive Ammunition Penalty", CurMech.GetDefensiveExcessiveAmmoPenalty() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Explosive Ammunition Penalty", CurMech.GetExplosiveAmmoPenalty() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Explosive Item Penalty  ", CurMech.GetExplosiveWeaponPenalty() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Subtotal", CurMech.GetUnmodifiedDefensiveBV() ) + NL;
        retval += "Defensive Speed Factor Breakdown:" + NL;
        retval += PrintDefensiveFactorCalculations();
        retval += String.format( "%1$-71s %2$,8.2f", "Total DBV (Subtotal * Defensive Speed Factor (" + String.format( "%1$,4.2f", CurMech.GetDefensiveFactor() ) + "))", CurMech.GetDefensiveBV() ) + NL;
        retval += NL + NL;
        retval += "Offensive BV Calculation Breakdown" + NL;
        retval += "________________________________________________________________________________" + NL;
        if( HasBonusFromCP() ) {
            retval += "Heat Efficiency (6 + " + CurMech.GetHeatSinks().TotalDissipation() + " - " + CurMech.GetBVMovementHeat() + " + " + GetBonusFromCP() + ") = " + ( 6 + CurMech.GetHeatSinks().TotalDissipation() - CurMech.GetBVMovementHeat() + GetBonusFromCP() ) + NL;
            retval += "    (Heat Efficiency calculation includes bonus from Coolant Pods)" + NL;
        } else {
            retval += "Heat Efficiency (6 + " + CurMech.GetHeatSinks().TotalDissipation() + " - " + CurMech.GetBVMovementHeat() + ") = "+ ( 6 + CurMech.GetHeatSinks().TotalDissipation() - CurMech.GetBVMovementHeat() ) + NL;
        }
        retval += String.format( "%1$-71s %2$,8.2f", "Adjusted Weapon BV Total WBV", CurMech.GetHeatAdjustedWeaponBV() ) + NL;
        retval += PrintHeatAdjustedWeaponBV();
        retval += String.format( "%1$-71s %2$,8.2f", "Non-Heat Equipment Total NHBV", CurMech.GetNonHeatEquipBV() ) + NL;
        retval += PrintNonHeatEquipBV();
        retval += String.format( "%1$-71s %2$,8.2f", "Excessive Ammunition Penalty", CurMech.GetExcessiveAmmoPenalty() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Mech Tonnage Bonus", CurMech.GetTonnageBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Subtotal (WBV + NHBV - Excessive Ammo + Tonnage Bonus)", CurMech.GetUnmodifiedOffensiveBV() ) + NL;
        retval += "Offensive Speed Factor Breakdown:" + NL;
        retval += PrintOffensiveFactorCalculations();
        retval += String.format( "%1$-71s %2$,8.2f", "Total OBV (Subtotal * Offensive Speed Factor (" + CurMech.GetOffensiveFactor() + "))", CurMech.GetOffensiveBV() ) + NL;
        retval += NL + NL;
        if( CurMech.GetCockpit().BVMod() != 1.0f ) {
            retval += String.format( "%1$-71s %2$,8.2f", CurMech.GetCockpit().CritName() + " modifier", CurMech.GetCockpit().BVMod() ) + NL;
            retval += String.format( "%1$-73s %2$,6d", "Total Battle Value ((DBV + OBV) * cockpit modifier, round off)", CurMech.GetCurrentBV() );
        } else {
            retval += String.format( "%1$-73s %2$,6d", "Total Battle Value (DBV + OBV, round off)", CurMech.GetCurrentBV() );
        }
        return retval;
    }

    private String GetEquipmentCostLines() {
        // returns a block of lines for the cost breakdown
        String retval = "";
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        abPlaceable a;
        for( int i = 0; i < v.size(); i++ ) {
            a = (abPlaceable) v.get( i );
            if( a instanceof RangedWeapon ) {
                if( ((RangedWeapon) a).IsUsingFCS() ) {
                    retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", a.CritName() + " w/ " + ((abPlaceable) ((RangedWeapon) a).GetFCS()).CritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
                } else {
                    retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", a.CritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
                }
            } else {
                if( a instanceof Equipment && ((Equipment)a).LookupName().equals("Radical Heat Sink"))
                    retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", a.CritName(), a.GetDefensiveBV(), CommonTools.RoundFullUp(CurMech.GetHeatSinks().GetNumHS() * 1.4), a.GetCost() ) + NL;
                else
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", a.CritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
            }
        }
        if( CurMech.HasCommandConsole() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetCommandConsole().CritName(), CurMech.GetCommandConsole().GetDefensiveBV(), CurMech.GetCommandConsole().GetOffensiveBV(), CurMech.GetCommandConsole().GetCost() ) + NL;
        }
        if( CurMech.UsingTC() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "Targeting Computer", CurMech.GetTC().GetDefensiveBV(), CurMech.GetTC().GetOffensiveBV(), CurMech.GetTC().GetCost() ) + NL;
        }
        if( CurMech.HasNullSig() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetNullSig().CritName(), CurMech.GetNullSig().GetDefensiveBV(), CurMech.GetNullSig().GetOffensiveBV(), CurMech.GetNullSig().GetCost() ) + NL;
        }
        if( CurMech.HasChameleon() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetChameleon().CritName(), CurMech.GetChameleon().GetDefensiveBV(), CurMech.GetChameleon().GetOffensiveBV(), CurMech.GetChameleon().GetCost() ) + NL;
        }
        if( CurMech.HasVoidSig() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetVoidSig().CritName(), CurMech.GetVoidSig().GetDefensiveBV(), CurMech.GetVoidSig().GetOffensiveBV(), CurMech.GetVoidSig().GetCost() ) + NL;
        }
        if( CurMech.HasBlueShield() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetBlueShield().CritName(), CurMech.GetBlueShield().GetDefensiveBV(), CurMech.GetBlueShield().GetOffensiveBV(), CurMech.GetBlueShield().GetCost() ) + NL;
        }
        if( CurMech.UsingPartialWing() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetPartialWing().CritName(), CurMech.GetPartialWing().GetDefensiveBV(), CurMech.GetPartialWing().GetOffensiveBV(), CurMech.GetPartialWing().GetCost() ) + NL;
        }
        if( CurMech.UsingJumpBooster() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetJumpBooster().CritName(), CurMech.GetJumpBooster().GetDefensiveBV(), CurMech.GetJumpBooster().GetOffensiveBV(), CurMech.GetJumpBooster().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasSupercharger() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetLoadout().GetSupercharger().CritName(), CurMech.GetLoadout().GetSupercharger().GetDefensiveBV(), CurMech.GetLoadout().GetSupercharger().GetOffensiveBV(), CurMech.GetLoadout().GetSupercharger().GetCost() ) + NL;
        }
        if( CurMech.HasLAAES() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetLAAES().CritName(), CurMech.GetLAAES().GetDefensiveBV(), CurMech.GetLAAES().GetOffensiveBV(), CurMech.GetLAAES().GetCost() ) + NL;
        }
        if( CurMech.HasRAAES() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetRAAES().CritName(), CurMech.GetRAAES().GetDefensiveBV(), CurMech.GetRAAES().GetOffensiveBV(), CurMech.GetRAAES().GetCost() ) + NL;
        }
        if( CurMech.HasLegAES() ) {
            if( CurMech.IsQuad() ) {
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetRLAES().CritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetRLAES().CritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetRLAES().CritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetRLAES().CritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
            } else {
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetRLAES().CritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetRLAES().CritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
            }
        }
        if( CurMech.GetLoadout().HasHDTurret() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetLoadout().GetHDTurret().CritName(), CurMech.GetLoadout().GetHDTurret().GetDefensiveBV(), CurMech.GetLoadout().GetHDTurret().GetOffensiveBV(), CurMech.GetLoadout().GetHDTurret().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasLTTurret() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetLoadout().GetLTTurret().CritName(), CurMech.GetLoadout().GetLTTurret().GetDefensiveBV(), CurMech.GetLoadout().GetLTTurret().GetOffensiveBV(), CurMech.GetLoadout().GetLTTurret().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasRTTurret() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurMech.GetLoadout().GetRTTurret().CritName(), CurMech.GetLoadout().GetRTTurret().GetDefensiveBV(), CurMech.GetLoadout().GetRTTurret().GetOffensiveBV(), CurMech.GetLoadout().GetRTTurret().GetCost() ) + NL;
        }
        if( CurMech.HasCTCase() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE", 0.0, 0.0, CurMech.GetLoadout().GetCTCase().GetCost() ) + NL;
        }
        if( CurMech.HasLTCase() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE", 0.0, 0.0, CurMech.GetLoadout().GetLTCase().GetCost() ) + NL;
        }
        if( CurMech.HasRTCase() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE", 0.0, 0.0, CurMech.GetLoadout().GetRTCase().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasHDCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE II", 0.0, 0.0, CurMech.GetLoadout().GetHDCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasCTCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE II", 0.0, 0.0, CurMech.GetLoadout().GetCTCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasLTCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE II", 0.0, 0.0, CurMech.GetLoadout().GetLTCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasRTCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE II", 0.0, 0.0, CurMech.GetLoadout().GetRTCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasLACASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE II", 0.0, 0.0, CurMech.GetLoadout().GetLACaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasRACASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE II", 0.0, 0.0, CurMech.GetLoadout().GetRACaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasLLCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE II", 0.0, 0.0, CurMech.GetLoadout().GetLLCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasRLCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "CASE II", 0.0, 0.0, CurMech.GetLoadout().GetRLCaseII().GetCost() ) + NL;
        }
        return retval;
    }

    public String PrintNonHeatEquipBV() {
        // return the BV of all offensive equipment
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        abPlaceable a = null;
        String retval = "";

        for( int i = 0; i < v.size(); i++ ) {
            if( ! ( v.get( i ) instanceof ifWeapon ) ) {
                a = ((abPlaceable) v.get( i ));
                if ( a.LookupName().equals("Radical Heat Sink"))
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.CritName(), CommonTools.RoundFullUp(CurMech.GetHeatSinks().GetNumHS() * 1.4) ) + NL;
                else
                retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.CritName(), a.GetOffensiveBV() ) + NL;
            }
        }
        return retval;
    }

    public String PrintHeatAdjustedWeaponBV() {
        ArrayList v = CurMech.GetLoadout().GetNonCore(), wep = new ArrayList();
        double foreBV = 0.0, rearBV = 0.0;
        boolean UseRear = false, TC = CurMech.UsingTC(), UseAESMod = false, Robotic = CurMech.UsingRoboticCockpit();
        String retval = "";
        abPlaceable a;

        // is it even worth performing all this?
        if( v.size() <= 0 ) {
            // nope
            return retval;
        }

        // trim out the other equipment and get a list of offensive weapons only.
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof ifWeapon ) {
                wep.add( v.get( i ) );
            }
        }

        // just to save us a headache if there are no weapons
        if( wep.size() <= 0 ) { return retval; }

        // now get the mech's heat efficiency and the total heat from weapons
        double heff = 6 + CurMech.GetHeatSinks().TotalDissipation() - CurMech.GetBVMovementHeat();
        heff += GetBonusFromCP();
        double wheat = CurMech.GetBVWeaponHeat();

        // find out the total BV of rear and forward firing weapons
        for( int i = 0; i < wep.size(); i++ ) {
            a = ((abPlaceable) wep.get( i ));
            // arm mounted weapons always count their full BV, so ignore them.
            int loc = CurMech.GetLoadout().Find( a );
            if( loc != LocationIndex.MECH_LOC_LA && loc != LocationIndex.MECH_LOC_RA ) {
                UseAESMod = CurMech.UseAESModifier( a );
                if( a.IsMountedRear() ) {
                    rearBV += a.GetCurOffensiveBV( true, TC, UseAESMod, Robotic );
                } else {
                    foreBV += a.GetCurOffensiveBV( false, TC, UseAESMod, Robotic );
                }
            }
        }
        if( rearBV > foreBV ) { UseRear = true; }

        // see if we need to run heat calculations
        if( heff - wheat >= 0 ) {
            // no need for extensive calculations, return the weapon BV
            for( int i = 0; i < wep.size(); i++ ) {
                a = ((abPlaceable) wep.get( i ));
                int loc = CurMech.GetLoadout().Find( a );
                UseAESMod = CurMech.UseAESModifier( ((abPlaceable) wep.get( i )) );
                if( loc != LocationIndex.MECH_LOC_LA && loc != LocationIndex.MECH_LOC_RA ) {
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.CritName(), a.GetCurOffensiveBV( UseRear, TC, UseAESMod ) ) + NL;
                } else {
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.CritName(), a.GetCurOffensiveBV( false, TC, UseAESMod ) ) + NL;
                }
            }
            return retval;
        }

        // Sort the weapon list
        abPlaceable[] sorted = CurMech.SortWeapons( wep, UseRear );

        // calculate the BV of the weapons based on heat
        int curheat = 0;
        for( int i = 0; i < sorted.length; i++ ) {
            boolean DoRear = UseRear;
            a = sorted[i];
            int loc = CurMech.GetLoadout().Find( a );
            //changed below to DoRear = false because it would set Rear to true if it was false to begin with.
            if( loc == LocationIndex.MECH_LOC_LA || loc == LocationIndex.MECH_LOC_RA ) { DoRear = false; } //!DoRear; }
            UseAESMod = CurMech.UseAESModifier( a );
            if( curheat < heff ) {
                retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.CritName(), a.GetCurOffensiveBV( DoRear, TC, UseAESMod ) ) + NL;
            } else {
                if( ((ifWeapon) sorted[i]).GetBVHeat() <= 0 ) {
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.CritName(), a.GetCurOffensiveBV( DoRear, TC, UseAESMod ) ) + NL;
                } else {
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.CritName(), a.GetCurOffensiveBV( DoRear, TC, UseAESMod ) * 0.5 ) + NL;
                }
            }
            curheat += ((ifWeapon) sorted[i]).GetBVHeat();
        }
        return retval;
    }

    public String PrintDefensiveFactorCalculations() {
        // returns the defensive factor for this mech based on it's highest
        // target number for speed.
        String retval = "";

        // subtract one since we're indexing an array
        int RunMP = CurMech.GetAdjustedRunningMP( true, true ) - 1;
        int JumpMP = 0;

        // this is a safeguard for using MASC on an incredibly speedy chassis
        // there is currently no way to get a bonus higher anyway.
        if( RunMP > 29 ) { RunMP = 29; }
        // safeguard for low walk mp (Modular Armor, for instance)
        if( RunMP < 0 ) { RunMP = 0; }

        // Get the defensive factors for jumping and running movement
        double ground = Mech.DefensiveFactor[RunMP];
        double jump = 0.0;
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            JumpMP = CurMech.GetAdjustedJumpingMP( true ) - 1;
                jump = Mech.DefensiveFactor[JumpMP] + 0.1;
        }
        if( CurMech.UsingJumpBooster() ) {
            int boostMP = CurMech.GetJumpBoosterMP();
            if( boostMP > JumpMP ) {
                JumpMP = boostMP;
                jump = Mech.DefensiveFactor[JumpMP] + 0.1;
            }
        }

        MechModifier m = CurMech.GetTotalModifiers( true, true );

        retval += "    Maximum Ground Movement Modifier: " + String.format( "%1$,.2f", ground ) + NL;
        retval += "    Maximum Jump Movement Modifier:   " + String.format( "%1$,.2f", jump ) + NL;
        retval += "    Defensive Speed Factor Bonus from Equipment: " + String.format( "%1$,.2f", m.DefensiveBonus() ) + NL;
        retval += "    Minimum Defensive Speed Factor:   " + String.format( "%1$,.2f", m.MinimumDefensiveBonus() ) + NL;
        retval += "    (Max of Run or Jump) + DSF Bonus = " + String.format( "%1$,.2f", CurMech.GetDefensiveFactor() ) + NL;

        return retval;
    }

    public String PrintOffensiveFactorCalculations() {
        String retval = "";

        double temp;
        if( CurMech.UsingJumpBooster() ) {
            int boost = CurMech.GetAdjustedBoosterMP( true );
            int jump = CurMech.GetAdjustedJumpingMP( true );
            if( jump >= boost ) {
                temp = (double) (CurMech.GetAdjustedRunningMP(true, true) + (Math.floor(CurMech.GetAdjustedJumpingMP(true) * 0.5 + 0.5)) - 5.0);
                retval += "    Adjusted Running MP (" + CurMech.GetAdjustedRunningMP( true, true ) + ") + ( Adjusted Jumping MP (" + CurMech.GetAdjustedJumpingMP( true ) + ") / 2 ) - 5 = " + String.format( "%1$,.2f", CurMech.GetAdjustedRunningMP( true, true ) + ( Math.floor( CurMech.GetAdjustedJumpingMP( true ) * 0.5 + 0.5 )  ) - 5.0 ) + NL;
            } else {
                temp = (double) (CurMech.GetAdjustedRunningMP(true, true) + (Math.floor(CurMech.GetAdjustedBoosterMP(true) * 0.5 + 0.5)) - 5.0);
                retval += "    Adjusted Running MP (" + CurMech.GetAdjustedRunningMP( true, true ) + ") + ( Adjusted Jumping MP (" + CurMech.GetAdjustedBoosterMP( true ) + ") / 2 ) - 5 = " + String.format( "%1$,.2f", CurMech.GetAdjustedRunningMP( true, true ) + ( Math.floor( CurMech.GetAdjustedBoosterMP( true ) * 0.5 + 0.5 )  ) - 5.0 ) + NL;
            }
        } else {
            temp = (double) (CurMech.GetAdjustedRunningMP(true, true) + (Math.floor(CurMech.GetAdjustedJumpingMP(true) * 0.5 + 0.5)) - 5.0);
            retval += "    Adjusted Running MP (" + CurMech.GetAdjustedRunningMP( true, true ) + ") + ( Adjusted Jumping MP (" + CurMech.GetAdjustedJumpingMP( true ) + ") / 2 ) - 5 = " + String.format( "%1$,.2f", CurMech.GetAdjustedRunningMP( true, true ) + ( Math.floor( CurMech.GetAdjustedJumpingMP( true ) * 0.5 + 0.5 )  ) - 5.0 ) + NL;
        }
        retval += "    " + String.format( "%1$,.2f", temp ) + " / 10 + 1 = " + String.format( "%1$.3f", ( temp * 0.1 + 1.0 ) ) + NL;
        temp = temp * 0.1 + 1.0;
        retval += "    " + String.format( "%1$,.2f", temp ) + " ^ 1.2 = " + (double) Math.floor( ( Math.pow( temp, 1.2 ) ) * 100 + 0.5 ) / 100 + " (rounded off to two digits)" + NL;

        return retval;
    }

    private boolean HasBonusFromCP() {
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        abPlaceable a;
        if( CurMech.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            // check for coolant pods
            for( int i = 0; i < v.size(); i++ ) {
                a = (abPlaceable) v.get( i );
                if( a instanceof Equipment ) {
                    if( ((Equipment) a).LookupName().equals( "Coolant Pod" ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int GetBonusFromCP() {
        int BonusFromCP, retval = 0;
        int NumHS = CurMech.GetHeatSinks().GetNumHS(), MaxHSBonus = NumHS * 2, NumPods = 0;
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        abPlaceable a;

        if( CurMech.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            // check for coolant pods
            for( int i = 0; i < v.size(); i++ ) {
                a = (abPlaceable) v.get( i );
                if( a instanceof Equipment ) {
                    if( ((Equipment) a).LookupName().equals( "Coolant Pod" ) ) {
                        NumPods++;
                    }
                }
            }
            // get the heat sink bonus
            BonusFromCP = (int) Math.ceil( (double) NumHS * ( (double) NumPods * 0.2f ) );
            if( BonusFromCP > MaxHSBonus ) { BonusFromCP = MaxHSBonus; }
            retval += BonusFromCP;
        }
        return retval;
    }
}
