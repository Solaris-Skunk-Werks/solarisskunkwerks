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

public class Talons extends PhysicalWeapon {

    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_CLAN );
    private int Placed = 0;
    private int Crits = 2;

    public Talons ( Mech m ) {
        AC.SetCLCodes( 'E', 'X', 'X', 'F' );
        AC.SetCLDates( 0, 0, false, 3072, 0, 0, false, false );
        AC.SetCLFactions( "", "", "CJF", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        SetBattleForceAbilities( new String[]{ "MEL" } );
        Owner = m;
    }

    @Override
    public int GetPWClass () {
        return PhysicalWeapon.PW_CLASS_TALON;
    }
    
    @Override
    public boolean RequiresLowerArm() {
        return false;
    }

    @Override
    public String ActualName() {
        return "Talons";
    }

    @Override
    public String LookupName() {
        return "Talons";
    }

    @Override
    public String CritName() {
        return "Talons";
    }

    @Override
    public String ChatName() {
        return "Talons";
    }

    @Override
    public String MegaMekName( boolean UseRear ) {
        return "Talons";
    }

    @Override
    public String BookReference() {
        return "Tactical Operations";
    }

    @Override
    public int NumCrits() {
        return Crits;
    }
    
    public void SetCrits( int crits ) {
        Crits = crits;
    }

    @Override
    public boolean Contiguous() {
        return true;
    }

    @Override
    public double GetTonnage() {
        double result = 0.0;
        if( Owner.UsingFractionalAccounting() ) {
            result = Math.ceil( Owner.GetTonnage() * 0.0666 * 1000 ) * 0.001;
        } else {
            result = (int) Math.ceil( Owner.GetTonnage() * 0.0666 );
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
            return ( Owner.GetTonnage() * 300.0 + ( NumCrits() * 150000.0 ) );
        } else {
            return Owner.GetTonnage() * 300.0;
        }
    }

    @Override
    public double GetOffensiveBV() {
        if( Owner.GetPhysEnhance().IsTSM() ) {
            return Math.ceil(Owner.GetTonnage() * 0.2);
        } else {
            return Math.ceil(Owner.GetTonnage() * 0.1);
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

    @Override
    public String GetType() {
        return "PA";
    }

    @Override
    public String GetSpecials() {
        return "PB";
    }

    @Override
    public int GetDamageShort() {
        return (int) Math.ceil( Owner.GetTonnage() * 0.2 * 1.5 );
    }

    @Override
    public int GetDamageMedium() {
        return (int) Math.ceil( Owner.GetTonnage() * 0.2 * 1.5 );
    }

    @Override
    public int GetDamageLong() {
        return (int) Math.ceil( Owner.GetTonnage() * 0.2 * 1.5 );
    }

    @Override
    public int GetTechBase() {
        return AC.GetTechBase();
    }

    @Override
    public boolean CanAllocHD()
    {
        return false;
    }

    @Override
    public boolean CanAllocCT()
    {
        return false;
    }

    @Override
    public boolean CanAllocTorso()
    {
        return false;
    }

    @Override
    public boolean CanAllocArms()
    {
        return false;
    }

    @Override
    public boolean CanAllocLegs()
    {
        return true;
    }

    @Override
    public boolean LocationLinked() {
        return true;
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public boolean Place( ifMechLoadout l ) {
        try {
            if( l.IsQuad() ) {
                l.AddToQueue( this );
                l.AddToLA( this, 4 );
                //l.AddToLA( this, 5 );
                l.AddToRA( this, 4 );
                //l.AddToRA( this, 5 );
                l.AddToLL( this, 4 );
                //l.AddToLL( this, 5 );
                l.AddToRL( this, 4 );
                //l.AddToRL( this, 5 );
            } else {
                l.AddToQueue( this );
                l.AddToLL( this, 4 );
                //l.AddToLL( this, 5 );
                l.AddToRL( this, 4 );
                //l.AddToRL( this, 5 );
            }
        } catch( Exception e ) {
            return false;
        }
        return true;
    }

    @Override
    public int NumPlaced() {
        return Placed;
    }

    @Override
    public void IncrementPlaced() {
        Placed++;
    }

    @Override
    public void DecrementPlaced() {
        Placed--;
    }

    @Override
    public void ResetPlaced() {
        Placed = 0;
    }

    @Override
    public String toString() {
        if( NumCrits() > Placed ) {
            return "Talons (" + ( NumCrits() - Placed ) + ")";
        } else {
            return "Talons";
        }
    }
}
