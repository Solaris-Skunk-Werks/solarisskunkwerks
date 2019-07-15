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

public class PartialWing extends abPlaceable {

    private Mech Owner;
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    String Manufacturer = "";
    private boolean Clan;

    public PartialWing( Mech m ) {
        this(m, true);
    }

    public PartialWing( Mech m, boolean clan ) {
        Owner = m;
        AC.SetISCodes( 'F', 'X', 'X', 'E' );
        AC.SetISDates( 3061, 3067, true, 3067, 0, 0, false, false );
        AC.SetISFactions( "", "", "", "" );
        AC.SetCLCodes( 'F', 'X', 'X', 'E' );
        AC.SetCLDates( 3061, 3067, true, 3067, 0, 0, false, false );
        AC.SetCLFactions( "CJF", "CJF", "", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Clan = clan;
        SetLocked( true );
    }

    public String ActualName() {
        return "BattleMech Partial Wing";
    }

    public String CritName() {
        return "Partial Wing";
    }

    public String LookupName() {
        if( Clan ) {
            return "(CL) Partial Wing";
        } else {
            return "(IS) Partial Wing";
        }
    }

    public String ChatName() {
        return "P-Wing";
    }

    public String MegaMekName( boolean UseRear ) {
        return "PartialWing";
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    public void SetClan( boolean b ) {
        Clan = b;
    }

    public boolean IsClan() {
        return Clan;
    }

    @Override
    public int NumCrits() {
        // like the engine, this will only return the number of crits in one location
        if ( Clan ) { return 3; }
        else { return 4; }
    }

    public int NumCVSpaces() {
        return 0;
    }

    @Override
    public double GetTonnage() {
        double result = Owner.GetTonnage() * 0.1;
        if ( Clan ) {
            result = ( Math.ceil( ( result * 0.5 ) * 2 ) * 0.5 );
        } else {
            result = ( Math.ceil( ( result * 0.7 ) * 2 ) * 0.5 );
        }
        if( IsArmored() ) {
            result += 3.0;
        }
        return result;
    }

    @Override
    public double GetCost() {
        double retval;
        if ( Owner.GetTechBase() == AvailableCode.TECH_CLAN ) {
            retval = 50000.0 * ( Math.ceil( Owner.GetTonnage() * 0.1 ) * 0.5 );
        } else {
            retval = 50000.0 * ( Math.ceil( Owner.GetTonnage() * 0.1 ) * 0.7 );
        }
        if( IsArmored() ) {
            retval += 900000.0;
        }
        return retval;
    }

    @Override
    public double GetOffensiveBV() {
        // partial wings modify the battlemech's BV in other ways.
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // partial wings modify the battlemech's BV in other ways.
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    @Override
    public double GetDefensiveBV() {
        if( IsArmored() ) {
            return 30.0;
        }
        return 0.0;
    }

    @Override
    public boolean Place( ifMechLoadout l ) {
        //Check how many crits are available in the torsos, if they are less then we need, stop!
        if ( ((ifMechLoadout) l).FreeCrits( ((ifMechLoadout) l).GetLTCrits() ) <= this.NumCrits() ) return false;
        if ( ((ifMechLoadout) l).FreeCrits( ((ifMechLoadout) l).GetRTCrits() ) <= this.NumCrits() ) return false;

        try {
            ((ifMechLoadout) l).AddToLT( this );
        } catch( Exception e ) {
            return false;
        }

        try {
            ((ifMechLoadout) l).AddToRT( this );
        } catch( Exception e ) {
            l.Remove( this );
            return false;
        }

        return true;
    }

    @Override
    public boolean Place( ifMechLoadout l, LocationIndex[] locs ) {
        // we should have two location indexes
        if( locs.length != 2 ) { return false; }

        //Check how many crits are available in the torsos, if they are less then we need, stop!
        if ( ((ifMechLoadout) l).FreeCrits( ((ifMechLoadout) l).GetLTCrits() ) <= this.NumCrits() ) return false;
        if ( ((ifMechLoadout) l).FreeCrits( ((ifMechLoadout) l).GetRTCrits() ) <= this.NumCrits() ) return false;

        try {
            l.AddTo( this, locs[0].Location, locs[0].Index );
        } catch( Exception e ) {
            return false;
        }

        try {
            l.AddTo( this, locs[1].Location, locs[1].Index );
        } catch( Exception e ) {
            l.Remove( this );
            return false;
        }

        return true;
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
    public boolean CanSplit() {
        return true;
    }

    @Override
    public String GetManufacturer() {
        return Manufacturer;
    }

    @Override
    public void SetManufacturer( String n ) {
        Manufacturer = n;
    }

    public int GetJumpBonus() {
        if( Owner.GetTonnage() < 60 ) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }
}
