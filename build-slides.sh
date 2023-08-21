#!/usr/bin/env bash

echo "
= 🕺 Youpi dansons la Kapoeira ! 💃

Presentation of Kafka integration test tool

== Slides

" > index.adoc

CONFERENCES=("webinar" "bdxio")

rm -rf public
mkdir -p public

for conf in "${CONFERENCES[@]}"
do
  CONFERENCE_PNG_BASE64=$(cat images/logo-${conf}.png | base64 -w0) \
  QRCODE_PNG_BASE64=$(cat images/qrcode.png | base64 -w0) \
    envsubst < custom.css > public/custom-${conf}.css

  docker run --rm -u $(id -u):$(id -g) -v $(pwd):/documents asciidoctor/docker-asciidoctor:1.49.0 \
    asciidoctor-revealjs -a data-uri -a revealjs_theme=simple \
    -a revealjsdir=https://cdnjs.cloudflare.com/ajax/libs/reveal.js/3.9.2 -a revealjs_transition=fade \
    -a customcss=custom-${conf}.css -a revealjs_slideNumber=true \
    -D public -o index-${conf}.html \
    presentation_fr.adoc

  echo "* link:index-${conf}.html[${conf}^]" >> index.adoc
done

echo "
== Vidéos

=== Webinar NAOS - Un Monde Ouvert 2023

Coming Soon !

=== BDX I/O - 2023

Coming Soon !

touch public/.nojekyll

" >> index.adoc

docker run --rm -u $(id -u):$(id -g) -v $(pwd):/documents asciidoctor/docker-asciidoctor:1.49.0 \
  asciidoctor -D public -o index.html index.adoc

rm index.adoc