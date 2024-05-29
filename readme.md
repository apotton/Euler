# Simulation eulérienne de fluides

## Introduction

Ce projet est une simulation de fluide prenant une approche eulérienne. Cela signifie que chaque case fixe de la simulation contient différentes valeurs, telles que la vitesse et la densité (d'encre qui correspond à l'affichage). Les variables importantes se trouvent au début du fichier `AnimationMain.java`. Parmi elles, la vitesse de l'écoulement, la taille de la simulation, les propriétés cinématiques du fluide et la taille du profil d'aile mince.

La simulation présente un écoulement entrant par la gauche, arrivant sur un profil d'aile mince dont l'équation a été établie empiriquement. Les couleurs correspondent à la présence ou non d'"encre" à cet endroit de l'affichage. L'encre en question est injectée à l'origine de l'écoulement. Le mouvement naturel du fluide conduit cette encre à se diffuser le long du profil et donc de colorer l'affichage.

## Fonctionnement du code

A chaque itération, le programme fait tourner la fonction `update(dt)`. Celle-ci commence par ajouter le champ de gravité, l'encre ainsi que la vitesse d'entrée au fluide.

Ensuite, il s'opère le processus de diffusion de la vitesse. Lorsqu'une particule possède une certaine vitesse, les interactions avec ses voisins font que cette vitesse se transmet aux cases voisines, par viscosité. Ici intervient la constante `VISC`.
