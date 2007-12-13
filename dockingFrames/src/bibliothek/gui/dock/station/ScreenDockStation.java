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

package bibliothek.gui.dock.station;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.station.screen.ScreenDockDialog;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.screen.ScreenDockStationFactory;
import bibliothek.gui.dock.station.support.CombinerWrapper;
import bibliothek.gui.dock.station.support.DisplayerFactoryWrapper;
import bibliothek.gui.dock.station.support.DockableVisibilityManager;
import bibliothek.gui.dock.station.support.StationPaintWrapper;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A {@link DockStation} which is the whole screen. Every child of this
 * station is a non modal dialog. These dialogs do not have a border or
 * a title (except a {@link DockTitle}), but they can be moved and resized
 * by the user.<br>
 * This station tries to register a {@link DockTitleVersion} with 
 * the key {@link #TITLE_ID}.
 * 
 * @author Benjamin Sigg
 */
public class ScreenDockStation extends AbstractDockStation {
    /** The key for the {@link DockTitleVersion} of this station */
    public static final String TITLE_ID = "screen dock";
    
    /** The visibility state of the dialogs */
    private boolean showing = false;
    
    /** A list of all dialogs that are used by this station */
    private List<ScreenDockDialog> dockables = new ArrayList<ScreenDockDialog>();
    
    /** The version of titles that are used */
    private DockTitleVersion version;
    
    /** Combiner to merge some {@link Dockable Dockables} */
    private CombinerWrapper combiner = new CombinerWrapper();
    
    /** Information about the current movement of a {@link Dockable} */
    private DropInfo dropInfo;
    
    /** The {@link Window} that is used as parent for the dialogs */
    private Window owner;
    
    /** The paint used to draw information on this station */
    private StationPaintWrapper stationPaint = new StationPaintWrapper();
    
    /** A factory to create new {@link DockableDisplayer}*/
    private DisplayerFactoryWrapper displayerFactory = new DisplayerFactoryWrapper();
    
    /** The set of {@link DockableDisplayer} used on this station */
    private DisplayerCollection displayers;
    
    /** The dialog which has currently the focus */
    private ScreenDockDialog frontDialog;
    
    /** A listener for dialogs */
  //  private DialogListener dialogListener = new DialogListener();
    
    /** A manager for the visibility of the children */
    private DockableVisibilityManager visibility;
    
    /**
     * Constructs a new <code>ScreenDockStation</code>.
     * @param owner the window which will be used as parent for the 
     * dialogs of this station
     */
    public ScreenDockStation( Window owner ){
    	if( owner == null )
            throw new IllegalArgumentException( "Owner must not be null" );
        visibility = new DockableVisibilityManager( listeners );
        this.owner = owner;
        
        displayers = new DisplayerCollection( this, displayerFactory );
    }
    
    /**
     * Gets the {@link DisplayerFactory} that is used by this station
     * to create an underground for its children.
     * @return the factory
     * @see DisplayerFactoryWrapper#setDelegate(DisplayerFactory)
     */
    public DisplayerFactoryWrapper getDisplayerFactory() {
        return displayerFactory;
    }
    
    /**
     * Gets the current set of {@link DockableDisplayer displayers} used
     * on this station.
     * @return the set of displayers
     */
    public DisplayerCollection getDisplayers() {
        return displayers;
    }
    
    /**
     * Gets the {@link Combiner} that is used to merge two {@link Dockable Dockables}
     * on this station.
     * @return the combiner
     * @see CombinerWrapper#setDelegate(Combiner)
     */
    public CombinerWrapper getCombiner() {
        return combiner;
    }
    
    /**
     * Gets the {@link StationPaint} for this station. The paint is needed to
     * paint information on this station, when a {@link Dockable} is dragged
     * or moved.
     * @return the paint
     * @see StationPaintWrapper#setDelegate(StationPaint)
     */
    public StationPaintWrapper getPaint() {
        return stationPaint;
    }
    
    @Override
    protected void callDockUiUpdateTheme() throws IOException {
    	DockUI.updateTheme( this, new ScreenDockStationFactory( owner ) );
    }
    
    @Override
    public void setController( DockController controller ) {
        if( getController() != null ){
            for( ScreenDockDialog dialog : dockables ){
                DockableDisplayer displayer = dialog.getDisplayer();
                DockTitle title = displayer.getTitle();
                Dockable dockable = displayer.getDockable();
                if( title != null && dockable != null ){
                    dockable.unbind( title );
                    displayer.setTitle( null );
                }
            }
            
            version = null;
        }
        
        super.setController(controller);
        displayers.setController( controller );
        
        if( controller != null ){
            version = controller.getDockTitleManager().registerDefault( TITLE_ID, ControllerTitleFactory.INSTANCE );
            
            for( ScreenDockDialog dialog : dockables ){
                DockableDisplayer displayer = dialog.getDisplayer();
                Dockable dockable = displayer.getDockable();
                DockTitle title = dockable.getDockTitle( version );
                if( title != null )
                    dockable.bind( title );
                displayer.setTitle( title );
            }
        }
    }
    
    
    public DefaultDockActionSource getDirectActionOffers( Dockable dockable ) {
        return null;
    }

    public DefaultDockActionSource getIndirectActionOffers( Dockable dockable ) {
        return null;
    }

    public int getDockableCount() {
        return dockables.size();
    }

    public Dockable getDockable( int index ) {
        return dockables.get( index ).getDisplayer().getDockable();
    }
    
    /**
     * Gets the index of a {@link Dockable} that is shown on this
     * station. A call to {@link #getDockable(int)} with the result of this
     * method would return <code>dockable</code>, if <code>dockable</code>
     * is on this station.
     * @param dockable the item to search
     * @return the index of the item or -1 if not found
     */
    public int indexOf( Dockable dockable ){
        for( int i = 0, n = dockables.size(); i<n; i++ ){
            ScreenDockDialog dialog = dockables.get( i );
            if( dialog.getDisplayer() != null ){
                if( dialog.getDisplayer().getDockable() == dockable )
                    return i;
            }
        }
        
        return -1;
    }

    public Dockable getFrontDockable() {
        if( frontDialog == null )
            return null;
        else
            return frontDialog.getDisplayer().getDockable();
    }

    public void setFrontDockable( Dockable dockable ) {
        frontDialog = getDialog( dockable );

        if( frontDialog != null ){
            frontDialog.toFront();
        }
    }

    public boolean prepareDrop( int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
        return prepare( x, y, titleX, titleY, dockable, true );
    }
    
    public boolean prepare( int x, int y, int titleX, int titleY, Dockable dockable, boolean drop ) {
        if( dropInfo == null )
            dropInfo = new DropInfo();
        
        ScreenDockDialog oldCombine = dropInfo.combine;
        
        dropInfo.x = x;
        dropInfo.y = y;
        dropInfo.titleX = titleX;
        dropInfo.titleY = titleY;
        dropInfo.dockable = dockable;
        dropInfo.combine = searchCombineDockable( x, y, dockable );
        
        if( dropInfo.combine != null && dropInfo.combine.getDisplayer().getDockable() == dockable )
            dropInfo.combine = null;
        
        if( dropInfo.combine != oldCombine ){
            if( oldCombine != null )
                oldCombine.repaint();
            
            if( dropInfo.combine != null )
                dropInfo.combine.repaint();
        }
        
        checkDropInfo();
        return dropInfo != null;
    }

    
    /**
     * Ensures that the desired location where to insert the next child is valid
     * If not, then {@link #dropInfo} is set to <code>null</code>
     */
    private void checkDropInfo(){
        if( dropInfo != null ){
            if( dropInfo.combine != null ){
                if( !accept( dropInfo.dockable ) || 
                        !dropInfo.dockable.accept( this, dropInfo.combine.getDisplayer().getDockable() ) ||
                        !getController().getAcceptance().accept( this, dropInfo.combine.getDisplayer().getDockable(), dropInfo.dockable )){
                    dropInfo = null;
                }
            }
            else{
                if( !accept( dropInfo.dockable ) ||
                        !dropInfo.dockable.accept( this ) ||
                        !getController().getAcceptance().accept( this, dropInfo.dockable )){
                    dropInfo = null;
                }
            }
        }
    }

    
    /**
     * Searches a dialog on the coordinates x/y which can be used to create
     * a combination with <code>drop</code>.
     * @param x the x-coordinate on the screen
     * @param y die y-coordinate on the screen
     * @param drop the {@link Dockable} which might be combined with a dialog
     * @return the dialog which might become the parent of <code>drop</code>.
     */
    protected ScreenDockDialog searchCombineDockable( int x, int y, Dockable drop ){
        DockAcceptance acceptance = getController() == null ? null : getController().getAcceptance();
        
        for( ScreenDockDialog dialog : dockables ){
        	DockableDisplayer displayer = dialog.getDisplayer();
            Point point = new Point( x, y );
            SwingUtilities.convertPointFromScreen( point, displayer.getComponent() );
            if( displayer.titleContains( point.x, point.y ) ){
                Dockable child = dialog.getDisplayer().getDockable();
                
                if( acceptance == null || 
                        acceptance.accept( this, child, drop )){
                
                    if( drop.accept( this, child ) &&
                            child.accept( this, drop )){
                        return dialog;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Tells whether there should be a "selection-rectangle" painted on the 
     * <code>dialog</code> or not. This is needed while a {@link Dockable}
     * is dragged around.
     * @param dialog the asking dialog
     * @return <code>true</code> if something should be painted, <code>false</code> 
     * otherwise
     */
    public boolean shouldDraw( ScreenDockDialog dialog ){
        return dropInfo != null && dropInfo.draw && dropInfo.combine == dialog;
    }
    
    public void drop() {
        if( dropInfo.combine != null ){
            combine( dropInfo.combine.getDisplayer().getDockable(), dropInfo.dockable );
        }
        else{
            Component component = dropInfo.dockable.getComponent();
            Rectangle bounds = new Rectangle( dropInfo.titleX, dropInfo.titleY, component.getWidth(), component.getHeight() );
            addDockable( dropInfo.dockable, bounds, false );
        }
    }

    public void drop( Dockable dockable ) {
        int x = owner.getX() + 30;
        int y = owner.getY() + 30;
        Dimension preferred = dockable.getComponent().getPreferredSize();
        Rectangle rect = new Rectangle( x, y, Math.max( preferred.width, 100 ), Math.max( preferred.height, 100 ));
        addDockable( dockable, rect );
    }

    public DockableProperty getDockableProperty( Dockable dockable ) {
        ScreenDockDialog dialog = getDialog( dockable );
        return new ScreenDockProperty( dialog.getX(), dialog.getY(), dialog.getWidth(), dialog.getHeight() );
    }
    
    /**
     * Searches the {@link ScreenDockDialog} which displays the <code>dockable</code>.<br>
     * Note: don't change the {@link DockableDisplayer} or the
     * {@link Dockable} of the dialog.
     * @param dockable the {@link Dockable} to search
     * @return the dialog or <code>null</code>
     */
    public ScreenDockDialog getDialog( Dockable dockable ){
        int index = indexOf( dockable );
        if( index < 0 )
            return null;
        
        return dockables.get( index );
    }
    
    /**
     * Get's the <code>index</code>'th dialog of this station. The number
     * of dialogs is identical to the {@link #getDockableCount() number of Dockables}.
     * @param index the index of the dialog
     * @return the dialog which shows the index'th Dockable.
     */
    public ScreenDockDialog getDialog( int index ){
        return dockables.get( index );
    }

    public boolean prepareMove( int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
        return prepare( x, y, titleX, titleY, dockable, false );
    }

    public void move() {
        if( dropInfo.combine != null ){
            combine( dropInfo.combine.getDisplayer().getDockable(), dropInfo.dockable );
        }
        else{
            ScreenDockDialog dialog = getDialog( dropInfo.dockable );
            
            DockTitle title = dialog.getDisplayer().getTitle();
            Point zero = new Point( 0, 0 );
            if( title != null )
                zero = SwingUtilities.convertPoint( title.getComponent(), zero, dialog );
            
            dialog.setBoundsInScreen( dropInfo.titleX - zero.x, dropInfo.titleY - zero.y, dialog.getWidth(), dialog.getHeight() );
        }
    }

    public void draw() {
        if( dropInfo == null )
            dropInfo = new DropInfo();
        
        dropInfo.draw = true;
        if( dropInfo.combine != null )
            dropInfo.combine.repaint();
    }

    public void forget() {
        if( dropInfo != null ){
            dropInfo.draw = false;
            if( dropInfo.combine != null )
                dropInfo.combine.repaint();
            dropInfo = null;
        }
    }

    public <D extends Dockable & DockStation> boolean isInOverrideZone( int x,
            int y, D invoker, Dockable drop ) {
        
        return searchCombineDockable( x, y, drop ) != null;
    }

    public boolean canDrag( Dockable dockable ) {
        return true;
    }

    public void drag( Dockable dockable ) {
        removeDockable( dockable );
    }

    /**
     * Adds a {@link Dockable} on a newly created {@link ScreenDockDialog} to
     * the station. If the station {@link #isShowing() is visible}, the dialog
     * will be made visible too.
     * @param dockable the {@link Dockable} to show
     * @param bounds the bounds that the dialog will have
     */
    public void addDockable( Dockable dockable, Rectangle bounds ){
        addDockable( dockable, bounds, true );
    }

    /**
     * Adds a {@link Dockable} on a newly created {@link ScreenDockDialog} to
     * the station. If the station {@link #isShowing() is visible}, the dialog
     * will be made visible too.
     * @param dockable the {@link Dockable} to show
     * @param bounds the bounds that the dialog will have
     * @param boundsIncludeTitle if <code>true</code>, the bounds describe the size
     * of the resulting window. Otherwise the size of the window will be a bit larger
     * such that the title can be shown in the new space
     */
    public void addDockable( Dockable dockable, Rectangle bounds, boolean boundsIncludeTitle ){
        DockUtilities.ensureTreeValidity( this, dockable );
        
        if( bounds == null )
            throw new IllegalArgumentException( "Bounds must not be null" );
        
        listeners.fireDockableAdding( dockable );
        
        ScreenDockDialog dialog = createDialog();
        DockTitle title = null;
        if( version != null ){
            title = dockable.getDockTitle( version );
            if( title != null )
                dockable.bind( title );
        }
        
        DockableDisplayer displayer = getDisplayers().fetch( dockable, title );
        
        register( dialog );
        dialog.setDisplayer( displayer );
        
        bounds = new Rectangle( bounds );
        if( !boundsIncludeTitle && title != null ){
            Dimension titleSize = title.getComponent().getPreferredSize();
            
            switch( displayer.getTitleLocation() ){
                case TOP:
                    bounds.y -= titleSize.height;
                case BOTTOM:
                    bounds.height += titleSize.height;
                    break;
                case LEFT:
                    bounds.x -= titleSize.width;
                case RIGHT:
                    bounds.width += titleSize.width;
                    break;
            }
        }
        
        if( !boundsIncludeTitle ){
            Component component = displayer == null ? null : displayer.getComponent();
            while( component != null ){
                if( component instanceof JComponent ){
                    JComponent jcomponent = (JComponent)component;
                    Insets insets = jcomponent.getInsets();
                    if( insets != null ){
                        bounds.x -= insets.left;
                        bounds.y -= insets.top;
                        bounds.width += insets.left + insets.right;
                        bounds.height += insets.top + insets.bottom;
                    }
                }
                component = component.getParent();
            }
        }
        
        
        dialog.setBoundsInScreen( bounds );
        dialog.validate();
        
        Point zero = new Point( 0, 0 );
        zero = SwingUtilities.convertPoint( displayer.getComponent(), zero, dialog );
        dialog.setBoundsInScreen( dialog.getX() - zero.x, dialog.getY() - zero.y, dialog.getWidth(), dialog.getHeight() );
        
        if( isShowing() )
            dialog.setVisible( true );
        
        dockable.setDockParent( this );
        listeners.fireDockableAdded( dockable );
    }
    
    public boolean drop( Dockable dockable, DockableProperty property ){
        if( property instanceof ScreenDockProperty )
            return drop( dockable, (ScreenDockProperty)property );
        else
            return false;
    }
    
    /**
     * Tries to add the <code>dockable</code> to this station, and uses
     * the <code>property</code> to determine its location. If the preferred
     * location overlaps an existing dialog, then the {@link Dockable} may be
     * added to a child-station of this station.
     * @param dockable the new {@link Dockable}
     * @param property the preferred location of the dockable
     * @return <code>true</code> if the dockable could be added, <code>false</code>
     * otherwise.
     */
    public boolean drop( Dockable dockable, ScreenDockProperty property ){
        return drop( dockable, property, true );
    }
    
    /**
     * Tries to add the <code>dockable</code> to this station, and uses
     * the <code>property</code> to determine its location. If the preferred
     * location overlaps an existing dialog, then the {@link Dockable} may be
     * added to a child-station of this station.
     * @param dockable the new {@link Dockable}
     * @param property the preferred location of the dockable
     * @param boundsIncludeTitle if <code>true</code>, the bounds describe the size
     * of the resulting window. Otherwise the size of the window will be a bit larger
     * such that the title can be shown in the new space
     * @return <code>true</code> if the dockable could be added, <code>false</code>
     * otherwise.
     */
    public boolean drop( Dockable dockable, ScreenDockProperty property, boolean boundsIncludeTitle ){
        DockUtilities.ensureTreeValidity( this, dockable );
        ScreenDockDialog best = null;
        double bestRatio = 0;
        
        int x = property.getX();
        int y = property.getY();
        int width = property.getWidth();
        int height = property.getHeight();
        
        double propertySize = width * height;
        
        Rectangle bounds = new Rectangle();
        
        for( ScreenDockDialog dialog : dockables ){
            bounds = dialog.getBounds( bounds );
            double dialogSize = bounds.width * bounds.height;
            bounds = SwingUtilities.computeIntersection( x, y, width, height, bounds );
            
            if( !(bounds.width == 0 || bounds.height == 0) ){
                double size = bounds.width * bounds.height;
                double max = Math.max( propertySize, dialogSize );
                double ratio = size / max;
                
                if( ratio > bestRatio ){
                    bestRatio = max;
                    best = dialog;
                }
            }
        }
        
        boolean done = false;
        
        if( bestRatio > 0.75 ){
            DockableProperty successor = property.getSuccessor();
            Dockable dock = best.getDisplayer().getDockable();
            if( successor != null ){
                DockStation station = dock.asDockStation();
                if( station != null )
                    done = station.drop( dockable, successor );
            }
            
            if( !done ){
                Dockable old = best.getDisplayer().getDockable();
                if( old.accept( this, dockable ) && dockable.accept( this, old )){
                    combine( old, dockable );
                    done = true;
                }
            }
        }
        
        if( !done ){
            boolean accept = accept( dockable ) && dockable.accept( this );
            if( accept ){
                addDockable( dockable, new Rectangle( x, y, width, height ), boundsIncludeTitle );
                done = true;
            }
        }
        
        return done;
    }
    
    /**
     * Combines the <code>lower</code> and the <code>upper</code> {@link Dockable}
     * to one {@link Dockable}, and replaces the <code>lower</code> with
     * this new Dockable. There are no checks whether this station 
     * {{@link #accept(Dockable) accepts} the new child or the children
     * can be combined. The creation of the new {@link Dockable} is done
     * by the {@link #getCombiner() combiner}.
     * @param lower a {@link Dockable} which must be child of this station
     * @param upper a {@link Dockable} which may be child of this station
     */
    public void combine( Dockable lower, Dockable upper ){
        removeDockable( upper );
        int index = indexOf( lower );
        
        listeners.fireDockableRemoving( lower );
        ScreenDockDialog dialog = dockables.get( index );
        DockableDisplayer displayer = dialog.getDisplayer();
        dialog.setDisplayer( null );
        DockTitle title = displayer.getTitle();
        if( title != null )
            displayer.getDockable().unbind( title );
        getDisplayers().release( displayer );
        lower.setDockParent( null );
        listeners.fireDockableRemoved( lower );
        
        Dockable valid = combiner.combine( lower, upper, this );
        
        listeners.fireDockableAdding( valid );
        title = null;
        if( version != null ){
            title = valid.getDockTitle( version );
            if( title != null )
                valid.bind( title );
        }
        
        displayer = getDisplayers().fetch( valid, title );
        dialog.setDisplayer( displayer );
        valid.setDockParent( this );
        listeners.fireDockableAdded( valid );
        
        dialog.validate();
    }
    
    public boolean canReplace( Dockable old, Dockable next ) {
        return true;
    }

    public void replace( Dockable current, Dockable other ){
        ScreenDockDialog dialog = getDialog( current );
        DockableDisplayer displayer = dialog.getDisplayer();
        
        listeners.fireDockableRemoving( current );
        DockTitle title = displayer.getTitle();
        if( title != null )
            displayer.getDockable().unbind( title );
        getDisplayers().release( displayer );
        current.setDockParent( null );
        listeners.fireDockableRemoved( current );
        
        listeners.fireDockableAdding( other );
        if( version != null ){
            title = other.getDockTitle( version );
            if( title != null )
                other.bind( title );
        }
        else
            title = null;
        
        displayer = getDisplayers().fetch( other, title );
        dialog.setDisplayer( displayer );
        other.setDockParent( this );
        listeners.fireDockableAdded( other );
    }
    
    /**
     * Removes the <code>dockable</code> from this station.
     * @param dockable the {@link Dockable} to remove
     */
    public void removeDockable( Dockable dockable ){
        int index = indexOf( dockable );
        
        if( index >= 0 ){
            removeDockable( index );
        }
    }
    
    /**
     * Removes the <code>index</code>'th {@link Dockable} of this station.
     * @param index the index of the {@link Dockable} to remove
     */
    public void removeDockable( int index ){
        ScreenDockDialog dialog = dockables.get( index );
        Dockable dockable = dialog.getDisplayer().getDockable();
        
        listeners.fireDockableRemoving( dockable );
        
        dockables.remove( index );
        dialog.dispose();
        
        DockableDisplayer displayer = dialog.getDisplayer();
        DockTitle title = displayer.getTitle();
        getDisplayers().release( displayer );
        dialog.setDisplayer( null );
        
        if( title != null )
            dockable.unbind( title );
        
        dockable.setDockParent( null );
        deregister( dialog );
        listeners.fireDockableRemoved( dockable );
    }
    
    /**
     * Invoked after a new {@link ScreenDockDialog} has been created. This
     * method adds some listeners to the dialog. If the method is overridden,
     * it should be called from the subclass to ensure the correct function
     * of this station.
     * @param dialog the dialog which was newly created
     */
    protected void register( ScreenDockDialog dialog ){
 //       dialog.addWindowFocusListener( dialogListener );
        dockables.add( dialog );
    }
    
    /**
     * Invoked when a {@link ScreenDockDialog} is no longer needed. This
     * method removes some listeners from the dialog. If overridden
     * by a subclass, the subclass should ensure that this implementation
     * is invoked too.
     * @param dialog the old dialog
     */
    protected void deregister( ScreenDockDialog dialog ){
 //       dialog.removeWindowFocusListener( dialogListener );
        if( frontDialog == dialog )
            frontDialog = null;
        dockables.remove( dialog );
    }
    
    /**
     * Creates a new dialog which is associated with this station.
     * @return the new dialog
     * @throws IllegalStateException if the {{@link #getOwner() owner}
     * of this station is neither a {@link Dialog} nor a {@link Frame}.
     */
    public ScreenDockDialog createDialog(){
        Window window = getOwner();
        if( window instanceof Dialog )
            return new ScreenDockDialog( this, (Dialog)window );
        else if( window instanceof Frame )
            return new ScreenDockDialog( this, (Frame)window );
        else
            throw new IllegalStateException( "Owner is not a frame or a dialog" );
    }
    
    /**
     * Gets the owner of this station. The owner is forwarded to some
     * dialogs as their owner. So the dialogs will always remain in the
     * foreground.
     * @return the owner
     */
    public Window getOwner(){
        return owner;
    }
    
    /**
     * Tells whether this station shows its children or not.
     * @return <code>true</code> if the dialogs are visible, <code>false</code>
     * otherwise
     * @see #setShowing(boolean)
     */
    public boolean isShowing() {
        return showing;
    }
    
    /**
     * Sets the visibility of all dialogs of this station.
     * @param showing <code>true</code> if all dialogs should be visible,
     * <code>false</code> otherwise.
     */
    public void setShowing( boolean showing ){
        if( this.showing != showing ){
            this.showing = showing;
            for( ScreenDockDialog dialog : dockables ){
                dialog.setVisible( showing );
            }
            visibility.fire();
        }
    }
        
    public Rectangle getStationBounds() {
        return null;
    }

    public Dockable asDockable() {
        return null;
    }

    public DockStation asDockStation() {
        return this;
    }

    public String getFactoryID() {
        return ScreenDockStationFactory.ID;
    }

    @Override
    public boolean canCompare( DockStation station ) {
        return true;
    }
    
    @Override
    public int compare( DockStation station ) {
        return -1;
    }
    
    /**
     * Information where a {@link Dockable} will be dropped. This class
     * is used only while a Dockable is dragged and this station has answered
     * as possible parent.
     */
    private class DropInfo{
        /** The Dockable which is dragged */
        public Dockable dockable;
        /** Location of the mouse */
        public int x, y, titleX, titleY;
        /** Possible new parent */
        public ScreenDockDialog combine;
        /** <code>true</code> if some sort of selection should be painted */
        public boolean draw;
    }
    
    /**
     * A listener to the {@link ScreenDockDialog dialogs} of the enclosing
     * {@link ScreenDockStation}. This listener ensures that a dialog with
     * focus has also the {@link DockController#getFocusedDockable() focused} {@link Dockable}
     *//*
    private class DialogListener extends WindowAdapter{
        @Override
        public void windowGainedFocus( WindowEvent e ) {
            DockController controller = getController();
            if( controller != null && !controller.isOnPut() && !controller.isOnFocusing() ){
                Dockable dockable = ((ScreenDockDialog)e.getWindow()).getDisplayer().getDockable();
                controller.setAtLeastFocusedDockable( dockable );
            }
        }
    }*/
}
