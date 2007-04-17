/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.themes;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.nostack.NoStackAcceptance;
import bibliothek.gui.dock.themes.nostack.NoStackTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.MovingTitleGetter;

/**
 * A {@link DockTheme} that wraps another theme and ensures that there
 * is no {@link StackDockStation} in another <code>StackDockStation</code>.
 * This theme hides some titles for the {@link StackDockStation}.
 * @author Benjamin Sigg
 */
public class NoStackTheme implements DockTheme {
    /** The delegate theme to get the basic factories */
    private DockTheme base;
    
    /** the acceptances which were used before this theme was installed to a controller */
    private Map<DockController, DockAcceptance> acceptances = new HashMap<DockController, DockAcceptance>();
    
    /**
     * Creates a new theme
     * @param base the wrapped theme, it is used as a delegate to get
     * some factories.
     */
    public NoStackTheme( DockTheme base ){
        if( base == null )
            throw new IllegalArgumentException( "Base theme must not be null" );
        
        this.base = base;
    }
    
    public Combiner getCombiner( DockStation station ) {
        return base.getCombiner( station );
    }

    public DisplayerFactory getDisplayFactory( DockStation station ) {
        return base.getDisplayFactory( station );
    }

    public StationPaint getPaint( DockStation station ) {
        return base.getPaint( station );
    }

    public DockTitleFactory getTitleFactory( DockController controller ) {
        return new NoStackTitleFactory( base.getTitleFactory(controller));
    }

    public MovingTitleGetter getMovingTitleGetter( DockController controller ) {
        return base.getMovingTitleGetter( controller );
    }
    
    public void install( DockController controller ) {
        base.install( controller );
        DockAcceptance acceptance = controller.getAcceptance();
        acceptances.put( controller, acceptance );
        NoStackAcceptance noStack = new NoStackAcceptance();
        if( acceptance == null )
            controller.setAcceptance( noStack );
        else
            controller.setAcceptance( noStack.andAccept( acceptance ));
    }
    
    public void uninstall(DockController controller) {
    	base.uninstall( controller );
    	controller.setAcceptance( acceptances.remove( controller ));
    }
}
