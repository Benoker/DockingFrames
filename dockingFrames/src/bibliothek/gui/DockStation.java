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

package bibliothek.gui;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JFrame;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A <code>DockStation</code> is some area (e.g. a {@link Component}) showing
 * a set of {@link Dockable}s called "children". The station is free to decide how and if to
 * show its children.<br>
 * Although a station can take any form, there are some (optional) practices to follow:
 * <ul>
 * 	<li>Each {@link Dockable} should be child of a {@link DockableDisplayer}. The displayer will
 *  paint border and title of the item.</li>
 *  <li>This station should be aware of the current {@link DockTheme} and use its factories and delegates whenever possible.</li>
 *  <li>Drag and drop is handled by the {@link DockController}. Methods like {@link #canDrag(Dockable)}, {@link #drag(Dockable)}, 
 * {@link #drop()} or {@link #move()} need to be implemented for this. Also {@link #accept(Dockable)}, {@link Dockable#accept(DockStation)},
 * {@link Dockable#accept(DockStation, Dockable)} and the {@link DockAcceptance} of the current {@link DockController} should be checked before
 * allowing a drag and drop operation.</li>
 *  <li>For each child there should be at least one {@link DockTitle}. This station needs to derive a {@link DockTitleVersion} from
 *  its controller using the {@link DockController#getDockTitleManager() DockTitleManager} and its 
 *  {@link DockTitleManager#registerDefault(String, DockTitleFactory) factory method}. With the {@link DockTitleVersion}-object one
 *  {@link DockTitleRequest} for each required {@link DockTitle} can be created.</li>
 *  <li>One child can be focused. If this station changes the focus it should use {@link DockController#setAtLeastFocusedDockable(Dockable)}.</li>
 *  <li>This station should support placeholders. The current {@link PlaceholderStrategy} can be used to convert {@link Dockable}s to placeholders. 
 *  A {@link PlaceholderList} is a good datastructure to store {@link Dockable}s and placeholders at the same time.</li>
 *  <li>Additional points where the user can start drag and drop operations can be installed by implementing a {@link DockElementRepresentative}. It
 *  has to be installed using {@link DockController#addRepresentative(DockElementRepresentative)}. </li>
 *  <li>And a new {@link DockFactory} will be required to persistently store the layout of this station.</li>
 * </ul>
 * @author Benjamin Sigg
 */
public interface DockStation extends DockElement{
    /**
     * Sets the controller of this station. If the station wants to
     * show any {@link DockTitle}, then the titles have to be replaced
     * by new instances (assuming the controller is really new). The
     * title has to get new {@link DockTitleVersion DockTitleVersions} through
     * the {@link DockTitleManager} of <code>controller</code>.<br>
     * An argument of <code>null</code> means that this station is currently 
     * not shown.
     * @param controller the owner of this station, can be <code>null</code>
     */
    public void setController( DockController controller );
    
    /**
     * Gets the controller of this station.
     * @return the controller or <code>null</code> if no controller is set
     * @see #setController(DockController)
     */
    public DockController getController();
   
    /**
     * Updates the {@link DockTheme} of this station. The new theme
     * has to be read from the {@link #getController() controller} of 
     * this station. If the controller is <code>null</code>, this method
     * should return immediately.<br>
     * You may use {@link DockUI#updateTheme(DockStation, DockFactory)} 
     * to implement this method.
     */
    public void updateTheme();
    
    /**
     * Gets the current theme of this station. The theme can be <code>null</code>,
     * but that means that the station is not fully initialized, and might not
     * work correct.
     * @return the theme of this station or <code>null</code>. 
     */
    public DockTheme getTheme();
    
    /**
     * Gets a list of actions which should be available for the user and
     * affect the child <code>dockable</code>.
     * @param dockable a child of this station
     * @return actions for <code>dockable</code>, can be <code>null</code>
     */
    public DockActionSource getDirectActionOffers( Dockable dockable );
    
    /**
     * Gets a list of actions which should be available for the user and
     * affect <code>dockable</code>. The argument <code>dockable</code>
     * can be a child of this station, or a child of any station which is
     * below this station.
     * @param dockable a child of this station or a child of another station
     * which is below this station
     * @return actions for <code>dockable</code> or <code>null</code>
     */
    public DockActionSource getIndirectActionOffers( Dockable dockable );
    
    /**
     * Adds a listener to this station. The station has to invoke the
     * methods of the listener such that its requirements are full filled.
     * @param listener the listener to add
     */
    public void addDockStationListener( DockStationListener listener );
    
    /**
     * Removes a listener from this station.
     * @param listener the listener to remove
     */
    public void removeDockStationListener( DockStationListener listener );
    
    /**
     * Tells whether the child <code>dockable</code> is visible or not. Visible
     * means that the {@link Dockable#getComponent() component} of <code>dockable</code>
     * can be seen by the user. The result must be <code>false</code> if
     * this station is not visible.
     * @param dockable the child whose visibility-state is questioned
     * @return whether <code>dockable</code> is visible or not
     * @see #isStationVisible()
     */
    public boolean isVisible( Dockable dockable );
    
    /**
     * Tells whether this station is visible or not. For example a station on 
     * a {@link JFrame} is not visible if the frame is minimized.
     * @return whether this station is visible
     */
    public boolean isStationVisible();
    
    /**
     * Gets the number of children.
     * @return the number of children on this station
     */
    public int getDockableCount();
    
    /**
     * Gets the index'th child of this station.
     * @param index a value between 0 (incl.) and {@link #getDockableCount()} 
     * (excl.).
     * @return a child of this station
     */
    public Dockable getDockable( int index );
    
    /**
     * Gets the favorite child of this station. The favorite child is the one 
     * child which is specially designated for the user. An example: if the 
     * station behaves like a stack, and only the top child is visible, then 
     * the favorite child could the the top.<br>
     * A result of <code>null</code> indicates that there are no children
     * at all, or that there is no favorite child (all children are equal important).<br>
     * Stations should not change this property directly, they should call
     * {@link DockController#setFocusedDockable(Dockable, boolean)}
     * which will then call {@link #setFrontDockable(Dockable)}. Note that the
     * DockController itself listens to the DockTitles, and maybe the station
     * doesn't need a logic to decide which child is important.
     * @return the most important child or <code>null</code>
     */
    public Dockable getFrontDockable();
    
    /**
     * Sets the most important child. The station should ensure that this child
     * is visible (assuming the station itself is visible). Read the
     * comment on {@link #getFrontDockable()} how stations can change
     * this property.
     * @param dockable the new favorite child, can be <code>null</code>
     * @see #getFrontDockable()
     */
    public void setFrontDockable( Dockable dockable );
    
    /**
     * Gets a snapshot of all placeholders that are currently stored in this {@link DockStation}. 
     * A {@link DockStation} is free in the format it chooses to fill the map. The map is to be 
     * created with the assumptions that {@link #getDockableCount()} is <code>0</code>, meaning
     * any existing {@link Dockable} gets replaced by its placeholder. The current 
     * {@link PlaceholderStrategy} should be used to convert {@link Dockable}s to placeholders.
     * @return the map of placeholders or <code>null</code> if this station does not support
     * placeholders
     */
    public PlaceholderMap getPlaceholders();
    
    /**
     * Sets an earlier snapshot of the placeholders of this station. This station can assume that
     * it currently does not have any children (that {@link #getDockableCount()} is <code>0</code>).<br>
     * This method does nothing if it cannot handle the format or the version of <code>placeholders</code>.
     * @param placeholders some set of placeholders
     * @throws IllegalStateException if {@link #getDockableCount()} is not equal to <code>0</code>
     */
    public void setPlaceholders( PlaceholderMap placeholders );

    /**
     * Called by the {@link DockController} of this station to indicate that
     * the active-state of <code>title</code> has been changed. This station
     * should call the method <code>title.{@link DockTitle#changed(bibliothek.gui.dock.event.DockTitleEvent) changed}</code>
     * with an appropriate event. The station may add some additional information
     * to this call.
     * @param dockable the child whose title is changed
     * @param title the changed title, may not be bound
     * @param active the new state of the title
     */
    public void changed( Dockable dockable, DockTitle title, boolean active );
    
    /**
     * Provides a {@link DockTitle} for a child of this station. This method
     * must call {@link DockTitleRequest#answer(DockTitle)} to set the result.<br>
     * Most {@link DockStation}s won't have the need to implement this method,
     * leaving it empty will advice the framework to use another source for
     * new {@link DockTitle}s.
     * @param request the request to answer, not <code>null</code>
     */
	public void requestChildDockTitle( DockTitleRequest request );
    
    /**
     * Tells whether this station accepts <code>child</code> as a new child,
     * or refuses <code>child</code>. The user will not be able to drop
     * a {@link Dockable} onto this station if this method returns
     * <code>false</code>.
     * @param child a {@link Dockable} which may become a child
     * @return <code>true</code> if <code>child</code> is accepted
     */
    public boolean accept( Dockable child );
    
    /**
     * Gets precise information about the location of a child of this station.
     * The result of this method could later be used to invoke
     * {@link #drop(Dockable, DockableProperty)}.
     * @param child a child of this station, this childs location is asked
     * @param target an optional hint telling for which dockable the location information
     * will be used, can be <code>null</code>. This hint can be used to find a placeholder
     * that should be part of the result.
     * @return the location
     * @see bibliothek.gui.dock.util.DockUtilities#getPropertyChain(DockStation, Dockable)
     */
    public DockableProperty getDockableProperty( Dockable child, Dockable target );
    
    /**
     * Prepares this station to get the new child <code>dockable</code>. The
     * station has to store a possible location of the child, and should draw
     * some indicators where the child will be put. The station can refuse
     * <code>dockable</code>, in this case nothing has to be painted and
     * this method returns <code>false</code>.<br>
     * There are some constraints:
     * <ul>
     * <li>The result should be <code>false</code> if this station is dockable,
     * <code>checkOverrideZone</code> is <code>true</code> and the mouse is in
     * the override-zone. of the parent. However, that condition is just "good manners" and may
     * be broken.</li>
     * <li>This method should use {@link #accept(Dockable)} and {@link Dockable#accept(DockStation)}
     * or {@link Dockable#accept(DockStation, Dockable)} to ensure that the desired
     * drop-location is valid.</li>
     * <li>The method should use the {@link DockAcceptance} of its controller
     * (see {@link DockController#getAcceptance()}) to ensure that the drop/location is valid.</li>
     * </ul>
     * This method gets two points: <code>mouseX/mouseY</code> is the location
     * of the mouse, <code>titleX/titleY</code> is the location of the dragged
     * title. The second point may be interesting if the title of a dropped
     * child should have the same coordinates as the image of the dragged title.<br>
     * This method is never called if <code>dockable</code> is a child of this
     * station. In such a case {@link #prepareMove(int, int, int, int, boolean, Dockable) prepareMove}
     * is invoked. 
     * @param mouseX the x-coordinate of the mouse on the screen
     * @param mouseY the y-coordinate of the mouse on the screen
     * @param titleX the x-location of the dragged title or <code>mouseX</code> if no
     * title is dragged
     * @param titleY the y-location of the dragged title or <code>mouseY</code> if no
     * title is dragged
     * @param checkOverrideZone whether this station has to check if the mouse
     * is in the override-zone of its parent
     * @param dockable the element which will be dropped
     * @return <code>true</code> if <code>dockable</code> can be added at the
     * current location, <code>false</code> otherwise.
     */
    public boolean prepareDrop( int mouseX, int mouseY, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable );
    
    /**
     * Adds the {@link Dockable} of the last run of
     * {@link #prepareDrop(int, int, int, int, boolean, Dockable) prepareDrop} to this station.
     * This method is only called if the new child and this station accepted
     * each other, <code>prepareDrop</code> returned <code>true</code> and
     * the new child is not yet a child of this station.
     */
    public void drop();
    
    /**
     * Adds <code>dockable</code> to this station. The station can decide
     * by its own where to put <code>dockable</code>.
     * @param dockable a new child
     */
    public void drop( Dockable dockable );
    
    /**
     * Tries to add <code>dockable</code> to this station such that the location
     * given by <code>property</code> is matched. If <code>property</code>
     * has a {@link DockableProperty#getSuccessor() successor} and points to
     * another station, just call the <code>drop</code>-method of this
     * child-station. Note that <code>property</code> can be of any type and
     * contain invalid information.
     * @param dockable the new child
     * @param property the location of the child, may be invalid data
     * @return <code>true</code> if <code>property</code> could be read
     * and <code>dockable</code> was dropped, <code>false</code>
     * otherwise.
     */
    public boolean drop( Dockable dockable, DockableProperty property );
    
    /**
     * Prepares the station that one of its children is moved from one
     * location to another location. See {@link #prepareDrop(int, int, int, int, boolean, Dockable) prepareDrop}
     * for detailed information about the behavior of this method. The only
     * difference between this method and <code>prepareDrop</code> is, that
     * <code>dockable</code> is a child of this station.
     * @param mouseX the x-coordinate of the mouse on the screen
     * @param mouseY the y-coordinate of the mouse on the screen
     * @param titleX the x-location of the dragged title or <code>mouseX</code> if no
     * title is dragged
     * @param titleY the y-location of the dragged title or <code>mouseY</code> if no
     * title is dragged
     * @param checkOverrideZone whether this station has to check if the
     * mouse is in the override-zone of its parent
     * @param dockable the element which will be moved
     * @return <code>true</code> if <code>dockable</code> can be added at the
     * current location, <code>false</code> otherwise.
     */
    public boolean prepareMove( int mouseX, int mouseY, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable );
    
    /**
     * Moves a child of this station to a new location according to the
     * information gathered by {@link #prepareMove(int, int, int, int, boolean, Dockable) prepareMove}.
     */
    public void move();
    
    /**
     * Tries to move the child <code>dockable</code> in such a way, that
     * {@link DockStation#getDockableProperty(Dockable, Dockable)} would return a
     * {@link DockableProperty} that equals <code>property</code>.<br>
     * There is no need to give a guarantee that the move successes, and clients
     * should always be prepared for the possibility that this {@link DockStation}
     * does nothing at all.
     * @param dockable a child of this station
     * @param property the preferred position of <code>dockable</code>
     */
    public void move( Dockable dockable, DockableProperty property );
    
    /**
     * Informs this station that the information gathered by 
     * {@link #prepareDrop(int, int, int, int, boolean, Dockable) prepareDrop} or
     * {@link #prepareMove(int, int, int, int, boolean, Dockable) prepareMove} should
     * be painted somehow onto this station.<br>
     * The station should use the {@link StationPaint} of its theme
     * to draw.
     */
    public void draw();
    
    /**
     * Tells this station that a possible drop or move on this station 
     * was canceled. The station can throw away any information gathered by
     * the last call {@link #prepareDrop(int, int, int, int, boolean, Dockable) prepareDrop} 
     * or {@link #prepareMove(int, int, int, int, boolean, Dockable) prepareMove}<br>
     * If the station is drawing some markings because of a call to
     * {@link #draw()}, than the station can throw away these markings too.
     */
    public void forget();
    
    /**
     * If the controller asks a station if a child could be dropped or moved,
     * the controller assumes that no other station has interest in this event.
     * However if this station is a dockable, and has a parent, the parent might
     * be interested in the new child. This dockable station has to ask the
     * parent if the current location of the mouse is in the override-zone. This
     * station should not accept a child if the parent returns <code>true</code>.<br>
     * On the other hand, this station could be asked by a child whether the mouse
     * is in the override-zone. If the mouse hits a point of special interest,
     * then the method should return <code>true</code>.<br>
     * Note: if this station is asked and is a dockable station itself, then
     * this method should ask the parent for his override-zone too.
     * @param x the x-coordinate of the mouse on the screen
     * @param y the y-coordinate of the mouse on the screen
     * @param invoker a child of this station which invoked the method
     * @param drop a {@link Dockable} which might become a child
     * @param <D> the type of <code>invoker</code>
     * @return <code>true</code> if the location of the mouse is of special
     * interest
     */
    public <D extends Dockable & DockStation> boolean isInOverrideZone( int x, int y, D invoker, Dockable drop );
    
    /**
     * Tells whether <code>dockable</code> can be removed from this station or not.
     * This method assumes that <code>dockable</code> is a child of
     * this station, if not, then the behavior of this method is unspecified.<br>
     * Note that the result of this method may not be respected every time,
     * it's more a hint for the controller how to act.
     * @param dockable a child of this station
     * @return <code>true</code> if <code>dockable</code> can be dragged
     */
    public boolean canDrag( Dockable dockable );
    
    /**
     * Removes a child from this station. This method may be called even
     * if {@link #canDrag(Dockable)} returned <code>false</code>.<br>
     * Note: clients may need to invoke {@link DockController#freezeLayout()}
     * and {@link DockController#meltLayout()} to ensure noone else adds or
     * removes <code>Dockable</code>s. 
     * @param dockable the child to remove
     */
    public void drag( Dockable dockable );
    
    /**
     * Tells whether its possible to replace the child <code>old</code>
     * with <code>next</code> where next is not a child of this station.
     * @param old a child of this station
     * @param next the replacement of <code>next</code>.
     * @return <code>true</code> if the replacement is possible
     * @throws IllegalArgumentException if <code>next</code> is a child
     * of this station
     */
    public boolean canReplace( Dockable old, Dockable next );
    
    /**
     * Replaces the child <code>old</code> by <code>next</code> which is
     * not yet a child of this station. This method should not be 
     * called if {@link #canReplace(Dockable, Dockable) canReplace} returned
     * <code>false</code>.
     * @param old a child
     * @param next the replacement of <code>old</code>
     * @throws IllegalArgumentException if <code>next</code> is a child of
     * this station or if <code>old</code> is not a child
     */
    public void replace( Dockable old, Dockable next );
    
    /**
     * Replaces the child <code>old</code> by <code>next</code> which is
     * not yet a child of this station. This method should not be 
     * called if {@link #canReplace(Dockable, Dockable) canReplace} returned
     * <code>false</code>. This method can assume that <code>next</code> was
     * a child of <code>old</code> but no longer is.
     * @param old a dockable station that is a child of this station
     * @param next the replacement of <code>old</code>
     * @throws IllegalArgumentException if <code>next</code> is a child of
     * this station, if <code>old</code> is not a child or if <code>old</code>
     * is not a {@link Dockable}
     */
    public void replace( DockStation old, Dockable next );
    
    /**
     * Gets a rectangle in which all points of the station are. The user is
     * only able to move a {@link Dockable} into this area onto this station. 
     * @return the bounds, relative to the screen, <code>null</code> to indicate that
     * this station has not any bounds
     */
    public Rectangle getStationBounds();
    
    /**
     * Tells whether this station knows a rule how to compare itself with
     * <code>station</code>. See {@link #compare(DockStation)} for more
     * details.
     * @param station another station
     * @return <code>true</code> if a call to {@link #compare(DockStation) compare}
     * will not end in an exception and return another value than 0
     */
    public boolean canCompare( DockStation station );
    
    /**
     * Compares this station with <code>station</code>. The comparison is needed
     * if the {@link #getStationBounds() stations bounds} of the two station
     * have common points. On a drag-event, the controller needs a way to
     * decide which station is more important (and receives the opportunity 
     * to get a new child first). The controller will use the method
     * <code>compare</code> to do this. This method works like
     * {@link Comparable#compareTo(Object)}.
     * @param station another station
     * @return a number less/equal/higher than zero, if this station has
     * higher/equal/lesser priority than <code>station</code>.
     */
    public int compare( DockStation station );
}
