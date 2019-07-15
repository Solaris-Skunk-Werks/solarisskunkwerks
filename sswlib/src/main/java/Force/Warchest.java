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
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.table.AbstractTableModel;
import org.w3c.dom.Node;

/**
 * This object stores information that is specific to the Warchest system
 * for Battletech.  This information includes the cost of the track, the Objectives
 * and their Rewards and also Optional Bonuses and their amounts.
 * 
 * @author George Blouin
 */
public class Warchest implements ifSerializable {
    private int TrackCost = 0;
    private ArrayList<Bonus> bonuses = new ArrayList<Bonus>();
    private ArrayList<Objective> objectives = new ArrayList<Objective>();

    public Warchest() {
        
    }
    
    /**
     * @param node An xml node that contains the required data
     */
    public Warchest( Node node ) {
        setTrackCost(Integer.parseInt(node.getAttributes().getNamedItem("cost").getTextContent().trim()));
        for (int i=0; i < node.getChildNodes().getLength(); i++) {
            Node n = node.getChildNodes().item(i);

            if (n.getNodeName().equals("bonus")) {bonuses.add(new Bonus(n)); }
            if (n.getNodeName().equals("objective")) {objectives.add(new Objective(n)); }
        }
    }

    /**
     * This method includes this objects xml format and any child objects into
     * the XML doc opened and passed in
     * <warchest cost="">
     *  <bonus value=""></bonus>
     *  <objective value=""></objective>
     * </warchest>
     *
     * @param BufferedWriter file  The open file to output the XML format to.
     * @throws java.io.IOException
     */
    public void SerializeXML(BufferedWriter file) throws IOException {
        if ( bonuses.size() > 0 || objectives.size() > 0 ) {
            file.write( CommonTools.Tabs(1) + "<warchest cost=\"" + this.TrackCost + "\">" );
            file.newLine();

            for ( Bonus b : bonuses ) {
                b.SerializeXML(file);
            }

            for ( Objective o : objectives ) {
                o.SerializeXML(file);
            }

            file.write( CommonTools.Tabs(1) + "</warchest>" );
            file.newLine();
        }
    }

    /**
     * This method returns a string of data formatted for output into the users
     * clipboard space.
     *
     */
    public String SerializeClipboard() {
        String data = "";
        int counter = 1;

        data += "Warchest" + CommonTools.NL + CommonTools.NL;
        data += "  Track Cost: " + TrackCost + CommonTools.NL + CommonTools.NL;
        if ( bonuses.size() > 0 ) {
            data += "  Optional Bonuses" + CommonTools.NL;
            for ( Bonus b : bonuses ) {
                data += "    " + b.SerializeClipboard();
            }
            data += CommonTools.NL;
        }
        
        if ( objectives.size() > 0 ) {
            data += "  Objectives" + CommonTools.NL;
            for ( Objective o : objectives ) {
                data += "    " + counter + ". " + o.SerializeClipboard();
                counter += 1;
            }
        }
        return data;
    }

    public String SerializeData() {
        return "";
    }

    public void AddObjective( Objective o ) {
        objectives.add(o);
    }

    public void AddBonus( Bonus b ) {
        bonuses.add(b);
    }

    public int getTrackCost() {
        return TrackCost;
    }

    public void setTrackCost(int TrackCost) {
        this.TrackCost = TrackCost;
    }

    public ArrayList<Bonus> getBonuses() {
        return bonuses;
    }

    public void setBonuses(ArrayList<Bonus> bonuses) {
        this.bonuses = bonuses;
    }

    public ArrayList<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(ArrayList<Objective> objectives) {
        this.objectives = objectives;
    }

    public DefaultListModel getObjectiveList() {
        DefaultListModel model = new DefaultListModel();
        for ( Objective objective : objectives ) {
            model.addElement(objective);
        }
        return model;
    }

    public DefaultListModel getBonusList() {
        DefaultListModel model = new DefaultListModel();
        for ( Bonus bonus : bonuses ) {
            model.addElement(bonus);
        }
        return model;
    }
    
    public AbstractTableModel getBonusTable() {
        AbstractTableModel model = new AbstractTableModel() {

            public int getRowCount() {
                return bonuses.size();
            }

            public int getColumnCount() {
                return 2;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0: return bonuses.get(rowIndex).getValue();
                    case 1: return bonuses.get(rowIndex).getDescription();
                }
                return null;
            }

            @Override
            public String getColumnName( int col ) {
                switch( col ) {
                    case 0: return "Amount";
                    case 1: return "Bonus";
                }
                return null;
            }
        };

        return model;
    }

    public AbstractTableModel getObjectiveTable() {
        AbstractTableModel model = new AbstractTableModel() {

            public int getRowCount() {
                return objectives.size();
            }

            public int getColumnCount() {
                return 2;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0: return objectives.get(rowIndex).getDescription();
                    case 1: return objectives.get(rowIndex).getValue();
                }
                return null;
            }

            @Override
            public String getColumnName( int col ) {
                switch( col ) {
                    case 0: return "Objective";
                    case 1: return "Reward";
                }
                return null;
            }
        };

        return model;
    }
    
}
