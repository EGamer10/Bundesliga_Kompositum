import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LigaVerwaltung {
    private static final Path SAVE_FILE = Paths.get("bundesliga_save.csv");

    // 1. Liste: Unsortierte Stammliste zur dauerhaften Speicherung der Mannschaften
    private Liste stammListe;

    // 2. Liste: Immer frisch sortierte Tabellenliste (wird per SortiertEinfuegen aufgebaut)
    private Liste sortierteListe;

    public LigaVerwaltung() {
        this.stammListe = new Liste();
        this.sortierteListe = new Liste();
        if (!VonFestplatteLaden()) {
            BundesligaVorlageLaden();
        }
    }

    public void BundesligaVorlageLaden() {
        stammListe = new Liste();
        String[] vereine = {
            "FC Bayern München", "Bayer 04 Leverkusen", "Borussia Dortmund",
            "RB Leipzig", "VfB Stuttgart", "Eintracht Frankfurt",
            "TSG Hoffenheim", "1. FC Heidenheim", "Werder Bremen",
            "SC Freiburg", "FC Augsburg", "VfL Wolfsburg",
            "1. FSV Mainz 05", "Borussia Mönchengladbach", "1. FC Union Berlin",
            "VfL Bochum", "FC St. Pauli", "Holstein Kiel"
        };
        for (String name : vereine) {
            stammListe.HintenEinfuegen(new Mannschaft(name));
        }
        SortierteListeAktualisieren();
    }

    public void SortierteListeAktualisieren() {
        // Baut die zweite Liste komplett neu über SortiertEinfuegen auf,
        // ohne Knoten in der Stammliste zu verschieben.
        sortierteListe = stammListe.SortierteListeErzeugen();
    }

    public Mannschaft ErgebnisBuchen(String vereinsName, int punkteArt) {
        Datenelement gefunden = stammListe.Suchen(vereinsName);
        if (gefunden instanceof Mannschaft) {
            Mannschaft m = (Mannschaft) gefunden;
            if (punkteArt == 3) {
                m.SiegBuchen();
            } else if (punkteArt == 1) {
                m.UnentschiedenBuchen();
            } else {
                m.NiederlageBuchen();
            }
            SortierteListeAktualisieren();
            return m;
        }
        return null;
    }

    public void SpieltagSimulieren() {
        int anzahl = stammListe.LaengeGeben();
        List<Mannschaft> alle = new ArrayList<>();
        for (int i = 0; i < anzahl; i++) {
            alle.add((Mannschaft) stammListe.DatenelementAnIndexGeben(i));
        }
        Collections.shuffle(alle);
        Random rnd = new Random();

        for (int i = 0; i + 1 < alle.size(); i += 2) {
            Mannschaft heim = alle.get(i);
            Mannschaft gast = alle.get(i + 1);
            int ausgang = rnd.nextInt(3);
            if (ausgang == 0) {
                heim.SiegBuchen();
                gast.NiederlageBuchen();
            } else if (ausgang == 1) {
                heim.UnentschiedenBuchen();
                gast.UnentschiedenBuchen();
            } else {
                heim.NiederlageBuchen();
                gast.SiegBuchen();
            }
        }
        SortierteListeAktualisieren();
    }

    public Liste StammListeGeben() {
        return stammListe;
    }

    public Liste SortierteListeGeben() {
        return sortierteListe;
    }

    public void AufFestplatteSpeichern() throws IOException {
        List<String> zeilen = new ArrayList<>();
        int anzahl = stammListe.LaengeGeben();
        for (int i = 0; i < anzahl; i++) {
            Mannschaft m = (Mannschaft) stammListe.DatenelementAnIndexGeben(i);
            zeilen.add(String.join(";", m.NameGeben(),
                    String.valueOf(m.SiegeGeben()),
                    String.valueOf(m.UnentschiedenGeben()),
                    String.valueOf(m.NiederlagenGeben()),
                    String.valueOf(m.PunkteGeben())));
        }
        Files.write(SAVE_FILE, zeilen);
    }

    public boolean VonFestplatteLaden() {
        if (!Files.exists(SAVE_FILE)) return false;
        try {
            List<String> zeilen = Files.readAllLines(SAVE_FILE);
            stammListe = new Liste();
            for (String zeile : zeilen) {
                String[] p = zeile.split(";");
                if (p.length == 5) {
                    stammListe.HintenEinfuegen(new Mannschaft(
                            p[0],
                            Integer.parseInt(p[1]),
                            Integer.parseInt(p[2]),
                            Integer.parseInt(p[3]),
                            Integer.parseInt(p[4])
                    ));
                }
            }
            if (stammListe.LaengeGeben() > 0) {
                SortierteListeAktualisieren();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
