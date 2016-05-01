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
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleEvent;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.util.Condition;

/**
 * A {@link DockTitle} normally used by the {@link DockTitleTab} to show
 * a very simple tab.
 * @author Benjamin Sigg
 *
 */
@ColorCodes({"stack.tab.top.selected.focused", "stack.tab.bottom.selected.focused", 
    "stack.tab.text", "stack.tab.top.selected", "stack.tab.bottom.selected",
    "stack.tab.top.disabled", "stack.tab.bottom.disabled", 
    "stack.tab.text", "stack.border"})
public class BasicTabDockTitle extends BasicDockTitle {
    /**
     * A factory creating new {@link BasicTabDockTitle}s.
     */
    public static final DockTitleFactory FACTORY = new DockTitleFactory(){
    	public void install( DockTitleRequest request ){
        	// ignore	
    	}
    	
    	public void uninstall( DockTitleRequest request ){
        	// ignore	
    	}
    	
    	public void request( DockTitleRequest request ){
    		request.answer( new BasicTabDockTitle( request.getTarget(), request.getVersion() ) );
    	}
    };
    
    /** whether this tab is currently selected */
    private boolean selected = false;
    
    /** whether to paint the icon when this tab is not selected */
    private boolean paintIconWhenInactive = true;
    
    private TitleColor borderColor;
    
    /**
     * Creates a new title
     * @param dockable the element for which this title is shown
     * @param origin the type of this title
     */
    public BasicTabDockTitle( Dockable dockable, DockTitleVersion origin ) {
        super( dockable, origin, false );
        
        setBorder( ThemeManager.BORDER_MODIFIER + ".title.tab", BorderFactory.createEmptyBorder( 0, 0, 1, 0 ) );
        
        setActiveLeftColorId( "stack.tab.top.selected.focused" );
        setActiveRightColorId( "stack.tab.bottom.selected.focused" );
        setActiveTextColorId( "stack.tab.text" );
        
        setInactiveLeftColorId( "stack.tab.top.selected" );
        setInactiveRightColorId( "stack.tab.bottom.selected" );
        setInactiveTextColorId( "stack.tab.text" );
        
        setDisabledLeftColorId( "stack.tab.top.disabled" );
        setDisabledRightColorId( "stack.tab.bottom.disabled" );
        
        borderColor = new TitleColor( "stack.border", this, Color.BLACK ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                repaint();
            }
        };
        
        addConditionalFont( DockFont.ID_TAB_FOCUSED, TitleFont.KIND_TAB_TITLE_FONT,
                new Condition(){
            public boolean getState() {
                return isActive();
            }
        }, null );
        
        addConditionalFont( DockFont.ID_TAB_SELECTED, TitleFont.KIND_TAB_TITLE_FONT,
                new Condition(){
            public boolean getState() {
                return selected;
            }
        }, null );
        
        addConditionalFont( DockFont.ID_TAB_UNSELECTED, TitleFont.KIND_TAB_TITLE_FONT,  
                new Condition(){
            public boolean getState() {
                return !isActive();
            }
        }, null );
    }
    
    @Override
    public void bind() {
        super.bind();
        
        DockController controller = getDockable().getController();
        ColorManager colors = controller.getColors();

        borderColor.setManager( colors );
    }
    
    @Override
    public void unbind() {
        super.unbind();

        borderColor.setManager( null );
    }
    
    @Override
    public Dimension getMinimumSize(){
	    return getPreferredSize();
    }
    
    @Override
    public void changed( DockTitleEvent event ) {
        super.changed( event );
        if( event instanceof EclipseDockTitleEvent ){
            EclipseDockTitleEvent e = (EclipseDockTitleEvent)event;
            selected = e.isSelected();
            paintIconWhenInactive = e.isPaintIconWhenInactive();
            updateTabIcon();
        }
    }
    
    @Override
    protected void setIcon( Icon icon ) {
        if( selected || paintIconWhenInactive )
            super.setIcon( icon );
        else
            super.setIcon( null );
    }
    
    /**
     * Ensures that the icon of the {@link #getDockable() Dockable} is
     * shown but only if this title is {@link #selected} or
     * {@link #paintIconWhenInactive} is <code>true</code>.
     */
    private void updateTabIcon(){
        if( selected || paintIconWhenInactive )
            setIcon( getDockable().getTitleIcon() );
        else
            setIcon( null );
    }
}
