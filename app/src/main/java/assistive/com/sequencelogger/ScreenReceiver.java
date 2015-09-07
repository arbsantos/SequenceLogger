package assistive.com.sequencelogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by andre on 27-May-15.
 */
public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            SequenceManager sm = SequenceManager.sharedInstance();
            sm.finishSequence();
        }
    }

}
