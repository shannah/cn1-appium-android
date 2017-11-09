# Codename One Appium Android Test Driver

Appium test driver for Android apps developed using Codename One

## Testing Locally

In order to run the tests, you will need to install [Apache Maven](http://maven.apache.org), and Appium (according to the Appium [installation instructions](https://github.com/appium/appium).

You will then need to start appium, eg:

    appium

Then, make sure you have the ANDROID_HOME environment variable set to the location of your android installation.

    export ANDROID_HOME=/path/to/android_home

To compile and run all tests, run:

    mvn test [options]

Where `[options]` should include your command-line options:

* `-Dapp=/path/to/myapp.apk` - The path to the apk file to test.  Required.
* `-DdeviceName=[Device Name]` - The device name to test on.  Get a list of available devices by running `adb devices -l`.


## Testing On Amazon Device Farm

To do
