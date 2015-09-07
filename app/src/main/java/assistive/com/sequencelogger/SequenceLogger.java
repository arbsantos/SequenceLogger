package assistive.com.sequencelogger;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.w3c.dom.NodeList;


/**
 * Created by andre on 25-May-15.
 */
public class SequenceLogger extends AccessibilityService {
    final String TAG = "SequenceLog";
    private SequenceManager sm = null;
    private final int THRESHOLD=100;

    /**
     * Gets the node text either getText() or contentDescription
     *
     * @param src
     * @return node text/description null if it doesnt have
     */
    public static String getText(AccessibilityNodeInfo src) {
        String text = null;

        if (src.getText() != null || src.getContentDescription() != null) {
            if (src.getText() != null)
                text = src.getText().toString();
            else
                text = src.getContentDescription().toString();
            src.recycle();
        }

        return text;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo source = event.getSource();
        String app = event.getPackageName().toString();
        String alt = "";
        String step = "";
        if (event.getText().size() > 0) {
            step = event.getText().get(0).toString();
        }

        source = event.getSource();
        if (source == null) {
            sm.addStep(step, alt, app);
            return;
        }

        String sourceText = getDescription(source);
        if (step.length() > 0)
            alt = sourceText;
        else
            step = sourceText;
        sm.addStep(step, alt, app);

    }



    /**
     * Get root parent from node source
     *
     * @param source
     * @return
     */
    private AccessibilityNodeInfo getRootParent(AccessibilityNodeInfo source) {
        AccessibilityNodeInfo current = source;
        while (current.getParent() != null) {
            AccessibilityNodeInfo oldCurrent = current;
            current = current.getParent();
            oldCurrent.recycle();
        }
        return current;
    }

    /**
     * If creating macro is active it sends the text of the clicked node
     */
    private String getDescription(AccessibilityNodeInfo src) {
        try {
            if (src != null) {
                String text;
                if ((text = getText(src)) != null) {
                    return cleanText(text);
                }
                else {
                    int numchild = src.getChildCount();
                    for (int i = 0; i < numchild; i++) {
                        if ((text = getText(src.getChild(i))) != null) {
                            return  cleanText(text);
                        } else {
                            src.getChild(i).recycle();
                        }
                    }
                    src = src.getParent();
                    numchild = src.getChildCount();
                    for (int i = 0; i < numchild; i++) {
                        if ((text = getText(src.getChild(i))) != null) {

                            return cleanText(text);
                        } else {
                            src.getChild(i).recycle();
                        }
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    private String cleanText(String text) {
      String  result = text.replaceAll("\""," ");
        result = text.replaceAll("\'"," ");
        result = result.substring(0, Math.min(result.length(),THRESHOLD));
        return result;
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        sm = sm.sharedInstance();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        Log.d(TAG, "CONNECTED");

    }




}
