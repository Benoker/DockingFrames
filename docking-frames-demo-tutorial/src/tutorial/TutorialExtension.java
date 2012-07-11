package tutorial;

import tutorial.support.sets.TutorialSet;

/**
 * Allows to load additional classes as tutorials.
 * @author Benjamin Sigg
 */
public interface TutorialExtension {
	/**
	 * Tells what additional classes are available for the {@link TutorialSet} <code>set</code>.
	 * @param set the set into which to add additional tutorials
	 * @return the additional tutorials or {@link TutorialSet}s
	 */
	public Class<?>[] getTutorials( Class<? extends TutorialSet> set );
}
