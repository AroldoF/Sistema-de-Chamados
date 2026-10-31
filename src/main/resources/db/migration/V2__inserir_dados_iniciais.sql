INSERT INTO departments (name, code) VALUES ('Coordenação de Tecnologia da Informação', 'COTIC'), ('Manutenção e Infraestrutura', 'MANUT');
INSERT INTO categories (name, description, department_id) VALUES
    ('Hardware', 'Computadores, projetores e periféricos.', 1),
    ('Rede', 'Acesso à rede cabeada e Wi-Fi.', 1),
    ('Elétrica', 'Tomadas, iluminação e instalações elétricas.', 2),
    ('Estrutura', 'Portas, salas e mobiliário.', 2);
INSERT INTO users (name, email, registration, type, department_id) VALUES
    ('Ana Solicitante', 'ana@ifrn.edu.br', '20260001', 'SOLICITANTE', NULL),
    ('Bruno Solicitante', 'bruno@ifrn.edu.br', '20260002', 'SOLICITANTE', NULL),
    ('Carla Técnica', 'carla@ifrn.edu.br', '20260003', 'TECNICO', 1),
    ('Diego Técnico', 'diego@ifrn.edu.br', '20260004', 'TECNICO', 2);
