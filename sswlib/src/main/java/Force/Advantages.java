/*
Copyright (c) 2009, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package Force;

import java.util.ArrayList;
import javax.swing.DefaultListModel;


public class Advantages {
    private ArrayList<Enhancement> Advantage = new ArrayList<Enhancement>();

    public Advantages() {
        Advantage.add(new Enhancement("hot_dog", "HotDog", "Hot Dog", "P<4", "", "Reduce heat-related rolls by 1", "AToW", 0));
        Advantage.add(new Enhancement("jumping_jack", "JumpJack", "Jumping Jack", "P<4", "", "Attacker movement modifer for jumping is +1 instead of +3", "AToW", 0));
        Advantage.add(new Enhancement("maneuvering_ace", "ManAce", "Maneuvering Ace", "P<4", "", "Gain quad side-step, quad side-step penalty reduced by 1, -1 to-hit for all skidding rolls", "AToW", 0));
        Advantage.add(new Enhancement("melee_specialist", "MeleeSpecialist", "Melee Specialist", "P<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("multi_tasker", "MultiTask", "Multi-Tasker", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("oblique_attacker", "ObliqueAttacker", "Oblique Attacker", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("sniper", "Sniper", "Sniper", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("tactical_genius", "TacGenius", "Tactical Genius", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("specialist", "GunSpecial", "Gunnery Specialization", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("weapon_specialist", "WeapSpecial", "Weapon Specialization", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "BloodStalker", "Blood Stalker", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "FistFire", "Fist Fire", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "Marksman", "Marksman", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "RangeMaster", "Range Master", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "Sharpshooter", "Sharpshooter", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "Dodge", "Dodge", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "HeavyLifter", "Heavy lifter", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "MeleeMaster", "Melee Master", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "NaturalGrace", "Natural Grace", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "SpeedDemon", "Speed Demon", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "CombatIntuition", "Combat Intuition", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "Demoralizer", "Demoralizer", "G<4", "", "", "AToW", 0));
        Advantage.add(new Enhancement("", "CBeauty", "Cosmetic Beauty Enhancement", "", "", "Attractive Trait; +1 CHA; –1 TN to Seduction and Negotiation", "JHS3072", 0));
        Advantage.add(new Enhancement("", "CHorror", "Cosmetic Horror Enhancement", "", "", "Unattractive Trait; +1 CHA; –2 TN to Intimidation and Interrogation", "JHS3072", 0));
        Advantage.add(new Enhancement("", "Prosthetic", "Prosthetic Hand/Foot/Arm/Leg", "", "", "", "JHS3072", 0));
        Advantage.add(new Enhancement("", "EnhProsthetic", "Enhanced Prosthetic Hand/Foot/Arm/Leg", "", "", "", "JHS3072", 1));
        Advantage.add(new Enhancement("", "ImpEnhProsthetic", "Improved Enhanced Prosthetic Hand/Foot/Arm/Leg", "", "", "", "JHS3072", 3));
        Advantage.add(new Enhancement("", "2ndPowerSupply", "Secondary Power Supply", "", "", "80 points per supply; rechargeable; reduce pain med needs for myomer use to 1/24 hr", "JHS3072", 1));
        Advantage.add(new Enhancement("", "ProsthMASC", "Prosthetic Leg MASC", "", "", "", "JHS3072", 3));
        Advantage.add(new Enhancement("pain_shunt", "Shunt", "Pain Shunt", "", "", "Pain Resistance Trait and +1 TN to DEX (permanent); Clumsy Trait (1 year)", "JHS3072", 2));
        Advantage.add(new Enhancement("", "Pheremone", "Pheremone Effuser", "", "", "Range: 3m; 10 doses; –4 TN to Seduction and related for 1D6-1 hours (min 1)*", "JHS3072", 3));
        Advantage.add(new Enhancement("", "Toxin", "Toxin Effuser", "", "", "Range: 3m; 10 doses; “Manei Mortis”: [6D6 (–1D6/meter); Lethal; Duration 4 (–1/meter); Inhaled; Speed: 1 (+1/meter); Detection Diffi culty +3]", "JHS3072", 4));
        Advantage.add(new Enhancement("", "cybeyeir", "Cybernetic Eye (IR)", "", "", "No darkness TN mods", "JHS3072", 2));
        Advantage.add(new Enhancement("", "cybeyeem", "Cybernetic Eye (EM)", "", "", "May detect electronic/magnetic targets as a Radar Sensor (see p. 108, LT); Range: 2 km", "JHS3072", 2));
        Advantage.add(new Enhancement("", "cybeyetele", "Cybernetic Eye (Telescopic)", "", "", "–2 TN modifier to M/L/E when used with weapons or surveillance skills", "JHS3072", 2));
        Advantage.add(new Enhancement("", "cybeyels", "Cybernetic Eye (Laser Sight)", "", "", "–4 TN modifier to M/L/E when used with weapons or surveillance skills", "JHS3072", 2));
        Advantage.add(new Enhancement("", "cybeyeenh", "Cybernetic Eye (Enhanced)", "", "", "–3 TN modifier to surveillance skills; +2 TN to stealth against the user", "JHS3072", 2));
        Advantage.add(new Enhancement("", "cybeyesigp", "Cybernetic Eye (Signal Pickup)", "", "", "100 meter range", "JHS3072", 2));
        Advantage.add(new Enhancement("", "cybspeechvar", "Cybernetic Speech (Variable)", "", "", "", "JHS3072", 2));
        Advantage.add(new Enhancement("", "cybspeechult", "Cybernetic Speech (Ultrasonic)", "", "", "", "JHS3072", 2));
        Advantage.add(new Enhancement("mm_eye_im", "MModeCyb", "Multi-Modal Cybernetic Eye/Ear/Speech Implants", "", "", "", "JHS3072", 3));
        Advantage.add(new Enhancement("mm_eye_im", "EnhMModeCyb", "Enhanced Multi-Modal Cybernetic Eye/Ear/Speech Implants", "", "", "", "JHS3072", 5));
        Advantage.add(new Enhancement("", "Recorder", "Recorder", "", "", "Unit Duration: 6 hours, looping", "JHS3072", 2));
        Advantage.add(new Enhancement("", "BoostRecorder", "Boosted Recorder", "", "", "Unit Duration: 24 hours, looping", "JHS3072", 2));
        Advantage.add(new Enhancement("", "TransReceive", "Transmitter/Receiver", "", "", "Unit Range: 100 meters", "JHS3072", 2));
        Advantage.add(new Enhancement("", "BoostTransReceive", "Boosted Transmitter/Receiver", "", "", "Unit Range: 1 kilometer", "JHS3072", 2));
        Advantage.add(new Enhancement("comm_implant", "Comm", "Communications", "", "", "Unit Range: 100 meters", "JHS3072", 2));
        Advantage.add(new Enhancement("boost_comm_implant", "BoostComm", "Boosted Communications", "", "", "Unit Range: 1 kilometer", "JHS3072", 2));
        Advantage.add(new Enhancement("", "FiltLiver", "Filtration Liver Implant", "", "", "Poison Resistance Trait; When sweating: +1 TN to CHA skills, +1 to +3 TN to Stealth", "JHS3072", 3));
        Advantage.add(new Enhancement("", "FiltLung", "Filtration Lung Implant", "", "", "AV 1 vs. gas weapons", "JHS3072", 3));
        Advantage.add(new Enhancement("vdni", "VDNI", "Vehicular Direct Neural Interface", "", "", "–1 to Piloting, half all Aimed Shot, Attacker Movement and Target Movement mods", "JHS3072", 3));
        Advantage.add(new Enhancement("bvdni", "BVDNI", "Buffered VDNI", "", "", "Ignore Small Cockpit penalty; half all Aimed Shot, Attacker and Target Movement mods", "JHS3072", 5));
        Advantage.add(new Enhancement("dermal_armor", "DermalArmor", "Dermal Myomer Armor Implants", "", "", "+2 BOD, +2 STR, +1 AV Increase: 3/3/3/3, Pain Resistance, –1 CHA, Unattractive", "JHS3072", 4));
        Advantage.add(new Enhancement("", "TSMMyomer", "Triple-Strength Myomer Implants", "", "", "+4 STR, +2 REF, Toughness, –1 CHA, Unattractive", "JHS3072", 4));
    }

    public Enhancement find( String shortName ) {
        for ( Enhancement e : Advantage ) {
            if ( e.ShortName.equals(shortName) ) return e;
        }
        return null;
    }

    public ArrayList<Enhancement> getMDMods() {
        ArrayList<Enhancement> ret = new ArrayList<Enhancement>();
        for ( Enhancement e : Advantage ) {
            if ( e.Source.startsWith("JHS") ) ret.add(e);
        }
        return ret;
    }

    public DefaultListModel getMDModsModel() {
        DefaultListModel m = new DefaultListModel();
        for ( Enhancement e : getMDMods() ) {
            m.addElement(e);
        }
        return m;
    }

    public ArrayList<Enhancement> getMMChoices() {
        ArrayList<Enhancement> ret = new ArrayList<Enhancement>();
        for ( Enhancement e : Advantage ) {
            if ( !e.MMName.isEmpty() ) ret.add(e);
        }
        return ret;
    }

    public class Enhancement {
        public final static String ManeiDomini = "MD",
                                    MegaMek = "MM";

        String MMName = "",
                ShortName = "",
                ActualName = "",
                Requirement = "",
                Description = "",
                Effect = "",
                Source = "";

        int Level = 0;

        Enhancement( String MMName,
                             String ShortName,
                             String ActualName,
                             String Requirement,
                             String Description,
                             String Effect,
                             String Source,
                             int Level ) {
            this.MMName = MMName;
            this.ShortName = ShortName;
            this.ActualName = ActualName;
            this.Requirement = Requirement;
            this.Description = Description;
            this.Effect = Effect;
            this.Source = Source;
            this.Level = Level;
        }


        public String SerializeXML() {
            return "<enhancement code=\"" + ShortName + "\" />";
        }

        public int getLevel() {
            return Level;
        }

        @Override
        public String toString() {
            return "[" + Level + "] " + ActualName;
        }
    }
}
