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
package bibliothek.gui.dock.frontend;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.layout.PredefinedDockSituation;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.perspective.PredefinedPerspective;

/**
 * This default implementation of {@link DockFrontendPerspective} is used by the
 * {@link DefaultLayoutChangeStrategy} and builds upon a {@link PredefinedDockSituation}.
 * @author Benjamin Sigg
 */
public class DefaultDockFrontendPerspective implements DockFrontendPerspective{
	private DockFrontend frontend;
	private PredefinedPerspective perspective;
	private boolean entry;
	
	/**
	 * Creates a new perspective
	 * @param frontend the {@link DockFrontend} in whose realm this perspective is used
	 * @param perspective the perspective to build upon, not <code>null</code>
	 * @param entry whether the layout is a full or regular layout
	 */
	public DefaultDockFrontendPerspective( DockFrontend frontend, PredefinedPerspective perspective, boolean entry ){
		if( perspective == null ){
			throw new IllegalArgumentException( "perspective must not be null" );
		}
		this.frontend = frontend;
		this.perspective = perspective;
	}
	
	public PropertyTransformer getPropertyTransformer(){
		return frontend.createPropertyTransformer();
	}
	
	public PredefinedPerspective getPerspective(){
		return perspective;
	}
	
	public PerspectiveStation getRoot( String root ){
		PerspectiveElement result = perspective.get( DockFrontend.ROOT_KEY_PREFIX + root );
		if( result == null ){
			return null;
		}
		return result.asStation();
	}
	
	public void apply(){
		frontend.setSetting( createSetting(), entry );	
	}
	
	public void store( String name ){
		frontend.setSetting( name, createSetting() );	
	}
	
	private Setting createSetting(){
		Setting setting = frontend.getSetting( entry );
		for( String key : frontend.getRootNames() ){
			setting.putRoot( key, perspective.convert( getRoot( key ) ) );
		}
		return setting;
	}
}
