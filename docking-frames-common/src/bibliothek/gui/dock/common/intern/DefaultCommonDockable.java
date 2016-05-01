package bibliothek.gui.dock.common.intern;
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
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.intern.action.CActionSource;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A default implementation of {@link CommonDockable}, based on a {@link DefaultDockable}.
 * @author Benjamin Sigg
 *
 */
public class DefaultCommonDockable extends DefaultDockable implements CommonDockable{
    /** the model */
    private CDockable dockable;
    
    /** the list of actions of this dockable */
    private CActionSource actions;
    
    /** the action source with the potential close action */
    private DockActionSource[] sources;
    
    /**
     * Creates a new dockable
     * @param dockable the model of this element
     * @param sources action source which shows the close action
     */
    public DefaultCommonDockable( CDockable dockable, DockActionSource... sources ){
        this.dockable = dockable;
        this.sources = sources;
        actions = new CActionSource( new LocationHint( LocationHint.DOCKABLE, LocationHint.LEFT ));
        setActionOffers( actions );
        dockable.addCDockablePropertyListener( new CDockableAdapter(){
            @Override
            public void titleShownChanged( CDockable dockable ) {
                fireTitleExchanged();
            }
        });
        
        setFactoryID( CommonSingleDockableFactory.BACKUP_FACTORY_ID );
    }
    
    /**
     * Gets the action source for actions that are added by the client.
     * @return the source, not <code>null</code>
     */
    public CActionSource getActions() {
        return actions;
    }
    
    public CDockable getDockable(){
        return dockable;
    }
    
    @Override
    public CommonDockable asDockable(){
	    return this;
    }
    
    @Override
    public CommonDockStation<?,?> asDockStation(){
    	return null;
    }
    
    public CStation<?> getStation(){
    	return null;
    }
    
    public DockActionSource[] getSources() {
        return sources;
    }
    
    @Override
    public void requestDockTitle( DockTitleRequest request ){
        if( dockable.isTitleShown() ){
        	super.requestDockTitle( request );
        }
        else{
        	DockTitleVersion version = request.getVersion();
        	
	        boolean hide = 
	            version.getID().equals( SplitDockStation.TITLE_ID ) ||
	            version.getID().equals( StackDockStation.TITLE_ID ) ||
	            version.getID().equals( ScreenDockStation.TITLE_ID ) ||
	            version.getID().equals( FlapDockStation.WINDOW_TITLE_ID );
	        
	        if( hide ){
	            request.answer( null );
	        }
	        else{
	         	super.requestDockTitle( request );
	        }
        }
    }
    
    @Override
    public String toString(){
	    return getClass().getSimpleName() + "[dockable=" + dockable + "]";
    }
}
