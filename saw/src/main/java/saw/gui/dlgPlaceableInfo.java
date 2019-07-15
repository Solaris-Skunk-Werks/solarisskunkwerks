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
import components.AvailableCode;
import components.abPlaceable;

public class dlgPlaceableInfo extends javax.swing.JDialog {
    abPlaceable CurItem;

    /** Creates new form dlgPlaceableInfo */
    public dlgPlaceableInfo( java.awt.Frame parent, boolean modal, abPlaceable a ) {
        super(parent, modal);
        initComponents();
        CurItem = a;
        setTitle( "Item Information" );
        SetState();
        pack();
    }

    private void SetState() {
        abPlaceable a = CurItem;
        AvailableCode AC = a.GetAvailability();

        switch( AC.GetTechBase() ){
            case AvailableCode.TECH_INNER_SPHERE:
                pnlClan.setVisible( false );
                lblTechRating.setText( AC.GetISTechRating() + "" );
                break;
            case AvailableCode.TECH_CLAN:
                pnlInnerSphere.setVisible( false );
                lblTechRating.setText( AC.GetCLTechRating() + "" );
                break;
            case AvailableCode.TECH_BOTH:
                lblTechRating.setText( AC.GetISTechRating() + " (IS) / " + AC.GetCLTechRating() + " (CL)" );
                break;
        }

        lblName.setText( a.ActualName() );
        lblTonnage.setText( "" + a.GetTonnage() );
//        lblCost.setText( String.format("", a.GetCost() )"" + CommonTools.RoundFractionalCost( a.GetCost() ) );
        lblCost.setText( String.format( "%1$,.4f", a.GetCost() ) );
        lblBV.setText( CommonTools.GetAggregateReportBV( a ) );
        lblISACSL.setText( "" + AC.GetISSLCode() );
        lblISACSW.setText( "" + AC.GetISSWCode() );
        lblISACCI.setText( "" + AC.GetISCICode() );
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
        lblClanACSL.setText( "" + AC.GetCLSLCode() );
        lblClanACSW.setText( "" + AC.GetCLSWCode() );
        lblClanACCI.setText( "" + AC.GetCLCICode() );
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
        lblBookRef.setText( a.BookReference() );
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

        jLabel4 = new javax.swing.JLabel();
        lblTechRating = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblBV = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblTonnage = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnClose = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        lblCost = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblRulesBM = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lblRulesIM = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        pnlClan = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lblClanExtraInfo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblClanACCI = new javax.swing.JLabel();
        lblClanACSW = new javax.swing.JLabel();
        lblClanACSL = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblClanReIntro = new javax.swing.JLabel();
        lblClanExtinct = new javax.swing.JLabel();
        lblClanIntro = new javax.swing.JLabel();
        pnlInnerSphere = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        lblISACSL = new javax.swing.JLabel();
        lblISACSW = new javax.swing.JLabel();
        lblISACCI = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lblISIntro = new javax.swing.JLabel();
        lblISExtinct = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblISReIntro = new javax.swing.JLabel();
        lblISExtraInfo = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        lblBookRef = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Tech Rating");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        getContentPane().add(jLabel4, gridBagConstraints);

        lblTechRating.setText("X (IS)/X (CL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        getContentPane().add(lblTechRating, gridBagConstraints);

        lblName.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblName.setText("Hyper Assault Gauss-40");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        getContentPane().add(lblName, gridBagConstraints);

        jLabel10.setText("Tonnage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 4);
        getContentPane().add(jLabel10, gridBagConstraints);

        lblBV.setText("000.00/000.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        getContentPane().add(lblBV, gridBagConstraints);

        jLabel12.setText("BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        getContentPane().add(jLabel12, gridBagConstraints);

        lblTonnage.setText("000.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 6);
        getContentPane().add(lblTonnage, gridBagConstraints);

        jSeparator1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator1, gridBagConstraints);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(btnClose, gridBagConstraints);

        jLabel14.setText("Cost:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(jLabel14, gridBagConstraints);

        lblCost.setText("000,000,000,000.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(lblCost, gridBagConstraints);

        jLabel6.setText("Rules Level (BattleMech):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(jLabel6, gridBagConstraints);

        lblRulesBM.setText("Experimental");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(lblRulesBM, gridBagConstraints);

        jSeparator2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator2, gridBagConstraints);

        lblRulesIM.setText("Experimental");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(lblRulesIM, gridBagConstraints);

        jLabel13.setText("Rules Level (IndustrialMech):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(jLabel13, gridBagConstraints);

        jSeparator3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator3, gridBagConstraints);

        pnlClan.setLayout(new java.awt.GridBagLayout());

        jLabel11.setText("Clan Availability");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlClan.add(jLabel11, gridBagConstraints);

        lblClanExtraInfo.setText("Status: R&D Start Date: 9999 (CJF), Prototype: 9999 (CJF)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlClan.add(lblClanExtraInfo, gridBagConstraints);

        jLabel3.setText("Availability (CI)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlClan.add(jLabel3, gridBagConstraints);

        jLabel2.setText("Availability (SW)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlClan.add(jLabel2, gridBagConstraints);

        jLabel1.setText("Availability (AOW/SL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlClan.add(jLabel1, gridBagConstraints);

        lblClanACCI.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 6);
        pnlClan.add(lblClanACCI, gridBagConstraints);

        lblClanACSW.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 6);
        pnlClan.add(lblClanACSW, gridBagConstraints);

        lblClanACSL.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 6);
        pnlClan.add(lblClanACSL, gridBagConstraints);

        jLabel9.setText("Reintroduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 2);
        pnlClan.add(jLabel9, gridBagConstraints);

        jLabel8.setText("Extinction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 2);
        pnlClan.add(jLabel8, gridBagConstraints);

        jLabel7.setText("Introduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 2);
        pnlClan.add(jLabel7, gridBagConstraints);

        lblClanReIntro.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlClan.add(lblClanReIntro, gridBagConstraints);

        lblClanExtinct.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlClan.add(lblClanExtinct, gridBagConstraints);

        lblClanIntro.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlClan.add(lblClanIntro, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(pnlClan, gridBagConstraints);

        pnlInnerSphere.setLayout(new java.awt.GridBagLayout());

        jLabel18.setText("Availability (AOW/SL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlInnerSphere.add(jLabel18, gridBagConstraints);

        lblISACSL.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 6);
        pnlInnerSphere.add(lblISACSL, gridBagConstraints);

        lblISACSW.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 6);
        pnlInnerSphere.add(lblISACSW, gridBagConstraints);

        lblISACCI.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 6);
        pnlInnerSphere.add(lblISACCI, gridBagConstraints);

        jLabel24.setText("Introduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 2);
        pnlInnerSphere.add(jLabel24, gridBagConstraints);

        jLabel25.setText("Extinction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 2);
        pnlInnerSphere.add(jLabel25, gridBagConstraints);

        jLabel26.setText("Reintroduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 2);
        pnlInnerSphere.add(jLabel26, gridBagConstraints);

        lblISIntro.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlInnerSphere.add(lblISIntro, gridBagConstraints);

        lblISExtinct.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlInnerSphere.add(lblISExtinct, gridBagConstraints);

        jLabel20.setText("Availability (CI)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlInnerSphere.add(jLabel20, gridBagConstraints);

        jLabel19.setText("Availability (SW)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlInnerSphere.add(jLabel19, gridBagConstraints);

        lblISReIntro.setText("9999 (TH)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlInnerSphere.add(lblISReIntro, gridBagConstraints);

        lblISExtraInfo.setText("Status: R&D Start Date: 9999 (CJF), Prototype: 9999 (CJF)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlInnerSphere.add(lblISExtraInfo, gridBagConstraints);

        jLabel16.setText("Inner Sphere Availability");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlInnerSphere.add(jLabel16, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(pnlInnerSphere, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator4, gridBagConstraints);

        jLabel5.setText("Book Reference:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(jLabel5, gridBagConstraints);

        lblBookRef.setText("jLabel15");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
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
    private javax.swing.JLabel lblBV;
    private javax.swing.JLabel lblBookRef;
    private javax.swing.JLabel lblClanACCI;
    private javax.swing.JLabel lblClanACSL;
    private javax.swing.JLabel lblClanACSW;
    private javax.swing.JLabel lblClanExtinct;
    private javax.swing.JLabel lblClanExtraInfo;
    private javax.swing.JLabel lblClanIntro;
    private javax.swing.JLabel lblClanReIntro;
    private javax.swing.JLabel lblCost;
    private javax.swing.JLabel lblISACCI;
    private javax.swing.JLabel lblISACSL;
    private javax.swing.JLabel lblISACSW;
    private javax.swing.JLabel lblISExtinct;
    private javax.swing.JLabel lblISExtraInfo;
    private javax.swing.JLabel lblISIntro;
    private javax.swing.JLabel lblISReIntro;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblRulesBM;
    private javax.swing.JLabel lblRulesIM;
    private javax.swing.JLabel lblTechRating;
    private javax.swing.JLabel lblTonnage;
    private javax.swing.JPanel pnlClan;
    private javax.swing.JPanel pnlInnerSphere;
    // End of variables declaration//GEN-END:variables

}
