package com.Main.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.Main.entity.User;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserId"));
        user.setAccount(rs.getString("UserEmail"));
        user.setPassword(rs.getString("UserPwd"));
        user.setName(rs.getString("UserName"));
        user.setDepartment(rs.getString("Location"));
        user.setAvatarPath(rs.getString("avatarUrl"));
        
        // 如果数据库中有这些字段，也需要设置
        if (hasColumn(rs, "role")) {
            user.setRole(rs.getString("role"));
        }
        if (hasColumn(rs, "contact")) {
            user.setContact(rs.getString("contact"));
        }
        
        return user;
    }
    
    // 辅助方法：检查ResultSet是否包含特定列
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
