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

import Force.Unit;
import battleforce.BattleForceStats;
import common.CommonTools;
import filehandlers.MechReader;

public class UnitListData extends abUnitData {
    public UnitListData(String Name, String Model, String Configuration, String Level, String Era, String Tech, String Source, String Type, String Motive, String Info, int Tonnage, int Year, int BV, double Cost, String filename, BattleForceStats bfStats){
        this.Name = Name;
        this.Model = Model;
        this.Configuration = Configuration;
        this.TypeModel = this.getFullName();
        this.Level = Level;
        this.Era = Era;
        this.Tech = Tech;
        this.Source = Source;
        this.Type = Type;
        this.Motive = Motive;
        this.Info = Info;
        this.Tonnage = Tonnage;
        this.Year = Year;
        this.BV = BV;
        this.Cost = Cost;
        this.filename = filename;
        this.bfstat = bfStats;
    }

    public UnitListData() {
        this("", "",  "", "", "", "", "", "BattleMech", "Biped", "", 0, 2750, 0, 0, "", new BattleForceStats());
    }

    public UnitListData( UnitListData m ) {
        this(m.Name, m.Model, m.Configuration, m.Level, m.Era, m.Tech, m.Source, m.Type, m.Motive, m.Info, m.Tonnage, m.Year, m.BV, m.Cost, m.filename, m.bfstat);
    }

    public UnitListData( String filename, String basePath ) throws Exception {
        MechReader read = new MechReader();
        UnitListData tempData = new UnitListData();
        try
        {
            tempData = read.ReadMechData(filename, basePath);
            this.Name = tempData.getName();
            this.Model = tempData.getModel();
            this.Configuration = tempData.getConfig();
            this.TypeModel = tempData.getFullName();
            this.Level = tempData.getLevel();
            this.Era = tempData.getEra();
            this.Source = tempData.getSource();
            this.Tech = tempData.getTech();
            this.Type = tempData.getType();
            this.Motive = tempData.getMotive();
            this.Info = tempData.getInfo();
            this.Tonnage = tempData.getTonnage();
            this.Year = tempData.getYear();
            this.BV = tempData.getBV();
            this.Cost = tempData.getCost();
            this.Omni = tempData.isOmni();
            this.filename = tempData.getFilename().replace(basePath, "");
            this.Config = tempData.getConfig();
            for ( int i=0; i < tempData.Configurations.size(); i++ ){
                this.Configurations.add(tempData.Configurations.get(i));
            }
            this.bfstat = tempData.getBattleForceStats();
        } catch ( Exception e1 ) {
            throw new Exception("[MechListData " + e1.getMessage() + "]");
        }
    }

    public UnitListData( String[] Items ) {
        this.Name = Items[name];
        this.Model = Items[model];
        this.Configuration = Items[configuration];
        this.TypeModel = this.getFullName();
        this.Level = Items[level];
        this.Era = Items[era];
        this.Tech = Items[tech];
        this.Source = Items[source];
        this.Tonnage = Integer.parseInt(Items[tonnage]);
        this.Year = Integer.parseInt(Items[year]);
        this.BV = Integer.parseInt(Items[bv]);
        this.Cost = Double.parseDouble(Items[cost]);
        this.filename = Items[Filename];
        this.Type = Items[type];
        this.Motive = Items[motive];
        this.Info = Items[info];
        this.Config = Items[config];
        if ( !Config.isEmpty() ) { this.Omni = true; }

        this.bfstat = new BattleForceStats( new String[]{this.getFullName(), Items[pv], Items[wt], Items[mv], Items[s], Items[m], Items[l], Items[e], Items[ov], Items[armor], Items[internal], Items[abilities]} );
        this.bfstat.setName(Name);
        this.bfstat.setModel(Model);

    }

    public Unit getUnit() {
        Unit u = new Unit();
        u.TypeModel = getFullName();
        u.Name = this.Name;
        u.Model = this.getModel();
        u.setOmni(this.isOmni());
        if ( this.isOmni() ) {
            u.setOmni(true);
            u.Configuration = this.Config;
        }
        u.BaseBV = this.BV;
        u.Tonnage = this.Tonnage;
        u.UnitType = CommonTools.BattleMech;
        u.Filename = this.filename;
        u.Info = this.Info;
        u.Refresh();

        return u;
    }

    public String SerializeIndex() {
        String data = "";

        data += this.Name + ",";
        data += this.Model + ",";
        data += this.Configuration + ",";
        data += this.Level + ",";
        data += this.Era + ",";
        data += this.Tech + ",";
        data += this.Source + ",";
        data += this.Tonnage + ",";
        data += this.Year + ",";
        data += this.BV + ",";
        data += this.Cost + ",";
        data += this.filename + ",";
        data += this.Type + ",";
        data += this.Motive + ",";
        data += this.Info.replace(",", " ") + ",";
        data += this.Config + ",";
        data += this.bfstat.getPointValue() + ",";
        data += this.bfstat.getWeight() + ",";
        data += this.bfstat.getAbilitiesString().replace(",", "~") + ",";
        data += this.bfstat.getMovement() + ",";
        data += this.bfstat.getShort() + ",";
        data += this.bfstat.getMedium() + ",";
        data += this.bfstat.getLong() + ",";
        data += this.bfstat.getExtreme() + ",";
        data += this.bfstat.getOverheat() + ",";
        data += this.bfstat.getArmor() + ",";
        data += this.bfstat.getInternal();

        return data;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + getBV() + ") " + getInfo();
    }
    
    public int getUnitType() {
        if ( this.filename.endsWith(".ssw") ) {
            return CommonTools.BattleMech;
        } else if ( this.filename.endsWith(".saw") ) {
            return CommonTools.Vehicle;
        }
        return 12;
    }
}
