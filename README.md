# Easy Scoreboards
A Sponge plugin to create dynamic scoreboards

## Features
1. Easy setup
2. Change the scoreboard texts by commands AND by config
3. Use multiple colors per line
4. Use multiple styles per line
5. Use the players name
6. Use the number of online players
7. Use the player balance of economy plugins
8. Show the current Ticks per second
9. Show the current time both from minecraft and the server time
10. Dynamically changes its size to be as small as possible
11. See changes immediately
12. Include countdowns in scoreboard, chat, XP-Bar and titles
13. Single players can hide the scoreboard


If you have ideas for more features please open an issue here:
https://github.com/byYottaFLOPS/EasyScoreboards/issues
If this is not possible just write it into the comments below or send me a PM here: 
https://forums.spongepowered.org/users/yottaflops/

## Setup
1. Place the plugin file in the mods folder of you Sponge server
2. Start the server
3. Edit the text by commands

##Links (Download)
**Latest Release (Download): https://github.com/byYottaFLOPS/EasyScoreboards/releases/latest**

This guide: http://byyottaflops.github.io/EasyScoreboards/

Github page: https://github.com/byYottaFLOPS/EasyScoreboards

## Commands
    !! Changes are instantly applied for all players !!

> Instead of `/esb` you can also use `/easyscoreboard`

### Scoreboard

`/esb set <Line> <Text>`

`<Line>` is the line of the scoreboard and can be anything between 0 and 15. The line 0 is the title of the scoreboard.

`<Text>` Is the text the line will contain. To use spaces the hole text must be in quotation marks (see examples).

    To use multiple colors and styles separate the parts with semicolons (;)

`/esb clear` will clear the scoreboard and remove it from every player.

`/esb show` shows the scoreboard to the player who uses this command

`/esb hide` hides the scoreboard from the player who uses this command

`/esb showall` shows the scoreboard to all players (except if they are on the list of players that don't receive the scoreboard)

`/esb hideall` hides the scoreboard from all players without exceptions


### Countdown

`/esb countdown set <Seconds> <Command>`

    The `<Command>` will be executed after the countdown is over.
    If you want to use quotation marks (") in the command, just replace them with
    single quotation marks (')


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
    Notification for every seconds if the remaining time is smaller than 5 seconds


`/esb countdown start` starts the countdown.

`/esb countdown stop` stops the countdown. If you run start afterwards it will continue.

`/esb countdown reset` resets the countdown to the time defined.


### Other

`/easyscoreboard reload` reloads the config file. If you made any changes in it these will be applied instantly.

## Permissions

> easyscoreboard.use 
 easyscoreboard.set 
 easyscoreboard.clear 
 easyscoreboard.show
 easyscoreboard.hide
 easyscoreboard.showall
 easyscoreboard.hideall
 easyscoreboard.reload
 easyscoreboard.countdown.use
 easyscoreboard.countdown.set
 easyscoreboard.countdown.start
 easyscoreboard.countdown.stop
 easyscoreboard.countdown.reset
 easyscoreboard.countdown.xp
 easyscoreboard.countdown.chat
 easyscoreboard.countdown.title


## Placeholders
In the `<Text>` argument the following strings will be replaced.

To use multiple colors and/or styles separate the textparts with semicolons (;):
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
`PLAYERNAME` will be replaced with the name of the player
who sees the scoreboard

`PLAYERBALANCE` will show the balance of the player, if you have installed an economy plugin, that uses the Sponge Economy API

    Please note that the usage of `PLAYERBALANCE` will give you  
    an error in the console on 1.8 because the economy API is 
    not implemented in this version.

`ONLINECOUNT` will be replaced with the number of online players

    Using "PLAYERNAME"/"PLAYERBALANCE" together with "ONLINECOUNT"
    is not recommended for large servers because it has to compute
    a new scoreboard for every player if a player leaves or joines
    
`COUNTDOWN` will be replaced with the time left of the countdown. The time is wrapped into hours, minutes and seconds.

`TPS` will be replaced with the average ticks of the last 10 seconds. It will update every 10 seconds.

`MTIME` is the current time Minecraft. Updates every 8.35 seconds aka every 10 Minecraft-Minutes.

`STIME` is the time the server has. Updates every 60 seconds.

![Placeholders](https://github.com/byYottaFLOPS/EasyScoreboards/blob/master/screenshots/screenshot5_placeholders.png?raw=true)

## Example
`/esb set 0 "BOLDGREENHello PLAYERNAME"`

`/esb set 1 "DARK_PURPLEWelcome to the ;YELLOWBOLDse;GOLDBOLDrv;REDBOLDer"`

`/esb set 2 "--------------------"`

`/esb set 3 "GREENMoney:"`

`/esb set 4 " PLAYERBALANCE"`

`/esb set 5 "--------------------"`

`/esb set 6 "GREENPlayers Online:"`

`/esb set 7 " ONLINECOUNT"`

`/esb set 8 "--------------------"`

`/esb set 9 "GREENCountdown:"`

`/esb set 10 " COUNTDOWN"`

`/esb countdown set 500 "tellraw @a {'text':'The countdown is over','color':'aqua'}"`

Result:
![Result](https://github.com/byYottaFLOPS/EasyScoreboards/blob/master/screenshots/screenshot2.png?raw=true)

## Bugs & Feature Requests
There are no bugs.

But in case you found one or you have a new stunning feature that must be included, please open an issue 
[here](https://github.com/byYottaFLOPS/EasyScoreboards/issues), just write it into the comments below or send me a PM
[here](https://forums.spongepowered.org/users/yottaflops/)