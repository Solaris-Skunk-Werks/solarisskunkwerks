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

package ssw.filehandlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;


public class MechList extends AbstractTableModel {
    private Vector List = new Vector();
    private String Directory = "";

    public MechList() {

    }
    
    public MechList(String directory) {
        this(directory, true);
    }
    
    public MechList( String directory, boolean useIndex ) {
        this();
        Directory = directory;

        if (! useIndex ) {
            Load(directory);

            //Index this information for faster reloading
            try {
                Write();
            } catch ( IOException e ) {
                //do nothing
            }
        } else {
            if (! Read() ) {
                Load(directory);

                //Index this information for faster reloading
                try {
                    Write();
                } catch ( IOException e ) {
                    //do nothing
                }
            }
        }
    }

    void Load( String Directory ) {
        File d = new File(Directory);
        if ( d.isDirectory() ) {
            if( d.listFiles() == null ) { return; }
            for ( File f : d.listFiles() ) {
                if ( f.isFile() && f.getPath().endsWith(".ssw") ) {
                    Add(f);
                }
                if ( f.isDirectory() ) {
                    Load( f.getPath() );
                }
            }
        }
    }

    public void Add( File f ) {
        try
        {
            MechListData mData = new MechListData( f.getCanonicalPath() );
            if (mData.isOmni()) {
                for ( int d=0; d < mData.Configurations.size(); d++ ) {
                    List.add((MechListData) mData.Configurations.get(d));
                }
            } else {
                List.add(mData);
            }
        } catch (Exception e) {
            //do nothing
        }
    }

    public void Add( MechListData m ) {
        List.add(m);
    }

    public MechListData Get( int row ) {
        return (MechListData) List.get(row);
    }

    public void Remove(MechListData m) {
        List.remove(m);
    }

    public void RemoveAll() {
        List.removeAllElements();
    }

    public int Size() {
        return List.size();
    }

    public Vector getList() {
        return List;
    }

    public void setList(Vector l) {
        this.List = l;
    }

    public MechList Filter(ListFilter filter) {
        MechList m = new MechList();
        for ( int d=0; d < List.size(); d++ ) {
            m.Add((MechListData) List.get(d));
        }

        boolean remove = false;
        for ( int i=List.size(); i > 0; i-- ) {
            remove = false;
            MechListData mData = (MechListData) List.get(i-1);

            if ( filter.getIsOmni() ) {
                if (! mData.isOmni() ) remove = true;
            }
            if ( ! filter.getTech().isEmpty() ) {
                if (! mData.getTech().equals(filter.getTech()) ) remove = true;
            }
            if ( ! filter.getEra().isEmpty() ) {
                if (! mData.getEra().equals(filter.getEra()) ) remove = true;
            }
            if ( ! filter.getLevel().isEmpty() ) {
                if (! mData.getLevel().equals(filter.getLevel()) ) remove = true;
            }
            if ( ! filter.getType().isEmpty() ) {
                if (! mData.getType().equals(filter.getType()) ) remove = true;
            }
            if ( ! filter.getMotive().isEmpty() ) {
                if (! mData.getMotive().equals(filter.getMotive()) ) remove = true;
            }
            if ( filter.getMaxBV() > 0 ) {
                if ((filter.getMinBV() > mData.getBV()) || (mData.getBV() > filter.getMaxBV())) remove = true;
            }
            if ( filter.getMaxCost() > 0 ) {
                if ((filter.getMinCost() > mData.getCost()) || (mData.getCost() > filter.getMaxCost())) remove = true;
            }
            if ( filter.getMaxTonnage() > 0 ) {
                if ((filter.getMinTonnage() > mData.getTonnage()) || (mData.getTonnage() > filter.getMaxTonnage())) remove = true;
            }
            if ( ! filter.getName().isEmpty() ) {
                if (! mData.getName().toUpperCase().startsWith( filter.getName().toUpperCase() ) ) remove = true;
            }
            
            if (remove) m.Remove(mData);
        }

        return m;
    }
    
    public void Write() throws IOException {
        if (List.size() > 0) {
            BufferedWriter FR = new BufferedWriter( new FileWriter( Directory + File.separator + "index.ssi" ) );

            for (int i=0; i < List.size(); i++ ) {
                MechListData m = (MechListData) List.get(i);
                FR.write(m.SerializeIndex());
                FR.newLine();
            }

            FR.close();
        }
    }

    public boolean Read() {
        try {
            BufferedReader FR = new BufferedReader( new FileReader( Directory + File.separator + "index.ssi" ) );
            boolean EOF = false;
            String read = "";
            while( EOF == false ) {
                try {
                    read = FR.readLine();
                    if( read == null ) {
                        // We've hit the end of the file.
                        EOF = true;
                    } else {
                        if( read.equals( "EOF" ) ) {
                            // end of file.
                            EOF = true;
                        } else {
                            String[] Items = read.split(",");
                            if (Items.length >= 11) {
                                List.add(new MechListData(Items));
                            }
                        }
                    }
                } catch (IOException e ) {
                    // probably just reached the end of the file
                    System.out.println( "had an ioexception reading options:\n" + read + "\n\n" );
                    EOF = true;
                    return false;
                }
            }
            FR.close();
            return true;
        } catch ( IOException e ) {
            return false;
        }
    }

    //Fields required for AbstractTableModel
    @Override
    public String getColumnName( int col ) {
        switch( col ) {
            case 0:
                return "Tons";
            case 1:
                return "Type/Model";
            case 2:
                return "BV";
            case 3:
                return "Cost";
            case 4:
                return "Level";
            case 5:
                return "Era";
            case 6:
                return "Tech";
            case 7:
                return "Year";
            case 8:
                return "Source";
        }
        return "";
    }
    public int getRowCount() { return List.size(); }
    public int getColumnCount() { return 9; }
    @Override
    public Class getColumnClass(int c) {
        if (List.size() > 0) {
            return getClassOf(c).getClass();
        } else {
            return String.class;
        }
    }

    public Object getClassOf( int c ) {
        MechListData m = (MechListData) List.get( 0 );
        switch( c ) {
            case 0:
                return m.getTonnage();
            case 1:
                return m.getName();
            case 2:
                return m.getBV();
            case 3:
                return new String();
            case 4:
                return m.getLevel();
            case 5:
                return m.getEra();
            case 6:
                return m.getTech();
            case 7:
                return m.getYear();
            case 8:
                return m.getSource();
        }
        return null;
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }
    
    public Object getValueAt( int row, int col ) {
        MechListData m = (MechListData) List.get( row );
        switch( col ) {
            case 0:
                return m.getTonnage();
            case 1:
                return (m.getName() + " " + m.getModel()).trim();
            case 2:
                return m.getBV();
            case 3:
                return String.format( "%1$,.2f", m.getCost() );
            case 4:
                return m.getLevel();
            case 5:
                return m.getEra();
            case 6:
                return m.getTech();
            case 7:
                return m.getYear();
            case 8:
                return m.getSource();
        }
        return null;
    }
    @Override
    public boolean isCellEditable( int row, int col ) {
        return false;
    }
    @Override
    public void setValueAt( Object value, int row, int col ) {
        return;
    }
}
