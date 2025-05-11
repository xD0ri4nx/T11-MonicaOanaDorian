document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const authorId = urlParams.get("id");

    if (authorId) {
        fetchAuthorStats(authorId);
    }
});

async function fetchAuthorStats(id) {
    try {
        const res = await fetch(`http://localhost:8080/api/authors/${id}/stats`);
        const data = await res.json();

        const author = data.author;
        const books = data.books;

        document.getElementById("authorInfo").innerHTML = `
      <h2>${author.name}</h2>
      <p><strong>Bio:</strong> ${author.bio}</p>
      <p><strong>Total Books:</strong> ${data.totalBooks}</p>
      <p><strong>Total Ratings:</strong> ${data.totalRatings}</p>
      <p><strong>Average Rating:</strong> ${data.averageRating.toFixed(2)}</p>
    `;

        const bookList = books.map(book => `
      <div class="book-row">
        <img src="${book.thumbnail || 'https://via.placeholder.com/100x140'}" alt="${book.title}" />
        <div class="book-details">
          <h4><a href="/user/book.html?id=${book.id}">${book.title}</a></h4>
          <p><strong>Author:</strong> ${book.author.name}</p>
          <p><strong>Year:</strong> ${book.year}</p>
        </div>
      </div>
    `).join("");

        document.getElementById("authorBooks").innerHTML = bookList;
    } catch (err) {
        console.error("Failed to load author info:", err);
    }
}
