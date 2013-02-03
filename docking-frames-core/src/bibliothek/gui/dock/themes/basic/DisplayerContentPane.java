/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.themes.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayer.Location;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.Transparency;

/**
 * The {@link DisplayerContentPane} is used by {@link DockableDisplayer} to show a 
 * {@link Component} for a {@link DockTitle} and one for a {@link Dockable}.
 * @author Benjamin Sigg
 */
public class DisplayerContentPane extends ConfiguredBackgroundPanel{
	private Component title;
	private Component dockable;
	private Location location;
	
	/**
	 * Creates a new content pane
	 */
	public DisplayerContentPane(){
		super( null, Transparency.DEFAULT );
	}

	/**
	 * Sets the location at which the title should be shown.
	 * @param location the new location of the title
	 */
	public void setTitleLocation( Location location ){
		this.location = location;
		revalidate();
	}
	
	/**
	 * Sets the component which represents the dockable
	 * @param dockable the representation of the dockable, can be <code>null</code>
	 */
	public void setDockable( Component dockable ){
		if( this.dockable != null ){
			remove( this.dockable );
		}
		this.dockable = dockable;
		if( this.dockable != null ){
			this.add( this.dockable );
		}
	}
	
	/**
	 * Sets the component which represents the title.
	 * @param title the representation of the title, can be <code>null</code>
	 */
	public void setTitle( Component title ){
		if( this.title != null ){
			remove( this.title );
		}
		this.title = title;
		if( this.title != null ){
			add( this.title );
		}
	}
	
	@Override
	public Dimension getPreferredSize(){
    	Dimension base;
    	
    	if( title == null && dockable != null )
    		base = dockable.getPreferredSize();
    	else if( dockable == null && title != null )
    		base = title.getPreferredSize();
    	else if( dockable == null && title == null )
    		base = new Dimension( 0, 0 );
    	else if( location == Location.LEFT || location == Location.RIGHT ){
    		Dimension titleSize = title.getPreferredSize();
    		base = dockable.getPreferredSize();
    		base = new Dimension( base.width + titleSize.width, 
    				Math.max( base.height, titleSize.height ));
    	}
    	else{
    		Dimension titleSize = title.getPreferredSize();
    		base = dockable.getPreferredSize();
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
	public Dimension getMaximumSize(){
		Dimension base;
    	
    	if( title == null && dockable != null )
    		base = dockable.getMaximumSize();
    	else if( dockable == null && title != null )
    		base = title.getMaximumSize();
    	else if( dockable == null && title == null )
    		base = new Dimension( 0, 0 );
    	else if( location == Location.LEFT || location == Location.RIGHT ){
    		Dimension titleSize = title.getMaximumSize();
    		base = dockable.getMaximumSize();
    		base = new Dimension( base.width + titleSize.width, 
    				Math.max( base.height, titleSize.height ));
    	}
    	else{
    		Dimension titleSize = title.getMaximumSize();
    		base = dockable.getMaximumSize();
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
	public Dimension getMinimumSize(){
    	Dimension base;
    	
    	if( title == null && dockable != null )
    		base = dockable.getMinimumSize();
    	else if( dockable == null && title != null )
    		base = title.getMinimumSize();
    	else if( dockable == null && title == null )
    		base = new Dimension( 0, 0 );
    	else if( location == Location.LEFT || location == Location.RIGHT ){
    		Dimension titleSize = title.getMinimumSize();
    		base = dockable.getMinimumSize();
    		base = new Dimension( base.width + titleSize.width, 
    				Math.max( base.height, titleSize.height ));
    	}
    	else{
    		Dimension titleSize = title.getMinimumSize();
    		base = dockable.getMinimumSize();
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
            insets = new Insets( 0,0,0,0 );
        
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        if( title == null && dockable == null )
            return;
        
        width = Math.max( 0, width );
        height = Math.max( 0, height );
        
        if( title == null )
            dockable.setBounds( x, y, width, height );

        else if( dockable == null )
            title.setBounds( x, y, width, height );
        
        else{
            Dimension preferred = title.getPreferredSize();
            
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
                title.setBounds( x, y, preferredWidth, preferredHeight );
                dockable.setBounds( x+preferredWidth, y, width - preferredWidth, height );
            }
            else if( location == Location.RIGHT ){
                title.setBounds( x+width-preferredWidth, y, preferredWidth, preferredHeight );
                dockable.setBounds( x, y, width - preferredWidth, preferredHeight );
            }
            else if( location == Location.BOTTOM ){
                title.setBounds( x, y+height - preferredHeight, preferredWidth, preferredHeight );
                dockable.setBounds( x, y, preferredWidth, height - preferredHeight );
            }
            else{
                title.setBounds( x, y, preferredWidth, preferredHeight );
                dockable.setBounds( x, y+preferredHeight, preferredWidth, height - preferredHeight );
            }
        }
    }
}
