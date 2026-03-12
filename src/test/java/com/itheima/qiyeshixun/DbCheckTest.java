package com.itheima.qiyeshixun;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class DbCheckTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void dumpData() throws Exception {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=============== TRANSFER ORDERS ===============\n");
        List<Map<String, Object>> transfers = jdbcTemplate.queryForList("SELECT * FROM transfer_order ORDER BY id DESC LIMIT 10");
        for (Map<String, Object> map : transfers) sb.append(map).append("\n");

        sb.append("=============== INSPECTION ORDERS ===============\n");
        List<Map<String, Object>> inspections = jdbcTemplate.queryForList("SELECT * FROM inspection_order ORDER BY id DESC LIMIT 10");
        for (Map<String, Object> map : inspections) sb.append(map).append("\n");

        sb.append("=============== TASK ORDERS ===============\n");
        List<Map<String, Object>> tasks = jdbcTemplate.queryForList("SELECT * FROM task_order ORDER BY id DESC LIMIT 10");
        for (Map<String, Object> map : tasks) sb.append(map).append("\n");

        sb.append("=============== SYSTEM USERS ===============\n");
        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id, username, real_name, role FROM system_user");
        for (Map<String, Object> map : users) sb.append(map).append("\n");

        Files.write(Paths.get("dump2.txt"), sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
