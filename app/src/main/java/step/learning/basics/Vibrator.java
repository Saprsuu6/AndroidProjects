package step.learning.basics;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.VibratorManager;

import step.learning.basics.Calculator.CalcActivity;

public class Vibrator {
    private final android.os.Vibrator vibrator;
    private final long[] vibrationPattern = {0, 200, 100, 200};

    public Vibrator(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) activity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = (android.os.Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    public void run() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1));
        } else {
            //vibrator.vibrate(300);
            vibrator.vibrate(vibrationPattern, -1);
        }
    }
}
