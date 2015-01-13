# lein-sitecompiler

A Leiningen plugin for static sites compiling.

See https://github.com/dbushenko/sitecompiler for details.

## Usage

Add the following into the dependencies in .lein/profiles.clj

    [lein-sitecompiler "0.2.0"]

E.g. your profiles.clj may look like this:

    {:user {:plugins [[lein-sitecompiler "0.1.0"]]}}

Run following to get help:

    $ lein sitecompiler help

or

    $ lein sc help

Commands are available:

* templates -- list available templates
* new <site-name> <template-name> -- generate new project
* compile -- compile current project
* help -- show help

You may run command 'sitecompiler' as well as 'sc' for saving keystrokes.

## QuickStart

See which templates are available:

    $ lein sc templates

Generate a stub for your website:

    $ lein sc new my nightsky

Compile it:

    $ cd my
    $ lein sc compile

Open in your browser file my/output/index.html.

## License

Copyright © 2015 D.Bushenko:

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
