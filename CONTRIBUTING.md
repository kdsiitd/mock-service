# Contributing to Mock Service

Thank you for your interest in contributing to the Mock Service project! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Reporting](#issue-reporting)
- [Release Process](#release-process)

## Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

### Our Standards

Examples of behavior that contributes to creating a positive environment include:

- Using welcoming and inclusive language
- Being respectful of differing viewpoints and experiences
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members

Examples of unacceptable behavior include:

- The use of sexualized language or imagery and unwelcome sexual attention or advances
- Trolling, insulting/derogatory comments, and personal or political attacks
- Public or private harassment
- Publishing others' private information without explicit permission
- Other conduct which could reasonably be considered inappropriate

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps which reproduce the problem**
- **Provide specific examples to demonstrate the steps**
- **Describe the behavior you observed after following the steps**
- **Explain which behavior you expected to see instead and why**
- **Include details about your configuration and environment**

### Suggesting Enhancements

If you have a suggestion for a new feature or enhancement, please:

- **Use a clear and descriptive title**
- **Provide a step-by-step description of the suggested enhancement**
- **Provide specific examples to demonstrate the steps**
- **Describe the current behavior and explain which behavior you expected to see instead**

### Pull Requests

- Fork the repo and create your branch from `main`
- If you've added code that should be tested, add tests
- If you've changed APIs, update the documentation
- Ensure the test suite passes
- Make sure your code follows the existing code style
- Issue that pull request!

## Development Setup

### Prerequisites

- JDK 21 or higher
- Gradle 8.x or higher
- MySQL 8.0 or higher
- Git

### Local Development Environment

1. **Fork and clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/mock-service.git
   cd mock-service
   ```

2. **Set up the database**
   ```bash
   # Create MySQL database
   mysql -u root -p -e "CREATE DATABASE mock_db;"
   
   # Run schema initialization
   mysql -u root -p mock_db < src/main/resources/schema.sql
   ```

3. **Configure environment variables**
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mock_db
   export SPRING_DATASOURCE_USERNAME=your_username
   export SPRING_DATASOURCE_PASSWORD=your_password
   ```

4. **Build the project**
   ```bash
   ./gradlew clean build
   ```

5. **Run the application**
   ```bash
   ./gradlew bootrun
   ```

### Docker Development Environment

1. **Start the complete environment**
   ```bash
   docker-compose up -d
   ```

2. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/health

## Coding Standards

### Java Code Style

- Follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Add comprehensive JavaDoc comments for public APIs
- Keep methods small and focused (max 50 lines)
- Use appropriate access modifiers

### Spring Boot Conventions

- Follow Spring Boot best practices
- Use constructor injection for dependencies
- Implement proper exception handling
- Use appropriate annotations
- Follow REST API conventions

### Code Organization

```
src/main/java/com/kds/mock/
├── config/          # Configuration classes
├── controllers/     # REST controllers
├── dto/            # Data transfer objects
├── entity/         # JPA entities
├── repository/     # Data access layer
├── service/        # Business logic
└── utils/          # Utility classes
```

### Naming Conventions

- **Classes**: PascalCase (e.g., `MockEndpointRequest`)
- **Methods**: camelCase (e.g., `getMockEndpoint`)
- **Variables**: camelCase (e.g., `endpointPath`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_PATH_LENGTH`)
- **Packages**: lowercase (e.g., `com.kds.mock.controllers`)

## Testing Guidelines

### Test Structure

- Unit tests for all service classes
- Integration tests for controllers
- Repository tests for data access
- End-to-end tests for critical flows

### Test Naming

- Use descriptive test method names
- Follow the pattern: `should_ExpectedBehavior_When_StateUnderTest`
- Example: `should_ReturnMockEndpoint_When_ValidPathAndMethodProvided`

### Test Coverage

- Aim for at least 80% code coverage
- Focus on critical business logic
- Test both happy path and error scenarios
- Mock external dependencies

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests DtoTests

# Run with coverage
./gradlew jacocoTestReport

# Run integration tests
./gradlew integrationTest
```

## Pull Request Process

### Before Submitting

1. **Ensure your code builds successfully**
   ```bash
   ./gradlew clean build
   ```

2. **Run all tests**
   ```bash
   ./gradlew test
   ```

3. **Check code coverage**
   ```bash
   ./gradlew jacocoTestReport
   ```

4. **Update documentation**
   - Update README.md if needed
   - Add/update API documentation
   - Update CHANGELOG.md

5. **Check code style**
   - Ensure consistent formatting
   - Remove unused imports
   - Fix any linting issues

### Pull Request Template

```markdown
## Description
Brief description of the changes

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed
- [ ] Code coverage maintained

## Checklist
- [ ] My code follows the style guidelines of this project
- [ ] I have performed a self-review of my own code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
- [ ] Any dependent changes have been merged and published in downstream modules

## Additional Notes
Any additional information or context
```

### Review Process

1. **Automated Checks**
   - Build must pass
   - All tests must pass
   - Code coverage must be maintained
   - No security vulnerabilities

2. **Code Review**
   - At least one maintainer must approve
   - Address all review comments
   - Ensure code quality standards are met

3. **Merge**
   - Squash commits if necessary
   - Use conventional commit messages
   - Update version if needed

## Issue Reporting

### Bug Reports

When reporting bugs, please include:

- **Environment**: OS, Java version, MySQL version
- **Steps to reproduce**: Detailed step-by-step instructions
- **Expected behavior**: What you expected to happen
- **Actual behavior**: What actually happened
- **Screenshots**: If applicable
- **Logs**: Relevant error logs

### Feature Requests

When requesting features, please include:

- **Use case**: Why this feature is needed
- **Proposed solution**: How you think it should work
- **Alternatives considered**: Other approaches you considered
- **Impact**: Who will benefit from this feature

## Release Process

### Versioning

We follow [Semantic Versioning](https://semver.org/):

- **MAJOR**: Incompatible API changes
- **MINOR**: New functionality in a backwards compatible manner
- **PATCH**: Backwards compatible bug fixes

### Release Steps

1. **Update version**
   - Update `build.gradle` version
   - Update `CHANGELOG.md`
   - Create release branch

2. **Final testing**
   - Run full test suite
   - Perform integration testing
   - Test Docker deployment

3. **Create release**
   - Tag the release
   - Create GitHub release
   - Update documentation

4. **Deploy**
   - Deploy to staging
   - Perform smoke tests
   - Deploy to production

## Getting Help

If you need help with contributing:

- **Documentation**: Check the README and Wiki
- **Issues**: Search existing issues or create a new one
- **Discussions**: Use GitHub Discussions for questions
- **Chat**: Join our community chat (if available)

## Recognition

Contributors will be recognized in:

- **README.md**: List of contributors
- **CHANGELOG.md**: Credit for contributions
- **GitHub**: Contributor statistics

Thank you for contributing to Mock Service! 🚀 