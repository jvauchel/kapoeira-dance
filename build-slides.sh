#!/usr/bin/env bash

rm -rf public
mkdir -p public


CONFERENCE_PNG_BASE64=$(cat images/logo-webinar.png | base64 -w0) \
QRCODE_PNG_BASE64=$(cat images/qrcode.png | base64 -w0) \
  envsubst < custom.css > public/custom.css

docker run --rm -u $(id -u):$(id -g) -v $(pwd):/documents asciidoctor/docker-asciidoctor:1.49.0 \
  asciidoctor-revealjs -a data-uri -a revealjs_theme=simple \
  -a revealjsdir=https://cdnjs.cloudflare.com/ajax/libs/reveal.js/3.9.2 -a revealjs_transition=fade \
  -a customcss=custom.css -a revealjs_slideNumber=true \
  -D public -o index.html \
  presentation_fr.adoc

#-a customcss=custom.css \


# CONFERENCE_NAME="xxx"
# CONFERENCE_PNG_BASE64=$(cat images/logo-${CONFERENCE_NAME}.png | base64 -w0) QRCODE_PNG_BASE64=$(cat images/qrcode.png | base64 -w0) \
#   envsubst < custom.css > public/custom.css 

touch public/.nojekyll
