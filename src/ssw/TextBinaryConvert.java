/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 *
 * @author justin
 */
public class TextBinaryConvert {
    // converts a text delimited file into a binary file
    // it will use the newline character \n to determine when a string ends
    // since each string must be broken up into characters.

    public void Convert( String input, String output ) {
        // take two filenames as input.
        BufferedReader FR;
        DataOutputStream FW;
        try {
            FR = new BufferedReader( new FileReader( input ) );
            FW = new DataOutputStream( new FileOutputStream( output ) );
            boolean EOF = false;
            String read = "";
            while( EOF == false ) {
                read = FR.readLine();
                if( read == null ) {
                    // We've hit the end of the file.
                    EOF = true;
                } else {
                    if( read.equals( "EOF" ) ) {
                        // end of file.
                        EOF = true;
                    } else {
                        ProcessString( FW, read );
                    }
                }
            }
            FR.close();
            FW.close();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private void ProcessString( DataOutputStream FW, String read ) throws Exception {
        // here we're going to read the data string and output it to binary form
        // Assuming pipe-delimited for now.
        String[] data = read.split( ";" );
        // this is very unsafe, but we're going to assume that all the information
        // is correct and in the proper order.
        FW.writeUTF( data[0] );
        FW.writeUTF( data[1] );
        FW.writeUTF( data[2] );
        FW.writeUTF( data[3] );
        FW.writeUTF( data[4] );
        FW.writeInt( Integer.parseInt( data[5] ) );
        FW.writeInt( Integer.parseInt( data[6] ) );
        FW.writeInt( Integer.parseInt( data[7] ) );
        FW.writeInt( Integer.parseInt( data[8] ) );
        FW.writeInt( Integer.parseInt( data[9] ) );
        FW.writeInt( Integer.parseInt( data[10] ) );
        FW.writeInt( Integer.parseInt( data[11] ) );
        FW.writeBoolean( Boolean.parseBoolean( data [12] ) );
        FW.writeInt( Integer.parseInt( data[13] ) );
        FW.writeInt( Integer.parseInt( data[14] ) );
        FW.writeInt( Integer.parseInt( data[15] ) );
        FW.writeInt( Integer.parseInt( data[16] ) );
        FW.writeInt( Integer.parseInt( data[17] ) );
        FW.writeInt( Integer.parseInt( data[18] ) );
        FW.writeBoolean( Boolean.parseBoolean( data [19] ) );
        FW.writeInt( Integer.parseInt( data[20] ) );
        FW.writeInt( Integer.parseInt( data[21] ) );
        FW.writeBoolean( Boolean.parseBoolean( data [22] ) );
        FW.writeFloat( Float.parseFloat( data[23] ) );
        FW.writeInt( Integer.parseInt( data[24] ) );
        FW.writeInt( Integer.parseInt( data[25] ) );
        FW.writeInt( Integer.parseInt( data[26] ) );
        FW.writeFloat( Float.parseFloat( data[27] ) );
        FW.writeFloat( Float.parseFloat( data[28] ) );
        FW.writeFloat( Float.parseFloat( data[29] ) );
    }
}
