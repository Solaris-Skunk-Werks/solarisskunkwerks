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

public class AESSystem extends abPlaceable {
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private Mech Owner;
    private boolean LegSystem;
    private static MechModifier LegMod = new MechModifier( 0, 0, 0, 0, 0, -2, 0, 0.0, 0.0, 0.0, 0.0, false, false );

    public AESSystem( Mech m, boolean legs ) {
        AC.SetISCodes( 'E', 'X', 'X', 'F' );
        AC.SetISDates( 3067, 3070, true, 3070, 0, 0, false, false );
        AC.SetISFactions( "KH", "BC", "", "" );
        AC.SetCLCodes( 'E', 'X', 'X', 'F' );
        AC.SetCLDates( 3067, 3070, true, 3070, 0, 0, false, false );
        AC.SetCLFactions( "WD", "WD", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Owner = m;
        LegSystem = legs;
        SetExclusions( new Exclusion( new String[] { "Targeting Computer", "MASC", "TSM" }, "A.E.S." ) );
    }

    public String ActualName() {
        return "Actuator Enhancement System";
    }

    public String LookupName() {
        return CritName();
    }

    public String CritName() {
        return "A.E.S.";
    }

    public String ChatName() {
        return "AES";
    }

    public String MegaMekName( boolean UseRear ) {
        if( Owner.GetBaseTechbase() >= AvailableCode.TECH_CLAN ) {
            return "CLAES";
        } else {
            return "ISAES";
        }
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    @Override
    public int NumCrits() {
        int MechTons = Owner.GetTonnage();
        if( MechTons < 40 ) {
            return 1;
        } else if( MechTons > 35 && MechTons < 60 ) {
            return 2;
        } else if( MechTons > 55 && MechTons < 80 ) {
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public int NumCVSpaces() {
        return 0;
    }

    @Override
    public double GetTonnage() {
        double retval = 0.0;
        if( Owner.UsingFractionalAccounting() ) {
            if( Owner.IsQuad() ) {
                retval = Owner.GetTonnage() * 0.02;
                retval = Math.ceil( retval * 1000 ) * 0.001;
            } else {
                retval = Owner.GetTonnage() * 0.02857;
                retval = Math.ceil( retval * 1000 ) * 0.001;
            }
        } else {
            if( Owner.IsQuad() ) {
                retval = ((int) ( Math.ceil( Owner.GetTonnage() * 0.02 * 2.0 ))) * 0.5;
            } else {
                retval = ((int) ( Math.ceil( Owner.GetTonnage() * 0.02857 * 2.0 ))) * 0.5;
            }
        }
        if( IsArmored() ) {
            retval += NumCrits() * 0.5;
        }
        return retval;
    }

    @Override
    public double GetCost() {
        double retval = 0.0;
        if( IsArmored() ) {
            retval += NumCrits() * 150000.0;
        }
        if( LegSystem ) {
            retval += Owner.GetTonnage() * 700.0;
        } else {
            retval += Owner.GetTonnage() * 500.0;
        }
        return retval;
    }

    @Override
    public double GetOffensiveBV() {
        // AES modifies BV, but doesn't have one of its own
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // AES modifies BV, but doesn't have one of its own
        return 0.0;
    }
    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // AES modifies BV, but doesn't have one of its own
        return 0.0;
    }

    @Override
    public double GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0 * NumCrits();
        } else {
            return 0.0;
        }
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public void AddMechModifier(MechModifier m) {
        // do nothing here, we provide our own.
    }

    @Override
    public MechModifier GetMechModifier() {
        if( LegSystem ) {
            return LegMod;
        } else {
            return null;
        }
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public AvailableCode GetAvailability() {
        return AC;
    }
}