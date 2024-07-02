public class Scene {
    // Taille de la grille (pour ne pas avoir à allonger le code)
    private static final int X_SIZE = AnimationMain.X_SIZE;
    private static final int Y_SIZE = AnimationMain.Y_SIZE;

    /** Tableau des cases contenant les propriétés spatiales */
    public Case[][] grille = new Case[X_SIZE][Y_SIZE];

    /** Stockage temporaire de la vitesse horizontale */
    private double[][] nouvelleVitesseHorizontale = new double[X_SIZE][Y_SIZE];

    /** Sotckage temporaire de la vitesse verticale */
    private double[][] nouvelleVitesseVerticale = new double[X_SIZE][Y_SIZE];

    /** Stockage temporaire de la pression */
    private double[][] nouvellePression = new double[X_SIZE][Y_SIZE];

    /**
     * Tableau indiquant si une case voisine est solide
     * Pour chaque case, on a dans l'ordre la guauche, la
     * droite, le bas, le haut et le nombre de voisins non solides
     */
    private int[][][] estSolide = new int[X_SIZE][Y_SIZE][5];

    /** Tableau de booléens représentant un obstacle à l'écoulement */
    public boolean[][] solide = new boolean[X_SIZE][Y_SIZE];

    // Valeurs extrémales de vitesse
    public double min = 0;
    public double max = AnimationMain.VITESSE;

    /*
     * Constructeur d'une scène
     */
    public Scene() {
        // Initialisation de chaque case
        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                grille[x][y] = new Case();
            }
        }

        // Initialisation de l'obstacle solide
        initialiserObstacles();
        initialiserVoisins();
    }

    /**
     * Initialise le tableau des voisins de chaque case
     */
    void initialiserVoisins() {
        for (int x = 1; x < X_SIZE - 1; x++) {
            for (int y = 1; y < Y_SIZE - 1; y++) {
                estSolide[x][y][0] = solide[x - 1][y] ? 0 : 1; // Gauche
                estSolide[x][y][1] = solide[x + 1][y] ? 0 : 1; // Droite
                estSolide[x][y][2] = solide[x][y - 1] ? 0 : 1; // Bas
                estSolide[x][y][3] = solide[x][y + 1] ? 0 : 1; // Haut

                int nombreVoisins = 0;
                for (int i = 0; i < 4; i++) {
                    nombreVoisins += estSolide[x][y][i];
                }

                // Nombre de voisins non solides
                estSolide[x][y][4] = nombreVoisins;
            }
        }

    }

    /**
     * Initialisation du tableau solide avec un obstacle
     */
    private void initialiserObstacles() {
        if (AnimationMain.CERCLE)
            cercle();
        if (AnimationMain.AILE)
            aileMince();
        if (AnimationMain.CARRE)
            carre();
    }

    /** Place un cercle dans l'écoulement */
    private void cercle() {
        int centreX = X_SIZE / 8;
        int centreY = Y_SIZE / 2;
        int rayon = AnimationMain.HAUTEUR / 2;

        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                if (Math.sqrt(Math.pow(x - centreX, 2) + Math.pow(y - centreY, 2)) <= rayon) {
                    solide[x][y] = true;
                }
            }
        }
    }

    /**
     * Ligne de haut du profil d'aile mince
     * 
     * @param x Abscisse
     * @return L'ordonnée du haut du profil
     */
    static public double haut(double x) {
        return 0.5 / 8.556 * (Math.sqrt(8.556 * x / 2) - 1.1 * Math.pow(8.556 * x / 5, 2));
    }

    /**
     * Ligne de bas du profil d'aile mince
     * 
     * @param x Abscisse
     * @return L'ordonnée du bas du profil
     */
    static public double bas(double x) {
        return 0.5 / 8.556 * (-Math.sqrt(8.556 * x) + Math.pow(8.556 * x / 3, 2) - 0.7 * Math.pow(8.556 * x / 4.1, 3));
    }

    /**
     * Place une aile mince dans l'écoulement
     */
    private void aileMince() {
        int hauteur = AnimationMain.HAUTEUR;
        int longueur = (int) ((double) hauteur / 0.1106);

        for (int X = 50; X < 50 + longueur; X++) {
            int yxmax = (int) (haut((double) (X - 50) * 0.1106 / hauteur) * hauteur /
                    0.1106);
            int yxmin = (int) (bas((double) (X - 50) * 0.1106 / hauteur) * hauteur /
                    0.1106);

            for (int y = -yxmax; y <= -yxmin; y++) {
                solide[X][y + Y_SIZE / 2] = true;

            }
        }
    }

    /** Place un carré dans l'écoulement */
    private void carre() {
        int debut = X_SIZE / 8;

        for (int y = (Y_SIZE - AnimationMain.HAUTEUR) / 2; y < (Y_SIZE + AnimationMain.HAUTEUR) / 2; y++) {
            for (int x = debut; x < AnimationMain.HAUTEUR + debut; x++) {
                solide[x][y] = true;
            }
        }
    }

    /**
     * Définir les conditions aux limites pour la simulation de fluide
     */
    public void imposerLimites() {
        for (int y = 0; y < Y_SIZE; y++) {
            // Limite gauche (flux incident)
            grille[0][y].vitesseHorizontale = AnimationMain.VITESSE;
            grille[0][y].vitesseVerticale = 0.0;

            // Limite droite (le fluide peut s'échapper)
            grille[X_SIZE - 1][y].vitesseHorizontale = grille[X_SIZE - 2][y].vitesseHorizontale;
            grille[X_SIZE - 1][y].vitesseVerticale = grille[X_SIZE - 2][y].vitesseVerticale;
        }

        // Limites supérieure et inférieure (le fluide ne peut pas s'échapper)
        for (int x = 1; x < X_SIZE; x++) {
            // Limite supérieure
            grille[x][0].vitesseVerticale = 0.0;
            grille[x][0].vitesseHorizontale = grille[x][1].vitesseHorizontale;

            // Limite inférieure
            grille[x][Y_SIZE - 1].vitesseVerticale = 0.0;
            grille[x][Y_SIZE - 1].vitesseHorizontale = grille[x][Y_SIZE - 2].vitesseHorizontale;
        }
    }

    /**
     * Application des forces extérieures au fluide (gravité)
     * 
     * @param dt Intervalle de temps
     */
    private void appliquerForcesExterieures(double dt) {
        for (int x = 1; x < X_SIZE - 1; x++) {
            for (int y = 1; y < Y_SIZE - 1; y++) {
                grille[x][y].vitesseVerticale += AnimationMain.GRAVITE * dt;
            }
        }
    }

    /**
     * Projette un champ selon les vecteurs vitesse (déplacement)
     * 
     * @param dt Intervalle de temps
     */
    public void advection(double dt) {

        for (int x = 1; x < X_SIZE - 1; x++) {
            for (int y = 1; y < Y_SIZE - 1; y++) {

                if (solide[x][y]) {
                    // La vitesse dans le solide est nulle
                    grille[x][y].vitesseHorizontale = 0;
                    grille[x][y].vitesseVerticale = 0;
                    continue;
                }

                // Calcul de la position d'origine du fluide
                double ancienX = x - dt * grille[x][y].vitesseHorizontale;
                double ancienY = y - dt * grille[x][y].vitesseVerticale;

                // Recalage des coordonnées dans la grille
                ancienX = Math.max(0.5, Math.min(X_SIZE - 1.5, ancienX));
                ancienY = Math.max(0.5, Math.min(Y_SIZE - 1.5, ancienY));

                // Calcul des indices des cases environnantes
                int x0 = (int) ancienX;
                int y0 = (int) ancienY;
                int x1 = x0 + 1;
                int y1 = y0 + 1;

                // Calcul des facteurs d'interpolation
                double sx1 = ancienX - x0;
                double sy1 = ancienY - y0;
                double sx0 = 1 - sx1;
                double sy0 = 1 - sy1;

                // Interpolation de la pression
                nouvellePression[x][y] = sx0 * (sy0 * grille[x0][y0].pression + sy1 * grille[x0][y1].pression) +
                        sx1 * (sy0 * grille[x1][y0].pression + sy1 * grille[x1][y1].pression);

                // Interpolation de la vitesse horizontale
                nouvelleVitesseHorizontale[x][y] = sx0
                        * (sy0 * grille[x0][y0].vitesseHorizontale + sy1 * grille[x0][y1].vitesseHorizontale) +
                        sx1 * (sy0 * grille[x1][y0].vitesseHorizontale + sy1 * grille[x1][y1].vitesseHorizontale);

                // Interpolation de la vitesse verticale
                nouvelleVitesseVerticale[x][y] = sx0
                        * (sy0 * grille[x0][y0].vitesseVerticale + sy1 * grille[x0][y1].vitesseVerticale) +
                        sx1 * (sy0 * grille[x1][y0].vitesseVerticale + sy1 * grille[x1][y1].vitesseVerticale);
            }
        }

        // Transfert du buffer à la grille
        for (int x = 1; x < X_SIZE - 1; x++) {
            for (int y = 1; y < Y_SIZE - 1; y++) {
                grille[x][y].pression = nouvellePression[x][y];
                grille[x][y].vitesseHorizontale = nouvelleVitesseHorizontale[x][y];
                grille[x][y].vitesseVerticale = nouvelleVitesseVerticale[x][y];
            }
        }

        // Application des conditions aux limites
        imposerLimites();
    }

    /**
     * Diffusion des propriétés du fluides d'une case vers ses voisines
     * 
     * @param dt L'intervalle de temps
     */
    public void diffusion(double dt) {
        // Relaxation de Gauss-Seidel
        for (int k = 0; k < AnimationMain.ITER; k++) {
            for (int x = 1; x < X_SIZE - 1; x++) {
                for (int y = 1; y < Y_SIZE - 1; y++) {
                    // On ne calcule rien si on est dans le solide
                    if (solide[x][y])
                        continue;

                    // Calul de la vitesse horizontale
                    nouvelleVitesseHorizontale[x][y] = (grille[x][y].vitesseHorizontale +
                            dt * (grille[x - 1][y].vitesseHorizontale + grille[x + 1][y].vitesseHorizontale +
                                    grille[x][y - 1].vitesseHorizontale + grille[x][y + 1].vitesseHorizontale))
                            / (1 + 4 * dt);

                    // Calul de la vitesse verticale
                    nouvelleVitesseVerticale[x][y] = (grille[x][y].vitesseVerticale +
                            dt * (grille[x - 1][y].vitesseVerticale + grille[x + 1][y].vitesseVerticale +
                                    grille[x][y - 1].vitesseVerticale + grille[x][y + 1].vitesseVerticale))
                            / (1 + 4 * dt);

                    // Calcul de la pression
                    nouvellePression[x][y] = (grille[x][y].pression +
                            dt * (grille[x - 1][y].pression + grille[x + 1][y].pression +
                                    grille[x][y - 1].pression + grille[x][y + 1].pression))
                            / (1 + 4 * dt);
                }
            }

            // Mise à jour des valeurs de la grille
            for (int x = 1; x < X_SIZE - 1; x++) {
                for (int y = 1; y < Y_SIZE - 1; y++) {
                    grille[x][y].vitesseHorizontale = nouvelleVitesseHorizontale[x][y];
                    grille[x][y].vitesseVerticale = nouvelleVitesseVerticale[x][y];
                    grille[x][y].pression = nouvellePression[x][y];
                }
            }
        }

        // Application des conditions aux limites
        imposerLimites();
    }

    /**
     * Calcul des forces de pression en fonction de la divergence
     * 
     * @param dt Intervalle de temps
     */
    public void calculPression(double dt) {
        double h = 1.0 / Math.min(X_SIZE, Y_SIZE);
        double factor = dt / (AnimationMain.DENSITE * h * h);

        // Calcul de la divergence
        for (int x = 1; x < X_SIZE - 1; x++) {
            for (int y = 1; y < Y_SIZE - 1; y++) {
                if (!solide[x][y]) {
                    grille[x][y].divergence = -0.5 * h
                            * (grille[x + 1][y].vitesseHorizontale - grille[x - 1][y].vitesseHorizontale +
                                    grille[x][y + 1].vitesseVerticale - grille[x][y - 1].vitesseVerticale);
                } else {
                    grille[x][y].divergence = 0;
                }
            }
        }

        // Initialisation de la pression
        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                grille[x][y].pression = 0;
            }
        }

        // Calcul de la pression par relaxation de Gauss-Seidel
        for (int k = 0; k < AnimationMain.ITER; k++) {
            for (int x = 1; x < X_SIZE - 1; x++) {
                for (int y = 1; y < Y_SIZE - 1; y++) {
                    int solid = solide[x][y] ? 0 : 1;
                    double sumPressure = 0;

                    sumPressure = estSolide[x][y][0] * grille[x - 1][y].pression
                            + estSolide[x][y][1] * grille[x + 1][y].pression
                            + estSolide[x][y][2] * grille[x][y - 1].pression
                            + estSolide[x][y][3] * grille[x][y + 1].pression;

                    if (estSolide[x][y][4] > 0) {
                        nouvellePression[x][y] = (sumPressure + solid * factor * grille[x][y].divergence)
                                / estSolide[x][y][4];
                    } else {
                        nouvellePression[x][y] = 0;
                    }
                }
            }
            for (int x = 1; x < X_SIZE - 1; x++) {
                for (int y = 1; y < Y_SIZE - 1; y++) {
                    grille[x][y].pression = nouvellePression[x][y];
                }
            }
        }

        // Application de la pression à la vitesse
        for (int x = 1; x < X_SIZE - 1; x++) {
            for (int y = 1; y < Y_SIZE - 1; y++) {
                if (!solide[x][y]) {
                    // Calcul du gradient de pression
                    double gradientPressionX = (grille[x + 1][y].pression - grille[x - 1][y].pression) / (2 * h);
                    double gradientPressionY = (grille[x][y + 1].pression - grille[x][y - 1].pression) / (2 * h);

                    // Application des forces
                    grille[x][y].vitesseHorizontale -= dt * gradientPressionX / AnimationMain.DENSITE;
                    grille[x][y].vitesseVerticale -= dt * gradientPressionY / AnimationMain.DENSITE;
                } else {
                    // Vitesse nulle à l'intérieur du solide
                    grille[x][y].vitesseHorizontale = 0;
                    grille[x][y].vitesseVerticale = 0;
                }
            }
        }

        // Application des conditions aux limites
        imposerLimites();
    }

    /**
     * Gestion des interactions entre le fluide et l'obstacle
     */
    private void interactionsSolide() {
        for (int x = 1; x < X_SIZE - 1; x++) {
            for (int y = 1; y < Y_SIZE - 1; y++) {
                if (solide[x][y]) {
                    // Inversion des vitesses au bord du solide
                    if (!solide[x - 1][y])
                        grille[x - 1][y].vitesseHorizontale = -grille[x - 1][y].vitesseHorizontale;
                    if (!solide[x + 1][y])
                        grille[x + 1][y].vitesseHorizontale = -grille[x + 1][y].vitesseHorizontale;
                    if (!solide[x][y - 1])
                        grille[x][y - 1].vitesseVerticale = -grille[x][y - 1].vitesseVerticale;
                    if (!solide[x][y + 1])
                        grille[x][y + 1].vitesseVerticale = -grille[x][y + 1].vitesseVerticale;
                }
            }
        }
    }

    /**
     * Mise à jour des valeurs extrémales de vitesse pour l'affichage
     */
    private void majMinMax() {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;

        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                // Calcul de la norme de la vitesse pour chaque case
                grille[x][y].vitesse = Math.sqrt(grille[x][y].vitesseHorizontale * grille[x][y].vitesseHorizontale +
                        grille[x][y].vitesseVerticale * grille[x][y].vitesseVerticale);

                if (!solide[x][y]) {
                    min = Math.min(min, grille[x][y].vitesse);
                    max = Math.max(max, grille[x][y].vitesse);
                }
            }
        }
    }

    /**
     * Changement des propriétés du fluide
     * 
     * @param dt
     */
    public void update(double dt) {
        // Etape 1: Apply external forces (if any)
        appliquerForcesExterieures(dt);

        // Etape 2: Advection
        advection(dt);

        // Etape 3: Diffusion
        diffusion(dt);

        // Etape 4: Calcul de la pression
        calculPression(dt);

        // Etape 5: Gestion des interactions avec le solide
        interactionsSolide();

        // Etape 6: Application des conditions aux limites
        imposerLimites();

        // Etape 7: Mise à jour des valeurs extrémales
        majMinMax();
    }

}