# This is a legacy Firebase example (for SDK 2.x.x). You probably want to use one of the up-to-date examples at https://firebase.google.com/docs/samples

---

# Inetech Office Mover 5000

Inetech Office Mover 5000 is a cross-platform example of a collaborative app written on
[Firebase](https://firebase.com). It helps you and your coworkers plan the layout of your
office furniture with a realtime drag-and-drop interface.

It illustrates using Firebase on web, iOS, and Android. It also illustrates Firebase login
with Google.

Check out the [web demo](https://office-mover-demo.firebaseapp.com/).

![screenshot of office mover](web-screencap.png)

## What's here

This repository contains a client for each platform, and data common across all three.

- [HTML & JavaScript web client](/web)
- [Java Android client](/android)
- [Swift iOS client](/ios)
- [Firebase security rules](security-rules.json)

## Setup

0. This is a pretty advanced example project, so if you're not already familiar with Firebase,
   run through one of our [quickstarts](https://www.firebase.com/docs/web/quickstart.html) and
   [platform guides](https://www.firebase.com/docs/web/guide/).
0. Copy the [security rules](security-rules.json) into your Firebase, either by pasting into
   Firebase Dashboard or add your Firebase to `firebase.json` and deploy the web version with
   [Firebase command line tools](https://www.firebase.com/docs/hosting/command-line-tool.html).
0. Proceed to the setup instructions for your preferred platform.
    - [Android](/android)
    - [iOS](/ios)
    - [Web](/web)
