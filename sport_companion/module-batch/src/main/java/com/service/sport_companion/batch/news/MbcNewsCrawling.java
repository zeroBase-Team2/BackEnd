package com.service.sport_companion.batch.news;

import com.service.sport_companion.core.component.WebDriverHandler;
import com.service.sport_companion.core.exception.GlobalException;
import com.service.sport_companion.domain.entity.NewsEntity;
import com.service.sport_companion.domain.model.type.FailedResultType;
import com.service.sport_companion.domain.model.type.NewsType;
import com.service.sport_companion.domain.model.type.UrlType;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MbcNewsCrawling {

  private final String baseUrl = UrlType.MBC_NEWS_URL.getUrl();
  private final WebDriverHandler webDriverHandler;

  // 배치로 실행시킬 MBC News 크롤링 메인 작업
  @Transactional
  public List<NewsEntity> crawlingMain() {
    log.info("MBC 뉴스 크롤링 시작");
    String url = baseUrl + "/more/search/?mainSearch=%EC%95%BC%EA%B5%AC";
    int pageSize = 5;

    WebDriver driver = webDriverHandler.getDriver();
    driver.get(url);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(
      d -> ((JavascriptExecutor) d).executeScript("return jQuery.active == 0"));

    try {
      List<NewsEntity> totalNewsList = parsing(driver.getPageSource());

      while(existsNextPage(driver)) {
        List<NewsEntity> newsData = parsing(driver.getPageSource());
        totalNewsList.addAll(newsData);

        if (newsData.size() < pageSize) break;
      }

      Collections.reverse(totalNewsList);
      return totalNewsList;

    } finally {
      driver.quit();
      log.info("MBC 뉴스 크롤링 종료");
    }
  }

  // 스크롤을 내려 다음 페이지 버튼을 누르며, 다음 페이지가 없으면 false를 리턴
  public static boolean existsNextPage(WebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    final String prevHeadline = driver.findElement(By.cssSelector(".tit.ellipsis2")).getText();

    List<WebElement> nextButton = driver.findElements(By.className("next"));
    if (nextButton.isEmpty()) return false;

    // 스크롤을 내려 다음 페이지 이동 버튼 클릭
    Actions actions = new Actions(driver);
    actions.moveToElement(nextButton.get(0)).perform();
    nextButton.get(0).sendKeys(Keys.ENTER);

    // 화면이 업데이트 될 때까지 wait
    wait.until(webDriver -> {
      WebElement element = webDriver.findElement(By.cssSelector(".tit.ellipsis2"));
      try {
        return !prevHeadline.equals(element.getText());
      } catch (StaleElementReferenceException e) {
        return false;
      }
    });

    return true;
  }

  // pageSource에서 파싱하여 필요한 뉴스 데이터 가져오기
  public List<NewsEntity> parsing(String pageSource) {
    Document doc = Jsoup.parse(pageSource);
    Elements items = doc.getElementsByClass("result_list")
      .get(0)
      .select("ul > li");

    List<NewsEntity> newsItemList = new ArrayList<>();
    for (Element item : items) {
      String newsDate = item.select("a > .txt_w > .sub > .date").text().trim();
      String today = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      if (!newsDate.equals(today)) break;

      String newsLink = item.select("a").attr("href");
      String thumbnail = item.select("a > .img > img").attr("src");
      String headline = item.select("a > .txt_w > .tit").text();
      NewsType newsType = (item.select("a > .img").hasClass("ico_vod")) ?
        NewsType.VIDEO : NewsType.TEXT;

      if (newsLink.isEmpty() || thumbnail.isEmpty() || headline.isEmpty()) {
        throw new GlobalException(FailedResultType.MBC_NEWS_PARSING_FAILED);
      }

      newsItemList.add(NewsEntity.builder()
        .newsLink(baseUrl + newsLink)
        .thumbnail(thumbnail)
        .headline(headline)
        .newsType(newsType)
        .newsDate(LocalDate.now())
        .build());
    }

    return newsItemList;
  }
}
