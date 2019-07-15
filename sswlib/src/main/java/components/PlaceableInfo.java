/*
Copyright (c) 2008~2009, George Blouin Jr (george.blouin@gmail.com)
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
package components;

import Print.PrintConsts;
import filehandlers.FileCommon;
import java.util.Vector;

public class PlaceableInfo {
        public int Location,
                    UnitType = common.Constants.BattleMech,
                    NameLength = 23;
        public String   Count = "",
                        name = "",
                        name2 = "",
                        locName = "",
                        heat = "--",
                        min = "--",
                        damage = "--",
                        specials = "--",
                        rShort = "--",
                        rMed = "--",
                        rLong = "--";
        public abPlaceable Item;
        private Mech CurMech;
        private int MiniConvRate = 1;

        public PlaceableInfo( Mech m, int MiniConvRate ) {
            CurMech = m;
            this.MiniConvRate = MiniConvRate;
        }
        
        public PlaceableInfo( int MiniConvRate, int UnitType ) {
            this.MiniConvRate = MiniConvRate;
            this.UnitType = UnitType;
        }

        public PlaceableInfo( Mech m, int MiniConvRate, String Count, int Location, String LocationName, String Name, String Heat, String Damage, String Min, String Short, String Medium, String Long, String Specials) {
            this.CurMech = m;
            this.MiniConvRate = MiniConvRate;
            this.Count = Count;
            this.Location = Location;
            this.locName = LocationName;
            this.name = Name;
            this.heat = Heat;
            this.damage = Damage;
            this.min = Min;
            this.rShort = Short;
            this.rMed = Medium;
            this.rLong = Long;
            this.specials = Specials;
        }

        public PlaceableInfo( Mech m, int MiniConvRate, MultiSlotSystem item ) {
            this.CurMech = m;
            this.MiniConvRate = MiniConvRate;
            this.name = item.CritName();
            this.damage = "[E]";
        }

        public PlaceableInfo( Mech m, int MiniConvRate, abPlaceable item, int Location ) {
            this(MiniConvRate, item, Location, m.GetLoadout().GetTechBase(), m.IsQuad(), common.Constants.BattleMech);
            this.CurMech = m;
        }
        
        public PlaceableInfo( int MiniConvRate, abPlaceable item, int Location, int Techbase, boolean IsQuad, int UnitType ) {
            this.MiniConvRate = MiniConvRate;
            this.Item = item;
            this.UnitType = UnitType;
            this.name = PrintConsts.GetPrintName( item, Techbase, Location ).trim();
                    //.replace("Medium Pulse", "Med. Pulse")
                    //.replace("Beagle Active Probe", "Beagle Active Prb")
                    //.replace("Guardian ECM Suite", "Guardian ECM")
                    //.replace("Targeting Computer", "Targeting Comp.");
            String[] names = PrintConsts.wrapText(this.name, NameLength, false);
            if ( names.length > 1 ) {
                name = names[0];
                name2 = names[1];
            }
            this.Location = Location;
            this.locName = FileCommon.EncodeLocation( Location, IsQuad, this.UnitType );

            if( item instanceof Equipment ) {
                Equipment e = (Equipment) item;
                this.heat = e.GetHeat() + "";
                if( e.GetSpecials().equals( "-" ) )
                    this.damage = "[" + e.GetType() + "]";
                else
                    this.specials = ("[" + e.GetType() + ", " + e.GetSpecials() + "]").replace(", -", "");
                this.rShort = ((e.GetShortRange() * MiniConvRate) + "").replace("0", "--");
                this.rMed = ((e.GetMediumRange() * MiniConvRate) + "").replace("0", "--");
                this.rLong = ((e.GetLongRange() * MiniConvRate) + "").replace("0", "--");
            } else if( item instanceof ifWeapon ) {
                ifWeapon weap = (ifWeapon) item;
                this.heat = weap.GetHeat() + "";
                if( weap.IsUltra() || weap.IsRotary() ) this.heat += "/s";
                this.damage = weap.GetDamageShort() + "";
                if( weap.GetWeaponClass() == ifWeapon.W_MISSILE ) this.damage += "/m";
                if( weap.GetDamageShort() != weap.GetDamageMedium() ||
                     weap.GetDamageShort() != weap.GetDamageLong() ||
                     weap.GetDamageMedium() != weap.GetDamageLong() )
                    this.damage = weap.GetDamageShort() + "/" + weap.GetDamageMedium() + "/" + weap.GetDamageLong();
                if( weap.GetSpecials().equals( "-" ) )
                    this.damage += " [" + weap.GetType() + "]";
                else
                    this.specials = ("[" + weap.GetType() + ", " + weap.GetSpecials() + "]").replace(", -", "");
                if( weap.GetRangeMin() > 0 ) this.min = (weap.GetRangeMin() * MiniConvRate ) + "";
                this.rShort = (weap.GetRangeShort() * MiniConvRate) + "";
                this.rMed = (weap.GetRangeMedium() * MiniConvRate) + "";
                this.rLong = (weap.GetRangeLong() * MiniConvRate) + "";
            }
        }

        public void Clean() {
            this.damage = this.specials;
            this.min = "";
            this.rShort = "";
            this.rMed = "";
            this.rLong = "";
            this.specials = "-";
        }

        public Vector getRowData() {
            Vector data = new Vector();
            data.add(this.Count);
            data.add(this.name);
            data.add(this.locName);
            data.add(this.heat);
            data.add(this.damage);
            data.add(this.min);
            data.add(this.rShort);
            data.add(this.rMed);
            data.add(this.rLong);
            return data;
        }

        public PlaceableInfo ATMERAmmo( PlaceableInfo item ) {
            return new PlaceableInfo(CurMech, MiniConvRate, "", 0, "", " ER", "", "1/m", (4 * MiniConvRate ) + "", (9 * MiniConvRate ) + "", (18 * MiniConvRate ) + "", (27 * MiniConvRate ) + "", item.specials);
        }

        public PlaceableInfo ATMHEAmmo( PlaceableInfo item ) {
            return new PlaceableInfo(CurMech, MiniConvRate, "", 0, "", " HE", "", "3/m", "-", (3 * MiniConvRate ) + "", (6 * MiniConvRate ) + "", (9 * MiniConvRate ) + "", item.specials);
        }

        public PlaceableInfo MMLLRMAmmo( PlaceableInfo item ) {
            return new PlaceableInfo(CurMech, MiniConvRate, "", 0, "", " LRM", "", "1/Msl.", (6 * MiniConvRate ) + "", (7 * MiniConvRate ) + "", (14 * MiniConvRate ) + "", (21 * MiniConvRate ) + "", "-");
        }

        public PlaceableInfo MMLSRMAmmo( PlaceableInfo item ) {
            return new PlaceableInfo(CurMech, MiniConvRate, "", 0, "", " SRM", "", "2/Msl.", "--", (3 * MiniConvRate ) + "", (6 * MiniConvRate ) + "", (9 * MiniConvRate ) + "", "-");
        }

        public PlaceableInfo ArtemisIV( PlaceableInfo item ) {
            return new PlaceableInfo(CurMech, MiniConvRate, "", 0, "", "w/Artemis IV FCS", "", item.specials, "", "", "", "", "-");
        }

        public PlaceableInfo ArtemisV( PlaceableInfo item ) {
            return new PlaceableInfo(CurMech, MiniConvRate, "", 0, "", "w/Artemis V FCS", "", item.specials, "", "", "", "", "-");
        }

        public PlaceableInfo Apollo( PlaceableInfo item ) {
            return new PlaceableInfo(CurMech, MiniConvRate, "", 0, "", "w/Apollo FCS", "", item.specials, "", "", "", "", "-");
        }

        public PlaceableInfo TargetingComputer() {
            TargetingComputer tc = CurMech.GetTC();
            return new PlaceableInfo(CurMech, MiniConvRate, "1", CurMech.GetLoadout().Find((abPlaceable) tc), FileCommon.EncodeLocation( CurMech.GetLoadout().Find((abPlaceable) tc), CurMech.IsQuad() ), "", tc.CritName(), "", "-", "-", "-", "-", "-");
        }
}
