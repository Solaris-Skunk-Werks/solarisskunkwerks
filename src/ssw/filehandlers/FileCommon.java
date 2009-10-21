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

package ssw.filehandlers;

import java.util.Vector;
import java.util.prefs.Preferences;
import components.*;
import ssw.gui.frmMain;

public class FileCommon {
    // contains common filehandler routines (mostly those shared between TXT and
    // HTML exporters).
    public final static String tab = "    ";

    public static String GetSafeFilename( String s ) {
        s = s.replaceAll( "%", "%25" );
        s = s.replaceAll( " ", "%20" );
        s = s.replaceAll( "!", "%21" );
        s = s.replaceAll( "[{(}]", "%28" );
        s = s.replaceAll( "[{)}]", "%29" );
        s = s.replaceAll( "[{;}]", "%3B" );
        s = s.replaceAll( "[{@}]", "%40" );
        s = s.replaceAll( "[{&}]", "%26" );
        s = s.replaceAll( "[{=}]", "%3D" );
        s = s.replaceAll( "[{+}]", "%2B" );
        s = s.replaceAll( "[{$}]", "%24" );
        s = s.replaceAll( "[{?}]", "%3F" );
        s = s.replaceAll( "[{,}]", "%2C" );
        s = s.replaceAll( "[{#}]", "%23" );
        s = s.replaceAll( "[{\\[}]", "%5B" );
        s = s.replaceAll( "[{\\]}]", "%5D" );
        s = s.replaceAll( "[{*}]", "%2A" );
        return s;
    }

    public static String GetInternalLocations( Mech m ) {
        // returns a string formated with the internal structure locations
        String retval = "";
        int[] locs = m.GetLoadout().FindInstances( m.GetIntStruc() );

        if( locs[LocationIndex.MECH_LOC_HD] > 0 ) {
            retval += locs[LocationIndex.MECH_LOC_HD] + " " + EncodeLocation( LocationIndex.MECH_LOC_HD, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_CT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_CT] + " " + EncodeLocation( LocationIndex.MECH_LOC_CT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LT] + " " + EncodeLocation( LocationIndex.MECH_LOC_LT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RT] + " " + EncodeLocation( LocationIndex.MECH_LOC_RT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LA] + " " + EncodeLocation( LocationIndex.MECH_LOC_LA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RA] + " " + EncodeLocation( LocationIndex.MECH_LOC_RA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LL] + " " + EncodeLocation( LocationIndex.MECH_LOC_LL, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RL] + " " + EncodeLocation( LocationIndex.MECH_LOC_RL, m.IsQuad() );
        }
        if( retval.startsWith( "," ) ) {
            retval = retval.substring( 2 );
        }
        return retval;
    }

    public static String GetArmorLocations( Mech m ) {
        // returns a string formated with the internal structure locations
        String retval = "";
        int[] locs = m.GetLoadout().FindInstances( m.GetArmor() );

        if( locs[LocationIndex.MECH_LOC_HD] > 0 ) {
            retval += locs[LocationIndex.MECH_LOC_HD] + " " + EncodeLocation( LocationIndex.MECH_LOC_HD, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_CT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_CT] + " " + EncodeLocation( LocationIndex.MECH_LOC_CT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LT] + " " + EncodeLocation( LocationIndex.MECH_LOC_LT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RT] + " " + EncodeLocation( LocationIndex.MECH_LOC_RT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LA] + " " + EncodeLocation( LocationIndex.MECH_LOC_LA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RA] + " " + EncodeLocation( LocationIndex.MECH_LOC_RA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LL] + " " + EncodeLocation( LocationIndex.MECH_LOC_LL, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RL] + " " + EncodeLocation( LocationIndex.MECH_LOC_RL, m.IsQuad() );
        }
        if( retval.startsWith( "," ) ) {
            retval = retval.substring( 2 );
        }
        return retval;
    }

    public static String GetHeatSinkLocations( Mech m ) {
        // returns a string formated with the internal structure locations
        String retval = "";
        int[] locs = m.GetLoadout().FindHeatSinks();

        if( locs[LocationIndex.MECH_LOC_HD] > 0 ) {
            retval += locs[LocationIndex.MECH_LOC_HD] + " " + EncodeLocation( LocationIndex.MECH_LOC_HD, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_CT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_CT] + " " + EncodeLocation( LocationIndex.MECH_LOC_CT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LT] + " " + EncodeLocation( LocationIndex.MECH_LOC_LT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RT] + " " + EncodeLocation( LocationIndex.MECH_LOC_RT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LA] + " " + EncodeLocation( LocationIndex.MECH_LOC_LA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RA] + " " + EncodeLocation( LocationIndex.MECH_LOC_RA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LL] + " " + EncodeLocation( LocationIndex.MECH_LOC_LL, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RL] + " " + EncodeLocation( LocationIndex.MECH_LOC_RL, m.IsQuad() );
        }
        if( retval.startsWith( "," ) ) {
            retval = retval.substring( 2 );
        }
        return retval;
    }

    public static String GetJumpJetLocations( Mech m ) {
        // returns a string formated with the internal structure locations
        String retval = "";
        int[] locs = m.GetLoadout().FindJumpJets( m.GetJumpJets().IsImproved() );

        if( locs[LocationIndex.MECH_LOC_HD] > 0 ) {
            retval += locs[LocationIndex.MECH_LOC_HD] + " " + EncodeLocation( LocationIndex.MECH_LOC_HD, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_CT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_CT] + " " + EncodeLocation( LocationIndex.MECH_LOC_CT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LT] + " " + EncodeLocation( LocationIndex.MECH_LOC_LT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RT] + " " + EncodeLocation( LocationIndex.MECH_LOC_RT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LA] + " " + EncodeLocation( LocationIndex.MECH_LOC_LA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RA] + " " + EncodeLocation( LocationIndex.MECH_LOC_RA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LL] + " " + EncodeLocation( LocationIndex.MECH_LOC_LL, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RL] + " " + EncodeLocation( LocationIndex.MECH_LOC_RL, m.IsQuad() );
        }
        if( retval.startsWith( "," ) ) {
            retval = retval.substring( 2 );
        }
        return retval;
    }

    public static String GetTSMLocations( Mech m ) {
        // returns a string formated with the internal structure locations
        String retval = "";
        if( ! m.GetPhysEnhance().IsTSM() ) { return ""; }

        int[] locs = m.GetLoadout().FindInstances( m.GetPhysEnhance() );

        if( locs[LocationIndex.MECH_LOC_HD] > 0 ) {
            retval += locs[LocationIndex.MECH_LOC_HD] + " " + EncodeLocation( LocationIndex.MECH_LOC_HD, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_CT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_CT] + " " + EncodeLocation( LocationIndex.MECH_LOC_CT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LT] + " " + EncodeLocation( LocationIndex.MECH_LOC_LT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RT] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RT] + " " + EncodeLocation( LocationIndex.MECH_LOC_RT, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LA] + " " + EncodeLocation( LocationIndex.MECH_LOC_LA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RA] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RA] + " " + EncodeLocation( LocationIndex.MECH_LOC_RA, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_LL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_LL] + " " + EncodeLocation( LocationIndex.MECH_LOC_LL, m.IsQuad() );
        }
        if( locs[LocationIndex.MECH_LOC_RL] > 0 ) {
            retval += ", " + locs[LocationIndex.MECH_LOC_RL] + " " + EncodeLocation( LocationIndex.MECH_LOC_RL, m.IsQuad() );
        }
        if( retval.startsWith( "," ) ) {
            retval = retval.substring( 2 );
        }
        return retval;
    }

    public static String BuildActuators( Mech m, boolean web ) {
        // builds a string that explains what arm actuators the mech has
        if( m.IsQuad() ) {
            if( web ) {
                return "L: H+UL+LL+F&nbsp&nbsp&nbsp R: H+UL+LL+F";
            } else {
                return "L: H+UL+LL+F    R: H+UL+LL+F";
            }
        }
        String retval;
        String left = "L: SH+UA";
        String right = "R: SH+UA";

        if( m.GetActuators().LeftLowerInstalled() ) {
            left += "+LA";
            if( m.GetActuators().LeftHandInstalled() ) {
                left += "+H";
            }
        }
        if( m.GetActuators().RightLowerInstalled() ) {
            right += "+LA";
            if( m.GetActuators().RightHandInstalled() ) {
                right += "+H";
            }
        }

        if( web ) {
            retval = left + "&nbsp&nbsp&nbsp " + right;
        } else {
            retval = left + "    " + right;
        }
        return retval;
    }

    public static abPlaceable HasECM( Mech m ) {
        ifMechLoadout l = m.GetLoadout();
        Vector v = l.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            if( ((abPlaceable) v.get( i )).LookupName().contains( "ECM Suite" ) || ((abPlaceable) v.get( i )).LookupName().contains( "CEWS" ) ) {
                return (abPlaceable) v.get( i );
            }
        }
        return new EmptyItem();
    }

    public static abPlaceable HasBAP( Mech m ) {
        ifMechLoadout l = m.GetLoadout();
        Vector v = l.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            if( ((abPlaceable) v.get( i )).LookupName().contains( "Probe" ) || ((abPlaceable) v.get( i )).LookupName().contains( "CEWS" ) ) {
                return (abPlaceable) v.get( i );
            }
        }
        return new EmptyItem();
    }

    public static Object[] HasC3( Mech m ) {
        // this one returns a full array because a mech may have more than one
        // C3 computer in it.
        Vector retval = new Vector();
        Vector v = m.GetLoadout().GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            if( ((abPlaceable) v.get( i )).LookupName().contains( "C3" ) ) {
                retval.add( v.get( i ) );
            }
        }

        if( retval.size() <= 0 ) {
            return null;
        } else {
            return retval.toArray();
        }
    }

    public static String GetCaseLocations( Mech m ) {
        String retval = "";
        if( m.GetLoadout().GetTechBase() >= AvailableCode.TECH_CLAN && m.GetLoadout().IsUsingClanCASE() ) {
            int[] check = m.GetLoadout().FindExplosiveInstances();
            if( check[LocationIndex.MECH_LOC_HD] > 0 ) {
                retval += "HD";
            }
            if( check[LocationIndex.MECH_LOC_CT] > 0 ) {
                retval += ", CT";
            }
            if( check[LocationIndex.MECH_LOC_LT] > 0 ) {
                retval += ", LT";
            }
            if( check[LocationIndex.MECH_LOC_RT] > 0 ) {
                retval += ", RT";
            }
            if( check[LocationIndex.MECH_LOC_LA] > 0 ) {
                if( m.IsQuad() ) {
                    retval += ", FLL";
                } else {
                    retval += ", LA";
                }
            }
            if( check[LocationIndex.MECH_LOC_RA] > 0 ) {
                if( m.IsQuad() ) {
                    retval += ", FRL";
                } else {
                    retval += ", RA";
                }
            }
            if( check[LocationIndex.MECH_LOC_LL] > 0 ) {
                if( m.IsQuad() ) {
                    retval += ", RLL";
                } else {
                    retval += ", LL";
                }
            }
            if( check[LocationIndex.MECH_LOC_RL] > 0 ) {
                if( m.IsQuad() ) {
                    retval += ", RRL";
                } else {
                    retval += ", RL";
                }
            }
        } else {
            if( m.HasCTCase() ) {
                retval += "1 CT";
            }
            if( m.HasLTCase() ) {
                retval += ", 1 LT";
            }
            if( m.HasRTCase() ) {
                retval += ", 1 RT";
            }
        }
        if( retval.startsWith( "," ) ) {
            return retval.substring( 2 );
        } else {
            return retval;
        }
    }

    public static String GetCaseIILocations( Mech m ) {
        String retval = "";

        if( m.GetLoadout().HasHDCASEII() ) {
            retval += "1 HD";
        }
        if( m.GetLoadout().HasCTCASEII() ) {
            retval += ", 1 CT";
        }
        if( m.GetLoadout().HasLTCASEII() ) {
            retval += ", 1 LT";
        }
        if( m.GetLoadout().HasRTCASEII() ) {
            retval += ", 1 RT";
        }
        if( m.GetLoadout().HasLACASEII() ) {
            if( m.IsQuad() ) {
                retval += ", 1 FLL";
            } else {
                retval += ", 1 LA";
            }
        }
        if( m.GetLoadout().HasRACASEII() ) {
            if( m.IsQuad() ) {
                retval += ", 1 FRL";
            } else {
                retval += ", 1 RA";
            }
        }
        if( m.GetLoadout().HasLLCASEII() ) {
            if( m.IsQuad() ) {
                retval += ", 1 RLL";
            } else {
                retval += ", 1 LL";
            }
        }
        if( m.GetLoadout().HasRLCASEII() ) {
            if( m.IsQuad() ) {
                retval += ", 1 RRL";
            } else {
                retval += ", 1 RL";
            }
        }
        if( retval.startsWith( "," ) ) {
            return retval.substring( 2 );
        } else {
            return retval;
        }
    }

    public static String EncodeLocation( int loc, boolean quad ) {
        switch( loc ) {
        case LocationIndex.MECH_LOC_HD:
            return "HD";
        case LocationIndex.MECH_LOC_CT:
            return "CT";
        case LocationIndex.MECH_LOC_LT:
            return "LT";
        case LocationIndex.MECH_LOC_RT:
            return "RT";
        case LocationIndex.MECH_LOC_LA:
            if( quad ) {
                return "FLL";
            } else {
                return "LA";
            }
        case LocationIndex.MECH_LOC_RA:
            if( quad ) {
                return "FRL";
            } else {
                return "RA";
            }
        case LocationIndex.MECH_LOC_LL:
            if( quad ) {
                return "RLL";
            } else {
                return "LL";
            }
        case LocationIndex.MECH_LOC_RL:
            if( quad ) {
                return "RRL";
            } else {
                return "RL";
            }
        default:
            return "??";
        }
    }

    public static String EncodeLocations( int[] check, boolean quad ) {
        // this will work initializing to 0 since you cannot split into the head
        // and no contiguous item can be split into more than two locations.

        String retval = "";
        int loc1 = 0;
        int loc2 = 0;

        for( int i = 0; i < check.length; i++ ) {
            if( check[i] > 0 ) {
                if( loc1 > 0 ) {
                    loc2 = i;
                } else {
                    loc1 = i;
                }
            }
        }

        if( loc2 == 0 ) {
            // only took up one location
            retval = EncodeLocation( loc1, quad );
        } else {
            // two locations
            retval = EncodeLocation( loc1, quad ) + "/" + EncodeLocation( loc2, quad );
        }

        return retval;
    }

    public static String DecodeCrits( int[] check ) {
        String retval = "";
        int loc1 = 0;
        int loc2 = 0;

        for( int i = 0; i < check.length; i++ ) {
            if( check[i] > 0 ) {
                if( loc1 > 0 ) {
                    loc2 = check[i];
                } else {
                    loc1 = check[i];
                }
            }
        }

        if( loc2 == 0 ) {
            // only took up one location
            retval = "" + loc1;
        } else {
            // two locations
            retval = loc1 + "/" + loc2;
        }

        return retval;
    }

    public static Vector SortEquipmentForStats( Mech m, Vector v ) {
        // this routine takes the given vector of equipment and sorts it by
        // location, starting with the head and working down to legs.
        Vector[] sort = { new Vector(), new Vector(), new Vector(), new Vector(),
            new Vector(), new Vector(), new Vector(), new Vector() };
        Vector retval = new Vector();
        boolean ExportOut = m.GetPrefs().getBoolean( "ExportSortOut", false );
        boolean AmmoEnd = m.GetPrefs().getBoolean( "AmmoGroupAtBottom", true );

        // for each item in the given vector, find it's innermost location and
        // place it into the appropriate vector
        int index = 0;
        abPlaceable a;
        for( int i = 0; i < v.size(); i++ ) {
            a = (abPlaceable) v.get( i );
            index = m.GetLoadout().Find( a );
            if( ! ( index < 0 || index > 7 ) ) {
                sort[index].add( a );
            }
        }

        // for each vector that has items, add them to the return vector in
        // the correct order, based on options.
        if( ExportOut ) {
            if( sort[0].size() > 0 ) {
                for( int j = 0; j < sort[0].size(); j++ ) {
                    retval.add( sort[0].get( j ) );
                }
            }
            if( sort[1].size() > 0 ) {
                for( int j = 0; j < sort[1].size(); j++ ) {
                    retval.add( sort[1].get( j ) );
                }
            }
            if( sort[3].size() > 0 ) {
                for( int j = 0; j < sort[3].size(); j++ ) {
                    retval.add( sort[3].get( j ) );
                }
            }
            if( sort[2].size() > 0 ) {
                for( int j = 0; j < sort[2].size(); j++ ) {
                    retval.add( sort[2].get( j ) );
                }
            }
            if( sort[5].size() > 0 ) {
                for( int j = 0; j < sort[5].size(); j++ ) {
                    retval.add( sort[5].get( j ) );
                }
            }
            if( sort[4].size() > 0 ) {
                for( int j = 0; j < sort[4].size(); j++ ) {
                    retval.add( sort[4].get( j ) );
                }
            }
            if( sort[7].size() > 0 ) {
                for( int j = 0; j < sort[7].size(); j++ ) {
                    retval.add( sort[7].get( j ) );
                }
            }
            if( sort[6].size() > 0 ) {
                for( int j = 0; j < sort[6].size(); j++ ) {
                    retval.add( sort[6].get( j ) );
                }
            }
        } else {
            if( sort[5].size() > 0 ) {
                for( int j = 0; j < sort[5].size(); j++ ) {
                    retval.add( sort[5].get( j ) );
                }
            }
            if( sort[4].size() > 0 ) {
                for( int j = 0; j < sort[4].size(); j++ ) {
                    retval.add( sort[4].get( j ) );
                }
            }
            if( sort[3].size() > 0 ) {
                for( int j = 0; j < sort[3].size(); j++ ) {
                    retval.add( sort[3].get( j ) );
                }
            }
            if( sort[2].size() > 0 ) {
                for( int j = 0; j < sort[2].size(); j++ ) {
                    retval.add( sort[2].get( j ) );
                }
            }
            if( sort[1].size() > 0 ) {
                for( int j = 0; j < sort[1].size(); j++ ) {
                    retval.add( sort[1].get( j ) );
                }
            }
            if( sort[0].size() > 0 ) {
                for( int j = 0; j < sort[0].size(); j++ ) {
                    retval.add( sort[0].get( j ) );
                }
            }
            if( sort[7].size() > 0 ) {
                for( int j = 0; j < sort[7].size(); j++ ) {
                    retval.add( sort[7].get( j ) );
                }
            }
            if( sort[6].size() > 0 ) {
                for( int j = 0; j < sort[6].size(); j++ ) {
                    retval.add( sort[6].get( j ) );
                }
            }
        }

        // if we need to put ammunition at the end, do it now.
        if( AmmoEnd ) {
            Vector Ammo = new Vector();
            for( int i = retval.size() - 1; i >= 0; i-- ) {
                if( retval.get( i ) instanceof Ammunition ) {
                    Ammo.add( retval.remove( i ) );
                }
            }

            // add the ammunition back in.  Do it in reverse order since that
            // was how the vector was built.
            for( int i = Ammo.size() - 1; i >= 0; i-- ) {
                retval.add( Ammo.remove( i ) );
            }
        }

        return retval;
    }

    public static String EncodeFluff( String s ) {
        String retval = s.replaceAll( "&", "&amp;" );
        retval = retval.replaceAll( "<", "&lt;" );
        retval = retval.replaceAll( ">", "&gt;" );
        retval = retval.replaceAll( "\'", "&apos;" );
        retval = retval.replaceAll( "\"", "&quot;" );
        retval = retval.replaceAll( "\n\r", ":br:" );
        retval = retval.replaceAll( "\n", ":br:" );
        retval = retval.replaceAll( "\r", ":br:" );
        return retval;
    }

    public static String DecodeFluff( String s ) {
        String retval = s.replaceAll( "&amp;", "&" );
        retval = retval.replaceAll( "&lt;", "<" );
        retval = retval.replaceAll( "&gt;", ">" );
        retval = retval.replaceAll( "&apos;", "\'" );
        retval = retval.replaceAll( "&quot;", "\"" );
        retval = retval.replaceAll( ":br:", "\n" );
        return retval;
    }

    public static String FormatFluffHTML( String s ) {
        String retval = s.replaceAll( "\n", "<br />" );
        return retval;
    }

    public static String FormatFluffTXT( String s ) {
        String retval = "";
        return retval;
    }

    public static int DecodeLocation( String s ) {
        if( s.equals( "HD" ) ) {
            return LocationIndex.MECH_LOC_HD;
        } else if( s.equals( "CT" ) ) {
            return LocationIndex.MECH_LOC_CT;
        } else if( s.equals( "LT" ) ) {
            return LocationIndex.MECH_LOC_LT;
        } else if( s.equals( "RT" ) ) {
            return LocationIndex.MECH_LOC_RT;
        } else if( s.equals( "LA" ) ) {
            return LocationIndex.MECH_LOC_LA;
        } else if( s.equals( "RA" ) ) {
            return LocationIndex.MECH_LOC_RA;
        } else if( s.equals( "LL" ) ) {
            return LocationIndex.MECH_LOC_LL;
        } else if( s.equals( "RL" ) ) {
            return LocationIndex.MECH_LOC_RL;
        } else if( s.equals( "FLL" ) ) {
            return LocationIndex.MECH_LOC_LA;
        } else if( s.equals( "FRL" ) ) {
            return LocationIndex.MECH_LOC_RA;
        } else if( s.equals( "RLL" ) ) {
            return LocationIndex.MECH_LOC_LL;
        } else if( s.equals( "RRL" ) ) {
            return LocationIndex.MECH_LOC_RL;
        } else {
            return -1;
        }
    }

    public static String FormatAmmoPrintName( Ammunition a, int tons ) {
        // this routine returns a user-defined ammunition name based on a user-
        // defined ammunition filter.
        Preferences Prefs = Preferences.userNodeForPackage( frmMain.class );
        String retval = Prefs.get( "AmmoNamePrintFormat", "@%P (%L)" );
        retval = retval.replace( "%F", a.LookupName().replace( "@ ", "" ) );
        retval = retval.replace( "%P", a.CritName().replace( "@ ", "" ) );
        retval = retval.replace( "%F", a.LookupName().replace( "@", "" ) );
        retval = retval.replace( "%P", a.CritName().replace( "@", "" ) );
        if( tons > 1 ) {
            retval = retval.replace( "%L", "" + ( a.GetLotSize() * tons ) );
        } else {
            retval = retval.replace( "%L", "" + a.GetLotSize() );
        }
        return retval;
    }

    public static String FormatAmmoExportName( Ammunition a, int tons ) {
        // this routine returns a user-defined ammunition name based on a user-
        // defined ammunition filter.
        Preferences Prefs = Preferences.userNodeForPackage( frmMain.class );
        String retval = Prefs.get( "AmmoNameExportFormat", "@%P (%L)" );
        retval = retval.replace( "%F", a.ActualName().replace( "@ ", "" ) );
        retval = retval.replace( "%P", a.CritName().replace( "@ ", "" ) );
        retval = retval.replace( "%F", a.ActualName().replace( "@", "" ) );
        retval = retval.replace( "%P", a.CritName().replace( "@", "" ) );
        if( tons > 1 ) {
            retval = retval.replace( "%L", "" + ( a.GetLotSize() * tons ) );
        } else {
            retval = retval.replace( "%L", "" + a.GetLotSize() );
        }
        return retval;
    }

    public static String LookupStripArc( String s ) {
        // removes the arc from the lookupname for more precise grouping
        String retval = "";
        if( s.length() > 3 ) {
            if( s.substring( 0, 4 ).equals( "(R) " ) || s.substring( 0, 4 ).equals( "(F) " ) ) {
                retval = s.substring( 4 );
            } else if( s.substring( 0, 5 ).equals( "(RS) " ) || s.substring( 0, 5 ).equals( "(FS) " ) ) {
                retval = s.substring( 5 );
            } else {
                retval = s;
            }
        } else {
            retval = s;
        }
        return retval;
    }
}