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

public class BoobyTrap extends abPlaceable {

    private ifMechLoadout Owner;
    private int MP = 0;
    private static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private int Crits = 1;

    public BoobyTrap( ifMechLoadout m ) {
        Owner = m;
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        AC.SetISCodes( 'B', 'D', 'F', 'D' );
        AC.SetISDates( 3055, 3060, true, 3060, 0, 0, false, false );
        AC.SetISFactions( "CC", "TC", "WB", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
    }

    @Override
    public String ActualName() {
        return "Booby Trap";
    }

    @Override
    public String LookupName() {
        return "Booby Trap";
    }

    @Override
    public String CritName() {
        return "Booby Trap";
    }

    @Override
    public String ChatName() {
        return "Booby Trap";
    }

    @Override
    public String MegaMekName(boolean UseRear) {
        return "";
    }

    @Override
    public String BookReference() {
        return "Tactical Operations";
    }

    @Override
    public int NumCrits() {
        return Crits;
    }

    @Override
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
        double retval = Owner.GetMech().GetTonnage() * 0.1;
        if( IsArmored() ) {
            retval += 0.5;
        }
        return retval;
    }

    @Override
    public double GetCost() {
        return 0.0;
    }

    @Override
    public double GetOffensiveBV() {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES) {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    @Override
    public double GetDefensiveBV() {
        return 0.0;
    }

    @Override
    public boolean CanAllocHD()
    {
        return false;
    }

    @Override
    public boolean CanAllocCT()
    {
        return true;
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
        return false;
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
    public AvailableCode GetAvailability() {
        return AC;
    }
}