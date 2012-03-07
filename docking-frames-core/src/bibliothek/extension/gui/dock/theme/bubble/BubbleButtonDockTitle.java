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

import static bibliothek.gui.dock.station.flap.button.ButtonContent.IF_DOCKABLE;
import static bibliothek.gui.dock.station.flap.button.ButtonContent.IF_STATION;
import static bibliothek.gui.dock.station.flap.button.ButtonContent.TRUE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.FilteredDockActionSource;
import bibliothek.gui.dock.action.MultiDockActionSource;
import bibliothek.gui.dock.action.StationChildrenActionSource;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.station.flap.button.ButtonContentFilter;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonContentValue;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleEvent;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.util.Condition;
import bibliothek.util.Path;

/**
 * A {@link DockTitle} used for the buttons on a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
@ColorCodes({ 
    "title.background.top.active.mouse.flap",
    "title.background.top.active.flap",
    "title.background.top.inactive.mouse.flap",
    "title.background.top.inactive.flap",
    "title.background.top.selected.mouse.flap",
    "title.background.top.selected.flap",
    "title.background.top.disabled.flap",
    
    "title.background.bottom.active.mouse.flap",
    "title.background.bottom.active.flap",
    "title.background.bottom.inactive.mouse.flap",
    "title.background.bottom.inactive.flap",
    "title.background.bottom.selected.mouse.flap",
    "title.background.bottom.selected.flap",
    "title.background.bottom.disabled.flap",
    
    "title.foreground.active.mouse.flap",
    "title.foreground.active.flap",
    "title.foreground.inactive.mouse.flap",
    "title.foreground.inactive.flap",
    "title.foreground.selected.mouse.flap",
    "title.foreground.selected.flap",
    
	"title.flap.active.knob.highlight",
	"title.flap.active.knob.shadow",
	"title.flap.active.mouse.knob.highlight",
	"title.flap.active.mouse.knob.shadow",
	"title.flap.inactive.knob.highlight",
	"title.flap.inactive.knob.shadow",
	"title.flap.inactive.mouse.knob.highlight",
	"title.flap.inactive.mouse.knob.shadow",
	"title.flap.selected.knob.highlight",
	"title.flap.selected.knob.shadow",
	"title.flap.selected.mouse.knob.highlight",
	"title.flap.selected.mouse.knob.shadow",
	"title.flap.disabled.knob.highlight",
	"title.flap.disabled.knob.shadow"
})
public class BubbleButtonDockTitle extends AbstractBubbleDockTitle{
    /**
     * A factory which creates new {@link BubbleButtonDockTitle}s.
     */
    public static final DockTitleFactory FACTORY = new DockTitleFactory(){
    	public void install( DockTitleRequest request ){
    		// ignore
    	}
    	
    	public void uninstall( DockTitleRequest request ){
    		// ignore
    	}
    	
    	public void request( DockTitleRequest request ){
	    	request.answer( new BubbleButtonDockTitle( request.getTarget(), request.getVersion() ) );	
    	}
    };
    
	/** amount of space required to paint the knob */
	private final int KNOB_SIZE = 10;
    
	/** key for the color that is used to paint the knob */
	public static final String ANIMATION_KEY_KNOB_HIGHLIGHT = "knob.highlight";
	
	/** key for the color that is used to paint the knob */
	public static final String ANIMATION_KEY_KNOB_SHADOW = "knob.shadow";
	
    private ButtonContentValue behavior;

	/** tells what items to filter */
	private PropertyValue<ButtonContentFilter> connector = new PropertyValue<ButtonContentFilter>( FlapDockStation.BUTTON_CONTENT_FILTER ) {
		protected void valueChanged( ButtonContentFilter oldValue, ButtonContentFilter newValue ){
			if( behavior != null ){
				updateActionSource( true );
			}
		}
	};
	
    private boolean selected = false;
    
    /** keeps all the {@link DockActionSource}s that have to be shown on this title */
	private MultiDockActionSource allActionsSource = new MultiDockActionSource();
	
	/** whether children are currently shown */
	private boolean showChildren = false;
	
	/** wether actions are currently shown */
	private boolean showActions = false;

	/** whether all actions should be painted or only a selection */
	private boolean filterActions = false;
	
    /**
     * Creates a new title.
     * @param dockable the dockable for which this title will be shown
     * @param origin the {@link DockTitleVersion} which was used to create this title
     */
    public BubbleButtonDockTitle( Dockable dockable, DockTitleVersion origin ) {
    	if( origin != null ){
            connector.setProperties( origin.getController() );
        }
    	
        behavior = new ButtonContentValue( new ButtonContent( TRUE, TRUE, IF_DOCKABLE, IF_STATION, TRUE, TRUE ) ){
			@Override
			protected void propertyChanged(){
				updateContent();
			}
		};
    	
        init( dockable, origin, false );
        allActionsSource.setSeparateSources( true );
        updateContent();
    }
    
    /**
     * Constructor that does nothing, subclasses should call {@link #init(Dockable, DockTitleVersion, boolean)}
     * to initialize the tile.
     */
    protected BubbleButtonDockTitle(){
        // nothing
    }
    
    @Override
    protected void init( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ) {
        super.init( dockable, origin, showMiniButtons );
        initAnimation();
        updateAnimation();
        updateFonts();
    }
    
    /**
     * Sets up the animation such that it can be started at any time.
     */
    private void initAnimation(){
        Path path = TitleColor.KIND_FLAP_BUTTON_COLOR;
        
        addColor( "title.background.top.active.mouse.flap", path, Color.RED );
        addColor( "title.background.top.active.flap", path, Color.LIGHT_GRAY );
        addColor( "title.background.top.inactive.mouse.flap", path, Color.BLUE );
        addColor( "title.background.top.inactive.flap", path, Color.DARK_GRAY );
        addColor( "title.background.top.selected.mouse.flap", path, Color.BLUE );
        addColor( "title.background.top.selected.flap", path, Color.DARK_GRAY );
        addColor( "title.background.top.disabled.flap", path, Color.DARK_GRAY );

        addColor( "title.background.bottom.active.mouse.flap", path, Color.LIGHT_GRAY );
        addColor( "title.background.bottom.active.flap", path, Color.WHITE );
        addColor( "title.background.bottom.inactive.mouse.flap", path, Color.DARK_GRAY );
        addColor( "title.background.bottom.inactive.flap", path, Color.BLACK );
        addColor( "title.background.bottom.selected.mouse.flap", path, Color.DARK_GRAY );
        addColor( "title.background.bottom.selected.flap", path, Color.BLACK );
        addColor( "title.background.bottom.disabled.flap", path, Color.BLACK );

        addColor( "title.foreground.active.mouse.flap", path, Color.BLACK );
        addColor( "title.foreground.active.flap", path, Color.BLACK );
        addColor( "title.foreground.inactive.mouse.flap", path, Color.WHITE );
        addColor( "title.foreground.inactive.flap", path, Color.WHITE );
        addColor( "title.foreground.selected.mouse.flap", path, Color.WHITE );
        addColor( "title.foreground.selected.flap", path, Color.WHITE );
        
        addColor( "title.flap.active.knob.highlight", path, Color.WHITE );
        addColor( "title.flap.active.knob.shadow", path, Color.BLACK );
        addColor( "title.flap.active.mouse.knob.highlight", path, Color.WHITE );
        addColor( "title.flap.active.mouse.knob.shadow", path, Color.BLACK );
        addColor( "title.flap.inactive.knob.highlight", path, Color.WHITE );
        addColor( "title.flap.inactive.knob.shadow", path, Color.BLACK );
        addColor( "title.flap.inactive.mouse.knob.highlight", path, Color.WHITE );
        addColor( "title.flap.inactive.mouse.knob.shadow", path, Color.BLACK );
        addColor( "title.flap.selected.knob.highlight", path, Color.WHITE );
        addColor( "title.flap.selected.knob.shadow", path, Color.BLACK );
        addColor( "title.flap.selected.mouse.knob.highlight", path, Color.WHITE );
        addColor( "title.flap.selected.mouse.knob.shadow", path, Color.BLACK );
        addColor( "title.flap.disabled.knob.highlight", path, Color.WHITE );
        addColor( "title.flap.disabled.knob.shadow", path, Color.BLACK );
        
        addConditionalFont( DockFont.ID_FLAP_BUTTON_ACTIVE, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return isActive();
            }
        }, null );

        addConditionalFont( DockFont.ID_FLAP_BUTTON_SELECTED, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return isSelected();
            }
        }, null );

        addConditionalFont( DockFont.ID_FLAP_BUTTON_INACTIVE, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return !isActive();
            }
        }, null );
    }

    @Override
    public void bind(){
    	DockTitleVersion origin = getOrigin();
    	if( origin != null ){
        	behavior.setProperties( origin.getController() );
        }
    	behavior.setDockable( getDockable() );
        
    	super.bind();
    }
    
    @Override
    public void unbind(){
    	behavior.setProperties( (DockController)null );
    	behavior.setDockable( null );
    	super.unbind();
    }
    
    @Override
    public void changed( DockTitleEvent event ) {
    	if( event instanceof ActivityDockTitleEvent ){
    		ActivityDockTitleEvent activity = (ActivityDockTitleEvent)event;
    		
	        selected = activity.isActive() || activity.isPreferred();
	        super.setActive( activity.isActive() );
	        updateAnimation();
	        updateFonts();
    	}
    	else{
    		super.changed( event );
    	}
    }
    
    @Override
    public void setActive( boolean active ) {
        if( active != isActive() ){
            super.setActive(active);
            selected = active;
            updateAnimation();
            updateFonts();
        }
    }
    
    
    @Override
    protected void updateAnimation(){
    	if( isDisabled() ){
    		String top = "title.background.top.disabled.flap";
	        String bottom = "title.background.bottom.disabled.flap";
	        String text = "title.foreground.inactive.flap";
	        String knobHighlight = "title.flap.disabled.knob.highlight";
	        String knobShadow = "title.flap.disabled.knob.shadow";
	        
	        updateAnimation( ANIMATION_KEY_TEXT, text );
	        updateAnimation( ANIMATION_KEY_BACKGROUND_TOP, top );
	        updateAnimation( ANIMATION_KEY_BACKGROUND_BOTTOM, bottom );
	        updateAnimation( ANIMATION_KEY_KNOB_HIGHLIGHT, knobHighlight );
	        updateAnimation( ANIMATION_KEY_KNOB_SHADOW, knobShadow );
    	}
    	else{
	        String postfix = "";
	        if( isActive() ){
	            if( isMouseOver() )
	                postfix = "active.mouse";
	            else
	                postfix = "active";
	        }
	        else if( isSelected() ){
	            if( isMouseOver() )
	                postfix = "selected.mouse";
	            else
	                postfix = "selected";
	        }
	        else{
	            if( isMouseOver() )
	                postfix = "inactive.mouse";
	            else
	                postfix = "inactive";            
	        }
	        
	        String top = "title.background.top." + postfix + ".flap";
	        String bottom = "title.background.bottom." + postfix + ".flap";
	        String text = "title.foreground." + postfix + ".flap";
	        String knobHighlight = "title.flap." + postfix + ".knob.highlight";
	        String knobShadow = "title.flap." + postfix + ".knob.shadow";
	        
	        updateAnimation( ANIMATION_KEY_TEXT, text );
	        updateAnimation( ANIMATION_KEY_BACKGROUND_TOP, top );
	        updateAnimation( ANIMATION_KEY_BACKGROUND_BOTTOM, bottom );
	        updateAnimation( ANIMATION_KEY_KNOB_HIGHLIGHT, knobHighlight );
	        updateAnimation( ANIMATION_KEY_KNOB_SHADOW, knobShadow );
    	}
    }
    
    /**
     * Tells whether this title is selected, being focused implies being
     * selected.
     * @return <code>true</code> if this button is selected
     */
    public boolean isSelected(){
        return selected;
    }
    
    private void updateContent(){
    	updateIcon();
    	updateText();
    	updateActionSource( false );
    	
    	if( behavior.isShowActions() || behavior.isShowChildren() ){
    		setShowMiniButtons( true );
    	}
    	else{
    		setShowMiniButtons( false );
    	}
    	
    	revalidate();
    	repaint();
    }
    
    @Override
    protected void updateIcon() {
        if( behavior.isShowIcon() )
            super.updateIcon();
        else
            setIcon( null );
    }
    
    @Override
    protected void updateText() {
        if( behavior.isShowText() )
            super.updateText();
        else
            setText( "" );     
    }
    
    @Override
    protected DockActionSource getActionSourceFor( Dockable dockable ){
    	return allActionsSource;
    }
    
    private void updateActionSource( boolean force ){
    	boolean showChildren = behavior.isShowChildren();
    	boolean showActions = behavior.isShowActions();
    	boolean filterActions = behavior.isFilterActions();
    		
    	if( force || this.showChildren != showChildren || this.showActions != showActions || this.filterActions != filterActions ){
    		allActionsSource.removeAll();
    		
	    	if( showChildren ){
	    		allActionsSource.add( getChildrenActionSourceFor( getDockable() ) );
	    	}
    	
	    	if( showActions ){
	    		if( filterActions ){
	    			allActionsSource.add( createFilter( getDefaultActionSourceFor( getDockable() ) ) );
	    		}
	    		else{
	    			allActionsSource.add( getDefaultActionSourceFor( getDockable() ) );
	    		}
	    	}
	    	
	    	this.showChildren = showChildren;
	    	this.showActions = showActions;
    	}
    }
    
    /**
     * Creates a filter around <code>actions</code>, only the actions going through the filter
     * will be shown.
     * @param actions the actions to filter
     * @return the filter
     */
    protected DockActionSource createFilter( DockActionSource actions ){
    	final ButtonContentFilter connector = this.connector.getValue();
    	
    	return new FilteredDockActionSource( actions ){
			protected boolean include( DockAction action ){
				return connector.isButtonAction( action );
			}
		};
    }
    
    /**
     * Gets the "normal" actions for <code>dockable</code>.
     * @param dockable some item for which actions are required
     * @return the normal actions, may be a new {@link DockActionSource}, not <code>null</code>
     */
    protected DockActionSource getDefaultActionSourceFor( Dockable dockable ){
    	return super.getActionSourceFor( dockable );
    }
    
    /**
     * Gets the "special" children actions for <code>dockable</code>
     * @param dockable some item for which actions are required
     * @return the children actions, may be a new {@link DockActionSource}, not <code>null</code>
     */
    protected DockActionSource getChildrenActionSourceFor( Dockable dockable ){
    	return new StationChildrenActionSource( dockable, null );
    }
    
    @Override
    protected Insets getInnerInsets(){
    	Insets base = super.getInnerInsets();
    	
    	if( behavior.isShowKnob() ){
    		if( getOrientation().isHorizontal() ){
    			base = new Insets( base.top, base.left + KNOB_SIZE, base.bottom, base.right );
    		}
    		else{
    			base = new Insets( base.top + KNOB_SIZE, base.left, base.bottom, base.right );
    		}
    	}
    	
    	return base;
    }
    
    @Override
    protected void paintForeground( Graphics g, JComponent component ){
    	// paint icon etc.
    	super.paintForeground( g, component );
    	
    	// paint knob
    	if( behavior.isShowKnob() ){
    		Insets insets = getInnerInsets();
    		
    		if( getOrientation().isHorizontal() ){
    			int x = insets.left - KNOB_SIZE + 3;
    			int y1 = insets.top + 3;
    			int y2 = getHeight() - insets.bottom - 4;
    			
    			g.setColor( getColor( ANIMATION_KEY_KNOB_HIGHLIGHT ) );
    			g.drawLine( x, y1+1, x, y2-1 );
    			g.drawLine( x+1, y1, x+1, y1 );
    			
    			g.setColor( getColor( ANIMATION_KEY_KNOB_SHADOW ) );
    			g.drawLine( x+1, y2, x+1, y2 );
    			g.drawLine( x+2, y1+1, x+2, y2-1 );
    		}
    		else{
    			int y = insets.top - KNOB_SIZE + 3;
    			int x1 = insets.left + 3;
    			int x2 = getWidth() - insets.right - 4;
    			
    			g.setColor( getColor( ANIMATION_KEY_KNOB_HIGHLIGHT ) );
    			g.drawLine( x1+1, y, x2-1, y );
    			g.drawLine( x1, y+1, x1, y+1 );
    			
    			g.setColor( getColor( ANIMATION_KEY_KNOB_SHADOW ) );
    			g.drawLine( x1+1, y+2, x2-1, y+2 );
    			g.drawLine( x2, y+1, x2, y+2 );
    		}
    	}
    }
    
    @Override
    public Point getPopupLocation( Point click, boolean popupTrigger ) {
        if( popupTrigger )
            return click;
        
        return null;
    }
    
    @Override
    public void setOrientation( Orientation orientation ) {
        switch( orientation ){
            case SOUTH_SIDED:
            case NORTH_SIDED:
            case FREE_HORIZONTAL:
                orientation = Orientation.FREE_HORIZONTAL;
                break;
            case EAST_SIDED:
            case WEST_SIDED:
            case FREE_VERTICAL:
                orientation = Orientation.FREE_VERTICAL;
                break;
        }
        
        super.setOrientation( orientation );
    }
}
