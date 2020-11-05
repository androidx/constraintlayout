#!/bin/bash
#find . -type f *.xml -print0 | xargs -0 sed -i 's/com\.android\.support\.constraint/androidx.constraintlayout/g'
find ./app/src/main/res/layout/ -type f -name "*.xml" -print0 \
  | xargs -0 \
  gsed -i "s/android\.support\.constraint\.Barrier/androidx\.constraintlayout\.widget\.Barrier/g"
find ./app/src/main/res/layout/ -type f -name "*.xml" -print0 \
  | xargs -0 \
  gsed -i "s/android\.support\.constraint\.Guideline/androidx\.constraintlayout\.widget\.Guideline/g"
find ./app/src/main/res/layout/ -type f -name "*.xml" -print0 \
  | xargs -0 \
  gsed -i "s/android\.support\.constraint\.helper\.Flow/androidx\.constraintlayout\.helper\.widget\.Flow/g"
find ./app/src/main/res/layout/ -type f -name "*.xml" -print0 \
  | xargs -0 \
  gsed -i "s/android\.support\.constraint\.helper\.Layer/androidx\.constraintlayout\.helper\.widget\.Layer/g"
find ./app/src/main/res/layout/ -type f -name "*.xml" -print0 \
  | xargs -0 \
  gsed -i "s/android\.support\.constraint\.Group/androidx\.constraintlayout\.widget\.Group/g"
find ./app/src/main/res/layout/ -type f -name "*.xml" -print0 \
  | xargs -0 \
  gsed -i "s/android\.support\.constraint\.Placeholder/androidx\.constraintlayout\.widget\.Placeholder/g"
find ./app/src/main/res/layout/ -type f -name "*.xml" -print0 \
  | xargs -0 \
  gsed -i "s/android\.support\.constraint\.ConstraintLayout/androidx\.constraintlayout\.widget\.ConstraintLayout/g"

