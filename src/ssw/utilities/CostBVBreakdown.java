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

package ssw.utilities;

import java.util.Vector;
import ssw.CommonTools;
import ssw.Constants;
import ssw.components.*;

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
        retval += String.format( "Tech Base:   %1$-41s Total BV:   %2$,d", CommonTools.GetTechbaseString( CurMech.GetTechBase() ), CurMech.GetCurrentBV() ) + NL;
        retval += NL;
        retval +=                "Item                                            DefBV     OffBV             Cost" + NL;
        retval += String.format( "Internal Structure - %1$-25s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetIntStruc().toString(), CurMech.GetIntStruc().GetDefensiveBV(), CurMech.GetIntStruc().GetOffensiveBV(), CurMech.GetIntStruc().GetCost() ) + NL;
        retval += String.format( "Engine - %1$-37s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetEngine().GetCritName(), CurMech.GetEngine().GetDefensiveBV(), CurMech.GetEngine().GetOffensiveBV(), CurMech.GetEngine().GetCost() ) + NL;
        retval += String.format( "Gyro - %1$-39s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetGyro().GetLookupName(), CurMech.GetGyro().GetDefensiveBV(), CurMech.GetGyro().GetOffensiveBV(), CurMech.GetGyro().GetCost() ) + NL;
        retval += String.format( "Cockpit - %1$-36s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetCockpit().GetLookupName(), CurMech.GetCockpit().GetDefensiveBV(), CurMech.GetCockpit().GetOffensiveBV(), CurMech.GetCockpit().GetCost() ) + NL;
        retval += String.format( "Heat Sinks - %1$-33s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetHeatSinks().GetLookupName(), CurMech.GetHeatSinks().GetDefensiveBV(), CurMech.GetHeatSinks().GetOffensiveBV(), CurMech.GetHeatSinks().GetCost() ) + NL;
        if( CurMech.GetPhysEnhance().IsTSM() ) {
            retval += String.format( "Musculature - %1$-32s %2$,6.0f    %3$,6.0f    %4$,13.0f", "Triple-Strength", CurMech.GetPhysEnhance().GetDefensiveBV(), CurMech.GetPhysEnhance().GetOffensiveBV(), CurMech.GetPhysEnhance().GetCost() ) + NL;
        } else {
            retval += String.format( "Musculature - %1$-32s %2$,6.0f    %3$,6.0f    %4$,13.0f", "Standard", 0.0f, CurMech.GetTonnage() * 1.0f, CurMech.GetTonnage() * 2000.0f ) + NL;
        }
        retval += String.format( "Actuators %1$-36s %2$,6.0f    %3$,6.0f    %4$,13.0f", "", CurMech.GetActuators().GetDefensiveBV(), CurMech.GetActuators().GetOffensiveBV(), CurMech.GetActuators().GetCost() ) + NL;
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            retval += String.format( "Jump Jets - %1$-34s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetJumpJets().GetLookupName(), CurMech.GetJumpJets().GetDefensiveBV(), CurMech.GetJumpJets().GetOffensiveBV(), CurMech.GetJumpJets().GetCost() ) + NL;
        }
        if( CurMech.GetPhysEnhance().IsMASC() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "MASC", CurMech.GetPhysEnhance().GetDefensiveBV(), CurMech.GetPhysEnhance().GetOffensiveBV(), CurMech.GetPhysEnhance().GetCost() ) + NL;
        }
        if( ! CurMech.GetEngine().IsNuclear() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "Power Amplifiers", 0.0f, 0.0f, CurMech.GetLoadout().GetPowerAmplifier().GetCost() ) + NL;
        }
        retval += String.format( "Armor - %1$-38s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetArmor().GetLookupName(), CurMech.GetArmor().GetDefensiveBV(), CurMech.GetArmor().GetOffensiveBV(), CurMech.GetArmor().GetCost() ) + NL;
        retval += NL;
        retval += GetEquipmentCostLines();
        retval += NL;
        retval += String.format( "Cost Multiplier                                                            %1$,1.3f", CurMech.GetCostMult() ) + NL;
        retval += String.format( "Dry Cost                                                           %1$,13.2f", CurMech.GetDryCost() ) + NL;
        retval += String.format( "Total Cost                                                         %1$,13.2f", CurMech.GetTotalCost() ) + NL;
        retval += NL + NL;
        retval += "Defensive BV Calculation Breakdown" + NL;
        if( CurMech.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            retval += "(Note: BV Calculations include defensive BV for armored components.)" + NL;
        }
        retval += "________________________________________________________________________________" + NL;
        if( CurMech.GetCockpit().IsTorsoMounted() ) {
            retval += String.format( "%1$-71s %2$,8.2f", "Total Armor Factor (" + ( CurMech.GetArmor().GetArmorValue() + CurMech.GetArmor().GetLocationArmor( Constants.LOC_CT) + CurMech.GetArmor().GetLocationArmor( Constants.LOC_CTR) ) + ") * Armor Type Modifier (" + CurMech.GetArmor().GetBVTypeMult() + ") * 2.5", CurMech.GetArmor().GetDefensiveBV() ) + NL;
            retval += "    (Front and Rear CT armor value doubled due to Torso-Mounted Cockpit)" + NL;
        } else {
            retval += String.format( "%1$-71s %2$,8.2f", "Total Armor Factor (" + CurMech.GetArmor().GetArmorValue() + ") * Armor Type Modifier (" + CurMech.GetArmor().GetBVTypeMult() + ") * 2.5", CurMech.GetArmor().GetDefensiveBV() ) + NL;
        }
        retval += "Total Structure Points (" + CurMech.GetIntStruc().GetTotalPoints() + ") * Structure Type Modifier (" + CurMech.GetIntStruc().GetBVTypeMult() + ") *" + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "    Engine Type Modifier (" + CurMech.GetEngine().GetBVMult() + ") * 1.5", CurMech.GetIntStruc().GetDefensiveBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Mech Tonnage (" + CurMech.GetTonnage() + ") * Gyro Type Modifer (" + CurMech.GetGyro().GetBVTypeMult() + ")", CurMech.GetGyro().GetDefensiveBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Total Defensive BV of all Equipment", CurMech.GetDefensiveEquipBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Explosive Ammunition Penalty", CurMech.GetExplosiveAmmoPenalty() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Explosive Weapon Penalty", CurMech.GetExplosiveWeaponPenalty() ) + NL;
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
            retval += String.format( "%1$-71s %2$,8.2f", CurMech.GetCockpit().GetCritName() + " modifier", CurMech.GetCockpit().BVMod() ) + NL;
            retval += String.format( "%1$-73s %2$,6d", "Total Battle Value ((DBV + OBV) * cockpit modifier, round off)", CurMech.GetCurrentBV() );
        } else {
            retval += String.format( "%1$-73s %2$,6d", "Total Battle Value (DBV + OBV, round off)", CurMech.GetCurrentBV() );
        }
        return retval;
    }

    private String GetEquipmentCostLines() {
        // returns a block of lines for the cost breakdown
        String retval = "";
        Vector v = CurMech.GetLoadout().GetNonCore();
        abPlaceable a;
        for( int i = 0; i < v.size(); i++ ) {
            a = (abPlaceable) v.get( i );
            if( a instanceof RangedWeapon ) {
                if( ((RangedWeapon) a).IsUsingFCS() ) {
                    retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", a.GetCritName() + " w/ " + ((abPlaceable) ((RangedWeapon) a).GetFCS()).GetCritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
                } else {
                    retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", a.GetCritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
                }
            } else {
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", a.GetCritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
            }
        }
        if( CurMech.HasCommandConsole() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetCommandConsole().GetCritName(), CurMech.GetCommandConsole().GetDefensiveBV(), CurMech.GetCommandConsole().GetOffensiveBV(), CurMech.GetCommandConsole().GetCost() ) + NL;
        }
        if( CurMech.UsingTC() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "Targeting Computer", CurMech.GetTC().GetDefensiveBV(), CurMech.GetTC().GetOffensiveBV(), CurMech.GetTC().GetCost() ) + NL;
        }
        if( CurMech.HasNullSig() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetNullSig().GetCritName(), CurMech.GetNullSig().GetDefensiveBV(), CurMech.GetNullSig().GetOffensiveBV(), CurMech.GetNullSig().GetCost() ) + NL;
        }
        if( CurMech.HasChameleon() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetChameleon().GetCritName(), CurMech.GetChameleon().GetDefensiveBV(), CurMech.GetChameleon().GetOffensiveBV(), CurMech.GetChameleon().GetCost() ) + NL;
        }
        if( CurMech.HasVoidSig() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetVoidSig().GetCritName(), CurMech.GetVoidSig().GetDefensiveBV(), CurMech.GetVoidSig().GetOffensiveBV(), CurMech.GetVoidSig().GetCost() ) + NL;
        }
        if( CurMech.HasBlueShield() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetBlueShield().GetCritName(), CurMech.GetBlueShield().GetDefensiveBV(), CurMech.GetBlueShield().GetOffensiveBV(), CurMech.GetBlueShield().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasSupercharger() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetLoadout().GetSupercharger().GetCritName(), CurMech.GetLoadout().GetSupercharger().GetDefensiveBV(), CurMech.GetLoadout().GetSupercharger().GetOffensiveBV(), CurMech.GetLoadout().GetSupercharger().GetCost() ) + NL;
        }
        if( CurMech.HasLAAES() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetLAAES().GetCritName(), CurMech.GetLAAES().GetDefensiveBV(), CurMech.GetLAAES().GetOffensiveBV(), CurMech.GetLAAES().GetCost() ) + NL;
        }
        if( CurMech.HasRAAES() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetRAAES().GetCritName(), CurMech.GetRAAES().GetDefensiveBV(), CurMech.GetRAAES().GetOffensiveBV(), CurMech.GetRAAES().GetCost() ) + NL;
        }
        if( CurMech.HasLegAES() ) {
            if( CurMech.IsQuad() ) {
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetRLAES().GetCritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetRLAES().GetCritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetRLAES().GetCritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetRLAES().GetCritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
            } else {
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetRLAES().GetCritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", CurMech.GetRLAES().GetCritName(), CurMech.GetRLAES().GetDefensiveBV(), CurMech.GetRLAES().GetOffensiveBV(), CurMech.GetRLAES().GetCost() ) + NL;
            }
        }
        if( CurMech.HasCTCase() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE", 0.0f, 0.0f, CurMech.GetLoadout().GetCTCase().GetCost() ) + NL;
        }
        if( CurMech.HasLTCase() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE", 0.0f, 0.0f, CurMech.GetLoadout().GetLTCase().GetCost() ) + NL;
        }
        if( CurMech.HasRTCase() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE", 0.0f, 0.0f, CurMech.GetLoadout().GetRTCase().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasHDCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE II", 0.0f, 0.0f, CurMech.GetLoadout().GetHDCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasCTCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE II", 0.0f, 0.0f, CurMech.GetLoadout().GetCTCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasLTCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE II", 0.0f, 0.0f, CurMech.GetLoadout().GetLTCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasRTCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE II", 0.0f, 0.0f, CurMech.GetLoadout().GetRTCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasLACASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE II", 0.0f, 0.0f, CurMech.GetLoadout().GetLACaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasRACASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE II", 0.0f, 0.0f, CurMech.GetLoadout().GetRACaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasLLCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE II", 0.0f, 0.0f, CurMech.GetLoadout().GetLLCaseII().GetCost() ) + NL;
        }
        if( CurMech.GetLoadout().HasRLCASEII() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", "CASE II", 0.0f, 0.0f, CurMech.GetLoadout().GetRLCaseII().GetCost() ) + NL;
        }
        return retval;
    }

    public String PrintNonHeatEquipBV() {
        // return the BV of all offensive equipment
        Vector v = CurMech.GetLoadout().GetNonCore();
        abPlaceable a = null;
        String retval = "";

        for( int i = 0; i < v.size(); i++ ) {
            if( ! ( v.get( i ) instanceof ifWeapon ) ) {
                a = ((abPlaceable) v.get( i ));
                retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.GetCritName(), a.GetOffensiveBV() ) + NL;
            }
        }
        return retval;
    }

    public String PrintHeatAdjustedWeaponBV() {
        Vector v = CurMech.GetLoadout().GetNonCore(), wep = new Vector();
        float foreBV = 0.0f, rearBV = 0.0f;
        boolean UseRear = false, TC = CurMech.UsingTC(), UseAESMod = false;
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
        int heff = 6 + CurMech.GetHeatSinks().TotalDissipation() - CurMech.GetBVMovementHeat();
        heff += GetBonusFromCP();
        int wheat = CurMech.GetBVWeaponHeat();

        // find out the total BV of rear and forward firing weapons
        for( int i = 0; i < wep.size(); i++ ) {
            a = ((abPlaceable) wep.get( i ));
            UseAESMod = CurMech.UseAESModifier( a );
            if( a.IsMountedRear() ) {
                rearBV += a.GetCurOffensiveBV( false, TC, UseAESMod );
            } else {
                foreBV += a.GetCurOffensiveBV( false, TC, UseAESMod );
            }
        }
        if( rearBV > foreBV ) { UseRear = true; }

        // see if we need to run heat calculations
        if( heff - wheat >= 0 ) {
            // no need for extensive calculations, return the weapon BV
            for( int i = 0; i < wep.size(); i++ ) {
                a = ((abPlaceable) wep.get( i ));
                UseAESMod = CurMech.UseAESModifier( ((abPlaceable) wep.get( i )) );
                retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.GetCritName(), a.GetCurOffensiveBV( UseRear, TC, UseAESMod ) ) + NL;
            }
            return retval;
        }

        // Sort the weapon list
        abPlaceable[] sorted = CurMech.SortWeapons( wep, UseRear );

        // calculate the BV of the weapons based on heat
        int curheat = 0;
        for( int i = 0; i < sorted.length; i++ ) {
            a = sorted[i];
            UseAESMod = CurMech.UseAESModifier( a );
            if( curheat < heff ) {
                retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.GetCritName(), a.GetCurOffensiveBV( UseRear, TC, UseAESMod ) ) + NL;
            } else {
                if( ((ifWeapon) sorted[i]).GetBVHeat() <= 0 ) {
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.GetCritName(), a.GetCurOffensiveBV( UseRear, TC, UseAESMod ) ) + NL;
                } else {
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.GetCritName(), a.GetCurOffensiveBV( UseRear, TC, UseAESMod ) * 0.5f ) + NL;
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
        float ground = Mech.DefensiveFactor[RunMP];
        float jump = 0.0f;
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            JumpMP = CurMech.GetAdjustedJumpingMP( true ) - 1;
                jump = Mech.DefensiveFactor[JumpMP] + 0.1f;
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

        float temp = (float) (CurMech.GetAdjustedRunningMP(true, true) + (Math.floor(CurMech.GetAdjustedJumpingMP(true) * 0.5f + 0.5f)) - 5.0f);
        retval += "    Adjusted Running MP (" + CurMech.GetAdjustedRunningMP( true, true ) + ") + ( Adjusted Jumping MP (" + CurMech.GetAdjustedJumpingMP( true ) + ") / 2 ) - 5 = " + String.format( "%1$,.2f", CurMech.GetAdjustedRunningMP( true, true ) + ( Math.floor( CurMech.GetAdjustedJumpingMP( true ) * 0.5f + 0.5f )  ) - 5.0f ) + NL;
        retval += "    " + String.format( "%1$,.2f", temp ) + " / 10 + 1 = " + ( temp * 0.1f + 1.0f ) + NL;
        temp = temp * 0.1f + 1.0f;
        retval += "    " + String.format( "%1$,.2f", temp ) + " ^ 1.2 = " + (float) Math.floor( ( Math.pow( temp, 1.2f ) ) * 100 + 0.5f ) / 100 + " (rounded off to two digits)" + NL;

        return retval;
    }

    private boolean HasBonusFromCP() {
        Vector v = CurMech.GetLoadout().GetNonCore();
        abPlaceable a;
        if( CurMech.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            // check for coolant pods
            for( int i = 0; i < v.size(); i++ ) {
                a = (abPlaceable) v.get( i );
                if( a instanceof Equipment ) {
                    if( ((Equipment) a).GetCritName().equals( "Coolant Pod" ) ) {
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
        Vector v = CurMech.GetLoadout().GetNonCore();
        abPlaceable a;

        if( CurMech.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            // check for coolant pods
            for( int i = 0; i < v.size(); i++ ) {
                a = (abPlaceable) v.get( i );
                if( a instanceof Equipment ) {
                    if( ((Equipment) a).GetCritName().equals( "Coolant Pod" ) ) {
                        NumPods++;
                    }
                }
            }
            // get the heat sink bonus
            BonusFromCP = (int) Math.ceil( (float) NumHS * ( (float) NumPods * 0.2f ) );
            if( BonusFromCP > MaxHSBonus ) { BonusFromCP = MaxHSBonus; }
            retval += BonusFromCP;
        }
        return retval;
    }
}
