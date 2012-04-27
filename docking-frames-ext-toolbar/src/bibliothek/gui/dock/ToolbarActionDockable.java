/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Hervé Guillaume, Benjamin Sigg
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
 * Hervé Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.dockable.DockableIcon;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.station.OrientationObserver;
import bibliothek.gui.dock.station.toolbar.ToolbarActionDockableFactory;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.icon.DockIcon;

/**
 * This specialized {@link Dockable} shows a {@link DockAction} as {@link Component}. The {@link DockAction}
 * will be converted into a {@link JComponent} by using the target {@link #TOOLBAR}. 
 * @author Benjamin Sigg
 */
public class ToolbarActionDockable extends AbstractDockable {
	/**
	 * A target for converting a {@link DockAction} into a {@link Component} that can be shown on
	 * a toolbar. If nothing else is defined, this target will automatically fall back to {@link ViewTarget#TARGET} 
	 */
	public static final ViewTarget<BasicTitleViewItem<JComponent>> TOOLBAR =
			new ViewTarget<BasicTitleViewItem<JComponent>>( "target TOOLBAR" );

	/** the panel actually showing {@link #view} */
	private JPanel content = new JPanel( new GridLayout( 1, 1 ) );
	
	/** the action shown on this dockable */
	private DockAction action;
	
	/** the conversion of {@link #action} into a {@link JComponent} */
	private BasicTitleViewItem<JComponent> view;
	
	/** the orientation of the {@link #view} */
	private Orientation orientation = Orientation.FREE_HORIZONTAL;
	
	/** Called if the current {@link DockTheme} changes */
	private UIListener uiListener = new UIListener(){
		@Override
		public void themeWillChange( DockController controller, DockTheme oldTheme, DockTheme newTheme ){
			// ignore
		}
		@Override
		public void themeChanged( DockController controller, DockTheme oldTheme, DockTheme newTheme ){
			destroyView();
			createView();
		}
		@Override
		public void updateUI( DockController controller ){
			destroyView();
			createView();	
		}
	};
	
	/**
	 * Creates a new dockable showing {@link #action}
	 * @param action the action to show on this dockable
	 */
	public ToolbarActionDockable( DockAction action ){
		super( PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP );
		if( action == null ){
			throw new IllegalArgumentException( "action must not be null" );
		}
		this.action = action;
		
		new OrientationObserver( this ){
			@Override
			protected void orientationChanged( bibliothek.gui.Orientation current ){
				if( current != null ){
					if( current == bibliothek.gui.Orientation.HORIZONTAL ){
						setOrientation( Orientation.FREE_HORIZONTAL );
					}
					else{
						setOrientation( Orientation.FREE_VERTICAL );
					}
				}
			}
		};
	}
	
	private void setOrientation( Orientation orientation ){
		this.orientation = orientation;
		if( view != null ){
			view.setOrientation( orientation );
		}
	}
	
	@Override
	public void setController( DockController controller ){
		if( getController() != null ){
			getController().getThemeManager().removeUIListener( uiListener );
			destroyView();
		}
		super.setController( controller );
		if( controller != null ){
			createView();
			controller.getThemeManager().addUIListener( uiListener );
		}
	}
	
	private void destroyView(){
		if( getController() != null ){
			if( view != null ){
				view.unbind();
				view = null;
			}
			action.unbind( this );
			content.removeAll();
		}
	}
	
	private void createView(){
		DockController controller = getController();
		if( controller != null ){
			action.bind( this );
			view = action.createView( TOOLBAR, controller.getActionViewConverter(), this );
			if( view != null ){
				view.setOrientation( orientation );
				view.bind();
				content.add( view.getItem() );
			}
		}
	}
	
	@Override
	public Component getComponent(){
		return content;
	}

	@Override
	public DockStation asDockStation(){
		return null;
	}

	@Override
	public String getFactoryID(){
		return ToolbarActionDockableFactory.ID;
	}

	@Override
	protected DockIcon createTitleIcon(){
		return new DockableIcon("dockable.default", this){
			@Override
			protected void changed( Icon oldValue, Icon newValue ){
				fireTitleIconChanged(oldValue, newValue);
			}
		};
	}
}
