package bibliothek.extension.gui.dock.theme.flat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.station.stack.CombinedTab;
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
 * A small button which can be clicked by the user.
 * @author Benjamin Sigg
 */
@ColorCodes({
    "stack.tab.border.out.selected", 
    "stack.tab.border.center.selected",
    "stack.tab.border.out.focused", 
    "stack.tab.border.center.focused",
    "stack.tab.border.out", 
    "stack.tab.border.center",
    "stack.tab.border.out.disabled", 
    "stack.tab.border.center.disabled", 
    "stack.tab.border", 
                
    "stack.tab.background.top.selected", 
    "stack.tab.background.bottom.selected",
    "stack.tab.background.top.focused", 
    "stack.tab.background.bottom.focused",
    "stack.tab.background.top", 
    "stack.tab.background.bottom",
    "stack.tab.background.top.disabled", 
    "stack.tab.background.bottom.disabled", 
    "stack.tab.background",
    
    "stack.tab.foreground.selected",
    "stack.tab.foreground.focused",
    "stack.tab.foreground.disabled",
    "stack.tab.foreground" })
public class FlatTab extends ConfiguredBackgroundPanel implements CombinedTab, DockableFocusListener{
	/** the dockable for which this button is shown */
    private Dockable dockable;
    
    /** the current controller */
    private DockController controller;
    
    /** the parent of this tab */
    private FlatTabPane pane;
    
    /** the label which paints the content of this tab */
    private OrientedLabel label = new OrientedLabel(){
    	@Override
    	protected void updateFonts(){
	    	FlatTab.this.updateFonts();
    	}
    };
    
    /** the algorithm painting the background of this tab */
    private Background backgroundAlgorithm = new Background();
    
    /** whether {@link #dockable} is currently focused */
    private boolean focused = false;
    
    private TabColor borderSelectedOut;
    private TabColor borderSelectedCenter;
    private TabColor borderFocusedOut;
    private TabColor borderFocusedCenter;
    private TabColor borderOut;
    private TabColor borderCenter;
    private TabColor borderDisabledOut;
    private TabColor borderDisabledCenter;
    private TabColor border;
    private TabColor backgroundSelectedTop;
    private TabColor backgroundSelectedBottom;
    private TabColor backgroundFocusedTop;
    private TabColor backgroundFocusedBottom;
    private TabColor backgroundTop;
    private TabColor backgroundBottom;
    private TabColor backgroundDisabledTop;
    private TabColor backgroundDisabledBottom;
    private TabColor background;
    private TabColor foreground;
    private TabColor foregroundSelected;
    private TabColor foregroundFocused;
    private TabColor foregroundDisabled;
    
    private TabFont fontFocused;
    private TabFont fontSelected;
    private TabFont fontUnselected;
    
    private int zOrder;
    
    private TabPlacement orientation = TabPlacement.BOTTOM_OF_DOCKABLE;

	/** a panel showing additional actions on this tab */
	private ButtonPanel actions = new ButtonPanel( false );
	
	/** the actions shown on {@link #actions} */
	private DockActionDistributorSource actionsSource;
	
	/** layout manager for {@link #label} and {@link #actions} */
	private TabComponentLayoutManager layoutManager;
	
	/** all the {@link MouseInputListener}s that were added to this tab */
	private List<MouseInputListener> mouseInputListeners = new ArrayList<MouseInputListener>();
	
    /**
     * Constructs a new button
     * @param pane the owner of this tab
     * @param dockable the Dockable for which this tab is displayed
     */
    public FlatTab( FlatTabPane pane, Dockable dockable ){
    	super( Transparency.SOLID );
    	this.pane = pane;
    	this.dockable = dockable;
    	
    	add( label );
    	add( actions );
    	layoutManager = new TabComponentLayoutManager( label, actions, pane.getConfiguration( dockable ) );
    	layoutManager.setFreeSpaceToSideBorder( 2 );
    	layoutManager.setFreeSpaceToParallelBorder( 2 );
    	layoutManager.setFreeSpaceToOpenSide( 2 );
    	layoutManager.setFreeSpaceBetweenLabelAndActions( 2 );
    	setLayout( layoutManager );
    	
    	label.setBackground( backgroundAlgorithm );
    	setBackground( backgroundAlgorithm );
    	
        borderSelectedOut    = new FlatTabColor( "stack.tab.border.out.selected", dockable );
        borderSelectedCenter = new FlatTabColor( "stack.tab.border.center.selected", dockable );
        borderFocusedOut    = new FlatTabColor( "stack.tab.border.out.focused", dockable );
        borderFocusedCenter = new FlatTabColor( "stack.tab.border.center.focused", dockable );
        borderOut            = new FlatTabColor( "stack.tab.border.out", dockable );
        borderCenter         = new FlatTabColor( "stack.tab.border.center", dockable );
        borderDisabledOut    = new FlatTabColor( "stack.tab.border.out.disabled", dockable );
        borderDisabledCenter = new FlatTabColor( "stack.tab.border.center.disabled", dockable );
        border               = new FlatTabColor( "stack.tab.border", dockable );
        
        backgroundSelectedTop    = new FlatTabColor( "stack.tab.background.top.selected", dockable );
        backgroundSelectedBottom = new FlatTabColor( "stack.tab.background.bottom.selected", dockable );
        backgroundFocusedTop    = new FlatTabColor( "stack.tab.background.top.focused", dockable );
        backgroundFocusedBottom = new FlatTabColor( "stack.tab.background.bottom.focused", dockable );
        backgroundTop            = new FlatTabColor( "stack.tab.background.top", dockable );
        backgroundBottom         = new FlatTabColor( "stack.tab.background.bottom", dockable );
        backgroundDisabledTop    = new FlatTabColor( "stack.tab.background.top.disabled", dockable );
        backgroundDisabledBottom = new FlatTabColor( "stack.tab.background.bottom.disabled", dockable );
        background               = new FlatTabColor( "stack.tab.background", dockable );
        
        foreground = new FlatTabColor( "stack.tab.foreground", dockable ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                if( !isSelected() && isEnabled() )
                    setForeground( newColor );
            }
        };
        foregroundSelected = new FlatTabColor( "stack.tab.foreground.selected", dockable ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                if( isSelected() && !focused && isEnabled() ){
                    setForeground( newColor );
                }
            }
        };
        foregroundFocused = new FlatTabColor( "stack.tab.foreground.focused", dockable ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                if( focused && isEnabled() ){
                    setForeground( newColor );
                }
            }
        };
        foregroundDisabled = new FlatTabColor( "stack.tab.foreground.disabled", dockable ){
        	@Override
        	protected void changed( Color oldColor, Color newColor ){
        		if( !isEnabled() ){
        			setForeground( newColor );
        		}
        	}
        };
        
        fontFocused = new FlatTabFont( DockFont.ID_TAB_FOCUSED, dockable );
        fontSelected = new FlatTabFont( DockFont.ID_TAB_SELECTED, dockable );
        fontUnselected = new FlatTabFont( DockFont.ID_TAB_UNSELECTED, dockable );
        
        setController( pane.getController() );
//        setOpaque( false );
        setFocusable( true );
        
        addMouseListener( new MouseAdapter(){
            @Override
            public void mousePressed( MouseEvent e ){
                FlatTab.this.pane.setSelectedDockable( FlatTab.this.dockable );
            }
        });
        
        setBorder( new Border(){
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){
                Graphics2D g2 = (Graphics2D)g;
                Paint oldPaint = g2.getPaint();
                
                Color out = null;
                Color center = null;

                if( !isEnabled() ){
                	out = borderDisabledOut.value();
                	center = borderDisabledCenter.value();
                }
                else if( focused ){
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
                        background = FlatTab.this.background.value();
                    
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
                
                if( orientation.isHorizontal() ){
	                g2.setPaint( new GradientPaint( x, y, out, x, y+h/2, center ));
	                g.drawLine( x, y, x, y+h/2 );
	                g.drawLine( x+w-1, y, x+w-1, y+h/2 );
	                
	                g2.setPaint( new GradientPaint( x, y+h, out, x, y+h/2, center ));
	                g.drawLine( x, y+h, x, y+h/2 );
	                g.drawLine( x+w-1, y+h, x+w-1, y+h/2 );
                }
                else{
                	g2.setPaint( new GradientPaint( x, y, out, x+w/2, y, center ));
	                g.drawLine( x, y, x+w/2, y );
	                g.drawLine( x, y+h-1, x+w/2, y+h-1 );
	                
	                g2.setPaint( new GradientPaint( x+w, y, out, x+w/2, y, center ));
	                g.drawLine( x+w, y, x+w/2, y );
	                g.drawLine( x+w, y+h-1, x+w/2, y+h-1 );
                }
                
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
    
    public void setConfiguration( TabConfiguration configuration ){
	    layoutManager.setConfiguration( configuration );	
    }
    
    public void updateForeground(){
    	if( !isEnabled() ){
    		setForeground( foregroundDisabled.value() );
    	}
    	else if( focused ){
            setForeground( foregroundFocused.value() );
        }
        else if( isSelected() ){
            setForeground( foregroundSelected.value() );
        }
        else{
            setForeground( foreground.value() );
        }
    }
    
    @Override
    public void setForeground( Color fg ) {
    	super.setForeground( fg );
    	if( label != null ){
    		label.setForeground( fg );
    	}
    }
    
    public void updateFonts(){
        if( focused ){
            setFontModifier( fontFocused.font() );
        }
        else if( isSelected() ){
            setFontModifier( fontSelected.font() );
        }
        else{
            setFontModifier( fontUnselected.font() );
        }
    }
    
    /**
     * Sets the modifier which modifies the font of this tab, this modifier
     * may be replaced any time.
     * @param modifier the modifier
     */
    public void setFontModifier( FontModifier modifier ){
    	label.setFontModifier( modifier );
    }
    
    /**
     * Gets the font modifier of this tab.
     * @return the modifier
     */
    public FontModifier getFontModifier(){
    	return label.getFontModifier();
    }
    
    public void setIcon( Icon icon ){
	    label.setIcon( icon );	
    }
    
    /**
     * Gets the icon shown on this tab.
     * @return the icon
     */
    public Icon getIcon(){
    	return label.getIcon();
    }
    
    public void setText( String text ){
	    label.setText( text );	
    }
    
    /**
     * Gets the text shown on this tab.
     * @return the text
     */
    public String getText(){
    	return label.getText();
    }
    
    /**
     * Connects this tab with <code>controller</code>.
     * @param controller the controller in whose realm this tab is used,
     * can be <code>null</code>
     */
    public void setController( DockController controller ){
    	if( this.controller != null )
            this.controller.removeDockableFocusListener( this );
        this.controller = controller;
    	
        actions.setController( controller );
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
				actionsSource = new DockActionDistributorSource( Target.TAB, FlatTheme.ACTION_DISTRIBUTOR );
				actionsSource.setDockable( getDockable() );
				actions.set( getDockable(), actionsSource );
			}
		}
		
        borderSelectedOut.connect( controller );
        borderSelectedCenter.connect( controller );
        borderFocusedOut.connect( controller );
        borderFocusedCenter.connect( controller );
        borderOut.connect( controller );
        borderCenter.connect( controller );
        borderDisabledOut.connect( controller );
        borderDisabledCenter.connect( controller );
        border.connect( controller );
        
        backgroundSelectedTop.connect( controller );
        backgroundSelectedBottom.connect( controller );
        backgroundFocusedTop.connect( controller );
        backgroundFocusedBottom.connect( controller );
        backgroundTop.connect( controller );
        backgroundBottom.connect( controller );
        backgroundDisabledTop.connect( controller );
        backgroundDisabledBottom.connect( controller );
        background.connect( controller );
        
        foregroundSelected.connect( controller );
        foregroundFocused.connect( controller );
        foregroundDisabled.connect( controller );
        foreground.connect( controller );
        
        fontFocused.connect( controller );
        fontSelected.connect( controller );
        fontUnselected.connect( controller );
        
        backgroundAlgorithm.setController( controller );
    }
    
    public Point getPopupLocation( Point click, boolean popupTrigger ) {
        if( popupTrigger )
            return click;
        
        return null;
    }
    
    public void dockableFocused( DockableFocusEvent event ) {
        focused = this.dockable == event.getNewFocusOwner();
        updateForeground();
        updateFonts();
        repaint();
    }
    
    public TabPane getTabParent(){
    	return pane;
    }
    
    public Dockable getDockable(){
    	return dockable;
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
    
    @Override
    public void setEnabled( boolean enabled ){
    	if( isEnabled() != enabled ){
    		label.setEnabled( enabled );
    		super.setEnabled( enabled );
    		if( enabled ){
    			for( MouseInputListener listener : mouseInputListeners ){
    				doAddMouseInputListener( listener );
    			}
    		}
    		else{
    			for( MouseInputListener listener : mouseInputListeners ){
    				doRemoveMouseInputListener( listener );
    			}
    		}
    		updateFonts();
    		updateForeground();
    		repaint();
    	}
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
    
    public Dimension getPreferredSize( Tab[] tabs ){
	    return getPreferredSize();
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
    
    public Dimension getMinimumSize( Tab[] tabs ){
	    return getMinimumSize();
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
    
    public void setPaneVisible( boolean visible ){
    	pane.getTabHandler().setVisible( this, visible );
    }
    
    public boolean isPaneVisible(){
    	return pane.getTabHandler().isVisible( this );
    }
    
    /**
     * Determines whether this button is selected or not.
     * @return <code>true</code> if the button is selected
     */
    public boolean isSelected() {
        return pane.getSelectedDockable() == dockable;
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
    
    public void setOrientation( TabPlacement orientation ){
    	if( orientation == null )
    		throw new IllegalArgumentException( "orientation is null" );
    	
    	if( this.orientation != orientation ){
	    	this.orientation = orientation;
	    	
	    	layoutManager.setOrientation( orientation );
	    	
	    	revalidate();
	    	repaint();
    	}
    }
    
    /**
     * Gets the orientation of this tab.
     * @return the orientation, not <code>null</code>
     */
    public TabPlacement getOrientation(){
		return orientation;
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
	    	super.paintBackground( g );
	    	
	        Graphics2D g2 = (Graphics2D)g;
	        Paint oldPaint = g2.getPaint();
	        
	        int w = getWidth();
	        int h = getHeight();
	        
	        Color top = null;
	        Color bottom = null;
	        
	        if( !isEnabled() ){
	        	top = backgroundDisabledTop.value();
	        	bottom = backgroundFocusedBottom.value();
	        }
	        else if( focused ){
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
	            Color background = FlatTab.this.background.value();
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
	        	if( orientation.isHorizontal() )
	        		g2.setPaint( new GradientPaint( 0, 0, top, 0, h, bottom ) );
	        	else
	        		g2.setPaint( new GradientPaint( 0, 0, top, w, 0, bottom ) );
	        }
	        
	        g.fillRect( 0, 0, w, h );
	        
	        g2.setPaint( oldPaint );
    	}
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
            super( id, pane.getStation(), dockable, null );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            repaint();
        }
    }
    
    /**
     * A font of this tab.
     * @author Benjamin Sigg
     */
    private class FlatTabFont extends TabFont{
        /**
         * Creates a new font
         * @param id the identifier of the font
         * @param dockable the element for which the font is used
         */
        public FlatTabFont( String id, Dockable dockable ){
            super( id, pane.getStation(), dockable );
        }
        
        @Override
        protected void changed( FontModifier oldValue, FontModifier newValue ) {
            updateFonts();
        }
    }
    
    /**
     * The background algorithm of this tab.
     * @author Benjamin Sigg
     */
    private class Background extends BackgroundAlgorithm implements TabPaneTabBackgroundComponent{
    	public Background(){
    		super( TabPaneTabBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".tabPane.child.tab" );
    	}

		public Tab getTab(){
			return FlatTab.this;
		}

		public TabPaneComponent getChild(){
			return FlatTab.this;
		}

		public TabPane getPane(){
			return FlatTab.this.getTabParent();
		}

		public Component getComponent(){
			return FlatTab.this;
		}
    }
}
