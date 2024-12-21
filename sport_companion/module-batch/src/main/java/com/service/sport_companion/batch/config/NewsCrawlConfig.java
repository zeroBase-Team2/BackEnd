package com.service.sport_companion.batch.config;

import com.service.sport_companion.batch.news.MbcNewsCrawling;
import com.service.sport_companion.batch.news.NewsItemWriter;
import com.service.sport_companion.domain.entity.NewsEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class NewsCrawlConfig {

  private final EntityManagerFactory entityManagerFactory;
  private final MbcNewsCrawling mbcNewsCrawling;
  private final NewsItemWriter newsItemWriter;

  @Bean
  public Job mbcCrawlingJob(JobRepository jobRepository, Step mbcCrawlingStep) {
    return new JobBuilder("mbcCrawlingJob", jobRepository)
      .start(mbcCrawlingStep)
      .build();
  }

  @Bean
  @JobScope
  public Step mbcCrawlingStep(JobRepository jobRepository) {
    return new StepBuilder("mbcCrawlingStep", jobRepository)
      .<NewsEntity, NewsEntity>chunk(100, new JpaTransactionManager(entityManagerFactory))
      .reader(new ListItemReader<>(mbcNewsCrawling.crawlingMain()))
      .writer(newsItemWriter)
      .build();
  }
}
