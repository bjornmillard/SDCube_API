SDCube Programming Library 

Software for the creation and manipulation of Semantically Typed Data Cubes:
Structured storage of large biological data sets.

Copyright (C) 2011 Bjorn Millard <bjornmillard@gmail.com>
License: LGPL v3, see LICENSE.txt and COPYING.txt


1. Introduction

The SDCube webpage is at http://www.semanticbiology.com/software/sdcube

This is a software library to help programmers add SDCube functionality to
their programs.  It is not really useful to end users.  See the Semantic
Biology website linked above for more information on actual applications which
can work with SDCubes.


2. Requirements

* Java 1.6 - http://www.java.com/

* Java HDF5 2.6 - http://www.hdfgroup.org/hdf-java-html/JNI/jhi5/

The Java HDF5 jar file and native code for three common platforms (Windows
32-bit, Mac OS X 32-bit, and Linux 64-bit) are included in the lib/ directory.


3. Compiling

Use the included Ant build file, etc/build.xml .  It contains one target,
makejar, which will create SDCube_API.jar in the build/ directory.  This jar
will contain just the SDCube API class files, without the dependencies.  See
below for how to use the jar file in your application.


4. Using the SDCube API

Include SDCube_API.jar and the provided lib/jhdf5.jar in your classpath.  Also
add lib/native to your java.library.path .  In the future the distributable jar
will contain these files. 
