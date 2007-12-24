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
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link DockableDisplayer} drawing a border around its content, but leaves
 * the side at which the title lies open.
 * @author Benjamin Sigg
 */
public class BubbleDisplayer extends BasicDockableDisplayer {
	/** the size of the border in pixel */
    private int borderSize = 2;
    /** the panel on which the {@link Dockable} of this displayer is shown */
    private JPanel dockable;
    /** the animation changing the colors of this displayer */
    private BubbleColorAnimation animation;
    
    /** 
     * a listener to the controller informing this displayer when the focused
     * {@link Dockable} has changed.
     */
    private Listener listener = new Listener();
    
    /**
     * Creates a new displayer
     * @param theme the theme to read colors from
     * @param dockable the {@link Dockable} which will be shown on this displayer, might be <code>null</code>
     * @param title the title to show on this displayer, might be <code>null</code>
     */
    public BubbleDisplayer( BubbleTheme theme, Dockable dockable, DockTitle title ){
        super( dockable, title );
        
        animation = new BubbleColorAnimation( theme );
        animation.putColor( "high", "border.high.inactive" );
        animation.putColor( "low", "border.low.inactive" );
        animation.addTask( new Runnable(){
            public void run() {
                pulse();
            }
        });
        
        setBorder( null );
    }
    
    /**
     * Sets the colors to which the animation should run.
     */
    protected void updateAnimation(){
        DockController controller = getController();
        if( controller != null && controller.getFocusedDockable() == getDockable() ){
            animation.putColor( "high", "border.high.active" );
            animation.putColor( "low", "border.low.active" );
        }
        else{
            animation.putColor( "high", "border.high.inactive" );
            animation.putColor( "low", "border.low.inactive" );
        }
    }
    
    /**
     * Called by the animation when the colors changed and the displayer should
     * be repainted.
     */
    protected void pulse(){
        dockable.repaint();
    }
    
    @Override
    public void setController( DockController controller ) {
        DockController old = getController();
        if( old != controller ){
            if( old != null )
                old.removeDockControllerListener( listener );
            
            if( controller != null )
                controller.addDockControllerListener( listener );
            
            super.setController( controller );
        }
    }
    
    @Override
    protected void addDockable( Component component ) {
        ensureDockable();
        dockable.add( component );
    }
    
    @Override
    protected void removeDockable( Component component ) {
        ensureDockable();
        dockable.remove( component );
    }
    
    @Override
    protected Component getComponent( Dockable dockable ) {
        ensureDockable();
        return this.dockable;
    }
    
    /**
     * Ensures that there is a panel for the {@link Dockable}
     */
    private void ensureDockable(){
        if( dockable == null ){
            dockable = new JPanel( new GridLayout( 1, 1 ));
            dockable.setBorder(  new OpenBorder() );
            add( dockable );
        }
    }
    
    /**
     * A listener to the controller, reacting when the focused {@link Dockable}
     * has changed.
     * @author Benjamin Sigg
     */
    private class Listener extends DockControllerAdapter{
        @Override
        public void dockableFocused( DockController controller, Dockable dockable ) {
            updateAnimation();
        }
    }
    
    /**
     * The border which will be painted around the {@link BubbleDisplayer#dockable dockable}.
     * @author Benjamin Sigg
     */
    private class OpenBorder implements Border{
        public Insets getBorderInsets( Component c ) {
            if( getTitle() == null )
                return new Insets( borderSize, borderSize, borderSize, borderSize );
            else{
                switch( getTitleLocation() ){
                    case BOTTOM: return new Insets( borderSize, borderSize, 0, borderSize );
                    case LEFT: return new Insets( borderSize, 0, borderSize, borderSize );
                    case RIGHT: return new Insets( borderSize, borderSize, borderSize, 0 );
                    case TOP: return new Insets( 0, borderSize, borderSize, borderSize );
                }
            }
            
            // error
            return new Insets( 0, 0, 0, 0 );
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
            Color high = animation.getColor( "high" );
            Color low = animation.getColor( "low" );
            
            boolean noTitle = getTitle() == null;
            boolean top = noTitle || getTitleLocation() != Location.TOP;
            boolean left = noTitle || getTitleLocation() != Location.LEFT;
            boolean right = noTitle || getTitleLocation() != Location.RIGHT;
            boolean bottom = noTitle || getTitleLocation() != Location.BOTTOM;
            
            int highSize = borderSize / 2;
            int lowSize = borderSize - highSize;
            
            if( top ){
                g.setColor( high );
                g.fillRect( x, y, width, highSize );
                g.setColor( low );
                g.fillRect( x, y+highSize, width, lowSize );
            }
            
            if( left ){
                g.setColor( high );
                g.fillRect( x, y, highSize, height );
                g.setColor( low );
                if( top )
                    g.fillRect( x+highSize, y+highSize, lowSize, height-highSize );
                else
                    g.fillRect( x+highSize, y, lowSize, height );
            }
            
            if( right ){
                g.setColor( high );
                g.fillRect( x+width-borderSize, y, highSize, height );
                g.setColor( low );
                if( top )
                    g.fillRect( x+width-lowSize, y+highSize, lowSize, height-highSize );
                else
                    g.fillRect( x+width-lowSize, y, lowSize, height );
            }
            
            if( bottom ){
                g.setColor( high );
                if( right )
                	g.fillRect( x, y+height-borderSize, width-borderSize, highSize );
                else
                	g.fillRect( x, y+height-borderSize, width, highSize );
                g.setColor( low );
                g.fillRect( x, y+height-lowSize, width, lowSize );
            }
        }
    }
}
