document.getElementById('load-sessions').addEventListener('click', function() {
    fetch('/api/sessions/active')
        .then(response => response.json())
        .then(data => {
            const sessionList = document.getElementById('session-list');
            sessionList.innerHTML = '';
            data.forEach(session => {
                const sessionElement = document.createElement('div');
                sessionElement.className = 'session';
                sessionElement.innerHTML = `
                    <h2>${session.sessionName}</h2>
                    <p>Start Time: ${session.startTime}</p>
                    <p>End Time: ${session.endTime}</p>
                    <p>Status: ${session.status}</p>
                `;
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
    })
    .catch(error => console.error('Error:', error));
});
