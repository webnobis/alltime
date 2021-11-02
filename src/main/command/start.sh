#!/bin/sh
${java.home}/bin/java -cp 'lib/*' -p 'modules' -m ${project.module}/${project.main.class}