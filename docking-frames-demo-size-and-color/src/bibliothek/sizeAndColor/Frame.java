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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.EmptyMultipleCDockableFactory;
import bibliothek.gui.dock.common.FontMap;
import bibliothek.util.Path;

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
    /** whether the width is locked */
    private JCheckBox lockedWidth = new JCheckBox( "Width locked during resize" );
    /** whether the height is locked */
    private JCheckBox lockedHeight = new JCheckBox( "Height locked during resize" );
    /** whether titles are shown or not */
    private JCheckBox showTitle;
    /** whether single tabs should be shown */
    private JCheckBox showSingleTab;
    /** whether other dockables are allowed to get the focus */
    private JCheckBox preventFocusLost = new JCheckBox( "Prevent focus lost", false );
    
    /** placeholder for this frame */
    private Path placeholder;
    
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
        
        JPanel various = new JPanel( new GridLayout( 3, 1 ) );
        various.setBorder( BorderFactory.createTitledBorder( "Various" ) );
        showTitle = new JCheckBox( "Show title", isTitleShown() );
        showSingleTab = new JCheckBox( "Show single tab", isSingleTabShown() );
        various.add( showTitle );
        various.add( showSingleTab );
        various.add( preventFocusLost );
        
        JPanel sizes = new JPanel( new GridBagLayout() );
        sizes.setBorder( BorderFactory.createTitledBorder( "Size" ) );
        
        sizes.add( lockedWidth, new GridBagConstraints( 0, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        sizes.add( lockedHeight, new GridBagConstraints( 0, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        sizes.add( new JLabel( "Width: " ), new GridBagConstraints( 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        sizes.add( new JLabel( "Height: " ), new GridBagConstraints( 0, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        
        sizes.add( width, new GridBagConstraints( 1, 2, 1, 1, 100.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        sizes.add( height, new GridBagConstraints( 1, 3, 1, 1, 100.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ));
        
        JPanel buttons = new JPanel( new GridLayout( 1, 2 ));
        JButton set = new JButton( "Request" );
        set.setToolTipText( "States the request, but does not yet process it." );
        JButton process = new JButton( "Process" );
        process.setToolTipText( "States the request and processes all pending requests." );
        buttons.add( set );
        buttons.add( process );
        sizes.add( buttons, new GridBagConstraints( 0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.LAST_LINE_END,
                GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 )); 
        
        
        JPanel properties = new JPanel( new GridLayout( 23, 1 ));
        properties.setBorder( BorderFactory.createTitledBorder( "Color" ) );
        
        ColorMap colors = getColors();
        FontMap fonts = getFonts();
        
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TAB_BACKGROUND, Color.WHITE ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TAB_BACKGROUND_SELECTED, Color.WHITE ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TAB_BACKGROUND_FOCUSED, Color.WHITE ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TAB_FOREGROUND, Color.BLACK ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TAB_FOREGROUND_SELECTED, Color.BLACK ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TAB_FOREGROUND_FOCUSED, Color.BLACK ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TITLE_BACKGROUND, Color.WHITE ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TITLE_BACKGROUND_FOCUSED, Color.WHITE ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TITLE_FOREGROUND, Color.BLACK ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_TITLE_FOREGROUND_FOCUSED, Color.BLACK ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_MINIMIZED_BUTTON_BACKGROUND, Color.WHITE ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_MINIMIZED_BUTTON_BACKGROUND_FOCUSED, Color.WHITE ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_MINIMIZED_BUTTON_BACKGROUND_SELECTED, Color.WHITE ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_MINIMIZED_BUTTON_FOREGROUND, Color.BLACK ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_MINIMIZED_BUTTON_FOREGROUND_FOCUSED, Color.BLACK ));
        properties.add( new ColorButton( colors, ColorMap.COLOR_KEY_MINIMIZED_BUTTON_FOREGROUND_SELECTED, Color.BLACK ));
        properties.add( new FontButton( fonts, FontMap.FONT_KEY_TITLE ));
        properties.add( new FontButton( fonts, FontMap.FONT_KEY_TITLE_FOCUSED ));
        properties.add( new FontButton( fonts, FontMap.FONT_KEY_MINIMIZED_BUTTON ));
        properties.add( new FontButton( fonts, FontMap.FONT_KEY_MINIMIZED_BUTTON_FOCUSED ));
        properties.add( new FontButton( fonts, FontMap.FONT_KEY_TAB ));
        properties.add( new FontButton( fonts, FontMap.FONT_KEY_TAB_SELECTED ));
        properties.add( new FontButton( fonts, FontMap.FONT_KEY_TAB_FOCUSED ));
        
        JPanel all = new JPanel( new GridBagLayout() );
        all.add( various, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        all.add( sizes, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        all.add( properties, new GridBagConstraints( 0, 2, 1, 1, 1.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        
        getContentPane().add( new JScrollPane( all ));
        getContentPane().addComponentListener( new ComponentAdapter(){
            @Override
            public void componentResized( ComponentEvent e ) {
                setTitleText( getContentPane().getWidth() + " x " + getContentPane().getHeight() );
            }
        });
        showTitle.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                setTitleShown( showTitle.isSelected() );
            }
        });
        showSingleTab.addActionListener( new ActionListener(){
        	public void actionPerformed( ActionEvent e ){
	        	setSingleTabShown( showSingleTab.isSelected() );	
        	}
        });
        lockedWidth.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                setResizeLockedHorizontally( lockedWidth.isSelected() );
            }
        });
        lockedHeight.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                setResizeLockedVertically( lockedHeight.isSelected() );
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
    
    public boolean isFocusLostAllowed(){
    	return !preventFocusLost.isSelected();
    }
    
    public void setPlaceholder( Path placeholder ){
		this.placeholder = placeholder;
	}
    
    public Path getPlaceholder(){
		return placeholder;
	}
}
