package assistive.com.sequencelogger;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by andre on 27-May-15.
 */
public class Settings extends Activity {
    private static String postLogFile = "http://accessible-serv.lasige.di.fc.ul.pt/~lost/log/uploadSequence.php";
    private static final String KEY_MAC_ADDRESS = "mac_address";
    private static final String KEY_CURRENT_IP_ADDRESS = "current_ip_address";
    private final static String TAG = "SequenceLog";
    private static Context c;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        c=this;

    }

    public void sync(View v){
        String mac =getMac();
        mac = mac.replaceAll(":","");
        uploadLogFile(mac);
    }


    private String getMac() {
        WifiManager wifiMan = (WifiManager) this.getSystemService(
                getApplicationContext().WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String macAddr = wifiInf.getMacAddress();
        return macAddr;
    }

    public static void uploadLogFile(final String nodeid) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                String fileName = "sequences.json";
                String filePath = Environment.getExternalStorageDirectory()
                        + "/";

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;
                File sourceFile = new File(filePath + fileName);

                if (!sourceFile.isFile()) {

                    Log.e("uploadFile", "Source File not exist" + fileName);

                    return;

                } else {
                    try {

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(postLogFile);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        // conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setChunkedStreamingMode(maxBufferSize);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("uploaded_file", nodeid + "_"
                                + System.currentTimeMillis() + ".json");

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename='"
                                + nodeid
                                + "_"
                                + System.currentTimeMillis()
                                + ".json" + "'" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);
                            dos.flush();

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        Log.i("uploadFile", "HTTP Response is : "
                                + serverResponseMessage + ": "
                                + serverResponseCode);

                        if (serverResponseCode == 200) {
                            Toast.makeText(c.getApplicationContext(), (String)"Uploaded successful",
                                    Toast.LENGTH_LONG).show();
                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                        sourceFile.delete();

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
