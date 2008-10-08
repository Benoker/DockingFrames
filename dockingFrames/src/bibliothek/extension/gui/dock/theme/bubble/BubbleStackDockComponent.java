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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.CombinedTab;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.themes.font.TabFont;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.gui.dock.util.swing.DLabel;

/**
 * A {@link bibliothek.gui.dock.station.stack.StackDockComponent StackDockComponent} 
 * used by a {@link BubbleTheme}. This component can animate its tabs.
 * @author Benjamin Sigg
 */
@ColorCodes({ 
    "stack.tab.background.top.mouse",
    "stack.tab.background.bottom.mouse",
    "stack.tab.border.mouse",
    "stack.tab.foreground.mouse",
    
    "stack.tab.background.top",
    "stack.tab.background.bottom",
    "stack.tab.border",
    "stack.tab.foreground",
    
    "stack.tab.background.top.selected.mouse",
    "stack.tab.background.bottom.selected.mouse",
    "stack.tab.border.selected.mouse",
    "stack.tab.foreground.selected.mouse",
    
    "stack.tab.background.top.selected",
    "stack.tab.background.bottom.selected",
    "stack.tab.border.selected",
    "stack.tab.foreground.selected",
    
    "stack.tab.background.top.focused.mouse",
    "stack.tab.background.bottom.focused.mouse",
    "stack.tab.border.focused.mouse",
    "stack.tab.foreground.focused.mouse",
    
    "stack.tab.background.top.focused",
    "stack.tab.background.bottom.focused",
    "stack.tab.border.focused",
    "stack.tab.foreground.focused"
})
public class BubbleStackDockComponent extends CombinedStackDockComponent<BubbleStackDockComponent.Tab> {
	/** the size of the arc of the round tabs */
	private int arc = 6;
	/** the size of the border of the tabs */
	private int borderSize = 3;
	/** the free space around text and icon of the tabs */
	private Insets insets = new Insets( borderSize, borderSize, 0, borderSize );
	/** the station for which this component is used */
	private StackDockStation station;
	
	/**
	 * Creates a new component.
	 * @param station the station on which this component is used
	 */
	public BubbleStackDockComponent( StackDockStation station ){
		this.station = station;
	}
	
	@Override
	protected Tab createTab( Dockable dockable ){
		Tab tab = new Tab( dockable );
		addChangeListener( tab );
		return tab;
	}

	@Override
	protected void destroy( Tab tab ){
		removeChangeListener( tab );
		tab.setController( null );
        tab.animation.stop();
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		for( int i = 0, n = getTabCount(); i<n; i++ )
			getTab( i ).setController( controller );
	}

	public boolean hasBorder() {
	    return true;
	}
	
	/**
	 * Some color needed on a {@link Tab}.
	 * @author Benjamin Sigg
	 */
	protected class BubbleTabColor extends TabColor{
	    private Tab tab;
	    private int state;
	    private String animationId;
	    private BubbleColorAnimation animation;
	    
	    public BubbleTabColor( Tab tab, int state, String id, String animationId, BubbleColorAnimation animation, Dockable dockable, Color backup ){
	        super( id, station, dockable, backup);
	        this.tab = tab;
	        this.state = state;
	        this.animationId = animationId;
	        this.animation = animation;
	    }
	    
	    /**
	     * Transmits the color of this {@link TabColor} if the state is
	     * correct.
	     */
	    public void transmit(){
	        if( tab.getState() == state ){
                animation.putColor( animationId, value() );
            }
	    }
	    
	    @Override
	    protected void changed( Color oldColor, Color newColor ) {
	        if( tab.getState() == state ){
	            animation.putColor( animationId, newColor );
	        }
	    }
	}
	
	/**
	 * Some font needed on a {@link Tab}
	 * @author Benjamin Sigg
	 */
	protected class BubbleTabFont extends TabFont{
	    private Tab tab;
	    
	    /**
	     * Creates a new font
	     * @param tab the tab for which this font is used
	     * @param id the name of the font
	     * @param dockable the element shown on the tab
	     */
	    public BubbleTabFont( Tab tab, String id, Dockable dockable ){
	        super( id, station, dockable );
	        this.tab = tab;
	    }
	    
	    @Override
	    protected void changed( FontModifier oldValue, FontModifier newValue ) {
	        tab.updateFonts();
	    }
	}
	
	/**
	 * A tab of the StackDockComponent
	 * @author Benjamin Sigg
	 */
	protected class Tab extends JPanel implements CombinedTab, ChangeListener, Runnable, DockableFocusListener{
		/** the location of this tab */
		private int index = 0;
		/** a label showing text and icon for this tab */
		private DLabel label = new DLabel();
		/** an animation used when the mouse enters or leaves this tab */
		private BubbleColorAnimation animation;
		/** whether the mouse is inside this tab or not */
        private boolean mouse = false;
        /** whether this tab is currently focused or not */
        private boolean focused = false;
        /** the Dockable for which this tab is used */
        private Dockable dockable;
        /** the currently observed controller */
        private DockController controller;
        
        private BubbleTabColor topMouse;
        private BubbleTabColor bottomMouse;
        private BubbleTabColor borderMouse;
        private BubbleTabColor textMouse;
        
        private BubbleTabColor top;
        private BubbleTabColor bottom;
        private BubbleTabColor border;
        private BubbleTabColor text;
        
        private BubbleTabColor topSelectedMouse;
        private BubbleTabColor bottomSelectedMouse;
        private BubbleTabColor borderSelectedMouse;
        private BubbleTabColor textSelectedMouse;
        
        private BubbleTabColor topSelected;
        private BubbleTabColor bottomSelected;
        private BubbleTabColor borderSelected;
        private BubbleTabColor textSelected;
     
        private BubbleTabColor topFocusedMouse;
        private BubbleTabColor bottomFocusedMouse;
        private BubbleTabColor borderFocusedMouse;
        private BubbleTabColor textFocusedMouse;
        
        private BubbleTabColor topFocused;
        private BubbleTabColor bottomFocused;
        private BubbleTabColor borderFocused;
        private BubbleTabColor textFocused;
        
        private BubbleTabColor[] colors;
        
        private BubbleTabFont fontFocused;
        private BubbleTabFont fontSelected;
        private BubbleTabFont fontUnselected;
        
        private static final int STATE_SELECTED = 1;
        private static final int STATE_FOCUSED = 2 | STATE_SELECTED;
        private static final int STATE_MOUSE = 4;
        
        private int state = 0;
        
        /**
         * Creates a new tab
         * @param dockable the element whose title will be shown on this tab
         */
		public Tab( Dockable dockable ){
			this.dockable = dockable;
			
            animation = new BubbleColorAnimation();
            animation.addTask( this );
            
            topMouse     = new BubbleTabColor( this, STATE_MOUSE, "stack.tab.background.top.mouse", "top", animation, dockable, Color.RED.brighter() );
            bottomMouse  = new BubbleTabColor( this, STATE_MOUSE, "stack.tab.background.bottom.mouse", "bottom", animation, dockable, Color.RED.darker() );
            borderMouse  = new BubbleTabColor( this, STATE_MOUSE, "stack.tab.border.mouse", "border", animation, dockable, Color.RED.darker().darker() );
            textMouse    = new BubbleTabColor( this, STATE_MOUSE, "stack.tab.foreground.mouse", "text", animation, dockable, Color.BLACK );
            
            top     = new BubbleTabColor( this, 0, "stack.tab.background.top", "top", animation, dockable, Color.RED.brighter() );
            bottom  = new BubbleTabColor( this, 0, "stack.tab.background.bottom", "bottom", animation, dockable, Color.RED.darker() );
            border  = new BubbleTabColor( this, 0, "stack.tab.border", "border", animation, dockable, Color.RED.darker().darker() );
            text    = new BubbleTabColor( this, 0, "stack.tab.foreground", "text", animation, dockable, Color.BLACK );
            
            topSelectedMouse     = new BubbleTabColor( this, STATE_SELECTED | STATE_MOUSE, "stack.tab.background.top.selected.mouse", "top", animation, dockable, Color.RED.brighter() );
            bottomSelectedMouse  = new BubbleTabColor( this, STATE_SELECTED | STATE_MOUSE, "stack.tab.background.bottom.selected.mouse", "bottom", animation, dockable, Color.RED.darker() );
            borderSelectedMouse  = new BubbleTabColor( this, STATE_SELECTED | STATE_MOUSE, "stack.tab.border.selected.mouse", "border", animation, dockable, Color.RED.darker().darker() );
            textSelectedMouse    = new BubbleTabColor( this, STATE_SELECTED | STATE_MOUSE, "stack.tab.foreground.selected.mouse", "text", animation, dockable, Color.BLACK );

            topSelected     = new BubbleTabColor( this, STATE_SELECTED, "stack.tab.background.top.selected", "top", animation, dockable, Color.RED.brighter() );
            bottomSelected  = new BubbleTabColor( this, STATE_SELECTED, "stack.tab.background.bottom.selected", "bottom", animation, dockable, Color.RED.darker() );
            borderSelected  = new BubbleTabColor( this, STATE_SELECTED, "stack.tab.border.selected", "border", animation, dockable, Color.RED.darker().darker() );
            textSelected    = new BubbleTabColor( this, STATE_SELECTED, "stack.tab.foreground.selected", "text", animation, dockable, Color.BLACK );
            
            topFocusedMouse     = new BubbleTabColor( this, STATE_FOCUSED | STATE_MOUSE, "stack.tab.background.top.focused.mouse", "top", animation, dockable, Color.RED.brighter() );
            bottomFocusedMouse  = new BubbleTabColor( this, STATE_FOCUSED | STATE_MOUSE, "stack.tab.background.bottom.focused.mouse", "bottom", animation, dockable, Color.RED.darker() );
            borderFocusedMouse  = new BubbleTabColor( this, STATE_FOCUSED | STATE_MOUSE, "stack.tab.border.focused.mouse", "border", animation, dockable, Color.RED.darker().darker() );
            textFocusedMouse    = new BubbleTabColor( this, STATE_FOCUSED | STATE_MOUSE, "stack.tab.foreground.focused.mouse", "text", animation, dockable, Color.BLACK );
            
            topFocused     = new BubbleTabColor( this, STATE_FOCUSED, "stack.tab.background.top.focused", "top", animation, dockable, Color.RED.brighter() );
            bottomFocused  = new BubbleTabColor( this, STATE_FOCUSED, "stack.tab.background.bottom.focused", "bottom", animation, dockable, Color.RED.darker() );
            borderFocused  = new BubbleTabColor( this, STATE_FOCUSED, "stack.tab.border.focused", "border", animation, dockable, Color.RED.darker().darker() );
            textFocused    = new BubbleTabColor( this, STATE_FOCUSED, "stack.tab.foreground.focused", "text", animation, dockable, Color.BLACK );
            
            colors = new BubbleTabColor[]{
                    top, bottom, border, text,
                    topMouse, bottomMouse, borderMouse, textMouse,
                    
                    topSelected, bottomSelected, borderSelected, textSelected,
                    topSelectedMouse, bottomSelectedMouse, borderSelectedMouse, textSelectedMouse,
                    
                    topFocused, bottomFocused, borderFocused, textFocused,
                    topFocusedMouse, bottomFocusedMouse, borderFocusedMouse, textFocusedMouse };
            
            fontFocused = new BubbleTabFont( this, DockFont.ID_TAB_FOCUSED, dockable );
            fontSelected = new BubbleTabFont( this, DockFont.ID_TAB_SELECTED, dockable );
            fontUnselected = new BubbleTabFont( this, DockFont.ID_TAB_UNSELECTED, dockable );
            
            setController( getController() );
            checkAnimation();
            
			setOpaque( false );
			add( label );
			setLayout( null );
			
			MouseListener listener = new MouseAdapter(){
				@Override
				public void mouseClicked( MouseEvent e ){
					setSelectedIndex( index );
				}
                
                @Override
                public void mouseEntered( MouseEvent e ) {
                    mouse = true;
                    checkAnimation();
                }
                
                @Override
                public void mouseExited( MouseEvent e ) {
                    mouse = false;
                    checkAnimation();
                }
			};
			
			addMouseListener( listener );
			label.addMouseListener( listener );
		}
		
        public Point getPopupLocation( Point click, boolean popupTrigger ) {
            if( popupTrigger )
                return click;
            
            return null;
        }
		
		public void setTooltip( String tooltip ) {
		    setToolTipText( tooltip );
		    label.setToolTipText( tooltip );
		}
		
		public int getState() {
            return state;
        }
		
		public void setController( DockController controller ){
		    if( this.controller != null )
		        this.controller.removeDockableFocusListener( this );
		    
			for( BubbleTabColor color : colors )
			    color.connect( controller );
			
			fontFocused.connect( controller );
			fontSelected.connect( controller );
			fontUnselected.connect( controller );
			
			this.controller = controller;
			if( controller != null ){
			    controller.addDockableFocusListener( this );
			    focused = controller.getFocusedDockable() == dockable;
			}
			
			checkAnimation();
			animation.kick();
		}
		
		public void dockableFocused( DockableFocusEvent event ) {
		    boolean old = focused;
		    focused = this.dockable == event.getNewFocusOwner();
		    if( old != focused ){
		        checkAnimation();
		    }
		}
		
        public void run() {
            label.setForeground( animation.getColor( "text" ));
            repaint();
        }
        
		@Override
		public Dimension getPreferredSize(){
			Dimension size = label.getPreferredSize();
			return new Dimension( 
					size.width+2*borderSize+insets.left+insets.right,
					size.height+arc+insets.top+insets.bottom );
		}
		
		@Override
		public Dimension getMinimumSize(){
			return getPreferredSize();
		}
		
		@Override
		public void doLayout(){
			label.setBounds(
					borderSize+insets.left, 
					insets.top, 
					getWidth()-borderSize-insets.left-insets.right,
					getHeight()-arc-insets.top-insets.bottom );
		}
		
		@Override
		public void paintComponent( Graphics g ){
			Color bottom = animation.getColor( "bottom" );
            Color top = animation.getColor( "top" );
            Color border = animation.getColor( "border" );
			
			int w = getWidth();
			int h = getHeight();

            // Rectangle clip = g.getClipBounds();
            
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                        
            // draw border
			g2.setColor( border );
            g2.fillRoundRect( 0, -arc, w, h+arc, 2*arc, 2*arc );
            
            // draw background
            g2.setPaint( new GradientPaint( 0, 0, top, 0, h-borderSize, bottom ) );
            g2.fillRoundRect( borderSize, -arc, w-2*borderSize, h+arc-borderSize, 2*arc, 2*arc );
			
            // draw text and icon
			Graphics child = g.create( label.getX(), label.getY(), label.getWidth(), label.getHeight() );
			label.update( child );
			child.dispose();
			
            // draw horizon
			g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), 0, h/2, Color.WHITE ));
			g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
			g2.fillRect( 0, 0, w, h/2 );
            
			g2.dispose();
		}
		
		@Override
		protected void paintChildren( Graphics g ){
			// stop
		}
		
		public JComponent getComponent(){
			return this;
		}

		public DockElement getElement() {
		    return dockable;
		}
		
		public boolean isUsedAsTitle() {
		    return false;
		}
		
		public void addMouseInputListener( MouseInputListener listener ) {
		    addMouseListener( listener );
		    addMouseMotionListener( listener );
		    label.addMouseListener( listener );
		    label.addMouseMotionListener( listener );
		}
		
		public void removeMouseInputListener( MouseInputListener listener ) {
		    removeMouseListener( listener );
		    removeMouseMotionListener( listener );
		    label.removeMouseListener( listener );
		    label.removeMouseMotionListener( listener );
		}
		
		public void stateChanged( ChangeEvent e ){
            checkAnimation();
		}
		
		public void setIndex( int index ){
			this.index = index;
            checkAnimation();
		}
        
		/**
		 * Ensures that {@link #animation} uses the correct set of color pairs
		 * and that the correct {@link FontModifier} is used.
		 */
        private void checkAnimation(){
            state = 0;
            
            if( getSelectedIndex() == index )
                state |= STATE_SELECTED;
               
            if( mouse )
                state |= STATE_MOUSE;
                
            if( focused )
                state |= STATE_FOCUSED;

            for( BubbleTabColor color : colors )
                color.transmit();
            
            updateFonts();
        }
        
        /**
         * Ensures that the correct font modifier is used.
         */
        public void updateFonts(){
            if( focused ){
                label.setFontModifier( fontFocused.value() );
            }
            else if( getSelectedIndex() == index ){
                label.setFontModifier( fontSelected.value() );
            }
            else{
                label.setFontModifier( fontUnselected.value() );
            }
        }
		
		public void setIcon( Icon icon ){
			label.setIcon( icon );
		}
		
		public void setText( String text ){
			label.setText( text );
		}
	}
}
