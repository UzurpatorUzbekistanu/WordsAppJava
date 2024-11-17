document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Zapobiegamy domyślnemu przeładowaniu strony

    const name = document.getElementById('name').value;
    const password = document.getElementById('password').value;

    console.log('Nazwa:', name);
    console.log('Hasło:', password);

    // Wysłanie danych logowania do backendu w formacie x-www-form-urlencoded
    const formData = new URLSearchParams();
    formData.append('username', name);
    formData.append('password', password);

    fetch('/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded', // Poprawny typ dla logowania
        },
        body: formData
    })
    .then(response => {
        console.log('Odpowiedź z serwera:', response);  // Debugowanie odpowiedzi
        if (response.ok) {
            window.location.href = '/index'; // Zmiana na stronę główną
        } else {
            alert('Nieprawidłowa nazwa użytkownika lub hasło');
        }
    })
    .catch(error => {
        console.error('Błąd logowania:', error);
        alert('Wystąpił problem z logowaniem, spróbuj ponownie');
    });
});
