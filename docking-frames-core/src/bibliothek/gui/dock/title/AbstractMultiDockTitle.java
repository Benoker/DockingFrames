/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.title;

import java.awt.Color;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.component.DockComponentConfiguration;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.font.AbstractDockFont;
import bibliothek.gui.dock.util.font.FontManager;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.gui.dock.util.render.DockRenderingHints;
import bibliothek.gui.dock.util.swing.OrientedLabel;
import bibliothek.util.Condition;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Path;

/**
 * An abstract implementation of {@link DockTitle}. This title can have an icon and some text, but it does not
 * show buttons for {@link DockAction}s.<br>
 * Clients should make use of {@link AbstractDockTitle} which is also the super-class for all the {@link DockTitle}s that
 * are used by the framework.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public abstract class AbstractMultiDockTitle extends ConfiguredBackgroundPanel implements DockTitle {
    /** Insets of the size 1,2,1,2 */
    private static final Insets DEFAULT_INSETS_HORIZONTAL = new Insets( 0, 1, 0, 1 );
    /** Insets of the size 2,1,2,1 */
    private static final Insets DEFAULT_INSETS_VERTICAL = new Insets( 1, 0, 1, 0 );
    
    /** The {@link Dockable} for which this title is shown */
    private Dockable dockable;
    

    /** informs clients about all the {@link Component}s of this title */
    private DockComponentRootHandler rootHandler;
    
    /** A label for the title-text */
    private OrientedLabel label = new OrientedLabel(){
    	@Override
    	protected void updateFonts(){
    		AbstractMultiDockTitle.this.updateFonts();
    	}
    };
    
    /** 
     * A listener added to the owned {@link Dockable}. The listener changes the
     * title-text and the icon of this title. 
     */
    private Listener listener = new Listener();
    /** The creator of this title */
    private DockTitleVersion origin;
    
    /** <code>true</code> if this title is currently selected, <code>false</code> otherwise */
    private boolean active = false;
    /** <code>true</code> if this title is currently bound to a {@link Dockable} */
    private boolean bound = false;
    
    /** Whether the layout should be horizontal or vertical */
    private Orientation orientation = Orientation.FREE_HORIZONTAL;
    /** The icon which is shown on this title */
    private Icon icon;
    
    /** disabled version of {@link #icon} */
    private Icon disabledIcon;
    
    /** number of pixels to paint between icon and text */
    private int iconTextGap = 0;
    
    /** the colors used by this title */
    private List<AbstractDockColor> colors = new ArrayList<AbstractDockColor>();
    /** the fonts used by this title */
    private List<AbstractDockFont> fonts = new ArrayList<AbstractDockFont>();
    /** the fonts which are used automatically */
    private List<ConditionalFont> conditionalFonts;
    
    /** the background of this title */
    private Background background = new Background();
    
    /** the current border, can be <code>null</code> */
    private TitleBorder border;
    
    /** whether this title is disabled */
    private boolean disabled = false;
    
    /** all the mouse listeners of this title */
    private List<MouseInputListener> mouseInputListeners = new ArrayList<MouseInputListener>();
    
    /** tells how to paint the text on this title */
    private PropertyValue<OrientationToRotationStrategy> orientationConverter = new PropertyValue<OrientationToRotationStrategy>( DockTitle.ORIENTATION_STRATEGY ){
		protected void valueChanged( OrientationToRotationStrategy oldValue, OrientationToRotationStrategy newValue ){
			if( oldValue != null ){
				oldValue.removeListener( orientationListener );
				oldValue.uninstall( AbstractMultiDockTitle.this );
			}
			if( newValue != null ){
				newValue.install( AbstractMultiDockTitle.this );
				newValue.addListener( orientationListener );
			}
			
			updateLabelRotation();
		}
	};
	
	/** a listener added to the current {@link OrientationToRotationStrategy} represented by {@link #orientationConverter} */
	private OrientationToRotationStrategyListener orientationListener = new OrientationToRotationStrategyListener(){
		public void rotationChanged( Dockable dockable, DockTitle title ){
			if( title == AbstractMultiDockTitle.this || title == null ){
				if( dockable == AbstractMultiDockTitle.this.dockable || dockable == null ){
					updateLabelRotation();
				}
			}
		}
	};

    /**
     * Standard constructor
     * @param dockable The Dockable whose title this will be
     * @param origin The version which was used to create this title
     */
    public AbstractMultiDockTitle( Dockable dockable, DockTitleVersion origin ){
    	super( Transparency.SOLID );
        init( dockable, origin );
    }
    
    /**
     * Constructor which does not do anything. Subclasses should call
     * {@link #init(Dockable, DockTitleVersion)} to initialize
     * the title.
     */
    protected AbstractMultiDockTitle(){
       super( Transparency.SOLID ); 
    }
    
    /**
     * Initializer called by the constructor.
     * @param dockable The Dockable whose title this will be
     * @param origin The version which was used to create this title
     */
    protected void init( Dockable dockable, DockTitleVersion origin ){
        this.dockable = dockable;
        this.origin = origin;
        
        label.setBackground( background );
        setBackground( background );
        
        setLayout( null );
        add( label );
        setActive( false );
        
        setFocusTraversalPolicyProvider( true );
        setFocusTraversalPolicy( new ContainerOrderFocusTraversalPolicy(){
            @Override
            protected boolean accept( Component component ) {
                return component != AbstractMultiDockTitle.this && super.accept( component );
            }
        });
        setOpaque( false );
        
     	rootHandler = createRootHandler();
     	rootHandler.addRoot( getComponent() );
    }
    
    /**
     * Creates the {@link DockComponentRootHandler} which is responsible for informing the client about
     * the {@link Component}s that are shown on this title.
     * @return the new handler
     */
    protected DockComponentRootHandler createRootHandler(){
    	return new DockComponentRootHandler( this ) {
			protected TraverseResult shouldTraverse( Component component ) {
				return TraverseResult.INCLUDE_CHILDREN;
			}
		};
    }
    
    /**
     * Sets the number of pixels to paint between icon and text.
     * @param iconTextGap the number of pixels to paint
     */
    public void setIconTextGap( int iconTextGap ){
		this.iconTextGap = iconTextGap;
		revalidate();
	}
    
    /**
     * Gets the number of pixels to paint between icon and text
     * @return the number of pixels
     */
    public int getIconTextGap(){
		return iconTextGap;
	}
    
    /**
     * Adds a color to the list of colors, this title will ensure that 
     * <code>color</code> gets connected to a {@link ColorManager} as soon
     * as this title is bound.
     * @param color the new color
     */
    protected void addColor( AbstractDockColor color ){
        colors.add( color );
        if( bound ){
            color.connect( getDockable().getController() );
        }
    }
    
    /**
     * Removes a color from this title
     * @param color the color to remove
     */
    protected void removeColor( AbstractDockColor color ){
        colors.remove( color );
        color.connect( null );
    }
    
    /**
     * Adds a font to the list of fonts, this title will ensure that 
     * <code>font</code> gets connected to a {@link FontManager} as soon
     * as this title is bound.
     * @param font the new font
     */
    protected void addFont( AbstractDockFont font ){
        fonts.add( font );
        if( bound ){
            font.connect( getDockable().getController() );
        }
    }
    
    /**
     * Removes a font from this title.
     * @param font the font to remove
     */
    protected void removeFont( AbstractDockFont font ){
        fonts.remove( font );
        font.connect( null );
    }
    

    /**
     * Adds a new conditional font to this title, the conditional font will
     * be applied to {@link #setFont(Font)} when its <code>condition</code>
     * is met. If there is more than one font whose condition is met, then the
     * first one that was registered is used.
     * @param id the id of the font which is to be used
     * @param kind what kind of title this is
     * @param condition the condition to met
     * @param backup to be used when there is not font set in the {@link FontManager}
     */
    protected void addConditionalFont( String id, Path kind, Condition condition, FontModifier backup ){
        ConditionalFont font = new ConditionalFont( id, kind, condition, backup );
        addFont( font );
        if( conditionalFonts == null )
            conditionalFonts = new ArrayList<ConditionalFont>();
        conditionalFonts.add( font );
        updateFonts();
    }
    
    /**
     * Removes all fonts which were set using {@link #addConditionalFont(String, Path, Condition, FontModifier)}
     */
    protected void removeAllConditionalFonts(){
        if( conditionalFonts != null ){
            for( ConditionalFont font : conditionalFonts ){
                removeFont( font );
            }
            conditionalFonts = null;
            updateFonts();
        }
    }
    
    /**
     * Gets the location and the size of the icon.
     * @return the bounds or <code>null</code> if no icon is registered
     */
    public Rectangle getIconBounds(){
        if( icon == null )
            return null;
        
        Insets insets = titleInsets();
        if( orientation.isVertical() ){
            int width = getWidth() - insets.left - insets.right;
            return new Rectangle( insets.left + (width - icon.getIconWidth())/2, insets.top, icon.getIconWidth(), icon.getIconHeight() );
        }
        else{
            int height = getHeight() - insets.top - insets.bottom;
            return new Rectangle( insets.left, insets.top + (height - icon.getIconHeight()) / 2, icon.getIconWidth(), icon.getIconHeight() );
        }
    }

    /**
     * Tells this title whether it should be disabled or not. This method is called when the {@link DisablingStrategy}
     * changes. A disabled title should react to any {@link InputEvent}, and should be painted differently than an
     * enabled title.
     * @param disabled whether this title is disabled
     * @see #isDisabled()
     */
    protected void setDisabled( boolean disabled ){
    	if( this.disabled != disabled ){
	    	this.disabled = disabled;
	    	label.setEnabled( !disabled );
	    	setEnabled( !disabled );
	    	
	    	if( disabled ){
	    		for( MouseInputListener listener : mouseInputListeners ){
	    			doRemoveMouseInputListener( listener );
	    		}
	    	}
	    	else{
	    		for( MouseInputListener listener : mouseInputListeners ){
	    			doAddMouseInputListener( listener );
	    		}
	    	}
    	}
    }
    
    /**
     * Tells whether this title is disabled, a disabled title does not react to any user input.
     * @return whether the title is disabled
     * @see #setDisabled(boolean)
     */
    protected boolean isDisabled(){
    	return disabled;
    }
    
    @Override
    protected void setupRenderingHints( Graphics g ) {
    	Dockable dockable = getDockable();
    	if( dockable != null ){
    		DockController controller = dockable.getController();
    		if( controller != null ){
    			DockRenderingHints renderingHints = controller.getProperties().get( DockRenderingHints.RENDERING_HINTS );
    			renderingHints.setupGraphics( g );
    		}
    	}
    }
    
    @Override
    public void paintBackground( Graphics g ){
    	if( getTransparency() != Transparency.TRANSPARENT ){
    		paintBackground( g, this );
    	}
    }
    
    /**
     * Paints the whole background of this title. The default implementation
     * just fills the background with the background color of <code>component</code>.
     * @param g the graphics context used to paint
     * @param component the {@link Component} which represents this title
     */
    protected void paintBackground( Graphics g, JComponent component ){
        g.setColor( component.getBackground() );
        g.fillRect( 0, 0, component.getWidth(), component.getHeight() );
    }
    
    @Override
    public void paintForeground( Graphics g ){
    	paintForeground( g, this );
    }
    
    /**
     * Paints the whole foreground of this title. The default implementation only paints an icon.
     * @param g the graphics context to use
     * @param component the {@link Component} which represents this title
     */
    protected void paintForeground( Graphics g, JComponent component ){
    	paintIcon( g, component );
    }
    
    /**
     * Paints the icon of this title.
     * @param g the graphics context to use
     * @param component the {@link Component} which represents this title
     */
    protected void paintIcon( Graphics g, JComponent component ){
    	Icon icon = this.icon;
    	if( icon != null ){
    		if( isDisabled() ){
    			if( disabledIcon == null ){
    				disabledIcon = DockUtilities.disabledIcon( component, icon );
    			}
    			icon = disabledIcon;
    		}
    		if( icon != null ){
	    		Insets insets = titleInsets();
	            if( orientation.isVertical() ){
	                int width = getWidth() - insets.left - insets.right;
	                icon.paintIcon( this, g, insets.left + (width - icon.getIconWidth())/2, insets.top );
	            }
	            else{
	                int height = getHeight() - insets.top - insets.bottom;
	                icon.paintIcon( this, g, insets.left, insets.top + (height - icon.getIconHeight()) / 2 );
	            }
    		}
        }
    }
    
    /**
     * Sets the icon of this title. The icon is shown on the top or the left
     * edge.
     * @param icon the icon, can be <code>null</code>
     */
    protected void setIcon( Icon icon ){
        this.icon = icon;
        disabledIcon = null;
        revalidate();
        repaint();
    }
    
    /**
     * Gets the icon of this title.
     * @return the icon or <code>null</code>
     * @see #setIcon(Icon)
     */
    protected Icon getIcon(){
        return icon;
    }
    
    /**
     * Grants access to the {@link OrientedLabel} which paints the title text.
     * @return the label, never <code>null</code>
     */
    protected OrientedLabel getLabel(){
		return label;
	}
    
    /**
     * Sets the text of this title. The text either painted horizontally or
     * vertically.
     * @param text the text or null
     */
    protected void setText( String text ){
        label.setText( text );
        repaint();
    }
    
    /**
     * Gets the text which is shown on this title.
     * @return the text
     */
    protected String getText(){
        return label.getText();
    }
    
    /**
     * Sets the tooltip that will be shown on this title.
     * @param text the new tooltip, can be <code>null</code>
     */
    protected void setTooltip( String text ){
        setToolTipText( text );
        label.setToolTipText( text );
    }
    
    public void setOrientation( Orientation orientation ) {
        this.orientation = orientation;
        updateLabelRotation();
        revalidate();
    }
    
    private void updateLabelRotation(){
    	label.setRotation( orientationConverter.getValue().convert( getOrientation(), this ));
    }
    
    /**
     * Gets the current orientation.
     * @return the orientation
     * @see #setOrientation(bibliothek.gui.dock.title.DockTitle.Orientation)
     */
    public Orientation getOrientation() {
        return orientation;
    }
    
    public DockTitleVersion getOrigin() {
        return origin;
    }
    
    @Override
    public void setForeground( Color fg ) {
        super.setForeground( fg );
        if( label != null )
            label.setForeground( fg );
    }
    
    @Override
    public void setBackground( Color fg ) {
        super.setBackground( fg );
        
        if( label != null )
            label.setBackground( fg );
    }
    
    @Override
    public void setFont( Font font ) {
        super.setFont( font );
        
        if( label != null )
            label.setFont( font );
    }
    
    public void setFontModifier( FontModifier modifier ) {
        label.setFontModifier( modifier );
    }
    
    @Override
    public Dimension getMinimumSize() {
    	if( icon != null )
    		return new Dimension( icon.getIconWidth(), icon.getIconHeight() );
    	
    	Dimension preferred = getPreferredSize();
    	int min = Math.min( preferred.width, preferred.height );
    	return new Dimension( min, min );
    }
    
    /**
     * Sets the border and the {@link BorderModifier} that should be used by this title.
     * @param key the identifier of the modifier or <code>null</code>
     * @param border the default title, can be <code>null</code>
     */
    public void setBorder( String key, Border border ){
    	if( this.border != null ){
    		if( key == null || !this.border.id.equals( key )){
    			this.border.setController( null );
    			this.border = null;
    		}
    	}
    	if( this.border == null && key != null ){
    		this.border = new TitleBorder( key );
    		if( bound ){
    			this.border.setController( getOrigin().getController() );
    		}
    	}
    	if( this.border == null ){
    		setBorder( border );
    	}
    	else{
    		this.border.setBorder( border );
    	}
    }
    
    /**
     * Gets the insets that have to be applied between the border and the 
     * content (icon, text, actions) of this title. Subclasses may use this method to
     * create free space in which they can paint additional items.
     * @return the insets, not <code>null</code>
     */
    protected Insets getInnerInsets(){
        if( getOrientation().isHorizontal() )
            return DEFAULT_INSETS_HORIZONTAL;
        else
            return DEFAULT_INSETS_VERTICAL;
    }
    
    /**
     * Gets the {@link Insets} of this title, tells how much space is not to be covered
     * by children {@link Component}s.
     * @return the insets, never <code>null</code>
     */
    protected Insets titleInsets(){
        Insets insets = getInsets();
        
        if( insets == null ){
            return getInnerInsets();
        }
        else{
            insets = new Insets( insets.top, insets.left, insets.bottom, insets.right );
        }
        
        Insets inner = getInnerInsets();
        insets.top += inner.top;
        insets.bottom += inner.bottom;
        insets.left += inner.left;
        insets.right += inner.right;
        
        return insets;
    }
    
    @Override
    public void doLayout(){
        super.doLayout();
        doTitleLayout();
    }
    
    /**
     * Updates the layout (position and size of all children) of this title.
     */
    protected void doTitleLayout(){
        Insets insets = titleInsets();
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        if( orientation.isHorizontal() ){
            if( icon != null ){
                x += icon.getIconWidth() + iconTextGap;
                width -= icon.getIconWidth() + iconTextGap;
            }
            
            label.setBounds( x, y, width, height );
        }
        else{
            if( icon != null ){
                y += icon.getIconWidth() + iconTextGap;
                height -= icon.getIconWidth() + iconTextGap;
            }
            
            label.setBounds( x, y, width, height );
        }
    }

    public void addMouseInputListener( MouseInputListener listener ) {
    	mouseInputListeners.add( listener );
    	if( !isDisabled() ){
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
    	if( !isDisabled() ){
	        doRemoveMouseInputListener( listener );
    	}
    }
    
    private void doRemoveMouseInputListener( MouseInputListener listener ){
    	removeMouseListener( listener );
        removeMouseMotionListener( listener );
        label.removeMouseListener( listener );
        label.removeMouseMotionListener( listener );
    }

    public Point getPopupLocation( Point click, boolean popupTrigger ){
        if( popupTrigger )
            return click;
        
        Rectangle icon = getIconBounds();
        if( icon != null ){
            if( icon.contains( click )){
                if( getOrientation().isHorizontal() )
                    return new Point( icon.x, icon.y + icon.height );
                else
                    return new Point( icon.x + icon.width, icon.y );
            }
        }
        
        return null;
    }
    
    public Dockable getDockable() {
        return dockable;
    }
    
    public DockElement getElement() {
        return getDockable();
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

    /**
     * Sets whether this title should be painted as focused or not.
     * @param active <code>true</code> if the {@link Dockable} of this title
     * has the focus.
     */
    public void setActive( boolean active ) {
        this.active = active;
    }
    
    public void changed( DockTitleEvent event ) {
    	if( event instanceof ActivityDockTitleEvent ){
    		setActive( ((ActivityDockTitleEvent)event).isActive() );
    	}
    }
    
    public boolean isActive(){
        return active;
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension preferred;
        if( (getText() == null || getText().length() == 0 ) ){
            preferred = new Dimension( 5, 5 );
        }
        else{
            preferred = label.getPreferredSize();
        }
        
        Insets insets = titleInsets();

        if( orientation.isHorizontal() ){
            int width = 0;
            int height = 0;
            if( icon != null ){
                width = icon.getIconWidth();
                height = icon.getIconHeight();
            }
            
            height = Math.max( height, preferred.height );
            width += preferred.width;
            
            if( icon == null )
                width = Math.max( width, 2*height );
            
            preferred = new Dimension( width + iconTextGap + insets.left + insets.right,
                    height + insets.top + insets.bottom );
        }
        else{
            int width = 0;
            int height = 0;
            if( icon != null ){
                width = icon.getIconWidth();
                height = icon.getIconHeight();
            }
            
            
            width = Math.max( width, preferred.width );
            height += preferred.height;
            
            if( icon == null )
                height = Math.max( height, 2*width );
            
            preferred = new Dimension( width + insets.left + insets.right,
                    height + iconTextGap + insets.top + insets.bottom );
        }
        return preferred;
    }

    /**
     * Creates a new item for <code>action</code> which will be shown on this title.
     * @param action The action which will be triggered by the button
     * @param dockable The {@link Dockable} which will be affected by the action
     * @return the new graphical representation of the action 
     */
    protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
    	return dockable.getController().getActionViewConverter().createView( 
    			action, ViewTarget.TITLE, dockable );
    }
    
    public void bind() {        
        if( bound )
            throw new IllegalArgumentException( "Do not call bound twice!" );
        bound = true;
        
        DockController controller = getDockable().getController();
        
        dockable.addDockableListener( listener );
        
        if( controller != null ){
            for( AbstractDockColor color : colors )
                color.connect( controller );
            
            for( AbstractDockFont font : fonts )
                font.connect( controller );
            
            orientationConverter.setProperties( controller );
        }
        
        background.setController( controller );
        
        if( border != null ){
        	border.setController( controller );
        }
        
        updateText();
        updateIcon();
        updateTooltip();
        
        rootHandler.setController( controller );
        
        revalidate();
    }

    public void unbind() {
        if( !bound )
            throw new IllegalArgumentException( "Do not call unbind twice" );
        bound = false;
        
        dockable.removeDockableListener( listener );
        
        rootHandler.setController( null );
        
        for( AbstractDockColor color : colors )
            color.connect( null );
        
        for( AbstractDockFont font : fonts )
            font.connect( null );
        
        orientationConverter.setProperties( (DockProperties)null );
        if( border != null ){
        	border.setController( null );
        }
        
        setText( "" );
        setIcon( null );
        setTooltip( null );
        background.setController( null );
    }
    
    public void setComponentConfiguration( DockComponentConfiguration configuration ) {
    	rootHandler.setConfiguration( configuration );
    }
    
    public DockComponentConfiguration getComponentConfiguration() {
    	return rootHandler.getConfiguration();
    }
    
    /**
     * Called when the icon of this title should be updated. This title
     * never calls {@link #setIcon(Icon)} directly, it always calls this method
     * which then calls {@link #setIcon(Icon)} (the only exception: on
     * unbinding the icon is set to <code>null</code>)
     */
    protected void updateIcon(){
        setIcon( dockable.getTitleIcon() );
    }
    
    /**
     * Called when the text of this title should be updated. This title
     * never calls {@link #setText(String)} directly, it always calls this method
     * which then calls {@link #setText(String)} (the only exception: on
     * unbinding the text is set to <code>null</code>)
     */
    protected void updateText(){
        setText( dockable.getTitleText() );
    }
    
    /**
     * Called when the tooltip of this title should be updated. This
     * title never calls {@link #setTooltip(String)} directly, it always
     * calls this method which then calls {@link #setTooltip(String)} (the
     * only exception: on unbinding the tooltip is set to <code>null</code>)
     */
    protected void updateTooltip(){
        setTooltip( dockable.getTitleToolTip() );
    }
    
    /**
     * Tells whether this title is bound to a {@link Dockable} or not.
     * @return true if the title is {@link #bind() bound}, <code>false</code>
     * {@link #unbind() otherwise}
     */
    public boolean isBound(){
        return bound;
    }
    
    /**
     * Checks the state of this title and may replace the font of the title.
     */
    protected void updateFonts(){
        if( conditionalFonts != null ){
            FontModifier modifier = null;
            
            for( ConditionalFont font : conditionalFonts ){
                if( font.getState() ){
                    modifier = font.value();
                    break;
                }
            }
            
            setFontModifier( modifier );
        }
    }
    
    /**
     * A font that is only used when a condition is met.
     * @author Benjamin Sigg
     */
    private class ConditionalFont extends TitleFont{
        private Condition condition;
        
        public ConditionalFont( String id, Path kind, Condition condition, FontModifier backup ){
            super( id, AbstractMultiDockTitle.this, kind, backup );
            this.condition = condition;
        }
        
        /**
         * Gets whether the condition is met or not.
         * @return <code>true</code> if this font should be used
         */
        public boolean getState(){
            return condition.getState();
        }
        
        @Override
        protected void changed( FontModifier oldValue, FontModifier newValue ) {
            updateFonts();
        }
    }
    
    /**
     * A listener to the {@link Dockable} of this title.
     * @author Benjamin Sigg
     */
    private class Listener implements DockableListener, DockHierarchyListener{
        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            updateIcon();
            updateText();
        }
        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            updateIcon();
            updateText();
        }
        
        public void titleToolTipChanged( Dockable dockable, String oldTooltip, String newTooltip ) {
            updateTooltip();
        }
        
        public void titleUnbound( Dockable dockable, DockTitle title ) {
            // do nothing
        }
        
        public void titleBound( Dockable dockable, DockTitle title ) {
            // do nothing
        }
        
        public void titleExchanged( Dockable dockable, DockTitle title ) {
            // do nothing
        }
        
        public void controllerChanged( DockHierarchyEvent event ) {
            DockController controller = event.getDockable().getController();
            for( AbstractDockColor color : colors )
                color.connect( controller );
        }
        
        public void hierarchyChanged( DockHierarchyEvent event ) {
            // do nothing
        }
    }
    
    /** 
     * Represents the background of this {@link DockTitle}.
     * @author Benjamin Sigg
     */
    private class Background extends BackgroundAlgorithm implements DockTitleBackgroundComponent{
    	public Background(){
    		super( DockTitleBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".title" );
    	}
    	
    	public DockTitle getTitle(){
    		return AbstractMultiDockTitle.this;
    	}
    	
    	public Component getComponent(){
    		return getTitle().getComponent();
    	}
    }
    
    /**
     * Represents this title as {@link UIValue} to get a {@link BorderModifier}
     * @author Benjamin Sigg
     */
    private class TitleBorder implements TitleDockBorder{
    	/** the identifier of this border */
    	private String id;
    	/** the current modifier */
    	private BorderModifier modifier;
    	/** the source of all values */
    	private DockController controller;
    	/** the default border */
    	private Border border;
    	
    	/**
    	 * Creates a new wrapper
    	 * @param id the identifier of this {@link UIValue}
    	 */
    	public TitleBorder( String id ){
    		this.id = id;
    	}
    	
		public DockTitle getTitle(){
			return AbstractMultiDockTitle.this;
		}

		public void set( BorderModifier value ){
			if( value != modifier ){	
				modifier = value;
				update();
			}
		}
		
		/**
		 * Sets the default border
		 * @param border the default border, can be <code>null</code>
		 */
		public void setBorder( Border border ){
			if( this.border != border ){
				this.border = border;
				update();
			}
		}
		
		private void update(){
			if( modifier == null ){
				AbstractMultiDockTitle.this.setBorder( border );
			}
			else{
				AbstractMultiDockTitle.this.setBorder( modifier.modify( border ) );
			}
		}

		/**
		 * Sets the controller to observe for a value.
		 * @param controller the controller, can be <code>null</code>
		 */
		public void setController( DockController controller ){
			if( this.controller != null ){
				this.controller.getThemeManager().remove( this );
			}
			this.controller = controller;
			if( this.controller == null ){
				set( null );
			}
			else{
				this.controller.getThemeManager().add( id, TitleDockBorder.KIND, ThemeManager.BORDER_MODIFIER_TYPE, this );
			}
		}
    }
}
