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

import Print.ForceListPrinter;
import Print.PrintConsts;
import battleforce.BattleForceStats;
import common.CommonTools;
import common.Constants;
import components.*;
import filehandlers.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import list.UnitListData;
import list.view.Column;
import org.w3c.dom.Node;

public class Unit implements ifSerializable {
    public String TypeModel = "",
                  Name = "",
                  Model = "",
                  Info = "",
                  C3Type = "",
                    UnitImage = "";
    private String Mechwarrior = "";
    public String Filename = "",
                  Configuration = "";
    private String Group = "",
                    prevGroup = "";
    private String MechwarriorQuirks = "";
    public String UnitQuirks = "";
    public float BaseBV = 0.0f,
                 MiscMod = 1.0f,
                 Tonnage = 20.0f,
                 SkillsBV = 0.0f,
                 ModifierBV = 0.0f,
                 C3BV = 0.0f;
    private float ForceC3BV = 0.0f;
    public float TotalBV = 0.0f;
    private int Piloting = 5;
    private int Gunnery = 4;
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
    public int UnitType = CommonTools.BattleMech;
    public Warrior warrior = new Warrior();
    public boolean UsingC3 = false,
                    C3Available = false,
                    StatsCalced = false;
    private boolean isOmni = false;
    public Mech m = null;
    public CombatVehicle v = null;
    private BattleForceStats BFStats = new BattleForceStats();
    private Preferences Prefs;

    public Unit(){
        Prefs = Preferences.userRoot().node( Constants.SSWPrefs );
    }

    public Unit( UnitListData m ) {
        this();
        this.Name = m.getName();
        this.Model = m.getModel();
        this.TypeModel = m.getFullName();
        this.Tonnage = m.getTonnage();
        this.BaseBV = m.getBV();
        this.Filename = m.getFilename();
        this.Configuration = m.getConfig();
        this.Info = m.getInfo();
        this.BFStats = m.getBattleForceStats();
        this.UnitType = m.getUnitType();
        if ( Info.contains("C3") ) {
            C3Available = true;
            for ( String Item : Info.split(" ") ) {
                if ( Item.contains("C3") ) C3Type = Item.replace(",", "").trim();
            }
        }
        Refresh();
    }

    public Unit( Mech m ) {
        this();
        UnitType = CommonTools.BattleMech;
        Name = m.GetName();
        Model = m.GetModel();
        TypeModel = m.GetFullName();
        Tonnage = m.GetTonnage();
        BaseBV = m.GetCurrentBV();
        Info = m.GetChatInfo();

        if ( m.HasC3() ) {
            C3Available = true;
            for ( String Item : Info.split(",") ) {
                if ( Item.contains("C3") ) C3Type = Item.replace(",", "").trim();
            }
        }

        if ( m.IsOmnimech() ) {
            setOmni(true);
            Configuration = m.GetLoadout().GetName();
        }
        
        if ( !m.GetSSWImage().isEmpty()) {
            UnitImage = m.GetSSWImage();
        }

        //Calc Fields
        if ( m.HasECM() ) ECM = 1;
        if ( m.HasProbe() ) Probe = 1;
        if ( m.GetJumpJets().GetNumJJ() > 0 ) Jump = 1;
        Armor = m.GetArmor().GetArmorValue();
        TSM = m.GetPhysEnhance().IsTSM() ? 1 : 0;
        Speed = m.GetAdjustedRunningMP(false, true);
        if ( m.UsingTC() ) TC = 1;

        ArrayList v = (ArrayList) m.GetLoadout().GetNonCore().clone();
        for (int i=0; i < v.size(); i++)
        {
            if ( v.get(i) instanceof PhysicalWeapon ) {
                Physical += 1;
            }

            if ( v.get(i) instanceof RangedWeapon ) {
                RangedWeapon w = (RangedWeapon)v.get(i);
                Damage += w.GetDamageShort();
                if ( w.GetDamageShort() >= 12 )
                    HeadCap += 1;
                if ( w.GetDamageShort() >= 10 )
                    Ten += 1;
                if ( w.GetDamageShort() >= 8 )
                    Eight += 1;
            }
        }
        StatsCalced = true;

        this.m = m;
        Refresh();
    }

    public Unit( CombatVehicle vee ) {
        this();
        UnitType = CommonTools.Vehicle;
        Name = vee.GetName();
        Model = vee.GetModel();
        TypeModel = vee.GetFullName();
        Tonnage = vee.GetTonnage();
        BaseBV = vee.GetCurrentBV();
        Info = vee.GetChatInfo();

        if ( vee.HasC3() ) {
            C3Available = true;
            for ( String Item : Info.split(",") ) {
                if ( Item.contains("C3") ) C3Type = Item.replace(",", "").trim();
            }
        }

        if ( vee.IsOmni() ) {
            setOmni(true);
            Configuration = vee.GetLoadout().GetName();
        }
        
        if ( !vee.GetSSWImage().isEmpty() ) {
            UnitImage = vee.GetSSWImage();
        }

        //Calc Fields
        if ( vee.HasECM() ) ECM = 1;
        //if ( vee.HasProbe() ) Probe = 1;
        if ( vee.GetJumpJets().GetNumJJ() > 0 ) Jump = 1;
        Armor = vee.GetArmor().GetArmorValue();
        TSM = vee.GetPhysEnhance().IsTSM() ? 1 : 0;
        Speed = vee.getFlankMP();
        if ( vee.UsingTC() ) TC = 1;

        ArrayList v = (ArrayList) vee.GetLoadout().GetNonCore().clone();
        for (int i=0; i < v.size(); i++)
        {
            if ( v.get(i) instanceof PhysicalWeapon ) {
                Physical += 1;
            }

            if ( v.get(i) instanceof RangedWeapon ) {
                RangedWeapon w = (RangedWeapon)v.get(i);
                Damage += w.GetDamageShort();
                if ( w.GetDamageShort() >= 12 )
                    HeadCap += 1;
                if ( w.GetDamageShort() >= 10 )
                    Ten += 1;
                if ( w.GetDamageShort() >= 8 )
                    Eight += 1;
            }
        }
        StatsCalced = true;

        this.v = vee;
        Refresh();
    }
    
    public Unit( BattleForceStats stat ) {
        this();
        Name = stat.getName();
        Model = stat.getModel();
        TypeModel = stat.getElement();
        BaseBV = stat.getBasePV() * 100;
        UnitImage = stat.getImage();
        this.BFStats = stat;

        if ( stat.getType().equals("Combat Vehicle") || stat.getType().equals("Naval Vessel") ) this.UnitType = CommonTools.Vehicle;
        if ( stat.getType().equals("Protomech") ) this.UnitType = CommonTools.ProtoMech;
        if ( stat.getType().equals("IndustrialMech") ) this.UnitType = CommonTools.IndustrialMech;
        if ( stat.getType().equals("Infantry") ) {
            if ( stat.getSubType().equals("Battle Armor") )
                this.UnitType = CommonTools.Infantry;
            else
                this.UnitType = CommonTools.BattleArmor;
        }
        if ( stat.getType().equals("Aerospace") ) {
            if ( stat.getSubType().equals("Conventional Fighter"))
                this.UnitType = CommonTools.ConvFighter;
            else
                this.UnitType = CommonTools.AeroFighter;
        }

    }

    public Unit( Node n ) throws Exception {
        this();
        for (int i=0; i < n.getChildNodes().getLength(); i++) {
            String nodeName = n.getChildNodes().item(i).getNodeName();

            if ( !nodeName.equals("#text") ) {
                //Previous File structure
                if (nodeName.equals("type")) {Name = FileCommon.DecodeFluff(n.getChildNodes().item(i).getTextContent().trim());}
                if (nodeName.equals("model")) {Model = FileCommon.DecodeFluff(n.getChildNodes().item(i).getTextContent().trim());}
                if (nodeName.equals("config")) {Configuration = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("tonnage")) {Tonnage = Float.parseFloat(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("basebv")) {BaseBV = Float.parseFloat(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("modifier")) {MiscMod = Float.parseFloat(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("piloting")) {Piloting = Integer.parseInt(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("gunnery")) {Gunnery = Integer.parseInt(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("unittype")) {UnitType = Integer.parseInt(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("usingc3")) {UsingC3 = Boolean.parseBoolean(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("mechwarrior")) {Mechwarrior = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("ssw")) {Filename = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("group")) {Group = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("mechwarriorquirks")) {MechwarriorQuirks = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("unitquirks")) {UnitQuirks = n.getChildNodes().item(i).getTextContent().trim();}
            }
        }
        this.Refresh();
        TypeModel = (Name + " " + Model + " " + Configuration).replace("  ", " ").trim();
        this.warrior.setGunnery(Gunnery);
        this.warrior.setPiloting(Piloting);
        this.warrior.setName(Mechwarrior);
        this.warrior.setQuirks(MechwarriorQuirks);
    }

    public Unit(Node n, int Version) throws Exception {
        this();
        try {
            this.Name = FileCommon.DecodeFluff(n.getAttributes().getNamedItem("type").getTextContent().trim());
            this.Model = FileCommon.DecodeFluff(n.getAttributes().getNamedItem("model").getTextContent().trim());
            this.Configuration = n.getAttributes().getNamedItem("config").getTextContent().trim();
            TypeModel = (Name + " " + Model + " " + Configuration).replace("  ", " ").trim();
            if ( !Configuration.isEmpty() ) isOmni = true;
            this.Tonnage = Float.parseFloat(n.getAttributes().getNamedItem("tonnage").getTextContent().trim());
            this.BaseBV = Float.parseFloat(n.getAttributes().getNamedItem("bv").getTextContent().trim());
            this.UnitType = Integer.parseInt(n.getAttributes().getNamedItem("design").getTextContent().trim());
            this.Filename = n.getAttributes().getNamedItem("file").getTextContent().trim();
            this.UsingC3 = Boolean.parseBoolean(n.getAttributes().getNamedItem("c3status").getTextContent().trim());

            for (int i = 0; i < n.getChildNodes().getLength(); i++) {
                Node node = n.getChildNodes().item(i);
                if (node.getNodeName().equals("quirks")) {
                    this.UnitQuirks = node.getTextContent().trim();
                }
                if (node.getNodeName().equals("warrior")) {
                    try {
                        this.warrior = new Warrior(node);
                        this.Gunnery = warrior.getGunnery();
                        this.Piloting = warrior.getPiloting();
                        this.BFStats.setGunnery(Gunnery);
                        this.BFStats.setPiloting(Piloting);
                        this.MechwarriorQuirks = warrior.getQuirks();
                        this.Mechwarrior = (warrior.getRank() + " " + warrior.getName()).trim();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        throw ex;
                    }
                }
                if (node.getNodeName().equals("battleforce")) {
                    this.BFStats = new BattleForceStats(node);
                    this.BFStats.setElement(this.TypeModel);
                    this.BFStats.setPiloting(Piloting);
                    this.BFStats.setGunnery(Gunnery);
                    this.BFStats.setWarrior(warrior.getName());
                    this.BFStats.setName(this.Name);
                    this.BFStats.setModel(this.Model);
                }
                if (node.getNodeName().equals("info")) {
                    this.Info = node.getTextContent().trim();
                    if (Info.contains("C3")) {
                        C3Available = true;
                        for ( String s : Info.split(" ") ) {
                            if ( s.startsWith("C3") ) C3Type = s.replace(",", "").trim();
                        }
                    }
                }

                if ( node.getNodeName().equals("mech")) {
                    try {
                        MechReader mread = new MechReader();
                        m = mread.ReadMech(node);
                        if ( !Configuration.isEmpty() ) m.SetCurLoadout(Configuration);
                        BFStats = new BattleForceStats(m);
                    } catch (Exception e) {
                        Media.Messager("Error loading Mech " + e.getMessage());
                    }
                }
                this.Refresh();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public final void Refresh() {
        SkillsBV = 0;
        ModifierBV = 0;
        TotalBV = 0;
        //C3BV = 0;
        //if (UsingC3) { C3BV += BaseBV * .05;}
        SkillsBV = CommonTools.GetSkillBV((BaseBV + ForceC3BV), getGunnery(), getPiloting());
        ModifierBV = CommonTools.GetModifierBV(SkillsBV, MiscMod);
        TotalBV = CommonTools.GetFullAdjustedBV((BaseBV + ForceC3BV), getGunnery(), getPiloting(), MiscMod);
    }

    public void UpdateByUnit() {
        switch(UnitType) {
            case CommonTools.BattleMech:
                UpdateByMech();
                break;
            case CommonTools.Vehicle:
                UpdateByVehicle();
                break;
        }
    }
    
    public void UpdateByVehicle() {
        LoadUnit();
        if ( v != null ) {
            Name = v.GetName();
            Model = v.GetModel();
            TypeModel = v.GetFullName();
            Tonnage = v.GetTonnage();
            BaseBV = v.GetCurrentBV();
            Info = v.GetChatInfo();

            if ( v.HasC3() ) {
                C3Available = true;
                for ( String Item : Info.split(",") ) {
                    if ( Item.contains("C3") ) C3Type = Item.replace(",", "").trim();
                }
            }

            if ( v.IsOmni() ) {
                setOmni(true);
                Configuration = v.GetLoadout().GetName();
            }

            if ( !v.GetSSWImage().isEmpty() ) {
                UnitImage = v.GetSSWImage();
            }

            //Calc Fields
            if ( v.HasECM() ) ECM = 1;
            //if ( vee.HasProbe() ) Probe = 1;
            if ( v.GetJumpJets().GetNumJJ() > 0 ) Jump = 1;
            Armor = v.GetArmor().GetArmorValue();
            TSM = v.GetPhysEnhance().IsTSM() ? 1 : 0;
            Speed = v.getFlankMP();
            if ( v.UsingTC() ) TC = 1;

            ArrayList w = (ArrayList) v.GetLoadout().GetNonCore().clone();
            for (int i=0; i < w.size(); i++)
            {
                if ( w.get(i) instanceof PhysicalWeapon ) {
                    Physical += 1;
                }

                if ( w.get(i) instanceof RangedWeapon ) {
                    RangedWeapon wea = (RangedWeapon)w.get(i);
                    Damage += wea.GetDamageShort();
                    if ( wea.GetDamageShort() >= 12 )
                        HeadCap += 1;
                    if ( wea.GetDamageShort() >= 10 )
                        Ten += 1;
                    if ( wea.GetDamageShort() >= 8 )
                        Eight += 1;
                }
            }
            StatsCalced = true;
        }
    }
    
    public void UpdateByMech() {
        LoadUnit();
        if ( m != null ) {
            Name = m.GetName();
            Model = m.GetModel();
            TypeModel = m.GetFullName();
            Configuration = m.GetLoadout().GetName();
            BaseBV = m.GetCurrentBV();
            Info = m.GetChatInfo();
            if (Info.contains("C3")) {
                C3Available = true;
                for ( String s : Info.split(" ") ) {
                    if ( s.startsWith("C3") ) C3Type = s.replace(",", "").trim();
                }
            }
            BFStats = new BattleForceStats(m);

            //Calc Fields
            if ( m.HasECM() ) ECM = 1;
            if ( m.HasProbe() ) Probe = 1;
            if ( m.GetJumpJets().GetNumJJ() > 0 ) Jump = 1;
            Armor = m.GetArmor().GetArmorValue();
            TSM = m.GetPhysEnhance().IsTSM() ? 1 : 0;
            Speed = m.GetAdjustedRunningMP(false, true);
            if ( m.UsingTC() ) TC = 1;

            ArrayList v = (ArrayList) m.GetLoadout().GetNonCore().clone();
            for (int i=0; i < v.size(); i++)
            {
                if ( v.get(i) instanceof PhysicalWeapon ) {
                    Physical += 1;
                }

                if ( v.get(i) instanceof RangedWeapon ) {
                    RangedWeapon w = (RangedWeapon)v.get(i);
                    Damage += w.GetDamageShort();
                    if ( w.GetDamageShort() >= 12 )
                        HeadCap += 1;
                    if ( w.GetDamageShort() >= 10 )
                        Ten += 1;
                    if ( w.GetDamageShort() >= 8 )
                        Eight += 1;
                }
            }
            StatsCalced = true;
        }
        Refresh();
    }

    public String GetSkills(){
        return getGunnery() + "/" + getPiloting();
    }

    public void RenderPrint(ForceListPrinter p) {
        p.setFont(PrintConsts.PlainFont);
        p.WriteStr(TypeModel, 120);
        p.WriteStr(getMechwarrior(), 140);
        p.WriteStr(CommonTools.UnitTypes[UnitType], 60);
        p.WriteStr(String.format("%1$,.2f", Tonnage), 50);
        p.WriteStr(String.format("%1$,.0f", BaseBV), 40);
        p.WriteStr(GetSkills(), 30);
        p.WriteStr(String.format("%1$,.2f", MiscMod), 40);
        p.WriteStr(Boolean.valueOf(UsingC3).toString(), 30);
        p.WriteStr(String.format("%1$,.0f", TotalBV), 0);
        p.NewLine();
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        LoadUnit();
        file.write(CommonTools.Tabs(4) + "<unit type=\"" + FileCommon.EncodeFluff(this.Name) + "\" model=\"" + FileCommon.EncodeFluff(this.Model) + "\" config=\"" + this.Configuration + "\" tonnage=\"" + this.Tonnage + "\" bv=\"" + this.BaseBV + "\" design=\"" + this.UnitType + "\" file=\"" + this.Filename + "\" c3status=\"" + this.UsingC3 + "\">");
        file.newLine();
        BFStats.SerializeXML(file, 5);
        file.newLine();
        file.write(CommonTools.Tabs(5) + "<info>" + this.Info + "</info>");
        file.newLine();
        file.write(CommonTools.Tabs(5) + "<quirks>" + this.UnitQuirks + "</quirks>");
        file.newLine();
        warrior.SerializeXML(file);
        if ( m != null) {
            MechWriter mwrite = new MechWriter(m);
            mwrite.WriteXML(file);
            m.SetCurLoadout(this.Configuration);
        }
        file.write(CommonTools.Tabs(4) + "</unit>");
        file.newLine();
    }

    public void SerializeMUL(BufferedWriter file) throws IOException {
        if ( this.Name.contains("(") && this.Name.contains(")") ) {
            this.Name = this.Name.substring(0, this.Name.indexOf(" (")).trim();
        }

        this.Model.replace("Alternate Configuration", "");
        this.Model.replace("Alternate", "");
        this.Model.replace("Alt", "");
        this.Model.trim();

        file.write(CommonTools.tab + "<entity chassis=\"" + this.Name + "\" model=\"" + this.Model + "\">");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + "<pilot name=\"" + this.getMechwarrior() + "\" gunnery=\"" + this.getGunnery() + "\" piloting=\"" + this.getPiloting() + "\" />");
        file.newLine();
        file.write(CommonTools.tab + "</entity>");
        file.newLine();
    }

    public String SerializeClipboard() {
        String data = "";

        for ( Column c : Force.ScenarioClipboardColumns() ) {
            data += CommonTools.spaceRight(convertColumn(c), c.preferredWidth) + CommonTools.Tab;
        }
        return data;
    }

    public String SerializeFactors() {
        String data = "";

        data += FileCommon.CSVFormat(getFullName());
        data += FileCommon.CSVFormat(CommonTools.UnitTypes[UnitType]);
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
        data += FileCommon.CSVFormat((int)BaseBV);
        data += FileCommon.CSVFormat((int)TotalBV);

        return data.substring(0, data.length()-2);
    }

    private String convertColumn( Column c ) {
        if ( c.Title.equals("Unit") ) {
            return this.TypeModel.trim();
        } else if ( c.Title.equals("Tons") ) {
            return String.format("%1$,.0f", Tonnage);
        } else if ( c.Title.equals("BV") ) {
            return String.format("%1$,.0f", BaseBV);
        } else if ( c.Title.equals("Mechwarrior") ) {
            return this.getMechwarrior();
        } else if ( c.Title.equals("Lance/Star") ) {
            return this.Group;
        } else if ( c.Title.equals("G/P") ) {
            return this.GetSkills();
        } else if ( c.Title.equals("Adj BV") ) {
            return String.format("%1$,.0f", TotalBV);
        } else {
            return "";
        }
    }

    public String SerializeData() {
        return "";
    }

    @Override
    public String toString() {
        return TypeModel + " (" + warrior.getName() + " " + warrior.getGunnery() + "/" + warrior.getPiloting() + ")";
    }

    public void LoadUnit() {
        switch(UnitType) {
            case CommonTools.BattleMech:
                if ( m == null ) {
                    try {
                        LoadMech();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                break;
            case CommonTools.Vehicle:
                if ( v == null ) {
                    try {
                        LoadVehicle();
                    } catch ( Exception ex ) {
                        System.out.println(ex.getMessage());
                    }
                }
        }
    }

    private void LoadMech() throws Exception {
        MechReader reader = new MechReader();
        this.m = reader.ReadMech( Prefs.get("ListPath", Prefs.get( "LastOpenDirectory", "" ) ) + this.Filename );
        if ( ! this.Configuration.isEmpty() ) {
            this.m.SetCurLoadout(this.Configuration.trim());
        }
        if ( BFStats.getPointValue() == 0 ) {
            BFStats = new BattleForceStats(m);
        }
        if ( m != null && !StatsCalced ) {
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

            //Calc Fields
            if ( m.HasECM() ) ECM = 1;
            if ( m.HasProbe() ) Probe = 1;
            if ( m.GetJumpJets().GetNumJJ() > 0 ) Jump = 1;
            Armor = m.GetArmor().GetArmorValue();
            TSM = m.GetPhysEnhance().IsTSM() ? 1 : 0;
            Speed = m.GetAdjustedRunningMP(false, true);
            if ( m.UsingTC() ) TC = 1;

            ArrayList v = (ArrayList) m.GetLoadout().GetNonCore().clone();
            for (int i=0; i < v.size(); i++)
            {
                if ( v.get(i) instanceof PhysicalWeapon ) {
                    Physical += 1;
                }

                if ( v.get(i) instanceof RangedWeapon ) {
                    RangedWeapon w = (RangedWeapon)v.get(i);
                    Damage += w.GetDamageShort();
                    if ( w.GetDamageShort() >= 12 )
                        HeadCap += 1;
                    if ( w.GetDamageShort() >= 10 )
                        Ten += 1;
                    if ( w.GetDamageShort() >= 8 )
                        Eight += 1;
                }
            }
            StatsCalced = true;
        }
    }
    
    private void LoadVehicle() throws Exception {
        CVReader reader = new CVReader();
        this.v = reader.ReadUnit( Prefs.get("ListPath", Prefs.get( "LastOpenDirectory", "" ) ) + this.Filename );
        if ( ! this.Configuration.isEmpty() ) {
            this.v.SetCurLoadout(this.Configuration.trim());
        }
        if ( !v.GetSSWImage().isEmpty() )
            this.UnitImage = v.GetSSWImage();
        
        if ( BFStats.getPointValue() == 0 ) {
            BFStats = new BattleForceStats(v);
        }
        if ( v != null && !StatsCalced ) {
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

            //Calc Fields
            if ( v.HasECM() ) ECM = 1;
            if ( v.HasProbe() ) Probe = 1;
            if ( v.GetJumpJets().GetNumJJ() > 0 ) Jump = 1;
            Armor = v.GetArmor().GetArmorValue();
            TSM = v.GetPhysEnhance().IsTSM() ? 1 : 0;
            Speed = v.getFlankMP();
            if ( v.UsingTC() ) TC = 1;

            ArrayList d = (ArrayList) m.GetLoadout().GetNonCore().clone();
            for (int i=0; i < d.size(); i++)
            {
                if ( d.get(i) instanceof PhysicalWeapon ) {
                    Physical += 1;
                }

                if ( d.get(i) instanceof RangedWeapon ) {
                    RangedWeapon w = (RangedWeapon)d.get(i);
                    Damage += w.GetDamageShort();
                    if ( w.GetDamageShort() >= 12 )
                        HeadCap += 1;
                    if ( w.GetDamageShort() >= 10 )
                        Ten += 1;
                    if ( w.GetDamageShort() >= 8 )
                        Eight += 1;
                }
            }
            StatsCalced = true;
        }
    }
    
    public BattleForceStats getBFStats() {
        if ( BFStats != null ) {
            BFStats.setName(this.Name);
            BFStats.setModel(this.Model);
            if ( !this.Configuration.isEmpty() ) BFStats.setModel((Model + " " + Configuration).trim());
            BFStats.setWarrior(warrior.getName());
            BFStats.setGunnery(warrior.getGunnery());
            BFStats.setPiloting(warrior.getPiloting());
            BFStats.setImage(this.UnitImage);
            switch(this.UnitType){
                case CommonTools.BattleMech:
                    BFStats.setBFType("BM");
                    break;
                case CommonTools.Vehicle:
                    BFStats.setBFType("CV");
                    break;
                default:
                    BFStats.setBFType("BM");
            }
            return BFStats;
        }

        LoadUnit();
        if ( m != null ) {
            BFStats = new BattleForceStats(m, Group, getGunnery(), getPiloting());
            BFStats.setWarrior(warrior.getName());
        }
        return BFStats;
    }

    public String getMechwarrior() {
        return warrior.getName();
    }

    public void setMechwarrior(String Mechwarrior) {
        warrior.setName(Mechwarrior);
        BFStats.setWarrior(Mechwarrior);
    }

    public String getMechwarriorQuirks() {
        return warrior.getQuirks();
    }

    public void setMechwarriorQuirks(String MechwarriorQuirks) {
        warrior.setQuirks(MechwarriorQuirks);
    }

    public int getPiloting() {
        return warrior.getPiloting();
    }

    public void setPiloting(int Piloting) {
        warrior.setPiloting(Piloting);
        BFStats.setPiloting(Piloting);
    }

    public int getGunnery() {
        return warrior.getGunnery();
    }

    public void setGunnery(int Gunnery) {
        warrior.setGunnery(Gunnery);
        BFStats.setGunnery(Gunnery);
    }

    public void setGP(int Gunnery, int Piloting) {
        this.Gunnery = Gunnery;
        this.Piloting = Piloting;

        warrior.setGunnery(Gunnery);
        warrior.setPiloting(Piloting);

        BFStats.setGP(Gunnery, Piloting);
    }

    public boolean IsOmni() {
        return isOmni;
    }

    public final void setOmni(boolean isOmni) {
        this.isOmni = isOmni;
    }
    
    public String getInfo() {
        return Info;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String Group) {
        prevGroup = this.Group;
        this.Group = Group;
    }

    public String getPrevGroup() {
        return prevGroup;
    }

    public String isUsingC3() {
        String val = "N/A";
        if ( C3Available ) {
            if ( UsingC3 ) {
                val = "Yes";
            } else {
                val = "No";
            }
            val += " (" + C3Type + ")";
        }
        return val;
    }
    
    public boolean HasC3() {
        return C3Available;
    }

    public float getForceC3BV() {
        return ForceC3BV;
    }

    public void setForceC3BV(float ForceC3BV) {
        this.ForceC3BV = ForceC3BV;
        this.Refresh();
    }

    public String getFullName() {
        return (Name + " " + Model + " " + Configuration).replace("  ", " ").trim();
    }
    
    public String getImage() {
        return UnitImage;
    }
    
    public void SetCurLoadout( String loadout) {
        switch(UnitType) {
            case CommonTools.BattleMech:
                m.SetCurLoadout(loadout);
                break;
            case CommonTools.Vehicle:
                v.SetCurLoadout(loadout);
        }
    }
    
    public void SetUnitImage( String image ) {
        UnitImage = image;
        switch(UnitType) {
            case CommonTools.BattleMech:
                m.SetSSWImage(image);
                break;
            case CommonTools.Vehicle:
                v.SetSSWImage(image);
                break;
        }
    }
    
    public void SaveUnit() {
        try
        {
            switch(UnitType) {
                case CommonTools.BattleMech:
                    MechWriter writer = new MechWriter();
                    writer.setMech(m);
                    writer.WriteXML(Filename);
                    break;
                case CommonTools.Vehicle:
                    CVWriter cwriter = new CVWriter();
                    cwriter.setUnit(v);
                    cwriter.WriteXML(Filename);
                    break;
            }
        } catch ( IOException io ) {
            System.err.println(io.getMessage());
        }
    }
}