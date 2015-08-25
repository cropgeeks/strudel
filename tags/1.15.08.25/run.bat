@ECHO OFF

SET lib=lib

SET mvcp=%lib%\
SET mvcp=%mvcp%;%lib%\i4jruntime.jar
SET mvcp=%mvcp%;%lib%\office-2.0.jar
SET mvcp=%mvcp%;%lib%\scri-commons.jar
SET mvcp=%mvcp%;%lib%\swing-layout-1.0.3.jar

java -Xmx512m -cp .;classes;%mvcp% sbrn.mapviewer.Strudel %1