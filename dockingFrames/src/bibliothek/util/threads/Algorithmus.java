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
 * Created on 26.08.2004
 */
package bibliothek.util.threads;


/**
 * Manche Algorithmen benötigen eine längere Zeit um ausgeführt zu werden.
 * Dieses Interfaces wird von allen Algorithmen der Bibliothek2 implementiert, 
 * so ist es möglich z.B. eine graphische Overfläche aufzubauen, welche Informationen
 * über den Zustand eines Algorithmuses ausgibt. Der Benutzer könnte so z.B.
 * selbst sehen, dass der Algorithmus nicht in einer Endlosschlaufe hängt.
 * 
 * @author Benjamin Sigg
 * @version 1.0
 */
public interface Algorithmus {
	/**
	 * Startet den Algorithmus mit der übergebenen Struktur
	 * @param pause Das Objekt, das über Pausen und/oder abbruch informiert.
	 * @throws IllegalStateException Sollte ein Thread diese Methode aufrufen,
	 * obwohl ein anderer bereits diese Methode aufgerufen hat. Oder falls
	 * dem Algorithmus noch nicht alle benötigten Parameter gesetzt wurden.
	 */
	public void work( Pause pause );
	
	/**
	 * Gibt eine Liste aller möglichen Informationen zurück, welche dieser
	 * Algorithmus liefert.
	 * @return Die verschiedenen Schlüssel zu den Informationen.
	 * @see #getInformation(String)
	 */
	public String[] getInformationKeys();
	
	/**
	 * Gibt eine Information zurück, welche einen aktuellen Aspekt des 
	 * Algorithmuses beschreibt.
	 * @param key Der Schlüssel zu einer Information.
	 * @return Die Information
	 */
	public String getInformation( String key );
	
	/**
	 * Gibt die Anzahl Fortschritte an, für welche dieser Algorithmus Informationen
	 * bereitsstellt.
	 * @return Die Anzahl ProgressBaren die diesen Algorithmus beschreiben
	 * würden, auch 0 ist erlaubt.
	 */
	public int getProgressCount();
	
	/**
	 * Den Minimalwert für die indexte Progressbar.
	 * @param index Der Index
	 * @return Der Minimalewert
	 */
	public int getMinimum( int index );
	
	/**
	 * Der Maximalwert für die indexte Progressbar.
	 * @param index Der Index
	 * @return Die Maximalwert
	 */
	public int getMaximum( int index );
	
	/**
	 * Gibt den Fortschritt der indexten Progressbar an.
	 * @param index Der Index
	 * @return Der aktuelle Wert
	 */
	public int getValue( int index );
}
