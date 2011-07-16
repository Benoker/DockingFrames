/**
 * Contains classes to create and manage extensions. An extension is a plug-in
 * that is integrated into the framework and treated as if it were always present.
 * Modules of the framework (or of the plug-in) need to define extension-points at which
 * new code can be introduced. Also modules have to ask the
 * {@link bibliothek.gui.dock.util.extension.ExtensionManager} for all the extensions fitting a particular
 * extension point.
 */
package bibliothek.gui.dock.util.extension;
