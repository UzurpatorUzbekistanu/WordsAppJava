package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.AttemptsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishEnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishWordRepository;
import com.bkleszcz.WordApp.database.userRepository.UserRepository;
import com.bkleszcz.WordApp.model.Attempts;
import com.bkleszcz.WordApp.domain.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.bkleszcz.WordApp.model.EnglishWord;
import com.bkleszcz.WordApp.model.PolishWord;
import com.bkleszcz.WordApp.model.dto.AttemptsDto;
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

  public AttemptResult doAttempt(String polishWord, String englishWord, boolean isCorrect){

    PolishWord polishWordEntity = polishWordRepository.findFirstByWord(polishWord).get();
    EnglishWord englishWordEntity = englishWordRepository.findFirstByWord(englishWord).get();
    User userApi = getLoggedUser();
    Date date = getDateToday();
    int numberOfAttempts = attemptsRepository.countByPolishWordIdAndEnglishWordIdAndAppUser_Id(
            polishWordEntity.getId(),
            englishWordEntity.getId(),
            userApi.getId()
    ) + 1;

    Optional<Attempts> newestAttemptOfThisWord = attemptsRepository.findFirstByPolishWordIdAndEnglishWordIdAndAppUser_IdOrderByDateLastTryDesc(
            polishWordEntity.getId(),
            englishWordEntity.getId(),
            userApi.getId()
    );

    int correctAnswers = (isCorrect ? 1 : 0);
    int wrongAnswers = (isCorrect ? 0 : 1);
    int levelOfKnowledge = (isCorrect ? 1 : 0);
    Date dateLastSucces = date;


    if(newestAttemptOfThisWord.isPresent()){
      correctAnswers = newestAttemptOfThisWord.get().getCorrectAnswers() + (isCorrect ? 1 : 0);
      wrongAnswers = newestAttemptOfThisWord.get().getCorrectAnswers() + (isCorrect ? 0 : 1);
      levelOfKnowledge = newestAttemptOfThisWord.get().getCorrectAnswers() + (isCorrect ? 1 : 0);
      dateLastSucces = isCorrect ? date : newestAttemptOfThisWord.get().getDateLastSuccess();
    }

    Date dateRepeat = generateDateRepeat(levelOfKnowledge);

    int experienceGained = experienceService.doUserExperienceGainedAndStrike(isCorrect, userApi);


    Attempts newAttempt = Attempts.builder()
            .polishWord(polishWordEntity)
            .englishWord(englishWordEntity)
            .appUser(userApi)
            .dateLastTry(date)
            .dateLastSuccess(dateLastSucces)
            .numberOfAttempts(numberOfAttempts)
            .correctAnswers(isCorrect ? 1 : 0)
            .wrongAnswers(isCorrect ? 0 : 1)
            .levelOfKnowledge(isCorrect ? 1 : 0)
            .dateRepeat(dateRepeat)
            .experienceGained(experienceGained)
            .level(userApi.getLevel().getNumber())
            .withStrike(userApi.getStrikeCurrent().getStrikeCount())
            .isCorrectAnswer(isCorrect)
            .build();

    attemptsRepository.save(newAttempt);
    return new AttemptResult(experienceGained, userApi.getStrikeCurrent().getStrikeCount());
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
}
