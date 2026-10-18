public abstract class Listenelement {
    public abstract Knoten HintenEinfuegen(Datenelement dneu);
    public abstract Knoten EinfuegenVor(Datenelement dneu, Datenelement d_vergleich);
    public abstract Knoten SortiertEinfuegen(Datenelement dneu);
    public abstract Listenelement KnotenEntfernen(Datenelement dvergleich);
    public abstract Datenelement EndeGeben(Datenelement d);
    public abstract Datenelement DatenelementGeben();
    public abstract Listenelement NachfolgerGeben();
    public abstract Datenelement Suchen(String vergleichswert);
    public abstract void InformationAusgeben();
    public abstract int RestlaengeGeben();

    // Hilfsmethoden für den Zwei-Listen-Mechanismus und die GUI-Anbindung
    public abstract void InSortierteListeEinfuegen(Liste zielListe);
    public abstract Datenelement DatenelementAnIndexGeben(int index);
}
