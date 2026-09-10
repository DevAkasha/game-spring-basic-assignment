package com.gamebasic.ranking.service;

import com.gamebasic.ranking.CardType;
import com.gamebasic.ranking.client.RankingClient;
import com.gamebasic.ranking.dto.RankingEntryResponse;
import com.gamebasic.ranking.dto.RankingResponse;
import com.gamebasic.ranking.dto.RankingSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private static final Set<String> CARD_TYPE_NAMES =
            Arrays.stream(CardType.values()).map(Enum::name).collect(Collectors.toSet());
    private static final List<String> BOSS_PHASES = List.of("THRONE", "UNBOUND", "ECLIPSE");

    private final RankingClient rankingClient;

    public RankingResponse getRankings() {
        RankingSource source = rankingClient.fetch();
        List<RankingSource.Record> records = source.getRecords();

        List<RankingSource.Record> targets = new ArrayList<>();
        for (RankingSource.Record record : records) {
            RankingSource.Run run = record.getRun();
            if ("CLEARED".equals(run.getStatus()) && run.getClearedFloor() == 10) {
                targets.add(record);
            }
        }

        List<RankingSource.Record> valid = new ArrayList<>();
        for (RankingSource.Record record : targets) {
            if (isValid(record)) {
                valid.add(record);
            }
        }
        int excludedCount = targets.size() - valid.size();

        valid.sort(
                Comparator.comparingInt((RankingSource.Record r) -> r.getRun().getDurationSeconds())
                        .thenComparing(r -> r.getRun().getFinalHp(), Comparator.reverseOrder())
                        .thenComparingLong(RankingSource.Record::getId)
        );

        List<RankingEntryResponse> entries = new ArrayList<>();
        Set<String> seenPlayers = new HashSet<>();
        int rank = 1;
        for (RankingSource.Record record : valid) {
            if (!seenPlayers.add(record.getPlayer().getId())) {
                continue;
            }
            entries.add(new RankingEntryResponse(
                    rank++,
                    record.getPlayer().getName(),
                    record.getRun().getDurationSeconds(),
                    record.getRun().getFinalHp(),
                    record.getBossFight().getTotalTurns(),
                    record.getDeck().getCards().size()
            ));
        }

        return new RankingResponse(
                source.getMeta().getSeason().getId(),
                records.size(),
                excludedCount,
                entries
        );
    }

    private boolean isValid(RankingSource.Record record) {
        RankingSource.Run run = record.getRun();
        if (run.getDurationSeconds() < run.getClearedFloor() * 30) return false;
        if (run.getFinalHp() < 1 || run.getFinalHp() > 99) return false;

        RankingSource.Deck deck = record.getDeck();
        if (deck == null || deck.getCards() == null) return false;
        List<RankingSource.Card> cards = deck.getCards();
        if (cards.size() < 9 || cards.size() > 20) return false;
        if (deck.getSize() != cards.size()) return false;

        Set<String> deckCardTypes = new HashSet<>();
        for (RankingSource.Card card : cards) {
            if (!CARD_TYPE_NAMES.contains(card.getCardType())) return false;
            if (card.getAcquiredFloor() < 0 || card.getAcquiredFloor() > 9) return false;
            deckCardTypes.add(card.getCardType());
        }

        RankingSource.BossFight boss = record.getBossFight();
        if (boss == null || boss.getPhases() == null || boss.getPhases().size() != 3) return false;
        int turnSum = 0;
        for (int i = 0; i < 3; i++) {
            RankingSource.Phase phase = boss.getPhases().get(i);
            if (!BOSS_PHASES.get(i).equals(phase.getPhase())) return false;
            if (phase.getTurns() < 1) return false;
            turnSum += phase.getTurns();
        }
        if (boss.getTotalTurns() != turnSum) return false;
        if (!deckCardTypes.contains(boss.getFinishingCard())) return false;

        return true;
    }
}
