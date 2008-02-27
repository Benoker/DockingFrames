/**
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
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
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
@ColorCodes({"stack.tab.border.out.selected", 
    "stack.tab.border.center.selected", 
    "stack.tab.border.out", 
    "stack.tab.border.center", 
    "stack.tab.border", 
                
    "stack.tab.background.top.selected", 
    "stack.tab.background.bottom.selected", 
    "stack.tab.background.top", 
    "stack.tab.background.bottom", 
    "stack.tab.background",
    
    "stack.tab.foreground",
    "stack.tab.foreground.selected" })
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
		// nothing to do
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
	protected class FlatButton extends JLabel implements CombinedTab{
		/** the currently used remote to do drag&drop operations */
	    private RemoteRelocator relocator;
		
	    /** the dockable for which this button is shown */
	    private Dockable dockable;
	    
        /** The location of this button */
        private int index;
       
        private TabColor borderSelectedOut;
        private TabColor borderSelectedCenter;
        private TabColor borderOut;
        private TabColor borderCenter;
        private TabColor border;
        private TabColor backgroundSelectedTop;
        private TabColor backgroundSelectedBottom;
        private TabColor backgroundTop;
        private TabColor backgroundBottom;
        private TabColor background;
        private TabColor foreground;
        private TabColor foregroundSelected;
        
        /**
         * Constructs a new button
         * @param dockable the Dockable for which this tab is displayed
         */
        public FlatButton( Dockable dockable ){
        	this.dockable = dockable;
        	            
            borderSelectedOut    = new FlatTabColor( "stack.tab.border.out.selected", dockable );
            borderSelectedCenter = new FlatTabColor( "stack.tab.border.center.selected", dockable );
            borderOut            = new FlatTabColor( "stack.tab.border.out", dockable );
            borderCenter         = new FlatTabColor( "stack.tab.border.center", dockable );
            border               = new FlatTabColor( "stack.tab.border", dockable );
            
            backgroundSelectedTop    = new FlatTabColor( "stack.tab.background.top.selected", dockable );
            backgroundSelectedBottom = new FlatTabColor( "stack.tab.background.bottom.selected", dockable );
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
                    if( isSelected() ){
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
                    
                    if( relocator != null && !e.isConsumed()){
                    	Point mouse = e.getPoint();
                    	SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
                    	Reaction reaction = relocator.init( mouse.x, mouse.y, 0, 0, e.getModifiersEx() );
                    	switch( reaction ){
                    		case BREAK_CONSUMED:
                    		case CONTINUE_CONSUMED:
                    			e.consume();
                    			break;
                    	}
                    }
                }
                
                @Override
                public void mouseReleased( MouseEvent e ){
                	if( relocator != null && !e.isConsumed()){
                    	Point mouse = e.getPoint();
                    	SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
                    	Reaction reaction =relocator.drop( mouse.x, mouse.y, e.getModifiersEx() );
                    	switch( reaction ){
                			case BREAK_CONSUMED:
                			case CONTINUE_CONSUMED:
                				e.consume();
                				break;
                    	}
                    }
                }
            });
            
            addMouseMotionListener( new MouseMotionAdapter(){
            	@Override
            	public void mouseDragged( MouseEvent e ){
            		if( relocator != null && !e.isConsumed()){
                    	Point mouse = e.getPoint();
                    	SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
                    	Reaction reaction =relocator.drag( mouse.x, mouse.y, e.getModifiersEx() );
                    	switch( reaction ){
                			case BREAK_CONSUMED:
                			case CONTINUE_CONSUMED:
                				e.consume();
                				break;
                    	}
                    }
            	}
            });
            
            setBorder( new Border(){
                public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){
                    Graphics2D g2 = (Graphics2D)g;
                    Paint oldPaint = g2.getPaint();
                    
                    Color out;
                    Color center;

                    if( isSelected() ){
                        out = borderSelectedOut.color();
                        center = borderSelectedCenter.color();
                    }
                    else{
                        out = borderOut.color();
                        center = borderCenter.color();
                    }
                    
                    if( out == null || center == null ){
                        Color background = border.color();
                        if( background == null )
                            background = FlatButton.this.background.color();
                        
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
                setForeground( foregroundSelected.color() );
            else
                setForeground( foreground.color() );
        }
        
        public void setController( DockController controller ){
        	if( controller == null )
        		relocator = null;
        	else
        		relocator = controller.getRelocator().createRemote( dockable );
        	
            
            borderSelectedOut.connect( controller );
            borderSelectedCenter.connect( controller );
            borderOut.connect( controller );
            borderCenter.connect( controller );
            border.connect( controller );
            
            backgroundSelectedTop.connect( controller );
            backgroundSelectedBottom.connect( controller );
            backgroundTop.connect( controller );
            backgroundBottom.connect( controller );
            background.connect( controller );
            
            foreground.connect( controller );
            foregroundSelected.connect( controller );
        }
        
        public JComponent getComponent(){
        	return this;
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
            
            Color top;
            Color bottom;
            
            if( isSelected() ){
                top = backgroundSelectedTop.color();
                bottom = backgroundSelectedBottom.color();
            }
            else{
                top = backgroundTop.color();
                bottom = backgroundBottom.color();
            }
            
            if( top == null || bottom == null ){
                Color background = FlatButton.this.background.color();
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
                super( id, TabColor.class, station, dockable, null );
            }
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                repaint();
            }
        }
    }
}
