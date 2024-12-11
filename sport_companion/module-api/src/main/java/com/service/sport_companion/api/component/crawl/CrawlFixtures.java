package com.service.sport_companion.api.component.crawl;

import com.service.sport_companion.api.component.club.ClubsHandler;
import com.service.sport_companion.api.component.club.FixtureHandler;
import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.FixturesEntity;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlFixtures {

  private final ClubsHandler clubsHandler;
  private final FixtureHandler fixtureHandler;
  private final WebDriver driver;

  public void crawlFixtures(String year) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    Map<String, String> seriesMap = new HashMap<>();
    seriesMap.put("KBO 시범경기 일정", "1");
    seriesMap.put("KBO 정규시즌 일정", "0,9,6");
    seriesMap.put("KBO 포스트시즌 일정", "3,4,5,7");


    try {
      driver.get("https://www.koreabaseball.com/Schedule/Schedule.aspx#");
      driver.manage().window().maximize();

      for (Entry<String, String> series : seriesMap.entrySet()) {

        log.info("Series : {}}", series.getKey());

        // JavaScript로 `ddlSeries` 드롭다운 값을 설정
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('ddlSeries').value = '" + series.getValue() + "';");
        js.executeScript("document.getElementById('ddlSeries').dispatchEvent(new Event('change'));");

        // 각 월에 대해 크롤링
        for (int month = 1; month <= 12; month++) {
          List<FixturesEntity> fixturesList = new ArrayList<>();
          log.info("month = {}", month);

          // 연도 선택
          Select yearSelect = new Select(driver.findElement(By.id("ddlYear")));
          yearSelect.selectByVisibleText(year);

          // 월 선택
          String formattedMonth = String.format("%02d", month);
          Select monthSelect = new Select(driver.findElement(By.id("ddlMonth")));
          monthSelect.selectByVisibleText(formattedMonth);

          // 페이지 로드 완료 대기
          wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#tblScheduleList > tbody > tr")));

          // Jsoup으로 HTML 파싱
          Document doc = Jsoup.parse(driver.getPageSource());
          Elements baseballSchedule = doc.select("#tblScheduleList > tbody > tr");

          String lastDate = null; // 마지막으로 추출한 날짜를 저장

          for (Element schedule : baseballSchedule) {
            // 날짜가 있으면 업데이트, 없으면 이전 날짜 사용
            Element dayElement = schedule.selectFirst("td.day");
            if (dayElement != null) {
              lastDate = dayElement.text();
            }

            FixturesEntity fixtures = parseToFixturesEntity(year, lastDate, schedule, series.getKey());

            if (fixtures != null) {
              fixturesList.add(fixtures);

              // 로그 출력
              log.info("날짜: {}", fixtures.getFixtureDate());
              log.info("시간: {}", fixtures.getFixtureTime());
              log.info("홈팀: {} ({})", fixtures.getHomeClub().getClubName(), fixtures.getHomeScore());
              log.info("원정팀: {} ({})", fixtures.getAwayClub().getClubName(), fixtures.getAwayScore());
              log.info("비고: {}", fixtures.getNotes());
              log.info("----------------------------");
            }
          }
          fixtureHandler.saveFixtureList(fixturesList);
        } // month for end
      } // season for end
    } catch (Exception e) {
      log.error("크롤링 중 오류 발생: ", e);
    } finally {
      driver.quit();
    }
  }

  public FixturesEntity parseToFixturesEntity(String year, String lastDate, Element schedule, String season) {
    // 날짜 파싱
    LocalDate fixtureDate = (lastDate != null) ? parseToLocalDate(year, lastDate) : null;

    // 시간 파싱
    Element fixtureTimeElement = schedule.selectFirst("td.time > b");
    LocalTime fixtureTime = (fixtureTimeElement != null) ? LocalTime.parse(fixtureTimeElement.text()) : null;

    // 팀 이름 파싱 및 DB 조회
    Element homeClubElement = schedule.selectFirst("td.play > span:nth-child(3)");
    ClubsEntity homeClub = (homeClubElement != null) ? clubsHandler.findByFieldContaining(homeClubElement.text()) : null;

    Element awayClubElement = schedule.selectFirst("td.play > span:nth-child(1)");
    ClubsEntity awayClub = (awayClubElement != null) ? clubsHandler.findByFieldContaining(awayClubElement.text()) : null;

    // 점수 파싱
    Element homeScoreElement = null;
    Element awayScoreElement = null;

    // em 태그 내 span 태그의 개수를 확인
    Elements scoreSpans = schedule.select("td.play em > span");
    if (scoreSpans.size() >= 3) {
      // span 태그가 3개 이상인 경우 점수 파싱
      homeScoreElement = scoreSpans.get(2); // 세 번째 span
      awayScoreElement = scoreSpans.get(0); // 첫 번째 span
    }

    // 점수를 가져오거나 "-"로 설정
    int homeScore = (homeScoreElement != null) ? Integer.parseInt(homeScoreElement.text()) : 0;
    int awayScore = (awayScoreElement != null) ? Integer.parseInt(awayScoreElement.text()) : 0;

    // 경기장 및 비고
    int tdCount = schedule.select("td").size();
    int locationIndex = (tdCount > 8) ? 7 : 6;
    int notesIndex = locationIndex + 1;

    // 경기장 및 비고
    String notes = (tdCount > notesIndex) ? schedule.select("td").get(notesIndex).text() : "-";


    // 중요 필드가 null인 경우 저장하지 않음
    if (fixtureDate == null || fixtureTime == null || homeClub == null || awayClub == null) {
      log.warn("중요 필드가 null 입니다. 저장하지 않습니다: fixtureDate={}, fixtureTime={}, homeClub={}, awayClub={}",
          fixtureDate, fixtureTime, homeClub, awayClub);
      return null;
    }

    // FixturesEntity 생성
    return FixturesEntity.builder()
        .season(season)
        .fixtureDate(fixtureDate)
        .fixtureTime(fixtureTime)
        .homeClub(homeClub)
        .homeScore(homeScore)
        .awayClub(awayClub)
        .awayScore(awayScore)
        .notes(notes)
        .build();
  }

  private LocalDate parseToLocalDate(String year, String dayElement) {
    String date = dayElement.replaceAll("\\(.*\\)", "").trim();

    // DateTimeFormatter 생성
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    // 연도와 날짜를 결합
    String fullDate = year + "." + date;

    // LocalDate 변환
    return LocalDate.parse(fullDate, formatter);
  }
}
