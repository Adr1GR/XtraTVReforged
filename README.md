# XtraTV Reforged

<img src="https://github.com/AndreyAsadchy/Xtra/blob/197ba90cac879abd2a5645393ce361847f12fa0b/app/src/main/ic_launcher-web.png" align="left" width="100"/>

XtraTV Reforged is an unofficial fork of [Xtra](https://github.com/crackededed/Xtra), a Twitch player and browser for Android, with added support for Android TV and remote controls (D-pad).

</br>
</br>

## About this repository

This repository is **not** the official Xtra project. It is based on the latest Xtra upstream and adds the TV features from [JoshTheMenace/XtraTV](https://github.com/JoshTheMenace/XtraTV), which was itself a fork of an older Xtra version. Those changes were ported on top of the current upstream code.

### AI DISCLAIMER

The TV changes in this fork were written with the help of AI tools (Claude), both in the original XtraTV fork and in porting them to the current upstream.

### TV features

- Side navigation rail and D-pad focus states
- Fixes for focus traps and dead ends on tabbed pages
- Starts on Following with the Channels tab on TV
- Remote control for the player: OK = play/pause, left/right = seek 15 s (VODs and clips), up/down = show controls, back = hide controls and then close the player
- Search field opens the keyboard with OK
- Player chat pane hidden on TV

## Installing

There are no official releases of this fork. Build the APK yourself:

```
./gradlew assembleRelease
```

The output is in `app/build/outputs/apk/release/`. It uses the same application ID as Xtra but is signed with the debug key from this repository, so it can't be installed over the F-Droid or upstream release. Uninstall that one first (this deletes its data).

For the official app, see [Xtra on F-Droid](https://f-droid.org/packages/com.github.andreyasadchy.xtra/) or the [upstream releases](https://github.com/crackededed/Xtra/releases/tag/latest).

## License
Xtra is licensed under the [GNU Affero General Public License v3.0](LICENSE), and so is this fork.
