# Mapwize UI

![Gitlab pipeline status](https://img.shields.io/gitlab/pipeline/mapwize/mapwize-ui-android-mirror/master)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/mapwize/mapwize-ui-android)
![JitPack](https://img.shields.io/jitpack/v/github/mapwize/mapwize-ui-android)

Fully featured and ready to use Fragment to add Mapwize Indoor Maps and Navigation in your Android app.

And it's open-source !

For documentation about Mapwize SDK objects like Venue, Place, MapOptions... Please refer to the Mapwize SDK documentation on [docs.mapwize.io](https://docs.mapwize.io).

## Description

The Mapwize UI fragment comes with the following components:

- Mapwize SDK
- Floor controller
- Follow user button
- Search module
- Direction module
- Place selection
- Universes button
- Languages button

## Installation

MapwizeUI is compatible with MapwizeSDK 3.0.0 and above. The library won't work with lower version.

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

The activity that embeds the Fragment must implement `MapwizeUIView.OnViewInteractionListener` with the followings methods :

```java
// The user clicked on the menu button (left button on the search bar).
void onMenuButtonClick()
// See Information Button section below.
void onInformationButtonClick(MapwizeObject mapwizeObject)
// The fragment is ready to use.
void onFragmentReady(MapwizeMap mapwizeMap)
// The user clicked on the follow user button but no location has been found.
void onFollowUserButtonClickWithoutLocation();
// Method called when a place or a place list is selected. Return true if you want to show the information button in the bottom view.
default boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject) {
    return true;
}
// Method called when the available floors list changed. Return true if you want to display the floor controller.
default boolean shouldDisplayFloorController(List<Floor> floors) {
    return true;
}
```

Mapwize Fragment can be instantiated with the constructor :

```java
public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions)
public static MapwizeFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions)
public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings)
public static MapwizeFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings)
public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings, @NonNull MapboxMapOptions mapboxMapOptions)
public static MapwizeFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings, @NonNull MapboxMapOptions mapboxMapOptions)
```

### Access to MapwizeMap

The `void onFragmentReady(MapwizeMap mapwizeMap);` contains both Mapbox map and Mapwize map. Once this method is called you can store and use them.

### Simple example

```java
MapOptions opts = new MapOptions.Builder()
        .language(Locale.getDefault().getLanguage())
        .build();
MapwizeFragment mapwizeFragment = MapwizeFragment.newInstance(opts);
```

### Center on venue

To have the map centered on a venue at start up:

```java
MapOptions opts = new MapOptions.Builder()
    .centerOnVenue("YOUR_VENUE_ID")
    .build();
MapwizeFragment mapwizeFragment = MapwizeFragment.newInstance(opts);
```

### Center on place

To have the map centered on a place with the place selected: 

```java
MapOptions opts = new MapOptions.Builder()
    .centerOnPlace("YOUR_PLACE_ID")
    .build();
MapwizeFragment mapwizeFragment = MapwizeFragment.newInstance(opts);
```

### Map options

The following parameters are available for map initialization:

- `floor` to set the default floor when entering a venue. Floors are Double and can be decimal values.
- `language` to set the default language for venues. It is a string with the 2 letter code for the language. Example: "fr" or "en".
- `universeId` to set the default universe for the displayed venue.
- `restrictContentToVenueId` to show only the related venue on the map.
- `restrictContentToVenueIds` to show only the related venues on the map.
- `restrictContentToOrganizationId` to show only the venues of that organization on the map.
- `centerOnPlace` to center on a place at start.
- `centerOnVenue` to center on a venue at start.

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
* @param directionMode determine the mode used by the direction
*/
public void setDirection(Direction direction, DirectionPoint from, DirectionPoint to, DirectionMode directionMode)

/**
* Friendly method to add new access to the map and refresh the UI
* @param accesskey that provide new access right
* @param callback called when the access is done
*/
public void grantAccess(String accesskey, ApiCallback<Boolean> callback)
```

## Place details

Place details provides you with a ready to use, yet customizable UI.
You can have a full control over the displayed buttons and rows.

You can use the following callback to control the Buttons and the Rows of the Details UI.

```java
/**
* Setup the UI to display details about the selected place
* @param mapwizeObject the selected place/placelist
* @param placeDetailsConfig the place details configuration
*/
boolean onPlaceSelected(MapwizeObject mapwizeObject, PlaceDetailsConfig placeDetailsConfig) {
            return placeDetailsConfig;
        }
```

The `placeDetailsConfig` object contains:

```java
List<ButtonSmall> buttonsSmall;
List<ButtonBig> buttonsBig;
List<Row> rows;
boolean preventExpandDetails = false;
```

You have full control over these objects. You can modify them, change their order or remove some of them.


The `shouldDisplayInformationButton` callback is called before the `onPlaceSelected`.
The `onPlaceSelected` callback will have the last word on the display of the Rows or Buttons.

You can set `preventExpandDetails` to `true` in order to display the legacy place details (without the new Rows and Buttons).

### Managing Buttons

You can create a Small Button object using the `ButtonSmall(@NonNull Context context, String label, int icon, boolean highlighted, int buttonType, OnClickListener clickListener)` constructor.

To change the label of a particular Button, you can find it using its category with the `getButtonType` method that returns one of the following values: `ButtonSmall.DIRECTION_BUTTON`, `Row.CALL_BUTTON`, `Row.WEBSITE_BUTTON`, `Row.SHARE_BUTTON`, `Row.INFORMATION_BUTTON`, `Row.OTHER`.

The **Big Buttons** also can be managed the same way as the Small Buttons.

### Managing Rows
You can create a Row object using: `Row(@NonNull Context context, String label, int icon, boolean available, int rowType, OnClickListener clickListener)` constructor.

To change the label of a particular Row,  you can find it using its category with the `getRowType` method that returns: `Row.FLOOR_ROW`, `Row.OPENING_TIME_ROW`, `Row.PHONE_NUMBER_ROW`, `Row.WEBSITE_ROW`, `Row.CAPACITY_ROW`, `Row.OCCUPANCY_ROW`,  `Row.OTHER`.


## Information button

When users select a Place or a PlaceList, either by clicking on the map or using the search engine, you might want to give the possibility to the user to open a page of your app about it. Think about shops or exhibitors for example for which your app probably has a page with all the details about.

The proposed solution is to display an "information" button on the bottom view in the Fragment.

Using the OnFragmentListener interface, you can use the method `shouldDisplayInformationButton` to say if the button should be displayed or not. Return true to display the button for the given Mapwize object.

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
<string name="mapwize_time_placeholder">%1$d min</string>
<string name="mapwize_floor_placeholder">Floor %1$s</string>
<string name="mapwize_search_in_placeholder">Search in %1$s…</string>
<string name="mapwize_search_venue">Search a venue…</string>
<string name="mapwize_loading_venue_placeholder">Loading %1$s…</string>
<string name="mapwize_current_location">Current location</string>
<string name="mapwize_no_result">No result found</string>
<string name="mapwize_choose_language">Choose your language</string>
<string name="mapwize_choose_universe">Choose your universe</string>
<string name="mapwize_direction">Direction</string>
<string name="mapwize_information">Information</string>
<string name="mapwize_starting_point">Starting point</string>
<string name="mapwize_destination">Destination</string>
```

Be careful with strings containing placeholders. Please ensure that the exact placeholders are kept!
For example, if you replace "Floor %1$s" with "My floor" without placeholder, your application will crash.

## Demo application

A demo application is available in this repository to quickly test the UI. 
The only thing you need to get started is a Mapwize api key. 
You can get your key by signing up for a free account at [mapwize.io](https://www.mapwize.io).

Once you have your API key, add it to the project in the MapwizeApplication.java file and run the app.

To test the UI further, go to MainActivity.java and change some options or add some code in it.

## Analytics

Mapwize SDK and Mapwize UI do __not__ have analytics trackers built in. This means that Mapwize does not know how maps are used in your applications, which we believe is a good thing for privacy. This also means that Mapwize is not able to provide you with analytics metrics and that, if you want any, you will have to intrument your code with your own analytics tracker.

Events and callbacks from Mapwize SDK and Mapwize UI can be used to detect changes in the interface and trigger tracking events.

Events from the core SDK can be accessed using the listeners on the `MapwizeMap` class. We believe using the following events would make sense:

- `MapwizeMap.OnFloorChangeListener`
- `MapwizeMap.OnFloorsChangeListener`
- `MapwizeMap.OnLanguageChangeListener`
- `MapwizeMap.OnUniverseChangeListener`
- `MapwizeMap.OnVenueEnterListener`
- `MapwizeMap.OnVenueExitListener`

Other events are available directly in MapwizeUI and are accessible using the `EventManager` singleton. We believe using the following events would make sense:

- `onContentSelect` for both places and placelist
- `onDirectionStart`
