package com.jvo.viewlogkafkaproducer.dto;

import java.io.Serializable;

public record CampaignDataDto(int networkId,
                              int campaignId,
                              String campaignName) implements Serializable {
}
