# NYE_progtech_25_26_1
Az Amőba terminál játék megvalósítása Java 21 és Maven használatával. Ez a projekt a Programozási technológiák tantárgyhoz készült.

## Játék szabályok

* Az amőba kétszemélyes stratégiai táblajáték, mely egy db NxM-es (N és M pozitiv egész szám, 5 <=M <= N <=25), tipikusan 10x10-as táblán játszható. N -- sorok, M -- oszlopok száma.
* Az oszlopok számozása tipikusan a,b,c, ... betűkkel történik, a soroké 1,2,3,..,N sorszámokkal -- de ettől nem függ a játékprogram, más megnevezés is használható.
* Induláskor a tábla üres. 
* A két játékos közül az egyik az "x", a másik a "o" jelet vezeti. Az "x" lesz a humán játékosé, a "o" a gépi játékosé. Az "x" játékos, a humán kezd.
* A játékosok minden körben egy-egy, még nem elfoglalt helyre tehetik a jelüket, de úgy, hogy a lerakott jel legalább diagonálisan érintkezzen a már fennlévőkkel. A kezdő jel a tábla egyik középső mezőjére menjen. 
* A gépi ellenfél csak random generál egy lehetséges lépést, mindegyiket egyenlő valószínűséggel.
* Az a játékos nyer, amelyik függőlegesen, vízszintesen, vagy átlósan kirakott négyet a jeléből.