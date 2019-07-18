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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataReader {
    Document load;
    DocumentBuilderFactory dbf;
    DocumentBuilder db;

    public DataReader() {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        dbf.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
        dbf.setExpandEntityReferences(false);
    }
    public Node ReadNode( String filename, String nodeName ) throws Exception {
        try {
            filename = CommonTools.SafeFileName( filename );

            db = dbf.newDocumentBuilder();
            load = db.parse( filename );
            NodeList n = load.getElementsByTagName(nodeName);
            if ( n.getLength() > 0 ) {
                return n.item(0);
            } else {
                return null;
            }
        } catch ( Exception e ) {
            throw e;
        }
    }

    public NodeList ReadNodes( String filename, String nodeName ) throws Exception {
        try {
            filename = CommonTools.SafeFileName( filename );

            db = dbf.newDocumentBuilder();
            load = db.parse( filename );
            NodeList n = load.getElementsByTagName(nodeName);
            if ( n.getLength() > 0 ) {
                return n;
            } else {
                return null;
            }
        } catch ( Exception e ) {
            throw e;
        }
    }
}
