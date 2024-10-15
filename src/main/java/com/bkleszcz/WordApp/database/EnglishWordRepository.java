package com.bkleszcz.WordApp.database;

import com.bkleszcz.WordApp.model.EnglishWord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnglishWordRepository extends JpaRepository<EnglishWord, Long> {

  Optional<EnglishWord> findByWord(String word);


}
