# Wikidot Importer

This is a java application that can import text files and associated binary files into a wikidot site.

## Prerequisites

* Java 11
* A [Wikidot](http://www.wikidot.com/) Pro Lite account
* A bunch of pages and optionally corresponding files to upload to your Wikidot site.
  
## How-To

This application is ran from the command line and takes the following arguments:

* The [name of a wikidot site](#the-name-to-use-for-the-wikidot-site),
* A [Wikidot API key](#apikey),
* A path to a directory containing the text files (sources) to be imported,
* A path to a directory containing the binary files (files) to be imported.

A sample command line invocation for importing sources and files into a wikidot site at `http://hiddenmeanings.wikidot.com` would be:

```bash
$ java -jar wikidot-importer.jar --wikidotName=mysitename --apiKey=rGta6Hw4fOYZFFm633I6rJjNRUb7hHt9 --sourcesDir=C:\Users\Henry\Documents\import-wikidot --filesDir=C:\Users\Henry\Documents\import-wikidot-files
```

## The arguments
### The name to use for the wikidot site

If your wikidot site's URL is `http://mysitename.wikidot.com`, the name to use as the first argument is `mysitename`.

### API key

To allow programmatic read/write access to any wikidot site, an API key is needed. To obtain an API key, one needs a [Wikidot Pro Lite account](http://www.wikidot.com/plans)  or better. To find your API keys, login to Wikidot and navigate to the [API access](https://www.wikidot.com/account/settings#/api) section.

### The sources to be imported

The directory configured above for the sources to be imported should contain a bunch of \*.txt files.
The extension is relevant: only \*.txt files will be processed.

### The files (binaries) to be imported

Files are attached to a page in Wikidot on a per-page basis. This means that if there is a source (text page) in the source directory called str-015.txt that has an image as attachment called `whole-human-brain-cerebral-cortex.png`, which it might use in a construct such as:

```
= [[image /str-015/whole-human-brain-cerebral-cortex.png]]
```

then there should be a directory in the files directory called `str-015`, which contains the image file called `whole-human-brain-cerebral-cortex.png`.

## Building the application

In a terminal, navigate to the root of the project and do `mvn clean package`.  
The built application will be in target under the name `wikidot-importer.jar`. Use it as above in the sample command line invocation.  