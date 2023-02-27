# FTB Library [![](http://cf.way2muchnoise.eu/ftb-library-legacy-forge.svg) ![](https://cf.way2muchnoise.eu/packs/ftb-library-legacy-forge.svg) ![](http://cf.way2muchnoise.eu/versions/ftb-library-legacy-forge.svg)](https://www.curseforge.com/minecraft/mc-mods/ftb-library-legacy-forge)

Use https://github.com/FTBTeam/FTB-Mods-Issues for any mod issues

### Building the mod

- Make sure you have Java JDK 1.8+ installed
- Downloading it as zip or clone with Git
- Open console / Command Prompt in downloaded folder's location
- Run "gradlew build"

### Adding as dependency

```groovy
repositories {
	maven { url "https://maven.latmod.com/" }
}

dependencies {
	deobfCompile "com.feed_the_beast.mods:FTBLib:${ftblib_version}"
}
```

And `ftblib_version=5.+` line in your `gradle.properties` file. You can also specify exact version, e.g. `5.0.0`.

You probably also want to add "required-after:ftblib" or "after:ftblib" in your @Mod's dependencies.
Most of the APIs don't need hard dependency, but things like GUIs won't work without FTBLib loaded

### NOTE:
openjdk 1.8 DOES NOT WORK! It doesn't include javafx.
use oricale's jdk instead
