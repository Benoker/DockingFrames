package tutorial.common.basics;

import java.awt.Color;

import javax.swing.JComponent;

import tutorial.support.ColorIcon;
import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockActionLocation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CloseActionFactory;
import bibliothek.gui.dock.common.action.predefined.CCloseAction;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.themes.basic.action.BasicButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;
import bibliothek.gui.dock.util.IconManager;

@Tutorial(title="A close button like Eclipse has", id="EclipseLikeCloseButton")
public class EclipseLikeCloseButtonExample {
	public static void main( String[] args ){
		/* The EclipseTheme is a close match to the look and feel of Eclipse. But maybe you want the close button
		 * not to have any borders but just change the icon when hovering over it? */
		
		/* We start by creating a frame... */
		JTutorialFrame frame = new JTutorialFrame( EclipseLikeCloseButtonExample.class );
		
		/* ... and a control */
		CControl control = new CControl( frame );
		
		/* We set the EclipseTheme... */
		control.setTheme( ThemeMap.KEY_ECLIPSE_THEME );
		/* ... and at the same time configure the framework to support our specialized button view. This view
		 * does not paint a button without any borders. */
		control.getController().getActionViewConverter().putClient( FLAT_BUTTON, ViewTarget.TITLE, new FlatButtonGenerator() );
		
		/* Then we replace the icons with our custom icons. For the sake of simpliciy we just use ovals, but you
		 * can of course use nicer icons in your application. */
		IconManager icons = control.getIcons();
		icons.setIconClient( "close", new ColorIcon( Color.WHITE ) );
		icons.setIconClient( "close.hover", new ColorIcon( Color.ORANGE ) );
		icons.setIconClient( "close.pressed", new ColorIcon( Color.RED ) );
		
		/* We need to introduce a subclass of the standart "close action" for further customization. In order to do
		 * this we change the "close action factory". */
		control.putProperty( CControl.CLOSE_ACTION_FACTORY, new CloseActionFactory(){
			public CAction create( CControl control, CDockable dockable ){
				return new EclipseCloseButton( control );
			}
		});
		
		/* And now we just add some closeable dockables to the application */
		frame.add( control.getContentArea() );
		
		/* First we create the dockables and make them closeable... */
		ColorSingleCDockable red = new ColorSingleCDockable( "Red", Color.RED );
		red.setCloseable( true );
		
		ColorSingleCDockable green = new ColorSingleCDockable( "Green", Color.GREEN );
		green.setCloseable( true );
		
		ColorSingleCDockable blue = new ColorSingleCDockable( "Blue", Color.BLUE );
		blue.setCloseable( true );
		
		/* and then we make the dockables visible */
		CGrid grid = new CGrid( control );
		grid.add( 0, 0, 1, 1, red, green, blue );
		control.getContentArea().deploy( grid );
		
		frame.setVisible( true );
	}
	
	/* We want the buttons for the close action to be flat, for this we first define a new type of action "flat_button" */
	private static final ActionType<ButtonDockAction> FLAT_BUTTON = new ActionType<ButtonDockAction>( "flat button" );
	
	/* And then we define a factory that creates the view for a "flat_button". We can resuse the BasicMiniButton and just
	 * change its border a bit.  */
	private static class FlatButtonGenerator implements ViewGenerator<ButtonDockAction, BasicTitleViewItem<JComponent>>{
		public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ){
			BasicButtonHandler handler = new BasicButtonHandler( action, dockable );
			BasicMiniButton button = new BasicMiniButton( handler, handler );
			button.setNormalBorder( null );
			button.setNormalSelectedBorder( null );
			button.setMouseOverBorder( null );
			button.setMouseOverSelectedBorder( null );
			button.setMousePressedBorder( null );
			button.setMousePressedSelectedBorder( null );
			handler.setModel( button.getModel() );
			return handler;
		}
	}
	
	/* This is now our customized close action.
	 * It has the annotation EclipseTabDockAction which will make it appear on the tab, but it will be hidden
	 * if the tab is not selected. */
	@EclipseTabDockAction( normal=EclipseTabDockActionLocation.HIDDEN )
	private static class EclipseCloseButton extends CCloseAction{
		public EclipseCloseButton( CControl control ){
			super( control );
		}

		@Override
		protected Action createAction(){
			/* This DockAction is responsible for closing a dockable, it is also responsible for the look and feel
			 * of the action. */
			return new Action(){
				@Override
				public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
					/* If this action is to be shown in a title or a tab, then we use the type "flat_button" 
					 * (instead of "button") to create its view. */
					if( target == ViewTarget.TITLE ){
						return converter.createView( FLAT_BUTTON, this, target, dockable );
					}
					else{
						return super.createView( target, converter, dockable );
					}
				}
			};
		}
	}
}
