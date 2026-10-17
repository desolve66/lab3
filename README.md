# Newsroom (Задание 3)

REST API сервис для редакции периодического издания. Реализует жизненный цикл статей, прохождение редакционной коллегии и выпуск тиражей.

## Модель данных и сущности

- **User (`users`)**: Пользователи системы. Ограничение целостности: уникальный `email`, обязательное поле `name`.
- **EditorialSection (`editorial_sections`)**: Рубрики издания. Флаг `isAvailableForPublication` регулирует доступность рубрики.
- **Article (`articles`)**: Статьи. Внешние ключи на `users(id)` и `editorial_sections(id)`. Статусы: `DRAFT`, `IN_REVIEW`, `APPROVED`, `PUBLISHED`, `REJECTED`.
- **EditorialReview (`editorial_reviews`)**: Проверки редактора. Внешний ключ на `articles(id)`. Фиксирует решение, комментарий и timestamp.
- **EditionIssue (`edition_issues`)**: Номера издания. Связь многие-ко-многим с `articles` через связующую таблицу `issue_articles`.

## Реализованные бизнес-операции

1. **Отправка статьи на модерацию (`POST /api/articles/{id}/submit`)**
    - Затрагивает `Article` и `EditorialSection`.
    - Проверяет: статус должен быть строго `DRAFT`, рубрика статьи обязана быть доступна для публикаций (`isAvailableForPublication == true`), длина текста от 10 символов. Переводит статью в `IN_REVIEW`.
2. **Редакторская проверка (`POST /api/editorial-reviews`)**
    - Затрагивает `EditorialReview` и `Article`.
    - Проверяет: статья должна быть в статусе `IN_REVIEW`. При решении `REJECTED` обязателен комментарий с замечаниями. Переводит статью в `APPROVED` или `REJECTED`.
3. **Включение статьи в номер (`POST /api/edition-issues/{issueId}/articles/{articleId}`)**
    - Затрагивает `EditionIssue` и `Article`.
    - Проверяет: номер не должен быть опубликован (`PUBLISHED`), статья обязана иметь статус `APPROVED`. Защита от дубликатов.
4. **Публикация выпуска (`POST /api/edition-issues/{id}/publish`)**
    - Затрагивает `EditionIssue` и список `Article` (`@Transactional`).
    - Проверяет: непустоту выпуска. В одной транзакции переводит выпуск в `PUBLISHED` и все входящие в него статьи в `PUBLISHED`.
5. **Отзыв статьи (`POST /api/articles/{id}/retract`)**
    - Затрагивает `Article` и `EditionIssue`.
    - Черновик отозвать нельзя. Переводит статью в `REJECTED` и автоматически удаляет её из всех ещё не вышедших номеров.

## Переменные окружения и СУБД

Конфигурация вынесена в `application.properties`:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`