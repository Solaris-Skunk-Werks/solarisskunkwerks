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

import java.io.File;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;


public class MechList extends AbstractTableModel {
    private Vector List = new Vector();

    public MechList() throws Exception {

    }
    
    public MechList(String directory) throws Exception {
        FileList fl = new FileList(directory);
        for ( int i=0; i <= fl.getFiles().length-1; i++ ) {
            File f = fl.getFiles()[i];
            if (f.isFile() && f.getCanonicalPath().endsWith(".ssw")) {
                try
                {
                    MechListData mData = new MechListData(f.getCanonicalPath());
                    if (mData.isOmni()) {
                        for ( int d=0; d < mData.Configurations.size(); d++ ) {
                            List.add((MechListData) mData.Configurations.get(d));
                        }
                    } else {
                        List.add(mData);
                    }
                } catch (Exception e) {
                    throw new Exception("[MechList " + f.getCanonicalPath() + " :: " + fl.getFiles().length + " :: " + e.getMessage() + "]");
                }
            }
        }
    }

    public void Add( File f ) throws Exception {
        if (f.isFile() && f.getCanonicalPath().endsWith(".ssw")) {
            try
            {
                MechListData mData = new MechListData(f.getCanonicalPath());
                if (mData.isOmni()) {
                    for ( int d=0; d < mData.Configurations.size(); d++ ) {
                        List.add((MechListData) mData.Configurations.get(d));
                    }
                } else {
                    List.add(mData);
                }
            } catch (Exception e) {
                throw new Exception("[MechList " + f.getCanonicalPath() + " :: " + e.getMessage() + "]");
            }
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

    public int Size() {
        return List.size();
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
                return "Era";
            case 5:
                return "Tech";
        }
        return "";
    }
    public int getRowCount() { return List.size(); }
    public int getColumnCount() { return 6; }
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    public Object getValueAt( int row, int col ) {
        MechListData m = (MechListData) List.get( row );
        switch( col ) {
            case 0:
                return m.getTonnage();
            case 1:
                return m.getName();
            case 2:
                return m.getBV();
            case 3:
                return m.getCost();
            case 4:
                return m.getEra();
            case 5:
                return m.getTech();
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
