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
import Force.View.abTable;

import battleforce.BattleForce;
import battleforce.BattleForceStats;
import filehandlers.FileCommon;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import org.w3c.dom.Node;

public class Scenario implements ifSerializable {
    public int VersionNumber = 2;
    private boolean forceSizeModifier = false,
                    allowOverwrite = true,
                    isDirty = false;
    private String Name = "",
                    Source = "",
                    Situation = "",
                    Setup = "",
                    Attacker = "",
                    Defender = "",
                    VictoryConditions = "",
                    SpecialRules = "",
                    Aftermath = "";
    private ArrayList<Force> forces = new ArrayList<Force>();
    private Warchest warchest = new Warchest();
    private abTable currentModel;

    public Scenario() {
        forces.add(new Force());
        forces.add(new Force());
    }

    public Scenario( Force force ) {
        forces.add(force);
        forces.add(new Force());
    }

    public Scenario( ArrayList<Force> forces ) {
        this.forces = forces;
    }

    public Scenario( Node node ) {
        String errorMessage = "";

        this.Name = node.getAttributes().getNamedItem("name").getTextContent().trim();
        this.VersionNumber = Integer.parseInt(node.getAttributes().getNamedItem("version").getTextContent().trim());
        this.allowOverwrite = Boolean.parseBoolean(node.getAttributes().getNamedItem("overwrite").getTextContent().trim());
        if ( node.getAttributes().getNamedItem("forcesizemodifier") != null ) this.forceSizeModifier = Boolean.parseBoolean(node.getAttributes().getNamedItem("forcesizemodifier").getTextContent().trim());

        for (int i=0; i < node.getChildNodes().getLength(); i++) {
            Node n = node.getChildNodes().item(i);
            if (n.getNodeName().equals("situation")) { setSituation(FileCommon.DecodeFluff(n.getTextContent())); }
            if (n.getNodeName().equals("setup")) { setSetup(FileCommon.DecodeFluff(n.getTextContent())); }
            if (n.getNodeName().equals("specialrules")) { setSpecialRules(FileCommon.DecodeFluff(n.getTextContent())); }
            if (n.getNodeName().equals("victoryconditions")) { setVictoryConditions(FileCommon.DecodeFluff(n.getTextContent())); }
            if (n.getNodeName().equals("aftermath")) { setAftermath(FileCommon.DecodeFluff(n.getTextContent())); }

            if (n.getNodeName().equals("attacker")) {
                setAttacker(FileCommon.DecodeFluff(n.getAttributes().getNamedItem("description").getTextContent().trim()));
                try {
                    forces.add(new Force(n.getChildNodes().item(1), VersionNumber));
                } catch (Exception ex) {
                    errorMessage += "Error loading Attacker (" + ex.getMessage() + ")\n";
                }
            }
            if (n.getNodeName().equals("defender")) {
                setDefender(FileCommon.DecodeFluff(n.getAttributes().getNamedItem("description").getTextContent().trim()));
                try {
                    forces.add(new Force(n.getChildNodes().item(1), VersionNumber));
                } catch (Exception ex) {
                    errorMessage += "Error loading Defender (" + ex.getMessage() + ")\n";
                }
            }
            if (n.getNodeName().equals("warchest")) {
                setWarchest(new Warchest(n));
            }
        }

        if ( forces.size() < 2 ) {
            for (int i=forces.size(); i <=2; i++ ) {
                forces.add(new Force());
            }
        }
        if ( !errorMessage.isEmpty() ) {
            Media.Messager("Errors occured during load:\n" + errorMessage);
        }
    }

    public void AddForce( Force f ) {
        try {
            forces.add(f);
        } catch ( Exception e ) {
            System.out.println(e.getMessage());
        }
    }

    public void AddListener( TableModelListener listener ) {
        for ( Force force : getForces() ) {
            force.getCurrentModel().addTableModelListener(listener);
        }
    }

    public void setupTables(JTable top, JTable bottom) {
        getAttackerForce().setupTable(top);
        getDefenderForce().setupTable(bottom);
    }

    public void updateOpFor( boolean UseMod ) {
        for ( Force f : forces ) {
            f.useUnevenForceMod = UseMod;
        }
        
        if ( UseMod ) {
            forceSizeModifier = true;
            setOpFor();
        } else {
            forceSizeModifier = false;
            clearOpFor();
        }
        Refresh();
    }

    public void setOpFor() {
        getAttackerForce().OpForSize = getDefenderForce().getUnits().size();
        getDefenderForce().OpForSize = getAttackerForce().getUnits().size();
    }

    public void clearOpFor() {
        getAttackerForce().OpForSize = 0;
        getDefenderForce().OpForSize = 0;
    }

    public void setModel( abTable model ) {
        for ( Force force : getForces() ) {
            force.setCurrentModel(model.Create(force));
        }
    }

    public void setFactors() {
        for ( Force force : getForces() ) {
            for ( Unit u : force.getUnits() ) {
                u.LoadUnit();
            }
            force.RefreshBV();
        }
    }

    public void Refresh() {
        for ( Force f : forces ) {
            f.RefreshBV();
        }
    }

    public ArrayList<BattleForce> toBattleForceBySize( int SizeLimit ) {
        ArrayList<BattleForce> BattleForces = new ArrayList<BattleForce>();
        BattleForce bf = new BattleForce();
        for ( Force f : forces ) {
            bf.ForceName = f.ForceName;
            f.sortForPrinting();

            for ( Group g : f.Groups ) {
                for ( Unit u : g.getUnits() ) {
                    BattleForceStats stat = u.getBFStats();
                    stat.setUnit( g.getName() );
                    stat.setForceName( f.ForceName );
                    stat.setLogo(g.getLogo());
                    bf.BattleForceStats.add(stat);
                    
                    if ( bf.BattleForceStats.size() == SizeLimit ) {
                        BattleForces.add(bf);
                        bf = new BattleForce();
                        bf.ForceName = f.ForceName;
                    }
                }
            }
        }
        if ( bf.BattleForceStats.size() > 0 ) BattleForces.add(bf);

        return BattleForces;
    }

    public ArrayList<BattleForce> toBattleForceByGroup( int SizeLimit ) {
        ArrayList<BattleForce> Forces = new ArrayList<BattleForce>();
        for ( Force f : forces ) {
            f.sortForPrinting();
            for ( Group g : f.Groups ) {
                ArrayList<BattleForce> groupForces = g.toBattleForce(SizeLimit);
                for ( BattleForce bf : groupForces ) {
                    Forces.add(bf);
                }
            }
        }
        return Forces;
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write( "<scenario name=\"" + this.Name + "\" version=\"" + VersionNumber + "\" forcesizemodifier=\"" + this.forceSizeModifier + "\" overwrite=\"" + allowOverwrite + "\">" );
        file.newLine();

        file.write( CommonTools.tab + "<situation>" + FileCommon.EncodeFluff(this.Situation) + "</situation>" );
        file.newLine();

        file.write( CommonTools.tab + "<setup>" + FileCommon.EncodeFluff(this.Setup) + "</setup>" );
        file.newLine();

        file.write( CommonTools.tab + "<attacker description=\"" + FileCommon.EncodeFluff(this.Attacker) + "\">" );
        file.newLine();

        getAttackerForce().SerializeXML(file);

        file.write( CommonTools.tab + "</attacker>" );
        file.newLine();

        file.write( CommonTools.tab + "<defender description=\"" + FileCommon.EncodeFluff(this.Defender) + "\">" );
        file.newLine();

        getDefenderForce().SerializeXML(file);

        file.write( CommonTools.tab + "</defender>" );
        file.newLine();

        getWarchest().SerializeXML(file);

        file.write( CommonTools.tab + "<specialrules>" + FileCommon.EncodeFluff(this.SpecialRules) + "</specialrules>" );
        file.newLine();

        file.write( CommonTools.tab + "<victoryconditions>" + FileCommon.EncodeFluff(this.VictoryConditions) + "</victoryconditions>" );
        file.newLine();

        file.write( CommonTools.tab + "<aftermath>" + FileCommon.EncodeFluff(this.Aftermath) + "</aftermath>" );
        file.newLine();

        file.write("</scenario>");
    }

    public String SerializeClipboard() {
        String data = "";

        data += this.Name + CommonTools.NL + CommonTools.NL;
        data += "Situation" + CommonTools.NL + this.Situation + CommonTools.NL + CommonTools.NL;
        data += "Setup" + CommonTools.NL + this.Setup + CommonTools.NL + CommonTools.NL;
        data += "Attacker" + CommonTools.NL + this.Attacker + CommonTools.NL + CommonTools.NL;
        data += getAttackerForce().SerializeClipboard() + CommonTools.NL;
        data += "Defender" + CommonTools.NL + this.Defender + CommonTools.NL + CommonTools.NL;
        data += getDefenderForce().SerializeClipboard() + CommonTools.NL;
        data += getWarchest().SerializeClipboard() + CommonTools.NL;
        data += "Special Rules" + CommonTools.NL + this.SpecialRules + CommonTools.NL + CommonTools.NL;
        data += "Victory Conditions" + CommonTools.NL + this.VictoryConditions + CommonTools.NL + CommonTools.NL;
        data += "Aftermath" + CommonTools.NL + this.Aftermath + CommonTools.NL + CommonTools.NL;

        return data;
    }

    public String SerializeData() {
        return SerializeClipboard();
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getSituation() {
        return Situation;
    }

    public void setSituation(String Situation) {
        this.Situation = Situation;
    }

    public String getSetup() {
        return Setup;
    }

    public void setSetup(String Setup) {
        this.Setup = Setup;
    }

    public String getAttacker() {
        return Attacker;
    }

    public void setAttacker(String Attacker) {
        this.Attacker = Attacker;
    }

    public Force getAttackerForce() {
        return getForces().get(0);
    }

    public String getDefender() {
        return Defender;
    }

    public void setDefender(String Defender) {
        this.Defender = Defender;
    }

    public Force getDefenderForce() {
        return getForces().get(1);
    }

    public String getVictoryConditions() {
        return VictoryConditions;
    }

    public void setVictoryConditions(String VictoryConditions) {
        this.VictoryConditions = VictoryConditions;
    }

    public String getSpecialRules() {
        return SpecialRules;
    }

    public void setSpecialRules(String SpecialRules) {
        this.SpecialRules = SpecialRules;
    }

    public String getAftermath() {
        return Aftermath;
    }

    public void setAftermath(String Aftermath) {
        this.Aftermath = Aftermath;
    }

    public ArrayList<Force> getForces() {
        return forces;
    }

    public void setForces(ArrayList<Force> forces) {
        this.forces = forces;
    }

    public ArrayList<Group> getGroups() {
        ArrayList<Group> groups = new ArrayList<Group>();
        for ( Force f : forces ) {
            for ( Group g : f.Groups ) {
                groups.add(g);
            }
        }
        return groups;
    }

    public ArrayList<Unit> getUnits() {
        ArrayList<Unit> units = new ArrayList<Unit>();
        for ( Force f : forces ) {
            for ( Unit u : f.getUnits() ) {
                units.add(u);
            }
        }
        return units;
    }

    public Warchest getWarchest() {
        return warchest;
    }

    public void setWarchest(Warchest warchest) {
        this.warchest = warchest;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public boolean isOverwriteable() {
        return allowOverwrite;
    }

    public void setOverwriteable(boolean allowOverwrite) {
        this.allowOverwrite = allowOverwrite;
    }

    public boolean IsDirty() {
        return isDirty;
    }

    public void MakeDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public boolean UseForceSizeModifier() {
        return forceSizeModifier;
    }

    public int getForcePrintCount() {
        int count = 0;
        for ( Force f : forces ) {
            count += (f.Groups.size() * 2);
            count += f.getUnits().size();
        }
        return count;
    }
}
