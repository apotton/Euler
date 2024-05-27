# Simulation eulérienne de fluides

## Introduction

Ce projet est une simulation de fluide prenant une approche eulérienne. Cela signifie que chaque case fixe de la simulation contient différentes valeurs, telles que la vitesse et la densité (d'encre qui correspond à l'affichage). Les variables importantes se trouvent au début du fichier `AnimationMain.java`. Parmi elles, la vitesse de l'écoulement, la taille de la simulation, les propriétés cinématiques du fluide et la taille du profil d'aile mince.

La simulation présente un écoulement entrant par la gauche, arrivant sur un profil d'aile mince dont l'équation a été établie empiriquement. Les couleurs correspondent à la présence ou non d'"encre" à cet endroit de l'affichage. L'encre en question est injectée à l'origine de l'écoulement. Le mouvement naturel
