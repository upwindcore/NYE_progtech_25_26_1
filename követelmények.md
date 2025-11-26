## Projekt követelmények

* A repository tartalmaz egy megfelelő .gitignore fájlt annak érdekébe, hogy IDE vagy Maven specifikus ideiglenes fájlok ne kerüljenek fel a repository-ba
* Egy Java 21-es Maven projekt létrehozása (pom.xml és Maven folder struktúra)
* A Maven projekt az alábbi konfigurációkat tartalmazza:
    * Plugin-ok:
        * org.apache.maven.plugins.maven-jar-plugin - annak érdekében, hogy felkonfiguráljuk az alkalmazásunk belépési pontját (Main Class)
        * org.apache.maven.plugins.maven-assembly-plugin - annak érdekében, hogy egy függőségeket tartalmazó, futtatható JAR fájl jöjjön létre az alkalmazás build-elése eredményeként
        * org.jacoco.jacoco-maven-plugin - annak érdekében, hogy a megírt Egység tesztek kód lefedettségét tudjuk mérni
        * org.apache.maven.plugins.maven-checkstyle-plugin - annak érdekében, hogy a projekten elkövetett kód formázási hibákat és egyéb rossz praktikák automatikus detektáljunk
    * Függőségek:
        * JUnit5
        * Mockito
        * Logback
* Az alkalmazás objektum-orientált modellezésének megkezdése:
    * Az alkalmazásunkhoz szükséges VO (Value Object) osztályok létrehozása (ügyelve és figyelembe véve a "best practice"-eket: Object methods overriding, Immutability, stb.)
* Az induláskor egy szövegfájlból beolvas egy  játékállást, ha nincs meg az input fájl, akkor üres pályáról indulunk
* Az alkalmazás képes kezdetleges felhasználói interakciókat fogadni:
    * egy szövegfájlból betölteni egy pályát
    * egy szövegfájlba kiírni egy pályát
    * Például: Játékos nevének bekérése, Játék elindítása, a játéktér kiiratása, Egy lépés fogadása a parancssoron, a lépés vizsgálata abból a szempontból, hogy alkalmazható-e; a lépés alkalmazása és az eredmény kiírása, stb
    * Itt nem határozunk meg kötelező elvárásokat, tetszőleges kezdetleges interakciók elegendőek
    * Egység tesztek 80% lefedettséget biztosítanak üzleti logikát tartalmazó osztályokra (tehát például VO osztályokra nem szükséges egységteszteket írni)
    * A teljes játék funkcionalitás lefejlesztésre került (lehetséges egy játékot végig játszani elejétől a végéig)
    * A projekt a `mvn clean install` parancs futtatására hiba nélkül fordul
    * Az alkalmazás egy adatbázisba lementi a játékosok nevét és azt, hogy hányszor nyertek
        * Az alkalmazás képes megjeleníteni parancssorban egy high score táblázatot (melyik játékos hány meccset nyert)
    * Opcionális: egy aktuálisan folyamatban lévő játék állást az alkalmazás képes egy XML fájlba kimenteni és később visszatölteni (tehát a játékos onnan folytathatja a játékot, ahol korábban abba hagyta)
    * Egység tesztek továbbra is 80% lefedettséget biztosítanak üzleti logikát tartalmazó osztályokra

