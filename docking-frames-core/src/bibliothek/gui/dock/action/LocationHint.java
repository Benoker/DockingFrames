/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.action;

import bibliothek.gui.Dockable;


/**
 * A LocationHint gives information about the preferred location of a 
 * {@link DockActionSource} in respect to other sources. How to interpret
 * the LocationHint is up to the {@link ActionOffer} which creates the
 * final {@link DockActionSource} for a {@link Dockable}.
 * @author Benjamin Sigg
 *
 */
public class LocationHint {
	/** Used if this hint was produced by a {@link Dockable} */
	public static final Origin DOCKABLE = new Origin( "dockable" );
	/** Used if this hint was produced by an {@link ActionGuard} */
	public static final Origin ACTION_GUARD = new Origin( "action_guard" );
	/** Used if this hint was produced by the parent of a dockable */
	public static final Origin DIRECT_ACTION = new Origin( "direct_action" );
	/** Used if this hint was produced by one of the parents of a dockable */
	public static final Origin INDIRECT_ACTION = new Origin( "indirect_action" );
	/** Used if this hint was produced by an {@link ActionOffer}*/
	public static final Origin ACTION_OFFER = new Origin( "action_offer" );
	/** Used if it is unclear who produced this hint */
	public static final Origin UNKNOWN_ORIGIN = new Origin( "unknown" );
	
	/** Used to indicate that this hint likes to stay as left as possible */
	public static final Hint LEFT_OF_ALL = new Hint( "left_of_all" );
	/** Used to indicate that this hint likes to stay at the left side */
	public static final Hint VERY_LEFT = new Hint( "very_left" );
	/** Used to indicate that this hint likes to stay left of the middle */
	public static final Hint LEFT = new Hint( "left" );
	/** Used to indicate that this hint likes to stay a bit left of the middle */
	public static final Hint LITTLE_LEFT = new Hint( "little_left" );
	/** Used to indicate that this hint likes to stay in the middle */
	public static final Hint MIDDLE = new Hint( "middle" );
	/** Used to indicate that this hint likes to stay a bit right of the middle */
	public static final Hint LITTLE_RIGHT = new Hint( "little_right" );
	/** Used to indicate that this hint likes to stay right of the middle */
	public static final Hint RIGHT = new Hint( "right" );
	/** Used to indicate that this hint likes to stay at the right side */
	public static final Hint VERY_RIGHT = new Hint( "very_right" );
	/** Used to indicate that this hint likes to stay as right as possible */
	public static final Hint RIGHT_OF_ALL = new Hint( "right_of_all" );
	/** Used to indicate that this hint does not know where to stay */
	public static final Hint UNKNOWN_HINT = new Hint( "unknown" );
	
	/** The default-location-hint does not know anything*/
	public static final LocationHint UNKNOWN = new LocationHint( null, null );
	
	/** An object that can be freely used by client code */
	private Object clientObject;
	/** Tells who produced this hint */
	private Origin origin;
	/** Tells where this hint likes to stay */
	private Hint hint;
	
	/**
	 * Creates a new LocationHint.
	 * @param origin tells who produces this hint
	 */
	public LocationHint( Origin origin ){
		this( origin, null, null );
	}
	
	/**
	 * Creates a new LocationHint.
	 * @param hint tells where this hint likes to stay
	 */
	public LocationHint( Hint hint ){
		this( null, hint, null );
	}
	
	/**
	 * Creates a new LocationHint.
	 * @param origin tells who produces this hint
	 * @param hint tells where this hint likes to stay
	 */
	public LocationHint( Origin origin, Hint hint ){
		this( origin, hint, null );
	}
	
	/**
	 * Creates a new LocationHint.
	 * @param origin tells who produces this hint
	 * @param hint tells where this hint likes to stay
	 * @param clientObject an object that can be freely chosen and used by client code
	 */
	public LocationHint( Origin origin, Hint hint, Object clientObject ){
		if( origin == null )
			origin = UNKNOWN_ORIGIN;
		
		if( hint == null )
			hint = UNKNOWN_HINT;
		
		this.origin = origin;
		this.hint = hint;
		this.clientObject = clientObject;
	}
	
	/**
	 * Gets the object which has no special meaning.
	 * @return the client-object
	 */
	public Object getClientObject(){
		return clientObject;
	}
	
	/**
	 * Sets an object which has no special meaning. This object can
	 * be used freely by client code.
	 * @param clientObject the client-object
	 */
	public void setClientObject( Object clientObject ){
		this.clientObject = clientObject;
	}
	
	/**
	 * Gets the origin of this hint.
	 * @return information who produced this hint
	 */
	public Origin getOrigin(){
		return origin;
	}
	
	/**
	 * Gets the preferred location of this hint. It's up to the {@link ActionOffer}
	 * how to interpret this property.
	 * @return the preferred location
	 */
	public Hint getHint(){
		return hint;
	}
	
	/**
	 * Describes the preferred location of a {@link LocationHint}
	 * @author Benjamin Sigg
	 */
	public static class Hint extends Enumeration{
		/**
		 * Creates a new hint.
		 * @param id the unique id of this hint
		 */
		public Hint( String id ){
			super( id );
		}

	}
	
	/**
	 * Describes who created a {@link LocationHint}
	 * @author Benjamin Sigg
	 */
	public static class Origin extends Enumeration{
		/**
		 * Creates a new origin
		 * @param id the unique id of this origin
		 */
		public Origin( String id ){
			super( id );
		}

	}
	
	/**
	 * Base-class for Enumerations (sets of uniquely identified objects).
	 * @author Benjamin Sigg
	 */
	public static class Enumeration{
		/** The unique id of this enumeration */
		private String id;
		
		/**
		 * Creates a new Enumeration
		 * @param id a unique id
		 */
		public Enumeration( String id ){
			if( id == null )
				throw new IllegalArgumentException( "Identity must not be null" );
			this.id = id;
		}
		
		/**
		 * Gets the id of this Enumeration
		 * @return the id
		 */
		public String getId(){
			return id;
		}
		
		@Override
		public boolean equals( Object obj ){
			return (obj instanceof Enumeration) && ((Enumeration)obj).id.equals( id );
		}
		
		@Override
		public int hashCode(){
			return id.hashCode();
		}
	}
}
