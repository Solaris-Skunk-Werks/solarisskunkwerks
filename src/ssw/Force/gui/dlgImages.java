/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ssw.Force.gui;

import ssw.Force.Force;
import ssw.Force.Unit;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import ssw.filehandlers.Media;
import ssw.filehandlers.XMLWriter;
import ssw.gui.frmMain;

public class dlgImages extends javax.swing.JDialog {
    public Force force;
    public boolean hasWork = false;

    private frmMain Parent;
    private Unit curUnit;
    private Hashtable<String, Unit> units = new Hashtable<String, Unit>();
    private DefaultListModel unitList = new DefaultListModel();
    private Media media = new Media();

    /** Creates new form dlgMechImages */
    public dlgImages(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public dlgImages(java.awt.Frame parent, Force force) {
        this(parent, true);
        this.Parent = (frmMain) parent;
        this.force = force;

        //load units without images
        for (int i=0; i<force.Units.size(); i++) {
            Unit u = (Unit) force.Units.get(i);
            u.LoadMech();
            if ( u.m != null ) {
                if ( u.m.GetSSWImage().isEmpty() || u.m.GetSSWImage().toLowerCase().equals("../images/no_image.png") ) {
                    if ( !unitList.contains(u.TypeModel)) {
                        unitList.addElement(u.TypeModel);
                        units.put(u.TypeModel, u);
                    }
                } else {
                    //It says it has an image, try to load
                    try {
                        Image image = media.GetImage(u.m.GetSSWImage());
                        if ( image.getWidth(null) == -1 ) {
                            if ( !unitList.contains(u.TypeModel)) {
                                unitList.addElement(u.TypeModel);
                                units.put(u.TypeModel, u);
                            }
                        }
                    } catch ( Exception e ) {
                        if ( !unitList.contains(u.TypeModel)) {
                            unitList.addElement(u.TypeModel);
                            units.put(u.TypeModel, u);
                        }
                    }
                }
            }
        }

        if ( unitList.isEmpty() ) {
            Media.Messager("All of your units have images selected.  To change individual images double click the unit." );
            this.setVisible(false);
            this.dispose();
        } else {
            hasWork = true;
            lstUnits.setModel(unitList);
        }
    }

    private void setImage() {

        try {
            ImageIcon icon = new ImageIcon(curUnit.m.GetSSWImage());

            if( icon == null ) { return; }

            // See if we need to scale
            int h = icon.getIconHeight();
            int w = icon.getIconWidth();
            if ( w > lblImage.getWidth() || h > lblImage.getHeight() ) {
                if ( w > h ) { // resize based on width
                    icon = new ImageIcon(icon.getImage().
                        getScaledInstance(lblImage.getWidth(), -1, Image.SCALE_DEFAULT));
                } else { // resize based on height
                    icon = new ImageIcon(icon.getImage().
                        getScaledInstance(-1, lblImage.getHeight(), Image.SCALE_DEFAULT));
                }
            }

            lblImage.setIcon(icon);
        } catch (Exception e) {
            //do nothing
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstUnits = new javax.swing.JList();
        pnlMech = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Image Manager");

        pnlList.setBorder(javax.swing.BorderFactory.createTitledBorder("Designs Missing Images"));

        lstUnits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstUnits.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstUnitsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstUnits);

        javax.swing.GroupLayout pnlListLayout = new javax.swing.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlMech.setBorder(javax.swing.BorderFactory.createTitledBorder("Image Selected"));

        lblImage.setBackground(new java.awt.Color(255, 255, 255));
        lblImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblStatus.setPreferredSize(new java.awt.Dimension(34, 14));

        javax.swing.GroupLayout pnlMechLayout = new javax.swing.GroupLayout(pnlMech);
        pnlMech.setLayout(pnlMechLayout);
        pnlMechLayout.setHorizontalGroup(
            pnlMechLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMechLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMechLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMechLayout.createSequentialGroup()
                        .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)))
        );
        pnlMechLayout.setVerticalGroup(
            pnlMechLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMechLayout.createSequentialGroup()
                .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMech, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMech, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lstUnitsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstUnitsValueChanged
        if ( lstUnits.getSelectedValue() != null ) {
            curUnit = units.get( ( (Object) lstUnits.getSelectedValue() ).toString() );
            pnlMech.setBorder(new TitledBorder(curUnit.TypeModel));
            lblStatus.setText(curUnit.TypeModel + " Loaded.");
            media.setLogo(lblImage, new File(curUnit.m.GetSSWImage()));

            File imageFile = media.SelectImage(Parent.Prefs.get("LastMechImage", ""), "Select Image for " + curUnit.TypeModel);
            if ( imageFile != null ) {
                try {
                    Parent.Prefs.put("LastMechImage", imageFile.getCanonicalPath());
                    curUnit.m.SetSSWImage(imageFile.getCanonicalPath());
                    media.setLogo(lblImage, new File(curUnit.m.GetSSWImage()));

                    XMLWriter writer = new XMLWriter();
                    writer.setMech(curUnit.m);
                    writer.WriteXML(curUnit.Filename);
                    lblStatus.setText(curUnit.TypeModel + " Image Selection Saved!");
                } catch (IOException ex) {
                    //do nothing
                }
            }
        }
    }//GEN-LAST:event_lstUnitsValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JList lstUnits;
    private javax.swing.JPanel pnlList;
    private javax.swing.JPanel pnlMech;
    // End of variables declaration//GEN-END:variables

}
