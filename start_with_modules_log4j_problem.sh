steffen@lenovo-mint:~/tmp/alltime_2.1.1-SNAPSHOT$ java -p 'lib' -m com.webnobis.alltime.Alltime/com.webnobis.alltime.Alltime
Exception in Application init method
java.lang.reflect.InvocationTargetException
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at javafx.graphics/com.sun.javafx.application.LauncherImpl.launchApplicationWithArgs(LauncherImpl.java:464)
	at javafx.graphics/com.sun.javafx.application.LauncherImpl.launchApplication(LauncherImpl.java:363)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at java.base/sun.launcher.LauncherHelper$FXHelper.main(LauncherHelper.java:1051)
Caused by: java.lang.RuntimeException: Exception in Application init method
	at javafx.graphics/com.sun.javafx.application.LauncherImpl.launchApplication1(LauncherImpl.java:895)
	at javafx.graphics/com.sun.javafx.application.LauncherImpl.lambda$launchApplication$2(LauncherImpl.java:195)
	at java.base/java.lang.Thread.run(Thread.java:834)
Caused by: java.lang.NoClassDefFoundError: org/apache/logging/log4j/Logger
	at org.apache.logging.log4j.slf4j@2.11.1/org.apache.logging.slf4j.SLF4JServiceProvider.initialize(SLF4JServiceProvider.java:53)
	at org.slf4j/org.slf4j.LoggerFactory.bind(LoggerFactory.java:153)
	at org.slf4j/org.slf4j.LoggerFactory.performInitialization(LoggerFactory.java:141)
	at org.slf4j/org.slf4j.LoggerFactory.getProvider(LoggerFactory.java:419)
	at org.slf4j/org.slf4j.LoggerFactory.getILoggerFactory(LoggerFactory.java:405)
	at org.slf4j/org.slf4j.LoggerFactory.getLogger(LoggerFactory.java:354)
	at org.slf4j/org.slf4j.LoggerFactory.getLogger(LoggerFactory.java:380)
	at com.webnobis.alltime.Alltime/com.webnobis.alltime.config.Config.<clinit>(Config.java:22)
	at com.webnobis.alltime.Alltime/com.webnobis.alltime.Alltime.init(Alltime.java:63)
	at javafx.graphics/com.sun.javafx.application.LauncherImpl.launchApplication1(LauncherImpl.java:824)
	... 2 more
Caused by: java.lang.ClassNotFoundException: org.apache.logging.log4j.Logger
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:582)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:178)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521)
	... 12 more
Exception running application com.webnobis.alltime.Alltime
steffen@lenovo-mint:~/tmp/alltime_2.1.1-SNAPSHOT$ 

