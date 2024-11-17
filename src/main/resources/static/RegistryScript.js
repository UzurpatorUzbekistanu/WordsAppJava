document.addEventListener('DOMContentLoaded', function () {
    const registerForm = document.getElementById('registerForm');

    registerForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const name = document.getElementById('name').value;
        const password = document.getElementById('password').value;

        const user = {
            name: name,
            password: password
        };

        try {
            const response = await fetch('/api/users/save', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(user)
            });

            if (response.ok) {
                alert("Rejestracja zakończona sukcesem!");
                window.location.href = '/users.html';
            } else {
                const errorData = await response.json();
                alert(`Rejestracja nie powiodła się: ${errorData.message}`);
            }
        } catch (error) {
            console.error("Wystąpił błąd:", error);
            alert("Wystąpił błąd podczas rejestracji.");
        }
    });
});