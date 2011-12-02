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
package bibliothek.gui.dock.station.support;

import bibliothek.gui.dock.station.Combiner;

/**
 * Describes how much a caller to {@link Combiner#prepare(CombinerSource, Enforcement)} would
 * like the method to succeed.
 * @author Benjamin Sigg
 */
public enum Enforcement{
	/** 
	 * {@link Combiner#prepare(CombinerSource, Enforcement) prepare} must return a value that
	 * is not <code>null</code>. 
	 */
	HARD(1.0f),
	
	/**
	 * The caller of {@link Combiner#prepare(CombinerSource, Enforcement) prepare} does have a backup
	 * plan should the method return <code>null</code>, but it is not a good one.
	 */
	EXPECTED(0.6f),
	
	/**
	 * The caller of {@link Combiner#prepare(CombinerSource, Enforcement) prepare} does have a backup
	 * plan should the method return <code>null</code>, and it is a good one.
	 */
	WHISHED(0.3f), 
	
	/**
	 * It is completely up to the {@link Combiner} whether {@link Combiner#prepare(CombinerSource, Enforcement) prepare}
	 * returns <code>null</code> or not.
	 */
	FREE(0.0f);
	
	private final float force;
	
	private Enforcement( float force ){
		this.force = force;
	}
	
	/**
	 * Gets the force on a scale of 0 to 1, where 0 means that {@link Combiner#prepare(CombinerSource, Enforcement) prepare}
	 * is free to do what it wants, while 1 means that {@link Combiner#prepare(CombinerSource, Enforcement) prepare}
	 * must not return a <code>null</code> value.
	 * @return the force of the request
	 */
	public float getForce(){
		return force;
	}
}