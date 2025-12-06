// yagu1324/java_project_backend/java_project_backend-main/javaproject/src/main/java/com/javaprgraming/javaproject/controller/ItemController.java

package com.javaprgraming.javaproject.controller;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Cloudinary 업로드 옵션용
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.javaprgraming.javaproject.repository.ItemRepository;
import com.javaprgraming.javaproject.repository.UserRepository;
import com.javaprgraming.javaproject.table.Item;
import com.javaprgraming.javaproject.table.ItemStatus;
import com.javaprgraming.javaproject.table.User;

@RestController
@RequestMapping("/api/auctions")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    // ======== ⭐ [시작] Cloudinary 객체 주입 ========
    // 4단계(CloudinaryConfig)에서 @Bean으로 등록한 객체를 주입받습니다.
    @Autowired
    private Cloudinary cloudinary;
    // ======== ⭐ [끝] Cloudinary 객체 주입 ========

    // ======== ⭐ [시작] 로컬 폴더 생성 로직 삭제 ========
    // private final Path rootLocation = Paths.get("uploads"); // <-- Cloudinary 사용
    // 시 삭제

    public ItemController() {
        // try {
        // Files.createDirectories(rootLocation); // <-- Cloudinary 사용 시 삭제
        // } catch (IOException e) {
        // throw new RuntimeException("Could not initialize storage", e);
        // }
    }
    // ======== ⭐ [끝] 로컬 폴더 생성 로직 삭제 ========

    /**
     * 경매 등록 (Cloudinary 이미지 업로드 처리 포함)
     * POST /api/auctions/register
     */
    @PostMapping("/register")
    public Map<String, Object> registerAuction(
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("startingPrice") long startingPrice,
            @RequestParam(value = "buyNowPrice", required = false) Long buyNowPrice,
            @RequestParam("bidIncrement") long bidIncrement,
            @RequestParam("endTime") String endTimeString,
            @RequestParam("sellerId") long sellerId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        Map<String, Object> response = new HashMap<>();

        try {
            User seller = userRepository.findById(sellerId)
                    .orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다: " + sellerId));

            Item item = new Item();
            item.setName(title);
            item.setCategory(category);
            item.setDescription(description);
            item.setStartPrice(startingPrice);
            item.setCurrentPrice(startingPrice);
            item.setBuyNowPrice(buyNowPrice);
            item.setBidIncrement(bidIncrement);

            ZonedDateTime zdt = ZonedDateTime.parse(endTimeString);
            item.setAuctionEndTime(zdt.toLocalDateTime());

            item.setStatus(ItemStatus.ON_AUCTION);
            item.setSeller(seller);
            item.setSellerUsername(seller.getUsername());

            // ======== ⭐ [시작] Cloudinary 이미지 업로드 로직으로 변경 ========
            if (imageFile != null && !imageFile.isEmpty()) {

                // 1. (핵심) Cloudinary로 파일 업로드
                // (파일의 바이트, "resource_type", "auto" 옵션)
                // "resource_type", "auto"는 이미지, 비디오 등을 자동으로 감지하라는 의미입니다.
                Map uploadResult = cloudinary.uploader().upload(
                        imageFile.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));

                // 2. (핵심) 업로드 성공 후, 반환된 '보안 URL'(https)을 가져옵니다.
                // Cloudinary는 "url"(http)과 "secure_url"(https)을 반환합니다.
                String imageUrl = uploadResult.get("secure_url").toString();

                // 3. DB에 Cloudinary의 https URL을 저장합니다.
                item.setImageUrl(imageUrl);

            } else {
                item.setImageUrl(null); // 이미지가 없으면 null
            }
            // ======== ⭐ [끝] Cloudinary 이미지 업로드 로직으로 변경 ========

            itemRepository.save(item);

            response.put("success", true);
            response.put("message", "경매가 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "경매 등록 실패: " + e.getMessage());
            e.printStackTrace(); // 콘솔에 자세한 오류 출력
        }
        return response;
    }

    @GetMapping("/{id}")
    public Item getItemDetail(@PathVariable("id") long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 상품이 존재하지 않습니다. id=" + id));
    }

    /**
     * 메인 페이지 경매 목록 (기존과 동일)
     * GET /api/auctions
     */
    @GetMapping
    public List<Item> getAllAuctions() {
        // ⭐ [수정] 경매 진행 중인 아이템만 조회
        return itemRepository.findByStatus(ItemStatus.ON_AUCTION);
    }
}