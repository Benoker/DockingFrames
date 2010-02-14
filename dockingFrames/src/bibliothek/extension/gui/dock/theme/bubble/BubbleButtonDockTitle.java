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
import java.awt.Point;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.ButtonContent;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.util.Condition;

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
    
    "title.background.bottom.active.mouse.flap",
    "title.background.bottom.active.flap",
    "title.background.bottom.inactive.mouse.flap",
    "title.background.bottom.inactive.flap",
    "title.background.bottom.selected.mouse.flap",
    "title.background.bottom.selected.flap",
    
    "title.foreground.active.mouse.flap",
    "title.foreground.active.flap",
    "title.foreground.inactive.mouse.flap",
    "title.foreground.inactive.flap",
    "title.foreground.selected.mouse.flap",
    "title.foreground.selected.flap" })
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
	    	request.setAnswer( new BubbleButtonDockTitle( request.getTarget(), request.getVersion() ) );	
    	}
    };
    
    private ButtonContent behavior;
    
    private boolean selected = false;
    
    /**
     * Creates a new title.
     * @param dockable the dockable for which this title will be shown
     * @param origin the {@link DockTitleVersion} which was used to create this title
     */
    public BubbleButtonDockTitle( Dockable dockable, DockTitleVersion origin ) {
        behavior = FlapDockStation.ButtonContent.THEME_DEPENDENT;
        if( origin != null )
            behavior = origin.getController().getProperties().get( FlapDockStation.BUTTON_CONTENT );
        
        init( dockable, origin, behavior.showActions( false ) );
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

        addColor( "title.background.bottom.active.mouse.flap", path, Color.LIGHT_GRAY );
        addColor( "title.background.bottom.active.flap", path, Color.WHITE );
        addColor( "title.background.bottom.inactive.mouse.flap", path, Color.DARK_GRAY );
        addColor( "title.background.bottom.inactive.flap", path, Color.BLACK );
        addColor( "title.background.bottom.selected.mouse.flap", path, Color.DARK_GRAY );
        addColor( "title.background.bottom.selected.flap", path, Color.BLACK );

        addColor( "title.foreground.active.mouse.flap", path, Color.BLACK );
        addColor( "title.foreground.active.flap", path, Color.BLACK );
        addColor( "title.foreground.inactive.mouse.flap", path, Color.WHITE );
        addColor( "title.foreground.inactive.flap", path, Color.WHITE );
        addColor( "title.foreground.selected.mouse.flap", path, Color.WHITE );
        addColor( "title.foreground.selected.flap", path, Color.WHITE );
        
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
    public void changed( DockTitleEvent event ) {
        selected = event.isActive() || event.isPreferred();
        super.setActive( event.isActive() );
        updateAnimation();
        updateFonts();
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
        
        updateAnimation( ANIMATION_KEY_TEXT, text );
        updateAnimation( ANIMATION_KEY_BACKGROUND_TOP, top );
        updateAnimation( ANIMATION_KEY_BACKGROUND_BOTTOM, bottom );
    }
    
    /**
     * Tells whether this title is selected, being focused implies being
     * selected.
     * @return <code>true</code> if this button is selected
     */
    public boolean isSelected(){
        return selected;
    }
    
    @Override
    protected void updateIcon() {
        String text = getDockable().getTitleText();
        if( behavior.showIcon( text != null && text.length() > 0, true ) )
            super.updateIcon();
        else
            setIcon( null );
    }
    
    @Override
    protected void updateText() {
        if( behavior.showText( getDockable().getTitleIcon() != null, true ) )
            super.updateText();
        else
            setText( "" );     
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
