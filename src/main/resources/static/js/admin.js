// Hàm hiển thị danh sách game từ API
function displayGames() {
    fetch("/api/games")
        .then(response => response.json())
        .then(games => {
            const tableBody = document.querySelector("tbody");
            tableBody.innerHTML = "";
            games.forEach((game, index) => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${index + 1}</td>
                    <td>${game.name}</td>
                    <td>${game.description}</td>
                    <td>${game.category}</td>
                    <td>${game.platform}</td>
                    <td>${game.releaseDate}</td>
                    <td>${game.developer}</td>
                    <td><img src="${game.image}" class="table-image"></td>
                    <td><iframe width="150" height="100" src="${game.video}" frameborder="0" allowfullscreen></iframe></td>
                    <td>${game.status}</td>
                    <td>
                        <button class="btn btn-sm btn-primary edit-btn" data-id="${game.id}" data-toggle="modal" data-target="#addGameModal">Sửa</button>
                        <button class="btn btn-sm btn-danger delete-btn" data-id="${game.id}">Xóa</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
            addEventListeners();
        });
}

// Hàm thêm event listeners cho nút sửa và xóa
function addEventListeners() {
    document.querySelectorAll(".edit-btn").forEach(button => {
        button.addEventListener("click", () => {
            const gameId = button.dataset.id;
            fetch(`/api/games/${gameId}`)
                .then(response => response.json())
                .then(game => {
                    document.getElementById("gameName").value = game.name;
                    document.getElementById("gameDescription").value = game.description;
                    document.getElementById("gameCategory").value = game.category;
                    document.getElementById("gamePlatform").value = game.platform;
                    document.getElementById("gameReleaseDate").value = game.releaseDate;
                    document.getElementById("gameDeveloper").value = game.developer;
                    document.getElementById("gameImage").value = game.image;
                    document.getElementById("gameVideo").value = game.video;
                    document.getElementById("gameStatus").value = game.status;
                    document.getElementById("addGameForm").dataset.id = gameId; // Lưu ID game để cập nhật
                });
        });
    });

    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", () => {
            const gameId = button.dataset.id;
            if (confirm("Bạn có chắc chắn muốn xóa game này?")) {
                fetch(`/api/games/${gameId}`, { method: "DELETE" })
                    .then(() => displayGames());
            }
        });
    });
}

// Hàm xử lý thêm mới game
document.getElementById("addGameForm").addEventListener("submit", function(event) {
    event.preventDefault();
    const gameId = this.dataset.id;
    const game = {
        name: document.getElementById("gameName").value,
        description: document.getElementById("gameDescription").value,
        category: document.getElementById("gameCategory").value,
        platform: document.getElementById("gamePlatform").value,
        releaseDate: document.getElementById("gameReleaseDate").value,
        developer: document.getElementById("gameDeveloper").value,
        image: document.getElementById("gameImage").value,
        video: document.getElementById("gameVideo").value,
        status: document.getElementById("gameStatus").value
    };

    const method = gameId ? "PUT" : "POST";
    const url = gameId ? `/api/games/${gameId}` : "/api/games";

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(game)
    })
        .then(() => {
            displayGames();
            $("#addGameModal").modal("hide");
            document.getElementById("addGameForm").reset();
            delete this.dataset.id; // Xóa ID game sau khi cập nhật
        });
});

// Hiển thị danh sách game khi trang được tải
displayGames();