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

package ssw.battleforce;

import java.util.Vector;
import ssw.Constants;
import ssw.components.Mech;

public class BattleForceStats {
    private String Element = "",
                    MV = "",
                    Unit = "",
                    Image = "";
    private double[] Mods = {2.63, 2.24, 1.82, 1.38, 1.00, 0.86, 0.77, 0.68};

    private Vector<String> Abilities = new Vector<String>();
    private Vector<String> AltMunitions = new Vector<String>();

    private int S = 0,
                M = 0,
                L = 0,
                E = 0,
                Wt = 0,
                Skill = 9,
                OV = 0,
                Armor = 0,
                Internal = 0,
                BasePV = 0,
                PV = 0,
                Gunnery = 4,
                Piloting = 5;

    public BattleForceStats( Mech m ) {
        Element = m.GetFullName();
        Abilities = m.GetBFAbilities();
        int[] Data = m.GetBFDamage( this );
        S = Data[Constants.BF_SHORT];
        M = Data[Constants.BF_MEDIUM];
        L = Data[Constants.BF_LONG];
        E = Data[Constants.BF_EXTREME];
        OV = Data[Constants.BF_OV];
        BasePV = m.GetBFPoints();
        PV = BasePV;

        Wt = m.GetBFSize();
        Armor = m.GetBFArmor();
        Internal = m.GetBFStructure();

        MV = m.GetBFPrimeMovement() + m.GetBFPrimeMovementMode();
        if ( m.GetBFSecondaryMovement() != 0 ) {
            MV += "/" + m.GetBFSecondaryMovement() + m.GetBFSecondaryMovementMode();
        }

        Image = m.GetSSWImage();
    }

    public BattleForceStats( Mech m, String Unit, int Gunnery, int Piloting ) {
        this(m);
        this.Unit = Unit;
        setGunnery(Gunnery);
        setPiloting(Piloting);
    }

    private void updateSkill() {
        int Total = Gunnery + Piloting;
        if ( Total <= 1 ) {
            Skill = 0;
        } else if ( Total <= 3 ) {
            Skill = 1;
        } else if ( Total <= 5 ) {
            Skill = 2;
        } else if ( Total <= 7 ) {
            Skill = 3;
        } else if ( Total <= 9 ) {
            Skill = 4;
        } else if ( Total <= 11 ) {
            Skill = 5;
        } else if ( Total <= 13 ) {
            Skill = 6;
        } else {
            Skill = 7;
        }
        updatePointValue();
    }

    private void updatePointValue() {
        PV = (int)((int) BasePV * Mods[Skill]);
    }

    public String SerializeCSV() {
        String data = "";

        data += CSVFormat(Element);
        data += CSVFormat(PV + "");
        data += CSVFormat(Wt + "");
        data += CSVFormat(MV + "");
        data += CSVFormat(getShort() + "");
        data += CSVFormat(getMedium() + "");
        data += CSVFormat(getLong() + "");
        data += CSVFormat(getExtreme() + "");
        data += CSVFormat(getOverheat() + "");
        data += CSVFormat(getAbilities().toString().replace("[", "").replace("]", ""));

        return data.substring(0, data.length()-2);
    }

    public String CSVFormat( String data ) {
        return "\"" + data + "\", ";
    }

    public Vector<String> getAbilities() {
        return Abilities;
    }


    public String getAbilitiesString() {
        String retval = "";

        for ( int i = 0; i < Abilities.size(); i++ )
        {
            retval += Abilities.get(i);
            if ( i != Abilities.size() - 1 )
                retval += ", ";
        }

        return retval.replace("[", "").replace("]", "");
    }

    public void addAbility(String s)
    {
        if ( !Abilities.contains(s) ) { Abilities.add(s); }
    }

    public Vector<String> getAltMunitions() {
        return AltMunitions;
    }

    public String getAltMunitionsString() {
        String retval = "";
        for ( String munition : AltMunitions ) {
            retval += munition;
        }
        return retval;
    }

    public void addAltMunition(String s) {
        AltMunitions.add(s);
    }

    public int getShort() {
        return S;
    }

    public int getMedium() {
        return M;
    }

    public int getLong() {
        return L;
    }

    public int getExtreme() {
        return E;
    }

    public int getWeight() {
        return Wt;
    }

    public int getOverheat() {
        return OV;
    }

    public int getArmor() {
        return Armor;
    }

    public int getInternal() {
        return Internal;
    }

    public String getElement() {
        return Element;
    }
    
    public String getMovement() {
        return MV;
    }

    public int getPointValue() {
        return PV;
    }

    public int getSkill() {
        return Skill;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public int getGunnery() {
        return Gunnery;
    }

    public void setGunnery(int Gunnery) {
        this.Gunnery = Gunnery;
        updateSkill();
    }

    public int getPiloting() {
        return Piloting;
    }

    public void setPiloting(int Piloting) {
        this.Piloting = Piloting;
        updateSkill();
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public int getBasePV() {
        return BasePV;
    }
}
