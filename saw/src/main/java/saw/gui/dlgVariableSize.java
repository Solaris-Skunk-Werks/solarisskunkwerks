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
import components.CombatVehicle;
import javax.swing.SpinnerNumberModel;
import components.Equipment;

public class dlgVariableSize extends javax.swing.JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 7797500260179881452L;

    private Equipment CurEquip;
    private double CurTons;
    private CombatVehicle Owner;
    private double Max;
    private boolean result = true;

    /** Creates new form dlgVariableSize */
    public dlgVariableSize( java.awt.Frame parent, boolean modal, Equipment e, CombatVehicle c ) {
        super( parent, modal );
        initComponents();
        CurEquip = e;
        CurTons = CurEquip.GetTonnage();
        Owner = c;
        boolean hasItem = Owner.GetLoadout().Find(e) == 11 ? false : true;
        Max = Math.max((Owner.GetTonnage() - Owner.GetCurrentTons() + (hasItem ? CurEquip.GetTonnage() : 0.0)), 0);
        CurEquip.SetMaxTons(Max);
        SetState();
    }

    private void SetState() {
        spnTonnage.setModel( new javax.swing.SpinnerNumberModel(
            CurTons, CurEquip.GetMinTons(), Max, CurEquip.GetVariableIncrement() ) );
        setTitle( "Setting tonnage for " + CurEquip );
    }

    public boolean GetResult() {
        return result;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        btnOkay = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        spnTonnage = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        btnOkay.setText("Okay");
        btnOkay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkayActionPerformed(evt);
            }
        });
        jPanel1.add(btnOkay);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(jPanel1, gridBagConstraints);

        jLabel2.setText("Tonnage:");
        jPanel2.add(jLabel2);

        spnTonnage.setMinimumSize(new java.awt.Dimension(75, 20));
        spnTonnage.setPreferredSize(new java.awt.Dimension(75, 20));
        spnTonnage.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnTonnageStateChanged(evt);
            }
        });
        jPanel2.add(spnTonnage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jPanel2, gridBagConstraints);

        jLabel1.setText("Choose a tonnage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void spnTonnageStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnTonnageStateChanged
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnTonnage.getModel();
        javax.swing.JComponent editor = spnTonnage.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnTonnage.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if ( editor instanceof javax.swing.JSpinner.DefaultEditor ) {
                tf.setValue( spnTonnage.getValue() );
            }
            return;
        }

        CurTons = n.getNumber().doubleValue();
    }//GEN-LAST:event_spnTonnageStateChanged

    private void btnOkayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkayActionPerformed
        // now round to the nearest increment (up)
        double value = CommonTools.RoundHalfUp(CurTons); //Math.ceil( CurTons / CurEquip.GetVariableIncrement() ) * CurEquip.GetVariableIncrement();
        if( value > Max ) { value = Max; }
        if( value < CurEquip.GetMinTons() ) { value = CurEquip.GetMinTons(); }
        CurEquip.SetTonnage( value );
        result = true;
        setVisible( false );
    }//GEN-LAST:event_btnOkayActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        result = false;
        setVisible( false );
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOkay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSpinner spnTonnage;
    // End of variables declaration//GEN-END:variables

}
