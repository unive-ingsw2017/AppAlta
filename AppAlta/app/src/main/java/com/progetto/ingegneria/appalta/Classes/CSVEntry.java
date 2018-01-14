package com.progetto.ingegneria.appalta.Classes;

/**
 * Created by matteo on 04/10/2017.
 */

public class CSVEntry {
    private String
            n_pg_atto_autorizzativo,
            anno_pg_atto_autorizzativo,
            tipo_gara,
            titolo,
            cig,
            oggetto,
            importo,
            tipo_appalto,
            durata,
            utilizzo_mepa,
            link_determina_pubblica,
            oggetto_determina,
            settore_dipartimento,
            servizio,
            ui,
            responsabile_gara,
            responsabile_procedimento,
            modalita_aggiudicazione,
            scadenza_offerte,
            ora_scadenza_offerte,
            variante_tempi_completamento,
            importo_somme_liquidate,
            numero_atto_aggiudicazione,
            anno_atto_aggiudicazione,
            data_atto_aggiudicazione,
            aggiudicatario,
            iva,
            codice_fiscale,
            importo_aggiudicazione,
            link_aggiudicazione,
            elenco_operatori_aggiudicazione,
            esiti_aggiudicazione,
            /* DATI NON INSERITI
            link_elaborati_gara,
            link_comunicazioni,
            link_chiarimenti,
            n_lotto,
            cig_lotto,
            oggetto_lotto,
            importo_lotto,
            n_pg_atto_autorizzativo_lotto,
            anno_atto_aggiudicazione_lotto,
            data_atto_aggiudicazione_lotto,
            aggiudicatario_lotto,
            iva_lotto,
            codice_fiscale_lotto,
            importo_aggiudicazione_lotto,
            variante_tempi_completamento_lotto,
            importo_somme_liquidate_lotto,
            elenco_operatori_link,
            esiti_lotto,
            */
            indirizzo;

    public CSVEntry() {
    }

    public String getN_pg_atto_autorizzativo() {
        return n_pg_atto_autorizzativo;
    }

    public void setN_pg_atto_autorizzativo(String n_pg_atto_autorizzativo) {
        this.n_pg_atto_autorizzativo = n_pg_atto_autorizzativo;
    }

    public String getAnno_pg_atto_autorizzativo() {
        return anno_pg_atto_autorizzativo;
    }

    public void setAnno_pg_atto_autorizzativo(String anno_pg_atto_autorizzativo) {
        this.anno_pg_atto_autorizzativo = anno_pg_atto_autorizzativo;
    }

    public String getTipo_gara() {
        return tipo_gara;
    }

    public void setTipo_gara(String tipo_gara) {
        this.tipo_gara = tipo_gara;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getCig() {
        return cig;
    }

    public void setCig(String cig) {
        this.cig = cig;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getImporto() {
        return importo;
    }

    public void setImporto(String importo) {
        this.importo = importo;
    }

    public String getTipo_appalto() {
        return tipo_appalto;
    }

    public void setTipo_appalto(String tipo_appalto) {
        this.tipo_appalto = tipo_appalto;
    }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    public String getUtilizzo_mepa() {
        return utilizzo_mepa;
    }

    public void setUtilizzo_mepa(String utilizzo_mepa) {
        this.utilizzo_mepa = utilizzo_mepa;
    }

    public String getLink_determina_pubblica() {
        return link_determina_pubblica;
    }

    public void setLink_determina_pubblica(String link_determina_pubblica) {
        this.link_determina_pubblica = link_determina_pubblica;
    }

    public String getOggetto_determina() {
        return oggetto_determina;
    }

    public void setOggetto_determina(String oggetto_determina) {
        this.oggetto_determina = oggetto_determina;
    }

    public String getSettore_dipartimento() {
        return settore_dipartimento;
    }

    public void setSettore_dipartimento(String settore_dipartimento) {
        this.settore_dipartimento = settore_dipartimento;
    }

    public String getServizio() {
        return servizio;
    }

    public void setServizio(String servizio) {
        this.servizio = servizio;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getResponsabile_gara() {
        return responsabile_gara;
    }

    public void setResponsabile_gara(String responsabile_gara) {
        this.responsabile_gara = responsabile_gara;
    }

    public String getResponsabile_procedimento() {
        return responsabile_procedimento;
    }

    public void setResponsabile_procedimento(String responsabile_procedimento) {
        this.responsabile_procedimento = responsabile_procedimento;
    }

    public String getModalita_aggiudicazione() {
        return modalita_aggiudicazione;
    }

    public void setModalita_aggiudicazione(String modalita_aggiudicazione) {
        this.modalita_aggiudicazione = modalita_aggiudicazione;
    }

    public String getScadenza_offerte() {
        return scadenza_offerte;
    }

    public void setScadenza_offerte(String scadenza_offerte) {
        this.scadenza_offerte = scadenza_offerte;
    }

    public String getOra_scadenza_offerte() {
        return ora_scadenza_offerte;
    }

    public void setOra_scadenza_offerte(String ora_scadenza_offerte) {
        this.ora_scadenza_offerte = ora_scadenza_offerte;
    }

    public String getVariante_tempi_completamento() {
        return variante_tempi_completamento;
    }

    public void setVariante_tempi_completamento(String variante_tempi_completamento) {
        this.variante_tempi_completamento = variante_tempi_completamento;
    }

    public String getImporto_somme_liquidate() {
        return importo_somme_liquidate;
    }

    public void setImporto_somme_liquidate(String importo_somme_liquidate) {
        this.importo_somme_liquidate = importo_somme_liquidate;
    }

    public String getNumero_atto_aggiudicazione() {
        return numero_atto_aggiudicazione;
    }

    public void setNumero_atto_aggiudicazione(String numero_atto_aggiudicazione) {
        this.numero_atto_aggiudicazione = numero_atto_aggiudicazione;
    }

    public String getAnno_atto_aggiudicazione() {
        return anno_atto_aggiudicazione;
    }

    public void setAnno_atto_aggiudicazione(String anno_atto_aggiudicazione) {
        this.anno_atto_aggiudicazione = anno_atto_aggiudicazione;
    }

    public String getData_atto_aggiudicazione() {
        return data_atto_aggiudicazione;
    }

    public void setData_atto_aggiudicazione(String data_atto_aggiudicazione) {
        this.data_atto_aggiudicazione = data_atto_aggiudicazione;
    }

    public String getAggiudicatario() {
        return aggiudicatario;
    }

    public void setAggiudicatario(String aggiudicatario) {
        this.aggiudicatario = aggiudicatario;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getCodice_fiscale() {
        return codice_fiscale;
    }

    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
    }

    public String getImporto_aggiudicazione() {
        return importo_aggiudicazione;
    }

    public void setImporto_aggiudicazione(String importo_aggiudicazione) {
        this.importo_aggiudicazione = importo_aggiudicazione;
    }

    public String getLink_aggiudicazione() {
        return link_aggiudicazione;
    }

    public void setLink_aggiudicazione(String link_aggiudicazione) {
        this.link_aggiudicazione = link_aggiudicazione;
    }

    public String getElenco_operatori_aggiudicazione() {
        return elenco_operatori_aggiudicazione;
    }

    public void setElenco_operatori_aggiudicazione(String elenco_operatori_aggiudicazione) {
        this.elenco_operatori_aggiudicazione = elenco_operatori_aggiudicazione;
    }

    public String getEsiti_aggiudicazione() {
        return esiti_aggiudicazione;
    }

    public void setEsiti_aggiudicazione(String esiti_aggiudicazione) {
        this.esiti_aggiudicazione = esiti_aggiudicazione;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
}
