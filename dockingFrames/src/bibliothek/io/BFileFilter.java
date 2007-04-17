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
 
package bibliothek.io;

import java.io.File;

/**
 * BFileFilter ist ein FileFilter, wie er im JFileChooser benötigt wird.
 */

public class BFileFilter extends javax.swing.filechooser.FileFilter
    implements java.io.FileFilter{

  private String name = "unbekannt";
  private String ex[] = new String[0];

  public BFileFilter() {
  }

  public BFileFilter ( String name ){
    setName ( name );
  }

  /**
   * Konstruktor mit name und Endung.
   * @param name Der Name der Dateisorte.
   * @param extencion Die Endung der Dateisorte (Bsp: "txt").
   */
  public BFileFilter(String name, String extencion) {
    setName ( name );
    addExtencion ( extencion );
  }
  /**
   * Setzt die Beschreibung des Files. (z.B. Bitmap-Datei)
   */
  public void setName ( String name ){
    this.name = name;
  }
  /**
   * Fügt ein mögliches Ende hinzu. (z.B. txt )
   */
  public void addExtencion ( String ext ){
    if ( ext == null ) return;

    String temp[] = ex;
    ex = new String [ex.length+1];
    System.arraycopy(temp, 0, ex, 0, temp.length);
    ex [ temp.length ] = ext.toLowerCase();
  }
  /**
   * Liefert die Endung nummer i.
   */
  public String getExtencion ( int i ){
    if ( i < 0 ) return null;
    if ( i > ex.length ) return null;
    return ex [i];
  }
  /**
   * Liefert den String, der angezeigt wird.
   */
  @Override
  public String getDescription(){
    if ( ex.length == 0 ){
      return name + " ( *.* )";
    }

    String ext = " (";
    for ( int i = 0; i < ex.length; i++){
      ext += "*." + ex[i];
      if ( i + 1 < ex.length )
        ext += ", ";
    }
    ext += " )";
    return name + ext;
  }
  /**
   * Überprüft, ob dieses File diesem FileFilter entspricht.
   * Ordner werden immer als true zurückgegeben.
   * Wurde keine Endung gesetzt, wird true zurückgegeben.
   * Ist file gleich null, wird false zurückgegeben.
   * Eine Überprüfung auf mehrere Endungen ist möglich.
   */
  @Override
  public boolean accept(File file){
    if ( file == null ) return false;
    if ( file.isDirectory() ) return true;

    if ( !file.isFile() ) return false;

    if ( ex.length == 0 ) return true;

    String name = file.getName();

    for ( int i = 0; i < ex.length; i++){
      if ( name.toLowerCase().endsWith( ex[i] )){
        if ( name.length() + 1 > ex[i].length() ){
          if ( name.substring( name.length() - ex[i].length() - 1 ).startsWith("."))
            return true;
        }
      }
    }
    return false;
  }
}