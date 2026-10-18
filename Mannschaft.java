public class Mannschaft implements Datenelement {
    private final String name;
    private int siege;
    private int unentschieden;
    private int niederlagen;
    private int punkte;

    public Mannschaft(String name) {
        this.name = name;
        this.siege = 0;
        this.unentschieden = 0;
        this.niederlagen = 0;
        this.punkte = 0;
    }

    public Mannschaft(String name, int siege, int unentschieden, int niederlagen, int punkte) {
        this.name = name;
        this.siege = siege;
        this.unentschieden = unentschieden;
        this.niederlagen = niederlagen;
        this.punkte = punkte;
    }

    public void SiegBuchen() {
        siege++;
        punkte += 3;
    }

    public void UnentschiedenBuchen() {
        unentschieden++;
        punkte += 1;
    }

    public void NiederlageBuchen() {
        niederlagen++;
    }

    public int SpieleGeben() {
        return siege + unentschieden + niederlagen;
    }

    public String NameGeben() { return name; }
    public int SiegeGeben() { return siege; }
    public int UnentschiedenGeben() { return unentschieden; }
    public int NiederlagenGeben() { return niederlagen; }
    public int PunkteGeben() { return punkte; }

    @Override
    public void InformationAusgeben() {
        System.out.printf("%-26s | Sp: %2d | S: %2d | U: %2d | N: %2d | Pkt: %2d%n",
                name, SpieleGeben(), siege, unentschieden, niederlagen, punkte);
    }

    @Override
    public boolean IstKleinerAls(Datenelement dvergleich) {
        if (!(dvergleich instanceof Mannschaft)) {
            return false;
        }
        Mannschaft andere = (Mannschaft) dvergleich;

        // Da Platz 1 (meiste Punkte) am Anfang der Liste stehen soll,
        // prüft IstKleinerAls, ob diese Mannschaft in der Tabelle VOR der anderen steht.
        if (this.punkte != andere.punkte) {
            return this.punkte > andere.punkte;
        }
        if (this.siege != andere.siege) {
            return this.siege > andere.siege;
        }
        if (this.niederlagen != andere.niederlagen) {
            return this.niederlagen < andere.niederlagen;
        }
        return this.name.compareToIgnoreCase(andere.name) < 0;
    }

    @Override
    public boolean SchluesselIstGleich(String s) {
        return this.name.equalsIgnoreCase(s);
    }
}
