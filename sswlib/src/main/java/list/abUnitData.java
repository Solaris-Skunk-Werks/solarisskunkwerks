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

import java.util.ArrayList;
import battleforce.BattleForceStats;

/**
 *
 * @author gblouin
 */
public abstract class abUnitData implements ifUnitData {
    String Name = "",
           Model = "",
           Configuration = "",
           TypeModel = "",
           Level= "Tournament Legal",
           Era = "Age of War",
           Tech = "Clan",
           Config = "",
           Source = "",
           Type = "BattleMech",
           Motive = "Biped",
           Info = "";
    int     Tonnage = 0,
            Year = 2750,
            BV = 0,
            MinMP = 1;
    double Cost = 0.0d;
    boolean Omni = false;
    String basePath = "",
           filename = "";
    public ArrayList Configurations = new ArrayList();
    public BattleForceStats bfstat = new BattleForceStats();
    private String[] indexFields = new String[]{"Name", "Model", "Level", "Era",
    "Tech", "Source", "Tonnage", "Year", "BV", "Cost", "Filename", "Type",
    "Motive", "Info", "Config"};
    static final int name = 0,
                model = 1,
                configuration = 2,
                level = 3,
                era = 4,
                tech = 5,
                source = 6,
                tonnage = 7,
                year = 8,
                bv = 9,
                cost = 10,
                Filename = 11,
                type = 12,
                motive = 13,
                info = 14,
                config = 15,
                pv = 16,
                wt = 17,
                abilities = 18,
                mv = 19,
                s = 20,
                m = 21,
                l = 22,
                e = 23,
                ov = 24,
                armor = 25,
                internal = 26;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getFullName() {
        return (Name + " " + Model + " " + Configuration).replace("  ", " ").trim();
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String Level) {
        this.Level = Level;
    }

    public String getEra() {
        return Era;
    }

    public void setEra(String Era) {
        this.Era = Era;
    }

    public String getTech() {
        return Tech;
    }

    public void setTech(String Tech) {
        this.Tech = Tech;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getMotive() {
        return Motive;
    }

    public void setMotive(String Motive) {
        this.Motive = Motive;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String Info) {
        this.Info = Info;
    }

    public int getTonnage() {
        return Tonnage;
    }

    public void setTonnage(int Tonnage) {
        this.Tonnage = Tonnage;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int Year) {
        this.Year = Year;
    }

    public int getBV() {
        return BV;
    }

    public void setBV(int BV) {
        this.BV = BV;
    }

    public double getCost() {
        return Cost;
    }

    public void setCost(double Cost) {
        this.Cost = Cost;
    }

    public String getFinalCost() {
        return Long.toString(Math.round(Cost));
    }

    public boolean isOmni() {
        return Omni;
    }

    public void setOmni(boolean Omni) {
        this.Omni = Omni;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String Model) {
        this.Model = Model;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getConfig() {
        return Config;
    }

    public void setConfig(String Config) {
        this.Config = Config;
        this.Configuration = Config;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public String getTypeModel() {
        if ( TypeModel.length() == 0 )  TypeModel = getFullName();
        return TypeModel;
    }

    public void setTypeModel(String TypeModel) {
        this.TypeModel = TypeModel;
    }

    public int getMinMP() {
        return MinMP;
    }

    public void setMinMP(int MinMP) {
        this.MinMP = MinMP;
    }

    public void setBattleForceStats( BattleForceStats stat ) {
        this.bfstat = stat;
    }
    
    public BattleForceStats getBattleForceStats() {
        if ( bfstat.getName().isEmpty() ) bfstat.setName(getFullName());
        return bfstat;
    }

    public void setConfiguration(String configuration) {
        Configuration = configuration;
    }
    public String getConfiguration() {
        return Configuration;
    }
}
