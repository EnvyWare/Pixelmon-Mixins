# Pixelmon-Mixins [![Discord](https://img.shields.io/discord/831966641586831431)](https://discord.gg/7vqgtrjDGw)

Active Patches:
* Reduces grass block growth attempts (lots of calls to Random)
* Changes Legendary and UltraBeast checks to Set rather than ArrayList (O(n) checking vs O(1))
* Adds cache to the Quests' DateObjective (faster date checking)
* Adds a cache to Pokemon's BossMode
* Adds a new path navigator for Pokemon larger than a specific growth size
* Adds an "isFlying" cache to Flying Pokemon
* Moves Pokemon's despawning logic inline with Sponge's
* Removes all "travel" calls from EntityStatue
* Adds reflection Cache for ExtraStats
* Fixes waiting glitch with Leftovers
* Fixes MagicCoat ignoring Sketch
* Adds Pokemon team cache
* Fixes ArrayOutOfBounds exception in Party
* Fixes Poison waiting glitch
* Adds Pokemon form cache
* Fixes regenerator waiting glitch
* Adds reflection cache to StatusBase
* Optimizes BerryTrees
