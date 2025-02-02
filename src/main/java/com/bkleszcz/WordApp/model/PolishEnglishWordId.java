package com.bkleszcz.WordApp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;

@Embeddable
@Data
public class
PolishEnglishWordId implements Serializable {

  @Column(name = "polish_word_id")
  private Integer polishWordId;

  @Column(name = "english_word_id")
  private Integer englishWordId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PolishEnglishWordId that = (PolishEnglishWordId) o;
    return Objects.equals(polishWordId, that.polishWordId) &&
        Objects.equals(englishWordId, that.englishWordId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(polishWordId, englishWordId);
  }
}

