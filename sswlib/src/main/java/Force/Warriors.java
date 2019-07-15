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

import IO.DataReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import org.w3c.dom.*;

/**
 * This object loads, saves, and stores a list of personnel
 *
 * @author George Blouin
 */
public class Warriors extends AbstractTableModel {
    private ArrayList<Warrior> list = new ArrayList<Warrior>();
    private String defaultPath = "data/Personnel/WarriorList.psn",
                    Title = "",
                    PersonnelFile = "";

    public Warriors() {
        Load(defaultPath);
    }

    public Warriors ( String filename ) {
        Load(filename);
    }

    /**
     * Load a list of personnel that has been saved to the disk.  This file must
     * have a .psn extension
     *
     * @param filename
     */
    public void Load( String filename ) {
        if ( !filename.isEmpty() ) {
            Clear();
            DataReader reader = new DataReader();
            Node node;
            try {
                node = reader.ReadNode(filename, "warriors");

                NamedNodeMap map = node.getAttributes();
                this.Title = map.getNamedItem("title").getTextContent().trim();

                if ( node.hasChildNodes() ) {
                    for (int i=0; i < node.getChildNodes().getLength(); i++) {
                        if ( node.getChildNodes().item(i).getNodeName().equals("warrior")) {
                            list.add(new Warrior(node.getChildNodes().item(i)));
                        }
                    }
                }
                setPersonnelFile(filename);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        fireTableDataChanged();
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void Add( Warrior warrior ) {
        list.add(warrior);
        fireTableDataChanged();
    }

    public Warrior Get( int index ) {
        return (Warrior) list.get(index);
    }

    public void Remove( Warrior warrior ) {
        list.remove(warrior);
        fireTableDataChanged();
    }

    public void Clear() {
        list.clear();
        fireTableDataChanged();
    }

    public DefaultListModel getModel() {
        DefaultListModel model = new DefaultListModel();

        for (int i = 0; i < list.size(); i++) {
            Warrior w = (Warrior) list.get(i);
            model.addElement(w);
        }

        return model;
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write( "<warriors title=\"" + this.Title + "\">" );
        file.newLine();

        for ( Warrior w : list ) {
            w.SerializeXML(file);
        }

        file.write("</warriors>" );
    }


    public void setupTable( JTable tbl ) {
        tbl.setModel(this);

        //Create a sorting class and apply it to the list
        TableRowSorter Leftsorter = new TableRowSorter<Warriors>(this);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        Leftsorter.setSortKeys(sortKeys);
        tbl.setRowSorter(Leftsorter);

        tbl.getColumnModel().getColumn(0).setPreferredWidth(60);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbl.getColumnModel().getColumn(3).setPreferredWidth(20);
    }
        
    @Override
    public String getColumnName( int col ) {
        switch( col ) {
            case 0:
                return "Name";
            case 1:
                return "Rank";
            case 2:
                return "Faction";
            case 3:
                return "Skills";
        }
        return "";
    }
    public int getRowCount() { return list.size(); }
    public int getColumnCount() { return 4; }
    @Override
    public Class getColumnClass(int c) {
        if (list.size() > 0) {
            return getClassOf(0, c).getClass();
        } else {
            return String.class;
        }
    }
    public Object getClassOf( int row, int col ) {
        Warrior w = list.get( row );
        switch( col ) {
            case 0:
                return w.getName();
            case 1:
                return "";
            case 2:
                return "";
            case 3:
                return "";
        }
        return "";
    }
    public Object getValueAt( int row, int col ) {
        Warrior w = list.get( row );
        switch( col ) {
            case 0:
                return w.getName();
            case 1:
                return w.getRank();
            case 2:
                return w.getFaction();
            case 3:
                return w.getSkills();
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
     * @return the PersonnelFile
     */
    public String getPersonnelFile() {
        return PersonnelFile;
    }

    /**
     * @param PersonnelFile the PersonnelFile to set
     */
    public void setPersonnelFile(String PersonnelFile) {
        this.PersonnelFile = PersonnelFile;
    }
}
