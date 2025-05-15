const user = JSON.parse(localStorage.getItem("user"));

async function fetchNotifications() {
    if (!user) return;

    try {
        const [res1, res2, res3] = await Promise.all([
            fetch(`http://localhost:8080/api/notifications/user/${user.id}`),               // reservation
            fetch(`http://localhost:8080/api/notifications/overdue/user/${user.id}`),      // loan overdue
            fetch(`http://localhost:8080/api/notifications/new-book/user/${user.id}`)       // new book
        ]);

        const reservationNotifs = await res1.json();
        const overdueNotifs = await res2.json();

        let newBookNotifs = await res3.json();
        if (!Array.isArray(newBookNotifs)) newBookNotifs = [];

        const all = [
            ...reservationNotifs.map(n => ({ type: "reservation", ...n })),
            ...overdueNotifs.map(n => ({ type: "overdue", ...n })),
            ...newBookNotifs.map(n => ({ type: "newbook", ...n }))
        ];

        renderNotificationList(all);
    } catch (err) {
        console.error("Error fetching notifications:", err);
    }
}

function renderNotificationList(notifs) {
    const container = document.getElementById("notificationList");
    if (!notifs.length) {
        container.innerHTML = "<p>No notifications.</p>";
        return;
    }

    container.innerHTML = notifs
        .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
        .map(renderNotification)
        .join("");

    document.querySelectorAll(".mark-read-btn").forEach(btn => {
        btn.addEventListener("click", async () => {
            const notifId = btn.dataset.id;
            let url = "";
            if (btn.dataset.type === "reservation") {
                url = `http://localhost:8080/api/notifications/${notifId}/read`;
            } else if (btn.dataset.type === "overdue") {
                url = `http://localhost:8080/api/notifications/overdue/${notifId}/read`;
            } else if (btn.dataset.type === "newbook") {
                url = `http://localhost:8080/api/notifications/new-book/${notifId}/read`;
            }

            try {
                const res = await fetch(url, { method: "PUT" });
                if (res.ok) {
                    fetchNotifications();
                }
            } catch (err) {
                console.error("Failed to mark as read:", err);
            }
        });
    });
}

function renderNotification(notif) {
    const date = new Date(notif.timestamp).toLocaleString();
    let content = "";
    const readClass = notif.read ? "read" : "unread";

    // Only show mark as read button for unread notifications
    const markAsReadButton = notif.read
        ? ""
        : `<button class="mark-read-btn" data-id="${notif.id}" data-type="${notif.type}">Mark as read</button>`;

    if (notif.type === "reservation") {
        const book = notif.reservation.bookCopy.book;
        content = `
            <div class="notification ${readClass}">
                <img src="${book.thumbnail}" alt="cover" class="notif-thumb">
                <div class="notif-details">
                    <p><strong>${book.title}</strong> is now available for pickup.</p>
                    <small>${date}</small>
                </div>
                ${markAsReadButton}
            </div>
        `;
    } else if (notif.type === "overdue") {
        const book = notif.loan.bookCopy.book;
        content = `
            <div class="notification ${readClass}">
                <img src="${book.thumbnail}" alt="cover" class="notif-thumb">
                <div class="notif-details">
                    <p><strong>${book.title}</strong> loan is overdue! Please return it.</p>
                    <small>${date}</small>
                </div>
                ${markAsReadButton}
            </div>
        `;
    } else if (notif.type === "newbook") {
        const book = notif.book;
        content = `
            <div class="notification ${readClass}">
                <img src="${book.thumbnail}" alt="cover" class="notif-thumb">
                <div class="notif-details">
                    <p>📚 New Book Alert: <strong>${book.title}</strong> by ${book.author.name}. <a href="/user/book.html?id=${book.id}">Check it out!</a></p>
                    <small>${date}</small>
                </div>
                ${markAsReadButton}
            </div>
        `;
    }

    return content;
}

async function fetchUnreadNotificationCount() {
    if (!user) return;

    try {
        const [res1, res2, res3] = await Promise.all([
            fetch(`http://localhost:8080/api/notifications/user/${user.id}`),               // Reservation
            fetch(`http://localhost:8080/api/notifications/overdue/user/${user.id}`),       // Loan overdue
            fetch(`http://localhost:8080/api/notifications/new-book/user/${user.id}`)       // New book
        ]);

        const data1 = await res1.json();
        const data2 = await res2.json();
        const data3 = await res3.json();

        const unreadCount =
            (Array.isArray(data1) ? data1.filter(n => !n.read).length : 0) +
            (Array.isArray(data2) ? data2.filter(n => !n.read).length : 0) +
            (Array.isArray(data3) ? data3.filter(n => !n.read).length : 0);

        const badge = document.getElementById("notificationCount");
        if (unreadCount > 0) {
            badge.textContent = unreadCount;
            badge.classList.remove("hidden");
        } else {
            badge.classList.add("hidden");
        }
    } catch (err) {
        console.error("Failed to fetch notification counts:", err);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    fetchNotifications();
    fetchUnreadNotificationCount();
});
