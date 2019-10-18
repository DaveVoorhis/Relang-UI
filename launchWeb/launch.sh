#!/bin/sh
jdk/bin/java -cp "bin:tomcat/*:WebContent/WEB-INF/lib/*" org.reldb.relang.MainProd --port 8015
