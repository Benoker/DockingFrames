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

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockUI;

/**
 * An abstract dialog used to show the content of some {@link PreferenceModel}. The
 * exact graphical user interface for the model depends on the subclass.
 * @author Benjamin Sigg
 *
 * @param <M> What kind of model this dialog can show
 */
public abstract class AbstractPreferenceDialog<M extends PreferenceModel> extends JPanel{
    private M model;
    private JComponent content;
    private JDialog dialog;
    
    /**
     * Creates a new dialog.
     */
    public AbstractPreferenceDialog(){
        this( null );
    }
    
    /**
     * Creates a new dialog using the given model.
     * @param model the model to use
     */
    public AbstractPreferenceDialog( M model ){
        init( model );
    }
    
    /**
     * A constructor which does not initialize this dialog. Subclasses must
     * call {@link #init(PreferenceModel)} to finish constructing this dialog.
     * @param init whether to call {@link #init(PreferenceModel)}.
     * @param model the model to use, can be <code>null</code>
     */
    protected AbstractPreferenceDialog( boolean init, M model ){
    	if( init ){
    		init( null );
    	}
    }
    
    /**
     * Creates the contents of this dialog.
     * @param model the model to use, can be <code>null</code>
     */
    protected void init( M model ){
    	if( content != null )
    		throw new IllegalStateException( "Already initialized" );
    	
    	setLayout( new GridBagLayout() );
    	this.model = model;
        
        content = getContent();
        setModelForContent( model );
        
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
        
        dialog.setTitle( DockUI.getDefaultDockUI().getString( "preference.dialog.title" ) );
        dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        dialog.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing( WindowEvent e ) {
                doCancel();
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
    }
    
    private class OkAction extends AbstractAction{
        public OkAction(){
            putValue( NAME, DockUI.getDefaultDockUI().getString( "preference.dialog.ok.text" ) );
            putValue( SHORT_DESCRIPTION, DockUI.getDefaultDockUI().getString( "preference.dialog.ok.description" ) );
        }
        
        public void actionPerformed( ActionEvent e ) {
            doOk();
        }
    }
    
    private class ApplyAction extends AbstractAction{
        public ApplyAction(){
            putValue( NAME, DockUI.getDefaultDockUI().getString( "preference.dialog.apply.text" ) );
            putValue( SHORT_DESCRIPTION, DockUI.getDefaultDockUI().getString( "preference.dialog.apply.description" ) );
        }
        
        public void actionPerformed( ActionEvent e ) {
            doApply();
        }
    }
    
    private class CancelAction extends AbstractAction{
        public CancelAction(){
            putValue( NAME, DockUI.getDefaultDockUI().getString( "preference.dialog.cancel.text" ) );
            putValue( SHORT_DESCRIPTION, DockUI.getDefaultDockUI().getString( "preference.dialog.cancel.description" ) );
        }
        
        public void actionPerformed( ActionEvent e ) {
            doCancel();
        }
    }
    
    private class ResetAction extends AbstractAction{
        public ResetAction(){
            putValue( NAME, DockUI.getDefaultDockUI().getString( "preference.dialog.reset.text" ) );
            putValue( SHORT_DESCRIPTION, DockUI.getDefaultDockUI().getString( "preference.dialog.reset.description" ) );
        }
        
        public void actionPerformed( ActionEvent e ) {
            doReset();
        }
    }
}
