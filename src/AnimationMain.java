import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimationMain extends JPanel implements ActionListener {
    /// Paramètres de la simulation
    static final int X_SIZE = 400; // Largeur de la fenêtre
    static final int Y_SIZE = 100; // Hauteur de la fenêtre
    static final int Y_OFFSET = 37; // Offset en Y pour l'affichage
    static final int ESPACEMENT = 3; // Nombre de pixels par carré
    static final int ITER = 30; // Nombre d'itérations par calcul

    // Paramètres de l'écoulement
    static final int GRAVITE = 0; // Force de gravité qui s'applique au fluide
    static final double DENSITE = 0.7; // Densité du fluide

    static final int VITESSE = 900; // Vitesse turbine en entrée d'écoulement
    static final int HAUTEUR = 15; // Hauteur de l'objet dans l'écoulement

    // Objets au milieu
    static final boolean CERCLE = true;
    static final boolean AILE = false;
    static final boolean CARRE = false;

    Timer timer;
    static public Scene scene = new Scene();
    static int compteur;
    public long temps = System.nanoTime();
    double fps = temps;

    /**
     * Affichage de l'écoulement
     * 
     * @param g Scène contenant des objets et des couleurs
     */
    void paint_scene(Graphics g) {
        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {

                // Détermination de la couleur correspondant à la densité au point (x,y)
                Color coul;
                if (scene.solide[x][y]) {
                    coul = Color.BLACK;
                } else {
                    coul = getSciColor(scene.grille[x][y].vitesse, 0, scene.max);
                }

                // Coloration d'une case
                g.setColor(coul);
                g.fillRect(ESPACEMENT * (x), ESPACEMENT * (y), ESPACEMENT, ESPACEMENT);
            }
        }

        // Affichage des stats
        g.setColor(Color.BLACK);
        g.drawString(Double.toString(Double.valueOf(String.valueOf(((int) (fps * 1000)))) / 1000), 0, 10);
    }

    /**
     * Fonction qui détermine l'échelle de couleur à utiliser
     * 
     * @param val    Valeur du champ au point
     * @param minVal Valeur minimale du champ
     * @param maxVal Valeur maximale du champ
     * @return La couleur idéale
     */
    Color getSciColor(double value, double minVal, double maxVal) {
        // Normaliser la valeur
        double valeurNormee = 1 - (Math.min(Math.max(value, minVal), maxVal) - minVal) / (maxVal - minVal);

        // Déterminer le segment de la couleur
        int segment = (int) Math.floor(valeurNormee * 4);
        double offsetSegment = (valeurNormee * 4) - segment;
        double rouge = 0, vert = 0, bleu = 1;

        // Déterminer la couleur en fonction du segment
        switch (segment) {
            case 0:
                rouge = 0.0;
                vert = offsetSegment;
                bleu = 1.0;
                break;
            case 1:
                rouge = 0.0;
                vert = 1.0;
                bleu = 1.0 - offsetSegment;
                break;
            case 2:
                rouge = offsetSegment;
                vert = 1.0;
                bleu = 0.0;
                break;
            case 3:
                rouge = 1.0;
                vert = 1.0 - offsetSegment;
                bleu = 0.0;
                break;
        }

        // Convertir les nombres en couleur
        return new Color((int) (255 * rouge), (int) (255 * vert), (int) (255 *
                bleu));

        // // Noir et blanc
        // return new Color((int) (255 * valeurNormee), (int) (255 *
        // valeurNormee), (int) (255 * valeurNormee));
    }

    /**
     * Initialisation du minuteur
     */
    public AnimationMain() {
        timer = new Timer(10, this);
        timer.start();
    }

    public void paint(Graphics g) {
        compteur++;
        if (compteur % 5 == 1) {
            temps = System.nanoTime();
        }

        // Render de la scène
        scene.update((double) 1 / 100);
        paint_scene(g);

        // Calcul des fps
        if (compteur % 5 == 0) {
            temps = System.nanoTime() - temps;
            fps = 5 / ((double) temps / Math.pow(10, 9));
        }

    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Univers");
        frame.add(new AnimationMain());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((X_SIZE) * ESPACEMENT, Y_SIZE * ESPACEMENT + Y_OFFSET);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}