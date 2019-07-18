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

import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Node;

/**
 * This abstract stores a Description and Value for a Bonus and Objective which
 * are both Warchest Items
 *
 * @author George Blouin
 */
public class abWarchestItem implements ifSerializable {
    private String Description = "";
    private int Value = 0;

    public abWarchestItem( String Description, int Value ) {
        this.Description = Description;
        this.Value = Value;
    }

    public abWarchestItem( Node n ) {
        this.Description = n.getTextContent().trim();
        this.Value = Integer.parseInt(n.getAttributes().getNamedItem("value").getTextContent().trim());
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String SerializeClipboard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String SerializeData() {
        return SerializeClipboard();
    }
    
    public String getDescription() {
        return Description;
    }

    public void setDescription( String description ) {
        this.Description = description;
    }

    public int getValue() {
        return Value;
    }

    public void setValue( int value ) {
        this.Value = value;
    }
}
