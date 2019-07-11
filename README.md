# Solaris Skunk Werks
Solaris Skunk Werks is a tool used to design Battlemechs for use with the Battletech tabletop wargame.

## Getting Started
Download the [latest release](https://github.com/WEKarnesky/solarisskunkwerks/releases) and extract it into the desired directory. You can then launch the application on some platforms by double-clicking `SSW.jar` or from the command line with `java -jar SSW.jar`.

## Building From Source
Development of this project currently requires the [NetBeans IDE](https://netbeans.apache.org/download/index.html).

1. Clone this repository with `git clone --recursive https://github.com/WEKarnesky/solarisskunkwerks`.
2. Open the project with NetBeans.
3. To be able to run the application directly from the NetBeans IDE, right click on the SSW project and go to the Run tab, then change the working directory to `./dist`.
4. Right click the `SSW` project and click `Build`. From now on you can launch the application from `./dist` with `java -jar SSW.jar` or from NetBeans with the `Run Main Project` menu.

## Contributing
SSW's development workflow generally follows the git workflow described [here](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow). In summary:

1. Fork this repository and clone your copy locally with `--recursive`.
2. Checkout the `develop` branch and create a new branch for your feature or bug fix.
3. Commit your changes to your branch and then submit a pull request to merge into `develop`.
