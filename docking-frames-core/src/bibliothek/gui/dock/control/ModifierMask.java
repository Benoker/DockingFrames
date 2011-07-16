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
package bibliothek.gui.dock.control;

import java.awt.event.InputEvent;

/**
 * A {@link ModifierMask} represents a pattern that tells what modifier keys 
 * are currently pressed. Modifier keys are keys like ctrl, shift or alt.<br>
 * This class will be used to check the result of {@link InputEvent#getModifiersEx()}.<br>
 * @author Benjamin Sigg
 */
public class ModifierMask {
	/** all the masks for which a {@link ModifierMask} looks out */
	public static int KEY_MASK = 
		InputEvent.ALT_DOWN_MASK | 
		InputEvent.ALT_GRAPH_DOWN_MASK |
		InputEvent.CTRL_DOWN_MASK | 
		InputEvent.META_DOWN_MASK |
		InputEvent.SHIFT_DOWN_MASK;
	
	private int onmask;
    private int offmask;
    
    /**
     * Creates a new mask which allows only modifiers that match exactly
     * <code>mask</code>.
     * @param mask the mask to match
     */
    public ModifierMask( int mask ){
    	this( mask, ~mask );
    }
    
    /**
     * Creates a new mask.
     * @param on the keys that must be pressed
     * @param off the keys that must not be pressed
     */
    public ModifierMask( int on, int off ){
        this.onmask = on & KEY_MASK;
        this.offmask = off & KEY_MASK;
    }
    
    /**
     * Creates a string that represents the on-mask.
     * @return the string
     */
    public String onMaskToString(){
    	StringBuilder builder = new StringBuilder();
    	put( builder, InputEvent.ALT_DOWN_MASK, "alt" );
    	put( builder, InputEvent.ALT_GRAPH_DOWN_MASK, "alt graph" );
    	put( builder, InputEvent.CTRL_DOWN_MASK, "ctrl" );
    	put( builder, InputEvent.META_DOWN_MASK, "meta" );
    	put( builder, InputEvent.SHIFT_DOWN_MASK, "shift" );
    	return builder.toString();
    }
    
    private void put( StringBuilder builder, int mask, String text ){
    	if( (onmask & mask) == mask ){
    		if( builder.length() > 0 )
    			builder.append( " + " );
    		
    		builder.append( text );
    	}
    }
	
    /**
     * Tells whether this {@link ModifierMask} relates to <code>modifiers</code>
     * or not. The <code>modifiers</code> have the same form as 
     * {@link InputEvent#getModifiersEx()} would have.
     * @param modifiers the modifiers from an <code>InputEvent</code>
     * @return <code>true</code> if this mask matches the modifiers
     */
    public boolean matches( int modifiers ){
        return (modifiers & (onmask | offmask)) == onmask;
    }
    
    /**
     * Sets the mask of the modifiers which must be pressed in order to
     * activate this mask.
     * @param onmask the modifiers that must be pressed
     */
    public void setOnmask(int onmask) {
		this.onmask = onmask & KEY_MASK;
	}
    
    /**
     * Gets the mask of modifiers which must be pressed in order to
     * activate this.
     * @return the mask of active modifiers
     */
    public int getOnmask() {
		return onmask;
	}
    
    /**
     * Sets the mask of the modifiers which must not be pressed in order to
     * activate this mask.
     * @param offmask the modifiers that must not be pressed
     */
    public void setOffmask(int offmask) {
		this.offmask = offmask & KEY_MASK;
	}
    
    /**
     * Gets the mask of the modifiers which must not be pressed.
     * @return the mask of inactive modifiers
     */
    public int getOffmask() {
		return offmask;
	}
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + offmask;
		result = prime * result + onmask;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ModifierMask other = (ModifierMask) obj;
		if (offmask != other.offmask)
			return false;
		if (onmask != other.onmask)
			return false;
		return true;
	}
}
