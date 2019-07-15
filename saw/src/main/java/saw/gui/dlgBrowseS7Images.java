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

import filehandlers.Media;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import saw.filehandlers.XMLRPCClient;

public class dlgBrowseS7Images extends javax.swing.JDialog {
    ArrayList<ImageID> ImageList = new ArrayList<ImageID>();
    int UserID = -1,
            defaultIndex = 0;
    String ImageID = "-1",
           ImageName ="none";
    private Media media = new Media();
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
            Media.Messager( this, e.getMessage() );
            dispose();
        }

        // now that the list is loaded, get the combo box up and running
        DefaultListModel listModel = new DefaultListModel();
        for ( ImageID id : ImageList ) {
            listModel.addElement( id );
            if ( id.ID.equals(Image) || id.Name.equals(Image) ) defaultIndex = listModel.size()-1;
        }

        // load the list up
        lstImages.setModel(listModel);

        // load the selected image
        lstImages.setSelectedIndex(defaultIndex);
    }

    private void LoadImageList() throws Exception {
        // see if the file exists, otherwise get the RPCXMLClient and get it
        String filename = saw.Constants.ImageListFileName;

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
                    XMLRPCClient serve = new XMLRPCClient( saw.Constants.Solaris7URL );
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
                File file = new File( saw.Constants.ImageListFileName );
                try {
                    // Create file if it does not exist
                    file.createNewFile();

                    // get the image list from the website
                    XMLRPCClient serve = new XMLRPCClient( saw.Constants.Solaris7URL );
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

    private ImageID ProcessString( String s ) {
        ImageID retval = new ImageID();
        String[] c = s.split( "," );
        retval.ID = c[0];
        retval.Name = c[1];
        retval.URL = c[2].replace(" ", "%20");
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

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnUseImage = new javax.swing.JButton();
        brnCancel = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstImages = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Image");

        jLabel1.setText("Use the following image:");

        btnUseImage.setText("Use Image");
        btnUseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseImageActionPerformed(evt);
            }
        });
        jPanel1.add(btnUseImage);

        brnCancel.setText("Cancel");
        brnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(brnCancel);

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/No_Image.png"))); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblImage.setMaximumSize(new java.awt.Dimension(300, 500));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 500));
        lblImage.setPreferredSize(new java.awt.Dimension(300, 500));

        lstImages.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstImages.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstImages.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstImagesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstImages);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void brnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brnCancelActionPerformed
    setVisible( false );
}//GEN-LAST:event_brnCancelActionPerformed

private void btnUseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseImageActionPerformed
    ImageID = ((ImageID) lstImages.getModel().getElementAt( lstImages.getSelectedIndex() )).ID;
    ImageName = ((ImageID) lstImages.getModel().getElementAt( lstImages.getSelectedIndex() )).Name;
    setVisible( false );
}//GEN-LAST:event_btnUseImageActionPerformed

private void lstImagesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstImagesValueChanged
    if ( lstImages.getSelectedIndex() > -1 ) {
        media.setLogo( lblImage, ((ImageID) lstImages.getModel().getElementAt( lstImages.getSelectedIndex() )).URL );
    }
}//GEN-LAST:event_lstImagesValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton brnCancel;
    private javax.swing.JButton btnUseImage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblImage;
    private javax.swing.JList lstImages;
    // End of variables declaration//GEN-END:variables

    private class ImageID {
        public String ID = "";
        public String Name = "";
        public String URL = "";
        @Override
        public String toString() {
            return Name;
            //return ID + "," + Name + "," + URL;
        }
    }
}
