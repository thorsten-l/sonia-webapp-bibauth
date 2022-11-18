#!/bin/bash

if [ ! -f "keystore.p12" ]; then
  keytool -genkeypair -alias bibauth -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore "keystore.p12" \
    -dname 'cn=bibauth.test.de' \
    -storepass topsecret \
    -validity 3650
else
  echo "keystore already exists"
fi
