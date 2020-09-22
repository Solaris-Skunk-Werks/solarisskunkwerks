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

package list;

import filehandlers.UnitCacheParser;
import list.view.abView;
import list.view.tbTotalWarfareView;

import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.util.ArrayList;


public class UnitList extends AbstractTableModel {
    private ArrayList<UnitListData> List = new ArrayList<UnitListData>();
    private String Directory = "";
    private int IndexVersion = 10;
    private abView currentModel = new tbTotalWarfareView(this);
    String[] Extensions = { ".ssw", ".saw" };

    public UnitList() {

    }
    
    public UnitList(String directory) {
        this(directory, true);
    }

    public UnitList( String directory, boolean useIndex ) {
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
    
    final void Load( String Directory ) {
        File d = new File(Directory);
        if ( d.isDirectory() && !d.isHidden() ) {
            if( d.listFiles() == null ) { return; }
            for ( File f : d.listFiles() ) {
                if ( f.isFile() && EditorFile(f) ) {
                    Add(f);
                }
                if ( f.isDirectory() && !f.isHidden() ) {
                    Load( f.getPath() );
                }
            }
        }
    }
    
    boolean EditorFile(File f) {
        for ( String ext : Extensions ) {
            if ( f.getPath().endsWith(ext) ) return true;
        }
        return false;
    }

    public void Add( File f ) {
        try
        {
            UnitListData mData = new UnitListData( f.getCanonicalPath(), getDirectory());
            if (mData.isOmni()) {
                for ( int d=0; d < mData.Configurations.size(); d++ ) {
                    List.add((UnitListData) mData.Configurations.get(d));
                }
            } else {
                List.add(mData);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void Add( UnitListData m ) {
        List.add(m);
    }

    public UnitListData Get( int row ) {
        return (UnitListData) List.get(row);
    }

    public void Remove(UnitListData m) {
        List.remove(m);
    }

    public void RemoveAll() {
        List.clear();
    }

    public int Size() {
        return List.size();
    }

    public ArrayList<UnitListData> getList() {
        return List;
    }

    public void setList(ArrayList<UnitListData> l) {
        this.List = l;
    }

    public UnitList Filter(ListFilter filter) {
        UnitList m = new UnitList();
        for ( UnitListData mech : List ) {
            m.Add(mech);
        }

        boolean remove = false;
        for ( UnitListData mData : List ) {
            remove = false;

            if ( filter.getUnitType() < 99 ) {
                if ( mData.getUnitType() != filter.getUnitType() ) remove = true;
            }
            if ( !filter.getExtension().isEmpty() ) {
                if (! mData.getFilename().endsWith(filter.getExtension())) remove = true;
            }
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
                if (! mData.getType().equals(filter.getType().replace(" ", "")) ) remove = true;
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
            if ( filter.getMaxYear() > 0 ) {
                if ((filter.getMinYear() > mData.getYear()) || (mData.getYear() > filter.getMaxYear())) remove = true;
            }
            if ( filter.getMinMP() > 0 ) {
                if ( !mData.getInfo().isEmpty() ) {
                    String[] parts = mData.getInfo().split(" ");
                    for ( String part : parts ) {
                        if ( part.contains("/") ) {
                            String[] mp = part.split("/");
                            if ( mp[0].contains("[") ) { mp[0] = mp[0].substring(mp[0].indexOf("["), mp[0].indexOf("]")).replace("[", "").replace("]", ""); }
                            try
                            {
                                int Walk = Integer.parseInt(mp[0]);
                                if (filter.getMinMP() > Walk) remove = true;
                                break;
                            } catch ( Exception e ) {
                                //Wrong field!
                            }
                        }
                    }
                } else {
                    remove = true;
                }
            }
            if ( ! filter.getName().isEmpty() ) {
                if ( (! mData.getTypeModel().toUpperCase().contains( filter.getName().toUpperCase() ) ) &&
                        (! mData.getName().toUpperCase().contains( filter.getName().toUpperCase() ) ) &&
                        (! mData.getModel().toUpperCase().contains( filter.getName().toUpperCase() ) )) remove = true;
            }
            if ( ! filter.getSource().isEmpty() ) {
                if (! mData.getSource().toUpperCase().contains( filter.getSource().toUpperCase() ) ) remove = true;
            }
            
            if (remove) m.List.remove(mData);
        }

        return m;
    }
    
    public final void Write() throws IOException {
        if (List.size() > 0) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(getDirectory() + File.separator + "index.ssi") );
            bw.write("version:" + IndexVersion);
            bw.newLine();
            int numWritten = UnitCacheParser.WriteUnitCache(List, bw);
            bw.flush();
            bw.close();
            System.out.println("Wrote " + numWritten + " units to cache.");
        }
    }

    public final boolean Read() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(getDirectory() + File.separator + "index.ssi"));
            int version = Integer.parseInt(br.readLine().split(":")[1]);
            if (version != IndexVersion) {
                return false;
            }
            List = UnitCacheParser.LoadUnitCache(br);
            br.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
        UnitListData m = (UnitListData) List.get( 0 );
        switch( c ) {
            case 0:
                return m.getTonnage();
            case 1:
                return m.getFullName();
            case 2:
                return m.getBV();
            case 3:
                return Integer.class;
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
        UnitListData m = (UnitListData) List.get( row );
        switch( col ) {
            case 0:
                return m.getTonnage();
            case 1:
                return m.getFullName();
            case 2:
                return m.getBV();
            case 3:
                return String.format( "%1$,.0f", m.getCost() );
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
    }

    /**
     * @return the currentModel
     */
    public abView getCurrentModel() {
        return currentModel;
    }

    /**
     * @param currentModel the currentModel to set
     */
    public void setCurrentModel(abView currentModel) {
        this.currentModel = currentModel;
    }

    public String getDirectory() {
        return Directory;
    }
}
