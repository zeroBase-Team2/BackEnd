package com.service.sport_companion.batch.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

  private final JobLauncher jobLauncher;
  private final Job mbcCrawlingJob;

  // 매일 11:50, 23:50 mbcCrawlingJob 수행
  @Scheduled(cron = "0 50 11,23 * * *", zone = "Asia/Seoul")
  public void scheduleMbcCrawlingJob() throws Exception {
    log.info("MbcCrawlingJob 실행 시작 : {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    JobParameters jobParameters = new JobParametersBuilder()
      .addString("timestamp", String.valueOf(System.currentTimeMillis()))
      .toJobParameters();

    jobLauncher.run(mbcCrawlingJob, jobParameters);
    log.info("MbcCrawlingJob 실행 종료 : {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
  }
}
