package tutorial.core.guide;

import tutorial.support.Tutorial;
import tutorial.support.sets.TutorialSet;

@Tutorial(title="Examples from the guide", id="GuideCore")
public class GuideCoreSet extends TutorialSet{
	public GuideCoreSet(){
		super(  PlaceholderExample.class,
				PersistentLayoutExample.class,
				PerspectiveExample.class,
				ActionsExample.class,
				GroupActionExample.class,
				TitleExample.class,
				ColorExample.class,
				DisplayerExample.class );
	}
}
