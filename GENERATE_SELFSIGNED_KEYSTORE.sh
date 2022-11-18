#!/bin/bash

KEYSTORE_DIR="src/main/resources/keystore"

mkdir -p $KEYSTORE_DIR

if [ ! -f "$KEYSTORE_DIR/keystore.p12" ]; then
  keytool -genkeypair -alias bibauth -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore "$KEYSTORE_DIR/keystore.p12" \
    -dname 'cn=bibauth,o=webserver,dc=test,dc=de' \
    -storepass topsecret \
    -validity 3650
else
  echo "keystore already exists"
fi
