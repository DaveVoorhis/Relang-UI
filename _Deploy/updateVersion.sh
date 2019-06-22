#!/bin/bash

oldrelangVersion=1.000
newrelangVersion=1.001

sed -i '' -e "s/return $oldrelangVersion;/return $newrelangVersion;/" ../Relang/src/org/reldb/relang/version/Version.java

sed -i '' -e "s/relangversion=$oldrelangVersion/relangversion=$newrelangVersion/" ../_Deploy/buildProduct.sh
