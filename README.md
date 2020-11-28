# mentorbot

Mentorbot is a Discord chat bot that automates the management of virtual mentoring centers. It allows server owners to create queues for each topic and assign mentors to those topics.

## Installation and Setup

### Installation

The simplest way to get started with mentorbot is to [invite it to your server (link not available yet)](#). If you'd rather build and host it yourself:

1. Create a new application at the [Discord Developer Portal](https://discord.com/developers/applications). In the "Bot" tab, click "Add Bot", then "Click to Reveal Token". Copy the
token.

2. Store that token into a new environment variable named `MENTORBOT_TOKEN`. Twillo has [a good tutorial](https://www.twilio.com/blog/2017/01/how-to-set-environment-variables.html) on
how to do this.

3. Compile the source code yourself: `gradlew.bat shadowJar`
  a. You will need to install JDK 15 to compile and run this project.

4. Run the project: `java -jar build/libs/mentorbot-1.0-SNAPSHOT-all.jar`


### Server Setup

mentorbot requires the following server permissions:

- Manage Roles
- Send Messages

If you are hosting the bot yourself, inviting the bot to your server using this URL should automatically grant it those permissions:
`https://discord.com/oauth2/authorize?scope=bot&permissions=268437504&client_id=YOUR_CLIENT_ID_HERE`. Make sure to replace `YOUR_CLIENT_ID` with your bot account's client ID. If you are
using our hosted bot instead, the invite link specified at the top of "Installation" already has the permissions flags set.



## Usage

A quick setup guide and info about commands can be found in our [wiki](https://github.com/codeRIT/mentorbot/wiki).
