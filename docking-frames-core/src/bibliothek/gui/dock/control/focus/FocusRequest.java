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
package bibliothek.gui.dock.control.focus;

import java.awt.Component;
import java.awt.event.MouseEvent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;

/**
 * A {@link FocusRequest} can be sent to the {@link FocusController} in order for a
 * {@link Component} to gain the focus. Whether the request is granted depends on the
 * {@link FocusController}. A {@link FocusRequest} does not necessarily need to point
 * to a specific {@link Component}, it can also point e.g. to a {@link Dockable} telling
 * that one of its child {@link Component}s should be focused.<br>
 * A {@link FocusRequest} can have a {@link #getDelay() delay}, meaning it will be executed
 * not right away but at a later time. Hence it is possible for a {@link FocusRequest} to
 * be overtaken by another request. In such a case the request that is executed first
 * wins and cancels the other request.
 * @author Benjamin Sigg
 */
public interface FocusRequest {
	/**
	 * Gets a delay in milliseconds, the {@link FocusController} will wait executing this
	 * request until the delay has passed.
	 * @return the delay in milliseconds, can be 0
	 */
	public int getDelay();
	
	/**
	 * A hard request is not checked for correctness regarding the {@link Component#isVisible() visibility}
	 * or {@link Component#isShowing() showing}. These requests can be executed even if the application
	 * is not yet visible.
	 * @return whether this request should be executable even if the involved {@link Component}s are not
	 * visible
	 */
	public boolean isHardRequest();
	
	/**
	 * Invoked by <code>controller</code> right before this request is processed. The method
	 * checks whether this request is still valid, e.g. a request may no longer be valid because
	 * it points to a {@link Dockable} that is no longer visible. 
	 * @param controller the controller which will process this request
	 * @return <code>true</code> if this request is valid, <code>false</code> if this request is
	 * invalid and should be ignored
	 */
	public boolean validate( FocusController controller );
	
	/**
	 * Informs this request of the result of calling the {@link FocusVetoListener}s. This method is called
	 * after {@link #validate(FocusController)}.
	 * @param veto the veto that was cast, including {@link FocusVeto#NONE no veto}
	 */
	public void veto( FocusVeto veto );
	
	/**
	 * Gets the source of this request. The source may be the {@link Dockable} which receives
	 * the focus, or the {@link Component} which received a {@link MouseEvent}. The
	 * source will be used to ask the {@link FocusVetoListener}s whether to accept this
	 * request or not.
	 * @return the source of this request, may be <code>null</code>
	 */
	public DockElementRepresentative getSource();
	
	/**
	 * Gets the {@link Component} which may receive the focus. This is no necessarily the 
	 * {@link Component} that gains the focus because the {@link FocusStrategy} may choose
	 * another {@link Component}. This {@link Component} may be not focusable, not visible,
	 * not showing, or not valid in any other respect. It is the {@link FocusController}'s job
	 * to correct such issues.  
	 * @return the {@link Component} which may receive the focus, can be <code>null</code>
	 */
	public Component getComponent();
	
	/**
	 * Tells whether <code>component</code> would be an acceptable {@link Component} to receive
	 * the focus. This method usually is called when {@link #getComponent()} returned an invalid
	 * {@link Component} (e.g. <code>null</code> or a component that is not focusable) and that 
	 * {@link Component} gets replaced.
	 * @param component the component that might gain the focus
	 * @return <code>true</code> if this request approves on <code>component</code> replacing
	 * the result of {@link #getComponent()}
	 */
	public boolean acceptable( Component component );
	
	/**
	 * Called once this {@link FocusRequest} is granted, this request must now call
	 * a method like {@link Component#requestFocusInWindow()} on <code>component</code>.
	 * @param component the {@link Component} which gains the focus. Usually this 
	 * {@link Component} is valid in the sense of that it is focusable, visible and
	 * showing. There are no guarantees for this properties tough.
	 * @return a new {@link FocusRequest} that will be executed. If the result of
	 * {@link #getSource()} of <code>this</code> and of the result matches, and if the
	 * result of {@link #getComponent()} is the same as <code>component</code>, then 
	 * the {@link FocusController} will accept the request without calling neither
	 * {@link FocusVetoListener} nor {@link FocusStrategy}. In any other case the request
	 * will be treated like a completely new request. The result of this method can also
	 * be <code>null</code>. The result of this method can also by <code>this</code>.
	 */
	public FocusRequest grant( Component component );
}
