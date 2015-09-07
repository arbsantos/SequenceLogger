package assistive.com.sequencelogger;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class SequenceManager {

    private final static String TAG = "SequenceLog";
    private static final String INIT_FILE = "{\"Sessions\":[";
    private static SequenceManager mSharedInstance = null;
    private final String INIT_SEQUENCE = "{\"sequence\":[";
    private final String END_SEQUENCE = "]},";
    private long lastWrite = 0;
    private String filepath;
    private ArrayList<Step> currentSequence;

    //TO ANALYZE LOGS ADD THIS TO THE  END OF THE JSON AND DELETE THE LAST COMMA
    private static final String END_FILE = "]}";

    public SequenceManager() {
        filepath = Environment.getExternalStorageDirectory().toString()
                + "/sequences.json";
        currentSequence = new ArrayList<Step>();
    }

    public static SequenceManager sharedInstance() {
        if (mSharedInstance == null)
            mSharedInstance = new SequenceManager();
        return mSharedInstance;
    }


    public void createSequence() {
        Log.d(TAG, "new sequence");
        currentSequence = new ArrayList<Step>();
    }

    public boolean addStep(String step, String alternative, String application) {
        long time = System.currentTimeMillis();
        return currentSequence.add(new Step(step, alternative, time, application));
    }

    public void finishSequence() {
        File file = new File(filepath);
        boolean exists = file.exists();
        FileWriter fw;
        try {
            fw = new FileWriter(file, true);
            if (!exists) {
                fw.write(INIT_FILE);
            }
            fw.write(INIT_SEQUENCE);
            boolean first = true;
            for (Step step : currentSequence) {
                if (first) {
                    fw.write(step.toString());
                    first = false;
                } else {
                    fw.write(" , " + step.toString());
                }
            }
            fw.write(END_SEQUENCE);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        createSequence();
    }


}
