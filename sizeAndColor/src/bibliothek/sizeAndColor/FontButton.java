package bibliothek.sizeAndColor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.FontMap;
import bibliothek.gui.dock.util.font.ConstantFontModifier;

/**
 * This button can be used to change a font in a {@link FontMap}.
 * @author Benjamin Sigg
 */
public class FontButton extends JPanel{
    private FontMap map;
    private String key;
    
    private Font font;
    
    private JCheckBox selected;
    private JButton button;

    /**
     * Creates a new button.
     * @param map the map which will be changed by this button
     * @param key the key of the entry
     */
    public FontButton( FontMap map, String key ){
        this.map = map;
        this.key = key;
        
        selected = new JCheckBox( key );
        button = new JButton( "F" );
        
        setLayout( new BorderLayout() );
        add( selected, BorderLayout.CENTER );
        add( button, BorderLayout.EAST );
        
        selected.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                transmit();
            }
        });
        button.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                changeFont();
            }
        });
    }
    
    /**
     * Opens a {@link JColorChooser} and lets the user change the color.
     */
    private void changeFont(){
        Font font = FontChooser.showDialog( this, key, this.font );
        if( font != null ){
            this.font = font;
            button.setFont( font );
            transmit();
        }
    }
    
    /**
     * Transmits the current setting to the {@link ColorMap} of this button.
     */
    private void transmit(){
        if( selected.isSelected() ){
            Font font = this.font;
            if( font == null )
                font = button.getFont();
            
            map.setFont( key, new ConstantFontModifier( font ) );
        }
        else{
            map.setFont( key, null );
        }
    }    
}
