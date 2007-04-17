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
 
package bibliothek.util;

import java.util.Enumeration;

/**
 * Funktioniert wie jede Enumeration, die Elemente werden aber in
 * zufälliger Reihenfolge ausgegeben.
 * @author Benjamin Sigg
 * @version 1.0
 */

public class RandomEnumeration<E> implements Enumeration<E>{

  private int count = 0;
  private Element firstElement;

  public RandomEnumeration ( E [] values ){
    this ( values, 0, values.length );
  }

  public RandomEnumeration ( E [] values, int size ){
    this ( values, 0, size );
  }

  /**
   * Übernimmt einen Teil der Elemente des Arrays.
   * @param values Der Array mit den Elementen.
   * @param begin Der Index des ersten Elements.
   * @param size Die Anzahl Elemente die übernommen werden sollen.
   */
  public RandomEnumeration (final E [] values, final int begin, final int size ){
    this ( new Enumeration<E> (){
      int count = begin;

      public boolean hasMoreElements (){
        return count < begin + size;
      }

      public E nextElement (){
        return values [ count++ ];
      }
    });
  }

  /**
   * Übernimmt alle Elemente dieser Enumeration.
   * @param source Die Startwerte.
   */
  public RandomEnumeration ( Enumeration<E> source ){
    if ( source.hasMoreElements() ){
      firstElement = new Element ( source.nextElement() );
      count = 1;
    }

    Element element = firstElement;

    while ( source.hasMoreElements() ){
      element = new Element ( element, source.nextElement() );
      count++;
    }

  }

  public synchronized boolean hasMoreElements (){
    return count > 0;
  }

  public synchronized E nextElement (){
    if ( ! hasMoreElements() ) return null;

    int index = (int)(Math.random() * count);
    count--;

    if ( index == 0 ){
      if ( count == 0 )
        return firstElement.delete();

      firstElement = firstElement.next;
      return firstElement.previous.delete();
    }
    else
      return getElement(index).delete();

  }
  
  /**
   * Gibt die Anzahl noch verbleibender Elemente zurück.
   * @return Anzahl noch verbleibender Elemente.
   */
  public int getCount(){
  	return count;
  }
  
  /**
   * Liefert das Element an der Stelle index;
   * @param index Der Index des Elements, zwischen 0 und {@link #getCount() getCount}.
   * @return Das Element.
   */
  protected Element getElement ( int index ){
    Element element = firstElement;

    for ( int i = index; i > 0; i--){
      element = element.next;
    }

    return element;
  }

  /**
   * Ein Element hält ein einziges Object. Elemente sind so aufgebaut, dass
   * sie sehr schnell gelöscht werden können.
   * @author Benjamin Sigg
   * @version 1.0
   */
  private class Element {
    private Element next, previous;
    private E object;

    public Element ( E object ){
      this.object = object;
    }

    public Element ( Element previous, E object ){
      this.object = object;
      this.previous = previous;
      previous.next = this;
    }

    public E delete (){
      E temp = object;
      object = null;

      if ( next != null )
        next.previous = previous;

      if ( previous != null )
        previous.next = next;

      return temp;
    }
  }

}