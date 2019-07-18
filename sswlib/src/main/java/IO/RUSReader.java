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

package IO;

import Force.RUS;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RUSReader {

      /**
      * This method loads the given text file from disk and creates an RUS object full of the
      * options provided.
       * Expected format: AS7-D Atlas,80
       * If the last comma in the line contains a numeric value it will be read, otherwise ignored
      * 
      * @param filename The canonical filename to load this.
      * @return An RUS object filled with whatever options were in the file
      * @throws java.lang.FileNotFoundException 
      */
    public void Load( String filename, RUS rus ) throws FileNotFoundException {
        try {
            rus.ClearItems();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
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
                        rus.AddItem(line, line.substring(0, line.lastIndexOf(",")), val);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            //Logger.getLogger(RUSReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
