# English Description:
# 🏆 **EtiesChallengesMod**

EtiesChallengesMod is a powerful Fabric mod designed to shake up your Minecraft experience with unpredictable, game-changing challenges. Whether you want to make your casual survival world more punishing or kick off a chaotic speedrun with friends to defeat the Ender Dragon, this mod gives you the ultimate control.

---

## **In-Game Configuration Menu 🕹️**
*(New in Version a0.1.154t!)*
Forget typing long chat commands every time! You can now open a fully integrated, modern configuration screen using your dedicated keybind. It features clean sidebar navigation divided into three sections:
*   **Timer Controls:** Easily Start, Pause, Resume, or Stop the challenge clock. You can also open a dedicated **Timer Color** menu to customize the timer's look live in-game!
*   **Available Challenges:** Choose, toggle, and configure your challenges seamlessly. Your selected challenges stay highlighted in the menu so you always know what's currently active.
*   **Settings:** Access global mod preferences and reset your configurations in a flash.
* **Menu Keybind:** press "M" to open the menu
---

## **Features 🔥**

**Random Item Challenge:** Receive a random item directly into your inventory every minute. From a shiny diamond to a simple dirt block, you never know what's coming next!
*   **Randomized Block Drops:** Every single block type drops a completely random item. The drops stay consistent throughout the challenge!
*   **Randomized Mob Drops:** When mobs die, their drops are replaced with random items while maintaining the original drop quantity (e.g., if a cow drops 2 leather, you'll get 2 of the random replacement item instead).
*   **GUI-Configurable Drop Amount:** Use the new text field in the challenge menu to set the drop quantity from **1 to 64 items** dynamically. Want full stacks of chaos? You decide!

**Movement Speed Challenge:** This challenge will keep you on your toes! Every 5 to 15 minutes, your movement speed will be randomly increased or decreased, creating an ever-changing dynamic.

**Timer above the hotbar:** Keep track of your challenge time with a handy, color-customizable timer right above your hotbar.

**Start lock:** Players cannot move or mine blocks before a challenge officially begins. This guarantees a fair start for everyone on the server!

---

## **Commands (For Admins) 🎮**
While the new GUI is the easiest way to control the mod, all actions can still be triggered via the `/challenge` command:
/challenge start [name] [stackSize]
: Starts a new challenge.
- **Challenge names:** `random_item`, `movement_speed`, `none` (timer only, no challenge effects)
- **Stack size (optional, random_item only):** A number between 1 and 64 that determines how many items you receive every 60 seconds
  - *Example:* `/challenge start random_item` - Starts with 1 item per minute (default)
  - *Example:* `/challenge start random_item 32` - Starts with 32 items per minute
  - *Example:* `/challenge start random_item 64` - Starts with 64 items per minute
/challenge pause
: Pauses the current challenge and the timer.
/challenge resume
: Resumes a paused challenge.
/challenge stop
: Ends the current challenge, stops all effects, and resets the interface.
/challenge test
: Skips the challenge waiting time for quick testing purposes.

---

## Known Issues ⚠️
- **Stack Limit Restriction:** Items with naturally low stack limits (like helmets or swords with a max stack of 1) can cause inventory clutter when receiving large quantities through the 60-second item distribution.
- **Future Solution:** I am currently working on bypassing stack size limitations during the Random Item Challenge to improve inventory management.

---

## Future plans 🚀
I am actively developing this mod and plan to add a voting system in the future that will allow players to vote on upcoming events. I will also expand the mod with additional challenges:

*   **Voting system:** A system allowing server players to vote on the next active challenge.
*   **Random effects:** A challenge that applies random positive and negative status effects to players at intervals.
*   **Item removal:** A challenge that removes a random item from the players' inventory unexpectedly.
*   **Hostile spawns:** A challenge that spawns hostile mobs in random locations nearby (always wearing iron helmets!).

---

# German Description:
# 🏆 **EtiesChallengesMod**

EtiesChallengesMod ist ein kleiner, aber feiner Fabric-Mod, der deine Minecraft-Erfahrung mit unvorhersehbaren Herausforderungen auf den Kopf stellt. Perfekt, um deine Survival-Welt schwerer zu machen oder einen chaotischen Speedrun mit Freunden oder alleine zu starten, bei dem ihr den Enderdrachen besiegen müsst.

---

## **In-Game Steuerungs-Menü 🕹️**
*(Neu in Version a0.1.154t!)*
Vergiss umständliche Chat-Befehle! Ab sofort kannst du über eine eigene konfigurierbare Taste ein modernes Einstellungs-Menü direkt im Spiel öffnen. Das intuitive Design bietet eine übersichtliche Sidebar-Navigation:
*   **Timer Controls:** Starte, pausiere, setze fort oder stoppe die Challenge-Uhr. Über das eigene **Timer Color** Menü kannst du die Farbe des Timers live im Spiel anpassen!
*   **Available Challenges:** Wähle mehrere Challenges gleichzeitig aus, konfiguriere sie und starte sie simultan. Die Haken bleiben im GUI aktiv, damit du immer siehst, was gerade läuft.
*   **Settings:** Verwalte globale Mod-Einstellungen und setze den Timer im Handumdrehen zurück.

---

## **Features 🔥**

**Zufällige Item-Challenge:** Erhalte jede Minute ein zufälliges Item direkt in dein Inventar. Von einem wertvollen Diamanten bis zu einem einfachen Dirt-Block – du weißt nie, was als Nächstes kommt!
*   **Zufällige Block-Drops:** Jeder Block-Typ droppt ein komplett zufälliges Item, welches über die gesamte Dauer der Challenge gleich bleibt!
*   **Zufällige Mob-Drops:** Wenn Mobs sterben, werden ihre Drops durch zufällige Items ersetzt. Die ursprüngliche Drop-Anzahl bleibt erhalten (z.B. wenn eine Kuh 2x Leder droppt, erhältst du stattdessen 2x das zufällige Ersatz-Item).
*   **Einstellbare Drop-Menge im GUI:** Nutze das neue Textfeld im Challenge-Tab, um die Menge der Items dynamisch von **1 bis 64 Items** festzulegen. Lust auf pures Chaos mit vollen Stacks? Du entscheidest!

**Bewegungstempo-Challenge:** Diese Challenge hält dich auf Trab! Alle 5 bis 15 Minuten wird dein Bewegungstempo zufällig erhöht oder verringert, was für eine ständig wechselnde Dynamik sorgt.

**Timer über der Hotbar:** Behalte deine Challenge-Zeit mit einem praktischen, farblich anpassbaren Timer direkt über der Hotbar im Blick.

**Start-Sperre:** Vor dem offiziellen Beginn einer Challenge können sich Spieler weder bewegen noch Blöcke abbauen. Das garantiert einen fairen Start für alle auf dem Server!

---

## **Befehle (Für Admins) 🎮**
Obwohl das neue GUI der einfachste Weg ist, die Mod zu steuern, können alle Aktionen weiterhin über den `/challenge`-Befehl ausgeführt werden:
/challenge start [name] [stackSize]
: Startet eine neue Challenge.
- **Challenge-Namen:** `random_item`, `movement_speed`, `none` (nur Timer, keine Challenge-Effekte)
- **Stack-Größe (optional, nur für random_item):** Eine Zahl zwischen 1 und 64, die bestimmt, wie viele Items du alle 60 Sekunden erhältst
  - *Beispiel:* `/challenge start random_item` - Startet mit 1 Item pro Minute (Standard)
  - *Beispiel:* `/challenge start random_item 32` - Startet mit 32 Items pro Minute
  - *Beispiel:* `/challenge start random_item 64` - Startet mit 64 Items pro Minute
/challenge pause
: Pausiert die laufende Challenge und den Timer.
/challenge resume
: Setzt eine pausierte Challenge fort.
/challenge stop
: Beendet die aktuelle Challenge, stoppt alle Effekte und setzt die Auswahl im Menü zurück.
/challenge test
: Überspringt die Wartezeit der Challenge zu Testzwecken.

---

## Bekannte Probleme ⚠️
- **Stack-Limit-Einschränkung:** Items mit niedrigen Stack-Limits (wie Helme oder Schwerter mit einem maximalen Stack von 1) können zu Inventar-Unordnung führen, wenn große Mengen durch die 60-Sekunden-Item-Verteilung empfangen werden.
- **Zukünftige Lösung:** Ich arbeite derzeit daran, diese Stack-Größen-Limitierungen während der Random Item Challenge komplett zu entfernen, um das Inventar-Management zu verbessern.

---

## Zukünftige Pläne 🚀
Ich entwickle diesen Mod aktiv weiter und plane, in Zukunft ein Abstimmungssystem hinzuzufügen, mit dem Spieler über anstehende Events abstimmen können. Außerdem wird die Mod um folgende Challenges erweitert:

*   **Abstimmungssystem:** Ein System, das es Spielern auf einem Server erlaubt, gemeinsam über die nächste Challenge abzustimmen.
*   **Zufällige Effekte:** Eine Challenge, die Spielern in bestimmten Abständen zufällige positive und negative Statuseffekte gibt.
*   **Item-Entzug:** Eine Challenge, die den Spielern unerwartet ein zufälliges Item aus dem Inventar löscht.
*   **Feindliche Spawns:** Eine Challenge, die an zufälligen Orten feindliche Mobs in deiner Nähe spawnen lässt (natürlich immer mit einem schicken Eisenhelm!).