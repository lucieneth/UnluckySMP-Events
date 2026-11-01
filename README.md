# Unlucky Events

A tiny **server-side** Fabric mod for Minecraft **26.3**. It puts Minecraft's own seasonal events under operator control. It adds nothing that isn't in vanilla: no custom items, particles or effects. It only decides *when* vanilla's built-in date-based behaviour happens.

## What it does

### 🎃 Halloween

| Feature | Vanilla dates (auto) | Effect |
|---------|----------------------|--------|
| **Pumpkin heads** | Oct 31 | Zombies and skeletons sometimes spawn wearing a carved pumpkin or jack o'lantern. This is vanilla's own Halloween easter egg. |
| **Halloween bats** | Oct 20 – Nov 3 | Restores a rule from older versions that newer releases dropped: bats skip their 50% spawn rejection and spawn in light up to level 7 instead of 4. |

Halloween has a **mode**:

| Mode | Effect |
|------|--------|
| **auto** (default) | Each feature follows its vanilla dates above, in the server's time zone. |
| **on** | Both features are active every day. Handy for testing or a surprise Halloween. |
| **off** | Both are disabled. This also removes vanilla's Oct 31 pumpkin heads. |

When Halloween season starts or ends (Oct 20 / Nov 3 in auto mode, or when an operator switches it), everyone gets a chat announcement. This can be turned off.

### What about Christmas and New Year?

Minecraft does have them, but only on the client: present-wrapped chests on Dec 24–26, and title-screen splash texts on Oct 31, Dec 24 and Jan 1. Each player's own game decides these from its own clock, so a server can't turn them on or off. Halloween is the only vanilla event the server controls.

### How it works (for the curious)

- **Pumpkin heads:** a mixin answers `SpecialDates.isHalloween()` from the Halloween settings. Vanilla calls this when zombies and skeletons spawn.
- **Bats:** a mixin on `Bat.checkBatSpawnRules` skips the coin flip and raises the light limit from 4 to 7 while Halloween is active.
- **Dates** are checked once per second from the server tick and cached, so the spawn checks cost nothing extra.

## Requirements

- Minecraft **26.3** (Java Edition)
- Fabric Loader **≥ 0.19.5**, Java **≥ 25**
- Server-side only. No Fabric API dependency. Clients can join with an unmodified game.

## Installation

1. Grab `UnluckySMP-Events 26.3.jar` from the releases (or build it, see below).
2. Drop it into your server's `mods/` folder.
3. Start the server. A config file is generated at `config/unlucky-events.json`.

## Commands

`/unluckyevents` shows the version and Halloween's status. Everyone can use it.

Operator-only (`gamemasters` / permission level 2):

- `/unluckyevents enable halloween` forces Halloween on and saves.
- `/unluckyevents disable halloween` forces Halloween off and saves.
- `/unluckyevents auto halloween` puts Halloween back on vanilla's dates and saves.
- `/unluckyevents reload` reloads `config/unlucky-events.json` from disk.

## Configuration

`config/unlucky-events.json`:

```json
{
  "timeZone": "",
  "announceEventChanges": true,
  "halloween": {
    "pumpkinHeads": true,
    "halloweenBats": true,
    "mode": "auto"
  }
}
```

- `timeZone`: an IANA zone such as `Europe/Amsterdam`. Leave it empty to use the server machine's zone.
- `pumpkinHeads` / `halloweenBats`: turn either feature off on its own.

Edit and run `/unluckyevents reload`, or use the commands above.

## Building

```
./gradlew build
```

The mod jar lands in `build/libs/`.

## License

CC0-1.0. Made by Lucien & Claude.
