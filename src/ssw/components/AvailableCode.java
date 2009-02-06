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

import ssw.Constants;

public class AvailableCode {
    // Provides a class for availability codes for CBT equipment.
    
    // Declares
    private char SL,
                 SW,
                 CI,
                 TechRating;
    private int Intro,
                Extinct,
                ReIntro,
                RulesLevelBM,
                RulesLevelIM,
                RulesLevelVee,
                RandDStart;
    private String IntroFaction,
                   ReIntroFaction,
                   RandDFaction;
    private boolean WentExtinct,
                    ReIntroduced,
                    Clan,
                    Prototype,
                    // in general, we want to allow all components to be allowed for primitive 'Mechs
                    // use the setter routine later if a component is not allowed.
                    PrimitiveAllowed = true;

    // Constructor
    public AvailableCode( boolean c, char TR, char SL_Era, char SW_Era, char CI_Era, int IntDate, int ExtDate, int ReIDate, String IntFaction, String ReIFaction, boolean Ext_TF, boolean ReI_TF ) {
        Clan = c;
        SL = SL_Era;
        SW = SW_Era;
        CI = CI_Era;
        TechRating = TR;

        // Now check the values for validity.  If not, assign defaults.
        if( SL < 65 || SL > 70 ) {
            if( SL != 88 ) {
                SL ='X';
            }
        }

        if( SW < 65 || SW > 70 ) {
            if( SW != 88 ) {
                SW ='X';
            }
        }

        if( CI < 65 || CI > 70 ) {
            if( CI != 88 ) {
                CI ='X';
            }
        }

        // Assign dates and factions
        Intro = IntDate;
        Extinct = ExtDate;
        ReIntro = ReIDate;
        IntroFaction = IntFaction;
        ReIntroFaction = ReIFaction;
        WentExtinct = Ext_TF;
        ReIntroduced = ReI_TF;
        RulesLevelBM = Constants.TOURNAMENT;
        RulesLevelIM = Constants.TOURNAMENT;
        RulesLevelVee = Constants.TOURNAMENT;
        RandDStart = 0;
        RandDFaction = "";
        Prototype = false;
    }

    public AvailableCode( boolean c, char TR, char SL_Era, char SW_Era, char CI_Era, int IntDate, int ExtDate, int ReIDate, String IntFaction, String ReIFaction, boolean Ext_TF, boolean ReI_TF, int RDDate, boolean Proto, String RDFaction, int RulesBM, int RulesIM ) {
        this( c, TR, SL_Era, SW_Era, CI_Era, IntDate, ExtDate, ReIDate, IntFaction, ReIFaction, Ext_TF, ReI_TF );

        // this constructor added for static available codes.
        RandDStart = RDDate;
        Prototype = Proto;
        RandDFaction = RDFaction;
        RulesLevelBM = RulesBM;
        RulesLevelIM = RulesIM;
    }

    // Public Methods
    public void SetRulesLevelBM( int a ) {
        // sets the rules level
        RulesLevelBM = a;
    }

    public void SetRulesLevelIM( int a ) {
        // sets the rules level
        RulesLevelIM = a;
    }

    public void SetRulesLevelVee( int a ) {
        RulesLevelVee = a;
    }

    public void SetPrimitiveAllowed( boolean pa ) {
        PrimitiveAllowed = pa;
    }

    public void SetRandDStart( int i ) {
        RandDStart = i;
    }

    public void SetRandDFaction( String f ) {
        RandDFaction = f;
    }

    public void SetPrototype( boolean p ) {
        Prototype = p;
    }

    public int GetRulesLevelBM() {
        return RulesLevelBM;
    }

    public int GetRulesLevelIM() {
        return RulesLevelIM;
    }

    public int GetRulesLevelVee() {
        return RulesLevelVee;
    }

    public char GetTechRating() {
        return TechRating;
    }

    public char GetSLCode() {
        return SL;
    }

    public char GetSWCode() {
        return SW;
    }

    public char GetCICode() {
        return CI;
    }

    public int GetIntroDate() {
        return Intro;
    }
    
    public int GetExtinctDate() {
        if( WentExtinct ) {
            return Extinct;
        } else {
            return 0;
        }
    }
    
    public int GetReIntroDate() {
        if( ReIntroduced ) {
            return ReIntro;
        } else {
            return 0;
        }
    }

    public int GetRandDStart() {
        return RandDStart;
    }

    public boolean IsPrototype() {
        return Prototype;
    }

    public boolean IsPrimitiveAllowed() {
        return PrimitiveAllowed;
    }

    public String GetRandDFaction() {
        return RandDFaction;
    }

    public boolean WentExtinct() {
        return WentExtinct;
    }
    
    public boolean WasReIntroduced() {
        return ReIntroduced;
    }
    
    public String GetIntroFaction() {
        return IntroFaction;
    }

    public String GetReIntroFaction() {
        if( ReIntroduced ) {
            return ReIntroFaction;
        } else {
            return "--";
        }
    }

    public boolean IsClan() {
        return Clan;
    }

    public void Combine( AvailableCode a ) {
        // Alters this code from the given AvailableCode.  Mainly used for final
        // availability, does not include faction stuff, only dates and codes.

        if( a.SL > SL ) {
            SL = a.SL;
        }
        if( a.SW > SW ) {
            SW = a.SW;
        }
        if( a.CI > CI ) {
            CI = a.CI;
        }
        if( a.TechRating > TechRating ) {
            TechRating = a.TechRating;
        }
        if( a.Intro > Intro ) {
            Intro = a.Intro;
            IntroFaction = a.GetIntroFaction();
        }
        if( a.Extinct > 0 ) {
            if( Extinct > 0 ) {
                if( a.Extinct < Extinct ) {
                    Extinct = a.Extinct;
                }
            } else {
                Extinct = a.Extinct;
            }
        }
        if( a.ReIntro > ReIntro ) {
            ReIntro = a.ReIntro;
            ReIntroFaction = a.GetReIntroFaction();
        }
        if( a.GetRandDStart() > RandDStart ) {
            RandDStart = a.GetRandDStart();
            RandDFaction = a.GetRandDFaction();
        }

        if( a.WentExtinct ) {
            WentExtinct = true;
        }
        if( a.WasReIntroduced() ) {
            ReIntroduced = true;
        }
        if( a.GetRulesLevelBM() > RulesLevelBM ) {
            RulesLevelBM = a.GetRulesLevelBM();
        }
        if( a.GetRulesLevelIM() > RulesLevelIM ) {
            RulesLevelIM = a.GetRulesLevelIM();
        }
        if( a.GetRulesLevelVee() > RulesLevelVee ) {
            RulesLevelVee = a.GetRulesLevelVee();
        }
        if( a.IsPrototype() ) {
            Prototype = true;
        }

        // double checking routines.
        if( Intro > Extinct ) {
            Extinct = 0;
            ReIntro = 0;
            WentExtinct = false;
            ReIntroduced = false;
            ReIntroFaction = "";
        }
    }

    public String GetShortenedCode() {
        return TechRating + "/" + SL + "-" + SW + "-" + CI;
    }

    // toString
    @Override
    public String toString() {
        String retval = "";
        if( Prototype ) {
            retval = TechRating + "/" + SL + "-" + SW + "-" + CI + ", Intro Date: " + Intro + "P (" + IntroFaction + "), R&D Start Date: " + RandDStart + " (" + RandDFaction + ")";
        } else {
            retval = TechRating + "/" + SL + "-" + SW + "-" + CI + ", Intro Date: " + Intro + " (" + IntroFaction + ")";
        }
        if( WentExtinct ) {
            if( ReIntroduced ) {
                retval += ", Extinct By: " + GetExtinctDate() + ", Reintroduced By: " + GetReIntroDate() + " (" + GetReIntroFaction() + ")";
            } else {
                retval += ", Extinct By: " + GetExtinctDate();
            }
        }
        return retval;
    }
}
