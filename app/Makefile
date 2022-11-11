setup:
	gradle wrapper --gradle-version 7.4

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	./gradlew run

install:
	./gradlew install

start-dist:
	./build/install/app/bin/app

generate-migrations:
	./gradlew generateMigrations

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

#image-build:
#	docker build -t hexletcomponents/java-project-72:latest .
#
#image-push:
#	docker push hexletcomponents/java-project-72:latest

.PHONY: build