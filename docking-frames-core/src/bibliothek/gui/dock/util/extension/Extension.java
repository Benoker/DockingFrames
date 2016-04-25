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
package bibliothek.gui.dock.util.extension;

import java.util.Collection;

import bibliothek.gui.DockController;

/**
 * An extension provides a set of resources that override existing resources. The
 * resources are acquired lazily.<br>
 * New extensions will be added to the framework when needed or upon request.
 * @see ExtensionName
 * @author Benjamin Sigg
 */
public interface Extension {
	/**
	 * Informs this extension that it will now be used for <code>controller</code>.
	 * @param controller a new controller for which this extension is used
	 */
	public void install( DockController controller );
	
	/**
	 * Informs this extension that it will no longer be used for <code>controller</code>.
	 * @param controller the controller for which this extension was used
	 */
	public void uninstall( DockController controller );
	
	/**
	 * Loads a set of extensions, it is the {@link Extension}s responsibility to implement any
	 * kind of caching.
	 * @param <E> the kind of extension that is requested
	 * @param controller the controller in whose realm the extension will be used
	 * @param extension	the unique name of the extension
	 * @return the extension, can be <code>null</code>
	 */
	public <E> Collection<E> load( DockController controller, ExtensionName<E> extension );
}
