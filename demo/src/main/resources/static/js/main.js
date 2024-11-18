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
                    </div>
                `;
                sessionElement.addEventListener('click', () => toggleSessionDetails(sessionElement));
                sessionList.appendChild(sessionElement);
            });
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('create-session-form').addEventListener('submit', function(event) {
    event.preventDefault();
    const sessionName = document.getElementById('sessionName').value;
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;

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
        // Обновляем список сессий после создания новой сессии
        document.getElementById('load-sessions').click();
    })
    .catch(error => console.error('Error:', error));
});

function toggleSessionDetails(sessionElement) {
    const sessionDetails = sessionElement.querySelector('.session-details');
    if (sessionDetails.style.display === 'none' || sessionDetails.style.display === '') {
        sessionDetails.style.display = 'block';
    } else {
        sessionDetails.style.display = 'none';
    }
}
