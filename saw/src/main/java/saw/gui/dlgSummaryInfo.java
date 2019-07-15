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

import components.AvailableCode;
import components.CombatVehicle;
import javax.swing.JFrame;

public class dlgSummaryInfo extends javax.swing.JDialog {
    JFrame Parent;
    CombatVehicle CurVee;

    /** Creates new form dlgSummaryInfo */
    public dlgSummaryInfo(java.awt.Frame parent, boolean modal, CombatVehicle vee) {
        super(parent, modal);
        Parent = (JFrame) parent;
        initComponents();
        setResizable( false );
        setTitle( "Basic Vehicle Summary" );
        CurVee = vee;
        BuildLabels();
    }

    private void BuildLabels() {
        // sets the text for all the labels
        AvailableCode AC = CurVee.GetAvailability();
        lblName.setText( CurVee.GetName() );
        lblModel.setText( CurVee.GetModel() );
        lblActualProdYear.setText( "" + CurVee.GetYear() );
        switch( CurVee.GetLoadout().GetTechBase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                lblAvailability.setText( AC.GetISCombinedCode() );
                if( AC.WentExtinctIS() && AC.WasReIntrodIS() ) {
                    if( AC.GetISIntroDate() >= AC.GetISReIntroDate() ) {
                        lblEarliestProdYear.setText( "" + AC.GetISIntroDate() );
                    } else {
                        lblEarliestProdYear.setText( AC.GetISIntroDate() + " or " + AC.GetISReIntroDate() );
                    }
                } else {
                    lblEarliestProdYear.setText( "" + AC.GetISIntroDate() );
                }
                if( AC.WentExtinctIS() ) {
                    if( AC.GetISIntroDate() >= AC.GetISReIntroDate() ) {
                        lblExtinctBy.setText( "NA" );
                    } else {
                        lblExtinctBy.setText( "" + AC.GetISExtinctDate() );
                    }
                } else {
                    lblExtinctBy.setText( "NA" );
                }
                break;
            case AvailableCode.TECH_CLAN:
                lblAvailability.setText( AC.GetCLCombinedCode() );
                if( AC.WentExtinctCL() && AC.WasReIntrodCL() ) {
                    if( AC.GetCLIntroDate() >= AC.GetCLReIntroDate() ) {
                        lblEarliestProdYear.setText( "" + AC.GetCLIntroDate() );
                    } else {
                        lblEarliestProdYear.setText( AC.GetCLIntroDate() + " or " + AC.GetCLReIntroDate() );
                    }
                } else {
                    lblEarliestProdYear.setText( "" + AC.GetCLIntroDate() );
                }
                if( AC.WentExtinctCL() ) {
                    if( AC.GetCLIntroDate() >= AC.GetCLReIntroDate() ) {
                        lblExtinctBy.setText( "NA" );
                    } else {
                        lblExtinctBy.setText( "" + AC.GetCLExtinctDate() );
                    }
                } else {
                    lblExtinctBy.setText( "NA" );
                }
                break;
            case AvailableCode.TECH_BOTH:
                lblAvailability.setText( AC.GetBestCombinedCode() );
                lblEarliestProdYear.setText( "" + CurVee.GetYear() );
                lblExtinctBy.setText( "NA" );
                break;
        }

        lblDefensiveBV.setText( String.format( "%1$.2f", CurVee.GetDefensiveBV() ) );
        lblOffensiveBV.setText( String.format( "%1$.2f", CurVee.GetOffensiveBV() ) );
        lblTotalBV.setText( String.format( "%1$,d", CurVee.GetCurrentBV() ) );

        lblBaseChassisCost.setText( "" + (int) Math.floor( CurVee.GetBaseChassisCost() + 0.5 ) );
        lblBaseEngineCost.setText( "" + (int) Math.floor( CurVee.GetEngine().GetCost() + 0.5 ) );
        double BEC = CurVee.GetEquipCost();
        if( CurVee.UsingTC() ) {
            BEC += CurVee.GetTC().GetCost();
        }
        lblBaseEquipmentCost.setText( "" + (int) Math.floor( BEC + 0.5 ) );
        lblTotalDryCost.setText( "" + (int) Math.floor( CurVee.GetDryCost() + 0.5 ) );
        lblTotalCost.setText( "" + (int) Math.floor( CurVee.GetTotalCost() + 0.5 ) );

        lblTotalDryWeight.setText( CurVee.GetCurrentDryTons() + " tons" );
        lblTotalWeight.setText( CurVee.GetCurrentTons() + " tons" );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblModel = new javax.swing.JLabel();
        lblAvailability = new javax.swing.JLabel();
        lblEarliestProdYear = new javax.swing.JLabel();
        lblActualProdYear = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblBaseChassisCost = new javax.swing.JLabel();
        lblBaseEngineCost = new javax.swing.JLabel();
        lblBaseEquipmentCost = new javax.swing.JLabel();
        lblTotalDryCost = new javax.swing.JLabel();
        lblTotalCost = new javax.swing.JLabel();
        lblTotalDryWeight = new javax.swing.JLabel();
        lblTotalWeight = new javax.swing.JLabel();
        btnOkay = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        lblExtinctBy = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblDefensiveBV = new javax.swing.JLabel();
        lblOffensiveBV = new javax.swing.JLabel();
        lblTotalBV = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Mech Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setText("Model:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel2, gridBagConstraints);

        jLabel3.setText("Availability:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel3, gridBagConstraints);

        jLabel4.setText("Base Chassis Cost:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 4);
        getContentPane().add(jLabel4, gridBagConstraints);

        jLabel5.setText("Base Engine Cost:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel5, gridBagConstraints);

        jLabel6.setText("Base Equipment Cost:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel6, gridBagConstraints);

        jLabel7.setText("Total Cost (Dry):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel7, gridBagConstraints);

        jLabel8.setText("Total Cost:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 4);
        getContentPane().add(jLabel8, gridBagConstraints);

        jLabel9.setText("Total Weight (Dry):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 4);
        getContentPane().add(jLabel9, gridBagConstraints);

        jLabel10.setText("Total Weight:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel10, gridBagConstraints);

        jLabel11.setText("Earliest Production Year:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel11, gridBagConstraints);

        lblName.setText("lblName");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblName, gridBagConstraints);

        lblModel.setText("lblModel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblModel, gridBagConstraints);

        lblAvailability.setText("lblAvailability");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblAvailability, gridBagConstraints);

        lblEarliestProdYear.setText("lblEarliestProdYear");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblEarliestProdYear, gridBagConstraints);

        lblActualProdYear.setText("lblActualProdYear");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblActualProdYear, gridBagConstraints);

        jLabel12.setText("Production Year:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel12, gridBagConstraints);

        lblBaseChassisCost.setText("lblBaseChassisCost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 0);
        getContentPane().add(lblBaseChassisCost, gridBagConstraints);

        lblBaseEngineCost.setText("lblBaseEngineCost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblBaseEngineCost, gridBagConstraints);

        lblBaseEquipmentCost.setText("lblBaseEquipmentCost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblBaseEquipmentCost, gridBagConstraints);

        lblTotalDryCost.setText("lblTotalDryCost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblTotalDryCost, gridBagConstraints);

        lblTotalCost.setText("lblTotalCost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(lblTotalCost, gridBagConstraints);

        lblTotalDryWeight.setText("lblTotalDryWeight");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 0);
        getContentPane().add(lblTotalDryWeight, gridBagConstraints);

        lblTotalWeight.setText("lblTotalWeight");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(lblTotalWeight, gridBagConstraints);

        btnOkay.setText("Okay");
        btnOkay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkayActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        getContentPane().add(btnOkay, gridBagConstraints);

        jLabel13.setText("Extinct By:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 4);
        getContentPane().add(jLabel13, gridBagConstraints);

        lblExtinctBy.setText("lblExtinctBy");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(lblExtinctBy, gridBagConstraints);

        jLabel14.setText("Defensive BV:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 4);
        getContentPane().add(jLabel14, gridBagConstraints);

        jLabel15.setText("Offensive BV:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 4);
        getContentPane().add(jLabel15, gridBagConstraints);

        jLabel16.setText("Total BV:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 4);
        getContentPane().add(jLabel16, gridBagConstraints);

        lblDefensiveBV.setText("lblDefensiveBV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 0);
        getContentPane().add(lblDefensiveBV, gridBagConstraints);

        lblOffensiveBV.setText("lblOffensiveBV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(lblOffensiveBV, gridBagConstraints);

        lblTotalBV.setText("lblTotalBV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(lblTotalBV, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOkayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkayActionPerformed
        dispose();
    }//GEN-LAST:event_btnOkayActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOkay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblActualProdYear;
    private javax.swing.JLabel lblAvailability;
    private javax.swing.JLabel lblBaseChassisCost;
    private javax.swing.JLabel lblBaseEngineCost;
    private javax.swing.JLabel lblBaseEquipmentCost;
    private javax.swing.JLabel lblDefensiveBV;
    private javax.swing.JLabel lblEarliestProdYear;
    private javax.swing.JLabel lblExtinctBy;
    private javax.swing.JLabel lblModel;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOffensiveBV;
    private javax.swing.JLabel lblTotalBV;
    private javax.swing.JLabel lblTotalCost;
    private javax.swing.JLabel lblTotalDryCost;
    private javax.swing.JLabel lblTotalDryWeight;
    private javax.swing.JLabel lblTotalWeight;
    // End of variables declaration//GEN-END:variables
    
}
