package assistive.com.sequencelogger;

import android.util.Log;

/**
 * Created by andre on 27-May-15.
 */
public class Step {
    private String name;
    private String alternative;
    private long timestamp;
    private String application;
    private final static String TAG = "SequenceLog";

    public Step(String name, String alternative, long timestamp, String application) {
        this.name = name;
        this.timestamp = timestamp;
        this.application = application;
        this.alternative = alternative;

        //Log.d(TAG, this.toString());

    }

    public String getName() {
        return name;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public String getApplication() {
        return application;
    }

    public String getAlternative() {
        return alternative;
    }

    @Override
    public String toString() {
        return "{\"step\":\"" + name + "\""+
                " , \"alt\":\"" + alternative + "\""+
                " , \"timestamp\":" + timestamp +
                " , \"app\":\"" + application + "\" }";
    }

}
