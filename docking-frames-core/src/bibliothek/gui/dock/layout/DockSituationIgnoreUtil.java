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
package bibliothek.gui.dock.layout;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;

/**
 * Utility class for {@link DockSituationIgnore}.
 * @author Benjamin Sigg
 *
 */
public abstract class DockSituationIgnoreUtil {
    private DockSituationIgnoreUtil(){
        // nothing
    }
    
    /**
     * Returns a new {@link DockSituationIgnore} which returns only <code>true</code>
     * when all the <code>ignores</code> return <code>true</code>.
     * @param ignores the ignores to put together
     * @return the new ignore
     */
    public static DockSituationIgnore and( final DockSituationIgnore... ignores ){
        return new DockSituationIgnore(){
            public boolean ignoreChildren( DockStation station ) {
                for( DockSituationIgnore ignore : ignores ){
                    if( !ignore.ignoreChildren( station ))
                        return false;
                }
                return true;
            }
            public boolean ignoreChildren( PerspectiveStation station ){
        	   for( DockSituationIgnore ignore : ignores ){
                   if( !ignore.ignoreChildren( station ))
                       return false;
               }
               return true;
            }
            public boolean ignoreElement( DockElement element ) {
                for( DockSituationIgnore ignore : ignores ){
                    if( !ignore.ignoreElement( element ))
                        return false;
                }
                return true;
            }
            public boolean ignoreElement( PerspectiveElement element ){
                for( DockSituationIgnore ignore : ignores ){
                    if( !ignore.ignoreElement( element ))
                        return false;
                }
                return true;
            }
        };
    }
    
    /**
     * Returns a new {@link DockSituationIgnore} which returns only <code>false</code>
     * when all the <code>ignores</code> return <code>false</code>.
     * @param ignores the ignores to put together
     * @return the new ignore
     */
    public static DockSituationIgnore or( final DockSituationIgnore... ignores ){
        return new DockSituationIgnore(){
            public boolean ignoreChildren( DockStation station ) {
                for( DockSituationIgnore ignore : ignores ){
                    if( !ignore.ignoreChildren( station ))
                        return true;
                }
                return false;
            }
            public boolean ignoreChildren( PerspectiveStation station ){
                for( DockSituationIgnore ignore : ignores ){
                    if( !ignore.ignoreChildren( station ))
                        return true;
                }
                return false;
            }
            public boolean ignoreElement( DockElement element ) {
                for( DockSituationIgnore ignore : ignores ){
                    if( !ignore.ignoreElement( element ))
                        return true;
                }
                return false;
            }
            public boolean ignoreElement( PerspectiveElement element ){
                for( DockSituationIgnore ignore : ignores ){
                    if( !ignore.ignoreElement( element ))
                        return true;
                }
                return false;
            }
        };
    }
    
    /**
     * Returns a new {@link DockSituationIgnore} which returns behaves like
     * the reverse of <code>ignore</code>.
     * @param ignore the strategy to reverse
     * @return the new ignore
     */
    public static DockSituationIgnore not( final DockSituationIgnore ignore ){
        return new DockSituationIgnore(){
            public boolean ignoreChildren( DockStation station ) {
                return !ignore.ignoreChildren( station );
            }
            public boolean ignoreChildren( PerspectiveStation station ){
                return !ignore.ignoreChildren( station );
            }
            public boolean ignoreElement( DockElement element ) {
                return !ignore.ignoreElement( element );
            }
            public boolean ignoreElement( PerspectiveElement element ){
            	return !ignore.ignoreElement( element );
            }
        };
    }
}
