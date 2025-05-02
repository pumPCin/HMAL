# HMAL (Hide My AppList)

![banner](banner.png)

## About this Xposed (LSPosed) module app
Although it's bad practice to detect the installation of specific apps, not every app using root provides random package name support. In this case, if apps related to root (such as Fake Location and Storage Isolation) are detected, it is tantamount to detecting that the device is rooted.

Additionally, some apps use various loopholes to acquire your app list, in order to use it as fingerprinting data or for other nefarious purposes.

This module can work as an Xposed module to hide apps or reject app list requests, and provides some methods to test whether you have hidden your app list properly.

## Fork differences with the original module HMA
- Removed: Google Services, ads, logcat logs, filtered counters, network activity.
- Changed: Package name, log filename, other little things in UI.

## Guide
https://github.com/mModule/guide_hma

## Update Log
[Reference to the release page](https://github.com/pumPCin/HMAL/releases)  

## Support me
[Donate](https://new.donatepay.ru/@1377115)
