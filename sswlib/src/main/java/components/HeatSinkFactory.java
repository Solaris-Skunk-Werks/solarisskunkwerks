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

import java.util.LinkedList;
import states.*;

public class HeatSinkFactory {
    // provides a means of getting heatsinks into the loadout easily.
    private int NumHS,
                BaseLoadoutNumHS = 0;
    private ifMechLoadout Owner;
    private LinkedList CurrentSinks = new LinkedList();
    private static ifHeatSinkFactory SHS = new stHeatSinkSingle(),
                                     ISDHS = new stHeatSinkISDHS(),
                                     CLDHS = new stHeatSinkCLDHS(),
                                     ISCOM = new stHeatSinkISCompact(),
                                     CLLAS = new stHeatSinkCLLaser();
    private ifHeatSinkFactory CurConfig = SHS;

    public HeatSinkFactory( ifMechLoadout l ) {
        // the basic constructor
        Owner = l;
        NumHS = Owner.GetMech().GetEngine().FreeHeatSinks();

        // now we have to add heat sinks to the currentsinks if they can't all
        // fit internal to the engine.
        int i = NumHS - InternalHeatSinks();
        if( i > 0 ) {
            for( ; i > 0; i-- ) {
                HeatSink h = (HeatSink) GetHeatSink();
                h.Place( l );
            }
        }
    }

    public HeatSinkFactory( ifMechLoadout l, int BaseNumHS, ifHeatSinkFactory type, HeatSink[] PlacedHeatSinks ) {
        // this constructor is used for cloning purposes.  since we should only
        // be doing this when we have an omnimech, recalculation is not needed
        Owner = l;
        NumHS = BaseNumHS;
        CurConfig = type;
        BaseLoadoutNumHS = NumHS;
        for( int i = 0; i < PlacedHeatSinks.length; i++ ) {
            CurrentSinks.add( PlacedHeatSinks[i] );
        }
    }

    public void SetSingle() {
        CurConfig = SHS;
        Owner.GetMech().SetChanged( true );
    }

    public void SetClanDHS() {
        CurConfig = CLDHS;
        Owner.GetMech().SetChanged( true );
    }

    public void SetISDHS() {
        CurConfig = ISDHS;
        Owner.GetMech().SetChanged( true );
    }

    public void SetISCompact() {
        CurConfig = ISCOM;
        Owner.GetMech().SetChanged( true );
    }

    public void SetCLLaser() {
        CurConfig = CLLAS;
        Owner.GetMech().SetChanged( true );
    }

    public boolean IsDouble() {
        return CurConfig.IsDouble();
    }

    public boolean IsCompact() {
        return CurConfig.IsCompact();
    }

    public boolean IsLaser() {
        return CurConfig.IsLaser();
    }

    public int GetTechBase() {
        return CurConfig.GetAvailability().GetTechBase();
    }

    public ifState GetCurrentState() {
        return (ifState) CurConfig;
    }

    public int GetNumHS() {
        return NumHS;
    }

    public int GetBaseLoadoutNumHS() {
        return BaseLoadoutNumHS;
    }

    public void SetBaseLoadoutNumHS( int h ) {
        BaseLoadoutNumHS = h;
    }

    public ifHeatSinkFactory CurrentConfig() {
        return CurConfig;
    }

    public String LookupName() {
        return ((ifState) CurConfig).LookupName();
    }

    public String ChatName() {
        return CurConfig.ChatName();
    }

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) SHS, (ifState) ISDHS, (ifState) CLDHS,
                             (ifState) ISCOM, (ifState) CLLAS };
        return retval;
    }

    public void IncrementNumHS() {
        // do we need a new heat sink?
        if( NeedNewHS() ) {
            HeatSink h = (HeatSink) GetHeatSink();
            h.Place( Owner );
        } else {
            if( IsCompact() ) {
                HeatSink h = FindOpenCompact();
                if( h == null ) {
                    // shouldn't happen, we'll have to figuure out what went wrong
                } else {
                    h.SetNumHS( 2 );
                }
            }
        }
        NumHS++;
        Owner.GetMech().SetChanged( true );
    }

    public boolean DecrementNumHS() {
        HeatSink h;

        // Can we actually remove heatsinks?
        if( NumHS <= Owner.GetMech().GetEngine().FreeHeatSinks() ) {
            return false;
        }

        if( CurrentSinks.size() > 0 ) {
            if( IsCompact() ) {
                h = FindOpenCompact();
                if( h == null ) {
                    // reduce the last Current Sink
                    ((HeatSink) CurrentSinks.getLast()).SetNumHS( 1 );
                } else {
                    h.Remove( Owner );
                    CurrentSinks.remove( h );
                }
            } else {
                h = (HeatSink) CurrentSinks.getLast();
                h.Remove( Owner );
                CurrentSinks.remove( h );
            }
        }

        NumHS--;
        Owner.GetMech().SetChanged( true );
        return true;
    }

    public void SetNumHS( int hs ) {
        // sets the number of heat sinks to the specified number.  Only really
        // used when loading a mech.  First, blow out all the sinks.
        NumHS = 0;
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            Owner.Remove( (abPlaceable) CurrentSinks.get( i ) );
        }
        CurrentSinks.clear();

        // get some required information
        int BaseHS = Owner.GetMech().GetEngine().FreeHeatSinks();
        if( BaseHS > hs ) { hs = BaseHS; }

        // now get all the heat sinks we need
        for( int i = 0; i < hs; i++ ) {
            IncrementNumHS();
        }
    }

    public int TotalDissipation() {
        if( Owner.GetMech().UsingPartialWing() ) {
            return NumHS * CurConfig.GetDissipation() + 3;
        } else {
            return NumHS * CurConfig.GetDissipation();
        }
    }

    public double GetTonnage() {
        // returns the total tonnage of heat sinks we have.
        double tons = ( NumHS - Owner.GetMech().GetEngine().FreeHeatSinks() ) * CurConfig.GetTonnage();
        if( tons <= 0.0 ) { 
            // we're still initializing or even.  return a 0 answer.
            tons =  0.0;
        }
        // now check for armored sinks and add that to the result
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            tons += ((HeatSink) CurrentSinks.get( i )).GetTonnage();
        }
        return tons;
    }

    public double GetLoadoutTonnage() {
        // returns the tonnage of heatsinks over the base loadout.
        double tons = ( NumHS - BaseLoadoutNumHS ) * CurConfig.GetTonnage();
        if( tons <= 0.0 ) { 
            // we're still initializing or even.  return a 0 answer.
            tons =  0.0;
        }
        // now check for armored sinks and add that to the result
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            tons += ((HeatSink) CurrentSinks.get( i )).GetTonnage();
        }
        return tons;
    }

    public double GetOmniTonnage() {
        // returns the tonnage used by the particular omni loadout
        return ( NumHS - BaseLoadoutNumHS ) * CurConfig.GetTonnage();
    }

    public double GetCost() {
        // returns the cost of these heat sinks
        double CostHS;
        if( CurConfig.IsDouble() || CurConfig.IsLaser() || CurConfig.IsCompact() ) {
            CostHS = NumHS * CurConfig.GetCost();
        } else {
            if( Owner.GetMech().GetEngine().IsFusion() ) {
                // free single fusion sinks cost nothing
                CostHS = ( NumHS - Owner.GetMech().GetEngine().FreeHeatSinks() ) * CurConfig.GetCost();
            } else {
                CostHS = NumHS * CurConfig.GetCost();
            }
        }
        // now check for armored sinks and add that to the result
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            CostHS += ((HeatSink) CurrentSinks.get( i )).GetCost();
        }
        return CostHS;
    }

    public abPlaceable GetHeatSink() {
        HeatSink h = CurConfig.GetHeatSink();
        CurrentSinks.add(h);
        return h;
    }

    public int NumCrits() {
        int result = CurrentSinks.size() * CurConfig.GetNumCrits();
        if( result < 0 ) {
            return 0;
        } else {
            return result;
        }
    }

    private boolean NeedNewHS() {
        // this returns whether or not we need to get a new heat sink or whether
        // we can simply increment the number of heatsinks.
        if( Owner.GetMech().IsOmnimech() ) {
            if( IsCompact() ) {
                if( FindOpenCompact() == null ) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            if( NumHS >= InternalHeatSinks() ) {
                // need to get a new sink
                if( IsCompact() ) {
                    if( FindOpenCompact() != null ) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public HeatSink FindOpenCompact() {
        // this routine finds the next available single Compact Heat Sink
        // if it cannot find a single compact, it returns null
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            if( ((HeatSink) CurrentSinks.get( i )).NumHeatSinks() == 1 ) {
                return (HeatSink) CurrentSinks.get( i );
            }
        }
        return null;
    }

    public int InternalHeatSinks() {
        // returns the number of internal heat sinks.  needed because of compacts
        if( IsCompact() ) {
            return Owner.GetMech().GetEngine().InternalHeatSinks() * 2;
        } else {
            return Owner.GetMech().GetEngine().InternalHeatSinks();
        }
    }

    public void ReCalculate() {
        // this routine completely blows out the heat sinks and rebuilds itself
        // needs to be done when the heat sink type or techbase changes.

        // remember how many heat sinks we have.
        int temp = NumHS;
        NumHS = 0;

        if( temp < Owner.GetMech().GetEngine().FreeHeatSinks() ) {
            temp = Owner.GetMech().GetEngine().FreeHeatSinks();
        }

        // blow out all the sinks
        abPlaceable h;
        for( int i = CurrentSinks.size(); i > 0; i-- ) {
            h = (abPlaceable) CurrentSinks.getLast();
            h.Remove( Owner );
            CurrentSinks.remove(h);
        }

        // rebuild the heat sinks
        for( ; temp > 0; temp-- ) {
            // temp -= NumHS 
            IncrementNumHS();
        }
    }

    public HeatSink[] GetPlacedHeatSinks() {
        // returns the currently placed heat sinks in array form
        HeatSink[] retval = new HeatSink[CurrentSinks.size()];
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            retval[i] = (HeatSink) CurrentSinks.get( i );
        }
        return retval;
    }

    public AvailableCode GetAvailability() {
        // returns the appropriate Available Code
        return CurConfig.GetAvailability();
    }

    public double GetOffensiveBV() {
        // convenience method
        double result = 0.0;
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            result += ((HeatSink) CurrentSinks.get( i )).GetOffensiveBV();
        }
        return result;
    }

    public double GetDefensiveBV() {
        // convenience method
        double result = 0.0;
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            result += ((HeatSink) CurrentSinks.get( i )).GetDefensiveBV();
        }
        return result;
    }

    public MechModifier GetMechModifier() {
        return CurConfig.GetMechModifier();
    }

    @Override
    public String toString() {
        return "Heat Sink Factory";
    }
}
