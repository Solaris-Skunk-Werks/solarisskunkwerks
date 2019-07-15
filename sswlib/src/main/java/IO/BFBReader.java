/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
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

package IO;

import common.CommonTools;
import Force.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.XMLConstants;
import org.w3c.dom.*;

public class BFBReader {
    Document load;
    DocumentBuilderFactory dbf;
    DocumentBuilder db;

    public BFBReader() {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        dbf.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
        dbf.setExpandEntityReferences(false);
    }
    
    public Scenario ReadScenario( String filename ) throws Exception {
        System.out.println("Loading Scenario from " + filename);
        Scenario scenario = new Scenario();
        filename = CommonTools.SafeFileName( filename );
        db = dbf.newDocumentBuilder();
        load = db.parse( filename );

        if ( load.getFirstChild().getNodeName().equals("scenario") ) {
            scenario = new Scenario( load.getFirstChild() );
        } else if ( load.getFirstChild().getNodeName().equals("forces") ) {
            try {
                NodeList nl = load.getElementsByTagName("force");
                Node n = load.getFirstChild();
                scenario.setName(n.getAttributes().getNamedItem("scenario").getTextContent().trim());
                scenario.getForces().clear();
                scenario.AddForce( new Force(nl.item(0)) );
                scenario.AddForce( new Force(nl.item(1)) );
            } catch ( Exception e ) {
                System.out.println(e.getMessage());
                throw e;
            }
        }

        return scenario;
    }

    public void ReadUnit( Force force, String filename ) throws Exception {
        System.out.println("Loading Force from " + filename);
        filename = CommonTools.SafeFileName( filename );

        try {
            db = dbf.newDocumentBuilder();
            load = db.parse( filename );
            NodeList n = load.getElementsByTagName("force");
            if ( n.getLength() > 0 ) { force.Load(n.item(0)); }
        } catch ( Exception e ) {
            throw e;
        }
    }

    public void ReadMUL( Force force, String filename ) throws Exception {
        System.out.println("Loading MUL from " + filename);
        filename = CommonTools.SafeFileName( filename );

        db = dbf.newDocumentBuilder();
        load = db.parse( filename );
        NodeList n = load.getElementsByTagName("unit");   
    }

    public Node ReadWarriors( String filename ) throws Exception {
        System.out.println("Loading Warriors from " + filename);
        filename = CommonTools.SafeFileName( filename );

        db = dbf.newDocumentBuilder();
        load = db.parse( filename );
        NodeList n = load.getElementsByTagName("warriors");
        if ( n.getLength() > 0 ) {
            return n.item(0);
        } else {
            return null;
        }
    }
}
