/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.components;

/**
 *
 * @author Michael Mills
 */
public class IndustrialPhysicalWeapon extends PhysicalWeapon {
    public IndustrialPhysicalWeapon (String name, String lookup, Mech m, AvailableCode a){
        super(name, lookup, m, a);
        this.resetAllocations(m);
        this.SetReplacesHand(true);
        this.SetRequiresLowerArm(true);
        this.SetPWClass(ssw.Constants.PW_CLASS_INDUSTRIAL);
    }

    public void resetAllocations(Mech m)
    {
        if(m.IsQuad()){
            this.SetAllocations(false, false, true, false, false, false);
            this.SetReplacesHand(false);
            this.SetRequiresLowerArm(false);
        }
        else{
            this.SetAllocations(false, false, false, true, false, false);
            this.SetReplacesHand(true);
            this.SetRequiresLowerArm(true);
        }
    }

    public void SetSpecials(int cost, int obv, int dbv)
    {
        this.SetSpecials("PA", "-", 0, cost, 0, obv, dbv, false);
    }
}
