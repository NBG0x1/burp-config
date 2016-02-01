# Burp Config Hacker

## What?

Set Burp config and specify Extensions outside of Burp.


## Why?

Allows easier automation of Burp instances, probably most interestingly for
enterprise usage, but also interesting for those of us who like running
applications headless :)


## How?

The easiest way to get up and running is to clean your current prefs:

```sh
java -jar burp-config.jar -clear
```

then run Burp, license it, and close it without configuring anything. Now we can
save the defaults:

```sh
java -jar burp-config.jar -dump > defaults
```

Now run Burp, and configure to your liking, then close it back down again. We
can now diff against the defaults.

```sh
java -jar burp-config.jar -dump | diff defaults - | awk '/^>/ { sub(/^> /, ""); print; }' | grep -v '^extender.availablebapps'
```

Use these to write your own config file, which can then be loaded with:

```sh
java -jar burp-config.jar -c my_config -e ext1.jar -e ext2.jar
```
