import os
from github import Github


def remove_todos():
    # Load the GitHub access token and repository name from the environment
    github_access_token = os.getenv('GH_TOKEN')
    github_repository_name = os.getenv('GITHUB_REPOSITORY')

    # Initialize the GitHub client
    github_client = Github(github_access_token)

    # Get the GitHub repository
    github_repo = github_client.get_repo(github_repository_name)

    # Read the existing TODOs from the file
    with open('TODO.md', 'r') as todo_file:
        todos = todo_file.readlines()

    # Iterate through closed issues and remove corresponding TODOs
    for issue in github_repo.get_issues(state='closed'):
        title = issue.title
        todos = [todo for todo in todos if title not in todo]

    # Write the updated TODOs back to the TODO.md file
    with open('TODO.md', 'w') as todo_file:
        for todo in todos:
            todo_file.write(todo)


if __name__ == "__main__":
    remove_todos()
