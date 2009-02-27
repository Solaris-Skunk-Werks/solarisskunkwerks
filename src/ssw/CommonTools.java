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

package ssw;

import ssw.components.*;

public class CommonTools {
    private final static float[][] BVMods = {
        { 2.8f, 2.63f, 2.45f, 2.28f, 2.01f, 1.82f, 1.75f, 1.67f, 1.59f },
        { 2.56f, 2.4f, 2.24f, 2.08f, 1.84f, 1.6f, 1.58f, 1.51f, 1.44f },
        { 2.24f, 2.1f, 1.96f, 1.82f, 1.61f, 1.4f, 1.33f, 1.31f, 1.25f },
        { 1.92f, 1.8f, 1.68f, 1.56f, 1.38f, 1.2f, 1.14f, 1.08f, 1.06f },
        { 1.6f, 1.5f, 1.4f, 1.3f, 1.15f, 1.0f, 0.95f, 0.9f, 0.85f },
        { 1.5f, 1.35f, 1.26f, 1.17f, 1.04f, 0.9f, 0.86f, 0.81f, 0.77f },
        { 1.43f, 1.33f, 1.19f, 1.11f, 0.98f, 0.85f, 0.81f, 0.77f, 0.72f },
        { 1.36f, 1.26f, 1.16f, 1.04f, 0.92f, 0.8f, 0.76f, 0.72f, 0.68f },
        { 1.28f, 1.19f, 1.1f, 1.01f, 0.86f, 0.75f, 0.71f, 0.68f, 0.64f }
    };

    public static String GetRulesLevelString( int level ) {
        switch( level ) {
            case Constants.TOURNAMENT:
                return "Tournament Legal";
            case Constants.ADVANCED:
                return "Advanced";
            case Constants.EXPERIMENTAL:
                return "Experimental";
            case Constants.UNALLOWED:
                return "Unallowed";
            default:
                return "Unknown";
        }
    }

    public static String GetTechbaseString( int tech ) {
        switch( tech ) {
            case Constants.CLAN:
                return "Clan";
            case Constants.INNER_SPHERE:
                return "Inner Sphere";
            default:
                return "Unknown";
        }
    }

    public static String GetAggregateReportBV( abPlaceable p ) {
        // since an item may have both offensive and defensive BV, this gives us
        // an aggregate battle value string for reporting
        String result = "";
        if( p.GetOffensiveBV() > 0.0f ) {
            result += p.GetOffensiveBV() + "";
            if( p.GetDefensiveBV() > 0.0f ) {
                result += " / " + p.GetDefensiveBV() + "(D)";
            }
        } else if( p.GetDefensiveBV() > 0.0f ) {
            result += p.GetDefensiveBV() + "(D)";
        } else {
            result += "0";
        }
        return result;
    }

    public static boolean IsAllowed( AvailableCode AC, Mech m ) {
        // check an available code to see if it can be used legally

        // ensure it's within our rules-level first
        switch( m.GetRulesLevel() ) {
        case Constants.TOURNAMENT:
            // tournament legal
            if( m.IsIndustrialmech() ) {
                if( AC.GetRulesLevelIM() != Constants.TOURNAMENT ) { return false; }
            } else {
                if( AC.GetRulesLevelBM() != Constants.TOURNAMENT ) { return false; }
            }
            break;
        case Constants.ADVANCED:
            // advanced rules
            if( m.IsIndustrialmech() ) {
                if( AC.GetRulesLevelIM() > Constants.ADVANCED || AC.GetRulesLevelIM() < Constants.TOURNAMENT ) { return false; }
            } else {
                if( AC.GetRulesLevelBM() > Constants.ADVANCED || AC.GetRulesLevelBM() < Constants.TOURNAMENT ) { return false; }
            }
            break;
        default:
            // experimental rules.  everything allowed.
            if( m.IsIndustrialmech() ) {
                if( AC.GetRulesLevelIM() > Constants.EXPERIMENTAL || AC.GetRulesLevelIM() < Constants.TOURNAMENT ) { return false; }
            } else {
                if( AC.GetRulesLevelBM() > Constants.EXPERIMENTAL || AC.GetRulesLevelBM() < Constants.TOURNAMENT ) { return false; }
            }
            break;
        }

        // is this within our techbase?
        if( AC.IsClan() ) {
            if( ! m.IsClan() ) { return false; }
        } else {
            if( m.IsClan() ) { return false; }
        }

        // are we restricting by year?
        if( m.IsYearRestricted() ) {
            // we are.
            if( AC.WentExtinct() ) {
                if( AC.WasReIntroduced() ) {
                    if( ( m.GetYear() >= AC.GetIntroDate() && m.GetYear() < AC.GetExtinctDate() ) || m.GetYear() >= AC.GetReIntroDate() ) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if( m.GetYear() >= AC.GetIntroDate() && m.GetYear() < AC.GetExtinctDate() ) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                if( m.GetYear() >= AC.GetIntroDate() ) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            // we aren't, go by the era
            switch( m.GetEra() ) {
            case 0:
                if( AC.GetSLCode() < 'X' ) {
                    return true;
                } else {
                    return false;
                }
            case 1:
                if( m.IsClan() ) {
                    if( AC.GetSWCode() < 'X' ) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if( AC.GetSWCode() < 'F' ) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case 2:
                if( m.IsClan() ) {
                    if( AC.GetCICode() < 'X' ) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if( AC.GetCICode() < 'X' ) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case 3:
                // the "All" era.
                return true;
            }
        }

        return false;
    }

/*    public static boolean CheckExclusions( abPlaceable a, Mech m ) throws Exception {
        if( a == null ) { return false; }

        // initialize
        Vector v;

        // See if it requires a specific engine
        if( a instanceof ifWeapon ) {
            if( ((ifWeapon) a).RequiresNuclear() &! m.GetEngine().IsNuclear() ) {
                throw new Exception( a.GetCritName() + " may not be mounted because the mech\ndoes not use a nuclear engine (fission or fusion)." );
            }
            if( ((ifWeapon) a).RequiresFusion() &! m.GetEngine().IsFusion() ) {
                throw new Exception( a.GetCritName() + " may not be mounted\nbecause the mech does not use a fusion engine." );
            }
        }
        // does it require certain actuators?
        if( a instanceof PhysicalWeapon ) {
            if( m.IsQuad() ) {
                if ( ((PhysicalWeapon)a).GetPWClass() != Constants.PW_CLASS_TALON )
                    throw new Exception( "A quad mech has no hand or lower arm actuators\nand may not mount physical weapons." );
            }
            // check to ensure that no more than two physical weapons are in the mech
            // unless they are spikes which are allowed up to eight
            // may have only one set of talons
            v = m.GetLoadout().GetNonCore();
            int pcheck = 0;
            int spikecheck = 0;
            int shieldcheck = 0;
            int taloncheck = 0;
            for( int i = 0; i < v.size(); i++ ) {
                if( v.get( i ) instanceof PhysicalWeapon ) {
                    if ( ((PhysicalWeapon)v.get(i)).GetPWClass() == ssw.Constants.PW_CLASS_SPIKE )
                        spikecheck++;
                    else if ( ((PhysicalWeapon)v.get(i)).GetPWClass() == ssw.Constants.PW_CLASS_SHIELD )
                        shieldcheck++;
                    else if ( ((PhysicalWeapon)v.get(i)).GetPWClass() == ssw.Constants.PW_CLASS_TALON )
                        taloncheck++;
                    else
                        pcheck++;
                }
            }
            if( ((PhysicalWeapon)a).GetPWClass() == ssw.Constants.PW_CLASS_NORMAL && pcheck >= 2 ) {
                throw new Exception(  "A mech may mount no more than two physical weapons." );
            }
            else if( ((PhysicalWeapon)a).GetPWClass() == ssw.Constants.PW_CLASS_SPIKE && spikecheck >= 8 ) {
                throw new Exception(  "A mech may mount no more than eight spikes, one per location." );
            }
            else if ( ((PhysicalWeapon)a).GetPWClass() == ssw.Constants.PW_CLASS_SHIELD && shieldcheck >= 2 ) {
                throw new Exception(  "A mech may mount no more than two shields." );
            }
            else if ( ((PhysicalWeapon)a).GetPWClass() == ssw.Constants.PW_CLASS_TALON && taloncheck >= 1 ) {
                throw new Exception(  "A mech may mount only one set of talons." );
            }
        }
        else if(a instanceof IndustrialEquipment){
            if ( !((IndustrialEquipment) a).validate(m)){
                throw new Exception( ((IndustrialEquipment) a).getValidationFalseMessage());
            }
        }
        // do we have equipment exclusions?
        if( a.GetExclusions() != null ) {
            try {
                m.GetLoadout().CheckExclusions( a );
            } catch( Exception e ) {
                throw e;
            }
        }

        // we haven't thrown any exceptions, must be fine
        return true;
    }*/

    public static float GetAdjustedBV( float BV, int Gunnery, int Piloting ) {
        return BV * BVMods[Gunnery][Piloting];
    }

    public static String DecodeEra( int era ) {
        switch( era ) {
            case Constants.STAR_LEAGUE:
                return Constants.strSTAR_LEAGUE;
            case Constants.SUCCESSION:
                return Constants.strSUCCESSION;
            case Constants.CLAN_INVASION:
                return Constants.strCLAN_INVASION;
            case Constants.ALL_ERA:
                return Constants.strALL_ERA;
            default:
                return "Unknown";
        }
    }
}
