/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.station.stack.CombinedTab;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentListener;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor.Target;
import bibliothek.gui.dock.station.stack.action.DockActionDistributorSource;
import bibliothek.gui.dock.station.stack.tab.Tab;
import bibliothek.gui.dock.station.stack.tab.TabComponentLayoutManager;
import bibliothek.gui.dock.station.stack.tab.TabConfiguration;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.TabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabPaneTabBackgroundComponent;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.themes.font.TabFont;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.gui.dock.util.render.DockRenderingHints;
import bibliothek.gui.dock.util.swing.OrientedLabel;

/**
 * A {@link Tab} used by the {@link BubbleStackDockComponent}.
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
	"stack.tab.foreground.focused",
	
	"stack.tab.background.top.disabled",
	"stack.tab.background.bottom.disabled",
	"stack.tab.border.disabled",
	"stack.tab.foreground.disabled"
})
public class BubbleTab extends ConfiguredBackgroundPanel implements CombinedTab, StackDockComponentListener, Runnable, DockableFocusListener{
	/** a label showing text and icon for this tab */
	private OrientedLabel label = new OrientedLabel();
	
	/** a panel showing additional actions on this tab */
	private ButtonPanel actions = new ButtonPanel( false );
	
	/** the actions shown on {@link #actions} */
	private DockActionDistributorSource actionsSource;
	
	/** layout manager for {@link #label} and {@link #actions} */
	private TabComponentLayoutManager layoutManager;

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
	/** parent component */
	private BubbleStackDockComponent parent;

	/** all the {@link MouseListener} that were added */
	private List<MouseInputListener> mouseInputListeners = new ArrayList<MouseInputListener>();
	
	/** when to paint this panel */
	private int zOrder;
	
	/** the size of the round borders */
	private int arc = 6;
	/** the size of the border */
	private int borderSize = 3;

	private TabPlacement orientation = TabPlacement.TOP_OF_DOCKABLE;

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
	
	private BubbleTabColor topDisabled;
	private BubbleTabColor bottomDisabled;
	private BubbleTabColor borderDisabled;
	private BubbleTabColor textDisabled;

	private BubbleTabColor[] colors;

	private BubbleTabFont fontFocused;
	private BubbleTabFont fontSelected;
	private BubbleTabFont fontUnselected;
	
	private Background background = new Background();

	private static final int STATE_SELECTED = 1;
	private static final int STATE_FOCUSED = 2 | STATE_SELECTED;
	private static final int STATE_MOUSE = 4;
	private static final int STATE_DISABLED = 8;
	
	private int state = 0;

	/**
	 * Creates a new tab
	 * @param parent the owner of this tab
	 * @param dockable the element whose title will be shown on this tab
	 */
	public BubbleTab( BubbleStackDockComponent parent, Dockable dockable ){
		super( Transparency.DEFAULT );
		this.dockable = dockable;
		this.parent = parent;
		label.setHorizontal( orientation.isHorizontal() );
		
		label.setBackground( background );
		setBackground( background );

		animation = new BubbleColorAnimation();
		animation.addTask( this );

		topMouse     = new BubbleTabColor( STATE_MOUSE, "stack.tab.background.top.mouse", "top", animation, dockable, Color.RED.brighter() );
		bottomMouse  = new BubbleTabColor( STATE_MOUSE, "stack.tab.background.bottom.mouse", "bottom", animation, dockable, Color.RED.darker() );
		borderMouse  = new BubbleTabColor( STATE_MOUSE, "stack.tab.border.mouse", "border", animation, dockable, Color.RED.darker().darker() );
		textMouse    = new BubbleTabColor( STATE_MOUSE, "stack.tab.foreground.mouse", "text", animation, dockable, Color.BLACK );

		top     = new BubbleTabColor( 0, "stack.tab.background.top", "top", animation, dockable, Color.RED.brighter() );
		bottom  = new BubbleTabColor( 0, "stack.tab.background.bottom", "bottom", animation, dockable, Color.RED.darker() );
		border  = new BubbleTabColor( 0, "stack.tab.border", "border", animation, dockable, Color.RED.darker().darker() );
		text    = new BubbleTabColor( 0, "stack.tab.foreground", "text", animation, dockable, Color.BLACK );

		topSelectedMouse     = new BubbleTabColor( STATE_SELECTED | STATE_MOUSE, "stack.tab.background.top.selected.mouse", "top", animation, dockable, Color.RED.brighter() );
		bottomSelectedMouse  = new BubbleTabColor( STATE_SELECTED | STATE_MOUSE, "stack.tab.background.bottom.selected.mouse", "bottom", animation, dockable, Color.RED.darker() );
		borderSelectedMouse  = new BubbleTabColor( STATE_SELECTED | STATE_MOUSE, "stack.tab.border.selected.mouse", "border", animation, dockable, Color.RED.darker().darker() );
		textSelectedMouse    = new BubbleTabColor( STATE_SELECTED | STATE_MOUSE, "stack.tab.foreground.selected.mouse", "text", animation, dockable, Color.BLACK );

		topSelected     = new BubbleTabColor( STATE_SELECTED, "stack.tab.background.top.selected", "top", animation, dockable, Color.RED.brighter() );
		bottomSelected  = new BubbleTabColor( STATE_SELECTED, "stack.tab.background.bottom.selected", "bottom", animation, dockable, Color.RED.darker() );
		borderSelected  = new BubbleTabColor( STATE_SELECTED, "stack.tab.border.selected", "border", animation, dockable, Color.RED.darker().darker() );
		textSelected    = new BubbleTabColor( STATE_SELECTED, "stack.tab.foreground.selected", "text", animation, dockable, Color.BLACK );

		topFocusedMouse     = new BubbleTabColor( STATE_FOCUSED | STATE_MOUSE, "stack.tab.background.top.focused.mouse", "top", animation, dockable, Color.RED.brighter() );
		bottomFocusedMouse  = new BubbleTabColor( STATE_FOCUSED | STATE_MOUSE, "stack.tab.background.bottom.focused.mouse", "bottom", animation, dockable, Color.RED.darker() );
		borderFocusedMouse  = new BubbleTabColor( STATE_FOCUSED | STATE_MOUSE, "stack.tab.border.focused.mouse", "border", animation, dockable, Color.RED.darker().darker() );
		textFocusedMouse    = new BubbleTabColor( STATE_FOCUSED | STATE_MOUSE, "stack.tab.foreground.focused.mouse", "text", animation, dockable, Color.BLACK );

		topFocused     = new BubbleTabColor( STATE_FOCUSED, "stack.tab.background.top.focused", "top", animation, dockable, Color.RED.brighter() );
		bottomFocused  = new BubbleTabColor( STATE_FOCUSED, "stack.tab.background.bottom.focused", "bottom", animation, dockable, Color.RED.darker() );
		borderFocused  = new BubbleTabColor( STATE_FOCUSED, "stack.tab.border.focused", "border", animation, dockable, Color.RED.darker().darker() );
		textFocused    = new BubbleTabColor( STATE_FOCUSED, "stack.tab.foreground.focused", "text", animation, dockable, Color.BLACK );

		topDisabled     = new BubbleTabColor( STATE_DISABLED, "stack.tab.background.top.disabled", "top", animation, dockable, Color.LIGHT_GRAY.brighter() );
		bottomDisabled  = new BubbleTabColor( STATE_DISABLED, "stack.tab.background.bottom.disabled", "bottom", animation, dockable, Color.LIGHT_GRAY.darker() );
		borderDisabled  = new BubbleTabColor( STATE_DISABLED, "stack.tab.border.disabled", "border", animation, dockable, Color.LIGHT_GRAY.darker().darker() );
		textDisabled    = new BubbleTabColor( STATE_DISABLED, "stack.tab.foreground.disabled", "text", animation, dockable, Color.BLACK );
		
		colors = new BubbleTabColor[]{
				top, bottom, border, text,
				topMouse, bottomMouse, borderMouse, textMouse,
				topDisabled, bottomDisabled, borderDisabled, textDisabled,
				
				topSelected, bottomSelected, borderSelected, textSelected,
				topSelectedMouse, bottomSelectedMouse, borderSelectedMouse, textSelectedMouse,

				topFocused, bottomFocused, borderFocused, textFocused,
				topFocusedMouse, bottomFocusedMouse, borderFocusedMouse, textFocusedMouse };

		fontFocused = new BubbleTabFont( DockFont.ID_TAB_FOCUSED, dockable );
		fontSelected = new BubbleTabFont( DockFont.ID_TAB_SELECTED, dockable );
		fontUnselected = new BubbleTabFont( DockFont.ID_TAB_UNSELECTED, dockable );

		setController( parent.getController() );
		checkAnimation();

		setOpaque( false );
		add( label );
		add( actions );
		layoutManager = new TabComponentLayoutManager( label, actions, parent.getConfiguration( dockable ) );
		layoutManager.setFreeSpaceToSideBorder( borderSize + borderSize );
		layoutManager.setFreeSpaceToParallelBorder( borderSize );
		layoutManager.setFreeSpaceBetweenLabelAndActions( borderSize );
		layoutManager.setFreeSpaceToOpenSide( arc );
		setLayout( layoutManager );

		MouseListener listener = new MouseAdapter(){
			@Override
			public void mouseClicked( MouseEvent e ){
				BubbleTab.this.parent.setSelectedDockable( BubbleTab.this.dockable );
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
	
	public void setConfiguration( TabConfiguration configuration ){
		layoutManager.setConfiguration( configuration );	
	}

	public TabPane getTabParent(){
		return parent;
	}

	public Dockable getDockable(){
		return dockable;
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

	public void setPaneVisible( boolean visible ){
		parent.getTabHandler().setVisible( this, visible );	
	}

	@Override
	public void setEnabled( boolean enabled ){
		if( isEnabled() != enabled ){
			super.setEnabled( enabled );
			label.setEnabled( enabled );
			
			if( enabled ){
				for( MouseInputListener listener : mouseInputListeners ){
					doRemoveMouseInputListener( listener );
				}
			}
			else{
				for( MouseInputListener listener : mouseInputListeners ){
					doAddMouseInputListener( listener );
				}
			}
			
			checkAnimation();
		}
	}
	
	public boolean isPaneVisible(){
		return parent.getTabHandler().isVisible( this );
	}

	public void setOrientation( TabPlacement orientation ){
		if( orientation == null )
			throw new IllegalArgumentException( "orientation must not be null" );

		if( this.orientation != orientation ){	
			this.orientation = orientation;
			
			layoutManager.setOrientation( orientation );
			
			revalidate();
			repaint();
		}
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
		
		background.setController( controller );

		actions.setController( controller );
		this.controller = controller;
		
		if( controller == null ){
			if( actionsSource != null ){
				actions.set( null );
				actionsSource.setDockable( null );
				actionsSource = null;
			}
		}
		else{
			controller.addDockableFocusListener( this );
			focused = controller.getFocusedDockable() == dockable;
		
			if( actionsSource == null ){
				actionsSource = new DockActionDistributorSource( Target.TAB, BubbleTheme.ACTION_DISTRIBUTOR );
				actionsSource.setDockable( getDockable() );
				actions.set( getDockable(), actionsSource );
			}
		}
		

		checkAnimation();
		animation.kick();
	}

	public void setZOrder( int order ){
		this.zOrder = order;	
	}

	public int getZOrder(){
		return zOrder;
	}

	public Insets getOverlap( TabPaneComponent other ){
		return new Insets( 0, 0, 0, 0 );
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

	public Dimension getPreferredSize( Tab[] tabs ){
		return getPreferredSize();
	}

	public Dimension getMinimumSize( Tab[] tabs ){
		return getMinimumSize();
	}

	@Override
	protected void setupRenderingHints( Graphics g ) {
		if( controller != null ){
			DockRenderingHints renderingHints = controller.getProperties().get( DockRenderingHints.RENDERING_HINTS );
			renderingHints.setupGraphics( g );
		}
	}
	
	@Override
	public void paintBackground( Graphics g ){
		if( getTransparency() != Transparency.TRANSPARENT ){
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			
			Color bottom = animation.getColor( "bottom" );
			Color top = animation.getColor( "top" );
			Color border = animation.getColor( "border" );
	
			int w = getWidth();
			int h = getHeight();
	
			// Rectangle clip = g.getClipBounds();
		
			// draw border
			g2.setColor( border );
			switch( orientation ){
				case TOP_OF_DOCKABLE:
					g2.fillRoundRect( 0, 0, w, h+arc, 2*arc, 2*arc );
					break;
				case BOTTOM_OF_DOCKABLE:
					g2.fillRoundRect( 0, -arc, w, h+arc, 2*arc, 2*arc );
					break;
				case LEFT_OF_DOCKABLE:
					g2.fillRoundRect( 0, 0, w+arc, h, 2*arc, 2*arc );
					break;
				case RIGHT_OF_DOCKABLE:
					g2.fillRoundRect( -arc, 0, w+arc, h, 2*arc, 2*arc );
					break;
			}
		
			// draw background
			if( orientation.isHorizontal() )
				g2.setPaint( new GradientPaint( 0, 0, top, 0, h-borderSize, bottom ) );
			else
				g2.setPaint( new GradientPaint( 0, 0, top, w-borderSize, 0, bottom ) );
			switch( orientation ){
				case TOP_OF_DOCKABLE:
					g2.fillRoundRect( borderSize, borderSize, w-2*borderSize, h+arc-borderSize, 2*arc, 2*arc );
					break;
				case BOTTOM_OF_DOCKABLE:
					g2.fillRoundRect( borderSize, -arc, w-2*borderSize, h+arc-borderSize, 2*arc, 2*arc );
					break;
				case LEFT_OF_DOCKABLE:
					g2.fillRoundRect( borderSize, borderSize, w+arc-borderSize, h-2*borderSize, 2*arc, 2*arc );
					break;
				case RIGHT_OF_DOCKABLE:
					g2.fillRoundRect( -arc, borderSize, w+arc-borderSize, h-2*borderSize, 2*arc, 2*arc );
					break;
			}
			
			g2.dispose();
		}
	}

	@Override
	public void paintForeground( Graphics g ){
		super.paintChildren( g );
	}
	
	@Override
	public void paintOverlay( Graphics g ){
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		int w = getWidth();
		int h = getHeight();
				
		// draw horizon
		if( orientation.isHorizontal() ){
			g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), 0, h/2, Color.WHITE ));
		}
		else{
			g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), w/2, 0, Color.WHITE ));
		}
		
		g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
		switch( orientation ){
			case TOP_OF_DOCKABLE:
				g2.setClip( new RoundRectangle2D.Float( 0, 0, w, h+arc, 2*arc, 2*arc ));
				break;
			case BOTTOM_OF_DOCKABLE:
				g2.setClip( new RoundRectangle2D.Float( 0, -arc, w, h+arc, 2*arc, 2*arc ));
				break;
			case LEFT_OF_DOCKABLE:
				g2.setClip( new RoundRectangle2D.Float( 0, 0, w+arc, h, 2*arc, 2*arc ));
				break;
			case RIGHT_OF_DOCKABLE:
				g2.setClip( new RoundRectangle2D.Float( -arc, 0, w+arc, h, 2*arc, 2*arc ));
				break;
		}

		
		if( orientation.isHorizontal() ){
			g2.fillRect( 0, 0, w, h/2 );
		}
		else{
			g2.fillRect( 0, 0, w/2, h );
		}

		g2.dispose();
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
	
	public boolean shouldFocus(){
    	return true;
    }
	
	public boolean shouldTransfersFocus(){
		return true;
	}

	public void addMouseInputListener( MouseInputListener listener ) {
		mouseInputListeners.add( listener );
		if( isEnabled() ){
			doAddMouseInputListener( listener );
		}
	}
	
	private void doAddMouseInputListener( MouseInputListener listener ){
		addMouseListener( listener );
		addMouseMotionListener( listener );
		label.addMouseListener( listener );
		label.addMouseMotionListener( listener );
	}

	public void removeMouseInputListener( MouseInputListener listener ) {
		mouseInputListeners.remove( listener );
		if( isEnabled() ){
			doRemoveMouseInputListener( listener );
		}
	}
	
	private void doRemoveMouseInputListener( MouseInputListener listener ){
		removeMouseListener( listener );
		removeMouseMotionListener( listener );
		label.removeMouseListener( listener );
		label.removeMouseMotionListener( listener );
	}

	public void selectionChanged( StackDockComponent stack ){
		checkAnimation();
	}

	public void tabChanged( StackDockComponent stack, Dockable dockable ){
		// ignore
	}
	
	/**
	 * Ensures that {@link #animation} uses the correct set of color pairs
	 * and that the correct {@link FontModifier} is used.
	 */
	private void checkAnimation(){
		state = 0;

		if( !isEnabled() ){
			state = STATE_DISABLED;
		}
		else{
			if( parent.getSelectedDockable() == dockable )
				state |= STATE_SELECTED;
	
			if( mouse )
				state |= STATE_MOUSE;
	
			if( focused )
				state |= STATE_FOCUSED;
		}

		for( BubbleTabColor color : colors )
			color.transmit();

		updateFonts();
	}

	/**
	 * Ensures that the correct font modifier is used.
	 */
	public void updateFonts(){
		if( !isEnabled() ){
			label.setFontModifier( fontUnselected.value() );
		}
		else if( focused ){
			label.setFontModifier( fontFocused.value() );
		}
		else if( parent.getSelectedDockable() == dockable ){
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

	/**
	 * Stops the {@link BubbleColorAnimation animation} of this tab.
	 */
	public void stopAnimation(){
		animation.stop();
	}

	/**
	 * Some color needed on a {@link Tab}.
	 * @author Benjamin Sigg
	 */
	protected class BubbleTabColor extends TabColor{
		private int state;
		private String animationId;
		private BubbleColorAnimation animation;

		public BubbleTabColor( int state, String id, String animationId, BubbleColorAnimation animation, Dockable dockable, Color backup ){
			super( id, parent.getStation(), dockable, backup);
			this.state = state;
			this.animationId = animationId;
			this.animation = animation;
		}

		/**
		 * Transmits the color of this {@link TabColor} if the state is
		 * correct.
		 */
		public void transmit(){
			if( getState() == state ){
				animation.putColor( animationId, value() );
			}
		}

		@Override
		protected void changed( Color oldColor, Color newColor ) {
			if( getState() == state ){
				animation.putColor( animationId, newColor );
			}
		}
	}

	/**
	 * Some font needed on a {@link Tab}
	 * @author Benjamin Sigg
	 */
	protected class BubbleTabFont extends TabFont{
		/**
		 * Creates a new font
		 * @param id the name of the font
		 * @param dockable the element shown on the tab
		 */
		public BubbleTabFont( String id, Dockable dockable ){
			super( id, parent.getStation(), dockable );
		}

		@Override
		protected void changed( FontModifier oldValue, FontModifier newValue ) {
			updateFonts();
		}
	}

	/**
	 * A representation of the background of this {@link BubbleTab}.
	 * @author Benjamin Sigg
	 */
	private class Background extends BackgroundAlgorithm implements TabPaneTabBackgroundComponent{
		public Background(){
			super( TabPaneTabBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".tabPane.child.tab" );
		}

		public Tab getTab(){
			return BubbleTab.this;
		}

		public TabPaneComponent getChild(){
			return BubbleTab.this;
		}

		public TabPane getPane(){
			return getTabParent();
		}

		public Component getComponent(){
			return BubbleTab.this;
		}
	}
}
