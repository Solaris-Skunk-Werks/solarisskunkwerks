/*
Copyright (c) 2008~2012, George Blouin Jr. (george.blouin@gmail.com)
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

package components;

public class Quirk {
    private String name = "";
    private int cost = 0;
    private boolean positive = true,
                    battlemech = false,
                    industrialmech = false,
                    combatvehicle = false,
                    battlearmor = false,
                    aerospacefighter = false,
                    conventionalfighter = false,
                    dropship = false,
                    jumpship = false,
                    warship = false,
                    spacestation = false,
                    protomech = false,
                    isvariable = false;
    private String description = "";

    public Quirk( String Name,
                    boolean Type,
                    int Cost,
                    boolean BattleMech,
                    boolean IndustrialMech,
                    boolean CombatVehicle,
                    boolean BattleArmor,
                    boolean AerospaceFighter,
                    boolean ConventionalFighter,
                    boolean Dropship,
                    boolean Jumpship,
                    boolean Warship,
                    boolean SpaceStation,
                    boolean ProtoMech,
                    boolean IsVariable,
                    String Description) {
        name = Name;
        positive = Type;
        cost = Cost;
        battlemech = BattleMech;
        industrialmech = IndustrialMech;
        combatvehicle = CombatVehicle;
        battlearmor = BattleArmor;
        aerospacefighter = AerospaceFighter;
        conventionalfighter = ConventionalFighter;
        dropship = Dropship;
        jumpship = Jumpship;
        warship = Warship;
        spacestation = SpaceStation;
        protomech = ProtoMech;
        isvariable = IsVariable;
        description = Description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the positive
     */
    public boolean isPositive() {
        return positive;
    }

    /**
     * @return the battlemech
     */
    public boolean isBattlemech() {
        return battlemech;
    }

    /**
     * @return the industrialmech
     */
    public boolean isIndustrialmech() {
        return industrialmech;
    }

    /**
     * @return the combatvehicle
     */
    public boolean isCombatvehicle() {
        return combatvehicle;
    }

    /**
     * @return the battlearmor
     */
    public boolean isBattlearmor() {
        return battlearmor;
    }

    /**
     * @return the aerospacefighter
     */
    public boolean isAerospacefighter() {
        return aerospacefighter;
    }

    /**
     * @return the conventionalfighter
     */
    public boolean isConventionalfighter() {
        return conventionalfighter;
    }

    /**
     * @return the dropship
     */
    public boolean isDropship() {
        return dropship;
    }

    /**
     * @return the jumpship
     */
    public boolean isJumpship() {
        return jumpship;
    }

    /**
     * @param jumpship the jumpship to set
     */
    public void setJumpship(boolean jumpship) {
        this.jumpship = jumpship;
    }

    /**
     * @return the warship
     */
    public boolean isWarship() {
        return warship;
    }

    /**
     * @return the spacestation
     */
    public boolean isSpacestation() {
        return spacestation;
    }

    /**
     * @return the protomech
     */
    public boolean isProtomech() {
        return protomech;
    }

    /**
     * @return the isvariable
     */
    public boolean isIsvariable() {
        return isvariable;
    }

    public String ToString()
    {
        return String.format("($0) $1", isvariable ? "*":cost, name);
    }

}
