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
package bibliothek.gui.dock.focus;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

/**
 * A {@link FocusTraversalPolicy} that uses the algorithms of a {@link SimplifiedFocusTraversalPolicy}
 * to do its work. This policy ensures that only valid {@link Component}s
 * are selected, and it respects the constraints for nested policies.
 * @author Benjamin Sigg
 */
public class DockFocusTraversalPolicy extends FocusTraversalPolicy {
    /** the delegate used to determine the next or previous component */
    private SimplifiedFocusTraversalPolicy policy;

    /** whether this policy should try to create real circles or not */
    private boolean circle;

    /** detects recursion in the {@link #getComponentBefore(Container, Component)} method */
    private boolean recursionComponentBefore = false;

    /** detects recursion in the {@link #getComponentAfter(Container, Component)} method */
    private boolean recursionComponentAfter = false;

    /** detects recursion in the {@link #getDefaultComponent(Container)} method */
    private boolean recursionDefaultComponent = false;

    /**
     * Creates a new policy.
     * @param policy the delegate providing algorithms for this policy
     * @param circle if <code>true</code> then this policy does not respect
     * the exact description of the functionality of {@link FocusTraversalPolicy}
     * in order to ensure that <code>getComponentAfter( getComponentBefore( x )) == x</code> and
     * <code>getComponentBefore( getComponentAfter( x )) == x</code>.
     */
    public DockFocusTraversalPolicy( SimplifiedFocusTraversalPolicy policy, boolean circle ){
        if( policy == null )
            throw new IllegalArgumentException( "policy must not be null" );

        this.policy = policy;
        this.circle = circle;
    }

    /**
     * Tells whether <code>component</code> can be focused or not.
     * @param component some {@link Component} which might gain the focus.
     * @return <code>true</code> if <code>component</code> is allowed to
     * gain the focus, <code>false</code> otherwise
     */
    protected boolean accept( Component component ){
        return component.isFocusable() &&
        component.isEnabled() &&
        component.isDisplayable() &&
        component.isShowing();
    }

    @Override
    public Component getComponentAfter( Container container, Component component ) {
        if( recursionComponentAfter ){
            return policy.getAfter( container, component );
        }

        try{
            recursionComponentAfter = true;

            Component next = after( component );

            while( true ){
                if( next == component )
                    return null;

                if( next == null )
                    return null;

                if( next instanceof Container ){
                    Container nextContainer = (Container)next;
                    if( !nextContainer.isFocusCycleRoot() && nextContainer.isFocusTraversalPolicyProvider() ){
                        Component selected;
                        if( circle )
                            selected = nextContainer.getFocusTraversalPolicy().getFirstComponent( nextContainer );
                        else
                            selected = nextContainer.getFocusTraversalPolicy().getDefaultComponent( nextContainer );

                        if( selected == next )
                            return next;
                        next = selected;
                        continue;
                    }
                }

                if( accept( next ))
                    return next;

                next = after( next );
            }
        }
        finally{
            recursionComponentAfter = false;
        }
    }

    /**
     * Searches the next {@link Component} which might gain the focus. This
     * method searches recursively through the tree of {@link Component}s, but
     * does not loop.
     * @param component the currently focused {@link Component}.
     * @return the next {@link Component} which might gain the focus
     */
    protected Component after( Component component ){
        Container provider = getRootOrProvider( component );
        if( provider == null )
            return null;

        FocusTraversalPolicy providerPolicy = getFocusTraversalPolicy( provider );
        if( providerPolicy == null )
            return null;

        Component result = providerPolicy.getComponentAfter( provider, component );

        if( provider.isFocusCycleRoot() ){
            return result;
        }
        else{ // is policy provider
            if( providerPolicy == this ){
                if( result == null || policy.getFirst( provider ) == result ){
                    result = after( provider );
                }
            }
            else{
                if( result == null || providerPolicy.getFirstComponent( provider ) == result ){
                    result = after( provider );
                }
            }
            if( result == component )
            	return null;
            return result;
        }
    }

    @Override
    public Component getComponentBefore( Container container, Component component ) {
        if( recursionComponentBefore ){
            return policy.getBefore( container, component );
        }
        try{
            recursionComponentBefore = true;

            Component previous = before( component );

            while( true ){
                if( previous == component )
                    return null;

                if( previous == null )
                    return null;

                if( previous instanceof Container ){
                    Container previousContainer = (Container)previous;
                    if( !previousContainer.isFocusCycleRoot() && previousContainer.isFocusTraversalPolicyProvider() ){
                        Component selected;
                        if( circle )
                            selected = previousContainer.getFocusTraversalPolicy().getLastComponent( previousContainer );
                        else
                            selected = previousContainer.getFocusTraversalPolicy().getDefaultComponent( previousContainer );

                        if( selected == previous )
                            return previous;
                        previous = selected;
                        continue;
                    }
                }

                if( accept( previous ))
                    return previous;

                previous = after( previous );
            }
        }
        finally{
            recursionComponentBefore = false;
        }
    }


    /**
     * Searches the previous {@link Component} which might gain the focus. This
     * method searches recursively through the tree of {@link Component}s, but
     * does not loop.
     * @param component the currently focused {@link Component}.
     * @return the previous {@link Component} which might gain the focus
     */
    protected Component before( Component component ){
        Container provider = getRootOrProvider( component );
        FocusTraversalPolicy providerPolicy = getFocusTraversalPolicy( provider );

        Component result = providerPolicy.getComponentBefore( provider, component );

        if( provider.isFocusCycleRoot() ){
            return result;
        }
        else{ // is policy provider
            if( providerPolicy == this ){
                if( result == null || policy.getLast( provider ) == result ){
                    result = before( provider );
                }
            }
            else{
                if( result == null || providerPolicy.getLastComponent( provider ) == result ){
                    result = before( provider );
                }
            }
            if( result == component )
            	return null;
            return result;
        }
    }

    @Override
    public Component getDefaultComponent( Container container ) {
        if( recursionDefaultComponent ){
            return policy.getDefault( container );
        }
        try{
            recursionDefaultComponent = true;

            FocusTraversalPolicy providerPolicy = getFocusTraversalPolicy( container );
            Component component = providerPolicy.getDefaultComponent( container );
            
            if( component == container )
                return component;

            if( component instanceof Container ){
                Container ccontainer = (Container)component;
                if( ccontainer.isFocusCycleRoot() || ccontainer.isFocusTraversalPolicyProvider() ){
                    Component result = getDefaultComponent( ccontainer );
                    if( result != null )
                        return result;
                }
            }

            return component;
        }
        finally{
            recursionDefaultComponent = false;
        }
    }

    @Override
    public Component getFirstComponent( Container container ) {
        FocusTraversalPolicy providerPolicy = getFocusTraversalPolicy( container );
        
        Component component = providerPolicy.getDefaultComponent( container );

        if( component == container )
            return component;

        if( component instanceof Container ){
            Container ccontainer = (Container)component;
            if( ccontainer.isFocusCycleRoot() || ccontainer.isFocusTraversalPolicyProvider() ){
                Component result = getFirstComponent( ccontainer );
                if( result != null )
                    return result;
            }
        }

        return component;
    }

    @Override
    public Component getLastComponent( Container container ) {
        FocusTraversalPolicy providerPolicy = getFocusTraversalPolicy( container );
        Component component = providerPolicy.getDefaultComponent( container );

        if( component == container )
            return component;

        if( component instanceof Container ){
            Container ccontainer = (Container)component;
            if( ccontainer.isFocusCycleRoot() || ccontainer.isFocusTraversalPolicyProvider() ){
                Component result = getLastComponent( ccontainer );
                if( result != null )
                    return result;
            }
        }

        return component;
    }

    /**
     * Searches the first parent of <code>component</code> that is either
     * a {@link Container#isFocusCycleRoot() focus cycle root} or
     * a {@link Container#isFocusTraversalPolicyProvider() policy provider}.
     * @param component some component
     * @return some parent or <code>null</code>
     */
    protected Container getRootOrProvider( Component component ){
        Container container = component.getParent();

        while( container != null ){
            if( container.isFocusCycleRoot() || container.isFocusTraversalPolicyProvider() )
                return container;

            container = container.getParent();
        }

        return null;
    }

    /**
     * Searches the {@link FocusTraversalPolicy} which should be used by
     * <code>provider</code>. This method searches for a focus cycle root or
     * policy provider whose traversal policy is {@link Container#isFocusTraversalPolicySet() set}.
     * @param provider a focus cycle root or policy provider whose 
     * {@link SimplifiedFocusTraversalPolicy} is searched.
     * @return the policy of <code>provider</code> or <code>null</code>
     */
    protected FocusTraversalPolicy getFocusTraversalPolicy( Container provider ){
        while( provider != null ){
            if( provider.isFocusCycleRoot() || provider.isFocusTraversalPolicyProvider() ){
                if( provider.isFocusTraversalPolicySet() ){
                    return provider.getFocusTraversalPolicy();
                }
            }

            provider = provider.getParent();
        }

        return null;
    }
}
