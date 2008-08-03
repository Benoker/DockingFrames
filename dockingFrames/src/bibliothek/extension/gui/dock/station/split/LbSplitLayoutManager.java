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
package bibliothek.extension.gui.dock.station.split;

import java.awt.Dimension;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.*;

/**
 * This {@link SplitLayoutManager} uses a {@link SizeManager} to find the size
 * a {@link Dockable} should get when dropping onto the {@link SplitDockStation}.
 * 
 * @author Parag Shah
 * @author Benjamin Sigg
 */
public class LbSplitLayoutManager extends DefaultSplitLayoutManager{
    private static final double MINIMUM_ORIGINAL_SIZE = 0.25;
    private static final Dimension DEFAULT_MINIMUM_SIZE = new Dimension(10, 10);

    private SizeManager sizeManager;

    /**
     * Creates a new manager.
     * @param sizeManager information about the size of new {@link Dockable}s
     */
    public LbSplitLayoutManager( SizeManager sizeManager ){
        if( sizeManager == null )
            throw new NullPointerException( "sizeManager must not be null" );

        this.sizeManager = sizeManager;
    }

    @Override
    public void calculateDivider( SplitDockStation station, PutInfo putInfo, Leaf origin ){
        Dockable dockable = putInfo.getDockable();
        if( origin != null ){
            super.calculateDivider(station, putInfo, origin);
        }

        double percentSize = sizeManager.getSize(dockable); 

        if( percentSize < 0 ){
            // no size stored
            super.calculateDivider( station, putInfo, origin );
        }
        else{
            SplitNode other = putInfo.getNode();
            Dimension oldSize = dockable.getComponent().getSize();                      
            int size = Math.min( oldSize.width, oldSize.height );

            double divider = 0.5;
            if( putInfo.getPut() == PutInfo.Put.TOP ){
                divider = 
                    validateDivider(station, 
                            percentSize, 
                            DEFAULT_MINIMUM_SIZE, //dockable.getComponent().getMinimumSize(),
                            DEFAULT_MINIMUM_SIZE, //other.getMinimumSize(), 
                            Orientation.VERTICAL, 
                            other.getWidth(), 
                            other.getHeight());
                if( divider > 1 - MINIMUM_ORIGINAL_SIZE )
                    divider = 1 - MINIMUM_ORIGINAL_SIZE;
            }
            else if(putInfo.getPut() == PutInfo.Put.BOTTOM){
                divider = 
                    validateDivider(station, 
                            1.0-percentSize,
                            DEFAULT_MINIMUM_SIZE, //other.getMinimumSize(),
                            DEFAULT_MINIMUM_SIZE, //dockable.getComponent().getMinimumSize(),                                          
                            Orientation.VERTICAL, 
                            other.getWidth(), 
                            other.getHeight());
                if( divider < MINIMUM_ORIGINAL_SIZE )
                    divider = MINIMUM_ORIGINAL_SIZE;
            }
            else if(putInfo.getPut() == PutInfo.Put.LEFT){
                divider = 
                    validateDivider(station, 
                            percentSize, 
                            DEFAULT_MINIMUM_SIZE, //dockable.getComponent().getMinimumSize(),
                            DEFAULT_MINIMUM_SIZE, //other.getMinimumSize(), 
                            Orientation.HORIZONTAL, 
                            other.getWidth(), other.getHeight() );
                if( divider > 1 - MINIMUM_ORIGINAL_SIZE )
                    divider = 1 - MINIMUM_ORIGINAL_SIZE;
            }
            else if(putInfo.getPut() == PutInfo.Put.RIGHT){
                divider = 
                    validateDivider(station, 
                            1.0-percentSize, 
                            DEFAULT_MINIMUM_SIZE, //other.getMinimumSize(),
                            DEFAULT_MINIMUM_SIZE, //dockable.getComponent().getMinimumSize(),                                          
                            Orientation.HORIZONTAL, 
                            other.getWidth(), 
                            other.getHeight() );
                if( divider < MINIMUM_ORIGINAL_SIZE )
                    divider = MINIMUM_ORIGINAL_SIZE;
            }
            putInfo.setDivider(divider);
            putInfo.setOldSize(size);
        }
    }
}
