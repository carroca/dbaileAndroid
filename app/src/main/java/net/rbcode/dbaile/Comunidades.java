package net.rbcode.dbaile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Borja on 21/09/2014.
 */
public class Comunidades {

    Map<String, String> mapES = new HashMap<String, String>();
    private String arrayES[][];

    public Comunidades(){
        esMap();
        esArray();
    }

    public Map<String, String> getMapES(){return mapES; }
    public String[][] getArrayES() { return arrayES; }

    private void esMap() {
        mapES.put("AL", "Alava");
        mapES.put("AB", "Albacete");
        mapES.put("AC", "Alicante");
        mapES.put("AM", "Almeria");
        mapES.put("AS", "Asturias");
        mapES.put("AV", "Avila");
        mapES.put("BJ", "Badajoz");
        mapES.put("IB", "Alava");
        mapES.put("BA", "Barcelona");
        mapES.put("BU", "Burgos");
        mapES.put("CC", "Caceres");
        mapES.put("CZ", "Cadiz");
        mapES.put("CT", "Cantabria");
        mapES.put("CL", "Castellon");
        mapES.put("CR", "Ciudad Real");
        mapES.put("CD", "Cordoba");
        mapES.put("CA", "A Coruña");
        mapES.put("CU", "Cuenca");
        mapES.put("GI", "Girona");
        mapES.put("GD", "Granada");
        mapES.put("GJ", "Guadalajara");
        mapES.put("GP", "Guipuzcoa");
        mapES.put("HL", "Huelva");
        mapES.put("HS", "Huesca");
        mapES.put("JN", "Jaen");
        mapES.put("RJ", "La Rioja");
        mapES.put("LE", "Leon");
        mapES.put("LL", "Lleida");
        mapES.put("LG", "Lugo");
        mapES.put("MD", "Madrid");
        mapES.put("ML", "Malaga");
        mapES.put("MU", "Murcia");
        mapES.put("NV", "Navarra");
        mapES.put("OU", "Ourense");
        mapES.put("PL", "Palencia");
        mapES.put("PM", "Las Palmas");
        mapES.put("PO", "Pontevedra");
        mapES.put("SL", "Salamanca");
        mapES.put("SC", "Santa Cruz de Tererife");
        mapES.put("SG", "Segovia");
        mapES.put("SV", "Sevilla");
        mapES.put("SO", "Soria");
        mapES.put("TA", "Tarragona");
        mapES.put("TE", "Teruel");
        mapES.put("TO", "Toledo");
        mapES.put("VC", "Valencia");
        mapES.put("VD", "Valladolid");
        mapES.put("VZ", "Vizcaya");
        mapES.put("ZM", "Zamora");
        mapES.put("ZR", "Zaragoza");
    }

    private void esArray() {
        arrayES = new String[50][2];
        arrayES[0][0] = "AB";
        arrayES[0][1] = "Albacete";
        arrayES[1][0] = "AC";
        arrayES[1][1] = "Alicante";
        arrayES[2][0] = "AM";
        arrayES[2][1] = "Almeria";
        arrayES[3][0] = "AS";
        arrayES[3][1] = "Asturias";
        arrayES[4][0] = "AV";
        arrayES[4][1] = "Avila";
        arrayES[5][0] = "BJ";
        arrayES[5][1] = "Badajoz";
        arrayES[6][0] = "IB";
        arrayES[6][1] = "Alava";
        arrayES[7][0] = "BA";
        arrayES[7][1] = "Barcelona";
        arrayES[8][0] = "BU";
        arrayES[8][1] = "Burgos";
        arrayES[9][0] = "CC";
        arrayES[9][1] = "Caceres";
        arrayES[10][0] = "CZ";
        arrayES[10][1] = "Cadiz";
        arrayES[11][0] = "CT";
        arrayES[11][1] = "Cantabria";
        arrayES[12][0] = "CL";
        arrayES[12][1] = "Castellon";
        arrayES[13][0] = "CR";
        arrayES[13][1] = "Ciudad Real";
        arrayES[14][0] = "CD";
        arrayES[14][1] = "Cordoba";
        arrayES[15][0] = "CA";
        arrayES[15][1] = "A coruña";
        arrayES[16][0] = "CU";
        arrayES[16][1] = "Cuenca";
        arrayES[17][0] = "GI";
        arrayES[17][1] = "Girona";
        arrayES[18][0] = "GD";
        arrayES[18][1] = "Granada";
        arrayES[19][0] = "GJ";
        arrayES[19][1] = "Guadalajara";
        arrayES[20][0] = "GP";
        arrayES[20][1] = "Guipuzcoa";
        arrayES[21][0] = "HL";
        arrayES[21][1] = "Huelva";
        arrayES[22][0] = "HS";
        arrayES[22][1] = "Huesca";
        arrayES[23][0] = "JN";
        arrayES[23][1] = "Jaen";
        arrayES[24][0] = "RJ";
        arrayES[24][1] = "La rioja";
        arrayES[25][0] = "LE";
        arrayES[25][1] = "Leon";
        arrayES[26][0] = "LL";
        arrayES[26][1] = "LLeida";
        arrayES[27][0] = "LG";
        arrayES[27][1] = "Lugo";
        arrayES[28][0] = "MD";
        arrayES[28][1] = "Madrid";
        arrayES[29][0] = "ML";
        arrayES[29][1] = "Malaga";
        arrayES[30][0] = "MU";
        arrayES[30][1] = "Murcia";
        arrayES[31][0] = "NV";
        arrayES[31][1] = "Navarra";
        arrayES[32][0] = "OU";
        arrayES[32][1] = "Ourense";
        arrayES[33][0] = "PL";
        arrayES[33][1] = "Palencia";
        arrayES[34][0] = "PM";
        arrayES[34][1] = "Las palmas";
        arrayES[35][0] = "PO";
        arrayES[35][1] = "Pontevedra";
        arrayES[36][0] = "SL";
        arrayES[36][1] = "Salamanca";
        arrayES[37][0] = "SC";
        arrayES[37][1] = "Santa Cruz de Tenerife";
        arrayES[38][0] = "SG";
        arrayES[38][1] = "Segovia";
        arrayES[39][0] = "SV";
        arrayES[39][1] = "Sevilla";
        arrayES[40][0] = "SO";
        arrayES[40][1] = "Soria";
        arrayES[41][0] = "TA";
        arrayES[41][1] = "Tarragona";
        arrayES[42][0] = "TE";
        arrayES[42][1] = "Teruel";
        arrayES[43][0] = "TO";
        arrayES[43][1] = "Toledo";
        arrayES[44][0] = "VC";
        arrayES[44][1] = "Valencia";
        arrayES[45][0] = "VD";
        arrayES[45][1] = "Valladolid";
        arrayES[46][0] = "VZ";
        arrayES[46][1] = "Vizcaya";
        arrayES[47][0] = "ZM";
        arrayES[47][1] = "Zamora";
        arrayES[48][0] = "ZR";
        arrayES[48][1] = "Zaragoza";
        arrayES[49][0] = "null";
        arrayES[49][1] = "";
    }
}
