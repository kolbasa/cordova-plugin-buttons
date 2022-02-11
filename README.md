Cordova Buttons Plugin
=========================

This plugin allows you to listen to the hardware buttons on Android devices.

# Installation

### Cordova

    cordova plugin add cordova-plugin-buttons

### Ionic + Cordova

    ionic cordova plugin add cordova-plugin-buttons

### Capacitor

    npm install cordova-plugin-buttons

# API

### subscribe()

```js
Buttons
    .subscribe(function(button) {
        // your callback
    })
    .then(function () {
        // started
    })
    .catch(function (err) {
        // something broke
    });
```

### unsubscribe()

```js
Buttons
    .unsubscribe()
    .then(function () {
        // stopped
    })
    .catch(function (err) {
        // something broke
    });
```