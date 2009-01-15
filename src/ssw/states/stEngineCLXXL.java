/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.states;

import ssw.Constants;
import ssw.components.AvailableCode;
import ssw.components.MechModifier;

/**
 *
 * @author justin
 */
public class stEngineCLXXL implements ifEngine, ifState {
    // A Clan XXL Fusion Engine
    private final static AvailableCode AC = new AvailableCode( false, 'F', 'X', 'X', 'F',
        2954, 0, 0, "CDS", "", false, false, 2582, true, "TH", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
    private final static float[] Masses = {0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,
        0.5f,0.5f,0.5f,0.5f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.5f,1.5f,
        1.5f,1.5f,1.5f,1.5f,1.5f,2.0f,2.0f,2.0f,2.0f,2.0f,2.0f,2.0f,2.5f,2.5f,
        2.5f,2.5f,3.0f,3.0f,3.0f,3.0f,3.5f,3.5f,3.5f,3.5f,4.0f,4.0f,4.0f,
        4.5f,4.5f,4.5f,5.0f,5.0f,5.5f,5.5f,5.5f,6.0f,6.0f,6.5f,6.5f,
        7.0f,7.5f,7.5f,8.0f,8.5f,8.5f,9.0f,9.5f,10.0f,10.5f,11.0f,11.5f,
        12.5f,13.0f,14.0f,14.5f,15.5f,16.5f,17.5f};

    public boolean IsClan() {
        return true;
    }
    
    public float GetTonnage( int Rating ) {
        return Masses[GetIndex( Rating )];
    }
    
    public int GetCTCrits() {
        return 3;
    }
    
    public int GetSideTorsoCrits() {
        return 4;
    }
    
    public int NumCTBlocks() {
        return 2;
    }
    
    public boolean CanSupportRating( int rate ) {
        if( rate < 5 || rate > 400 || rate % 5 != 0 ) {
            return false;
        } else {
            return true;
        }
    }

    public String GetLookupName() {
        return "XXL Engine";
    }

    public String GetCritName() {
        return "XXL Engine";
    }

    public String GetMMName() {
        return "Fusion Engine";
    }

    public float GetCost( int MechTonnage, int Rating ) {
        return ( 100000 * MechTonnage * Rating ) / 75;
    }
    
    public AvailableCode GetAvailability() {
        return AC;
    }
    
    public int FreeHeatSinks() {
        return 10;
    }

    public float GetBVMult() {
        return 0.75f;
    }
    
    public boolean IsFusion() {
        return true;
    }

    public boolean IsNuclear() {
        return true;
    }

    public int GetFullCrits() {
        return 14;
    }

    private int GetIndex( int Rating ) {
        return Rating / 5 - 2;
    }

    public int MaxMovementHeat() {
        return 6;
    }

    public int MinimumHeat() {
        return 2;
    }

    public int JumpingHeatMultiplier() {
        return 2;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    @Override
    public String toString() {
        return "XXL Fusion Engine";
    }
}
