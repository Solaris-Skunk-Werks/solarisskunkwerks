/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * dlgCCGMech.java
 *
 * Created on 24-Feb-2009, 2:19:03 PM
 */

package ssw.gui;

import java.util.ArrayList;
import ssw.components.CCGMech;
import components.Mech;
import components.abPlaceable;
import components.ifMechLoadout;

/**
 *
 * @author Kevin
 */
public class dlgCCGMech extends javax.swing.JDialog {

    /**
     *
     */
    private static final long serialVersionUID = -4454863213045636732L;
    private Mech CurMech;
    // private String NL; // Not Used

    /** Creates new form dlgCCGMech */
    public dlgCCGMech(java.awt.Frame parent, boolean modal, Mech m) {
        super(parent, modal);
        initComponents();
        CurMech = m;
        // NL = System.getProperty("line.separator"); // Variable is never accessed.
        // CCGMech ccgMech = GenerateCCGCard();
        GenerateCCGCard(); // Since variable not used, call without assignment
        setTitle(CurMech.GetName() + " CCG Card");
    }

    /**
     * GenerateCCGCard()
     */
    private CCGMech GenerateCCGCard() {
        // Here goes nothing!
        CCGMech card = new CCGMech(CurMech.GetName(), CurMech.GetFullName());

        card.setMass(CurMech.GetTonnage());
        card.setMovementRate(CurMech.GetWalkingMP());
        card.setArmourAndStructure(CurMech.GetArmor().GetArmorValue());
        card.setJump(CurMech.GetLoadout().GetJumpJets().GetNumJJ());

        ifMechLoadout loadout = CurMech.GetLoadout();
        ArrayList<abPlaceable> CurrentLoadout = loadout.GetNonCore();
        card.setAttackValue(CurrentLoadout, CurMech.GetBVMovementHeat(), CurMech.GetHeatSinks().TotalDissipation());

        lblCardTitle.setText(card.getName());
        lblCardCostMassWeapons.setText("Mass: " + card.getMass());
        lblCardAbilitiesAndFlavour.setText(card.getSpecial());
        lblArmour.setText(" " + card.getArmour() + "/" + card.getStructure());
        lblAttack.setText("  " + card.getAttack());
        lblSpeed.setText(card.getSpeed() + "");

        return card;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblCardTitle = new javax.swing.JLabel();
        lblCardCostMassWeapons = new javax.swing.JLabel();
        lblCardPictureSpace = new javax.swing.JLabel();
        lblCardFactionAvailability = new javax.swing.JLabel();
        lblCardAbilitiesAndFlavour = new javax.swing.JLabel();
        lblArmour = new javax.swing.JLabel();
        lblArtistAndCopyright = new javax.swing.JLabel();
        lblAttack = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        lblSpeed = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblCardTitle.setText(" ");
        lblCardTitle.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblCardCostMassWeapons.setText(" ");
        lblCardCostMassWeapons.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblCardPictureSpace.setText("Picture Goes Here");
        lblCardPictureSpace.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblCardFactionAvailability.setText(" ");
        lblCardFactionAvailability.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblCardAbilitiesAndFlavour.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblArmour.setText("jLabel1");
        lblArmour.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblArtistAndCopyright.setText("jLabel2");
        lblArtistAndCopyright.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblAttack.setText("jLabel3");
        lblAttack.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblSpeed)
                        .addComponent(lblCardTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCardCostMassWeapons, javax.swing.GroupLayout.PREFERRED_SIZE, 65,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCardPictureSpace, javax.swing.GroupLayout.PREFERRED_SIZE, 115,
                                        Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblArmour, javax.swing.GroupLayout.PREFERRED_SIZE, 57,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblArtistAndCopyright, javax.swing.GroupLayout.PREFERRED_SIZE, 53,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblAttack, javax.swing.GroupLayout.PREFERRED_SIZE, 58,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 69,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(54, 54, 54))
                        .addComponent(lblCardAbilitiesAndFlavour, javax.swing.GroupLayout.DEFAULT_SIZE, 186,
                                Short.MAX_VALUE)
                        .addComponent(lblCardFactionAvailability, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                .addContainerGap()));

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                new java.awt.Component[] { lblArmour, lblArtistAndCopyright, lblAttack });

        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addComponent(lblCardTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblCardCostMassWeapons, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCardPictureSpace, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCardFactionAvailability)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblSpeed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCardAbilitiesAndFlavour, javax.swing.GroupLayout.PREFERRED_SIZE, 71,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblArmour, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblArtistAndCopyright, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblAttack, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnClose)
                .addContainerGap(25, Short.MAX_VALUE)));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }// GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JLabel lblArmour;
    private javax.swing.JLabel lblArtistAndCopyright;
    private javax.swing.JLabel lblAttack;
    private javax.swing.JLabel lblCardAbilitiesAndFlavour;
    private javax.swing.JLabel lblCardCostMassWeapons;
    private javax.swing.JLabel lblCardFactionAvailability;
    private javax.swing.JLabel lblCardPictureSpace;
    private javax.swing.JLabel lblCardTitle;
    private javax.swing.JLabel lblSpeed;
    // End of variables declaration//GEN-END:variables

}
