// Переменная для хранения текущего пользователя
let currentUser = '';

// Запрашиваем текущего пользователя с сервера при загрузке страницы
fetch('/api/sessions/current-user')
    .then(response => response.text())
    .then(data => {
        currentUser = data;
    })
    .catch(error => console.error('Error fetching current user:', error));

// Загрузка активных сессий
document.getElementById('load-sessions').addEventListener('click', function() {
    fetch('/api/sessions/active')
        .then(response => response.json())
        .then(data => {
            const sessionList = document.getElementById('session-list');
            sessionList.innerHTML = ''; // Очищаем список сессий
            data.forEach(session => {
                const sessionElement = document.createElement('div');
                sessionElement.className = 'session-item';
                sessionElement.innerHTML = `
                    <h2>${session.sessionName}</h2>
                    <div class="session-details">
                        <p>Start Time: ${new Date(session.startTime).toLocaleString()}</p>
                        <p>End Time: ${new Date(session.endTime).toLocaleString()}</p>
                        <p>Status: ${session.status}</p>
                        <p>Creator: ${session.creator}</p>
                        <p>Joined Users: ${session.joinedUsers}</p>
                        <button class="join-button" data-session-id="${session.id}">Join Session</button>
                    </div>
                `;
                sessionElement.addEventListener('click', () => toggleSessionDetails(sessionElement));
                sessionList.appendChild(sessionElement);
            });

            // Обработчики кнопок присоединения к сессии
            document.querySelectorAll('.join-button').forEach(button => {
                button.addEventListener('click', function(event) {
                    event.stopPropagation(); // Предотвращаем всплытие события
                    const sessionId = this.getAttribute('data-session-id');
                    if (currentUser) {
                        joinSession(sessionId, currentUser);
                    } else {
                        alert('Error: User is not authenticated.');
                    }
                });
            });
        })
        .catch(error => console.error('Error loading sessions:', error));
});

// Обработчик создания новой сессии
document.getElementById('create-session-form').addEventListener('submit', function(event) {
    event.preventDefault();
    const sessionName = document.getElementById('sessionName').value;
    const startTime = new Date(document.getElementById('startTime').value);
    const endTime = new Date(document.getElementById('endTime').value);

    if (startTime > endTime) {
        alert('Start time cannot be after end time');
        return;
    }

    fetch('/api/sessions/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ sessionName, startTime, endTime, status: 'active' })
    })
    .then(response => response.json())
    .then(data => {
        alert('Session created successfully!');
        document.getElementById('load-sessions').click();
    })
    .catch(error => console.error('Error creating session:', error));
});

// Функция для отображения деталей сессии
function toggleSessionDetails(sessionElement) {
    const sessionDetails = sessionElement.querySelector('.session-details');
    sessionDetails.style.display = sessionDetails.style.display === 'none' || sessionDetails.style.display === ''
        ? 'block'
        : 'none';
}

// Функция для присоединения к сессии
function joinSession(sessionId, username) {
    fetch(`/api/sessions/join?sessionId=${sessionId}&username=${username}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to join session');
        }
        return response.text();
    })
    .then(() => {
        alert('Joined session successfully!');
        document.getElementById('load-sessions').click();
    })
    .catch(error => console.error('Error joining session:', error));
}
