package com.progetto.ingegneria.appalta.Classes;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by matteo on 16/12/2017.
 */
public class Appalto {
    private String
            cig,
            titolo,
            tipo,
            anno,
            aggiudicatario,
            cod_fiscale_iva,
            importo_aggiudicazione,
            dataInizio,
            citta,
            responsabile;

    private LatLng coordinate;

    public Appalto() {
    }

    public String getCig() {
        return cig;
    }

    public void setCig(String cig) {
        this.cig = cig;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public String getAggiudicatario() {
        return aggiudicatario;
    }

    public void setAggiudicatario(String aggiudicatario) {
        this.aggiudicatario = aggiudicatario;
    }

    public String getCod_fiscale_iva() {
        return cod_fiscale_iva;
    }

    public void setCod_fiscale_iva(String cod_fiscale_iva) {
        this.cod_fiscale_iva = cod_fiscale_iva;
    }

    public String getImporto_aggiudicazione() {
        return importo_aggiudicazione;
    }

    public void setImporto_aggiudicazione(String importo_aggiudicazione) {
        this.importo_aggiudicazione = importo_aggiudicazione;
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(String dataInizio) {
        this.dataInizio = dataInizio;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String responsabile) {
        this.responsabile = responsabile;
    }
}
