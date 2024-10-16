document.addEventListener('DOMContentLoaded', () => {
    const randomPolishWordBox = document.getElementById('random-polish-word');
    const englishWordInput = document.getElementById('english-word');
    const submitButton = document.getElementById('submit-btn');
    const nextWordButton = document.getElementById('next-word-btn');
    const resultBox = document.getElementById('result');
    const sentenceA1 = document.getElementById('sentenceA1');
    const sentenceHigher = document.getElementById('sentenceHigher');
    const correctEnglishWord = document.getElementById('translation')

    let isAnswerSubmitted = false;

    function getRandomPolishWord() {
        fetch('api/guess/random')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.statusText);
                }
                return response.text();
            })
            .then(word => {
                randomPolishWordBox.innerText = word;
                resultBox.textContent = '';
                englishWordInput.value = '';
                correctEnglishWord.innerText = '';

                isAnswerSubmitted = false;
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
    }

    function checkTranslation() {
        const polishWord = randomPolishWordBox.textContent;
        const englishWord = englishWordInput.value;

        fetch('api/guess/check', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ polishWord, englishWord })
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

function getSentences(englishWord) {
    fetch(`get/sentences?englishWord=${encodeURIComponent(englishWord)}`, { // Używamy prawidłowego endpointu
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        return response.json();
    })
    .then(words => {
        // Upewnij się, że words to tablica z co najmniej dwoma elementami
        if (Array.isArray(words) && words.length >= 2) { // Dodajemy sprawdzenie
            const [sentenceA1, sentenceHigher] = words; // Destrukturyzacja tablicy
            document.getElementById('sentenceA1').innerText = sentenceA1; // Ustawiamy tekst zdania A1
            document.getElementById('sentenceHigher').innerText = sentenceHigher; // Ustawiamy tekst zdania wyższego poziomu
        } else {
            console.error('Otrzymano niewłaściwy format danych:', words);
        }
    })
    .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
    });
}


    getRandomPolishWord();


    submitButton.addEventListener('click', checkTranslation);
    nextWordButton.addEventListener('click', getRandomPolishWord);

    englishWordInput.addEventListener('keypress', function (event) {
        if (event.key === 'Enter') {
            if (!isAnswerSubmitted) {
                checkTranslation();
            } else {
                getRandomPolishWord();
            }
            event.preventDefault();
        }
    });
});
    function getCorrectEnglishWord(polishWord) {
        fetch(`get/correctEnglishWord?polishWord=${encodeURIComponent(polishWord)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.text(); // Zmienione na text, aby zobaczyć, co dokładnie jest zwracane
        })
        .then(word => {
            console.log('Received response:', word); // Loguj odpowiedź
            document.getElementById('translation').innerText = word; // Ustaw odpowiedź jako tekst
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
    }

