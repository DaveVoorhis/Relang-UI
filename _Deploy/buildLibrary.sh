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

echo 'Build Relang Core'
pushd ../Core
ant -S
popd
cp ../Core/lib/* ../_Deploy/lib/relang
