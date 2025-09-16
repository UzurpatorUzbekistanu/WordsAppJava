package com.bkleszcz.WordApp.database;

import com.bkleszcz.WordApp.model.EnglishSynonyms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnglishSynonymsRepository extends JpaRepository<EnglishSynonyms, Long> {

    List<EnglishSynonyms> findByEnglishWordId(Long englishWordId);

    boolean existsByEnglishWordId(Long englishWordId);

    boolean existsByEnglishWordIdAndSynonymIgnoreCase(Long englishWordId, String synonym);
}
