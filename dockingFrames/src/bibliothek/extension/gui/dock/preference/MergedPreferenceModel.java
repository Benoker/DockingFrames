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

/**
 * A preference model that envelops other models and uses their preferences.
 * @author Benjamin Sigg
 */
public class MergedPreferenceModel extends AbstractPreferenceModel{
    private List<PreferenceModel> models = new ArrayList<PreferenceModel>();
    
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
     * Adds <code>model</code> at the end of this model.
     * @param model the additional model
     */
    public void add( PreferenceModel model ){
        insert( models.size(), model );
    }
    
    /**
     * Inserts a new submodel into this model.
     * @param index the location of the new model
     * @param model the new model
     */
    public void insert( int index, PreferenceModel model ){
        if( this == model )
            throw new IllegalArgumentException( "model must not be this" );
        
        if( models.contains( model ))
            throw new IllegalArgumentException( "can't add a model twice" );
        
        models.add( index, model );
        if( hasListeners() )
            model.addModelListener( listener );
        
        int size = model.getSize();

        if( size > 0 ){
            int begin = 0;
            for( int i = 0; i < index; i++ ){
                begin += models.get( i ).getSize();
            }
            firePreferenceAdded( begin, begin+size-1 );
        }
    }
    
    /**
     * Removes the <code>index'th</code> model of this merged model.
     * @param index the location of a child
     */
    public void remove( int index ){
        PreferenceModel model = models.remove( index );
        if( hasListeners() )
            model.removeModelListener( listener );
        
        int size = model.getSize();
        if( size > 0 ){
            int begin = 0;
            for( int i = 0; i < index; i++ ){
                begin += models.get( i ).getSize();
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
     * Removes all children from this model.
     */
    public void clear(){
        int size = getSize();
        if( hasListeners() ){
            for( PreferenceModel model : models ){
                model.removeModelListener( listener );
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
        return models.indexOf( model );
    }
    
    /**
     * Gets the <code>index</code>'th model of this merged model.
     * @param index some index
     * @return a child of this model
     */
    public PreferenceModel getModel( int index ){
        return models.get( index );
    }
    
    @Override
    public void addModelListener( PreferenceModelListener listener ) {
        boolean hadListeners = hasListeners();
        super.addModelListener( listener );
        if( hasListeners() && !hadListeners ){
            for( PreferenceModel model : models ){
                model.addModelListener( this.listener );
            }
        }
    }
    
    @Override
    public void removeModelListener( PreferenceModelListener listener ) {
        boolean hadListeners = hasListeners();
        super.removeModelListener( listener );
        if( !hasListeners() && hadListeners ){
            for( PreferenceModel model : models ){
                model.removeModelListener( this.listener );
            }
        }
    }
    
    public int getSize() {
        int size = 0;
        for( PreferenceModel model : models ){
            size += model.getSize();
        }
        return size;
    }
    
    public String getLabel( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.getLabel( local.index );
    }
    
    @Override
    public String getDescription( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.getDescription( local.index );
    }
    
    public Object getValue( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.getValue( local.index );
    }
    
    public void setValue( int index, Object value ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        local.model.setValue( local.index, value );
    }
    
    @Override
    public Class<?> getPreferenceClass( int index ) {
        Index local = indexAt( index );
        if( local == null )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return local.model.getPreferenceClass( local.index );
    }
    
    /**
     * Gets the model and the index that <code>globalIndex</code> describe in
     * this model.
     * @param globalIndex some global index
     * @return the local index
     */
    protected Index indexAt( int globalIndex ){
        for( PreferenceModel model : models ){
            int size = model.getSize();
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
        for( PreferenceModel check : models ){
            if( check == model )
                return index;
            
            index += check.getSize();
        }
        
        return index;
    }
    
    /**
     * Describes an index in one of the childen of a {@link MergedPreferenceModel}.
     * @author Benjamin Sigg
     */
    protected static class Index{
        public PreferenceModel model;
        public int index;
        
        public Index( PreferenceModel model, int index ){
            this.model = model;
            this.index = index;
        }
    }
}
