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
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.disable.DisablingStrategyListener;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayerDecorator;
import bibliothek.gui.dock.themes.basic.TabDecorator;
import bibliothek.gui.dock.themes.color.DisplayerColor;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A {@link DockableDisplayer} drawing a border around its content, but leaves
 * the side at which the title lies open.
 * @author Benjamin Sigg
 */
@ColorCodes({ 
    "displayer.border.high.active",
    "displayer.border.high.active.mouse",
    "displayer.border.high.inactive",
    "displayer.border.high.inactive.mouse",
    "displayer.border.high.disabled",
    "displayer.border.low.active",
    "displayer.border.low.active.mouse",
    "displayer.border.low.inactive",
    "displayer.border.low.inactive.mouse",
    "displayer.border.low.disabled" })
public class BubbleDisplayer extends BasicDockableDisplayer {
	/** the size of the border in pixel */
    private int borderSize = 2;
    /** the panel on which the {@link Dockable} of this displayer is shown */
    private JPanel dockable;
    /** the animation changing the colors of this displayer */
    private BubbleColorAnimation animation;

    private DisplayerColor borderHighActive = new BubbleDisplayerColor( "displayer.border.high.active", Color.WHITE );
    private DisplayerColor borderHighActiveMouse = new BubbleDisplayerColor( "displayer.border.high.active.mouse", Color.WHITE );
    private DisplayerColor borderHighInactive = new BubbleDisplayerColor( "displayer.border.high.inactive", Color.DARK_GRAY );
    private DisplayerColor borderHighInactiveMouse = new BubbleDisplayerColor( "displayer.border.high.inactive.mouse", Color.DARK_GRAY );
    private DisplayerColor borderHighDisabled = new BubbleDisplayerColor( "displayer.border.high.disabled", Color.WHITE );
    private DisplayerColor borderLowActive = new BubbleDisplayerColor( "displayer.border.low.active", Color.LIGHT_GRAY );
    private DisplayerColor borderLowActiveMouse = new BubbleDisplayerColor( "displayer.border.low.active.mouse", Color.LIGHT_GRAY );
    private DisplayerColor borderLowInactive = new BubbleDisplayerColor( "displayer.border.low.inactive", Color.BLACK );
    private DisplayerColor borderLowInactiveMouse = new BubbleDisplayerColor( "displayer.border.low.inactive.mouse", Color.BLACK );
    private DisplayerColor borderLowDisabled = new BubbleDisplayerColor( "displayer.border.low.disabled", Color.LIGHT_GRAY );
    
    /** <code>true</code> if the mouse is over the title of this displayer */
    private boolean mouse = false;
    
    /** whether the {@link Dockable} of this displayer is disabled */
    private boolean disabled = false;
    
    /** 
     * a listener to the controller informing this displayer when the focused
     * {@link Dockable} has changed.
     */
    private Listener listener = new Listener();
    
    /** The border of this displayer */
    private DisplayerBorder openBorder;
    
    /** the current {@link DisablingStrategy} */
    private PropertyValue<DisablingStrategy> disablingStrategy = new PropertyValue<DisablingStrategy>( DisablingStrategy.STRATEGY ){
    	@Override
    	protected void valueChanged( DisablingStrategy oldValue, DisablingStrategy newValue ){
    		if( oldValue != null ){
    			oldValue.removeDisablingStrategyListener( disablingStrategyListener );
    		}
    		if( newValue != null ){
    			newValue.addDisablingStrategyListener( disablingStrategyListener );
    			setDisabled( newValue.isDisabled( getDockable() ));
    		}
    		else{
    			setDisabled( false );
    		}
    	}
    };   
    
    /** a listener to {@link #disablingStrategy} */
    private DisablingStrategyListener disablingStrategyListener = new DisablingStrategyListener(){
    	public void changed( DockElement item ){
    		if( getDockable() == item ){
    			setDisabled( disablingStrategy.getValue().isDisabled( item ));
    		}
    	}
    };
    
    /**
     * Creates a new displayer
     * @param station the station for which this displayer will be used
     * @param dockable the {@link Dockable} which will be shown on this displayer, might be <code>null</code>
     * @param title the title to show on this displayer, might be <code>null</code>
     */
    public BubbleDisplayer( DockStation station, Dockable dockable, DockTitle title ){
        super( station, dockable, title );
        
        this.dockable.setOpaque( false );
        openBorder = new DisplayerBorder( this.dockable, "bubble" );
        animation = new BubbleColorAnimation();
        animation.addTask( new Runnable(){
            public void run() {
                pulse();
            }
        });
        
        updateAnimation();
        
        setRespectBorderHint( true );
        setDefaultBorderHint( true );
        setSingleTabShowInnerBorder( true );
        setSingleTabShowOuterBorder( false );
    }
    
    /**
     * Called when the {@link DisablingStrategy} changes the disabled state.
     * @param disabled the new state of the {@link Dockable}
     */
    protected void setDisabled( boolean disabled ){
		if( this.disabled != disabled ){
			this.disabled = disabled;
			updateAnimation();
		}
	}
    
    /**
     * Whether the {@link Dockable} is currently enabled or not
     * @return the disabled state
     */
    public boolean isDisabled(){
		return disabled;
	}
    
    /**
     * Sets the colors to which the animation should run.
     */
    protected void updateAnimation(){
        if( animation != null ){
            DockController controller = getController();
            
            if( isDisabled() ){
            	animation.putColor( "high", borderHighDisabled.value() );
                animation.putColor( "low", borderLowDisabled.value() );
            } 
            else if( controller != null && controller.getFocusedDockable() == getDockable() ){
                if( mouse ){
                    animation.putColor( "high", borderHighActiveMouse.value() );
                    animation.putColor( "low", borderLowActiveMouse.value() );
                }
                else{
                    animation.putColor( "high", borderHighActive.value() );
                    animation.putColor( "low", borderLowActive.value() );
                }
            }
            else{
                if( mouse ){
                    animation.putColor( "high", borderHighInactiveMouse.value() );
                    animation.putColor( "low", borderLowInactiveMouse.value() );
                }
                else{
                    animation.putColor( "high", borderHighInactive.value() );
                    animation.putColor( "low", borderLowInactive.value() );
                }
            }
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
                old.removeDockableFocusListener( listener );
            
            if( controller != null )
                controller.addDockableFocusListener( listener );
            
            super.setController( controller );
        }
        
        disablingStrategy.setProperties( controller );
        
        openBorder.setController( controller );
        borderHighActive.connect( controller );
        borderHighActiveMouse.connect( controller );
        borderHighInactive.connect( controller );
        borderHighInactiveMouse.connect( controller );
        borderHighDisabled.connect( controller );
        borderLowActive.connect( controller );
        borderLowActiveMouse.connect( controller );
        borderLowInactive.connect( controller );
        borderLowInactiveMouse.connect( controller );
        borderLowDisabled.connect( controller );
        animation.kick();
    }
    
    @Override
    protected Component getComponent( Dockable dockable ) {
        ensureDockable();
        return this.dockable;
    }
    
    @Override
    public void setTitle( DockTitle title ) {
        DockTitle old = getTitle();
        if( old != null )
            old.removeMouseInputListener( listener );
        
        super.setTitle( title );
        
        if( title != null )
            title.addMouseInputListener( listener );
        
        mouse = false;
        updateAnimation();
        ensureDockable();
    }
    
    @Override
    public void setDockable( Dockable dockable ) {
        super.setDockable( dockable );
        ensureDockable();
        this.dockable.removeAll();
        if( dockable != null ){
        	this.dockable.add( dockable.getComponent() );
        }
        ensureBorder();
    }
    
    /**
     * Ensures that there is a panel for the {@link Dockable}
     */
    private void ensureDockable(){
        if( dockable == null ){
            dockable = new JPanel( new GridLayout( 1, 1 ));
        }
        
        ensureBorder();
    }
    
    private void ensureBorder(){
        if( dockable != null ){
            Dockable dock = getDockable();
            boolean station = dock != null && dock.asDockStation() != null;
            
            if( getTitle() == null && station )
                setDefaultBorderHint( false );
            else
                setDefaultBorderHint( true );
        }
    }
    
    @Override
    protected void updateBorder() {
        if( isRespectBorderHint() ){
            if( getHints().getShowBorderHint() || getTitle() != null ){
            	openBorder.setBorder( getDefaultBorder() );
            }
            else{
            	openBorder.setBorder( null );
            }
        }
    }
    
    @Override
    protected Border getDefaultBorder() {
        return new OpenBorder();
    }
    
    @Override
    public Insets getDockableInsets() {
        Insets insets = super.getDockableInsets();
        Border border = dockable.getBorder();
        if( border != null ){
            Insets borderInsets = border.getBorderInsets( dockable );
            insets.left += borderInsets.left;
            insets.right += borderInsets.right;
            insets.top += borderInsets.top;
            insets.bottom += borderInsets.bottom;
        }
        return insets;
    }

    @Override
    protected BasicDockableDisplayerDecorator createStackedDecorator(){
    	return createStackedDecorator( BubbleTheme.ACTION_DISTRIBUTOR );
    }
    
    @Override
    protected TabDecorator createTabDecorator(){
	    return new TabDecorator( getStation(), BubbleTheme.ACTION_DISTRIBUTOR );
    }
    
    /**
     * A listener to the controller, reacting when the focused {@link Dockable}
     * has changed.
     * @author Benjamin Sigg
     */
    private class Listener extends MouseInputAdapter implements DockableFocusListener{
        public void dockableFocused( DockableFocusEvent event ) {
            updateAnimation();
        }
        @Override
        public void mouseEntered( MouseEvent e ) {
            mouse = true;
            updateAnimation();
        }
        @Override
        public void mouseExited( MouseEvent e ) {
            mouse = false;
            updateAnimation();
        }
    }
    
    /**
     * A color used on a {@link BubbleDisplayer}.
     * @author Benjamin Sigg
     */
    private class BubbleDisplayerColor extends DisplayerColor{
        /**
         * Creates a new color.
         * @param id the name of the color
         * @param backup a backup in case that no color could be read
         */
        public BubbleDisplayerColor( String id, Color backup ) {
            super( id, BubbleDisplayer.this, backup );
        }

        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateAnimation();
        }
    }
    
    /**
     * The border which will be painted around the {@link BubbleDisplayer#dockable dockable}.
     * @author Benjamin Sigg
     */
    public class OpenBorder implements Border{
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
