package com.bkleszcz.WordApp.database;

import com.bkleszcz.WordApp.model.PolishEnglishWord;
import com.bkleszcz.WordApp.model.PolishEnglishWordId;
import com.bkleszcz.WordApp.model.PolishWord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PolishEnglishWordRepository extends JpaRepository<PolishEnglishWord, PolishEnglishWordId> {

  List<PolishEnglishWord> findByPolishWord(PolishWord polishWord);

  @Query("SELECT p.englishWord.id FROM PolishEnglishWord p WHERE p.polishWord.id = :polishWordId")
  Optional<Integer> findByPolishWordId(@Param("polishWordId") Integer polishWordId);

}
