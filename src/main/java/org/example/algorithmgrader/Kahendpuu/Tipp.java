package org.example.algorithmgrader.Kahendpuu;

public class Tipp {
    public int väärtus;
    public Tipp vasak;
    public Tipp parem;
    public VisuaalneTipp visuaalneTipp;
    public Tipp(int väärtus, Tipp v, Tipp p) {
        this.väärtus = väärtus;
        this.vasak = v;
        this.parem = p;
    }

    public Tipp(int väärtus) {
        this.väärtus = väärtus;
        this.vasak = null;
        this.parem = null;

    }


    public int getVäärtus() {
        return väärtus;
    }
    /*public VisuaalneTipp getVisuaalneTipp(){
        return visuaalneTipp;
    }*/

    @Override
    public String toString() {
        return ""+väärtus;
    }
}