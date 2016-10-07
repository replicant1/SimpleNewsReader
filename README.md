#Simple News Reader

Rod Bailey
Sunday 9 October 2016

##Summary
This is an Android coding exercise. The app is called `SimpleNewsReader` and reads in a list of news items read from a well defined remote JSON file.

##Assumptions
Being a simple coding exercies, I have made a few simplifying assumpions:

- News items that don't have **both** a title and a description should not be displayed at all.
- The location from which the JSON is loaded won't change - it is configured in `assets/config.proerties`

##Technologies
The following libraries are used:

- **Volley** - by Google. Handles the mechanics of the HTTP request for downloading the JSON. Saves me having to deal with redirects etc.
- **GSON** - by Google. Handles the parsing of the JSON data into data objects you provide.
- **Espresso** - by Google. Enables instrumented testing i.e. testing with an Application context available.