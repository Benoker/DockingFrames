package tutorial.common.basics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonDockAction;
import bibliothek.gui.dock.station.flap.button.ButtonContentAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;
import bibliothek.gui.dock.themes.basic.action.menu.MenuViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

@Tutorial(title="Title with a textfield", id="TitleWithTextField")
public class TitleWithTextFieldExample {
	public static void main( String[] args ){
		/* What if you want to create a CDockable that shows a textfield (or any other Component) in its title? 
		 * The textfield can be implemented as CAction. Implementing the textfield may be a bit tedious, but
		 * using it will be straight forward.  */
		
		/* Before any testing we need a frame and a control */
		JTutorialFrame frame = new JTutorialFrame( TitleWithTextFieldExample.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		frame.add( control.getContentArea(), BorderLayout.CENTER );
		
		/* As we are defining the textfield as "action", we need to inform the framework of how to 
		 * show the action. The action itself will not be a JComponent, rather it is a model encapsulating
		 * the data we need for a textfield. The factories we install here will actually create
		 * a JTextField that shows the text of the action.
		 * 
		 * In this example we will only create a view for a textfield on a title, there will be no
		 * textfield in menus. */
		ActionViewConverter converter = control.getController().getActionViewConverter();
		converter.putDefault( TEXT_FIELD_TYPE, ViewTarget.TITLE, new TextFieldOnTitleGenerator() );
		converter.putDefault( TEXT_FIELD_TYPE, ViewTarget.MENU, new TextFieldInMenuGenerator() );
		converter.putDefault( TEXT_FIELD_TYPE, ViewTarget.DROP_DOWN, new TextFieldInDropDownGenerator() );
		
		/* This is the action representing a textfield. As you see, it is really easy to create... */
		CTextFieldAction sharedAction = new CTextFieldAction();
		
		/* ... and to apply to some Dockables. Note that we forward the same action to both Dockables,
		 * as a result editing the text on one Dockable will change the text on the other as well. */
		DefaultSingleCDockable dockableA = new DefaultSingleCDockable( "a", "Aaaa", sharedAction );
		DefaultSingleCDockable dockableB = new DefaultSingleCDockable( "b", "Bbbb", sharedAction );
		
		/* As usual, we need to place our Dockables and make the entire application visible. */
		CGrid grid = new CGrid( control );
		grid.add( 0, 0, 1, 1, dockableA );
		grid.add( 0, 1, 1, 1, dockableB );
		control.getContentArea().deploy( grid );
		
		frame.setVisible( true );
	}
	
	/* As we are going to introduce a new type of action, we need an identifier for this kind of action.
	 * The identifier is necessary to find appropriate factories when creating the view of the action. */
	public static final ActionType<TextFieldAction> TEXT_FIELD_TYPE = 
			new ActionType<TitleWithTextFieldExample.TextFieldAction>( "text field" );
	
	/* In the Common API an action is always a "CAction". "CAction" is just a wrapper around a DockAction,
	 * and in our case we do not need any complex logic inside of the CAction. We can use this wrapper
	 * to present a nice API to the client. */
	public static class CTextFieldAction extends CAction{
		private TextFieldAction textFieldAction;
		
		public CTextFieldAction(){
			super( null );
			/* CActions delegate all their work to a DockAction. The best place to create and set
			 * this DockAction is in the constructor. */
			textFieldAction = new TextFieldAction( this );
			init( textFieldAction );
		}
		
		public String getText(){
			return textFieldAction.getText();
		}
		
		public void setText( String text ){
			textFieldAction.setText( text );
		}
	}
	
	/* The DockAction "TextFieldAction" is our model of a textfield. The model of a textfield consists
	 * of one String, the "text".
	 * 
	 * Notice the annotation "ButtonContentAction", this will cause a textfield to appear on the button
	 * of a minimized CDockable. In a real application you would probably remove this annotation. */
	@ButtonContentAction
	private static class TextFieldAction implements DockAction, CommonDockAction{
		/* This is the CAction that delegates all its work to this DockAction */
		private CTextFieldAction wrapper;
		/* This is the all important data: the text */
		private String text = "";
		/* And with help of a ChangeListener our view can be informed when the model changes. */
		private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
		
		public TextFieldAction( CTextFieldAction wrapper ){
			this.wrapper = wrapper;
		}
		
		/* There is not much to say about the next few methods: text can be set, and listeners
		 * are called if the text changes. */
		public void addChangeListener( ChangeListener listener ){
			listeners.add( listener );
		}
		
		public void removeChangeListener( ChangeListener listener ){
			listeners.remove( listener );
		}
		
		public String getText(){
			return text;
		}
		
		public void setText( String text ){
			this.text = text;
			ChangeEvent event = new ChangeEvent( this );
			for( ChangeListener listener : listeners ){
				listener.stateChanged( event );
			}
		}
		
		public CAction getAction(){
			return wrapper;
		}
		
		/* Now this method is interesting. The model is responsible for creating its own view. The proper
		 * implementation of this method is just to forward the call to the "ActionViewConverter". Subclasses
		 * may however override this method and tweak the view. */
		public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
			return converter.createView( TEXT_FIELD_TYPE, this, target, dockable );
		}

		/* The methods "bind" and "unbind" inform the action that it is actually shown somewhere.
		 * As this DockAction does not depend on any outside sources, they are not interesting. */
		public void bind( Dockable dockable ){
			// ignore
		}

		public void unbind( Dockable dockable ){
			// ignore
		}

		/* "trigger" is the method usually called when the user clicks a button. For a text field it might be
		 * called if the user hits "enter", but in this example we are going to ignore it. */
		public boolean trigger( Dockable dockable ){
			return false; 
		}
	}
	
	/* We have our model, the TextFieldAction, but now we need a way to actually show the text on screen. The
	 * TextFieldView is a JTextField showing the text of the action. */
	private static class TextFieldView implements BasicTitleViewItem<JComponent>, ChangeListener, DocumentListener, ActionListener{
		/* This is the component we are placing on the title */
		private JTextField textField;
		/* This is the Dockable using this action */
		private Dockable dockable;
		/* And this is the model */
		private TextFieldAction action;
		
		/* Some flags to prevent our methods from running into a StackTraceException */
		private boolean updatingAction = false;
		private boolean updatingTextField = false;
		
		public TextFieldView( Dockable dockable, TextFieldAction action ){
			this.dockable = dockable;
			this.action = action;
		}
		
		/* This method is called if the view is made visible. We create and wire all the
		 * Components we need at this place. */
		public void bind(){
			action.addChangeListener( this );
			textField = new JTextField();
			textField.setColumns( 10 );
			textField.getDocument().addDocumentListener( this );
			textField.setText( action.getText() );
			textField.addActionListener( this );
		}
		
		/* And this method is called once this view is no longer visible, we can clean up
		 * all the listeners. */
		public void unbind(){
			action.removeChangeListener( this );
			textField.removeActionListener( this );
			textField.getDocument().removeDocumentListener( this );
			textField = null;
		}
		
		/* The next few methods deal with changes in the model and on the view:
		 *  - If the user enters some text we forward the new text to the action.
		 *  - If the text of the action changed, we update the text in "textField". */
		public void changedUpdate( DocumentEvent e ){
			if( !updatingTextField ){
				try{
					updatingAction = true;
					action.setText( textField.getText() );
				}
				finally {
					updatingAction = false;
				}
			}
		}
		
		public void insertUpdate( DocumentEvent e ){
			changedUpdate( e );
		}
		
		public void removeUpdate( DocumentEvent e ){
			changedUpdate( e );
		}
		
		public void stateChanged( ChangeEvent e ){
			if( !updatingAction ){
				try{
					updatingTextField = true;
					textField.setText( action.getText() );
				}
				finally{
					updatingTextField = false;
				}
			}
		}
		
		public void actionPerformed( ActionEvent e ){
			action.trigger( dockable );	
		}
		
		/* And finally some methods required by the interface BasicTitleViewItem. */
		public JComponent getItem(){
			return textField;
		}
		
		public DockAction getAction(){
			return action;
		}
		
		public void setOrientation( Orientation orientation ){
			// not supported
		}
		
		public void setForeground( Color foreground ){
			// not supported
		}
		
		public void setBackground( Color background ){
			// not supported
		}
	}

	/* At the very end we can define the factories which will create new views for our new type of action. */
	private static class TextFieldOnTitleGenerator implements ViewGenerator<TextFieldAction, BasicTitleViewItem<JComponent>>{
		public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, TextFieldAction action, Dockable dockable ){
			return new TextFieldView( dockable, action );
		}
	}
	
	/* ... although for menus and drop-down-menus we are not creating views. But the factories are required anyway.  */
	private static class TextFieldInMenuGenerator implements ViewGenerator<TextFieldAction, MenuViewItem<JComponent>>{
		public MenuViewItem<JComponent> create( ActionViewConverter converter, TextFieldAction action, Dockable dockable ){
			return null;
		}
	}
	
	private static class TextFieldInDropDownGenerator implements ViewGenerator<TextFieldAction, DropDownViewItem>{
		public DropDownViewItem create( ActionViewConverter converter, TextFieldAction action, Dockable dockable ){
			return null;
		}
	}
}
