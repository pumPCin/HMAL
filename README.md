# HMAL

[![Stars](https://img.shields.io/github/stars/pumPCin/HMAL?label=Stars)](https://github.com/Dr-TSNG)
[![Build](https://img.shields.io/github/actions/workflow/status/pumPCin/HMAL/main.yml?branch=master&logo=github)](https://github.com/pumPCin/HMAL/actions)
[![Release](https://img.shields.io/github/v/release/pumPCin/HMAL?label=Release)](https://github.com/pumPCin/HMAL/releases/latest)
[![Download](https://img.shields.io/github/downloads/pumPCin/HMAL/total)](https://github.com/pumPCin/HMAL/releases/latest)
[![License](https://img.shields.io/github/license/pumPCin/HMAL?label=License)](https://choosealicense.com/licenses/gpl-3.0/)

![banner](banner.png)

- English  

## About this module

Although it's bad practice to detect the installation of specific apps, not every app using root provides random package name support. In this case, if apps related to root (such as Fake Location and Storage Isolation) are detected, it is tantamount to detecting that the device is rooted.

Additionally, some apps use various loopholes to acquire your app list, in order to use it as fingerprinting data or for other nefarious purposes.

This module can work as an Xposed module to hide apps or reject app list requests, and provides some methods to test whether you have hidden your app list properly.

## Update Log
[Reference to the release page](https://github.com/pumPCin/HMAL/releases)  
