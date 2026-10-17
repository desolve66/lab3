INSERT INTO users (id, name, email, role) VALUES
                                              (100, 'Алексей Главред', 'editor.chief@news.com', 'EDITOR'),
                                              (101, 'Мария Журналист', 'maria.journalist@news.com', 'AUTHOR')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO editorial_sections (id, name, is_available_for_publication) VALUES
                                                                            (100, 'Информационные технологии', true),
                                                                            (101, 'Закрытый архив (публикация запрещена)', false)
    ON CONFLICT (id) DO NOTHING;