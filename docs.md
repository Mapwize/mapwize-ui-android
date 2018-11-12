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

### Gradle

TODO

### Manual

- Clone the Github repository
- Import mapwize-ui module in your android project (File -> New -> Import Module)

## Initialization

The activity that embeds the Fragment must implement `OnFragmentInteractionListener` with the followings methods :

```java
void onMenuButtonClick() // The user clicked on the menu button (left button on the search bar)
void onInformationButtonClick(Place place); // The user clicked on the informations button (in the bottom view when a view is selected)
void onFragmentReady(MapboxMap mapboxMap, MapwizePlugin mapwizePlugin); // The fragment is ready to use
```

Mapwize Fragment can be instantiated with the constructor :

```java
public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions)
```

Example: 

```java
MapOptions opts = new MapOptions.Builder().build();
MapwizePlugin mapwizePlugin = MapwizeFragment.newInstance(opts);
```

### Center on venue

To have the map centered on a venue at start up:

```java
MapOptions opts = new MapOptions.Builder().build();
MapwizePlugin mapwizePlugin = MapwizeFragment.newInstance(opts);
```

### Center on place

To have the map centered on a place with the place selected: 

```java
MapOptions opts = new MapOptions.Builder().build();
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

### Dynamic components

Dans certains cas, il peut être intéressant de pouvoir afficher ou non certains composants. C'est notamment le cas pour le boutton information quand une place est selectionnée.

Le fragment dispose d'une méthode qui permet de configurer un UIBehaviour qui est une interface contenant la méthode suivante : 

```java
boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject);
```

Un exemple d'utilisation :

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

### Colors 

The fragment uses two colors to display his content :

```xml
<color name="colorAccent">#C51586</color>
<color name="mapwize_main_color">#C51586</color>
```

You can override them in your colors.xml file to customize colors.
'colorAccent' changes the loading bar's color.
'mapwize_main_color' changes ui main color such as buttons.

### Translations

The fragment contains some strings that you may want to translate or change. 
You can override them in your strings.xml file to customize colors.

```xml
<string name="time_placeholder">%1$d min</string>
<string name="floor_placeholder">Floor %1$s</string>
<string name="search_in_placeholder">Search in %1$s…</string>
<string name="search_venue">Search a venue…</string>
<string name="loading_venue_placeholder">Loading %1$s…</string>
<string name="current_location">Current location</string>
<string name="choose_language">Choose your language</string>
<string name="choose_universe">Choose your universe</string>
<string name="direction">Direction</string>
<string name="information">Information</string>
<string name="starting_point">Starting point</string>
<string name="destination">Destination</string>
```

Be carreful with string that contains placeholder. If you want to change them, you have to ensure that they still contain a place holder value.
Example : if you replace "Floor %1$s" with "My floor" without place holder, your application will crash.
