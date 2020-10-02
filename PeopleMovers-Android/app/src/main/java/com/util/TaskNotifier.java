package com.util;

/**
 * Created by Sandip Phatangare, Nextremer solutions India Pvt Ltd  on 23-06-2016.
 */
public interface TaskNotifier {
    public  void onSuccess(String message);
    public void onError(String message);
}
