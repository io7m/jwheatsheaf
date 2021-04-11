jwheatsheaf
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jwheatsheaf/com.io7m.jwheatsheaf.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jwheatsheaf%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.io7m.jwheatsheaf/com.io7m.jwheatsheaf.svg?style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/com/io7m/jwheatsheaf/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m/jwheatsheaf.svg?style=flat-square)](https://codecov.io/gh/io7m/jwheatsheaf)

![jwheatsheaf](./src/site/resources/jwheatsheaf.jpg?raw=true)

| JVM             | Platform | Status |
|-----------------|----------|--------|
| OpenJDK LTS     | Linux    | [![Build (OpenJDK LTS, Linux)](https://img.shields.io/github/workflow/status/io7m/jwheatsheaf/main-openjdk_lts-linux)](https://github.com/io7m/jwheatsheaf/actions?query=workflow%3Amain-openjdk_lts-linux) |
| OpenJDK Current | Linux    | [![Build (OpenJDK Current, Linux)](https://img.shields.io/github/workflow/status/io7m/jwheatsheaf/main-openjdk_current-linux)](https://github.com/io7m/jwheatsheaf/actions?query=workflow%3Amain-openjdk_current-linux)
| OpenJDK Current | Windows  | [![Build (OpenJDK Current, Windows)](https://img.shields.io/github/workflow/status/io7m/jwheatsheaf/main-openjdk_current-windows)](https://github.com/io7m/jwheatsheaf/actions?query=workflow%3Amain-openjdk_current-windows)

An alternative to JavaFX's FileChooser that aims to be feature-compatible, 
if not fully API-compatible.

### Features

  * Configurable, styleable (CSS), consistent, non-native JavaFX file chooser.
  * Compatible with any JSR 203 filesystem.
  * Directory creation.
  * Configurable, extensible file/directory filtering.
  * Simple case-insensitive directory searching.
  * Written in pure Java 11.
  * [OSGi](https://www.osgi.org/) ready
  * [JPMS](https://en.wikipedia.org/wiki/Java_Platform_Module_System) ready
  * ISC license
  * High-coverage automated test suite

### Motivation

[JavaFX](https://openjfx.io/) provides `FileChooser` and `DirectoryChooser`
classes that delegate to the operating system's default file chooser implementation on each platform.
This is in contrast to the file chooser abstractions available in [Swing](https://docs.oracle.com/en/java/javase/13/docs/api/java.desktop/javax/swing/JFileChooser.html),
which provides a single non-native file chooser that behaves identically on all platforms. While native file
choosers do have certain benefits, it also means that the file choosers in JavaFX applications cannot be easily
styled to match the rest of the application. It also means that applications, for better or worse, behave
slightly differently on each platform.  The purpose of the `jwheatsheaf` package is to provide a
configurable, styleable, consistent, non-native file chooser implementation analogous to the `JFileChooser`
class.

### Usage

The simplest possible code that can open a file chooser and select at most one file:

```
final Window mainWindow = ...;

final var configuration =
  JWFileChooserConfiguration.builder()
    .setFileSystem(FileSystems.getDefault())
    .build();

final var choosers = JWFileChoosers.create();
final var chooser = choosers.create(mainWindow, configuration);
final List<Path> selected = chooser.showAndWait();
```

The above code will open a modal file chooser that can choose files from the 
default Java NIO filesystem, and will return the selected files (if any) in 
`selected`.

### Configuration

The `JWFileChooserConfiguration` class comes with numerous configuration parameters,
with the `FileSystem` parameter being the only mandatory parameter.

#### Filtering

A list of file filters can be passed to file choosers via the `fileFilters` configuration
parameter. File choosers are _always_ equipped with a file filter that displays all files
(in other words, does no filtering) even if an empty list is passed in `fileFilters`. The
list of file filters will appear in the menu at the bottom of file chooser dialogs, allowing the
user to select one to filter results.

```
final var configuration =
  JWFileChooserConfiguration.builder()
    .setFileSystem(FileSystems.getDefault())
    .addFileFilters(new ExampleFilterRejectAll())
    .addFileFilters(new ExampleFilterXML())
    .build();
```

#### Action

    By default, file choosers are configured to allow the selection of at most one file. The "OK"
    button cannot be clicked until one file is selected. Other behaviours can be specified by
    setting the <i>action</i> for the chooser:

```
final var configuration =
  JWFileChooserConfiguration.builder()
    .setFileSystem(FileSystems.getDefault())
    .setAction(OPEN_EXISTING_MULTIPLE)
    .build();
```

#### Home Directory

If a home directory path is specified (typically a value taken from `System.getProperty("user.home")`),
a button will be displayed in the UI that allows for navigating directly to this path.

```
final var configuration =
  JWFileChooserConfiguration.builder()
    .setFileSystem(FileSystems.getDefault())
    .setHomeDirectory(someHomeDirectoryPath)
    .build();
```

#### Custom Titles

The titles of file chooser dialogs can be adjusted in the configuration. By default, a generic "Please select…"
title is used if no other value is specified.

```
final var configuration =
  JWFileChooserConfiguration.builder()
    .setFileSystem(FileSystems.getDefault())
    .setTitle("Export to PNG…")
    .build();
```

#### Recent Items

The file chooser can display a list of recently used items. It is the responsibility of
applications using the file chooser to save and otherwise manage this list between
application runs; the `jwheatsheaf` file chooser simply displays whatever list
of paths is passed in:

```
final var configuration =
  JWFileChooserConfiguration.builder()
    .setFileSystem(FileSystems.getDefault())
    .setRecentFiles(List.of(
      Paths.get("/tmp/x"),
      Paths.get("/tmp/y"),
      Paths.get("/tmp/z"),
    ))
    .build();
```

#### Directory Creation

The file chooser contains a button that allows for the creation of new directories.
This can be disabled.

  <pre class="code"><![CDATA[
final var configuration =
  JWFileChooserConfiguration.builder()
    .setFileSystem(FileSystems.getDefault())
    .setAllowDirectoryCreation(false)
    .build();
]]></pre>

#### Icons

The file chooser provides a `JWFileImageSetType` interface that allows for
defining the icons used by the user interface. Users wishing to use custom icon
sets should implement this interface and pass in an instance to the configuration:

```
final var configuration =
  JWFileChooserConfiguration.builder()
    .setFileSystem(FileSystems.getDefault())
    .setFileImageSet(new CustomIcons())
    .build();
```

<h4><a id="css-styling" href="#css-styling">Styling</a></h4>

The `jwheatsheaf` file chooser is styleable via CSS. By default, the file chooser applies
no styling and uses whatever is the default for the application. A custom stylesheet and icon set
can be supplied via the `JWFileChooserConfiguration` class, allowing for very different
visuals:

![Basic light theme](./src/site/resources/select0.png?raw=true)

![Olive theme](./src/site/resources/select1.png?raw=true)

All of the elements in a file chooser window are assigned CSS identifiers.

![CSS identifiers](./src/site/resources/css.png?raw=true)

|Identifier|Description|
|----------|-----------|
|fileChooserPathMenu|The path menu used to select directory ancestors.|
|fileChooserUpButton|The button used to move to the parent directory.|
|fileChooserHomeButton|The button used to move to the home directory.|
|fileChooserCreateDirectoryButton|The button used to create directories.|
|fileChooserSelectDirectButton|The button used to enter paths directly.|
|fileChooserSearchField|The search field used to filter the directory table.|
|fileChooserDirectoryTable|The table that shows the contents of the current directory.|
|fileChooserSourceList|The list view that shows the recent items and the filesystem roots.|
|fileChooserNameField|The field that shows the selected file name.|
|fileChooserFilterMenu|The menu that allows for selecting file filters.|
|fileChooserCancelButton|The cancel button.|
|fileChooserOKButton|The confirmation button.|
|fileChooserProgress|The indeterminate progress indicator shown during I/O operations.|

