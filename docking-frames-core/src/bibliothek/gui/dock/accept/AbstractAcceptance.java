/**
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

package bibliothek.gui.dock.accept;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.CombinatoryAcceptance.Combination;

/**
 * A DockAcceptance that returns <code>true</code> for all cases.
 * @author Benjamin Sigg
 */
public abstract class AbstractAcceptance implements DockAcceptance {
    public boolean accept( DockStation parent, Dockable child ) {
        return true;
    }

    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
        return true;
    }

    /**
     * Combines this acceptance with the <code>other</code> acceptance. The new acceptance
     * returns <code>true</code> only if this and the <code>other</code>
     * acceptance return <code>true</code>
     * @param other A second acceptance
     * @return An acceptance that represents a logical AND.
     */
    public DockAcceptance andAccept( DockAcceptance other ){
        return new CombinatoryAcceptance( Combination.AND, this, other );
    }
    
    /**
     * Combines this acceptance with the <code>other</code> acceptance in 
     * a logical "OR" operation.
     * @param other The other acceptance
     * @return An acceptance that returns <code>true</code> if this or
     * the <code>other</code> acceptance returns <code>true</code>
     */
    public DockAcceptance orAccept( DockAcceptance other ){
        return new CombinatoryAcceptance( Combination.OR, this, other );
    }
    
    /**
     * Combines this acceptance with the <code>other</code> acceptance
     * in a logical "XOR" relation.
     * @param other The other acceptance
     * @return An acceptance that returns <code>true</code> only if
     * one of this and <code>other</code> returned <code>true</code>,
     * and the other one returned <code>false</code>
     */
    public DockAcceptance xorAccept( DockAcceptance other ){
        return new CombinatoryAcceptance( Combination.XOR, this, other );
    }
    
    /**
     * Combines this acceptance with the <code>other</code> acceptance
     * in a logical {@literal "<->"} operation.
     * @param other The other acceptance 
     * @return An acceptance that returns <code>true</code> if
     * this and the <code>other</code> acceptance returned the
     * same value.
     */
    public DockAcceptance equalAccept( DockAcceptance other ){
        return new CombinatoryAcceptance( Combination.EQUAL, this, other );
    }
    
    /**
     * Combines this acceptance with the <code>other</code> acceptance
     * such that the result is a logical implication of the form
     * "this -&gt; other"
     * @param other The other acceptance
     * @return An acceptance that returns <code>true</code> if this
     * acceptance returned <code>false</code> or if the <code>other</code>
     * acceptance returned <code>true</code>
     */
    public DockAcceptance impliesAccept( DockAcceptance other ){
        return new CombinatoryAcceptance( Combination.IMPLIES, this, other );
    }
    
    /**
     * Combines this acceptance with the <code>other</code> acceptance
     * such that the result is a logical implication of the form
     * "other -&gt; this".
     * @param other The other acceptance
     * @return An acceptance that returns <code>true</code> if
     * this acceptance returned <code>true</code> or if the 
     * <code>other</code> acceptance returned <code>false</code>
     */
    public DockAcceptance impliedAccept( DockAcceptance other ){
        return new CombinatoryAcceptance( Combination.IMPLIES, other, this );
    }
}
