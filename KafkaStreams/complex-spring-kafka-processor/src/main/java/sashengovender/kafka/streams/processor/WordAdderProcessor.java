package sashengovender.kafka.streams.processor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class is not used just yet. Still playing around with transformers
public class WordAdderProcessor implements Processor<String, String> {

  private final static Logger logger = LoggerFactory.getLogger(WordAdderProcessor.class.getName());
  private ProcessorContext processorContext;
  private KeyValueStore<String, String> wordStateStore;

  @Override
  public void init(ProcessorContext context) {
    this.processorContext = context;
    this.wordStateStore = (KeyValueStore<String, String>) context.getStateStore("words");
    logger.info("Word Processor created ");
  }

  @Override
  public void process(String key, String value) {
    //Add record to state store
    logger.info("Adding record to state store");
    this.wordStateStore.put(key, value);
  }

  @Override
  public void close() {

  }
}
