-- 创建用户表
CREATE TABLE IF NOT EXISTS User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    account VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL,               -- 角色: s-学生, t-教师, a-管理员
    department VARCHAR(100),                 -- 部门/院系
    contact VARCHAR(100),                    -- 联系方式
    avatar_path VARCHAR(255)                 -- 头像路径
);

-- 添加测试数据，包含不同角色的用户
INSERT INTO User (account, password, name, role, department, contact) 
VALUES 
('admin', '123456', '管理员', 'a', '系统管理部', '13800000000'),
('teacher', '123456', '张教授', 't', '计算机学院', '13900000000'),
('student', '123456', '李同学', 's', '计算机学院', '13700000000');

-- 创建课程表
CREATE TABLE IF NOT EXISTS Course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    course_description TEXT,
    teacher_id INT NOT NULL,
    credit FLOAT NOT NULL,
    category VARCHAR(50),
    FOREIGN KEY (teacher_id) REFERENCES User(user_id)
);

-- 创建教室表
CREATE TABLE IF NOT EXISTS Classroom (
    classroom_id INT PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(100) NOT NULL,
    capacity INT NOT NULL
);

-- 创建开课信息表
CREATE TABLE IF NOT EXISTS Section (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    classroom_id INT NOT NULL,
    capacity INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    sec_year INT NOT NULL,
    sec_time VARCHAR(100) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Course(course_id),
    FOREIGN KEY (classroom_id) REFERENCES Classroom(classroom_id)
); 