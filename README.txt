# Solaris Skunk Werks
Brief description of the project goes here.

## Getting Started
Some notes about how to download a release and get started using the application.

## Building From Source
Development of this project currently requires the [NetBeans IDE](https://netbeans.apache.org/download/index.html).

1. Clone this repository as well as [SSWlib](https://github.com/WEKarnesky/sswlib).
2. Open the `SSW` project in NetBeans. You should get an error about `SSWlib` missing. Click Resolve to point it to the cloned `SSWlib` directory.
3. Right click on the `SSW` project and go to the `Run` tab. Change the working directory to `./dist`.
4. Click the `Sources` tab and at the bottom, change `Source/Binary Format` to JDK 6. Repeat this process for the `SSWlib` project.
5. Right click the `SSW` project and click `Build`. From now on you can launch the application from `./dist` with `java -jar SSW.jar` or from NetBeans with the `Run Main Project` menu.

## Contributing
First, fork this repository and create a new branch for your feature. Commit your changes and then submit a pull request to the `develop` branch to have your changes reviewed and merged.