document.addEventListener("DOMContentLoaded", () => {
    // Check if user is admin
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || user.role !== "ADMIN") {
        window.location.href = "/index.html";
        return;
    }

    // Setup sidebar navigation
    document.querySelectorAll(".sidebar-menu li").forEach(item => {
        item.addEventListener("click", () => {
            // Update active state
            document.querySelector(".sidebar-menu li.active").classList.remove("active");
            item.classList.add("active");

            // Hide all sections
            document.querySelectorAll(".content-section").forEach(section => {
                section.style.display = "none";
            });

            // Show selected section
            const sectionId = item.getAttribute("data-section") + "-section";
            document.getElementById(sectionId).style.display = "block";

            // Load data for the section
            switch(item.getAttribute("data-section")) {
                case "books":
                    loadBooks();
                    break;
                case "authors":
                    loadAuthors();
                    break;
                case "branches":
                    loadBranches();
                    break;
                case "copies":
                    loadCopies();
                    break;
                case "users":
                    loadUsers();
                    break;
                case "dashboard":
                    loadDashboardStats();
                    break;
            }
        });
    });

    // Setup form submissions
    document.getElementById("book-form").addEventListener("submit", handleBookSubmit);
    document.getElementById("author-form").addEventListener("submit", handleAuthorSubmit);
    document.getElementById("branch-form").addEventListener("submit", handleBranchSubmit);
    document.getElementById("copy-form").addEventListener("submit", handleCopySubmit);
    document.getElementById("user-form").addEventListener("submit", handleUserSubmit);

    // Load dashboard by default
    loadDashboardStats();
});

// Modal functions
function showBookModal(book = null) {
    const modal = document.getElementById("book-modal");
    const title = document.getElementById("book-modal-title");
    const form = document.getElementById("book-form");

    if (book) {
        title.textContent = "Edit Book";
        document.getElementById("book-id").value = book.id;
        document.getElementById("book-title").value = book.title;
        document.getElementById("book-isbn").value = book.isbn || "";
        document.getElementById("book-category").value = book.category || "";
        document.getElementById("book-thumbnail").value = book.thumbnail || "";
        document.getElementById("book-description").value = book.description || "";

        // Need to load authors first to set the selected value
        loadAuthorsForSelect().then(() => {
            if (book.author) {
                document.getElementById("book-author").value = book.author.id;
            }
            modal.style.display = "block";
        });
    } else {
        title.textContent = "Add New Book";
        form.reset();
        loadAuthorsForSelect().then(() => {
            modal.style.display = "block";
        });
    }
}

function showAuthorModal(author = null) {
    const modal = document.getElementById("author-modal");
    const title = document.getElementById("author-modal-title");
    const form = document.getElementById("author-form");

    if (author) {
        title.textContent = "Edit Author";
        document.getElementById("author-id").value = author.id;
        document.getElementById("author-name").value = author.name;
        document.getElementById("author-bio").value = author.bio || "";
    } else {
        title.textContent = "Add New Author";
        form.reset();
    }
    modal.style.display = "block";
}

function showBranchModal(branch = null) {
    const modal = document.getElementById("branch-modal");
    const title = document.getElementById("branch-modal-title");
    const form = document.getElementById("branch-form");

    if (branch) {
        title.textContent = "Edit Branch";
        document.getElementById("branch-id").value = branch.id;
        document.getElementById("branch-name").value = branch.name;
        document.getElementById("branch-address").value = branch.address;
    } else {
        title.textContent = "Add New Branch";
        form.reset();
    }
    modal.style.display = "block";
}

function showCopyModal(copy = null) {
    const modal = document.getElementById("copy-modal");
    const title = document.getElementById("copy-modal-title");
    const form = document.getElementById("copy-form");

    if (copy) {
        title.textContent = "Edit Book Copy";
        document.getElementById("copy-id").value = copy.id;
        document.getElementById("copy-book-id").value = copy.book?.id || '';
        document.getElementById("copy-status").value = copy.status;

        // Only load branches for the select
        loadBranchesForSelect().then(() => {
            if (copy.branch) {
                document.getElementById("copy-branch").value = copy.branch.id;
            }
            modal.style.display = "block";
        });
    } else {
        title.textContent = "Add New Book Copy";
        form.reset();
        // Only load branches for the select
        loadBranchesForSelect().then(() => {
            modal.style.display = "block";
        });
    }
}

function showUserModal(user = null) {
    const modal = document.getElementById("user-modal");
    const title = document.getElementById("user-modal-title");
    const form = document.getElementById("user-form");

    if (user) {
        title.textContent = "Edit User";
        document.getElementById("user-id").value = user.id;
        document.getElementById("user-name").value = user.name;
        document.getElementById("user-email").value = user.email;
        document.getElementById("user-role").value = user.role;
        document.getElementById("user-password").placeholder = "Leave blank to keep current password";
    } else {
        title.textContent = "Add New User";
        form.reset();
    }
    modal.style.display = "block";
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = "none";
}

// Data loading functions
async function loadDashboardStats() {
    try {
        const [booksRes, authorsRes, branchesRes, copiesRes, usersRes] = await Promise.all([
            fetch("http://localhost:8080/api/books"),
            fetch("http://localhost:8080/api/authors"),
            fetch("http://localhost:8080/api/branches"),
            fetch("http://localhost:8080/api/book-copies"),
            fetch("http://localhost:8080/api/users")
        ]);

        const books = await booksRes.json();
        const authors = await authorsRes.json();
        const branches = await branchesRes.json();
        const copies = await copiesRes.json();
        const users = await usersRes.json();

        const statsHtml = `
            <div class="stat-card">
                <h3>Books</h3>
                <p>${books.length}</p>
            </div>
            <div class="stat-card">
                <h3>Authors</h3>
                <p>${authors.length}</p>
            </div>
            <div class="stat-card">
                <h3>Branches</h3>
                <p>${branches.length}</p>
            </div>
            <div class="stat-card">
                <h3>Book Copies</h3>
                <p>${copies.length}</p>
            </div>
            <div class="stat-card">
                <h3>Users</h3>
                <p>${users.length}</p>
            </div>
        `;

        document.getElementById("stats").innerHTML = statsHtml;
    } catch (err) {
        console.error("Failed to load dashboard stats:", err);
    }
}

let currentPage = 0;
const pageSize = 100;

async function loadBooks(page = 0) {
    currentPage = page;
    try {
        const res = await fetch(`http://localhost:8080/api/books?page=${page}&size=${pageSize}`);

        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }

        const data = await res.json();

        // Check if the response has the expected structure
        if (!data.content || !Array.isArray(data.content)) {
            throw new Error("Invalid response structure from API");
        }

        const books = data.content;
        const totalPages = data.totalPages || 1; // Default to 1 if not provided

        const tableBody = document.querySelector("#books-table tbody");
        tableBody.innerHTML = books.map(book => `
            <tr>
                <td>${book.id}</td>
                <td><img src="${book.thumbnail || 'https://via.placeholder.com/50x70'}" alt="Cover" style="height: 50px;"></td>
                <td>${book.title}</td>
                <td>${book.author?.name || 'Unknown'}</td>
                <td>${book.category || '-'}</td>
                <td class="action-buttons">
                    <button class="edit-btn" onclick="showBookModal(${JSON.stringify(book).replace(/"/g, '&quot;')})"><i class="fas fa-edit"></i></button>
                    <button class="delete-btn" onclick="deleteBook(${book.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join("");

        // Only render pagination if we have the element and multiple pages
        const paginationContainer = document.getElementById("books-pagination");
        if (paginationContainer && totalPages > 1) {
            let html = "";
            for (let i = 0; i < totalPages; i++) {
                html += `<button onclick="loadBooks(${i})" class="${i === currentPage ? 'active-page' : ''}">${i + 1}</button>`;
            }
            paginationContainer.innerHTML = html;
        }
    } catch (err) {
        console.error("Failed to load books:", err);

        // Fallback to non-paginated API if paginated fails
        try {
            const fallbackRes = await fetch("http://localhost:8080/api/books");
            const books = await fallbackRes.json();

            const tableBody = document.querySelector("#books-table tbody");
            tableBody.innerHTML = books.map(book => `
                <tr>
                    <td>${book.id}</td>
                    <td><img src="${book.thumbnail || 'https://via.placeholder.com/50x70'}" alt="Cover" style="height: 50px;"></td>
                    <td>${book.title}</td>
                    <td>${book.author?.name || 'Unknown'}</td>
                    <td>${book.category || '-'}</td>
                    <td class="action-buttons">
                        <button class="edit-btn" onclick="showBookModal(${JSON.stringify(book).replace(/"/g, '&quot;')})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" onclick="deleteBook(${book.id})"><i class="fas fa-trash"></i></button>
                    </td>
                </tr>
            `).join("");

            // Hide pagination since we're using non-paginated results
            const paginationContainer = document.getElementById("books-pagination");
            if (paginationContainer) {
                paginationContainer.innerHTML = "";
            }
        } catch (fallbackErr) {
            console.error("Failed to load books with fallback:", fallbackErr);
            document.querySelector("#books-table tbody").innerHTML = `<tr><td colspan="6">Error loading books. Please try again.</td></tr>`;
        }
    }
}

function renderPagination(totalPages) {
    const container = document.getElementById("books-pagination");
    if (!container) return;

    let html = "";
    for (let i = 0; i < totalPages; i++) {
        html += `<button onclick="loadBooks(${i})" class="${i === currentPage ? 'active-page' : ''}">${i + 1}</button>`;
    }
    container.innerHTML = html;
}


async function loadAuthors() {
    try {
        const res = await fetch("http://localhost:8080/api/authors");
        const authors = await res.json();

        const tableBody = document.querySelector("#authors-table tbody");
        tableBody.innerHTML = authors.map(author => `
            <tr>
                <td>${author.id}</td>
                <td>${author.name}</td>
                <td>${author.bio ? author.bio.substring(0, 50) + (author.bio.length > 50 ? '...' : '') : '-'}</td>
                <td class="action-buttons">
                    <button class="edit-btn" onclick="showAuthorModal(${JSON.stringify(author).replace(/"/g, '&quot;')})"><i class="fas fa-edit"></i></button>
                    <button class="delete-btn" onclick="deleteAuthor(${author.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join("");
    } catch (err) {
        console.error("Failed to load authors:", err);
    }
}

async function loadBranches() {
    try {
        const res = await fetch("http://localhost:8080/api/branches");
        const branches = await res.json();

        const tableBody = document.querySelector("#branches-table tbody");
        tableBody.innerHTML = branches.map(branch => `
            <tr>
                <td>${branch.id}</td>
                <td>${branch.name}</td>
                <td>${branch.address || '-'}</td>
                <td class="action-buttons">
                    <button class="edit-btn" onclick="showBranchModal(${JSON.stringify(branch).replace(/"/g, '&quot;')})"><i class="fas fa-edit"></i></button>
                    <button class="delete-btn" onclick="deleteBranch(${branch.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join("");
    } catch (err) {
        console.error("Failed to load branches:", err);
    }
}

async function loadCopies() {
    try {
        const res = await fetch("http://localhost:8080/api/book-copies");
        const copies = await res.json();

        const tableBody = document.querySelector("#copies-table tbody");
        tableBody.innerHTML = copies.map(copy => `
            <tr>
                <td>${copy.id}</td>
                <td>${copy.book?.id || 'Unknown'} - ${copy.book?.title || ''}</td>
                <td>${copy.branch?.name || 'Unknown'}</td>
                <td>${copy.status}</td>
                <td class="action-buttons">
                    <button class="edit-btn" onclick="showCopyModal(${JSON.stringify(copy).replace(/"/g, '&quot;')})"><i class="fas fa-edit"></i></button>
                    <button class="delete-btn" onclick="deleteCopy(${copy.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join("");
    } catch (err) {
        console.error("Failed to load copies:", err);
    }
}

async function loadUsers() {
    try {
        const res = await fetch("http://localhost:8080/api/users");
        const users = await res.json();

        const tableBody = document.querySelector("#users-table tbody");
        tableBody.innerHTML = users.map(user => `
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${user.role}</td>
                <td class="action-buttons">
                    <button class="edit-btn" onclick="showUserModal(${JSON.stringify(user).replace(/"/g, '&quot;')})"><i class="fas fa-edit"></i></button>
                    <button class="delete-btn" onclick="deleteUser(${user.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join("");
    } catch (err) {
        console.error("Failed to load users:", err);
    }
}

async function loadAuthorsForSelect() {
    try {
        const res = await fetch("http://localhost:8080/api/authors");
        const authors = await res.json();

        const select = document.getElementById("book-author");
        select.innerHTML = authors.map(author =>
            `<option value="${author.id}">${author.name}</option>`
        ).join("");
    } catch (err) {
        console.error("Failed to load authors for select:", err);
    }
}

async function loadBooksForSelect() {
    try {
        const res = await fetch("http://localhost:8080/api/books");
        const books = await res.json();

        const select = document.getElementById("copy-book");
        select.innerHTML = books.map(book =>
            `<option value="${book.id}">${book.title}</option>`
        ).join("");
    } catch (err) {
        console.error("Failed to load books for select:", err);
    }
}

async function loadBranchesForSelect() {
    try {
        const res = await fetch("http://localhost:8080/api/branches");
        const branches = await res.json();

        const select = document.getElementById("copy-branch");
        select.innerHTML = branches.map(branch =>
            `<option value="${branch.id}">${branch.name}</option>`
        ).join("");
    } catch (err) {
        console.error("Failed to load branches for select:", err);
    }
}

// CRUD Operations
async function handleBookSubmit(e) {
    e.preventDefault();

    const bookId = document.getElementById("book-id").value;
    const url = bookId ? `http://localhost:8080/api/books/${bookId}` : "http://localhost:8080/api/books";
    const method = bookId ? "PUT" : "POST";

    const bookData = {
        title: document.getElementById("book-title").value,
        isbn: document.getElementById("book-isbn").value,
        author: { id: document.getElementById("book-author").value },
        category: document.getElementById("book-category").value,
        thumbnail: document.getElementById("book-thumbnail").value,
        description: document.getElementById("book-description").value
    };

    try {
        const res = await fetch(url, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(bookData)
        });

        if (res.ok) {
            closeModal("book-modal");
            loadBooks();
        } else {
            alert("Failed to save book");
        }
    } catch (err) {
        console.error("Error saving book:", err);
        alert("Error saving book");
    }
}

async function handleAuthorSubmit(e) {
    e.preventDefault();

    const authorId = document.getElementById("author-id").value;
    const url = authorId ? `http://localhost:8080/api/authors/${authorId}` : "http://localhost:8080/api/authors";
    const method = authorId ? "PUT" : "POST";

    const authorData = {
        name: document.getElementById("author-name").value,
        bio: document.getElementById("author-bio").value
    };

    try {
        const res = await fetch(url, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(authorData)
        });

        if (res.ok) {
            closeModal("author-modal");
            loadAuthors();
        } else {
            alert("Failed to save author");
        }
    } catch (err) {
        console.error("Error saving author:", err);
        alert("Error saving author");
    }
}

async function handleBranchSubmit(e) {
    e.preventDefault();

    const branchId = document.getElementById("branch-id").value;
    const url = branchId ? `http://localhost:8080/api/branches/${branchId}` : "http://localhost:8080/api/branches";
    const method = branchId ? "PUT" : "POST";

    const branchData = {
        name: document.getElementById("branch-name").value,
        address: document.getElementById("branch-address").value
    };

    try {
        const res = await fetch(url, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(branchData)
        });

        if (res.ok) {
            closeModal("branch-modal");
            loadBranches();
        } else {
            alert("Failed to save branch");
        }
    } catch (err) {
        console.error("Error saving branch:", err);
        alert("Error saving branch");
    }
}

async function handleCopySubmit(e) {
    e.preventDefault();

    const copyId = document.getElementById("copy-id").value;
    const url = copyId ? `http://localhost:8080/api/book-copies/${copyId}` : "http://localhost:8080/api/book-copies";
    const method = copyId ? "PUT" : "POST";

    const copyData = {
        book: { id: document.getElementById("copy-book-id").value },
        branch: { id: document.getElementById("copy-branch").value },
        status: document.getElementById("copy-status").value
    };

    try {
        const res = await fetch(url, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(copyData)
        });

        if (res.ok) {
            closeModal("copy-modal");
            loadCopies();
        } else {
            const error = await res.json();
            alert(`Failed to save book copy: ${error.message || res.statusText}`);
        }
    } catch (err) {
        console.error("Error saving book copy:", err);
        alert("Error saving book copy");
    }
}

async function handleUserSubmit(e) {
    e.preventDefault();

    const userId = document.getElementById("user-id").value;
    const url = userId ? `http://localhost:8080/api/users/${userId}` : "http://localhost:8080/api/users/register";
    const method = userId ? "PUT" : "POST";

    const userData = {
        name: document.getElementById("user-name").value,
        email: document.getElementById("user-email").value,
        role: document.getElementById("user-role").value
    };

    // Only include password if it's a new user or if it was changed
    const password = document.getElementById("user-password").value;
    if (password || !userId) {
        userData.password = password;
    }

    try {
        const res = await fetch(url, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        if (res.ok) {
            closeModal("user-modal");
            loadUsers();
        } else {
            alert("Failed to save user");
        }
    } catch (err) {
        console.error("Error saving user:", err);
        alert("Error saving user");
    }
}

async function deleteBook(id) {
    if (confirm("Are you sure you want to delete this book?")) {
        try {
            const res = await fetch(`http://localhost:8080/api/books/${id}`, {
                method: "DELETE"
            });

            if (res.ok) {
                loadBooks();
            } else {
                alert("Failed to delete book");
            }
        } catch (err) {
            console.error("Error deleting book:", err);
            alert("Error deleting book");
        }
    }
}

async function deleteAuthor(id) {
    if (confirm("Are you sure you want to delete this author?")) {
        try {
            const res = await fetch(`http://localhost:8080/api/authors/${id}`, {
                method: "DELETE"
            });

            if (res.ok) {
                loadAuthors();
            } else {
                alert("Failed to delete author");
            }
        } catch (err) {
            console.error("Error deleting author:", err);
            alert("Error deleting author");
        }
    }
}

async function deleteBranch(id) {
    if (confirm("Are you sure you want to delete this branch?")) {
        try {
            const res = await fetch(`http://localhost:8080/api/branches/${id}`, {
                method: "DELETE"
            });

            if (res.ok) {
                loadBranches();
            } else {
                alert("Failed to delete branch");
            }
        } catch (err) {
            console.error("Error deleting branch:", err);
            alert("Error deleting branch");
        }
    }
}

async function deleteCopy(id) {
    if (confirm("Are you sure you want to delete this book copy?")) {
        try {
            const res = await fetch(`http://localhost:8080/api/book-copies/${id}`, {
                method: "DELETE"
            });

            if (res.ok) {
                loadCopies();
            } else {
                alert("Failed to delete book copy");
            }
        } catch (err) {
            console.error("Error deleting book copy:", err);
            alert("Error deleting book copy");
        }
    }
}

async function deleteUser(id) {
    if (confirm("Are you sure you want to delete this user?")) {
        try {
            const res = await fetch(`http://localhost:8080/api/users/${id}`, {
                method: "DELETE"
            });

            if (res.ok) {
                loadUsers();
            } else {
                alert("Failed to delete user");
            }
        } catch (err) {
            console.error("Error deleting user:", err);
            alert("Error deleting user");
        }
    }
}

function generateMonthlyReport() {
    const monthInput = document.getElementById("report-month");
    if (!monthInput.value) {
        alert("Please select a month");
        return;
    }

    const [year, month] = monthInput.value.split("-");
    const url = `http://localhost:8080/api/reports/monthly-activity?year=${year}&month=${month}`;

    // Trigger download
    const a = document.createElement('a');
    a.href = url;
    a.download = `library_report_${year}_${month}.pdf`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

function generateOverdueReport() {
    const url = `/api/reports/overdue-loans`;

    // Trigger download
    const a = document.createElement('a');
    a.href = url;
    a.download = `overdue_loans_report.pdf`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}