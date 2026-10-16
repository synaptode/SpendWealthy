.PHONY: help ios fast-ios android both test build-android

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

build-android:
	@./gradlew :composeApp:assembleDebug
