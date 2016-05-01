/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.layout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.xml.XElement;

/**
 * This factory is only used to extract {@link MultipleCDockable}s that have been inserted into
 * a layout by the {@link CLayoutChangeStrategy}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class ReplacementDockFactory implements DockFactory<CommonDockable, PerspectiveElement, MultipleCDockable> {
	public static final String REPLACEMENT_FACTORY_ID = "dock.common.replacement_factory";
	
	public void estimateLocations( MultipleCDockable layout, LocationEstimationMap children ){
		// ignore
	}

	public CommonDockable layout( MultipleCDockable layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		return layout( layout, placeholders );
	}

	public CommonDockable layout( MultipleCDockable layout, PlaceholderStrategy placeholders ){
		return layout.intern();
	}

	public String getID(){
		return REPLACEMENT_FACTORY_ID;
	}

	public MultipleCDockable getLayout( CommonDockable element, Map<Dockable, Integer> children ){
		return (MultipleCDockable)element.getDockable();
	}
	
	public MultipleCDockable getPerspectiveLayout( PerspectiveElement element, Map<PerspectiveDockable, Integer> children ){
		return null;
	}
	
	public PerspectiveElement layoutPerspective( MultipleCDockable layout, Map<Integer, PerspectiveDockable> children ){
		return null;
	}
	
	public void layoutPerspective( PerspectiveElement perspective, MultipleCDockable layout, Map<Integer, PerspectiveDockable> children ){
		// ignore	
	}

	public MultipleCDockable read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		return null;
	}

	public MultipleCDockable read( XElement element, PlaceholderStrategy placeholders ){
		return null;
	}

	public void setLayout( CommonDockable element, MultipleCDockable layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		// ignore
	}

	public void setLayout( CommonDockable element, MultipleCDockable layout, PlaceholderStrategy placeholders ){
		// ignore
	}

	public void write( MultipleCDockable layout, DataOutputStream out ) throws IOException{
		// ignore	
	}

	public void write( MultipleCDockable layout, XElement element ){
		// ignore	
	}
}
