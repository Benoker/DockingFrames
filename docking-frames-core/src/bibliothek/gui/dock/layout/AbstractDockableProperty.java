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

package bibliothek.gui.dock.layout;

/**
 * A simple implementation of {@link DockableProperty} which provides
 * only the basic features.
 * @author Benjamin Sigg
 */
public abstract class AbstractDockableProperty implements DockableProperty {
    /** The successor of this property */
    private DockableProperty successor;
    
    public DockableProperty getSuccessor() {
        return successor;
    }

    public void setSuccessor( DockableProperty properties ) {
        this.successor = properties;
    }
    
    /**
     * Copies the fields of this property to <code>copy</code>.
     * @param copy the copy of <code>this</code>
     */
    protected void copy( AbstractDockableProperty copy ){
        if( successor != null )
            copy.successor = successor.copy();
    }

    public boolean equalsNoSuccessor( DockableProperty property ){
    	DockableProperty successor = this.successor;
    	DockableProperty successorProperty = property.getSuccessor();
    	
    	try{
    		this.successor = null;
    		property.setSuccessor( null );
    		
    		return equals( property );
    	}
    	finally{
    		this.successor = successor;
    		property.setSuccessor( successorProperty );
    	}
    }
    
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((successor == null) ? 0 : successor.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj ){
		if( this == obj ) {
			return true;
		}

		if( obj == null ) {
			return false;
		}

		if( this.getClass() == obj.getClass() ) {
			AbstractDockableProperty other = (AbstractDockableProperty)obj;
			if( successor == null ){
				if( other.successor != null )
					return false;
			}else if( !successor.equals( other.successor ) )
				return false;
			return true;
		}

		return false;
	}
}
