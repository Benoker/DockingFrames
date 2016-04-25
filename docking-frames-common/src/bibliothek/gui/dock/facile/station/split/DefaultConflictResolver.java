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
package bibliothek.gui.dock.facile.station.split;

/**
 * A conflict resolver that tries to distribute space in conflicts 
 * equally to all parties
 * @author Benjamin Sigg
 */
public class DefaultConflictResolver<T> implements ConflictResolver<T>{

    /**
     * Increments an integer but only if the integer is not -1.
     * @param i the integer to increment.
     * @return i+1 if i is not -1. -1 if i is -1.
     */
    private int increment( int i ){
        return (i==-1) ? -1 : (i+1);
    }
    
    public ResizeRequest requestHorizontal( ResizeRequest left, ResizeRequest right, ResizeNode<T> node ) {
        if( left == null && right == null )
            return null;
        
        if( left == null ){
            return new ResizeRequest( 
                    right.getDeltaWidth(), 
                    right.getDeltaHeight(),
                    increment( right.getFractionWidth() ),
                    right.getFractionHeight());
        }
        
        if( right == null ){
            return new ResizeRequest( 
                    left.getDeltaWidth(), 
                    left.getDeltaHeight(),
                    increment(left.getFractionWidth()),
                    left.getFractionHeight());
        }
    
        double widthDelta = 0;
        int widthFraction = -1;
        if( left.getFractionWidth() == -1 && right.getFractionWidth() == -1 ){
            widthDelta = 0;
            widthFraction = -1;
        }
        else if( left.getFractionWidth() == -1 ){
            widthDelta = right.getDeltaWidth();
            widthFraction = right.getFractionWidth()+1;
        }
        else if( right.getFractionWidth() == -1 ){
            widthDelta = left.getDeltaWidth();
            widthFraction = left.getFractionWidth()+1;
        }
        else{
            widthDelta = left.getDeltaWidth() / left.getFractionWidth() + 
                right.getDeltaWidth() / right.getFractionWidth() +
                node.getNewDividerSize() - node.getOldDividerSize();
            widthFraction = 1;
        }
        
        double heightDelta = 0;
        int heightFraction = -1;
        if( left.getFractionHeight() == -1 && right.getFractionHeight() == -1 ){
            heightDelta = 0;
            heightFraction = -1;
        }
        else if( left.getFractionHeight() == -1 ){
            heightDelta = right.getDeltaHeight();
            heightFraction = right.getFractionHeight();
        }
        else if( right.getFractionHeight() == -1 ){
            heightDelta = left.getDeltaHeight();
            heightFraction = left.getFractionHeight();
        }
        else if( left.getFractionHeight() == right.getFractionHeight()){
            heightDelta = (left.getDeltaHeight() + right.getDeltaHeight())/2;
            heightFraction = left.getFractionHeight();
        }
        else if( left.getFractionHeight() < right.getFractionHeight() ){
            heightDelta = left.getDeltaHeight();
            heightFraction = left.getFractionHeight();
        }
        else{
            heightDelta = right.getDeltaHeight();
            heightFraction = right.getFractionHeight();
        }
        
        if( widthFraction == -1 && heightFraction == -1 )
            return null;
        
        return new ResizeRequest( widthDelta, heightDelta, widthFraction, heightFraction );
    }
    
    public ResizeRequest requestVertical( ResizeRequest top, ResizeRequest bottom, ResizeNode<T> node ) {
        if( top == null && bottom == null )
            return null;
        
        if( top == null ){
            return new ResizeRequest( 
                    bottom.getDeltaWidth(), 
                    bottom.getDeltaHeight(),
                    bottom.getFractionWidth(),
                    increment(bottom.getFractionHeight()));
        }
        
        if( bottom == null ){
            return new ResizeRequest( 
                    top.getDeltaWidth(), 
                    top.getDeltaHeight(),
                    top.getFractionWidth(),
                    increment(top.getFractionHeight()));
        }
        
        double widthDelta = 0;
        int widthFraction = -1;
        if( top.getFractionWidth() == -1 && bottom.getFractionWidth() == -1 ){
            widthDelta = 0;
            widthFraction = -1;
        }
        else if( top.getFractionWidth() == -1 ){
            widthDelta = bottom.getDeltaWidth();
            widthFraction = bottom.getFractionWidth();
        }
        else if( bottom.getFractionWidth() == -1 ){
            widthDelta = top.getDeltaWidth();
            widthFraction = top.getFractionWidth();
        }
        else if( top.getFractionWidth() == bottom.getFractionWidth()){
            widthDelta = (top.getDeltaWidth() + bottom.getDeltaWidth())/2;
            widthFraction = top.getFractionWidth();
        }
        else if( top.getFractionWidth() < bottom.getFractionWidth() ){
            widthDelta = top.getDeltaWidth();
            widthFraction = top.getFractionWidth();
        }
        else{
            widthDelta = bottom.getDeltaWidth();
            widthFraction = bottom.getFractionWidth();
        }
        
        double heightDelta = 0;
        int heightFraction = -1;
        if( top.getFractionHeight() == -1 && bottom.getFractionHeight() == -1 ){
            heightDelta = 0;
            heightFraction = -1;
        }
        else if( top.getFractionHeight() == -1 ){
            heightDelta = bottom.getDeltaHeight();
            heightFraction = bottom.getFractionHeight()+1;
        }
        else if( bottom.getFractionHeight() == -1 ){
            heightDelta = top.getDeltaHeight();
            heightFraction = top.getFractionHeight()+1;
        }
        else{
            heightDelta = top.getDeltaHeight() / top.getFractionHeight() +
                bottom.getDeltaHeight() / bottom.getFractionHeight() +
                node.getNewDividerSize() - node.getOldDividerSize();
            heightFraction = 1;
        }
        
        if( widthFraction == -1 && heightFraction == -1 )
            return null;
        
        return new ResizeRequest( widthDelta, heightDelta, widthFraction, heightFraction );
    }
    
    
    public double resolveHorizontal( ResizeNode<T> node, ResizeRequest left,
            double deltaLeft, ResizeRequest right, double deltaRight ) {


        if( left.getFractionWidth() == 1 && right.getFractionWidth() > 1 )
            return deltaLeft;
        else if( left.getFractionWidth() > 1 && right.getFractionWidth() == 1 )
            return deltaRight;
        else
            return (deltaLeft * left.getFractionWidth() + deltaRight * right.getFractionWidth()) /
                (left.getFractionWidth() + right.getFractionWidth() );
    }
    public double resolveVertical( ResizeNode<T> node, ResizeRequest top,
            double deltaTop, ResizeRequest bottom, double deltaBottom ) {
        
        if( top.getFractionHeight() == 1 && bottom.getFractionHeight() > 1 )
            return deltaTop;
        else if( top.getFractionHeight() > 1 && bottom.getFractionHeight() == 1 )
            return deltaBottom;
        else
            return (deltaTop * top.getFractionHeight() + deltaBottom * bottom.getFractionHeight()) /
                (top.getFractionHeight() + bottom.getFractionHeight() );
    }
}
