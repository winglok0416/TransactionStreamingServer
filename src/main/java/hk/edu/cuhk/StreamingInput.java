package hk.edu.cuhk;

import java.io.Serializable;

public class StreamingInput implements Serializable {

  private String time;
  private Double price;
  private Long volume;

  public StreamingInput() {
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Long getVolume() {
    return volume;
  }

  public void setVolume(Long volume) {
    this.volume = volume;
  }

  @Override
  public String toString() {
    return "StreamingInput{" +
        "time='" + time + '\'' +
        ", price=" + price +
        ", volume=" + volume +
        '}';
  }
}
