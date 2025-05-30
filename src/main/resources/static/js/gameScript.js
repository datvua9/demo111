document.addEventListener('DOMContentLoaded', () => {
    const updateColor = (element, score) => {
        if (score >= 0 && score < 3.5) {
            element.style.backgroundColor = 'red';
        } else if (score >= 3.5 && score < 7) {
            element.style.backgroundColor = '#D5D500FF';
        } else if (score >= 7 && score <= 10) {
            element.style.backgroundColor = 'green';
        }
    };

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
            gamesListContainer.scrollLeft = scrollAmount;
        });
        preButton.addEventListener('click', () => {
            scrollAmount = Math.max(scrollAmount - cardWidth, 0);
            gamesListContainer.scrollLeft = scrollAmount;
        });
    });

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

document.addEventListener('DOMContentLoaded', function() {
    // Xử lý dropdown (đã xử lý bằng CSS hover, nhưng có thể thêm JS nếu cần)
});