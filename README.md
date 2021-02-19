gwt290
==============

This is a demonstrator application for full blown Vaadin 8 project using JDK 11.

Vaadin 8 is compatible with Java 11 on server side. But you may want to use Java 11 in whole application including custom widgets.

Vaadin 8 comes with GWT 2.8.2, which does not support Java 11 code. In this project we override that using GWT 2.9.0 instead.

Tested this with Amazon Corretto 11.

Based on template for a full-blown Vaadin application that only requires a Servlet 3.0 container to run (no other JEE dependencies).

Longer story in the blog https://vaadin.com/blog/update-your-vaadin-8-project-to-java-11-and-gwt-2.9.0

Project Structure
=================

The project consists of the following three modules:

- parent project: common metadata and configuration
- gwt290-widgetset: widgetset, custom client side code and dependencies to widget add-ons
  - Patched to use GWT 2.9.0, see pom.xml for details
  - Added: com.google.gwt.dev.shell.CheckForUpdates.java to circumvent compilation issue
  - Custom widget migrated to Java 11
- gwt290-ui: main application module, development time
- gwt290-production: module that produces a production mode WAR for deployment

The production mode module recompiles the widgetset (obfuscated, not draft), activates production mode for Vaadin with a context parameter in web.xml and contains a precompiled theme. The ui module WAR contains an unobfuscated widgetset, and is meant to be used at development time only.

Workflow
========

To compile the entire project, run "mvn install" in the parent project.

Other basic workflow steps:

- getting started
- compiling the whole project
  - run "mvn install" in parent project
- developing the application
  - edit code in the ui module
  - run "mvn jetty:run" in ui module
  - open http://localhost:8080/
- client side changes or add-ons
  - edit code/POM in widgetset module
  - run "mvn install" in widgetset module
  - if a new add-on has an embedded theme, run "mvn vaadin:update-theme" in the ui module
- debugging client side code
  - run "mvn vaadin:run-codeserver" in widgetset module
  - activate Super Dev Mode in the debug window of the application
- creating a production mode war
  - run "mvn -Pproduction package" in the production mode module or in the parent module
- testing the production mode war
  - run "mvn -Pproduction jetty:run-war" in the production mode module


Developing a theme using the runtime compiler
-------------------------

When developing the theme, Vaadin can be configured to compile the SASS based
theme at runtime in the server. This way you can just modify the scss files in
your IDE and reload the browser to see changes.

To use on the runtime compilation, open pom.xml of your UI project and comment 
out the compile-theme goal from vaadin-maven-plugin configuration. To remove 
an existing pre-compiled theme, remove the styles.css file in the theme directory.

When using the runtime compiler, running the application in the "run" mode 
(rather than in "debug" mode) can speed up consecutive theme compilations
significantly.

The production module always automatically precompiles the theme for the production WAR.

Using Vaadin pre-releases
-------------------------

If Vaadin pre-releases are not enabled by default, use the Maven parameter
"-P vaadin-prerelease" or change the activation default value of the profile in pom.xml .
