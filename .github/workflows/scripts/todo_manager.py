import logging
import os
import re
from github import Github


class TodoManager:
    def __init__(self, github_access_token, github_repository_name, java_files_directory):
        self.github_client = Github(github_access_token)
        self.github_repo = self.github_client.get_repo(github_repository_name)
        self.java_files_directory = java_files_directory
        self.seen_todos = set()
        logging.basicConfig(level=logging.INFO)

    @staticmethod
    def get_code_snippet(file_name, line_number, context=3):
        with open(file_name, 'r') as f:
            lines = f.readlines()
        start = max(line_number - context - 1, 0)
        end = min(line_number + context, len(lines))
        snippet = ''.join(lines[start:end])
        return snippet

    @staticmethod
    def find_todos_in_lines(lines, file_name, seen_todos):
        todos = []
        for line_number, line in enumerate(lines, start=1):
            if re.search(r'//\s*TODO:', line, re.IGNORECASE):
                todo_line = line.strip().lower()
                if todo_line not in seen_todos:
                    todos.append((file_name, line_number, line.strip()))
                    seen_todos.add(todo_line)
        return todos

    def get_current_branch(self):
        ref = os.getenv('GITHUB_REF')
        if ref.startswith('refs/heads/'):
            return ref.split('refs/heads/')[-1]
        return 'main'  # default if not found

    def extract_todos_from_file(self, file_path):
        with open(file_path, 'r') as file:
            lines = file.readlines()
        return self.find_todos_in_lines(lines, file_path, self.seen_todos)

    def scan_for_new_todos(self):
        java_files_directory = os.path.join(os.environ.get('GITHUB_WORKSPACE', ''), self.java_files_directory)
        new_todos = []

        for root, dirs, files in os.walk(java_files_directory):
            for file_name in files:
                if file_name.endswith('.java'):
                    java_file_path = os.path.join(root, file_name)
                    new_todos.extend(self.extract_todos_from_file(java_file_path))

        return new_todos

    def create_issues_for_new_todos(self, new_todos, branch_name):
        for todo in new_todos:
            file_name, line_number, description = todo  # Unpack the tuple
            snippet = self.get_code_snippet(file_name, line_number)
            issue_title = f"TODO: {description} (from branch {branch_name})"

            # Check if an issue already exists
            existing_issue = None
            for issue in self.github_repo.get_issues(state='open'):
                if issue.title == issue_title:
                    existing_issue = issue
                    break

            if existing_issue is None:
                issue_body = self.format_issue_body(description, file_name, line_number, snippet, branch_name)
                self.github_repo.create_issue(title=issue_title, body=issue_body, labels=["TODO"])
                logging.info(f"Created GitHub issue for TODO: {description}")

    def format_issue_body(self, description, file_name, line_number, snippet, branch_name):
        return (f"### TODO Description\n{description}\n\n"
                f"### Branch\n{branch_name}\n\n"
                f"### File\n[Link to File](https://github.com/{self.github_repo.full_name}/"
                f"blob/{branch_name}/{file_name}#L{line_number})\n\n"
                f"### Code Snippet\n```java\n{snippet}\n```\n\n")

    def handle_push_event(self, branch_name):
        new_todos = self.scan_for_new_todos()
        self.create_issues_for_new_todos(new_todos, branch_name)

    def handle_issue_event(self, issue_event, issue_title):
        """
        Handle adding or removing TODOs based on issue events.

        :param issue_event: The type of issue event (e.g., 'opened', 'closed').
        :param issue_title: The title of the GitHub issue.
        """
        if issue_event == 'opened':
            self.add_todo_comment(issue_title)
        elif issue_event in ['closed', 'deleted']:
            self.remove_todo_comment(issue_title)

    def add_todo_comment(self, issue_title, issue_body):
        """
        Append a TODO comment in the TODO.md for the given issue title.

        :param issue_title: The title of the GitHub issue.
        :param issue_body: The body text of the GitHub issue which may contain additional details.
        """
        with open('TODO.md', 'a') as todo_file:
            todo_entry = f"// TODO: {issue_title}\nDetails: {issue_body}\n\n"
            todo_file.write(todo_entry)
        logging.info(f"TODO added for issue: {issue_title}")

    def remove_todo_comment(self, issue_title):
        """
        Remove the TODO comment in the code that corresponds to the given issue title.

        :param issue_title: The title of the GitHub issue.
        """
        updated_files = False
        todo_regex = re.compile(r'//\s*TODO:\s*' + re.escape(issue_title), re.IGNORECASE)

        for root, dirs, files in os.walk(self.java_files_directory):
            for file_name in files:
                if file_name.endswith('.java'):
                    file_path = os.path.join(root, file_name)
                    with open(file_path, 'r') as file:
                        lines = file.readlines()

                    with open(file_path, 'w') as file:
                        for line in lines:
                            if not todo_regex.search(line):
                                file.write(line)
                            else:
                                updated_files = True

        if updated_files:
            logging.info(f"Removed TODO for issue: {issue_title}")
        else:
            logging.warning(f"No TODO found for issue: {issue_title}")


def main():
    # Load environment variables
    github_access_token = os.getenv('GITHUB_ACCESS_TOKEN')
    github_repository_name = os.getenv('GITHUB_REPOSITORY')
    java_files_directory = os.getenv('JAVA_FILES_DIRECTORY', 'src/')  # Default to 'src/' if not set

    todo_manager = TodoManager(github_access_token, github_repository_name, java_files_directory)
    event_name = os.getenv('GITHUB_EVENT_NAME')
    issue_title = os.getenv('GITHUB_ISSUE_TITLE')
    issue_body = os.getenv('GITHUB_ISSUE_BODY')

    if event_name == 'push':
        branch_name = todo_manager.get_current_branch()
        todo_manager.handle_push_event(branch_name)
    elif event_name == 'issues':
        issue_event = os.getenv('GITHUB_EVENT_ACTION')
        if issue_event == 'opened':
            todo_manager.add_todo_comment(issue_title, issue_body)
        else:
            todo_manager.handle_issue_event(issue_event, issue_title)


if __name__ == '__main__':
    main()
