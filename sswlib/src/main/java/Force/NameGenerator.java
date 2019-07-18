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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NameGenerator {
    private ArrayList<String> MaleFirstNames = new ArrayList<String>();
    private ArrayList<String> FemaleFirstNames = new ArrayList<String>();
    private ArrayList<String> LastNames = new ArrayList<String>();

    public NameGenerator() {
        LoadFile("./Data/Personnel/last_names.txt", LastNames);
        LoadFile("./Data/Personnel/first_names_female.txt", FemaleFirstNames);
        LoadFile("./Data/Personnel/first_names_male.txt", MaleFirstNames);
    }

    public String SimpleGenerate() {
        ArrayList<String> FirstNames = new ArrayList<String>();
        FirstNames.addAll(MaleFirstNames);
        FirstNames.addAll(FemaleFirstNames);

        String data = "";
        java.util.Random random = new Random();
        int Row = random.nextInt(FirstNames.size());

        data += FirstNames.get(Row) + " ";

        Row = random.nextInt(LastNames.size());

        data += LastNames.get(Row);

        return data;
    }

    private void LoadFile( String filename, ArrayList<String> store ) {
        try {
            store.clear();
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Is there a comma in the line?  If not ignore it
                if ( line.contains(",") ) {
                    String[] parts = line.split(",");
                    int val = -1;
                    try {
                        //Is the last section of the line numeric?  If not ignore it
                        val = Integer.parseInt(parts[parts.length-1].trim());
                    } catch ( Exception e ) {
                        //do nothing
                    }
                    if (val >= 0) {
                        for ( int i=0; i < val; i++ ) {
                            store.add(parts[0]);
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
