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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.util.Condition;
import bibliothek.util.Path;

/**
 * A <code>BubbleDockTitle</code> is a title which has two or four round edges.
 * The title can smoothly change its colors when touched by the mouse.
 * @author Benjamin Sigg
 */

@ColorCodes({ 
    "title.background.top.active.mouse",
    "title.background.top.active",
    "title.background.top.inactive.mouse",
    "title.background.top.inactive",
    "title.background.top.disabled",
    
    "title.background.bottom.active.mouse",
    "title.background.bottom.active",
    "title.background.bottom.inactive.mouse",
    "title.background.bottom.inactive",
    "title.background.bottom.disabled",
    
    "title.foreground.active.mouse",
    "title.foreground.active",
    "title.foreground.inactive.mouse",
    "title.foreground.inactive" })
public class BubbleDockTitle extends AbstractBubbleDockTitle {
	
    /**
     * Creates a new title.
     * @param dockable the {@link Dockable} for which this title is shown
     * @param origin the creator of this title
     */
    public BubbleDockTitle( Dockable dockable, DockTitleVersion origin ) {
        this( dockable, origin, true );
    }
    
    /**
     * Creates a new title.
     * @param dockable the {@link Dockable} for which this title is shown
     * @param origin the creator of this title
     * @param showMiniButtons whether this title should show the {@link bibliothek.gui.dock.action.DockAction actions} or not
     */
    public BubbleDockTitle( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
        init( dockable, origin, showMiniButtons );
    }
    
    /**
     * A constructor that does not do anything, subclasses should later call
     * {@link #init(Dockable, DockTitleVersion, boolean)}.
     */
    protected BubbleDockTitle(){
        
    }

    /**
     * Initializes this title, this method should be called only once.
     * @param dockable the {@link Dockable} for which this title is shown
     * @param origin the creator of this title
     * @param showMiniButtons whether this title should show the {@link bibliothek.gui.dock.action.DockAction actions} or not
     */
    @Override
    protected void init( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
        super.init( dockable, origin, showMiniButtons );
        initAnimation();
        updateAnimation();
    }
    
    /**
     * Sets up the animation such that it can be started at any time.
     */
    private void initAnimation(){
        Path path = TitleColor.KIND_TITLE_COLOR;
        
        addColor( "title.background.top.active.mouse", path, Color.RED );
        addColor( "title.background.top.active", path, Color.LIGHT_GRAY );
        addColor( "title.background.top.inactive.mouse", path, Color.BLUE );
        addColor( "title.background.top.inactive", path, Color.DARK_GRAY );
        addColor( "title.background.top.disabled", path, Color.DARK_GRAY );

        addColor( "title.background.bottom.active.mouse", path, Color.LIGHT_GRAY );
        addColor( "title.background.bottom.active", path, Color.WHITE );
        addColor( "title.background.bottom.inactive.mouse", path, Color.DARK_GRAY );
        addColor( "title.background.bottom.inactive", path, Color.BLACK );
        addColor( "title.background.bottom.disabled", path, Color.BLACK );

        addColor( "title.foreground.active.mouse", path, Color.BLACK );
        addColor( "title.foreground.active", path, Color.BLACK );
        addColor( "title.foreground.inactive.mouse", path, Color.WHITE );
        addColor( "title.foreground.inactive", path, Color.WHITE );
        
        addConditionalFont( DockFont.ID_TITLE_ACTIVE, TitleFont.KIND_TITLE_FONT, new Condition(){
            public boolean getState() {
                return isActive();
            }
        }, null );
        
        addConditionalFont( DockFont.ID_TITLE_INACTIVE, TitleFont.KIND_TITLE_FONT, new Condition(){
            public boolean getState() {
                return !isActive();
            }
        }, null );
    }
    
    @Override
    protected void updateAnimation(){
        updateFonts();
        
        String postfix = "";
        if( isDisabled() ){
            updateAnimation( ANIMATION_KEY_TEXT, "title.foreground.inactive" );
            updateAnimation( ANIMATION_KEY_BACKGROUND_TOP, "title.background.top.disabled" );
            updateAnimation( ANIMATION_KEY_BACKGROUND_BOTTOM, "title.background.bottom.disabled" );
        }
        else{
        	if( isActive() ){
	            if( isMouseOver() )
	                postfix = "active.mouse";
	            else
	                postfix = "active";
	        }
	        else{
	            if( isMouseOver() )
	                postfix = "inactive.mouse";
	            else
	                postfix = "inactive";            
	        }
	        
	        String top = "title.background.top." + postfix;
	        String bottom = "title.background.bottom." + postfix;
	        String text = "title.foreground." + postfix;
	        
	        updateAnimation( ANIMATION_KEY_TEXT, text );
	        updateAnimation( ANIMATION_KEY_BACKGROUND_TOP, top );
	        updateAnimation( ANIMATION_KEY_BACKGROUND_BOTTOM, bottom );
        }
    }
}
