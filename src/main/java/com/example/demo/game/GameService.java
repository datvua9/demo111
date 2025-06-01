package com.example.demo.game;

import com.example.demo.game.component.RatingCalculator;
import com.example.demo.game.data.GameDTO;
import com.example.demo.game.data.Games;
import com.example.demo.reviews.ReviewsRepository;
import com.example.demo.reviews.data.ReviewDTO;
import com.example.demo.reviews.data.Reviews;
import com.example.demo.service.RatingCalculatorHelper;
import com.example.demo.user.UserProfileRepository;
import com.example.demo.user.data.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService implements IGameService {
    private final GamesRepository gamesRepository;
    private final ReviewsRepository reviewsRepository;
    private final UserProfileRepository userProfileRepository; // Inject
    private final RatingCalculatorHelper ratingCalculatorHelper; // Inject helper mới
    private final RatingCalculator ratingCalculator;


    @Autowired
    public GameService(GamesRepository gamesRepository,
                       ReviewsRepository reviewsRepository,
                       UserProfileRepository userProfileRepository,
                       RatingCalculatorHelper ratingCalculatorHelper,
                       RatingCalculator ratingCalculator) {
        this.gamesRepository = gamesRepository;
        this.reviewsRepository = reviewsRepository;
        this.userProfileRepository = userProfileRepository;
        this.ratingCalculatorHelper = ratingCalculatorHelper;
        this.ratingCalculator = ratingCalculator;
    }

    private ReviewDTO convertToReviewDTO(Reviews review, String gameName) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReview_id(review.getReview_id());

        if (review.getUser() != null) {
            User user = review.getUser();
            reviewDTO.setUserId(user.getUserId());
            reviewDTO.setUsername(user.getUsername());
            // Lấy avatar
            userProfileRepository.findByUser(user).ifPresent(profile -> { // Bây giờ .ifPresent() sẽ hoạt động
                reviewDTO.setUserAvatar(profile.getAvatar()); // Đảm bảo UserProfile.java có getAvatar()
            });
        } else {
            reviewDTO.setUsername("Anonymous"); // Hoặc giá trị mặc định khác
            // reviewDTO.setUserAvatar(null); // Hoặc avatar mặc định nếu có
        }

        reviewDTO.setGameId(review.getGameId()); // gameId từ Review entity
        reviewDTO.setGameName(gameName); // Tên game được truyền vào

        reviewDTO.setMusic_rating(review.getMusic_rating());
        reviewDTO.setGameplay_rating(review.getGameplay_rating());
        reviewDTO.setStory_rating(review.getStory_rating());
        reviewDTO.setGraphic_rating(review.getGraphic_rating());
        reviewDTO.setComment(review.getComment());
        reviewDTO.setPlatform(review.getPlatform());
        reviewDTO.setReviewDate(review.getReviewDate());
        return reviewDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public GameDTO getGameById(Long id) {
        Games gameEntity = gamesRepository.findById(id).orElse(null);
        if (gameEntity == null) {
            return null;
        }

        GameDTO gameDTO = new GameDTO(gameEntity); // Tạo GameDTO từ entity Game

        // Tải danh sách reviews từ entity và chuyển sang ReviewDTO
        List<Reviews> reviewEntities = reviewsRepository.findByGame_GameId(id); // Hoặc tên phương thức tương ứng trong repo

        List<ReviewDTO> reviewDTOs = reviewEntities.stream()
                .map(reviewEntity -> convertToReviewDTO(reviewEntity, gameEntity.getName()))
                .collect(Collectors.toList());
        gameDTO.setReviews(reviewDTOs); // Gán danh sách ReviewDTO vào GameDTO

        // Tính rating tổng thể cho GameDTO (sử dụng RatingCalculator cũ với List<Reviews> entities)
        float overallGameRating = ratingCalculator.calculateOverallAverage(reviewEntities);
        gameDTO.setRating(overallGameRating); // Đây là rating chính của GameDTO

        // Tính các điểm trung bình chi tiết từ List<ReviewDTO> và set vào GameDTO
        gameDTO.setAvgGameplay(ratingCalculatorHelper.calculateAverageRatingForDTOs(reviewDTOs, ReviewDTO::getGameplay_rating));
        gameDTO.setAvgMusic(ratingCalculatorHelper.calculateAverageRatingForDTOs(reviewDTOs, ReviewDTO::getMusic_rating));
        gameDTO.setAvgGraphic(ratingCalculatorHelper.calculateAverageRatingForDTOs(reviewDTOs, ReviewDTO::getGraphic_rating));
        gameDTO.setAvgStory(ratingCalculatorHelper.calculateAverageRatingForDTOs(reviewDTOs, ReviewDTO::getStory_rating));
        gameDTO.setOverallReviewAverage(ratingCalculatorHelper.calculateOverallAverageRatingForDTOs(reviewDTOs));

        return gameDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GameDTO> getFilteredGames(String category, String year, String platform, Pageable pageable) {
        Specification<Games> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (category != null && !category.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("genre")),
                        "%" + category.toLowerCase() + "%"
                ));
            }
            if (year != null && !year.isEmpty()) {
                try {
                    int yearValue = Integer.parseInt(year);
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.function("YEAR", Integer.class, root.get("releaseDate")),
                            yearValue
                    ));
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu year không phải là số
                }
            }
            if (platform != null && !platform.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("platform")),
                        "%" + platform.toLowerCase() + "%"
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        // Map sang GameDTO, các GameDTO này sẽ không có reviews và điểm chi tiết để tối ưu performance cho list
        return gamesRepository.findAll( pageable).map(GameDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GameDTO> getAllGames(Pageable pageable, String genre, String platform, String year) {
        Specification<Games> spec = Specification.where(null); // Khởi tạo spec rỗng
        if (genre != null && !genre.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("genre"), "%" + genre + "%"));
        }
        if (platform != null && !platform.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("platform"), "%" + platform + "%"));
        }
        if (year != null && !year.isEmpty()) {
            try {
                int yearValue = Integer.parseInt(year);
                spec = spec.and((root, query, cb) -> cb.equal(
                        cb.function("YEAR", Integer.class, root.get("releaseDate")), yearValue
                ));
            } catch (NumberFormatException e) {
                // Bỏ qua
            }
        }
        return gamesRepository.findAll( pageable).map(GameDTO::new);
    }


    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getRelatedGames(String genre) {
        // Trả về danh sách GameDTO cơ bản, không cần reviews chi tiết cho related games
        return gamesRepository.findByGenre(genre).stream()
                .map(GameDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GameDTO> getAllGames(Pageable pageable) {
        return gamesRepository.findAll(pageable).map(GameDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getAllGamesList() {
        return gamesRepository.findAll().stream()
                .map(GameDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getNewReleaseGames() {
        // Lấy game có status != 0 (đã release), sắp xếp theo ngày release giảm dần
        return gamesRepository.findByStatusNot(0, Sort.by(Sort.Direction.DESC, "releaseDate")).stream()
                .map(GameDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getMustPlayGames() {
        // Lấy tất cả game đã release
        List<Games> releasedGames = gamesRepository.findByStatusNot(0, Sort.unsorted());

        List<GameDTO> gameDTOs = releasedGames.stream().map(gameEntity -> {
            GameDTO dto = new GameDTO(gameEntity);
            List<Reviews> reviews = reviewsRepository.findByGame_GameId(gameEntity.getGameId());
            float avgRating = ratingCalculator.calculateOverallAverage(reviews);
            dto.setRating(avgRating); // Set rating chính cho game
            return dto;
        }).collect(Collectors.toList());

        // Sắp xếp theo rating giảm dần
        return gameDTOs.stream()
                .sorted(Comparator.comparing(GameDTO::getRating).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getCommingGames() {
        // Lấy game có status = 0 (chưa release/coming soon)
        return gamesRepository.findByStatus(0).stream()
                .map(GameDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GameDTO createGame(GameDTO gameDTO) {
        Games game = gameDTO.toEntity();
        Games savedGame = gamesRepository.save(game);
        return new GameDTO(savedGame);
    }

    @Override
    @Transactional
    public GameDTO updateGame(Long id, GameDTO gameDTO) {
        Games existingGame = gamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id)); // Hoặc một exception cụ thể hơn

        // Update các trường của existingGame từ gameDTO
        existingGame.setName(gameDTO.getName());
        existingGame.setSteamAppId(gameDTO.getGameId()); // Giả sử steamAppId cũng là gameId trong DTO này
        existingGame.setDescription(gameDTO.getDescription());
        existingGame.setGenre(gameDTO.getGenre());
        existingGame.setPlatform(gameDTO.getPlatform());
        existingGame.setReleaseDate(gameDTO.getReleaseDate());
        existingGame.setGameDeveloper(gameDTO.getDeveloper());
        existingGame.setImage(gameDTO.getImage());
        existingGame.setVideo(gameDTO.getVideo());
        existingGame.setStatus(gameDTO.getStatus());
        // Không cần setReviews ở đây vì quan hệ được quản lý bởi Reviews entity hoặc không cần thiết khi chỉ update game info

        Games updatedGame = gamesRepository.save(existingGame);
        return new GameDTO(updatedGame); // Trả về DTO của game đã update
    }

    @Override
    @Transactional
    public boolean deleteGame(Long id) {
        if (gamesRepository.existsById(id)) {
            // Cần xem xét việc xóa các reviews liên quan hoặc xử lý foreign key constraint ở DB
            // Ví dụ: reviewsRepository.deleteByGameId(id); nếu có logic cascade
            gamesRepository.deleteById(id);
            return true;
        }
        return false;
    }
}