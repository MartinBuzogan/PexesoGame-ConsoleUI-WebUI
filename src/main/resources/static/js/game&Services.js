
$(document).ready(function () {
    scoreTable();
    rating();
    fetchAndDisplayComments();
});

function rating() {
    $.ajax({
        url: "/api/rating/avg/pexeso",
    }).done(function (response) {
        var userRating = parseFloat(response);
        $("#star-rating").empty();
        for (var i = 1; i <= 5; i++) {
            if (i <= userRating) {
                $('#star-rating').append('<i class="fa fa-star"></i>');
            } else {
                $('#star-rating').append('<i class="fa fa-star-o fa-2x"></i>');
            }
        }
    });
}
function scoreTable() {
        $.ajax({
            url: "/api/score/pexeso",
        }).done(function (json) {
            $("#ScoreTable tbody").empty();
            for (var i = 1; i < json.length; i++) {
                var score = json[i];
                var playedOnDate = new Date(score.playedOn);
                var formattedDate = playedOnDate.getDate() + '.' + (playedOnDate.getMonth() + 1) + '.' + playedOnDate.getFullYear();
                $("#ScoreTable tbody").append($("<tr><td>" + i + "<td>" + score.player + "<td>" + score.points + "<td>" + formattedDate + "</tr>"));
            }
        });
}

$(document).ready(function () {
    refreshPexesofield("/pexeso/field");
    fetchAndDisplayComments();
});

function refreshGameState(url) {
    $.ajax({
        url: url,
    }).done(function (html) {
        $("#Gamestate").html(html);
        scoreTable();

    });
}

function refreshPexesofield(url) {
    $.ajax({
        url: url,
    }).done(function (html) {
        $("#PexesoFromService").html(html);
        refreshGameState("pexeso/gamestate");
        });
}


$(document).ready(function () {
    var ratingBtn = document.getElementById('ratingBtn');
    var commentsBtn = document.getElementById('commentsBtn');
    var scoreBtn = document.getElementById('scoreBtn');

    var ratingDiv = document.getElementById('ratingDiv');
    var commentsDiv = document.getElementById('commentsDiv');
    var scoreDiv = document.getElementById('scoreDiv');

    ratingBtn.addEventListener('click', function () {
        ratingDiv.style.display = 'block';
        commentsDiv.style.display = 'none';
        scoreDiv.style.display = 'none';

        ratingBtn.classList.add('active');
        commentsBtn.classList.remove('active');
        scoreBtn.classList.remove('active');
    });

    commentsBtn.addEventListener('click', function () {
        ratingDiv.style.display = 'none';
        commentsDiv.style.display = 'block';
        scoreDiv.style.display = 'none';

        ratingBtn.classList.remove('active');
        commentsBtn.classList.add('active');
        scoreBtn.classList.remove('active');
    });

    scoreBtn.addEventListener('click', function () {
        ratingDiv.style.display = 'none';
        commentsDiv.style.display = 'none';
        scoreDiv.style.display = 'block';

        ratingBtn.classList.remove('active');
        commentsBtn.classList.remove('active');
        scoreBtn.classList.add('active');
    });
});
async function submitRating(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const ratingValue = formData.get('value');

    const response = await fetch('/pexeso/rate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ rating: ratingValue })
    });

    if (response.ok) {
        rating();
        console.error('Error submitting rating');
    } else {
        console.error('Error submitting rating');
    }
}

async function submitComments(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const comment = formData.get('text');

    const response = await fetch('/pexeso/comment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ comment: comment })
    });

    if (response.ok) {
        fetchAndDisplayComments();
        console.error('submitting comment');
    } else {
        console.error('Error submitting comment');
    }
}

async function fetchAndDisplayComments() {
    const response = await fetch('/api/comment/pexeso');

    if (!response.ok) {
        console.error('Error fetching comments');
        return;
    }

    const comments = await response.json();
    const commentsContainer = document.getElementById('comments-container');
    commentsContainer.innerHTML = '';

    comments.forEach(comment => {
        const commentCard = `
            <div class="card mb-3">
                <div class="card-body">
                    <h5 class="card-title">${comment.player}</h5>
                    <p class="card-text">${comment.comment}</p>
                    <p class="card-text">
                        <small class="text-muted">${new Date(comment.commentedOn).toLocaleString()}</small>
                    </p>
                </div>
            </div>
        `;

        commentsContainer.insertAdjacentHTML('beforeend', commentCard);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    const buttons = document.querySelectorAll(".custom-button");
    const field = document.getElementById("field");

    buttons.forEach(button => {
        button.addEventListener("click", () => {
            const row = parseInt(button.dataset.row, 10);
            const column = parseInt(button.dataset.column, 10);

            fetchGameState(row, column).then(message => {
                console.log("Response message:", message);
            });
        });
    });
});

async function fetchGameState(row, column) {
    const url = new URL("/pexeso/changedfield", document.baseURI);
    if (row !== undefined && column !== undefined) {
        url.searchParams.append("row", row);
        url.searchParams.append("column", column);
    }

    try {
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Accept": "text/html"
            }
        });

        if (response.ok) {
            const message = await response.text();
            refreshPexesofield("/pexeso/field");
            return message;
        } else {
            throw new Error(`HTTP error: ${response.status}`);
        }
    } catch (error) {
        console.error("Error fetching game state:", error);
    }
}
