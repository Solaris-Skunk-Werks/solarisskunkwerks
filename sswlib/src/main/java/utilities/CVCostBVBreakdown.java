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

public class CVCostBVBreakdown {
    private CombatVehicle CurUnit = null;
    private String NL = System.getProperty( "line.separator" );

    public CVCostBVBreakdown(CombatVehicle v) {
        CurUnit = v;
    }

    public String Render() {
        // this method returns a formated string with the cost/bv breakdown
        //  ----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
        String retval = "";
        retval += String.format( "Vehicle Name:   %1$-41s Tonnage:    %2$d", CurUnit.GetName() + " " + CurUnit.GetModel(), CurUnit.GetTonnage() ) + NL;
        retval += String.format( "Rules Level: %1$-41s Total Cost:    %2$,.0f", CommonTools.GetRulesLevelString( CurUnit.GetRulesLevel() ), CurUnit.GetTotalCost() ) + NL;
        retval += String.format( "Tech Base:   %1$-41s   Total BV:    %2$,d", CommonTools.GetTechbaseString( CurUnit.GetLoadout().GetTechBase() ), CurUnit.GetCurrentBV() ) + NL;
        retval += NL;
        retval +=                "Item                                            DefBV     OffBV             Cost" + NL;
        retval += String.format( "Internal Structure - %1$-22s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurUnit.GetIntStruc().toString(), CurUnit.GetIntStruc().GetDefensiveBV(), CurUnit.GetIntStruc().GetOffensiveBV(), CurUnit.GetIntStruc().GetCost() ) + NL;
        retval += String.format( "%1$-43s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Controls", 0.0, 0.0, CurUnit.GetControlsCost() ) + NL;
        retval += String.format( "%1$-43s %2$,6.0f    %3$,6.0f    %4$,16.2f", (CurUnit.IsVTOL()) ? "Rotors" : "Lift Equipment", 0.0, 0.0, CurUnit.GetLiftEquipmentCost() ) + NL;
        retval += String.format( "Engine - %1$-34s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurUnit.GetEngine().CritName(), CurUnit.GetEngine().GetDefensiveBV(), CurUnit.GetEngine().GetOffensiveBV(), CurUnit.GetEngine().GetCost() ) + NL;
        retval += String.format( "Heat Sinks - %1$-30s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurUnit.GetHeatSinks().LookupName(), CurUnit.GetHeatSinks().GetDefensiveBV(), CurUnit.GetHeatSinks().GetOffensiveBV(), CurUnit.GetHeatSinks().GetCost() ) + NL;
        if( CurUnit.GetJumpJets().GetNumJJ() > 0 ) {
            retval += String.format( "Jump Jets - %1$-31s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurUnit.GetJumpJets().LookupName(), CurUnit.GetJumpJets().GetDefensiveBV(), CurUnit.GetJumpJets().GetOffensiveBV(), CurUnit.GetJumpJets().GetCost() ) + NL;
        }
        if( ! CurUnit.GetEngine().IsNuclear() ) {
            retval += String.format( "%1$-43s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Power Amplifiers", 0.0f, 0.0f, CurUnit.GetLoadout().GetPowerAmplifier().GetCost() ) + NL;
        }
        if( CurUnit.IsPrimitive() && CurUnit.GetYear() < 2450 ) {
            retval += String.format( "Armor - %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurUnit.GetArmor().LookupName() + " (early)", CurUnit.GetArmor().GetDefensiveBV(), CurUnit.GetArmor().GetOffensiveBV(), CurUnit.GetArmor().GetCost() ) + NL;
        } else {
            retval += String.format( "Armor - %1$-35s %2$,6.0f    %3$,6.0f    %4$,16.2f", CurUnit.GetArmor().LookupName(), CurUnit.GetArmor().GetDefensiveBV(), CurUnit.GetArmor().GetOffensiveBV(), CurUnit.GetArmor().GetCost() ) + NL;
        }
        if ( CurUnit.isHasTurret1() )
            retval += String.format( "%1$-43s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Turret", CurUnit.GetLoadout().GetTurret().GetDefensiveBV(), CurUnit.GetLoadout().GetTurret().GetOffensiveBV(), CurUnit.GetLoadout().GetTurret().GetCost() ) + NL;
        if ( CurUnit.isHasTurret2() )
            retval += String.format( "%1$-43s %2$,6.0f    %3$,6.0f    %4$,16.2f", "Rear Turret", CurUnit.GetLoadout().GetRearTurret().GetDefensiveBV(), CurUnit.GetLoadout().GetRearTurret().GetOffensiveBV(), CurUnit.GetLoadout().GetRearTurret().GetCost() ) + NL;
        retval += NL;
        retval += GetEquipmentCostLines();
        retval += NL;
        retval += String.format( "Cost Multiplier                                                            %1$,1.3f", CurUnit.GetCostMult() ) + NL;
        retval += String.format( "Chassis Modifier                                                           %1$,1.3f", CurUnit.GetConfigMultiplier() ) + NL;
        retval += String.format( "Dry Cost                                                           %1$,13.0f", CurUnit.GetDryCost() ) + NL;
        retval += String.format( "Total Cost                                                         %1$,13.0f", CurUnit.GetTotalCost() ) + NL;
        retval += NL + NL;
        retval += "Defensive BV Calculation Breakdown" + NL;
        if( CurUnit.GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            retval += "(Note: BV Calculations include defensive BV for armored components.)" + NL;
        }
        retval += "________________________________________________________________________________" + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Total Armor Factor (" + CurUnit.GetArmor().GetArmorValue() + ") * Armor Type Modifier (" + CurUnit.GetArmor().GetBVTypeMult() + ") * 2.5", CurUnit.GetArmor().GetDefensiveBV() ) + NL;
        retval += "Total Structure Points (" + CurUnit.GetIntStruc().GetTotalPoints() + ") * Structure Type Modifier (" + CurUnit.GetIntStruc().GetBVTypeMult() + ")" + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "    * 1.5", CurUnit.GetIntStruc().GetDefensiveBV() ) + NL;
        //retval += String.format( "%1$-71s", "Vehicle Tonnage (" + CurUnit.GetTonnage() + ") ") + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Total Defensive BV of all Equipment", CurUnit.GetDefensiveEquipBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Excessive Ammunition Penalty", CurUnit.GetDefensiveExcessiveAmmoPenalty() ) + NL;
        //retval += String.format( "%1$-71s %2$,8.2f", "Explosive Ammunition Penalty", CurUnit.GetExplosiveAmmoPenalty() ) + NL;
        //retval += String.format( "%1$-71s %2$,8.2f", "Explosive Item Penalty  ", CurUnit.GetExplosiveWeaponPenalty() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Subtotal", CurUnit.GetUnmodifiedDefensiveBV() ) + NL;
        //retval += "Defensive Speed Factor Breakdown:" + NL;
        //retval += PrintDefensiveFactorCalculations();
        retval += String.format( "%1$-71s %2$,8.2f", "Vehicle Type Modifier ", CurUnit.GetDefensiveModifier() ) + NL;
        if ( CurUnit.GetChassisModifierString().length() > 0 )
            retval += String.format( "%1$-81s", CurUnit.GetChassisModifierString() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Total DBV (Subtotal * Vehicle Type Modifier * Defensive Factor (" + String.format( "%1$,4.2f", CurUnit.GetDefensiveFactor() ) + "))", CurUnit.GetDefensiveBV() ) + NL;
        retval += NL + NL;
        retval += "Offensive BV Calculation Breakdown" + NL;
        retval += "________________________________________________________________________________" + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Weapon BV Total WBV", CurUnit.GetWeaponBV() ) + NL;
        retval += PrintHeatAdjustedWeaponBV();
        retval += String.format( "%1$-71s %2$,8.2f", "Non-Heat Equipment Total NHBV", CurUnit.GetNonHeatEquipBV() ) + NL;
        retval += PrintNonHeatEquipBV();
        retval += String.format( "%1$-71s %2$,8.2f", "Excessive Ammunition Penalty", CurUnit.GetExcessiveAmmoPenalty() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Vehicle Tonnage Bonus", CurUnit.GetTonnageBV() ) + NL;
        retval += String.format( "%1$-71s %2$,8.2f", "Subtotal (WBV + NHBV + Tonnage Bonus)", CurUnit.GetUnmodifiedOffensiveBV() ) + NL;
        retval += "Offensive Speed Factor Breakdown:" + NL;
        retval += PrintOffensiveFactorCalculations();
        retval += String.format( "%1$-71s %2$,8.2f", "Total OBV (Subtotal * Offensive Speed Factor (" + CurUnit.GetOffensiveFactor() + "))", CurUnit.GetOffensiveBV() ) + NL;
        retval += NL + NL;
        retval += String.format( "%1$-73s %2$,6d", "Total Battle Value (DBV + OBV, round off)", CurUnit.GetCurrentBV() );
        return retval;
    }

    private String GetEquipmentCostLines() {
        // returns a block of lines for the cost breakdown
        String retval = "";
        ArrayList v = CurUnit.GetLoadout().GetNonCore();
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
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", a.CritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
            }
        }
        if( CurUnit.UsingTC() ) {
            //retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", "Targeting Computer", CurUnit.GetTC().GetDefensiveBV(), CurUnit.GetTC().GetOffensiveBV(), CurUnit.GetTC().GetCost() ) + NL;
        }
        if( CurUnit.HasBlueShield() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurUnit.GetBlueShield().CritName(), CurUnit.GetBlueShield().GetDefensiveBV(), CurUnit.GetBlueShield().GetOffensiveBV(), CurUnit.GetBlueShield().GetCost() ) + NL;
        }
        if( CurUnit.GetLoadout().HasSupercharger() ) {
            retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.2f", CurUnit.GetLoadout().GetSupercharger().CritName(), CurUnit.GetLoadout().GetSupercharger().GetDefensiveBV(), CurUnit.GetLoadout().GetSupercharger().GetOffensiveBV(), CurUnit.GetLoadout().GetSupercharger().GetCost() ) + NL;
        }
        return retval;
    }

    public String PrintNonHeatEquipBV() {
        // return the BV of all offensive equipment
        ArrayList v = CurUnit.GetLoadout().GetNonCore();
        abPlaceable a = null;
        String retval = "";

        for( int i = 0; i < v.size(); i++ ) {
            if( ! ( v.get( i ) instanceof ifWeapon ) ) {
                a = ((abPlaceable) v.get( i ));
                retval += String.format( "%1$-71s %2$,8.2f", "    -> " + a.CritName(), a.GetOffensiveBV() ) + NL;
            }
        }
        return retval;
    }

    public String PrintHeatAdjustedWeaponBV() {
        double foreBV = 0.0, rearBV = 0.0;
        boolean UseRear = false, TC = CurUnit.UsingTC(), UseAESMod = false;
        String retval = "";

        ArrayList<ArrayList<abPlaceable>> FrontRear = new ArrayList<ArrayList<abPlaceable>>();
        FrontRear.add(CurUnit.GetLoadout().GetFrontItems());
        FrontRear.add(CurUnit.GetLoadout().GetRearItems());
        
        ArrayList<ArrayList<abPlaceable>> Locations = new ArrayList<ArrayList<abPlaceable>>();
        Locations.add(CurUnit.GetLoadout().GetLeftItems());
        Locations.add(CurUnit.GetLoadout().GetRightItems());
        Locations.add(CurUnit.GetLoadout().GetTurret1Items());
        Locations.add(CurUnit.GetLoadout().GetTurret2Items());
        
        // is it even worth performing all this?
        if( CurUnit.GetLoadout().GetNonCore().size() <= 0 ) {
            // nope
            return retval;
        }

        // find out the total BV of rear and forward firing weapons
        for ( abPlaceable w : CurUnit.GetLoadout().GetFrontItems() ) {
            if ( w instanceof ifWeapon )
                foreBV += w.GetCurOffensiveBV(false, TC, false);
        }
        for ( abPlaceable w : CurUnit.GetLoadout().GetRearItems() ) {
            if ( w instanceof ifWeapon )
                rearBV += w.GetCurOffensiveBV(true, TC, false);
        }
        if( rearBV > foreBV ) { UseRear = true; }

        //Re-calculate values now based on rear adjustment
        for ( ArrayList<abPlaceable> list : FrontRear ) {
            for ( abPlaceable w : list ) {
                if ( w instanceof ifWeapon)
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + w.CritName(), w.GetCurOffensiveBV( UseRear, TC, UseAESMod ) ) + NL;
            }
        }
        
        //Sides and Turrets are full value no matter what
        for ( ArrayList<abPlaceable> list : Locations ) {
            for ( abPlaceable w : list ) {
                if ( w instanceof ifWeapon)
                    retval += String.format( "%1$-71s %2$,8.2f", "    -> " + w.CritName(), w.GetCurOffensiveBV( false, TC, UseAESMod ) ) + NL;
            }
        }
        
        return retval;
    }

    public String PrintDefensiveFactorCalculations() {
        // returns the defensive factor for this mech based on it's highest
        // target number for speed.
        String retval = "";

        // subtract one since we're indexing an array
        int RunMP = CurUnit.getCruiseMP() - 1;
        int JumpMP = 0;

        // this is a safeguard for using MASC on an incredibly speedy chassis
        // there is currently no way to get a bonus higher anyway.
        if( RunMP > 29 ) { RunMP = 29; }
        // safeguard for low walk mp (Modular Armor, for instance)
        if( RunMP < 0 ) { RunMP = 0; }

        // Get the defensive factors for jumping and running movement
        double ground = Mech.DefensiveFactor[RunMP];
        double jump = 0.0;

        MechModifier m = CurUnit.GetTotalModifiers( true, true );

        retval += "    Maximum Ground Movement Modifier: " + String.format( "%1$,.2f", ground ) + NL;
        retval += "    Maximum Jump Movement Modifier:   " + String.format( "%1$,.2f", jump ) + NL;
        retval += "    Defensive Speed Factor Bonus from Equipment: " + String.format( "%1$,.2f", m.DefensiveBonus() ) + NL;
        retval += "    Minimum Defensive Speed Factor:   " + String.format( "%1$,.2f", m.MinimumDefensiveBonus() ) + NL;
        retval += "    (Max of Run or Jump) + DSF Bonus = " + String.format( "%1$,.2f", CurUnit.GetDefensiveFactor() ) + NL;

        return retval;
    }

    public String PrintOffensiveFactorCalculations() {
        String retval = "";

        double temp;
        temp = (double) (CurUnit.getFlankMP() + ( Math.floor( 0 * 0.5 + 0.5 )  ) - 5.0 );
        retval += "    Adjusted Flank MP (" + CurUnit.getFlankMP( ) + ") + ( Adjusted Jumping MP (" + 0 + ") / 2 ) - 5 = " + String.format( "%1$,.2f", CurUnit.getFlankMP() + ( Math.floor( 0 * 0.5 + 0.5 )  ) - 5.0 ) + NL;
        retval += "    " + String.format( "%1$,.2f", temp ) + " / 10 + 1 = " + String.format( "%1$.3f", ( temp * 0.1 + 1.0 ) ) + NL;
        temp = temp * 0.1 + 1.0;
        retval += "    " + String.format( "%1$,.2f", temp ) + " ^ 1.2 = " + (double) Math.floor( ( Math.pow( temp, 1.2 ) ) * 100 + 0.5 ) / 100 + " (rounded off to two digits)" + NL;

        return retval;
    }
}
