/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB;

import components.Mech;
import filehandlers.MechReader;

public class MechUnit extends abUnit {

    public void LoadDesign() {
        if ( design == null ) {
            try {
                MechReader reader = new MechReader();
                this.design = reader.ReadMech( this.Filename );
                if ( ! this.Configuration.isEmpty() ) {
                    ((Mech) this.design).SetCurLoadout(this.Configuration.trim());
                }
            } catch (Exception ex) {
                //do nothing
            }
        }
    }

    public void UpdateByDesign() {
        TypeModel = ((Mech) design).GetFullName();
        Configuration = ((Mech) design).GetLoadout().GetName();
        BaseBV = ((Mech) design).GetCurrentBV();
        Refresh();
    }

}
