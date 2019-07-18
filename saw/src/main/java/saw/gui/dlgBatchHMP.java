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

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;
import javax.swing.SwingWorker;
import components.Mech;
import filehandlers.*;
import saw.filehandlers.*;

public class dlgBatchHMP extends javax.swing.JDialog implements PropertyChangeListener {

    /** Creates new form dlgTextExport */
    public dlgBatchHMP(java.awt.Frame parent, boolean modal ) {
        super(parent, modal);
        initComponents();
        setTitle( "Import Multiple HMP Files" );
    }

    public void propertyChange( PropertyChangeEvent e ) {
       prgImporting.setValue( ((Importer) e.getSource()).getProgress() );
    }

    private class Importer extends SwingWorker<Void,Void> {
        dlgBatchHMP Owner;
        public Importer( dlgBatchHMP owner ) {
            Owner = owner;
        }

        @Override
        public void done() {
            setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            Media.Messager( "Finished!  Check the log for errors." );
            prgImporting.setValue( 0 );
        }

        @Override
        protected Void doInBackground() throws Exception {
            String Messages = "", MsgTemp = "";
            int LastMsgLength = Messages.length();
            HMPReader HMPr = new HMPReader();
            MechWriter XMLw = new MechWriter();
            Vector<File> files = new Vector<File>();

            // load up the list of files
            File d = new File( txtSource.getText() );
            if ( d.isDirectory() ) {
                if( d.listFiles() == null ) {
                    throw new Exception( "There are no files in the source directory.\nCannot continue." );
                }
                for ( File f : d.listFiles() ) {
                    if ( f.isFile() && f.getPath().endsWith(".hmp") ) {
                        files.add( f );
                    }
                }
            } else {
                throw new Exception( "The source is not a directory.\nCannot continue." );
            }

            if( files.size() < 1 ) {
                throw new Exception( "No HMP files found in the source directory.\nCannot continue." );
            }

            for( int i = 0; i < files.size(); i++ ) {
                File f = files.get( i );
                String basename = f.getName().replace( ".hmp", "" );

                try {
                    // import the new 'Mech
                    Mech m = HMPr.GetMech( f.getCanonicalPath(), true );
                    MsgTemp = HMPr.GetErrors();

                    // save it off to SSW format
                    XMLw.setMech( m );
                    XMLw.WriteXML( txtDestination.getText() + File.separator + basename + ".ssw" );
                } catch( Exception e ) {
                    // had a problem loading the mech.  let the user know.
                    if( e.getMessage() == null ) {
                        Messages += "An unknown error has occured.\n" + f.getName() + " is not loadable.\n\n";
                    } else {
                        Messages += e.getMessage() + "\n" + f.getName() + "\n\n";
                    }
                }

                if( MsgTemp.length() > 0 ) {
                    Messages += MsgTemp + "\n" + f.getName() + "\n\n";
                }

                if( Messages.length() != LastMsgLength ) {
                    LastMsgLength = Messages.length();
                    txtMessages.setText( Messages );
                    txtMessages.setCaretPosition( 0 );
                }

                int progress = ((int) (( ((double) i + 1) / (double) files.size() ) * 100.0 ) );
                setProgress( progress );
            }

            return null;
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
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        txtMessages = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSource = new javax.swing.JTextField();
        btnSource = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtDestination = new javax.swing.JTextField();
        btnDestination = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnImport = new javax.swing.JButton();
        prgImporting = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(600, 300));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(600, 300));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(600, 300));

        txtMessages.setFont(new java.awt.Font("Lucida Sans Typewriter", 0, 12)); // NOI18N
        txtMessages.setMaximumSize(new java.awt.Dimension(575, 200000));
        txtMessages.setMinimumSize(new java.awt.Dimension(575, 300));
        txtMessages.setPreferredSize(new java.awt.Dimension(575, 300));
        jScrollPane1.setViewportView(txtMessages);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Source Directory:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel1, gridBagConstraints);

        txtSource.setEditable(false);
        txtSource.setMinimumSize(new java.awt.Dimension(200, 20));
        txtSource.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPanel2.add(txtSource, gridBagConstraints);

        btnSource.setText("...");
        btnSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSourceActionPerformed(evt);
            }
        });
        jPanel2.add(btnSource, new java.awt.GridBagConstraints());

        jLabel2.setText("Destination Directory:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel2.add(jLabel2, gridBagConstraints);

        txtDestination.setEditable(false);
        txtDestination.setMinimumSize(new java.awt.Dimension(200, 20));
        txtDestination.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPanel2.add(txtDestination, gridBagConstraints);

        btnDestination.setText("...");
        btnDestination.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDestinationActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel2.add(btnDestination, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jPanel2, gridBagConstraints);

        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });
        jPanel3.add(btnImport);

        prgImporting.setMinimumSize(new java.awt.Dimension(148, 20));
        prgImporting.setPreferredSize(new java.awt.Dimension(148, 20));
        prgImporting.setStringPainted(true);
        jPanel3.add(prgImporting);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(jPanel3, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
    dispose();
}//GEN-LAST:event_btnCloseActionPerformed

private void btnSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSourceActionPerformed
    Media media = new Media();
    String dirPath = media.GetDirectorySelection( this );
    txtSource.setText( dirPath );
}//GEN-LAST:event_btnSourceActionPerformed

private void btnDestinationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDestinationActionPerformed
    Media media = new Media();
    String dirPath = media.GetDirectorySelection( this );
    txtDestination.setText( dirPath );
}//GEN-LAST:event_btnDestinationActionPerformed

private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        prgImporting.setValue(0);
        if( txtSource.getText().length() < 1 ) {
            Media.Messager( this, "The Source directory is empty.\nPlease choose a Source directory." );
            return;
        }
        if( txtDestination.getText().length() < 1 ) {
            Media.Messager( this, "The Destination directory is empty.\nPlease choose a Destination directory." );
            return;
        }
        int Response = javax.swing.JOptionPane.showConfirmDialog( this, "This will import each HMP file in the Source directory\nand save it to an SSW file in the Destination directory.\nThis process could take a few minutes, are you ready?", "Batch HMP Import", javax.swing.JOptionPane.YES_NO_OPTION );
        if( Response == javax.swing.JOptionPane.YES_OPTION ) {
            setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR ) );
            try {
                Importer Import = new Importer( this );
                Import.addPropertyChangeListener( this );
                Import.execute();
            } catch( Exception e ) {
                // fatal error.  let the user know
                Media.Messager( this, "A fatal error occured while processing the 'Mechs:\n" + e.getMessage() );
                e.printStackTrace();
            }
            setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            prgImporting.setValue(0);
        }
}//GEN-LAST:event_btnImportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDestination;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnSource;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar prgImporting;
    private javax.swing.JTextField txtDestination;
    private javax.swing.JTextPane txtMessages;
    private javax.swing.JTextField txtSource;
    // End of variables declaration//GEN-END:variables

}
