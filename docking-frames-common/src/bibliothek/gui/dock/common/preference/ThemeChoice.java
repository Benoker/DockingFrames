/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.common.preference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bibliothek.extension.gui.dock.preference.preferences.choice.DefaultChoice;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.themes.ThemeMeta;
import bibliothek.gui.dock.themes.ThemeMetaListener;

/**
 * A choice offering all the {@link ThemeFactory}s a {@link ThemeMap} provides.
 * @author Benjamin Sigg
 */
public class ThemeChoice extends DefaultChoice<ThemeFactory>{
	/** all the wrappers that are currently used */
	private List<FactoryWrapper> wrappers = new ArrayList<FactoryWrapper>();
	
    /**
     * Creates a new set of choices.
     * @param themes the map to read
     * @param controller the controller in whose realm this choice is required, can be <code>null</code>
     */
    public ThemeChoice( ThemeMap themes, DockController controller ){
    	super( controller );
    	
        setNullEntryAllowed( false );
        
        for( int i = 0, n = themes.size(); i<n; i++ ){
        	FactoryWrapper wrapper = new FactoryWrapper( themes.getKey( i ), themes.getFactory( i ) );
        	wrappers.add( wrapper );
        	wrapper.install();
        }
    }
    
    @Override
    public void setController( DockController controller ){
    	super.setController( controller );
    	for( FactoryWrapper wrapper : wrappers ){
    		wrapper.setController( controller );
    	}
    }
    
    @Override
    public void remove( int index ){
    	String id = getId( index );
    	super.remove( index );
    	
    	Iterator<FactoryWrapper> iterator = wrappers.iterator();
    	while( iterator.hasNext() ){
    		FactoryWrapper wrapper = iterator.next();
    		if( wrapper.key.equals( id )){
    			wrapper.setController( null );
    			iterator.remove();
    		}
    	}
    }
    
    /**
     * A wrapper around a {@link ThemeFactory}, updates the name if required.
     * @author Benjamin Sigg
     */
    private class FactoryWrapper implements ThemeMetaListener{
    	private String key;
    	private ThemeFactory factory;
    	private ThemeMeta meta;
    	private Entry<ThemeFactory> entry;
    	
    	public FactoryWrapper( String key, ThemeFactory factory ){
    		this.key = key;
    		this.factory = factory;
    	}
    	
    	public void install(){
    		entry = add( key, "", factory );
    	}
    	
    	public void setController( DockController controller ){
    		if( controller == null ){
    			entry.setEntryText( "" );
    			if( meta != null ){
	    			meta.removeListener( this );
	    			meta = null;
    			}
    		}
    		else{
    			if( meta != null ){
    				meta.removeListener( this );
    				meta = null;
    			}
    			meta = factory.createMeta( controller );
    			meta.addListener( this );
    			entry.setEntryText( meta.getName() );
    		}
    	}
    	
    	public void nameChanged( ThemeMeta meta ){
    		entry.setEntryText( meta.getName() );
    	}
    	
    	public void authorsChanged( ThemeMeta meta ){
    		// ignore
    	}
    	
    	public void descriptionChanged( ThemeMeta meta ){
    		// ignore
    	}
    	
    	public void webpagesChanged( ThemeMeta meta ){
	    	// ignore	
    	}
    }
}
