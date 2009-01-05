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
    private String CritName,
                   Type,
                   LookupName,
                   Specials = "",
                   Manufacturer = "";
    private int Crits = 0,
                LotSize = 0,
                AmmoIndex = 0,
                ShtRange = 0,
                MedRange = 0,
                LngRange = 0,
                Heat = 0;
    private float Tonnage = 0.0f,
                  Cost = 0.0f,
                  OffBV = 0.0f,
                  DefBV = 0.0f;
    private boolean HasAmmo = false,
                    Clan = false,
                    alloc_head = true,
                    alloc_ct = true,
                    alloc_torsos = true,
                    alloc_arms = true,
                    alloc_legs = true,
                    CanSplit = false,
                    Rear = false,
                    CanMountRear = false;
    private AvailableCode AC;

    public Equipment() {
        // provided for any classes that extend this one.  should not be used
        // for regular equipment
    }

    public Equipment( String name, String t, AvailableCode a, boolean c ) {
        CritName = name;
        LookupName = name;
        Type = t;
        AC = a;
        Clan = c;
    }

    public void SetLookupName( String n ) {
        // provided if it's anything different than the CritName
        LookupName = n;
    }

    public void SetStats( int crits, float tons, float cost, float obv, float dbv, String spec ) {
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

    @Override
    public String GetCritName() {
        if( Rear ) {
            return "(R) " + CritName;
        } else {
            return CritName;
        }
    }

    @Override
    public int NumCrits() {
        return Crits;
    }

    @Override
    public float GetTonnage() {
        if( IsArmored() ) {
            return Tonnage + ( Crits * 0.5f );
        } else {
            return Tonnage;
        }
    }

    @Override
    public float GetCost() {
        if( IsArmored() ) {
            return Cost + ( Crits * 150000.0f );
        } else {
            return Cost;
        }
    }

    public float GetOffensiveBV() {
        return OffBV;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        return GetOffensiveBV();
    }

    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return (( OffBV + DefBV ) * 0.5f * NumCrits() ) + DefBV;
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

    public String GetMMName( boolean UseRear ) {
        if( Rear ) {
            return LookupName + " (R)";
        } else {
            return LookupName;
        }
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

    public boolean IsClan() {
        return Clan;
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

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = new AvailableCode( AC.IsClan(), AC.GetTechRating(), AC.GetSLCode(), AC.GetSWCode(), AC.GetCICode(), AC.GetIntroDate(), AC.GetExtinctDate(), AC.GetReIntroDate(), AC.GetIntroFaction(), AC.GetReIntroFaction(), AC.WentExtinct(), AC.WasReIntroduced(), AC.GetRandDStart(), AC.IsPrototype(), AC.GetRandDFaction(), AC.GetRulesLevelBM(), AC.GetRulesLevelIM() );
        if( IsArmored() ) {
            if( AC.IsClan() ) {
                retval.Combine( CLArmoredAC );
            } else {
                retval.Combine( ISArmoredAC );
            }
        }
        return retval;
    }

    @Override
    public String toString() {
        return CritName;
    }
}
