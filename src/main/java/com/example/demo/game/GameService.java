package com.example.demo.game;

import com.example.demo.game.component.RatingCalculator;
import com.example.demo.game.data.GameDTO;
import com.example.demo.game.data.Games;
import com.example.demo.reviews.data.Reviews;
import com.example.demo.reviews.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.reviews.data.ReviewDTO;

@Service
public class GameService implements IGameService {
    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private RatingCalculator ratingCalculator;

    @Override
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

        return gamesRepository.findAll(specification, pageable)
                .map(game -> new GameDTO((Games) game));
    }

    @Override
    public Page<GameDTO> getAllGames(Pageable pageable, String genre, String platform, String year) {
        Specification<Games> spec = Specification.where(null);
        if (genre != null && !genre.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("genre"), "%" + genre + "%"));
        }
        if (platform != null && !platform.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("platform"), "%" + platform + "%"));
        }
        if (year != null && !year.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(
                    cb.function("YEAR", Integer.class, root.get("releaseDate")), Integer.parseInt(year)
            ));
        }
        return gamesRepository.findAll(spec, pageable)
                .map(game -> new GameDTO((Games) game));
    }

    @Override
    public GameDTO getGameById(Long id) {
        Games game = gamesRepository.findById(id).orElse(null);
        if (game == null) {
            return null;
        }
        GameDTO gameDTO = new GameDTO(game);
        // Tải danh sách reviews từ entity và chuyển sang ReviewDTO
        List<Reviews> reviews = reviewsRepository.findByGameId(id);
        List<ReviewDTO> reviewDTOs = reviews.stream().map(this::convertToReviewDTO).collect(Collectors.toList());
        gameDTO.setReviews(reviewDTOs);
        return gameDTO;
    }

    // Phương thức chuyển đổi Reviews sang ReviewDTO
    private ReviewDTO convertToReviewDTO(Reviews review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReview_id(review.getReview_id());
        reviewDTO.setUserId(review.getUser().getUser_id());
        reviewDTO.setUsername(review.getUser().getUsername()); // Giả sử ReviewDTO có setUsername()
        reviewDTO.setGameplay_rating(review.getGameplay_rating());
        reviewDTO.setMusic_rating(review.getMusic_rating());
        reviewDTO.setGraphic_rating(review.getGraphic_rating());
        reviewDTO.setStory_rating(review.getStory_rating());
        reviewDTO.setComment(review.getComment());
        reviewDTO.setPlatform(review.getPlatform());
        reviewDTO.setReviewDate(review.getReviewDate());
        return reviewDTO;
    }

    @Override
    public List<GameDTO> getRelatedGames(String genre) {
        return gamesRepository.findByGenre(genre).stream()
                .map(game -> new GameDTO((Games) game))
                .collect(Collectors.toList());
    }

    @Override
    public Page<GameDTO> getAllGames(Pageable pageable) {
        return gamesRepository.findAll(pageable)
                .map(game -> new GameDTO((Games) game));
    }

    @Override
    public List<GameDTO> getAllGamesList() {
        return gamesRepository.findAll().stream()
                .map(game -> new GameDTO((Games) game))
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getNewReleaseGames() {
        return gamesRepository.findByStatusNot(0, Sort.by(Sort.Direction.DESC, "releaseDate")).stream()
                .map(game -> new GameDTO((Games) game))
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getMustPlayGames() {
        List<GameDTO> games = getAllGamesList().stream()
                .filter(game -> game.getStatus() != 0)
                .collect(Collectors.toList());

        games.forEach(game -> {
            List<Reviews> reviews = reviewsRepository.findByGameId(game.getGameId());
            float avgRating = ratingCalculator.calculateOverallAverage(reviews);
            game.setRating(avgRating);
        });
        return games.stream()
                .sorted(Comparator.comparing(GameDTO::getRating).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getCommingGames() {
        return gamesRepository.findByStatus(0).stream()
                .map(game -> new GameDTO((Games) game))
                .collect(Collectors.toList());
    }

    @Override
    public GameDTO createGame(GameDTO gameDTO) {
        Games game = gameDTO.toEntity();
        Games savedGame = gamesRepository.save(game);
        return new GameDTO(savedGame);
    }

    @Override
    public GameDTO updateGame(Long id, GameDTO gameDTO) {
        Games existingGame = gamesRepository.findById(id).orElse(null);

        if (existingGame != null) {
            List<Reviews> existingReviews = existingGame.getReviews();
            // Update the existing game with values from the DTO
            existingGame.setName(gameDTO.getName());
            existingGame.setDescription(gameDTO.getDescription());
            existingGame.setGenre(gameDTO.getGenre());
            existingGame.setPlatform(gameDTO.getPlatform());
            existingGame.setReleaseDate(gameDTO.getReleaseDate());
            existingGame.setGameDeveloper(gameDTO.getDeveloper());
            existingGame.setImage(gameDTO.getImage());
            existingGame.setVideo(gameDTO.getVideo());
            existingGame.setStatus(gameDTO.getStatus());
            // Preserve existing reviews
            existingGame.setReviews(existingReviews);
            Games updatedGame = gamesRepository.save(existingGame);
            return new GameDTO(updatedGame);
        }
        return null;
    }

    @Override
    public boolean deleteGame(Long id) {
        if (gamesRepository.existsById(id)) {
            gamesRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
