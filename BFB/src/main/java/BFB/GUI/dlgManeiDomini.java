package BFB.GUI;

import Force.Advantages;
import Force.Advantages.Enhancement;
// import Force.Warrior;
import java.util.ArrayList;
import javax.swing.JDialog;

public class dlgManeiDomini extends javax.swing.JDialog {
    private static final long serialVersionUID = -8720839392985654131L;

    public double Modifier = 1.0;
    public ArrayList<Enhancement> Enhancements = new ArrayList<Enhancement>();

    /** Creates new form dlgManeiDomini */
    public dlgManeiDomini(JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Advantages a = new Advantages();
        lstMods.setModel(a.getMDModsModel());
    }

    public void setEnhancements(ArrayList<Enhancement> List ) {
        Enhancements = List;
        for ( Enhancement e : Enhancements ) {
            lstMods.setSelectedValue(e, false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstMods = new javax.swing.JList<Advantages.Enhancement>();
        jLabel5 = new javax.swing.JLabel();
        lblHighestLevel = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblMod = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Manei Domini Modifier Calculation");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Select Manei Domini Enhancements");

        /*
         * Not needed. Data is reset after initialization.
        lstMods.setModel(new javax.swing.AbstractListModel<Advantages.Enhancement>() {
            private static final long serialVersionUID = 1L;

            String[] strings = {
                    "0, Cosmetic Enhancement",
                    "0, Prosthetic Hand/Foot/Arm/Leg",
                    "1, Enhanced Prosthetic Hand/Foot/Arm/Leg",
                    "3, Improved Enhanced Prosthetic Hand/Foot/Arm/Leg",
                    "1, Secondary Power Supply",
                    "3, Prosthetic Leg MASC",
                    "2, Pain Shunt",
                    "3, Pheremone Effuser",
                    "4, Toxin Effuser",
                    "2, Cybernetic Eye/Ear/Speech Implants",
                    "3, Multi-Modal Cybernetic Eye/Ear/Speech Implants",
                    "5, Enhanced Multi-Modal Cybernetic Eye/Ear/Speech Implants",
                    "2, Recorder/Transmitter/Receiver/Communications Implant",
                    "4, Boosted Recorder/Transmitter/Receiver/Communications Implants",
                    "3, Filtration Liver/Lung Implants",
                    "3, Vehicular Direct Neural Interface (VDNI)",
                    "5, Buffered VDNI",
                    "4, Myomer Full-Body Implants"
                };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        */

        lstMods.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstModsValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(lstMods);

        jLabel5.setText("Highest Level:");

        lblHighestLevel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblHighestLevel.setText("0");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jLabel1.setText("Modifier:");

        lblMod.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblMod.setText("0.00");

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHighestLevel)
                        .addGap(44, 44, 44)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMod)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClose))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(jLabel1)
                    .addComponent(lblMod)
                    .addComponent(jLabel5)
                    .addComponent(lblHighestLevel)
                    .addComponent(btnSave))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lstModsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstModsValueChanged
        int curMax = 0;
        if ( lstMods.getSelectedIndices().length > 0 ) {
            Enhancements.clear();
            for ( int index : lstMods.getSelectedIndices() ) {
                Enhancement e = (Enhancement) lstMods.getModel().getElementAt(index);
                Enhancements.add(e);
                //String item = lstMods.getModel().getElementAt(index).toString();
                //String[] items = item.split(",");
                //if ( !items[0].trim().isEmpty() ) {
                    int val = e.getLevel(); //Integer.parseInt(items[0]);
                    if ( val > curMax ) curMax = val;
                //}
            }
        }
        lblHighestLevel.setText("" + curMax);
        Modifier = (0.75 + ( (double) curMax / 4 ) );
        lblMod.setText(Modifier + "");
}//GEN-LAST:event_lstModsValueChanged

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        Modifier = 1.0;
        this.setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblHighestLevel;
    private javax.swing.JLabel lblMod;
    private javax.swing.JList<Advantages.Enhancement> lstMods;
    // End of variables declaration//GEN-END:variables

}
