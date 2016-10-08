#Simple News Reader

Rod Bailey
Sunday 9 October 2016

##Summary
This is an Android coding exercise. The app is called `SimpleNewsReader` and reads in a list of news items read from a well defined remote JSON file. It supports local caching of images and text and the ability to manually request a refresh of all the loaded data.

##Screenshots

![Portrait](/snr_screenshot_portrait.png)

#Running the app
You should be able to import the contents of this Github repository directly into Android Studio 2 or later:

- Select menu `VCS` then menu item `Checkout from version control >` 
- Select `Github`. 
- In the resulting dialog, enter a Github repository URL of `https://github.com/replicant1/SimpleNewsReader`.

To run the app you will need an AVD or real device that has at least Android API level 16 (4.1 JELLY BEAN) and internet connectivity.


## Test Coverage

TBD

##Assumptions
Being a simple coding exercise, I have made a few simplifying assumpions:

- News items that don't have **both** a title and a description should not be displayed at all.
- The location from which the JSON is loaded won't change regularly - it is configured in `assets/config.proerties`
- The presence of the right arrow `>` in the requirements document at the right of each list item is an oversight. No functionaity for it is defined, so it should be omitted.

##Technologies
The following libraries are used:

- **Volley** - by Google. Handles the mechanics of the HTTP request for downloading the JSON. Saves me having to deal with redirects etc.
- **GSON** - by Google. Handles the parsing of the JSON data into data objects you provide.
- **JUnit** - for unit testing.
- **UIL** - Android Universal Image Loader. Asynchronously loads and caches the images referenced from the JSON.