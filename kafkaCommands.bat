rem download kafka at https://kafka.apache.org/downloads
rem extract kafka in a folder

rem Please Note that these commands are only for windows

rem Start Zookeeper at localhost:2181
bin\windows\zookeeper-server-start.bat config\zookeeper.properties

rem Start kafka server at localhost:9092
bin\windows\kafka-server-start.bat config\server.properties

rem Create kafka topic
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic streams-input-topic

rem Verify topics have been created
bin\windows\kafka-topics.bat --zookeeper localhost:2181 --list

rem Start a kafka producer
bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic streams-input-topic

rem Start a kafka Consumer
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
    --topic sometopic ^
    --from-beginning ^
    --formatter kafka.tools.DefaultMessageFormatter ^
    --property print.key=true ^
    --property print.value=true ^
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

rem Run the streams application
bin\windows\kafka-run-class.bat org.apache.kafka.myFirstApplication
