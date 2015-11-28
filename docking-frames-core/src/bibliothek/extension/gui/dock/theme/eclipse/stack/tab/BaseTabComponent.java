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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabStateInfo;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.focus.DockFocusTraversalPolicy;
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
 * A base implementation of {@link TabComponent}. This component contains
 * an {@link OrientedLabel} which is used to paint icon and text, also a
 * {@link ButtonPanel} is present to paint additional buttons.
 * @author Benjamin Sigg
 */
@ColorCodes({
	"stack.tab.border", 
	"stack.tab.border.selected", 
	"stack.tab.border.selected.focused", 
	"stack.tab.border.selected.focuslost",
	"stack.tab.border.disabled",
	
    "stack.tab.top", 
    "stack.tab.top.selected", 
    "stack.tab.top.selected.focused",
    "stack.tab.top.selected.focuslost",
    "stack.tab.top.disabled", 
    
    "stack.tab.bottom", 
    "stack.tab.bottom.selected", 
    "stack.tab.bottom.selected.focused", 
    "stack.tab.bottom.selected.focuslost",
    "stack.tab.bottom.disabled",
    
    "stack.tab.text", 
    "stack.tab.text.selected", 
    "stack.tab.text.selected.focused", 
    "stack.tab.text.selected.focuslost",
    "stack.tab.text.disabled", 
    
    "stack.border" })
public abstract class BaseTabComponent extends ConfiguredBackgroundPanel implements TabComponent, EclipseTabStateInfo{
    protected final TabColor colorStackTabBorder;
    protected final TabColor colorStackTabBorderSelected;
    protected final TabColor colorStackTabBorderSelectedFocused;
    protected final TabColor colorStackTabBorderSelectedFocusLost;
    protected final TabColor colorStackTabBorderDisabled;
    
    protected final TabColor colorStackTabTop;
    protected final TabColor colorStackTabTopSelected;
    protected final TabColor colorStackTabTopSelectedFocused;
    protected final TabColor colorStackTabTopSelectedFocusLost;
    protected final TabColor colorStackTabTopDisabled;
    
    protected final TabColor colorStackTabBottom;
    protected final TabColor colorStackTabBottomSelected;
    protected final TabColor colorStackTabBottomSelectedFocused;
    protected final TabColor colorStackTabBottomSelectedFocusLost;
    protected final TabColor colorStackTabBottomDisabled;
    
    protected final TabColor colorStackTabText;
    protected final TabColor colorStackTabTextSelected;
    protected final TabColor colorStackTabTextSelectedFocused;
    protected final TabColor colorStackTabTextSelectedFocusLost;
    protected final TabColor colorStackTabTextDisabled;
    
    protected final TabColor colorStackBorder;
    
    protected final TabFont fontSelected;
    protected final TabFont fontFocused;
    protected final TabFont fontUnselected;
    
    private TabColor[] colors;
    private TabFont[] fonts;
    
    private Dockable dockable;
    
    private boolean paintIconWhenInactive = false;
    private Icon icon;
    
    private ButtonPanel buttons;
    private EclipseDockActionSource actions;
    
    private boolean hasFocus;
    private boolean isSelected;
    private EclipseTabPane pane;
    private EclipseTab tab;

    private boolean bound;
    
    private Background background;
    
    
    private OrientedLabel label = new OrientedLabel();
    
    private TabPlacement orientation = TabPlacement.TOP_OF_DOCKABLE;
    
    private boolean previousTabSelectedSet = false;
    private boolean previousTabSelected = false;
    
    private boolean nextTabSelectedSet = false;
    private boolean nextTabSelected = false;
    
    private TabComponentLayoutManager layoutManager;
    
    /**
     * Creates a new {@link TabComponent}
     * @param pane the owner of this tab, not <code>null</code>
     * @param dockable the element which is represented by this component, not <code>null</code>
     */
    public BaseTabComponent( EclipseTabPane pane, Dockable dockable ){
    	this( pane, dockable, null );
    }
    
    /**
     * Creates a new {@link TabComponent}
     * @param pane the owner of this tab, not <code>null</code>
     * @param dockable the element which is represented by this component, not <code>null</code>
     * @param colorPostfix a string that will be added to any key for a color, can be <code>null</code>
     */    
    public BaseTabComponent( EclipseTabPane pane, Dockable dockable, String colorPostfix ){
    	super( null, Transparency.DEFAULT );
    	
    	if( pane == null )
    		throw new IllegalArgumentException( "pane must not be null" );
    	if( dockable == null )
    		throw new IllegalArgumentException( "dockable must not be null" );
    	
        this.pane = pane;
        this.dockable = dockable;
        
        add( label );
        
        DockStation station = pane.getStation();
        
        if( colorPostfix == null ){
        	colorPostfix = "";
        }
        
        colorStackTabBorder = new BorderTabColor( "stack.tab.border" + colorPostfix, station, Color.WHITE );
        colorStackTabBorderSelected = new BorderTabColor( "stack.tab.border.selected" + colorPostfix, station, Color.WHITE );
        colorStackTabBorderSelectedFocused = new BorderTabColor( "stack.tab.border.selected.focused" + colorPostfix, station, Color.WHITE );
        colorStackTabBorderSelectedFocusLost = new BorderTabColor( "stack.tab.border.selected.focuslost" + colorPostfix, station, Color.WHITE );
        colorStackTabBorderDisabled = new BorderTabColor( "stack.tab.border.disabled" + colorPostfix, station, Color.WHITE );
        
        colorStackTabTop = new BaseTabColor( "stack.tab.top" + colorPostfix, station, Color.LIGHT_GRAY );
        colorStackTabTopSelected = new BaseTabColor( "stack.tab.top.selected" + colorPostfix, station, Color.LIGHT_GRAY );
        colorStackTabTopSelectedFocused = new BaseTabColor( "stack.tab.top.selected.focused" + colorPostfix, station, Color.LIGHT_GRAY );
        colorStackTabTopSelectedFocusLost = new BaseTabColor( "stack.tab.top.selected.focuslost" + colorPostfix, station, Color.LIGHT_GRAY );
        colorStackTabTopDisabled = new BaseTabColor( "stack.tab.top.disabled" + colorPostfix, station, Color.LIGHT_GRAY );
        
        colorStackTabBottom = new BaseTabColor( "stack.tab.bottom" + colorPostfix, station, Color.WHITE );
        colorStackTabBottomSelected = new BaseTabColor( "stack.tab.bottom.selected" + colorPostfix, station, Color.WHITE );
        colorStackTabBottomSelectedFocused = new BaseTabColor( "stack.tab.bottom.selected.focused" + colorPostfix, station, Color.WHITE );
        colorStackTabBottomSelectedFocusLost = new BaseTabColor( "stack.tab.bottom.selected.focuslost" + colorPostfix, station, Color.WHITE );
        colorStackTabBottomDisabled = new BaseTabColor( "stack.tab.bottom.disabled" + colorPostfix, station, Color.WHITE );
        
        colorStackTabText = new BaseTabColor( "stack.tab.text" + colorPostfix, station, Color.BLACK );
        colorStackTabTextSelected = new BaseTabColor( "stack.tab.text.selected" + colorPostfix, station, Color.BLACK );
        colorStackTabTextSelectedFocused = new BaseTabColor( "stack.tab.text.selected.focused" + colorPostfix, station, Color.BLACK );
        colorStackTabTextSelectedFocusLost = new BaseTabColor( "stack.tab.text.selected.focuslost" + colorPostfix, station, Color.BLACK );
        colorStackTabTextDisabled = new BaseTabColor( "stack.tab.text.disabled" + colorPostfix, station, Color.BLACK );
        
        colorStackBorder = new BaseTabColor( "stack.border" + colorPostfix, station, Color.BLACK );
        
        fontFocused = new BaseTabFont( DockFont.ID_TAB_FOCUSED, station );
        fontSelected = new BaseTabFont( DockFont.ID_TAB_SELECTED, station );
        fontUnselected = new BaseTabFont( DockFont.ID_TAB_UNSELECTED, station );
        
        colors = new TabColor[]{
                colorStackTabBorder,
                colorStackTabBorderSelected,
                colorStackTabBorderSelectedFocused,
                colorStackTabBorderSelectedFocusLost,
                colorStackTabBorderDisabled,
                colorStackTabTop,
                colorStackTabTopSelected,
                colorStackTabTopSelectedFocused,
                colorStackTabTopSelectedFocusLost,
                colorStackTabTopDisabled,
                colorStackTabBottom,
                colorStackTabBottomSelected,
                colorStackTabBottomSelectedFocused,
                colorStackTabBottomSelectedFocusLost,
                colorStackTabBottomDisabled,
                colorStackTabText,
                colorStackTabTextSelected,
                colorStackTabTextSelectedFocused,
                colorStackTabTextSelectedFocusLost,
                colorStackTabTextDisabled,
                colorStackBorder
        };
        
        fonts = new TabFont[]{
                fontFocused,
                fontSelected,
                fontUnselected
        };
        
        buttons = new ButtonPanel( false );
		add( buttons );
        
        setFocusable( false );
        setFocusTraversalPolicyProvider( true );
        setFocusTraversalPolicy( new DockFocusTraversalPolicy( new BaseTabFocusTraversalPolicy( buttons ), true ) );
        
		layoutManager = new TabComponentLayoutManager( label, buttons, pane.getConfiguration( dockable ) );
		setLayout( layoutManager );
    }
    
    public EclipseTabStateInfo getEclipseTabStateInfo(){
    	return this;
    }
    
    /**
     * Adds an additional set of colors to this tab. This method should be called before this
     * tab is {@link #bind() bound}.
     * @param colors the additional set of colors
     */
    protected void addAdditionalColors( TabColor... colors ){
    	TabColor[] newColors = new TabColor[ this.colors.length + colors.length ];
    	System.arraycopy( this.colors, 0, newColors, 0, this.colors.length );
    	System.arraycopy( colors, 0, newColors, this.colors.length, colors.length );
    	this.colors = newColors;
    }
    
    public void setConfiguration( TabConfiguration configuration ){
    	layoutManager.setConfiguration( configuration );
    }
    
    /**
     * Called when one of the border colors changed
     */
    public abstract void updateBorder();
    
    /**
     * Called when the font of this component has to be updated
     */
    protected void updateFont(){
        TabFont font = null;
        if( !isEnabled() ){
        	font = fontUnselected;
        }
        else if( isFocused() ){
            font = fontFocused;
        }
        else if( isSelected() ){
            font = fontSelected;
        }
        else{
            font = fontUnselected;
        }
        
        label.setFontModifier( font.font() );
    }
    
    /**
     * Recalculates which actions to show on this component.
     */
    protected void refreshActions(){
    	if( actions != null ){
    		actions.refresh();
    	}
    	pane.getInfoComponent().refreshActions();
    }
    
    /**
     * Called when the focus state of this component changed.
     */
    public abstract void updateFocus();
    
    /**
     * Called when the selection state of this tab changed.
     */
    protected abstract void updateSelected();
    
    /**
     * Called when the colors of this tab changed.
     */
    protected abstract void updateColors();
    
    /**
     * Called when the enabled state of this tab changed.
     */
    protected abstract void updateEnabled();
    
    /**
     * Called when the {@link #doPaintIconWhenInactive() paint icon property} of
     * this component changed.
     */
    protected void updatePaintIcon(){
    	if( isSelected() || doPaintIconWhenInactive() ){
    		label.setIcon( icon );
    	}
    	else{
    		label.setIcon( null );
    	}
    	revalidate();
    	repaint();
    }
    
    /**
     * Called when the tab placement of this tab changed.
     */
    protected abstract void updateOrientation();
    
    public void setTab( EclipseTab tab ){
    	if( background != null ){
    		background.setController( null );
    		background = null;
    	}
    	
	    this.tab = tab;
	    
	    if( bound ){
	    	background = new Background( tab );
	    	background.setController( getController() );
	    	buttons.setController( getController() );
	    }
	    setBackground( background );
    }
    
    public void bind() {
        if( buttons != null ){
        	actions = new EclipseDockActionSource(
            		pane.getTheme(), dockable.getGlobalActionOffers(), this, true );
            buttons.set( dockable, actions );
        }
        
        DockController controller = pane.getController();
        if( tab != null ){
        	background = new Background( tab );
        	background.setController( controller );
        }
        setBackground( background );
        buttons.setController( controller );
        
        for( TabColor color : colors ){
            color.connect( controller );
        }
        for( TabFont font : fonts ){
            font.connect( controller );
        }
        
        		
        revalidate();
        bound = true;
    }
    
    public void unbind() {
    	bound = false;
        if( buttons != null )
            buttons.set( null );
        
        if( background != null ){
        	background.setController( null );
        	background = null;
        }
        setBackground( background );
        buttons.setController( null );
        
        for( TabColor color : colors )
            color.connect( null );
        for( TabFont font : fonts )
            font.connect( null );
    }
    
    /**
     * Tells whether the {@link #bind()} method has been called.
     * @return <code>true</code> if this tab is bound to its owner
     */
    public boolean isBound(){
    	return bound;
    }

    public Dockable getDockable() {
        return dockable;
    }
    
    public DockElement getElement() {
        return dockable;
    }
    
    public boolean isUsedAsTitle() {
        return true;
    }
    
    public boolean shouldTransfersFocus(){
	    return true;
    }
    
    public boolean shouldFocus(){
    	return true;
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
    public synchronized void addMouseListener( MouseListener l ){
    	super.addMouseListener( l );
    	label.addMouseListener( l );
    	buttons.addMouseListener( l );
    }
    
    @Override
    public synchronized void removeMouseListener( MouseListener l ){
    	super.removeMouseListener( l );
    	label.removeMouseListener( l );
    	buttons.removeMouseListener( l );
    }
    
    @Override
    public synchronized void addMouseMotionListener( MouseMotionListener l ){
    	super.addMouseMotionListener( l );
    	label.addMouseMotionListener( l );
    	buttons.addMouseMotionListener( l );
    }
    
    @Override
    public synchronized void removeMouseMotionListener( MouseMotionListener l ){
    	super.removeMouseMotionListener( l );
    	label.removeMouseMotionListener( l );
    	buttons.removeMouseMotionListener( l );
    }
    
    public Point getPopupLocation( Point click, boolean popupTrigger ) {
        if( popupTrigger )
            return click;
        
        return null;
    }
    
    public DockController getController() {
        return pane.getController();
    }
    
    public DockStation getStation() {
        return pane.getStation();
    }
    
    @Override
    protected void setupRenderingHints( Graphics g ) {
    	DockController controller = getController();
    	if( controller != null ){
    		DockRenderingHints renderingHints = controller.getProperties().get( DockRenderingHints.RENDERING_HINTS );
    		renderingHints.setupGraphics( g );
    	}
    }
    
    /**
     * Gets the parent of this component.
     * @return the owner
     */
    public EclipseTabPane getPane(){
		return pane;
	}
    
    public Component getComponent(){
        return this;
    }
    
    public void setFocused( boolean focused ){
    	if( hasFocus != focused ){
    		hasFocus = focused;
    		refreshActions();
    		updateFocus();
    	}
    }
    
    public boolean isFocused(){
        return hasFocus;
    }
    
    /**
     * Tells whether the focus of this component is currently lost, but
     * will be retrieved as soon as the underlying frame gets activated.
     * @return <code>true</code> if the focus is only temporarily lost
     */
    public  boolean isFocusTemporarilyLost(){
		Window window = SwingUtilities.getWindowAncestor( getComponent() );
		boolean focusTemporarilyLost = false;

		if( window != null ){
			focusTemporarilyLost = !window.isActive();
		}
		
		return focusTemporarilyLost;
    }
    
    public void setSelected( boolean selected ){
    	if( isSelected != selected ){
    		isSelected = selected;
    		if( isSelected() || doPaintIconWhenInactive() ){
    			label.setIcon( icon );
    		}
    		else{
    			label.setIcon( null );
    		}
    		refreshActions();
    		revalidate();
    		updateSelected();
    	}
    }
    
    public boolean isSelected(){
        return isSelected;
    }
    
    /**
     * Overrides the result of {@link #isPreviousTabSelected()}, the method
     * will from now on only return <code>selected</code> until
     * {@link #cleanPreviousTabSelected()} is called which reinstates the
     * original behavior.
     * @param selected the future result of {@link #isPreviousTabSelected()}
     */
    protected void setPreviousTabSelected( boolean selected ){
    	previousTabSelected = selected;
    	previousTabSelectedSet = true;
    }
    
    /**
     * Cleans the state set by {@link #setPreviousTabSelected(boolean)}
     */
    protected void cleanPreviousTabSelected(){
    	previousTabSelectedSet = false;
    }
    
	/**
	 * Tells whether the tab before this one is selected. This method only
	 * checks visible tabs.
	 * @return <code>true</code> if the tab before is selected
	 */
	protected boolean isPreviousTabSelected(){
		if( previousTabSelectedSet )
			return previousTabSelected;
		
		EclipseTabPane pane = getPane();
		pane.getSelectedIndex();
		
		int self = getTabIndex();
		if( self <= 0 )
			return false;
		
		EclipseTab previous = pane.getVisibleTab( self-1 );
		
		return previous.getDockable() == pane.getSelectedDockable();
	}
	
    /**
     * Overrides the result of {@link #isNextTabSelected()}, the method
     * will from now on only return <code>selected</code> until
     * {@link #cleanNextTabSelected()} is called which reinstates the
     * original behavior.
     * @param selected the future result of {@link #isNextTabSelected()}
     */
    protected void setNextTabSelected( boolean selected ){
    	nextTabSelected = selected;
    	nextTabSelectedSet = true;
    }
    
    /**
     * Cleans the state set by {@link #setNextTabSelected(boolean)}
     */
    protected void cleanNextTabSelected(){
    	nextTabSelectedSet = false;
    }
    
	/**
	 * Tells whether the tab after this one is selected. This method only
	 * checks visible tabs.
	 * @return <code>true</code> if the tab before is selected
	 */
	protected boolean isNextTabSelected(){
		if( nextTabSelectedSet )
			return nextTabSelected;
		
		EclipseTabPane pane = getPane();
		
		int self = getTabIndex();
		if( self >= pane.getVisibleTabCount() )
			return false;
		
		EclipseTab next = pane.getVisibleTab( self+1 );
		if( next == null )
			return false;
		
		return next.getDockable() == pane.getSelectedDockable();
	}
    
    /**
     * Tells which index the {@link #getDockable() dockable} of this tab
     * has on the owner.
     * @return the index of the dockable on the owner, -1 if the owner is
     * unknown or the dockable is no longer child of the owner
     */
    public int getDockableIndex(){
    	EclipseTabPane pane = getPane();
    	if( pane == null || !isBound() )
    		return -1;
    	return pane.indexOf( getDockable() );
    }
    
    public int getTabIndex(){
    	EclipseTabPane pane = getPane();
    	if( pane == null || !isBound() )
    		return -1;
    	
    	return pane.indexOfVisible( this );
    }
    
    public boolean doPaintIconWhenInactive() {
        return paintIconWhenInactive;
    }

    /**
     * Tells this component whether to paint an icon if not selected. Please
     * note that this method only stores the value, but {@link BaseTabComponent}
     * itself never checks this property.
     */
    public void setPaintIconWhenInactive(boolean paintIconWhenInactive) {
    	if( this.paintIconWhenInactive != paintIconWhenInactive ){
    		this.paintIconWhenInactive = paintIconWhenInactive;
    		updatePaintIcon();
    	}
    }
    
    /**
     * Sets the icon that is painted on this component. Please note that the icon
     * is not painted if this tab is not selected and {@link #setPaintIconWhenInactive(boolean)}
     * is <code>false</code>.
     * @param icon the icon to paint, can be <code>null</code>
     */
    public void setIcon( Icon icon ){
    	this.icon = icon;
    	if( isSelected() || doPaintIconWhenInactive() ){
    		label.setIcon( icon );
    		revalidate();
    	}
    }
    
    /**
     * Gets the icon that is painted on this component.
     * @return the icon, can be <code>null</code>
     */
    public Icon getIcon(){
    	return label.getIcon();
    }
    
    /**
     * Sets the text that is to be painted on this component.
     * @param text the text, can be <code>null</code>
     */
    public void setText( String text ){
    	label.setText( text );
    	revalidate();
    }
    
    public void setTooltip( String tooltip ){
	    setToolTipText( tooltip );	
	    label.setToolTipText( tooltip );
    }
    
    /**
     * Gets the text of this component.
     * @return the text, may be <code>null</code>
     */
    public String getText(){
    	return label.getText();
    }
    
    /**
     * Sets the insets to be left free around the label.
     * @param labelInsets the free space, not <code>null</code>
     */
    public void setLabelInsets( Insets labelInsets ){
    	layoutManager.setLabelInsets( labelInsets );
		revalidate();
	}
    
    /**
     * Gets the space that is left free around the label.
     * @return the free space, not <code>null</code>
     */
    public Insets getLabelInsets(){
		return layoutManager.getLabelInsets();
	}
    
    /**
     * Sets the insets to be left free around the buttons.
     * @param buttonInsets the free space, not <code>null</code>
     */
    public void setButtonInsets( Insets buttonInsets ){
    	layoutManager.setActionInsets( buttonInsets );
		revalidate();
	}
    
    /**
     * Gets the space that is to be left free around the buttons.
     * @return the free space
     */
    public Insets getButtonInsets(){
		return layoutManager.getActionInsets();
	}
    
    /**
     * Gets the label which is used to paint icon and text.
     * @return the label
     */
    protected OrientedLabel getLabel(){
		return label;
	}

    /**
     * Gets the panel which shows a set of {@link DockAction}s.
     * @return the panel, not <code>null</code>
     */
    public ButtonPanel getButtons() {
        return buttons;
    }
    
    /**
     * Tells whether the {@link #getButtons() buttons} contain the position <code>x/y</code>.
     * @param x x coordinate
     * @param y y coordinate
     * @return <code>true</code> if the buttons contains this location
     */
    protected boolean containsButton( int x, int y ){
    	x -= buttons.getX();
    	y -= buttons.getY();
    	return buttons.contains( x, y );
    }
    
    @Override
    public void setEnabled( boolean enabled ){
    	if( isEnabled() != enabled ){
	    	super.setEnabled( enabled );
	    	label.setEnabled( enabled );
	    	refreshActions();
	    	updateEnabled();
    	}
    }
    
    public void setOrientation( TabPlacement orientation ){
    	if( orientation == null )
    		throw new IllegalArgumentException( "orientation must not be null" );
	    
    	if( this.orientation != orientation ){
	    	this.orientation = orientation;	
	    	layoutManager.setOrientation( orientation );
	    	refreshActions();
		    updateOrientation();
    	}
    }
    
    /**
     * Gets the current orientation of this tab, see {@link #setOrientation(TabPlacement)}.
     * @return the placement
     */
    public TabPlacement getOrientation(){
		return orientation;
	}
    
	public Dimension getMinimumSize( TabComponent[] tabs ){
		setSelection( tabs );
		Dimension result = getMinimumSize();
		unsetSelection();
		return result;
	}
	
	public Dimension getPreferredSize( TabComponent[] tabs ){
		setSelection( tabs );
		Dimension result = getPreferredSize();
		unsetSelection();
		return result;
	}
	
	private void setSelection( TabComponent[] tabs ){
		for( int i = 0; i < tabs.length; i++ ){
			if( tabs[i] == this ){
				if( i > 0 ){
					if( tabs[i-1] instanceof BaseTabComponent ){
						setPreviousTabSelected( ((BaseTabComponent)tabs[i-1]).isSelected() );
					}
				}
				if( i+1 < tabs.length ){
					if( tabs[i+1] instanceof BaseTabComponent ){
						setNextTabSelected( ((BaseTabComponent)tabs[i+1]).isSelected() );
					}
				}
				break;
			}
		}
	}
	
	private void unsetSelection(){
		cleanNextTabSelected();
		cleanPreviousTabSelected();
	}

    /**
     * A color used in the border
     * @author Benjamin Sigg
     */
    private class BorderTabColor extends TabColor{
        public BorderTabColor( String id, DockStation station, Color backup ){
            super( id, station, dockable, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateBorder();
        }
    }

    /**
     * A color used on this tab
     * @author Benjamin Sigg
     */
    private class BaseTabColor extends TabColor{
        public BaseTabColor( String id, DockStation station, Color backup ){
            super( id, station, dockable, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateColors();
        }
    }

    /**
     * A font used on this tab
     * @author Benjamin Sigg
     */
    private class BaseTabFont extends TabFont{
        public BaseTabFont( String id, DockStation station ){
            super( id, station, dockable );
        }
        @Override
        protected void changed( FontModifier oldValue, FontModifier newValue ) {
            updateFont();
        }
    }


	/**
	 * A representation of the background of this {@link EclipseTab}.
	 * @author Benjamin Sigg
	 */
	private class Background extends BackgroundAlgorithm implements TabPaneTabBackgroundComponent{
		private EclipseTab tab;
		
		/**
		 * Creates a new background.
		 * @param tab the tab which is using this component
		 */
		public Background( EclipseTab tab ){
			super( TabPaneTabBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".tabPane.child.tab" );
			this.tab = tab;
		}

		public Tab getTab(){
			return tab;
		}

		public TabPaneComponent getChild(){
			return tab;
		}

		public TabPane getPane(){
			return tab.getTabParent();
		}

		public Component getComponent(){
			return BaseTabComponent.this.getComponent();
		}
	}
}
