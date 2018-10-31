#!/bin/sh
${java.home}/bin/java  {env.OpenJFX}/lib  -cp lib/*:${project.build.finalName}.${project.packaging} ${project.main.class}