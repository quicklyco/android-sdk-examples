# Zowdow Auto-Suggest SDK for Android

© 2015-2017 Zowdow, Inc.

The Zowdow Auto-Suggest SDK is intended to provide Android application developers with a convenient access to Zowdow services via native APIs.

## Version

Current version as of May 25, 2017 is 2.1

## Terminology

Zowdow is an autocomplete suggestions service. Therefore we operate with the following concepts:

*   **Fragment** - several characters (usually without whitespace), a phrase entered into a search box
*   **Suggestion** - a phrase returned by the service, which contains or is associated in some way with a given fragment

## Features implemented to date

There are 2 integration scenarios:

*   **Basic** - when we provide the data/adapter used to fill the list of suggestions - and Zowdow suggestions are on top of the list of your own suggestions
*   **Advanced** - when you have your own data/adapter and place Zowdow suggestions at arbitrary positions

# Basic API Integration

The API addresses a use case when you allow a user to input a phrase into a text field, and use that phrase as a fragment to receive autocomplete suggestions from the Zowdow service.

For example: User types in _**st**_ and the SDK sends _**'s'**_ and then _**'st'**_ to the Zowdow Auto-Suggest service and receives back suggestions that are then displayed to the user.

In [Basic Integration Guide](INTEGRATION_BASIC.md) you can find all essential instructions that will help
you to perform the quick basic Zowdow SDK integration in your app.

# Advanced API Integration

For more complex Zowdow integration & customization consider reading our [Advanced Integration Guide](INTEGRATION_ADVANCED.md).
 
Advanced integration includes:

*   additional Zowdow suggestions processing and errors handling;
*   mixing Zowdow suggestions with the ones from the sources used in your project;
*   Zowdow suggestions UI customization;
*   Trending suggestions Fragment implementation and customization;
*   Cards and Suggestions clicks handling;
*   events tracking.

# Sample project

For an example of integration - please see a Sample project contained as part of this SDK package.

# Contact

For technical support please email support@zowdow.com

## Thank You
