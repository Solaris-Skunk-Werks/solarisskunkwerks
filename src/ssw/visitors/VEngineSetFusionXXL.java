/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.visitors;

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
    }
}
