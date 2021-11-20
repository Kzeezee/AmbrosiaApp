<h1 align="center">Ambrosia App</h1>
 <p align="center"><img src="https://user-images.githubusercontent.com/68066041/140487252-17e706a0-64ef-48b7-b0b2-0a594b6d0349.png" width="250"></p>
 <hr>
 
## Introduction
 Ambrosia is an Android calories tracker app providing users with an easy-to-use, food tracking and recording mobile application to monitor its users' food intake. It aims to simplify the often long and annoying process of monitoring nutritional intake by allowing users to quickly add it to the user's records either through QR code scanning or manual recording. 
 
 Ambrosia is built with the side functionality of allowing food vendors, particularly those who cater towards healthy eating, to take advantage of the QR code scanning feature. Vendors can create their Ambrosia vendor accounts and register their restaurants and food items' nutritional information in Ambrosia. The food items will then have a QR code that Ambrosia users can quickly scan and add it to their nutritional records. 
 
 Ambrosia utilises Google's Firebase to handle authentication and Firestore to hold user's nutritional intake history. This app was developed for a project assignment that I had as a student.

## Features
### Food Recording
 <p align="center">
  <img src="https://user-images.githubusercontent.com/68066041/140501515-42d9f5a5-d540-44d4-8da4-03dea5f34dec.png" width="250">
 </p>
 
 Food vendors enter information about their food item to get the information generated as a QR code.

 <p align="center">
  <img src="https://user-images.githubusercontent.com/68066041/140502564-519c1666-0d02-48e0-803d-8efd08cf6f73.jpg" width="250">
 </p>
 
 Users then can scan and fill up the food item information automatically and quickly add it to their intake list.
 
 <p align="center">
  <img src="https://user-images.githubusercontent.com/68066041/140503018-ddc0feb0-dcfb-48cb-bdef-c7093462e4a7.png" width="250">
  <img src="https://user-images.githubusercontent.com/68066041/140503058-c4d6deff-eeba-4b68-975a-e1f8621eb079.png" width="250">
 </p>
 
 Alternatively, users can enter by manual input and favourite it for quick adding if their supported vendors do not have Ambrosia.
 
 ### Authentication
 <p align="center">
  <img src="https://user-images.githubusercontent.com/68066041/140506640-9f6c1266-b43d-4d2b-a578-ea9cb67a16cb.png" width="250">
  <img src="https://user-images.githubusercontent.com/68066041/140506709-ac6ed692-fe9d-45d6-a29a-8e139168ae01.png" width="250">
 </p>
 
 Powered by Firebase's Authentication, users can register and login into their accounts to start recording their food nutritional history and monitor their food intake.
 
 ### Dashboard, Daily Summary and miscellaneous
 <p align="center">
  <img src="https://user-images.githubusercontent.com/68066041/140503746-a0967d16-18c6-4c88-903d-71273276f9e7.png" width="250">
  <img src="https://user-images.githubusercontent.com/68066041/140503753-ad14f466-c888-4e01-93e8-593c9fcb4b4d.png" width="250">
  <img src="https://user-images.githubusercontent.com/68066041/140507142-e20545af-4b91-4784-a533-218de5333f75.png" width="250">
 </p>
 
 The dashboard features a profile of the nutritional summary of the current day. It contains:
 * User's first name in the middle of the screen (e.g. Test)
 * Slide-show of 3 images encouraging healthy eating and exploratory tips for Ambrosia
 * Visual calories counter based on user's lifestyle adjusted in 'Profile Settings'
 * Floating action button to quickly access the 'Food Recording' screens

Ambrosia also features a navigation drawer allowing access to the following:
* 'Home' - Redirects to the home page dashboard
* 'Daily Information' - List containing every food item consumed for the user's current day, grouped together by their meal types. Users can hit the refresh button to re-sync the calories intake and generate suggestions and improvements to diet if there is overconsumption of types of food. This list will be cleared when the app is launched and detects a new day has begun
* 'Find Recipes' - Option to launch a web view linked to Epicurious search, a cooking recipe website which users can use to find healthy recipes
* 'Profile Settings' - View current user's credentials and allow them to change their lifestyle option. By changing their lifestyle option, the recommeded calories intake changes
* 'Sign Out' - Signs out the current user from the current session, bringing them back to the login page and allowing another account to be signed in 

### Vendors

 Vendors have a different set of home screen and navigation tabs. By ticking the Vendor Checkbox when registering, vendor accounts will be prompted to enter their restaurant/establishment name. 
 
 Once the restaurant name has been entered, vendors will be directed to the dashboard. Initially, no food items have been added, hence, a message will be shown to prompt the vendor to add food items through the navigation drawer.
 
 <p align="center">
  <img src="https://user-images.githubusercontent.com/68066041/140508842-5617bced-9752-4efd-99d0-70b353130bb6.png" width="250">
  <img src="https://user-images.githubusercontent.com/68066041/140508852-8fd55e46-b3d3-4193-b73a-54a2755d0ddc.png" width="250">
 </p>
 
 The navigation drawer for vendors are different from the normal users and consist of:
 * 'Dashboard' - Main screen as shown above. Contains the list of menu items added. Upon tapping or clicking on any of the listed items, the item's information and QR code will shown
 * 'Add New Food Item' - Adds new menu item to the menu item list. Vendors must do this step first in order to generate the food's information and QR code shown later
 * 'Sign Out' - Signs out the current user from the current session, bringing them back to the login page and allowing another account to be signed in 

<p align="center">
  <img src="https://user-images.githubusercontent.com/68066041/140507970-60b216cf-38a5-4959-83fd-4d60cf619841.png" width="250">
 </p>
 
 Tapping the menu item listed on the dashboard page brings up the food item’s information and QR code. Vendors can edit or delete any menu items from this screen. The QR code generated here contains the current menu item’s information and can be scanned by diners to add it to their nutritional intake history. 
 
 ## Ambrosia's Mission
  Ambrosia seeks to promote healthy eating by simplifying and easing users' dietary intake. Many people are often shunned away from taking healthy meal habits as it is too much of a hassle to record and take note of the various nutritional information they take in. 
  
  Ambrosia aims to amend this by providing a more hassle-free approach in assisting users in keeping track of their nutritional intake history, allowing technology to provide us with a more convenient way of life.
  
  In the future, Ambrosia hopes to incentivise both vendors and users to use Ambrosia and promote conscious eating by allowing vendors to customise or reward concescutive customers to their establishment.
