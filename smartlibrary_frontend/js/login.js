document.getElementById('loginForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const res = await fetch(`http://localhost:8080/api/users/login?email=${email}&password=${password}`, {
            method: 'POST'
        });

        if (!res.ok) throw new Error('Invalid credentials');

        const user = await res.json();
        localStorage.setItem("user", JSON.stringify(user));

        // Role-based redirect
        if (user.role === 'ADMIN') {
            localStorage.setItem("user", JSON.stringify(user));
            window.location.href = "/admin/admin.html";
        } else if (user.role === 'LIBRARIAN') {
            localStorage.setItem("user", JSON.stringify(user));
            window.location.href = "/librarian/dashboard.html";
        } else if (user.role === 'READER') {
            localStorage.setItem("user", JSON.stringify(user));
            window.location.href = "/user/dashboard.html";
        } else {
            document.getElementById('error').textContent = "Unknown role or unauthorized access.";
        }
    } catch (err) {
        document.getElementById('error').textContent = err.message;
    }
});
