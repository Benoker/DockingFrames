package bibliothek.demonstration.util;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 * A list of {@link LookAndFeel}s, can setup a <code>LookAndFeel</code> when
 * asked. It's possible to add a {@link ChangeListener} to this list and
 * receive events whenever the <code>LookAndFeel</code> changes.
 * @author Benjamin Sigg
 */
public class LookAndFeelList{
    /** the {@link LookAndFeel} used when no other <code>LookAndFeel</code> has been set */
    private Info defaultInfo;
    /** the {@link LookAndFeel} that imitates the system */
    private Info systemInfo;
    /** the {@link LookAndFeel} that is currently used */
    private Info currentInfo;
    /** a list of available {@link LookAndFeel}s */
    private Info[] infos;
    /** the list of listeners that get informed when the <code>LookAndFeel</code> changes */
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    
    /** The roots of the {@link Component}-trees that need to be updated when the <code>LookAndFeel</code> changes */
    private List<ComponentCollector> componentCollectors = new ArrayList<ComponentCollector>();
    
    /**
     * Crates a new list and collects all available {@link LookAndFeel}s.
     */
    public LookAndFeelList(){
        LookAndFeel feel = UIManager.getLookAndFeel();
        defaultInfo = new Info( -1, feel.getClass().getName(), feel.getName() );
        currentInfo = defaultInfo;
        
        systemInfo = new Info( -2, UIManager.getSystemLookAndFeelClassName(), "System" );
        
        LookAndFeelInfo[] preset = UIManager.getInstalledLookAndFeels();
        infos = new Info[ preset.length+1 ];
        for( int i = 0; i < preset.length; i++ ){
            infos[i] = new Info( i, preset[i].getClassName(), preset[i].getName() );
        }
        
        infos[ infos.length-1 ] = new Info( infos.length-1, 
                MetalLookAndFeel.class.getName(), "Retro" ){
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
        };
    }
    
    /**
     * Adds a listener to this list, the listener will be notified
     * whenever the {@link LookAndFeel} is changed.
     * @param listener the new listener
     */
    public void addChangeListener( ChangeListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes a listener from this list.
     * @param listener the listener to remove
     */
    public void removeChangeListener( ChangeListener listener ){
        listeners.remove( listener );
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
     * Reads which {@link LookAndFeel} was used earlier and calls 
     * {@link #setLookAndFeel(bibliothek.demonstration.util.LookAndFeelList.Info) setLookAndFeel}
     * to set the old <code>LookAndFeel</code>.
     * @param in the stream to read from
     * @throws IOException if <code>in</code> can't be read
     */
    public void read( DataInputStream in ) throws IOException {
        int index = in.readInt();
        
        if( index == defaultInfo.getIndex() ){
            setLookAndFeel( defaultInfo );
        }
        else if( index == systemInfo.getIndex() ){
            setLookAndFeel( systemInfo );
        }
        else if( index >= 0 && index < infos.length ){
            setLookAndFeel( infos[ index ]);
        }
    }
    
    /**
     * Writes which {@link LookAndFeel} is currently used.
     * @param out the stream to write into
     * @throws IOException if the method can't write into <code>out</code>
     */
    public void write( DataOutputStream out ) throws IOException {
        out.writeInt( currentInfo.getIndex() );
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
     * Gets information about the {@link LookAndFeel} that imitates
     * the native look of the system.
     * @return information about a <code>LookAndFeel</code>
     */
    public Info getSystem() {
        return systemInfo;
    }
    
    /**
     * Gets information about the {@link LookAndFeel} that is
     * currently used.
     * @return the currently used <code>LookAndFeel</code>
     */
    public Info getCurrent() {
        return currentInfo;
    }
    
    /**
     * Gets the number of available {@link LookAndFeel}s.
     * @return the number of looks
     * @see #getInfo(int)
     */
    public int getInfoCount(){
        return infos.length;
    }
    
    /**
     * Gets information about one {@link LookAndFeel}.
     * @param index the index of the look, between 0 and {@link #getInfoCount()}
     * @return information about a <code>LookAndFeel</code>
     */
    public Info getInfo( int index ){
        return infos[index];
    }
    
    /**
     * Exchanges the currently used {@link LookAndFeel}. The object <code>info</code>
     * must have been created by this <code>LookAndFeelList</code>.
     * @param info information about a {@link LookAndFeel}, not <code>null</code>
     */
    public void setLookAndFeel( Info info ){
        if( info == currentInfo )
            return;
        
        if( info.getIndex() == defaultInfo.getIndex() && info != defaultInfo )
            throw new IllegalArgumentException( "Not the info that it claims to be" );
        
        if( info.getIndex() == systemInfo.getIndex() && info != systemInfo )
            throw new IllegalArgumentException( "Not the info that it claims to be" );
        
        if( info.getIndex() >= 0 && infos[ info.getIndex() ] != info )
            throw new IllegalArgumentException( "Info not created by this list" );
        
        if( info.getIndex() < -2 )
            throw new IllegalArgumentException( "Info not created by this list" );
        
        try {
            currentInfo.kill();
            info.setup();
            UIManager.setLookAndFeel( info.getClassName() );

            DemoUtilities.updateUI( listComponents() );
            
            currentInfo = info;
            
            ChangeEvent event = new ChangeEvent( this );
            for( ChangeListener listener : listeners )
                listener.stateChanged( event );
            
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
     * Information about a {@link LookAndFeel}.
     * @author Benjamin Sigg
     */
    public static class Info{
        /** the location where this information is stored, used for internal optimations only */
        private int index;
        /** the class of the {@link LookAndFeel} that is represented by this <code>Info</code> */
        private String className;
        /** the name of the <code>LookAndFeel</code> */
        private String name;
        
        /**
         * Creates a new set of information
         * @param index the location of this <code>Info</code>
         * @param className the name of the class of the {@link LookAndFeel}
         * @param name the name of the <code>LookAndFeel</code>
         */
        public Info( int index, String className, String name ){
            this.index = index;
            this.className = className;
            this.name = name;
        }
        
        /**
         * Gets the location of this <code>Info</code> in the 
         * {@link LookAndFeelList look and feel list}.
         * @return the location or something below 0
         */
        public int getIndex() {
            return index;
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