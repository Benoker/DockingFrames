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
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.stack.DefaultStackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.themes.basic.*;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;

/**
 * A {@link DockTheme theme} that does not install anything and uses the
 * default-implementations off all factories. It is possible to replace 
 * any of the factories.
 * @author Benjamin Sigg
 */
@ThemeProperties(
        nameBundle="theme.basic", 
        descriptionBundle="theme.basic.description",
        authors={"Benjamin Sigg"},
        webpages={})
public class BasicTheme implements DockTheme{
    /** combines several Dockables */
    private Combiner combiner;
    
    /** paints on stations */
    private StationPaint paint;
    
    /** creates panels for Dockables */
    private DisplayerFactory displayerFactory;
    
    /** creates titles Dockables */
    private DockTitleFactory titleFactory;
    
    /** selects the image which should be displayed when moving a dockable*/
    private DockableMovingImageFactory movingImage;
    
    /** the factory used to create components for {@link StackDockStation} */
    private StackDockComponentFactory stackDockComponentFactory;
    
    /**
     * Creates a new <code>BasicTheme</code>.
     */
    public BasicTheme() {
        setCombiner( new BasicCombiner() );
        setPaint( new BasicStationPaint() );
        setDisplayerFactory( new BasicDisplayerFactory() );
        setTitleFactory( new BasicDockTitleFactory() );
        setMovingImageFactory( new BasicMovingImageFactory() );
        setStackDockComponentFactory( new StackDockComponentFactory(){
            public StackDockComponent create( StackDockStation station ) {
                return new DefaultStackDockComponent();
            }
        });
    }
    
    public void install( DockController controller ) {
        controller.getProperties().set( StackDockStation.COMPONENT_FACTORY, stackDockComponentFactory );
    }
    
    public void uninstall(DockController controller) {
        controller.getProperties().set( StackDockStation.COMPONENT_FACTORY, null );
    }
    
    /**
     * Sets the factory which will be used to create components for 
     * {@link StackDockStation}. Note that this property has to be set
     * before the theme is installed. Otherwise it will take no effect.
     * @param stackDockComponentFactory the factory or <code>null</code>
     */
    public void setStackDockComponentFactory(
            StackDockComponentFactory stackDockComponentFactory ) {
        this.stackDockComponentFactory = stackDockComponentFactory;
    }
    
    /**
     * Sets the movingImage-property. The movignImage is needed to show an
     * image when the user grabs a {@link Dockable}
     * @param movingImage the new factory, not <code>null</code>
     */
    public void setMovingImageFactory( DockableMovingImageFactory movingImage ) {
        if( movingImage == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.movingImage = movingImage;
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
    
    public DockableMovingImageFactory getMovingImageFactory( DockController controller ) {
        return movingImage;
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
