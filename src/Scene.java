
/**
 * Classe simple représentant un fluide dans un carré
 */
public class Scene {

    /**
     * Densité maximale de l'encre
     */
    public float maxDensity = 0.1f;

    /**
     * Densité minimale de l'encre
     */
    public float minDensity = 0;

    /**
     * Nombre d'itérations par calcul
     */
    private int numIter = 10;

    /**
     * Méthode de calcul numérique pour la divergence
     */
    private double OVER_RELAXATION = 1.99;

    // Copie des variables globales
    static private int X_SIZE = AnimationMain.X_SIZE;
    static private int Y_SIZE = AnimationMain.Y_SIZE;
    private float diff = AnimationMain.DIFF;
    private float visc = AnimationMain.VISC;

    /**
     * Obtenir l'indice d'une case dans un tableau unidimensionnel
     * 
     * @param x L'absisce de la case
     * @param y L'ordonnée de la case
     * @return L'indice correspondant à la case
     */
    static public int IX(int x, int y) {
        x = Math.max(0, Math.min(X_SIZE - 1, x));
        y = Math.max(0, Math.min(Y_SIZE - 1, y));
        return x + X_SIZE * y;
    }

    /**
     * Valeur précédente de densité
     */
    public float[] s = new float[X_SIZE * Y_SIZE];
    /**
     * Valeur actuelle de la densité
     */
    public float[] density = new float[X_SIZE * Y_SIZE];

    /**
     * Valeur de la vitesse horizontale
     */
    public float[] u = new float[X_SIZE * Y_SIZE];
    /**
     * Valeur précédente de la vitesse horizontale
     */
    public float[] u0 = new float[X_SIZE * Y_SIZE];

    /**
     * Valeur actuelle de la vitesse verticale
     */
    public float[] v = new float[X_SIZE * Y_SIZE];
    /**
     * Valeur précédente de la vitesse verticale
     */
    public float[] v0 = new float[X_SIZE * Y_SIZE];

    /**
     * Construit une scène où toutes les grandeurs sont nulles
     */
    public Scene() {
        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                s[IX(x, y)] = 0;
                density[IX(x, y)] = 0;
                u[IX(x, y)] = 0;
                u0[IX(x, y)] = 0;
                v[IX(x, y)] = 0;
                v0[IX(x, y)] = 0;
            }
        }
    }

    /**
     * Ajoute un terme source au champ x
     * 
     * @param x      Le champ à actualiser
     * @param source La source à ajouter
     * @param dt     L'intervalle de temps
     */
    public void add_source(float[] x, float source, float dt) {
        int size = (X_SIZE - 1) * (Y_SIZE - 1);
        for (int i = 0; i < size; i++) {
            x[i] += dt * source;
        }

        // Ajout de la vitesse en entrée (turbine)
        int ymin = (int) (Y_SIZE * (0.5 - (float) AnimationMain.LARGEUR / 200));
        int ymax = (int) ((0.5 + (float) AnimationMain.LARGEUR / 200) * Y_SIZE);

        for (int X = 0; X < 2; X++) {
            for (int y = ymin; y < ymax; y++) {
                u[IX(X, y)] = AnimationMain.VITESSE;
                density[IX(X, y)] = 1;
            }
        }
    }

    /**
     * Ajoute du fluide
     * 
     * @param x      L'abscisse
     * @param y      L'ordonnée
     * @param amount La quantité de densité à ajouter
     */
    public void addDensity(int x, int y, float amount) {
        this.density[IX(x, y)] += amount;
    }

    /**
     * Ajoute de la vitesse
     * 
     * @param x       L'abscisse
     * @param y       L'ordonnée
     * @param amountX La quantité horizontale
     * @param amountY La quantité verticale
     */
    public void addVelocity(int x, int y, float amountX, float amountY) {
        this.u[IX(x, y)] += amountX;
        this.v[IX(x, y)] += amountY;
    }

    /**
     * Ligne de haut du profil d'aile mince
     * 
     * @param x Abscisse
     * @return L'ordonnée du haut du profil
     */
    static public double top(double x) {
        return 0.5 / 8.556 * (Math.sqrt(8.556 * x / 2) - 1.1 * Math.pow(8.556 * x / 5, 2));
    }

    /**
     * Ligne de bas du profil d'aile mince
     * 
     * @param x Abscisse
     * @return L'ordonnée du bas du profil
     */
    static public double bottom(double x) {
        return 0.5 / 8.556 * (-Math.sqrt(8.556 * x) + Math.pow(8.556 * x / 3, 2) - 0.7 * Math.pow(8.556 * x / 4.1, 3));
    }

    /**
     * Règle les conditions aux bords pour ne pas faire s'échapper du fluide
     * 
     * @param b Composante horizontale si b=1, verticale si b=2, rien de particulier
     *          sinon
     * @param x Le champ à actualiser
     */
    private void setBoundary(int b, float[] x) {

        // Objet au milieu
        int hauteur = AnimationMain.HAUTEUR;
        int longueur = (int) ((double) hauteur / 0.1106);

        for (int X = 50; X < 50 + longueur; X++) {
            int yxmax = (int) (top((double) (X - 50) * 0.1106 / hauteur) * hauteur / 0.1106);
            int yxmin = (int) (bottom((double) (X - 50) * 0.1106 / hauteur) * hauteur / 0.1106);

            for (int y = -yxmax; y <= -yxmin; y++) {
                u[IX(X, y + Y_SIZE / 2)] = 0;
                v[IX(X, y + Y_SIZE / 2)] = 0;
                density[IX(X, y + Y_SIZE / 2)] = 0;

            }
        }

        // System.out.println("Min : " + minmin);
        // System.out.println("Minmax : " + minmax);

        // Conditions en haut et en bas
        for (int i = 1; i < X_SIZE - 1; i++) {
            x[IX(i, 0)] = b == 2 ? -x[IX(i, 1)] : x[IX(i, 1)];
            x[IX(i, Y_SIZE - 1)] = b == 2 ? -x[IX(i, Y_SIZE - 2)] : x[IX(i, Y_SIZE - 2)];
        }

        // Conditions à droite et à gauche
        for (int j = 1; j < Y_SIZE - 1; j++) {
            x[IX(0, j)] = b == 1 ? -x[IX(1, j)] : x[IX(1, j)];
            x[IX(X_SIZE - 1, j)] = x[IX(X_SIZE - 2, j)];
        }

        // Conditions aux coins
        x[IX(0, 0)] = (x[IX(1, 0)]
                + x[IX(0, 1)]) / 2;

        x[IX(0, Y_SIZE - 1)] = (x[IX(1, Y_SIZE - 1)]
                + x[IX(0, Y_SIZE - 2)]) / 2;

        x[IX(X_SIZE - 1, 0)] = (x[IX(X_SIZE - 2, 0)]
                + x[IX(X_SIZE - 1, 1)]) / 2;

        x[IX(X_SIZE - 1, Y_SIZE - 1)] = (x[IX(X_SIZE - 2, Y_SIZE - 1)]
                + x[IX(X_SIZE - 1, Y_SIZE - 2)]) / 2;
    }

    /**
     * Solveur linéaire, qui interpole la valeur en un point à partir de ses voisins
     * 
     * @param b    La donnée pour les bords
     * @param x    Le champ actuel à actualiser
     * @param x0   Le champ à l'instant précédent
     * @param a    Le paramètre de résolution dans la méthode de Poisson
     * @param c    =1+4a en 2D et 1+6a en 3D (le coefficient devant a correpond au
     *             nombre de voisins)
     * @param iter Nombre d'itérations de l'algorithme
     */
    private void lin_solve(int b, float[] x, float[] x0, float a, float c, int iter) {

        for (int k = 0; k < iter; k++) {
            for (int j = 1; j < Y_SIZE - 1; j++) {
                for (int i = 1; i < X_SIZE - 1; i++) {
                    x[IX(i, j)] = (x0[IX(i, j)]
                            + a * (x[IX(i + 1, j)]
                                    + x[IX(i - 1, j)]
                                    + x[IX(i, j + 1)]
                                    + x[IX(i, j - 1)]))
                            / c;
                }
            }
            setBoundary(b, x);
        }

    }

    /**
     * Diffuse un champ à ses voisin selon l'algorithme de Poisson
     * 
     * @param b    Paramètre pour les bords
     * @param x    Le champ actuel à actualiser
     * @param x0   Le champ à l'instant précédent
     * @param diff Le facteur de diffusion
     * @param dt   Le delta temporel
     * @param iter Le nombre d'itérations
     */
    private void diffuse(int b, float[] x, float[] x0, float diff, float dt, int iter) {

        float a = dt * diff * (X_SIZE) * (Y_SIZE);
        lin_solve(b, x, x0, a, 1 + 4 * a, iter);
    }

    /**
     * Méthode qui modifie les valeurs de vitesse pour avoir un fluide
     * incompressible
     * 
     * @param velocX La vitesse horizontale
     * @param velocY La vitesse verticale
     * @param p      La pression
     * @param div    La divergence
     * @param iter   Le nombre d'iterations
     */
    private void project(float[] velocX, float[] velocY, float[] p, float[] div, int iter) {
        // Calcul des divergences en tout point

        for (int j = 1; j < Y_SIZE - 1; j++) {
            for (int i = 1; i < X_SIZE - 1; i++) {
                div[IX(i, j)] = -0.5f * (velocX[IX(i + 1, j)]
                        - velocX[IX(i - 1, j)]
                        + velocY[IX(i, j + 1)]
                        - velocY[IX(i, j - 1)]) / X_SIZE;
                div[IX(i, j)] *= OVER_RELAXATION;
                p[IX(i, j)] = 0;
            }
        }

        // Calcul des pressions en tout point
        setBoundary(0, div);
        setBoundary(0, p);
        lin_solve(0, p, div, 1, 4, iter);

        // Actualisation des vitesses pour avoir une divergence nulle
        for (int j = 1; j < Y_SIZE - 1; j++) {
            for (int i = 1; i < X_SIZE - 1; i++) {
                velocX[IX(i, j)] -= 0.5f * (p[IX(i + 1, j)]
                        - p[IX(i - 1, j)]) * X_SIZE;
                velocY[IX(i, j)] -= 0.5f * (p[IX(i, j + 1)]
                        - p[IX(i, j - 1)]) * Y_SIZE;
            }
        }

        setBoundary(1, velocX);
        setBoundary(2, velocY);

    }

    /**
     * Projette un champ selon les vecteurs vitesse (déplacement)
     * 
     * @param b      Le paramètre pour les bords
     * @param d      Le champ actuel
     * @param d0     Le champ précédent
     * @param velocX La vitesse horizontale
     * @param velocY La vitesse verticale
     * @param dt     L'intervalle de temps
     */
    private void advect(int b, float[] d, float[] d0, float[] velocX, float[] velocY, float dt) {
        float i0, i1, j0, j1;

        float dtx = dt * (X_SIZE - 1);
        float dty = dt * (Y_SIZE - 1);

        float s0, s1, t0, t1;
        float tmp1, tmp2, x, y;

        float Nfloat = Math.max(X_SIZE, Y_SIZE);
        float ifloat = 1, jfloat = 1;
        int i, j;

        for (j = 1, jfloat = 1; j < Y_SIZE - 1; j++, jfloat++) {
            for (i = 1, ifloat = 1; i < X_SIZE - 1; i++, ifloat++) {
                // On interpole la vitesse à la position précédente par une approche
                // semi-lagrangienne avec des gros calculs savants
                tmp1 = dtx * velocX[IX(i, j)];
                tmp2 = dty * velocY[IX(i, j)];
                x = ifloat - tmp1;
                y = jfloat - tmp2;

                if (x < 0.5f) {
                    x = 0.5f;
                }
                if (x > Nfloat + 0.5f) {
                    x = Nfloat + 0.5f;
                }
                i0 = (int) (x);
                i1 = i0 + 1;

                if (y < 0.5f) {
                    y = 0.5f;
                }
                if (y > Nfloat + 0.5f) {
                    y = Nfloat + 0.5f;
                }
                j0 = (int) (y);
                j1 = j0 + 1;

                s1 = x - i0;
                s0 = 1.0f - s1;
                t1 = y - j0;
                t0 = 1.0f - t1;

                int i0i = (int) i0;
                int i1i = (int) i1;
                int j0i = (int) j0;
                int j1i = (int) j1;

                // On actualise le champ en question selon ses valeurs précédentes
                d[IX(i, j)] = s0 * (t0 * d0[IX(i0i, j0i)] + t1 * d0[IX(i0i, j1i)]) +
                        s1 * (t0 * d0[IX(i1i, j0i)] + t1 * d0[IX(i1i, j1i)]);
            }
        }

        setBoundary(b, d);
    }

    /**
     * Fonction de mise à jour de la scène
     * 
     * @param dt L'intervalle de temps
     */
    public void update(float dt) {
        // Ajout de la gravité et du fluide
        add_source(v, AnimationMain.GRAVITE, dt);

        // Diffusion des vitesses selon elles-mêmes
        diffuse(1, u0, u, visc, dt, numIter);
        diffuse(2, v0, v, visc, dt, numIter);

        // Annulation de la divergence
        project(u0, v0, u, v, numIter);

        // Déplacement du champ de vitesse selon lui-même
        advect(1, u, u0, u0, v0, dt);
        advect(2, v, v0, u0, v0, dt);

        // Annulation de la divergence résulante
        project(u, v, u0, v0, numIter);

        // Diffusion et advection de la densité
        diffuse(0, s, density, diff, dt, numIter);
        advect(0, density, s, u, v, dt);
    }

    /**
     * Fonction qui met à jour les valeurs max et min de la densité pour l'affichage
     */
    public void updateMaxMin() {
        minDensity = 0;
        maxDensity = 0;
        for (int x = 10; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                if (density[IX(x, y)] > maxDensity) {
                    maxDensity = density[IX(x, y)];
                }
                if (density[IX(x, y)] < minDensity) {
                    minDensity = density[IX(x, y)];
                }
            }
        }

        maxDensity = (float) Math.max(1E-10, maxDensity);
    }
}
