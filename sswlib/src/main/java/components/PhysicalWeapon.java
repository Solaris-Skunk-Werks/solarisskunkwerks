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

package components;

public class PhysicalWeapon extends abPlaceable implements ifWeapon {
    public static final int PW_CLASS_NORMAL = 0,
                     PW_CLASS_SHIELD = 1,
                     PW_CLASS_SPIKE = 2,
                     PW_CLASS_TALON = 3,
                     PW_CLASS_INDUSTRIAL = 4,
                     PW_CLASS_SPOTWELDER = 5;

    private String ActualName,
                   CritName,
                   MegaMekName,
                   LookupName,
                   ChatName = "",
                   Type,
                   Specials,
                   BookReference = "",
                   Manufacturer = "";
    protected ifUnit Owner;
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
                    ReplacesLowerArm = false,
                    Alloc_HD = false,
                    Alloc_CT = false,
                    Alloc_Torso = false,
                    Alloc_Arms = true,
                    Alloc_Legs = false,
                    CanSplit = false,
                    PowerAmps = false;

    public PhysicalWeapon( String actualname, String lookupname, String critname, String mname, String chatn, AvailableCode a ) {
        ActualName= actualname;
        LookupName = lookupname;
        CritName = critname;
        MegaMekName = mname;
        ChatName = chatn;
        //Owner = m;
        AC = a;
    }

    // Required to allow Talons to extend PhysicalWeapon
    public PhysicalWeapon() { }

    public PhysicalWeapon( PhysicalWeapon p ) {
        ActualName= p.ActualName;
        LookupName = p.LookupName;
        CritName = p.CritName;
        MegaMekName = p.MegaMekName;
        Owner = p.Owner;
        AC = p.AC.Clone();
        TonMult = p.TonMult;
        CritMult = p.CritMult;
        TonAdd = p.TonAdd;
        CritAdd = p.CritAdd;
        Alloc_HD = p.Alloc_HD;
        Alloc_CT = p.Alloc_CT;
        Alloc_Torso = p.Alloc_Torso;
        Alloc_Arms = p.Alloc_Arms;
        Alloc_Legs = p.Alloc_Legs;
        CanSplit = p.CanSplit;
        DamageMult = p.DamageMult;
        DamageAdd = p.DamageAdd;
        Heat = p.Heat;
        Type = p.Type;
        Specials = p.Specials;
        CostMult = p.CostMult;
        CostAdd = p.CostAdd;
        BVMult = p.BVMult;
        BVAdd = p.BVAdd;
        DefBV = p.DefBV;
        RoundToHalfTon = p.RoundToHalfTon;
        ChatName = p.ChatName;
        BookReference = p.BookReference;
        ToHitShort = p.ToHitShort;
        ToHitMedium = p.ToHitMedium;
        ToHitLong = p.ToHitLong;
        Nuclear = p.Nuclear;
        Fusion = p.Fusion;
        PowerAmps = p.PowerAmps;
        RequiresHand = p.RequiresHand;
        ReplacesLowerArm = p.ReplacesLowerArm;
        SetBattleForceAbilities( p.GetBattleForceAbilities() );
        if ( RequiresHand == true ) { RequiresLowerArm = true; }
        ReplacesHand = p.ReplacesHand;
        if ( ReplacesHand == true ) { RequiresHand = false; }
        RequiresLowerArm = p.RequiresLowerArm;
        if ( RequiresLowerArm == false ) { RequiresHand = false; }
        PWClass = p.PWClass;
        if( p.GetMechModifier() != null ) { AddMechModifier( p.GetMechModifier() ); }
    }

    public void SetOwner( ifUnit m ) {
        // convenience method since physical weapons are based on tonnage
        Owner = m;
        if( PWClass == PW_CLASS_INDUSTRIAL ) {
            if( Owner.IsQuad() ){
                SetAllocations( false, false, true, false, false, false );
            } else {
                SetAllocations( false, false, false, true, false, false );
            }
        }
    }

    public void SetType( String type, String spec ) {
        Type = type;
        Specials = spec;
    }
    
    public void SetTonnage( double tmult, double tadd, boolean roundhalf ) {
        TonMult = tmult;
        TonAdd = tadd;
        RoundToHalfTon = roundhalf;
    }

    public void SetCost( double costmult, double costadd ) {
        CostMult = costmult;
        CostAdd = costadd;
    }

    public void SetBV( double bmult, double badd, double dbv ) {
        BVMult = bmult;
        BVAdd = badd;
        DefBV = dbv;
    }

    public void SetCrits( double critmult, int critadd ) {
        CritMult = critmult;
        CritAdd = critadd;
    }

    public void SetHeat( int h ) {
        Heat = h;
    }

    public void SetToHit( int thsrt, int thmed, int thlng ) {
        ToHitShort = thsrt;
        ToHitMedium = thmed;
        ToHitLong = thlng;
    }

    public void SetDamage( double dmult, int dadder ) {
        // sets the weapons damage potential
        DamageMult = dmult;
        DamageAdd = dadder;
    }

    public void SetAllocations( boolean hd, boolean ct, boolean torso, boolean arms, boolean legs, boolean split ) {
        Alloc_HD = hd;
        Alloc_CT = ct;
        Alloc_Torso = torso;
        Alloc_Arms = arms;
        Alloc_Legs = legs;
        CanSplit = split;
    }

    public void SetRequiresLowerArm( boolean b ) {
        RequiresLowerArm = b;
        if ( b == false )
            RequiresHand = false;
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

    public void SetReplacesLowerArm( boolean b ) {
        ReplacesLowerArm = b;
        if ( b == true ) {
            RequiresHand = false;
            RequiresLowerArm = false;
        }
    }

    public void SetRequirements( boolean nuc, boolean fus, boolean amps ) {
        Nuclear = nuc;
        Fusion = fus;
        PowerAmps = amps;
    }

    public void SetPWClass( int pwclass ) {
        PWClass = pwclass;
        if ( Owner != null )
            SetOwner( Owner );
    }

    public void SetBookReference( String s ) {
        BookReference = s;
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

    public int GetPWClass () {
        return PWClass;
    }

    public int GetWeaponClass() {
        return ifWeapon.W_PHYSICAL;
    }

    public int GetFCSType() {
        return ifMissileGuidance.FCS_NONE;
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

    public boolean ReplacesLowerArm() {
        return ReplacesLowerArm;
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

    public String ActualName() {
        return ActualName;
    }

    public String LookupName() {
        return LookupName;
    }

    public String CritName() {
        return CritName;
    }

    public String ChatName() {
        return ChatName;
    }

    public String MegaMekName( boolean UseRear ) {
        return MegaMekName;
    }

    public String BookReference() {
        return BookReference;
    }

    @Override
    public int NumCrits() {
        return (int) Math.ceil( Owner.GetTonnage() * CritMult ) + CritAdd;
    }

    public int NumCVSpaces() {
        return 0;
    }

    @Override
    public double GetTonnage() {
        double result = 0.0;
        if( Owner.UsingFractionalAccounting() ) {
            result = Math.ceil( ( Owner.GetTonnage() * TonMult + TonAdd ) * 1000 ) * 0.001;
        } else {
            if( RoundToHalfTon ) {
                result = ((int) ( Math.ceil( Owner.GetTonnage() * TonMult * 2 ))) * 0.5 + TonAdd;
            } else {
                result = (int) Math.ceil( Owner.GetTonnage() * TonMult ) + TonAdd;
            }
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
            return GetOffensiveBV() * 1.25;
        } else {
            return GetOffensiveBV();
        }
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetCurOffensiveBV(UseRear, UseTC, UseAES);
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

    public String GetType() {
        return Type;
    }

    public String GetSpecials() {
        return Specials;
    }

    public int GetHeat() {
        return Heat;
    }

    public double GetBVHeat() {
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

    public int ClusterModShort() {
        return 0;
    }

    public int ClusterModMedium() {
        return 0;
    }

    public int ClusterModLong() {
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

    public PhysicalWeapon Clone() {
        return new PhysicalWeapon( this );
    }

    @Override
    public String toString() {
        return CritName;
    }
}
