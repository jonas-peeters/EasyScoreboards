# Easy Scoreboards
A Sponge plugin to create dynamic scoreboards

[![Build Status](https://img.shields.io/travis/byYottaFLOPS/EasyScoreboards.svg?style=for-the-badge)](https://travis-ci.org/byYottaFLOPS/EasyScoreboards)
[![Downloads on Github](https://img.shields.io/github/downloads/byYottaFLOPS/EasyScoreboards/total.svg?style=for-the-badge)](https://github.com/byYottaFLOPS/EasyScoreboards/releases/latest)
[![Current Issues](https://img.shields.io/github/issues/byYottaFLOPS/EasyScoreboards.svg?style=for-the-badge)](https://github.com/byYottaFLOPS/EasyScoreboards/issues)
![License](https://img.shields.io/github/license/byYottaFLOPS/EasyScoreboards.svg?style=for-the-badge)

## Features
1. Easy setup
2. Use multiple colors per line
3. Use multiple styles per line
4. Use various placeholders
5. See changes immediately
6. Include countdowns in scoreboard, chat, XP-Bar and titles
7. Single players can hide their scoreboard
8. Support for the PlaceholderAPI
9. Setting text in the tab bar


If you have ideas for more features please open an issue here:
https://github.com/byYottaFLOPS/EasyScoreboards/issues

If this is not possible just write it into the comments below or send me a PM here: 
https://forums.spongepowered.org/users/yottaflops/

## Setup
1. Place the plugin file in the mods folder of your sponge server
2. Start the server
3. Edit the config file (config/de_yottaflops_easyscoreboard.conf)
4. Reload the scoreboard (/sponge plugins reload)

### Optional: Setup for the PlaceholderAPI
1. Get the latest release: https://ore.spongepowered.org/rojo8399/PlaceholderAPI/versions
2. Move the plugin into the mods folder of your sponge server
3. Add placeholders to the scoreboard by editing the config file. 
[Here](https://github.com/rojo8399/PlaceholderAPI/wiki/Placeholders) is a list of built-in placeholders. (You can also 
add plugins that add even more placeholders to the API.)
4. Restart the server

## Links (Download)
**Latest Release (Download): https://github.com/byYottaFLOPS/EasyScoreboards/releases/latest**

Guide: http://byyottaflops.github.io/EasyScoreboards/

Github page: https://github.com/byYottaFLOPS/EasyScoreboards

## Commands

> Instead of `/esb` you can also use `/easyscoreboard`

### Scoreboard

`/esb clear` will clear the scoreboard and remove it from every player.

`/esb show` shows the scoreboard to the player who uses this command

`/esb hide` hides the scoreboard from the player who uses this command

`/esb showall` shows the scoreboard to all players (except if they are on the list of players that don't receive the scoreboard)

`/esb hideall` hides the scoreboard from all players without exceptions


### Countdown

`/esb countdown set <Seconds> <Command>`

    The `<Command>` will be executed after the countdown is over.
    You can to anything from writing in the chat over placing a block to summon a monster.
    If you want to use quotation marks (") in the command, just replace them with
    single quotation marks (')

`/esb countdown add <Seconds>` Add some time to the countdown. This won't affect the config.

`/esb countdown xp <true/false>` Choose if the countdown should be shown in the experience bar of the players.

    Using the XP countdown is not recommended for countdowns larger than a few 
    minutes as this countdown wont be seperated in hours, minutes and seconds.

`/esb countdown chat <true/false>` Choose if there should be a countdown in the chat.

`/esb countdown title <true/false>` Choose if there should be a countdown in the form of titles.

    The countdown in the title and in the chat will be shown like this:
    
    Notification for every hour if the remaining time is larger than 3600 seconds (1 hour)
    Notification for every 10 minutes if the remaining time is beetwen 600 seconds and 3600 seconds (10 and 60 minutes)
    Notification for every minute if the remaining time is beetwen 60 seconds and 600 seconds (1 and 10 minutes)
    Notification for every 10 seconds if the remaining time is beetwen 10 and 60 seconds
    Notification for every second if the remaining time is smaller than 5 seconds


`/esb countdown start` starts the countdown.

`/esb countdown stop` stops the countdown. If you run start afterwards it will continue.

`/esb countdown reset` resets the countdown to the time defined.


### Other

To reload the config file, just use the Plugin reload command from Sponge to reload all plugins `/sponge plugins reload`
 or to reload just esb `/esb reload`.
If you made any changes to the config file these will be applied instantly.

## Permissions

> easyscoreboard.use 
 easyscoreboard.clear 
 easyscoreboard.show
 easyscoreboard.hide
 easyscoreboard.showall
 easyscoreboard.hideall
 easyscoreboard.reload
 easyscoreboard.countdown.use
 easyscoreboard.countdown.set
 easyscoreboard.countdown.add
 easyscoreboard.countdown.start
 easyscoreboard.countdown.stop
 easyscoreboard.countdown.reset
 easyscoreboard.countdown.xp
 easyscoreboard.countdown.chat
 easyscoreboard.countdown.title


## Placeholders
In the `<Text>` argument there are a few strings that will be replaced.

To use multiple colors and/or styles separate the parts of the text with semicolons (;):
`/esb set 1 "AQUAC;BLUEo;GOLDl;GREENo;YELLOWr;REDf;WHITEu;BLACKl"`
`/esb set 1 "ITALICDifferent ;BOLDStyles"`

### Colors
* `AQUA` or `&b`
* `BLUE` or `&9`
* `GOLD` or `&6`
* `GREEN` or `&a`
* `YELLOW` or `&e`
* `RED` or `&c`
* `LIGHT_PURPLE` or `&d`
* `DARK_AQUA` or `&3`
* `DARK_BLUE` or `&1`
* `DARK_GREEN` or `&2`
* `DARK_RED` or `&4`
* `DARK_PURPLE` or `&d`
* `WHITE` or `&f`
* `GRAY` or `&7`
* `DARK_GRAY` or `&8`
* `BLACK` or `&0`

These will be replaced with the equivalent color.

![Colors](https://github.com/byYottaFLOPS/EasyScoreboards/blob/master/screenshots/screenshot4_colors.png?raw=true)

### Styles
* `BOLD` or `&l`
* `OBFUSCATED` or `&k`
* `ITALIC` or `&o`
* `STRIKETHROUGH` or `&m`
* `UNDERLINE` or `&n`

![Styles](https://github.com/byYottaFLOPS/EasyScoreboards/blob/master/screenshots/screenshot3_styles.png?raw=true)

### Other
    Using placeholders that require a custom scoreboard for every player
    while using some that require a lot of updating should be done with 
    care on large servers because this will be very CPU intensive.

`%PLAYERNAME%` will be replaced with the name of the player
who sees the scoreboard

`%PLAYERBALANCE%` will show the balance of the player, if you have installed an economy plugin, that uses the Sponge Economy API

`%PLAYERBALANCEWRAP%` will show the balance of the player in a parsed style like *1.2k* or *34.5m*

    Please note that the usage of `%PLAYERBALANCE%` and `%PLAYERBALANCEWRAP%`
    will give you an error in the console on 1.8 because the economy API is 
    not implemented in this version.

`%ONLINECOUNT%` will be replaced with the number of online players
    
`%COUNTDOWN%` will be replaced with the time left of the countdown. The time is wrapped into hours, minutes and seconds.

`%TPS%` will be replaced with the average ticks of the last 10 seconds. It will update every 10 seconds.

`%MTIME%` is the current time Minecraft. Updates every 8.35 seconds aka every 10 Minecraft-Minutes.

`%STIME%` is the time the server has. Updates every 60 seconds.

On top of these integrated placeholders you can install and use every placeholder from the PlaceholderAPI by rojo8399.
You can find a download [here](https://ore.spongepowered.org/rojo8399/PlaceholderAPI).

![Placeholders](https://github.com/byYottaFLOPS/EasyScoreboards/blob/master/screenshots/screenshot5_placeholders.png?raw=true)

## Example
![Example](https://github.com/byYottaFLOPS/EasyScoreboards/blob/master/screenshots/screenshot2.png?raw=true)

## Bugs & Feature Requests
There are no bugs.

But in case you found one or you have a new feature that must be included, please open an issue 
[here](https://github.com/byYottaFLOPS/EasyScoreboards/issues), just write it into the comments below or send me a PM
[here](https://forums.spongepowered.org/users/yottaflops/)
