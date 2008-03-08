/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.EmptyMultipleCDockableFactory;

/**
 * A frame contains some buttons that change some properties in order to 
 * change size and colors.
 * @author Benjamin Sigg
 */
public class Frame extends DefaultMultipleCDockable {
    /** the preferred width */
    private JSpinner width = new JSpinner( new SpinnerNumberModel( 200, 50, 500, 1 ));
    /** the preferred height */
    private JSpinner height = new JSpinner( new SpinnerNumberModel( 200, 50, 500, 1 ));
    /** whether the size is locked */
    private JCheckBox locked = new JCheckBox( "Size locked during resize" );
    
    /** a factory that can create new frames */
    public static final EmptyMultipleCDockableFactory<Frame> FACTORY = new EmptyMultipleCDockableFactory<Frame>(){
        @Override
        public Frame createDockable() {
            return new Frame();
        }
    };
    
    /**
     * Creates a new Frame
     */
    public Frame(){
        super( FACTORY );
        setTitleText( "Frame" );
        
        JPanel sizes = new JPanel( new GridBagLayout() );
        sizes.setBorder( BorderFactory.createTitledBorder( "Size" ) );
        
        sizes.add( locked, new GridBagConstraints( 0, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        sizes.add( new JLabel( "Width: " ), new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        sizes.add( new JLabel( "Height: " ), new GridBagConstraints( 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        
        sizes.add( width, new GridBagConstraints( 1, 1, 1, 1, 100.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        sizes.add( height, new GridBagConstraints( 1, 2, 1, 1, 100.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        
        JPanel buttons = new JPanel( new GridLayout( 1, 2 ));
        JButton set = new JButton( "Request" );
        set.setToolTipText( "States the request, but does not yet process it." );
        JButton process = new JButton( "Process" );
        process.setToolTipText( "States the request and processes all pending requests." );
        buttons.add( set );
        buttons.add( process );
        sizes.add( buttons, new GridBagConstraints( 0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.LAST_LINE_END,
                GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 )); 
        
        
        JPanel colors = new JPanel( new GridLayout( 10, 1 ));
        colors.setBorder( BorderFactory.createTitledBorder( "Color" ) );
        
        ColorMap map = getColors();
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TAB_BACKGROUND, Color.WHITE ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TAB_BACKGROUND_SELECTED, Color.WHITE ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TAB_BACKGROUND_FOCUSED, Color.WHITE ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TAB_FOREGROUND, Color.BLACK ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TAB_FOREGROUND_SELECTED, Color.BLACK ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TAB_FOREGROUND_FOCUSED, Color.BLACK ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TITLE_BACKGROUND, Color.WHITE ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TITLE_BACKGROUND_FOCUSED, Color.WHITE ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TITLE_FOREGROUND, Color.BLACK ));
        colors.add( new ColorButton( map, ColorMap.COLOR_KEY_TITLE_FOREGROUND_FOCUSED, Color.BLACK ));
        
        JPanel all = new JPanel( new GridBagLayout() );
        all.add( sizes, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        all.add( colors, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        
        getContentPane().add( new JScrollPane( all ));
        getContentPane().addComponentListener( new ComponentAdapter(){
            @Override
            public void componentResized( ComponentEvent e ) {
                setTitleText( getContentPane().getWidth() + " x " + getContentPane().getHeight() );
            }
        });
        locked.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                setResizeLocked( locked.isSelected() );
            }
        });
        set.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                setResizeRequest( new Dimension( ((Number)width.getValue()).intValue(), ((Number)height.getValue()).intValue() ), false );
            }
        });
        process.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                setResizeRequest( new Dimension( ((Number)width.getValue()).intValue(), ((Number)height.getValue()).intValue() ), true );
            }
        });
    }
}
