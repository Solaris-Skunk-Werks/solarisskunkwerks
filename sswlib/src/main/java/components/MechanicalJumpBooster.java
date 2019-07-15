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

public class MechanicalJumpBooster extends abPlaceable {

    private Mech Owner;
    private int MP = 0;
    private static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private int Crits = 2;

    public MechanicalJumpBooster( Mech m ) {
        Owner = m;
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        AC.SetISCodes( 'E', 'X', 'X', 'F' );
        AC.SetISDates( 3055, 3060, true, 3060, 0, 0, false, false );
        AC.SetISFactions( "FS", "FS", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
    }

    @Override
    public String ActualName() {
        return "'Mech Mechanical Jump Booster";
    }

    @Override
    public String LookupName() {
        return "Jump Booster";
    }

    @Override
    public String CritName() {
        return "Jump Booster";
    }

    @Override
    public String ChatName() {
        return "JBst(" + GetMP() + ")";
    }

    @Override
    public String MegaMekName(boolean UseRear) {
        return "Jump Boosters";
    }

    @Override
    public String BookReference() {
        return "Tactical Operations";
    }

    public int GetMP() {
        return MP;
    }

    public void SetBoostMP( int i ) {
        if( i < 0 ) { i = 0; }
        if( i > 20 ) { i = 20; }
        MP = i;
    }

    @Override
    public int NumCrits() {
        return Crits;
    }

    public int NumCVSpaces() {
        return 0;
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
        double retval = Math.ceil( MP * Owner.GetTonnage() * 0.1 ) * 0.5;
        if( IsArmored() ) {
            if( Owner.IsQuad() ) {
                retval += 4.0;
            } else {
                retval += 2.0;
            }
        }
        return retval;
    }

    @Override
    public double GetCost() {
        return 150.0 * Owner.GetTonnage() * ( MP * MP );
    }

    @Override
    public double GetOffensiveBV() {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES) {
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
            if( Owner.IsQuad() ) {
                return 40.0;
            } else {
                return 20.0;
            }
        } else {
            return 0.0;
        }
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
                l.AddToLA( this, 4 );
                //l.AddToLA( this, 5 );
                l.AddToRA( this, 4 );
                //l.AddToRA( this, 5 );
                l.AddToLL( this, 4 );
                //l.AddToLL( this, 5 );
                l.AddToRL( this, 4 );
                //l.AddToRL( this, 5 );
            } else {
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
    public AvailableCode GetAvailability() {
        return AC;
    }
}