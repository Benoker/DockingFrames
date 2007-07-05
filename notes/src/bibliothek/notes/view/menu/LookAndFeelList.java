package bibliothek.notes.view.menu;

import java.awt.Component;
import java.awt.Window;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import bibliothek.gui.Dockable;
import bibliothek.notes.view.MainFrame;
import bibliothek.notes.view.ViewManager;
import bibliothek.notes.view.actions.icon.IconGrid;

public class LookAndFeelList{
    private Info defaultInfo, systemInfo, currentInfo;
    private Info[] infos;
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    
    private MainFrame frame;
    private ViewManager views;
    
    public LookAndFeelList( MainFrame frame, ViewManager views ){
    	this.frame = frame;
    	this.views = views;
    	
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
            
            Set<Component> visited = new HashSet<Component>();
            change( frame, visited );
            change( IconGrid.GRID, visited );
            if( frame.getAbout( false ) != null )
            	change( frame.getAbout( false ), visited );
            
            Window parent = SwingUtilities.getWindowAncestor( views.getList().getComponent() );
            if( parent == null || !visited.contains( parent ))
                change( views.getList().getComponent(), visited );
            
            for( Dockable d : views.getFrontend().getController().getRegister().listDockables() ){
            	parent = SwingUtilities.getWindowAncestor( d.getComponent() );
                if( parent == null || !visited.contains( parent ))
                    change( d.getComponent(), visited );
            }
            
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
    
    private void change( Component base, Set<Component> visit ){
        if( visit.add( base )){
            SwingUtilities.updateComponentTreeUI( base );
            if( base instanceof Window ){
                Window window = (Window)base;
                
                for( Window child : window.getOwnedWindows() )
                    change( child, visit );
            }
        }
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
