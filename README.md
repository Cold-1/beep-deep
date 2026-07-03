# Beep Deep

A RuneLite plugin that plays customizable sounds when certain things happen in a raid. Only supported in Tombs of Amascut (ToA) for now!

Each event can be given up to **3 sounds**. When the event fires, one of the configured
sounds is chosen at random and played. Every event has its own enable/disable toggle, and
every sound slot has its own volume (0–100%).

## Events

- Enter Path of Crondis
- Leave Path of Crondis
- Het seal not one-phased
- Apmeken issue not fixed
- Vault opens with no rare loot
- Vault opens with rare loot

These events are supported by the plugin and can each play a configured sound when triggered.

## Configuring sounds

For each sound slot you can provide either:

- a **local file path** (e.g. `C:\sounds\beep.wav`)
- a **URL** (e.g. `https://example.com/beep.wav`), only used when *Allow remote URLs* is enabled
- a RuneScape **sound ID** (e.g. `123`), see [List of sound IDs](https://oldschool.runescape.wiki/w/List_of_sound_IDs).

Leave a slot blank to disable it.

### Supported formats

Only formats supported by Java's built-in audio system are playable: **WAV, AU, and AIFF**.
MP3 and OGG are not supported.

### Remote URLs

Remote URLs are **disabled by default**. When enabled, downloaded sounds are cached on
disk under `.runelite/beep-deep/cache` so each URL is only fetched once.

> ⚠️ Enabling remote URLs submits your IP address to a 3rd-party server not controlled or
> verified by RuneLite developers.

## Volume

Volume is a percentage from 0 to 100, where 100% is the sound's original loudness. A value
of 0 mutes that slot.
