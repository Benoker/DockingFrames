/*
 * Bibliothek License
 * ==================
 * 
 * Except where otherwise noted, all of the documentation and software included
 * in the bibliothek package is copyrighted by Benjamin Sigg.
 * 
 * Copyright (C) 2001-2005 Benjamin Sigg. All rights reserved.
 * 
 * This software is provided "as-is," without any express or implied warranty.
 * In no event shall the author be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter and redistribute it,
 * provided that the following conditions are met:
 * 
 * 1. All redistributions of source code files must retain all copyright
 *    notices that are currently in place, and this list of conditions without
 *    modification.
 * 
 * 2. All redistributions in binary form must retain all occurrences of the
 *    above copyright notice and web site addresses that are currently in
 *    place (for example, in the About boxes).
 * 
 * 3. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software to
 *    distribute a product, an acknowledgment in the product documentation
 *    would be appreciated but is not required.
 * 
 * 4. Modified versions in source or binary form must be plainly marked as
 *    such, and must not be misrepresented as being the original software.
 * 
 * 
 * Benjamin sigg
 * benjamin_sigg@gmx.ch
 * 
 */
 
/*
 * Created on 25.08.2004
 */
package bibliothek.util.threads;

/**
 * Dauern Prozesse länger, sollte es möglich sein, sie zu unterbrechen, oder
 * gar ganz abzuschalten.
 * 
 * @author Benjamin Sigg
 * @version 1.0
 */
public interface Pause {
	/**
	 * Diese Methode wird vom Algorithmus oft aufgerufen. Sollte tatsächlich
	 * eine Pause gewünscht sein, kehrt die Methode solange nicht zurück, bis
	 * die Pause beendet wurde.
	 */
	public void pause();
	
	/**
	 * Gibt an, ob der Algorithmus abgebrochen wurde. Der Algorithmus wird diese
	 * Methode in unregelmässigen Abständen aufrufen.
	 * @return true, falls der Algorithmus seine Arbeit einstellen soll. Ob der
	 * Algorithmus seine Arbeit rückgängig macht, hängt von seiner Implementation
	 * ab.
	 */
	public boolean isInterrupted();
	
	/**
	 * Wird vom Algorithmus aufgerufen und gibt an, ob der Algorithmus seine
	 * Arbeit ohne Schäden unterbrechen kann. 
	 * @param interruptable true, falls der Algorithmus seine Arbeit unterbrechen
	 * kann, ohne dass dabei eine Datenstruktur oder ähnliches zerstört wird,
	 * false falls er das nicht kann.
	 */
	public void setInterruptable( boolean interruptable );
}
