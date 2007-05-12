package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import bibliothek.extension.gui.dock.theme.BubbleTheme;

public class BubbleColorAnimation {
    private int duration = 1000;
    
    private Map<String, Entry> colors = new HashMap<String, Entry>();
    private Timer timer;
    private long time = 0;
    
    private List<Runnable> tasks = new ArrayList<Runnable>();
    private BubbleTheme theme;
    
    public BubbleColorAnimation( BubbleTheme theme ){
        this.theme = theme;
        
        timer = new Timer( 25, new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                pulse();
            }
        });
    }
    
    public void putColors( String key, String source, String destination ){
        Entry entry = colors.get( key );
        if( entry == null ){
            entry = new Entry();
            colors.put( key, entry );
        }
        entry.setColors( source, destination );
    }
    
    public void putColor( String key, String color ){
        Entry entry = colors.get( key );
        if( entry == null ){
            entry = new Entry();
            entry.age = 0;
            entry.source = color;
            colors.put( key, entry );
        }
        else{
            entry.setDestination( color );
            start();
        }
    }
    
    public Color getColor( String key ){
        Entry entry = colors.get( key );
        if( entry == null )
            return null;
        
        return entry.getColor();
    }
    
    public void addTask( Runnable runnable ){
        tasks.add( runnable );
    }
    
    public void stop(){
        timer.stop();
    }
    
    protected void start(){
        if( !timer.isRunning() ){
            time = System.currentTimeMillis();
            timer.start();
        }
    }
    
    protected void pulse(){
        boolean run = false;
        long current = System.currentTimeMillis();
        int delta = (int)( current - time );
        time = current;
        
        for( Entry entry : colors.values() )
            run = entry.step( delta ) | run;
        
        if( !run )
            timer.stop();
        
        for( Runnable task : tasks )
            task.run();
    }
    
    private class Entry{
        private String source, destination;
        private Color intermediate;
        private int age;
        
        public Color getColor(){
            if( age <= 0 )
                return theme.getColor( source );
            
            if( age >= duration )
                return theme.getColor( destination );
            
            Color source = intermediate == null ? theme.getColor( this.source ) : intermediate;
            Color destination = theme.getColor( this.destination );
            
            double s = (duration - age) / (double)duration;
            double d = age / (double)duration;
            return new Color(
                    Math.max( 0, Math.min( 255, (int)(s * source.getRed() + d * destination.getRed()))),
                    Math.max( 0, Math.min( 255, (int)(s * source.getGreen() + d * destination.getGreen()))),
                    Math.max( 0, Math.min( 255, (int)(s * source.getBlue() + d * destination.getBlue()))));
        }
        
        public boolean step( int delta ){
            if( destination == null )
                return false;
            
            age += delta;
            if( age >= duration ){
                age = 0;
                source = destination;
                destination = null;
                intermediate = null;
                return false;
            }
            
            return true;
        }
        
        public void setColors( String source, String destination ){
            if( age == 0 ){
                this.source = destination;
            }
            else{
                this.source = source;
                this.destination = destination;
            }
        }
        
        public void setDestination( String color ){
            if( age == 0 ){
                destination = color;
                intermediate = null;
            }
            else if( source.equals( color ) ){
                source = destination;
                destination = color;
                age = duration - age;
                intermediate = null;
            }
            else{
                intermediate = getColor();
                destination = color;
                age = 0;
            }
        }
    }
}
