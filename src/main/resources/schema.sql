-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    account VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL,               -- 角色: s-学生, t-教师, a-管理员
    department VARCHAR(100),                 -- 部门/院系
    contact VARCHAR(100),                    -- 联系方式
    avatar_path VARCHAR(255),                -- 头像路径
    major_id INT
);

-- 添加测试数据，包含不同角色的用户
INSERT INTO user (account, password, name, role, department, contact, major_id)
VALUES
('admin', '123456', '管理员', 'a', '系统管理部', '13800000000', 1),
('teacher', '123456', '张教授', 't', '计算机学院', '13900000000', 1),
('student', '123456', '李同学', 's', '计算机学院', '13700000000', 1),
('student2', '123456','同学2','s', '计算机学院','13800000000', 1);

-- 专业表
CREATE TABLE IF NOT EXISTS major (
    major_id INT PRIMARY KEY,
    major_name VARCHAR(100) NOT NULL
);

INSERT INTO major (major_id, major_name) VALUES (1, "软件工程");

-- 创建课程表
CREATE TABLE IF NOT EXISTS Course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    course_description TEXT,
    teacher_id INT NOT NULL,
    credit FLOAT NOT NULL,
    category VARCHAR(50),
    hours_per_week INT,
    FOREIGN KEY (teacher_id) REFERENCES user(user_id)
);

-- 添加测试数据
INSERT INTO Course (course_name, course_description, teacher_id, credit, category,hours_per_week) VALUES
('计算机科学导论', '本课程介绍计算机科学的基本概念和原理。', 2, 3.0, '普通',5),
('数据结构与算法', '学习常见的数据结构及其算法实现。', 2, 4.0, '普通',4),
('数据库系统', '介绍数据库的基本概念及其应用。', 2, 3.5, '普通',6),
('操作系统', '深入了解操作系统的结构和功能。', 2, 4.0, '普通',4),
('软件工程', '学习软件开发的生命周期和管理。', 2, 3.0, '实验',5);

-- 创建教室表
CREATE TABLE IF NOT EXISTS Classroom (
    classroom_id INT PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    category VARCHAR(10) NOT NULL
);

-- 添加教室数据
INSERT INTO Classroom (location, capacity, category) VALUES
('A101', 50, "普通"),
('B202', 30, "实验"),
('C303', 100, "普通"),
('D404', 200, "普通");

-- 创建开课信息表
CREATE TABLE IF NOT EXISTS Section (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    classroom_id INT NOT NULL,
    capacity INT NOT NULL,
    available_capacity INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    sec_year INT NOT NULL,
    sec_time VARCHAR(100) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Course(course_id),
    FOREIGN KEY (classroom_id) REFERENCES Classroom(classroom_id)
);

-- 添加开课信息数据
INSERT INTO Section (course_id, classroom_id, capacity, available_capacity, semester, sec_year, sec_time) VALUES
(1, 1, 45, 45, '秋冬', 2024, 'Monday 1; Monday 2'),
(1, 4, 30, 30, '秋冬', 2024, 'Tuesday 1; Tuesday 2'),
(2, 3, 25, 25, '春夏', 2024, 'Wednesday 3; Wednesday 4'),
(3, 3, 90, 90, '春夏', 2025, 'Friday 7; Friday 8');

-- 创建基础成绩表
CREATE TABLE IF NOT EXISTS GradeBase (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    section_id INT NOT NULL,
    score INT,
    gpa FLOAT,
    submit_status ENUM('0', '1') DEFAULT '0',
    FOREIGN KEY (student_id) REFERENCES user(user_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id),
    FOREIGN KEY (section_id) REFERENCES Section(section_id)
);

-- 添加基础成绩数据
INSERT INTO GradeBase (student_id, course_id, section_id, score, gpa, submit_status) VALUES
(3, 1, 1, 85, 3.5, '0'),
(3, 4, 3, 92, 4.0, '0'),
(3, 2, 3, 78, 2.8, '1');

-- 创建成绩组成表
CREATE TABLE IF NOT EXISTS GradeComponent (
    component_id INT PRIMARY KEY AUTO_INCREMENT,
    component_name VARCHAR(255) NOT NULL,
    grade_id INT NOT NULL,
    component_type ENUM('0', '1', '2') NOT NULL,
    ratio INT NOT NULL,
    score INT,
    FOREIGN KEY (grade_id) REFERENCES GradeBase(grade_id)
);

-- 创建成绩修改申请表
CREATE TABLE IF NOT EXISTS Apply (
    apply_id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_id INT NOT NULL,
    admin_id INT,
    grade_id INT NOT NULL,
    old_score INT NOT NULL,
    new_score INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    audit_reason VARCHAR(255) NOT NULL,
    audit_status ENUM('0', '1', '2') DEFAULT '0',  -- 0-待审核, 1-已通过, 2-已拒绝
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    review_time DATETIME,
    FOREIGN KEY (teacher_id) REFERENCES user(user_id),
    FOREIGN KEY (admin_id) REFERENCES user(user_id),
    FOREIGN KEY (grade_id) REFERENCES GradeBase(grade_id)
);

-- 选课记录表
CREATE TABLE IF NOT EXISTS course_selection (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    select_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stu_section (student_id, section_id),
    FOREIGN KEY (student_id) REFERENCES user(user_id),
    FOREIGN KEY (section_id) REFERENCES section(section_id)
);

-- 补选申请表
CREATE TABLE IF NOT EXISTS course_supplement (
    supplement_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 0 COMMENT '0-待处理, 1-已同意, 2-已拒绝',
    FOREIGN KEY (student_id) REFERENCES user(user_id),
    FOREIGN KEY (section_id) REFERENCES section(section_id),
    UNIQUE KEY uk_stu_section_suppl (student_id, section_id)
);

-- 专业培养方案表（存储为JSON格式）
CREATE TABLE IF NOT EXISTS curriculum (
    id INT AUTO_INCREMENT PRIMARY KEY,
    major_id INT NOT NULL,
    curriculum_json JSON NOT NULL,
    UNIQUE KEY uk_major (major_id),
    FOREIGN KEY (major_id) REFERENCES major(major_id)
);

-- 个人培养方案表（存储为JSON格式）
CREATE TABLE IF NOT EXISTS personal_curriculum (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    curriculum_json JSON NOT NULL,
    UNIQUE KEY uk_student (student_id),
    FOREIGN KEY (student_id) REFERENCES user(user_id)
);

-- 选课系统时间配置表（存储为JSON格式）
CREATE TABLE IF NOT EXISTS selection_time (
    id INT PRIMARY KEY DEFAULT 1,
    max_number INT NOT NULL,
    first_time_list JSON NOT NULL,
    second_time_list JSON NOT NULL,
    drop_time_list JSON NOT NULL
);

-- 创建章节表 (新添加)
CREATE TABLE IF NOT EXISTS Chapter (
  chapter_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '章节ID，主键',
  course_id INT NOT NULL COMMENT '所属课程ID',
  chapter_name VARCHAR(255) NOT NULL COMMENT '章节名称',
  sequence INT NOT NULL COMMENT '章节顺序号',
  FOREIGN KEY (course_id) REFERENCES Course(course_id),
  UNIQUE KEY uk_course_chapter_sequence (course_id, sequence) COMMENT '同一课程下章节顺序唯一',
  UNIQUE KEY uk_course_chapter_name (course_id, chapter_name) COMMENT '同一课程下章节名唯一 (可选)'
) COMMENT '课程章节表';

-- 为章节表添加一些示例数据
INSERT INTO Chapter (course_id, chapter_name, sequence) VALUES
(1, '第一章：计算机系统概述', 1),
(1, '第二章：信息表示与处理', 2),
(2, '第一章：绪论', 1),
(2, '第二章：线性表', 2);

-- 创建题库表 (新添加)
CREATE TABLE IF NOT EXISTS QuestionBank (
  question_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID，主键',
  course_id INT NOT NULL COMMENT '所属课程ID',
  chapter_id INT NOT NULL COMMENT '所属章节ID',
  question_type VARCHAR(10) NOT NULL COMMENT '题目类型 (例如: MC-选择题, TF-判断题)',
  content TEXT NOT NULL COMMENT '题目内容',
  options JSON COMMENT '题目选项 (对于选择题，以JSON数组形式存储，例如：["选项A", "选项B"])',
  answer VARCHAR(255) NOT NULL COMMENT '题目答案 (对于选择题可以是选项标识如A/B/C/D，或正确选项的文本；对于判断题是True/False)',
  score INT NOT NULL DEFAULT 1 COMMENT '题目分数',
  difficulty INT NOT NULL DEFAULT 1 COMMENT '题目难度 (例如1-5)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (course_id) REFERENCES Course(course_id),
  FOREIGN KEY (chapter_id) REFERENCES Chapter(chapter_id),
  INDEX idx_qb_course (course_id),
  INDEX idx_qb_chapter (chapter_id)
) COMMENT '题库表';

-- 为题库表添加一些示例数据
INSERT INTO QuestionBank (course_id, chapter_id, question_type, content, options, answer, score, difficulty) VALUES
(1, 1, 'MC', '世界上第一台电子计算机诞生于哪一年？', '["1945年", "1946年", "1947年", "1948年"]', 'B', 5, 2),
(1, 1, 'TF', '冯·诺依曼体系结构是现代计算机的基础。', NULL, 'True', 3, 1),
(2, 3, 'MC', '下列哪个不是线性结构？', '["栈", "队列", "数组", "树"]', 'D', 5, 2);


-- 创建试卷发布表 (新添加)
CREATE TABLE IF NOT EXISTS TestPublish (
  test_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '测试/考试ID，主键',
  teacher_id INT NOT NULL COMMENT '发布教师ID',
  course_id INT NOT NULL COMMENT '所属课程ID',
  test_name VARCHAR(255) NOT NULL COMMENT '测试/考试名称',
  publish_time TIMESTAMP NULL COMMENT '发布时间',
  deadline TIMESTAMP NULL COMMENT '截止时间',
  question_count INT COMMENT '题目数量',
  is_random BOOLEAN DEFAULT FALSE COMMENT '是否随机组卷',
  question_ids JSON COMMENT '题目ID列表 (如果非随机组卷，以JSON数组形式存储题目ID，例如：[101, 102, 105])',
  FOREIGN KEY (teacher_id) REFERENCES User(user_id),
  FOREIGN KEY (course_id) REFERENCES Course(course_id),
  INDEX idx_tp_teacher (teacher_id),
  INDEX idx_tp_course (course_id)
) COMMENT '试卷发布表';

-- 为试卷发布表添加一些示例数据
INSERT INTO TestPublish (teacher_id, course_id, test_name, publish_time, deadline, question_count, is_random, question_ids) VALUES
(2, 1, '计算机导论期中测试', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 2, FALSE, '[1, 2]'),
(2, 2, '数据结构第一次随堂测验', NOW(), DATE_ADD(NOW(), INTERVAL 3 HOUR), 1, TRUE, NULL);


-- 创建学生作答结果表 (新添加)
CREATE TABLE IF NOT EXISTS StudentAnswerResult (
  result_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '作答结果ID，主键',
  test_id INT NOT NULL COMMENT '所属测试/考试ID',
  student_id INT NOT NULL COMMENT '学生ID',
  question_id INT NOT NULL COMMENT '题目ID',
  student_answer VARCHAR(255) COMMENT '学生答案',
  is_correct BOOLEAN COMMENT '是否正确',
  score_obtained INT COMMENT '得分',
  answer_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作答时间',
  FOREIGN KEY (test_id) REFERENCES TestPublish(test_id),
  FOREIGN KEY (student_id) REFERENCES User(user_id),
  FOREIGN KEY (question_id) REFERENCES QuestionBank(question_id),
  UNIQUE KEY uk_student_test_question (student_id, test_id, question_id) COMMENT '确保学生对同一测试的同一题目只作答一次',
  INDEX idx_sar_test_student (test_id, student_id),
  INDEX idx_sar_student (student_id)
) COMMENT '学生作答结果表';

-- 为学生作答结果表添加一些示例数据
INSERT INTO StudentAnswerResult (test_id, student_id, question_id, student_answer, is_correct, score_obtained) VALUES
(1, 3, 1, 'B', TRUE, 5),
(1, 3, 2, 'False', FALSE, 0);
