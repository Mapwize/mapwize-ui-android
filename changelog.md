# UI Components Changelog

## 2.1.2

- SelectPlacelist now center on the placelist 

## 2.1.1

- Fixing crash when clicking search bar at the very beginning

## 2.1.0

- Improving direction flow
- Fixing issue with multi universes search

## 2.0.5

- Including MapwizeSDK 3.1.5
- Fixing navigation does not use accessibility option

## 2.0.4

- Including MapwizeSDK 3.1.1

## 2.0.3

- Including MapwizeSDK 3.0.2

## 2.0.2

- Fixing place selection with options.centerOnPlaceId

## 2.0.1

- Updating mapwize-sdk to 3.0.1

## 2.0.0

- Changing dependency from MapwizeForMapbox to mapwize-sdk
- Improving direction search module
- Migrating to AndroidX

## 1.3.2

- Updating MapwizeForMapbox to 2.3

## 1.3.1

- Fixing crash on quick create and destroy

## 1.3.0

- Updating MapwizeForMapbox to 2.2

## 1.2.1

- Fixing crash when a direction is not found

## 1.2.0

- Updating Mapwize version to 2.1.5
- Moving methods from UIBehaviour interface to OnFragmentInteractionListener (iOS & Android parity)
- Adding method when click on followuser button without location
- Changing onInformationButtonClick argument from Place to MapwizeObject to handle PlaceList and Place
- Adding call to MapwizePlugin#onPause and MapwizePlugin#onResume in the fragment lifecycle
- Improving search results list with multiple universe
- Fixing inconsistant behaviour with differents UI components

## 1.1.0

- Adding support for MapwizeForMapbox 2.0.0
- Adding details in bottom view if the selected content has details to display
- Adding subtitle in search result
- Using navigation instead of direction if the user start from its current position
- Fixing loading bar display
- Displaying loading bar when searching for a direction
- Improving design

## 1.0.4

- Adding no result found in search result list
- Improving search direction UI behaviour
- Changing select and unselect methods visibility
- Adding grantAccess and setDirection methods
- Adding language button
- Adding initialization method that takes MapboxMapOptions as parameter

## 1.0.3

- Hidding swap button when in search
- Fixing crash on swap direction (PR #12 thanks to CodeFactoryPDabrowski)
- Fixing crash on search result if place's floor is null

## 1.0.2

- Moving UIBehaviour to another file
- Adding shouldDisplayFloorController method
- Providing default value to the methods in UIBehaviour
- UIBehaviour can now be set before the onFragmentReady call
- Fixing crash with search bar if not loading or if universe does not exist

## 1.0.1

- Adding options to show or hide the following component : menu button, follow user button, floor controller and compass.
- Adding support for navigation when the user has a Indoor location
- Updating Mapwize SDK dependencies to 1.7.1

## 1.0.0

- Publishing a first version of the UI Components module
