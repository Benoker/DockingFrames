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

import javax.swing.JFrame;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.gui.dock.station.StationDragOperation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayerFactory;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

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
 * {@link #prepareDrop(StationDropItem)} need to be implemented for this. Also {@link #accept(Dockable)}, {@link Dockable#accept(DockStation)},
 * {@link Dockable#accept(DockStation, Dockable)} and the {@link DockAcceptance} of the current {@link DockController} should be checked before
 * allowing a drag and drop operation.</li>
 *  <li>For each child there should be at least one {@link DockTitle}. This station needs to derive a {@link DockTitleVersion} from
 *  its controller using the {@link DockController#getDockTitleManager() DockTitleManager} and its 
 *  {@link DockTitleManager#registerDefault(String, DockTitleFactory) factory method}. With the {@link DockTitleVersion}-object one
 *  {@link DockTitleRequest} for each required {@link DockTitle} can be created.</li>
 *  <li>One child can be focused. If this station changes the focus it should use {@link DockController#setAtLeastFocusedDockable(Dockable, Component)}.</li>
 *  <li>This station should support placeholders. The current {@link PlaceholderStrategy} can be used to convert {@link Dockable}s to placeholders. 
 *  A {@link PlaceholderList} is a good data structure to store {@link Dockable}s and placeholders at the same time.</li>
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
     * @deprecated use {@link #isChildShowing(Dockable)} instead, this method will be removed in a future release
     */
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )
    @Deprecated
    public boolean isVisible( Dockable dockable );

    /**
     * Tells whether the child <code>dockable</code> is visible or not. Visible
     * means that the {@link Dockable#getComponent() component} of <code>dockable</code>
     * can be seen by the user. The result must be <code>false</code> if
     * this station is not visible.
     * @param dockable the child whose visibility-state is questioned
     * @return whether <code>dockable</code> is visible or not
     * @see #isStationVisible()
     */
    public boolean isChildShowing( Dockable dockable );
    
    /**
     * Tells whether this station is visible or not. For example a station on 
     * a {@link JFrame} is not visible if the frame is minimized.
     * @return whether this station is visible
     * @deprecated use {@link #isStationShowing()} instead, this method will be removed in a future release
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )
    public boolean isStationVisible();
    
    /**
     * Tells whether this station is visible or not. For example a station on 
     * a {@link JFrame} is not visible if the frame is minimized.
     * @return whether this station is visible
     */
    public boolean isStationShowing();
    
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
     * Allows access to the placeholders that are stored on this station. Clients may use the {@link PlaceholderMapping} to add or remove
     * placeholders from the station.<br>
     * Not all stations support placeholders, and these station may return a mapping that performs no actions. 
     * @return access to the placeholders
     */
    public PlaceholderMapping getPlaceholderMapping();
    
    /**
     * Gets a snapshot of all placeholders that are currently stored in this {@link DockStation}. 
     * A {@link DockStation} is free in the format it chooses to fill the map. The map is to be 
     * created with the assumptions that {@link #getDockableCount()} is <code>0</code>, meaning
     * any existing {@link Dockable} gets replaced by its placeholder. The current 
     * {@link PlaceholderStrategy} should be used to convert {@link Dockable}s to placeholders.<br>
     * Clients interested in modifying the placeholders of this station should call {@link #getPlaceholderMapping()}.
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
     * should call the method <code>title.{@link DockTitle#changed(bibliothek.gui.dock.title.DockTitleEvent) changed}</code>
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
	 * Provides a {@link DockableDisplayer} for a child of this station. This method
	 * must call {@link DisplayerRequest#answer(DockableDisplayer)} to set the result.<br>
	 * The usual implementation should be to do nothing.
	 * @param request the request to answer, not <code>null</code>
	 */
	public void requestChildDisplayer( DisplayerRequest request );
    
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
     * @param child a child of this station, this child's location is asked
     * @param target an optional hint telling for which dockable the location information
     * will be used, can be <code>null</code>. This hint can be used to find a placeholder
     * that should be part of the result.
     * @return the location
     * @see bibliothek.gui.dock.util.DockUtilities#getPropertyChain(DockStation, Dockable)
     */
    public DockableProperty getDockableProperty( Dockable child, Dockable target );
    
    /**
     * Requests this {@link DockStation} to find a {@link DockableProperty} that is "aside" another
     * property. What exactly the meaning of "aside" is, is left to the implementation. Usually
     * "aside" means on the same "stack" but with the next higher index.<br>
     * The <code>request</code> object contains information about the location whose neighbor is
     * searched, about a placeholder that should be used for the new location, and offers methods
     * to forward the call to the next {@link DockStation} or {@link Combiner} if there is a
     * {@link DockableProperty#getSuccessor() successor}.<br>
     * This method should call the <code>answer</code> methods of <code>request</code> with every
     * kind of information it finds.<br>
     * If this method cannot handle <code>request</code>, then it just has to return without calling
     * any of the <code>answer</code> methods. 
     * @param request information about a location and methods to create the neighbor location
     * @see Combiner#aside(AsideRequest)
     */
    public void aside( AsideRequest request );
    
    /**
     * Prepares this station to get the new child <code>dockable</code> or to move around the known child <code>dockable</code>.
     * The station can refuse <code>dockable</code>, in this case this method just returns <code>null</code>.
     * There are some constraints:
     * <ul>
     * <li>This method should use {@link #accept(Dockable)} and {@link Dockable#accept(DockStation)}
     * or {@link Dockable#accept(DockStation, Dockable)} to ensure that the desired
     * drop-location is valid.</li>
     * <li>The method should use the {@link DockAcceptance} of its controller
     * (see {@link DockController#getAcceptance()}) to ensure that the drop/location is valid.</li>
     * </ul>
     * @param dockable information about the dockable that is going to be dropped
     * @return an object describing where the {@link Dockable} can be dropped or <code>null</code> if
     * no drop operation is possible
     */
    public StationDropOperation prepareDrop( StationDropItem dockable );
    
    /**
     * Informs this station that a drag and drop operation is in progress and that <code>dockable</code> might
     * be removed from this station.
     * @param dockable the child that might be removed in the near future
     * @return a callback that will be informed when the dockable was removed or the operation canceled, can be <code>null</code>
     */
    public StationDragOperation prepareDrag( Dockable dockable );
    
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
     * Gets a description of all the areas of the screen where this {@link DockStation} can handle a drop event.<br>
     * Every time the mouse is moved or released during a drag &amp; drop operation, this method is called for
     * all {@link DockStation}s. The returned {@link DockStationDropLayer}s are then filtered and ordered, the resulting
     * order defines the order in which the method {@link #prepareDrop(StationDropItem)} is called.
     * @return all the layers of this station, must not be <code>null</code>, must not contain <code>null</code>,
     * must not contain the same entry twice. The array or the {@link DockStationDropLayer}s may be modified,
     * hence this method should always create new objects.
     * @see DockStationDropLayerFactory
     */
    public DockStationDropLayer[] getLayers();
    
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
     * and {@link DockController#meltLayout()} to ensure no-one else adds or
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
}
