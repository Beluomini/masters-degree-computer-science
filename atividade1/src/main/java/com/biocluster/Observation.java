package com.biocluster;

public class Observation {
    private int id, especieId;
    private double x, y, saude;
    private boolean invasora;

    public Observation(int id, int especieId, double x, double y, double saude, boolean invasora) {
        this.id = id; this.especieId = especieId;
        this.x = x; this.y = y; this.saude = saude;
        this.invasora = invasora;
    }
    public int getId() { return id; }
    public int getEspecieId() { return especieId; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSaude() { return saude; }
    public boolean isInvasora() { return invasora; }
}
