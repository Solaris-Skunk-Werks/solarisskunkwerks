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

import IO.RUSReader;
import common.CommonTools;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class Skills {
    private ArrayList<Skill> List = new ArrayList<Skill>();
    private ArrayList<Skill> filtered = new ArrayList<Skill>();
    private int MaxSeperation = 3;
    private float MaxBV = 9999999;
    private String MaxSkill = "";
    private RUSReader reader = new RUSReader();
    private RUS rus = new RUS();
    private String skillLevel = "";
    private float BV = 1.0f;

    public static String Gunnery = "Gunnery";
    public static String Piloting = "Piloting";

    public Skills() {

    }
    
    public Skills(float BV) {
        this.BV = BV;
        SetSkills();
    }

    public final void SetSkills() {
        List.clear();
        for ( int G=0; G<=7; G++ ) {
            for ( int P=0; P<=7; P++ ) {
                List.add(new Skill(G, P, CommonTools.GetSkillBV(BV, G, P)));
            }
        }
        sortByBV();
    }

    public void sortByBV() {
        int i = 1, j = 2;
        Skill swap;
        while( i < List.size() ) {
            // get the two items we'll be comparing
            if( ((Skill) List.get( i - 1 )).getBV() >= ((Skill) List.get( i )).getBV() ) {
                i = j;
                j += 1;
            } else {
                swap = List.get( i - 1 );
                List.set( i - 1, List.get( i ) );
                List.set( i, swap );
                i -= 1;
                if( i == 0 ) {
                    i = 1;
                }
            }
        }
    }

    public Skill getBestSkills() {
        ArrayList<Skill> oList = filter();
        if ( oList.size() > 0 ) {
            return (Skill) oList.get(0);
        } else {
            return new Skill(4, 5, 0);
        }
    }

    public ArrayList<Skill> filter() {
        Boolean Include = false;
        filtered.clear();

        for (int i=0; i<List.size(); i++) {
            Skill data = (Skill) List.get(i);
            if ( data.getSeperation() <= MaxSeperation && data.getBV() <= MaxBV ) {
                if ( !MaxSkill.isEmpty() ) {
                    if ( MaxSkill.equals("Gunnery") ) {
                        if ( data.Gunnery <= data.Piloting ) { Include = true; }
                    } else {
                        if ( data.Piloting <= data.Gunnery ) { Include = true; }
                    }
                } else {
                    Include = true;
                }
                if ( Include ) { filtered.add((Skill) List.get(i)); }
            }
            Include = false;
        }

        int i = 1, j = 2;
        Skill swap;
        while( i < filtered.size() ) {
            // get the two items we'll be comparing
            if( ((Skill) filtered.get( i - 1 )).getBV() >= ((Skill) filtered.get( i )).getBV() ) {
                i = j;
                j += 1;
            } else {
                swap = filtered.get( i - 1 );
                filtered.set( i - 1, filtered.get( i ) );
                filtered.set( i, swap );
                i -= 1;
                if( i == 0 ) {
                    i = 1;
                }
            }
        }

        return filtered;
    }

    public DefaultListModel getListModel() {
        DefaultListModel model = new DefaultListModel();
        Boolean Include = false;

        for (int i=0; i<List.size(); i++) {
            Skill data = (Skill) List.get(i);
            if ( data.getSeperation() <= MaxSeperation && data.getBV() <= MaxBV ) {
                if ( !MaxSkill.isEmpty() ) {
                    if ( MaxSkill.equals("Gunnery") ) {
                        if ( data.Gunnery <= data.Piloting ) { Include = true; }
                    } else {
                        if ( data.Piloting <= data.Gunnery ) { Include = true; }
                    }
                } else {
                    Include = true;
                }
                if ( Include ) { model.addElement(((Skill) List.get(i)).getDisplay()); }
            }
            Include = false;
        }
        return model;
    }

    public Skill generateRandomSkill() {
        if ( !skillLevel.isEmpty() ) {
            return generateRandomSkills(skillLevel);
        }
        return new Skill();
    }

    public Skill generateRandomSkills( String SkillLevel ) {
        String skills = generateRandomSkill(SkillLevel);
        Skill skill = new Skill();
        skill.Gunnery = Integer.parseInt(skills.split("/")[0]);
        skill.Piloting = Integer.parseInt(skills.split("/")[1]);
        return skill;
    }

    public String generateRandomSkill( String SkillLevel ) {
        String filename = "./Data/Tables/Total Warfare/random skills {Level}.txt";
        String[] skills = new String[] {"4", "5"};

        if ( SkillLevel.equals("Random") ) {
            try {
                reader.Load("./Data/Tables/Total Warfare/random experience rating.txt", rus);
                SkillLevel = rus.Generate().trim();
                //SkillLevel = SkillLevel.substring(0, SkillLevel.lastIndexOf(","));
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }

        filename = filename.replace("{Level}", SkillLevel);
        try {
            reader.Load(filename, rus);
            skills = rus.Generate().split(",");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        if ( skills.length > 0 ) {
            skills[0] = skills[0].replace("(Piloting ", "");
            skills[1] = skills[1].replace("Gunnery ", "").replace(")", "");
            return skills[1].trim() + "/" + skills[0].trim();
        } else {
            return "4/5";
        }
    }

    /**
     * @return the MaxSeperation
     */
    public int getMaxSeperation() {
        return MaxSeperation;
    }

    /**
     * @param MaxSeperation the MaxSeperation to set
     */
    public void setMaxSeperation(int MaxSeperation) {
        this.MaxSeperation = MaxSeperation;
    }

    /**
     * @return the MaxBV
     */
    public float getMaxBV() {
        return MaxBV;
    }

    /**
     * @param MaxBV the MaxBV to set
     */
    public void setMaxBV(float MaxBV) {
        this.MaxBV = MaxBV;
    }

    /**
     * @return the MaxSkill
     */
    public String getMaxSkill() {
        return MaxSkill;
    }

    /**
     * @param MaxSkill the MaxSkill to set
     */
    public void setMaxSkill(String MaxSkill) {
        this.MaxSkill = MaxSkill;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public float getBV() {
        return BV;
    }

    public void setBV(float BV) {
        this.BV = BV;
        SetSkills();
    }

    public class Skill {
        private int Gunnery = 4;
        private int Piloting = 5;
        private float BV = 0f;

        public Skill() {
            
        }

        public Skill(int Gunnery, int Piloting, float BV) {
            this.Gunnery = Gunnery;
            this.Piloting = Piloting;
            this.BV = BV;
        }

        public String getDisplay() {
            return getGunnery() + " / " + getPiloting() + " (" + String.format("%1$,.0f", BV) + ")";
        }

        public float getBV() {
            return this.BV;
        }

        public int getGunnery() {
            return Gunnery;
        }

        public int getPiloting() {
            return Piloting;
        }

        public int getSeperation() {
            if ( Gunnery > Piloting ) {
                return Gunnery - Piloting;
            } else {
                return Piloting - Gunnery;
            }
        }
    }
}
