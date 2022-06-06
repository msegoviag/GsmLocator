package com.example.myapplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationApi {

    private String token;
    private String radio;
    private int mcc;
    private int mnc;
    private int lac;
    private int cid;
    private String finalResponse;
    private String jsonInputString;

    /**
     * Constructor principal
     *
     * @param token
     * @param mcc
     * @param mnc
     * @param lac
     * @param cid
     */

    public LocationApi(String token, int mcc, int mnc, int lac, int cid) {

        this.token = token;
        //this.radio = radio;
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cid = cid;


        /*
        Se efectúa la conexión a la API y se realiza una petición con los parámetros pasados en
        el constructor. La respuesta obtenida se guarda en una variable que será recuperada con el
        getter getResponse().
         */

        try {

            URL url = new URL("https://eu1.unwiredlabs.com/v2/process.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            jsonInputString = "{\"token\":\" " + this.token + "\",\"radio\": \"gsm\",\"mcc\": " +
                    this.mcc + ",\"mnc\": " + this.mnc + ",\"cells\": [{\"lac\": " + this.lac + ",\"cid\":  " + this.cid + "}],\"address\": 1}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(jsonInputString);
                finalResponse = response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    Métodos getters  para la obtención de datos desde una instancia de LocationApi.
     */

    public String getResponse() {
        return finalResponse;
    }

    public String getToken() {
        return token;
    }

    public String getRadio() {
        return radio;
    }

    public int getMcc() {
        return mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public int getLac() {
        return lac;
    }

    public int getCid() {
        return cid;
    }

    public String getJSONInputString() {
        return jsonInputString;
    }

}
