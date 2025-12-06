package com.javaprgraming.javaproject.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.javaprgraming.javaproject.repository.BidRepository;
import com.javaprgraming.javaproject.repository.HistoryRepository;
import com.javaprgraming.javaproject.repository.ItemRepository;
import com.javaprgraming.javaproject.repository.UserRepository;
import com.javaprgraming.javaproject.table.Item;
import com.javaprgraming.javaproject.table.ItemStatus;
import com.javaprgraming.javaproject.table.User;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BidRepository bidRepository;
    
    @Autowired
    private HistoryRepository historyRepository;

    // 모든 유저 조회
    @GetMapping("/users")
    @ResponseBody
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 모든 아이템 조회
    @GetMapping("/items")
    @ResponseBody
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // ⭐ [수정됨] 아이템 삭제 (관련 기록도 함께 삭제)
    @DeleteMapping("/items/{itemId}")
    @ResponseBody
    @Transactional // 도중에 에러나면 롤백
    public Map<String, Object> deleteItem(@PathVariable("itemId") Long itemId) {
        Map<String, Object> response = new HashMap<>();
        
        if (itemId == null) {
            response.put("success", false);
            response.put("message", "아이템 ID가 없습니다.");
            return response;
        }
        
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            response.put("success", false);
            response.put("message", "이미 삭제되었거나 없는 물품입니다.");
            return response;
        }

        try {
            // 1. 자식 데이터 삭제 (순서 중요: 입찰 -> 거래내역 -> 아이템)
            bidRepository.deleteByItem_Id(itemId);
            historyRepository.deleteByItem_Id(itemId);

            // 2. 부모 데이터(아이템) 삭제
            itemRepository.deleteById(itemId);

            response.put("success", true);
            response.put("message", "물품과 관련 기록이 모두 삭제되었습니다.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }

    // 유저 포인트 수정
    @PutMapping("/users/{userId}/points")
    @ResponseBody
    public Map<String, Object> updateUserPoints(@PathVariable("userId") Long userId,
            @RequestBody Map<String, Integer> request) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPoints(request.get("points"));
            userRepository.save(user);
            response.put("success", true);
        } else {
            response.put("success", false);
            response.put("message", "유저를 찾을 수 없습니다.");
        }
        return response;
    }

    // ⭐ [수정됨] 유저 강제 탈퇴 (관리자용) - 물품 처리 포함
    @DeleteMapping("/users/{userId}")
    @ResponseBody
    @Transactional
    public Map<String, Object> deleteUser(@PathVariable("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return response;
        }

        // 관리자 계정 삭제 방지
        if ("ADMIN".equals(user.getRole())) {
            response.put("success", false);
            response.put("message", "관리자 계정은 삭제할 수 없습니다.");
            return response;
        }

        // 1. 진행 중인 경매 물품 삭제
        // (진행중, 종료됨, 취소됨 상태의 물품은 아예 삭제해버림 / 이미 팔린건 유지)
        List<Item> userItems = itemRepository.findBySeller_Id(userId);
        for (Item item : userItems) {
            if (item.getStatus() == ItemStatus.ON_AUCTION || 
                item.getStatus() == ItemStatus.CLOSED || 
                item.getStatus() == ItemStatus.CANCELLED) {
                
                // 물품 삭제 전 관련 기록 청소 (필수)
                bidRepository.deleteByItem_Id(item.getId());
                historyRepository.deleteByItem_Id(item.getId());
                
                itemRepository.delete(item);
            }
        }

        // 2. 유저 정보 익명화 (완전 삭제 대신 탈퇴 회원으로 변경)
        String anonymousName = "탈퇴한 유저_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        user.setUsername(anonymousName);
        user.setPassword(""); // 비밀번호 삭제
        user.setEmail(anonymousName + "@deleted.com");
        user.setPhone(null);
        user.setBirthdate(null);
        user.setProfileImage(null);
        user.setPoints(0);
        user.setRole("DELETED");

        userRepository.save(user);

        response.put("success", true);
        response.put("message", "회원 강제 탈퇴가 완료되었습니다.");
        return response;
    }
}