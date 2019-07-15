/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB;

import Force.Warrior;
import common.CommonTools;
import Print.ForceListPrinter;

import BFB.Common.Constants;
import Print.PrintConsts;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class abUnit implements ifUnit {
    public String TypeModel = "",
                  Type = "",
                  Model = "",
                  Mechwarrior = "",
                  Filename = "",
                  Configuration = "",
                  Group = "",
                  MechwarriorQuirks = "",
                  UnitQuirks = "";
    public float BaseBV = 0.0f,
                 MiscMod = 1.0f,
                 Tonnage = 20.0f,
                 SkillsBV = 0.0f,
                 ModifierBV = 0.0f,
                 C3BV = 0.0f,
                 TotalBV = 0.0f;
    public int Piloting = 5,
               Gunnery = 4,
               UnitType = Constants.BattleMech;
    public Object design;
    public Warrior warrior = new Warrior();
    public boolean UsingC3 = false;
    public Warrior pilot = new Warrior();

    public void Refresh() {
        SkillsBV = 0;
        ModifierBV = 0;
        TotalBV = 0;
        SkillsBV += CommonTools.GetSkillBV(BaseBV, Gunnery, Piloting);
        ModifierBV += CommonTools.GetModifierBV(SkillsBV, MiscMod);
        TotalBV += CommonTools.GetFullAdjustedBV(BaseBV, Gunnery, Piloting, MiscMod);
        if (UsingC3) { C3BV += TotalBV * .05;}
    }

    public String GetSkills(){
        return Gunnery + "/" + Piloting;
    }

    public void RenderPrint(ForceListPrinter p) {
        p.setFont(PrintConsts.PlainFont);
        p.WriteStr(TypeModel, 120);
        p.WriteStr(Mechwarrior, 140);
        p.WriteStr(Constants.UnitTypes[UnitType], 60);
        p.WriteStr(String.format("%1$,.2f", Tonnage), 50);
        p.WriteStr(String.format("%1$,.0f", BaseBV), 40);
        p.WriteStr(GetSkills(), 30);
        //p.WriteStr(String.format("%1$,.0f", SkillsBV), 50);
        p.WriteStr(String.format("%1$,.2f", MiscMod), 40);
        //p.WriteStr(String.format("%1$,.0f", TotalBV), 50);
        p.WriteStr(Boolean.valueOf(UsingC3).toString(), 30);
        //p.WriteStr(String.format("%1$,.0f", C3BV), 30);
        p.WriteStr(String.format("%1$,.0f", TotalBV), 0);
        p.NewLine();
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<type>" + this.Type.trim() + "</type>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<model>" + this.Model.trim() + "</model>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<config>" + this.Configuration.trim() + "</config>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<tonnage>" + this.Tonnage + "</tonnage>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<group>" + this.Group.trim() + "</group>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<mechwarrior>" + this.Mechwarrior.trim() + "</mechwarrior>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<basebv>" + this.BaseBV + "</basebv>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<modifier>" + this.MiscMod + "</modifier>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<piloting>" + this.Piloting + "</piloting>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<gunnery>" + this.Gunnery + "</gunnery>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<unittype>" + this.UnitType + "</unittype>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<usingc3>" + this.UsingC3 + "</usingc3>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<mechwarriorquirks>" + this.MechwarriorQuirks + "</mechwarriorquirks>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<unitquirks>" + this.UnitQuirks + "</unitquirks>");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + CommonTools.tab + "<ssw>" + this.Filename.trim() + "</ssw>");
        file.newLine();
    }

    public void SerializeMUL(BufferedWriter file) throws IOException {
        if ( this.Type.contains("(") && this.Type.contains(")") ) {
            this.Type = this.Type.substring(0, this.Type.indexOf(" (")).trim();
        }

        this.Model.replace("Alternate Configuration", "");
        this.Model.replace("Alternate", "");
        this.Model.replace("Alt", "");
        this.Model.trim();

        file.write(CommonTools.tab + "<entity chassis=\"" + this.Type + "\" model=\"" + this.Model + "\">");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + "<pilot name=\"" + this.Mechwarrior + "\" gunnery=\"" + this.Gunnery + "\" piloting=\"" + this.Piloting + "\" />");
        file.newLine();
        file.write(CommonTools.tab + "</entity>");
        file.newLine();
    }

    public String SerializeClipboard() {
        String data = "";

        data += CommonTools.spaceRight(this.TypeModel.trim(), 30) + Constants.Tab;
        data += String.format("%1$,.0f", Tonnage) + Constants.Tab;
        data += String.format("%1$,.0f", BaseBV) + "" + Constants.Tab;
        data += CommonTools.spaceRight(this.Mechwarrior, 30) + Constants.Tab;
        data += CommonTools.spaceRight(this.Group, 20) + Constants.Tab;
        data += this.Gunnery + "/" + this.Piloting + Constants.Tab;
        data += String.format("%1$,.0f", TotalBV) + "";

        return data;
    }
}
