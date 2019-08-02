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

import components.LocationIndex;
import components.abPlaceable;
import components.ifWeapon;
import filehandlers.Media;

public class dlgSplitCrits extends javax.swing.JDialog {

    abPlaceable ItemToPlace;
    ifMechForm Parent;
    int FirstLoc = 0,
        CritsPlaced = 0,
        FirstIndex = 0;
    boolean result = false;

    /** Creates new form dlgSplitCrits */
    public dlgSplitCrits( java.awt.Frame parent, boolean modal, abPlaceable p, int first, int findex ) throws Exception {
        super(parent, modal);
        Parent = (ifMechForm) parent;
        ItemToPlace = p;
        initComponents();
        setTitle( "Split Criticals" );
        setResizable( false );
        lblItemName.setText( "Allocating " + p.CritName() );
        FirstLoc = first;
        FirstIndex = findex;

        // see if we can actually allocate to the location in question
        switch( FirstLoc ) {
            case LocationIndex.MECH_LOC_HD:
                if( ! p.CanAllocHD() ) {
                    throw new Exception( p.CritName() + " cannot be allocated to the head." );
                }
                break;
            case LocationIndex.MECH_LOC_CT:
                if( ! p.CanAllocCT() ) {
                    throw new Exception( p.CritName() + " cannot be allocated to the center torso." );
                }
                break;
            case LocationIndex.MECH_LOC_LT: case LocationIndex.MECH_LOC_RT:
                if( ! p.CanAllocTorso() ) {
                    throw new Exception( p.CritName() + " cannot be allocated to a side torso." );
                }
                break;
            case LocationIndex.MECH_LOC_LA: case LocationIndex.MECH_LOC_RA:
                if( ! p.CanAllocArms() ) {
                    throw new Exception( p.CritName() + " cannot be allocated to the arms." );
                }
                break;
            case LocationIndex.MECH_LOC_LL: case LocationIndex.MECH_LOC_RL:
                if( ! p.CanAllocLegs() ) {
                    throw new Exception( p.CritName() + " cannot be allocated to the legs." );
                }
                break;
        }

        // FirstLoc tells us where it's starting.  We'll have to decode the
        // adjacent locations from there
        switch( FirstLoc ) {
        case LocationIndex.MECH_LOC_CT:
            lblFirstLoc.setText( "Center Torso" );
            cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Left Torso", "Right Torso" } ) );
            break;
        case LocationIndex.MECH_LOC_LT:
            lblFirstLoc.setText( "Left Torso" );
            if( ! p.CanAllocArms() &! p.CanAllocLegs() ) {
                cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Center Torso" } ) );
            } else {
                if( p instanceof ifWeapon ) {
                    if( ((ifWeapon) p).OmniRestrictActuators() && Parent.GetMech().GetActuators().LeftLowerInstalled() && Parent.GetMech().IsOmnimech() ) {
                        cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Center Torso", "Left Leg" } ) );
                    } else {
                        cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Center Torso", "Left Leg", "Left Arm" } ) );
                    }
                } else {
                    cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Center Torso", "Left Leg", "Left Arm" } ) );
                }
            }
            break;
        case LocationIndex.MECH_LOC_RT:
            lblFirstLoc.setText( "Right Torso" );
            if( ! p.CanAllocArms() &! p.CanAllocLegs() ) {
                cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Center Torso" } ) );
            } else {
                if( p instanceof ifWeapon ) {
                    if( ((ifWeapon) p).OmniRestrictActuators() && Parent.GetMech().GetActuators().RightLowerInstalled() && Parent.GetMech().IsOmnimech() ) {
                        cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Center Torso", "Right Leg" } ) );
                    } else {
                        cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Center Torso", "Right Leg", "Right Arm" } ) );
                    }
                } else {
                    cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Center Torso", "Right Leg", "Right Arm" } ) );
                }
            }
            break;
        case LocationIndex.MECH_LOC_LA:
            lblFirstLoc.setText( "Left Arm" );
            cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Left Torso" } ) );
            break;
        case LocationIndex.MECH_LOC_RA:
            lblFirstLoc.setText( "Right Arm" );
            cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Right Torso" } ) );
            break;
        case LocationIndex.MECH_LOC_LL:
            cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Left Torso" } ) );
            lblFirstLoc.setText( "Left Leg" );
            break;
        case LocationIndex.MECH_LOC_RL:
            cmbSecondLoc.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Right Torso" } ) );
            lblFirstLoc.setText( "Right Leg" );
            break;
        }
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

        cmbSecondLoc = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblFirstLoc = new javax.swing.JLabel();
        btnOkay = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblItemName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        cmbSecondLoc.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSecondLoc.setMaximumSize(new java.awt.Dimension(100, 20));
        cmbSecondLoc.setMinimumSize(new java.awt.Dimension(100, 20));
        cmbSecondLoc.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 0);
        getContentPane().add(cmbSecondLoc, gridBagConstraints);

        jLabel1.setText("First Location");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setText("Second Location");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        lblFirstLoc.setText("<First>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        getContentPane().add(lblFirstLoc, gridBagConstraints);

        btnOkay.setText("Okay");
        btnOkay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkayActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 8);
        getContentPane().add(btnOkay, gridBagConstraints);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        getContentPane().add(btnCancel, gridBagConstraints);

        jLabel3.setText("Choose a location for the rest");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(jLabel3, gridBagConstraints);

        jLabel4.setText("of the Item's critical slots:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(jLabel4, gridBagConstraints);

        lblItemName.setText("Allocating Ultra Autocannon/20");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        getContentPane().add(lblItemName, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOkayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkayActionPerformed
        // decode what happened based on the first location.
        switch( FirstLoc ) {
        case LocationIndex.MECH_LOC_CT:
            switch( cmbSecondLoc.getSelectedIndex() ) {
            case 0:
                try {
                    Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_LT );
                } catch( Exception e ) {
                    Media.Messager( this, e.getMessage() );
                    result = false;
                    setVisible( false );
                    return;
                }
                break;
            case 1:
                try {
                    Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_RT );
                } catch( Exception e ) {
                    Media.Messager( this, e.getMessage() );
                    result = false;
                    setVisible( false );
                    return;
                }
                break;
            }
            break;
        case LocationIndex.MECH_LOC_LT:
            switch( cmbSecondLoc.getSelectedIndex() ) {
            case 0:
                try {
                    Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_CT );
                } catch( Exception e ) {
                    Media.Messager( this, e.getMessage() );
                    result = false;
                    setVisible( false );
                    return;
                }
                break;
            case 1:
                try {
                    Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_LL );
                } catch( Exception e ) {
                    Media.Messager( this, e.getMessage() );
                    result = false;
                    setVisible( false );
                    return;
                }
                break;
            case 2:
                try {
                    Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_LA );
                } catch( Exception e ) {
                    Media.Messager( this, e.getMessage() );
                    result = false;
                    setVisible( false );
                    return;
                }
                break;
            }
            break;
        case LocationIndex.MECH_LOC_RT:
            switch( cmbSecondLoc.getSelectedIndex() ) {
            case 0:
                try {
                    Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_CT );
                } catch( Exception e ) {
                    Media.Messager( this, e.getMessage() );
                    result = false;
                    setVisible( false );
                    return;
                }
                break;
            case 1:
                try {
                    Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_RL );
                } catch( Exception e ) {
                    Media.Messager( this, e.getMessage() );
                    result = false;
                    setVisible( false );
                    return;
                }
                break;
            case 2:
                try {
                    Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_RA );
                } catch( Exception e ) {
                    Media.Messager( this, e.getMessage() );
                    result = false;
                    setVisible( false );
                    return;
                }
                break;
            }
            break;
        case LocationIndex.MECH_LOC_LA:
            // there's only one location we can split to.
            try {
                Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_LT );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                result = false;
                setVisible( false );
                return;
            }
            break;
        case LocationIndex.MECH_LOC_RA:
            // there's only one location we can split to.
            try {
                Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_RT );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                result = false;
                setVisible( false );
                return;
            }
            break;
        case LocationIndex.MECH_LOC_LL:
            // there's only one location we can split to.
            try {
                Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_LT );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                result = false;
                setVisible( false );
                return;
            }
            break;
        case LocationIndex.MECH_LOC_RL:
            // there's only one location we can split to.
            try {
                Parent.GetMech().GetLoadout().SplitAllocate( ItemToPlace, FirstLoc, FirstIndex, LocationIndex.MECH_LOC_RT );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                result = false;
                setVisible( false );
                return;
            }
            break;
        }
        result = true;
        setVisible( false );
    }//GEN-LAST:event_btnOkayActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // fix for items disappearing from the queue
        result = false;
        setVisible( false );
    }//GEN-LAST:event_btnCancelActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOkay;
    private javax.swing.JComboBox cmbSecondLoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblFirstLoc;
    private javax.swing.JLabel lblItemName;
    // End of variables declaration//GEN-END:variables
    
}
