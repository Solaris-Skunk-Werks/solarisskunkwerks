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

package ssw;

import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ssw.gui.*;

public class Main {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        Preferences prefs = Preferences.userRoot().node( common.Constants.SSWPrefs );
        prefs.remove( "FileToOpen" );
        final int screensize = prefs.getInt( "SSWScreenSize", SSWConstants.SCREEN_SIZE_NORMAL );

        //Was trying to save args into Prefs and read inside frmMain but keeps showing up as blank!?!?!
        //Turns out that the prefs are going to ssw/gui/frmMain but are going to java/lang.
        //args = new String[]{"k:\\ssw\\master\\Anvil ANV-6M.ssw"};

        if ( args.length >= 1 ) {
            prefs.put("FileToOpen", args[0].toString());
        }

        Runtime runtime = Runtime.getRuntime();
        System.out.println("Memory Allocated [" + runtime.maxMemory() / 1000 + "]");

        //Requires 256mb of memory for canon dot pattern printing.
        if( runtime.maxMemory() < 256000000 ) {
            try {
                String[] call = { "java", "-Xmx256m", "-jar", "SSW.jar" };
                runtime.exec( call );
                System.exit( 0 );
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }

        // uncomment the following line before creating a build.
        SetupLogFile( SSWConstants.LogFileName );

        try {
            // added code to turn off the boldface of Metal L&F.
            // Set System L&F
            if( ! UIManager.getSystemLookAndFeelClassName().equals( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" ) ) {
                UIManager.put( "swing.boldMetal", Boolean.FALSE );
                UIDefaults uiDefaults = UIManager.getDefaults();
                Font f = uiDefaults.getFont( "Label.font" );
                uiDefaults.put( "Label.font", f.deriveFont( f.getStyle(), 10.0f ));
                f = uiDefaults.getFont( "ComboBox.font" );
                uiDefaults.put( "ComboBox.font", f.deriveFont( f.getStyle(), 10.0f ));
                f = uiDefaults.getFont( "Button.font" );
                uiDefaults.put( "Button.font", f.deriveFont( f.getStyle(), 10.0f ));
                f = uiDefaults.getFont( "CheckBox.font" );
                uiDefaults.put( "CheckBox.font", f.deriveFont( f.getStyle(), 10.0f ));
                UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );
            } else {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.err.flush();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.flush();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
            System.err.flush();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.flush();
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                javax.swing.JFrame MainFrame;
                switch( screensize ) {
                    case SSWConstants.SCREEN_SIZE_WIDE_1280:
                        MainFrame = new frmMainWide();
                        MainFrame.setSize( 1240, 610 );
                        break;
                    default:
                        MainFrame = new frmMain();
                        MainFrame.setSize( 750, 610 );
                        break;
                }

                MainFrame.setLocationRelativeTo( null );
                MainFrame.setResizable( true );
                MainFrame.setDefaultCloseOperation( javax.swing.JFrame.DISPOSE_ON_CLOSE );
                MainFrame.setVisible( true );
            }
        });

        System.out.flush();
        System.err.flush();
    }

    private static void SetupLogFile( String LogFile ) {
        // Inspriration for this from Megamek.  mine is simpler and probably not
        // as fully featured, but does the job.
        try {
            PrintStream ps = new PrintStream( new BufferedOutputStream( new FileOutputStream( LogFile ), 64 ) );
            System.setOut(ps);
            System.setErr(ps);
            System.out.println( "Log File open for business..." );
        } catch (Exception e) {
            System.err.println( "Unable to send output to " + LogFile );
//            e.printStackTrace();
//            System.err.flush();
        }
    }
}
