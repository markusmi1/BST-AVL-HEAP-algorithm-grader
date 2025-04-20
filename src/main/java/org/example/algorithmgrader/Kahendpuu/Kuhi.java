package org.example.algorithmgrader.Kahendpuu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kuhi {

    public List<Integer> kuhi = new ArrayList<>();

    public int leiaVasakuAlluvaIndeks(int k) {//k on elemendi indeks
        int vasakuAlluvaIndeks = 2*k + 1;
        return (vasakuAlluvaIndeks >= kuhi.size()) ? -1 : vasakuAlluvaIndeks;
    }

    public int leiaParemaAlluvaIndeks(int k) {//k on elemendi indeks
        int paremaAlluvaIndeks = 2*k + 2;
        return (paremaAlluvaIndeks >= kuhi.size()) ? -1 : paremaAlluvaIndeks;
    }

    public int leiaÜlemuseIndeks(int k) {//k on elemendi indeks
        int ülemuseIndeks = Math.floorDiv(k-1, 2);
        return (ülemuseIndeks < 0) ? -1 : ülemuseIndeks;
    }

    //Ülesanne 2
    //Koostada meetod, mis massiivina (või listina) etteantud kahendkuhjast  loob puu juurtipuga Tipp juur, mida saab siis kuvada teegi dendroloj abil.
    public Tipp looTipp(int tipuIndeksKuhjas) {//kui tahad tervet puud, siis indeks = 0
        if(tipuIndeksKuhjas == -1) return null;
        Tipp vasakAlluv = looTipp(leiaVasakuAlluvaIndeks(tipuIndeksKuhjas));
        Tipp paremAlluv = looTipp(leiaParemaAlluvaIndeks(tipuIndeksKuhjas));
        return new Tipp(kuhi.get(tipuIndeksKuhjas), vasakAlluv, paremAlluv, tipuIndeksKuhjas);
    }

    //Koostada meetodid
    //kuhja kirje mullina ülesviimiseks;
    //kuhja kirje mullina allaviimiseks.
    public void viiMullinaÜles(int kirjeIndeks) {
        int ülemuseIndeks = leiaÜlemuseIndeks(kirjeIndeks);

        while (ülemuseIndeks != -1 && //vaadeldaval tipul on ülemus
                kuhi.get(kirjeIndeks) > kuhi.get(ülemuseIndeks)) { //vaadeldav väärtus on ülemuse omast suurem

            Collections.swap(kuhi, kirjeIndeks, ülemuseIndeks);

            kirjeIndeks = ülemuseIndeks;
            ülemuseIndeks = leiaÜlemuseIndeks(kirjeIndeks);
        }
    }

    public void viiMullinaAlla(int kirjeIndeks) {
        while (true) {
            int alluvaIndeksV = leiaVasakuAlluvaIndeks(kirjeIndeks);
            int alluvaIndeksP = leiaParemaAlluvaIndeks(kirjeIndeks);

            //kui mõlemad alluvad on olemas ja nende mõlema väärtused on suuremad praegusest, vahetame neist suuremaga
            if(alluvaIndeksV != -1 && alluvaIndeksP != -1 && kuhi.get(alluvaIndeksV) > kuhi.get(kirjeIndeks) && kuhi.get(alluvaIndeksP) > kuhi.get(kirjeIndeks)) {
                int suuremaIndeks = kuhi.get(alluvaIndeksP) > kuhi.get(alluvaIndeksV) ? alluvaIndeksP : alluvaIndeksV;
                Collections.swap(kuhi, kirjeIndeks, suuremaIndeks);
                kirjeIndeks = suuremaIndeks;
                continue;
            }

            // kui vasak alluv on olemas ja vasaku alluva väärtus on suurem praegusest, vahetame vasaku alluvaga
            if(alluvaIndeksV != -1 && kuhi.get(alluvaIndeksV) > kuhi.get(kirjeIndeks)) {
                Collections.swap(kuhi, kirjeIndeks, alluvaIndeksV);
                kirjeIndeks = alluvaIndeksV;
                continue;
            }

            // kui parem alluv on olemas ja parema alluva väärtus on suurem praegusest, vahetame parema alluvaga
            if(alluvaIndeksP != -1 && kuhi.get(alluvaIndeksP) > kuhi.get(kirjeIndeks)) {
                Collections.swap(kuhi, kirjeIndeks, alluvaIndeksP);
                kirjeIndeks = alluvaIndeksP;
                continue;
            }

            //kui ei saanud enam allapoole viia, siis lõpetame töö
            return;
        }
    }

    //etteantud võtmeväärtusega kirje lisamiseks.
    //suurima (juurtipu) võtmeväärtusega kirje eemaldamiseks.
    public void lisaKirje(int väärtus) {
        kuhi.add(väärtus);
        viiMullinaÜles(kuhi.size()-1);

    }

    public void eemaldaJuur() {
        int viimaneKirje = kuhi.remove(kuhi.size()-1);
        if (kuhi.isEmpty()){
            return;
        }
        kuhi.set(0, viimaneKirje);
        viiMullinaAlla(0);
    }
    public void vaheta(int i, int j){
        int ajutine = kuhi.get(i);
        kuhi.set(i, kuhi.get(j));
        kuhi.set(j, ajutine);
    }
    public boolean kasOnKuhi(){
        for (int i = 0; i < kuhi.size(); i++) {
            int ülemus = leiaÜlemuseIndeks(i);
            if (ülemus != -1){
                if(kuhi.get(i) > kuhi.get(ülemus)){
                    return false;
                }
            }
        }
        return true;
    }


}
