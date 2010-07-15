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
import components.ifMechLoadout;
import components.EquipmentCollection;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

public class dlgSelectiveAllocate extends javax.swing.JDialog {

    ifMechForm Parent;
    abPlaceable Item = null;
    EquipmentCollection Items = null;
    ifMechLoadout CurLoadout;
    int total = 0;
    int[] Crits = { 0, 0, 0, 0, 0, 0, 0, 0 };
    int[] Alloc = { 0, 0, 0, 0, 0, 0, 0, 0 };

    /** Creates new form dlgSelectiveAllocate */
    public dlgSelectiveAllocate( java.awt.Frame parent, boolean modal, abPlaceable p ) {
        super(parent, modal);
        initComponents();
        setTitle( "Selective Allocation" );
        setResizable( false );
        Parent = (ifMechForm) parent;
        Item = p;
        CurLoadout = Parent.GetMech().GetLoadout();
        InitializeSingle();
    }

    public dlgSelectiveAllocate( java.awt.Frame parent, boolean modal, EquipmentCollection e ) {
        super(parent, modal);
        initComponents();
        setTitle( "Selective Allocation" );
        setResizable( false );
        Parent = (ifMechForm) parent;
        Items = e;
        CurLoadout = Parent.GetMech().GetLoadout();
        InitializeCollection();
    }

    private void InitializeSingle() {
        if( Parent.GetMech().IsQuad() ) {
            ((TitledBorder) pnlRA.getBorder()).setTitle( "RFL" );
            ((TitledBorder) pnlLA.getBorder()).setTitle( "LFL" );
            ((TitledBorder) pnlRL.getBorder()).setTitle( "RRL" );
            ((TitledBorder) pnlLL.getBorder()).setTitle( "LRL" );
        } else {
            ((TitledBorder) pnlRA.getBorder()).setTitle( "RA" );
            ((TitledBorder) pnlLA.getBorder()).setTitle( "LA" );
            ((TitledBorder) pnlRL.getBorder()).setTitle( "RL" );
            ((TitledBorder) pnlLL.getBorder()).setTitle( "LL" );
        }

        // first, get the available crits for each location
        Crits[LocationIndex.MECH_LOC_HD] = CurLoadout.FreeCrits( CurLoadout.GetHDCrits() );
        Crits[LocationIndex.MECH_LOC_CT] = CurLoadout.FreeCrits( CurLoadout.GetCTCrits() );
        Crits[LocationIndex.MECH_LOC_LT] = CurLoadout.FreeCrits( CurLoadout.GetLTCrits() );
        Crits[LocationIndex.MECH_LOC_RT] = CurLoadout.FreeCrits( CurLoadout.GetRTCrits() );
        Crits[LocationIndex.MECH_LOC_LA] = CurLoadout.FreeCrits( CurLoadout.GetLACrits() );
        Crits[LocationIndex.MECH_LOC_RA] = CurLoadout.FreeCrits( CurLoadout.GetRACrits() );
        Crits[LocationIndex.MECH_LOC_LL] = CurLoadout.FreeCrits( CurLoadout.GetLLCrits() );
        Crits[LocationIndex.MECH_LOC_RL] = CurLoadout.FreeCrits( CurLoadout.GetRLCrits() );

        lblAllocateItem.setText( "Allocating " + Item.CritName() );
        lblItemCrits.setText( Item.NumPlaced() + " of " + Item.NumCrits() + " Allocated" );
        lblHDQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_HD] );
        lblCTQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_CT] );
        lblLTQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_LT] );
        lblRTQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_RT] );
        lblLAQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_LA] );
        lblRAQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_RA] );
        lblLLQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_LL] );
        lblRLQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_RL] );

        // now set all the spinners
        spnHDCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_HD], 1 ) );
        spnCTCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_CT], 1 ) );
        spnLTCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_LT], 1 ) );
        spnRTCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_RT], 1 ) );
        spnLACrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_LA], 1 ) );
        spnRACrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_RA], 1 ) );
        spnLLCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_LL], 1 ) );
        spnRLCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_RL], 1 ) );

        // set the total to the item's currently placed crits
        total = Item.NumPlaced();
    }

    private void InitializeCollection() {
        abPlaceable CurItem = Items.GetType();
        int size = CurItem.NumCrits();
        
        if( Parent.GetMech().IsQuad() ) {
            ((TitledBorder) pnlRA.getBorder()).setTitle( "RFL" );
            ((TitledBorder) pnlLA.getBorder()).setTitle( "LFL" );
            ((TitledBorder) pnlRL.getBorder()).setTitle( "RRL" );
            ((TitledBorder) pnlLL.getBorder()).setTitle( "LRL" );
        } else {
            ((TitledBorder) pnlRA.getBorder()).setTitle( "RA" );
            ((TitledBorder) pnlLA.getBorder()).setTitle( "LA" );
            ((TitledBorder) pnlRL.getBorder()).setTitle( "RL" );
            ((TitledBorder) pnlLL.getBorder()).setTitle( "LL" );
        }

        // first, get the available crits for each location
        Crits[LocationIndex.MECH_LOC_HD] = GetNumSlots( size, CurLoadout.FreeCrits( CurLoadout.GetHDCrits() ) );
        Crits[LocationIndex.MECH_LOC_CT] = GetNumSlots( size, CurLoadout.FreeCrits( CurLoadout.GetCTCrits() ) );
        Crits[LocationIndex.MECH_LOC_LT] = GetNumSlots( size, CurLoadout.FreeCrits( CurLoadout.GetLTCrits() ) );
        Crits[LocationIndex.MECH_LOC_RT] = GetNumSlots( size, CurLoadout.FreeCrits( CurLoadout.GetRTCrits() ) );
        Crits[LocationIndex.MECH_LOC_LA] = GetNumSlots( size, CurLoadout.FreeCrits( CurLoadout.GetLACrits() ) );
        Crits[LocationIndex.MECH_LOC_RA] = GetNumSlots( size, CurLoadout.FreeCrits( CurLoadout.GetRACrits() ) );
        Crits[LocationIndex.MECH_LOC_LL] = GetNumSlots( size, CurLoadout.FreeCrits( CurLoadout.GetLLCrits() ) );
        Crits[LocationIndex.MECH_LOC_RL] = GetNumSlots( size, CurLoadout.FreeCrits( CurLoadout.GetRLCrits() ) );

        lblAllocateItem.setText( "Allocating " + CurItem.CritName() );
        lblItemCrits.setText( "0 of " + Items.GetSize() + " Allocated" );
        lblHDQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_HD] );
        lblCTQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_CT] );
        lblLTQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_LT] );
        lblRTQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_RT] );
        lblLAQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_LA] );
        lblRAQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_RA] );
        lblLLQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_LL] );
        lblRLQuant.setText( "0 of " + Crits[LocationIndex.MECH_LOC_RL] );

        // now set all the spinners
        spnHDCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_HD], 1 ) );
        spnCTCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_CT], 1 ) );
        spnLTCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_LT], 1 ) );
        spnRTCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_RT], 1 ) );
        spnLACrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_LA], 1 ) );
        spnRACrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_RA], 1 ) );
        spnLLCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_LL], 1 ) );
        spnRLCrits.setModel( new javax.swing.SpinnerNumberModel( 0, 0, Crits[LocationIndex.MECH_LOC_RL], 1 ) );

        // set the total to the item's currently placed crits
        total = 0;
    }

    private int GetNumSlots( int size, int free ) {
        return (int) Math.floor( ((double) free) / ((double) size) );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlHD = new javax.swing.JPanel();
        lblHDQuant = new javax.swing.JLabel();
        spnHDCrits = new javax.swing.JSpinner();
        pnlCT = new javax.swing.JPanel();
        lblCTQuant = new javax.swing.JLabel();
        spnCTCrits = new javax.swing.JSpinner();
        pnlLT = new javax.swing.JPanel();
        lblLTQuant = new javax.swing.JLabel();
        spnLTCrits = new javax.swing.JSpinner();
        pnlRT = new javax.swing.JPanel();
        lblRTQuant = new javax.swing.JLabel();
        spnRTCrits = new javax.swing.JSpinner();
        pnlLA = new javax.swing.JPanel();
        lblLAQuant = new javax.swing.JLabel();
        spnLACrits = new javax.swing.JSpinner();
        pnlRA = new javax.swing.JPanel();
        lblRAQuant = new javax.swing.JLabel();
        spnRACrits = new javax.swing.JSpinner();
        pnlLL = new javax.swing.JPanel();
        lblLLQuant = new javax.swing.JLabel();
        spnLLCrits = new javax.swing.JSpinner();
        pnlRL = new javax.swing.JPanel();
        lblRLQuant = new javax.swing.JLabel();
        spnRLCrits = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        btnOkay = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblItemCrits = new javax.swing.JLabel();
        lblAllocateItem = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlHD.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "HD", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlHD.setLayout(new java.awt.BorderLayout());

        lblHDQuant.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHDQuant.setText("00 of 00");
        pnlHD.add(lblHDQuant, java.awt.BorderLayout.CENTER);

        spnHDCrits.setMaximumSize(new java.awt.Dimension(50, 25));
        spnHDCrits.setMinimumSize(new java.awt.Dimension(50, 25));
        spnHDCrits.setPreferredSize(new java.awt.Dimension(50, 25));
        spnHDCrits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnHDCritsStateChanged(evt);
            }
        });
        pnlHD.add(spnHDCrits, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        getContentPane().add(pnlHD, gridBagConstraints);

        pnlCT.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "CT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlCT.setLayout(new java.awt.BorderLayout());

        lblCTQuant.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCTQuant.setText("00 of 00");
        pnlCT.add(lblCTQuant, java.awt.BorderLayout.CENTER);

        spnCTCrits.setMaximumSize(new java.awt.Dimension(50, 25));
        spnCTCrits.setMinimumSize(new java.awt.Dimension(50, 25));
        spnCTCrits.setPreferredSize(new java.awt.Dimension(50, 25));
        spnCTCrits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCTCritsStateChanged(evt);
            }
        });
        pnlCT.add(spnCTCrits, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        getContentPane().add(pnlCT, gridBagConstraints);

        pnlLT.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLT.setLayout(new java.awt.BorderLayout());

        lblLTQuant.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLTQuant.setText("00 of 00");
        pnlLT.add(lblLTQuant, java.awt.BorderLayout.CENTER);

        spnLTCrits.setMaximumSize(new java.awt.Dimension(50, 25));
        spnLTCrits.setMinimumSize(new java.awt.Dimension(50, 25));
        spnLTCrits.setPreferredSize(new java.awt.Dimension(50, 25));
        spnLTCrits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLTCritsStateChanged(evt);
            }
        });
        pnlLT.add(spnLTCrits, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        getContentPane().add(pnlLT, gridBagConstraints);

        pnlRT.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "RT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRT.setLayout(new java.awt.BorderLayout());

        lblRTQuant.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRTQuant.setText("00 of 00");
        pnlRT.add(lblRTQuant, java.awt.BorderLayout.CENTER);

        spnRTCrits.setMaximumSize(new java.awt.Dimension(50, 25));
        spnRTCrits.setMinimumSize(new java.awt.Dimension(50, 25));
        spnRTCrits.setPreferredSize(new java.awt.Dimension(50, 25));
        spnRTCrits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRTCritsStateChanged(evt);
            }
        });
        pnlRT.add(spnRTCrits, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        getContentPane().add(pnlRT, gridBagConstraints);

        pnlLA.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LA", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLA.setLayout(new java.awt.BorderLayout());

        lblLAQuant.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLAQuant.setText("00 of 00");
        pnlLA.add(lblLAQuant, java.awt.BorderLayout.CENTER);

        spnLACrits.setMaximumSize(new java.awt.Dimension(50, 25));
        spnLACrits.setMinimumSize(new java.awt.Dimension(50, 25));
        spnLACrits.setPreferredSize(new java.awt.Dimension(50, 25));
        spnLACrits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLACritsStateChanged(evt);
            }
        });
        pnlLA.add(spnLACrits, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(pnlLA, gridBagConstraints);

        pnlRA.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "RA", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRA.setLayout(new java.awt.BorderLayout());

        lblRAQuant.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRAQuant.setText("00 of 00");
        pnlRA.add(lblRAQuant, java.awt.BorderLayout.CENTER);

        spnRACrits.setMaximumSize(new java.awt.Dimension(50, 25));
        spnRACrits.setMinimumSize(new java.awt.Dimension(50, 25));
        spnRACrits.setPreferredSize(new java.awt.Dimension(50, 25));
        spnRACrits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRACritsStateChanged(evt);
            }
        });
        pnlRA.add(spnRACrits, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        getContentPane().add(pnlRA, gridBagConstraints);

        pnlLL.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LL", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLL.setLayout(new java.awt.BorderLayout());

        lblLLQuant.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLLQuant.setText("00 of 00");
        lblLLQuant.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlLL.add(lblLLQuant, java.awt.BorderLayout.CENTER);

        spnLLCrits.setMaximumSize(new java.awt.Dimension(50, 25));
        spnLLCrits.setMinimumSize(new java.awt.Dimension(50, 25));
        spnLLCrits.setPreferredSize(new java.awt.Dimension(50, 25));
        spnLLCrits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLLCritsStateChanged(evt);
            }
        });
        pnlLL.add(spnLLCrits, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        getContentPane().add(pnlLL, gridBagConstraints);

        pnlRL.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "RL", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRL.setLayout(new java.awt.BorderLayout());

        lblRLQuant.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRLQuant.setText("00 of 00");
        pnlRL.add(lblRLQuant, java.awt.BorderLayout.CENTER);

        spnRLCrits.setMaximumSize(new java.awt.Dimension(50, 25));
        spnRLCrits.setMinimumSize(new java.awt.Dimension(50, 25));
        spnRLCrits.setPreferredSize(new java.awt.Dimension(50, 25));
        spnRLCrits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRLCritsStateChanged(evt);
            }
        });
        pnlRL.add(spnRLCrits, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        getContentPane().add(pnlRL, gridBagConstraints);

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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel1, gridBagConstraints);

        lblItemCrits.setText("00 of 00 Allocated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 0);
        getContentPane().add(lblItemCrits, gridBagConstraints);

        lblAllocateItem.setText("Allocating Endo-Steel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        getContentPane().add(lblAllocateItem, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // surprise!  we don't do anything here.
        dispose();
}//GEN-LAST:event_btnCancelActionPerformed

    private void btnOkayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkayActionPerformed
        // allocate the item into the loadout with the given values
        try {
            for( int i = 0; i < Alloc[LocationIndex.MECH_LOC_HD]; i++ ) {
                if( Item == null ) {
                    CurLoadout.AddToHD( Items.GetType() );
                } else {
                    CurLoadout.AddToHD( Item );
                }
            }
            for( int i = 0; i < Alloc[LocationIndex.MECH_LOC_CT]; i++ ) {
                if( Item == null ) {
                    CurLoadout.AddToCT( Items.GetType() );
                } else {
                    CurLoadout.AddToCT( Item );
                }
            }
            for( int i = 0; i < Alloc[LocationIndex.MECH_LOC_LT]; i++ ) {
                if( Item == null ) {
                    CurLoadout.AddToLT( Items.GetType() );
                } else {
                    CurLoadout.AddToLT( Item );
                }
            }
            for( int i = 0; i < Alloc[LocationIndex.MECH_LOC_RT]; i++ ) {
                if( Item == null ) {
                    CurLoadout.AddToRT( Items.GetType() );
                } else {
                    CurLoadout.AddToRT( Item );
                }
            }
            for( int i = 0; i < Alloc[LocationIndex.MECH_LOC_LA]; i++ ) {
                if( Item == null ) {
                    CurLoadout.AddToLA( Items.GetType() );
                } else {
                    CurLoadout.AddToLA( Item );
                }
            }
            for( int i = 0; i < Alloc[LocationIndex.MECH_LOC_RA]; i++ ) {
                if( Item == null ) {
                    CurLoadout.AddToRA( Items.GetType() );
                } else {
                    CurLoadout.AddToRA( Item );
                }
            }
            for( int i = 0; i < Alloc[LocationIndex.MECH_LOC_LL]; i++ ) {
                if( Item == null ) {
                    CurLoadout.AddToLL( Items.GetType() );
                } else {
                    CurLoadout.AddToLL( Item );
                }
            }
            for( int i = 0; i < Alloc[LocationIndex.MECH_LOC_RL]; i++ ) {
                if( Item == null ) {
                    CurLoadout.AddToRL( Items.GetType() );
                } else {
                    CurLoadout.AddToRL( Item );
                }
            }
        } catch ( Exception e ) {
            // found a problem, report it
        }

        dispose();
    }//GEN-LAST:event_btnOkayActionPerformed

    private void spnHDCritsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnHDCritsStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDCrits.getModel();
        javax.swing.JComponent editor = spnHDCrits.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnHDCrits.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue( spnHDCrits.getValue() );
            }
            return;
        }

        int change = 0;
        if( Item == null ) {
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_HD];
            if( change + total > Items.GetSize() ) {
                int diff = change + total - Items.GetSize();
                change -= diff;
                spnHDCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_HD] );
                tf.setValue( spnHDCrits.getValue() );
            }
        } else {
            // is it over the maximum allocatable for this item?
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_HD];
            if( change + total > Item.NumCrits() ) {
                int diff = change + total - Item.NumCrits();
                change -= diff;
                spnHDCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_HD] );
                tf.setValue( spnHDCrits.getValue() );
            }
        }

        // update the allocated number and the running total.
        Alloc[LocationIndex.MECH_LOC_HD] += change;
        total += change;

        // update the labels to reflect the new values.
        lblHDQuant.setText( Alloc[LocationIndex.MECH_LOC_HD] + " of " + Crits[LocationIndex.MECH_LOC_HD] );
        if( Item == null ) {
            lblItemCrits.setText( total + " of " + Items.GetSize() + " Allocated" );            
        } else {
            lblItemCrits.setText( total + " of " + Item.NumCrits() + " Allocated" );            
        }
    }//GEN-LAST:event_spnHDCritsStateChanged

    private void spnRTCritsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRTCritsStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRTCrits.getModel();
        javax.swing.JComponent editor = spnRTCrits.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRTCrits.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue( spnRTCrits.getValue() );
            }
            return;
        }

        int change = 0;
        if( Item == null ) {
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_RT];
            if( change + total > Items.GetSize() ) {
                int diff = change + total - Items.GetSize();
                change -= diff;
                spnRTCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_RT] );
                tf.setValue( spnRTCrits.getValue() );
            }
        } else {
            // is it over the maximum allocatable for this item?
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_RT];
            if( change + total > Item.NumCrits() ) {
                int diff = change + total - Item.NumCrits();
                change -= diff;
                spnRTCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_RT] );
                tf.setValue( spnRTCrits.getValue() );
            }
        }

        // update the allocated number and the running total.
        Alloc[LocationIndex.MECH_LOC_RT] += change;
        total += change;

        // update the labels to reflect the new values.
        lblRTQuant.setText( Alloc[LocationIndex.MECH_LOC_RT] + " of " + Crits[LocationIndex.MECH_LOC_RT] );
        if( Item == null ) {
            lblItemCrits.setText( total + " of " + Items.GetSize() + " Allocated" );            
        } else {
            lblItemCrits.setText( total + " of " + Item.NumCrits() + " Allocated" );            
        }
    }//GEN-LAST:event_spnRTCritsStateChanged

    private void spnLTCritsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLTCritsStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLTCrits.getModel();
        javax.swing.JComponent editor = spnLTCrits.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLTCrits.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue( spnLTCrits.getValue() );
            }
            return;
        }

        int change = 0;
        if( Item == null ) {
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_LT];
            if( change + total > Items.GetSize() ) {
                int diff = change + total - Items.GetSize();
                change -= diff;
                spnLTCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_LT] );
                tf.setValue( spnLTCrits.getValue() );
            }
        } else {
            // is it over the maximum allocatable for this item?
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_LT];
            if( change + total > Item.NumCrits() ) {
                int diff = change + total - Item.NumCrits();
                change -= diff;
                spnLTCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_LT] );
                tf.setValue( spnLTCrits.getValue() );
            }
        }

        // update the allocated number and the running total.
        Alloc[LocationIndex.MECH_LOC_LT] += change;
        total += change;

        // update the labels to reflect the new values.
        lblLTQuant.setText( Alloc[LocationIndex.MECH_LOC_LT] + " of " + Crits[LocationIndex.MECH_LOC_LT] );
        if( Item == null ) {
            lblItemCrits.setText( total + " of " + Items.GetSize() + " Allocated" );            
        } else {
            lblItemCrits.setText( total + " of " + Item.NumCrits() + " Allocated" );            
        }
    }//GEN-LAST:event_spnLTCritsStateChanged

    private void spnCTCritsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCTCritsStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnCTCrits.getModel();
        javax.swing.JComponent editor = spnCTCrits.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnCTCrits.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue( spnCTCrits.getValue() );
            }
            return;
        }

        int change = 0;
        if( Item == null ) {
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_CT];
            if( change + total > Items.GetSize() ) {
                int diff = change + total - Items.GetSize();
                change -= diff;
                spnCTCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_CT] );
                tf.setValue( spnCTCrits.getValue() );
            }
        } else {
            // is it over the maximum allocatable for this item?
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_CT];
            if( change + total > Item.NumCrits() ) {
                int diff = change + total - Item.NumCrits();
                change -= diff;
                spnCTCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_CT] );
                tf.setValue( spnCTCrits.getValue() );
            }
        }

        // update the allocated number and the running total.
        Alloc[LocationIndex.MECH_LOC_CT] += change;
        total += change;

        // update the labels to reflect the new values.
        lblCTQuant.setText( Alloc[LocationIndex.MECH_LOC_CT] + " of " + Crits[LocationIndex.MECH_LOC_CT] );
        if( Item == null ) {
            lblItemCrits.setText( total + " of " + Items.GetSize() + " Allocated" );            
        } else {
            lblItemCrits.setText( total + " of " + Item.NumCrits() + " Allocated" );            
        }
    }//GEN-LAST:event_spnCTCritsStateChanged

    private void spnLACritsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLACritsStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLACrits.getModel();
        javax.swing.JComponent editor = spnLACrits.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLACrits.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue( spnLACrits.getValue() );
            }
            return;
        }

        int change = 0;
        if( Item == null ) {
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_LA];
            if( change + total > Items.GetSize() ) {
                int diff = change + total - Items.GetSize();
                change -= diff;
                spnLACrits.setValue( change + Alloc[LocationIndex.MECH_LOC_LA] );
                tf.setValue( spnLACrits.getValue() );
            }
        } else {
            // is it over the maximum allocatable for this item?
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_LA];
            if( change + total > Item.NumCrits() ) {
                int diff = change + total - Item.NumCrits();
                change -= diff;
                spnLACrits.setValue( change + Alloc[LocationIndex.MECH_LOC_LA] );
                tf.setValue( spnLACrits.getValue() );
            }
        }

        // update the allocated number and the running total.
        Alloc[LocationIndex.MECH_LOC_LA] += change;
        total += change;

        // update the labels to reflect the new values.
        lblLAQuant.setText( Alloc[LocationIndex.MECH_LOC_LA] + " of " + Crits[LocationIndex.MECH_LOC_LA] );
        if( Item == null ) {
            lblItemCrits.setText( total + " of " + Items.GetSize() + " Allocated" );            
        } else {
            lblItemCrits.setText( total + " of " + Item.NumCrits() + " Allocated" );            
        }
    }//GEN-LAST:event_spnLACritsStateChanged

    private void spnRACritsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRACritsStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRACrits.getModel();
        javax.swing.JComponent editor = spnRACrits.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRACrits.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue( spnRACrits.getValue() );
            }
            return;
        }

        int change = 0;
        if( Item == null ) {
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_RA];
            if( change + total > Items.GetSize() ) {
                int diff = change + total - Items.GetSize();
                change -= diff;
                spnRACrits.setValue( change + Alloc[LocationIndex.MECH_LOC_RA] );
                tf.setValue( spnRACrits.getValue() );
            }
        } else {
            // is it over the maximum allocatable for this item?
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_RA];
            if( change + total > Item.NumCrits() ) {
                int diff = change + total - Item.NumCrits();
                change -= diff;
                spnRACrits.setValue( change + Alloc[LocationIndex.MECH_LOC_RA] );
                tf.setValue( spnRACrits.getValue() );
            }
        }

        // update the allocated number and the running total.
        Alloc[LocationIndex.MECH_LOC_RA] += change;
        total += change;

        // update the labels to reflect the new values.
        lblRAQuant.setText( Alloc[LocationIndex.MECH_LOC_RA] + " of " + Crits[LocationIndex.MECH_LOC_RA] );
        if( Item == null ) {
            lblItemCrits.setText( total + " of " + Items.GetSize() + " Allocated" );            
        } else {
            lblItemCrits.setText( total + " of " + Item.NumCrits() + " Allocated" );            
        }
    }//GEN-LAST:event_spnRACritsStateChanged

    private void spnLLCritsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLLCritsStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLLCrits.getModel();
        javax.swing.JComponent editor = spnLLCrits.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLLCrits.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue( spnLLCrits.getValue() );
            }
            return;
        }

        int change = 0;
        if( Item == null ) {
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_LL];
            if( change + total > Items.GetSize() ) {
                int diff = change + total - Items.GetSize();
                change -= diff;
                spnLLCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_LL] );
                tf.setValue( spnLLCrits.getValue() );
            }
        } else {
            // is it over the maximum allocatable for this item?
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_LL];
            if( change + total > Item.NumCrits() ) {
                int diff = change + total - Item.NumCrits();
                change -= diff;
                spnLLCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_LL] );
                tf.setValue( spnLLCrits.getValue() );
            }
        }

        // update the allocated number and the running total.
        Alloc[LocationIndex.MECH_LOC_LL] += change;
        total += change;

        // update the labels to reflect the new values.
        lblLLQuant.setText( Alloc[LocationIndex.MECH_LOC_LL] + " of " + Crits[LocationIndex.MECH_LOC_LL] );
        if( Item == null ) {
            lblItemCrits.setText( total + " of " + Items.GetSize() + " Allocated" );            
        } else {
            lblItemCrits.setText( total + " of " + Item.NumCrits() + " Allocated" );            
        }
    }//GEN-LAST:event_spnLLCritsStateChanged

    private void spnRLCritsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRLCritsStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRLCrits.getModel();
        javax.swing.JComponent editor = spnRLCrits.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRLCrits.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue( spnRLCrits.getValue() );
            }
            return;
        }

        int change = 0;
        if( Item == null ) {
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_RL];
            if( change + total > Items.GetSize() ) {
                int diff = change + total - Items.GetSize();
                change -= diff;
                spnRLCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_RL] );
                tf.setValue( spnRLCrits.getValue() );
            }
        } else {
            // is it over the maximum allocatable for this item?
            change = n.getNumber().intValue() - Alloc[LocationIndex.MECH_LOC_RL];
            if( change + total > Item.NumCrits() ) {
                int diff = change + total - Item.NumCrits();
                change -= diff;
                spnRLCrits.setValue( change + Alloc[LocationIndex.MECH_LOC_RL] );
                tf.setValue( spnRLCrits.getValue() );
            }
        }

        // update the allocated number and the running total.
        Alloc[LocationIndex.MECH_LOC_RL] += change;
        total += change;

        // update the labels to reflect the new values.
        lblRLQuant.setText( Alloc[LocationIndex.MECH_LOC_RL] + " of " + Crits[LocationIndex.MECH_LOC_RL] );
        if( Item == null ) {
            lblItemCrits.setText( total + " of " + Items.GetSize() + " Allocated" );            
        } else {
            lblItemCrits.setText( total + " of " + Item.NumCrits() + " Allocated" );            
        }
    }//GEN-LAST:event_spnRLCritsStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOkay;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAllocateItem;
    private javax.swing.JLabel lblCTQuant;
    private javax.swing.JLabel lblHDQuant;
    private javax.swing.JLabel lblItemCrits;
    private javax.swing.JLabel lblLAQuant;
    private javax.swing.JLabel lblLLQuant;
    private javax.swing.JLabel lblLTQuant;
    private javax.swing.JLabel lblRAQuant;
    private javax.swing.JLabel lblRLQuant;
    private javax.swing.JLabel lblRTQuant;
    private javax.swing.JPanel pnlCT;
    private javax.swing.JPanel pnlHD;
    private javax.swing.JPanel pnlLA;
    private javax.swing.JPanel pnlLL;
    private javax.swing.JPanel pnlLT;
    private javax.swing.JPanel pnlRA;
    private javax.swing.JPanel pnlRL;
    private javax.swing.JPanel pnlRT;
    private javax.swing.JSpinner spnCTCrits;
    private javax.swing.JSpinner spnHDCrits;
    private javax.swing.JSpinner spnLACrits;
    private javax.swing.JSpinner spnLLCrits;
    private javax.swing.JSpinner spnLTCrits;
    private javax.swing.JSpinner spnRACrits;
    private javax.swing.JSpinner spnRLCrits;
    private javax.swing.JSpinner spnRTCrits;
    // End of variables declaration//GEN-END:variables
    
}
