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

public class MultiSlotSystem extends abPlaceable {
    // this class covers most systems that take one critical slot in each location
    // stuff like the Blue Shield, Null Signature System, etc...

    private AvailableCode AC;
    private double Tonnage,
                  Cost = 0.0;
    private int DefensiveBonus = 0;
    private boolean ExcludeCT,
                    ExcludeHD,
                    CostTons,
                    BasedOnMechTons = false;
    private String ActualName,
                   CritName,
                   LookupName,
                   MegaMekName,
                   ChatName ="",
                   BookReference ="";
    protected ifUnit Owner;

    public MultiSlotSystem( Mech owner, String actualname, String lookupname, String critname, String mname, double tons, boolean xct, boolean xhd, double cost, boolean costtons, AvailableCode a ) {
        Owner = owner;
        ActualName = actualname;
        CritName = critname;
        MegaMekName = mname;
        LookupName = lookupname;
        Tonnage = tons;
        ExcludeCT = xct;
        ExcludeHD = xhd;
        Cost = cost;
        CostTons = costtons;
        AC = a;
    }
    public MultiSlotSystem( CombatVehicle owner, String actualname, String lookupname, String critname, String mname, double tons, boolean xct, boolean xhd, double cost, boolean costtons, AvailableCode a ) {
        Owner = owner;
        ActualName = actualname;
        CritName = critname;
        MegaMekName = mname;
        LookupName = lookupname;
        Tonnage = tons;
        ExcludeCT = xct;
        ExcludeHD = xhd;
        Cost = cost;
        CostTons = costtons;
        AC = a;
    }
    
    public void SetChatName( String s ) {
        ChatName = s;
    }

    public void SetBookReference( String s ) {
        BookReference = s;
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
        // Multi Slot Systems should only ever be one crit in each location
        return 1;
    }

    public int NumCVSpaces() {
        return 0;
    }

    public int ReportCrits() {
        if( ExcludeCT ) {
            if( ExcludeHD ) {
                return 6;
            } else {
                return 7;
            }
        } else {
            if( ExcludeHD ) {
                return 7;
            } else {
                return 8;
            }
        }
    }

    public void SetWeightBasedOnMechTonnage( boolean b ) {
        BasedOnMechTons = b;
    }

    @Override
    public double GetTonnage() {
        double retval = 0.0;
        if( BasedOnMechTons ) {
            retval += Owner.GetTonnage() * Tonnage;
        } else {
            retval += Tonnage;
        }
        if( IsArmored() ) {
            retval += 0.5 * ReportCrits();
        }
        return retval;
    }

    @Override
    public double GetCost() {
        double retval = 0.0;
        if( IsArmored() ) {
            retval += 150000.0 * ReportCrits();
        }
        if( CostTons ) {
            retval += Owner.GetTonnage() * Cost;
        } else {
            retval += Cost;
        }
        return retval;
    }

    @Override
    public double GetOffensiveBV() {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    @Override
    public double GetDefensiveBV() {
        double retval = 0.0;
        if( IsArmored() ) {
            retval += 5.0 * ReportCrits();
        }
        return retval;
    }

    public int GetDefensiveBonus() {
        // for a system that does not have a defensive BV, this provides the
        // defensive factor modifier
        return DefensiveBonus;
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public boolean Place( ifMechLoadout l ) {
        // Place the system in the mech
        boolean placed = false;
        int increment;
        // quads have less space in the front legs, hence the check
        try {
            if( l.IsQuad() ) {
                increment = 5;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToLA( this, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
                placed = false;
                increment = 5;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToRA( this, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
            } else {
                increment = 11;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToLA( this, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
                placed = false;
                increment = 11;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToRA( this, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
            }

            // check each available space from the bottom.  If we cannot allocate
            // then we can't load the item
            placed = false;
            increment = 11;
            while( placed == false ) {
                if ( increment < 0 ) { return false; }
                try {
                    l.AddToLT( this, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
            placed = false;
            increment = 11;
            while( placed == false ) {
                if ( increment < 0 ) { return false; }
                try {
                    l.AddToRT( this, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
            // do we need to allocate to the center torso?
            if( ! ExcludeCT ) {
                placed = false;
                increment = 11;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToCT( this, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
            }
            // do we need to allocate to the head?
            if( ! ExcludeHD ) {
                placed = false;
                increment = 5;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToHD( this, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
            }
            // allocate to the (rear) legs.
            placed = false;
            increment = 5;
            while( placed == false ) {
                if ( increment < 0 ) { return false; }
                try {
                    l.AddToLL( this, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
            placed = false;
            increment = 5;
            while( placed == false ) {
                if ( increment < 0 ) { return false; }
                try {
                    l.AddToRL( this, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } catch ( Exception e ) {
            // something else was probably in the way.  Tell the placer
            return false;
        }

        // all went well
        return true;
    }

    @Override
    public boolean Place( ifMechLoadout l, LocationIndex[] Locs ) {
        LocationIndex li;
        try {
            for( int i = 0; i < Locs.length; i++ ) {
                li = (LocationIndex) Locs[i];
                l.AddTo( this, li.Location, li.Index );
            }
        } catch( Exception e ) {
            return false;
        }
        return true;
    }

    @Override
    public boolean Contiguous() {
        return false;
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