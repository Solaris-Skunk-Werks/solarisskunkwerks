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

public class ExtendedFuelTank extends Equipment {
    private ifUnit Owner;
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public ExtendedFuelTank( ifUnit m ) {
        Owner = m;
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        AC.SetISCodes( 'C', 'C', 'D', 'C' );
        AC.SetISDates( 0, 0, false, 1900, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'C', 'X', 'C', 'C' );
        AC.SetCLDates( 0, 0, false, 1900, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
        AC.SetPIMAllowed( true );
        AC.SetPBMAllowed( true );
    }

    public void SetOwner( ifUnit m ) {
        Owner = m;
    }

    @Override
    public String ActualName() {
        return "Extended Fuel Tank";
    }

    @Override
    public String CritName() {
        return "Fuel Tank";
    }

    @Override
    public String LookupName() {
        return "Extended Fuel Tank";
    }

    @Override
    public String ChatName() {
        return "Fuel";
    }

    @Override
    public String MegaMekName( boolean UseRear ) {
        return "Extended Fuel Tank";
    }

    @Override
    public String BookReference() {
        return "Tech Manual";
    }

    private double GetSize() {
        return Math.ceil( Owner.GetEngine().GetTonnage() * 0.2 ) * 0.5;
    }

    @Override
    public double GetTonnage() {
        return GetSize();
    }

    @Override
    public int NumCrits() {
        return (int) Math.ceil( GetSize() );
    }

    @Override
    public double GetCost() {
        return Math.ceil( GetSize() ) * 500.0;
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
    public boolean IsExplosive() {
        return true;
    }

    @Override
    public AvailableCode GetAvailability() {
        return AC;
    }
}
