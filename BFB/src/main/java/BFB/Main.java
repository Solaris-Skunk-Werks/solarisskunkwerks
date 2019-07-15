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

package BFB;

import BFB.GUI.frmBase;
import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

    public static void main(String[] args) {
        String logFile = "./Logs/bfb.log";

        try {
            if (!new File("./Logs/").exists()) {
                new File("./Logs/").mkdir();
            }
            PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile), 64));
            System.setOut(ps);
            System.setErr(ps);
        } catch (Exception ex) {
            System.err.println("Unable to redirect output");
        }

       Runtime runtime = Runtime.getRuntime();
       System.out.println("Battletech Force Balancer is starting...");
       System.out.println("Memory Allocated [" + runtime.maxMemory() / 1000 + "]");
       System.out.flush();
        //Require 256mb of Memory for canon dot patterns
        if (runtime.maxMemory() < 256000000) {
            try {
                String[] call = { "java", "-Xmx256m", "-jar", "BFB.jar" };
                runtime.exec(call);
                System.exit(0);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        try {
            // added code to turn off the boldface of Metal L&F.
            // Set System L&F
            if( UIManager.getSystemLookAndFeelClassName().equals( "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" ) ||
            UIManager.getSystemLookAndFeelClassName().equals( "javax.swing.plaf.metal.MetalLookAndFeel" ) ) {
                UIManager.put( "swing.boldMetal", Boolean.FALSE );
                UIDefaults uiDefaults = UIManager.getDefaults();
                Font f = uiDefaults.getFont( "Label.font" );
                uiDefaults.put( "Label.font", f.deriveFont( f.getStyle(), 11.0f ));
                f = uiDefaults.getFont( "ComboBox.font" );
                uiDefaults.put( "ComboBox.font", f.deriveFont( f.getStyle(), 11.0f ));
                f = uiDefaults.getFont( "Button.font" );
                uiDefaults.put( "Button.font", f.deriveFont( f.getStyle(), 11.0f ));
                UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );
            } else {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }
        }
        catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            //System.err.flush();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            //System.err.flush();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
            //System.err.flush();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            //System.err.flush();
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ImageIcon icon;
                frmBase MainFrame = new frmBase();
                try {
                    icon = new ImageIcon(MainFrame.getClass().getResource("/Images/Battletech.png"));
                    MainFrame.setIconImage(icon.getImage());
                } catch (Exception e) {
                    System.out.println("Error loading Icon image...\n" + e.getMessage());
                }
                MainFrame.setLocationRelativeTo( null );
                //MainFrame.setResizable( false );
                MainFrame.setDefaultCloseOperation( javax.swing.JFrame.DO_NOTHING_ON_CLOSE );
                MainFrame.setVisible( true );
            }
        });
    }
}
