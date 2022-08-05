build: gradle.properties
	gradlew clean
	gradlew buildPlugin -PlegacyBuild=false

build-legacy: gradle.properties
	gradlew clean
	gradlew buildPlugin -PlegacyBuild=true

publish: gradle.properties
	gradlew clean
	gradlew publishPlugin -PlegacyBuild=false

publish-legacy: gradle.properties
	gradlew clean
	gradlew publishPlugin -PlegacyBuild=true

publish-all: publish publish-legacy