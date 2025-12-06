package com.javaprgraming.javaproject.controller;

import com.javaprgraming.javaproject.repository.ItemRepository;
import com.javaprgraming.javaproject.repository.UserRepository;
import com.javaprgraming.javaproject.table.Item;
import com.javaprgraming.javaproject.table.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

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

    // 아이템 삭제
    @DeleteMapping("/items/{itemId}")
    @ResponseBody
    public Map<String, Object> deleteItem(@PathVariable("itemId") Long itemId) {
        Map<String, Object> response = new HashMap<>();
        if (itemId == null) {
            response.put("success", false);
            response.put("message", "아이템 ID가 없습니다.");
            return response;
        }
        if (itemRepository.existsById(itemId)) {
            itemRepository.deleteById(itemId);
            response.put("success", true);
        } else {
            response.put("success", false);
            response.put("message", "아이템을 찾을 수 없습니다.");
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

    // 유저 강제 탈퇴 (관리자용)
    @DeleteMapping("/users/{userId}")
    @ResponseBody
    public Map<String, Object> deleteUser(@PathVariable("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return response;
        }

        // ⭐ [추가] 관리자 계정 삭제 방지
        if ("ADMIN".equals(user.getRole())) {
            response.put("success", false);
            response.put("message", "관리자 계정은 삭제할 수 없습니다.");
            return response;
        }

        // 1. 진행 중인 경매 물품 삭제
        // (주의: UserController의 로직과 동일하게 구현하거나, Service로 분리하는 것이 좋음.
        // 여기서는 간단히 로직을 복사해서 사용)
        // 실제로는 ItemStatus, ItemRepository 등을 사용하여 구현해야 함.
        // 하지만 AdminController에는 이미 itemRepository가 주입되어 있음.

        // * ItemStatus import 필요 (상단에 추가해야 함, 일단 로직만 작성)
        // * 아래 로직은 UserController와 중복되므로 리팩토링 대상이지만,
        // 빠른 구현을 위해 직접 작성함.

        // 진행 중인 경매 물품 삭제
        List<Item> userItems = itemRepository.findBySeller_Id(userId);
        for (Item item : userItems) {
            // ItemStatus.ON_AUCTION 등은 Enum이므로 import 필요
            // 여기서는 간단히 삭제 처리 (모든 물품 삭제? 아니면 진행중만?)
            // 요구사항: 강제 탈퇴 시 유저 정보 익명화 및 진행중 경매 취소
            // UserController 로직 참조: ON_AUCTION, CLOSED, CANCELLED 삭제. SOLD 유지.

            // Enum 비교를 위해 toString() 사용하거나 import 추가 필요.
            // 안전하게 import 추가를 권장하지만, replace_file_content로 import까지 한 번에 처리하기 어려울 수 있음.
            // 따라서 일단 삭제 로직만 구현.

            String status = item.getStatus().name();
            if ("ON_AUCTION".equals(status) || "CLOSED".equals(status) || "CANCELLED".equals(status)) {
                itemRepository.delete(item);
            }
        }

        // 2. 유저 정보 익명화
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
