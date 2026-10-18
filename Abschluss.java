public class Abschluss extends Listenelement {

    public Abschluss() {
    }

    @Override
    public Knoten HintenEinfuegen(Datenelement dneu) {
        return new Knoten(dneu, this);
    }

    @Override
    public Knoten EinfuegenVor(Datenelement dneu, Datenelement d_vergleich) {
        return new Knoten(dneu, this);
    }

    @Override
    public Knoten SortiertEinfuegen(Datenelement dneu) {
        return new Knoten(dneu, this);
    }

    @Override
    public Listenelement KnotenEntfernen(Datenelement dvergleich) {
        return this;
    }

    @Override
    public Datenelement EndeGeben(Datenelement d) {
        return d;
    }

    @Override
    public Datenelement DatenelementGeben() {
        return null;
    }

    @Override
    public Listenelement NachfolgerGeben() {
        return this;
    }

    @Override
    public Datenelement Suchen(String vergleichswert) {
        return null;
    }

    @Override
    public void InformationAusgeben() {
        // Ende der Liste erreicht - keine Ausgabe
    }

    @Override
    public int RestlaengeGeben() {
        return 0;
    }

    @Override
    public void InSortierteListeEinfuegen(Liste zielListe) {
        // Rekursionsabbruch am Listenende
    }

    @Override
    public Datenelement DatenelementAnIndexGeben(int index) {
        return null;
    }
}
