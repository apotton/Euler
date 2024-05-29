import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimationMain extends JPanel implements ActionListener {
    /// Paramètres de la simulation
    static final int X_SIZE = 300; // Largeur de la fenêtre
    static final int Y_SIZE = 75; // Hauteur de la fenêtre
    static final int Y_OFFSET = 37; // Offset en Y pour l'affichage
    static final int DUREE_IMAGES = 10; // Durée d'affichage de chaque image

    static final int SUBSTEPS = 2; // Nombre de calculations par image
    static final int ESPACEMENT = 3; // Nombre de pixels par carré

    static final float GRAVITE = 0f; // Force de gravité qui s'applique au fluide
    // static final float DIFF = 100f; // Taux de diffusion du fluide
    static final float VISC = 0.01f; // Viscosité du fluide

    static final int VITESSE = 20; // Vitesse turbine en entrée d'écoulement
    static final int LARGEUR = 100; // Largeur du flux d'entrée (en %)
    static final int HAUTEUR = 20; // Hauteur du profil d'aile mince

    Timer timer;
    static public Scene scene = new Scene();
    static int compteur;
    public long temps = System.nanoTime();
    double fps = temps;

    /**
     * Fonction qui détermine l'échelle de couleur à utiliser
     * 
     * @param val    Valeur du champ au point
     * @param minVal Valeur minimale du champ
     * @param maxVal Valeur maximale du champ
     * @return La couleur idéale
     */
    Color getSciColor(float val, float minVal, float maxVal) {
        val = 1 - (float) Math.abs((Math.min(Math.max(val, minVal), maxVal) - minVal) / (maxVal - minVal));

        // System.out.println(val);

        // float detail = 4.1f;

        // int num = (int) Math.floor(val * detail);
        // float s = (val - (float) num / detail) * detail;

        // double r = 0, g = 0, b = 1;

        // switch (num) {
        // case 0:
        // r = 0.0;
        // g = s;
        // b = 1.0;
        // break;
        // case 1:
        // r = 0.0;
        // g = 1.0;
        // b = 1.0 - s;
        // break;
        // case 2:
        // r = s;
        // g = 1.0;
        // b = 0.0;
        // break;
        // case 3:
        // r = 1.0;
        // g = 1.0 - s;
        // b = 0.0;
        // break;
        // }

        val = (float) (Math.exp(val) / Math.E);

        // return new Color((int) (255 * r), (int) (255 * g), (int) (255 * b));
        return new Color((int) (254 * val), (int) (254 * val), (int) (254 * val));
    }

    void paint_scene(Graphics2D g) {
        float[] valeurs = scene.u;

        scene.majMaxMin(valeurs);

        System.out.println("Max:" + scene.max);
        System.out.println("Min:" + scene.min);

        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                // Détermination de la couleur correspondant à la densité au point (x,y)
                Color coul = getSciColor(valeurs[Scene.IX(x, y)], scene.min, scene.max);

                g.setColor(coul);
                g.fillRect(ESPACEMENT * (x), ESPACEMENT * (y), ESPACEMENT, ESPACEMENT);
            }
        }

        // Objet au milieu
        // int hauteur = AnimationMain.HAUTEUR;
        // int longueur = (int) ((double) hauteur / 0.1106);

        // for (int X = 50; X < 50 + longueur; X++) {
        // int yxmax = (int) (Scene.top((double) (X - 50) * 0.1106 / hauteur) * hauteur
        // / 0.1106);
        // int yxmin = (int) (Scene.bottom((double) (X - 50) * 0.1106 / hauteur) *
        // hauteur / 0.1106);

        // for (int y = Y_SIZE / 2 - yxmax; y <= Y_SIZE / 2 - yxmin; y++) {
        // g.setColor(Color.BLACK);
        // g.fillRect(ESPACEMENT * (X), ESPACEMENT * (y), ESPACEMENT, ESPACEMENT);
        // }
        // }

        // Affichage des stats
        g.setColor(Color.BLACK);
        g.drawString(Double.toString(Double.valueOf(String.valueOf(((int) (fps * 1000)))) / 1000), 0, 10);
    }

    // Initialisation du minuteur
    public AnimationMain() {
        timer = new Timer(DUREE_IMAGES, this);
        timer.start();
    }

    public void paint(Graphics g) {
        if (compteur % 5 == 1) {
            temps = System.nanoTime();
        }

        // Render de la scène
        Graphics2D g2d = (Graphics2D) g;
        paint_scene(g2d);

        // Réalisation de plusieurs updates
        for (int i = 0; i < SUBSTEPS; i++) {
            scene.maj((float) 1 / (10 * DUREE_IMAGES * SUBSTEPS));
        }

        compteur++;

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
