#!/bin/bash

mvn clean package && cp target/fakeomatic-runner $HOME/.local/bin/fake
