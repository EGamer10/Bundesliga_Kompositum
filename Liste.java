public class Liste {
    private Listenelement anfang;

    public Liste() {
        anfang = new Abschluss();
    }

    public void VorneEinfuegen(Datenelement dneu) {
        anfang = new Knoten(dneu, anfang);
    }

    public void HintenEinfuegen(Datenelement dneu) {
        anfang = anfang.HintenEinfuegen(dneu);
    }

    public void EinfuegenVor(Datenelement dneu, Datenelement d_vergleich) {
        anfang = anfang.EinfuegenVor(dneu, d_vergleich);
    }

    public void SortiertEinfuegen(Datenelement dneu) {
        anfang = anfang.SortiertEinfuegen(dneu);
    }

    public void KnotenEntfernen(Datenelement dvergleich) {
        anfang = anfang.KnotenEntfernen(dvergleich);
    }

    public Datenelement AnfangEntfernen() {
        Datenelement d = anfang.DatenelementGeben();
        anfang = anfang.NachfolgerGeben();
        return d;
    }

    public Datenelement EndeEntfernen() {
        Datenelement d_letzter = anfang.EndeGeben(null);
        anfang = anfang.KnotenEntfernen(d_letzter);
        return d_letzter;
    }

    public Datenelement Suchen(String vergleichswert) {
        return anfang.Suchen(vergleichswert);
    }

    public void InformationAusgeben() {
        anfang.InformationAusgeben();
    }

    public int LaengeGeben() {
        return anfang.RestlaengeGeben();
    }

    public Liste SortierteListeErzeugen() {
        Liste sortiert = new Liste();
        anfang.InSortierteListeEinfuegen(sortiert);
        return sortiert;
    }

    public Datenelement DatenelementAnIndexGeben(int index) {
        return anfang.DatenelementAnIndexGeben(index);
    }
}
