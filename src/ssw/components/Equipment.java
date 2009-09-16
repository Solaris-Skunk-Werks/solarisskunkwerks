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

package ssw.components;

public class Equipment extends abPlaceable {
    private String ActualName,
                   CritName,
                   Type,
                   LookupName,
                   MegaMekName = "",
                   BookReference = "",
                   ChatName = "",
                   Specials = "",
                   Manufacturer = "";
    private int Crits = 0,
                LotSize = 0,
                AmmoIndex = 0,
                ShtRange = 0,
                MedRange = 0,
                LngRange = 0,
                Heat = 0;
    private double Tonnage = 0.0,
                  Cost = 0.0,
                  OffBV = 0.0,
                  DefBV = 0.0,
                  MinTons = 0.0,
                  MaxTons = 0.0,
                  VariableIncrement = 0.0,
                  TonsPerCrit = 0.0,
                  CostPerTon = 0.0;
    private boolean HasAmmo = false,
                    alloc_head = true,
                    alloc_ct = true,
                    alloc_torsos = true,
                    alloc_arms = true,
                    alloc_legs = true,
                    CanSplit = false,
                    Rear = false,
                    CanMountRear = false,
                    Explosive = false,
                    VariableSize = false;
    private AvailableCode AC;

    public Equipment() {
        // provided for any classes that extend this one.  should not be used
        // for regular equipment
    }

    public Equipment( String actualname, String lookupname, String critname, String t, AvailableCode a ) {
        ActualName = actualname;
        CritName = critname;
        LookupName = lookupname;
        Type = t;
        AC = a;
    }

    public Equipment( Equipment e ) {
        ActualName = e.ActualName;
        CritName = e.CritName;
        LookupName = e.LookupName;
        Type = e.Type;
        AC = e.AC.Clone();
        MegaMekName = e.MegaMekName;
        Crits = e.Crits;
        Tonnage = e.Tonnage;
        Cost = e.Cost;
        OffBV = e.OffBV;
        DefBV = e.DefBV;
        Specials = e.Specials;
        ShtRange = e.ShtRange;
        MedRange = e.MedRange;
        LngRange = e.LngRange;
        HasAmmo = e.HasAmmo;
        LotSize = e.LotSize;
        AmmoIndex = e.AmmoIndex;
        Heat = e.Heat;
        alloc_head = e.alloc_head;
        alloc_ct = e.alloc_ct;
        alloc_torsos = e.alloc_torsos;
        alloc_arms = e.alloc_arms;
        alloc_legs = e.alloc_legs;
        CanSplit = e.CanSplit;
        Explosive = e.Explosive;
        VariableSize = e.VariableSize;
        MinTons = e.MinTons;
        MaxTons = e.MaxTons;
        VariableIncrement = e.VariableIncrement;
        TonsPerCrit = e.TonsPerCrit;
        CostPerTon = e.CostPerTon;
        BookReference = e.BookReference;
        ChatName = e.ChatName;
        Manufacturer = e.Manufacturer;
        SetExclusions( e.GetExclusions() );
    }

    public void SetMegaMekName( String n ) {
        // provided if it's anything different than the CritName
        MegaMekName = n;
    }

    public void SetStats( int crits, double tons, double cost, double obv, double dbv, String spec ) {
        Crits = crits;
        Tonnage = tons;
        Cost = cost;
        OffBV = obv;
        DefBV = dbv;
        Specials = spec;
    }

    public void SetRange( int sht, int med, int lng ) {
        // most equipment only has a long range, but this needs to be supported
        // no equipment should have a minimum range
        ShtRange = sht;
        MedRange = med;
        LngRange = lng;
    }

    public void SetAmmo( boolean ammo, int lot, int index ) {
        // provided for the only equipment that uses it
        HasAmmo = ammo;
        LotSize = lot;
        AmmoIndex = index;
    }

    public void SetHeat( int h ) {
        Heat = h;
    }

    public void SetAllocs( boolean hd, boolean ct, boolean st, boolean ar, boolean lg ) {
        alloc_head = hd;
        alloc_ct = ct;
        alloc_torsos = st;
        alloc_arms = ar;
        alloc_legs = lg;
    }

    public void SetSplitable( boolean s ) {
        CanSplit = s;
    }

    public void SetExplosive( boolean b ) {
        Explosive = b;
    }

    public void SetVariableSize( boolean s, double min, double max, double increment, double tpc, double cpt ) {
        VariableSize = s;
        MinTons = min;
        MaxTons = max;
        VariableIncrement = increment;
        TonsPerCrit = tpc;
        CostPerTon = cpt;
    }

    public void SetBookReference( String b ) {
        BookReference = b;
    }

    public void SetChatName( String c ) {
        ChatName = c;
    }

    public String ActualName() {
        return ActualName;
    }

    public String CritName() {
        String retval = CritName;
        if( VariableSize ) {
            retval += " (" + Tonnage + " tons)";
        }
        if( Rear ) {
            return "(R) " + retval;
        } else {
            return retval;
        }
    }

    public String LookupName() {
        return LookupName;
    }

    public String ChatName() {
        return ChatName;
    }

    public String MegaMekName( boolean UseRear ) {
        if( Rear ) {
            return MegaMekName + " (R)";
        } else {
            return MegaMekName;
        }
    }

    public String BookReference() {
        return BookReference;
    }

    @Override
    public int NumCrits() {
        if( VariableSize ) {
            return (int) Math.ceil( Tonnage / TonsPerCrit );
        }
        return Crits;
    }

    @Override
    public double GetTonnage() {
        if( IsArmored() ) {
            return Tonnage + ( NumCrits() * 0.5 );
        } else {
            return Tonnage;
        }
    }

    @Override
    public double GetCost() {
        double retval = 0.0;
        if( VariableSize ) {
            retval = Tonnage * CostPerTon;
        } else {
            retval = Cost;
        }
        if( IsArmored() ) {
            return retval + ( NumCrits() * 150000.0 );
        } else {
            return retval;
        }
    }

    public double GetOffensiveBV() {
        return OffBV;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        if( IsArmored() ) {
            return (( OffBV + DefBV ) * 0.5 * NumCrits() ) + DefBV;
        }
        return DefBV;
    }

    public boolean HasAmmo() {
        // only the AMS will use this, but it's needed
        return HasAmmo;
    }

    public int GetAmmo() {
        // only the AMS will use this, but it's needed
        return LotSize;
    }

    public int GetAmmoIndex() {
        // only the AMS will use this, but it's needed
        return AmmoIndex;
    }

    public int GetShortRange() {
        return ShtRange;
    }

    public int GetMediumRange() {
        return MedRange;
    }

    public int GetLongRange() {
        return LngRange;
    }

    public String GetSpecials() {
        return Specials;
    }

    public String GetType() {
        return Type;
    }

    public int GetHeat() {
        return Heat;
    }

    @Override
    public boolean CanAllocHD() {
        return alloc_head;
    }

    @Override
    public boolean CanAllocCT() {
        return alloc_ct;
    }

    @Override
    public boolean CanAllocTorso() {
        return alloc_torsos;
    }

    @Override
    public boolean CanAllocArms() {
        return alloc_arms;
    }

    @Override
    public boolean CanSplit() {
        return CanSplit;
    }

    public int GetTechBase() {
        return AC.GetTechBase();
    }

    @Override
    public boolean CanAllocLegs() {
        return alloc_legs;
    }

    public void SetMountableRear( boolean b ) {
        CanMountRear = b;
    }

    @Override
    public boolean CanMountRear() {
        return CanMountRear;
    }

    @Override
    public void MountRear( boolean rear ) {
        Rear = rear;
    }

    @Override
    public boolean IsMountedRear() {
        return Rear;
    }

    @Override
    public String GetManufacturer() {
        return Manufacturer;
    }

    @Override
    public void SetManufacturer( String n ) {
        Manufacturer = n;
    }

    public boolean IsExplosive() {
        return Explosive;
    }

    public boolean IsVariableSize() {
        return VariableSize;
    }

    public double GetVariableIncrement() {
        return VariableIncrement;
    }

    public double GetMaxTons() {
        return MaxTons;
    }

    public double GetMinTons() {
        return MinTons;
    }

    public double GetCostPerTon() {
        return CostPerTon;
    }

    public double GetTonsPerCrit() {
        return TonsPerCrit;
    }

    public void SetTonnage( double d ) {
        if( d < MinTons || d > MaxTons ) { return; }
        Tonnage = d;
    }

    public Equipment Clone() {
        return new Equipment( this );
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    @Override
    public String toString() {
        return CritName();
    }
}
