package com.bkleszcz.WordApp.database;

import com.bkleszcz.WordApp.model.PolishWord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolishWordRepository extends JpaRepository<PolishWord, Long> {

  Optional<PolishWord> findByWord(String word);

}
