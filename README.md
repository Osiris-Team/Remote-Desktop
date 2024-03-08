# Remote-Desktop

A website that streams your desktop.

## Usage
1. Install the latest [release](https://github.com/Osiris-Team/Remote-Desktop/releases/tag/latest).
2. Make sure you have [latest Java](https://www.oracle.com/java/technologies/downloads/) installed. 
3. Double-click the downloaded jar (if that doesn't work, open a terminal and run `java -jar NAME.jar`).
4. <b>Voila!</b> Your browser should open http://localhost:8080 and show your desktop. 

<details>
<summary>If you want to watch from another device, that is in...</summary>
<br>
  
... in the same network, visit http://insert_device_local_ip:8080 see [how-to](https://www.youtube.com/watch?v=mdp3HtO7Cjs)
   and (if needed) open/forward the port 8080 in your [firewall (video)](https://www.youtube.com/watch?v=cRZ26576d1g).
   
... on the internet, visit http://insert_device_public_ip:8080 get the [ip from here](https://whatismyipaddress.com/),
          also open/forward the port 8080 in your [firewall (video)](https://www.youtube.com/watch?v=cRZ26576d1g)
          and [router (video)](https://www.youtube.com/watch?v=WOZQppVNGvA).
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


## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

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

