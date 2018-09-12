TRUNCATE TABLE sys_department;
INSERT INTO sys_department(id, parent_id, name, description, enable, sort) VALUES
(1, NULL, '总公司', NULL, 1, 0),
(2, 1, '人事部', NULL, 1, 0),
(3, 1, '研发部', NULL, 1, 0),
(4, 3, '后端', NULL, 1, 0),
(5, 3, '前端', NULL, 1, 0);

TRUNCATE TABLE sys_role;
INSERT INTO sys_role(id, name, sign, department_id, enable) VALUES
(1, '超级管理员', 'root', 1, 1),
(2, '大老板', 'boss', 1, 1),
(3, '人事部经理', 'hr-manager', 2, 1),
(4, '人事部员工', 'hr', 2, 1),
(5, '研发总监', 'rd-manager', 3, 1),
(6, '项目负责人', 'proj-manager', 3, 1),
(7, '后端开发', 'bed', 4, 1),
(8, '前端开发', 'fed', 5, 1),
(9, '运维', 'op', 3, 1);

TRUNCATE TABLE sys_permission;
INSERT INTO sys_permission(id, parent_id, type, label, sign, icon, sort, enable) VALUES
(1, NULL, 1, '首页', '/dashboard', NULL, 0, 1),
(2, NULL, 0, '系统管理', NULL, NULL, 0, 1),
(3, 2, 1, '部门管理', '/department', NULL, 0, 1),
(4, 2, 1, '菜单管理', '/menu', NULL, 2, 1),
(5, 2, 1, '角色管理', '/role', NULL, 1, 1),
(6, 2, 1, '用户管理', '/user', NULL, 3, 1),
(7, NULL, 0, '内容管理', NULL, NULL, 0, 1),
(8, 7, 1, '图片管理', '/picture', NULL, 0, 1),
(9, 7, 1, '音频管理', '/audio', NULL, 0, 1),
(10, 7, 1, '视频管理', '/video', NULL, 0, 1),
(11, 7, 1, '文件管理', '/file', NULL, 0, 1),
(12, 7, 1, '文章管理', '/article', NULL, 0, 1),
(13, NULL, 0, '系统监控', NULL, NULL, 0, 1),
(14, 13, 1, '系统日志', '/log', NULL, 0, 1),
(15, 13, 1, '运行监控', '/monitor', NULL, 0, 1),
(16, 6, 2, '用户查询', 'system:user:read', NULL, 0, 1),
(17, 6, 2, '用户新增', 'system:user:save', NULL, 0, 1),
(18, 6, 2, '用户修改', 'system:user:update', NULL, 0, 1),
(19, 6, 2, '用户删除', 'system:user:delete', NULL, 0, 1),
(20, 3, 2, '部门查询', 'system:user:read', NULL, 0, 1),
(21, 3, 2, '部门新增', 'system:user:save', NULL, 0, 1),
(22, 3, 2, '部门修改', 'system:user:update', NULL, 0, 1),
(23, 3, 2, '部门删除', 'system:user:delete', NULL, 0, 1),
(24, 5, 2, '菜单查询', 'system:user:read', NULL, 0, 1),
(25, 5, 2, '菜单新增', 'system:user:save', NULL, 0, 1),
(26, 5, 2, '菜单修改', 'system:user:update', NULL, 0, 1),
(27, 5, 2, '菜单删除', 'system:user:delete', NULL, 0, 1),
(28, 4, 2, '角色查询', 'system:user:read', NULL, 0, 1),
(29, 4, 2, '角色新增', 'system:user:save', NULL, 0, 1),
(30, 4, 2, '角色修改', 'system:user:update', NULL, 0, 1),
(31, 4, 2, '角色删除', 'system:user:delete', NULL, 0, 1);

TRUNCATE TABLE sys_user;
INSERT INTO sys_user(id, username, password, nickname, mobile, email, birthday, sex, address, enable) VALUES
(1, 'administrator', '2c2266d825c3af2409016d971529391a', '超管', '13611111111', 'asdfsdaf@163.com', NULL, NULL, NULL, 1),
(2, 'boss123', '300ea247b1e20ca391c2672d26df58ca', '大老板', '13611111112', NULL, NULL, 1, NULL, 1),
(3, 'liudehua', '8bda7a159c71d2e2bd7a0f2e34a76fed', '刘德华', '13611111113', 'XSDFSD@qq.com', NULL, 1, NULL, 1),
(4, 'zhangxueyou', '8c795a819d307216607f4d8144b8447b', '张学友', '13611111114', NULL, NULL, NULL, '顺德北滘美的总部', 1),
(5, 'zhangjingxuan', '405249ced2da65b952605aeeb7fff11e', '张敬轩', '13611111115', NULL, '1989-03-01', NULL, '顺德北滘美的总部', 1),
(6, 'chenbaiyu', 'a43a767ad212f835fe9e09ea3a1613ad', '陈柏宇', '13611111116', NULL, '1988-03-01', 1, '顺德北滘美的总部', 1),
(7, 'chenhuilin', '9c10d02e6c974d4182e62041ea0dab3f', '陈慧琳', '13611111116', NULL, '1982-12-31', 0, '广东广州圆大厦', 1),
(8, 'zhangdada', 'ffb3aa32352eb1ba9ce0f87da1515a9e', '张大大', '13611111116', NULL, '1982-01-01', 1, '广东广州圆大厦', 1);

TRUNCATE TABLE sys_role_permission;
INSERT INTO sys_role_permission(role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27),
(1, 28), (1, 29), (1, 30), (1, 31),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10), (2, 11), (2, 12), (2, 13), (2, 14),
(2, 15), (2, 16), (2, 17), (2, 18), (2, 19), (2, 20), (2, 21), (2, 22), (2, 23), (2, 24), (2, 25), (2, 26), (2, 27),
(2, 28), (2, 29), (2, 30), (2, 31),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 16), (3, 17), (3, 18), (3, 19), (3, 20), (3, 21), (3, 22),
(3, 23), (3, 24), (3, 25), (3, 26), (3, 27), (3, 28), (3, 29), (3, 30), (3, 31),
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6),  (4, 16), (4, 17), (4, 18), (4, 19), (4, 20), (4, 21), (4, 22),
(4, 23), (4, 24), (4, 25), (4, 26), (4, 27), (4, 28), (4, 29), (4, 30), (4, 31),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10), (5, 11), (5, 12), (5, 13), (5, 14),
(5, 15), (5, 16), (5, 17), (5, 18), (5, 19), (5, 20), (5, 21), (5, 22), (5, 23), (5, 24), (5, 25), (5, 26), (5, 27),
(5, 28), (5, 29), (5, 30), (5, 31),
(6, 1), (6, 7), (6, 8), (6, 9), (6, 10), (6, 11), (6, 12), (6, 13), (6, 14), (6, 15),
(7, 1), (7, 7), (7, 8), (7, 9), (7, 10), (7, 11), (7, 12), (7, 13), (7, 14), (7, 15),
(8, 1), (8, 7), (8, 8), (8, 9), (8, 10), (8, 11), (8, 12), (8, 13), (8, 14), (8, 15);

TRUNCATE TABLE sys_user_role;
INSERT INTO sys_user_role(user_id, role_id) VALUES
(1, 1), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
(3, 3), (3, 4),
(4, 4),
(5, 5), (5, 6), (5, 7), (5, 8), (5, 9),
(6, 7),
(7, 8),
(8, 9);