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
package bibliothek.gui.dock.support.lookandfeel;

import java.awt.Component;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A list of {@link LookAndFeel}s, can setup a <code>LookAndFeel</code> when
 * asked. It's possible to add a {@link LookAndFeelListener} to this list and
 * receive events whenever the <code>LookAndFeel</code> changes.<br>
 * Clients should use {@link #getDefaultList()} to get a list of {@link LookAndFeel}s
 * @author Benjamin Sigg
 */
public class LookAndFeelList{
	/** global list of look and feels */
	private static LookAndFeelList list;

	/**
	 * Gets the global list of {@link LookAndFeel}s
	 * @return the global list, not <code>null</code>
	 */
	public static LookAndFeelList getDefaultList(){
		if( list == null ){
		    synchronized( LookAndFeelList.class ){
		        if( list == null ){
		            list = new LookAndFeelList();
		        }
		    }
		}
		return list;
	}
	
	/**
	 * Sets the default {@link LookAndFeelList}.
	 * @param list the list, can be <code>null</code>
	 */
	public static void setDefaultList( LookAndFeelList list ){
	    LookAndFeelList.list = list;
	}

    /** the {@link LookAndFeel} used when no other <code>LookAndFeel</code> has been set */
    private Wrapper defaultInfo;
    /** the {@link LookAndFeel} that imitates the system */
    private Wrapper systemInfo;
    /** the {@link LookAndFeel} that is currently used */
    private Info currentInfo;
    /** a list of available {@link LookAndFeel}s */
    private List<Info> infos = new ArrayList<Info>();
    /** the list of listeners that get informed when the <code>LookAndFeel</code> changes */
    private List<LookAndFeelListener> listeners = new ArrayList<LookAndFeelListener>();
    
    /** The roots of the {@link Component}-trees that need to be updated when the <code>LookAndFeel</code> changes */
    private List<ComponentCollector> componentCollectors = new ArrayList<ComponentCollector>();
    
    /** Whether the {@link #read(DataInputStream)}-method has effect when it is called a second time */
    private boolean allowReadOnlyOnce = false;
    /** Whether the {@link #read(DataInputStream)}-method was called at least once */
    private boolean hasRead = false;
    
    /**
     * Crates a new list and collects all available {@link LookAndFeel}s.
     */
    protected LookAndFeelList(){
        LookAndFeel feel = UIManager.getLookAndFeel();
        setDefault( new Info( feel.getClass().getName(), feel.getName() ));
        currentInfo = defaultInfo;
        
        setSystem( new Info( UIManager.getSystemLookAndFeelClassName(), "System" ));
        
        LookAndFeelInfo[] preset = UIManager.getInstalledLookAndFeels();
        for( int i = 0; i < preset.length; i++ ){
            add( new Info( preset[i].getClassName(), preset[i].getName() ));
        }
        
        add( new Info( MetalLookAndFeel.class.getName(), "Retro" ){
            private MetalTheme oldTheme;
            
            @Override
            protected void setup() {
                oldTheme = MetalLookAndFeel.getCurrentTheme();
                MetalLookAndFeel.setCurrentTheme( new DefaultMetalTheme() );
            }
            
            @Override
            protected void kill() {
                MetalLookAndFeel.setCurrentTheme( oldTheme );
            }
        });
    }
    
    /**
     * Whether multiple calls to {@link #read(DataInputStream)} have
     * an effect or not.
     * @return <code>true</code> if only the first read-call has an effect.
     */
    public boolean isAllowReadOnlyOnce() {
        return allowReadOnlyOnce;
    }
    
    /**
     * Sets whether multiple calls to {@link #read(DataInputStream)} will
     * have an effect.
     * @param allowReadOnlyOnce <code>true</code> if only the first
     * read will have an effect, <code>false</code> if the {@link LookAndFeel}
     * can change every time {@link #read(DataInputStream)} is called.
     */
    public void setAllowReadOnlyOnce( boolean allowReadOnlyOnce ) {
        this.allowReadOnlyOnce = allowReadOnlyOnce;
    }
    
    /**
     * Sets whether this list has already read something once, or whether
     * it is fresh. Can be used to reset a list to its initial state.
     * @param read <code>true</code> if at least one time one of the
     * <code>read</code> methods was called, <code>false</code> otherwise.
     */
    public void setReadOnce( boolean read ){
        hasRead = read;
    }
    
    /**
     * Adds a listener to this list, the listener will be notified
     * whenever the {@link LookAndFeel} is changed.
     * @param listener the new listener
     */
    public void addLookAndFeelListener( LookAndFeelListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        listeners.add( listener );
    }
    
    /**
     * Removes a listener from this list.
     * @param listener the listener to remove
     */
    public void removeLookAndFeelListener( LookAndFeelListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets all {@link LookAndFeelListener} that are known to this list.
     * @return the list of listeners
     */
    protected LookAndFeelListener[] listeners(){
        return listeners.toArray( new LookAndFeelListener[ listeners.size() ] );
    }
    
    /**
     * Adds a set of root-{@link Component}s to this list, the set of
     * roots will be used to find all {@link JComponent}s
     * which need to be updated when the {@link LookAndFeel} changes.
     * @param c the new set of roots
     */
    public void addComponentCollector( ComponentCollector c ){
    	componentCollectors.add( c );
    }
    
    /**
     * Removes an earlier added set of roots.
     * @param c the roots
     */
    public void removeComponentCollector( ComponentCollector c ){
    	componentCollectors.remove( c );
    }
    
    /**
     * Adds a new {@link LookAndFeel} to the list.
     * @param info the new LookAndFeel
     */
    public void add( Info info ){
        insert( infos.size(), info );
    }
    
    /**
     * Inserts a new {@link LookAndFeel} into the list.
     * @param index the location of the new LookAndFeel
     * @param info the new LookAndFeel
     */
    public void insert( int index, Info info ){
        if( info == null )
            throw new NullPointerException( "Info must not be null" );
        infos.add( index, info );
        for( LookAndFeelListener listener : listeners() )
            listener.lookAndFeelAdded( this, info );
    }
    
    /**
     * Gets the number of {@link LookAndFeel}s that are known to this list.
     * @return the number of LookAndFeels
     */
    public int size(){
        return infos.size();
    }
    
    /**
     * Gets the index'th {@link LookAndFeel}.
     * @param index the location of the LookAndFeel
     * @return the LookAndFeel
     */
    public Info get( int index ){
        return infos.get( index );
    }
    
    /**
     * Gets the location of <code>info</code>.
     * @param info a {@link LookAndFeel}
     * @return the location of <code>info</code> or -1
     */
    public int indexOf( Info info ){
        return infos.indexOf( info );
    }
    
    /**
     * Gets the index'th {@link LookAndFeel}, where 0 means the
     * {@link #getDefault() default}, 1 the {@link #getSystem() system} and
     * anything else the {@link #get(int) normal}, moved by 2 steps, LookAndFeels.
     * @param index the location of the LookAndFeel
     * @return the selected LookAndFeel
     */
    public Info getFull( int index ){
        if( index == 0 )
            return getDefault();
        
        if( index == 1 )
            return getSystem();
        
        return get( index-2 );
    }
    
    /**
     * Gets the index of <code>info</code>, where 0 means the
     * {@link #getDefault() default}, 1 the {@link #getSystem() system} and
     * anything else the {@link #get(int) normal}, moved by 2 steps, LookAndFeels.
     * @param info the LookAndFeel to search
     * @return the location of <code>info</code>
     */
    public int indexOfFull( Info info ){
        if( info == defaultInfo )
            return 0;
        
        if( info == systemInfo )
            return 1;
        
        int index = indexOf( info );
        if( index < 0 )
            return -1;
        else
            return index+2;
    }
    
    /**
     * Gets the {@link LookAndFeel} whose unique identifier is <code>key</code>.
     * @param key the key to search
     * @return the handler of the {@link LookAndFeel} or <code>null</code> if <code>key</code> was not found
     */
    public Info getFull( String key ){
    	if( "s.default".equals( key ))
    		return getDefault();
    	
    	if( "s.system".equals( key ))
    		return getSystem();
    	
    	for( Info info : infos ){
    		if( key.equals( keyOfFullNormal( info ) )){
    			return info;
    		}
    	}
    	
    	return null;
    }
    
    /**
     * Gets a unique identifier for <code>info</code>.
     * @param info the item whose identifier is searched
     * @return a unique identifier describing <code>info</code>
     */
    public String keyOfFull( Info info ){
    	if( info == defaultInfo )
    		return "s.default";
    	
    	if( info == systemInfo )
    		return "s.system";
    	
    	return keyOfFullNormal( info );
    }
    
    private String keyOfFullNormal( Info info ){
    	return "l." + info.getName().length() + "." + info.getName() + "." + info.className;
    }
    
    /**
     * Removes the {@link LookAndFeel} at location <code>index</code> from 
     * this list.
     * @param info the LookAndFeel to remove
     */
    public void remove( Info info ){
        int index = indexOf( info );
        if( index >= 0 )
            remove( index );
    }
    
    /**
     * Removes a {@link LookAndFeel} from this list.
     * @param index the location of the element to remove
     */
    public void remove( int index ){
        Info info = infos.remove( index );
        for( LookAndFeelListener listener : listeners() )
            listener.lookAndFeelRemoved( this, info );
    }
    
    /**
     * Gets the {@link LookAndFeel} which is currently used.
     * @return the currently used LookAndFeel
     */
    public Info getLookAndFeel() {
        return currentInfo;
    }
    

    /**
     * Exchanges the currently used {@link LookAndFeel}.
     * @param lookAndFeel information about a {@link LookAndFeel}, not <code>null</code>
     */
    public void setLookAndFeel( Info lookAndFeel ){
        if( lookAndFeel == currentInfo )
            return;
        
        if( lookAndFeel == null )
            throw new NullPointerException( "lookAndFeel must not be null" );
        
        try {
            currentInfo.kill();
            currentInfo = lookAndFeel;
            
            lookAndFeel.setup();
            UIManager.setLookAndFeel( lookAndFeel.getClassName() );

            LookAndFeelUtilities.updateUI( listComponents() );
            
            for( LookAndFeelListener listener : listeners() )
                listener.lookAndFeelChanged( this, lookAndFeel );
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Gets information about the {@link LookAndFeel} that is used when
     * the system can decide on its own.
     * @return information about a {@link LookAndFeel}
     */
    public Info getDefault() {
        return defaultInfo;
    }
    
    /**
     * Sets the default- {@link LookAndFeel}. Please note that {@link #getDefault()}
     * will return another {@link Info}, even if the behavior of that other
     * info is the same as <code>defaultInfo</code>.
     * @param defaultInfo the default LookAndFeel
     */
    public void setDefault( Info defaultInfo ) {
        if( defaultInfo == null )
            throw new NullPointerException( "argument must not be null" );
        this.defaultInfo = new Wrapper( defaultInfo );
        for( LookAndFeelListener listener : listeners() )
            listener.defaultLookAndFeelChanged( this, this.defaultInfo );
    }
    
    /**
     * Gets information about the {@link LookAndFeel} that imitates
     * the native look of the system.
     * @return information about a <code>LookAndFeel</code>
     */
    public Info getSystem() {
        return systemInfo;
    }
    
    /**
     * Sets the system- {@link LookAndFeel}. Please note that {@link #getSystem()}
     * will return another {@link Info}, even if the behavior of that other
     * info is the same as <code>systemInfo</code>.
     * @param systemInfo the system LookAndFeel
     */
    public void setSystem( Info systemInfo ) {
        if( systemInfo == null )
            throw new NullPointerException( "argument must not be null" );
        this.systemInfo = new Wrapper( systemInfo );
        for( LookAndFeelListener listener : listeners() )
            listener.systemLookAndFeelChanged( this, this.systemInfo );
    }
    
    /**
     * Writes which {@link LookAndFeel} is currently used.
     * @param out the stream to write into
     * @throws IOException if the method can't write into <code>out</code>
     */
    public void write( DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_1_1 );
        out.writeUTF( keyOfFull( getLookAndFeel() ) );
    }
    
    /**
     * Reads which {@link LookAndFeel} was used earlier and calls 
     * {@link #setLookAndFeel(LookAndFeelList.Info) setLookAndFeel}
     * to set the old <code>LookAndFeel</code>.
     * @param in the stream to read from
     * @throws IOException if <code>in</code> can't be read
     */
    public void read( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        
        if( version.equals( Version.VERSION_1_0_4 )){
	        int index = in.readInt();
	        if( !hasRead || !allowReadOnlyOnce ){
	            if( index >= 0 && index < size()+2 ){
	                setLookAndFeel( getFull( index ) );
	            }
	        }
	        hasRead = true;
        }
        else{
        	String key = in.readUTF();
        	if( !hasRead || !allowReadOnlyOnce ){
        		Info laf = getFull( key );
        		if( laf == null ){
        			laf = getDefault();
        		}
        		if( laf != null ){
        			setLookAndFeel( laf );
        		}
        	}
        	hasRead = true;
        }
    }
    
    /**
     * Writes which {@link LookAndFeel} is currently used.
     * @param element the element to write into, the attributes of
     * <code>element</code> will not be changed.
     */
    public void writeXML( XElement element ){
        element.addElement( "key" ).setString( keyOfFull( getLookAndFeel() ) );
    }
    
    /**
     * Reads which {@link LookAndFeel} was used earlier and calls 
     * {@link #setLookAndFeel(LookAndFeelList.Info) setLookAndFeel}
     * to set the old <code>LookAndFeel</code>.
     * @param element the element to read from
     */
    public void readXML( XElement element ){
        if( !hasRead || !allowReadOnlyOnce ){
        	XElement xkey = element.getElement( "key" );
        	Info info = null;
        	
        	if( xkey == null ){
        		XElement xindex = element.getElement( "index" );
        		int index = xindex.getInt();
        		if( index >= 0 && index < size()+2 ){
                    info = getFull( index );
                }
        		info = getFull( xindex.getInt() );
        	}
        	else{
        		info = getFull( xkey.getString() );
        	}
        	if( info == null ){
        		info = getDefault();
        	}
        	if( info != null ){
                setLookAndFeel( info );
            }
        }
        hasRead = true;
    }
    
    /**
     * Creates a list containing all root-{@link Component}s of this application,
     * the {@link ComponentCollector}s are used to build this list.
     * @return the list of roots
     */
    protected Collection<Component> listComponents(){
    	Collection<Component> list = new ArrayList<Component>();
    	for( ComponentCollector c : componentCollectors )
    		list.addAll( c.listComponents() );
    	return list;
    }
    
    /**
     * A wrapper around an {@link Info}.
     * @author Benjamin Sigg
     */
    private class Wrapper extends Info{
        /** the delegate */
        private Info info;
        /**
         * Creates a new wrapper
         * @param info delegate to get information
         */
        public Wrapper( Info info ) {
            super( info.getClassName(), info.getName() );
            this.info = info;
        }
        
        @Override
        protected void setup() {
            info.setup();
        }
        
        @Override
        protected void kill() {
            info.kill();
        }
    }
    
    /**
     * Information about a {@link LookAndFeel}.
     * @author Benjamin Sigg
     */
    public static class Info{
        /** the class of the {@link LookAndFeel} that is represented by this <code>Info</code> */
        private String className;
        /** the name of the <code>LookAndFeel</code> */
        private String name;
        
        /**
         * Creates a new set of information
         * @param className the name of the class of the {@link LookAndFeel}
         * @param name the name of the <code>LookAndFeel</code>
         */
        public Info( String className, String name ){
            this.className = className;
            this.name = name;
        }
        
        /**
         * Gets the name of the class of a {@link LookAndFeel}.
         * @return the class name used for reflection
         */
        public String getClassName() {
            return className;
        }
        
        /**
         * Gets the name of the {@link LookAndFeel} that is represented
         * by this <code>Info</code>.
         * @return the name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Informs this <code>Info</code> that its <code>LookAndFeel</code>
         * will be shown soon.
         */
        protected void setup(){
        	// do nothing
        }
        
        /**
         * Informs this <code>Info</code> that its <code>LookAndFeel</code>
         * will be removed soon.
         */
        protected void kill(){
        	// do nothing
        }
    }
}