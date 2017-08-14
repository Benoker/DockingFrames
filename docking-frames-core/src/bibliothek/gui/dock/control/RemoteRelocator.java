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
package bibliothek.gui.dock.control;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import bibliothek.gui.Dockable;

/**
 * A remote relocator can be used to perform a {@literal drag & drop} operation of a
 * {@link Dockable} without the need to know more than the <code>Dockable</code>.<br>
 * The methods of this interface correspond to methods which are used in 
 * <code>MouseListener</code> and <code>MouseMotionListener</code>:
 * <table>
 *  <caption>Corresponding listener methods</caption>
 *  <tr>
 *   <td>{@link #init(int, int, int, int, int)}</td>
 *   <td>{@link MouseListener#mousePressed(MouseEvent)}</td>
 *  </tr>
 *  <tr>
 *   <td>{@link #drag(int, int, int)}</td>
 *   <td>{@link MouseMotionListener#mouseDragged(MouseEvent)}</td>
 *  </tr>
 *  <tr>
 *   <td>{@link #drop(int, int, int)}</td>
 *   <td>{@link MouseListener#mouseReleased(MouseEvent)}</td>
 *  </tr>
 * </table><br>
 * The names of these three methods correspond to their normal reaction, when
 * only the left mouse button is pressed (or released). When other buttons
 * are pressed, then the behavior of this relocator may change heavily. The
 * relocator might even cancel a {@literal drag & drop} operation.
 * <br><br>
 * The described three methods return a {@link Reaction}. A result that is 
 * {@link Reaction#CONTINUE CONTINUE} or 
 * {@link Reaction#CONTINUE_CONSUMED CONTINUE_CONSUMED} means, that 
 * the relocator is not yet finished with the operation. Further events have to
 * be sent to the relocator.<br>
 * If the result is {@link Reaction#BREAK BREAK} or 
 * {@link Reaction#BREAK_CONSUMED BREAK_CONSUMED}, then
 * the relocator has finished the operation.<br>
 * If the result is {@link Reaction#BREAK_CONSUMED BREAK_CONSUMED} or 
 * {@link Reaction#CONTINUE_CONSUMED CONTINUE_CONSUMED},
 * then the relocator has "used up" the event. If the client uses existing 
 * {@link MouseEvent MouseEvents} to call the methods, then {@link MouseEvent#consume()}
 * should be invoked.
 * <br><br>
 * The described three methods need the (simulated) location of the mouse
 * in relation to the screen (arguments <code>x</code> and <code>y</code>). The
 * methods need also information which buttons are currently pressed
 * (argument <code>modifiers</code>). Have a look at {@link InputEvent#getModifiersEx()}
 * to find out, how <code>modifiers</code> has to be encoded. 
 * <br><br>
 * Clients can safely assume, that the relocator is not interested in events
 * performed after the last button of the mouse is released.<br>
 * Clients might implement a {@link MouseListener} and a {@link MouseMotionListener}
 * which are both added to the same {@link Component}. The events received
 * by these listeners can be safely and unchecked forwarded to the relocator, 
 * as long as the <code>Component</code> represents only one {@link Dockable}.<br>
 * If the <code>Component</code> represents more than one <code>Dockable</code>,
 * then each event should be delivered only to one <code>RemoteRelocator</code>.
 * If that relocator is interested in the event, all other events have to be
 * forwarded to the same relocator, until the relocator is no longer interested
 * in the events. That includes events which would normally be sent to another
 * relocator.<br>
 * If two relocators are assigned to the same <code>Dockable</code>, then they
 * can substitute each other at any time.<br>
 * Mixing events with different sources or using different, not substitutable 
 * relocators while one relocator is interested in the events, will lead to 
 * unspecified behavior.<br>
 * A {@literal drag & drop} operation can be canceled in any state by calling {@link #cancel()}.
 * Afterwards no events have to be sent to the relocator any more (it is not forbidden to
 * send more events).<br><br>
 * New <code>RemoveRelocators</code> can be delivered by 
 * {@link DockRelocator#createRemote(Dockable)}.
 *  
 * @author Benjamin Sigg
 */
public interface RemoteRelocator extends BasicRemoteRelocator{
    /**
     * Tells a caller of a method whether the {@link RemoteRelocator} has
     * finished the {@literal drag & drop} operation or not.
     * @author Benjamin Sigg
     */
    public enum Reaction{
        /** The operation is going on, but the event was not consumed */
        CONTINUE,
        /** The operation is going on, and the event was consumed */
        CONTINUE_CONSUMED,
        /** The operation is finished, and the event was not consumed */
        BREAK,
        /** The operation is finished, but the event was consumed */
        BREAK_CONSUMED;
    }
    

    
    /**
     * This method starts or cancels a {@literal drag & drop} operation. This method simulates
     * a mouse-pressed event.
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     * @param dx the x-coordinate of the mouse on the simulated Component which sent the event, 0 is a good default-value.
     * @param dy the y-coordinate of the mouse on the simulated Component which sent the event, 0 is a good default-value.
     * @param modifiers the state of the mouse, see {@link MouseEvent#getModifiersEx()}.
     * @return how this remote reacts on the call, see {@link Reaction}
     */
    public Reaction init( int x, int y, int dx, int dy, int modifiers );
    
    /**
     * This method works on the drag-part of a {@literal drag & drop} operation.
     * This method simulates a mouse-dragged event.
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     * @param modifiers the state of the mouse, see {@link MouseEvent#getModifiersEx()}.
     * @return how this remote reacts on the call, see {@link Reaction}
     */
    public Reaction drag( int x, int y, int modifiers );
    
    /**
     * This method works on the drop-part of a {@literal drag & drop} operation.
     * This method simulates a mouse-released event.<br>
     * The {@literal drag & drop} operation may not be finished after an invocation of this
     * method, clients should carefully analyze the resulting {@link Reaction} 
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     * @param modifiers the state of the mouse, see {@link MouseEvent#getModifiersEx()}.
     * @return how this remote reacts on the call, see {@link Reaction}
     */
    public Reaction drop( int x, int y, int modifiers );
    
    /**
     * Cancels the current {@literal drag & drop} operation. No events have to be delivered
     * any more to this relocator.
     */
    public void cancel();
}
