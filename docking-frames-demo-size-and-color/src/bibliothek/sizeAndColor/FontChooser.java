/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.sizeAndColor;

import java.awt.*;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Allows the user to select a font. The user can choose whether the font
 * should be italic or bold and the size of the font.
 * @author Benjamin Sigg
 */
public class FontChooser extends JComponent {
    private Font font = null;
    private JList list = new JList (
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames() );

    private JSpinner numberField;
    private SpinnerNumberModel numberFieldModel;  

    private JCheckBox boxBold = new JCheckBox ("Bold");
    private JCheckBox boxItalic = new JCheckBox ("Italic");

    private JTextField exampleField = new JTextField();
    private JLabel fontLabel = new JLabel();

    private boolean setting = false;

    /**
     * Creates a new chooser
     */
    public FontChooser(){
        numberFieldModel = new SpinnerNumberModel( 12, 5, 1000, 1 );
        numberField = new JSpinner( numberFieldModel );

        ChangeListener listener = new ChangeListener (){
            public void stateChanged ( ChangeEvent e ){
                updateCurrentFont ();
            }};

            exampleField.getDocument().addDocumentListener( new DocumentListener(){
                public void changedUpdate( DocumentEvent e ) {
                    fontLabel.setText( exampleField.getText() );
                }
                public void insertUpdate( DocumentEvent e ) {
                    fontLabel.setText( exampleField.getText() );
                }
                public void removeUpdate( DocumentEvent e ) {
                    fontLabel.setText( exampleField.getText() );
                }
            });

            numberField.addChangeListener( listener );
            boxBold.addChangeListener( listener );
            boxItalic.addChangeListener( listener );
            list.addListSelectionListener( new ListSelectionListener (){
                public void valueChanged ( ListSelectionEvent e ){
                    updateCurrentFont ();
                }
            });

            list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

            exampleField.setText( "AaBbCcDd" );

            // Zusammenbauen der Oberfläche
            createGUI ( list, boxBold, boxItalic, numberField, exampleField, fontLabel );
    }

    protected void createGUI ( JList list, JCheckBox boxBold, JCheckBox boxItalic,
            JSpinner fontSize, JTextField exampleField, JLabel fontLabel ){

        GridBagLayout gbl = new GridBagLayout ();
        GridBagConstraints gbc;

        JScrollPane scroll = new JScrollPane ( list );
        JScrollPane area = new JScrollPane( fontLabel );

        fontLabel.setHorizontalAlignment( SwingConstants.CENTER );
        fontLabel.setVerticalAlignment( SwingConstants.CENTER );

        setLayout( gbl );
        add( scroll );
        add( boxBold );
        add( boxItalic );
        add( fontSize );
        add( area );
        add( exampleField );

        JLabel sizeLabel = new JLabel ( "Size" );
        sizeLabel.setHorizontalAlignment( SwingConstants.CENTER );

        add( sizeLabel );

        gbc = createConstraints ( 1, 0, 1, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints( sizeLabel, gbc );

        gbc = createConstraints ( 2, 0, 1, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints( fontSize, gbc );

        gbc = createConstraints ( 1, 1, 1, 1 );
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints( boxBold, gbc );

        gbc = createConstraints ( 2, 1, 1, 1 );
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints( boxItalic, gbc );

        gbc = createConstraints ( 1, 2, 2, 1 );
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints( exampleField, gbc );

        gbc = createConstraints ( 1, 3, 2, 1 );
        gbc.weighty = 100;
        gbl.setConstraints( area, gbc );

        gbc = createConstraints ( 0, 0, 1, 5 );
        gbl.setConstraints( scroll, gbc );
    }

    protected GridBagConstraints createConstraints ( int x, int y, int w, int h ){
        GridBagConstraints gbc = new GridBagConstraints ();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridheight = h;
        gbc.gridwidth = w;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }
    
    public static Font showDialog( Component owner, String title, Font font ){
        return showDialog ( owner, title, font, "AaBbCcDd" );
    }
    
    public static Font showDialog( Component owner, String title, Font font, String exampleString ){
        FontChooser chooser = new FontChooser();
        chooser.setChoosedFont( font );
        FontDialog dialog;

        if ( owner == null )
            dialog = new FontDialog( chooser, title );
        else{
            Component comp = SwingUtilities.getRoot( owner );

            if ( comp instanceof Frame )
                dialog = new FontDialog( chooser, title, (Frame)comp );
            else if ( comp instanceof Dialog )
                dialog = new FontDialog( chooser, title, (Dialog)comp );
            else
                dialog = new FontDialog( chooser, title );
        }

        chooser.setExampleString( exampleString );
        dialog.pack();
        dialog.setLocationRelativeTo( owner );
        return dialog.showDialog();
    }

    public synchronized Font showDialog ( Font font ){
        return showDialog( null, "Font", font );
    }

    public String getFontName (){
        return (String)list.getSelectedValue();
    }
    public void setFontName ( String name ){
        list.setSelectedValue( name, true );

        Object select = list.getSelectedValue();

        if( select == null || !select.equals( name )){
            list.setSelectedValue( "Default", true );
        }
    }

    public boolean isBold (){
        return boxBold.isSelected();
    }
    public void setBold ( boolean bold ){
        boxBold.setSelected( bold );
    }
    public boolean isItalic (){
        return boxItalic.isSelected();
    }
    public void setItalic ( boolean italic ){
        boxItalic.setSelected( italic );
    }
    public int getFontSize (){
        return numberFieldModel.getNumber().intValue();
    }
    public void setFontSize ( int size ){
        numberFieldModel.setValue( new Integer( size ) );
    }
    
    protected void updateCurrentFont (){
        if ( !setting ){
            font = createCurrentFont ();
            fontLabel.setFont( font );
        }
    }
    
    protected Font createCurrentFont (){
        String name = (String)list.getSelectedValue();

        int style = 0;
        if ( isBold() )
            style = Font.BOLD;
        if ( isItalic() )
            style |= Font.ITALIC;

        return new Font( name, style, getFontSize() );
    }
    
    protected void setCurrentFont (){
        setting = true; 

        if( font == null ){
            font = new Font( (String)list.getModel().getElementAt( 0 ), 0, 12 );
        }

        setFontName( font.getName() );
        setBold( font.isBold() );
        setItalic( font.isItalic() );
        setFontSize( font.getSize() );

        setting = false;
    }

    public void setChoosedFont( Font font ){
        this.font = font;
        setCurrentFont();
    }

    public Font getChoosedFont(){
        return this.font;
    }

    public String getExampleString() {
        return exampleField.getText();
    }
    public void setExampleString(String exampleString) {
        exampleField.setText( exampleString );
    }

    protected static class FontDialog extends JDialog{
        private boolean ok = false;
        private FontChooser chooser;

        public FontDialog( FontChooser chooser, String title, Frame owner ){
            super( owner, title, true );
            this.chooser = chooser;
            init();
        }
        public FontDialog( FontChooser chooser, String title, Dialog owner ){
            super( owner, title, true );
            this.chooser = chooser;
            init();
        }

        public FontDialog( FontChooser chooser, String title ){
            this.chooser = chooser;
            setTitle( title );
            setModal( true );
            init();
        }

        private void init(){
            setLayout( new GridBagLayout());
            add( chooser, new GridBagConstraints( 0, 0, 1, 1, 100.0, 100.0, 
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
                    new Insets( 2, 2, 2, 2 ), 0, 0));
            
            JPanel buttonPanel = new JPanel( new GridLayout( 1, 2 ));
            JButton buttonOk = new JButton( "Ok" );
            getRootPane().setDefaultButton( buttonOk );
            
            JButton buttonCancel = new JButton( "Cancel" );
            buttonPanel.add( buttonOk );
            buttonPanel.add( buttonCancel );
            
            add( buttonPanel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, 
                    GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE, 
                    new Insets( 2, 2, 2, 2 ), 0, 0));

            pack();
            
            buttonOk.addActionListener( new ActionListener(){
                public void actionPerformed( ActionEvent e ) {
                    ok();
                }
            });
            buttonCancel.addActionListener( new ActionListener(){
                public void actionPerformed( ActionEvent e ) {
                    cancel();
                }
            });
        }

        public Font showDialog(){
            ok = false;
            setVisible( true );
            if( ok )
                return chooser.getChoosedFont();
            else
                return null;
        }

        protected void ok() {
            ok = true;
            dispose();
        }

        protected void cancel() {
            ok = false;
            dispose();
        }
    }
}

