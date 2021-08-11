#!/bin/bash

#~

INDEP=false
INEXC=false
DEPS=""

while IFS= read -r ln; do

  [[ "$ln" == *"</dependencies>"* ]] && INDEP=false
  [[ "$ln" == *"<exclusions>"* ]] && INEXC=true

  $INDEP && ! $INEXC && DEPS+="${ln:4}"$'\n'

  [[ "$ln" == *"</exclusions>"* ]] && INEXC=false
  [[ "$ln" == *"<dependencies>"* ]] && INDEP=true

done < "../pom.xml"

#~

DF="pom.xml"

rm "$DF"
touch "$DF"

while IFS= read -r ln; do

  if [[ "$ln" == *"<dependencies>"* ]]; then
    echo "$ln" >> "$DF"
    echo "$DEPS" >> "$DF"
  else
    echo "$ln" >> "$DF"
  fi

done < "pom.xml.tpl"

cp ../.snyk .

