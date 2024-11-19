// Переменная для хранения текущего пользователя
let currentUser = null;

// Запрашиваем текущего пользователя с сервера при загрузке страницы
fetch('/api/users/current-user')
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        currentUser = data;
        console.log('Current user:', currentUser); // Логируем текущего пользователя для отладки
        checkUserRole();
        loadActiveSessions(); // Загружаем активные сессии после получения текущего пользователя
    })
    .catch(error => {
        console.error('Error fetching current user:', error);
        return fetch('/api/users/current-user')
            .then(response => response.text())
            .then(text => console.log('Server response:', text))
            .catch(error => console.error('Error fetching server response:', error));
    });

// Функция для проверки роли пользователя и управления видимостью формы создания сессии
function checkUserRole() {
    const createSessionForm = document.getElementById('create-session-form');
    if (currentUser && currentUser.role === 'Host') {
        createSessionForm.style.display = 'block';
    } else {
        createSessionForm.style.display = 'none';
    }
    console.log('Form display style:', createSessionForm.style.display); // Логируем стиль для отладки
}

// Функция для загрузки активных сессий
function loadActiveSessions() {
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
                        ${currentUser && currentUser.username === session.creator ? `<button class="end-button" data-session-id="${session.id}">End Session</button>` : ''}
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
                        joinSession(sessionId, currentUser.username);
                    } else {
                        alert('Error: User is not authenticated.');
                    }
                });
            });

            // Обработчики кнопок завершения сессии
            document.querySelectorAll('.end-button').forEach(button => {
                button.addEventListener('click', function(event) {
                    event.stopPropagation(); // Предотвращаем всплытие события
                    const sessionId = this.getAttribute('data-session-id');
                    if (currentUser) {
                        endSession(sessionId);
                    } else {
                        alert('Error: User is not authenticated.');
                    }
                });
            });
        })
        .catch(error => console.error('Error loading sessions:', error));
}

// Автоматически загружаем активные сессии при загрузке страницы
document.addEventListener('DOMContentLoaded', loadActiveSessions);


// Функция для загрузки завершенных сессий
function loadEndedSessions() {
    fetch('/api/sessions/ended')
        .then(response => response.json())
        .then(data => {
            const endedSessionList = document.getElementById('ended-session-list');
            endedSessionList.innerHTML = ''; // Очищаем список сессий
            data.forEach(session => {
                const sessionElement = document.createElement('div');
                sessionElement.className = 'ended-session-item';
                sessionElement.innerHTML = `
                    <h3>${session.sessionName}</h3>
                    <p>Start Time: ${new Date(session.startTime).toLocaleString()}</p>
                    <p>End Time: ${new Date(session.endTime).toLocaleString()}</p>
                    <p>Creator: ${session.creator}</p>
                    <p>Joined Users: ${session.joinedUsers}</p>
                `;
                endedSessionList.appendChild(sessionElement);
            });
        })
        .catch(error => console.error('Error loading ended sessions:', error));
}

// Автоматически загружаем завершенные сессии при загрузке страницы
document.addEventListener('DOMContentLoaded', loadEndedSessions);


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
        loadActiveSessions(); // Обновляем список сессий после создания
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
        loadActiveSessions(); // Обновляем список сессий после присоединения
    })
    .catch(error => console.error('Error joining session:', error));
}

// Функция для завершения сессии
function endSession(sessionId) {
    fetch(`/api/sessions/end/${sessionId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to end session');
        }
        return response.json();
    })
    .then(data => {
        alert('Session ended successfully!');
        loadActiveSessions(); // Обновляем список сессий после завершения
    })
    .catch(error => console.error('Error ending session:', error));
}
