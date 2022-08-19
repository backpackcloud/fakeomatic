#!/bin/bash

cd ../zipper && mvn -P backpack clean install && cd ../fakeomatic && mvn package && cp target/fakeomatic-runner $HOME/.local/bin/fake
