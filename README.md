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
8. Show the current TPS
9. Dynamically changes its size to be as small as possible
10. See changes immediately
11. Include countdowns in scoreboard, chat, XP-Bar and titles
12. Single players can hide the scoreboard

If you have ideas for more features please open an issue here:
https://github.com/byYottaFLOPS/EasyScoreboards/issues
If this is not possible just write it into the comments below or send me a PM here: 
https://forums.spongepowered.org/users/yottaflops/

## Setup
1. Place the plugin file in the mods folder of you Sponge server
2. Start the server
3. Edit the text by commands

##Links (Download)
Latest Release (Download): https://github.com/byYottaFLOPS/EasyScoreboards/releases/latest

This guide: http://byyottaflops.github.io/EasyScoreboards/

Github page: https://github.com/byYottaFLOPS/EasyScoreboards

## Commands
    !! Changes are instantly applied for all players !!

> Instead of `/easyscoreboard` you can also use `/esb`

### Scoreboard

`/easyscoreboard set <Line> <Text>`

`<Line>` is the line of the scoreboard and can be anything between 0 and 15. The line 0 is the title of the scoreboard.

`<Text>` Is the text the line will contain. To use spaces the hole text must be in quotation marks (see examples).

    To use multiple colors and styles separate the parts with semicolons (;)

`/easyscoreboard clear` will clear the scoreboard and remove it from every player.

`/easyscoreboard show` shows the scoreboard to the player who uses this command

`/easyscoreboard hide` hides the scoreboard from the player who uses this command

`/easyscoreboard showall` shows the scoreboard to all players (except if they are on the list of players that don't receive the scoreboard)

`/easyscoreboard hideall` hides the scoreboard from all players without exceptions


### Countdown

`/easyscoreboard countdown set <Seconds> <Command>`

    The `<Command>` will be executed after the countdown is over.
    If you want to use quotation marks (") in the command, just replace them with
    single quotation marks (')


`/easyscoreboard countdown xp <true/false>` Choose if the countdown should be shown in the experience bar of the players.

    Using the XP countdown is not recommended for countdowns larger than a few 
    minutes as this countdown wont be seperated in hours, minutes and seconds.

`/easyscoreboard countdown chat <true/false>` Choose if there should be a countdown in the chat.

`/easyscoreboard countdown title <true/false>` Choose if there should be a countdown in the form of titles.

    The countdown in the title and in the chat will be shown like this:
    
    Notification for every hour if the remaining time is larger than 3600 seconds (1 hour)
    Notification for every 10 minutes if the remaining time is beetwen 600 seconds and 3600 seconds (10 and 60 minutes)
    Notification for every minute if the remaining time is beetwen 60 seconds and 600 seconds (1 and 10 minutes)
    Notification for every 10 seconds if the remaining time is beetwen 10 and 60 seconds
    Notification for every seconds if the remaining time is smaller than 5 seconds


`/easyscoreboard countdown start` starts the countdown.

`/easyscoreboard countdown stop` stops the countdown. If you run start afterwards it will continue.

`/easyscoreboard countdown reset` resets the countdown to the time defined.


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


## Replacements
In the `<Text>` argument the following strings will be replaced.

To use multiple colors and/or styles separate the textparts with semicolons (;):
`/easyscoreboard set 1 "AQUAC;BLUEo;GOLDl;GREENo;YELLOWr;REDf;WHITEu;BLACKl"`
`/easyscoreboard set 1 "ITALICDifferent ;BOLDStyles"`

### Colors
`AQUA`
`BLUE`
`GOLD`
`GREEN`
`YELLOW`
`RED`
`LIGHT_PURPLE`
`DARK_AQUA`
`DARK_BLUE`
`DARK_GREEN`
`DARK_RED`
`DARK_PURPLE`
`WHITE`
`GRAY`
`DARK_GRAY`
`BLACK`

These will be replaced with the equivalent color.

### Styles
`BOLD`
`OBFUSCATED`
`ITALIC`
`STRIKETHROUGH`
`UNDERLINE`

### Other
`PLAYERNAME` will be replaced with the name of the player
who sees the scoreboard

`PLAYERBALANCE` will show the balance of the player, if you have installed an economy plugin, that uses the Sponge Economy API

    Please note that the usage of `PLAYERBALANCE` will give you  
    an error in the console on 1.8 because the economy API is 
    not implemented in this version

`ONLINECOUNT` will be replaced with the number of online players

    Using "PLAYERNAME"/"PLAYERBALANCE" together with "ONLINECOUNT"
    is not recommended for large servers because it has to compute
    a new scoreboard for every player if a player leaves or joines
    
`COUNTDOWN` will be replaced with the time left of the countdown. The time is wrapped into hours, minutes and seconds.

`TPS` will be replaced with the average ticks of the last 10 seconds. This updates every 10 seconds.

## Example
`/easyscoreboard set 0 "BOLDGREENHello PLAYERNAME"`

`/easyscoreboard set 1 "DARK_PURPLEWelcome to the ;YELLOWBOLDse;GOLDBOLDrv;REDBOLDer"`

`/easyscoreboard set 2 "--------------------"`

`/easyscoreboard set 3 "GREENMoney:"`

`/easyscoreboard set 4 " PLAYERBALANCE"`

`/easyscoreboard set 5 "--------------------"`

`/easyscoreboard set 6 "GREENPlayers Online:"`

`/easyscoreboard set 7 " ONLINECOUNT"`

`/easyscoreboard set 8 "--------------------"`

`/easyscoreboard set 9 "GREENCountdown:"`

`/easyscoreboard set 10 " COUNTDOWN"`

`/easyscoreboard countdown set 500 "tellraw @a {'text':'The countdown is over','color':'aqua'}"`

Result:
![Result](https://github.com/byYottaFLOPS/EasyScoreboards/blob/master/screenshots/screenshot2.png?raw=true)

## Bugs & Feature Requests
There are no bugs.

But in case you found one or you have a new stunning feature that must be included, please open an issue here: 
https://github.com/byYottaFLOPS/EasyScoreboards/issues
If this is not possible just write it into the comments below or send me a PM here:
https://forums.spongepowered.org/users/yottaflops/