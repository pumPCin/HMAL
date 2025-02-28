package com.android.hmal.common;

interface IHMALService {

    void stopService(boolean cleanEnv) = 0;

    void syncConfig(String json) = 1;

    int getServiceVersion() = 2;
}
