package components;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TargetingComputerExclusionTest {

    @Test
    public void targetingComputerCannotBeAddedAfterNullSignatureSystem() throws Exception {
        Mech mech = new Mech();
        mech.GetLoadout().AddToQueue(mech.GetNullSig());

        assertExclusion(
                () -> mech.GetLoadout().CheckExclusions(mech.GetTC()),
                "Null Signature System");
    }

    @Test
    public void targetingComputerCannotBeAddedAfterVoidSignatureSystem() throws Exception {
        Mech mech = new Mech();
        mech.GetLoadout().AddToQueue(mech.GetVoidSig());

        assertExclusion(
                () -> mech.GetLoadout().CheckExclusions(mech.GetTC()),
                "Void Signature System");
    }

    @Test
    public void nullSignatureSystemCannotBeAddedAfterTargetingComputer() throws Exception {
        Mech mech = new Mech();
        mech.UseTC(true, false);

        assertExclusion(
                () -> mech.GetLoadout().CheckExclusions(mech.GetNullSig()),
                "Targeting Computer");
    }

    @Test
    public void voidSignatureSystemCannotBeAddedAfterTargetingComputer() throws Exception {
        Mech mech = new Mech();
        mech.UseTC(true, false);

        assertExclusion(
                () -> mech.GetLoadout().CheckExclusions(mech.GetVoidSig()),
                "Targeting Computer");
    }

    private void assertExclusion(CheckedAction action, String existingComponent) throws Exception {
        try {
            action.run();
            fail("Expected exclusion for " + existingComponent);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains(existingComponent));
        }
    }

    private interface CheckedAction {
        void run() throws Exception;
    }
}
