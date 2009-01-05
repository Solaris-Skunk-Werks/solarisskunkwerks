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

import java.awt.Cursor;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import ssw.Constants;
import ssw.filehandlers.XMLRPCClient;

public class dlgBrowseS7Images extends javax.swing.JDialog {
    Vector ImageList = new Vector();
    int UserID = -1;
    String ImageID = "-1",
           ImageName ="none";
    private Cursor Hourglass = new Cursor( Cursor.WAIT_CURSOR );
    private Cursor NormalCursor = new Cursor( Cursor.DEFAULT_CURSOR );

    /** Creates new form dlgBrowseS7Images */
    public dlgBrowseS7Images(java.awt.Frame parent, boolean modal, int user, String Image ) {
        super(parent, modal);
        initComponents();
        UserID = user;
        try {
            LoadImageList();
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            dispose();
        }

        // now that the list is loaded, get the combo box up and running
        String[] List = new String[ImageList.size()];
        for( int i = 0; i < ImageList.size(); i++ ) {
            List[i] = ((ImageID) ImageList.get( i )).Name;
        }

        // load the list up
        cmbImageList.setModel( new javax.swing.DefaultComboBoxModel( List ) );

        if( ! Image.equals( "0" ) ) {
            ImageID = Image;
        }

        // load the selected image
        LoadImage();
    }

    private void LoadImageList() throws Exception {
        // see if the file exists, otherwise get the RPCXMLClient and get it
        String filename = Constants.ImageListFileName;

        try {
            BufferedReader FR = new BufferedReader( new FileReader( filename ) );

            boolean EOF = false;
            String read = "";
            while( EOF == false ) {
                try {
                    read = FR.readLine();
                    if( read == null ) {
                        // We've hit the end of the file.
                        EOF = true;
                    } else {
                        ImageList.add( ProcessString( read ) );
                    }
                } catch (IOException e ) {
                    // probably just reached the end of the file
                    EOF = true;
                }
            }

            FR.close();

            // now we'll want to load up the user images
                try {
                    // get the image list from the website
                    XMLRPCClient serve = new XMLRPCClient( Constants.Solaris7URL );
                    String[] List = serve.GetUserImages( UserID );

                    // add in the seperator that says we have a list of users
                    ImageID sep = new ImageID();
                    sep.ID = "-1";
                    sep.Name = "---  User Images ---";
                    sep.URL = ".." + File.separator + "Images" + File.separator + "No_Image.png";
                    ImageList.add( 0, sep);

                    // now add in the user images
                    int i = 1;
                    for( ; i < List.length; i++ ) {
                        ImageList.add( i, ProcessString( List[i] ) );
                    }

                    // lastly, another seperator
                    sep = new ImageID();
                    sep.ID = "-1";
                    sep.Name = "---  Solaris 7 TRO Images ---";
                    sep.URL = ".." + File.separator + "Images" + File.separator + "No_Image.png";
                    ImageList.add( i, sep );

                } catch( Exception f ) {
                    // return the message to the user
                    throw f;
                }
        } catch ( Exception e ) {
            // could not access the file, we'll have to get the image list
            if( e instanceof FileNotFoundException ) {
                // no file found, we'll have to load the images from the site
                File file = new File( Constants.ImageListFileName );
                try {
                    // Create file if it does not exist
                    file.createNewFile();

                    // get the image list from the website
                    XMLRPCClient serve = new XMLRPCClient( Constants.Solaris7URL );
                    String[] List = serve.GetTROImages( "01/01/1900" );

                    // get a new file writer and load up the images
                    BufferedWriter FR = new BufferedWriter( new FileWriter( filename ) );
                    for( int i = 0; i < List.length; i++ ) {
                        FR.write( List[i] );
                        FR.newLine();
                    }
                    FR.close();

                    // now load the list again
                    LoadImageList();
                    return;
                } catch( Exception f ) {
                    // return the message to the user
                    throw f;
                }
            } else {
                // return the message to the user
                throw e;
            }
        }
    }

    private void LoadImage() {
        ImageIcon FluffImage;
        try {
            if( ((ImageID) ImageList.get( cmbImageList.getSelectedIndex() )).ID.equals( "-1" ) ) {
                FluffImage = new ImageIcon( ((ImageID) ImageList.get( cmbImageList.getSelectedIndex() )).URL );
            } else {
                FluffImage = new ImageIcon( new URL( GetURL( ((ImageID) ImageList.get( cmbImageList.getSelectedIndex() )).URL ) ) );
            }
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            return;
        }

        // See if we need to scale
        int h = FluffImage.getIconHeight();
        int w = FluffImage.getIconWidth();
        if ( w > 300 || h > 500 ) {
            if ( w > 300 ) { // resize based on width
                FluffImage = new ImageIcon(FluffImage.getImage().
                    getScaledInstance( 300, -1, Image.SCALE_DEFAULT));
            } else { // resize based on height
                FluffImage = new ImageIcon(FluffImage.getImage().
                    getScaledInstance(-1, 500, Image.SCALE_DEFAULT));
            }
        }
        lblImage.setIcon( FluffImage );
    }

    private ImageID ProcessString( String s ) {
        ImageID retval = new ImageID();
        String[] c = s.split( "," );
        retval.ID = c[0];
        retval.Name = c[1];
        retval.URL = c[2];
        return retval;
    }

    private String GetURL( String s ) {
        return s.replaceAll( " ", "%20" );
    }

    public String GetImageID() {
        return ImageID;
    }

    public String GetImageName() {
        return ImageName;
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

        jLabel1 = new javax.swing.JLabel();
        cmbImageList = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        brnCancel = new javax.swing.JButton();
        btnUseImage = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Use the following image:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        cmbImageList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbImageList.setMaximumSize(new java.awt.Dimension(270, 24));
        cmbImageList.setMinimumSize(new java.awt.Dimension(270, 24));
        cmbImageList.setPreferredSize(new java.awt.Dimension(270, 24));
        cmbImageList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbImageListActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(cmbImageList, gridBagConstraints);

        brnCancel.setText("Cancel");
        brnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(brnCancel);

        btnUseImage.setText("Use Image");
        btnUseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseImageActionPerformed(evt);
            }
        });
        jPanel1.add(btnUseImage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel1, gridBagConstraints);

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/No_Image.png"))); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 500));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 500));
        lblImage.setPreferredSize(new java.awt.Dimension(300, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(lblImage, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void cmbImageListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbImageListActionPerformed
    LoadImage();
}//GEN-LAST:event_cmbImageListActionPerformed

private void brnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brnCancelActionPerformed
    setVisible( false );
}//GEN-LAST:event_brnCancelActionPerformed

private void btnUseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseImageActionPerformed
    ImageID = ((ImageID) ImageList.get( cmbImageList.getSelectedIndex() )).ID;
    ImageName = ((ImageID) ImageList.get( cmbImageList.getSelectedIndex() )).Name;
    setVisible( false );
}//GEN-LAST:event_btnUseImageActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton brnCancel;
    private javax.swing.JButton btnUseImage;
    private javax.swing.JComboBox cmbImageList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblImage;
    // End of variables declaration//GEN-END:variables

    private class ImageID {
        public String ID = "";
        public String Name = "";
        public String URL = "";
        @Override
        public String toString() {
            return ID + "," + Name + "," + URL;
        }
    }
}
