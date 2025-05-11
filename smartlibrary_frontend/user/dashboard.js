const user = JSON.parse(localStorage.getItem("user"));
if (!user || user.role !== "READER") {
    window.location.href = "/index.html";
}

document.getElementById("welcome").textContent = `Welcome, ${user.name}!`;

async function loadDashboard() {
    const res = await fetch(`http://localhost:8080/api/loans/user/${user.id}`);
    const loans = await res.json();

    const activeLoans = loans.filter(l => l.returnDate === null);
    const pastLoans = loans.filter(l => l.returnDate !== null);

    document.getElementById("stats").innerHTML = `
    <p>You have <strong>${activeLoans.length}</strong> active loan(s),
       and <strong>${/* placeholder for reservation count */ 0}</strong> reservation(s).</p>
  `;

    renderLoanGrid("activeLoans", activeLoans);            // current loans
    renderLoanGrid("pastLoans", pastLoans, true);          // past loans with ✅

}


document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("user");
    window.location.href = "/index.html";
});

function renderBookCard(loan, isPast = false) {
    const book = loan.bookCopy.book;
    const author = book.author?.name || "Unknown Author";
    const branch = loan.bookCopy.branch?.name || "Unknown Branch";
    const image = book.thumbnail || "https://via.placeholder.com/100x140";
    const statusLabel = isPast
        ? `Returned: ${loan.returnDate || "N/A"} ✅`
        : `Due: ${loan.dueDate || "N/A"}`;

    const returnBtn = !isPast
        ? `<button class="return-btn" data-loan-id="${loan.id}">Return</button>`
        : "";

    return `
        <div class="book-card">
          <img src="${image}" alt="${book.title}" />
          <h4>${book.title}</h4>
          <p><em>${author}</em></p>
          <p class="branch-label">${branch}</p>
          <p><small>${statusLabel}</small></p>
          ${returnBtn}
        </div>
    `;
}


function renderLoanGrid(containerId, loans, isPast = false) {
    const container = document.getElementById(containerId);
    if (loans.length === 0) {
        container.innerHTML = "<p>No books found.</p>";
        return;
    }

    container.innerHTML = loans.map(loan => renderBookCard(loan, isPast)).join("");

    if (!isPast) {
        container.querySelectorAll(".return-btn").forEach(btn => {
            btn.addEventListener("click", async () => {
                const loanId = btn.dataset.loanId;
                try {
                    const res = await fetch(`http://localhost:8080/api/loans/${loanId}/return`, {
                        method: "PUT"
                    });
                    if (res.ok) {
                        alert("Book returned successfully!");
                        loadDashboard(); // refresh dashboard
                    } else {
                        alert("Failed to return book.");
                    }
                } catch (err) {
                    alert("Error: " + err.message);
                }
            });
        });
    }

}




loadDashboard();
