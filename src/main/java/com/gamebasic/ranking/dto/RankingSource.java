package com.gamebasic.ranking.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RankingSource {
    private Meta meta;
    private List<Record> records;

    @Getter
    public static class Meta {
        private Season season;
    }

    @Getter
    public static class Season {
        private String id;
    }

    @Getter
    public static class Record {
        private long id;
        private Player player;
        private Run run;
        private BossFight bossFight;
        private Deck deck;
    }

    @Getter
    public static class Player {
        private String id;
        private String name;
    }

    @Getter
    public static class Run {
        private String status;
        private int clearedFloor;
        private int durationSeconds;
        private int finalHp;
    }

    @Getter
    public static class BossFight {
        private List<Phase> phases;
        private String finishingCard;
        private int totalTurns;
    }

    @Getter
    public static class Phase {
        private String phase;
        private int turns;
    }

    @Getter
    public static class Deck {
        private int size;
        private List<Card> cards;
    }

    @Getter
    public static class Card {
        private String cardType;
        private int acquiredFloor;
    }
}
