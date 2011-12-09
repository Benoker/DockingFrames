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
package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.xml.XElement;

/**
 * A factory for reading and writing {@link ComponentDockable}s. Since there is
 * no way to guess which {@link Component} is actually shown by the
 * {@link ComponentDockable} this factory does never create new
 * {@link ComponentDockable}s nor does it store any information.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarPartDockFactory implements
		DockFactory<ComponentDockable, PerspectiveElement, Object>{
	/** unique unmodifiable identifier of this factory */
	public static final String ID = "ToolbarPartDockFactory";

	/** dummy object used as layout, does not contain any information */
	private static final Object DUMMY = new Object();

	@Override
	public String getID(){
		return ID;
	}

	@Override
	public Object getLayout( ComponentDockable element,
			Map<Dockable, Integer> children ){
		return DUMMY;
	}

	@Override
	public Object getPerspectiveLayout( PerspectiveElement element,
			Map<PerspectiveDockable, Integer> children ){
		return DUMMY;
	}

	@Override
	public void setLayout( ComponentDockable element, Object layout,
			Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		// nothing to do
	}

	@Override
	public void setLayout( ComponentDockable element, Object layout,
			PlaceholderStrategy placeholders ){
		// nothing to do
	}

	@Override
	public void write( Object layout, DataOutputStream out ) throws IOException{
		// nothing to do
	}

	@Override
	public void write( Object layout, XElement element ){
		// nothing to do
	}

	@Override
	public Object read( DataInputStream in, PlaceholderStrategy placeholders )
			throws IOException{
		return DUMMY;
	}

	@Override
	public Object read( XElement element, PlaceholderStrategy placeholders ){
		return DUMMY;
	}

	@Override
	public void estimateLocations( Object layout, LocationEstimationMap children ){
		// nothing to do
	}

	@Override
	public ComponentDockable layout( Object layout,
			Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		return null;
	}

	@Override
	public ComponentDockable layout( Object layout,
			PlaceholderStrategy placeholders ){
		return null;
	}

	@Override
	public PerspectiveElement layoutPerspective( Object layout,
			Map<Integer, PerspectiveDockable> children ){
		return null;
	}

	@Override
	public void layoutPerspective( PerspectiveElement perspective,
			Object layout, Map<Integer, PerspectiveDockable> children ){
		// nothing to do
	}
}
