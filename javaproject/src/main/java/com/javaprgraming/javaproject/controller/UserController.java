package com.javaprgraming.javaproject.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javaprgraming.javaproject.repository.BidRepository;
import com.javaprgraming.javaproject.repository.ItemRepository;
import com.javaprgraming.javaproject.repository.UserRepository;
import com.javaprgraming.javaproject.service.UserService;
import com.javaprgraming.javaproject.table.Bid;
import com.javaprgraming.javaproject.table.Item;
import com.javaprgraming.javaproject.table.ItemStatus;
import com.javaprgraming.javaproject.table.User;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService,
            ItemRepository itemRepository, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.bidRepository = bidRepository;
    }

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");
        String birthdate = request.get("birthdate");
        String phone = request.get("phone");

        if (userRepository.existsByUsername(username)) {
            response.put("success", false);
            response.put("message", "이미 사용중인 아이디입니다");
            return response;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setBirthdate(birthdate);
        user.setPhone(phone);

        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        userRepository.save(user);

        response.put("success", true);
        response.put("message", "회원가입 성공!");
        response.put("username", user.getUsername());
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            response.put("success", false);
            response.put("message", "아이디가 존재하지 않습니다");
            return response;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "비밀번호가 틀렸습니다");
            return response;
        }

        response.put("success", true);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("points", user.getPoints());
        response.put("role", user.getRole());
        return response;
    }

    @GetMapping("/user/{userId}")
    public Map<String, Object> getUser(@PathVariable("userId") long userId) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다");
            return response;
        }

        response.put("success", true);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("birthdate", user.getBirthdate());
        response.put("phone", user.getPhone());
        response.put("points", user.getPoints());
        response.put("role", user.getRole());
        response.put("hasProfileImage", user.getProfileImage() != null);

        return response;
    }

    @PostMapping("/user/{userId}/profile-image")
    public Map<String, Object> uploadProfileImage(@PathVariable("userId") long userId,
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                response.put("success", false);
                response.put("message", "사용자를 찾을 수 없습니다");
                return response;
            }
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "파일이 비어있습니다");
                return response;
            }
            user.setProfileImage(file.getBytes());
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "프로필 이미지가 업로드되었습니다");
            return response;
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/user/{userId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable("userId") long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(user.getProfileImage(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}/profile-image")
    public Map<String, Object> deleteProfileImage(@PathVariable("userId") long userId) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("success", false);
            return response;
        }
        user.setProfileImage(null);
        userRepository.save(user);
        response.put("success", true);
        return response;
    }

    @GetMapping("/user/{userId}/points")
    public Map<String, Object> getPoints(@PathVariable("userId") String userIdOrName) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long resolvedId = resolveUserId(userIdOrName);
            int points = userService.getPoints(resolvedId);
            response.put("success", true);
            response.put("points", points);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/user/{userId}/points")
    public Map<String, Object> addPoints(@PathVariable("userId") String userIdOrName,
            @RequestBody Map<String, Integer> request) {
        Map<String, Object> response = new HashMap<>();
        Integer add = request.get("points");

        try {
            Long resolvedId = resolveUserId(userIdOrName);
            User updated = userService.addPoints(resolvedId, add != null ? add : 0);
            response.put("success", true);
            response.put("points", updated.getPoints());
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PutMapping("/user/{userId}")
    public Map<String, Object> updateUser(@PathVariable("userId") long userId,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다");
            return response;
        }

        String newPassword = request.get("newPassword");
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        String phone = request.get("phone");
        if (phone != null) {
            user.setPhone(phone);
        }

        userRepository.save(user);

        response.put("success", true);
        response.put("message", "회원정보가 수정되었습니다");
        return response;
    }

    @GetMapping("/user/{userId}/auctions")
    public List<Map<String, Object>> getMyAuctions(@PathVariable("userId") long userId) {
        List<Item> items = itemRepository.findBySeller_Id(userId);
        return items.stream().map(this::convertItemToMap).collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}/bids")
    public List<Map<String, Object>> getMyBids(@PathVariable("userId") long userId) {
        List<Bid> bids = bidRepository.findByBidder_Id(userId);
        Map<Long, Item> itemMap = new HashMap<>();
        Map<Long, Long> myMaxBidMap = new HashMap<>();

        for (Bid bid : bids) {
            Item item = bid.getItem();
            itemMap.put(item.getId(), item);

            Long currentMax = myMaxBidMap.getOrDefault(item.getId(), 0L);
            if (bid.getBidAmount() > currentMax) {
                myMaxBidMap.put(item.getId(), bid.getBidAmount());
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Item item : itemMap.values()) {
            boolean isRunning = item.getStatus() == ItemStatus.ON_AUCTION;
            boolean isTimeLeft = item.getAuctionEndTime() != null && item.getAuctionEndTime().isAfter(now);

            // 경매가 진행중이고 시간이 남았을 때만 입찰 목록에 표시
            if (isRunning && isTimeLeft) {
                Map<String, Object> map = convertItemToMap(item);
                map.put("myBidPrice", myMaxBidMap.get(item.getId()));

                boolean isWinning = item.getCurrentPrice().equals(myMaxBidMap.get(item.getId()));
                map.put("isWinning", isWinning);

                result.add(map);
            }
        }
        return result;
    }

    @GetMapping("/user/{userId}/won")
    public List<Map<String, Object>> getMyWonAuctions(@PathVariable("userId") long userId) {
        List<Bid> bids = bidRepository.findByBidder_Id(userId);
        Map<Long, Long> myMaxBidMap = new HashMap<>();
        Map<Long, Item> itemMap = new HashMap<>();

        for (Bid bid : bids) {
            Item item = bid.getItem();
            itemMap.put(item.getId(), item);
            Long currentMax = myMaxBidMap.getOrDefault(item.getId(), 0L);
            if (bid.getBidAmount() > currentMax) {
                myMaxBidMap.put(item.getId(), bid.getBidAmount());
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Item item : itemMap.values()) {
            boolean isTimeEnded = item.getAuctionEndTime() != null && item.getAuctionEndTime().isBefore(now);
            boolean isSold = item.getStatus() == ItemStatus.SOLD;

            // 시간이 끝났거나, 이미 판매 완료(즉시 구매 등)된 경우
            if (isTimeEnded || isSold) {
                if (item.getCurrentPrice().equals(myMaxBidMap.get(item.getId()))) {
                    Map<String, Object> map = convertItemToMap(item);
                    map.put("finalPrice", item.getCurrentPrice());
                    // 즉시 구매의 경우 endTime이 미래일 수 있으므로, SOLD 상태라면 현재 시간을 표시하거나 endTime을 표시
                    map.put("wonDate", item.getAuctionEndTime().toString());
                    result.add(map);
                }
            }
        }
        return result;
    }

    @PostMapping("/user/{userId}/withdraw")
    @ResponseBody
    @Transactional
    public Map<String, Object> withdrawUser(@PathVariable("userId") long userId) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return response;
        }

        // ⭐ [추가] 관리자 계정 탈퇴 방지
        if ("ADMIN".equals(user.getRole())) {
            response.put("success", false);
            response.put("message", "관리자 계정은 탈퇴할 수 없습니다.");
            return response;
        }

        List<Item> userItems = itemRepository.findBySeller_Id(userId);
        for (Item item : userItems) {
            if (item.getStatus() == ItemStatus.ON_AUCTION || item.getStatus() == ItemStatus.CLOSED
                    || item.getStatus() == ItemStatus.CANCELLED) {
                itemRepository.delete(item);
            }
        }

        String anonymousName = "탈퇴한 유저_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        user.setUsername(anonymousName);
        user.setPassword("");
        user.setEmail(anonymousName + "@deleted.com");
        user.setPhone(null);
        user.setBirthdate(null);
        user.setProfileImage(null);
        user.setPoints(0);
        user.setRole("DELETED");

        userRepository.save(user);

        response.put("success", true);
        response.put("message", "회원 탈퇴가 완료되었습니다.");
        return response;
    }

    private Map<String, Object> convertItemToMap(Item item) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getId());
        map.put("title", item.getName());
        map.put("currentPrice", item.getCurrentPrice());
        map.put("imageUrl", item.getImageUrl());
        map.put("endTime", item.getAuctionEndTime());
        map.put("status", item.getStatus());
        map.put("bidCount", 0);
        return map;
    }

    private Long resolveUserId(String userIdOrName) {
        if (userIdOrName == null)
            throw new RuntimeException("사용자 식별자가 비어있습니다");
        try {
            return Long.parseLong(userIdOrName);
        } catch (NumberFormatException ex) {
            User user = userRepository.findByUsername(userIdOrName).orElse(null);
            if (user == null)
                throw new RuntimeException("사용자를 찾을 수 없습니다: " + userIdOrName);
            return user.getId();
        }
    }
}