package com.javaprgraming.javaproject.scheduler;

import com.javaprgraming.javaproject.repository.BidRepository;
import com.javaprgraming.javaproject.repository.HistoryRepository;
import com.javaprgraming.javaproject.repository.ItemRepository;
import com.javaprgraming.javaproject.repository.UserRepository;
import com.javaprgraming.javaproject.table.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {

    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    public AuctionScheduler(ItemRepository itemRepository, BidRepository bidRepository, UserRepository userRepository,
            HistoryRepository historyRepository) {
        this.itemRepository = itemRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Scheduled(fixedRate = 10000) // ⭐ [수정] 10초마다 실행 (조금 더 자주 체크)
    @Transactional
    public void checkEndedAuctions() {
        // System.out.println("스케줄러 실행: 경매 종료 확인 중...");

        List<Item> endedItems = itemRepository.findByStatusAndAuctionEndTimeBefore(ItemStatus.ON_AUCTION,
                LocalDateTime.now());

        for (Item item : endedItems) {
            System.out.println("⭕ [스케줄러] 종료된 경매 발견: ID=" + item.getId() + ", 이름=" + item.getName());

            Bid highestBid = bidRepository.findTopByItemOrderByBidAmountDesc(item);

            if (highestBid != null) {
                // 낙찰 처리
                Long price = highestBid.getBidAmount();
                System.out.println("   - 최고 입찰가: " + price);

                User bidderInfo = highestBid.getBidder();
                if (bidderInfo == null) {
                    System.out.println("   ❌ 오류: 입찰자 정보가 없습니다.");
                    item.setStatus(ItemStatus.CLOSED);
                    continue;
                }

                // ⭐ [핵심] 최신 유저 정보를 DB에서 다시 조회 (동시성/트랜잭션 문제 방지)
                User buyer = userRepository.findById(bidderInfo.getId()).orElse(null);
                User seller = item.getSeller() != null ? userRepository.findById(item.getSeller().getId()).orElse(null)
                        : null;

                if (buyer != null && buyer.getPoints() >= price.intValue()) {
                    System.out.println("   - 구매자(" + buyer.getId() + ") 포인트 차감: " + buyer.getPoints() + " -> "
                            + (buyer.getPoints() - price));
                    buyer.setPoints(buyer.getPoints() - price.intValue());

                    if (seller != null) {
                        System.out.println("   - 판매자(" + seller.getId() + ") 포인트 지급: " + seller.getPoints() + " -> "
                                + (seller.getPoints() + price));
                        seller.setPoints(seller.getPoints() + price.intValue());
                        userRepository.save(seller);
                    }
                    userRepository.save(buyer);

                    // 거래 기록 저장
                    History history = new History(buyer, seller, item, price, LocalDateTime.now());
                    historyRepository.save(history);

                    // 아이템 상태 변경
                    item.setStatus(ItemStatus.SOLD);
                    System.out.println("   ✅ 낙찰 처리 완료: Item Status -> SOLD");
                } else {
                    // 포인트 부족으로 낙찰 취소 -> 유찰 처리
                    if (buyer == null)
                        System.out.println("   ❌ 오류: 구매자 정보를 찾을 수 없습니다.");
                    else
                        System.out.println("   ❌ 실패: 구매자 포인트 부족 (보유: " + buyer.getPoints() + ", 필요: " + price + ")");

                    item.setStatus(ItemStatus.CLOSED);
                }
            } else {
                // 유찰 처리
                System.out.println("   - 입찰자 없음. 유찰 처리.");
                item.setStatus(ItemStatus.CLOSED);
            }
            itemRepository.save(item);
        }
    }
}
