package com.pavlov.smsparser;



public interface ServiceCallbacks {

    int SMSPARSER_SERVICE_DISCONNECT = 0;
    int SMSPARSER_SERVICE_AVAITING = 1;
    int SMSPARSER_SERVICE_CONNECTED = 2;

    void OnServiceStateChange(Boolean state);
}
