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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.dock.util.text.DialogText;
import bibliothek.gui.dock.util.text.SwingActionText;
import bibliothek.gui.dock.util.text.TextValue;

/**
 * An abstract dialog used to show the content of some {@link PreferenceModel}. The
 * exact graphical user interface for the model depends on the subclass.<br>
 * <b>Note: </b> clients using this panel have to call {@link #destroy()}. The only time {@link #destroy()} has
 * not to be called is if the dialog was shown using {@link #openDialog(Component, boolean)} and {@link #isDestroyOnClose() destroyOnClose}
 * was set to <code>true</code>.
 * @author Benjamin Sigg
 *
 * @param <M> What kind of model this dialog can show
 */
public abstract class AbstractPreferenceDialog<M extends PreferenceModel> extends JPanel{
    private M model;
    private JComponent content;
    private JDialog dialog;
    private boolean destroyOnClose;
    
    /** various texts that are used by this dialog */
    private List<TextValue> texts = new ArrayList<TextValue>();
    
    /**
     * Creates a new dialog.
     * @param destroyOnClose if set to <code>true</code>, then {@link #destroy()} is automatically called
     * if {@link #close()} is called. Clients have to call {@link #destroy()} manually if they are not
     * using {@link #openDialog(Component, boolean)}.
     */
    public AbstractPreferenceDialog( boolean destroyOnClose ){
        this( null, destroyOnClose );
    }
    
    /**
     * Creates a new dialog using the given model.
     * @param model the model to use
     * @param destroyOnClose if set to <code>true</code>, then {@link #destroy()} is automatically called
     * if {@link #close()} is called. Clients have to call {@link #destroy()} manually if they are not
     * using {@link #openDialog(Component, boolean)}.
     */
    public AbstractPreferenceDialog( M model, boolean destroyOnClose ){
        init( model, destroyOnClose );
    }
    
    /**
     * A constructor which does not initialize this dialog. Subclasses must
     * call {@link #init(PreferenceModel, boolean)} to finish constructing this dialog.
     * @param init whether to call {@link #init(PreferenceModel, boolean)}.
     * @param model the model to use, can be <code>null</code>
     * @param destroyOnClose if set to <code>true</code>, then {@link #destroy()} is automatically called
     * if {@link #close()} is called. Clients have to call {@link #destroy()} manually if they are not
     * using {@link #openDialog(Component, boolean)}.
     */
    protected AbstractPreferenceDialog( boolean init, M model, boolean destroyOnClose ){
    	if( init ){
    		init( model, destroyOnClose );
    	}
    }
    
    /**
     * Creates the contents of this dialog.
     * @param model the model to use, can be <code>null</code>
     * @param destroyOnClose if set to <code>true</code>, then {@link #destroy()} is automatically called
     * if {@link #close()} is called. Clients have to call {@link #destroy()} manually if they are not
     * using {@link #openDialog(Component, boolean)}.
     */
    protected void init( M model, boolean destroyOnClose ){
    	if( content != null )
    		throw new IllegalStateException( "Already initialized" );
    	
    	setLayout( new GridBagLayout() );
    	this.model = model;
    	this.destroyOnClose = destroyOnClose;
        
        content = getContent();
        
        JPanel buttons = new JPanel( new GridLayout( 1, 4 ));
        buttons.add( new JButton( new ApplyAction() ));
        buttons.add( new JButton( new ResetAction() ));
        buttons.add( new JButton( new OkAction() ));
        buttons.add( new JButton( new CancelAction() ));
        
        add( content, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1000.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets( 1, 1, 1, 1 ), 0, 0 ));
        
        add( buttons, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE,
                new Insets( 1, 1, 1, 1 ), 0, 0 ) );
        
        setModel( model );
    }
    
    /**
     * Gets the component which will show the {@link #getModel() model} 
     * of this dialog.
     * @return the component
     */
    protected abstract JComponent getContent();
    
    /**
     * Informs subclasses that the model has changed and that they might 
     * setup the {@link #getContent() content} again.
     * @param model the new model, can be <code>null</code>
     */
    protected abstract void setModelForContent( M model );

    
    /**
     * Sets the model of this dialog.
     * @param model the new model
     */
    public void setModel( M model ){
        this.model = model;
        
        if( model == null ){
        	for( TextValue text : texts ){
        		text.setManager( null );
        	}
        }
        else{
        	for( TextValue text : texts ){
        		text.setManager( model.getController().getTexts() );
        	}
        }
        
        setModelForContent( model );
    }
    
    /**
     * Gets the model which is shown on this dialog.
     * @return the model
     */
    public M getModel(){
        return model;
    }
    
    /**
     * Opens the dialog (if not yet open) and lets the user make the changes
     * of the preferences. This method will call {@link PreferenceModel#read()} and
     * {@link PreferenceModel#write()} to reset or to apply the changes of the user.
     * @param owner the owner of the dialog
     * @param modal whether the dialog should be modal
     */
    public void openDialog( Component owner, boolean modal ){
        if( dialog != null )
            return;
        
        dialog = createDialog( owner );
        dialog.setModal( modal );
        dialog.add( this );
        
        doReset();
        
        dialog.pack();
        dialog.setSize( (int)(dialog.getWidth() * 1.5), dialog.getHeight() );
        dialog.setLocationRelativeTo( owner );
        dialog.setVisible( true );
    }
    
    
    private JDialog createDialog( Component owner ){
        JDialog dialog;
        if( owner == null ){
            dialog = new JDialog();
        }
        else{
            Window window = SwingUtilities.getWindowAncestor( owner );
            if( window instanceof Frame ){
                dialog = new JDialog( (Frame)window );
            }
            else if( window instanceof Dialog ){
                dialog = new JDialog( (Dialog)window );
            }
            else{
                dialog = new JDialog();
            }
        }
        
        final DialogText title = new DialogText( "preference.dialog.title", dialog ){
			protected void changed( String oldValue, String newValue ){
				getDialog().setTitle( newValue );
			}
		};
        
        dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        dialog.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing( WindowEvent e ) {
                doCancel();
            }
            @Override
            public void windowClosed( WindowEvent e ){
            	texts.remove( title );
            	title.setController( null );
            }
            @Override
            public void windowOpened( WindowEvent e ){
            	texts.add( title );
            	if( model != null ){
            		title.setController( model.getController() );
            	}
            }
        });
        return dialog;
    }
    
    /**
     * Applies all changes and closes the dialog.
     */
    public void doOk(){
        doApply();
        close();
    }
    
    /**
     * Applies all changes but does not close the dialog.
     */
    public void doApply(){
        getModel().write();
    }
    
    /**
     * Closes the dialog without saving and changes
     */
    public void doCancel(){
        doReset();
        close();
    }
    
    /**
     * Resets all preferences to the value they had when the dialog opened
     */
    public void doReset(){
        getModel().read();
    }
    
    /**
     * Makes the dialog invisible.
     */
    public void close(){
    	if( dialog != null ){
            dialog.dispose();
            dialog.remove( this );
            dialog = null;
        }
    	if( destroyOnClose ){
    		destroy();
    	}
    }
    
    /**
     * Allows this dialog to free any resources that it used. Should be called once this dialog is no longer
     * used, otherwise memory leaks can appear.
     */
    public void destroy(){
    	setModel( null );
    }
    
    /**
     * Tells whether {@link #destroy()} is called automatically or not.
     * @return whether to free resources
     * @see #setDestroyOnClose(boolean)
     */
    public boolean isDestroyOnClose(){
		return destroyOnClose;
	}
    
    /**
     * If set to <code>true</code> then {@link #destroy()} is automatically called if this dialog is
     * {@link #close() closed}
     * @param destroyOnClose whether to free resources
     */
    public void setDestroyOnClose( boolean destroyOnClose ){
		this.destroyOnClose = destroyOnClose;
	}
    
    private class OkAction extends AbstractAction{
        public OkAction(){
        	texts.add( new SwingActionText( "preference.dialog.ok.text", NAME, this ) );
        	texts.add( new SwingActionText( "preference.dialog.ok.description", SHORT_DESCRIPTION, this ) );
        }
        
        public void actionPerformed( ActionEvent e ) {
            doOk();
        }
    }
    
    private class ApplyAction extends AbstractAction{
        public ApplyAction(){
        	texts.add( new SwingActionText( "preference.dialog.apply.text", NAME, this ) );
        	texts.add( new SwingActionText( "preference.dialog.apply.description", SHORT_DESCRIPTION, this ) );
        }
        
        public void actionPerformed( ActionEvent e ) {
            doApply();
        }
    }
    
    private class CancelAction extends AbstractAction{
        public CancelAction(){
        	texts.add( new SwingActionText( "preference.dialog.cancel.text", NAME, this ) );
        	texts.add( new SwingActionText( "preference.dialog.cancel.description", SHORT_DESCRIPTION, this ) );
        }
        
        public void actionPerformed( ActionEvent e ) {
            doCancel();
        }
    }
    
    private class ResetAction extends AbstractAction{
        public ResetAction(){
        	texts.add( new SwingActionText( "preference.dialog.reset.text", NAME, this ) );
        	texts.add( new SwingActionText( "preference.dialog.reset.description", SHORT_DESCRIPTION, this ) );
        }
        
        public void actionPerformed( ActionEvent e ) {
            doReset();
        }
    }
}
