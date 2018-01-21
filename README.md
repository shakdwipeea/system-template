# system-template

[![Clojars Project](https://img.shields.io/clojars/v/system-template/boot-template.svg)](https://clojars.org/system-template/boot-template)

A Boot template for opinionated web app dev setup using
[systems](https://github.com/danielsz/system) library.

## Usage

### Clojure development

This will generate a reloadable clojure environment.

```shell
boot -d boot/new new -t system-template -n <your-project-name> --snapshot
```

### Clojurescript development

This will generate a reloadable full stack development with reloading on server
with [systems](https://github.com/danielsz/system) and front end hot reloading.

Reagent setup including routing with [bide](https://github.com/funcool/bide). Server configuration for html5 routing(front end routing without #) is also included.

```shell
boot -d boot/new new -t system-template -n <your-project-name> -a +cljs --snapshot
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
