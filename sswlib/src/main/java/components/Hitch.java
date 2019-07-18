/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

public class Hitch extends abPlaceable {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public Hitch() {
        AC.SetISCodes( 'B', 'B', 'B', 'B' );
        AC.SetISDates( 0, 0, false, 2000, 0, 0, false, false );
        AC.SetISFactions( "", "", "CMN", "" );
        AC.SetCLCodes( 'B', 'B', 'B', 'B' );
        AC.SetCLDates( 0, 0, false, 2000, 0, 0, false, false );
        AC.SetCLFactions( "", "", "CMN", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }
    
    @Override
    public String ActualName() {
        return "Hitch";
    }

    @Override
    public String LookupName() {
        return "Hitch";
    }

    @Override
    public String CritName() {
        return "Hitch";
    }

    @Override
    public String ChatName() {
        return "Hitch";
    }

    @Override
    public String MegaMekName(boolean UseRear) {
        return "Hitch";
    }

    @Override
    public String BookReference() {
        return "Tech Manual";
    }

    @Override
    public int NumCrits() {
        return 0;
    }

    @Override
    public int NumCVSpaces() {
        return 1;
    }

    @Override
    public double GetTonnage() {
        return 0;
    }

    @Override
    public double GetCost() {
        return 0;
    }

    @Override
    public double GetOffensiveBV() {
        return 0;
    }

    @Override
    public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES) {
        return 0;
    }

    @Override
    public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic) {
        return 0;
    }

    @Override
    public double GetDefensiveBV() {
        return 0;
    }

    @Override
    public AvailableCode GetAvailability() {
        return AC;
    }

    @Override
    public boolean CanAllocCVBody() {
        return false;
    }

    @Override
    public boolean CanAllocCVFront() {
        return true;
    }

    @Override
    public boolean CanAllocCVSide() {
        return false;
    }

    @Override
    public boolean CanAllocCVRear() {
        return true;
    }

    @Override
    public boolean CanAllocCVTurret() {
        return false;
    }
    
    
    
}
