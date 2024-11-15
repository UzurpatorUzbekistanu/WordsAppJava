function loggedUser() {
    // Funkcja do pobrania zalogowanego użytkownika i zaktualizowania elementu "username"
    console.log('Inicjalizacja fetch dla użytkownika...');
    fetch('/api/users/user', {
        method: 'GET',
        credentials: 'include' // Użyj include zamiast same-origin
    })
        .then(response => {
            console.log('Odpowiedź serwera:', response);
            if (!response.ok) {
                throw new Error(`Błąd odpowiedzi serwera: ${response.status}`);
            }
            return response.text();
        })
        .then(username => {
            console.log('Zalogowany użytkownik:', username);
            const usernameElement = document.getElementById('username');
            if (usernameElement) {
                usernameElement.textContent = username || "Gość"; // Jeśli brak użytkownika, ustawiamy "Gość"
            } else {
                console.error("Element 'username' nie istnieje w DOM.");
            }
            // Po załadowaniu użytkownika wywołujemy funkcję sprawdzającą powtórki
            getRandomPolishWord(username);
        })
        .catch(error => {
            console.error("Błąd podczas pobierania użytkownika:", error);
            // W przypadku błędu (np. brak odpowiedzi z serwera), zakładamy, że użytkownik to gość
            const usernameElement = document.getElementById('username');
            if (usernameElement) {
                usernameElement.textContent = "Gość"; // Ustawiamy na Gość, jeśli nie udało się pobrać użytkownika
            }
            // Po tym wywołujemy funkcję do sprawdzenia powtórek
            getRandomPolishWord("Gość");
        });
}

function hideFunctionality() {
    const containerRepeat = document.getElementById('containerRepeat');
    if (!containerRepeat) {
        console.error("Kontener 'containerRepeat' nie został znaleziony w DOM.");
        return;
    }

    containerRepeat.style.display = 'none';

    const message = document.createElement('div');
    message.textContent = "Funkcjonalność dostępna dla zarejestrowanych i zalogowanych użytkowników";
    message.style.color = 'red';
    message.style.fontSize = '20px';
    message.style.textAlign = 'center';
    message.style.marginTop = '20px';

    const infoSection = document.getElementById('infoSection');
    if (infoSection) {
        infoSection.appendChild(message);
    } else {
        console.error("Element 'infoSection' nie został znaleziony w DOM.");
    }
}

function showNoRepeatsMessage() {
    // Wyświetlamy komunikat, jeśli użytkownik nie ma powtórek na dzisiaj
    const infoSection = document.getElementById('infoSection');
    if (infoSection) {
        const message = document.createElement('div');
        message.textContent = "Nie masz powtórek na dziś. Przejdź do sekcji Nowe Słowa.";
        message.style.color = 'blue';  // Kolor komunikatu
        message.style.fontSize = '20px';  // Rozmiar czcionki
        message.style.textAlign = 'center';  // Wyśrodkowanie tekstu
        message.style.marginTop = '20px';  // Odstęp od reszty strony
        infoSection.appendChild(message);
    }
}

function getRandomPolishWord(username) {
    console.log(`Wysyłanie zapytania GET na URL: /api/repeats/random?username=${encodeURIComponent(username)}`);

    if (username === "Gość" || username === "anonymousUser") {
        hideFunctionality();  // Ukryj funkcjonalności, jeśli użytkownik jest gościem
    } else {
        fetch(`/api/repeats/random?username=${encodeURIComponent(username)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.text();
        })
        .then(word => {
            console.log("Otrzymane słowo: ", word);
            if (!word) {
                // Jeśli odpowiedź jest pusta, wyświetlamy komunikat, że nie ma powtórek
                showNoRepeatsMessage();
            } else {
                const randomPolishWordBox = document.getElementById('repeated-polish-word');
                randomPolishWordBox.innerText = word;
                const resultBox = document.getElementById('result');
                resultBox.textContent = '';
                const englishWordInput = document.getElementById('repeated-english-word');
                englishWordInput.value = '';
                const correctEnglishWord = document.getElementById('translation');
                correctEnglishWord.innerText = '';
            }
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
    }
}
function checkTranslation() {
        const polishWord = randomPolishWordBox.textContent;
        const englishWord = englishWordInput.value;
        let loggedUser = document.getElementById('username').textContent;
        if (document.getElementById('username').value == "Gość"){
            loggedUser = null;
            }
        console.log(loggedUser)

        fetch('api/guess/check', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ polishWord, englishWord, loggedUser })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                if (data) {
                    resultBox.textContent = 'Poprawne tłumaczenie!';
                    resultBox.style.color = 'green';
                    getSentences(englishWord);
                    getCorrectEnglishWord(polishWord);

                } else {
//                wstaw jak powinno brzmiec poprawnie
                    resultBox.textContent = 'Niepoprawne tłumaczenie. Spróbuj ponownie.';
                    resultBox.style.color = 'red';
                    getCorrectEnglishWord(polishWord);
                }
                isAnswerSubmitted = true;
            })
            .catch(error => {
                resultBox.textContent = 'Błąd podczas sprawdzania tłumaczenia';
                console.error('Error:', error);
            });
    }

document.addEventListener('DOMContentLoaded', () => {
    loggedUser();  // Uruchamiamy pobieranie zalogowanego użytkownika

    const submitButton = document.getElementById('submit-btn');
    const nextWordButton = document.getElementById('next-word-btn');
    const englishWordInput = document.getElementById('repeated-english-word');  // Zmienna dla inputa

    // Sprawdzamy, czy przyciski istnieją przed dodaniem nasłuchiwaczy
    if (submitButton) {
        submitButton.addEventListener('click', checkTranslation);
    } else {
        console.error('Element submit-btn nie został znaleziony.');
    }

    if (nextWordButton) {
        nextWordButton.addEventListener('click', getRandomPolishWord);
    } else {
        console.error('Element next-word-btn nie został znaleziony.');
    }

    // Nasłuchujemy na zdarzenie 'Enter' w polu input
    if (englishWordInput) {
        englishWordInput.addEventListener('keypress', function (event) {
            if (event.key === 'Enter') {
                if (!isAnswerSubmitted) {
                    checkTranslation();  // Jeśli odpowiedź nie została jeszcze wysłana
                } else {
                    getRandomPolishWord();  // Jeśli odpowiedź została wysłana, weź następne słowo
                }
                event.preventDefault();  // Zapobiega domyślnemu zachowaniu (np. przesyłaniu formularza)
            }
        });
    } else {
        console.error('Element repeated-english-word nie został znaleziony.');
    }
});
