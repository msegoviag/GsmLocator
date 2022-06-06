package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private String[] PERMISSIONS;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        /*
        Definimos permisos que usará nuestra aplicación
         */
        PERMISSIONS = new String[]{

                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        /*
        Si no tenemos permisos se solicitarán al usuario.
         */
        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {

            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);

        }

        /*
        Si tenemos todos los permisos definidos en PERMISSIONS ejecutaremos la petición
        de obtención de información.
         */
        if (hasPermissions(MainActivity.this, PERMISSIONS)) {

            try {
                executedSecureConAndGetInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /*
    Método que cumple con la política de Android para enviar peticiones de red y la obtención
    de datos.
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void executedSecureConAndGetInfo() throws IOException {
        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            getInfo();

        }
    }

    /*
    Método que nos permite comprobar en tiempo de ejecución si el usuario tiene los permisos
    necesarios.
     */
    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {

            for (String permission : PERMISSIONS) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    /*
    Método que obtiene datos y credenciales del dispositivo móvil, tipo de conexión y antena GSM
    a la que está actualmente conectada.
     */

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getInfo() throws IOException {

        TelephonyManager tpm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GsmCellLocation cellLocation = (GsmCellLocation) tpm.getCellLocation();

        Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        System.out.println("---INFO MOBILE, SIGNAL AND GSM---");
        System.out.println(tpm.getAllCellInfo().toString());
        System.out.println("---GET SIGNAL STRENGHT---");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            if (tpm == null)
                tpm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            System.out.println(tpm.getSignalStrength().getLevel());
        }

        System.out.println("---OPERADOR---\n");
        System.out.println(tpm.getNetworkOperator() + " : " + tpm.getNetworkOperatorName() + ": " + tpm.getNetworkType() + ": " + tpm.getLine1Number());

        System.out.println("---MCC Y MNC");
        int mcc = Integer.parseInt(tpm.getNetworkOperator().substring(0, 3));
        int mnc = Integer.parseInt(tpm.getNetworkOperator().substring(3));
        System.out.println(mcc + " : " + mnc);
        System.out.println("---CID, LAC, PSC?---\n");
        System.out.println(cellLocation.getCid());
        System.out.println(cellLocation.getLac());
        System.out.println(cellLocation.getPsc());

        System.out.println(tpm.getNetworkCountryIso());
//
        System.out.println("---Ubicación exacta---");

        LocationApi locationCliente = new LocationApi("pk.148fa75e6fa9abc41c49cfbe12c2119d", mcc, mnc,
                cellLocation.getLac(), cellLocation.getCid());

        System.out.println(locationCliente.getResponse());

        Date currentTime = Calendar.getInstance().getTime();

        saveResults(currentTime + "\n\n" + locationCliente.getResponse() + "\n\n" + locationCliente.getJSONInputString(),
                getApplicationContext());

    }

    /*
    Callback que devuelve el resultado de la operación de solicitar permisos.
     */

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Phone State Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Phone State Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "STORAGE Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "STORAGE Permission is denied", Toast.LENGTH_SHORT).show();
            }

            try {
                executedSecureConAndGetInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*  Método que comprueba si la SD está montada, si así es guarda en el archivo GSM_Results.txt
     * la información y credenciales sobre la conexión GSM y
     * la ubicación de la antena que proporciona conectividad al usuario */

    public void saveResults(String data, Context context) throws IOException {

        //Directorio
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        // Nombre archivo dentro directorio
        File myFile = new File(folder, "GSM_Results.txt");
        writeData(myFile, data);
        System.out.println("Archivo guardado en: " + myFile
                .getAbsolutePath());


    }

    /*
    Método que se encarga de escribir el fichero en el almacenamiento externo SD.
     */
    private void writeData(File myFile, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myFile);
            fileOutputStream.write(data.getBytes());
            Toast.makeText(this, "Guardado" + myFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
