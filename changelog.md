# UI Components Changelog

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
