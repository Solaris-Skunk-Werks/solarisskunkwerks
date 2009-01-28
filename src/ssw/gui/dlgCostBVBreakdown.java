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

package ssw.gui;

import java.util.Vector;
import ssw.CommonTools;
import ssw.Constants;
import ssw.components.*;

public class dlgCostBVBreakdown extends javax.swing.JDialog {

    private Mech CurMech;
    private String NL;

    /** Creates new form dlgCostBVBreakdown */
    public dlgCostBVBreakdown(java.awt.Frame parent, boolean modal, Mech m ) {
        super(parent, modal);
        initComponents();
        CurMech = m;
        NL = System.getProperty( "line.separator" );
        txtCostBV.setText( BuildBreakdown() );
        txtCostBV.setCaretPosition( 0 );
        setTitle( CurMech.GetName() + " Cost/BV Breakdown" );
    }

    private String BuildBreakdown() {
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
        retval += String.format( "Total Cost                                                         %1$,13.0f", CurMech.GetTotalCost() ) + NL;
        retval += NL + NL;
        retval += "Defensive BV Calculation Breakdown" + NL;
        if( CurMech.GetRulesLevel() == Constants.EXPERIMENTAL ) {
            retval += "(Note: BV Calculations include defensive BV for armored components.)" + NL;
        }
        retval += "________________________________________________________________________________" + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Total Armor Factor (" + CurMech.GetArmor().GetArmorValue() + ") * Armor Type Modifier (" + CurMech.GetArmor().GetBVTypeMult() + ") * 2.5", CurMech.GetArmor().GetDefensiveBV() ) + NL;
        retval += "Total Structure Points (" + CurMech.GetIntStruc().GetTotalPoints() + ") * Structure Type Modifier (" + CurMech.GetIntStruc().GetBVTypeMult() + ") *" + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "    Engine Type Modifier (" + CurMech.GetEngine().GetBVMult() + ") * 1.5", CurMech.GetIntStruc().GetDefensiveBV() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Mech Tonnage (" + CurMech.GetTonnage() + ") * Gyro Type Modifer (" + CurMech.GetGyro().GetBVTypeMult() + ")", CurMech.GetGyro().GetDefensiveBV() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Total Defensive BV of all Equipment", CurMech.GetDefensiveEquipBV() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Explosive Ammunition Penalty", CurMech.GetExplosiveAmmoPenalty() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Explosive Weapon Penalty", CurMech.GetExplosiveWeaponPenalty() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Subtotal", CurMech.GetUnmodifiedDefensiveBV() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Total DBV (Subtotal * Defensive Factor (" + CurMech.GetDefensiveFactor() + "))", CurMech.GetDefensiveBV() ) + NL;
        retval += NL + NL;
        retval += "Offensive BV Calculation Breakdown" + NL;
        retval += "________________________________________________________________________________" + NL;
        retval += "Heat Efficiency (6 + " + CurMech.GetHeatSinks().TotalDissipation() + " - " + CurMech.GetBVMovementHeat() + ") = "+ ( 6 + CurMech.GetHeatSinks().TotalDissipation() - CurMech.GetBVMovementHeat() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Adjusted Weapon BV Total WBV", CurMech.GetHeatAdjustedWeaponBV() ) + NL;
        retval += PrintHeatAdjustedWeaponBV();
        retval += String.format( "%1$-73s %2$,6.2f", "Non-Heat Equipment Total NHBV", CurMech.GetNonHeatEquipBV() ) + NL;
        retval += PrintNonHeatEquipBV();
        retval += String.format( "%1$-73s %2$,6.2f", "Excessive Ammunition Penalty", CurMech.GetExcessiveAmmoPenalty() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Mech Tonnage Bonus", CurMech.GetTonnageBV() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Subtotal (WBV + NHBV - Excessive Ammo + Tonnage Bonus)", CurMech.GetUnmodifiedOffensiveBV() ) + NL;
        retval += String.format( "%1$-73s %2$,6.2f", "Total OBV (Subtotal * Offensive Factor (" + CurMech.GetOffensiveFactor() + "))", CurMech.GetOffensiveBV() ) + NL;
        retval += NL + NL;
        if( CurMech.GetCockpit().BVMod() != 1.0f ) {
            retval += String.format( "%1$-73s %2$,6.2f", CurMech.GetCockpit().GetCritName() + " modifier", CurMech.GetCockpit().BVMod() ) + NL;
            retval += String.format( "%1$-73s %2$,6d", "Total Battle Value ((DBV + OBV) * " + CurMech.GetCockpit().GetCritName() + " modifier, round off)", CurMech.GetCurrentBV() );
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
            if( a instanceof MissileWeapon ) {
                if( ((MissileWeapon) a).IsUsingArtemis() ) {
                    retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", a.GetCritName() + " w/ Artemis IV FCS", a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
                } else {
                    retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", a.GetCritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
                }
            } else {
                retval += String.format( "%1$-46s %2$,6.0f    %3$,6.0f    %4$,13.0f", a.GetCritName(), a.GetDefensiveBV(), a.GetOffensiveBV(), a.GetCost() ) + NL;
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
        return retval;
    }

    public String PrintHeatAdjustedWeaponBV() {
        Vector v = CurMech.GetLoadout().GetNonCore(), wep = new Vector();
        float result = 0.0f, foreBV = 0.0f, rearBV = 0.0f;
        boolean UseRear = false;
        String retval = "";
        abPlaceable a = null;

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
        int wheat = CurMech.GetBVWeaponHeat();
        float TCTotal = 0.0f;

        // find out the total BV of rear and forward firing weapons
        for( int i = 0; i < wep.size(); i++ ) {
            if( ((abPlaceable) wep.get( i )).IsMountedRear() ) {
                rearBV += ((abPlaceable) wep.get( i )).GetOffensiveBV();
            } else {
                foreBV += ((abPlaceable) wep.get( i )).GetOffensiveBV();
            }
        }
        if( rearBV > foreBV ) { UseRear = true; }

        // see if we need to run heat calculations
        if( heff - wheat >= 0 ) {
            // no need for extensive calculations, return the weapon BV
            for( int i = 0; i < wep.size(); i++ ) {
                a = ((abPlaceable) wep.get( i ));
                retval += String.format( "%1$-73s %2$,6.2f", "    -> " + a.GetCritName(), a.GetCurOffensiveBV( UseRear ) ) + NL;
                result += a.GetCurOffensiveBV( UseRear );
                if( ((ifWeapon) a).IsTCCapable() ) {
                    TCTotal += a.GetCurOffensiveBV( UseRear );
                }
            }
            if( CurMech.GetLoadout().UsingTC() ) {
                TCTotal = TCTotal * 0.25f;
                result += TCTotal;
                retval += String.format( "%1$-73s %2$,6.2f", "    -> Targeting Computer BV", TCTotal ) + NL;
            }
            return retval;
        }

        // Sort the weapon list
        abPlaceable[] sorted = CurMech.SortWeapons( wep, UseRear );

        // calculate the BV of the weapons based on heat
        int curheat = 0;
        for( int i = 0; i < sorted.length; i++ ) {
            a = ((abPlaceable) sorted[i]);
            if( curheat < heff ) {
                retval += String.format( "%1$-73s %2$,6.2f", "    -> " + a.GetCritName(), a.GetCurOffensiveBV( UseRear ) ) + NL;
                result += a.GetCurOffensiveBV( UseRear );
                if( ((ifWeapon) a).IsTCCapable() ) {
                    TCTotal += a.GetCurOffensiveBV( UseRear );
                }
            } else {
                if( ((ifWeapon) sorted[i]).GetBVHeat() <= 0 ) {
                    retval += String.format( "%1$-73s %2$,6.2f", "    -> " + a.GetCritName(), a.GetCurOffensiveBV( UseRear ) ) + NL;
                    result += a.GetCurOffensiveBV( UseRear );
                    if( ((ifWeapon) a).IsTCCapable() ) {
                        TCTotal += a.GetCurOffensiveBV( UseRear );
                    }
                } else {
                    retval += String.format( "%1$-73s %2$,6.2f", "    -> " + a.GetCritName(), a.GetCurOffensiveBV( UseRear ) * 0.5f ) + NL;
                    result += a.GetCurOffensiveBV( UseRear ) * 0.5f;
                    if( ((ifWeapon) a).IsTCCapable() ) {
                        TCTotal += a.GetCurOffensiveBV( UseRear ) * 0.5f;
                    }
                }
            }
            curheat += ((ifWeapon) a).GetBVHeat();
        }
        if( CurMech.GetLoadout().UsingTC() ) {
            TCTotal = TCTotal * 0.25f;
            result += TCTotal;
            retval += String.format( "%1$-73s %2$,6.2f", "    -> Targeting Computer BV", TCTotal ) + NL;
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
                retval += String.format( "%1$-73s %2$,6.2f", "    -> " + a.GetCritName(), a.GetOffensiveBV() ) + NL;
            }
        }
        return retval;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtCostBV = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 4);
        getContentPane().add(jPanel1, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(600, 300));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(600, 300));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(600, 300));

        txtCostBV.setFont(new java.awt.Font("Lucida Sans Typewriter", 0, 12));
        txtCostBV.setText("################################################################################");
        txtCostBV.setMaximumSize(new java.awt.Dimension(575, 100000));
        txtCostBV.setMinimumSize(new java.awt.Dimension(575, 300));
        txtCostBV.setPreferredSize(new java.awt.Dimension(575, 300));
        jScrollPane1.setViewportView(txtCostBV);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
    dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane txtCostBV;
    // End of variables declaration//GEN-END:variables

}
