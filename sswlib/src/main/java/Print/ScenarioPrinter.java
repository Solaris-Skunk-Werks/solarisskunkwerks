/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Print;

import Force.Bonus;
import Force.Objective;
import Force.Scenario;

import filehandlers.ImageTracker;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.print.*;

public class ScenarioPrinter implements Printable {
    public Graphics2D Graphic;
    private Scenario scenario = null;
    private PageFormat format = null;
    private String Title = "Scenario Information";
    private int characterWidth = 125,
                characterHalfWidth = 68,
                pageWidth = 0,
                pageHalfWidth = 0;
    private Point currentLocation = new Point(0, 0),
                    savePoint = new Point(0, 0);
    private ImageTracker imageTracker;

    public ScenarioPrinter( ImageTracker imageTracker ) {
        this.imageTracker = imageTracker;
    }

    public ScenarioPrinter( Scenario scenario, ImageTracker imageTracker ) {
        this.scenario = scenario;
        this.imageTracker = imageTracker;
    }

    public void SetScenario( Scenario scenario ) {
        this.scenario = scenario;
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if( scenario == null ) { return Printable.NO_SUCH_PAGE; }
        Graphic = (Graphics2D) graphics;
        format = pageFormat;
        pageWidth = (int) ( pageFormat.getImageableWidth() - ( pageFormat.getImageableX() * 2.0 ) );
        pageHalfWidth = pageWidth / 2;
        Reset();
        Graphic.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        PreparePrint();
        return Printable.PAGE_EXISTS;
    }

    private void PreparePrint() {
        Reset();

        //Battletech Logo on top of sheet
        Image Recordsheet = imageTracker.getImage( PrintConsts.BT_LOGO );
        Graphic.drawImage( Recordsheet, ((int) format.getImageableWidth())-306, 0, 306, 49, null);
        //currentLocation.y += 50;

        Graphic.setFont( PrintConsts.TitleFont);
        for ( String t : PrintConsts.wrapText(scenario.getName(), 28, true) ) {
            Graphic.drawString(t, currentLocation.x, currentLocation.y);
            currentLocation.y += Graphic.getFont().getSize()+1;
        }

        RenderTitle("SITUATION");
        RenderText( scenario.getSituation(), characterWidth );

        RenderTitle("GAME SETUP");
        RenderText( scenario.getSetup(), characterWidth );

        RenderItalic("Attacker");
        RenderText( scenario.getAttacker(), characterWidth );

        RenderItalic("Defender");
        RenderText( scenario.getDefender(), characterWidth );

        if ( scenario.getWarchest().getTrackCost() > 0 ) {
            RenderTitle("Track Cost: " + scenario.getWarchest().getTrackCost());
            currentLocation.y += 10;
        }

        if ( scenario.getWarchest().getBonuses().size() > 0 ) {
            RenderItalic("Optional Bonuses");
            for ( Bonus b : scenario.getWarchest().getBonuses() ) {
                RenderLine( b.toPrint(), characterWidth );
            }
            currentLocation.y += 10;
        }

        if ( scenario.getWarchest().getObjectives().size() > 0 ) {
            RenderItalic("Objectives");
            for ( Objective o : scenario.getWarchest().getObjectives() ) {
                RenderLine( o.toPrint(), characterWidth );
            }
            currentLocation.y += 10;
        }
        
        RenderTitle("SPECIAL RULES");
        RenderText( scenario.getSpecialRules(), characterWidth );

        if ( !scenario.getVictoryConditions().isEmpty() ) {
            RenderTitle("VICTORY CONDITIONS");
            RenderText( scenario.getVictoryConditions(), characterWidth );
        }

        RenderTitle("AFTERMATH");
        RenderText( scenario.getAftermath(), characterWidth );
        
        //Graphic.setFont( PrintConsts.SmallBoldFont );
        //Graphic.drawString(PrintConsts.getCopyright()[0], 100, (int)format.getHeight()-40);
        //Graphic.drawString(PrintConsts.getCopyright()[1], 60, (int)format.getHeight()-30);
    }

    private void RenderTitle( String title) {
        Graphic.setFont( PrintConsts.BoldFont );
        RenderLine( title, characterWidth );
        setPlain();
    }

    private void RenderItalic( String title ) {
        Graphic.setFont( PrintConsts.ItalicFont );
        RenderLine( title, characterWidth );
        setPlain();
    }

    private void RenderText( String text, int Width ) {
        RenderLine( text, Width );
        currentLocation.y += Graphic.getFont().getSize();
    }

    private void RenderLine( String text, int Width ) {
        String[] formattedText = PrintConsts.wrapText(text, Width, false);
        for ( String line : formattedText ) {
            Graphic.drawString(line, currentLocation.x, currentLocation.y);
            currentLocation.y += Graphic.getFont().getSize();
        }
    }

    public void Reset() {
        currentLocation.setLocation(10, (int) format.getImageableY());
    }

    private void setPlain() {
        Graphic.setFont( PrintConsts.PlainFont );
    }
}
