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

import java.util.ArrayList;
import java.util.List;

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

    public UnitListData(List<String> Items) {
        this.Name = Items.get(name);
        this.Model = Items.get(model);
        this.Configuration = Items.get(configuration);
        this.TypeModel = this.getFullName();
        this.Level = Items.get(level);
        this.Era = Items.get(era);
        this.Tech = Items.get(tech);
        this.Source = Items.get(source);
        this.Tonnage = Integer.parseInt(Items.get(tonnage));
        this.Year = Integer.parseInt(Items.get(year));
        this.BV = Integer.parseInt(Items.get(bv));
        this.Cost = Double.parseDouble(Items.get(cost));
        this.filename = Items.get(Filename);
        this.Type = Items.get(type);
        this.Motive = Items.get(motive);
        this.Info = Items.get(info);
        this.Config = Items.get(config);
        if ( !Config.isEmpty() ) { this.Omni = true; }

        this.bfstat = new BattleForceStats( new String[]{this.getFullName(), Items.get(pv), Items.get(wt), Items.get(mv),
                Items.get(s), Items.get(m), Items.get(l), Items.get(e), Items.get(ov), Items.get(armor), Items.get(internal),
                Items.get(abilities)} );
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

    public List<String> toCsvIndex() {
        List<String> data = new ArrayList<>();
        data.add(Name);
        data.add(Model);
        data.add(Configuration);
        data.add(Level);
        data.add(Era);
        data.add(Tech);
        data.add(Source);
        data.add(String.valueOf(Tonnage));
        data.add(String.valueOf(Year));
        data.add(String.valueOf(BV));
        data.add(String.valueOf(Cost));
        data.add(filename);
        data.add(Type);
        data.add(Motive);
        data.add(Info);
        data.add(Config);
        data.add(String.valueOf(bfstat.getPointValue()));
        data.add(String.valueOf(bfstat.getWeight()));
        data.add(bfstat.getAbilitiesString().replace(",", "~")); // check to make sure this is necessary
        data.add(bfstat.getMovement());
        data.add(String.valueOf(bfstat.getShort()));
        data.add(String.valueOf(bfstat.getMedium()));
        data.add(String.valueOf(bfstat.getLong()));
        data.add(String.valueOf(bfstat.getExtreme()));
        data.add(String.valueOf(bfstat.getOverheat()));
        data.add(String.valueOf(bfstat.getArmor()));
        data.add(String.valueOf(bfstat.getInternal()));

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
