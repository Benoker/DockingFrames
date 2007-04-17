/*
 * Bibliothek License
 * ==================
 * 
 * Except where otherwise noted, all of the documentation and software included
 * in the bibliothek package is copyrighted by Benjamin Sigg.
 * 
 * Copyright (C) 2001-2005 Benjamin Sigg. All rights reserved.
 * 
 * This software is provided "as-is," without any express or implied warranty.
 * In no event shall the author be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter and redistribute it,
 * provided that the following conditions are met:
 * 
 * 1. All redistributions of source code files must retain all copyright
 *    notices that are currently in place, and this list of conditions without
 *    modification.
 * 
 * 2. All redistributions in binary form must retain all occurrences of the
 *    above copyright notice and web site addresses that are currently in
 *    place (for example, in the About boxes).
 * 
 * 3. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software to
 *    distribute a product, an acknowledgment in the product documentation
 *    would be appreciated but is not required.
 * 
 * 4. Modified versions in source or binary form must be plainly marked as
 *    such, and must not be misrepresented as being the original software.
 * 
 * 
 * Benjamin sigg
 * benjamin_sigg@gmx.ch
 * 
 */
 
package bibliothek.data;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import bibliothek.io.IniReader;

/**
 * A class used to load resources (Icons...) in the whole bibliothek.
 * @author Benjamin Sigg
 */
public class ResourceManager {
    public static final Factory<Icon> ICON = new IconFactory();
    public static final Factory<Image> IMAGE = new ImageFactory();
    public static final Factory<String> TEXT = new StringFactory();
    public static final Factory<URL> URL = new URLFactory();
    public static final Factory<Properties> INI = new IniFactory();
    public static final Factory<Properties> PROPERTIES = new PropertiesFactory();
    
    private static ResourceManager manager = new ResourceManager();
    
    public static ResourceManager getDefault(){
        return manager;
    }
    
    public static void setDefault( ResourceManager manager ){
        if( manager == null )
            throw new IllegalArgumentException( "Manager must not be null" );
        ResourceManager.manager = manager;
    }
    
    private Map<String, Resource<?>> resources = 
        new HashMap<String, Resource<?>>();
    
    public synchronized boolean exists( String key ){
        return resources.get( key ) != null;
    }
    
    public synchronized Resource<Icon> icon( String key, String path, ClassLoader invoker ){
        return resource( key, path, invoker, ICON );
    }
    
    public synchronized Resource<Image> image( String key, String path, ClassLoader invoker ){
        return resource( key, path, invoker, IMAGE );
    }
    
    public synchronized Resource<String> text( String key, String path, ClassLoader invoker ){
        return resource( key, path, invoker, TEXT );
    }
    
    public synchronized Resource<URL> url( String key, String path, ClassLoader invoker ){
        return resource( key, path, invoker, URL );
    }
    
    public synchronized Resource<Properties> ini( String key, String path, ClassLoader invoker ){
        return resource( key, path, invoker, INI );
    }
    
    public synchronized Resource<Properties> properties( String key, String path, ClassLoader invoker ){
        return resource( key, path, invoker, PROPERTIES );
    }
    
    public synchronized Icon icon( String key ){
        Resource<Icon> resource = resource( key );
        return resource == null ? null : resource.get();
    }
    
    public synchronized Image image( String key ){
        Resource<Image> resource = resource( key );
        return resource == null ? null : resource.get();
    }
    
    public synchronized String text( String key ){
        Resource<String> resource = resource( key );
        return resource == null ? null : resource.get();
    }
    
    public synchronized URL url( String key ){
        Resource<URL> resource = resource( key );
        return resource == null ? null : resource.get();
    }
    
    public synchronized Properties ini( String key ){
        Resource<Properties> resource = resource( key );
        return resource == null ? null : resource.get();
    }
    
    public synchronized Properties properties( String key ){
        Resource<Properties> resource = resource( key );
        return resource == null ? null : resource.get();
    }
    
    /**
     * Searches a resource in the map, loaded with given key and ClassLoader
     * @param <T> The Type of the resource to load
     * @param key The key of the resource
     * @return The resource or <code>null</code>
     * @throws ClassCastException If the given type does not match the type
     * set for the Resource.
     */
    @SuppressWarnings( "unchecked" )
    public synchronized <T> Resource<T> resource( String key ){
        return (Resource<T>)resources.get( key );
    }
    
    /**
     * Searches, and if not found creates, a Ressource using the <code>invoker</code>s
     * ClassLoader to find the resource <code>path</code> in the system.
     * <b>Note:</b> The key for a resource containts the <code>key</code>-value,
     * <b>and</b> the ClassLoader!
     * @param <T> The type of resource this should load
     * @param key The key witch should be used for this resource
     * @param path The path to the resource
     * @param invoker The ClassLoader witch should be used to load the resource,
     * the loader will be a part of the key.
     * @param factory The factory to finally load the resource.
     * @return The Resource
     * @throws ClassCastException If there is another Resource with the
     * given key/invoker, but witch does not load T.
     */
    @SuppressWarnings( "unchecked" )
    public synchronized <T> Resource<T> resource( String key, String path, ClassLoader invoker, Factory<T> factory ){
        if( key == null )
            throw new IllegalArgumentException( "Key must not be null" );
        
        if( path == null )
            throw new IllegalArgumentException( "Path must not be null" );
        
        if( invoker == null )
            throw new IllegalArgumentException( "Invoker must not be null" );
        
        if( factory == null )
            throw new IllegalArgumentException( "Factory must not be null" );
        
        Resource<?> resource = resources.get( key );
        if( resource != null )
            return (Resource<T>)resource;
        
        Resource<T> res = new ResourceImpl<T>( path, invoker, factory );
        resources.put( key, res );
        return res;
    }
    
    /**
     * Puts the value of a key to the give resource.
     * @param key The key
     * @param resource The Resource to put (may be <code>null</code>)
     * @return <code>true</code> if there was no resource befor, 
     * <code>false</code> if there was a non-<code>null</code> value set.
     */
    public synchronized boolean putResource( String key, Resource<?> resource){
        return resources.put( key, resource ) == null;
    }
     
    /**
     * Sets a value for the given key
     * @param <T> The value-type
     * @param key The key
     * @param value The value
     * @return <code>true</code> if there was no other value set befor,
     * <code>false</code> if there was a non-<code>null</code> resource set.
     */
    public synchronized <T> boolean putValue( String key, final T value ){
        return putResource( key, new Resource<T>(){
            public T get() {
                return value;
            }
        });
    }
    
    /**
     * The implementation of a Resource. This one stores the read value
     * of the factory, and returns allways the same value.
     */
    private static class ResourceImpl<T> implements Resource<T>{
        private Factory<T> factory;
        private String path;
        private ClassLoader invoker;
        
        private T t;
        private boolean loaded = false;
        
        public ResourceImpl( String path, ClassLoader invoker, Factory<T> factory ){
            this.path = path;
            this.invoker = invoker;
            this.factory = factory;
        }
        
        public T get() {
            if( loaded )
                return t;
            
            try{
                URL url = invoker.getResource( path );
                if( url == null )
                    throw new IllegalStateException( "No Resource " + path + " found." );
                
                t = factory.load( url );
                loaded = true;
                return t;
            }
            catch( IOException ex ){
                throw new IllegalStateException( "No resource found.", ex );
            }
        }
    }
    
    public static class IconFactory implements Factory<Icon>{
        public Icon load( URL url ) throws IOException {
            return new ImageIcon( url );
        }
    }
    
    public static class ImageFactory implements Factory<Image>{
        public Image load( URL url ) throws IOException {
            return ImageIO.read( url );
        }
    }
    
    public static class URLFactory implements Factory<URL>{
        public URL load( URL url ) throws IOException {
            return url;
        }
    }
    
    public static class StringFactory implements Factory<String>{
        public String load( URL url ) throws IOException {
            InputStream in = new BufferedInputStream( url.openStream() );
            StringBuilder builder = new StringBuilder();
            int read = -1;
            while( (read = in.read() ) != -1)
                builder.append( (char)read );
            
            in.close();
            return builder.toString();
        }
    }
    
    public static class IniFactory implements Factory<Properties>{
        public Properties load( URL url ) throws IOException {
            return IniReader.readIni( url );
        }
    }
    
    public static class PropertiesFactory implements Factory<Properties>{
        public Properties load( URL url ) throws IOException {
            Properties p = new Properties();
            InputStream in = url.openStream();
                        
            p.load( in );
            in.close();
            return p;
        }
    }
}
