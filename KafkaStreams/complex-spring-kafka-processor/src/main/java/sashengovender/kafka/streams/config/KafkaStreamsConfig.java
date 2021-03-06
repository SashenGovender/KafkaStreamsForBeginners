package sashengovender.kafka.streams.config;

public class KafkaStreamsConfig {

  private String applicationId;
  private String inputTopic;
  private String outputTopic;
  private String stateStoreName;

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getInputTopic() {
    return inputTopic;
  }

  public void setInputTopic(String inputTopic) {
    this.inputTopic = inputTopic;
  }

  public String getOutputTopic() {
    return outputTopic;
  }

  public void setOutputTopic(String outputTopic) {
    this.outputTopic = outputTopic;
  }

  public String getStateStoreName() {
    return stateStoreName;
  }

  public void setStateStoreName(String stateStoreName) {
    this.stateStoreName = stateStoreName;
  }
}
