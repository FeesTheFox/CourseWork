document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const sessionId = urlParams.get('sessionId');

    if (sessionId) {
        fetch(`/api/sessions/${sessionId}`)
            .then(response => {
                if (!response.ok) {
                    if (response.status === 404) {
                        throw new Error('Session not found');
                    } else {
                        throw new Error('Network response was not ok ' + response.statusText);
                    }
                }
                return response.json();
            })
            .then(session => {
                if (session) {
                    const sessionContainer = document.getElementById('session-container');
                    sessionContainer.innerHTML = `
                        <h1>${session.sessionName || 'Session Name Not Available'}</h1>
                        <p>Status: ${session.status || 'Not Available'}</p>
                        <p>Creator: ${session.creator || 'Not Available'}</p>
                        <p>Joined Users: ${session.joinedUsers || 'Not Available'}</p>
                        <!-- Добавьте здесь дополнительный контент сессии -->
                    `;

                    if (session.videoData) {
                        fetch(`/api/sessions/${sessionId}/video`)
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('Network response was not ok ' + response.statusText);
                                }
                                return response.blob();
                            })
                            .then(blob => {
                                const videoPlayer = document.getElementById('video-player');
                                const videoUrl = URL.createObjectURL(blob);
                                videoPlayer.src = videoUrl;
                                videoPlayer.style.display = 'block';
                            })
                            .catch(error => {
                                console.error('Error loading video:', error);
                            });
                    }

                    getCurrentUsername().then(currentUsername => {
                        if (session.creator === currentUsername) {
                            document.getElementById('upload-form').style.display = 'block';
                            document.getElementById('answer-form').style.display = 'block';
                        } else {
                            document.getElementById('user-answer-form').style.display = 'block';
                        }
                    });
                } else {
                    console.error('Session data is missing');
                }
            })
            .catch(error => {
                console.error('Error loading session:', error);
                const sessionContainer = document.getElementById('session-container');
                sessionContainer.innerHTML = `<p>${error.message}</p>`;
            });

        const uploadForm = document.getElementById('upload-form');
        uploadForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const fileInput = document.getElementById('file');
            const file = fileInput.files[0];
            const formData = new FormData();
            formData.append('file', file);

            fetch(`/api/sessions/${sessionId}/upload-video`, {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(session => {
                const videoPlayer = document.getElementById('video-player');
                const videoUrl = URL.createObjectURL(new Blob([session.videoData], { type: 'video/mp4' }));
                videoPlayer.src = videoUrl;
                videoPlayer.style.display = 'block';
            })
            .catch(error => {
                console.error('Error uploading video:', error);
            });
        });

        const answerForm = document.getElementById('answer-form');
        answerForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const answer = document.getElementById('answer').value;

            fetch(`/api/sessions/${sessionId}/answer`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(answer)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(session => {
                alert('Answer submitted successfully');
            })
            .catch(error => {
                console.error('Error submitting answer:', error);
            });
        });

        const userAnswerForm = document.getElementById('user-answer-form');
        userAnswerForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const userAnswer = document.getElementById('user-answer').value;

            fetch(`/api/sessions/${sessionId}/submit-answer`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ answer: userAnswer })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(result => {
                const feedback = document.getElementById('feedback');
                feedback.textContent = result.message;
                feedback.style.display = 'block';
            })
            .catch(error => {
                console.error('Error submitting answer:', error);
            });
        });
    } else {
        console.error('Session ID is missing');
        const sessionContainer = document.getElementById('session-container');
        sessionContainer.innerHTML = `<p>Session ID is missing</p>`;
    }

    function getCurrentUsername() {
        // Fetch the current user's information from the backend
        return fetch('/api/users/current-user')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(user => {
                return user.username;
            })
            .catch(error => {
                console.error('Error fetching current user:', error);
                return null;
            });
    }
});
