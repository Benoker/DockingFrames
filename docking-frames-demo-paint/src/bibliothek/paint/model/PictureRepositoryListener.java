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
package bibliothek.paint.model;

/**
 * An observer of a {@link PictureRepository}. Gets informed when a 
 * {@link Picture} is added or removed from the repository.
 * @author Benjamin Sigg
 *
 */
public interface PictureRepositoryListener {
	/**
	 * Invoked when a picture was added to the observed repository.
	 * @param picture the new picture
	 */
	public void pictureAdded( Picture picture );
	
	/**
	 * Invoked when a picture was removed from the observed repository.
	 * @param picture the removed picture
	 */
	public void pictureRemoved( Picture picture );
}
