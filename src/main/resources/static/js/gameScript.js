document.addEventListener('DOMContentLoaded', () => {
    const gamesListContainer = document.querySelector('.games-list-container');
    const preButton = document.querySelector('.pre-button');
    const nextButton = document.querySelector('.next-button');
    const cardWidth = document.querySelector('.game-card').offsetWidth;
    let scrollAmount = 0;


    if (cardWidth === 0) {
        console.error("Không thể lấy được chiều rộng của card. Kiểm tra lại HTML và CSS.");
        return; // Dừng thực thi nếu không lấy được chiều rộng
    }

    nextButton.addEventListener('click', () => {
        const maxScroll = gamesListContainer.scrollWidth - gamesListContainer.offsetWidth;
        scrollAmount += cardWidth;
        if (scrollAmount > maxScroll) {
            scrollAmount = maxScroll;
        }
        gamesListContainer.style.transform = `translateX(-${scrollAmount}px)`;
    });

    preButton.addEventListener('click', () => {
        scrollAmount -= cardWidth;
        if (scrollAmount < 0) {
            scrollAmount = 0;
        }
        gamesListContainer.style.transform = `translateX(-${scrollAmount}px)`;
    });
});

const metascoreCards = document.querySelectorAll('.metascore-card');

metascoreCards.forEach(card => {
    const scoreElement = card.querySelector('.score-container span, .metascore-value');
    const score = parseInt(scoreElement.textContent);
    const scoreContainer = card.querySelector('.score-container, .metascore-value'); // Chọn ô vuông chứa điểm

    // Hàm để cập nhật màu nền của ô vuông
    const updateColor = (score) => {
        if (score >= 0 && score < 35) {
            scoreContainer.style.backgroundColor = 'red';
        } else if (score >= 35 && score < 70) {
            scoreContainer.style.backgroundColor = '#D5D500FF';
        } else if (score >= 70 && score <= 100) {
            scoreContainer.style.backgroundColor = 'green';
        }
    };

    updateColor(score);
});

document.addEventListener('DOMContentLoaded', () => {
    const rateCards = document.querySelectorAll('.rate-card');

    rateCards.forEach(card => {
        const rateValue = card.querySelector('.rate-value');
        const rateScore = parseInt(rateValue.textContent);

        const updateRateColor = (score) => {
            if (score >= 0 && score < 35) {
                rateValue.style.backgroundColor = 'red';
            } else if (score >= 35 && score < 70) {
                rateValue.style.backgroundColor = '#D5D500FF';
            } else if (score >= 70 && score <= 100) {
                rateValue.style.backgroundColor = 'green';
            }
        };

        updateRateColor(rateScore);
    });
});














