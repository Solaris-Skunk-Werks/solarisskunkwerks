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

/*
 * This code borrows liberally from HTMLDocumentEditor.java by Charles Bell,
 * circa May, 2002.  There was no license on the original, so I assumed public
 * domain.  This version is designed to be embedded into a pre-existing project
 * as a JPanel, rather than it's own application.  I have also included several
 * application-specific methods for our usage.
 */

package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Writer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;
import javax.swing.text.html.MinimalHTMLWriter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class HTMLPane extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 8903309571492323612L;
    private HTMLDocument Document;
    private JPanel Controls = new JPanel();
    private JScrollPane Scroller;
    private JTextPane Text = new JTextPane();

    private JButton Bold, Italic, Underline, Cut, Copy, Paste,
                    AlignLeft, AlignRight, AlignCenter;
    private JMenuBar MainMenu;
    private JMenu EditMenu, Font, FontSize, FontColor, Style;
    private JMenuItem Black, Yellow, Red, Orange, Blue, Green, Cyan, Magenta,
                      Undo, Redo, Source, Clear, SelectAll, // Bullets, Unordered, // Not Used
                      Subscript, Superscript, Strikethrough;

    protected UndoableEditListener UndoHandler = new UndoHandler();

    protected UndoManager UndoManager = new UndoManager();

    private UndoAction UndoAction = new UndoAction();
    private RedoAction RedoAction = new RedoAction();

    private Action CutAction = new DefaultEditorKit.CutAction();
    private Action CopyAction = new DefaultEditorKit.CopyAction();
    private Action PasteAction = new DefaultEditorKit.PasteAction();

    private Action BoldAction = new StyledEditorKit.BoldAction();
    private Action UnderlineAction = new StyledEditorKit.UnderlineAction();
    private Action ItalicAction = new StyledEditorKit.ItalicAction();

    // private javax.swing.ImageIcon check = new javax.swing.ImageIcon( getClass().getResource("/images/ui-check-box.png") ); // Not Used

/*    private HTMLEditorKit.InsertHTMLTextAction UnorderedListAction
        = new HTMLEditorKit.InsertHTMLTextAction( "Bullets", "<ul><li> </li></ul>", HTML.Tag.P, HTML.Tag.UL );
    private HTMLEditorKit.InsertHTMLTextAction BulletAction
        = new HTMLEditorKit.InsertHTMLTextAction( "Bullets", "<li> </li>", HTML.Tag.UL, HTML.Tag.LI );*/

    public HTMLPane() {
        this(false);
    }

    public HTMLPane( boolean OneLine ) {
        SSWHTMLEditorKit Kit = new SSWHTMLEditorKit();
        Document = (HTMLDocument) Kit.createDefaultDocument();
        Document.putProperty( "IgnoreCharsetDirective", true );
        Document.setPreservesUnknownTags( false );
        Document.addUndoableEditListener( UndoHandler );
        InitComponents( OneLine );
        ResetUndoManager();
    }

/**
 * Sets the dimensions of this editor panel.  HTMLPane has a practical lower
 * limit on width of 300 units.  Anything less will be ignored.  Also note that
 * this is the size of the actual editor component and does not include the
 * height of the controls, which are 50~60 units by themselves.  A good
 * practical size is at least 300 x 120, resulting in a total rough size of
 * 300 x 180.  Note that you may have to repaint() or pack() after doing this.
 *
 * @param SizeX The preferred width
 * @param SizeY The preferred height
 */
    public void SetEditorSize( int SizeX, int SizeY ) {
        Scroller.setPreferredSize( new java.awt.Dimension( SizeX, SizeY ) );
    }

/**
 * Sets the dimensions of this editor panel.  HTMLPane has a practical lower
 * limit on width of 300 units.  Anything less will be ignored.  Also note that
 * this is the size of the actual editor component and does not include the
 * height of the controls, which are 50~60 units by themselves.  A good
 * practical size is at least 300 x 120, resulting in a total rough size of
 * 300 x 180.  Note that you may have to repaint() or pack() after doing this.
 *
 * @param d The java.awt.Dimension cooresponding to the new preferred size.
 */
    public void SetEditorSize( java.awt.Dimension d ) {
        Scroller.setPreferredSize( d );
    }

/**
 * Provides access to the HTMLDocument we are currently editing
 *
 * @return The HTMLDocument
 */
    public HTMLDocument GetDocument() {
        return (HTMLDocument) Text.getDocument();
    }

/**
 * Returns the HTML source of of the current document, stripped of everything
 * except for what is between the body tags.
 *
 * @return A string containing the above.
 */
    public String GetHTMLSource() {
        if( Source.getText().equals( "View Document" ) ) {
            ViewSource();
        }
        String text = Text.getText();
        int start = text.indexOf( "<body>" ) + 7;
        int end = text.indexOf( "</body>" ) - 3;
        if ( end <= start ) { end = start + 1; }
        return text.substring( start, end ).replace( "\n", "" ).replace( "      ", "" ).replace( "    ", "" ).replace( "<p style=\"margin-top: 0\"></p>", "" );
    }

/**
 * Returns only the text of what is currently being editted, without HTML markup
 *
 * @return A String containing the above.
 */
    public String GetStrippedText() {
        HTMLDocument Doc = (HTMLDocument) Text.getDocument();
        String retval = "Operation Failed!";
        try {
            retval = Doc.getText( 0, Doc.getLength() );
            // remove the extraneous crap when nothing is in the box.
            retval = retval.replace( "<p style=\"margin-top: 0\"></p>\n", "" );
            retval = retval.replace( "\n", "" );
            if( retval.replace( "\n\r", "" ).length() < 2 ) { return ""; }
            return ">" + retval.replace( "      ", "" ) + "<";
        } catch( Exception e ) {
            return retval;
        }
    }

    public void SetText( String s ) {
        Text.setText( s );
        repaint();
    }

    protected void ResetUndoManager() {
        UndoManager.discardAllEdits();
        UndoAction.update();
        RedoAction.update();
    }

/**
 * Clears the current text and starts fresh with a new document
 */
    public void StartNewDocument(){
        Document OldDoc = Text.getDocument();
        if( OldDoc != null ) { OldDoc.removeUndoableEditListener( UndoHandler ); }
        HTMLEditorKit Kit = new SSWHTMLEditorKit();
        Document = (HTMLDocument) Kit.createDefaultDocument();
        Document.putProperty( "IgnoreCharsetDirective", true );
        Document.setPreservesUnknownTags( false );
        Text.setDocument( Document );
        Text.getDocument().addUndoableEditListener( UndoHandler );
        ResetUndoManager();
    }

    public void actionPerformed( ActionEvent e ) {
        String Command = e.getActionCommand();
        if( Command.compareTo( "Clear" ) == 0 ) {
            StartNewDocument();
        } else if ( Command.compareTo( "Select All" ) == 0 ) {
            Text.selectAll();
        } else if ( Command.compareTo( "View Source" ) == 0 ) {
            ViewSource();
        } else if ( Command.compareTo( "View Document" ) == 0 ) {
            ViewSource();
        }
    }

    private void ViewSource() {
        if( Text.getContentType().equals( "text/html" ) ) {
            String SourceCode = Text.getText();
            Text.setContentType( "text/plain" );
            Text.setText( SourceCode );
            Source.setText( "View Document" );
            repaint();
        } else {
            String SourceCode = Text.getText();
            Text.setContentType( "text/html" );
            Text.setText( SourceCode );
            Source.setText( "View Source" );
            repaint();
        }
    }

    private void InitComponents( boolean OneLine ) {
        Font = new JMenu( "" );
        Font.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit.png") ) );
        String[] FontTypes = { "SansSerif", "Serif", "Monospaced", "Dialog", "DialogInput" };
        for( int i = 0; i < FontTypes.length; i++ ) {
            JMenuItem nextTypeItem = new JMenuItem( FontTypes[i] );
            nextTypeItem.setAction( new StyledEditorKit.FontFamilyAction( FontTypes[i], FontTypes[i] ) );
            Font.add( nextTypeItem );
        }

        FontSize = new JMenu( "" );
        FontSize.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-size.png") ) );
        int[] FontSizes = { 6, 8, 10, 12, 14, 16, 20, 24, 32, 36, 48, 72 };
        for( int i = 0; i < FontSizes.length; i++ ) {
            JMenuItem nextSizeItem = new JMenuItem( String.valueOf( FontSizes[i] ) );
            nextSizeItem.setAction( new StyledEditorKit.FontSizeAction( String.valueOf( FontSizes[i] ), FontSizes[i] ) );
            FontSize.add( nextSizeItem );
        }

        FontColor = new JMenu( "" );
        FontColor.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-color.png") ) );
        Red = new JMenuItem( new StyledEditorKit.ForegroundAction( "Red", Color.red ) );
        Orange = new JMenuItem( new StyledEditorKit.ForegroundAction( "Orange", Color.orange ) );
        Yellow = new JMenuItem( new StyledEditorKit.ForegroundAction( "Yellow", Color.yellow ) );
        Green = new JMenuItem( new StyledEditorKit.ForegroundAction( "Green", Color.green ) );
        Blue = new JMenuItem( new StyledEditorKit.ForegroundAction( "Blue", Color.blue ) );
        Cyan = new JMenuItem( new StyledEditorKit.ForegroundAction( "Cyan", Color.cyan ) );
        Magenta = new JMenuItem( new StyledEditorKit.ForegroundAction( "Magenta", Color.magenta ) );
        Black = new JMenuItem( new StyledEditorKit.ForegroundAction( "Black", Color.black ) );
        FontColor.add( Black );
        FontColor.add( Red );
        FontColor.add( Orange );
        FontColor.add( Yellow );
        FontColor.add( Green );
        FontColor.add( Blue );
        FontColor.add( Cyan );
        FontColor.add( Magenta );

        EditMenu = new JMenu( "Edit" );
        Undo = new JMenuItem( UndoAction );
        Undo.setText( "Undo" );
        Undo.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/arrow-circle-315-left.png") ) );
        Redo = new JMenuItem( RedoAction );
        Redo.setText( "Redo" );
        Redo.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/arrow-circle-315.png") ) );
        Source = new JMenuItem( "View Source" );
        Clear = new JMenuItem( "Clear" );
        SelectAll = new JMenuItem( "Select All" );
        Source.addActionListener( this );
        Clear.addActionListener( this );
        SelectAll.addActionListener( this );
        EditMenu.add( Undo );
        EditMenu.add( Redo );
        EditMenu.add( SelectAll );
        EditMenu.add( Source );
        EditMenu.add( Clear );

        Style = new JMenu( "Style" );
/*        Bullets = new JMenuItem( BulletAction );
        Bullets.setText( "Bullets" );
        Bullets.setIcon( new javax.swing.ImageIcon( getClass().getResource( "/images/edit-list-order.png" ) ) );
        Unordered = new JMenuItem( UnorderedListAction );
        Unordered.setText( "Unordered List" );
        Unordered.setIcon( new javax.swing.ImageIcon( getClass().getResource( "/images/edit-list.png" ) ) );*/
        Subscript = new JMenuItem( new SubscriptAction() );
        Subscript.setText( "Subscript" );
        Subscript.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-subscript.png") ) );
        Superscript = new JMenuItem( new SuperscriptAction() );
        Superscript.setText( "Superscript" );
        Superscript.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-superscript.png") ) );
        Strikethrough = new JMenuItem( new StrikeThroughAction() );
        Strikethrough.setText( "Strikethrough" );
        Strikethrough.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-strike.png") ) );
/*        Style.add( Bullets );
        Style.add( Unordered );*/
        Style.add( Subscript );
        Style.add( Superscript );
        Style.add( Strikethrough );

        MainMenu = new JMenuBar();
        MainMenu.add( EditMenu );
        MainMenu.add( Font );
        MainMenu.add( FontSize );
        MainMenu.add( FontColor );
        MainMenu.add( Style );

        Bold = new JButton( BoldAction );
        Bold.setText( "" );
        Bold.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-bold.png") ) );
        Bold.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        Bold.setToolTipText( "Bold" );
        Italic = new JButton( ItalicAction );
        Italic.setText( "" );
        Italic.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-italic.png") ) );
        Italic.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        Italic.setToolTipText( "Italic" );
        Underline = new JButton( UnderlineAction );
        Underline.setText( "" );
        Underline.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-underline.png") ) );
        Underline.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        Underline.setToolTipText( "Underline" );
        Cut = new JButton( CutAction );
        Cut.setText( "" );
        Cut.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/scissors.png") ) );
        Cut.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        Cut.setToolTipText( "Cut" );
        Copy = new JButton( CopyAction );
        Copy.setText( "" );
        Copy.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/clipboard.png") ) );
        Copy.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        Copy.setToolTipText( "Copy" );
        Paste = new JButton( PasteAction );
        Paste.setText( "" );
        Paste.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/clipboard-paste.png") ) );
        Paste.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        Paste.setToolTipText( "Paste" );
        AlignLeft = new JButton( new StyledEditorKit.AlignmentAction( "Left Align", StyleConstants.ALIGN_LEFT ) );
        AlignLeft.setText( "" );
        AlignLeft.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-alignment.png") ) );
        AlignLeft.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        AlignLeft.setToolTipText( "Align Left" );
        AlignCenter = new JButton( new StyledEditorKit.AlignmentAction( "Center", StyleConstants.ALIGN_CENTER ) );
        AlignCenter.setText( "" );
        AlignCenter.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-alignment-center.png") ) );
        AlignCenter.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        AlignCenter.setToolTipText( "Align Center" );
        AlignRight = new JButton( new StyledEditorKit.AlignmentAction ( "Right Align", StyleConstants.ALIGN_RIGHT ) );
        AlignRight.setText( "" );
        AlignRight.setIcon( new javax.swing.ImageIcon( getClass().getResource("/images/edit-alignment-right.png") ) );
        AlignRight.setPreferredSize( new java.awt.Dimension( 28, 28 ) );
        AlignRight.setToolTipText( "Align Right" );

        Controls.setLayout( new BoxLayout( Controls, BoxLayout.X_AXIS ) );
        Controls.add( Cut );
        Controls.add( Copy );
        Controls.add( Paste );
        Controls.add( Box.createRigidArea( new java.awt.Dimension( 5, 0 ) ) );
        Controls.add( Bold );
        Controls.add( Italic );
        Controls.add( Underline );
        Controls.add( Box.createRigidArea( new java.awt.Dimension( 5, 0 ) ) );
        Controls.add( AlignLeft );
        Controls.add( AlignCenter );
        Controls.add( AlignRight );
        Controls.add( Box.createHorizontalGlue() );

        Text.setContentType( "text/html" );
        Text.setDocument( Document );

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        JPanel MenuAlign = new JPanel();
        MenuAlign.setLayout( new BoxLayout( MenuAlign, BoxLayout.X_AXIS ) );
        MenuAlign.add( MainMenu );
        MenuAlign.add( Box.createHorizontalGlue() );
        if( OneLine ) {
            JPanel Single = new JPanel();
            BoxLayout b = new BoxLayout( Single, BoxLayout.X_AXIS );
            Single.setLayout( b );
            Single.add( MenuAlign );
            Single.add( Box.createRigidArea( new java.awt.Dimension( 5, 0 ) ) );
            Single.add( Controls );
            Single.add( Box.createHorizontalGlue() );
            add( Single );
        } else {
            add( MenuAlign );
            add( Controls );
        }
        add( Box.createRigidArea( new java.awt.Dimension( 0, 5 ) ) );
        Scroller = new JScrollPane( Text );
        add( Scroller );
    }

    private class UndoHandler implements UndoableEditListener {
        public void undoableEditHappened( UndoableEditEvent e ) {
            UndoManager.addEdit( e.getEdit() );
            UndoAction.update();
            RedoAction.update();
        }
    }

    private class UndoAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = -611107834653405452L;

        public UndoAction() {
            super( "Undo" );
            setEnabled( false );
        }

        public void actionPerformed( ActionEvent e ) {
            try {
                UndoManager.undo();
            } catch( CannotUndoException ex ) {
                System.out.println( "Unable to undo: " + ex );
                ex.printStackTrace();
            }
            update();
            RedoAction.update();
        }

        protected void update() {
            if( UndoManager.canUndo() ) {
                setEnabled( true );
                putValue( Action.NAME, UndoManager.getUndoPresentationName() );
            } else {
                setEnabled( false );
                putValue( Action.NAME, "Undo" );
            }
        }
    }

    private class RedoAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = 3081425073533555277L;

        public RedoAction() {
            super( "Redo" );
            setEnabled( false );
        }

        public void actionPerformed( ActionEvent e ) {
            try {
                UndoManager.redo();
            } catch( CannotRedoException ex ) {
                System.err.println( "Unable to redo: " + ex );
                ex.printStackTrace();
            }
            update();
            UndoAction.update();
        }

        protected void update() {
            if( UndoManager.canRedo() ) {
                setEnabled( true );
                putValue( Action.NAME, UndoManager.getRedoPresentationName() );
            } else {
                setEnabled( false );
                putValue( Action.NAME, "Redo" );
            }
        }
    }

    class SubscriptAction extends StyledEditorKit.StyledTextAction {
        /**
         *
         */
        private static final long serialVersionUID = 7299699244636651073L;

        public SubscriptAction() {
            super( StyleConstants.Subscript.toString() );
        }

        public void actionPerformed( ActionEvent e ) {
            JEditorPane editor = getEditor( e );
            if( editor != null ) {
                StyledEditorKit kit = getStyledEditorKit( editor );
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean subscript = (StyleConstants.isSubscript( attr )) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setSubscript( sas, subscript );
                setCharacterAttributes( editor, sas, false );
            }
        }
    }

    class SuperscriptAction extends StyledEditorKit.StyledTextAction {
        /**
         *
         */
        private static final long serialVersionUID = 7113449619152288273L;

        public SuperscriptAction() {
            super( StyleConstants.Superscript.toString() );
        }

        public void actionPerformed( ActionEvent e ) {
            JEditorPane editor = getEditor( e );
            if( editor != null ) {
                StyledEditorKit kit = getStyledEditorKit( editor );
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean superscript = (StyleConstants.isSuperscript( attr )) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setSuperscript( sas, superscript );
                setCharacterAttributes( editor, sas, false );
            }
        }
    }

    class StrikeThroughAction extends StyledEditorKit.StyledTextAction {
        /**
         *
         */
        private static final long serialVersionUID = 7776719295905120017L;

        public StrikeThroughAction() {
            super( StyleConstants.StrikeThrough.toString() );
        }

        public void actionPerformed( ActionEvent e ){
            JEditorPane editor = getEditor( e );
            if( editor != null ) {
                StyledEditorKit kit = getStyledEditorKit( editor );
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean strikeThrough = (StyleConstants.isStrikeThrough( attr )) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setStrikeThrough( sas, strikeThrough );
                setCharacterAttributes( editor, sas, false );
            }
        }
    }

    private class SSWHTMLEditorKit extends HTMLEditorKit {
        /**
         *
         */
        private static final long serialVersionUID = 6286144508677076119L;

        @Override
        public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
            if (doc instanceof HTMLDocument) {
                SSWHTMLWriter w = new SSWHTMLWriter(out, (HTMLDocument)doc, pos, len);
                w.write();
            } else if (doc instanceof StyledDocument) {
                MinimalHTMLWriter w = new MinimalHTMLWriter(out, (StyledDocument)doc, pos, len);
                w.write();
            } else {
                super.write(out, doc, pos, len);
            }
        }
    }

    private class SSWHTMLWriter extends HTMLWriter {
        public SSWHTMLWriter( Writer w, HTMLDocument doc ) {
            super( w, doc );
            setIndentSpace( 0 );
            setCanWrapLines( true );
            setLineSeparator( "" );
        }

        public SSWHTMLWriter( Writer w, HTMLDocument doc, int pos, int len ) {
            super( w, doc, pos, len );
            setIndentSpace( 0 );
            setCanWrapLines( true );
            setLineSeparator( "" );
        }
    }
}
