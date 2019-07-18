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

public class VehicularGrenadeLauncher extends abPlaceable implements ifWeapon {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    public final static int ARC_FORE = 0,
                     ARC_REAR = 1,
                     ARC_FORE_SIDE = 2,
                     ARC_REAR_SIDE = 3,
                     AMMO_FRAG = 0,
                     AMMO_CHAFF = 1,
                     AMMO_INCEN = 2,
                     AMMO_SMOKE = 3;
    public final static String[] ARCDESC = { "Fore", "Rear", "Fore-Side", "Rear-Side" };
    public final static String[] ARCDESCSHORT = { "(F)", "(R)", "(FS)", "(RS)" };
    public final static String[] AMMODESC = { "Fragmentation", "Chaff", "Incendiary", "Smoke" };
    public final static String[] AMMODESCSHORT = { "(Frag)", "(Chaf)", "(Incn)", "(Smok)" };

    private int CurrentArc = ARC_FORE,
                CurrentAmmo = AMMO_FRAG;
    private String Manufacturer = "";

    public VehicularGrenadeLauncher() {
        AC.SetISCodes( 'C', 'D', 'E', 'F' );
        AC.SetISDates( 0, 0, false, 1900, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'C', 'X', 'C', 'E' );
        AC.SetCLDates( 0, 0, false, 1900, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public void SetArcFore() {
        CurrentArc = ARC_FORE;
    }

    public void SetArcRear() {
        CurrentArc = ARC_REAR;
    }

    public void SetArcForeSide() {
        CurrentArc = ARC_FORE_SIDE;
    }

    public void SetArcRearSide() {
        CurrentArc = ARC_REAR_SIDE;
    }

    public void SetAmmoFrag() {
        CurrentAmmo = AMMO_FRAG;
    }

    public void SetAmmoChaff() {
        CurrentAmmo = AMMO_CHAFF;
    }

    public void SetAmmoIncen() {
        CurrentAmmo = AMMO_INCEN;
    }

    public void SetAmmoSmoke() {
        CurrentAmmo = AMMO_SMOKE;
    }

    public void SetArc( int i ) {
        if( i < 0 || i > 3 ) { return; }
        CurrentArc = i;
    }

    public void SetAmmoType( int i ) {
        if( i < 0 || i > 3 ) { return; }
        CurrentAmmo = i;
    }

    public int GetAmmoType() {
        return CurrentAmmo;
    }

    public int GetCurrentArc() {
        return CurrentArc;
    }

    public String ActualName() {
        return "Vehicular Grenade Launcher";
    }

    public String CritName() {
        return ARCDESCSHORT[CurrentArc] + " Vehicular Grenade Launcher";
    }
    
    @Override
    public String PrintName() {
        return "Vehic Gren Launcher";
    }

    public String LookupName() {
        return "Vehicular Grenade Launcher";
    }

    public String ChatName() {
        return "VGL";
    }

    public String MegaMekName( boolean UseRear ) {
        if( UseRear && ( CurrentArc == ARC_FORE || CurrentArc == ARC_FORE_SIDE ) ) {
            return "VehicularGrenadeLauncher (R)";
        }
        if( CurrentArc == ARC_REAR || CurrentArc == ARC_REAR_SIDE ) {
            return "VehicularGrenadeLauncher (R)";
        }
        return "VehicularGrenadeLauncher";
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    @Override
    public boolean CanAllocHD() {
        return false;
    }

    @Override
    public boolean CanAllocArms() {
        return false;
    }

    @Override
    public boolean CanAllocLegs() {
        return false;
    }

    public int NumCrits() {
        return 1;
    }

    public int NumCVSpaces() {
        return 1;
    }

    public double GetTonnage() {
        return 0.5;
    }

    public double GetCost() {
        return 10000.0;
    }


    public double GetOffensiveBV() {
        return 15.0;
    }

    public double GetDefensiveBV() {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        double retval = GetOffensiveBV();
        if( UseRear ) {
            if( CurrentArc == ARC_FORE || CurrentArc == ARC_FORE_SIDE ) {
                retval *= 0.5;
            }
        } else {
            if( CurrentArc == ARC_REAR || CurrentArc == ARC_REAR_SIDE ) {
                retval *= 0.5;
            }
        }
        return retval;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetCurOffensiveBV(UseRear, UseTC, UseAES);
    }

    @Override
    public String GetManufacturer() {
        return Manufacturer;
    }

    @Override
    public void SetManufacturer(String n) {
        Manufacturer = n;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    public String GetType() {
        return "AE";
    }

    public String GetSpecials() {
        return "OS";
    }

    public int GetWeaponClass() {
        return ifWeapon.W_ARTILLERY;
    }

    public int GetHeat() {
        return 1;
    }

    public double GetBVHeat() {
        return 1.0;
    }

    public int GetDamageShort() {
        return 0;
    }

    public int GetDamageMedium() {
        return 0;
    }

    public int GetDamageLong() {
        return 0;
    }

    public int GetRangeMin() {
        return 0;
    }

    public int GetRangeShort() {
        return 0;
    }

    public int GetRangeMedium() {
        return 0;
    }

    public int GetRangeLong() {
        return 1;
    }

    public int GetToHitShort() {
        return 0;
    }

    public int GetToHitMedium() {
        return 0;
    }

    public int GetToHitLong() {
        return 0;
    }

    public int ClusterSize() {
        return 0;
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
        return true;
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

    public int GetFCSType() {
        return ifMissileGuidance.FCS_NONE;
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

    public boolean RequiresFusion() {
        return false;
    }

    public boolean RequiresNuclear() {
        return false;
    }

    public boolean RequiresPowerAmps() {
        return false;
    }
}
