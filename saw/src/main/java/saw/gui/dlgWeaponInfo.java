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

package saw.gui;

import common.CommonTools;
import components.*;

public class dlgWeaponInfo extends javax.swing.JDialog {
    abPlaceable CurItem;

    /** Creates new form dlgItemInfo */
    public dlgWeaponInfo(java.awt.Frame parent, boolean modal, abPlaceable a) {
        super(parent, modal);
        initComponents();
        CurItem = a;
        //setResizable( false );
        SetState();
        pack();
    }

    private void SetState() {
        // fills in all the information for the given weapon or ammo
        ifWeapon w = null;
        Ammunition a = null;
        String restrict = "";
        AvailableCode AC = CurItem.GetAvailability();
        if( CurItem instanceof Ammunition ) {
            setTitle( "Ammunition Information" );
            a = (Ammunition) CurItem;
            lblName.setText( a.ActualName() );
            lblType.setText( "Ammo" );
            lblHeat.setText( "--" );
            if( a.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                lblDamage.setText( a.GetDamageShort() + "/msl" );
            } else if( a.GetWeaponClass() == ifWeapon.W_ARTILLERY ) {
                lblDamage.setText( a.GetDamageShort() + "A" );
            } else if( a.GetDamageShort() == a.GetDamageMedium() && a.GetDamageShort() == a.GetDamageLong() ) {
                lblDamage.setText( "" + a.GetDamageShort() );
            } else {
                lblDamage.setText( a.GetDamageShort() + "/" + a.GetDamageMedium() + "/" + a.GetDamageLong() );
            }
            if( a.GetLongRange() < 1 ) {
                if( a.GetMediumRange() < 1 ) {
                    if( a.GetWeaponClass() == ifWeapon.W_ARTILLERY ) {
                        lblRange.setText( a.GetShortRange() + " boards" );
                    } else {
                        lblRange.setText( a.GetShortRange() + "" );
                    }
                } else {
                    lblRange.setText( a.GetMinRange() + "/" + a.GetShortRange() + "/" + a.GetMediumRange() + "/-" );
                }
            } else {
                lblRange.setText( a.GetMinRange() + "/" + a.GetShortRange() + "/" + a.GetMediumRange() + "/" + a.GetLongRange() );
            }
            lblAmmo.setText( "" + a.GetLotSize() );
            lblToHit.setText( a.GetToHitShort() + "/" + a.GetToHitMedium() + "/" + a.GetToHitLong() );
            lblFCSClass.setText( ifMissileGuidance.FCS_NAMES[a.GetFCSType()] );
            lblSpecials.setText( "--" );
            lblTonnage.setText( "" + ((abPlaceable) a).GetTonnage() );
            lblCrits.setText( "" + ((abPlaceable) a).NumCVSpaces() );
            lblCost.setText( "" + CommonTools.RoundFractionalCost( ((abPlaceable) a).GetCost() ) );
            lblBV.setText( CommonTools.GetAggregateReportBV( (abPlaceable) a ) );

            if( ! ((abPlaceable) a).CanAllocHD() ) {
                restrict += "No Head, ";
            }
            if( ! ((abPlaceable) a).CanAllocCT() ) {
                restrict += "No Center Torso, ";
            }
            if( ! ((abPlaceable) a).CanAllocTorso() ) {
                restrict += "No Side Torsos, ";
            }
            if( ! ((abPlaceable) a).CanAllocArms() ) {
                restrict += "No Arms, ";
            }
            if( ! ((abPlaceable) a).CanAllocLegs() ) {
                restrict += "No Legs, ";
            }
            if( ((abPlaceable) a).CanSplit() ) {
                restrict += "Can Split, ";
            }
        } else {
            setTitle( "Weapon Information" );
            w = (ifWeapon) CurItem;
            lblName.setText( ((abPlaceable) w).ActualName() );
            lblType.setText( w.GetType() );
            lblHeat.setText( "" + w.GetHeat() );
            if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                lblDamage.setText( w.GetDamageShort() + "/msl" );
            } else if( w.GetWeaponClass() == ifWeapon.W_ARTILLERY ) {
                lblDamage.setText( w.GetDamageShort() + "A" );
            } else if( w.GetDamageShort() == w.GetDamageMedium() && w.GetDamageShort() == w.GetDamageLong() ) {
                if( w.IsUltra() || w.IsRotary() ) {
                    lblDamage.setText( w.GetDamageShort() + "/shot" );
                } else {
                    lblDamage.setText( "" + w.GetDamageShort() );
                }
            } else {
                lblDamage.setText( w.GetDamageShort() + "/" + w.GetDamageMedium() + "/" + w.GetDamageLong() );
            }
            if( w.GetRangeLong() < 1 ) {
                if( w.GetRangeMedium() < 1 ) {
                    if( w.GetWeaponClass() == ifWeapon.W_ARTILLERY ) {
                        lblRange.setText( w.GetRangeShort() + " boards" );
                    } else {
                        lblRange.setText( w.GetRangeShort() + "" );
                    }
                } else {
                    lblRange.setText( w.GetRangeMin() + "/" + w.GetRangeShort() + "/" + w.GetRangeMedium() + "/-" );
                }
            } else {
                lblRange.setText( w.GetRangeMin() + "/" + w.GetRangeShort() + "/" + w.GetRangeMedium() + "/" + w.GetRangeLong() );
            }
            if( w.HasAmmo() ) {
                lblAmmo.setText( "" + w.GetAmmoLotSize() );
            } else {
                lblAmmo.setText( "--" );
            }
            String tohit = "";
            if( w.GetToHitShort() >= 0 ) {
                tohit += "+";
            }
            tohit += w.GetToHitShort() + "/";
            if( w.GetToHitMedium() >= 0 ) {
                tohit += "+";
            }
            tohit += w.GetToHitMedium() + "/";
            if( w.GetToHitLong() >= 0 ) {
                tohit += "+";
            }
            tohit += w.GetToHitLong();
            lblToHit.setText( tohit );
            lblFCSClass.setText( ifMissileGuidance.FCS_NAMES[w.GetFCSType()] );
            lblSpecials.setText( w.GetSpecials() );
            lblTonnage.setText( "" + ((abPlaceable) w).GetTonnage() );
            lblCrits.setText( "" + ((abPlaceable) w).NumCrits() );
            lblCost.setText( "" + ((abPlaceable) w).GetCost() );
            lblBV.setText( CommonTools.GetAggregateReportBV( (abPlaceable) w ) );

            if( ! ((abPlaceable) w).CanAllocHD() ) {
                restrict += "No Head, ";
            }
            if( ! ((abPlaceable) w).CanAllocCT() ) {
                restrict += "No Center Torso, ";
            }
            if( ! ((abPlaceable) w).CanAllocTorso() ) {
                restrict += "No Side Torsos, ";
            }
            if( ! ((abPlaceable) w).CanAllocArms() ) {
                restrict += "No Arms, ";
            }
            if( ! ((abPlaceable) w).CanAllocLegs() ) {
                restrict += "No Legs, ";
            }
            if( ((abPlaceable) w).CanSplit() ) {
                restrict += "Can Split, ";
            }
            if( w.OmniRestrictActuators() ) {
                restrict += "Omni Actuator Restricted";
            }
        }

        switch( AC.GetTechBase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                pnlClanAvailability.setVisible( false );
                break;
            case AvailableCode.TECH_CLAN:
                pnlISAvailability.setVisible( false );
                break;
        }

        lblISTechRating.setText( "" + AC.GetISTechRating() );
        lblISAVSL.setText( "" + AC.GetISSLCode() );
        lblISAVSW.setText( "" + AC.GetISSWCode() );
        lblISAVCI.setText( "" + AC.GetISCICode() );
        lblISIntro.setText( AC.GetISIntroDate() + " (" + AC.GetISIntroFaction() + ")" );
        if( AC.WentExtinctIS() ) {
            lblISExtinct.setText( "" + AC.GetISExtinctDate() );
        } else {
            lblISExtinct.setText( "--" );
        }
        if( AC.WasReIntrodIS() ) {
            lblISReIntro.setText( AC.GetISReIntroDate() + " (" + AC.GetISReIntroFaction() + ")" );
        } else {
            lblISReIntro.setText( "--" );
        }
        if( AC.Is_ISPrototype() ){
            String temp = "Status: R&D Start Date: " + AC.GetISRandDStartDate() + " (" + AC.GetISRandDFaction() + "), ";
            temp += "Prototype: " + AC.GetISPrototypeDate() + " (" + AC.GetISPrototypeFaction() + ")";
            lblISExtraInfo.setText( temp );
        } else {
            lblISExtraInfo.setText( "Status: Production Equipment" );
        }

        lblClanTechRating.setText( "" + AC.GetCLTechRating() );
        lblClanAVSL.setText( "" + AC.GetCLSLCode() );
        lblClanAVSW.setText( "" + AC.GetCLSWCode() );
        lblClanAVCI.setText( "" + AC.GetCLCICode() );
        lblClanIntro.setText( AC.GetCLIntroDate() + " (" + AC.GetCLIntroFaction() + ")" );
        if( AC.WentExtinctCL() ) {
            lblClanExtinct.setText( "" + AC.GetCLExtinctDate() );
        } else {
            lblClanExtinct.setText( "--" );
        }
        if( AC.WasReIntrodCL() ) {
            lblClanReIntro.setText( AC.GetCLReIntroDate() + " (" + AC.GetCLReIntroFaction() + ")" );
        } else {
            lblClanReIntro.setText( "--" );
        }
        if( AC.Is_CLPrototype() ){
            String temp = "Status: R&D Start Date: " + AC.GetCLRandDStartDate() + " (" + AC.GetCLRandDFaction() + "), ";
            temp += "Prototype: " + AC.GetCLPrototypeDate() + " (" + AC.GetCLPrototypeFaction() + ")";
            lblClanExtraInfo.setText( temp );
        } else {
            lblClanExtraInfo.setText( "Status: Production Equipment" );
        }

        lblRulesBM.setText( CommonTools.GetRulesLevelString( AC.GetRulesLevel_BM() ) );
        lblRulesIM.setText( CommonTools.GetRulesLevelString( AC.GetRulesLevel_IM() ) );

        if( restrict.length() > 0 ) {
            if( restrict.endsWith( ", ") ) {
                restrict = restrict.substring( 0, restrict.length() - 2 );
            }
            lblMountingRestrictions.setText( restrict );
        } else {
            lblMountingRestrictions.setText( "None" );
        }
        lblBookRef.setText( CurItem.BookReference() );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblInfoHeat = new javax.swing.JLabel();
        lblInfoRange = new javax.swing.JLabel();
        lblInfoAmmo = new javax.swing.JLabel();
        lblInfoTonnage = new javax.swing.JLabel();
        lblInfoCrits = new javax.swing.JLabel();
        lblInfoSpecial = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblHeat = new javax.swing.JLabel();
        lblRange = new javax.swing.JLabel();
        lblAmmo = new javax.swing.JLabel();
        lblTonnage = new javax.swing.JLabel();
        lblCrits = new javax.swing.JLabel();
        lblSpecials = new javax.swing.JLabel();
        lblInfoType = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        lblInfoDamage = new javax.swing.JLabel();
        lblDamage = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        lblInfoCost = new javax.swing.JLabel();
        lblInfoBV = new javax.swing.JLabel();
        lblCost = new javax.swing.JLabel();
        lblBV = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblRulesBM = new javax.swing.JLabel();
        lblRulesIM = new javax.swing.JLabel();
        pnlClanAvailability = new javax.swing.JPanel();
        lblInfoAVCI = new javax.swing.JLabel();
        lblInfoAVSW = new javax.swing.JLabel();
        lblInfoAVSL = new javax.swing.JLabel();
        lblClanAVSL = new javax.swing.JLabel();
        lblClanAVSW = new javax.swing.JLabel();
        lblClanAVCI = new javax.swing.JLabel();
        lblInfoReIntro = new javax.swing.JLabel();
        lblInfoExtinct = new javax.swing.JLabel();
        lblInfoIntro = new javax.swing.JLabel();
        lblClanReIntro = new javax.swing.JLabel();
        lblClanExtinct = new javax.swing.JLabel();
        lblClanIntro = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblClanExtraInfo = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblClanTechRating = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        pnlISAvailability = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblISExtraInfo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblISAVSL = new javax.swing.JLabel();
        lblISAVSW = new javax.swing.JLabel();
        lblISAVCI = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblISIntro = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblISReIntro = new javax.swing.JLabel();
        lblISExtinct = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblISTechRating = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        lblToHit = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lblFCSClass = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblMountingRestrictions = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        lblBookRef = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lblInfoHeat.setText("Heat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        getContentPane().add(lblInfoHeat, gridBagConstraints);

        lblInfoRange.setText("Range");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        getContentPane().add(lblInfoRange, gridBagConstraints);

        lblInfoAmmo.setText("Ammo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        getContentPane().add(lblInfoAmmo, gridBagConstraints);

        lblInfoTonnage.setText("Tonnage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        getContentPane().add(lblInfoTonnage, gridBagConstraints);

        lblInfoCrits.setText("Crits");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        getContentPane().add(lblInfoCrits, gridBagConstraints);

        lblInfoSpecial.setText("Specials");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 4);
        getContentPane().add(lblInfoSpecial, gridBagConstraints);

        lblName.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblName.setText("Hyper Assault Gauss 40");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 3);
        getContentPane().add(lblName, gridBagConstraints);

        lblHeat.setText("999");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblHeat, gridBagConstraints);

        lblRange.setText("999/999/999/999");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblRange, gridBagConstraints);

        lblAmmo.setText("999");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblAmmo, gridBagConstraints);

        lblTonnage.setText("999.9");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblTonnage, gridBagConstraints);

        lblCrits.setText("999");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblCrits, gridBagConstraints);

        lblSpecials.setText("AI/H/X/C/F/C5/20");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 4);
        getContentPane().add(lblSpecials, gridBagConstraints);

        lblInfoType.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        getContentPane().add(lblInfoType, gridBagConstraints);

        lblType.setText("DB");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblType, gridBagConstraints);

        lblInfoDamage.setText("Damage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        getContentPane().add(lblInfoDamage, gridBagConstraints);

        lblDamage.setText("999/999/999");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblDamage, gridBagConstraints);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(btnClose, gridBagConstraints);

        jSeparator1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator1, gridBagConstraints);

        lblInfoCost.setText("Cost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblInfoCost, gridBagConstraints);

        lblInfoBV.setText("BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        getContentPane().add(lblInfoBV, gridBagConstraints);

        lblCost.setText("9999999");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 4);
        getContentPane().add(lblCost, gridBagConstraints);

        lblBV.setText("999");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 4);
        getContentPane().add(lblBV, gridBagConstraints);

        jSeparator2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator2, gridBagConstraints);

        jLabel1.setText("Rules Level (BattleMech)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 2);
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setText("Rules Level (IndustrialMech)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 2);
        getContentPane().add(jLabel2, gridBagConstraints);

        lblRulesBM.setText("Tournament Legal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        getContentPane().add(lblRulesBM, gridBagConstraints);

        lblRulesIM.setText("Tournament Legal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        getContentPane().add(lblRulesIM, gridBagConstraints);

        pnlClanAvailability.setLayout(new java.awt.GridBagLayout());

        lblInfoAVCI.setText("Availability (CI)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlClanAvailability.add(lblInfoAVCI, gridBagConstraints);

        lblInfoAVSW.setText("Availability (SW)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlClanAvailability.add(lblInfoAVSW, gridBagConstraints);

        lblInfoAVSL.setText("Availability (AoW/SL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlClanAvailability.add(lblInfoAVSL, gridBagConstraints);

        lblClanAVSL.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        pnlClanAvailability.add(lblClanAVSL, gridBagConstraints);

        lblClanAVSW.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        pnlClanAvailability.add(lblClanAVSW, gridBagConstraints);

        lblClanAVCI.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        pnlClanAvailability.add(lblClanAVCI, gridBagConstraints);

        lblInfoReIntro.setText("Reintroduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 2);
        pnlClanAvailability.add(lblInfoReIntro, gridBagConstraints);

        lblInfoExtinct.setText("Extinction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 2);
        pnlClanAvailability.add(lblInfoExtinct, gridBagConstraints);

        lblInfoIntro.setText("Introduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 2);
        pnlClanAvailability.add(lblInfoIntro, gridBagConstraints);

        lblClanReIntro.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlClanAvailability.add(lblClanReIntro, gridBagConstraints);

        lblClanExtinct.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlClanAvailability.add(lblClanExtinct, gridBagConstraints);

        lblClanIntro.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlClanAvailability.add(lblClanIntro, gridBagConstraints);

        jLabel3.setText("Clan Availability");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlClanAvailability.add(jLabel3, gridBagConstraints);

        lblClanExtraInfo.setText("Status: R&D Start Date: 9999 (CJF), Prototype: 9999 (CJF)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlClanAvailability.add(lblClanExtraInfo, gridBagConstraints);

        jLabel18.setText("Tech Rating");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlClanAvailability.add(jLabel18, gridBagConstraints);

        lblClanTechRating.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        pnlClanAvailability.add(lblClanTechRating, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(pnlClanAvailability, gridBagConstraints);

        jSeparator3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator3, gridBagConstraints);

        pnlISAvailability.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Inner Sphere Availability");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlISAvailability.add(jLabel4, gridBagConstraints);

        lblISExtraInfo.setText("Status: R&D Start Date: 9999 (CJF), Prototype: 9999 (CJF)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlISAvailability.add(lblISExtraInfo, gridBagConstraints);

        jLabel6.setText("Availability (AoW/SL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlISAvailability.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Availability (SW)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlISAvailability.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Availability (CI)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlISAvailability.add(jLabel8, gridBagConstraints);

        lblISAVSL.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        pnlISAvailability.add(lblISAVSL, gridBagConstraints);

        lblISAVSW.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        pnlISAvailability.add(lblISAVSW, gridBagConstraints);

        lblISAVCI.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        pnlISAvailability.add(lblISAVCI, gridBagConstraints);

        jLabel12.setText("Reintroduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 2);
        pnlISAvailability.add(jLabel12, gridBagConstraints);

        lblISIntro.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlISAvailability.add(lblISIntro, gridBagConstraints);

        jLabel14.setText("Extinction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 2);
        pnlISAvailability.add(jLabel14, gridBagConstraints);

        jLabel15.setText("Introduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 2);
        pnlISAvailability.add(jLabel15, gridBagConstraints);

        lblISReIntro.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlISAvailability.add(lblISReIntro, gridBagConstraints);

        lblISExtinct.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlISAvailability.add(lblISExtinct, gridBagConstraints);

        jLabel20.setText("Tech Rating");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlISAvailability.add(jLabel20, gridBagConstraints);

        lblISTechRating.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        pnlISAvailability.add(lblISTechRating, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(pnlISAvailability, gridBagConstraints);

        jSeparator4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator4, gridBagConstraints);

        lblToHit.setText("+10/+10/+10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        getContentPane().add(lblToHit, gridBagConstraints);

        jLabel21.setText("To-Hit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        getContentPane().add(jLabel21, gridBagConstraints);

        lblFCSClass.setText("Artemis IV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        getContentPane().add(lblFCSClass, gridBagConstraints);

        jLabel23.setText("Missile FCS Class");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        getContentPane().add(jLabel23, gridBagConstraints);

        lblMountingRestrictions.setText("No HD, No CT, No Side Torsos, No Arms, No Legs, Omni Actuator Restricted");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        getContentPane().add(lblMountingRestrictions, gridBagConstraints);

        jLabel5.setText("Mounting Restrictions:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        getContentPane().add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator5, gridBagConstraints);

        jLabel9.setText("Book Reference:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        getContentPane().add(jLabel9, gridBagConstraints);

        lblBookRef.setText("jLabel10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(lblBookRef, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel lblAmmo;
    private javax.swing.JLabel lblBV;
    private javax.swing.JLabel lblBookRef;
    private javax.swing.JLabel lblClanAVCI;
    private javax.swing.JLabel lblClanAVSL;
    private javax.swing.JLabel lblClanAVSW;
    private javax.swing.JLabel lblClanExtinct;
    private javax.swing.JLabel lblClanExtraInfo;
    private javax.swing.JLabel lblClanIntro;
    private javax.swing.JLabel lblClanReIntro;
    private javax.swing.JLabel lblClanTechRating;
    private javax.swing.JLabel lblCost;
    private javax.swing.JLabel lblCrits;
    private javax.swing.JLabel lblDamage;
    private javax.swing.JLabel lblFCSClass;
    private javax.swing.JLabel lblHeat;
    private javax.swing.JLabel lblISAVCI;
    private javax.swing.JLabel lblISAVSL;
    private javax.swing.JLabel lblISAVSW;
    private javax.swing.JLabel lblISExtinct;
    private javax.swing.JLabel lblISExtraInfo;
    private javax.swing.JLabel lblISIntro;
    private javax.swing.JLabel lblISReIntro;
    private javax.swing.JLabel lblISTechRating;
    private javax.swing.JLabel lblInfoAVCI;
    private javax.swing.JLabel lblInfoAVSL;
    private javax.swing.JLabel lblInfoAVSW;
    private javax.swing.JLabel lblInfoAmmo;
    private javax.swing.JLabel lblInfoBV;
    private javax.swing.JLabel lblInfoCost;
    private javax.swing.JLabel lblInfoCrits;
    private javax.swing.JLabel lblInfoDamage;
    private javax.swing.JLabel lblInfoExtinct;
    private javax.swing.JLabel lblInfoHeat;
    private javax.swing.JLabel lblInfoIntro;
    private javax.swing.JLabel lblInfoRange;
    private javax.swing.JLabel lblInfoReIntro;
    private javax.swing.JLabel lblInfoSpecial;
    private javax.swing.JLabel lblInfoTonnage;
    private javax.swing.JLabel lblInfoType;
    private javax.swing.JLabel lblMountingRestrictions;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblRange;
    private javax.swing.JLabel lblRulesBM;
    private javax.swing.JLabel lblRulesIM;
    private javax.swing.JLabel lblSpecials;
    private javax.swing.JLabel lblToHit;
    private javax.swing.JLabel lblTonnage;
    private javax.swing.JLabel lblType;
    private javax.swing.JPanel pnlClanAvailability;
    private javax.swing.JPanel pnlISAvailability;
    // End of variables declaration//GEN-END:variables
    
}
