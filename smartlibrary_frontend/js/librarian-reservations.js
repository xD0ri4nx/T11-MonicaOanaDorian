document.getElementById("logoutBtn")?.addEventListener("click", () => {
    localStorage.removeItem("user");
    window.location.href = "/index.html";
});

const branchId = 1; // Optionally make this dynamic if needed

async function loadReservations() {
    try {
        const res = await fetch("http://localhost:8080/api/reservations");
        const data = await res.json();

        const tbody = document.querySelector("#reservationsTable tbody");
        tbody.innerHTML = "";

        data.forEach(reservation => {
            const book = reservation.bookCopy.book;
            const user = reservation.user;

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${reservation.id}</td>
                <td>${book.id}</td>
                <td>${book.title}</td>
                <td>${book.author?.name || "Unknown"}</td>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${reservation.requestDate}</td>
                <td>${reservation.status}</td>
                <td>
                    ${reservation.status === "PENDING" ?
                `<button onclick="assignBook(${reservation.id}, ${book.id}, ${user.id})">Assign Book</button>`
                : "—"}
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (err) {
        console.error("Failed to load reservations", err);
    }
}

async function assignBook(reservationId, bookId, userId) {
    try {
        const availableRes = await fetch(`http://localhost:8080/api/book-copies/available?bookId=${bookId}&branchId=${branchId}`);
        if (!availableRes.ok) return alert("❌ No available copy in this branch!");

        const copy = await availableRes.json();

        // Assign loan
        const loanRes = await fetch(`http://localhost:8080/api/loans?userId=${userId}&copyId=${copy.id}&days=14`, {
            method: "POST"
        });

        if (!loanRes.ok) throw new Error("Failed to create loan");

        // Mark reservation complete
        await fetch(`http://localhost:8080/api/reservations/${reservationId}/complete`, { method: "PUT" });

        alert("✅ Book assigned successfully!");
        loadReservations();
    } catch (err) {
        console.error(err);
        alert("❌ Could not assign book: " + err.message);
    }
}

loadReservations();
