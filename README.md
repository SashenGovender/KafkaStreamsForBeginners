# Kafka Streams for Beginners

This repository serves as an introduction into the kafka streams world. It will consist of several small projects demonstrating kafka and its use cases. Note that these projects have been written on a windows machine

These projects include:
* WordCounter -  Counts the number of times a word have been streamed. ie Hello 5
* FavouriteFood - A running count on a favourite food associated with users. Its important to note when a person favourite food changes, the food count will change
* spring-kafka-processor - Counts the number of times a word have been streamed using a spring boot application. Displays stream information every 5 seconds

## Getting Started
Please follow the below steps to setup the solution on your machine. 

### Prerequisites
* [Java 14 jdk](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html)
* [Maven 3.6](https://maven.apache.org/install.html) - Please follow this [guide](https://howtodoinjava.com/maven/how-to-install-maven-on-windows/)
* [IntelliJ IDE](https://www.jetbrains.com/idea/download/#section=windows)
* [Kafka](https://kafka.apache.org/downloads) - Choose the recommended Scala binary and download. I downloaded kafka to my c:\ drive .ie c:\kafka_2.12-2.5.0

### Kafka Setup
#### Zookeeper
* Open the cmd prompt in the location where kafka was extracted
* Start Kafka Zookeeper. Zookeeper is at localhost:2181
```
.\bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```
#### Kafka Server
* Open the cmd prompt in the location where kafka was extracted
* Start Kafka Server. Kafka is at localhost:9092
```
.\bin\windows\kafka-server-start.bat config\server.properties
```
#### Kafka Topic Creation
* Open the cmd prompt in the location where kafka was extracted
* Create Input Topic. It is important that all topics are created before they can be used in the kafka applications. Use the same cmd prompt to create all topics. Topics can be any name
```
.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic words-input
.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic words-count
```
* Verify the topic has been created
```
.\bin\windows\kafka-topics.bat --zookeeper localhost:2181 --list
```
#### Kafka Producer
* Open the cmd prompt in the location where kafka was extracted
* Create the Kafka Producer. Remember to include the topic name to publish to
```
.\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic words-input
```
#### Kafka Consumer
* Open the cmd prompt in the location where kafka was extracted
* Create the Kafka Consumer. Only the beginning and topic settings are compulsory
```
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
    --topic words-count ^
    --from-beginning ^
	--formatter kafka.tools.DefaultMessageFormatter ^
    --property print.key=true ^
    --property print.value=true ^
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```
#### Start Kafka Stream Application
* Using the cmd from the Kafka topic creation (or open a new cmd)
* Run the application specifying the full qualified name space
```
.\bin\windows\kafka-run-class.bat org.apache.kafka.streams.firstStreamsApplication
```
#### Notes
* Do not close any cmd prompts when running kafka
* Kafka has been setup using the default settings
 
## Resources

## Todo
* Lots

## Authors
* Sashen Govender

