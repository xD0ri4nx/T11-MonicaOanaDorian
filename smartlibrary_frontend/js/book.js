const params = new URLSearchParams(window.location.search);
const bookId = params.get("id");
const user = JSON.parse(localStorage.getItem("user"));
const loanMessage = document.getElementById("loanMessage");
const loanBtn = document.getElementById("loanBtn");
const branchSelect = document.getElementById("branchSelect");
const loanDaysSelect = document.getElementById("loanDays");

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("user");
    window.location.href = "/index.html";
});

async function fetchBook() {
    try {
        const res = await fetch(`http://localhost:8080/api/books/${bookId}`);
        if (!res.ok) throw new Error("Book not found");

        const book = await res.json();
        displayBook(book);
        loadSimilarBooks(book.id);

    } catch (err) {
        document.getElementById("bookContainer").innerHTML = `<p>${err.message}</p>`;
    }
}

async function loadBranches() {
    try {
        const res = await fetch("http://localhost:8080/api/branches");
        const branches = await res.json();
        branches.forEach(branch => {
            const option = document.createElement("option");
            option.value = branch.id;
            option.textContent = branch.name;
            branchSelect.appendChild(option);
        });
    } catch (err) {
        console.error("Failed to load branches", err);
    }
}

function displayBook(book) {
    const container = document.getElementById("bookContainer");

    const stars = renderStars(book.averageRating);

    container.innerHTML = `
        <div class="book-info">
            <img src="${book.thumbnail}" alt="${book.title}" class="book-cover" />
            <div class="book-meta">
                <h2>${book.title}</h2>
                <p><strong>Subtitle:</strong> ${book.subtitle || "N/A"}</p>
                <p><strong>Author:</strong> ${book.author?.name || "Unknown"}</p>
                <p><strong>ISBN:</strong> ${book.isbn}</p>
                <p><strong>Category:</strong> ${book.category}</p>
                <p><strong>Published:</strong> ${book.year}</p>
                <p><strong>Pages:</strong> ${book.numberOfPages}</p>
                <p><strong>Ratings:</strong> ${book.numberOfRatings || 0}</p>
                <p><strong>Average Rating:</strong> ${stars} <span class="rating-number">(${book.averageRating?.toFixed(1) || "N/A"})</span></p>
            </div>
        </div>
        <div class="book-description">
            <h3>Description</h3>
            <p>${book.description}</p>
        </div>
    `;
}

async function loadSimilarBooks(bookId) {
    try {
        const res = await fetch(`http://localhost:8080/api/books/${bookId}/similar`);
        const books = await res.json();

        const container = document.getElementById("similarBooks");
        if (books.length === 0) {
            container.innerHTML = "<p>No similar books found.</p>";
            return;
        }

        container.innerHTML = books.map(book => `
            <div class="book-card">
                <a href="/user/book.html?id=${book.id}">
                    <img src="${book.thumbnail || 'https://via.placeholder.com/100x140'}" alt="${book.title}" />
                </a>
                <h4><a href="/user/book.html?id=${book.id}">${book.title}</a></h4>
                <p><strong>${book.author?.name || "Unknown Author"}</strong></p>
            </div>
        `).join("");
    } catch (err) {
        console.error("Failed to load similar books:", err);
    }
}


function renderStars(rating) {
    const fullStars = Math.round(rating || 0);
    let starsHtml = "";
    for (let i = 1; i <= 5; i++) {
        starsHtml += `<span class="star ${i <= fullStars ? "filled" : ""}">★</span>`;
    }
    return starsHtml;
}

loanBtn.addEventListener("click", async () => {
    const branchId = parseInt(branchSelect.value);
    const days = parseInt(loanDaysSelect.value);

    if (!user || !branchId) {
        loanMessage.textContent = "Please select a branch and ensure you're logged in.";
        return;
    }

    const url = `http://localhost:8080/api/book-copies/available?bookId=${bookId}&branchId=${branchId}`;
    console.log("Requesting available copy from:", url);

    try {
        const res = await fetch(url);
        if (!res.ok) {
            // No available copies
            document.getElementById("reservationModal").style.display = "block";
            return;
        }

        const copy = await res.json();
        if (!copy || !copy.id) {
            loanMessage.textContent = "❌ No available copies at this branch.";
            return;
        }

        // Proceed to loan creation
        const loanRes = await fetch(`http://localhost:8080/api/loans?userId=${user.id}&copyId=${copy.id}&days=${days}`, {
            method: "POST"
        });

        if (!loanRes.ok) throw new Error("Loan failed");

        loanMessage.textContent = "✅ Book successfully loaned!";
    } catch (err) {
        console.error(err);
        loanMessage.textContent = "❌ Error: " + err.message;
    }
});

async function createReservation(bookId, branchId) {
    try {
        // Fetch all book copies in the branch to find one to attach the reservation to
        const res = await fetch(`http://localhost:8080/api/book-copies/by-branch-and-book?bookId=${bookId}&branchId=${branchId}`);
        const copies = await res.json();

        if (!Array.isArray(copies) || copies.length === 0) {
            loanMessage.textContent = "❌ No copies exist in this branch to reserve.";
            return;
        }

        const copyId = copies[0].id;

        const reservationRes = await fetch(`http://localhost:8080/api/reservations?userId=${user.id}&copyId=${copyId}`, {
            method: "POST"
        });

        if (!reservationRes.ok) throw new Error("Failed to create reservation.");

        loanMessage.textContent = "📌 Reservation created successfully!";
    } catch (err) {
        console.error(err);
        loanMessage.textContent = "❌ Reservation failed: " + err.message;
    }
}


document.addEventListener("DOMContentLoaded", () => {
    if (bookId) {
        fetchBook();  // uses global bookId
    } else {
        document.getElementById("bookContainer").innerHTML = "<p>Book not found.</p>";
    }

    loadBranches();
});

document.getElementById("confirmReservationBtn").addEventListener("click", async () => {
    document.getElementById("reservationModal").style.display = "none";
    await createReservation(bookId, parseInt(branchSelect.value));
});

document.getElementById("cancelReservationBtn").addEventListener("click", () => {
    document.getElementById("reservationModal").style.display = "none";
});