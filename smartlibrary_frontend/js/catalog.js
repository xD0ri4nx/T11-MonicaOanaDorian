const API_BASE = "http://localhost:8080/api/books";
let currentPage = 0;
let pageSize = 30; // default
let searchTitle = "";
let searchCategory = "";
let sortOption = "";



const bookGrid = document.getElementById("bookGrid");
const pagination = document.getElementById("pagination");
const pageSizeSelector = document.getElementById("pageSize");

pageSizeSelector.addEventListener("change", () => {
    pageSize = parseInt(pageSizeSelector.value);
    currentPage = 0;
    fetchAndDisplayBooks();
});

document.getElementById("searchBtn").addEventListener("click", () => {
    searchTitle = document.getElementById("searchTitle").value.trim();
    searchCategory = document.getElementById("searchCategory").value.trim();
    currentPage = 0;
    fetchAndDisplayBooks();
});

async function fetchAndDisplayBooks() {
    try {
        currentPage = parseInt(currentPage) || 0;
        const url = new URL(API_BASE); // Use the API_BASE constant
        url.searchParams.set("page", currentPage.toString());
        url.searchParams.set("size", pageSize.toString());
        if (searchTitle) url.searchParams.set("title", searchTitle);
        if (searchCategory) url.searchParams.set("category", searchCategory);
        if (sortOption) url.searchParams.set("sort", sortOption);


        console.log("Fetching from:", url.toString()); // Debug log
        const res = await fetch(url);

        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }

        const data = await res.json();

        bookGrid.innerHTML = data.content.map(renderBookCard).join("");
        renderPaginationControls(data.totalPages);
    } catch (error) {
        console.error("Error fetching books:", error);
        bookGrid.innerHTML = `<p class="error">Error loading books: ${error.message}</p>`;
    }
}

function renderBookCard(book) {
    const author = book.author?.name || "Unknown Author";
    const authorLink = book.author ? `<a href="/user/author.html?id=${book.author.id}" class="author-link">${author}</a>` : author;
    const image = book.thumbnail || "https://via.placeholder.com/100x140";

    return `
    <div class="book-card">
      <a href="/user/book.html?id=${book.id}">
        <img src="${image}" alt="${book.title}" />
      </a>
      <h4><a href="/user/book.html?id=${book.id}">${book.title}</a></h4>
      <p><strong>${authorLink}</strong></p>
      <p class="category">${book.category}</p>
    </div>
  `;
}



function renderPaginationControls(totalPages) {
    let html = "";
    for (let i = 0; i < totalPages; i++) {
        if (i > 2 && i < totalPages - 3 && i !== currentPage - 1 && i !== currentPage && i !== currentPage + 1) {
            if (!html.endsWith("...")) html += `<span class="dots">...</span>`;
            continue;
        }

        html += `<button class="page-btn ${i === currentPage ? 'active' : ''}" data-page="${i}">${i + 1}</button>`;
    }

    pagination.innerHTML = html;

    document.querySelectorAll(".page-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            currentPage = parseInt(btn.dataset.page);
            if (!Number.isInteger(currentPage)) currentPage = 0;
            fetchAndDisplayBooks();
        });
    });
}

async function loadCategories() {
    try {
        const res = await fetch("http://localhost:8080/api/books/categories");
        const categories = await res.json();

        const select = document.getElementById("searchCategory");
        categories.forEach(cat => {
            const option = document.createElement("option");
            option.value = cat;
            option.textContent = cat;
            select.appendChild(option);
        });
    } catch (err) {
        console.error("Failed to load categories:", err);
    }
}



document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("user");
    window.location.href = "/index.html";
});

document.getElementById("sortSelect").addEventListener("change", () => {
    sortOption = document.getElementById("sortSelect").value;
    currentPage = 0;
    fetchAndDisplayBooks();
});

document.addEventListener('DOMContentLoaded', async () => {
    pageSize = parseInt(document.getElementById('pageSize').value) || 30;
    await loadCategories(); // wait for categories to be loaded
    fetchAndDisplayBooks();
});