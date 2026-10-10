# Beep Deep

A RuneLite plugin that plays customizable sounds when certain things happen in a raid. Only supported in Tombs of Amascut (ToA) for now!

Each event can be given up to **5 sounds**. When the event fires, one of the configured
sounds is chosen at random and played. Every event has its own enable/disable toggle, and
every sound slot has its own volume (0–100%). **General → Master volume** adjusts all plugin sounds together; set it to 0 to mute them.

## Events

- Enter each of the four path puzzle rooms / leave them for their corresponding boss rooms
- Enter the raid / leave the raid, including teleports out
- Fail a puzzle or boss room challenge
- Fail to survive or abandon the raid
- Path of Apmeken
  - Apmeken issue not fixed
  - Any player slipping on a banana peel in Ba-Ba's boss room
- Path of Scabaras
  - Taking damage from the obelisk's falling rocks
  - Failing the sequence puzzle or number pressure plate puzzle
- Path of Crondis
  - Crocodile damages the Palm of Resourcefulness
  - Trying to take water without a container ("You don't have anything to fill.") or from an empty waterfall ("It's empty")
- Path of Het
  - Any player taking damage from light or dark orbs in the Het puzzle room
  - Het seal not one-phased
  - Any player hit by an Unstable Orb in Akkha's arena
- Vault opens with no rare loot
- Vault opens with rare loot

## Configuring sounds

For each sound slot you can provide either:

- an **absolute local file path**, such as `C:\sounds\beep.wav`, or a **relative path inside `.runelite`**, such as
`plugin-data/beep-deep/beep.wav`
- a **URL** (e.g. `https://example.com/beep.wav`), only used when *Allow remote URLs* is enabled
- a RuneScape **sound ID** (e.g. `123`), see [List of sound IDs](https://oldschool.runescape.wiki/w/List_of_sound_IDs).

Leave a slot blank to disable it.

Relative file paths resolve from your RuneLite directory, so `beep-deep/sounds/beep.wav` points to
`.runelite/beep-deep/sounds/beep.wav`. Absolute paths can point to sound files anywhere on your computer.

### Supported formats

Only formats supported by Java's built-in audio system are playable: **WAV, AU, and AIFF**.
MP3 and OGG are not supported.

### Testing a configured sound

With the plugin enabled, open **Test configured sounds**, select an **Event** and
**Sound slot** (1–5), then click **Play sound**. The checkbox returns to unchecked
after every click, so you can test the same sound repeatedly.

The selected slot plays at its own volume scaled by **Master volume**, even if
the event is disabled. A warning identifies the event and slot if the file is
missing or unsupported, the slot is empty or muted, or a remote sound cannot be
loaded. Remote previews require **Allow remote URLs**, just like event playback.

### Remote URLs

Enable **Allow remote URLs** in **General** to use sound links. It is disabled by default.

> ⚠️ Enabling remote URLs submits your IP address to a 3rd-party server not controlled or
> verified by RuneLite developers.

### Party Sync

Join the same RuneLite party and enable **General → Enable Party Sync** on each member's
Beep Deep settings. It is disabled by default. Choose one member to enable **Party Leader**
to detect shared raid events.

Synced events play the same sound slot for everyone, using each member's own sound assignments,
event toggles, and volume settings. Share a sound configuration if you want matching sounds.

## Sharing a sound configuration

With the plugin enabled, go to **General → Open sharing dialog**.
Use **Copy configuration** to share your setup, or paste a code and click **Import configuration**.
After importing, close and reopen Beep Deep's settings to refresh them.

Sharing includes event toggles, sound assignments, and slot volumes.
Your master volume and remote URL permission stay unchanged.
Local sound files are not included and their paths may need updating on another computer.

Invalid or incompatible codes leave your settings unchanged.
