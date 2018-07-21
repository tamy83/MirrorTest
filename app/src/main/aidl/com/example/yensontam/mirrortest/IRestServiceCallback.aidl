// IRestServiceCallback.aidl
package com.example.yensontam.mirrortest;

// Declare any non-default types here with import statements

interface IRestServiceCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void response(String value);
    void error(String message);
}
