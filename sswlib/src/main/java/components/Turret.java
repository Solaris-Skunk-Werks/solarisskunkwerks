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

import common.CommonTools;
import java.util.ArrayList;

public class Turret extends abPlaceable {
    private ifCVLoadout Owner;
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private boolean Clan,
                    isTonnageSet;
    private ArrayList Items = new ArrayList();
    private double MaxTonnage = 0;

    public Turret( ifCVLoadout l, boolean clan) {
        AC.SetISCodes( 'E', 'X', 'X', 'E' );
        AC.SetISDates( 0, 0, false, 3062, 0, 0, false, false );
        AC.SetISFactions( "", "", "FC", "" );
        AC.SetCLCodes( 'E', 'X', 'D', 'C' );
        AC.SetCLDates( 0, 0, false, 2860, 0, 0, false, false );
        AC.SetCLFactions( "", "", "CMN", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Owner = l;
        Clan = clan;
    }

    public String ActualName() {
        return "Turret";
    }

    public String CritName() {
        return "Turret";
    }

    public String LookupName() {
        return "Turret";
    }

    public String ChatName() {
        return "Tur";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Turret";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public void SetClan( boolean b ) {
        Clan = b;
    }

    public boolean IsClan() {
        return Clan;
    }

    @Override
    public int NumCrits() {
        return 0;
    }

    public int NumCVSpaces() {
        return 0;
    }

    @Override
    public double GetTonnage() {
        if ( isTonnageSet )
            return MaxTonnage;
        else 
            return GetSize();
    }

    @Override
    public double GetCost() {
        return GetSize() * 5000.0;
    }

    public double GetOffensiveBV() {
        // we can't really control this one, so we should always call the TC
        // with UseCurOffensiveBV().  Since almost all of the time a 'Mech will
        // mount it's weapons forward, we'll call it without using rear.
        return GetCurOffensiveBV( false, false, false );
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // BV calculations for the targeting computer are based on modified
        // weapon BV, which we won't know until later.
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        double retval = 0.0;
        if( IsArmored() ) {
            retval = 5.0 * NumCrits();
        }
        return retval;
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    private double GetSize() {
        double Build = 0.0;

        if ( isTonnageSet ) 
            return MaxTonnage;
        
        if( Items.isEmpty() ) {
            return 0;
        }

        for( int i = 0; i < Items.size(); i++ ) {
            abPlaceable a = (abPlaceable)Items.get(i);
            if( a instanceof RangedWeapon ) {
                if( ((RangedWeapon) a).IsUsingCapacitor() ) {
                    Build += a.GetTonnage() - ((RangedWeapon) a).GetCapacitor().GetTonnage();
                } else if( ((RangedWeapon) a).IsUsingInsulator() ) {
                    Build += a.GetTonnage() - ((RangedWeapon) a).GetInsulator().GetTonnage();
                } else {
                    Build += a.GetTonnage();
                }
            } else if ( !(a instanceof Ammunition) ) {
                Build += a.GetTonnage();
            }
            if( a.IsArmored() ) {
                Build -= a.NumCrits() * 0.5;
            }
        }

        return CommonTools.RoundHalfUp( Build * 0.10 );
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public String toString() {
        return CritName();
    }
    
    public void SetItems( ArrayList a ) {
        Items = a;
    }
    
    public ArrayList GetItems() {
        return Items;
    }
    
    public void TonnageSet( boolean i ) {
        isTonnageSet = i;
        if (!i)
            MaxTonnage = 0;
    }
    public void SetTonnage( double t ) {
        MaxTonnage = t;
        isTonnageSet = true;
    }
    public boolean isTonnageSet() {
        return isTonnageSet;
    }
    
    public double GetMaxTonnage() {
        return MaxTonnage;
    }
}
