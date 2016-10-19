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

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.util.Path;
import bibliothek.util.PathCombiner;

/**
 * A preference model that envelops other models and uses their preferences. This model
 * makes use of a {@link PathCombiner} to create unique identifiers for all preferences of its
 * sub-models.
 * @author Benjamin Sigg
 */
public class MergedPreferenceModel extends AbstractPreferenceModel{
    private List<Model> models = new ArrayList<Model>();
    
    /** how to create the result of {@link #getPath(int)} */
    private PathCombiner combiner = PathCombiner.UNIQUE;
    
    private PreferenceModelListener listener = new PreferenceModelListener(){
        public void preferenceAdded( PreferenceModel model, int beginIndex, int endIndex ){
            int begin = indexAt( model, beginIndex );
            int end = begin + (endIndex - beginIndex);
            firePreferenceAdded( begin, end );
        }
        
        public void preferenceChanged( PreferenceModel model, int beginIndex, int endIndex ){
            int begin = indexAt( model, beginIndex );
            int end = begin + (endIndex - beginIndex);
            firePreferenceChanged( begin, end );
        }
        
        public void preferenceRemoved( PreferenceModel model, int beginIndex, int endIndex ){
            int begin = indexAt( model, beginIndex );
            int end = begin + (endIndex - beginIndex);
            firePreferenceRemoved( begin, end );
        }
    };
    
    /**
     * Creates a new model
     * @param controller the controller in whose realm this model is used
     */
    public MergedPreferenceModel( DockController controller ){
    	super( controller );
    }
    
    /**
     * Creates a new path.
     * @param combiner tells how to combine the path of a model and of
     * its preferences in {@link #getPath(int)}. Not <code>null</code>.
     * @param controller the controller in whose realm this model is used
     */
    public MergedPreferenceModel( PathCombiner combiner, DockController controller ){
    	super( controller );
        if( combiner == null )
            throw new IllegalArgumentException( "combiner must not be null" );
        
        this.combiner = combiner;
    }
    
    /**
     * Adds <code>model</code> at the end of this model.
     * @param model the additional model
     * @param path the location of the new model
     * @see #insert(int, PreferenceModel, Path)
     */
    public void add( PreferenceModel model, Path path ){
        insert( models.size(), model, path );
    }
    
    /**
     * Inserts a new submodel into this model.
     * @param index the location of the new model
     * @param model the new model
     * @param path the path of the new model, the path must be unique compared
     * to the paths of any other model.
     */
    public void insert( int index, PreferenceModel model, Path path ){
        if( this == model )
            throw new IllegalArgumentException( "model must not be this" );
        
        for( Model check : models ){
            if( check.model == model )
                throw new IllegalArgumentException( "can't add a model twice" );
            
            if( check.path.equals( path ))
                throw new IllegalArgumentException( "there is already a model with the path " + path );
        }
        
        Model insert = new Model();
        insert.model = model;
        insert.path = path;
        
        models.add( index, insert );
        if( hasListeners() )
            model.addPreferenceModelListener( listener );
        
        int size = model.getSize();

        if( size > 0 ){
            int begin = 0;
            for( int i = 0; i < index; i++ ){
                begin += models.get( i ).model.getSize();
            }
            firePreferenceAdded( begin, begin+size-1 );
        }
    }
    
    /**
     * Removes the <code>index'th</code> model of this merged model.
     * @param index the location of a child
     */
    public void remove( int index ){
        Model model = models.remove( index );
        if( hasListeners() )
            model.model.removePreferenceModelListener( listener );
        
        int size = model.model.getSize();
        if( size > 0 ){
            int begin = 0;
            for( int i = 0; i < index; i++ ){
                begin += models.get( i ).model.getSize();
            }
            firePreferenceRemoved( begin, begin+size-1 );
        }
    }
    
    /**
     * Removes <code>model</code> from this merged model.
     * @param model the model to remove
     */
    public void remove( MergedPreferenceModel model ){
        int index = indexOf( model );
        if( index >= 0 )
            remove( index );
    }
    
    /**
     * Removes the model with the path <code>path</code>.
     * @param path some path
     */
    public void remove( Path path ){
        int index = indexOf( path );
        if( index >= 0 )
            remove( index );        
    }
    
    /**
     * Removes all children from this model.
     */
    public void clear(){
        int size = getSize();
        if( hasListeners() ){
            for( Model model : models ){
                model.model.removePreferenceModelListener( listener );
            }
        }
        models.clear();
        if( size > 0 ){
            firePreferenceRemoved( 0, size-1 );
        }
    }
    
    /**
     * Gets the index of <code>model</code>.
     * @param model some model to search
     * @return the index or -1 if not found
     */
    public int indexOf( PreferenceModel model ){
        int i = 0;
        for( Model check : models ){
            if( check.model == model )
                return i;
            
            i++;
        }
        
        return -1;
    }
    
    /**
     * Gets the index of <code>path</code>.
     * @param path the path of some model
     * @return the index or -1 if not found
     */
    public int indexOf( Path path ){
        int i = 0;
        for( Model check : models ){
            if( check.path.equals( path ) )
                return i;
            
            i++;
        }
        
        return -1;
    }
    
    /**
     * Gets the <code>index</code>'th model of this merged model.
     * @param index some index
     * @return a child of this model
     */
    public PreferenceModel getModel( int index ){
        return models.get( index ).model;
    }
    
    /**
     * Gets the model which was stored using the key <code>path</code>.
     * @param path the path of the model
     * @return the model or <code>null</code> if not found
     */
    public PreferenceModel getModel( Path path ){
    	int index = indexOf( path );
    	if( index < 0 ){
    		return null;
    	}
    	return getModel( index );
    }
    
    @Override
    public void read() {
        for( Model model : models ){
            model.model.read();
        }
    }
    
    @Override
    public void write() {
        for( Model model : models ){
            model.model.write();
        }
    }
    
    @Override
    public void addPreferenceModelListener( PreferenceModelListener listener ) {
        boolean hadListeners = hasListeners();
        super.addPreferenceModelListener( listener );
        if( hasListeners() && !hadListeners ){
            for( Model model : models ){
                model.model.addPreferenceModelListener( this.listener );
            }
        }
    }
    
    @Override
    public void removePreferenceModelListener( PreferenceModelListener listener ) {
        boolean hadListeners = hasListeners();
        super.removePreferenceModelListener( listener );
        if( !hasListeners() && hadListeners ){
            for( Model model : models ){
                model.model.removePreferenceModelListener( this.listener );
            }
        }
    }
    
    public int getSize() {
        int size = 0;
        for( Model model : models ){
            size += model.model.getSize();
        }
        return size;
    }
    
    public String getLabel( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.model.getLabel( local.index );
    }
    
    @Override
    public String getDescription( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.model.getDescription( local.index );
    }
    
    public Object getValueInfo(int index) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.model.getValueInfo( local.index );
    }
    
    public Object getValue( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.model.getValue( local.index );
    }
    
    public void setValue( int index, Object value ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        local.model.model.setValue( local.index, value );
    }
    
    public Path getTypePath( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.model.getTypePath( local.index );
    }
    
    public Path getPath( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
     
        return combiner.combine( local.model.path, local.model.model.getPath( local.index ) );
    }
    
    @Override
    public boolean isNatural( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
     
        return local.model.model.isNatural( local.index );
    }
    
    @Override
    public void setValueNatural( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
     
        local.model.model.setValueNatural( local.index );   
    }
    
    /**
     * Gets the model and the index that <code>globalIndex</code> describe in
     * this model.
     * @param globalIndex some global index
     * @return the local index
     */
    protected Index indexAt( int globalIndex ){
        for( Model model : models ){
            int size = model.model.getSize();
            if( globalIndex < size )
                return new Index( model, globalIndex );
            else
                globalIndex -= size;
        }
        return null;
    }
    
    /**
     * Finds the global index if <code>index</code> is part of <code>model</code>.
     * @param model a child of this model
     * @param index an index in <code>model</code>
     * @return the global index
     */
    protected int indexAt( PreferenceModel model, int index ){
        for( Model check : models ){
            if( check.model == model )
                return index;
            
            index += check.model.getSize();
        }
        
        return index;
    }
    
    /**
     * Describes an index in one of the children of a {@link MergedPreferenceModel}.
     * @author Benjamin Sigg
     */
    protected static class Index{
        public Model model;
        public int index;
        
        public Index( Model model, int index ){
            this.model = model;
            this.index = index;
        }
    }
    
    /**
     * A sub-model entry of a {@link MergedPreferenceModel}.
     * @author Benjamin Sigg
     */
    private static class Model{
        public PreferenceModel model;
        public Path path;
    }
}
