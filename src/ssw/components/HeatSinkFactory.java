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

package ssw.components;

import java.util.LinkedList;
import ssw.states.*;

public class HeatSinkFactory {
    // provides a means of getting heatsinks into the loadout easily.
    private int NumHS,
                BaseLoadoutNumHS = 0;
    private ifLoadout Owner;
    private LinkedList CurrentSinks = new LinkedList();
    private static ifHeatSinkFactory ISSHS = new stHeatSinkISSHS(),
                                     ISDHS = new stHeatSinkISDHS(),
                                     CLSHS = new stHeatSinkCLSHS(),
                                     CLDHS = new stHeatSinkCLDHS();
    private ifHeatSinkFactory CurConfig = ISSHS;

    public HeatSinkFactory( ifLoadout l ) {
        // the basic constructor
        Owner = l;
        NumHS = Owner.GetMech().GetEngine().FreeHeatSinks();

        // now we have to add heat sinks to the currentsinks if they can't all
        // fit internal to the engine.
        int i = NumHS - Owner.GetMech().GetEngine().InternalHeatSinks();
        if( i > 0 ) {
            for( ; i > 0; i-- ) {
                HeatSink h = (HeatSink) GetHeatSink();
                h.Place( l );
            }
        }
    }

    public HeatSinkFactory( ifLoadout l, int BaseNumHS, ifHeatSinkFactory type, HeatSink[] PlacedHeatSinks ) {
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

    public void SetInnerSphere() {
        if( CurConfig.IsDouble() ) {
            CurConfig = ISDHS;
        } else {
            CurConfig = ISSHS;
        }
        Owner.GetMech().SetChanged( true );
    }

    public void SetClan() {
        if( CurConfig.IsDouble() ) {
            CurConfig = CLDHS;
        } else {
            CurConfig = CLSHS;
        }
        Owner.GetMech().SetChanged( true );
    }

    public void SetSingle() {
        if( CurConfig.IsClan() ) {
            CurConfig = CLSHS;
        } else {
            CurConfig = ISSHS;
        }
        Owner.GetMech().SetChanged( true );
    }

    public void SetDouble() {
        if( CurConfig.IsClan() ) {
            CurConfig = CLDHS;
        } else {
            CurConfig = ISDHS;
        }
        Owner.GetMech().SetChanged( true );
    }

    public void SetISDouble() {
        CurConfig = ISDHS;
        Owner.GetMech().SetChanged( true );
    }

    public void SetISSingle() {
        CurConfig = ISSHS;
        Owner.GetMech().SetChanged( true );
    }

    public void SetCLDouble() {
        CurConfig = CLDHS;
        Owner.GetMech().SetChanged( true );
    }

    public void SetCLSingle() {
        CurConfig = CLSHS;
        Owner.GetMech().SetChanged( true );
    }

    public boolean IsDouble() {
        return CurConfig.IsDouble();
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

    public String GetLookupName() {
        return ((ifState) CurConfig).GetLookupName();
    }

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) ISSHS, (ifState) ISDHS, (ifState) CLSHS, (ifState) CLDHS };
        return retval;
    }

    public void IncrementNumHS() {
        // do we need a new heat sink?
        if( NeedNewHS() ) {
            HeatSink h = (HeatSink) GetHeatSink();
            h.Place( Owner );
            NumHS++;
        } else {
            //  nope, just increment the number
            NumHS++;
        }
        Owner.GetMech().SetChanged( true );
    }

    public boolean DecrementNumHS() {
        HeatSink h;

        // Can we actually remove heatsinks?
        if( NumHS <= Owner.GetMech().GetEngine().FreeHeatSinks() ) {
            return false;
        }

        if( CurrentSinks.size() > 0 ) {
            h = (HeatSink) CurrentSinks.getLast();
            h.Remove( Owner );
            CurrentSinks.remove( h );
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
        return NumHS * CurConfig.GetDissipation();
    }

    public float GetTonnage() {
        // returns the total tonnage of heat sinks we have.
        float tons = ( NumHS - Owner.GetMech().GetEngine().FreeHeatSinks() ) * CurConfig.GetTonnage();
        if( tons <= 0.0f ) { 
            // we're still initializing or even.  return a 0 answer.
            tons =  0.0f;
        }
        // now check for armored sinks and add that to the result
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            tons += ((HeatSink) CurrentSinks.get( i )).GetTonnage();
        }
        return tons;
    }

    public float GetLoadoutTonnage() {
        // returns the tonnage of heatsinks over the base loadout.
        float tons = ( NumHS - BaseLoadoutNumHS ) * CurConfig.GetTonnage();
        if( tons <= 0.0f ) { 
            // we're still initializing or even.  return a 0 answer.
            tons =  0.0f;
        }
        // now check for armored sinks and add that to the result
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            tons += ((HeatSink) CurrentSinks.get( i )).GetTonnage();
        }
        return tons;
    }

    public float GetOmniTonnage() {
        // returns the tonnage used by the particular omni loadout
        return ( NumHS - BaseLoadoutNumHS ) * CurConfig.GetTonnage();
    }

    public float GetCost() {
        // returns the cost of these heat sinks
        float CostHS;
        if( CurConfig.IsDouble() ) {
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
            // Locked omnimechs always require new heatsinks.
            return true;
        } else {
            if( NumHS >= Owner.GetMech().GetEngine().InternalHeatSinks() ) {
                // need to get a new sink
                return true;
            } else {
                return false;
            }
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

    public float GetOffensiveBV() {
        // convenience method
        float result = 0.0f;
        for( int i = 0; i < CurrentSinks.size(); i++ ) {
            result += ((HeatSink) CurrentSinks.get( i )).GetOffensiveBV();
        }
        return result;
    }

    public float GetDefensiveBV() {
        // convenience method
        float result = 0.0f;
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
