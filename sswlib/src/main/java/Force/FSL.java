/*
Copyright (c) 2009, George Blouin Jr. (skyhigh@solaris7.com)
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

package Force;

import common.CommonTools;

import java.io.*;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.AbstractTableModel;

public class FSL extends AbstractTableModel {
    private ArrayList Items = new ArrayList();

    public void Add( FSLItem item ) {
        Items.add( item );
    }

    public void Add( String Line ) {
        Items.add( new FSLItem( Line ));
    }

    public void Remove( FSLItem item ) {
        Items.remove( item );
    }

    public int Size() {
        return Items.size();
    }

    public void Load( String filename ) throws FileNotFoundException, IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String line = null;
            
            //skip the first line as it has headers
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                if ( line.trim().length() > 0 ) { Add(line); }
            }
        } catch (IOException ex) {
            //do nothing
        }
    }

    public DefaultComboBoxModel getFactions() {
        ArrayList storage = new ArrayList();
        DefaultComboBoxModel list = new DefaultComboBoxModel();
        for ( int i=0; i < Items.size(); i++ ) {
            FSLItem item = (FSLItem) Items.get(i);
            if ( ! storage.contains( item.Faction ) ) {
                list.addElement( item.Faction );
                storage.add(item.Faction);
            }
        }
        return list;
    }

    public DefaultComboBoxModel getTypes() {
        ArrayList storage = new ArrayList();
        DefaultComboBoxModel list = new DefaultComboBoxModel();
        for ( int i=0; i < Items.size(); i++ ) {
            FSLItem item = (FSLItem) Items.get(i);
            if ( ! storage.contains( item.Type ) ) {
                list.addElement( item.Type );
                storage.add(item.Type);
            }
        }
        return list;
    }

    public DefaultComboBoxModel getSources() {
        ArrayList storage = new ArrayList();
        DefaultComboBoxModel list = new DefaultComboBoxModel();
        for ( int i=0; i < Items.size(); i++ ) {
            FSLItem item = (FSLItem) Items.get(i);
            if ( ! storage.contains( item.Source ) ) {
                list.addElement( item.Source );
                storage.add(item.Source);
            }
        }
        return list;
    }

    public DefaultComboBoxModel getEras() {
        ArrayList storage = new ArrayList();
        DefaultComboBoxModel list = new DefaultComboBoxModel();
        for ( int i=0; i < Items.size(); i++ ) {
            FSLItem item = (FSLItem) Items.get(i);
            if ( ! storage.contains( item.Era ) ) {
                list.addElement( item.Era );
                storage.add(item.Era);
            }
        }
        return list;
    }

    @Override
    public String getColumnName( int col ) {
        switch( col ) {
            case 0:
                return "Faction";
            case 1:
                return "Type";
            case 2:
                return "Name";
            case 3:
                return "BV";
            case 4:
                return "Tons";
            case 5:
                return "Cost";
            case 6:
                return "Rules Level";
            case 7:
                return "Tech Base";
            case 8:
                return "Source";
            case 9:
                return "Date";
        }
        return "";
    }
    public int getRowCount() { return Items.size(); }
    public int getColumnCount() { return 10; }
    @Override
    public Class getColumnClass(int c) {
        if (Items.size() > 0) {
            return getClassOf(0, c).getClass();
        } else {
            return String.class;
        }
    }
    public Object getClassOf( int row, int col ) {
        FSLItem u = (FSLItem) Items.get( row );
        switch( col ) {
            case 0:
                return u.Faction;
            case 1:
                return u.Type;
            case 2:
                return u.Name;
            case 3:
                return u.BV;
            case 4:
                return u.Tonnage;
            case 5:
                return u.Cost;
            case 6:
                return u.RulesLevel;
            case 7:
                return u.TechBase;
            case 8:
                return u.Source;
            case 9:
                return u.Date;
        }
        return "";
    }
    public Object getValueAt( int row, int col ) {
        FSLItem u = (FSLItem) Items.get( row );
        switch( col ) {
            case 0:
                return u.Faction;
            case 1:
                return u.Type;
            case 2:
                return u.Name;
            case 3:
                return u.BV;
            case 4:
                return u.Tonnage;
            case 5:
                return u.Cost;
            case 6:
                return u.RulesLevel;
            case 7:
                return u.TechBase;
            case 8:
                return u.Source;
            case 9:
                return u.Date;
        }
        return null;
    }
    @Override
    public boolean isCellEditable( int row, int col ) {
        return false;
    }










    public class FSLItem {
        private String  Faction = "",
                        Type = "",
                        Name = "",
                        RulesLevel = "",
                        TechBase = "",
                        Source = "",
                        Date = "",
                        Era = "";

        private float   BV = 0.0f,
                        Cost = 0.0f;

        private int     Tonnage = 0;

        public FSLItem( String Faction, String Type, String Name, String RulesLevel, String TechBase, String Source, String Date, String Era, float BV, float Cost, int Tonnage ) {
            this.Faction = Faction;
            this.Type = Type;
            this.Name = Name;
            this.RulesLevel = RulesLevel;
            this.TechBase = TechBase;
            this.Source = Source;
            this.Date = Date;
            this.Era = Era;
            this.BV = BV;
            this.Cost = Cost;
            this.Tonnage = Tonnage;
        }

        public FSLItem( String Line ) {
            //javax.swing.JOptionPane.showMessageDialog(null, "Given: " + Line);
            if ( Line.contains(CommonTools.Tab) ) {
                String[] data = Line.split(CommonTools.Tab);
                if ( data.length >= 12 ) {
                    this.Faction = data[0].trim();
                    this.Type = data[1].trim();
                    this.Name = data[2].trim();
                    try {
                        this.Tonnage = Integer.parseInt(data[4].trim());
                    } catch ( Exception e ) {
                        //do nothing
                    }
                    try {
                        this.Cost = Float.parseFloat(data[5].trim());
                    } catch ( Exception e ) {
                        //do nothing
                    }
                    this.RulesLevel = data[6].trim();
                    this.TechBase = data[7].trim();
                    this.Source = data[8].trim();
                    this.Date = data[9].trim();
                    this.Era = data[10].trim();
                    try {
                        this.BV = Float.parseFloat(data[11].trim());
                    } catch ( Exception e ) {
                        //do nothing
                    }
                }
            }
        }
    }
}
