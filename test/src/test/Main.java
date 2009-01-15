/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.print.Paper;

/**
 *
 * @author jbengtson
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Paper p = new Paper();
        System.out.println( "Height: " + p.getHeight() );
        System.out.println( "Width: " + p.getWidth() );
        p.setSize( 595.0d, 842.0d );
        System.out.println( "Height: " + p.getHeight() );
        System.out.println( "Width: " + p.getWidth() );
        System.out.println( "IHeight: " + p.getImageableHeight() );
        System.out.println( "IWidth: " + p.getImageableWidth() );
        p.setImageableArea( 18.0d, 18.0d, 595.0d, 842.0d );
        System.out.println( "IHeight: " + p.getImageableHeight() );
        System.out.println( "IWidth: " + p.getImageableWidth() );
    }
}
