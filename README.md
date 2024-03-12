# Remote-Desktop

A website that streams your desktop.

## Usage
1. Install the latest [release](https://github.com/Osiris-Team/Remote-Desktop/releases/tag/latest).
2. Make sure you have [latest Java](https://www.oracle.com/java/technologies/downloads/) installed.
3. Double-click the downloaded jar (if that doesn't work, open a terminal and run `java -jar NAME.jar`).
4. <b>Voila!</b> Your browser should open http://localhost:8080 and show your desktop.
5. NOT suitable for production at its current state, since everyone can login with admin admin, and performance issues.

<details>
<summary>If you want to watch from another device, that is in...</summary>
<br>

... in the same network, visit http://insert_device_local_ip:8080 see [how-to](https://www.youtube.com/watch?v=mdp3HtO7Cjs)
and (if needed) open/forward the port 8080 in your [firewall (video)](https://www.youtube.com/watch?v=cRZ26576d1g).

... on the internet, visit http://insert_device_public_ip:8080 get the [ip from here](https://whatismyipaddress.com/),
also open/forward the port 8080 in your [firewall (video)](https://www.youtube.com/watch?v=cRZ26576d1g)
and [router (video)](https://www.youtube.com/watch?v=WOZQppVNGvA).

Tipps:
- Test if your port is open [here](https://www.yougetsignal.com/tools/open-ports/) or via the [terminal (video)](https://www.youtube.com/watch?v=7niN8ELj5B8).
- If port 8080 doesn't work, try port 80.
</details>


## Features
- Laggy, CPU intensive screen recording with high memory usage.
- Dropdown menu with stats.
- Basic authentication.
- Probably has the simplest installation steps in the industry.

## Todo
- Fix above.
- Forward mouse and keyboard interactions.

## Contributing
Always appreciated, since a lot still needs to be done.

<details>
<summary>Open/Close Build Details</summary>


[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/vaadin-flow/Lobby#?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/mvysny/vaadin-boot-example-maven)

# Vaadin 24 running in Embedded Jetty using Maven

A demo project showing the possibility of running a Vaadin app from an
embedded Jetty, as a simple `main()` method. Uses [Vaadin Boot](https://github.com/mvysny/vaadin-boot). Requires Java 17+.

Both the development and production modes are supported. Also, the project
demoes packaging itself both into a flatten uberjar and a zip file containing
a list of jars and a runner script. See "Packaging for production" below
for more details.

> Looking for **Vaadin 24 Gradle** version? See [vaadin-boot-example-gradle](https://github.com/mvysny/vaadin-boot-example-gradle)

> Looking for **Vaadin 14 Maven** version? See [vaadin14-boot-example-maven](https://github.com/mvysny/vaadin14-boot-example-maven)

See the live demo at [v-herd.eu/vaadin-boot-example-maven/](https://v-herd.eu/vaadin-boot-example-maven/)

# Documentation

Please see the [Vaadin Boot](https://github.com/mvysny/vaadin-boot#preparing-environment) documentation
on how you run, develop and package this Vaadin-Boot-based app.

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/myapp-1.0-SNAPSHOT.jar`

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorial at [vaadin.com/docs/latest/tutorial/overview](https://vaadin.com/docs/latest/tutorial/overview).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/docs/latest/components](https://vaadin.com/docs/latest/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes).
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).
</details>



