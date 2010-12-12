package tutorial.core.basics;

import tutorial.support.Tutorial;
import tutorial.support.sets.TutorialSet;

@Tutorial(title="Core", id="BasicsCore")
public class BasicCoreSet extends TutorialSet{
	public BasicCoreSet(){
		super(  HelloWorldExample.class,
                StationsExample.class,
                SplitDockStationExample.class,
                ThemesExample.class,
                PersistentLayoutExample.class,
                ActionsExample.class,
                InternalExample.class,
                BackgroundExample.class,
                BorderModifierExample.class );
	}
}
