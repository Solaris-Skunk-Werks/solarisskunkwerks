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

package filehandlers;

import components.*;
import common.*;
import battleforce.BattleForceStats;
import visitors.VMechFullRecalc;
import visitors.ifVisitor;
import list.UnitListData;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.XMLConstants;
import org.w3c.dom.*;
import visitors.VArmorSetPatchwork;
import visitors.VArmorSetPatchworkLocation;

public class CVReader {
    DataFactory data;
    Document load;
    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    int SaveFileVersion = 1;
    String Messages = "";

    public CVReader() throws Exception {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        dbf.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
        dbf.setExpandEntityReferences(false);
        db = dbf.newDocumentBuilder();
    }

/**
 * This version of ReadCombatVehicle is used when frmMain AND DataFactory are NOT present.
 * This method has been provided for external programs using the SSW code base.
 *
 * @param filename The canonical filename to load this 'CombatVehicle from.
 * @return The specified 'CombatVehicle
 * @throws java.lang.Exception Throws a variety of exceptions that should explain
 *                             what went wrong while loading the 'CombatVehicle.
 */
    public CombatVehicle ReadUnit( String filename ) throws Exception {
        data = null;
        CombatVehicle retval = new CombatVehicle();
        filename = CommonTools.GetSafeFilename( filename );
        load = db.parse( filename );

        retval = BuildVehicle( retval, load, data );
        return retval;
    }

 /**
 * This version of ReadCombatVehicle is used when frmMain is NOT present.  This method has
 * been provided for external programs using the SSW code base.
 * 
 * @param filename The canonical filename to load this 'CombatVehicle from.
 * @param f The DataFactory to use.  This can be set to null, in which case it
 *          will load a new DataFactory from file.
 * @return The specified 'CombatVehicle
 * @throws java.lang.Exception Throws a variety of exceptions that should explain
 *                             what went wrong while loading the 'CombatVehicle.
 */
    public CombatVehicle ReadUnit( String filename, DataFactory f ) throws Exception {
        CombatVehicle retval = new CombatVehicle();
        filename = CommonTools.GetSafeFilename( filename );
        load = db.parse( filename );

        retval = BuildVehicle( retval, load, f );
        return retval;
    }

/**
 * ReadCombatVehicleData is used for loading basic CombatVehicle info from a saved CombatVehicle file.  This
 * should be used only when the basics of a 'CombatVehicle are needed, not the entire
 * thing (cost, BV, tonnage, techbase, production year, etc...)
 * 
 * @param filename The 'CombatVehicle file to collect the data from
 * @return A completed CombatVehicleListData containing the information
 * @throws java.lang.Exception Throws a variety of exceptions that should explain
 *                             what went wrong while loading the 'CombatVehicle data.
 */
    public UnitListData ReadUnitData( String filename, String basePath ) throws Exception {
        UnitListData mData = new UnitListData();
        mData.setFilename(filename.replace(basePath, ""));
        filename = CommonTools.GetSafeFilename( filename );
        load = db.parse( filename );

        return BuildData( mData, load );
    }

    private UnitListData BuildData( UnitListData Data, Document d ) {
        NodeList n = d.getElementsByTagName( "CombatVehicle" );
        NamedNodeMap map = n.item( 0 ).getAttributes();

        boolean isOmni = ParseBoolean( map.getNamedItem( "omniCombatVehicle" ).getTextContent() );
        Data.setOmni( isOmni );

        Data.setName( FileCommon.DecodeFluff( map.getNamedItem( "name" ).getTextContent() ) );
        Data.setModel( FileCommon.DecodeFluff( map.getNamedItem( "model" ).getTextContent() ) );
        Data.setTonnage( Integer.parseInt( map.getNamedItem( "tons" ).getTextContent() ) );

        n = d.getElementsByTagName( "rules_level" );
        Data.setLevel( CommonTools.GetRulesLevelString( Integer.parseInt( n.item( 0 ).getTextContent() ) ) );

        n = d.getElementsByTagName( "era" );
        Data.setEra( CommonTools.DecodeEra( Integer.parseInt( n.item( 0 ).getTextContent() ) ) );

        n = d.getElementsByTagName( "techbase" );
        Data.setTech( n.item( 0 ).getTextContent() );

        n = d.getElementsByTagName( "year" );
        Data.setYear( Integer.parseInt( n.item( 0 ).getTextContent() ) );

        n = d.getElementsByTagName( "source" );
        if( n.getLength() > 0 ) {Data.setSource( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );}

        n = d.getElementsByTagName( "CombatVehicle_type" );
        Data.setType( n.item( 0 ).getTextContent() );

        n = d.getElementsByTagName( "motive_type" );
        Data.setMotive( n.item( 0 ).getTextContent() );

        n = d.getElementsByTagName( "info" );
        if ( n.getLength() > 0 ) { Data.setInfo( n.item( 0 ).getTextContent() ); }

        n = d.getElementsByTagName( "battleforce" );
        if ( n.getLength() > 0 ) {
            try {
                Data.setBattleForceStats( new BattleForceStats(n.item(0)) );
                Data.getBattleForceStats().setElement(Data.getFullName());
            } catch ( Exception e ) {
                System.out.println(e.getMessage());
            }
        }

        n = d.getElementsByTagName( "battle_value" );
        if (n.getLength() >= 1) Data.setBV( Integer.parseInt( n.item(0).getTextContent() ) );

        n = d.getElementsByTagName( "cost" );
        if (n.getLength() >= 1) Data.setCost( Double.parseDouble( n.item(0).getTextContent() ) );

        if( isOmni ) {
            NodeList OmniLoads = d.getElementsByTagName( "loadout" );
            for( int k = 0; k < OmniLoads.getLength(); k++ ) {
                UnitListData Config = new UnitListData(Data);
                Config.setOmni(true);
                map = OmniLoads.item( k ).getAttributes();
                if( map.getNamedItem( "name" ) != null ) {
                    Config.setName( Config.getName());
                    Config.setModel( Config.getModel() );
                    Config.setConfig(FileCommon.DecodeFluff( map.getNamedItem( "name" ).getTextContent() ));
                }
                if( map.getNamedItem( "ruleslevel" ) != null ) {
                    Config.setLevel( CommonTools.GetRulesLevelString( Integer.parseInt( map.getNamedItem( "ruleslevel" ).getTextContent() ) ) );
                }

                n = OmniLoads.item( k ).getChildNodes();
                for ( int dex=0; dex < n.getLength(); dex++ ) {
                    Node node = n.item(dex);
                    if (node.getNodeName().equals("techbase")) {Config.setTech( node.getTextContent() );}
                    if (node.getNodeName().equals("loadout_era")) {Config.setEra( CommonTools.DecodeEra( Integer.parseInt( node.getTextContent() ) ) );}
                    if (node.getNodeName().equals("loadout_year")) {Config.setYear( Integer.parseInt( node.getTextContent() ) );}
                    if (node.getNodeName().equals("battle_value")) {Config.setBV( Integer.parseInt( node.getTextContent() ) );}
                    if (node.getNodeName().equals("cost")) {Config.setCost( Double.parseDouble( node.getTextContent() ) );}
                    if (node.getNodeName().equals("source")) {Config.setSource( node.getTextContent() );}
                    if (node.getNodeName().equals("info")) {Config.setInfo( node.getTextContent() );}
                    try {
                        if (node.getNodeName().equals("battleforce")) {Config.setBattleForceStats( new BattleForceStats( node ) );}
                    } catch ( Exception e ) {
                        System.out.println(e.getMessage());
                    }
                }

                Data.Configurations.add(Config);
            }
        }        
        return Data;
    }

    public CombatVehicle ReadUnit( Node n ) throws Exception {
        Messages = "";
        CombatVehicle m = new CombatVehicle();
        data = new DataFactory(m);
        Document d = db.newDocument();
        Node newNode = d.importNode(n, true);
        d.appendChild(newNode);

        BuildVehicle(m, d, data);
        
        return m;
    }

    private CombatVehicle BuildVehicle( CombatVehicle m, Document d, DataFactory f ) throws Exception {
        Messages = "";
        if( f == null ) {
            data = new DataFactory( m );
        } else {
            data = f;
        }

        NodeList n = d.getElementsByTagName( "combatvehicle" );

        NamedNodeMap map = n.item( 0 ).getAttributes();
        LocationIndex l;
        ArrayList isLoc = new ArrayList();
        ArrayList armLoc = new ArrayList();
        ArrayList<ArmorType> armTypes = new ArrayList<ArmorType>();
        ArrayList hsLoc = new ArrayList();
        ArrayList jjLoc = new ArrayList();
        ArrayList enhLoc = new ArrayList();
        ArrayList acLoc = new ArrayList();
        String Source = "";

        VMechFullRecalc Recalc = new VMechFullRecalc();
        m.Visit( Recalc );

        // basics first
        m.setName( FileCommon.DecodeFluff( map.getNamedItem( "name" ).getTextContent() ) );
        m.setModel( FileCommon.DecodeFluff( map.getNamedItem( "model" ).getTextContent() ) );
        // save the omniCombatVehicle variable for later.  we'll need it after loading
        // the base loadout
        
        boolean omniCombatVehicle = ParseBoolean( map.getNamedItem( "omni" ).getTextContent() );
        m.setTonnage( Integer.parseInt( map.getNamedItem( "tons" ).getTextContent() ) );

        // Commented out since we are ignoring this until further notice.
        m.SetSolaris7ID( map.getNamedItem( "solaris7id" ).getTextContent() );
        m.SetSolaris7ImageID( map.getNamedItem( "solaris7imageid" ).getTextContent() );
        m.SetSSWImage( map.getNamedItem( "sswimage" ).getTextContent() );

        n = d.getElementsByTagName( "ssw_savefile_version" );
        if( n.getLength() <= 0 ) {
            // first version of the save file.
            SaveFileVersion = 0;
        } else {
            // a newer version
            SaveFileVersion = Integer.parseInt( n.item( 0 ).getTextContent() );
        }

        n = d.getElementsByTagName( "rules_level" );
        int ruleslevel = Integer.parseInt( n.item( 0 ).getTextContent() );
        if( SaveFileVersion == 0 ) {
            // alter the rules level since we've added the Introductory rules.
            ruleslevel += 1;
        }
        m.SetRulesLevel( ruleslevel );
        n = d.getElementsByTagName( "fractional" );
        if( n.getLength() > 0 ) {
            m.SetFractionalAccounting( true );
        }
        n = d.getElementsByTagName( "era" );
        int era = Integer.parseInt( n.item( 0 ).getTextContent() );
        if( SaveFileVersion == 0 ) {
            // alter the rules level since we've added the Dark Ages rules
            if( era == 3 )
            era += 1;
        }
        m.SetEra( era );

        n = d.getElementsByTagName( "motive" );
        map = n.item( 0 ).getAttributes();
        m.setMotiveType( FileCommon.DecodeFluff( map.getNamedItem( "type" ).getTextContent() ) );
        //Recall SetTonnage to fire necessary calcs
        m.setTonnage(m.GetTonnage());
        m.setCruiseMP( Integer.parseInt( FileCommon.DecodeFluff( map.getNamedItem( "cruise" ).getTextContent() ) ) );
        String turret = FileCommon.DecodeFluff( map.getNamedItem( "turret" ).getTextContent() );
        if ( turret.equals("Single Turret") )
            m.setHasTurret1(true);
        if ( turret.equals("Dual Turret") ) {
            m.setHasTurret1(true);
            m.setHasTurret2(true);
        }
        
        n = d.getElementsByTagName( "productionera" );
        if ( n.getLength() > 0 ) { m.SetProductionEra(Integer.parseInt( n.item( 0 ).getTextContent() )); } else { m.SetProductionEra(0); }

        if( SaveFileVersion < 2 ) {
            n = d.getElementsByTagName( "source" );
            if( n.getLength() > 0 ) {
                m.setSource( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
            }
            Source = m.getSource();
        }

        n = d.getElementsByTagName( "techbase" );
        map = n.item( 0 ).getAttributes();
        if( n.item( 0 ).getTextContent().equals( AvailableCode.TechBaseSTR[AvailableCode.TECH_CLAN] ) ) {
            m.SetClan();
        } else if( n.item( 0 ).getTextContent().equals( AvailableCode.TechBaseSTR[AvailableCode.TECH_BOTH] ) ) {
            m.SetMixed();
        }
        m.SetCompany( FileCommon.DecodeFluff( map.getNamedItem( "manufacturer" ).getTextContent() ) );
        m.SetLocation( FileCommon.DecodeFluff( map.getNamedItem( "location" ).getTextContent() ) );
        n = d.getElementsByTagName( "year" );
        map = n.item( 0 ).getAttributes();
        m.SetYear( Integer.parseInt( n.item( 0 ).getTextContent() ), true );
        m.SetYearRestricted( ParseBoolean( map.getNamedItem( "restricted" ).getTextContent() ) );

        ifVisitor v;
        n = d.getElementsByTagName( "structure" );
        map = n.item( 0 ).getAttributes();
        n = n.item( 0 ).getChildNodes();
        Node Type = null;
        for( int i = 0; i < n.getLength(); i++ ) {
            if( n.item( i ).getNodeName().equals( "location" ) ) {
                isLoc.add( DecodeLocation( n.item( i ) ) );
            } else if( n.item( i ).getNodeName().equals( "type" ) ) {
                Type = n.item( i );
            } else if( n.item( i ).getNodeName().equals( "mods" ) ) {
                NamedNodeMap Mods = n.item( i ).getAttributes();
                m.SetFlotationHull(Boolean.parseBoolean(Mods.getNamedItem("flotation").getTextContent()));
                m.SetLimitedAmphibious(Boolean.parseBoolean(Mods.getNamedItem("limitedamph").getTextContent()));
                m.SetFullAmphibious(Boolean.parseBoolean(Mods.getNamedItem("fullamph").getTextContent()));
                m.SetDuneBuggy(Boolean.parseBoolean(Mods.getNamedItem("dunebuggy").getTextContent()));
                m.SetEnvironmentalSealing(Boolean.parseBoolean(Mods.getNamedItem("enviroseal").getTextContent()));
                if ( Mods.getNamedItem("trailer") != null )
                    m.SetTrailer(Boolean.parseBoolean(Mods.getNamedItem("trailer").getTextContent()));
                
                //Refresh the Equipment data
                data.Rebuild(m);
            }
        }
        if( Type == null ) {
            throw new Exception( "The Internal Structure type could not be found (missing type node).\nThe Combat Vehicle cannot be loaded." );
        } else {
            v = m.Lookup( Type.getTextContent() );
            if( v == null ) {
                throw new Exception( "The Internal Structure type could not be found (lookup name missing or incorrect).\nThe Combat Vehicle cannot be loaded." );
            } else {
                if( map.getNamedItem( "techbase" ) == null ) {
                    // old style save file, set the internal structure based on the 'CombatVehicle's techbase
                    if( m.GetBaseTechbase() == AvailableCode.TECH_CLAN ) {
                        v.SetClan( true );
                    }
                } else {
                    if( Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() ) == AvailableCode.TECH_CLAN ) {
                        v.SetClan( true );
                   }
                }
                m.Visit( v );
            }
        }
        m.SetChassisModel( FileCommon.DecodeFluff( map.getNamedItem( "manufacturer" ).getTextContent() ) );
        
        n = d.getElementsByTagName( "engine" );
        map = n.item( 0 ).getAttributes();
        LocationIndex[] lengine = { null, null };
        v = m.Lookup( n.item( 0 ).getTextContent() );
        if( v == null ) {
            throw new Exception( "The Engine type could not be found (lookup name missing or incorrect).\nThe CombatVehicle cannot be loaded." );
        } else {
            if( map.getNamedItem( "techbase" ) == null ) {
                // old style save file, set the engine based on the 'CombatVehicle's techbase
                if( m.GetBaseTechbase() == AvailableCode.TECH_CLAN ) {
                    v.SetClan( true );
                }
            } else {
                if( Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() ) == AvailableCode.TECH_CLAN ) {
                    v.SetClan( true );
                }
            }
            v.LoadLocations( lengine );
            m.Visit( v );
        }
        //m.SetEngineRating( Integer.parseInt( map.getNamedItem( "rating" ).getTextContent() ) );
        m.SetEngineManufacturer( FileCommon.DecodeFluff( map.getNamedItem( "manufacturer" ).getTextContent() ) );

        // base loadout
        // get the actuators first since that will complete the structural components
        n = d.getElementsByTagName( "baseloadout" );
        map = n.item( 0 ).getAttributes();
        if( map.getNamedItem( "a4srm" ) != null ) {
            // old style loading, see if we need to add the message in
            boolean A4SRM = ParseBoolean( map.getNamedItem( "a4srm" ).getTextContent() );
            boolean A4LRM = ParseBoolean( map.getNamedItem( "a4lrm" ).getTextContent() );
            boolean A4MML = ParseBoolean( map.getNamedItem( "a4mml" ).getTextContent() );
            if( A4SRM || A4LRM || A4MML ) {
                Messages += "This save file is an earlier version and may not safely load Artemis-IV systems.\nAll Artemis-IV systems have been removed from the 'CombatVehicle.\nPlease add Artemis-IV systems back in safely and resave the 'CombatVehicle.\nAIV-SRM = " + ParseBoolean( map.getNamedItem( "a4srm" ).getTextContent() ) + ", AIV-LRM = " + ParseBoolean( map.getNamedItem( "a4lrm" ).getTextContent() ) + ", AIV-MML = " + ParseBoolean( map.getNamedItem( "a4mml" ).getTextContent() ) + "\n\n";
            }
        } else {
            // new style loading
            m.SetFCSArtemisIV( ParseBoolean( map.getNamedItem( "fcsa4" ).getTextContent() ) );
            m.SetFCSArtemisV( ParseBoolean( map.getNamedItem( "fcsa5" ).getTextContent() ) );
            m.SetFCSApollo( ParseBoolean( map.getNamedItem( "fcsapollo" ).getTextContent() ) );
        }
        if ( omniCombatVehicle && map.getNamedItem("turretlimit") != null ) {
            m.GetLoadout().GetTurret().SetTonnage( Double.parseDouble(map.getNamedItem("turretlimit").getTextContent() ) );
        }
        // take care of Clan CASE on previous save file versions
        if( SaveFileVersion < 1 ) {
            // this will fail if Inner Sphere, so we're safe
            //m.GetLoadout().SetClanCASE( true );
        }
        n = n.item( 0 ).getChildNodes();
        LocationIndex ltc = new LocationIndex();
        for( int i = 0; i < n.getLength(); i++ ) {
            // the main loadout routine
            if( n.item( i ).getNodeName().equals( "source" ) ) {
                m.setSource( n.item( i ).getTextContent() );
            } else if( n.item( i ).getNodeName().equals( "clancase" ) ) {
                //m.GetLoadout().SetClanCASE( ParseBoolean( n.item( i ).getTextContent() ) );
            } else if( n.item( i ).getNodeName().equals( "heatsinks" ) ) {
                map = n.item( i ).getAttributes();
                int numhs = Integer.parseInt( map.getNamedItem( "number" ).getTextContent() );
                NodeList nl = n.item( i ).getChildNodes();
                Type = null;
                for( int j = 0; j < nl.getLength(); j++ ) {
                    // get all the heatsink locations and type
                    if( nl.item( j ).getNodeName().equals( "type" ) ) {
                        Type = nl.item( j );
                    }
                }
                if( Type == null ) {
                    throw new Exception( "The Heat Sink type could not be found (missing type node).\nThe CombatVehicle cannot be loaded." );
                } else {
                    v = m.Lookup( Type.getTextContent() );
                    if( v == null ) {
                        throw new Exception( "The Heat Sink type could not be found (lookup name missing or incorrect).\nThe CombatVehicle cannot be loaded." );
                    } else {
                        if( map.getNamedItem( "techbase" ) == null ) {
                            // old style save file, set the armor based on the 'CombatVehicle's techbase
                            if( m.GetBaseTechbase() == AvailableCode.TECH_CLAN ) {
                                v.SetClan( true );
                            }
                        } else {
                            if( Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() ) == AvailableCode.TECH_CLAN ) {
                                v.SetClan( true );
                            }
                        }
                        m.Visit( v );
                    }
                }
                // set the heat sink number and place all sinks
                m.GetHeatSinks().SetNumHS( numhs );
            } else if( n.item( i ).getNodeName().equals( "jumpjets" ) ) {
                map = n.item( i ).getAttributes();
                int numjj = Integer.parseInt( map.getNamedItem( "number" ).getTextContent() );
                NodeList nl = n.item( i ).getChildNodes();
                Type = null;
                for( int j = 0; j < nl.getLength(); j++ ) {
                    // get all the jump jet locations and type
                    if( nl.item( j ).getNodeName().equals( "type" ) ) {
                        Type = nl.item( j );
                    } else if( nl.item( j ).getNodeName().equals( "location" ) ) {
                        jjLoc.add( DecodeLocation( nl.item( j ) ) );
                    }
                }
                if( Type == null ) {
                    throw new Exception( "The Jump Jet type could not be found (missing type node).\nThe CombatVehicle cannot be loaded." );
                } else {
                    v = m.Lookup( Type.getTextContent() );
                    if( v == null ) {
                        throw new Exception( "The Jump Jet type could not be found (lookup name missing or incorrect).\nThe CombatVehicle cannot be loaded." );
                    } else {
                        m.Visit( v );
                    }
                }
                // set the jump jet number and place all jumps
                m.GetJumpJets().ClearJumpJets();
                for( int j = 0; j < numjj; j++ ) {
                    m.GetJumpJets().IncrementNumJJ();
                }
                if( jjLoc.size() != m.GetJumpJets().GetPlacedJumps().length ) {
                    throw new Exception( "The jump jets in the loadout do not match the heatsinks that are saved.\nThe CombatVehicle cannot be loaded." );
                } else {
                    ifCVLoadout loadout = m.GetLoadout();
                    JumpJet[] jjList = m.GetJumpJets().GetPlacedJumps();
                    for( int j = 0; j < jjLoc.size(); j++ ) {
                        // place each heat sink
                        LocationIndex li = (LocationIndex) jjLoc.get( j );
                        loadout.AddTo( jjList[j], li.Location );
                    }
                }
            } else if( n.item( i ).getNodeName().equals( "equipment" ) ) {
                NodeList nl = n.item( i ).getChildNodes();
                ArrayList splitLoc = new ArrayList();
                String eMan = "";
                String eType = "";
                String eName = "";
                int VGLArc = 0;
                int VGLAmmo = 0;
                double vtons = 0.0;
                int lotsize = 0;
                l = new LocationIndex();
                for( int j = 0; j < nl.getLength(); j++ ) {
                    if( nl.item( j ).getNodeName().equals( "name" ) ) {
                        map = nl.item( j ).getAttributes();
                        eMan = map.getNamedItem( "manufacturer" ).getTextContent();
                        eName = nl.item( j ).getTextContent();
                    } else if( nl.item( j ).getNodeName().equals( "type" ) ) {
                        eType = nl.item( j ).getTextContent();
                    } else if( nl.item( j ).getNodeName().equals( "location" ) ) {
                        l = DecodeLocation( nl.item( j ) );
                    } else if( nl.item( j ).getNodeName().equals( "splitlocation" ) ) {
                        splitLoc.add( DecodeLocation( nl.item( j ) ) );
                    } else if( nl.item( j ).getNodeName().equals( "vglarc" ) ) {
                        VGLArc = Integer.parseInt( nl.item( j ).getTextContent() );
                    } else if( nl.item( j ).getNodeName().equals( "vglammo" ) ) {
                        VGLAmmo = Integer.parseInt( nl.item( j ).getTextContent() );
                    } else if( nl.item( j ).getNodeName().equals( "tons" ) ) {
                        vtons = Double.parseDouble( nl.item( j ).getTextContent() );
                    } else if( nl.item( j ).getNodeName().equals( "lot" ) ) {
                        lotsize = Integer.parseInt( nl.item( j ).getTextContent() );
                    }
                }
                if( eType.equals( "TargetingComputer" ) || eType.equals( "CASE" ) || eType.equals( "CASEII" ) || eType.equals( "Supercharger" ) ) {
                    if( eType.equals( "TargetingComputer") ) {
                        if( SaveFileVersion == 0 ) {
                            if( m.GetTechbase() == AvailableCode.TECH_CLAN ) {
                                m.UseTC( true, true );
                            } else {
                                m.UseTC( true, false );
                            }
                        } else {
                            if( eName.contains( "(CL)" ) ) {
                                m.UseTC( true, true );
                            } else {
                                m.UseTC( true, false );
                            }
                        }
                        ltc = l;
                    } else if( eType.equals( "CASE" ) ) {
                        m.GetLoadout().SetISCASE();
                        m.GetLoadout().SetClanCASE(( m.GetTechBase() == AvailableCode.TECH_CLAN));
                    } else if( eType.equals( "Supercharger" ) ) {
                        m.GetLoadout().SetSupercharger( true );
                    }
                } else {
                    abPlaceable p = GetEquipmentByName( eName, eType, m );
                    if( p == null ) {
                        throw new Exception( "Could not find " + eName + " as a piece of equipment.\nThe CombatVehicle cannot be loaded." );
                    }
                    p.SetManufacturer( eMan );
                    if( p instanceof Equipment ) {
                        if( ((Equipment) p).IsVariableSize() ) {
                            ((Equipment) p).SetMaxTons(m.GetTonnage());
                            ((Equipment) p).SetTonnage( vtons );
                        }
                    }
                    if( ( p instanceof Ammunition ) && lotsize > 0 ) {
                        ((Ammunition) p).SetLotSize( lotsize );
                    }
                    if( p instanceof VehicularGrenadeLauncher ) {
                        ((VehicularGrenadeLauncher) p).SetArc( VGLArc );
                        ((VehicularGrenadeLauncher) p).SetAmmoType( VGLAmmo );
                    }
                    m.GetLoadout().AddTo(p, l.Location);
                }
            } else if( n.item( i ).getNodeName().equals( "armored_locations" ) ) {
                NodeList nl = n.item( i ).getChildNodes();
                acLoc = new ArrayList();
                l = new LocationIndex();
                for( int j = 0; j < nl.getLength(); j++ ) {
                    if( nl.item( j ).getNodeName().equals( "location" ) ) {
                        l = DecodeLocation( nl.item( j ) );
                        acLoc.add( l );
                    }
                }
            } else if( n.item( i ).getNodeName().equals( "multislot" ) ) {
                map = n.item( i ).getAttributes();
                String type = map.getNamedItem( "name" ).getTextContent();
                ArrayList msLoc = new ArrayList();
                NodeList nl = n.item( i ).getChildNodes();
                l = new LocationIndex();
                for( int j = 0; j < nl.getLength(); j++ ) {
                    if( nl.item( j ).getNodeName().equals( "location" ) ) {
                        l = DecodeLocation( nl.item( j ) );
                        msLoc.add( l );
                    }
                }

                // turn the ArrayList into an array
                LocationIndex[] Locs = new LocationIndex[msLoc.size()];
                for( int j = 0; j < msLoc.size(); j++ ) {
                    Locs[j] = (LocationIndex) msLoc.get( j );
                }
            }
        }

        // place the internal structure
        ifCVLoadout loadout = m.GetLoadout();
        // place the internal structure
        if( isLoc.size() > 0 ) {
            InternalStructure is = m.GetIntStruc();
            for( int i = 0; i < isLoc.size(); i++ ) {
                l = (LocationIndex) isLoc.get( i );
                loadout.AddTo( is, l.Location );
            }
        }

        // armor next
        n = d.getElementsByTagName( "armor" );
        map = n.item( 0 ).getAttributes();
        n = n.item( 0 ).getChildNodes();
        String pwtype = "";
        int pwtech = 0;
        boolean oldfile = false, clanarmor = false;
        m.SetArmorModel( FileCommon.DecodeFluff( map.getNamedItem( "manufacturer" ).getTextContent() ) );
        if( map.getNamedItem( "techbase" ) == null ) {
            // old style save file, set the armor based on the 'CombatVehicle's techbase
            if( m.GetBaseTechbase() == AvailableCode.TECH_CLAN ) {
               oldfile = true;
            }
        } else {
            if( Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() ) == AvailableCode.TECH_CLAN ) {
                clanarmor = true;
            }
        }
        int[] ArmorPoints = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        Type = null;
        for( int i = 0; i < n.getLength(); i++ ) {
            if( n.item( i ).getNodeName().equals( "location" ) ) {
                armLoc.add( DecodeLocation( n.item( i ) ) );
            } else if( n.item( i ).getNodeName().equals( "front" ) ) {
                map = n.item( i ).getAttributes();
                if( map.getNamedItem( "type" ) != null ) {
                    pwtype = map.getNamedItem( "type" ).getTextContent();
                    if( map.getNamedItem( "techbase" ) != null ) {
                        pwtech = Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() );
                    } else {
                        pwtech = AvailableCode.TECH_INNER_SPHERE;
                    }
                    armTypes.add( new ArmorType( pwtype, LocationIndex.CV_LOC_FRONT, pwtech ) );
                }
                ArmorPoints[LocationIndex.CV_LOC_FRONT] = Integer.parseInt( n.item( i ).getTextContent() );
            } else if( n.item( i ).getNodeName().equals( "left" ) ) {
                map = n.item( i ).getAttributes();
                if( map.getNamedItem( "type" ) != null ) {
                    pwtype = map.getNamedItem( "type" ).getTextContent();
                    if( map.getNamedItem( "techbase" ) != null ) {
                        pwtech = Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() );
                    } else {
                        pwtech = AvailableCode.TECH_INNER_SPHERE;
                    }
                    armTypes.add( new ArmorType( pwtype, LocationIndex.CV_LOC_LEFT, pwtech ) );
                }
                ArmorPoints[LocationIndex.CV_LOC_LEFT] = Integer.parseInt( n.item( i ).getTextContent() );
            } else if( n.item( i ).getNodeName().equals( "right" ) ) {
                map = n.item( i ).getAttributes();
                if( map.getNamedItem( "type" ) != null ) {
                    pwtype = map.getNamedItem( "type" ).getTextContent();
                    if( map.getNamedItem( "techbase" ) != null ) {
                        pwtech = Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() );
                    } else {
                        pwtech = AvailableCode.TECH_INNER_SPHERE;
                    }
                    armTypes.add( new ArmorType( pwtype, LocationIndex.CV_LOC_RIGHT, pwtech ) );
                }
                ArmorPoints[LocationIndex.CV_LOC_RIGHT] = Integer.parseInt( n.item( i ).getTextContent() );
            } else if( n.item( i ).getNodeName().equals( "rear" ) ) {
                map = n.item( i ).getAttributes();
                if( map.getNamedItem( "type" ) != null ) {
                    pwtype = map.getNamedItem( "type" ).getTextContent();
                    if( map.getNamedItem( "techbase" ) != null ) {
                        pwtech = Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() );
                    } else {
                        pwtech = AvailableCode.TECH_INNER_SPHERE;
                    }
                    armTypes.add( new ArmorType( pwtype, LocationIndex.CV_LOC_REAR, pwtech ) );
                }
                ArmorPoints[LocationIndex.CV_LOC_REAR] = Integer.parseInt( n.item( i ).getTextContent() );
            } else if( n.item( i ).getNodeName().equals( "rotor" ) ) {
                map = n.item( i ).getAttributes();
                if( map.getNamedItem( "type" ) != null ) {
                    pwtype = map.getNamedItem( "type" ).getTextContent();
                    if( map.getNamedItem( "techbase" ) != null ) {
                        pwtech = Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() );
                    } else {
                        pwtech = AvailableCode.TECH_INNER_SPHERE;
                    }
                    armTypes.add( new ArmorType( pwtype, LocationIndex.CV_LOC_ROTOR, pwtech ) );
                }
                ArmorPoints[LocationIndex.CV_LOC_ROTOR] = Integer.parseInt( n.item( i ).getTextContent() );
            } else if( n.item( i ).getNodeName().equals( "primaryturret" ) ) {
                map = n.item( i ).getAttributes();
                if( map.getNamedItem( "type" ) != null ) {
                    pwtype = map.getNamedItem( "type" ).getTextContent();
                    if( map.getNamedItem( "techbase" ) != null ) {
                        pwtech = Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() );
                    } else {
                        pwtech = AvailableCode.TECH_INNER_SPHERE;
                    }
                    armTypes.add( new ArmorType( pwtype, LocationIndex.CV_LOC_TURRET1, pwtech ) );
                }
                ArmorPoints[LocationIndex.CV_LOC_TURRET1] = Integer.parseInt( n.item( i ).getTextContent() );
            } else if( n.item( i ).getNodeName().equals( "secondaryturret" ) ) {
                map = n.item( i ).getAttributes();
                if( map.getNamedItem( "type" ) != null ) {
                    pwtype = map.getNamedItem( "type" ).getTextContent();
                    if( map.getNamedItem( "techbase" ) != null ) {
                        pwtech = Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() );
                    } else {
                        pwtech = AvailableCode.TECH_INNER_SPHERE;
                    }
                    armTypes.add( new ArmorType( pwtype, LocationIndex.CV_LOC_TURRET2, pwtech ) );
                }
                ArmorPoints[LocationIndex.CV_LOC_TURRET2] = Integer.parseInt( n.item( i ).getTextContent() );
            } else if( n.item( i ).getNodeName().equals( "type" ) ) {
                Type = n.item( i );
            }
        }
        if( Type == null ) {
            throw new Exception( "The Armor type could not be found (missing type node).\nThe CombatVehicle cannot be loaded." );
        } else {
            v = m.Lookup( Type.getTextContent() );
            if( v == null ) {
                throw new Exception( "The Armor type could not be found (lookup name missing or incorrect).\nThe CombatVehicle cannot be loaded." );
            } else {
                LocationIndex[] Locs = new LocationIndex[armLoc.size()];
                for( int j = 0; j < armLoc.size(); j++ ) {
                    Locs[j] = (LocationIndex) armLoc.get( j );
                }
                if( oldfile ) {
                    // old style save file, set the armor based on the 'CombatVehicle's techbase
                    if( m.GetBaseTechbase() == AvailableCode.TECH_CLAN ) {
                        v.SetClan( true );
                    }
                } else {
                    if( clanarmor ) {
                        v.SetClan( true );
                    }
                }
                if( ! ( v instanceof VArmorSetPatchwork ) ) {
                    v.LoadLocations( Locs );
                }
                m.Visit( v );
                if( v instanceof VArmorSetPatchwork ) {
                    for( int i = 0; i < armTypes.size(); i++ ) {
                        ArmorType t = armTypes.get( i );
                        VArmorSetPatchworkLocation v1 = new VArmorSetPatchworkLocation();
                        v1.LoadLocations( Locs );
                        v1.SetLocation( t.Location );
                        v1.SetPatchworkType( t.Type );
                        v1.SetClan( t.Techbase == AvailableCode.TECH_CLAN );
                        m.Visit( v1 );
                    }
                }
            }
        }
        m.GetArmor().Recalculate();
        // set the armor points
        m.GetArmor().SetArmor( LocationIndex.CV_LOC_FRONT, ArmorPoints[LocationIndex.CV_LOC_FRONT] );
        m.GetArmor().SetArmor( LocationIndex.CV_LOC_LEFT, ArmorPoints[LocationIndex.CV_LOC_LEFT] );
        m.GetArmor().SetArmor( LocationIndex.CV_LOC_REAR, ArmorPoints[LocationIndex.CV_LOC_REAR] );
        m.GetArmor().SetArmor( LocationIndex.CV_LOC_RIGHT, ArmorPoints[LocationIndex.CV_LOC_RIGHT] );
        m.GetArmor().SetArmor( LocationIndex.CV_LOC_ROTOR, ArmorPoints[LocationIndex.CV_LOC_ROTOR] );
        m.GetArmor().SetArmor( LocationIndex.CV_LOC_TURRET1, ArmorPoints[LocationIndex.CV_LOC_TURRET1] );
        m.GetArmor().SetArmor( LocationIndex.CV_LOC_TURRET2, ArmorPoints[LocationIndex.CV_LOC_TURRET2] );

        // place the armor
        if( ! m.GetArmor().IsStealth() &! m.GetArmor().IsPatchwork() ) {
            if( armLoc.size() > 0 ) {
                CVArmor a = m.GetArmor();
                for( int i = 0; i < armLoc.size(); i++ ) {
                    l = (LocationIndex) armLoc.get( i );
                    loadout.AddTo( a, l.Location );
                }
            }
        }

        // enhancement next
        n = d.getElementsByTagName( "enhancement" );
        if( n.getLength() > 0 ) {
            map = n.item( 0 ).getAttributes();
            l = new LocationIndex();
            n = n.item( 0 ).getChildNodes();
            Type = null;
            for( int i = 0; i < n.getLength(); i++ ) {
                if( n.item( i ).getNodeName().equals( "location" ) ) {
                    enhLoc.add( DecodeLocation( n.item( i ) ) );
                } else if( n.item( i ).getNodeName().equals( "type" ) ) {
                    Type = n.item( i );
                }
            }
            if( Type == null ) {
                throw new Exception( "The Enhancement type could not be found (missing type node).\nThe CombatVehicle cannot be loaded." );
            } else {
                v = m.Lookup( Type.getTextContent() );
                if( map.getNamedItem( "techbase" ) == null ) {
                    // old style save file, set the armor based on the 'CombatVehicle's techbase
                    if( m.GetBaseTechbase() == AvailableCode.TECH_CLAN ) {
                        v.SetClan( true );
                    }
                } else {
                    if( Integer.parseInt( map.getNamedItem( "techbase" ).getTextContent() ) == AvailableCode.TECH_CLAN ) {
                        v.SetClan( true );
                    }
                }
                if( v == null ) {
                    throw new Exception( "The Enhancement type could not be found (lookup name missing or incorrect).\nThe CombatVehicle cannot be loaded." );
                } else {
                    m.Visit( v );
                    if( enhLoc.size() > 0 ) {
                        for( int j = 0; j < enhLoc.size(); j++ ) {
                            LocationIndex li = (LocationIndex) enhLoc.get( j );
                            m.GetLoadout().AddTo( m.GetPhysEnhance(), li.Location );
                        }
                    }
                }
            }
        }

        // any omniCombatVehicle loadouts we should worry about?
        if( omniCombatVehicle ) {
            // change the CombatVehicle into an omni.  we'll use a temporary name for the
            // first loadout so we can remove it easily since we don't know what
            // loadout names we have yet.
            m.SetOmni( "SSW_TEMP_LOADOUT_001" );
            NodeList OmniLoads = d.getElementsByTagName( "loadout" );
            // the actual loading routine
            for( int k = 0; k < OmniLoads.getLength(); k++ ) {
                map = OmniLoads.item( k ).getAttributes();
                if( map.getNamedItem( "name" ) == null ) {
                    throw new Exception( "An omniCombatVehicle loadout was specified but has no name.\nThe CombatVehicle cannot be loaded." );
                } else {
                    m.AddLoadout( FileCommon.DecodeFluff( map.getNamedItem( "name" ).getTextContent() ) );
                }
                if( map.getNamedItem( "a4srm" ) != null ) {
                    // old style loading, see if we need to add the message in
                    boolean A4SRM = ParseBoolean( map.getNamedItem( "a4srm" ).getTextContent() );
                    boolean A4LRM = ParseBoolean( map.getNamedItem( "a4lrm" ).getTextContent() );
                    boolean A4MML = ParseBoolean( map.getNamedItem( "a4mml" ).getTextContent() );
                    if( A4SRM || A4LRM || A4MML ) {
                        Messages += "This save file is an earlier version and may not safely load Artemis-IV systems.\nAll Artemis-IV systems have been removed from the 'CombatVehicle.\nPlease add Artemis-IV systems back into the " + m.GetLoadout().GetName() + " loadout and resave the 'CombatVehicle.\nAIV-SRM = " + ParseBoolean( map.getNamedItem( "a4srm" ).getTextContent() ) + ", AIV-LRM = " + ParseBoolean( map.getNamedItem( "a4lrm" ).getTextContent() ) + ", AIV-MML = " + ParseBoolean( map.getNamedItem( "a4mml" ).getTextContent() ) + "\n\n";
                    }
                } else {
                    // new style loading
                    m.SetFCSArtemisIV( ParseBoolean( map.getNamedItem( "fcsa4" ).getTextContent() ) );
                    m.SetFCSArtemisV( ParseBoolean( map.getNamedItem( "fcsa5" ).getTextContent() ) );
                    m.SetFCSApollo( ParseBoolean( map.getNamedItem( "fcsapollo" ).getTextContent() ) );
                }
                if( map.getNamedItem( "ruleslevel" ) != null ) {
                    ruleslevel = Integer.parseInt( map.getNamedItem( "ruleslevel" ).getTextContent() );
                    if( SaveFileVersion == 0 ) {
                        // alter the rules level since we've added the Introductory rules.
                        ruleslevel += 1;
                    }
                    m.GetLoadout().SetRulesLevel( ruleslevel );
                }
                // take care of Clan CASE on previous save file versions
                if( SaveFileVersion < 1 ) {
                    // this will fail if Inner Sphere, so we're safe
                    //m.GetLoadout().SetClanCASE( true );
                }
                if( SaveFileVersion < 2 ) {
                    m.setSource( Source );
                }
                n = OmniLoads.item( k ).getChildNodes();
                ltc = new LocationIndex();
                for( int i = 0; i < n.getLength(); i++ ) {
                    // the main loadout routine
                    if( n.item( i ).getNodeName().equals( "source" ) ) {
                        m.setSource( n.item( i ).getTextContent() );
                    } else if( n.item( i ).getNodeName().equals( "loadout_era" ) ) {
                        m.SetEra( Integer.parseInt( n.item( i ).getTextContent() ) );
                    } else if( n.item( i ).getNodeName().equals( "loadout_productionera" ) ) {
                        m.SetProductionEra( Integer.parseInt( n.item( i ).getTextContent() ) );
                    } else if( n.item( i ).getNodeName().equals( "loadout_year" ) ) {
                        m.SetYear( Integer.parseInt( n.item( i ).getTextContent() ), false );
                    } else if( n.item( i ).getNodeName().equals( "techbase" ) ) {
                        if( SaveFileVersion < 3 ) {
                            m.SetTechBase( Integer.parseInt( n.item( i ).getTextContent() ) );
                        } else {
                            if( n.item( i ).getTextContent().equals( AvailableCode.TechBaseSTR[AvailableCode.TECH_CLAN] ) ) {
                                m.SetTechBase( AvailableCode.TECH_CLAN );
                            } else if( n.item( i ).getTextContent().equals( AvailableCode.TechBaseSTR[AvailableCode.TECH_BOTH] ) ) {
                                m.SetTechBase( AvailableCode.TECH_BOTH );
                            }
                        }
                    } else if( n.item( i ).getNodeName().equals( "clancase" ) ) {
                        //m.GetLoadout().SetClanCASE( ParseBoolean( n.item( i ).getTextContent() ) );
                    } else if( n.item( i ).getNodeName().equals( "heatsinks" ) ) {
                        hsLoc.clear();
                        map = n.item( i ).getAttributes();
                        int numhs = Integer.parseInt( map.getNamedItem( "number" ).getTextContent() );
                        NodeList nl = n.item( i ).getChildNodes();
                        Type = null;
                        for( int j = 0; j < nl.getLength(); j++ ) {
                            // get all the heatsink locations and type
                            if( nl.item( j ).getNodeName().equals( "type" ) ) {
                                Type = nl.item( j );
                            } else if( nl.item( j ).getNodeName().equals( "location" ) ) {
                                hsLoc.add( DecodeLocation( nl.item( j ) ) );
                            }
                        }
                        if( Type == null ) {
                            throw new Exception( "The Heat Sink type could not be found (missing type node).\nThe CombatVehicle cannot be loaded." );
                        } else {
                            v = m.Lookup( Type.getTextContent() );
                            if( v == null ) {
                                throw new Exception( "The Heat Sink type could not be found (lookup name missing or incorrect).\nThe CombatVehicle cannot be loaded." );
                            }
                        }
                        // set the heat sink number and place all sinks
                        m.GetHeatSinks().SetNumHS(numhs);
                    } else if( n.item( i ).getNodeName().equals( "jumpjets" ) ) {
                        jjLoc.clear();
                        map = n.item( i ).getAttributes();
                        int numjj = Integer.parseInt( map.getNamedItem( "number" ).getTextContent() ) - m.GetJumpJets().GetBaseLoadoutNumJJ();
                        NodeList nl = n.item( i ).getChildNodes();
                        Type = null;
                        for( int j = 0; j < nl.getLength(); j++ ) {
                            // get all the jump jet locations and type
                            if( nl.item( j ).getNodeName().equals( "type" ) ) {
                                Type = nl.item( j );
                            } else if( nl.item( j ).getNodeName().equals( "location" ) ) {
                                jjLoc.add( DecodeLocation( nl.item( j ) ) );
                            }
                        }
                        if( Type == null ) {
                            throw new Exception( "The Jump Jet type could not be found (missing type node).\nThe CombatVehicle cannot be loaded." );
                        } else {
                            v = m.Lookup( Type.getTextContent() );
                            if( v == null ) {
                                throw new Exception( "The Jump Jet type could not be found (lookup name missing or incorrect).\nThe CombatVehicle cannot be loaded." );
                            } else {
                                m.Visit( v );
                            }
                        }
                        // set the jump jet number and place all jumps
                        // m.GetJumpJets().ClearJumpJets();
                        for( int j = 0; j < numjj; j++ ) {
                            m.GetJumpJets().IncrementNumJJ();
                        }
                        if( jjLoc.size() + m.GetJumpJets().GetBaseLoadoutNumJJ() != m.GetJumpJets().GetPlacedJumps().length ) {
                            throw new Exception( "The jump jets in the loadout do not match the jump jets that are saved.\nThe CombatVehicle cannot be loaded." );
                        } else {
                            loadout = m.GetLoadout();
                            JumpJet[] jjList = m.GetJumpJets().GetPlacedJumps();
                            ArrayList temp = new ArrayList();
                            for( int j = 0; j < jjList.length; j++ ) {
                                if( ! loadout.IsAllocated( jjList[j] ) ) {
                                    temp.add( jjList[j] );
                                }
                            }
                            for( int j = 0; j < jjLoc.size(); j++ ) {
                                // place each heat sink
                                LocationIndex li = (LocationIndex) jjLoc.get( j );
                                loadout.AddTo( (JumpJet) temp.get( j ), li.Location );
                            }
                        }
                    } else if( n.item( i ).getNodeName().equals( "equipment" ) ) {
                        NodeList nl = n.item( i ).getChildNodes();
                        ArrayList splitLoc = new ArrayList();
                        String eMan = "";
                        String eType = "";
                        String eName = "";
                        int VGLArc = 0;
                        int VGLAmmo = 0;
                        double vtons = 0.0;
                        int lotsize = 0;
                        l = new LocationIndex();
                        for( int j = 0; j < nl.getLength(); j++ ) {
                            if( nl.item( j ).getNodeName().equals( "name" ) ) {
                                map = nl.item( j ).getAttributes();
                                eMan = map.getNamedItem( "manufacturer" ).getTextContent();
                                eName = nl.item( j ).getTextContent();
                            } else if( nl.item( j ).getNodeName().equals( "type" ) ) {
                                eType = nl.item( j ).getTextContent();
                            } else if( nl.item( j ).getNodeName().equals( "location" ) ) {
                                l = DecodeLocation( nl.item( j ) );
                            } else if( nl.item( j ).getNodeName().equals( "splitlocation" ) ) {
                                splitLoc.add( DecodeLocation( nl.item( j ) ) );
                            } else if( nl.item( j ).getNodeName().equals( "vglarc" ) ) {
                                VGLArc = Integer.parseInt( nl.item( j ).getTextContent() );
                            } else if( nl.item( j ).getNodeName().equals( "vglammo" ) ) {
                                VGLAmmo = Integer.parseInt( nl.item( j ).getTextContent() );
                            } else if( nl.item( j ).getNodeName().equals( "tons" ) ) {
                                vtons = Double.parseDouble( nl.item( j ).getTextContent() );
                            } else if( nl.item( j ).getNodeName().equals( "lot" ) ) {
                                lotsize = Integer.parseInt( nl.item( j ).getTextContent() );
                            }
                        }
                        if( eType.equals( "TargetingComputer" ) || eType.equals( "CASE" ) || eType.equals( "CASEII" ) || eType.equals( "Supercharger" ) ) {
                            if( eType.equals( "TargetingComputer") ) {
                                if( SaveFileVersion == 0 ) {
                                    if( m.GetTechbase() == AvailableCode.TECH_CLAN ) {
                                        m.UseTC( true, true );
                                    } else {
                                        m.UseTC( true, false );
                                    }
                                } else {
                                    if( eName.contains( "(CL)" ) ) {
                                        m.UseTC( true, true );
                                    } else {
                                        m.UseTC( true, false );
                                    }
                                }
                                ltc = l;
                            } else if( eType.equals( "CASE" ) ) {
                                m.GetLoadout().SetISCASE();
                                m.GetLoadout().SetClanCASE(( m.GetTechBase() == AvailableCode.TECH_CLAN));
                            } else if( eType.equals( "Supercharger" ) ) {
                                m.GetLoadout().SetSupercharger( true );
                            }
                        } else {
                            abPlaceable p = GetEquipmentByName( eName, eType, m );
                            if( p == null ) {
                                throw new Exception( "Could not find " + eName + " as a piece of equipment.\nThe CombatVehicle cannot be loaded." );
                            }
                            p.SetManufacturer( eMan );
                            if( p instanceof Equipment ) {
                                if( ((Equipment) p).IsVariableSize() ) {
                                    ((Equipment) p).SetTonnage( vtons );
                                }
                            }
                            if( ( p instanceof Ammunition ) && lotsize > 0 ) {
                                ((Ammunition) p).SetLotSize( lotsize );
                            }
                            if( p instanceof VehicularGrenadeLauncher ) {
                                ((VehicularGrenadeLauncher) p).SetArc( VGLArc );
                                ((VehicularGrenadeLauncher) p).SetAmmoType( VGLAmmo );
                            }
                            m.GetLoadout().AddTo(p, l.Location);
                        }
                    } else if( n.item( i ).getNodeName().equals( "armored_locations" ) ) {
                        NodeList nl = n.item( i ).getChildNodes();
                        acLoc = new ArrayList();
                        l = new LocationIndex();
                        for( int j = 0; j < nl.getLength(); j++ ) {
                            if( nl.item( j ).getNodeName().equals( "location" ) ) {
                                l = DecodeLocation( nl.item( j ) );
                                acLoc.add( l );
                            }
                        }
                    }
                }
            }

            // now remove the first loadout that we added.
            m.RemoveLoadout( "SSW_TEMP_LOADOUT_001" );

            // make sure the 'CombatVehicle is set to the first loadout.
            m.SetCurLoadout( ((ifCVLoadout) m.GetLoadouts().get( 0 )).GetName() );
        }

        // fluff last
        n = d.getElementsByTagName( "overview" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.setOverview( "" );
        } else {
            m.setOverview( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "capabilities" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.setCapabilities( "" );
        } else {
            m.setCapabilities( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "battlehistory" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.setHistory( "" );
        } else {
            m.setHistory( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "deployment" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.setDeployment( "" );
        } else {
            m.setDeployment( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "variants" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.setVariants( "" );
        } else {
            m.setVariants( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "notables" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.SetNotables( "" );
        } else {
            m.SetNotables( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "additional" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.SetAdditional( "" );
        } else {
            m.SetAdditional( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "jumpjet_model" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.SetJJModel( "" );
        } else {
            m.SetJJModel( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "commsystem" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.SetCommSystem( "" );
        } else {
            m.SetCommSystem( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }
        n = d.getElementsByTagName( "tandtsystem" );
        if( n.item( 0 ).getTextContent() == null ) {
            m.SetTandTSystem( "" );
        } else {
            m.SetTandTSystem( FileCommon.DecodeFluff( n.item( 0 ).getTextContent() ) );
        }

        // all done, return the CombatVehicle
        m.SetChanged( false );
        return m;
    }

    private boolean ParseBoolean( String b ) {
        if( b.equals( "TRUE" ) ) {
            return true;
        } else {
            return false;
        }
    }

    private LocationIndex DecodeLocation( Node n ) {
        LocationIndex l = new LocationIndex();
        NamedNodeMap map = n.getAttributes();
        //l.Index = Integer.parseInt( map.getNamedItem( "index" ).getTextContent() );
        l.Location = FileCommon.DecodeLocation( n.getTextContent() );
        if( map.getNamedItem( "number" ) != null ) {
            l.Number = Integer.parseInt( map.getNamedItem( "number" ).getTextContent() );
        } else {
            l.Number = 1;
        }
        return l;
    }

    private abPlaceable GetEquipmentByName( String name, String type, CombatVehicle m ) {
        boolean rear = false;
        abPlaceable retval = null;
        String prepend = "";
        if( name.length() > 4 ) {
            if( name.substring( 0, 4 ).equals( "(R) " ) ) {
                name = FileCommon.LookupStripArc( name );
                rear = true;
            } else if( name.substring( 0, 4 ).equals( "(T) " ) ) {
                // turreted items are handled elsewhere, unfortunately.
                name = FileCommon.LookupStripArc( name );
            }
        }
        if( ! name.contains( "(CL)" ) |! name.contains( "(IS)" ) ) {
            // old style save file or an item that can be used by both techbases
            // we'll need to check.
            if( m.GetTechbase() == AvailableCode.TECH_CLAN ) {
                prepend = "(CL) ";
            } else {
                prepend = "(IS) ";
            }
        }
        if( type.equals( "energy" ) ) {
            boolean ppccap = false;
            boolean insulated = false;
            if( name.contains( " + PPC Capacitor" ) ) {
                name = name.substring( 0, name.length() - 16 );
                ppccap = true;
            }
            if( name.contains( " w/ Capacitor" ) ) {
                name = name.substring( 0, name.length() - 13 );
                ppccap = true;
            }
            if( name.contains( " (Insulated)" ) ) {
                name = name.substring( 0, name.length() - 12 );
                insulated = true;
            }
            if( name.contains( "Variable Speed Laser" ) ) {
                name = name.replace( "Variable Speed Laser", "Variable Speed Pulse Laser" );
            }
            retval = data.GetEquipment().GetRangedWeaponByName( name, m );
            if( retval == null ) {
                // try again with the prepend
                retval = data.GetEquipment().GetRangedWeaponByName( prepend + name, m );
            }
            if( retval != null ) {
                ((RangedWeapon) retval).UseCapacitor( ppccap );
                ((RangedWeapon) retval).UseInsulator( insulated );
            }
        } else if( type.equals( "ballistic" ) ) {
            boolean caseless = false;
            if( name.contains( " (Caseless)" ) ) {
                name = name.substring( 0, name.length() - 11 );
                caseless = true;
            }
            retval = data.GetEquipment().GetRangedWeaponByName( name, m );
            if( retval == null ) {
                // try again with the prepend
                retval = data.GetEquipment().GetRangedWeaponByName( prepend + name, m );
            }
            if( retval != null ) {
                ((RangedWeapon) retval).SetCaseless( caseless );
            }
        } else if( type.equals( "missile" ) ) {
            retval = data.GetEquipment().GetRangedWeaponByName( name, m );
            if( retval == null ) {
                // try again with the prepend
                retval = data.GetEquipment().GetRangedWeaponByName( prepend + name, m );
            }
            // we'll use a try here so that the correct error message will be
            // sent if we still have a null retval.
            try {
                switch( ((RangedWeapon) retval).GetFCSType() ) {
                case ifMissileGuidance.FCS_ArtemisIV:
                    if( m.UsingArtemisIV() ) { ((RangedWeapon) retval).UseFCS( true, ifMissileGuidance.FCS_ArtemisIV ); }
                case ifMissileGuidance.FCS_ArtemisV:
                    if( m.UsingArtemisIV() ) { ((RangedWeapon) retval).UseFCS( true, ifMissileGuidance.FCS_ArtemisIV ); }
                    if( m.UsingArtemisV() ) { ((RangedWeapon) retval).UseFCS( true, ifMissileGuidance.FCS_ArtemisV ); }
                    break;
                case ifMissileGuidance.FCS_Apollo:
                    if( m.UsingApollo() ) { ((RangedWeapon) retval).UseFCS( true, ifMissileGuidance.FCS_Apollo ); }
                    break;
                }
            } catch( Exception e ) {
                return null;
            }
        } else if( type.equals( "mgarray" ) ) {
            retval = data.GetEquipment().GetRangedWeaponByName( name, m );
            if( retval == null ) {
                // try again with the prepend
                retval = data.GetEquipment().GetRangedWeaponByName( prepend + name, m );
            }
        } else if( type.equals( "equipment" ) ) {
            if( name.equals( "Nail/Rivet Gun") ) {
                // just load the nail gun as default
                name = "Nail Gun";
            }
            retval = data.GetEquipment().GetEquipmentByName( name, m );
            if( retval == null ) {
                // try again with the prepend
                retval = data.GetEquipment().GetEquipmentByName( prepend + name, m );
            }
        } else if( type.equals( "ammunition" ) ) {
            if( SaveFileVersion == 0 ) {
                if( name.contains( "Artemis Capable" ) ) {
                    name = name.replace( "Artemis Capable", "Artemis IV Capable" );
                }
            }
            retval = data.GetEquipment().GetAmmoByName( name, m );
            if( retval == null ) {
                // try again with the prepend
                retval = data.GetEquipment().GetAmmoByName( prepend + name, m );
            }
        } else if( type.equals( "physical" ) ) {
            retval = data.GetEquipment().GetPhysicalWeaponByName( name, m );
            if( retval == null ) {
                // try again with the prepend
                retval = data.GetEquipment().GetPhysicalWeaponByName( prepend + name, m );
            }
        } else if( type.equals( "artillery" ) ) {
            retval = data.GetEquipment().GetRangedWeaponByName( name, m );
            if( retval == null ) {
                // try again with the prepend
                retval = data.GetEquipment().GetRangedWeaponByName( prepend + name, m );
            }
        } else {
            retval = data.GetEquipment().SearchForName( name, m );
        }
        // again, use a try statement for the correct error message
        try {
            if( rear ) {
                retval.MountRear( true );
            }
        } catch( Exception e ) {
            return null;
        }
        return retval;
    }

    public String GetMessages() {
        return Messages;
    }

    private class ArmorType {
        public String Type = null;
        public int Location = -1,
                   Techbase = 0;
        public ArmorType( String typ, int loc, int tech ) {
            Type = typ;
            Location = loc;
            Techbase = tech;
        }
    }
}
