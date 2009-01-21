/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.visitors;

import ssw.Constants;
import ssw.components.*;

/**
 *
 * @author justin
 */
public class VEngineSetFusionXXL implements ifVisitor {
    private Mech CurMech;

    public void LoadLocations( LocationIndex[] locs ) {
        // does nothing here, but may later.
    }

    public void Visit( Mech m ) {
        // only the engine changes here, so pass us off to the engine
        CurMech = m;
        ifLoadout l = CurMech.GetLoadout();
        Engine e = CurMech.GetEngine();
        boolean SChargerInstalled = false;
        int SChargerLoc = -1;

        // see if we have a supercharger installed
        if( l.HasSupercharger() ) {
            SChargerInstalled = true;
            SChargerLoc = l.Find( l.GetSupercharger() );
            try {
                CurMech.GetLoadout().SetSupercharger( false, -1, -1 );
            } catch ( Exception ex ) {
                // wow, a problem removing it.  Log it for later.
                System.err.println( ex.getMessage() );
            }
        }

        // remove the engine
        e.Remove( l );

        // change the engine type
        if( CurMech.IsClan() ) {
            e.SetCLXXLEngine();
        } else {
            e.SetISXXLEngine();
        }

        // place the engine
        e.Place( l );

        // try to reinstall the Supercharger
        if( SChargerInstalled ) {
            try {
                // we're not interested in where the suypercharger was since it
                // can only go in the same spot as an engine.
                CurMech.GetLoadout().SetSupercharger( true, Constants.LOC_CT, SChargerLoc );
            } catch ( Exception ex ) {
                System.err.println( ex.getMessage() );
            }
        }
    }
}
