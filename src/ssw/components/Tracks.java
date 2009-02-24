/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.components;

import ssw.Constants;

/**
 *
 * @author Michael Mills
 */
public class Tracks extends MultiSlotSystem{
    private AvailableCode ISAC = new AvailableCode (false, 'C', 'D', 'E', 'E', 2400, 0, 0, "DC", "", false, false, 0, false, "", Constants.TOURNAMENT, Constants.TOURNAMENT);
    private AvailableCode CLAC = new AvailableCode (true, 'C', 'X', 'E', 'E', 2400, 0, 0, "DC", "", false, false, 0, false, "", Constants.TOURNAMENT, Constants.TOURNAMENT);

    public Tracks(Mech m, AvailableCode a){
        super (m, "Tracks", "Tracks", 0.1f, true, true, 0.0f, false, a);
        this.SetWeightBasedOnMechTonnage(true);
    }

    @Override
    public int ReportCrits(){
        if (Owner.IsQuad())
            return 4;
        return 2;
    }

    @Override
    public float GetCost() {
        return 500 * Owner.GetTonnage() * Owner.GetEngine().GetRating() / 75;
    }

    @Override
    public AvailableCode GetAvailability() {
        if( Owner.IsClan() ) {
            return CLAC;
        } else {
            return ISAC;
        }
    }

    @Override
    public boolean Place( ifLoadout l ) {
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

}
