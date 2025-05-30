package org.example.algorithmgrader.Kahendpuu;

public class Tipp {
    public int väärtus;
    public Tipp vasak;
    public Tipp parem;
    public VisuaalneTipp visuaalneTipp;

    // metsa kuvamiseks
    public int tase = 0;

    //Elemendi indeks kuhjas
    public int indeks;
    public Tipp(int väärtus, Tipp v, Tipp p) {
        this.väärtus = väärtus;
        this.vasak = v;
        this.parem = p;
    }
    public Tipp(int väärtus, Tipp v, Tipp p, int indeks) {
        this.väärtus = väärtus;
        this.vasak = v;
        this.parem = p;
        this.indeks = indeks;
    }

    public Tipp(int väärtus) {
        this.väärtus = väärtus;
        this.vasak = null;
        this.parem = null;

    }
    public int getVäärtus() {
        return väärtus;
    }

    @Override
    public String toString() {
        return ""+väärtus;
    }
}