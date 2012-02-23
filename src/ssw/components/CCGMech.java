/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.components;

import components.PhysicalWeapon;
import components.RangedWeapon;
import components.abPlaceable;
import components.ifWeapon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
/**
 * 
 * CCGMech - Class that generates a Battletech CCG card
 * based upon data provided from Mech class.  For now, use
 * dlgCCGMech to display the relevant information.
 * 
 * CCGMech handles the conversion of a 'Mech from the tabletop
 * rules into a card that can be used in the Battletech CCG game.
 * 
 * The goal of this class is to reproduce the stats on the cards
 * for those 'Mechs already included in the game.  Other 'Mechs 
 * should be generated in a manner consistent with the canon
 * cards in an effort to keep play balanced.
 * 
 * The current conversion process is a little long to be
 * detailed here, but they are based upon Gauss Bears 
 * Inofficial Conversion Rules.
 *
 * There are still a few special abilities to generate, 'Mech cost to
 * calculate, and a whole bunch of fiddling with equations.  In addition,
 * the class currently does not check if a canon card already exists.
 *
 * @author Kevin
 */
public class CCGMech {
    private String Name = "",
                FullName = "",
                ImageSrc = "",
                Faction = "",
                SpecialAbilities = "",
                FlavourText = "",
                IllustratorName = "",
                Loadout = "",
                Cost = "";
    private char MovementRate = 'S';
    private int ResourceCost = 0,
                Mass = 0,
                MunitionsAttributeCost = 0,
                TacticsAttributeCost = 0,
                LogisticsAttributeCost = 0,
                PoliticsAttributeCost = 0,
                AssemblyAttributeCost = 0,
                AttackValue = 0,
                ArmourValue = 0,
                StructureValue = 0,
                MissileDamage = 0,
                OverheatDamage = 0,
                AlphaStrikeDamage = 0,
                LeakDamage = 0;

    //These Strings should be bundled up into a single class.
    //Also, I should learn to printf() Java styles cause this is dirty.
    private static String AblSlow = " cannot block unless guarding.\n";
    private static String AblDasher1 = "You may move ";
    private static String AblDasher2 = " to your Patrol region as soon as you activate it.\n";
    private static String AblAP1 = "AP (";
    private static String AblAP2 = " deals +1 damage to any target other than a 'Mech)\n";
    private static String AblPA1 = "If ";
    private static String AblPA2 = " blocks or is blocked by at least one ";
    private static String AblPA3= " 'Mech, ";
    private static String AblPA4 = " deals +1 damage to one of those ";
    private static String AblPA5 = " 'Mech.\n";
    private static String AblJump = "Jump (-1 attack: +1 initiative)\n";
    private static String AblLeak1 = "If the ";
    private static String AblLeak2 = " attacks and is blocked, it may deal ";
    private static String AblLeak3 = " of its damage to the target.\n";

    /**
     * This constructor should build the entire card.  Possibly an entire
     * Mech object can be passed in here, although this system works for now.
     *
     * Eventually, this constructor should check to see if there exists a
     * canon CCG card, in which case those stats should be used.  Otherwise
     * we can generate a (hopefully) balanced card using the conversion process.
     *
     * @param name The 'Mech's name without variant info.  Normally aMech.GetName().
     * @param fullname The full name of the unit (appears on top of card.  Normally aMech.GetFullName();
     */
    public CCGMech(String name, String fullname) {
        Name = name;
        FullName = fullname;
    }

    /**
     * Sets the movement rate for the card based upon the 'Mech's
     * walking speed.
     *
     * [0-2] Slow, Slowpoke ability.
     *  [3] Slow
     * [4-6] Moderate
     * [7-8] Fast
     * [9+] Fast, Dasher ability.
     *
     * TODO: Possibility of MASC or Supercharger ability. Something
     * like:
     *          MASC (Place a MASC token on this card.  The 'Mech
     *          takes 1 Damage for every MASC token on the card,
     *          but may operate as if it was one speed level faster.)
     *
     * @param walkSpeed The walk MP of the 'Mech
     */
    public void setMovementRate(int walkSpeed) {
        if(walkSpeed <= 3) {
            MovementRate = 'S';
            if(walkSpeed < 3) {
                SpecialAbilities = SpecialAbilities + Name + AblSlow + "\n";
            }
        } else if(walkSpeed <= 6) {
            MovementRate = 'M';
        } else if (walkSpeed > 6) {
            MovementRate = 'F';
            if(walkSpeed >= 9 ) {
                SpecialAbilities = SpecialAbilities + AblDasher1 + Name + AblDasher2 + "\n";
            }
        }
    }

    /**
     * IF the mech has a single jump jet, it gets
     * the jump ability.
     *
     * TODO: Possiblity of an IJJ ability.  Something
     * like:
     *      Improved Jump (-1 Attack, +1 Initiative), 'Mech
     *      operates as if it was one speed level faster.
     *
     * @param jump
     */
    public void setJump(int jump) {
        if(jump > 0) {
            SpecialAbilities = SpecialAbilities + AblJump + "\n";
        }
    }

    /**
     * The 'Mech's mass is used when assigning structure
     * values.  Heavier mechs tend to have more armour than
     * lighter ones.  You should set the mass before you
     * setArmourAndStructure().
     *
     * @param mass The mas of the 'Mech
     */
    public void setMass (int mass) {
        Mass = mass;
    }

    /**
     * The function is very much a work in progress.  Currently, it is one unholy
     * mess of an if statement.  Each piece of equipment from the loadout is
     * checked to see if it is a weapon.  If it is, check to see if it is rear-mounted
     * (in which case it is ignored) or an MG (in which case it gives the card
     * the AntiPersonnel ability.
     *
     * From there, each main class of weapons is checked.  Basic algorithm is
     * that weapon damage is divided by a range factor (long range weapons
     * have a lower range factor) to produce an Attack value for that weapon.
     *
     * Missile weapons are split between LRMs and all others.  LRMs increase a
     * Mech's missile ability, while MRMs and SRMs increase the attack value.
     * (unlike in the conversion rules, SRMs do not currently grant alpha
     * strike, although I think that maybe MRMs should).
     * Artemis IV is accounted for, but NARC and Apollo currently aren't.
     *
     * TODO: NARC and MRMs to Alpha Strike.
     *
     * Next is ballistic weapons.  Ac-20s of all types give +2 Alpha strike and
     * count half damage and heat towards Attack value.  Ac-10s and Heavy Gauss
     * Rifles count +1 to Alpha Strike and half damage and heat as well.
     * Gauss and Heavy Gauss also count +1 to leak damage for long range fire.
     * Remaining ballistic weapons add to Attack Value only.
     *
     * TODO: Light Gauss rifle.
     *
     * Finally Energy weapons (and all others).  3 or more PPCs or ERLL
     * contribute a +1 to leak damage, while 1 ER PPC does the same.
     * All Attack values from energy weapons are multiplied by 1.3 as
     * well.  Unlike the conversion rules, there is no range factor reduction
     * for IS Pulse Weapons, although there probably should be.
     *
     * The overheat situation is all borked up, I have no idea how to properly
     * implement it.  The conversion rules are all fuzzy and happy about it,
     * but it's basically fudge work.
     *
     * I'm going to try counting weapon damage as a block value (ie if the mech
     * overheats by 4, does 10 damage and produces 14 heat, instead of removing
     * individual weapons ill do the ideal solution and remove 4 heat worth of
     * damage under the (faulty) assumption that all weapons have identical
     * heat to damage ratios.
     *
     *
     * @param CurrentLoadout The ArrayList from a Mech class with weapons and equipment.
     * @param MovementHeat The maximum heat the 'Mech generates by moving (Usually 2 unless it jumps).
     * @param TotalHeatDis The total amount of heat the 'Mech can dissapate in one turn.
     */
    public void setAttackValue(ArrayList CurrentLoadout, int MovementHeat, int TotalHeatDis) {
        double attackValue = 0.0;
        double missileValue = 0.0;
        int vanPPCERLLcount = 0;
        List<WeaponData> weaponList = new ArrayList<WeaponData>();
        if( CurrentLoadout.size() > 0 ) {
            for( int i = 0; i < CurrentLoadout.size(); i++ ) {
                if(CurrentLoadout.get(i) instanceof ifWeapon) {
                    ifWeapon currentWeapon = (ifWeapon) CurrentLoadout.get(i);
                    double currentWeaponAttackValue = 0.0;
                    double currentMissileValue = 0.0;
                    //a bunch of checks to see if we even need to calculated
                    //for this particular weapon:
                    // if(currentWeapon.)
                    boolean notRear = true;
                    boolean notMG = true;
                    boolean physical = false;
                    boolean missile = false;
                    if(((abPlaceable)currentWeapon).IsMountedRear()) {
                        notRear = false;
                    }else if(currentWeapon.LookupName().contains("Machine Gun")) {
                        notMG = false;
                        SpecialAbilities = SpecialAbilities + AblAP1 + Name + AblAP2 + "\n";
                    }else if(currentWeapon instanceof PhysicalWeapon ){
                        physical = true;
                        if(Mass >= 80) {
                            currentWeaponAttackValue = 2.0;
                        } else {
                            currentWeaponAttackValue = 1.0;
                        }
                        SpecialAbilities = SpecialAbilities + AblPA1 + Name + AblPA2 + MovementRate + AblPA3 + Name + AblPA4 + MovementRate + AblPA5 + "\n";
                    }else if(currentWeapon.GetWeaponClass() == ifWeapon.W_MISSILE ){
                        //need to handle NARC in here somehow.
                        missile = true;
                        int clusterSize = currentWeapon.ClusterSize();
                        if(currentWeapon.GetRangeLong() > 15) {
                            //LRMS
                            if(clusterSize == 5) {
                                currentMissileValue = 3;
                            } else if (clusterSize == 10) {
                                currentMissileValue = 6;
                            } else if (clusterSize == 15) {
                                currentMissileValue = 9;
                            } else if (clusterSize == 20) {
                                currentMissileValue = 12;
                            }
                            if(((RangedWeapon)currentWeapon).GetFCS() != null) {
                                currentMissileValue = currentMissileValue * 4 / 3;
                            }
                            if(currentWeapon.GetRangeMin() > 0){
                                currentMissileValue = currentMissileValue * 0.75;
                            }
                            currentMissileValue = currentMissileValue / 8;
                            missileValue = missileValue + currentMissileValue;
                        } else if(currentWeapon.GetRangeLong() > 12) {
                            //MRMs (add some alpha strike code here)
                            if(clusterSize == 10) {
                                currentMissileValue = 6;
                            } else if (clusterSize == 20) {
                                currentMissileValue = 12;
                            } else if (clusterSize == 30) {
                                currentMissileValue = 18;
                            } else if (clusterSize == 40) {
                                currentMissileValue = 24;
                            }
                            currentMissileValue = currentMissileValue * 0.8 /7;
                            attackValue = attackValue + currentMissileValue;
                        } else if(currentWeapon.GetRangeLong() > 9) {
                            //Clan Streak SRMs
                            if(clusterSize == 2) {
                                currentMissileValue = 4;
                            } else if (clusterSize == 4) {
                                currentMissileValue = 8;
                            } else if (clusterSize == 6) {
                                currentMissileValue = 12;
                            }
                            currentMissileValue = currentMissileValue / 7;
                            attackValue = attackValue + currentMissileValue;
                        } else {
                            //SRMS
                            if(clusterSize == 2) {
                                currentMissileValue = 2;
                            } else if (clusterSize == 4) {
                                currentMissileValue = 6;
                            } else if (clusterSize == 6) {
                                currentMissileValue = 4;
                            }
                            if(currentWeapon.IsStreak()) {
                                currentMissileValue = clusterSize * 2;
                            } else if(((RangedWeapon)currentWeapon).GetFCS() != null) {
                                currentMissileValue = currentMissileValue + 2;
                            }
                            currentMissileValue = currentMissileValue / 9;
                            attackValue = attackValue + currentMissileValue;
                        }
                    } else if((currentWeapon.GetWeaponClass() == ifWeapon.W_BALLISTIC) ){
                        if(currentWeapon.GetDamageLong() == 20) {
                            AlphaStrikeDamage = AlphaStrikeDamage + 2;
                            if(currentWeapon.GetRangeLong() >= 12) {
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium() / 7.0 / 2.0;
                            } else {
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium() / 9.0 / 2.0;
                            }
                        } else if(currentWeapon.GetDamageLong() == 10) {
                            //Hvy Gauss or AC/10.
                            AlphaStrikeDamage = AlphaStrikeDamage + 1;
                            if(currentWeapon.GetRangeLong() >= 18){
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium()/ 5.0 / 2.0 * 0.75;
                                LeakDamage++;
                            } else if(currentWeapon.GetRangeLong() >= 12) {
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium()/ 7.0 / 2.0;
                            } else if(currentWeapon.GetRangeLong() >= 7) {
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium()/ 9.0 / 2.0;
                            } else {
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium()/ 11.0 / 2.0;
                            }
                        } else if(currentWeapon.GetDamageLong() == 15) {
                            //Standard Gauss
                            currentWeaponAttackValue = currentWeapon.GetDamageMedium()/ 5.0 / 2.0 * 0.75;
                            LeakDamage++;
                        } else {
                            if(currentWeapon.GetRangeLong() >= 18){
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium()/5.0;
                            } else if(currentWeapon.GetRangeLong() >= 12) {
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium()/7.0;
                            } else if(currentWeapon.GetRangeLong() >= 7) {
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium()/9.0;
                            } else {
                                currentWeaponAttackValue = currentWeapon.GetDamageMedium()/11.0;
                            }
                            if(currentWeapon.GetRangeMin() > 0) {
                                currentWeaponAttackValue = currentWeaponAttackValue * 0.75;
                            }
                        }
                    } else {
                        if(currentWeapon.GetRangeLong() >= 18){
                            currentWeaponAttackValue = currentWeapon.GetDamageMedium()/5.0;
                            if(currentWeapon.GetRangeLong() >= 19) {
                                LeakDamage++;
                            } else {
                                vanPPCERLLcount++;
                            }
                            if(vanPPCERLLcount > 2) {
                                vanPPCERLLcount = 0;
                                LeakDamage++;
                            }
                        } else if(currentWeapon.GetRangeLong() >= 12) {
                            currentWeaponAttackValue = currentWeapon.GetDamageMedium()/7.0;
                        } else if(currentWeapon.GetRangeLong() >= 7) {
                            currentWeaponAttackValue = currentWeapon.GetDamageMedium()/9.0;
                        } else {
                            currentWeaponAttackValue = currentWeapon.GetDamageMedium()/11.0;
                        }
                        if(currentWeapon.GetRangeMin() > 0) {
                            currentWeaponAttackValue = currentWeaponAttackValue * 0.75;
                        }
                        if(currentWeapon.GetWeaponClass() == ifWeapon.W_ENERGY ) {
                            currentWeaponAttackValue = currentWeaponAttackValue * 1.3;
                        }

                    }
                    //System.out.println(currentWeapon.GetName() + " " + currentWeapon.GetType() + " " + currentWeapon.GetDamageMedium());
                    if(currentWeaponAttackValue > 0.0) {
                        WeaponData wep = new WeaponData();
                        wep.Damage = currentWeaponAttackValue;
                        wep.Heat = currentWeapon.GetHeat();
                        weaponList.add(wep);
                        attackValue = attackValue + currentWeaponAttackValue;
                    }
                }
            }
        }
//        System.out.println("Un-Sorted");
//        for(int c = 0; c < weaponList.size(); c++) {
//            WeaponData wep = weaponList.get(c);
//            System.out.println("AttackValue: " + wep.Damage + " HeatValue: " + wep.Heat);
//        }
//
        Collections.sort(weaponList);
        System.out.println("Sorted");
        for(int c = 0; c < weaponList.size(); c++) {
            WeaponData wep = weaponList.get(c);
          //  System.out.println("AttackValue: " + wep.Damage + " HeatValue: " + wep.Heat);
        }

        int heat = MovementHeat;
        for(int c = 0; c < weaponList.size(); c++) {
                heat = heat + weaponList.get(c).Heat;
        }
        double overHeatDamage = 0.0;
        double overHeatHeat = 0.0;
        if(heat > TotalHeatDis) {
            for(int c = 0; c < weaponList.size(); c++) {
                heat = heat - weaponList.get(c).Heat;
                overHeatHeat = overHeatHeat + weaponList.get(c).Heat;
                attackValue = attackValue - weaponList.get(c).Damage;
                overHeatDamage = overHeatDamage + weaponList.get(c).Damage;
                if(heat <= TotalHeatDis) {
                    break;
                }
            }
            overHeatHeat = overHeatHeat / 5.0;
            if((int)overHeatHeat == 0) {
                overHeatHeat = 1;
            }
            if((int)overHeatDamage == 0) {
                overHeatDamage = 1;
            }
            SpecialAbilities = SpecialAbilities + "Overheat " + (int)overHeatHeat + ": " + (int)overHeatDamage + "\n";
        }
        AttackValue = (int)attackValue;
        //System.out.println(missileValue);
        MissileDamage = (int)missileValue;

        if(AlphaStrikeDamage > 0 ) {
            SpecialAbilities = SpecialAbilities + "AlphaStrike: " + AlphaStrikeDamage + "\n";
        }
        if(LeakDamage > 0 ) {
            SpecialAbilities = SpecialAbilities + AblLeak1 + Name + AblLeak2 + LeakDamage + AblLeak3 + "\n";
        }
        //System.out.println("Attack value: " + attackValue + " MissileValue: " + missileValue);
        if(MissileDamage > 0) {
            SpecialAbilities = SpecialAbilities + "Missile: " + MissileDamage + "\n";
        }
    }
    //Canon cards that are miscalculated by this method:
    //Light:    Jenner (IS variants)
    //          Spectre
    //Medium:   Assassin (ASN-23)
    //Heavy:    Axman
    public void setArmourAndStructure (int armourPoints) {
        //The Jenner seems to be the sole exception that light
        //fast mechs get 0 Armour unless their structure is at
        //least 5.
        if(armourPoints < 30) {
            ArmourValue = 0;
            StructureValue = 1;
        } else if(armourPoints < 50) {
            ArmourValue = 0;
            StructureValue = 2;
        } else if(armourPoints < 70) {
            if(MovementRate == 'F') {
                ArmourValue = 0;
                StructureValue = 3;
            } else {
                ArmourValue = 1;
                StructureValue = 2;
            }
        } else if(armourPoints < 90) {
            if(MovementRate == 'F') {
                ArmourValue = 0;
                StructureValue = 4;
            } else {
                ArmourValue = 1;
                StructureValue = 3;
            }
        } else if(armourPoints < 110) {
            if(MovementRate == 'F') {
                ArmourValue = 0;
                StructureValue = 5;
            } else if(Mass < 40) {
                ArmourValue = 0;
                StructureValue = 5;
            } else {
                ArmourValue = 1;
                StructureValue = 4;
            }
        } else if(armourPoints < 130) {
             if(MovementRate == 'F') {
                ArmourValue = 1;
                StructureValue = 5;
             } else if(Mass < 60) {
                ArmourValue = 1;
                StructureValue = 5;
            } else {
                ArmourValue = 2;
                StructureValue = 4;
            }
        } else if(armourPoints < 150) {
             if(MovementRate == 'F') {
                ArmourValue = 1;
                StructureValue = 6;
             } else if(Mass < 60) {
                ArmourValue = 1;
                StructureValue = 6;
             } else {
                ArmourValue = 2;
                StructureValue = 5;
            }
        } else if(armourPoints < 170) {
             if(MovementRate == 'F') {
                ArmourValue = 1;
                StructureValue = 7;
             } else if(Mass < 60) {
                ArmourValue = 1;
                StructureValue = 7;
             } else {
                ArmourValue = 2;
                StructureValue = 6;
            }
        } else if(armourPoints < 190) {
             if(MovementRate == 'F') {
                ArmourValue = 1;
                StructureValue = 8;
             } else if(Mass < 60) {
                ArmourValue = 1;
                StructureValue = 8;
             } else if(Mass >= 80) {
                 ArmourValue = 3;
                 StructureValue = 6;
             } else {
                ArmourValue = 2;
                StructureValue = 7;
            }
        } else if(armourPoints < 210) {
            if(Mass >= 80) {
                ArmourValue = 2;
                StructureValue = 8;
            } else {
                ArmourValue = 3;
                StructureValue = 7;
            }
        } else if(armourPoints < 230) {
            if(Mass >= 80) {
                ArmourValue = 2;
                StructureValue = 9;
            } else {
                ArmourValue = 3;
                StructureValue = 8;
            }
        } else if(armourPoints < 250) {
            ArmourValue = 3;
            StructureValue = 9;
        } else if(armourPoints < 270) {
            ArmourValue = 3;
            StructureValue = 10;
        } else if(armourPoints < 290) {
            ArmourValue = 3;
            StructureValue = 11;
        } else {
            ArmourValue = 4;
            StructureValue = 11;
        }
    }

    public char getSpeed() {
        return MovementRate;
    }
    public String getName() {
        return FullName;
    }
    public int getMass(){
        return Mass;
    }
    public String getSpecial() {
        return SpecialAbilities;
    }
    public int getArmour() {
        return ArmourValue;
    }
    public int getStructure() {
        return StructureValue;
    }
    public int getAttack() {
        return AttackValue;
    }

    private class WeaponData implements Comparable<WeaponData>{
        public int Heat;
        public double Damage;

        public int compareTo(WeaponData o){
            return (int)(this.Damage - o.Damage);
        }
    }
}
