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
package bibliothek.gui.dock.themes;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.Enforcement;

/**
 * A {@link Combiner} that forwards calls to the {@link Combiner} of the
 * current {@link DockTheme}.
 * @author Benjamin Sigg
 */
public class ThemeCombiner implements Combiner{
	private DockController controller;
	
	/**
	 * Creates a new combiner.
	 * @param controller the owner of this combiner
	 */
	public ThemeCombiner( DockController controller ){
		this.controller = controller;
	}

	private Combiner get( CombinerSource source ){
		return get( source.getParent() );
	}

	private Combiner get( DockStation station ){
		return controller.getTheme().getCombiner( station );
	}

	public CombinerTarget prepare( CombinerSource source, Enforcement force ){
		Combiner combiner = get( source );
		CombinerTarget delegate = combiner.prepare( source, force );
		if( delegate == null ){
			return null;
		}
		else{
			return new Target( combiner, delegate );
		}
	}
	
	public Dockable combine( CombinerSource source, CombinerTarget target ){
		Target tTarget = (Target) target;
		return tTarget.combiner.combine( source, tTarget.delegate );
	}
	
	public void aside( AsideRequest request ){
		DockStation parent = request.getParentStation();
		if( parent != null ){
			Combiner combiner = get( parent );
			combiner.aside( request );
		}
	}

	/**
	 * Wrapper around the real {@link CombinerTarget}.
	 * @author Benjamin Sigg
	 */
	private static class Target implements CombinerTarget{
		private Combiner combiner;
		private CombinerTarget delegate;
		
		/**
		 * Creates a new wrapper.
		 * @param combiner the combiner that created <code>delegate</code>
		 * @param delegate the real target
		 */
		public Target( Combiner combiner, CombinerTarget delegate ){
			this.combiner = combiner;
			this.delegate = delegate;
		}
		
		public void paint( Graphics g, Component component, StationPaint paint, Rectangle stationBounds, Rectangle dockableBounds ){
			delegate.paint( g, component, paint, stationBounds, dockableBounds );	
		}
		
		public DisplayerCombinerTarget getDisplayerCombination(){
			return delegate.getDisplayerCombination();
		}
	}
}
