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

package saw.filehandlers;

import common.CommonTools;
import components.AvailableCode;
import components.CombatVehicle;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLRPCClient {
    private String Server;

    public XMLRPCClient( String server ) {
        Server = server;
    }

    public String[][] GetArmoryList( int UserID ) throws Exception {
        String[][] retval = null;

        String send = "<?xml version=\"1.0\" encoding =\"UTF-8\"?>\r\n";
        send += "<methodCall>\r\n";
        send += "\t<methodName>Member_Armories</methodName>\r\n";
        send += "\t<params>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + UserID + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t</params>\r\n";
        send += "</methodCall>\r\n";

        Document dc = MethodCall( send );
        Armory[] AList = ExtractArmories( dc );

        retval = new String[AList.length][2];
        for( int i = 0; i < AList.length; i++ ) {
            retval[i][0] = AList[i].Name;
            retval[i][1] = AList[i].ID;
        }
        return retval;
    }

    public int GetMemberID( String UserName, String Password ) throws Exception {
        int retval = -1;

        String send = "<?xml version=\"1.0\" encoding =\"UTF-8\"?>\r\n";
        send += "<methodCall>\r\n";
        send += "\t<methodName>Member_Validate</methodName>\r\n";
        send += "\t<params>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + UserName + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + Password + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t</params>\r\n";
        send += "</methodCall>\r\n";

        Document dc = MethodCall( send );
        retval = ExtractMemberID( dc );

        return retval;
    }

    public String[] GetTROImages( String date ) throws Exception {
        String[] retval = null;

        String send = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
        send += "<methodCall>\r\n";
        send += "\t<methodName>TRO_Images</methodName>\r\n";
        send += "\t<params>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>0</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>battlemech</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + date + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t</params>\r\n";
        send += "</methodCall>\r\n";

        Document dc = MethodCall( send );
        retval = ExtractImageList( dc );

        return retval;
    }

    public String[] GetUserImages( int UserID ) throws Exception {
        String[] retval = null;

        String send = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
        send += "<methodCall>\r\n";
        send += "\t<methodName>TRO_Images_User</methodName>\r\n";
        send += "\t<params>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + UserID + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>battlemech</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t</params>\r\n";
        send += "</methodCall>\r\n";

        Document dc = MethodCall( send );
        retval = ExtractImageList( dc );

        return retval;
    }

    public String PostToSolaris7( String UserID, String ArmoryID, String HTML, String ImageID, String TROYear, CombatVehicle m ) throws Exception {
        String retval = "";

        String send = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
        send += "<methodCall>\r\n";
        send += "\t<methodName>TRO_BattleMech_Post2010</methodName>\r\n";
        send += "\t<params>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + m.GetSolaris7ID() + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + ArmoryID + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + UserID + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + m.GetName() + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + m.GetModel() + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + m.GetTonnage() + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        if( m.IsQuad() ) {
            send += "\t\t\t\t<string>Quad</string>\r\n";
        } else {
            send += "\t\t\t\t<string>Biped</string>\r\n";
        }
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        switch( m.GetBaseTechbase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                send += "\t\t\t\t<string>Inner Sphere</string>\r\n";
                break;
            case AvailableCode.TECH_CLAN:
                send += "\t\t\t\t<string>Clan</string>\r\n";
                break;
            case AvailableCode.TECH_BOTH:
                send += "\t\t\t\t<string>Mixed</string>\r\n";
                break;
        }
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + m.GetDeprecatedLevel() + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + CommonTools.GetRulesLevelString(m.GetRulesLevel()) + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + CommonTools.DecodeEra(m.GetEra()) + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + TROYear + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + m.GetYear() + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + ImageID + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t\t<param>\r\n";
        send += "\t\t\t<value>\r\n";
        send += "\t\t\t\t<string>" + filehandlers.FileCommon.EncodeFluff( HTML ) + "</string>\r\n";
        send += "\t\t\t</value>\r\n";
        send += "\t\t</param>\r\n";
        send += "\t</params>\r\n";
        send += "</methodCall>\r\n";

        Document dc = MethodCall( send );
        retval = ExtractMechID( dc );

        return retval;
    }

    private HttpURLConnection GetConnection() throws Exception {
        try {
            URL u = new URL( Server );
            URLConnection uc = u.openConnection();
            HttpURLConnection connection = (HttpURLConnection) uc;
            connection.setDoOutput( true );
            connection.setDoInput( true );
            connection.setRequestMethod( "POST" );
            return connection;
        } catch( Exception e ) {
            throw e;
        }
    }

    private Document MethodCall( String send ) throws Exception {
        HttpURLConnection connection = null;
        OutputStream out = null;
        OutputStreamWriter wout = null;
        InputStream in = null;
        Document dc = null;
        try {
            connection = GetConnection();
            out = connection.getOutputStream();
            wout = new OutputStreamWriter(out, "UTF-8");
            wout.write( "RPCxml=" + URLEncoder.encode( send, "UTF-8" ) ); 

            wout.flush();
            out.close();

            in = connection.getInputStream();
            dc = GetXML( in );
            if( dc == null ) {
                if( in != null ) { in.close(); }
                if( out != null ) { out.close(); }
                if( connection != null ) { connection.disconnect(); }
                throw new Exception( "An error occured with the server:\nNo data was returned.\nPlease try the request again later." );
            }

            in.close();
            out.close();
            connection.disconnect();
        } catch( Exception e ) {
            if( in != null ) { in.close(); }
            if( out != null ) { out.close(); }
            if( connection != null ) { connection.disconnect(); }
            throw e;
        }
        return dc;
    }

    private Document GetXML( InputStream is ) throws Exception {
        Document retval;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        retval = db.parse( is );
        if( retval.hasChildNodes() ) {
            return retval;
        } else {
            return null;
        }
    }

    private int ExtractMemberID( Document d ) throws Exception {
        int retval = -1;
        NodeList nl = d.getElementsByTagName( "member" );
        if( nl.getLength() < 1 ) {
            if( d.getElementsByTagName( "fault" ).getLength() > 0 ) {
                throw new Exception( "A fault was returned by the server and no Member ID was returned." );
            } else {
                throw new Exception( "Could not find a list of XML members while retrieving MemberID.\nDocument contains no data." );
            }
        }
        for( int i = 0; i < nl.getLength(); i++ ) {
            NodeList nodes = nl.item( i ).getChildNodes();
            if( nodes.item( 0 ).getTextContent() != null ) {
                if( nodes.item( 0 ).getTextContent().equals( "MemberID" ) ) {
                    try {
                        return Integer.parseInt( nodes.item( 1 ).getTextContent() );
                    } catch( Exception e ) {
                        throw new Exception( "Could not extract the Member ID.\nEither the username and password are incorrect\nor no meaningful data was returned." );
                    }
                }
            }
        } 
        return retval;
    }

    private String ExtractMechID( Document d ) throws Exception {
        String retval = "0";
        NodeList nl = d.getElementsByTagName( "i4" );
        if( nl.getLength() < 1 ) {
            if( d.getElementsByTagName( "fault" ).getLength() > 0 ) {
                throw new Exception( "A fault was returned by the server and the mech was not posted." );
            } else {
                throw new Exception( "Could not find a list of XML members while retrieving the Mech ID.\nDocument contains no data." );
            }
        }
        retval = nl.item( 0 ).getTextContent();
        return retval;
    }

    private Armory[] ExtractArmories( Document d ) throws Exception {
        Armory[] retval = null;
        ArrayList v = new ArrayList();

        NodeList nl = d.getElementsByTagName( "struct" );
        if( nl.getLength() < 1 ) {
            if( d.getElementsByTagName( "fault" ).getLength() > 0 ) {
                throw new Exception( "A fault was returned by the server and no Armories were returned." );
            } else {
                throw new Exception( "Could not find a list of XML structs while retrieving Armories.\nDocument contains no data." );
            }
        }
        // ditch the first struct because it contains the other structs
        for( int i = 1; i < nl.getLength(); i++ ) {
            NodeList nodes = nl.item( i ).getChildNodes();
            if( nodes.getLength() == 2 ) {
                // we have an armory
                String ID = "";
                String Name = "";
                Node member1 = nodes.item( 0 );
                Node member2 = nodes.item( 1 );
                if( member1.getFirstChild().getTextContent().equals( "ArmoryID" ) ) {
                    ID = member1.getLastChild().getTextContent();
                } else {
                    // no armory information
                    break;
                }
                if( member2.getFirstChild().getTextContent().equals( "Name" ) ) {
                    Name = member2.getLastChild().getTextContent();
                } else {
                    // no armory information
                    break;
                }
                Armory a = new Armory();
                a.ID = ID;
                a.Name = Name;
                v.add( a );
            }
        }
        if( v.size() > 0 ) {
            retval = new Armory[v.size()];
            for( int i = 0; i < v.size(); i++ ) {
                retval[i] = (Armory) v.get( i );
            }
        }
        return retval;
    }

    private String[] ExtractImageList( Document d ) throws Exception {
        ArrayList v = new ArrayList();
        NodeList nl = d.getElementsByTagName( "struct" );
        if( nl.getLength() < 1 ) {
            if( d.getElementsByTagName( "fault" ).getLength() > 0 ) {
                throw new Exception( "A fault was returned by the server and no Images were returned." );
            } else {
                throw new Exception( "Could not find a list of XML structs while retrieving Images.\nDocument contains no data." );
            }
        }
        // ditch the first struct because it contains the other structs
        for( int i = 1; i < nl.getLength(); i++ ) {
            NodeList nodes = nl.item( i ).getChildNodes();
            ImageID im = new ImageID();

            if( nodes.getLength() == 3 ) {
                // we have an image
                Node member1 = nodes.item( 0 );
                Node member2 = nodes.item( 1 );
                Node member3 = nodes.item( 2 );

                if( member1.getFirstChild().getTextContent().equals( "ImageID" ) ) {
                    im.ID = member1.getLastChild().getTextContent();
                } else {
                    // no image information
                    break;
                }

                if( member2.getFirstChild().getTextContent().equals( "Name" ) ) {
                    im.Name = member2.getLastChild().getTextContent();
                } else {
                    // no image information
                    break;
                }

                if( member3.getFirstChild().getTextContent().equals( "URL" ) ) {
                    im.URL = member3.getLastChild().getTextContent();
                } else {
                    // no image information
                    break;
                }
            }
            v.add( im );
        }

        String[] retval = new String[v.size()];
        for( int i = 0; i < v.size(); i++ ) {
            retval[i] = ((ImageID) v.get( i )).toString();
        }
        return retval;
    }

    private class Armory {
        public String ID = "";
        public String Name = "";
        @Override
        public String toString() {
            return ID + " " + Name;
        }
    }

    private class ImageID {
        public String ID = "";
        public String Name = "";
        public String URL = "";
        @Override
        public String toString() {
            return ID + "," + Name + "," + URL;
        }
    }
}