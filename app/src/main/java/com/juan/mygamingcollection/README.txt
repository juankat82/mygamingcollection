CHANGES TO BE DONE IF YOU WANT TO USE THIS APP:

-In file data/authentication/FirebaseDBConnectImpl.kt, line 58, method createConnection():
Replace: db = Firebase.database("firebase database uri")
With: Replace "firebase database uri" with the address of your firebase Database.

-In file ui/screens/AddNewItem.kt, line 68
Replace: const val BASE_STORAGE_ADDRESS = "base storage address"
With: "base storage address" replace with your Firebase storage base address.

-In file androidTest/data/firebaseDB/FirebaseDBConnectImplTest.kt, line 86
Replace: db = spyk(Firebase.database("firebase realtime database url"))
With: Base url of your Realtime Database
(Not sure this will make any difference though)

-In file antroidTest/data/ui/screens/UITestSignInOrRegister.kt lines 106 and 107
Replace:  androidComposeTestRule.onNodeWithText(context.getString(R.string.user_email)).performTextInput("myemailaddress@emailserver.xxx")
          androidComposeTestRule.onNodeWithText(context.getString(R.string.user_password)).performTextInput("MyPassword_007")
With: A valid name and a valid password (this account must exists already).

-In file antroidTest/data/ui/screens/UITestSignInOrRegister.kt lines 124, 125 and 126
Replace: androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_email)).performTextInput("myemailaddress@emailserver.xxx")
         androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_password)).performTextInput("MyPassword_007")
         androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_repeat_password)).performTextInput("MyPassword_007")
With: Your email address and password (and repeat the password)

-In file androidTest/data/viewmodel/UserViewModelTest.kt line 82
Replace:    GetSignInWithGoogleOption.Builder("Credentials from google authentication")
With: You credentials from your own google authentication account.

-In file test/data/authentication/UnitTestAuthenticationImpl.kt lines 136 and 137
Replace:  val userName = "a valid username"
          val userPassword = "a valid password"
With: A valid name and a valid password (this account must exists already).

-In file res/values/strings.xml
Replace:     Line 2:  <string name="fileprovider">com.yourname.yourappname.components.fileprovider</string>
With:                 <string name="fileprovider">your apps route.components.fileprovider</string>

Replace:    Line 35: <string name="fileprovider">Your own google server user id xxxxxxxxxxx-xxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com</string>
With:       <string name="fileprovider">Your own google server user id. Find this in google Firebase when you get your Account and activate the API</string>

-In file local.properties located in app's base folder, change sdk.dir=/home/my_account/Android/Sdk with the location of your Android SDK.

-In app folder (its the apps base folder) place your own `google-services.json` file. Available on your Firebase account.
I will leave my own but with the data relative to the APIs required changed so you can see what you will need.
FILLING THIS FILE IS A MUST!

-Ensure file `filepaths.xml` exists in src/ folder and its referenced by
<provider><metadata  android:name="android.support.FILE_PROVIDER_PATHS" resource=@xml/filepaths"/></provider>

-NOTES: You need to change the base packet name is you want to upload this to google playstore and refactor
 the base folder name with your new one (ensure this matches or it wont compile).
 This includes the routes in the AndroidManifest.xml file and the one in Strings.xml.
 Dont forget the google-json.xml file!
