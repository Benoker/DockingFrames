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

package bibliothek.extension.gui.dock.theme.flat;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.CombinedTab;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A panel that works like a {@link JTabbedPane}, but the buttons to
 * change between the children are smaller and "flatter" than the
 * buttons of the <code>JTabbedPane</code>.
 * @author Benjamin Sigg
 */
@ColorCodes({
    "stack.tab.border.out.selected", 
    "stack.tab.border.center.selected",
    "stack.tab.border.out.focused", 
    "stack.tab.border.center.focused",
    "stack.tab.border.out", 
    "stack.tab.border.center", 
    "stack.tab.border", 
                
    "stack.tab.background.top.selected", 
    "stack.tab.background.bottom.selected",
    "stack.tab.background.top.focused", 
    "stack.tab.background.bottom.focused",
    "stack.tab.background.top", 
    "stack.tab.background.bottom", 
    "stack.tab.background",
    
    "stack.tab.foreground.selected",
    "stack.tab.foreground.focused",
    "stack.tab.foreground" })
public class FlatTab extends CombinedStackDockComponent<FlatTab.FlatButton>{
    /** the station which uses this component */
    private StackDockStation station;
    
    /**
     * Creates a new {@link FlatTab}
     * @param station the station which uses this component
     */
    public FlatTab( StackDockStation station ){
        this.station = station;
    }
    
    @Override
	protected FlatButton createTab( Dockable dockable ){
		return new FlatButton( dockable );
	}
	
	@Override
	protected void destroy( FlatButton tab ){
	    tab.setController( null );
	}
	
	@Override
    public void setController( DockController controller ){
		super.setController( controller );
		for( int i = 0, n = getTabCount(); i<n; i++ ){
			getTab( i ).setController( controller );
		}
	}
	
	@Override
	public void setSelectedIndex( int index ) {
	    super.setSelectedIndex( index );
	    for( int i = 0, n = getTabCount(); i<n; i++ ){
            getTab( i ).updateForeground();
        }
	}
	
    /**
     * A small button which can be clicked by the user.
     * @author Benjamin Sigg
     */
	protected class FlatButton extends JLabel implements CombinedTab, DockableFocusListener{
		/** the dockable for which this button is shown */
	    private Dockable dockable;
	    
	    /** the current controller */
	    private DockController controller;
	    
        /** The location of this button */
        private int index;
        /** whether {@link #dockable} is currently focused */
        private boolean focused = false;
        
        private TabColor borderSelectedOut;
        private TabColor borderSelectedCenter;
        private TabColor borderFocusedOut;
        private TabColor borderFocusedCenter;
        private TabColor borderOut;
        private TabColor borderCenter;
        private TabColor border;
        private TabColor backgroundSelectedTop;
        private TabColor backgroundSelectedBottom;
        private TabColor backgroundFocusedTop;
        private TabColor backgroundFocusedBottom;
        private TabColor backgroundTop;
        private TabColor backgroundBottom;
        private TabColor background;
        private TabColor foreground;
        private TabColor foregroundSelected;
        private TabColor foregroundFocused;
        
        /**
         * Constructs a new button
         * @param dockable the Dockable for which this tab is displayed
         */
        public FlatButton( Dockable dockable ){
        	this.dockable = dockable;
        	            
            borderSelectedOut    = new FlatTabColor( "stack.tab.border.out.selected", dockable );
            borderSelectedCenter = new FlatTabColor( "stack.tab.border.center.selected", dockable );
            borderFocusedOut    = new FlatTabColor( "stack.tab.border.out.focused", dockable );
            borderFocusedCenter = new FlatTabColor( "stack.tab.border.center.focused", dockable );
            borderOut            = new FlatTabColor( "stack.tab.border.out", dockable );
            borderCenter         = new FlatTabColor( "stack.tab.border.center", dockable );
            border               = new FlatTabColor( "stack.tab.border", dockable );
            
            backgroundSelectedTop    = new FlatTabColor( "stack.tab.background.top.selected", dockable );
            backgroundSelectedBottom = new FlatTabColor( "stack.tab.background.bottom.selected", dockable );
            backgroundFocusedTop    = new FlatTabColor( "stack.tab.background.top.focused", dockable );
            backgroundFocusedBottom = new FlatTabColor( "stack.tab.background.bottom.focused", dockable );
            backgroundTop            = new FlatTabColor( "stack.tab.background.top", dockable );
            backgroundBottom         = new FlatTabColor( "stack.tab.background.bottom", dockable );
            background               = new FlatTabColor( "stack.tab.background", dockable );
            
            foreground = new FlatTabColor( "stack.tab.foreground", dockable ){
                @Override
                protected void changed( Color oldColor, Color newColor ) {
                    if( !isSelected() )
                        setForeground( newColor );
                }
            };
            foregroundSelected = new FlatTabColor( "stack.tab.foreground.selected", dockable ){
                @Override
                protected void changed( Color oldColor, Color newColor ) {
                    if( isSelected() && !focused ){
                        setForeground( newColor );
                    }
                }
            };
            foregroundFocused = new FlatTabColor( "stack.tab.foreground.focused", dockable ){
                @Override
                protected void changed( Color oldColor, Color newColor ) {
                    if( focused ){
                        setForeground( newColor );
                    }
                }
            };
            
            setController( getController() );
            setOpaque( false );
            setFocusable( true );
            
            addMouseListener( new MouseAdapter(){
                @Override
                public void mousePressed( MouseEvent e ){
                    setSelectedIndex( index );
                    repaint();
                }
            });
            
            setBorder( new Border(){
                public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){
                    Graphics2D g2 = (Graphics2D)g;
                    Paint oldPaint = g2.getPaint();
                    
                    Color out = null;
                    Color center = null;

                    if( focused ){
                        out = borderFocusedOut.value();
                        center = borderFocusedCenter.value();
                    }
                    if( isSelected() ){
                        if( out == null )
                            out = borderSelectedOut.value();
                        if( center == null )
                            center = borderSelectedCenter.value();
                    }
                    if( out == null )
                        out = borderOut.value();
                    if( center == null )
                        center = borderCenter.value();
                    
                    if( out == null || center == null ){
                        Color background = border.value();
                        if( background == null )
                            background = FlatButton.this.background.value();
                        
                        if( background == null )
                            background = getBackground();
                        
                        if( out == null )
                            out = background;
                        
                        if( center == null ){
                            if( isSelected() ){
                                center = background.brighter();
                            }
                            else{
                                center = background.darker();
                            }
                        }
                    }
                    
                    g2.setPaint( new GradientPaint( x, y, out, x, y+h/2, center ));
                    g.drawLine( x, y, x, y+h/2 );
                    g.drawLine( x+w-1, y, x+w-1, y+h/2 );
                    
                    g2.setPaint( new GradientPaint( x, y+h, out, x, y+h/2, center ));
                    g.drawLine( x, y+h, x, y+h/2 );
                    g.drawLine( x+w-1, y+h, x+w-1, y+h/2 );
                    
                    g2.setPaint( oldPaint );
                }
                public Insets getBorderInsets(Component c){
                    return new Insets( 0, 1, 0, 1 );
                }
                public boolean isBorderOpaque(){
                    return false;
                }
            });
        }
        
        public void updateForeground(){
            if( isSelected() )
                setForeground( foregroundSelected.value() );
            else
                setForeground( foreground.value() );
        }
        
        public void setController( DockController controller ){
        	if( this.controller != null )
                this.controller.removeDockableFocusListener( this );
            this.controller = controller;
            if( controller != null )
                controller.addDockableFocusListener( this );
        	
            borderSelectedOut.connect( controller );
            borderSelectedCenter.connect( controller );
            borderFocusedOut.connect( controller );
            borderFocusedCenter.connect( controller );
            borderOut.connect( controller );
            borderCenter.connect( controller );
            border.connect( controller );
            
            backgroundSelectedTop.connect( controller );
            backgroundSelectedBottom.connect( controller );
            backgroundFocusedTop.connect( controller );
            backgroundFocusedBottom.connect( controller );
            backgroundTop.connect( controller );
            backgroundBottom.connect( controller );
            background.connect( controller );
            
            foregroundSelected.connect( controller );
            foregroundFocused.connect( controller );
            foreground.connect( controller );
        }
        
        public Point getPopupLocation( Point click, boolean popupTrigger ) {
            if( popupTrigger )
                return click;
            
            return null;
        }
        
        public void dockableFocused( DockableFocusEvent event ) {
            focused = this.dockable == event.getNewFocusOwner();
            repaint();
        }
        
        public JComponent getComponent(){
        	return this;
        }
        
        public DockElement getElement() {
            return dockable;
        }
        
        public void addMouseInputListener( MouseInputListener listener ) {
            addMouseListener( listener );
            addMouseMotionListener( listener );
        }
        
        public void removeMouseInputListener( MouseInputListener listener ) {
            removeMouseListener( listener );
            removeMouseMotionListener( listener );
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension preferred = super.getPreferredSize();
            if( preferred.width < 10 || preferred.height < 10 ){
                preferred = new Dimension( preferred );
                preferred.width = Math.max( preferred.width, 10 );
                preferred.height = Math.max( preferred.height, 10 );
            }
            return preferred;
        }
        
        @Override
        public Dimension getMinimumSize() {
            Dimension min = super.getMinimumSize();
            if( min.width < 10 || min.height < 10 ){
                min = new Dimension( min );
                min.width = Math.max( min.width, 10 );
                min.height = Math.max( min.height, 10 );
            }
            return min;
        }
       
        public void setTooltip( String tooltip ) {
            setToolTipText( tooltip );
        }
        
        /**
         * Sets the location of this button. The buttons knows
         * through the {@link FlatTab#getSelectedIndex() selectedIndex}-property
         * whether it is selected or not.
         * @param index the location
         */
        public void setIndex( int index ) {
            this.index = index;
            repaint();
        }
        
        /**
         * Determines whether this button is selected or not.
         * @return <code>true</code> if the button is selected
         */
        public boolean isSelected() {
            return index == getSelectedIndex();
        }
        
        @Override
        public void paintComponent( Graphics g ){
            Graphics2D g2 = (Graphics2D)g;
            Paint oldPaint = g2.getPaint();
            
            int w = getWidth();
            int h = getHeight();
            
            Color top = null;
            Color bottom = null;
            
            if( focused ){
                top = backgroundFocusedTop.value();
                bottom = backgroundFocusedBottom.value();
            }
            if( isSelected() ){
                if( top == null )
                    top = backgroundSelectedTop.value();
                if( bottom == null )
                    bottom = backgroundSelectedBottom.value();
            }
            if( top == null )
                top = backgroundTop.value();
            if( bottom == null )
                bottom = backgroundBottom.value();
            
            if( top == null || bottom == null ){
                Color background = FlatButton.this.background.value();
                if( background == null )
                    background = getBackground();
                
                if( bottom == null )
                    bottom = background;
                
                if( top == null ){
                    if( isSelected() ){
                        top = background.brighter();
                    }
                    else{
                        top = background;
                    }
                }
            }

            if( top.equals( bottom ))
                g.setColor( top );
            else{
                GradientPaint gradient = new GradientPaint( 0, 0, top,
                        0, h, bottom );
                g2.setPaint( gradient );
            }
            
            g.fillRect( 0, 0, w, h );
            
            g2.setPaint( oldPaint );
            
            super.paintComponent( g );
        }
        
        /**
         * A color of this tab.
         * @author Benjamin Sigg
         */
        private class FlatTabColor extends TabColor{
            /**
             * Creates a new color.
             * @param id the id of the color
             * @param dockable the element for which the color is used
             */
            public FlatTabColor( String id, Dockable dockable ){
                super( id, station, dockable, null );
            }
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                repaint();
            }
        }
    }
}
