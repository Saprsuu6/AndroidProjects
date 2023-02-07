package step.learning.basics;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.VibratorManager;

public class Vibrator {
    private android.os.Vibrator vibrator;
    private long[] vibrationPattern = {0, 200, 100, 200};

    public Vibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) CalcActivity.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = (android.os.Vibrator) CalcActivity.context.getSystemService(Context.VIBRATOR_SERVICE);
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
