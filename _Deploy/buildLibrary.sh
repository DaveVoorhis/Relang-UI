#!/bin/sh

echo 'Build swt_linux'
pushd ../swtNative/swt_linux
ant -S
popd
cp ../swtNative/swt_linux/lib/* ../_Deploy/lib/swt/linux_64

echo 'Build swt_macos'
pushd ../swtNative/swt_macos
ant -S
popd
cp ../swtNative/swt_macos/lib/* ../_Deploy/lib/swt/macos_64

echo 'Build swt_win'
pushd ../swtNative/swt_win
ant -S
popd
cp ../swtNative/swt_win/lib/* ../_Deploy/lib/swt/win_64

echo 'Build RelangDesktop'
pushd ../RelangDesktop
ant -S
popd
cp ../RelangDesktop/lib/* ../_Deploy/lib/relang

echo 'Build platformWeb'
pushd ../platformWeb
ant -S
popd
cp ../platformWeb/lib/* ../_Deploy/lib/relang

echo 'Build platformDesktop'
pushd ../platformDesktop
ant -S
popd
cp ../platformDesktop/lib/* ../_Deploy/lib/relang

echo 'Build dEngine'
pushd ../dEngine
ant -S
popd
cp ../dEngine/lib/* ../_Deploy/lib/relang

echo 'Build dBrowser'
pushd ../dBrowser
ant -S
popd
cp ../dBrowser/lib/* ../_Deploy/lib/relang

echo 'Build RelangWeb'
pushd ../RelangWeb
ant -S
popd
cp ../RelangWeb/lib/* ../_Deploy/lib/relang

