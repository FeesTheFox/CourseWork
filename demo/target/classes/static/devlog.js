document.addEventListener('DOMContentLoaded', function() {
    const commitsContainer = document.getElementById('commits-container');
    const repoOwner = 'FeesTheFox'; // Your GitHub username
    const repoName = 'CourseWork'; // Your repository name
    const apiUrl = `https://api.github.com/repos/${repoOwner}/${repoName}/commits`;

    function fetchCommits() {
        fetch(apiUrl)
            .then(response => response.json())
            .then(data => {
                if (data.length === 0) {
                    commitsContainer.innerHTML = '<p>No commits found.</p>';
                    return;
                }

                // Clear the container before adding new commits
                commitsContainer.innerHTML = '';

                // Display all commits in sequence
                data.forEach(commit => {
                    const commitCard = document.createElement('div');
                    commitCard.classList.add('commit-card');

                    const commitTitle = document.createElement('h2');
                    commitTitle.textContent = commit.commit.message;

                    const commitAuthor = document.createElement('p');
                    commitAuthor.textContent = `Author: ${commit.commit.author.name}`;

                    const commitDate = document.createElement('p');
                    commitDate.textContent = `Date: ${new Date(commit.commit.author.date).toLocaleString()}`;

                    commitCard.appendChild(commitTitle);
                    commitCard.appendChild(commitAuthor);
                    commitCard.appendChild(commitDate);

                    commitsContainer.appendChild(commitCard);
                });
            })
            .catch(error => {
                console.error('Error fetching commits:', error);
                commitsContainer.innerHTML = '<p>Error fetching commits. Please try again later.</p>';
            });
    }

    // Fetch commits initially
    fetchCommits();

    // Set up a polling mechanism to dynamically update the commits
    setInterval(fetchCommits, 60000); // Update every 60 seconds
});
