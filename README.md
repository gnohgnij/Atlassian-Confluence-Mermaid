# Mermaid Plugin for Confluence

Plugin for Atlassian Confluence to support diagram rendering with
[Mermaid](https://github.com/knsv/mermaid).

Mermaid was primarily written by Knut Sveidqvist; see file THIRDPARTY for
license info.

## Prerequisites for development

* Install Atlassian SDK (currently 8.2.7)
* Install Java 8 (see [compatibility page][compat])

[compat]:https://confluence.atlassian.com/doc/supported-platforms-207488198.html

## Development

To set up development environment with Eclipse.

### Clone the git repository

```
git clone <repo>
```

### Create Eclipse project files

In the project root, run to create Eclipse project files

```
atlas-mvn eclipse:eclipse
```

### Import project into Eclipse

1. Select File > Import. Eclipse starts the Import wizard.
2. Expand the General folder tree.
3. Filter for Existing Projects into Workspace and press Next.
4. Choose Select root directory and then Browse to the root directory of your 
   workspace. Your Atlassian plugin folder should appear under Projects.
5. Select your plugin project and click Finish. Eclipse imports your project.

## Run the Confluence plugin locally

Run:

`atlas-clean` and then `atlas-run` and leave it running.

When a change have been made to source files run `atlas-mvn package` in another
terminal which will build the plugin and deploy it (check the other terminal)

## Contributors

The Mermaid plugin for Confluence of course owes its existence to Knut Sveidqvist,
the creator of the Mermaid JavaScript library.

These individuals have contributed code to the Confluence plugin:

* Alan Hohn
* Josh Bennett
* Jeremy Slater
* Joacim Järkeborn

All contributions are gratefully appreciated!

