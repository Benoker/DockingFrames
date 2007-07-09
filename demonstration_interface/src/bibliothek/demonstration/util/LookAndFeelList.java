package bibliothek.demonstration.util;

import java.awt.Component;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;


public class LookAndFeelList{
    private Info defaultInfo, systemInfo, currentInfo;
    private Info[] infos;
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    
    private List<ComponentCollector> componentCollectors = new ArrayList<ComponentCollector>();
    
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
    
    public void addChangeListener( ChangeListener listener ){
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ){
        listeners.remove( listener );
    }
    
    public void addComponentCollector( ComponentCollector c ){
    	componentCollectors.add( c );
    }
    
    public void removeComponentCollector( ComponentCollector c ){
    	componentCollectors.remove( c );
    }
    
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
    public void write( DataOutputStream out ) throws IOException {
        out.writeInt( currentInfo.getIndex() );
    }
    
    public Info getDefault() {
        return defaultInfo;
    }
    
    public Info getSystem() {
        return systemInfo;
    }
    
    public Info getCurrent() {
        return currentInfo;
    }
    
    public int getInfoCount(){
        return infos.length;
    }
    
    public Info getInfo( int index ){
        return infos[index];
    }
    
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
    
    protected Collection<Component> listComponents(){
    	Collection<Component> list = new ArrayList<Component>();
    	for( ComponentCollector c : componentCollectors )
    		list.addAll( c.listComponents() );
    	return list;
    }
    
    public static class Info{
        private int index;
        private String className;
        private String name;
        
        public Info( int index, String className, String name ){
            this.index = index;
            this.className = className;
            this.name = name;
        }
        
        public int getIndex() {
            return index;
        }
        
        public String getClassName() {
            return className;
        }
        
        public String getName() {
            return name;
        }
        
        protected void setup(){
        	// do nothing
        }
        
        protected void kill(){
        	// do nothing
        }
    }
}