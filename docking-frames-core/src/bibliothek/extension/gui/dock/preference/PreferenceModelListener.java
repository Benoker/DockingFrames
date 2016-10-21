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
package bibliothek.extension.gui.dock.preference;

/**
 * A listener to a {@link PreferenceModel}, gets informed about changes in the model.
 * @author Benjamin Sigg
 */
public interface PreferenceModelListener {
    /**
     * Called when new preferences have been added to <code>model</code>.
     * @param model the model that changed
     * @param beginIndex the index of the first new preference
     * @param endIndex the index of the last new preference
     */
    public void preferenceAdded( PreferenceModel model, int beginIndex, int endIndex );
    
    /**
     * Called when some preferences have been removed from <code>model</code>.
     * @param model the model that changed
     * @param beginIndex the old index of the first preference that was removed
     * @param endIndex the old index of the last preference that was removed
     */
    public void preferenceRemoved( PreferenceModel model, int beginIndex, int endIndex );
    
    /**
     * Called when some preferences have been changed. This includes
     * the value of a preference, the text or description, but also the enabled {@link PreferenceOperation}s.
     * @param model the source of the event
     * @param beginIndex the index of the first preference that changed
     * @param endIndex the index of the last preference that changed
     */
    public void preferenceChanged( PreferenceModel model, int beginIndex, int endIndex );
}
