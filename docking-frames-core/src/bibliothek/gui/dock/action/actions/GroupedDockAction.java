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

package bibliothek.gui.dock.action.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.util.container.Tuple;

/**
 * A {@link DockAction} which classifies its {@link Dockable Dockables}
 * in groups. Every {@link Dockable} must be in one group. The groups
 * itself are completely independent, except that they all must have
 * the same {@link ActionType}.<br>
 * A {@link Dockable} may change its group at any time. 
 * The method {@link #setGroup(Object, Dockable) setGroup} is used for that.<br>
 * If a new {@link Dockable} is {@link #bind(Dockable) bound} to this
 * action, the {@link #createGroupKey(Dockable) createGroupKey}-method
 * determines the group where the {@link Dockable} will be added.<br>
 * When a group is completely empty, it is removed. This behavior can
 * be changed by the method {@link #setRemoveEmptyGroups(boolean) setRemoveEmptyGroups}.
 * @author Benjamin Sigg
 * @param <K> the type of the key used to distinguish between groups 
 * @param <D> the internal representation of one group
 *
 * @see #createGroupKey(Dockable)
 * @see #setRemoveEmptyGroups(boolean)
 */
public abstract class GroupedDockAction<K, D extends SimpleDockAction> extends AbstractStandardDockAction {
    /** the groups of this action */
	private Map<K, D> groups = new HashMap<K, D>();
	/** tells which Dockable is part of which group */
    private Map<Dockable, Tuple<K, D>> dockActions = new HashMap<Dockable, Tuple<K, D>>();
    
    /** a listener to the groups */
    private Listener listener = new Listener();
    /** whether empty groups should be removed automatically or not */
    private boolean removeEmptyGroups = true;
    
    /** a generator for keys for unknown Dockables */
    private GroupKeyGenerator<? extends K> generator;
    
    /**
     * Creates a new action.
     * @param generator the generator that will be used to get a key for 
     * Dockables which do not yet have a key. The generator can be <code>null</code>
     * and set later through the method {@link #setGenerator(GroupKeyGenerator)}
     */
    public GroupedDockAction( GroupKeyGenerator<? extends K> generator ){
    	super( true );
    	this.generator = generator;
    }
    
    /**
     * Sets the generator that is used to create keys for unknown Dockables.
     * @param generator the generator
     * @see #createGroupKey(Dockable)
     */
    public void setGenerator( GroupKeyGenerator<? extends K> generator ){
		this.generator = generator;
	}
    
    /**
     * Gets the generator that is used to create keys for unknown Dockables.
     * @return the generator
     * @see #createGroupKey(Dockable)
     */
    public GroupKeyGenerator<? extends K> getGenerator(){
		return generator;
	}
    
    /**
     * If <code>true</code>, groups with no {@link Dockable} associated
     * to, will be deleted automatically.
     * @return <code>true</code> if empty groups are deleted
     */
    public boolean isRemoveEmptyGroups() {
        return removeEmptyGroups;
    }
    
    /**
     * Sets whether empty groups should be removed automatically.<br>
     * A group is a set of {@link Dockable Dockables}. A group can become
     * empty if all it's <code>Dockables</code> are {@link #setGroup(Object, Dockable) transferred}
     * to another group, or removed through an {@link #unbound(Dockable) unbound}.
     * @param removeEmptyGroups <code>true</code> if empty groups should
     * be deleted, <code>false</code> if the should remain in memory and 
     * be used again.
     */
    public void setRemoveEmptyGroups( boolean removeEmptyGroups ) {
        this.removeEmptyGroups = removeEmptyGroups;
    }
    
    public Icon getIcon( Dockable dockable, ActionContentModifier modifier ){
    	return getGroup( dockable ).getIcon( dockable, modifier );
    }

    public ActionContentModifier[] getIconContexts( Dockable dockable ){
    	return getGroup( dockable ).getIconContexts( dockable );
    }
    
    
    public String getText( Dockable dockable ) {
    	return getGroup( dockable ).getText( dockable );
    }
    
    public String getTooltipText( Dockable dockable ) {
    	return getGroup( dockable ).getTooltipText( dockable );
    }

    @Override
    public boolean isEnabled( Dockable dockable ) {
    	return getGroup( dockable ).isEnabled( dockable ) && super.isEnabled( dockable );
    }
    
    @Override
    public void bound( Dockable dockable ) {
        K key = createGroupKey( dockable );
        if( key == null )
            throw new IllegalStateException( "null-key generated, a null-key is not allowed" );
        
        super.bound(dockable);
        
        D action = ensureGroup( key );
        action.bind( dockable );
        dockActions.put( dockable, new Tuple<K, D>( key, action ));
    }
    
    @Override
    public void unbound( Dockable dockable ) {
        super.unbound(dockable);
        
        Tuple<K, D> action = dockActions.remove( dockable );
        action.getB().unbind( dockable );
        removeIfEmpty( action.getA() );
    }
    
    private void removeIfEmpty( K key ){
    	if( removeEmptyGroups ){
	    	D group = getGroup( key );
	    	if( group.getBoundDockables().isEmpty() ){
	    		groups.remove( key );
	    		group.removeDockActionListener( listener );
	    	}
        }
    }

    /**
     * Sets the <code>icon</code> of the group named <code>key</code>.
     * If this group does not exist, it will be created.
     * @param key the name of the group
     * @param icon the new icon of the group, may be <code>null</code>
     */
    public void setIcon( K key, Icon icon ){
        ensureGroup( key ).setIcon( icon );
    }
    
    /**
     * Gets the icon of the group named <code>key</code>.
     * @param key The name of the group
     * @return The icon of the group, may be <code>null</code>
     * @throws IllegalArgumentException If the group does not exist
     * @see #setIcon(Object, Icon)
     */
    public Icon getIcon( Object key ){
        SimpleDockAction action = groups.get( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getIcon();
    }
    
    /**
     * Sets the <code>icon</code> of the group named <code>key</code>.
     * If this group does not exist, it will be created.
     * @param key the name of the group
     * @param modifier tells in which context the icon is used
     * @param icon the new icon of the group, may be <code>null</code>
     */
    public void setIcon( K key, ActionContentModifier modifier, Icon icon ){
        ensureGroup( key ).setIcon( modifier, icon );
    }
    
    /**
     * Gets the icon of the group named <code>key</code>.
     * @param key The name of the group
     * @param modifier tells in which context the icon is used
     * @return The icon of the group, may be <code>null</code>
     * @throws IllegalArgumentException If the group does not exist
     * @see #setIcon(Object, Icon)
     */
    public Icon getIcon( Object key, ActionContentModifier modifier ){
        SimpleDockAction action = groups.get( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getIcon( modifier );
    }
    
    /**
     * Sets the <code>icon</code> which will be shown, when the group
     * named <code>key</code> is disabled. If the group <code>key</code>
     * does not exist, it will be created.
     * @param key The name of the group
     * @param icon The new icon for the disabled-state of the group,
     * may be <code>null</code>
     */
    public void setDisabledIcon( K key, Icon icon ){
        ensureGroup( key ).setDisabledIcon( icon );
    }
    
    public Icon getDisabledIcon( Dockable dockable ){
    	return getGroup( dockable ).getDisabledIcon();
    }
    
    /**
     * Gets the icon that is shown, when the group <code>key</code>
     * is disabled.
     * @param key The name of the group
     * @return The disabled-icon, may be <code>null</code>
     * @throws IllegalArgumentException If the group does not exist
     * @see #setDisabledIcon(Object, Icon)
     */
    public Icon getDisabledIcon( Object key ){
        SimpleDockAction action = groups.get( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getDisabledIcon();
    }
    
    /**
     * Sets the <code>text</code> for group <code>key</code>. If the
     * group does not exist, it will be created.
     * @param key The name of the group
     * @param text The text of the group
     */
    public void setText( K key, String text ){
        ensureGroup( key ).setText( text );
    }
    
    /**
     * Gets the text of the the group <code>key</code>.
     * @param key the key of the group
     * @return the text of the group
     * @throws IllegalArgumentException if the group does not exist
     * @see #setText(Object, String)
     */
    public String getText( Object key ){
        SimpleDockAction action = groups.get( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getText();
    }
    
    /**
     * Sets the tooltip of the group <code>key</code>. If the
     * group does not exist, it will be created. The tooltip is the first
     * part of the tooltip text. The whole tooltip text may contain additional
     * information like the {@link #setAccelerator(Object, KeyStroke) accelerator}.
     * @param key The name of the group
     * @param text The tooltip of the group
     */
    public void setTooltip( K key, String text ){
        ensureGroup( key ).setTooltip( text );
    }
    
    /**
     * Gets the tooltip text of the group <code>key</code>. The tooltip text is
     * the text which is really shown on the tooltip.
     * @param key The name of the group
     * @return The tooltip
     * @throws IllegalArgumentException If the group does not exist
     * @see #setTooltip(Object, String)
     * @see #getTooltip(Object)
     */
    public String getTooltipText( Object key ){
        SimpleDockAction action = groups.get( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getTooltipText();
    }
    
    /**
     * Gets the tooltip of the group <code>key</code>. The tooltip if the
     * first part of the tooltip text.
     * @param key The name of the group
     * @return The tooltip
     * @throws IllegalArgumentException If the group does not exist
     * @see #setTooltip(Object, String)
     */
    public String getTooltip( Object key ){
        SimpleDockAction action = groups.get( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getTooltip();
    }
    
    /**
     * Sets the enabled-state of the group <code>key</code>. This action
     * can only be triggered, if the associated {@link Dockable} is
     * in a group with <code>true</code> enable-state. If the group
     * does not exist, it will be created.
     * @param key The name of the group
     * @param enabled The state of the group
     */
    public void setEnabled( K key, boolean enabled ){
        ensureGroup( key ).setEnabled( enabled );
    }
    
    /**
     * Gets the enabled-state of the group <code>key</code>.
     * @param key The name of the group
     * @return The enabled-state
     * @throws IllegalArgumentException If the group does not exist
     * @see #setEnabled(Object, boolean)
     */
    public boolean isEnabled( Object key ){
        D action = getGroup( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.isEnabled();
    }

    public Dockable getDockableRepresentation( Dockable dockable ){
	    return getGroup( dockable ).getDockableRepresentation( dockable );
    }
    
    /**
     * Gets the {@link Dockable} which is represented by this {@link DockAction}.
     * @param key the name of the group
     * @return the element, can be <code>null</code>
     * @throws IllegalArgumentException if the group does not exist
     */
    public Dockable getDockableRepresentation( K key ){
        SimpleDockAction action = groups.get( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getDockableRepresentation();
    }
    
    /**
     * Sets the {@link Dockable} that is represented by this group.
     * @param key the name of the group
     * @param dockable the new represented {@link Dockable}, can be <code>null</code>
     */
    public void setDockableRepresentation( K key, Dockable dockable ){
    	ensureGroup( key ).setDockableRepresentation( dockable );
    }
    
    /**
     * Set the type of {@link java.awt.event.KeyEvent} that will trigger
     * an action.
     * @param key the group of actions whose trigger might be affected
     * @param accelerator the new key-trigger or <code>null</code>
     */
    public void setAccelerator( K key, KeyStroke accelerator ){
    	ensureGroup( key ).setAccelerator( accelerator );
    }
    
    /**
     * Gets the type of {@link java.awt.event.KeyEvent} that will
     * trigger an action.
     * @param key the affected group
     * @return the type of event or <code>null</code>
     * @throws IllegalArgumentException if <code>key</code> is unknown
     */
    public KeyStroke getAccelerator( K key ){
    	SimpleDockAction action = groups.get( key );
    	if( action == null )
    		throw new IllegalArgumentException( "There is no such group" );
    	return action.getAccelerator();
    }
    
    /**
     * Ensures that there exist a group with the name <code>key</code>.
     * @param key The name of the group
     * @return The group with the name <code>key</code>. This may be
     * a newly created group, or a group that already existed.
     */
    protected D ensureGroup( K key ){
        if( key == null )
            throw new IllegalArgumentException( "The key must be a non-null value" );
        
        D action = groups.get( key );
        if( action == null ){
        	action = createGroup( key );
            action.addDockActionListener( listener );
            groups.put( key, action );
        }
        return action;
    }
    
    /**
     * Gets the group in which <code>dockable</code> is.
     * @param dockable the Dockable whose group is searched
     * @return the group or <code>null</code>
     */
    protected D getGroup( Dockable dockable ){
    	Tuple<K, D> group = dockActions.get( dockable );
    	return group == null ? null : group.getB();
    }
    
    /**
     * Gets the group with the given key.
     * @param key the key of the group
     * @return the group or <code>null</code>
     */
    protected D getGroup( Object key ){
    	return groups.get( key );
    }
    
    /**
     * Creates a new group.
     * @param key the key of the new group
     * @return the new group
     */
    protected abstract D createGroup( K key );
    
    /**
     * Returns <code>true</code> if a group with the name of <code>key</code>
     * exists, return <code>false</code> otherwise.
     * @param key The group that is searched
     * @return <code>true</code> if <code>key</code> was found, <code>false</code>
     * otherwise
     */
    public boolean groupExists( Object key ){
        return groups.containsKey( key );
    }

    /**
     * Removes a group but only if the group is empty (no {@link Dockable Dockables}
     * are registered in that group).
     * @param key The name of the group 
     * @return <code>true</code> if there is no longer a group with name
     * <code>key</code> (also <code>true</code> if there never existed
     * a group with that name), or <code>false</code> if the group
     * was not deleted because it was not empty.
     */
    public boolean removeGroup( Object key ){
        SimpleDockAction group = groups.get( key );
        if( group == null )
            return true;
        
        if( group.getBoundDockables().isEmpty() ){
            group.removeDockActionListener( listener );
            groups.remove( key );
            return true;
        }
        else
            return false;
    }
    
    /**
     * Calculates the name of the group to which the <code>dockable</code>
     * should be added.<br>
     * Every {@link Dockable} is member of one group. The membership
     * determines text, icon, etc. for the dockable. Whenever a 
     * dockable is {@link #bound(Dockable) bound} to this action,
     * the group will be determined by this method. Later on, the group
     * can be changed by the method {@link #setGroup(Object, Dockable) setGroup}.<br>
     * The default implementation uses the {@link #getGenerator() generator} of
     * this action.
     * @param dockable The {@link Dockable} whose group has to be 
     * found
     * @return the name of the dockable's group. That can be an existing
     * or a non existing group. <code>null</code> is not a valid result.
     */
    protected K createGroupKey( Dockable dockable ){
    	return generator.generateKey( dockable );
    }
    
    /**
     * Assigns the <code>dockable</code> to the group with the given <code>key</code>.
     * @param key The name of the new group
     * @param dockable The {@link Dockable} whose membership will be changed.
     * The dockable must already be in a group of this action.
     * @throws IllegalArgumentException if the {@link Dockable} is not
     * in a group, or if <code>key</code> is <code>null</code>
     * @see #createGroupKey(Dockable)
     */
    public void setGroup( K key, Dockable dockable ){
        if( key == null )
            throw new IllegalArgumentException( "Key must not be null" );
        
        Tuple<K, D> old = dockActions.get( dockable );
        if( old == null )
            throw new IllegalArgumentException( "Dockable was not registered" );
        
        D put = ensureGroup( key );
        old.getB().unbind( dockable );
        removeIfEmpty( old.getA() );
        
        put.bind( dockable );
        dockActions.put( dockable, new Tuple<K, D>( key, put ) );
        
        Set<Dockable> set = new HashSet<Dockable>();
        set.add( dockable );
        fireActionEnabledChanged( set );
        fireActionIconChanged( null, set );
        fireActionTextChanged( set );
        fireActionTooltipTextChanged( set );
        fireActionRepresentativeChanged( set );
    }
    
    /**
     * Tells whether the <code>dockable</code> is {@link #bound(Dockable) bound}
     * to this action, or not. If the <code>dockable</code> was {@link #unbind(Dockable) unbound},
     * then this method will return <code>false</code>.
     * @param dockable the {@link Dockable} to search
     * @return <code>true</code> if the {@link Dockable} is bound
     */
    public boolean isKnown( Dockable dockable ){
        return dockActions.containsKey( dockable );
    }
    
    public boolean trigger( Dockable dockable ) {
        return getGroup( dockable ).trigger( dockable );
    }
    
    /**
     * A listener to the groups.
     * @author Benjamin Sigg
     */
    private class Listener implements StandardDockActionListener{
        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            fireActionTooltipTextChanged( dockables );
        }
        
        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            fireActionTextChanged( dockables );
        }

        public void actionIconChanged( StandardDockAction action, ActionContentModifier modifier, Set<Dockable> dockables ){
            fireActionIconChanged( modifier, dockables );
        }
        
        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ) {
            fireActionEnabledChanged( dockables );
        }
        
        public void actionRepresentativeChanged( StandardDockAction action, Set<Dockable> dockables ){
	        fireActionRepresentativeChanged( dockables );	
        }
    }
}
