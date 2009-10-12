/*
Copyright (c) 2008~2009, Justin R. Bengtson (poopshotgun@yahoo.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
    * Neither the name of Justin R. Bengtson nor the names of contributors may
        be used to endorse or promote products derived from this software
        without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ssw.components;

public abstract class abPlaceable {
    // An abstract class for items that can be placed inside a loadout.
    private boolean Locked = false,  Armored = false;
    private Exclusion Exclusions = null;
    public final static AvailableCode ArmoredAC = new AvailableCode( AvailableCode.TECH_BOTH );
    private MechModifier Modifier = null;
    private String[] BattleForceAbilities = new String[]{};

    public abPlaceable() {
        ArmoredAC.SetISCodes( 'E', 'X', 'X', 'F' );
        ArmoredAC.SetISDates( 3059, 3061, true, 3061, 0, 0, false, false );
        ArmoredAC.SetISFactions( "FW", "FW", "", "" );
        ArmoredAC.SetCLCodes( 'E', 'X', 'X', 'F' );
        ArmoredAC.SetCLDates( 3060, 3061, true, 3061, 0, 0, false, false );
        ArmoredAC.SetCLFactions( "CDS", "CDS", "", "" );
        ArmoredAC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean Place(ifLoadout l) {
        // By default, the item will be placed in the Loadout's unplaced area.
        // Items that override this can be creative with their placement.
        // This method is used when the item places itself (usually when
        // initially added to the layout).
        l.AddToQueue(this);
        return true;
    }

    public boolean Place( ifLoadout l, LocationIndex[] locs ) {
        // this method is provided for those Placeables that place themselves.
        // should only be overridden if needed.
        return Place( l );
    }

    public void Remove(ifLoadout l) {
        // Removes itself from the specified Loadout.  This method will rarely
        // be overridden.
        l.Remove(this);
    }

    // The following methods determine where this item can be placed.  When the
    // loadout attempts to add them to a location, it will check the specified
    // routine.  Defaults are assumed and can be overidden.
    public boolean CanAllocHD() {
        return true;
    }

    public boolean CanAllocCT() {
        return true;
    }

    public boolean CanAllocTorso() {
        return true;
    }

    public boolean CanAllocArms() {
        return true;
    }

    public boolean CanAllocLegs() {
        return true;
    }

    // This method determines whether the item can be split between adjacent
    // locations.  The CanAlloc methods will determine where the item can be
    // split to.
    public boolean CanSplit() {
        return false;
    }

    // This method tells us whether the item must be placed contiguously.  Some
    // items (like Endo-Steel) have a lot of crits but are not contiguous, so
    // the user must be allowed to split them between locations as they see fit.
    // The loadout will handle how many are alloc'd and how many not.
    // The default is contiguous.  Most items, even if they can be split, must
    // be alloc'd contiguously.
    public boolean Contiguous() {
        return true;
    }

    // This method tells us whether the item is locked in it's current location.
    // Useful for stuff like engines and gyros.
    public boolean LocationLocked() {
        return Locked;
    }

    public void SetLocked(boolean l) {
        Locked = l;
    }

    // this method is for items that are linked to a particular location for
    // some reason, such as Artemis IY FCS that must be in the same location
    // as the launcher.
    public boolean LocationLinked() {
        return false;
    }

    // actual name is the Battletech name for the equipment, from the books
    // we use other names elsewhere because this can get extremely long.
    public abstract String ActualName();

    // the lookup name is used when we are trying to find the piece of equipment.
    public abstract String LookupName();

    // the crit name is how the item appears in the loadout when allocated.
    public abstract String CritName();

    // the name to be used when expoerting this equipment to a chat line.
    public abstract String ChatName();

    // the name to be used when exporting to MegaMek
    public abstract String MegaMekName( boolean UseRear );

    // reference for the book that the equipment comes from 
    public abstract String BookReference();

    // returns the number of crits this item takes in the Loadout.
    public abstract int NumCrits();

    // returns the mass of the component
    public abstract double GetTonnage();

    // return the cost of the item
    public abstract double GetCost();

    // return the offensive battle value of the item
    public abstract double GetOffensiveBV();

    // return the current offensive battle value of the item.  This is useful
    // for weapons since they may be mounted to the rear.
    public abstract double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES );

    // return the defensive battle value of the item
    public abstract double GetDefensiveBV();

    // placement counter for non-contiguous items.  This method should only be
    // over-ridden by items that are non-contiguous.
    public int NumPlaced() {
        return 0;
    }

    // increments the number of times this item has been placed.  Once again,
    // should only be over-ridden by non-contiguous items
    public void IncrementPlaced() {
    }

    // decrements the number of times this item has been placed.  Only override
    // for non-contiguous items
    public void DecrementPlaced() {
    }

    // resets the placed counter.  only override for non-contiguous items
    public void ResetPlaced() {
    }

    // tells us whether the item can be mounted to the rear.  Most items cannot,
    // so this automatically returns false
    public boolean CanMountRear() {
        return false;
    }

    // This next two methods should be overridden by any component that can be
    // mounted to the rear.
    public void MountRear(boolean rear) {
    }

    public boolean IsMountedRear() {
        return false;
    }

    // tells us if this is a core component.  A special list is kept in the
    // loadout for items not of this type and calculations based on such.
    public boolean CoreComponent() {
        // most items aren't core components, so only override if neccesary
        return false;
    }

    // the manufacturer is provided for certain equipment that can be
    // specified as built by a certain company.  Most items don't need this.
    public String GetManufacturer() {
        return "Unknown";
    }

    public void SetManufacturer(String n) {
    }

    // added for BattleForce special abilities that could be part of the
    // equipment being added.  Defaulting to a blank string array.
    public String[] GetBattleForceAbilities() {
        return BattleForceAbilities;
    }

    public void SetBattleForceAbilities( String[] a ) {
        BattleForceAbilities = a;
    }

    // added for armored components in tech manual
    public boolean CanArmor() {
        // most components can be armored, only ones that can't should override
        return true;
    }

    public boolean IsArmored() {
        // return whether the component is armored
        return Armored;
    }

    public void ArmorComponent(boolean armor) {
        // armor or unarmor the component
        Armored = armor;
    }

    public void SetExclusions(Exclusion e) {
        Exclusions = e;
    }

    public Exclusion GetExclusions() {
        return Exclusions;
    }

    public void AddMechModifier(MechModifier m) {
        Modifier = m;
    }

    public MechModifier GetMechModifier() {
        return Modifier;
    }

    // tells us whether the item can be struck during a critical.  Should be
    // overridden if the item requires it.
    public boolean IsCritable() {
        return true;
    }

    // All placeables should be able to return their AvailableCode
    public abstract AvailableCode GetAvailability();
}
