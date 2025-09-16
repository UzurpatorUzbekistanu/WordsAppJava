package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.AttemptsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishEnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishWordRepository;
import com.bkleszcz.WordApp.database.userRepository.UserRepository;
import com.bkleszcz.WordApp.model.Attempts;
import com.bkleszcz.WordApp.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.bkleszcz.WordApp.model.EnglishWord;
import com.bkleszcz.WordApp.model.PolishEnglishWord;
import com.bkleszcz.WordApp.model.PolishWord;
import com.bkleszcz.WordApp.model.dto.*;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AttemptsService {

  private final AttemptsRepository attemptsRepository;
  private final PolishWordRepository polishWordRepository;
  private final EnglishWordRepository englishWordRepository;
  private final UserRepository userRepository;
  private final PolishEnglishWordRepository polishEnglishWordRepository;
  private final ExperienceService experienceService;

  public AttemptsService(AttemptsRepository attemptsRepository,
                         PolishWordRepository polishWordRepository,
                         EnglishWordRepository englishWordRepository,
                         UserRepository userRepository, PolishEnglishWordRepository polishEnglishWordRepository, ExperienceService experienceService) {
    this.attemptsRepository = attemptsRepository;
    this.polishWordRepository = polishWordRepository;
    this.englishWordRepository = englishWordRepository;
    this.userRepository = userRepository;
    this.polishEnglishWordRepository = polishEnglishWordRepository;
    this.experienceService = experienceService;
  }

  public Long getLoggedUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String)) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        String username = ((UserDetails) principal).getUsername();
        return userRepository.findByUserName(username)
                .map(User::getId)
                .orElse(null);
      }
    }
    return 0L;
  }

  public User getLoggedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String)) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        String username = ((UserDetails) principal).getUsername();
        return userRepository.findByUserName(username)
                .orElse(null);
      }
    }
    return null;
  }

  public Date getDateToday(){
    LocalDate localDate = LocalDate.now();
      return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }


  private Date generateDateRepeat(int level) {
    LocalDate localDate = LocalDate.now();
    LocalDate localDateRepeat = null;

    switch(level){
      case 0:
      case 1:
        localDateRepeat =  localDate.plusDays(1);
        break;
      case 2:
        localDateRepeat = localDate.plusDays(3);
        break;
      case 3:
        localDateRepeat =  localDate.plusDays(7);
        break;
      case 4:
        localDateRepeat = localDate.plusDays(21);
        break;
      case 5:
        localDateRepeat =  localDate.plusDays(48);
        break;
      default:
        if (level > 5) {
          localDateRepeat = localDate.plusDays(120);
        }
        break;
    }
      assert localDateRepeat != null;
      return Date.from(localDateRepeat.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public List<Attempts> getAllAttemptsByUserId(Long userId) {
    return attemptsRepository.findByAppUserId(userId);
  }

  public List<AttemptsDto> getAttemptsDtosByUserId(Long userId) {
    List<Attempts> attempts = attemptsRepository.findByAppUserId(userId);
    return attempts.stream()
            .map(this::convertToDtoForPersonallyUserStatistic)
            .collect(Collectors.toList());
  }

  public List<AttemptsDto> getYearlyAttemptsDtos(){
    List<Attempts> attempts = attemptsRepository.findByDateLastTryAfterAndIsCorrectAnswerTrue(getDateYearEarlier());
    return attempts.stream()
            .map(this::convertToDtoForExperienceStatistic)
            .collect(Collectors.toList());
  }

  private AttemptsDto convertToDtoForPersonallyUserStatistic(Attempts attempt) {
    AttemptsDto dto = new AttemptsDto();
    dto.setUserId(attempt.getAppUser().getId());
    dto.setExperienceGained(attempt.getExperienceGained());
    dto.setDateLastTry(attempt.getDateLastTry());
    dto.setLevel(attempt.getLevelOfKnowledge());
    dto.setWithStrike(attempt.getWithStrike());
    return dto;
  }

  private  AttemptsDto convertToDtoForExperienceStatistic (Attempts attempt) {
    AttemptsDto dto = new AttemptsDto();
    dto.setUserId(attempt.getId());
    dto.setExperienceGained(attempt.getExperienceGained());
    dto.setDateLastTry(attempt.getDateLastTry());
    return dto;
  }


  private Date getDateYearEarlier(){
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -365);
    return calendar.getTime();
  }

  public AttemptResult doAttempt(String polishWord, String englishWordTyped, boolean isCorrect) {

    // 1) Znajdź polskie hasło — jeśli brak, przerwij czytelnym błędem
    PolishWord polishWordEntity = polishWordRepository.findFirstByWord(polishWord)
            .orElseThrow(() -> new IllegalArgumentException("Polish word not found: " + polishWord));

    // 2) Ustal angielskie słowo do ZAPISU w próbie:
    //    - jeśli odpowiedź poprawna → to, co wpisał user (mamy je w bazie)
    //    - jeśli odpowiedź błędna → weź POPRAWNE przypisane do polskiego słowa
    EnglishWord englishWordEntity;
    if (isCorrect) {
      // jeśli odpowiedź dobra → to co wpisał user (bo istnieje w bazie)
      englishWordEntity = englishWordRepository.findFirstByWord(englishWordTyped)
              .orElseThrow(() -> new IllegalArgumentException("English word not found: " + englishWordTyped));
    } else {
      // jeśli błędna → pobierz poprawne angielskie słowo powiązane z PolishWord
      englishWordEntity = polishEnglishWordRepository
              .findFirstByPolishWordId(Long.valueOf(polishWordEntity.getId()))
              .map(PolishEnglishWord::getEnglishWord) // teraz już działa
              .orElseThrow(() -> new IllegalStateException("No English mapping for: " + polishWord));
    }

    User userApi = getLoggedUser();                                       // jak było
    Date date = getDateToday();                                           // jak było
    int numberOfAttempts = attemptsRepository
            .countByPolishWordIdAndEnglishWordIdAndAppUser_Id(
                    polishWordEntity.getId(), englishWordEntity.getId(), userApi.getId()
            ) + 1;

    Optional<Attempts> newestAttemptOfThisWord = attemptsRepository
            .findFirstByPolishWordIdAndEnglishWordIdAndAppUser_IdOrderByDateLastTryDesc(
                    polishWordEntity.getId(), englishWordEntity.getId(), userApi.getId()
            );

    int correctAnswers = (isCorrect ? 1 : 0);
    int wrongAnswers   = (isCorrect ? 0 : 1);
    int levelOfKnowledge = (isCorrect ? 1 : 0);
    Date dateLastSuccess = isCorrect ? date : null;

    if (newestAttemptOfThisWord.isPresent()) {
      Attempts prev = newestAttemptOfThisWord.get();
      correctAnswers   = prev.getCorrectAnswers() + (isCorrect ? 1 : 0);  // ✅ bazuj na poprzednich
      wrongAnswers     = prev.getWrongAnswers()   + (isCorrect ? 0 : 1);  // ✅ U CIEBIE BYŁO getCorrectAnswers() — błąd
      levelOfKnowledge = prev.getLevelOfKnowledge() + (isCorrect ? 1 : 0);// ✅ U CIEBIE BYŁO getCorrectAnswers() — błąd
      dateLastSuccess  = isCorrect ? date : prev.getDateLastSuccess();     // ✅ nie nadpisuj przy błędnej
    }

    Date dateRepeat = generateDateRepeat(levelOfKnowledge);                // jak było
    int experienceGained = experienceService.doUserExperienceGainedAndStrike(isCorrect, userApi); // jak było

    Attempts newAttempt = Attempts.builder()
            .polishWord(polishWordEntity)
            .englishWord(englishWordEntity)           // ✅ zawsze istniejący encja EnglishWord
            .appUser(userApi)
            .dateLastTry(date)
            .dateLastSuccess(dateLastSuccess)
            .numberOfAttempts(numberOfAttempts)
            .correctAnswers(correctAnswers)           // ✅ użyj skumulowanych wartości
            .wrongAnswers(wrongAnswers)               // ✅ użyj skumulowanych wartości
            .levelOfKnowledge(levelOfKnowledge)       // ✅ użyj skumulowanej wiedzy
            .dateRepeat(dateRepeat)
            .experienceGained(experienceGained)
            .level(userApi.getLevel().getNumber())
            .withStrike(userApi.getStrikeCurrent().getStrikeCount())
            .isCorrectAnswer(isCorrect)
            .build();

    attemptsRepository.save(newAttempt);
    return new AttemptResult(experienceGained, userApi.getStrikeCurrent().getStrikeCount());
  }

  public List<DailyStatsDto> getDailyStats(Long userId) {          // staty dla usera
    List<Attempts> attempts = attemptsRepository.findByAppUserId(userId); // wszystkie próby
    return attempts.stream()                                       // strumień prób
            .collect(Collectors.groupingBy(a ->                         // grupuj po LocalDate
                    a.getDateLastTry().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            ))
            .entrySet().stream()                                         // przejdź po grupach
            .map(e -> {                                                  // zbuduj DTO
              var date = e.getKey();                                     // data dnia
              var list = e.getValue();                                   // próby w dniu
              long correct = list.stream().filter(Attempts::isCorrectAnswer).count(); // policz poprawne
              long incorrect = list.size() - correct;                    // policz błędne
              return new DailyStatsDto(date.toString(), correct, incorrect); // DTO
            })
            .sorted(Comparator.comparing(DailyStatsDto::getDate))        // posortuj po dacie
            .collect(Collectors.toList());                               // zwróć listę
  }

  public StatsSummaryDto getSummaryForUser(Long userId, String range) { // zwraca staty do tabeli
    var attempts = attemptsRepository.findByAppUserId(userId);          // pobierz wszystkie próby
    // 1) wylicz datę startu okna (daily/weekly/monthly/total)
    LocalDateTime from = switch (range == null ? "daily" : range.toLowerCase()) {
      case "weekly"  -> LocalDateTime.now().minusDays(7);
      case "monthly" -> LocalDateTime.now().minusDays(30);
      case "total"   -> null;                                           // brak filtra daty
      default        -> LocalDateTime.now().toLocalDate().atStartOfDay();// dziś od 00:00
    };
    // 2) przefiltruj po dacie jeśli potrzeba
    var filtered = attempts.stream().filter(a -> {
      if (from == null) return true;                                    // total = wszystko
      var when = a.getDateLastTry().toInstant()
              .atZone(ZoneId.systemDefault()).toLocalDateTime();            // konwersja na LDT
      return !when.isBefore(from);                                      // >= from
    }).toList();

    // 3) policz metryki
    long attemptsNew       = filtered.stream().filter(a -> a.getNumberOfAttempts() == 1).count();
    long correct           = filtered.stream().filter(Attempts::isCorrectAnswer).count();
    long review            = filtered.stream().filter(a -> a.getNumberOfAttempts() > 1).count();
    long completedReviews  = filtered.stream()
            .filter(a -> a.getNumberOfAttempts() > 1 && a.isCorrectAnswer())
            .count();

    return new StatsSummaryDto(attemptsNew, correct, review, completedReviews); // 4) zwróć DTO
  }


  @Data
  public static class AttemptResult {
    private final int experienceGained;
    private final int currentStrike;

    public AttemptResult(int experienceGained, int currentStrike) {
      this.experienceGained = experienceGained;
      this.currentStrike = currentStrike;
    }
  }

  public GuessCheckResponse doAttemptAndBuildResponse(        // metoda żądana przez kontroler
                                                              String polishWord,                                      // PL ze żądania
                                                              String englishWord,                                     // EN z żądania
                                                              boolean isCorrect                                       // wynik (po uwzgl. kary)
  ) {
    User user = getLoggedUser();                              // bieżący użytkownik
    int levelBefore = user.getLevel().getNumber();            // numer levelu przed zapisem

    AttemptResult ar = doAttempt(polishWord, englishWord, isCorrect); // zapis próby + XP/strike

    LevelInfoDto lvl = experienceService.buildLevelInfo(user, levelBefore); // pasek/awans

    List<String> canonicalEnglish = Collections.emptyList();  // lista poprawnych EN (gdy pudło)
    if (!isCorrect) {                                         // tylko przy błędnej odpowiedzi
      canonicalEnglish = findEnglishForPolish(polishWord);    // wyciągnij EN z relacji
    }

    return GuessCheckResponse.builder()                       // zbuduj DTO zwrotki
            .correct(isCorrect)                                   // status poprawności
            .currentStrike(ar.getCurrentStrike())                 // aktualny strike
            .experienceGained(ar.getExperienceGained())           // zdobyty XP
            .canonicalEnglish(canonicalEnglish)                   // poprawne EN (gdy pudło)
            .level(lvl)                                           // info o levelu/pasku
            .build();                                             // gotowe
  }

  private List<String> findEnglishForPolish(String polish) {  // pomocniczo: EN dla PL
    return polishWordRepository.findFirstByWord(polish)       // znajdź encję PL
            .map(pl -> polishEnglishWordRepository                // weź relacje PL→EN
                    .findByPolishWord(pl))                          // (masz już taką metodę)
            .orElse(Collections.emptyList())                      // brak → pusta lista
            .stream()                                             // strumień relacji
            .map(rel -> rel.getEnglishWord().getWord())           // zmapuj na tekst EN
            .distinct()                                           // usuń duplikaty
            .toList();                                            // zbierz do listy
  }
}
