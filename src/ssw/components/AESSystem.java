/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.components;

import ssw.Constants;

/**
 *
 * @author justin
 */
public class AESSystem extends abPlaceable {
    AvailableCode ISAC = new AvailableCode( false, 'E', 'X', 'X', 'F', 3070, 0, 0, "KH", "", false, false, 3067, true, "BC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
    AvailableCode CLAC = new AvailableCode( false, 'E', 'X', 'X', 'F', 3070, 0, 0, "WD", "", false, false, 3067, true, "WD", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
    private Mech Owner;
    private boolean LegSystem;
    private static MechModifier LegMod = new MechModifier( 0, 0, 0, 0, 0, -2, 0, 0.0f, 0.0f, 0.0f, 0.0f, false );

    public AESSystem( Mech m, boolean legs ) {
        Owner = m;
        LegSystem = legs;
        SetExclusions( new Exclusion( new String[] { "Targeting Computer", "MASC", "TSM" }, "A.E.S." ) );
    }

    @Override
    public String GetCritName() {
        return "A.E.S.";
    }

    @Override
    public String GetMMName(boolean UseRear) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int NumCrits() {
        int MechTons = Owner.GetTonnage();
        if( MechTons < 40 ) {
            return 1;
        } else if( MechTons > 35 && MechTons < 60 ) {
            return 2;
        } else if( MechTons > 55 && MechTons < 80 ) {
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public float GetTonnage() {
        float retval = 0.0f;
        if( IsArmored() ) {
            retval += NumCrits() * 0.5f;
        }
        if( Owner.IsQuad() ) {
            retval += ((int) ( Math.ceil( Owner.GetTonnage() * 0.02f * 2.0f ))) * 0.5f;
        } else {
            retval += ((int) ( Math.ceil( Owner.GetTonnage() * 0.02857f * 2.0f ))) * 0.5f;
        }
        return retval;
    }

    @Override
    public float GetCost() {
        float retval = 0.0f;
        if( IsArmored() ) {
            retval += NumCrits() * 150000.0f;
        }
        if( LegSystem ) {
            retval += Owner.GetTonnage() * 700.0f;
        } else {
            retval += Owner.GetTonnage() * 500.0f;
        }
        return retval;
    }

    @Override
    public float GetOffensiveBV() {
        // AES modifies BV, but doesn't have one of its own
        return 0.0f;
    }

    @Override
    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // AES modifies BV, but doesn't have one of its own
        return 0.0f;
    }

    @Override
    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0f * NumCrits();
        } else {
            return 0.0f;
        }
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public void AddMechModifier(MechModifier m) {
        // do nothing here, we provide our own.
    }

    @Override
    public MechModifier GetMechModifier() {
        if( LegSystem ) {
            return LegMod;
        } else {
            return null;
        }
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public AvailableCode GetAvailability() {
        if( Owner.IsClan() ) {
            return CLAC;
        } else {
            return ISAC;
        }
    }
}