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
 * Created on 09.05.2004
 */
package bibliothek.util;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Ein AVL-Baum. Der Baum kann eine beliebige Anzahl Schlüssel/Werte-Paare
 * aufnehmen. Die Paare werden nach dem Schlüssel sortiert, und zwar so, dass
 * die maximale Suchzeit für ein Schlüssel O( log n ) beträgt (n ist die Anzahl
 * Paare des Baumes).<br>
 * 
 * Weitere Informationen: Algorithmen und Datenstrukturen, 
 * T.Ottmann / P.Widmayer, 4. Auflage, Kapitel 5.2.1
 * 
 * @author Benjamin Sigg
 * @version 1.0
 */
public class AVLTree<K, V> implements Cloneable, Iterable<V> {
    /** Das Root, der einzige Node der keine Parent hat */
    private Node root;
    
    /** Der Comparator der die Schlüssel in die richtige Reihenfolge bringt */
    private Comparator<? super K> comparator;
    
    /** Die Anzahl Elemente im Tree */
    private int size = 0;
    
    /**
     * Standartkonstruktor. Die Schlüssel müssen das Interface
     * {@link Comparable Comparable} implementieren.
     */
    public AVLTree(){
        this.comparator = new DefaultComparator();
    }
    
    /**
     * Konstruktor. Die Schlüssel werden mit dem Comparator verglichen.<br>
     * Der Comparator darf nicht verändert werden, gilt <code>x > y </code> zum
     * Zeitpunkt t, dann gilt <code>x > y</code> auch zum Zeitpunkt t +- delta t. 
     * @param comparator Der unveränderliche Comparator
     * @throws NullPointerException Sollte der Comparator null sein
     */
    public AVLTree( Comparator<? super K> comparator ){
        if( comparator == null )
            throw new NullPointerException( "The comparator must not be null" );
        
        this.comparator = comparator;
    }
    
    /**
     * Kopierkonstruktor.
     * @param original Der Baum, von dem alle Elemente kopiert werden
     */
    public AVLTree( AVLTree<K, V> original ){
        this.comparator = original.getComparator();
        
        int size = original.size();
        if( size != 0 ){
            this.root = (Node)original.getRoot().clone();
            this.size = size;
        }
    }
    
    /**
     * Übernimmt alle Schlüssel/Werte-Paare des Originalbaumes und sortiert
     * sie mit Hilfe des Comparators neu.
     * @param original Die Elemente
     * @param comparator Der neue Comparator
     */
    public AVLTree( AVLTree<? extends K, ? extends V> original, Comparator<? super K> comparator ){
        this( comparator );
        Enumeration nodes = original.nodes();
        while( nodes.hasMoreElements() ){
            Node node = (Node)nodes.nextElement();
            put( node.getKey(), node.getItem() );
        }
    }
    
    /**
     * Übernimmt alle Schlüssel/Werte-Paare der Map, und sortiert
     * sie mit dem Comparator neu.
     * @param original Die Map mit allen Paaren
     * @param comparator Der Comparator
     */
    public AVLTree( Map<? extends K, ? extends V> original, Comparator<? super K> comparator ){
        this( comparator );
        
        for( Map.Entry<? extends K, ? extends V> entry : original.entrySet() ){
            put( entry.getKey(), entry.getValue() );
        }
    }
    
    //************************************************//
    //                                                //
    //            Methoden aus Object                 //
    //                                                //
    //************************************************//
    
    /**
     * Gibt eine einfache Stringrepresentation des Trees zurück.
     * @return Stringrepresentation
     */
    @Override
    public String toString(){
       StringBuffer buffer = new StringBuffer();
       
       buffer.append( getClass().getName() );
       buffer.append( "[ size=" );
       buffer.append( size );
       buffer.append( " ]" );
       return buffer.toString();
    }
    
    /**
     * Vergleicht diesen Tree mit einem Object.<br>
     * Sollte <code>other</code> ebenfalls ein AVLTree oder ein 
     * {@link java.util.Map Map} sein, werden alle
     * Schlüssel verglichen (Zeit: O( n log n ) ). Dabei werde die
     * Suchmethoden dieses Trees verwendet.<br><br>
     * In jedem anderen Fall wird false zurückgegeben.
     * @return true falls der andere Tree / Set dieselben Schlüssel-Werte-Paare
     * besitzt.
     */
    @Override
    public boolean equals( Object other ){
        if( other == this )
            return true;
        
        if( other instanceof AVLTree ){
            AVLTree tree = (AVLTree)other;
            
            if( tree.size() != size )
                return false;
            
            
            // Die Enumeration geben die Nodes sortiert nach ihrer
            // Schlüsselwerten zurück, die tatsächliche Anordnung der Nodes
            // wird nicht weiter beachtet
            Enumeration otherNodes = tree.nodes();
            Enumeration myNodes = nodes();
            
            while( myNodes.hasMoreElements() ){
                Node otherNode = (Node)otherNodes.nextElement();
                Node myNode = (Node)myNodes.nextElement();
                
                if( ! myNode.getKey().equals( otherNode.getKey() ) )
                    return false;
                
                Object otherItem = otherNode.item;
                Object myItem = myNode.item;
                
                if( myItem != null && otherItem != null ){
                    if( !myItem.equals( otherItem ) )
                        return false;
                }
                else if( myItem == null && otherItem != null )
                    return false;
                else if( myItem != null && otherItem == null )
                    return false;
            }
            
            return true;
        }
        else if( other instanceof Map ){
            Map map = (Map)other;
            
            if( size != map.size() )
                return false;
            
            Enumeration nodes = nodes();
            
            while( nodes.hasMoreElements() ){
                Node node = (Node)nodes.nextElement();
                
                Object found = map.get( node.getKey() );
                Object item = node.getItem();
                
                if( found != null && item != null ){
                    if( !found.equals( item ))
                        return false;
                }
                else if( found == null && item != null )
                    return false;
                else if( found != null && item == null )
                    return false;
            }
            
            return true;
        }
        else
            return false;
    }
    
    /**
     * Kreiert eine Kopie des Trees.<br>
     * Die Schlüssel und die Werte werden nicht kopiert, aber der neue Tree
     * ist unabhängig von diesem.
     */
    @Override
    public AVLTree<K,V> clone(){
        return new AVLTree<K,V>( this );
    }
    
    //************************************************//
    //                                                //
    //              Abfrage-Methoden                  //
    //                                                //
    //************************************************//
    
    /**
     * Gibt den Comparator zurück, den dieser Tree benutzt.<br>
     * Es gibt <b>keine</b> Methode <code>setComparator</code>, da 
     * der Comparator nicht mehr verändert werden darf.
     * @return Den Comparator
     */
    public Comparator<? super K> getComparator(){
        return comparator;
    }
    
    /**
     * Gibt das Item zurück, welches mit diesem Schlüssel in den Tree gelegt wurde.
     * @param key Der Schlüssel
     * @return Das Item oder null
     */
    public V get( K key ){
        Node node = getNode( key );
        if( node != null )
            return node.getItem();
        else
            return null;
    }
    
    /**
     * Gibt die Anzahl Elemente dieses Baumes zurück.
     * @return Die Anzahl Elemente
     */
    public int size(){
        return size;
    }
    
    /**
     * Gibt an, ob dieser Tree leer ist.<br>
     * Ist identisch mit einem Aufruf von <code>size() == 0</code>
     * @return true, falls der Tree leer ist
     */
    public boolean isEmpty(){
        return size == 0;
    }
    
    /**
     * Gibt an, ob dieser Tree diesen Schlüssel enthält.
     * @param key Der Schlüssel
     * @return true, falls der Schlüssel gefunden wurde.
     * @throws NullPointerException Sollte der Schlüssel null sein
     * @throws ClassCastException Sollte der Comparator den Key nicht
     * verarbeiten können.
     */
    public boolean containsKey( K key ){
       if( root != null )
           return root.containsKey( key );
           
       return false;
    }
    
    /**
     * Gibt an, ob ein Element gefunden werden kann, welches diesem Object
     * ähnlich ist.<br>
     * Genau gesagt wird die Überprüfung 
     * <code>value == null ? (item == null) : (item.equals( value ))</code>
     * vorgenommen.
     */
    public boolean containsValue( V value ){
        if( root != null ){
            return root.containsValue( value );
        }
        return false;
    }
    
    /**
     * Sammelt die Schlüssel in einem Array.
     * @return Der Array mit den Schlüsseln
     */
    public Object[] keysToArray(){
        Object[] keys = new Object[ size ];
        if( root != null )
        	root.keysToArray( keys, 0 );
        return keys;
    }
    
    /**
     * Sammelt die Schlüssel in einem Array. Die Schlüssel sind dabei
     * automatisch nach ihrer Grösse sortiert.
     * @param keys Der Array in den die Schlüssel geschrieben werden sollen
     * @return Den Array, sollte der Array zu klein sein, wird ein neuer
     * Array der benötigten Grösse initialisiert.
     */
    @SuppressWarnings( "unchecked" )
    public K[] keysToArray( K[] keys ){
        if( keys.length < size )
            keys = (K[])Array.newInstance(
                    keys.getClass().getComponentType(), size );
        
        if( root != null )
            root.keysToArray( keys, 0 );
        
        return keys;
    }
    
    /**
     * Sammelt die Elemente in einem Array.
     * @return Der Array mit den Schlüsseln
     */
    public Object[] itemsToArray(){
        Object[] items = new Object[ size ];
        if( root != null )
        	root.itemsToArray( items, 0 );
        
        return items;
    }
    
    /**
     * Sammelt die Elemente in einem Array. Die Elemente sind nach ihren
     * Schlüsseln sortiert.
     * @return Den Array, sollte der Array zu klein sein, wird ein neuer
     * Array der benötigten Grösse initialisiert.
     */
    @SuppressWarnings( "unchecked" )
    public V[] itemsToArray( V[] items ){
        if( items.length < size )
        	items = (V[])Array.newInstance(
        			items.getClass().getComponentType(), size );
        
        if( root != null )
            root.itemsToArray( items, 0 );
        
        return items;
    }
    
    /**
     * Kreiert einen Array mit Schlüssel/Werte-Paaren.
     * @return Der 2-dimensionale Array
     */
    public Object[][] toArray(){
        return toArray( new Object[size][2] );
    }
    
    /**
     * Schreibt alle Schlüssel/Werte-Paare in den Array.
     * @param array Der zu füllende Array <code>[ size() ][ 2 ]</code> 
     * @return Der Array oder ein grösserer Array
     * @throws ArrayIndexOutOfBoundsException Sollte einer der 1-d Array
     * eine Länge kleiner als 2 haben
     */
    public Object[][] toArray( Object[][] array ){
        if( array.length < size )
            array = (Object[][])Array.newInstance(
                    array.getClass().getComponentType(), size );
        
        if( root != null )
            root.toArray( array, 0 );
            
        return array;
    }
    
    /**
     * Liefert eine Enumeration aller Schlüssel. Die Schlüssel sind nach ihrer
     * Grösse sortiert.<br>
     * Wird der Baum verändert, ist das Verhalten der Enumeration nicht
     * definiert.
     * @return Alle Keys dieses Baumes.
     */
    public Enumeration<K> keys(){
       final Enumeration<Node> nodes = nodes();
       return new Enumeration<K>(){
           public boolean hasMoreElements() {
               return nodes.hasMoreElements();
           }
           public K nextElement() {
               return nodes.nextElement().key;
           }
       };
    }
    
    /**
     * Liefert eine Enumeration aller Elemente. Die Elemente sind nach ihrer
     * Grösse sortiert.<br>
     * Wird der Baum verändert, ist das Verhalten der Enumeration nicht
     * definiert.
     * @return Alle Elemente dieses Baumes.
     */
    public Enumeration<V> items(){
       final Enumeration<Node> nodes = nodes();
       return new Enumeration<V>(){
           public boolean hasMoreElements() {
               return nodes.hasMoreElements();
           }
           public V nextElement() {
               return nodes.nextElement().item;
           }
       };
    }
    
    /**
     * Gibt einen Iterator über alle Werte zurück.
     * @return Der Iterator
     */
	public Iterator<V> iterator() {
		final Enumeration<Node> nodes = nodes();
		return new Iterator<V>(){
			public boolean hasNext() {
				return nodes.hasMoreElements();
			}
			public V next() {
				return nodes.nextElement().item;
			}
			public void remove() {
				throw new UnsupportedOperationException( "This operation may be implemented in a future release" );
			}
		};
	}
    
    /**
     * Gibt alle Nodes in einer Reihenfolge zurück, die der Sortierung ihrer
     * Schlüssel entspricht.
     * @return Alle Nodes
     */
    protected Enumeration<Node> nodes(){
        return new Enumeration<Node>(){
            private Node node = getFirstNode();
            
            /** 
             * Gibt an, ob der Node bereits benutzt wurde. Falls ja wird
             * er übersprungen, und sein Parent zurückgegeben. 
             */
            private boolean goUp = node.right == null;
            
            public boolean hasMoreElements() {
                return node != null;
            }
            public Node nextElement() {
                Node old = node;
                
                // der aktuelle Node wurde benutzt, jetzt wird der nächste Node
                // berechnet, der zurückgegeben werden soll
                
                // comingUp: kommt von links hoch
                
                if( goUp ){
                    // war bereits beim rechten Child
                    if( node.parent == null )
                        node = null;
                    else{
                        goUp = node.parent.right == node;
                        node = node.parent;
                    }
                }
                else{
                    if( node.right == null ){
                        // steigen bis zu einem Knoten, der noch nicht benutzt wurde
                        do{
                            if( node.parent == null )
                                node = null;
                            else{
                                goUp = node.parent.right == node;
                                node = node.parent;
                            }
                        }
                        while( goUp && node != null );
                    }
                    else{
                        // Symmetrischen Nachfolger wählen
                        node = node.right;
                        
                        while( node.left != null )
                            node = node.left;
                        
                        goUp = false;
                    }
                }
                return old;
            }
        };
    }
    
    
    /**
     * Sucht den kleinsten Key dieses Trees.
     * @return Der kleinste Key oder null
     */
    public K getFirstKey(){
        Node node = getFirstNode();
        if( node == null )
            return null;
        else
            return node.key;
    }
    
    /**
     * Sucht den grössten Key dieses Trees.
     * @return Der grösste Key oder null
     */
    public K getLastKey(){
        Node node = getLastNode();
        if( node == null )
            return null;
        else
            return node.key;
    }
    
    /**
     * Sucht den Node mit dem kleinsten Key.
     * @return Der Node mit dem kleinsten Key oder null
     */
    protected Node getFirstNode(){
        if( root != null ){
            Node node = root;
            while( node.left != null )
                node = node.left;
            
            return node;
        }
        else
            return null;
    }
    
    /**
     * Sucht den Node mit dem grössten Key.
     * @return Der Node mit dem grössten Key oder null
     */
    protected Node getLastNode(){
        if( root != null ){
            Node node = root;
            while( node.right != null )
                node = node.right;
            
            return node;
        }
        else
            return null;
    }
    
    /**
     * Gibt das Root dieses Trees zurück.
     * @return Das Root
     */
    protected Node getRoot(){
        return root;
    }
    
    /**
     * Sucht den Node mit diesem Key.
     * @param key Der Schlüssel
     * @return Der Node oder null
     */
    protected Node getNode( K key ){
        if( root == null )
            return null;
        else
            return root.getNode( key );
    }
    
    //************************************************//
    //                                                //
    //          Modifizierende Methoden               //
    //                                                //
    //************************************************//
    
    /**
     * Entfernt alle Elemente dieses Trees.
     */
    public void clear(){
        root = null;
        size = 0;
    }
    
    /**
     * Setzt ein neues Element. Sollte bereits ein Element mit diesem Schlüssel
     * gesetzt sein, so wird das alte Element ersetzt.
     * @param key Der Schlüssel, nicht null
     * @param item Das Item, null ist erlaubt
     * @return Das Item das zuvor mit diesem Schlüssel gesetzt war, oder null
     * @throws NullPointerException Sollte der Schlüssel null sein
     */
    public V put( K key, V item ){
        if( key == null )
            throw new NullPointerException( "The key must not be null" );
        
        if( root == null ){
            root = createNode( key, item );
            size++;
            return null;
        }
        else
            return root.put( key, item );
    }
    
    /**
     * Entfernt diesen Schlüssel und das dazugehörige Element aus dem Tree
     * @param key Der Schlüssel
     * @return Das entfernte Element oder null, sollte der Schlüssel nicht
     * gefunden werden.
     */
    public V remove( K key ){
        if( root == null )
            return null;
        else
            return root.remove( key );
    }
    
    //************************************************//
    //                                                //
    //          Innere Klassen & Methoden             //
    //                                                //
    //************************************************//
    
    /**
     * Neue Instanzen des Nodes werden über diese Methode angelegt. Subklassen
     * können Node überschreiben, und zusätzliche Funktionen einbauen.
     * @param key Der Schlüssel zu dem Node
     * @param item Das Item zu dem Node
     * @return Ein neuer Node
     */
    protected Node createNode( K key, V item ){
        return new Node( key, item );
    }
    
    /**
     * Eine einfache Implementation eines Comparators. Sie funktioniert nur,
     * falls die Objects <code>Comparable</code> implementiert haben.
     * @author Benjamin Sigg
     * @version 1.0
     */
    private class DefaultComparator implements Comparator<K>{
        public DefaultComparator(){}
     
        @SuppressWarnings( "unchecked" )
        public int compare(K a, K b){
            return ((Comparable)a).compareTo( b );
        }
    }
    
    /**
     * Der Baum besteht aus mehreren Nodes. Die Nodes sortieren sich selbst.
     * Subklassen von AVLTree haben nur auf die allgemeinen "Getter"-Methoden
     * und auf die put/get/remove-Methoden, die ein Delegate der
     * entsprechende Methoden des AVLTrees sind.
     * @author Benjamin Sigg
     * @version 1.0
     */
    protected class Node{
        
        /** Die linke Seite, die kleineren Object's */
        private Node left;
        
        /** Die rechte Seite, die grösseren Object's */
        private Node right;
        
        /** Der Parent dieses Nodes */
        private Node parent;
        
        /** Der Object zu diesem Node */
        private K key;
        
        /** Das  zu diesem Node */
        private V item;
        
        /**
         * Die Balance die angiebt, welcher Teilbaum grösser ist,<br> 
         * -1: der linke ist höher<br>
         *  0: beider gleichhoch<br>
         *  1: der rechte ist höher
         */
        private int balance = 0;

        /**
         * Einfacher Konstruktor
         * @param key Der Schlüssel
         * @param item Das Element
         */
        public Node( K key, V item ){
            this.key = key;
            this.item = item;
        }

        /**
         * Gibt an, ob dieser Node oder einer seiner Subnodes dieses
         * Objekt besitzt.
         * @param value Das zu suchende Objekt
         * @return true, falls es gefunden wurde, sonst false
         */
        public boolean containsValue( V value ){
            if( (value == null ) ? (item == null) : (item.equals( value )) )
                return true;
            
            else if( left != null && left.containsValue( value ))
                return true;
            
            else if( right != null && right.containsValue( value ))
                return true;
            
            else
                return false;
        }
        
        /**
         * Gibt an, ob dieser Node oder einer seiner Subnodes diesen Schlüssel
         * besitzt.
         * @param key Der zu suchende Schlüssel
         * @return true, falls der Schlüssel gefunden wurde
         */
        public boolean containsKey( K key ){
            int compare = comparator.compare( this.key, key );
            
            if( compare == 0 )
                return true;
            else if( compare < 0 ){ // this.key < key
                if( right != null )
                    return right.containsKey( key );
            }
            else // this.key > key
                if( left != null )
                    return left.containsKey( key );
            
            return false;
        }
        
        /**
         * Setzt den linken Child diesen Nodes.
         * @param node Der neue linke Child
         */
        private void setLeft( Node node ){
            left = node;
            if( node != null )
                node.parent = this;
        }
        
        /**
         * Gibt den linken Knoten zurück.
         * @return Der linke Knoten
         */
        public Node getLeft(){
            return left;
        }
        
        /**
         * Setzt den rechten Child dieses Nodes.
         * @param node Der neue rechte Child
         */
        private void setRight( Node node ){
            right = node;
            if( node != null )
                node.parent = this;
        }
        
        /**
         * Gibt den rechten Knoten zurück.
         * @return Der rechte Knoten
         */
        public Node getRight(){
            return right;
        }
        
        /**
         * Gibt den Parent zurück.
         * @return Der Parent
         */
        public Node getParent(){
            return parent;
        }
        
        /**
         * Gibt die Balance zurück.<br>
         * Die Balance ist definiert als 
         * <code>Höhe rechter Teilbaum - Höhe linker Teilbaum</code>
         * @return Die Balance
         */
        public int getBalance(){
            return balance;
        }
        
        /**
         * Gibt den Key dieses Nodes zurück.
         * @return Der Key
         */
        public K getKey(){
            return key;
        }
        
        /**
         * Gibt das Element dieses Nodes zurück.
         * @return Das Element
         */
        public V getItem(){
            return item;
        }
        
        /**
         * Stellt eine Kopie dieses Nodes her.<br>
         * Schlüssel und Wert werden nicht kopiert, der Rest schon.
         */
        @Override
        public Object clone(){
            Node rightClone = right == null ? null : (Node)right.clone();
            Node leftClone = left == null ? null : (Node)left.clone();
            
            Node node = new Node( key, item );
            
            node.setLeft( leftClone );
            node.setRight( rightClone );
            
            node.balance = balance;
            
            return node;
        }
        
        /**
         * Setzt alle Keys dieses Unterbaumes in den Array.
         * @param keys Der Array der gefüllt werden soll
         * @param index Der Index des nächsten freien Platzes
         * @return Der Index des nächsten freien Platzes
         */
        public int keysToArray( Object[] keys, int index ){
            if( left != null )
                index = left.keysToArray( keys, index );
            
            keys[ index++ ] = key;
            
            if( right != null )
                index = right.keysToArray( keys, index );
            
            return index;
        }
        
        /**
         * Setzt alle Items dieses Unterbaumes in den Array.
         * @param items Der Array der gefüllt werden soll
         * @param index Der Index des nächsten freien Platzes
         * @return Der Index des nächsten freien Platzes
         */
        public int itemsToArray( Object[] items, int index ){
            if( left != null )
                index = left.keysToArray( items, index );
            
            items[ index++ ] = item;
            
            if( right != null )
                index = right.keysToArray( items, index );
            
            return index;
        }
        
        /**
         * Setzt alle Schlüssel/Werte-Paare dieses Unterbaumes in den Array.
         * @param array Der Array der gefüllt werden soll
         * @param index Der Index des nächsten freien Platzes
         * @return Der Index des nächsten freien Platzes
         */
        public int toArray( Object[][] array, int index ){
            if( left != null )
                index = left.toArray( array, index );
            
            array[ index   ][0] = key;
            array[ index++ ][1] = item;
            
            if( right != null )
                index = right.toArray( array, index );
            
            return index;
        }
        
        /**
         * Sucht den Schlüssel und liefert den Node mit diesem Schlüssel.
         * @param key Der Schlüssel
         * @return Der Node oder null
         */
        public Node getNode( K key ){
            int compare = comparator.compare( this.key, key );
            
            if( compare == 0 )
                return this;
            else if( compare < 0 ){ // this.key < key
                if( right != null )
                    return right.getNode( key );
            }
            else // this.key > key
                if( left != null )
                    return left.getNode( key );
            
            return null;
        }
        
        /**
         * Entfernt den Node mit diesem Key, und liefert anschliessend dessen
         * Wert zurück.
         * @param key Der zu entfernende Key
         * @return Der ehemalige Wert, oder null
         */
        public V remove( K key ){
            int compare = comparator.compare( this.key, key );
            
            if( compare == 0 ){
                removeNode();
                size--;
                return item;
            }
            else if( compare < 0 ){ // this.key < key
                if( right != null )
                    return right.remove( key );
            }
            else // this.key > key
                if( left != null )
                    return left.remove( key );
            
            return null;
        }
        
        /**
         * Entfernt den Wert und den Key dieses Nodes aus dem Tree.
         * <b>ACHTUNG</b> Durch diese Aktion wird dieser Node ungültig.
         * Er ist entweder nicht mehr Teil des Baumes, oder er bekommt einen
         * anderen Inhalt!
         */
        public void remove(){
        	removeNode();
        	size--;
        }
        
        private void removeNode(){
            if( left == null && right == null ){
                if( parent == null ){
                    root = null;
                }
                else{
                    boolean isRightChild  = parent.right == this;
                    
                    if( isRightChild ){
                        if( parent.balance == 1 ){
	                        parent.balance = 0;
	                        parent.setRight( null );
	                        parent.upout();
	                    }
	                    else if( parent.balance == 0 ){
	                        parent.balance = -1;
	                        parent.setRight( null );
	                    }
	                    else{ // parent.balance == -1
	                        removeRightZZ();
	                    }
                    }
                    else{ // isLeftChild
                        if( parent.balance == -1 ){
	                        parent.balance = 0;
	                        parent.setLeft( null );
	                        parent.upout();
	                    }
	                    else if( parent.balance == 0 ){
	                        parent.balance = 1;
	                        parent.setLeft( null );
	                    }
	                    else{ // parent.balance == 1
	                        removeLeftZZ();
	                    }
                    }
                }
            }
            else if( left == null ){
                exchangeKeyAndItem( right );
                setRight( null );
                balance = 0;
                upout();
            }
            else if( right == null ){
                exchangeKeyAndItem( left );
                setLeft( null );
                balance = 0;
                upout();
            }
            else{  // Childs an beiden Enden
                // Symmetrischer Nachfolger suchen
                Node node = right;
                
                while( node.left != null )
                    node = node.left;
                
                exchangeKeyAndItem( node );
                node.removeNode();
            }
            
            // Versichern, dass nicht noch ein zu alter Parent herumschwebt
            if( parent != null && parent.left != this && parent.right != this )
                parent = null;
        }
       
        /**
         * Entfernt diesen Node von dem Parent.<br>
         * Dazu muss der Node rechtes Child sein, und darf selbst keine Childs
         * besitzen.
         */
        private void removeRightZZ(){
            parent.setRight( null );
            
            if( parent.left.balance == -1 ){
                removeSimpleRightZZ();
                
                parent.balance = 0;
                parent.right.balance = 0;
                
                parent.upout();
            }
            else if( parent.left.balance == 0 ){
                removeSimpleRightZZ();
                
                parent.balance = 1;
                parent.right.balance = -1;
            }
            else{  // parent.left.balance == 1
                parent.exchangeKeyAndItem( parent.left );
                
                parent.setRight( parent.left );
                parent.setLeft( parent.right.right );
                
                parent.right.setRight( null );
                
                parent.balance = 0;
                parent.right.balance = 0;
                
                parent.upout();
            }
        }
        
        /**
         * Ändert die Baumstruktur, so dass der Unterbaum beginnend bei
         * parent einen korrekten Aufbau, aber noch falsche Balance hat.<br>
         * Dieser Node war rechtes Child des Parents, hatte keine Childs, und
         * der Parent muss Balance 0 oder -1 haben.
         */
        private void removeSimpleRightZZ(){
            parent.exchangeKeyAndItem( parent.left );
            parent.setRight( parent.left );
            parent.setLeft( parent.right.left );
        
            parent.right.setLeft( parent.right.right );
            parent.right.setRight( null );
        }
        
        /**
         * Entfernt diesen Node von dem Parent.<br>
         * Dazu muss der Node linkes Child sein, und darf selbst keine Childs
         * besitzen.
         */
        private void removeLeftZZ(){
            parent.setLeft( null );
            
            if( parent.right.balance == 1 ){
                removeSimpleLeftZZ();
                
                parent.balance = 0;
                parent.left.balance = 0;
                
                parent.upout();
            }
            else if( parent.right.balance == 0 ){
                removeSimpleLeftZZ();
                
                parent.balance = -1;
                parent.left.balance = 1;
            }
            else{  // parent.left.balance == 1
                parent.exchangeKeyAndItem( parent.right );
                
                parent.setLeft( parent.right );
                parent.setRight( parent.left.left );
                
                parent.left.setLeft( null );
                
                parent.balance = 0;
                parent.left.balance = 0;
                
                parent.upout();
            }
        }
        
        /**
         * Ändert die Baumstruktur, so dass der Unterbaum beginnend bei
         * parent einen korrekten Aufbau, aber noch falsche Balance hat.<br>
         * Dieser Node war linkes Child des Parents, hatte keine Childs, und
         * der Parent muss Balance 0 oder 1 haben.
         */
        private void removeSimpleLeftZZ(){
            parent.exchangeKeyAndItem( parent.right );
            parent.setLeft( parent.right );
            parent.setRight( parent.left.right );
        
            parent.left.setRight( parent.left.left );
            parent.left.setLeft( null );
        }
        
        /**
         * Versichert, dass die AVL-Bedingung nach dem entfernen eines Knotens
         * noch korrekt ist.<br>
         * Diese Methode wird von {@link #remove() remove} aufgerufen, und 
         * sollte keinesfalls anderweitig genutzt werden!
         */
        private void upout(){
            if( parent == null )
                return;
            if( balance != 0 )
                return;
            
            if( parent.left == this ){
                if( parent.balance == -1 ){
                   parent.balance = 0;
                   parent.upout();
                }
                else if( parent.balance == 0 ){
                    parent.balance = 1;
                }
                else{ // parent.balance == 1
                    int prbalance = parent.right.balance;
                    
                    if( prbalance == 1 ){
                       parent.exchangeKeyAndItem( parent.right );
                        
                       parent.setLeft( parent.right );
                       parent.setRight( parent.left.right );
                       
                       parent.left.setRight( parent.left.left );
                       parent.left.setLeft( this );
                       
                       parent.balance = 0;
                       parent.parent.balance = 0;
                       
                       parent.parent.upout();
                    }
                    else if( prbalance == 0 ){
                       parent.exchangeKeyAndItem( parent.right );
                       
                       parent.setLeft( parent.right );
                       parent.setRight( parent.left.right );
                       
                       parent.left.setRight( parent.left.left );
                       parent.left.setLeft( this );
                       
                       parent.balance = 1;
                       parent.parent.balance = -1;
                    }
                    else{ // prbalance == -1
                       int tbalance = parent.right.left.balance;
                       
                       parent.exchangeKeyAndItem( parent.right.left );
                       
                       parent.setLeft( parent.right.left );
                       parent.right.setLeft( parent.left.right );
                       parent.left.setRight( parent.left.left );
                       parent.left.setLeft( this );
                       
                       if( tbalance == -1 ){
                           parent.balance = 0;
                           parent.parent.balance = 0;
                           parent.parent.right.balance = 1;
                       }
                       else if( tbalance == 0 ){
                           parent.balance = 0;
                           parent.parent.balance = 0;
                           parent.parent.right.balance = 0;
                       }
                       else{ // tbalance == 1
                           parent.balance = -1;
                           parent.parent.balance = 0;
                           parent.parent.right.balance = 0;
                       }
                       
                       parent.parent.upout();
                    }
                }
            }
            else{ // parent.right == this
                if( parent.balance == 1 ){
                    parent.balance = 0;
                    parent.upout();
                 }
                 else if( parent.balance == 0 ){
                     parent.balance = -1;
                 }
                 else{ // parent.balance == -1
                     int prbalance = parent.left.balance;
                     
                     if( prbalance == -1 ){
                        parent.exchangeKeyAndItem( parent.left );
                         
                        parent.setRight( parent.left );
                        parent.setLeft( parent.right.left );
                        
                        parent.right.setLeft( parent.right.right );
                        parent.right.setRight( this );
                        
                        parent.balance = 0;
                        parent.parent.balance = 0;
                        
                        parent.parent.upout();
                     }
                     else if( prbalance == 0 ){
                        parent.exchangeKeyAndItem( parent.left );
                        
                        parent.setRight( parent.left );
                        parent.setLeft( parent.right.left );
                        
                        parent.right.setLeft( parent.right.right );
                        parent.right.setRight( this );
                        
                        parent.balance = -1;
                        parent.parent.balance = 1;
                     }
                     else{ // prbalance == 1
                        int tbalance = parent.left.right.balance;
                        
                        parent.exchangeKeyAndItem( parent.left.right );
                        
                        parent.setRight( parent.left.right );
                        parent.left.setRight( parent.right.left );
                        parent.right.setLeft( parent.right.right );
                        parent.right.setRight( this );
                        
                        if( tbalance == 1 ){
                            parent.balance = 0;
                            parent.parent.balance = 0;
                            parent.parent.left.balance = -1;
                        }
                        else if( tbalance == 0 ){
                            parent.balance = 0;
                            parent.parent.balance = 0;
                            parent.parent.left.balance = 0;
                        }
                        else{ // tbalance == -1
                            parent.balance = 1;
                            parent.parent.balance = 0;
                            parent.parent.left.balance = 0;
                        }
                        
                        parent.parent.upout();
                     }
                 }
            }
        }
           
        /**
         * Fügt ein Element ein, und updated evtl. den Tree.
         * @param key Der Key für das neue Element
         * @param item Das neue Element
         * @return Das Item das zuvor gesetzt war
         */
        public V put( K key, V item ){
            int compare = comparator.compare( this.key, key );
            
            if( compare == 0 ){
                V oldItem = this.item;
                this.item = item;
                return oldItem;
            }
            else if( compare < 0 ){
                if( right == null ){
                    // Rechts wird ein neuer Node eingefügt
                    setRight( createNode( key, item ) );
                    size++;
                    
                    if( balance == -1 )
	                  balance = 0;
	                else if( balance == 0 ){
	                  balance = 1;
	                  upin();
	                }
	                    // parent.balance == 1 kann nicht sein, da dann
	                    // right != wäre
                    
                }
                else{
                    right.put( key, item );
                    
                }
            }
            else{
                if( left == null ){
                    // Links wird ein neuer Node eingefügt
                    setLeft( createNode( key, item ) );
                    size++;
                    
                    if( balance == 1 )
	                  balance = 0;
	                else if( balance == 0 ){
	                  balance = -1;
	                  upin();
	                }
	                // parent.balance == -1 kann nicht sein, da dann
	                // left != wäre                    
                }
                else{
                    left.put( key, item );
                    
                }
            }
            
            return null;
        }
        
        /**
         * Versichert, dass die AVL-Bedingung für den Teilbaum mit diesem
         * Root korrekt ist, nachdem ein Node hinzugefügt wurde.<br>
         * Diese Methode wird durch {@link #put put} aufgerufen,
         * und sollte keinesfalls anderweitig genutzt werden!
         */
        private void upin(){
            if( parent == null )
                return;
            if( balance == 0 )
                return;
            
            if( parent.left == this ){
                if( parent.balance == 1 ){
                    parent.balance = 0;
                }
                
                else if( parent.balance == 0 ){
                    parent.balance = -1;
                    parent.upin();
                }
                else{
                    if( balance == -1 ){
                        // Rotation nach rechts
                        rotateInRight();                        
                    }
                    else{
                        // Doppelrotation links - rechts
                        rotateInLeftRight();
                    }
                }
            }
            else{ // if( parent.right == this ) 
                if( parent.balance == -1 ){
                    parent.balance = 0;
                }
                
                else if( parent.balance == 0 ){
                    parent.balance = 1;
                    parent.upin();
                }
                else{
                    if( balance == 1 ){
                        // Rotation nach links
                        rotateInLeft();
                    }
                    else{
                        // Doppelrotation rechts-links
                        rotateInRightLeft();
                    }
                }
            }
        }
        

        
        /**
         * Dreht diesen Node zusammen mit dem Parent nach rechts.<br>
         * Die Balance wird dabei neu gesetzt.
         */
        private void rotateInRight(){
            rotateNodeInRight();
            parent.balance = 0;
            balance = 0;
        }
        
        /**
         * Dreht diesen Node zusammen mit dem Parent nach links.<br>
         * Die Balance wird dabei neu gesetzt.
         */
        private void rotateInLeft(){
            rotateNodeInLeft();
            parent.balance = 0;
            balance = 0;
        }
        
        /**
         * Dreht den rechten Child nach links, dann diesen Node nach rechts.<br>
         * Die Balance wird dabei neu gesetzt.
         */
        private void rotateInLeftRight(){
            int befor = right.balance;
            
            right.rotateNodeInLeft();
            rotateNodeInRight();
            
            if( befor == 0 ){ // beide Teilbäume leer
                parent.balance = 0;
                parent.left.balance = 0;
                parent.right.balance = 0;
            }
            else if( befor == -1 ){ // linker Teilbaum grösser
                parent.balance = 0;
                parent.left.balance = 0;
                parent.right.balance = 1;
            }
            else{
                parent.balance = 0;
                parent.left.balance = -1;
                parent.right.balance = 0;
            }
        }
        
        /**
         * Dreht den linken Child nach rechts, dann diesen Node nach links.<br>
         * Die Balance wird dabei neu gesetzt.
         */
        private void rotateInRightLeft(){
            int befor = left.balance;
            
            left.rotateNodeInRight();
            rotateNodeInLeft();
            
            if( befor == 0 ){ // beide Teilbäume leer
                parent.balance = 0;
                parent.left.balance = 0;
                parent.right.balance = 0;
            }
            else if( befor == 1 ){ // linker Teilbaum grösser
                parent.balance = 0;
                parent.left.balance = -1;
                parent.right.balance = 0;
            }
            else{
                parent.balance = 0;
                parent.left.balance = 0;
                parent.right.balance = 1;
            }
        }
        
        /**
         * Rotiert diesen Node mit seinem Parent nach rechts.<br>
         * Die Balance wird dabei nicht verändert!
         */
        private void rotateNodeInRight(){
            exchangeKeyAndItem( parent );
            
            parent.setLeft( left );
            setLeft( right );
            setRight( parent.right );
            parent.setRight( this );
        }
        
        /**
         * Rotiert diesen Node mit seinem Parent nach links.<br>
         * Die Balance wird dabei nicht verändert!
         */
        private void rotateNodeInLeft(){
            exchangeKeyAndItem( parent );
            
            parent.setRight( right );
            setRight( left );
            setLeft( parent.left );
            parent.setLeft( this );
        }
        
        /**
         * Tauscht Key und Item dieses Nodes mit einem anderen Node aus.
         * @param node Der andere Node
         */
        private void exchangeKeyAndItem( Node node ){
            K tempK = node.key;
            V tempI = node.item;
            
            node.key = key;
            node.item = item;
            
            key = tempK;
            item = tempI;
        }
    }
}