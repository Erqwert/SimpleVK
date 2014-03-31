SimpleVK
========

Simple vk.com client for Android (limited functionality)
Works on Android 2.3.3 and higher.

IMPLEMENTED:
- Authentication (login, logout, save session data)
- Feeds list (Pull-to-Refreshable)
- Detailed Post including Comments
- New Feeds loading after reaching end of list
- Pictures caching
- Data caching (for screen rotation etc)

LIBRARIES USED:
- Picasso
- AndroidAnnotations
- Support Library

KNOWN BUGS:
- NullPointerException after 2x rotate in PostFragment (Feed fragment isHidden
 and onActivityCreated doesn't trigger on rotate)
- Nonaligned likes icon and count position in Comments
- Some icons have wrong size on 2.3.3

IMPORTANT:
- Attachments will show only 1 photo or link.
- New comments won't be loaded after reaching end of list
- Designed for Smartphones

APK file is in apk folder.

