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
package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.color.ColorManager;

/**
 * A {@link DockTitle} normally used by the {@link DockTitleTab} to show
 * a very simple tab.
 * @author Benjamin Sigg
 *
 */
@ColorCodes({"stack.tab.top.selected.focused", "stack.tab.bottom.selected.focused", 
    "stack.tab.text", "stack.tab.top.selected", "stack.tab.bottom.selected",
    "stack.tab.text", "stack.border"})
public class BasicTabDockTitle extends BasicDockTitle {
    /**
     * Gets a new {@link DockTitleFactory} using <code>theme</code> as
     * source of various properties.
     * @param theme the settings
     * @return the new factory
     */
    public static DockTitleFactory createFactory( final EclipseTheme theme ){
        return new DockTitleFactory(){
            public DockTitle createDockableTitle( Dockable dockable,
                    DockTitleVersion version ) {
                
                return new BasicTabDockTitle( theme, dockable, version );
            }
            
            public <D extends Dockable & DockStation> DockTitle createStationTitle(
                    D dockable, DockTitleVersion version ) {
            
                return new BasicTabDockTitle( theme, dockable, version );
            }
        };
    }
    
    /** the theme used to get theme-properties */
    private EclipseTheme theme;
    
    /** whether this tab is currently selected */
    private boolean selected = false;
    
    /** whether to paint the icon when this tab is not selected */
    private boolean paintIconWhenInactive = true;
    
    private TitleColor activeLeft;
    private TitleColor activeRight;
    private TitleColor activeText;
    private TitleColor inactiveLeft;
    private TitleColor inactiveRight;
    private TitleColor inactiveText;
    
    private TitleColor borderColor;
    
    /**
     * Creates a new title
     * @param theme the properties needed to paint this title correctly
     * @param dockable the element for which this title is shown
     * @param origin the type of this title
     */
    public BasicTabDockTitle( EclipseTheme theme, Dockable dockable, DockTitleVersion origin ) {
        super( dockable, origin );
        this.theme = theme;
        
        setBorder( BorderFactory.createEmptyBorder( 0, 0, 1, 0 ) );
        
        activeLeft = new TitleColor( "stack.tab.top.selected.focused", TitleColor.class, this, Color.WHITE ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                setActiveLeftColor( newColor );
            }
        };
        activeRight = new TitleColor( "stack.tab.bottom.selected.focused", TitleColor.class, this, Color.WHITE ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                setActiveRightColor( newColor );
            }
        };
        activeText = new TitleColor( "stack.tab.text", TitleColor.class, this, Color.BLACK ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                setActiveTextColor( newColor );
            }
        };
        
        inactiveLeft = new TitleColor( "stack.tab.top.selected", TitleColor.class, this, Color.WHITE ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                setInactiveLeftColor( newColor );
            }
        };
        inactiveRight = new TitleColor( "stack.tab.bottom.selected", TitleColor.class, this, Color.WHITE ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                setInactiveRightColor( newColor );
            }
        };
        inactiveText = new TitleColor( "stack.tab.text", TitleColor.class, this, Color.BLACK ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                setInactiveTextColor( newColor );
            }
        };
        
        borderColor = new TitleColor( "stack.border", TitleColor.class, this, Color.BLACK ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                repaint();
            }
        };
    }
    
    @Override
    public void bind() {
        super.bind();
        
        DockController controller = getDockable().getController();
        ColorManager colors = controller.getColors();

        activeLeft.setManager( colors );
        activeRight.setManager( colors );
        activeText.setManager( colors );
        inactiveLeft.setManager( colors );
        inactiveRight.setManager( colors );
        inactiveText.setManager( colors );
        borderColor.setManager( colors );
    }
    
    @Override
    public void unbind() {
        super.unbind();

        activeLeft.setManager( null );
        activeRight.setManager( null );
        activeText.setManager( null );
        inactiveLeft.setManager( null );
        inactiveRight.setManager( null );
        inactiveText.setManager( null );
        borderColor.setManager( null );
    }
    
    @Override
    public void paintComponent( Graphics g ) {
        super.paintComponent( g );
        if( !selected ){
            g.setColor( borderColor.color() );
            g.drawLine( 0, getHeight()-1, getWidth(), getHeight()-1 );
        }
    }
    
    @Override
    protected DockActionSource getActionSourceFor( Dockable dockable ) {
        return new EclipseDockActionSource( theme, super.getActionSourceFor( dockable ), dockable, true );
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
