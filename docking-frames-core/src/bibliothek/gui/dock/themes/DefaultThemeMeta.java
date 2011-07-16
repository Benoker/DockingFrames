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
package bibliothek.gui.dock.themes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.TextManager;

/**
 * This default implementation of {@link ThemeMeta} uses the {@link TextManager} to read
 * label and description.
 * @author Benjamin Sigg
 */
public class DefaultThemeMeta implements ThemeMeta {
	/** the source of this meta */
	private ThemeFactory factory;
	/** the controller in whose realm this meta is used */
	private DockController controller;
	
	/** the user friendly name of the factory */
	private ThemeFactoryText name;
	/** a detailed description of the theme */
	private ThemeFactoryText description;

	/** the people creating the theme */
	private String[] authors;
	/** some webpages to show */
	private URI[] webpages;
	
	/** all the listeners currently registered */
	private List<ThemeMetaListener> listeners = new ArrayList<ThemeMetaListener>();
	
	/**
	 * Creates new meta information.
	 * @param factory the source of this information
	 * @param controller the controller to be used
	 * @param nameId the unique identifier of the name, will be used for the {@link TextManager}
	 * @param descriptionId the unique identifier of the description, will be used for the {@link TextManager}
	 * @param authors all the people creating the theme
	 * @param webpages additional webpages users may visit
	 */
	public DefaultThemeMeta( ThemeFactory factory, DockController controller, String nameId, String descriptionId, String[] authors, URI[] webpages ){
		this.factory = factory;
		this.controller = controller;
		this.authors = authors;
		this.webpages = webpages;
		
		name = new ThemeFactoryText( nameId, factory ){
			protected void changed( String oldValue, String newValue ){
				fireNameChanged();
			}
		};
		description = new ThemeFactoryText( descriptionId, factory ){
			protected void changed( String oldValue, String newValue ){
				fireDescriptionChanged();
			}
		};
	}
	
	/**
	 * Tells whether at least one {@link ThemeMetaListener} is registered at this {@link ThemeMeta}.
	 * @return <code>true</code> if there is at least one listener
	 */
	protected boolean hasListeners(){
		return listeners.size() > 0;
	}
	
	/**
	 * Invokes {@link ThemeMetaListener#nameChanged(ThemeMeta)} on all registered listeners.
	 */
	protected void fireNameChanged(){
		for( ThemeMetaListener listener : listeners.toArray( new ThemeMetaListener[ listeners.size() ] )){
			listener.nameChanged( this );
		}
	}
	
	/**
	 * Invokes {@link ThemeMetaListener#descriptionChanged(ThemeMeta)} on all registered listeners.
	 */
	protected void fireDescriptionChanged(){
		for( ThemeMetaListener listener : listeners.toArray( new ThemeMetaListener[ listeners.size() ] )){
			listener.descriptionChanged( this );
		}
	}
	
	
	/**
	 * Invokes {@link ThemeMetaListener#authorsChanged(ThemeMeta)} on all registered listeners.
	 */
	protected void fireAuthorChanged(){
		for( ThemeMetaListener listener : listeners.toArray( new ThemeMetaListener[ listeners.size() ] )){
			listener.authorsChanged( this );
		}
	}
	
	
	/**
	 * Invokes {@link ThemeMetaListener#webpagesChanged(ThemeMeta)} on all registered listeners.
	 */
	protected void fireWebpagesChanged(){
		for( ThemeMetaListener listener : listeners.toArray( new ThemeMetaListener[ listeners.size() ] )){
			listener.webpagesChanged( this );
		}
	}
	
	public ThemeFactory getFactory(){
		return factory;
	}
	
	public void setFactory( ThemeFactory factory ){
		this.factory = factory;	
	}
	
	public void addListener( ThemeMetaListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		if( listeners.size() == 0 ){
			name.setController( controller );
			description.setController( controller );
		}
		listeners.add( listener );
	}

	public void removeListener( ThemeMetaListener listener ){
		listeners.remove( listener );
		if( listeners.size() == 0 ){
			name.setController( null );
			description.setController( null );
		}
	}
	
	public void setAuthors( String[] authors ){
		this.authors = authors;
		fireAuthorChanged();
	}
	
	public String[] getAuthors(){
		return authors;
	}

	public String getDescription(){
		if( listeners.size() == 0 ){
			description.update( controller.getTexts() );
		}
		return description.value();
	}

	public String getName(){
		if( listeners.size() == 0 ){
			name.update( controller.getTexts() );
		}
		return name.value();
	}

	public void setWebpages( URI[] webpages ){
		this.webpages = webpages;
		fireWebpagesChanged();
	}
	
	public URI[] getWebpages(){
		return webpages;
	}
}
