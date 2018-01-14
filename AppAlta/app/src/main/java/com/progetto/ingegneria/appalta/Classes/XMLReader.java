package com.progetto.ingegneria.appalta.Classes;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by matteo on 16/12/2017.
 */

public class XMLReader {
    private static final String ns = null;
    private InputStream inputStream;

    public XMLReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    //Funzioni Test
    public ArrayList<XMLEntry> read(){
        XmlPullParserFactory pullParserFactory;
        ArrayList<XMLEntry> appalti = new ArrayList<>();
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            appalti = parseXML(parser);
        }
        catch (XmlPullParserException e) {
            Log.e("XMLReader readread()", e.getMessage());
        }
        return appalti;
    }

    private ArrayList<XMLEntry> parseXML(XmlPullParser parser){
        ArrayList<XMLEntry> appalti = new ArrayList<>();
        int eventType = 0;
        String anno = "null";
        boolean aggiudicatario = false;
        try {
            eventType = parser.getEventType();
        }
        catch (XmlPullParserException e) {
            Log.e("XMLReader parseXML()", e.getMessage());
        }
        XMLEntry appalto = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;

            try {
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        appalti = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        //Log.d("XMLReader parseXML()", "tag letto: "+name);
                        if (name.equalsIgnoreCase("annoRiferimento")){
                            anno = parser.nextText();
                        }
                        if (name.equalsIgnoreCase("lotto")){
                            appalto = new XMLEntry();
                            //Log.d("XMLReader parseXML()", "lotto creato");
                        }
                        else if (appalto != null){
                            switch (name) {
                                case "cig":
                                    appalto.setCig(parser.nextText());
                                    break;
                                case "aggiudicatario":
                                    aggiudicatario = true;
                                    break;
                                case "codiceFiscale":
                                    if(aggiudicatario)
                                        appalto.setCodiceFiscaleProp(parser.nextText());
                                    break;
                                case "ragioneSociale":
                                    if(aggiudicatario) {
                                        appalto.setRagioneSociale(parser.nextText());
                                        aggiudicatario=false;
                                    }
                                    break;
                                case "denominazione":
                                    appalto.setDenominazione(parser.nextText());
                                    break;
                                case "oggetto":
                                    appalto.setOggetto(parser.nextText());
                                    break;
                                case "sceltaContraente":
                                    appalto.setSceltaContraente(parser.nextText());
                                    break;
                                case "importoAggiudicazione":
                                    appalto.setImportoAggiudicazione(parser.nextText());
                                    break;
                                case "dataInizio":
                                    appalto.setDataInizio(parser.nextText());
                                    break;
                                case "dataUltimazione":
                                    appalto.setDataUltimazione(parser.nextText());
                                    break;
                                case "importoSommeLiquidate":
                                    appalto.setImportoSommeLiquidate(parser.nextText());
                                    break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("lotto") && appalto != null){
                            appalto.setAnno(anno);
                            appalti.add(appalto);
                        }
                }
                eventType = parser.next();

            }
            catch (XmlPullParserException e) {
                Log.e("XMLReader parseXML()", e.getMessage());
            }
            catch (IOException e) {
                Log.e("XMLReader parseXML()", e.getMessage());
            }
        }
        return appalti;
    }

}
