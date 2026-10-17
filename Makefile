.PHONY: help ios fast-ios android both test build-android check clean all

help:
	@./run.sh

ios:
	@./run.sh ios

fast-ios:
	@./run.sh fast-ios

android:
	@./run.sh android

both:
	@./run.sh both

all:
	@./run.sh both

test:
	@./run.sh test

check:
	@./gradlew compileDebugKotlinAndroid testDebugUnitTest

build-android:
	@./gradlew :composeApp:assembleDebug

clean:
	@./gradlew clean
