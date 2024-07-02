public class Case {
    // Propriétés du fluide
    public double vitesseHorizontale;
    public double vitesseVerticale;
    public double pression;
    public double divergence;
    public double vitesse;

    /**
     * Constructeur
     */
    public Case() {
        this.vitesseHorizontale = 0.0;
        this.vitesseVerticale = 0.0;
        this.pression = 0.0;
        this.divergence = 0.0;
        this.vitesse = 0.0;
    }

    @Override
    public String toString() {
        return String.format("Box(h=%.2f, v=%.2f, p=%.2f, d=%.2f, V=%.2f)",
                vitesseHorizontale, vitesseVerticale, pression, divergence, vitesse);
    }
}