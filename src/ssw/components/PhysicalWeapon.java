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
    private String Name,
                   MMName,
                   Type,
                   Specials,
                   Manufacturer = "";
    private Mech Owner;
    private AvailableCode AC;
    private int Heat = 0,
                ToHitShort = 0,
                ToHitMedium = 0,
                ToHitLong = 0,
                DamageAdd = 0,
                CritAdd = 0;
    private float TonMult = 0.0f,
                  CritMult = 0.0f,
                  TonAdd = 0.0f,
                  DamageMult = 0.0f,
                  CostMult = 0.0f,
                  CostAdd= 0.0f,
                  BVMult = 0.0f;
    private boolean Fusion = false,
                    Nuclear = false,
                    RoundToHalfTon = false,
                    RequiresHand = true,
                    ReplacesHand = false,
                    RequiresLowerArm = true;

    public PhysicalWeapon( String name, String lookup, Mech m, AvailableCode a ) {
        Name = name;
        MMName = lookup;
        Owner = m;
        AC = a;
    }

    public void SetStats( float tmult, float cmult, float tadder, int cadder ) {
        // sets the weapon's tonnage and critical statistics
        TonMult = tmult;
        CritMult = cmult;
        TonAdd = tadder;
        CritAdd = cadder;
    }

    public void SetDamage( float dmult, int dadder ) {
        // sets the weapons damage potential
        DamageMult = dmult;
        DamageAdd = dadder;
    }

    public void SetHeat( int h ) {
        Heat = h;
    }

    public void SetSpecials( String type, String spec, float cmult, float cadd, float bmult, boolean round ) {
        Type = type;
        Specials = spec;
        CostMult = cmult;
        CostAdd = cadd;
        BVMult = bmult;
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

    public void SetRequiresFusion( boolean b ) {
        Fusion = b;
    }

    public void SetRequiresNuclear( boolean b ) {
        Nuclear = b;
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

    public boolean RequiresHand() {
        return RequiresHand;
    }

    public boolean ReplacesHand() {
        return ReplacesHand;
    }

    public boolean RequiresLowerArm() {
        return RequiresLowerArm;
    }

    public float GetCostMult() {
        return CostMult;
    }

    public float GetCostAdd() {
        return CostAdd;
    }

    public float GetBVMult() {
        return BVMult;
    }

    public float GetTonMult() {
        return TonMult;
    }

    public float GetTonAdd() {
        return TonAdd;
    }

    public float GetCritMult() {
        return CritMult;
    }

    public int GetCritAdd() {
        return CritAdd;
    }

    public boolean GetRounding() {
        return RoundToHalfTon;
    }

    public float GetDamageMult() {
        return DamageMult;
    }

    public int GetDamageAdd() {
        return DamageAdd;
    }

    @Override
    public String GetCritName() {
        return Name;
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
    public float GetTonnage() {
        float result = 0.0f;
        if( RoundToHalfTon ) {
            result = ((int) ( Math.ceil( Owner.GetTonnage() * TonMult * 2 ))) * 0.5f + TonAdd;
        } else {
            result = (int) Math.ceil( Owner.GetTonnage() * TonMult ) + TonAdd;
        }
        if( IsArmored() ) {
            return result + ( NumCrits() * 0.5f );
        } else {
            return result;
        }
    }

    @Override
    public float GetCost() {
        if( IsArmored() ) {
            return ( GetTonnage() * CostMult + CostAdd + ( NumCrits() * 150000.0f ) );
        } else {
            return GetTonnage() * CostMult + CostAdd;
        }
    }

    public float GetOffensiveBV() {
        return GetDamageShort() * BVMult;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        return GetOffensiveBV();
    }

    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return GetOffensiveBV() * 0.05f * NumCrits();
        }
        return 0.0f;
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
        return 0;
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

    public int GetAmmo() {
        return 0;
    }

    public int GetAmmoIndex() {
        return 0;
    }

    public boolean IsClan() {
        return Owner.IsClan();
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

    public boolean IsArtemisCapable() {
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
        return false;
    }

    @Override
    public boolean CanAllocCT() {
        return false;
    }

    @Override
    public boolean CanAllocTorso() {
        return false;
    }

    @Override
    public boolean CanAllocLegs() {
        return false;
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
