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

/**
 * A DockAcceptance that is a combination out of two other acceptances.
 * @author Benjamin Sigg
 */
public class CombinatoryAcceptance extends AbstractAcceptance {
    public static enum Combination{
        AND, OR, XOR, EQUAL, IMPLIES
    }
    
    private Combination combination;
    private DockAcceptance first, second;
    
    /**
     * Constructor, sets up all fields of this acceptance
     * @param combination How the two Acceptances <code>first</code> and
     * <code>second</code> will be combined.
     * @param first The acceptance at the "left" side of the operation
     * @param second The acceptance at the "right" side of the operation
     */
    public CombinatoryAcceptance( Combination combination, DockAcceptance first, DockAcceptance second ){
        setCombination( combination );
        setFirst( first );
        setSecond( second );
    }
    
    @Override
    public boolean accept( DockStation parent, Dockable child ) {
        return compare( first.accept( parent, child ), second.accept( parent, child ));
    }
    @Override
    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
        return compare( first.accept(parent, child, next), second.accept(parent, child, next));
    }
    
    /**
     * Makes a logical operation with <code>first</code> and
     * <code>second</code> according to the operation specified
     * as {@link #setCombination(bibliothek.gui.dock.accept.CombinatoryAcceptance.Combination) combination}.
     * @param first The operand at the left side
     * @param second The operand at the right side
     * @return The combination
     */
    protected boolean compare( boolean first, boolean second ){
        switch( combination ){
            case AND: return first && second;
            case OR: return first || second;
            case XOR: return first != second;
            case EQUAL: return first == second;
            case IMPLIES: return second || !first;
            default: throw new IllegalStateException( "no combination" );
        }
    }
    
    /**
     * Sets, how the two acceptances of this {@link CombinatoryAcceptance}
     * have to be combined
     * @param combination How to calculate {@link #accept(DockStation, Dockable)}
     * out of the results of {@link #setFirst(DockAcceptance) first} and
     * of {@link #setSecond(DockAcceptance) second}
     * @throws IllegalArgumentException if <code>combination</code> is <code>null</code>
     */
    public void setCombination( Combination combination ) {
        if( combination == null )
            throw new IllegalArgumentException( "Combination must not be null" );
        
        this.combination = combination;
    }
    
    /**
     * Gets how the combination is calculated
     * @return The operand
     * @see #setCombination(bibliothek.gui.dock.accept.CombinatoryAcceptance.Combination)
     */
    public Combination getCombination() {
        return combination;
    }
    
    /**
     * Sets the "left" operand of the combination
     * @param first The first acceptance whose opinion for an
     * {@link #accept(DockStation, Dockable) accept} will be asked.
     * @throws IllegalArgumentException if the argument is <code>null</code>
     */
    public void setFirst( DockAcceptance first ) {
        if( first == null )
            throw new IllegalArgumentException( "First must not be null" );
        this.first = first;
    }
    
    /**
     * Gets the "left" operand of the combination
     * @return The acceptance
     * @see #setFirst(DockAcceptance)
     */
    public DockAcceptance getFirst() {
        return first;
    }
    
    /**
     * Sets the "right" operand of the combination
     * @param second The second acceptance whose opinion for 
     * {@link #accept(DockStation, Dockable)} will be asked.
     * @throws IllegalArgumentException if the argument is <code>null</code>
     */
    public void setSecond( DockAcceptance second ) {
        if( second == null )
            throw new IllegalArgumentException( "Second must not be null" );
        this.second = second;
    }
    
    /**
     * Gets the "right" operand of the combination
     * @return The right operand
     * @see #setSecond(DockAcceptance)
     */
    public DockAcceptance getSecond() {
        return second;
    }
}
