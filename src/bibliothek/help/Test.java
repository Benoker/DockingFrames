package bibliothek.help;

import java.io.File;

import javax.swing.JFrame;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.help.control.LinkManager;
import bibliothek.help.gui.SelectingView;
import bibliothek.help.gui.TypeHierarchyView;
import bibliothek.help.model.HelpModel;

public class Test {
    public static void main( String[] args ) throws Exception {
        DockFrontend frontend = new DockFrontend();
        SplitDockStation station = new SplitDockStation();
        frontend.addRoot( station, "root" );
        frontend.getController().setTheme( new NoStackTheme( new FlatTheme() ) );
        
        HelpModel model = new HelpModel( new File( "help/help.data" ) );
        LinkManager views = new LinkManager();
        views.setModel( model );
        
        SelectingView viewPackage = new SelectingView( views, "Packages", "package-list" );
        SelectingView viewClasses = new SelectingView( views, "Classes", "class-list" );
        SelectingView viewFields = new SelectingView( views, "Fields", "field-list" );
        SelectingView viewConstructors = new SelectingView( views, "Constructors", "constructor-list" );
        SelectingView viewMethods = new SelectingView( views, "Methods", "method-list" );
        SelectingView viewContent = new SelectingView( views, "Content", "class",
                "constructor-list", "constructor",
                "field-list", "field",
                "method-list", "method" );
        TypeHierarchyView viewHierarchy = new TypeHierarchyView( views );
        
        views.select( "package-list:root" );
        
        SplitDockGrid grid = new SplitDockGrid(  );
        grid.addDockable( 0, 0, 1, 1, viewPackage );
        grid.addDockable( 0, 1, 1, 2, viewClasses );
        grid.addDockable( 0, 3, 1, 1, viewConstructors );
        grid.addDockable( 1, 3, 1, 1, viewFields );
        grid.addDockable( 2, 3, 1, 1, viewMethods );
        grid.addDockable( 1, 0, 2, 3, viewContent );
        grid.addDockable( 3, 0, 1, 4, viewHierarchy );
        
        station.dropTree( grid.toTree() );
        
        JFrame frame = new JFrame();
        frame.add( station );
        frame.setBounds( 20, 20, 800, 600 );
        frame.setTitle( "Help - Demonstration of DockingFrames" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
