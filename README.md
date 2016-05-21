#Easy Scoreboard
A Sponge plugin to create easily dynamic scoreboards

##Features
1. Easy setup
2. Change the scoreboard texts by commands OR by config
3. Use colors
4. Use styles
5. Use the players name
6. Use the number of online players
7. Use the player balance of economy plugins
8. Dynamically changes its size to be as small as possible
9. See changes immediately
10. Include countdowns in scoreboard, chat, XP-Bar and titles.

If you have ideas for more features please open an issue here:
https://github.com/byYottaFLOPS/EasyScoreboards/issues
If this is not possible just write it into the comments below or send me a PM here: 
https://forums.spongepowered.org/users/yottaflops/

##Setup
1. Place the plugin file in the mods folder
of you Sponge server
2. Start the server
3. Edit the text by commands

##Links (Download)
Latest Release (Download): https://github.com/byYottaFLOPS/EasyScoreboards/releases/latest

This guide: http://byyottaflops.github.io/EasyScoreboards/

Github page: https://github.com/byYottaFLOPS/EasyScoreboards

## Commands
    !!Changes are instantly applied for all players!!

### Scoreboard

`/setscoreboard set <Line> <Text>`

`<Line>` is the line of the scoreboard and can be anything between 0 and 15. The line 0 is the title of the scoreboard.

`<Text>` Is the text the line will contain. To use spaces the hole text must be in quotation marks (see examples).

    !!You can only use one color and one style per line!!

`/scoreboard clear` will clear the scoreboard and remove it from every player.

### Countdown

`/scoreboard countdown set <Seconds> <Command>`

    The `<Command>` will be executed after the countdown is over.
    If you want to use quotation marks (") in the command, just replace them with
    single quotation marks (')


`/scoreboard countdown xp <true/false>` Choose if the countdown should be shown in the experience bar of the players.

    Using the XP countdown is not recommended for countdowns larger than a few 
    minutes as this countdown wont be seperated in hours, minutes and seconds.

`/scoreboard countdown chat <true/false>` Choose if there should be a countdown in the chat.

`/scoreboard countdown title <true/false>` Choose if there should be a countdown in the form of titles.

    The countdown in the title and in the chat will be shown like this:
    
    Notification for every hour if the remaining time is larger than 3600 seconds (1 hour)
    Notification for every 10 minutes if the remaining time is beetwen 600 seconds and 3600 seconds (10 and 60 minutes)
    Notification for every minute if the remaining time is beetwen 60 seconds and 600 seconds (1 and 10 minutes)
    Notification for every 10 seconds if the remaining time is beetwen 10 and 60 seconds
    Notification for every seconds if the remaining time is smaller than 5 seconds


`/scoreboard countdown start` starts the countdown.

`/scoreboard countdown stop` stops the countdown. If you run start afterwards it will continue.

`/scoreboard countdown reset` resets the countdown to the time defined.


### Other

`/scoreboard reload` reloads the config file. If you made any changes in it these will be applied instantly.


##Replacements
In the `<Text>` argument the following strings will
be replaced.
###Colors
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

###Styles
`BOLD`
`OBFUSCATED`
`ITALIC`
`STRIKETHROUGH`
`UNDERLINE`

###Other
`PLAYERNAME` will be replaced with the name of the player
who sees the scoreboard

`PLAYERBALANCE` will show the balance of the player, if you have installed an economy plugin, that uses the Sponge Economy API

`ONLINECOUNT` will be replaced with the number of online players

    Using "PLAYERNAME"/"PLAYERBALANCE" together with "ONLINECOUNT"
    is not recommended for large server because it has to compute
    a new scoreboard for every player if a player leaves or joines

##Examples
`/scoreboard set 0 "BOLDGREENHello PLAYERNAME"`

`/scoreboard set 1 "DARK_PURPLEWelcome to the server"`

`/scoreboard set 2 "--------------------"`

`/scoreboard set 3 "GREENMoney:"`

`/scoreboard set 4 " PLAYERBALANCE"`

`/scoreboard set 5 "--------------------"`

`/scoreboard set 6 "GREENPlayers Online:"`

`/scoreboard set 7 " ONLINECOUNT"`

`/scoreboard set 8 "--------------------"`

`/scoreboard set 9 "GREENCountdown:"`

`/scoreboard set 10 " COUNTDOWN"`

`/scoreboard countdown set 500 "tellraw @a {'text':'The countdown is over','color':'aqua'}"`

Result:
![Result](https://github.com/byYottaFLOPS/EasyScoreboards/blob/master/screenshots/screenshot.png?raw=true)

## Bugs & Feature Requests
There are no bugs.

But in case you found one or you have a new stunning feature that must be included, please open an issue here: 
https://github.com/byYottaFLOPS/EasyScoreboards/issues
If this is not possible just write it into the comments below or send me a PM here:
https://forums.spongepowered.org/users/yottaflops/