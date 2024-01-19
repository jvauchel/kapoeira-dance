#!/usr/bin/env bash

ASCIIDOCTOR_DOCKER_IMAGE="asciidoctor/docker-asciidoctor:1.55.0"
REVEALJS_DIR="https://cdn.jsdelivr.net/npm/reveal.js@4.1.2"
CONFERENCES=("webinar" "bdxio" "capitoledulibre" "scalaio")

cat summary.adoc > index.adoc

rm -rf public
mkdir -p public

for conf in "${CONFERENCES[@]}"
do
  CONFERENCE_PNG_BASE64=$(cat images/logo-${conf}.png | base64 -w0) \
  QRCODE_PNG_BASE64=$(cat images/qrcode-slides.png | base64 -w0) \
    envsubst < custom.css > public/custom-${conf}.css
  docker run --name $(uuidgen) --rm -u $(id -u):$(id -g) -v $(pwd):/documents ${ASCIIDOCTOR_DOCKER_IMAGE} \
    asciidoctor-revealjs -a data-uri -a revealjs_theme=simple \
    -a conf-${conf} -a confname=${conf} \
    -a revealjsdir=${REVEALJS_DIR} -a revealjs_transition=fade \
    -a customcss=custom-${conf}.css -a revealjs_slideNumber=true \
    -D public -o index-${conf}.html \
    presentation_en.adoc
  echo "* link:index-${conf}.html[${conf}^]" >> index.adoc
done

cat videos.adoc >> index.adoc

touch public/.nojekyll
cp -r sounds public/ 

docker run --rm --name $(uuidgen) -u $(id -u):$(id -g) -v $(pwd):/documents ${ASCIIDOCTOR_DOCKER_IMAGE} \
  asciidoctor -D public -o index.html index.adoc

rm index.adoc