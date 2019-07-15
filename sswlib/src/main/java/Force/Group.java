
package Force;

import common.CommonTools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.w3c.dom.Node;
import battleforce.BattleForce;
import battleforce.BattleForceStats;
import java.util.ArrayList;
import list.view.Column;

public class Group {
    private String Name = "",
                   Type = BattleForce.InnerSphere,
                   Logo = "";
    private ArrayList<Unit> Units = new ArrayList<Unit>();
    private Force force;
    public float TotalBV = 0.0f;
    public int TotalPV = 0;

    public Group( String Name, String Type, Force force ) {
        this.Name = Name;
        this.Type = Type;
        this.force = force;
        this.TotalBV = force.TotalForceBVAdjusted;
        this.Logo = force.LogoPath;
    }

    public Group( Node node, int Version, Force force ) throws Exception {
        this.force = force;
        this.Name = node.getAttributes().getNamedItem("name").getTextContent().trim();
        this.Type = force.getType();
        if ( node.getAttributes().getNamedItem("logo") != null ) { this.Logo = node.getAttributes().getNamedItem("logo").getTextContent().trim(); }
        for (int i=0; i < node.getChildNodes().getLength(); i++) {
            Node n = node.getChildNodes().item(i);
            if ( n.getNodeName().equals("unit") ) {
                Unit u = new Unit(n, Version);
                u.setGroup(Name);
                u.Refresh();
                Units.add( u );
            }
        }
        updateBV();
    }

    @Override
    public String toString() {
        String data = force.ForceName + ": " + Name + ": " + Type + " (" + Units.size() + " Units)";
        if ( data.startsWith(":") ) { data = "Unnamed Force: Blank Group: " + Type + " (" + Units.size() + " Units)" ; }
        return data;
    }

    public void updateBV() {
        TotalBV = 0.0f;
        for ( Unit u : Units ) {
            TotalBV += u.TotalBV;
        }
    }

    public void AddUnit( Unit u ) {
        Units.add(u);
    }

    public void Copy( Group g ) {
        this.Name = g.getName();
        this.Type = g.getType();
        this.Logo = g.getLogo();
        this.Units = g.getUnits();
        this.force = g.getForce();
    }

    public void sortUnits() {
        int i = 1, j = 2;
        Unit swap;
        boolean doSwap = false;
        while( i < Units.size() ) {
            //Are the units correctly sorted by tonnage
            if( Units.get( i - 1 ).Tonnage < Units.get( i ).Tonnage ) {
                i = j;
                j += 1;
            } else if ( Units.get( i - 1 ).Tonnage == Units.get( i ).Tonnage ) {
                if ( Units.get( i - 1 ).TypeModel.compareToIgnoreCase( Units.get( i ).TypeModel) <= 0 ) {
                    i = j;
                    j += 1;
                } else {
                    doSwap = true;
                }
            } else {
                doSwap = true;
            }

            if ( doSwap ) {
                swap = Units.get( i - 1 );
                Units.set( i-1, Units.get( i ) );
                Units.set( i, swap );
                i -= 1;
                if( i == 0 ) {
                    i = 1;
                }
                doSwap = false;
            }
        }
    }

    public ArrayList<BattleForce> toBattleForce( int SizeLimit ) {
        ArrayList<BattleForce> bforces = new ArrayList<BattleForce>();
        BattleForce bf = new BattleForce();
        bf.Type = getType();
        bf.ForceName = force.ForceName;
        bf.LogoPath = getLogo();
        for ( Unit u : getUnits() ) {
            //u.LoadMech();
            BattleForceStats stat = u.getBFStats();  //new BattleForceStats(u.m, getName(), u.getGunnery(),u.getPiloting());
            stat.setWarrior(u.getMechwarrior());
            stat.setForceName( force.ForceName );
            stat.setUnit( Name );
            bf.BattleForceStats.add(stat);
            if ( bf.BattleForceStats.size() == SizeLimit ) {
                bforces.add(bf);
                bf = new BattleForce();
                bf.ForceName = force.ForceName;
                bf.Type = Type;
                bf.LogoPath = Logo;
            }
        }
        if ( bf.BattleForceStats.size() > 0 ) {
            bforces.add(bf);
        }
        return bforces;
    }

    public void SerializeXML( BufferedWriter file ) throws IOException {
        file.write( CommonTools.Tabs(3) + "<group name=\"" + this.Name + "\" logo=\"" + this.Logo + "\" type=\"" + this.Type + "\" >");
        file.newLine();
        for ( Unit u : Units ) {
            u.SerializeXML(file);
        }
        file.write( CommonTools.Tabs(3) + "</group>");
        file.newLine();
    }

    public String SerializeClipboard() {
        String data = "";
        for ( Column c : Force.ScenarioClipboardColumns() ) {
            if ( c.Title.equals("Unit") && !getName().isEmpty() ) {
                data += CommonTools.spaceRight(getName(), c.preferredWidth) + CommonTools.Tab;
            } else {
                data += CommonTools.spaceRight(c.Title, c.preferredWidth) + CommonTools.Tab;
            }
        }
        data += CommonTools.NL;

        for ( Unit u : Units ) {
            data += u.SerializeClipboard() + CommonTools.NL;
        }

        if ( force.Groups.size() > 1 ) {
            for ( Column c : Force.ScenarioClipboardColumns() ) {
                if ( c.Title.equals("Adj BV") ) {
                    data += String.format("%1$,.0f", getTotalBV());
                } else {
                    data += CommonTools.spaceRight("", c.preferredWidth) + CommonTools.Tab;
                }
            }
        }
        data += CommonTools.NL + CommonTools.NL;
        return data;
    }

    public ArrayList<Unit> getUnits() {
        return Units;
    }

    public String getName() {
        return Name;
    }

    public String getType() {
        return Type;
    }

    public Force getForce() {
        return force;
    }

    public String getLogo() {
        if ( Logo.isEmpty() ) { Logo = force.LogoPath; }
        return Logo;
    }

    public void setLogo( String logoPath ) {
        this.Logo = logoPath;
    }

    public float getTotalTonnage() {
        float TotTons = 0.0f;
        for ( Unit u : Units ) {
            TotTons += u.Tonnage;
        }
        return TotTons;
    }

    public float getTotalBaseBV() {
        float BaseBV = 0.0f;
        for ( Unit u : Units ) {
            BaseBV += u.BaseBV;
        }
        return BaseBV;
    }

    public float getTotalBV() {
        TotalBV = 0.0f;
        for ( Unit u : Units ) {
            TotalBV += u.TotalBV;
        }
        return TotalBV;
    }
    
    public int getTotalPV() {
        TotalPV = 0;
        for ( Unit u : Units ) {
            TotalPV += u.getBFStats().getPointValue();
        }
        return TotalPV;
    }

    public void setUnits(ArrayList<Unit> Units) {
        this.Units = Units;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    /**
     * @param Name the Name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }
}
