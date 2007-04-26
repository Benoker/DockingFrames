/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JPanel;

import bibliothek.gui.dock.title.DockTitle;

/**
 * A panel which shows one {@link Dockable} and one {@link DockTitle}. The location
 * of the {@link DockTitle} is always at one of the four borders (left,
 * right, top, bottom). The title may be <code>null</code>, in this case only
 * the Dockable is shown.
 * @author Benjamin Sigg
 */
public class DockableDisplayer extends JPanel{
    /** The four possible locations of the title */
    public static enum Location{
        /** the left side */
        LEFT, 
        /** the right side */
        RIGHT, 
        /** the top side */
        TOP,
        /** the bottom side */
        BOTTOM };
    
    /** The content of this displayer */
    private Dockable dockable;
    /** The title on this displayer */
    private DockTitle title;
    /** the location of the title */
    private Location location;
    
    /**
     * Creates a new displayer
     */
    public DockableDisplayer(){
        this( null, null );
    }
    
    /**
     * Creates a new displayer, sets the title and the content.
     * @param dockable the content, may be <code>null</code>
     * @param title the title, may be <code>null</code>
     */
    public DockableDisplayer( Dockable dockable, DockTitle title ){
        this( dockable, title, Location.TOP );
    }
    
    /**
     * Creates a new displayer, sets the title, its location and the
     * content.
     * @param dockable the content, may be <code>null</code>
     * @param title the title of <code>dockable</code>, can be <code>null</code>
     * @param location the location of the title, can be <code>null</code>
     */
    public DockableDisplayer( Dockable dockable, DockTitle title, Location location ){
        super( null );
        setTitleLocation( location );
        setDockable( dockable );
        setTitle( title );
        setFocusable( true );
        setFocusCycleRoot( true );
    }
    
    /**
     * Gets the Dockable which is shown on this displayer.
     * @return the child, can be <code>null</code>
     */
    public Dockable getDockable() {
        return dockable;
    }

    /**
     * Sets the Dockable which should be shown on this displayer. A value
     * of <code>null</code> means that no Dockable should be visible at all.
     * @param dockable the child, can be <code>null</code>
     */
    public void setDockable( Dockable dockable ) {
        if( this.dockable != null )
            remove( this.dockable.getComponent() );
        
        this.dockable = dockable;
        if( dockable != null )
            add( dockable.getComponent() );
        
        invalidate();
    }

    /**
     * Gest the location of the title in respect to the Dockable.
     * @return the location
     */
    public Location getTitleLocation() {
        return location;
    }

    /**
     * Sets the location of the title in respect to the Dockable.
     * @param location the location, a value of <code>null</code> is transformed
     * into the default-value
     */
    public void setTitleLocation( Location location ) {
        if( location == null )
            location = Location.TOP;
        
        this.location = location;
        
        if( title != null )
            title.setOrientation( orientation( location ));
        
        invalidate();
    }

    /**
     * Determines the orientation of a {@link DockTitle} according to its
     * location on this displayer.
     * @param location the location on this displayer
     * @return the orientation
     */
    protected DockTitle.Orientation orientation( Location location ){
        switch( location ){
            case TOP: return DockTitle.Orientation.NORTH_SIDED;
            case BOTTOM: return DockTitle.Orientation.SOUTH_SIDED;
            case LEFT: return DockTitle.Orientation.WEST_SIDED;
            case RIGHT: return DockTitle.Orientation.EAST_SIDED;
        }
        
        return null;
    }
    
    /**
     * Gets the title which is shown on this displayer.
     * @return the title, can be <code>null</code>
     */
    public DockTitle getTitle() {
        return title;
    }

    /**
     * Sets the title of this displayer. If the title is set to <code>null</code>,
     * no title is visible. The displayer will change the 
     * {@link DockTitle#setOrientation(bibliothek.gui.dock.title.DockTitle.Orientation) orientation}
     * of the title.
     * @param title the title or <code>null</code>
     */
    public void setTitle( DockTitle title ) {
        if( this.title != null )
            remove( this.title.getComponent() );
        
        this.title = title;
        if( title != null ){
            title.setOrientation( orientation( location ));
            add( title.getComponent() );
        }
        
        invalidate();
    }
    
    @Override
    public Dimension getMinimumSize() {
    	Dimension base;
    	
    	if( title == null && dockable != null )
    		base = dockable.getComponent().getMinimumSize();
    	else if( dockable == null && title != null )
    		base = title.getComponent().getMinimumSize();
    	else if( dockable == null && title == null )
    		base = new Dimension( 0, 0 );
    	else if( location == Location.LEFT || location == Location.RIGHT ){
    		Dimension titleSize = title.getComponent().getMinimumSize();
    		base = dockable.getComponent().getMinimumSize();
    		base = new Dimension( base.width + titleSize.width, 
    				Math.max( base.height, titleSize.height ));
    	}
    	else{
    		Dimension titleSize = title.getComponent().getMinimumSize();
    		base = dockable.getComponent().getMinimumSize();
    		base = new Dimension( Math.max( titleSize.width, base.width ),
    				titleSize.height + base.height );
    	}
    	
    	Insets insets = getInsets();
    	if( insets != null ){
    		base = new Dimension( base.width + insets.left + insets.right,
    				base.height + insets.top + insets.bottom );
    	}
    	return base;
    }
    
    @Override
    public void doLayout(){
        Insets insets = getInsets();
        if( insets == null )
            insets = new Insets(0,0,0,0);
        
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        if( title == null && dockable == null )
            return;
        
        width = Math.max( 0, width );
        height = Math.max( 0, height );
        
        if( title == null )
            dockable.getComponent().setBounds( x, y, width, height );

        else if( dockable == null )
            title.getComponent().setBounds( x, y, width, height );
        
        else{
            Dimension preferred = title.getComponent().getPreferredSize();
            
            int preferredWidth = preferred.width;
            int preferredHeight = preferred.height;
            
            if( location == Location.LEFT || location == Location.RIGHT ){
                preferredWidth = Math.min( preferredWidth, width );
                preferredHeight = height;
            }
            else{
                preferredWidth = width;
                preferredHeight = Math.min( preferredHeight, height );
            }
            
            if( location == Location.LEFT ){
                title.getComponent().setBounds( x, y, preferredWidth, preferredHeight );
                dockable.getComponent().setBounds( x+preferredWidth, y, width - preferredWidth, height );
            }
            else if( location == Location.RIGHT ){
                title.getComponent().setBounds( x+width-preferredWidth, y, preferredWidth, preferredHeight );
                dockable.getComponent().setBounds( x, y, width - preferredWidth, preferredHeight );
            }
            else if( location == Location.BOTTOM ){
                title.getComponent().setBounds( x, y+height - preferredHeight, preferredWidth, preferredHeight );
                dockable.getComponent().setBounds( x, y, preferredWidth, height - preferredHeight );
            }
            else{
                title.getComponent().setBounds( x, y, preferredWidth, preferredHeight );
                dockable.getComponent().setBounds( x, y+preferredHeight, preferredWidth, height - preferredHeight );
            }
        }
    }
}
