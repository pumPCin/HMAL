package icu.nullptr.hidemyapplist.common;

interface IHMAService {

    void stopService(boolean cleanEnv) = 0;

    void syncConfig(String json) = 1;

    int getServiceVersion() = 2;
}
