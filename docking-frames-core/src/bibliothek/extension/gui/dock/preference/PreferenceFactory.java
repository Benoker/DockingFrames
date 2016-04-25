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
package bibliothek.extension.gui.dock.preference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.KeyStroke;

import bibliothek.gui.dock.control.ModifierMask;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A factory that is capable of writing and reading some kind of preference
 * to or from some repository. <code>PreferenceFactories</code> are used
 * by the {@link PreferenceStorage}.
 * @author Benjamin Sigg
 *
 * @param <V> the kind of preference this factory stores
 */
public interface PreferenceFactory<V> {
	/** a factory for handling {@link Boolean} */
	public static final PreferenceFactory<Boolean> FACTORY_BOOLEAN = new PreferenceFactory<Boolean>(){
		public Boolean read( DataInputStream in ) throws IOException {
			return in.readBoolean();
		}
		public Boolean readXML( XElement element ) {
			return element.getBoolean();
		}
		public void write( Boolean value, DataOutputStream out ) throws IOException {
			out.writeBoolean( value );
		}
		public void writeXML( Boolean value, XElement element ){
			element.setBoolean( value );
		}
	};
	
    /** a factory for handling {@link Integer} */
    public static final PreferenceFactory<Integer> FACTORY_INT = new PreferenceFactory<Integer>(){
        public Integer read( DataInputStream in ) throws IOException {
            return in.readInt();
        }
        public Integer readXML( XElement element ) {
            return element.getInt();
        }
        public void write( Integer value, DataOutputStream out ) throws IOException {
            out.writeInt( value );
        }
        public void writeXML( Integer value, XElement element ) {
            element.setInt( value );
        }
    };
    
    /** A factory for handling {@link String} */
    public static final PreferenceFactory<String> FACTORY_STRING = new PreferenceFactory<String>(){
    	public String read( DataInputStream in ) throws IOException {
    		return in.readUTF();
    	}
    	public String readXML( XElement element ){
    		return element.getString();
    	}
    	public void write( String value, DataOutputStream out ) throws IOException {
    		out.writeUTF( value );
    	}
    	public void writeXML( String value, XElement element ){
    		element.setString( value );
    	}
    };
    
    /** a factory for handling {@link KeyStroke} */
    public static final PreferenceFactory<KeyStroke> FACTORY_KEYSTROKE = new PreferenceFactory<KeyStroke>(){
        public KeyStroke read( DataInputStream in ) throws IOException {
            return KeyStroke.getKeyStroke( in.readUTF() );
        }

        public KeyStroke readXML( XElement element ) {
            return KeyStroke.getKeyStroke( element.getString() );
        }

        public void write( KeyStroke value, DataOutputStream out )throws IOException {
            out.writeUTF( value.toString() );
        }

        public void writeXML( KeyStroke value, XElement element ) {
            element.setString( value.toString() );
        }
    };
    
    /** a factory for {@link ModifierMask}s */
    public static final PreferenceFactory<ModifierMask> FACTORY_MODIFIER_MASK = new PreferenceFactory<ModifierMask>(){
		public ModifierMask read( DataInputStream in ) throws IOException {
			int on = in.readInt();
			int off = in.readInt();
			return new ModifierMask( on, off );
		}

		public ModifierMask readXML( XElement element ) {
			int on = element.getInt( "on" );
			int off = element.getInt( "off" );
			return new ModifierMask( on, off );
		}

		public void write( ModifierMask value, DataOutputStream out ) throws IOException {
			out.writeInt( value.getOnmask() );
			out.writeInt( value.getOffmask() );
		}

		public void writeXML( ModifierMask value, XElement element ){
			element.addInt( "on", value.getOnmask() );
			element.addInt( "off", value.getOffmask() );
		}
    };
    
    /**
     * Writes <code>value</code> into <code>out</code>.
     * @param value the value to write, never <code>null</code>
     * @param out the stream to write into
     * @throws IOException if this factory can't write into <code>out</code>
     */
    public void write( V value, DataOutputStream out ) throws IOException;
    
    /**
     * Writes <code>value</code> into <code>element</code>. This method
     * may add children and attributes to <code>element</code>. The attribute
     * "type" shall not be changed by this method.
     * @param value some value, not <code>null</code>
     * @param element to write into, the attribute "type" shall not be changed
     * by this method
     * @throws XException if the value can't be transformed
     */
    public void writeXML( V value, XElement element );
    
    /**
     * Reads a value from <code>in</code>. This method must read the same
     * number of bytes as {@link #write(Object, DataOutputStream)} had written.
     * @param in the stream to read from
     * @return the value that was read
     * @throws IOException if <code>in</code> is not readable or the data
     * is corrupted
     */
    public V read( DataInputStream in ) throws IOException;
    
    /**
     * Reads some value from <code>element</code>.
     * @param element the element to read from
     * @return the value that was read
     * @throws XException if <code>element</code> is not valid
     */
    public V readXML( XElement element );
}
