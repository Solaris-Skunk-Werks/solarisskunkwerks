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

import Force.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class XMLWriter {
    private String Scenario = "";
    private Force leftForce;
    private Force rightForce;
    private Scenario scenario;

    public XMLWriter() {
        
    }

    public XMLWriter( String Scenario, Force left, Force right ) {
        this.Scenario = Scenario;
        leftForce = left;
        rightForce = right;
    }

    public XMLWriter( Force left, Force right ) {
        this.Scenario = "";
        leftForce = left;
        rightForce = right;
    }

    public XMLWriter( Scenario scenario ) {
        this.scenario = scenario;
    }

    public void WriteScenario( Scenario scenario, String filename ) throws IOException {
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        // beginning of an XML file:
        FR.write( "<?xml version=\"1.0\" encoding =\"UTF-8\"?>" );
        FR.newLine();

        scenario.SerializeXML(FR);

        FR.newLine();
        FR.close();
    }

    public void WriteScenario( String filename ) throws IOException {
        WriteScenario( this.scenario, filename );
    }

    public void WriteXML( String filename ) throws IOException {
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );
        
        // beginning of an XML file:
        FR.write( "<?xml version=\"1.0\" encoding =\"UTF-8\"?>" );
        FR.newLine();

        FR.write( "<forces scenario=\"" + this.Scenario + "\">" );
        FR.newLine();

        // start parsing the forces

        leftForce.SerializeXML(FR);
        rightForce.SerializeXML(FR);

        FR.write( "</forces>" );
        FR.newLine();

        FR.close();
    }

    public void SerializeForce( Force f, String filename ) throws IOException {
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        // beginning of an XML file:
        FR.write( "<?xml version=\"1.0\" encoding =\"UTF-8\"?>" );
        FR.newLine();

        f.SerializeXML(FR);

        FR.close();
    }

    public void SerializeWarriors( Warriors w, String filename ) throws IOException {
        if ( !filename.endsWith(".psn") ) { filename += ".psn"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        // beginning of an XML file:
        FR.write( "<?xml version=\"1.0\" encoding =\"UTF-8\"?>" );
        FR.newLine();

        w.SerializeXML(FR);

        FR.close();
    }

}
