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

package bibliothek.gui.dock.action.view;

import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.themes.basic.action.*;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;
import bibliothek.gui.dock.themes.basic.action.buttons.DropDownMiniButton;
import bibliothek.gui.dock.themes.basic.action.buttons.MiniButton;
import bibliothek.gui.dock.themes.basic.action.dropdown.ButtonDropDownHandler;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;
import bibliothek.gui.dock.themes.basic.action.dropdown.SelectableDropDownHandler;
import bibliothek.gui.dock.themes.basic.action.dropdown.SubDropDownHandler;
import bibliothek.gui.dock.themes.basic.action.menu.*;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.util.container.Tuple;

/**
 * The <code>ActionViewConverter</code> transforms {@link DockAction DockActions} into 
 * views like buttons or menu-items.<br>
 * Every application has a set <code>DockAction</code>s. In order to create a view for an 
 * action, the {@link ActionType} of the <code>DockAction</code> must be known. The 
 * {@link ActionType} tells how the action normally behaves. Some types are
 * already defined, for example the {@link ActionType#BUTTON} behaves like a
 * button: once the action is triggered, it does something, and later the action
 * can be triggered again. There are several <code>DockAction</code>s which act like
 * a button, but their internal organization differs a lot.<br>
 * On the other hand, every application has a set of platforms which want to
 * display a <code>DockAction</code>. A platform might be a popup-menu, or one of the
 * many {@link DockTitle}s. Since some platforms need the same 
 * visualization of <code>DockAction</code>s (i.e. a popup-menu and a normal menu both need
 * menu-items), the platforms are grouped. Every group is identified by a 
 * {@link ViewTarget}. There are already some <code>ViewTarget</code>s defined, i.e. the 
 * {@link ViewTarget#TITLE} is used for the group of <code>DockTitle</code>s.<br>
 * The <code>ActionViewConverter</code> must known how to create a view for all possible
 * pairs of {@link ActionType}s and {@link ViewTarget}s. In order to do so, he has a set
 * of {@link ViewGenerator ViewGenerators}. Each <code>ViewGenerator</code> is used to handle
 * one pair of <code>ActionType</code> and <code>ViewTarget</code>.<br>
 * The <code>ActionViewConverter</code> has three slots for each pair. There can be a 
 * <code>ViewGenerator</code> in every slot. The slots have different priority and meaning.
 * Whenever a <code>ViewGenerator</code> for a pair is needed, the slots are searched for the
 * first non-<code>null</code> value with the highest priority. The meaning
 * of the three slots are:
 * <ul>
 * <li>Client: slot that might be filled by client-code. This slot has the
 * highest priority and will be used whenever possible.</li>
 * <li>Theme: slot that might be filled by a {@link DockTheme}. This slot
 * is only used if the client-slot is empty.</li>
 * <li>Default: the slot that will be used if the other two slots are empty</li>
 * </ul>
 * <br>
 * <b>Note:</b> if a client creates new <code>ActionType</code>s or new <code>ViewTarget</code>s, he has
 * to provide the <code>ViewGenerator</code>s for all new possible pairs. That includes pairs
 * where one partner is a predefined <code>ActionType</code> or <code>ViewTarget</code>.
 * @author Benjamin Sigg
 */
public class ActionViewConverter {
	/** the converters known to this ActionViewConverter */
	private Map<Tuple<ActionType<?>, ViewTarget<?>>, Entry<?,?>> converters =
		new HashMap<Tuple<ActionType<?>, ViewTarget<?>>, Entry<?,?>>();
	
	/**
	 * Creates a new ActionViewConverter
	 */
	public ActionViewConverter(){
		// Converter for menu
		
		putDefault( ActionType.BUTTON, ViewTarget.MENU, new ViewGenerator<ButtonDockAction, MenuViewItem<JComponent>>(){
			public MenuViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ){
				return new ButtonMenuHandler( action, dockable );
			}
		});
		
		putDefault( ActionType.CHECK, ViewTarget.MENU, new ViewGenerator<SelectableDockAction, MenuViewItem<JComponent>>(){
			public MenuViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				return new SelectableMenuHandler( action, dockable, new JCheckBoxMenuItem() );
			}
		});
		
		putDefault( ActionType.MENU, ViewTarget.MENU, new ViewGenerator<MenuDockAction, MenuViewItem<JComponent>>(){
			public MenuViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
				return new MenuMenuHandler( action, dockable );
			}
		});
		
		putDefault( ActionType.RADIO, ViewTarget.MENU, new ViewGenerator<SelectableDockAction, MenuViewItem<JComponent>>(){
			public MenuViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				return new SelectableMenuHandler( action, dockable, new JRadioButtonMenuItem() );
			}
		});
		
		putDefault( ActionType.SEPARATOR, ViewTarget.MENU, new ViewGenerator<SeparatorAction, MenuViewItem<JComponent>>(){
			public MenuViewItem<JComponent> create( ActionViewConverter converter, SeparatorAction action, Dockable dockable ){
				if( action.shouldDisplay( ViewTarget.MENU ))
					return new BasicSeparatorHandler( new JPopupMenu.Separator(), action );
				else
					return null;
			}
		});
		
		putDefault( ActionType.DROP_DOWN, ViewTarget.MENU, new ViewGenerator<DropDownAction, MenuViewItem<JComponent>>(){
			public MenuViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ){
				return new DropDownMenuHandler( action, dockable );
			}
		});
		
		// Converter for title
		
		putDefault( ActionType.BUTTON, ViewTarget.TITLE, new ViewGenerator<ButtonDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ){
                BasicButtonHandler handler = new BasicButtonHandler( action, dockable );
                MiniButton<BasicButtonModel> button = new BasicMiniButton( handler, handler );
                handler.setModel( button.getModel() );
                return handler;
			}
		});
		
		putDefault( ActionType.CHECK, ViewTarget.TITLE, new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
                BasicSelectableHandler.Check handler = new BasicSelectableHandler.Check( action, dockable );
                MiniButton<BasicButtonModel> button = new BasicMiniButton( handler, handler );
                handler.setModel( button.getModel() );
                return handler;
			}
		});
		
		putDefault( ActionType.MENU, ViewTarget.TITLE, new ViewGenerator<MenuDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
                BasicMenuHandler handler = new BasicMenuHandler( action, dockable );
                MiniButton<BasicButtonModel> button = new BasicMiniButton( handler, handler );
                handler.setModel( button.getModel() );
                return handler;
			}
		});
		
		putDefault( ActionType.RADIO, ViewTarget.TITLE, new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				BasicSelectableHandler.Radio handler = new BasicSelectableHandler.Radio( action, dockable );
                MiniButton<BasicButtonModel> button = new BasicMiniButton( handler, handler );
                handler.setModel( button.getModel() );
                return handler;
			}
		});
		
		putDefault( ActionType.SEPARATOR, ViewTarget.TITLE, new ViewGenerator<SeparatorAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SeparatorAction action, Dockable dockable ){
				if( action.shouldDisplay( ViewTarget.TITLE ))
					return new BasicSeparatorHandler( new JSeparator(), action );
				else
					return null;
			}
		});
		
		putDefault( ActionType.DROP_DOWN, ViewTarget.TITLE, new ViewGenerator<DropDownAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ){
                BasicDropDownButtonHandler handler = new BasicDropDownButtonHandler( action, dockable );
                DropDownMiniButton button = new DropDownMiniButton( handler );
                handler.setModel( button.getModel() );
                return handler;
			}
		});
		
		// Converter for drop down buttons
		
		putDefault( ActionType.BUTTON, ViewTarget.DROP_DOWN, new ViewGenerator<ButtonDockAction, DropDownViewItem>(){
			public DropDownViewItem create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ){
				return new ButtonDropDownHandler( action, dockable, new JMenuItem() );
			}
		});
		
		putDefault( ActionType.CHECK, ViewTarget.DROP_DOWN, new ViewGenerator<SelectableDockAction, DropDownViewItem>(){
			public DropDownViewItem create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				return new SelectableDropDownHandler( action, dockable, new JCheckBoxMenuItem() );
			}
		});
		
		putDefault( ActionType.RADIO, ViewTarget.DROP_DOWN, new ViewGenerator<SelectableDockAction, DropDownViewItem>(){
			public DropDownViewItem create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				return new SelectableDropDownHandler( action, dockable, new JRadioButtonMenuItem() );
			}
		});
		
		putDefault( ActionType.SEPARATOR, ViewTarget.DROP_DOWN, new ViewGenerator<SeparatorAction, DropDownViewItem>(){
			public DropDownViewItem create( ActionViewConverter converter, SeparatorAction action, Dockable dockable ){
				if( action.shouldDisplay( ViewTarget.DROP_DOWN ))
					return new SubDropDownHandler( new BasicSeparatorHandler( new JPopupMenu.Separator(), action ));
				else
					return null;
			}
		});
		
		putDefault( ActionType.DROP_DOWN, ViewTarget.DROP_DOWN, new ViewGenerator<DropDownAction, DropDownViewItem>(){
			public DropDownViewItem create( ActionViewConverter converter, DropDownAction action, Dockable dockable ){
				return new SubDropDownHandler( action.createView( ViewTarget.MENU, converter, dockable ) );
			}
		});
		
		putDefault( ActionType.MENU, ViewTarget.DROP_DOWN, new ViewGenerator<MenuDockAction, DropDownViewItem>(){
			public DropDownViewItem create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
				return new SubDropDownHandler( action.createView( ViewTarget.MENU, converter, dockable ) );
			}
		});
	}
	
	/**
	 * Registers a new {@link ViewGenerator} to this ActionViewConverter. The
	 * generator will have the high priority.
	 * @param <A> the type of view created by the converter
	 * @param <D> the type of action needed as input for the converter
	 * @param action the type of actions needed as input
	 * @param target the platform for which <code>converter</code> creates output
	 * @param generator the generator to store, may be <code>null</code>
	 */
	public <A, D extends DockAction> void putClient( ActionType<D> action, ViewTarget<A> target, ViewGenerator<D,A> generator ){
		if( action == null )
			throw new IllegalArgumentException( "Action must not be null" );
		
		if( target == null )
			throw new IllegalArgumentException( "Target must not be null" );
		
		Entry<D,A> entry = getEntry( action, target );
		entry.clientGenerator = generator;
	}
	
	/**
	 * Registers a new {@link ViewGenerator} to this ActionViewConverter. The
	 * generator will have the normal priority.
	 * @param <A> the type of view created by the converter
	 * @param <D> the type of action needed as input for the converter
	 * @param action the type of actions needed as input
	 * @param target the platform for which <code>converter</code> creates output
	 * @param generator the generator to store, may be <code>null</code>
	 */
	public <A, D extends DockAction> void putTheme( ActionType<D> action, ViewTarget<A> target, ViewGenerator<D,A> generator ){
		if( action == null )
			throw new IllegalArgumentException( "Action must not be null" );
		
		if( target == null )
			throw new IllegalArgumentException( "Target must not be null" );
		
		Entry<D,A> entry = getEntry( action, target );
		entry.themeGenerator = generator;
	}

	/**
	 * Registers a new {@link ViewGenerator} to this ActionViewConverter. The
	 * generator will have the low priority.
	 * @param <A> the type of view created by the converter
	 * @param <D> the type of action needed as input for the converter
	 * @param action the type of actions needed as input
	 * @param target the platform for which <code>converter</code> creates output
	 * @param generator the generator to store, may be <code>null</code>
	 */
	public <A, D extends DockAction> void putDefault( ActionType<D> action, ViewTarget<A> target, ViewGenerator<D,A> generator ){
		if( action == null )
			throw new IllegalArgumentException( "Action must not be null" );
		
		if( target == null )
			throw new IllegalArgumentException( "Target must not be null" );
		
		Entry<D,A> entry = getEntry( action, target );
		entry.defaultGenerator = generator;
	}


	/**
	 * Creates and sets up a new view. This method does nothing more than
	 * calling the method {@link DockAction#createView(ViewTarget, ActionViewConverter, Dockable) createView}
	 * of {@link DockAction}.
	 * @param <A> the type of the view
	 * @param action the action for which a view is created
	 * @param target the target platform, where the view will be shown
	 * @param dockable the Dockable for which the action is used
	 * @return the new view or <code>null</code> if nothing should be shown
	 * @throws IllegalArgumentException if an unknown argument is used
	 */
	public <A> A createView( DockAction action, ViewTarget<A> target, Dockable dockable ){
		return action.createView( target, this, dockable );
	}
	
	/**
	 * Creates and sets up a new view.
	 * @param <A> the type of the view
	 * @param <D> the type of action to convert
	 * @param type the type of action
	 * @param action the action for which a view is created
	 * @param target the target platform, where the view will be shown
	 * @param dockable the Dockable for which the action is used
	 * @return the new view or <code>null</code> if nothing should be shown
	 * @throws IllegalArgumentException if an unknown argument is used
	 */
	public <A, D extends DockAction> A createView( ActionType<D> type, D action, ViewTarget<A> target, Dockable dockable ){
		ViewGenerator<D,A> converter = getConverter( type, target );
		if( converter == null )
			throw new IllegalArgumentException( "That combination is not known: " + type + " " + target );
		
		return converter.create( this, action, dockable );
	}
	
	/**
	 * Searches a converter for the given <code>action</code> and <code>target</code>.
	 * @param <A> the type that the converter will produce
	 * @param <D> the type of action needed as input
	 * @param action the action that will be transformed
	 * @param target the target platform
	 * @return the converter or <code>null</code> if no converter is found
	 */
	protected <A, D extends DockAction> ViewGenerator<D,A> getConverter( ActionType<D> action, ViewTarget<? super A> target ){
		Entry<D, A> entry = getEntry( action, target );
		if( entry.clientGenerator != null )
			return entry.clientGenerator;
		
		if( entry.themeGenerator != null )
			return entry.themeGenerator;
		
		return entry.defaultGenerator;
	}
	
	/**
	 * Searches an entry for the given <code>action</code> and <code>target</code>.
	 * @param <A> the type that the converter will produce
	 * @param <D> the type of action needed as input
	 * @param action the action that will be transformed
	 * @param target the target platform
	 * @return the converter or <code>null</code> if no converter is found
	 */
	@SuppressWarnings( "unchecked" )
	private <A, D extends DockAction> Entry<D, A> getEntry( ActionType<D> action, ViewTarget<? super A> target ){
		Entry<?,?> result = converters.get( new Tuple<ActionType<?>, ViewTarget<?>>( action, target ));
		if( result == null ){
			result = new Entry<D,A>();
			converters.put( new Tuple<ActionType<?>, ViewTarget<?>>( action, target ), result );
		}
		return (Entry<D, A>)result;
	}
	
	/**
	 * A set of generators.
	 * @author Benjamin Sigg
	 *
	 * @param <D> the actions needed as input for the generators
	 * @param <A> the output view of the generators
	 */
	private static class Entry<D extends DockAction, A>{
		/** generator provided by the client */
		public ViewGenerator<D, A> clientGenerator;
		
		/** generator provided by a {@link DockTheme} */
		public ViewGenerator<D, A> themeGenerator;
		
		/** default generator, used if no other generator is known */
		public ViewGenerator<D, A> defaultGenerator;
	}
}
