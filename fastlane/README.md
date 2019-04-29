fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew cask install fastlane`

# Available Actions
## Android
### android assemble_release
```
fastlane android assemble_release
```
Build local released apk and send msg to @SlowLaneBot
### android beta
```
fastlane android beta
```
Deploy a new version to the Google Play and send msg to @SlowLaneBot
### android deploy
```
fastlane android deploy
```
Deploy a new version to the Google Play and send msg to @SlowLaneBot
### android test
```
fastlane android test
```
Runs all the tests

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
