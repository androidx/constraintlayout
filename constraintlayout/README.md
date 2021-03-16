Building the ConstraintLayout library
=====================================

Several environment variables need to be set:
  
```bash
export ANDROID_HOME=~/Android/Sdk/
export ANDROID_M2REPOSITORY=$ANDROID_HOME/extras/android/m2repository

mkdir -p /tmp/out/dist
OUT_DIR=/tmp/out
DIST_DIR=/tmp/out/dist

export STUDIO_MASTER=~/studio-master-dev/
export CL_PREBUILTS=$STUDIO_MASTER/prebuilts/tools/common/offline-m2/
export DOCLAVA_PREBUILTS=$STUDIO_MASTER/external/
export DOCLAVA_ROOTDIR=$STUDIO_MASTER/tools/sherpa/
```

To build the library, run:

```bash
./gradlew build
```

To compile and publish to your local offline M2 repository, you need to run:

```bash
./localBuild.sh
```

Alternatively you can set the URI of the repository you want to publish to:

```bash
./gradlew -Drepo=/path/to/repo publish
```

or even an external repository:

```bash
./gradlew -Drepo=https://com.mycompany/repo publish
```
