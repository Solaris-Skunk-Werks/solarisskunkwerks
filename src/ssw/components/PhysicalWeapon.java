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

public class PhysicalWeapon extends abPlaceable implements ifWeapon {
    public static final int PW_CLASS_NORMAL = 0,
                     PW_CLASS_SHIELD = 1,
                     PW_CLASS_SPIKE = 2,
                     PW_CLASS_TALON = 3,
                     PW_CLASS_INDUSTRIAL = 4,
                     PW_CLASS_SPOTWELDER = 5;

    private String Name,
                   MMName,
                   LookupName,
                   Type,
                   Specials,
                   Manufacturer = "";
    protected Mech Owner;
    private AvailableCode AC;
    private int Heat = 0,
                ToHitShort = 0,
                ToHitMedium = 0,
                ToHitLong = 0,
                DamageAdd = 0,
                CritAdd = 0,
                PWClass = PW_CLASS_NORMAL;
    private double TonMult = 0.0,
                  CritMult = 0.0,
                  TonAdd = 0.0,
                  DamageMult = 0.0,
                  CostMult = 0.0,
                  CostAdd= 0.0,
                  BVMult = 0.0,
                  BVAdd = 0.0,
                  DefBV = 0.0;
    private boolean Fusion = false,
                    Nuclear = false,
                    RoundToHalfTon = false,
                    RequiresHand = true,
                    ReplacesHand = false,
                    RequiresLowerArm = true,
                    Alloc_HD = false,
                    Alloc_CT = false,
                    Alloc_Torso = false,
                    Alloc_Arms = true,
                    Alloc_Legs = false,
                    CanSplit = false,
                    PowerAmps = false;

    public PhysicalWeapon( String name, String lookupname, String mname, Mech m, AvailableCode a ) {
        Name = name;
        LookupName = lookupname;
        MMName = mname;
        Owner = m;
        AC = a;
    }

    // Required to allow Talons to extend PhysicalWeapon
    public PhysicalWeapon(){}

    public void SetStats( double tmult, double cmult, double tadder, int cadder ) {
        // sets the weapon's tonnage and critical statistics
        TonMult = tmult;
        CritMult = cmult;
        TonAdd = tadder;
        CritAdd = cadder;
    }

    public void SetAllocations( boolean hd, boolean ct, boolean torso, boolean arms, boolean legs, boolean split ) {
        Alloc_HD = hd;
        Alloc_CT = ct;
        Alloc_Torso = torso;
        Alloc_Arms = arms;
        Alloc_Legs = legs;
        CanSplit = split;
    }

    public void SetDamage( double dmult, int dadder ) {
        // sets the weapons damage potential
        DamageMult = dmult;
        DamageAdd = dadder;
    }

    public void SetHeat( int h ) {
        Heat = h;
    }

    public void SetSpecials( String type, String spec, double cmult, double cadd, double bmult, double badd, double dbv, boolean round ) {
        Type = type;
        Specials = spec;
        CostMult = cmult;
        CostAdd = cadd;
        BVMult = bmult;
        BVAdd = badd;
        DefBV = dbv;
        RoundToHalfTon = round;
    }

    public void SetToHit( int thsrt, int thmed, int thlng ) {
        ToHitShort = thsrt;
        ToHitMedium = thmed;
        ToHitLong = thlng;
    }

    public int GetToHitShort() {
        return ToHitShort;
    }

    public int GetToHitMedium() {
        return ToHitMedium;
    }

    public int GetToHitLong() {
        return ToHitLong;
    }

    public void SetRequirements( boolean nuc, boolean fus, boolean amps ) {
        Nuclear = nuc;
        Fusion = fus;
        PowerAmps = amps;
    }

    public void SetRequiresHand( boolean b ) {
        RequiresHand = b;
        if ( b == true )
            RequiresLowerArm = true;
    }

    public void SetReplacesHand( boolean b ) {
        ReplacesHand = b;
        if ( b == true )
            RequiresHand = false;
    }

    public void SetRequiresLowerArm( boolean b ) {
        RequiresLowerArm = b;
        if ( b == false )
            RequiresHand = false;
    }

    public void SetPWClass( int pwclass ) {
        PWClass = pwclass;
    }

    public int GetPWClass () {
        return PWClass;
    }

    public int GetWeaponClass() {
        return ifWeapon.W_PHYSICAL;
    }

    public int GetFCSType() {
        return ifMissileGuidance.FCS_NONE;
    }

    public void SetOwner( Mech m ) {
        // convenience method since physical weapons are based on tonnage
        Owner = m;
    }

    public boolean RequiresFusion() {
        return Fusion;
    }

    public boolean RequiresNuclear() {
        return Nuclear;
    }

    public boolean RequiresPowerAmps() {
        return PowerAmps;
    }

    public boolean RequiresHand() {
        return RequiresHand;
    }

    public boolean ReplacesHand() {
        return ReplacesHand;
    }

    public boolean RequiresLowerArm() {
        return RequiresLowerArm;
    }

    public double GetCostMult() {
        return CostMult;
    }

    public double GetCostAdd() {
        return CostAdd;
    }

    public double GetBVMult() {
        return BVMult;
    }

    public double GetBVAdd() {
        return BVAdd;
    }

    public double GetDefBV() {
        return DefBV;
    }

    public double GetTonMult() {
        return TonMult;
    }

    public double GetTonAdd() {
        return TonAdd;
    }

    public double GetCritMult() {
        return CritMult;
    }

    public int GetCritAdd() {
        return CritAdd;
    }

    public boolean GetRounding() {
        return RoundToHalfTon;
    }

    public double GetDamageMult() {
        return DamageMult;
    }

    public int GetDamageAdd() {
        return DamageAdd;
    }

    @Override
    public String GetCritName() {
        return Name;
    }

    public String GetLookupName() {
        return LookupName;
    }

    @Override
    public String GetMMName(boolean UseRear) {
        return MMName;
    }

    @Override
    public int NumCrits() {
        return (int) Math.ceil( Owner.GetTonnage() * CritMult ) + CritAdd;
    }

    @Override
    public double GetTonnage() {
        double result = 0.0;
        if( RoundToHalfTon ) {
            result = ((int) ( Math.ceil( Owner.GetTonnage() * TonMult * 2 ))) * 0.5 + TonAdd;
        } else {
            result = (int) Math.ceil( Owner.GetTonnage() * TonMult ) + TonAdd;
        }
        if( IsArmored() ) {
            return result + ( NumCrits() * 0.5 );
        } else {
            return result;
        }
    }

    @Override
    public double GetCost() {
        if( IsArmored() ) {
            return ( GetTonnage() * CostMult + CostAdd + ( NumCrits() * 150000.0 ) );
        } else {
            return GetTonnage() * CostMult + CostAdd;
        }
    }

    public double GetOffensiveBV() {
        return GetDamageShort() * BVMult + BVAdd;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        if( UseAES ) {
            return GetOffensiveBV() * 1.5;
        } else {
            return GetOffensiveBV();
        }
    }

    public double GetDefensiveBV() {
        if( IsArmored() ) {
            return GetOffensiveBV() * 0.05 * NumCrits() + DefBV;
        }
        return DefBV;
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    public String GetName() {
        return Name;
    }

    public String GetType() {
        return Type;
    }

    public String GetSpecials() {
        return Specials;
    }

    public int GetHeat() {
        return Heat;
    }

    public int GetBVHeat() {
        return Heat;
    }

    public int GetDamageShort() {
        return (int) Math.ceil( Owner.GetTonnage() * DamageMult ) + DamageAdd;
    }

    public int GetDamageMedium() {
        return (int) Math.ceil( Owner.GetTonnage() * DamageMult ) + DamageAdd;
    }

    public int GetDamageLong() {
        return (int) Math.ceil( Owner.GetTonnage() * DamageMult ) + DamageAdd;
    }

    public int GetRangeMin() {
        return 0;
    }

    public int GetRangeShort() {
        return 1;
    }

    public int GetRangeMedium() {
        return 0;
    }

    public int GetRangeLong() {
        return 0;
    }

    public int ClusterSize() {
        return 1;
    }

    public int ClusterGrouping() {
        return 0;
    }

    public int GetAmmoLotSize() {
        return 0;
    }

    public int GetAmmoIndex() {
        return 0;
    }

    public int GetTechBase() {
        return AC.GetTechBase();
    }

    public boolean IsCluster() {
        return false;
    }

    public boolean IsOneShot() {
        return false;
    }

    public boolean IsStreak() {
        return false;
    }

    public boolean IsUltra() {
        return false;
    }

    public boolean IsRotary() {
        return false;
    }

    public boolean IsExplosive() {
        return false;
    }

    public boolean IsFCSCapable() {
        return false;
    }

    public boolean IsTCCapable() {
        return false;
    }

    public boolean IsArrayCapable() {
        return false;
    }

    public boolean OmniRestrictActuators() {
        return false;
    }

    public boolean HasAmmo() {
        return false;
    }

    public boolean SwitchableAmmo() {
        return false;
    }

    @Override
    public boolean CanAllocHD() {
        return Alloc_HD;
    }

    @Override
    public boolean CanAllocCT() {
        return Alloc_CT;
    }

    @Override
    public boolean CanAllocTorso() {
        return Alloc_Torso;
    }

    @Override
    public boolean CanAllocLegs() {
        return Alloc_Legs;
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
    public String toString() {
        return Name;
    }
}
