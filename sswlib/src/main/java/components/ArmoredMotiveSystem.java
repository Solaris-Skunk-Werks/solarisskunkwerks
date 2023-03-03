package components;

import common.CommonTools;
import common.EquipmentFactory;

public class ArmoredMotiveSystem extends Equipment {
    private ifCVLoadout Owner;
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private boolean IsClan = false;
    public ArmoredMotiveSystem(ifCVLoadout l) {
        AC.SetISCodes('E', 'X', 'X', 'F', 'E');
        AC.SetISDates( 3069, 3071, true, 0, 0, 0, false, false );
        AC.SetISFactions( "FWL", "FWL", "", "" );
        AC.SetCLCodes( 'F', 'X', 'X', 'F', 'E' );
        AC.SetCLDates( 3054, 3057, true, 0, 0, 0, false, false );
        AC.SetCLFactions( "CHH", "CHH", "", "" );
        AC.SetPBMAllowed( false );
        AC.SetPIMAllowed( false );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        Owner = l;
    }
    @Override
    public String ActualName() {
        return "Armored Motive System";
    }

    @Override
    public String LookupName() {
        return "Armored Motive System";
    }

    @Override
    public String CritName() {
        return "Armored Motive System";
    }

    @Override
    public String ChatName() {
        return "ArmMotSys";
    }

    @Override
    public String MegaMekName(boolean UseRear) {
        return (IsClan ? "CL" : "IS") + "ArmoredMotiveSystem";
    }

    @Override
    public String BookReference() {
        return "Tactical Operations";
    }

    @Override
    public int NumCrits() {
        return 0;
    }

    @Override
    public int NumCVSpaces() {
        return 0;
    }

    @Override
    public double GetTonnage() {
        return CommonTools.RoundHalfUp(Owner.GetOwner().GetTonnage() * (IsClan ? .1 : .15));
    }

    @Override
    public double GetCost() {
        return 100000 * GetTonnage();
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

    public void SetClan(boolean b ) {
        IsClan = b;
    }

    public boolean IsClan() { return IsClan; }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public String GetEquipmentType() {
        return "Armmored Motive System";
    }
}
