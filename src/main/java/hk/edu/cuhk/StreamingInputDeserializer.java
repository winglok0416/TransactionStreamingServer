package hk.edu.cuhk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamingInputDeserializer {

  static final ObjectMapper mapper = new ObjectMapper();

  private StreamingInputDeserializer() {}

  public static StreamingInput readValue(String jsonString) throws JsonProcessingException {
    return mapper.readValue(jsonString, StreamingInput.class);
  }
}
