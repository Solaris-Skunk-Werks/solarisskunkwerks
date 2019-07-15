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

package components;

public class MechModifier {
    // this class is used by various systems that modify the 'Mech
    private int PilotMod = 0,
                GunneryMod = 0,
                HeatAdd = 0,
                RunAdd = 0,
                WalkAdd = 0,
                JumpAdd = 0;
    private double DefBonus = 0.0,
                  MinDefBonus = 0.0,
                  ArmorMult = 0.0,
                  IntMult = 0.0,
                  RunMult = 0.0;
    private boolean BVMovement,
                    BVHeatMod,
                    CanJump = true,
                    ConfigOnly = false; //Sets whether or not the MechMod applies only to a single configuration (added for Modular Armor on Omni Configs)

/**
 * Creates a new MechModifier for miscellaneous statistics not covered elsewhere
 * 
 * @param wadd  Modifier to the base walking MP, adder
 * @param radd  Modifier to the base running MP, adder
 * @param jadd  Modifier to the base jumping MP, adder
 * @param rmult Modifier to the base running MP, multiplier
 * @param gmod  Modifier to gunnery skill checks, adder
 * @param pmod  Modifier to piloting skill checks, adder
 * @param heat  Modifier to the 'Mech's heat, not counted in BV, adder
 * @param def   Modifier to the Defensive factor, adder
 * @param mindef Minimum Defensive Factor Modifier
 * @param amult Modifier to the armor multiplier, adder
 * @param imult Modifier to the internal structure multiplier, adder, usually the same as the armor mult
 * @param BVMove Whether this MechMod counts towards BV Movement (add for Modular Armor)
 */
    public MechModifier( int wadd, int radd, int jadd, double rmult, int gmod, int pmod, int heat, double def, double mindef, double amult, double imult, boolean BVMove, boolean BVHeat ) {
        this(wadd, radd, jadd, rmult, gmod, pmod, heat, def, mindef, amult, imult, BVMove, BVHeat, false);
    }

    public MechModifier( int wadd, int radd, int jadd, double rmult, int gmod, int pmod, int heat, double def, double mindef, double amult, double imult, boolean BVMove, boolean BVHeat, boolean Config_Only ) {
        WalkAdd = wadd;
        RunAdd = radd;
        JumpAdd = jadd;
        RunMult = rmult;
        PilotMod = pmod;
        GunneryMod = gmod;
        HeatAdd = heat;
        DefBonus = def;
        MinDefBonus = mindef;
        ArmorMult = amult;
        IntMult = imult;
        BVMovement = BVMove;
        BVHeatMod = BVHeat;
        ConfigOnly = Config_Only;
    }

    public void SetCanJump(boolean j) {
        CanJump = j;
    }

    public boolean CanJump () {
        return CanJump;
    }

    public boolean ModifyConfigOnly() {
        return ConfigOnly;
    }

    public int WalkingAdder() {
        return WalkAdd;
    }

    public int RunningAdder() {
        return RunAdd;
    }

    public int JumpingAdder() {
        return JumpAdd;
    }

    public double RunningMultiplier() {
        return RunMult;
    }

    public int GunneryMod() {
        return GunneryMod;
    }

    public int PilotingModifier() {
        return PilotMod;
    }

    public int HeatAdder() {
        return HeatAdd;
    }

    public double DefensiveBonus() {
        return DefBonus;
    }

    public double MinimumDefensiveBonus() {
        return MinDefBonus;
    }

    public double ArmorMultiplier() {
        return ArmorMult;
    }

    public double InternalMultiplier() {
        return IntMult;
    }

    public boolean UseBVMovement() {
        return BVMovement;
    }

    public boolean UseBVHeat() {
        return BVHeatMod;
    }

    public void Combine( MechModifier m ) {
        // combines two mech modifiers together.
        WalkAdd += m.WalkingAdder();
        RunAdd += m.RunningAdder();
        JumpAdd += m.JumpingAdder();
        RunMult += m.RunningMultiplier();
        GunneryMod += m.GunneryMod();
        PilotMod += m.PilotingModifier();
        HeatAdd += m.HeatAdder();
        DefBonus += m.DefensiveBonus();
        if( MinDefBonus < m.MinDefBonus ) {
            MinDefBonus = m.MinDefBonus;
        }
        ArmorMult += m.ArmorMultiplier();
        IntMult += m.InternalMultiplier();
        if ( ! m.CanJump ) {
            CanJump = false;
        }
    }

    public void BVCombine( MechModifier m ) {
        // combines two mech modifiers together, taking special movement modifiers
        // into consideration (Modular Armor does not count towards BV movement)
        if( m.UseBVMovement() ) {
            WalkAdd += m.WalkingAdder();
            RunAdd += m.RunningAdder();
            JumpAdd += m.JumpingAdder();
            RunMult += m.RunningMultiplier();
        }
        GunneryMod += m.GunneryMod();
        PilotMod += m.PilotingModifier();
        if( m.UseBVHeat() ) {
            HeatAdd += m.HeatAdder();
        }
        DefBonus += m.DefensiveBonus();
        if( MinDefBonus < m.MinDefBonus ) {
            MinDefBonus = m.MinDefBonus;
        }
        ArmorMult += m.ArmorMultiplier();
        IntMult += m.InternalMultiplier();
        if ( ! m.CanJump ) {
            CanJump = false;
        }
    }

    @Override
    public String toString() {
        String retval = WalkAdd + "/" + RunAdd + "/" + JumpAdd + ", RunMult=" + RunMult + ", " + GunneryMod + "/" + PilotMod + ", " + HeatAdd + " heat, ";
        retval += DefBonus + " DMod, " + MinDefBonus + " MinDMod, " + ArmorMult + "/" + IntMult + " multipliers";
        return retval;
    }
}
