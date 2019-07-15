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

package Force;

import common.CommonTools;
import filehandlers.Media;
import Force.View.*;
import Print.ForceListPrinter;
import Print.PrintConsts;
import battleforce.*;

import filehandlers.FileCommon;
import filehandlers.ImageTracker;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import list.view.Column;
import org.w3c.dom.Node;

public class Force extends AbstractTableModel implements ifSerializable {
    public ArrayList<Unit> Units = new ArrayList<Unit>();
    public ArrayList<Group> Groups = new ArrayList<Group>();
    public String ForceName = "",
                  LogoPath = "";
    private String Type = BattleForce.InnerSphere;
    private Image Logo = null;
    public float TotalBaseBV = 0.0f,
                 TotalBasePV = 0.0f,
                 TotalModifier = 0.0f,
                 TotalTonnage = 0.0f,
                 TotalC3BV = 0.0f,
                 TotalSkillBV = 0.0f,
                 TotalModifierBV = 0.0f,
                 TotalAdjustedBV = 0.0f,
                 TotalForceBV = 0.0f,
                 TotalForcePV = 0.0f,
                 UnevenForceMod = 0.0f,
                 TotalForceBVAdjusted = 0.0f;
    public int  TotalUnits = 0,
                NumC3 = 0,
                OpForSize = 0;
    public int Probe = 0,
                Jump = 0,
                ECM = 0,
                HeadCap = 0,
                Eight = 0,
                Ten = 0,
                Physical = 0,
                TSM = 0,
                Speed = 0,
                Armor = 0,
                TC = 0,
                Damage = 0;
    public boolean isDirty = false,
                    useUnevenForceMod = true,
                    unitsChanged = false,
                    printTotals = true;
    private abTable currentModel = new tbTotalWarfare(this);


    public static ArrayList<Column> ScenarioClipboardColumns() {
        ArrayList<Column> cols = new ArrayList<Column>();
        cols.add(new Column(0, "Unit", "Unit", 30));
        cols.add(new Column(0, "Tons", "Tons", 6));
        cols.add(new Column(0, "BV", "BV", 6));
        cols.add(new Column(0, "Mechwarrior", "Mechwarrior", 30));
        //cols.add(new Column(0, "Lance/Star", "Group", 30));
        cols.add(new Column(0, "G/P", "Gunnery", 3));
        cols.add(new Column(0, "Adj BV", "TotalBV", 7));
        return cols;
    }

    public Force(){
        Groups.add(new Group("", Type, this));
    }

    public Force( Force f ) {
        ForceName = f.ForceName;
        LogoPath = f.LogoPath;
        Type = f.getType();
        useUnevenForceMod = f.useUnevenForceMod;
        printTotals = false;
    }

    public Force(Node ForceNode) throws Exception {
        Load( ForceNode );
    }

    public Force(Node ForceNode, int Version) throws Exception {
        Load( ForceNode, Version);
    }

    public void Load( Node ForceNode, int Version ) throws Exception {
        this.ForceName = ForceNode.getAttributes().getNamedItem("name").getTextContent().trim();
        this.LogoPath = ForceNode.getAttributes().getNamedItem("logo").getTextContent().trim();
        if ( ForceNode.getAttributes().getNamedItem("type") != null )
                this.Type = ForceNode.getAttributes().getNamedItem("type").getTextContent().trim();

        for (int i=0; i < ForceNode.getChildNodes().getLength(); i++) {
            Node n = ForceNode.getChildNodes().item(i);
            if (n.getNodeName().equals("group")) {
                    Groups.add( new Group(n, Version, this) );
            }
        }

        for ( Group g : Groups ) {
            Units.addAll(g.getUnits());
        }

        if ( Groups.size() == 0 ) { Groups.add(new Group("", Type, this)); }
        RefreshBV();
    }

    public void Load( Node ForceNode ) throws Exception {
        try {
            this.ForceName = ForceNode.getAttributes().getNamedItem("name").getTextContent().trim();
            if ( ForceNode.getAttributes().getNamedItem("logo") != null )
                this.LogoPath = ForceNode.getAttributes().getNamedItem("logo").getTextContent().trim();
            if ( ForceNode.getAttributes().getNamedItem("type") != null )
                this.Type = ForceNode.getAttributes().getNamedItem("type").getTextContent().trim();

            if ( this.Type.isEmpty() ) { this.Type = BattleForce.InnerSphere; }
            if ( ForceNode.getChildNodes().item(1).getNodeName().equals("group") ) {
                Load( ForceNode, 2 );
            } else {
                Group g = new Group("", Type, this);
                for (int i=0; i < ForceNode.getChildNodes().getLength(); i++) {
                    Node n = ForceNode.getChildNodes().item(i);
                    if (n.getNodeName().equals("unit")) {
                        try {
                            g.AddUnit( new Unit(n) );
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            throw e;
                        }
                    }
                }
                Groups.add( g );
                unitsChanged = true;
            }
            RefreshBV();
        } catch ( Exception e ) {
            throw new Exception("Unable to load Force\n" + e.getMessage());
        }
    }

    public void RefreshBV() {
        NumC3 = 0;
        TotalUnits = Units.size();
        TotalBaseBV = 0.0f;
        TotalBasePV = 0.0f;
        TotalModifier = 0.0f;
        TotalTonnage = 0.0f;
        TotalC3BV = 0.0f;
        TotalSkillBV = 0.0f;
        TotalModifierBV = 0.0f;
        TotalAdjustedBV = 0.0f;
        TotalForceBV = 0.0f;
        TotalForcePV = 0.0f;

        Probe = 0;
        Jump = 0;
        ECM = 0;
        HeadCap = 0;
        Eight = 0;
        Ten = 0;
        Physical = 0;
        TSM = 0;
        Speed = 0;
        Armor = 0;
        TC = 0;
        Damage = 0;

        //First pass to determine C3 totals
        for ( Unit u : getUnits() ) {
             if (u.UsingC3) {
                NumC3++;
                TotalC3BV += u.BaseBV;
            }
        }

        if (TotalC3BV > 1) TotalC3BV *= .05;

        for ( Unit u : getUnits() ) {
            if ( u.UsingC3 )
                u.setForceC3BV(TotalC3BV);
            
            TotalBaseBV += u.BaseBV;
            TotalModifier += u.MiscMod;
            TotalTonnage += u.Tonnage;
            TotalSkillBV += u.SkillsBV;
            TotalModifierBV += u.ModifierBV;
            TotalAdjustedBV += u.TotalBV;

            TotalBasePV += u.getBFStats().getBasePV();
            TotalForcePV += u.getBFStats().getPointValue();

            //Factors rollup
            Probe += u.Probe;
            Jump += u.Jump;
            ECM += u.ECM;
            HeadCap += u.HeadCap;
            Eight += u.Eight;
            Ten += u.Ten;
            Physical += u.Physical;
            TSM += u.TSM;
            Speed += u.Speed;
            Armor += u.Armor;
            TC += u.TC;
            Damage += u.Damage;
        }

        TotalForceBV += TotalAdjustedBV; //+ TotalC3BV;
        TotalForceBVAdjusted = TotalForceBV;
        if ( useUnevenForceMod ) {
            UnevenForceMod = CommonTools.GetForceSizeMultiplier(OpForSize, Units.size());
            if (Units.size() > OpForSize && OpForSize > 0) {
                TotalForceBVAdjusted = TotalForceBV * UnevenForceMod;
            }
        }

        currentModel.fireTableDataChanged();
    }

    public void SetTotals(Force f ) {
        printTotals = true;
        TotalUnits = f.TotalUnits;
        TotalTonnage = f.TotalTonnage;
        TotalBaseBV = f.TotalBaseBV;
        TotalForceBV = f.TotalForceBV;
        TotalForceBVAdjusted = f.TotalForceBVAdjusted;
    }

    public void AddUnit( Unit u ) {
        boolean isAdded = false;
        u.Refresh();
        for ( Group g : Groups ) {
            if ( g.getName().equals( u.getGroup() ) ) {
                g.AddUnit(u);
                isAdded = true;
                break;
            }
        }
        if ( !isAdded ) {
            Group g = new Group(u.getGroup(), Type, this);
            g.AddUnit(u);
            Groups.add(g);
        }
        isDirty = true;
        unitsChanged = true;
        RefreshBV();
    }

    public void RemoveUnit( Unit u ){
        for ( Group g : Groups ) {
            if ( g.getName().equals( u.getGroup() ) ) {
                g.getUnits().remove(u);
                break;
            }
        }
        unitsChanged = true;
        isDirty = true;
        RefreshBV();
    }

    public void GroupUnit( Unit u ) {
        boolean isAdded = false;

        for ( Group g : Groups ) {
            if ( g.getName().equals( u.getPrevGroup() ) ) {
                g.getUnits().remove(u);
            }

            if ( g.getName().equals(u.getGroup() ) ) {
                g.AddUnit(u);
                isAdded = true;
            }
        }
        if ( !isAdded ) {
            Group g = new Group(u.getGroup(), Type, this);
            g.AddUnit(u);
            Groups.add(g);
        }

        clearEmptyGroups();
        this.isDirty = true;
    }

    public void clearEmptyGroups() {
        ArrayList<Group> remove = new ArrayList<Group>();

        for ( Group g : Groups ) {
            if ( g.getUnits().size() == 0 ) { remove.add(g); }
        }
        
        for ( Group d : remove ) {
            Groups.remove(d);
        }
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        sortForPrinting();

        file.write( CommonTools.Tabs(2) + "<force name=\"" + this.ForceName + "\" logo=\"" + this.LogoPath + "\" type=\"" + this.Type + "\">" );
        file.newLine();

        for ( Group g : Groups ) {
            g.SerializeXML(file);
        }

        file.write( CommonTools.Tabs(2) + "</force>" );
        file.newLine();
        isDirty = false;
    }

    public String SerializeFactors() {
        String data = "";

        data += FileCommon.CSVFormat("");
        data += FileCommon.CSVFormat("");
        data += FileCommon.CSVFormat(Probe);
        data += FileCommon.CSVFormat(ECM);
        data += FileCommon.CSVFormat(Speed);
        data += FileCommon.CSVFormat(Jump);
        data += FileCommon.CSVFormat(TSM);
        data += FileCommon.CSVFormat(Physical);
        data += FileCommon.CSVFormat(Armor);
        data += FileCommon.CSVFormat(TC);
        data += FileCommon.CSVFormat(Eight);
        data += FileCommon.CSVFormat(Ten);
        data += FileCommon.CSVFormat(HeadCap);
        data += FileCommon.CSVFormat(Damage);
        data += FileCommon.CSVFormat((int)TotalBaseBV);
        data += FileCommon.CSVFormat((int)TotalAdjustedBV);

        return data.substring(0, data.length()-2);
    }

    public void SerializeMUL(BufferedWriter file) throws IOException {
        sortForPrinting();
        file.write("<unit>");
        file.newLine();

        for ( Unit u : getUnits() ) {
            u.SerializeMUL(file);
        }

        file.write("</unit>");
        file.newLine();
    }

    public String SerializeClipboard() {
        sortForPrinting();
        String data = "";

        data += this.ForceName + CommonTools.NL;
        for (int s=0; s < 95; s++ ) { data += "-"; }
        data += CommonTools.NL;

        for ( Group g : Groups ) {
            data += g.SerializeClipboard();
        }

        for ( Column c : Force.ScenarioClipboardColumns() ) {
            if ( c.Title.equals("Adj BV") ) {
                data += String.format("%1$,.0f", TotalAdjustedBV);
            } else {
                data += CommonTools.spaceRight("", c.preferredWidth) + CommonTools.Tab;
            }
        }
        data += CommonTools.Tab;

        return data;
    }

    public String SerializeData() {
        return SerializeClipboard();
    }

    public void RenderPrint(ForceListPrinter p, ImageTracker imageTracker, boolean PrintTotals) {
        if ( getUnits().size() == 0 ) { return; }
        p.setFont(PrintConsts.SectionHeaderFont);
        if ( p.PrintLogo() ) {
            loadLogo(imageTracker);
            if (Logo != null) {
                p.Graphic.drawImage(Logo, p.currentX, p.currentY-20, 25, 25, null);
                p.currentX += 30;
            }
        }
        p.WriteStr(ForceName, 0);
        p.NewLine();
        p.currentY += 5;

        for ( Group g : Groups ) {
            String curGroup = g.getName();
            if ( g.getName().isEmpty() ) {
                p.setFont(PrintConsts.ItalicFont);
                curGroup = "Unit";
            } else {
                p.setFont(PrintConsts.BoldFont);
            }
            p.WriteStr(curGroup, 120);

            p.setFont(PrintConsts.ItalicFont);
            String colName = "Mechwarrior";
            if( curGroup.length() >= 25) colName = "";
            p.WriteStr(colName, 140);
            p.WriteStr("Type", 60);
            p.WriteStr("Tonnage", 50);
            p.WriteStr("Base BV", 40);
            p.WriteStr("G/P", 30);
            p.WriteStr("Modifier", 40);
            p.WriteStr("C3", 30);
            p.WriteStr("Total BV", 40);
            p.NewLine();

            for ( Unit u : g.getUnits() ) {
                u.RenderPrint(p);
            }

            if ( Groups.size() > 1 ) {
                //Output Totals
                p.currentY -= 2;
                p.setFont(PrintConsts.SmallItalicFont);
                p.WriteStr(g.getUnits().size() + " Units", 120);
                p.WriteStr("", 140);
                p.WriteStr("", 60);
                p.WriteStr(String.format("%1$,.2f", g.getTotalTonnage()), 50);
                p.WriteStr(String.format("%1$,.0f", g.getTotalBaseBV()), 40);
                p.WriteStr("", 30);
                p.WriteStr("", 40);
                p.WriteStr("", 30);
                p.WriteStr(String.format("%1$,.0f", g.getTotalBV()), 20);
                p.NewLine();
                p.currentY += 5;
            }
        }

        //Output Totals
        if ( printTotals ) {
            p.WriteLine();

            p.setFont(PrintConsts.ItalicFont);
            p.WriteStr(TotalUnits + " Units", 120);
            p.WriteStr("", 140);
            p.WriteStr("", 60);
            p.WriteStr(String.format("%1$,.2f", TotalTonnage), 50);
            p.WriteStr(String.format("%1$,.0f", TotalBaseBV), 40);
            p.WriteStr("", 30);
            p.WriteStr("", 40);
            p.WriteStr("", 30);
            p.setFont(PrintConsts.BoldFont);
            p.WriteStr(String.format("%1$,.0f", TotalForceBV), 20);
            p.NewLine();
            if ( TotalForceBV != TotalForceBVAdjusted ) {
                p.currentX = 510;
                p.setFont(PrintConsts.ItalicFont);
                p.WriteStr(String.format("%1$,.0f", TotalForceBVAdjusted), 0);
            }
        }
        p.setFont(PrintConsts.PlainFont);
    }

    public void RenderPrint(ForceListPrinter p, ImageTracker imageTracker) {
        RenderPrint(p, imageTracker, printTotals);
    }

    public void Clear() {
        Units.clear();
        Groups.clear();
        ForceName = "";
        LogoPath = "";
        TotalBaseBV = 0.0f;
        TotalModifier = 0.0f;
        TotalTonnage = 0.0f;
        TotalC3BV = 0.0f;
        TotalSkillBV = 0.0f;
        TotalModifierBV = 0.0f;
        TotalAdjustedBV = 0.0f;
        TotalForceBV = 0.0f;
        UnevenForceMod = 0.0f;
        TotalForceBVAdjusted = 0.0f;
        NumC3 = 0;
        OpForSize = 0;
        isDirty = false;
        useUnevenForceMod = false;
        RefreshBV();
    }

    public boolean isSaveable() {
        boolean Flag = true;
        RefreshBV();
        if ( ForceName.isEmpty() ) { Flag = false; }
        if ( Units.size() == 0 ) { Flag = false; }
        return Flag;
    }

    public void loadLogo(ImageTracker imageTracker) {
        if (!LogoPath.isEmpty()) {
            Logo = imageTracker.getImage(LogoPath);
        }
    }

    public void setupTable( JTable tbl ) {
        tbl.setModel(currentModel);
        currentModel.setupTable(tbl);
    }

    public void setCurrentModel( abTable model ) {
        currentModel = model;
    }

    public abTable getCurrentModel() {
        return currentModel;
    }

    public void sortForPrinting() {
        //Sort by Group
        sortByGroupName();

        //Sort inside each group by tonnage then Name
        for ( Group g : Groups ) {
            g.sortUnits();
        }
    }

    public void sortByGroupName() {
        int i = 1, j = 2;
        Group swap;
        while( i < Groups.size() ) {
            // get the two items we'll be comparing
            if( Groups.get( i - 1 ).getName().compareToIgnoreCase( Groups.get( i ).getName()) <= 0 ) {
                i = j;
                j += 1;
            } else {
                swap = Groups.get( i - 1 );
                Groups.set( i - 1, Groups.get( i ) );
                Groups.set( i, swap );
                i -= 1;
                if( i == 0 ) {
                    i = 1;
                }
            }
        }
    }


    public BattleForce toBattleForce() {
        sortForPrinting();
        String error = "";

        BattleForce bf = new BattleForce();
        bf.Type = Type;
        bf.ForceName = ForceName;
        bf.LogoPath = this.LogoPath;
        for ( int i=0; i < Units.size(); i++ ) {
            Unit u = (Unit) Units.get(i);
            if ( u.getBFStats() != null ) {
                bf.BattleForceStats.add(u.getBFStats());
            } else {
                u.LoadUnit();
                switch(u.UnitType) {
                    case common.CommonTools.BattleMech:
                        if ( u.m != null ) {
                            BattleForceStats stat = new BattleForceStats(u.m,u.getGroup(), u.getGunnery(),u.getPiloting());
                            stat.setWarrior(u.getMechwarrior());
                            bf.BattleForceStats.add(stat);
                        } else {
                            error += "Could not load " + u.TypeModel + ".  The filename is most likely blank.\n";
                        }
                        break;
                    case common.CommonTools.Vehicle:
                        if ( u.v != null ) {
                            BattleForceStats stat = new BattleForceStats(u.v, u.getGroup(), u.getGunnery(),u.getPiloting());
                            stat.setWarrior(u.getMechwarrior());
                            bf.BattleForceStats.add(stat);
                        } else {
                            error += "Could not load " + u.TypeModel + ".  The filename is most likely blank.\n";
                        }
                }
            }
        }

        if ( !error.isEmpty() ) { Media.Messager(error); }
        return bf;
    }

    public ArrayList<BattleForce> toBattleForceByGroup( int SizeLimit ) {
        sortForPrinting();
        ArrayList<BattleForce> Forces = new ArrayList<BattleForce>();

        for ( Group g : Groups ) {
            ArrayList<BattleForce> groupForces = g.toBattleForce(SizeLimit);
            for ( BattleForce bf : groupForces ) {
                Forces.add(bf);
            }
        }

        return Forces;
    }

    public Image getLogo(ImageTracker imageTracker) {
        if ( Logo == null ) {loadLogo(imageTracker);}
        return Logo;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
        for ( Group g : Groups ) {
            g.setType(Type);
        }
    }

    @Override
    public String getColumnName( int col ) {
        switch( col ) {
            case 0:
                return "Unit";
            case 1:
                return "Type";
            case 2:
                return "Mechwarrior";
            case 3:
                return "Lance/Star";
            case 4:
                return "Tons";
            case 5:
                return "Base BV";
            case 6:
                return "G";
            case 7:
                return "P";
            case 8:
                return "Mod";
            case 9:
                return "C3";
            case 10:
                return "Adj BV";
        }
        return "";
    }
    public int getRowCount() { return Units.size(); }
    public int getColumnCount() { return 11; }
    @Override
    public Class getColumnClass(int c) {
        if (Units.size() > 0) {
            return getClassOf(0, c).getClass();
        } else {
            return String.class;
        }
    }
    public Object getClassOf( int row, int col ) {
        Unit u = (Unit) Units.get( row );
        switch( col ) {
            case 0:
                return u.TypeModel;
            case 1:
                return "";
            case 2:
                return u.getMechwarrior();
            case 3:
                return u.getGroup();
            case 4:
                return u.Tonnage;
            case 5:
                return u.BaseBV;
            case 6:
                return u.getGunnery();
            case 7:
                return u.getPiloting();
            case 8:
                return u.MiscMod;
            case 9:
                return "";
            case 10:
                return "";
        }
        return "";
    }
    public Object getValueAt( int row, int col ) {
        Unit u = (Unit) Units.get( row );
        switch( col ) {
            case 0:
                return u.TypeModel;
            case 1:
                return CommonTools.UnitTypes[u.UnitType];
            case 2:
                return u.getMechwarrior();
            case 3:
                return u.getGroup();
            case 4:
                return u.Tonnage;
            case 5:
                return u.BaseBV;
            case 6:
                return u.getGunnery();
            case 7:
                return u.getPiloting();
            case 8:
                return u.MiscMod;
            case 9:
                if( u.UsingC3 ) {
                    return "Yes";
                } else {
                    return "No";
                }
            case 10:
                return String.format( "%1$,.0f", u.TotalBV );
        }
        return null;
    }
    @Override
    public boolean isCellEditable( int row, int col ) {
        switch( col ) {
            case 2:
                return true;
            case 3:
                return true;
            case 6:
                return true;
            case 7:
                return true;
            case 8:
                return true;
        }
        return false;
    }
    @Override
    public void setValueAt( Object value, int row, int col ) {
        Unit u = (Unit) Units.get( row );
        switch( col ) {
            case 2:
                u.setMechwarrior(value.toString());
                break;
            case 3:
                u.setGroup(value.toString());
                GroupUnit(u);
                break;
            case 6:
                u.setGunnery(Integer.parseInt(value.toString()));
                break;
            case 7:
                u.setPiloting(Integer.parseInt(value.toString()));
                break;
            case 8:
                u.MiscMod = Float.parseFloat(value.toString());
                break;
        }
        isDirty = true;
        u.Refresh();
        RefreshBV();
    }

    public ArrayList<Unit> getUnits() {
        if ( unitsChanged ) {
            Units.clear();
            for ( Group g : Groups ) {
                Units.addAll(g.getUnits());
            }
            unitsChanged = false;
        }
        return Units;
    }

    public void AddGroup( Group g ) {
        Groups.add(g);
        unitsChanged = true;
    }

    public int getForcePrintCount() {
        int count = 0;
        count += (Groups.size() * 2);
        count += getUnits().size();
        return count;
    }
}
