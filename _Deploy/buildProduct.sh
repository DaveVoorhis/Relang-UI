#!/bin/bash

# This script constructs distributable Relang products. It is intended to run on MacOS.
#
# It requires that ant be available on the command line. On MacOS, it is easiest
# to install by installing Homebrew, then brew install ant.
#
# It assumes copies of Java JDKs are available in the folder denoted by $jredir,
# below, which expects to find untarred JDKs in linux, osx, and windows folders,
# respectively. Each JDK should be untarred but in its folder, so the expected
# directory subtree for JDK version 11.0.1 would be:
# 
# OpenJDKs
#   linux
#      jdk-11.0.1
#         bin 
#         ...etc...
#   osx
#      jdk-11.0.1.jdk
#         bin 
#         ...etc...
#   windows
#      jdk-11.0.1
#         bin 
#         ...etc...
#

relangversion=1.000
javaversion=jdk-11.0.1
jredir=~/Documents/OpenJDKs
proddir=~/git/Relang/_Deploy/product

linuxtarget=linux
mactarget=macos
wintarget=windows

# Clear
mkdir $proddir &>/dev/null
./clearProduct.sh
rm `find ./ -name .DS_Store -print` &>/dev/null

# Build libraries
./buildLibrary.sh

# Build JREs
pushd MakeJRE
./build.sh
popd

# Linux GTK 64bit
echo "---------------------- Linux Build ----------------------"
linuxtargetRelang=$linuxtarget/Relang
mkdir -p $proddir/$linuxtargetRelang
cp -R MakeJRE/Linux/jre $proddir/$linuxtargetRelang/jre
cp nativeLaunchers/binaries/Linux/Relang $proddir/$linuxtargetRelang
mkdir $proddir/$linuxtargetRelang/doc
cp doc/* $proddir/$linuxtargetRelang/doc
cp doc/LICENSE.txt $proddir/$linuxtargetRelang
cp -R lib $proddir/$linuxtargetRelang
rm -rf $proddir/$linuxtargetRelang/lib/swt/win_64
rm -rf $proddir/$linuxtargetRelang/lib/swt/macos_64
cp nativeLaunchers/Relang/Linux/Relang.ini $proddir/$linuxtargetRelang/lib
cp splash.png $proddir/$linuxtargetRelang/lib
chmod +x $proddir/$linuxtargetRelang/jre/bin/*
pushd $proddir/$linuxtarget
tar cfz ../Relang$relversion.$linuxtarget.tar.gz Relang
popd

# MacOS (64bit)
echo "---------------------- MacOS Build ----------------------"
mkdir $proddir/$mactarget
cp -R nativeLaunchers/binaries/MacOS/Relang.app $proddir/$mactarget
cp nativeLaunchers/binaries/MacOS/launchBinSrc/Relang $proddir/$mactarget/Relang.app/Contents/MacOS
mkdir $proddir/$mactarget/Relang.app/Contents/MacOS/doc
cp doc/* $proddir/$mactarget/Relang.app/Contents/MacOS/doc
rm $proddir/$mactarget/Relang.app/Contents/MacOS/README.txt
cp doc/LICENSE.txt $proddir/$mactarget/Relang.app/Contents/MacOS
cp -R MakeJRE/MacOS/jre $proddir/$mactarget/Relang.app/Contents/MacOS/jre
cp -R lib $proddir/$mactarget/Relang.app/Contents/MacOS/
rm -rf $proddir/$mactarget/Relang.app/Contents/MacOS/lib/swt/linux_64
rm -rf $proddir/$mactarget/Relang.app/Contents/MacOS/lib/swt/win_64
cp nativeLaunchers/Relang/MacOS/Relang.ini $proddir/$mactarget/Relang.app/Contents/MacOS/lib
cp splash.png $proddir/$mactarget/Relang.app/Contents/MacOS/lib
cp OSXPackager/Background.png $proddir/$mactarget
cp OSXPackager/Package.command $proddir/$mactarget
pushd $proddir/$mactarget
./Package.command $relversion
mv *.dmg $proddir
rm Background.png
rm Package.command
popd

# Windows 64bit
echo "---------------------- Windows Build ----------------------"
wintargetRelang=$wintarget/Relang
mkdir -p $proddir/$wintargetRelang
cp -R MakeJRE/Windows/jre $proddir/$wintargetRelang/jre
cp nativeLaunchers/binaries/Windows/x64/Relangease/Relang.exe $proddir/$wintargetRelang
mkdir $proddir/$wintargetRelang/doc
cp doc/* $proddir/$wintargetRelang/doc
cp doc/LICENSE.txt $proddir/$wintargetRelang
cp -R lib $proddir/$wintargetRelang
rm -rf $proddir/$wintargetRelang/lib/swt/linux_64
rm -rf $proddir/$wintargetRelang/lib/swt/macos_64
cp nativeLaunchers/Relang/Windows/Relang.ini $proddir/$wintargetRelang/lib
cp splash.png $proddir/$wintargetRelang/lib
pushd $proddir/$wintarget
zip -9r ../Relang$relversion.$wintarget.zip Relang
popd

# Cleanup
echo "Cleanup..."
rm -rf MakeJRE/Linux
rm -rf MakeJRE/MacOS
rm -rf MakeJRE/Windows

echo "Done."
