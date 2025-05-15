let currentPage = 0;
let pageSize = 100;


const user = JSON.parse(localStorage.getItem("user"));
if (!user || user.role !== "LIBRARIAN") {
    window.location.href = "/index.html";
}

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("user");
    window.location.href = "/index.html";
});

const branchSelect = document.getElementById("branchSelect");
const tableBody = document.querySelector("#copyTable tbody");

async function loadBranches() {
    const res = await fetch("http://localhost:8080/api/branches");
    const branches = await res.json();

    branches.forEach(branch => {
        const option = document.createElement("option");
        option.value = branch.id;
        option.textContent = branch.name;
        branchSelect.appendChild(option);
    });

    if (branches.length > 0) {
        branchSelect.value = branches[0].id;
        loadBookCopies(branches[0].id);
    }
}

branchSelect.addEventListener("change", () => {
    const branchId = branchSelect.value;
    loadBookCopies(branchId);
});

async function loadBookCopies(branchId) {
    try {
        const url = new URL(`http://localhost:8080/api/book-copies/branch/${branchId}`);
        url.searchParams.set("page", currentPage);
        url.searchParams.set("size", pageSize);

        const res = await fetch(url);
        if (!res.ok) throw new Error("Failed to fetch book copies");

        const data = await res.json();
        const copies = data.content;

        renderTable(copies);
        renderPagination(data.totalPages);

    } catch (err) {
        console.error(err);
        document.getElementById("copiesTableBody").innerHTML = "<tr><td colspan='6'>Error loading data.</td></tr>";
    }
}

function renderTable(copies) {
    const tableBody = document.querySelector("#copyTable tbody");
    tableBody.innerHTML = "";

    if (!copies.length) {
        tableBody.innerHTML = "<tr><td colspan='6'>No book copies found for this branch.</td></tr>";
        return;
    }

    copies.forEach(copy => {
        const book = copy.book || {};
        const author = book.author?.name || "Unknown";
        const image = book.thumbnail || "https://via.placeholder.com/50x70";

        const row = document.createElement("tr");
        row.innerHTML = `
            <td><img src="${image}" alt="${book.title}" width="50"/></td>
            <td>${copy.id}</td>
            <td>${book.title}</td>
            <td>${author}</td>
            <td>${copy.status}</td>
            <td>
                <button onclick="markStatus(${copy.id}, 'LOST')">Mark as Lost</button>
                <button onclick="markStatus(${copy.id}, 'DAMAGED')">Mark as Damaged</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}


function renderPagination(totalPages) {
    const container = document.getElementById("pagination");
    container.innerHTML = "";

    for (let i = 0; i < totalPages; i++) {
        const btn = document.createElement("button");
        btn.textContent = i + 1;
        btn.className = (i === currentPage) ? "active" : "";
        btn.addEventListener("click", () => {
            currentPage = i;
            loadBookCopies(document.getElementById("branchSelect").value);
        });
        container.appendChild(btn);
    }
}



async function markStatus(copyId, status) {
    const confirmed = confirm(`Are you sure you want to mark this book as ${status}?`);
    if (!confirmed) return;

    const res = await fetch(`http://localhost:8080/api/book-copies/${copyId}/status?status=${status}`, {
        method: "PUT"
    });

    if (res.ok) {
        loadBookCopies(branchSelect.value);
    } else {
        alert("Failed to update status");
    }
}



document.addEventListener("DOMContentLoaded", loadBranches);
