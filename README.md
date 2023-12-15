# mfinstruments
Service to load, save and update instruments like accounts, budgets equities etc

# build

mvn clean install -s settings.xml

# run local

java -jar ./target/mfinstruments-0.0.0-0-SNAPSHOT.jar --spring.config.activate.on-profile=local
