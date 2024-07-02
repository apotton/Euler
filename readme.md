# Simulation eulérienne de fluides 🌊✈️

## Introduction

Ce projet est une simulation de fluide prenant une approche eulérienne. Cela signifie que chaque case fixe de la simulation contient différentes propriétés du fluide à cet endroit, telles que la vitesse ou la pression. Les variables importantes se trouvent au début du fichier `AnimationMain.java`. Parmi elles, la vitesse de l'écoulement, la taille de la simulation, les propriétés cinématiques du fluide et la taille de l'obstacle au milieu.

La simulation présente un écoulement entrant par la gauche, arrivant sur un obtacle au choix (cercle, carré ou profil d'aile mince). Les couleurs correspondent à la présence ou non d'"encre" à cet endroit de l'affichage. L'encre en question est injectée à l'origine de l'écoulement. Le mouvement naturel du fluide conduit cette encre à se diffuser le long du profil et donc de colorer l'affichage.

## Fonctionnement du code

A chaque itération, le programme fait tourner la fonction `update(dt)`. Celle-ci applique différents procédés au fluide, qui sont exxpriqués ci-dessous

### `appliquerForcesExterieures`

Cette méthode applique des forces au fluide, selon la formule F=ma, qui donne v+=dt\*F. Seule la gravité est prise en compte.

### `advection`

L'advection est le phénomène de transport des propriétés du fluide selon le champ de vitesse. Dans l'approche lagragienne, une particule présente à un endroit donné à un instant t aura changé d'emplacement à l'instant t+dt. Ainsi, cette méthode calcule pour chaque case son emplacement précédent, et interpole les propriétés de l'écoulement à cet endroit.

### `diffusion`

La diffusion est le phénomène d'échange de propriétés entre deux positions voisines. A chaque instant, les vitesse et la pression d'une case se 'diluent' dans les cases environnantes. Le calcul est répété plusieurs fois afin d'avoir un effet moyenneur convaincant. On peut penser pour illustrer celà à une goutte d'encre qui se répand dans un liquide.

### `calculPression`

Cette méthode s'exécute en trois étapes. D'abord, la divergence est calculée pour chaque case à l'aide des forces de pressions dans les cases voisines (haut, bas, droite, gauche). Ensuite, des nouvelles pressions sont calculées à partir de cette divergence par un processus itératif. Enfin, les résultantes sont appliquées au champ de vitesse avec un facteur correctif (la grandeur `VISC`).

### `majMinMax`

Cette dernière méthode calcule la norme de la vitesse pour chaque case, et stocke les valeurs minimales et maximales. Celles-ci sont utilisées dans la classe `AnimationMain` pour le choix des couleurs de la visualitation.
