package sample.sdk.prime.com.mysamplecode.internal.utils;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;

public class CallbackHandlers {

    public static class CallbackToastHandler implements CommonCallbacks.CompletionCallback {

        @Override
        public void onResult(DJIError djiError) {
            String message = "Success";
            if (djiError != null) {
                message = djiError.getDescription();
            }
            ToastUtils.setResultToToast(message);
        }
    }

}
