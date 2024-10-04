package com.jvo.viewlogkafkaproducer.services;

import com.jvo.viewlogkafkaproducer.dto.CampaignDataDto;
import com.jvo.viewlogkafkaproducer.dto.ViewLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ViewLogService {

    private final KafkaTemplate<String, ViewLogDto> kafkaTemplate;
    private final CampaignDataService campaignDataService;

    @Value("${spring.kafka.view-logs-topic}")
    private String topicName;
    private final Random random = new Random();

    public ViewLogService(KafkaTemplate<String, ViewLogDto> kafkaTemplate, CampaignDataService campaignDataService) {
        this.kafkaTemplate = kafkaTemplate;
        this.campaignDataService = campaignDataService;
    }

    @Scheduled(fixedRate = 100)
    public void generateViewLog() {

        campaignDataService.getRandomCampaign()
                .map(this::getViewLog)
                .map(this::publishToKafkaTopic)
                .ifPresentOrElse(this::processResult, ViewLogService::raiseException);
    }

    private ViewLogDto getViewLog(CampaignDataDto campaign) {

        LocalDateTime now = LocalDateTime.now();

        return new ViewLogDto(
                UUID.randomUUID().toString(),
                now.minusSeconds(random.nextInt(10)),
                now,
                random.nextInt(500),
                campaign.campaignId()
        );
    }

    private CompletableFuture<SendResult<String, ViewLogDto>> publishToKafkaTopic(ViewLogDto viewLog) {
        return kafkaTemplate.send(topicName, String.valueOf(viewLog.campaignId()), viewLog);
    }

    private CompletableFuture<Void> processResult(CompletableFuture<SendResult<String, ViewLogDto>> result) {
        return result.thenAcceptAsync(this::logSendResult);
    }

    private void logSendResult(SendResult<String, ViewLogDto> sendResult) {
        log.info("Publishing message to topic {}. Result: {}", topicName, sendResult);
    }

    private static void raiseException() {
        throw new IllegalArgumentException("Campaigns aren't present!");
    }

}
