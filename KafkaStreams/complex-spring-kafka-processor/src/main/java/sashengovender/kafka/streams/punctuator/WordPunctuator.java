package sashengovender.kafka.streams.punctuator;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordPunctuator implements Punctuator {

  private final static Logger logger = LoggerFactory.getLogger(WordPunctuator.class.getName());
  private ProcessorContext processorContext;
  private KeyValueStore<String, String> wordStateStore;

  public WordPunctuator(ProcessorContext processorContext, KeyValueStore<String, String> wordStateStore) {
    this.processorContext = processorContext;
    this.wordStateStore = wordStateStore;
  }

  @Override
  public void punctuate(long currentStreamTimeInMs) {
    try (final KeyValueIterator<String, String> iterator = this.wordStateStore.all()) {
      while (iterator.hasNext()) {
        final KeyValue<String, String> record = iterator.next();

        //remove any word that is 15 characters long
        if (record.value.length() > 15) {
          logger.info("Found a long word({}). Removing from state store ", record.value);
          wordStateStore.delete(record.key);
          processorContext.forward(record.key, record.value); // send the long word down stream
        }
      }
    }
  }

}
