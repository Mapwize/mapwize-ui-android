# Mapwize UI

Fully featured and ready to use Fragment to add Mapwize Indoor Maps and Navigation in your Android app.

And it's open-source !

For documentation about Mapwize SDK objects like Venue, Place, MapOptions... Please refer to the Mapwize SDK documentation on [docs.mapwize.io](https://docs.mapwize.io).

## Description

The Mapwize UI fragment comes with the following components:

- Mapwize SDK intégration
- Floor controller
- Follow user button
- Search module
- Direction module
- Place selection
- Universes button
- Languages button

## Installation

MapwizeUI is compatible with MapwizeForMapbox 1.7.0 and above. The library won't work with lower version.

### Gradle

Add the following maven repository to your project :

```
maven { url "https://jitpack.io" }
maven { url 'https://maven.mapwize.io'}
```

Then, in your application project, import the MapwizeUI module :

```
implementation 'com.github.Mapwize:mapwize-ui-android:${lib-version}'
```

### Manual

- Clone the Github repository
- Import mapwize-ui module in your android project (File -> New -> Import Module)

## Initialization

The activity that embeds the Fragment must implement `OnFragmentInteractionListener` with the followings methods :

```java
void onMenuButtonClick() // The user clicked on the menu button (left button on the search bar)
void onInformationButtonClick(MapwizeObject mapwizeObject); // See Information Button section below
void onFragmentReady(MapboxMap mapboxMap, MapwizePlugin mapwizePlugin); // The fragment is ready to use
```

Mapwize Fragment can be instantiated with the constructor :

```java
public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions)
public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings)
public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings, @NonNull MapboxMapOptions mapboxMapOptions)
```

### Simple example

```java
MapOptions opts = new MapOptions.Builder().build()
MapwizePlugin mapwizePlugin = MapwizeFragment.newInstance(opts);
```

### Center on venue

To have the map centered on a venue at start up:

```java
MapOptions opts = new MapOptions.Builder()
.centerOnVenue("YOUR_VENUE_ID")
.build();
MapwizePlugin mapwizePlugin = MapwizeFragment.newInstance(opts);
```

### Center on place

To have the map centered on a place with the place selected: 

```java
MapOptions opts = new MapOptions.Builder()
.centerOnPlace("YOUR_PLACE_ID")
.build();
MapwizePlugin mapwizePlugin = MapwizeFragment.newInstance(opts);
```

### Map options

The following parameters are available for map initialization:

- `centerOnVenue` to center on a venue at start. Builder takes either a venueId or a venue object.
- `centerOnPlace` to center on a place at start. Builder takes either a placeId or a place object.
- `floor` to set the default floor when entering a venue. Floors are Double and can be decimal values. This is ignored when using centerOnPlace.
- `language` to set the default language for venues. It is a string with the 2 letter code for the language. Example: "fr" or "en".
- `universe` to set the default universe for the displayed venue. If using centerOnPlace, this needs to be an universe the place is in. Builder takes either an universeId or an universe object.
- `restrictContentToVenue` to show only the related venue on the map. Builder takes either a venueId or a venue object.
- `restrictContentToOrganization` to show only the venues of that organization on the map. Builder takes an organization id.

### UI Settings

UISettings are use for initialization only. Changing this after the initialization won't impact the user interface.

The following parameters are available to show or hide some components :

`menuButtonHidden` set to true to hide the menu button in the search bar
`followUserButtonHidden` set to true to hide the follow user mode button
`floorControllerHidden` set to true to hide the floor controller
`compassHidden` set to true to hide the compass

## Public methods

```java

/**
* Setup the UI to display information about the selected place
* Promote the place and add a marker on it
* @param place the selected place
* @param centerOn if true, center on the place
*/
public void selectPlace(Place place, boolean centerOn)

/**
* Setup the UI to display information about the selected venue
* @param venue the venue to select
*/
public void selectVenue(Venue venue)

/**
* Setup the UI to display information about the selected placelist
* Add markers on places contained in the placelist and promote them
* @param placeList the selected placelist
*/
public void selectPlaceList(PlaceList placeList)

/**
* Hide the UI component, remove markers and unpromote place if needed
* If we are in a venue, displayed the venue information
*/
public void unselectContent()

/**
* Display a direction object and show the direction UI already configured
* @param direction to display
* @param from the starting point
* @param to the destination point
* @param isAccessible determine if the direction should be accessible to low mobility people
*/
public void setDirection(Direction direction, DirectionPoint from, DirectionPoint to, boolean isAccessible)

/**
* Friendly method to add new access to the map and refresh the UI
* @param accesskey that provide new access right
* @param callback called when the access is done
*/
public void grantAccess(String accesskey, ApiCallback<Boolean> callback)
```

## Information button

When users select a Place or a PlaceList, either by clicking on the map or using the search engine, you might want to give the possibility to the user to open a page of your app about it. Think about shops or exhibitors for example for which your app probably has a page with all the details about.

The proposed solution is to display an "information" button on the bottom view in the Fragment.

Using the UIBehaviour interface, you can use the method `shouldDisplayInformationButton` to say if the button should be displayed or not. Return true to display the button for the given Mapwize object.

```java
boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject);
boolean shouldDisplayFloorController(List<Double> floors);
```

Example to display the information button only for Places and not for PlaceLists:

```java
@Override
boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject) {
if (mapwizeObject instanceof Place) {
return true
}
else {
return false
}
}
```

When the information button is clicked, the listener `onInformationButtonClick` is called with the selected Mapwize object.

```java
void onInformationButtonClick(MapwizeObject mapwizeObject); 
```

## Colors 

The fragment uses two colors to display its content:

```xml
<color name="colorAccent">#C51586</color>
<color name="mapwize_main_color">#C51586</color>
```

You can override them in your `colors.xml` file to customize colors.

- `colorAccent` changes the loading bar's color.
- `mapwize_main_color` changes UI main color such as buttons.

## Translations

The fragment contains some strings that you may want to translate or change.
You can override them in your `strings.xml` file.

```xml
<string name="time_placeholder">%1$d min</string>
<string name="floor_placeholder">Floor %1$s</string>
<string name="search_in_placeholder">Search in %1$s…</string>
<string name="search_venue">Search a venue…</string>
<string name="loading_venue_placeholder">Loading %1$s…</string>
<string name="current_location">Current location</string>
<string name="no_result">No result found</string>
<string name="choose_language">Choose your language</string>
<string name="choose_universe">Choose your universe</string>
<string name="direction">Direction</string>
<string name="information">Information</string>
<string name="starting_point">Starting point</string>
<string name="destination">Destination</string>
```

Be careful with strings containing placeholders. Please ensure that the exact placeholders are kept!
For example, if you replace "Floor %1$s" with "My floor" without placeholder, your application will crash.

## Demo application

A demo application is available in this repository to quickly test the UI. 
The only thing you need to get started is a Mapwize api key. 
You can get your key by signing up for a free account at [mapwize.io](https://www.mapwize.io).

Once you have your API key, add it to the project in the MapwizeApplication.java file and run the app.

To test the UI further, go to MainActivity.java and change some options or add some code in it.
