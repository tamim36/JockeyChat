package com.arefin.jockeychat.storiesthing.Utils;

import java.util.List;

public interface iFirebaseLoadDone {
    void onFirebaseLoadSuccess(List<Photos> photosList);
    void onFirebaseLoadFailed(String message);
}
