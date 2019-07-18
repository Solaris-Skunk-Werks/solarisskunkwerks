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

public interface ifUnitData {
    public String SerializeIndex();
    public String getName();
    public void setName(String Name);
    public String getFullName();
    public String getLevel();
    public void setLevel(String Level);
    public String getEra();
    public void setEra(String Era);
    public String getTech();
    public void setTech(String Tech);
    public String getType();
    public void setType(String Type);
    public String getMotive();
    public void setMotive(String Motive);
    public String getInfo();
    public void setInfo(String Info);
    public int getTonnage();
    public void setTonnage(int Tonnage);
    public int getYear();
    public void setYear(int Year);
    public int getBV();
    public void setBV(int BV);
    public double getCost();
    public void setCost(double Cost);
    public boolean isOmni();
    public void setOmni(boolean Omni);
    public String getModel();
    public void setModel(String Model);
    public String getFilename();
    public void setFilename(String filename);
    public String getConfig();
    public void setConfig(String Config);
    public String getSource();
    public void setSource(String Source);
    public String getTypeModel();
    public void setTypeModel(String TypeModel);
    public int getMinMP();
    public void setMinMP(int MinMP);
}
