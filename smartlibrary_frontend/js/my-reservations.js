const user = JSON.parse(localStorage.getItem("user"));
const grid = document.getElementById("reservationsGrid");

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("user");
    window.location.href = "/index.html";
});

if (!user) {
    grid.innerHTML = "<p>Please log in to view your reservations.</p>";
} else {
    fetchReservations(user.id);
}

async function fetchReservations(userId) {
    try {
        const res = await fetch(`http://localhost:8080/api/reservations/user/${userId}`);
        const reservations = await res.json();

        if (reservations.length === 0) {
            grid.innerHTML = "<p>You have no reservations.</p>";
            return;
        }

        grid.innerHTML = reservations.map(renderReservation).join("");
        attachCancelHandlers();
    } catch (err) {
        grid.innerHTML = `<p>Error loading reservations: ${err.message}</p>`;
    }
}

function renderReservation(reservation) {
    const book = reservation.bookCopy.book;
    const author = book.author?.name || "Unknown Author";
    const image = book.thumbnail || "https://via.placeholder.com/100x140";
    const cancelBtn = reservation.status === "PENDING"
        ? `<button class="cancel-btn" data-id="${reservation.id}">Cancel</button>`
        : "";

    return `
    <div class="book-card">
      <img src="${image}" alt="${book.title}" />
      <h4>${book.title}</h4>
      <p><strong>${author}</strong></p>
      <p>Status: <strong>${reservation.status}</strong></p>
      ${cancelBtn}
    </div>
  `;
}

function attachCancelHandlers() {
    document.querySelectorAll(".cancel-btn").forEach(btn => {
        btn.addEventListener("click", async () => {
            const id = btn.dataset.id;
            try {
                const res = await fetch(`http://localhost:8080/api/reservations/${id}/cancel`, {
                    method: "PUT"
                });
                if (res.ok) {
                    alert("Reservation cancelled.");
                    fetchReservations(user.id);
                } else {
                    alert("Failed to cancel reservation.");
                }
            } catch (err) {
                alert("Error cancelling reservation: " + err.message);
            }
        });
    });
}
