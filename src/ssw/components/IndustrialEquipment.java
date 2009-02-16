/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.components;

/**
 *
 * @author Michael Mills
 */
public class IndustrialEquipment extends Equipment{
    private EquipmentValidationInterface validator;
    private String validationFalseMessage;
    public IndustrialEquipment (String name, String t, AvailableCode a, boolean c, EquipmentValidationInterface validator, String vf){
        super(name,t,a,c);
        this.validator = validator;
        validationFalseMessage = vf;
    }

    public EquipmentValidationInterface getValidator(){
            return validator;
    }
    
    public boolean validate (Mech m){
            return validator.validate(m);
    }

    public String getValidationFalseMessage(){
            return validationFalseMessage;
    }
}
