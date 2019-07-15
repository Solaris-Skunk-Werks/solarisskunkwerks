# Solaris Skunk Werks
Solaris Skunk Werks is a community-supported tool used to design Battlemechs for use with the Battletech tabletop wargame.

## Getting Started
Download the [latest release](https://github.com/Solaris-Skunk-Werks/solarisskunkwerks/releases) and extract it into the desired directory. You can then launch the various applications from the SSW Suite from the `bin` folder.

## Building From Source
This project uses the gradle build system and requires gradle to be installed to be able to compile from source. If you intend to develop the project with an IDE, you'll need one with gradle support. Intellij IDEA and Netbeans 11.x have been tested and both have gradle support out of the box.

To build run SSW directly from the command line:

```
$ gradle ssw:run // Compile and run a dev build of ssw
$ gradle saw:run // Compile and run a dev build of solaris armor werks
$ gradle bfb:run // Compile and run a dev build of battletech-force-balancer
```

To build a release zip containing all of the applications that you can distribute, run the following gradle task:

```
$ gradle distZip
```

This will create a zip file containing all applications in the SSW suite under `dist/build/distributions`. Simply unzip that file and run the applications using the launchers from the `bin` folder.

## Contributing
SSW's development workflow generally follows the git workflow described [here](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow). In summary:

1. Fork this repository and clone your copy locally with `--recursive`.
2. If you're implementing a bug fix, checkout the `hotfix` branch.
3. If you're implementing a new feature, checkout the `develop` branch.
4. Create a new branch and commit your changes, then submit a pull request. Features should be merged into `develop` while bug fixes should be merged into `hotfix`.

Feel free to join our [Discord Server](https://discordapp.com/invite/xc5pUWP) to ask questions, report bugs or help with QA testing.
