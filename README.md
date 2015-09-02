#DZone Feed

A java (Spring Boot) implementation for fetching, storing and rendering dzone.com links and aritcles APIs. The fetched content is accessible in the form of RSS Feeds. MongoDB is used for storage.

#### Feeds Exposed

**/rss/links** - The latest submitted links.
**/rss/articles** - The latest submitted articles across several categories (see _application.yml_ for category config).