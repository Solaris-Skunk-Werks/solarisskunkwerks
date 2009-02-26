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

package ssw.filehandlers;

import java.util.Vector;

public class MechListData {
    private String Name = "",
                   Model = "",
                   Level= "Tournament Legal",
                   Era = "Age of War",
                   Tech = "Clan",
                   Config = "";
    private int Tonnage = 0,
                Year = 2750,
                BV = 0;
    private float Cost = 0.0f;
    private boolean Omni = false;
    private String filename = "";
    public Vector Configurations = new Vector();

    public MechListData(String Name, String Model, String Level, String Era, String Tech, int Tonnage, int Year, int BV, float Cost, String filename){
        this.Name = Name;
        this.Model = Model;
        this.Level = Level;
        this.Era = Era;
        this.Tech = Tech;
        this.Tonnage = Tonnage;
        this.Year = Year;
        this.BV = BV;
        this.Cost = Cost;
        this.filename = filename;
    }

    public MechListData() {
        this("", "", "", "", "", 0, 2750, 0, 0, "");
    }

    public MechListData( MechListData m ) {
        this(m.Name, m.Model, m.Level, m.Era, m.Tech, m.Tonnage, m.Year, m.BV, m.Cost, m.filename);
    }

    public MechListData( String filename ) throws Exception {
        XMLReader read = new XMLReader();
        MechListData tempData = new MechListData();
        try
        {
            tempData = read.ReadMechData(filename);
            this.Name = tempData.getName();
            this.Model = tempData.getModel();
            this.Level = tempData.getLevel();
            this.Era = tempData.getEra();
            this.Tech = tempData.getTech();
            this.Tonnage = tempData.getTonnage();
            this.Year = tempData.getYear();
            this.BV = tempData.getBV();
            this.Cost = tempData.getCost();
            this.Omni = tempData.isOmni();
            this.filename = tempData.getFilename();
            this.Config = tempData.getConfig();
            for ( int i=0; i < tempData.Configurations.size(); i++ ){
                this.Configurations.add(tempData.Configurations.get(i));
            }
        } catch ( Exception e ) {
            throw new Exception("[MechListData " + e.getMessage() + "]");
        }
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
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

    public float getCost() {
        return Cost;
    }

    public void setCost(float Cost) {
        this.Cost = Cost;
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

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the Config
     */
    public String getConfig() {
        return Config;
    }

    /**
     * @param Config the Config to set
     */
    public void setConfig(String Config) {
        this.Config = Config;
    }
}
