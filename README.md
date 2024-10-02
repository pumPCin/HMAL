# HMAL (fork of Hide My Applist https://github.com/Dr-TSNG/Hide-My-Applist)

![banner](banner.png)

## About this Xposed (LSPosed) module app

Although it's bad practice to detect the installation of specific apps, not every app using root provides random package name support. In this case, if apps related to root (such as Fake Location and Storage Isolation) are detected, it is tantamount to detecting that the device is rooted.

Additionally, some apps use various loopholes to acquire your app list, in order to use it as fingerprinting data or for other nefarious purposes.

This module can work as an Xposed module to hide apps or reject app list requests, and provides some methods to test whether you have hidden your app list properly.

## Update Log
[Reference to the release page](https://github.com/pumPCin/HMAL/releases)  
