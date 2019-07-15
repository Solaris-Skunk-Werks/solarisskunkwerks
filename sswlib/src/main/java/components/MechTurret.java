/*
Copyright (c) 2010, Justin R. Bengtson (poopshotgun@yahoo.com)
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

import common.CommonTools;
import java.util.Vector;

public class MechTurret extends abPlaceable implements ifTurret {

    private ifMechLoadout Owner;
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private Vector<ifWeapon> weapons = new Vector<ifWeapon>();

    public MechTurret( ifMechLoadout l ) {
        Owner = l;
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        AC.SetISCodes( 'C', 'F', 'X', 'F' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'C', 'X', 'E', 'E' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
    }

    public boolean AddWeapon( ifWeapon w ) {
        if( ! (( w instanceof RangedWeapon ) || ( w instanceof MGArray )) ) {
            return false;
        }
        if( w instanceof RangedWeapon ) {
            if( ((RangedWeapon) w).CanSplit() ) {
                if( Owner.FindIndexes( (abPlaceable) w ).size() > 1 ) {
                    return false;
                }
            }
        }
        weapons.add( w );
        return true;
    }

    public void RemoveWeapon( ifWeapon w ) {
        // extra code here!
        weapons.remove( w );
    }

    public Vector<ifWeapon> GetWeapons() {
        return weapons;
    }

    public boolean IsInstalled( ifWeapon w ) {
        return weapons.contains( w );
    }

    @Override
    public String ActualName() {
        int location = Owner.Find( this );
        if( Owner.Find( this ) == LocationIndex.MECH_LOC_CT ) {
            return "Mech Head Turret";
        } else if( location == LocationIndex.MECH_LOC_RT || location == LocationIndex.MECH_LOC_LT ) {
            if( Owner.IsQuad() ) {
                return "Quad Mech Turret";
            } else {
                return "Mech Shoulder Turret";
            }
        }
        // this should never happen as the turret should always be allocated...
        return "Turret";
    }

    @Override
    public String LookupName() {
        return "Turret";
    }

    @Override
    public String CritName() {
        return "Turret";
    }

    @Override
    public String ChatName() {
        return "Trt";
    }

    @Override
    public String MegaMekName(boolean UseRear) {
        return "Turret";
    }

    @Override
    public String BookReference() {
        return "Tactical Operations";
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public boolean CanAllocHD() {
        return false;
    }

    @Override
    public boolean CanAllocCT() {
        return true;
    }

    @Override
    public boolean CanAllocTorso() {
        return true;
    }

    @Override
    public boolean CanAllocArms() {
        return false;
    }

    @Override
    public boolean CanAllocLegs() {
        return false;
    }

    @Override
    public int NumCrits() {
        return 1;
    }

    public int NumCVSpaces() {
        return 0;
    }

    private double GetSize() {
        double retval = 0.0;
        for( int i = 0; i < weapons.size(); i++ ) {
            retval += ((abPlaceable) weapons.get( i )).GetTonnage();
        }
        if( Owner.GetMech().UsingFractionalAccounting() ) {
            return CommonTools.RoundFractionalTons( retval * 0.1 );
        } else {
            return ((int) ( Math.ceil( retval * 0.1 * 2 ))) * 0.5;
        }
    }

    @Override
    public double GetTonnage() {
        double retval = GetSize();
        if( IsArmored() ) { retval += 0.5; }
        return retval;
    }

    @Override
    public double GetCost() {
        double retval = GetSize() * 10000.0;
        if( IsArmored() ) { retval += 150000.0; }
        return retval;
    }

    @Override
    public double GetOffensiveBV() {
        // turrets only modify weapon BV for arc purposes
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES) {
        return GetOffensiveBV();
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    @Override
    public double GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public AvailableCode GetAvailability() {
        if( IsArmored() ) {
            return ArmoredAC;
        }
        return AC;
    }
}