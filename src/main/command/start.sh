#!/bin/sh
${java.home}/bin/java -cp lib/*:${project.build.finalName}.${project.packaging} ${project.main.class}