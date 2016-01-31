# Vann - Seibert Media Casino Pokerbott

### Abhängigkeiten

* Leiningen https://github.com/technomancy/leiningen
* Java 8

## Benutzung

### Java / Clojure Version

Direkt mittels Leiningen

    lein run URL TISCH BENUTZERNAME PASSWORD
    
Mittels Standalone-Jar

    java -jar vann-standalone.jar URL TISCH BENUTZERNAME PASSWORD
    
Beispiel

    java -jar vann-standalone.jar ws://localhost:8080 2af03e1aXf0f7X2bd8X00f2X81c34cbb5abc Vann secret

#### Kompilieren

Um das Standalone-Jar zu kompilieren

    lein uberjar
    cp target/vann-*-standalone.jar vann-standalone.jar

    
### Tests

    lein test
    

### Javascript Version

    lein compile
    firefox target-js/index.html
    
Danach die Datei ``target-js/index.html`` im Browser ausführen und das Formular ausfüllen. Die Ausgaben erfolgen in der Browser Konsole.

## Verzeichnisstruktur

* ``resources-cljs`` Statische Dateien für die Javascript Version, werden nach ``target-js`` kopiert
* ``resources-cljs/Vannbot.js`` Implementierung des Bot Interfaces für die direkte Casino Integration, dabei wird die Websocket Implementierung der Basisklasse verwendet und nicht die aus ``core.cljs``.
* ``src-clj`` Main Funktion und Websocket Anbindung der Java bzw. Clojure Version
* ``src-cljs`` Main Funktion und Websocket Anbindung der Standalone Javascript bzw. Clojurescript Version
* ``src-common`` Von Clojure und Clojurescript geteilter Code. Unter anderem die Spiellogik in ``game.cljc``
* ``target`` Clojure Output (``.class`` Dateien)
* ``target-js`` Javascript Output (Komprimierte ``.js`` Dateien)
* ``test`` Tests _\\o/_

## Referenzen

* Clojure http://clojure.org/
* Clojure Ausprobieren http://www.tryclj.com/
* Leiningen https://github.com/technomancy/leiningen
* Clojurescript https://github.com/clojure/clojurescript/wiki/Quick-Start
* Cljsbuild https://github.com/emezeske/lein-cljsbuild
* Reader Conditional http://dev.clojure.org/display/design/Reader+Conditionals

## TODO

* nodejs Version
    * http://www.mase.io/code/clojure/node/2015/01/24/getting-started-with-clojurecript-and-node/



## License

Copyright © 2015 Benjamin Peter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
