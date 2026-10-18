public class Knoten extends Listenelement {
    private Listenelement nachfolger;
    private Datenelement daten;

    public Knoten(Datenelement dneu, Listenelement lneu) {
        this.daten = dneu;
        this.nachfolger = lneu;
    }

    @Override
    public Knoten HintenEinfuegen(Datenelement dneu) {
        nachfolger = nachfolger.HintenEinfuegen(dneu);
        return this;
    }

    @Override
    public Knoten EinfuegenVor(Datenelement dneu, Datenelement d_vergleich) {
        if (daten == d_vergleich) {
            return new Knoten(dneu, this);
        } else {
            nachfolger = nachfolger.EinfuegenVor(dneu, d_vergleich);
            return this;
        }
    }

    @Override
    public Knoten SortiertEinfuegen(Datenelement dneu) {
        if (daten.IstKleinerAls(dneu)) {
            nachfolger = nachfolger.SortiertEinfuegen(dneu);
            return this;
        } else {
            return new Knoten(dneu, this);
        }
    }

    @Override
    public Listenelement KnotenEntfernen(Datenelement dvergleich) {
        if (daten == dvergleich) {
            return nachfolger;
        } else {
            nachfolger = nachfolger.KnotenEntfernen(dvergleich);
            return this;
        }
    }

    @Override
    public Datenelement EndeGeben(Datenelement d) {
        return nachfolger.EndeGeben(daten);
    }

    @Override
    public Datenelement DatenelementGeben() {
        return daten;
    }

    @Override
    public Listenelement NachfolgerGeben() {
        return nachfolger;
    }

    @Override
    public Datenelement Suchen(String vergleichswert) {
        if (daten.SchluesselIstGleich(vergleichswert)) {
            return daten;
        } else {
            return nachfolger.Suchen(vergleichswert);
        }
    }

    @Override
    public void InformationAusgeben() {
        daten.InformationAusgeben();
        nachfolger.InformationAusgeben();
    }

    @Override
    public int RestlaengeGeben() {
        return nachfolger.RestlaengeGeben() + 1;
    }

    @Override
    public void InSortierteListeEinfuegen(Liste zielListe) {
        zielListe.SortiertEinfuegen(daten);
        nachfolger.InSortierteListeEinfuegen(zielListe);
    }

    @Override
    public Datenelement DatenelementAnIndexGeben(int index) {
        if (index == 0) {
            return daten;
        } else {
            return nachfolger.DatenelementAnIndexGeben(index - 1);
        }
    }
}
