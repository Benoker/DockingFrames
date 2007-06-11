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

package bibliothek.gui.dock.themes;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.DefaultCombiner;
import bibliothek.gui.dock.station.support.DefaultDisplayerFactory;
import bibliothek.gui.dock.station.support.DefaultStationPaint;
import bibliothek.gui.dock.title.DefaultDockTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.MovingTitleGetter;

/**
 * A {@link DockTheme theme} that does not install anything and uses the
 * default-implementations off all factories. It is possible to replace 
 * any of the factories.
 * @author Benjamin Sigg
 */
@ThemeProperties(
        nameBundle="theme.default", 
        descriptionBundle="theme.default.description",
        authors={"Benjamin Sigg"},
        webpages={})
public class DefaultTheme implements DockTheme{
    /** combines several Dockables */
    private Combiner combiner;
    
    /** paints on stations */
    private StationPaint paint;
    
    /** creates panels for Dockables */
    private DisplayerFactory displayerFactory;
    
    /** creates titles Dockables */
    private DockTitleFactory titleFactory;
    
    /** selects the title which should be displayed when moving a dockable*/
    private MovingTitleGetter titleGetter;
    
    /**
     * Creates a new <code>DefaultTheme</code>.
     */
    public DefaultTheme() {
        setCombiner( new DefaultCombiner() );
        setPaint( new DefaultStationPaint() );
        setDisplayerFactory( new DefaultDisplayerFactory() );
        setTitleFactory( new DefaultDockTitleFactory() );
        setMovingTitleGetter( new MovingTitleGetter(){
            public DockTitle get( DockController controller, Dockable dockable ) {
            	return getTitleFactory( controller ).createDockableTitle( dockable, null );
            }
            public DockTitle get( DockController controller, DockTitle snapped ) {
                return snapped;
            }
        });
    }
    
    public void install( DockController controller ) {
    	// do nothing
    }
    
    public void uninstall(DockController controller) {
    	// do nothing
    }
    
    /**
     * Sets the titleGetter-property. The titlegetter is needed to show a 
     * title when the user grabs a {@link Dockable}
     * @param titleGetter the new getter, not <code>null</code>
     */
    public void setMovingTitleGetter( MovingTitleGetter titleGetter ) {
        if( titleGetter == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.titleGetter = titleGetter;
    }
    
    /**
     * Sets the {@link Combiner} of this theme. The combiner is used to
     * merge two Dockables.
     * @param combiner the combiner, not <code>null</code>
     */
    public void setCombiner( Combiner combiner ) {
        if( combiner == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.combiner = combiner;
    }
    
    /**
     * Sets the {@link StationPaint} of this theme. The paint is used to
     * draw markings on stations.
     * @param paint the paint, not <code>null</code>
     */
    public void setPaint( StationPaint paint ) {
        if( paint == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.paint = paint;
    }
    
    /**
     * Sets the {@link DisplayerFactory} of this theme. The factory is needed
     * to create {@link DockableDisplayer}.
     * @param factory the factory, not <code>null</code>
     */
    public void setDisplayerFactory( DisplayerFactory factory ) {
        if( factory == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        displayerFactory = factory;
    }
    
    /**
     * Sets the {@link DockTitleFactory} of this station. The factory is
     * used to create {@link DockTitle DockTitles} for some Dockables.
     * @param titleFactory the factory, not <code>null</code>
     */
    public void setTitleFactory( DockTitleFactory titleFactory ) {
        if( titleFactory == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.titleFactory = titleFactory;
    }
    
    public MovingTitleGetter getMovingTitleGetter( DockController controller ) {
        return titleGetter;
    }
    
    public Combiner getCombiner( DockStation station ) {
        return combiner;
    }

    public StationPaint getPaint( DockStation station ) {
        return paint;
    }

    public DisplayerFactory getDisplayFactory( DockStation station ) {
        return displayerFactory;
    }
    
    public DockTitleFactory getTitleFactory( DockController controller ) {
        return titleFactory;
    }
}
