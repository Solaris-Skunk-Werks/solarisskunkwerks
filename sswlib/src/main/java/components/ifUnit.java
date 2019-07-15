/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package components;

public interface ifUnit {
    public int GetUnitType();
    public int GetRulesLevel();
    public int GetTechbase();
    public int GetBaseTechbase();
    public int GetEra();
    public int GetProductionEra();
    public int GetYear();
    public int GetTonnage();
    public boolean IsYearRestricted();
    public boolean UsingFractionalAccounting();
    public void SetChanged( boolean b );
    public boolean HasFHES();
    public int GetTechBase();
    public Engine GetEngine();
    public boolean IsQuad();
    public boolean IsTripod();
    public MechModifier GetTotalModifiers( boolean BV, boolean MASCTSM );
    public PhysicalEnhancement GetPhysEnhance();
    public boolean UsingTC();
    public TargetingComputer GetTC();
}
