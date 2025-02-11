document.addEventListener('DOMContentLoaded', () => {
    // Hàm chung để cập nhật màu nền dựa trên điểm số
    const updateColor = (element, score) => {
        if (score >= 0 && score < 35) {
            element.style.backgroundColor = 'red';
        } else if (score >= 35 && score < 70) {
            element.style.backgroundColor = '#D5D500FF';
        } else if (score >= 70 && score <= 100) {
            element.style.backgroundColor = 'green';
        }
    };

    // Xử lý cuộn danh sách game
    const gameContainers = document.querySelectorAll('.games-list');

    gameContainers.forEach(container => {
        const gamesListContainer = container.querySelector('.games-list-container');
        const preButton = container.querySelector('.pre-button');
        const nextButton = container.querySelector('.next-button');
        const cardWidth = container.querySelector('.game-card')?.offsetWidth;

        if (!cardWidth) {
            console.error("Không thể lấy được chiều rộng của card. Kiểm tra lại HTML và CSS.");
            return;
        }

        let scrollAmount = 0;

        nextButton.addEventListener('click', () => {
            const maxScroll = gamesListContainer.scrollWidth - gamesListContainer.offsetWidth;
            scrollAmount = Math.min(scrollAmount + cardWidth, maxScroll);
            gamesListContainer.scrollLeft = scrollAmount;  // Sử dụng scrollLeft
        });

        preButton.addEventListener('click', () => {
            scrollAmount = Math.max(scrollAmount - cardWidth, 0);
            gamesListContainer.scrollLeft = scrollAmount;  // Sử dụng scrollLeft
        });
    });

    // Xử lý màu nền cho các card điểm số
    const processCards = (selector, scoreSelector) => {
        document.querySelectorAll(selector).forEach(card => {
            const scoreElement = card.querySelector(scoreSelector);
            const score = parseInt(scoreElement?.textContent);
            if (!isNaN(score)) {
                updateColor(scoreElement, score);
            }
        });
    };

    processCards('.metascore-card', '.score-container span, .metascore-value');
    processCards('.rate-card', '.rate-value');
    processCards('.review-card', '.review-score');
});