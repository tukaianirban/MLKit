This repository is meant to contain an app that demonstrates different on-device APIs/functionalities available from Google's MLKit.

Differentiation of what models are available (https://developers.google.com/ml-kit/migration):
1. ML Kit (https://developers.google.com/ml-kit/guides) contains all the on-device APIs 
2. Firebase Machine Learning (https://firebase.google.com/docs/ml) focuses on cloud APIs and custom model deployment


Artifacts for on-device APIs: https://developers.google.com/ml-kit/migration/android
These can operate fully even when the device is offline.

Typically the APIs allow 2 models:
1. Unbundled : The app does not contain the model inside it. The SDK (ML Kit) will download the models needed using Google Play Services from the phone.
2. Bundled : The app contains the models it will need and therefore should not need Google Play Services (not for downloading the models) later on.

In this respository, most of the packages will be using teh "Bundled" versions of apps so that the mobile device will not need an internet connection when running the app.

