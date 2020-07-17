package sashengovender.kafka.streams.processor;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sashengovender.kafka.streams.punctuator.WordPunctuator;

import java.time.Duration;

// This class is not used just yet. Still playing around with transformers
public class WordRemoverProcessor implements Transformer<String, String, KeyValue<String, String>> {

  private final static Logger logger = LoggerFactory.getLogger(WordRemoverProcessor.class.getName());
  private ProcessorContext processorContext;
  private KeyValueStore<String, String> wordStateStore;
  private Punctuator wordPunctuator;

  @Override
  public void init(ProcessorContext context) {
    this.processorContext = context;
    this.wordStateStore = (KeyValueStore<String, String>) context.getStateStore("words");

    logger.info("Word Punctuator created to run every {} seconds", 10);
    this.wordPunctuator = new WordPunctuator(this.processorContext, wordStateStore);
    processorContext.schedule(Duration.ofSeconds(10), PunctuationType.WALL_CLOCK_TIME, this.wordPunctuator);
  }

  @Override
  public KeyValue<String, String> transform(String key, String value) {
    return null; // dont send record down stream
  }

  @Override
  public void close() {

  }
}
