#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  openssl aes-256-cbc -K $encrypted_d171de1d648a_key -iv $encrypted_d171de1d648a_iv -in my.travis.gpg.enc -out my.travis.gpg -d
  ./gradlew uploadArchives -PossrhUsername=${SONATYPE_USERNAME} -PossrhPassword=${SONATYPE_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_KEY_PASSPHRASE} -Psigning.secretKeyRingFile=my.travis.gpg
fi
if [ "$TRAVIS_BRANCH" = 'release' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  openssl aes-256-cbc -K $encrypted_d171de1d648a_key -iv $encrypted_d171de1d648a_iv -in my.travis.gpg.enc -out my.travis.gpg -d
  ./gradlew release -Prelease.useAutomaticVersion=true  -PossrhUsername=${SONATYPE_USERNAME} -PossrhPassword=${SONATYPE_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_KEY_PASSPHRASE} -Psigning.secretKeyRingFile=my.travis.gpg
fi
